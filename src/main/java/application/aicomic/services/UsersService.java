package application.aicomic.services;

import application.aicomic.dataAccess.UserServiceResponseDto;
import application.aicomic.dataAccess.UsersDTO;
import application.aicomic.dataAccess.WalletsDTO;
import application.aicomic.enums.Role;
import application.aicomic.enums.TransactionsEnums;
import application.aicomic.enums.WalletType;
import application.aicomic.mapper.Mapper;
import application.aicomic.models.Transactions;
import application.aicomic.models.Users;
import application.aicomic.models.Wallets;
import application.aicomic.repositories.TransactionsRepository;
import application.aicomic.repositories.UsersRepository;
import application.aicomic.repositories.WalletsRepository;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import application.aicomic.services.WalletsService;
import org.springframework.transaction.annotation.Transactional;
import application.aicomic.enums.OrdersEnums;
import application.aicomic.models.Orders;

import java.util.*;

@Slf4j
@Service
public class UsersService {
    private final UsersRepository usersRepository;
    private final WalletsRepository walletsRepository;
    private Mapper mapper;
    private WalletsService walletsService;
    private TransactionsService transactionsService;
    private TransactionsRepository transactionsRepository;
    private static final Logger logger = LoggerFactory.getLogger(UsersService.class);

    @Autowired
    public UsersService(UsersRepository usersRepository, WalletsRepository walletsRepository, Mapper mapper, WalletsService walletsService, TransactionsService transactionsService, TransactionsRepository transactionsRepository) {
        this.walletsRepository = walletsRepository;
        this.usersRepository = usersRepository;
        this.mapper = mapper;
        this.walletsService = walletsService;
        this.transactionsService = transactionsService;
        this.transactionsRepository = transactionsRepository;
    }

    public List<Users> getAllUsers() {
        return usersRepository.findAll();
    }

    public Users getUserById(String userId) {
        return usersRepository.findById(userId).orElse(null);
    }

    public Users getByEmail(String email) {
        return usersRepository.findByEmail(email).orElse(null);
    }

    public Users saveUser(Users user) {
        Users savedUser = usersRepository.save(user);
        createWalletsForUser(savedUser);
        return savedUser;
    }

    public Users updateUsers(String id, UsersDTO usersDTO) {
        Users users = usersRepository.findById(id).orElseThrow(() -> new RuntimeException("Users not found"));
        mapper.updateUsers(users, usersDTO);
        return usersRepository.save(users);
    }

    private void createWalletsForUser(Users user) {
        List<Byte> eligibleRoles = List.of((byte) 5, (byte) 6, (byte) 7, (byte) 8);
        if (eligibleRoles.contains(user.getRole())) {
            Wallets mainWallet = new Wallets();
            mainWallet.setUserId(user.getUserId());
            mainWallet.setType(WalletType.MAIN);
            mainWallet.setBalance(0);
            mainWallet.setUpdatedDate(LocalDateTime.now());

            Wallets promoWallet = new Wallets();
            promoWallet.setUserId(user.getUserId());
            promoWallet.setType(WalletType.PROMOTION);
            promoWallet.setBalance(0);
            promoWallet.setUpdatedDate(LocalDateTime.now());

            walletsRepository.save(mainWallet);
            walletsRepository.save(promoWallet);
        }
    }

    public UserServiceResponseDto deleteUser(String id) {
        try {
            Optional<Users> userOptional = usersRepository.findById(id);
            if (userOptional.isEmpty()) {
                return new UserServiceResponseDto(false, "User not found.", Collections.emptyList());
            }

            Users user = userOptional.get();
            user.setStatus((byte) 0);
            usersRepository.save(user);

            return new UserServiceResponseDto(true, "User status updated to inactive (deleted).", Collections.singletonList(user));
        } catch (Exception ex) {
            logger.error("Error updating user status: ", ex);
            return new UserServiceResponseDto(false, "An error occurred: " + ex.getMessage(), Collections.emptyList());
        }
    }

    public List<Users> getAdminAndStaffUsers() {
        return usersRepository.findByRoleIn(List.of((byte) 1, (byte) 2,(byte) 3, (byte) 4));
    }

    public List<Users> getCustomerUsers() {
        return usersRepository.findByRoleIn(List.of((byte) 5, (byte) 6,(byte) 7, (byte) 8));
    }

    @Transactional
    public String updateUserRole(String userId, byte newRoleByte) {
        Users user = usersRepository.findById(userId).orElse(null);
        if (user == null) return null;

        Role currentRole = Role.fromValue(user.getRole());
        Role newRole = Role.fromValue(newRoleByte);
        if (!isValidRoleTransition(currentRole, newRole)) return null;

        double amount = getRoleUpgradeCost(newRole);
        if (amount == 0) return null;

        Wallets wallet = walletsService.getAvailableWallet(userId, amount);
        if (wallet == null) return null;

        // Cập nhật số dư ví
        boolean walletUpdated = walletsService.updateWalletBalance(wallet, amount);
        if (!walletUpdated) return null;

        // Cập nhật role user
        user.setRole(newRole.getValue());
        usersRepository.save(user);

        return wallet.getWalletId(); // ✅ Trả về walletId đã dùng
    }


    private boolean isValidRoleTransition(Role currentRole, Role newRole) {
        Map<Role, List<Role>> validTransitions = Map.of(
                Role.CUSTOMER_NORMAL, List.of(Role.CUSTOMER_READER, Role.CUSTOMER_AUTHOR, Role.CUSTOMER_VIP),
                Role.CUSTOMER_READER, List.of(Role.CUSTOMER_AUTHOR, Role.CUSTOMER_VIP),
                Role.CUSTOMER_AUTHOR, List.of(Role.CUSTOMER_VIP),
                Role.CUSTOMER_VIP, List.of()
        );
        return validTransitions.getOrDefault(currentRole, List.of()).contains(newRole);
    }

    private double getRoleUpgradeCost(Role newRole) {
        return switch (newRole) {
            case CUSTOMER_READER -> 30000;
            case CUSTOMER_AUTHOR -> 45000;
            case CUSTOMER_VIP -> 60000;
            default -> 0;
        };
    }
}
