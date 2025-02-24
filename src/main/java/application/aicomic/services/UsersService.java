package application.aicomic.services;

import application.aicomic.dataAccess.UserServiceResponseDto;
import application.aicomic.enums.OrdersEnums;
import application.aicomic.enums.Role;
import application.aicomic.models.Orders;
import application.aicomic.models.Users;
import application.aicomic.repositories.UsersRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UsersService {
    private final UsersRepository usersRepository;
    private static final Logger logger = LoggerFactory.getLogger(UsersService.class);

    public UsersService(UsersRepository usersRepository) {
        this.usersRepository = usersRepository;
    }

    public List<Users> getAllUsers() {
        return usersRepository.findAll();
    }

    public Users getUserById(String userId) {
        return usersRepository.findById(userId).orElse(null);
    }

    public Users saveUser(Users user) {
        return usersRepository.save(user);
    }

    public UserServiceResponseDto getById(String id) {
        Optional<Users> userOptional = usersRepository.findById(id);
        if (userOptional.isPresent()) {
            Users user = userOptional.get();
            return new UserServiceResponseDto(true, "User found.", Collections.singletonList(user));
        }
        return new UserServiceResponseDto(false, "No user found for the given user ID.", Collections.emptyList());
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

    public boolean updateUserRole(String userId, byte newRoleByte) {
        Optional<Users> userOpt = usersRepository.findById(userId);
        if (userOpt.isPresent()) {
            Users user = userOpt.get();
            Role currentRole = Role.fromValue(user.getRole());
            Role newRole = Role.fromValue(newRoleByte);

            // Kiểm tra trạng thái hợp lệ
            if (!isValidRoleTransition(currentRole, newRole)) {
                return false; // Tránh cập nhật trạng thái sai logic
            }

            user.setRole(newRole.getValue());
            usersRepository.save(user);
            return true;
        }
        return false;
    }

    private boolean isValidRoleTransition(Role currentRole, Role newRole) {
        Map<Role, List<Role>> validTransitions = new HashMap<>();

        validTransitions.put(Role.CUSTOMER_NORMAL, List.of(Role.CUSTOMER_READER, Role.CUSTOMER_AUTHOR, Role.CUSTOMER_VIP));
        validTransitions.put(Role.CUSTOMER_READER, List.of(Role.CUSTOMER_AUTHOR, Role.CUSTOMER_VIP));
        validTransitions.put(Role.CUSTOMER_AUTHOR, List.of(Role.CUSTOMER_VIP));
        validTransitions.put(Role.CUSTOMER_VIP, List.of());

        return validTransitions.getOrDefault(currentRole, List.of()).contains(newRole);
    }

}
