package application.aicomic.services;

import application.aicomic.dataAccess.CommentsDTO;
import application.aicomic.dataAccess.TransactionsDTO;
import application.aicomic.enums.CommentsEnums;
import application.aicomic.enums.TransactionsEnums;
import application.aicomic.enums.WalletType;
import application.aicomic.mapper.Mapper;
import application.aicomic.models.Comments;
import application.aicomic.models.Orders;
import application.aicomic.models.Transactions;
import application.aicomic.models.Wallets;
import application.aicomic.repositories.OrdersRepository;
import application.aicomic.repositories.TransactionsRepository;
import application.aicomic.repositories.WalletsRepository;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class TransactionsService {
    private TransactionsRepository transactionsRepository;
    private Mapper mapper;
    private static final Logger logger = LoggerFactory.getLogger(TransactionsService.class);
    private final OrdersRepository ordersRepository;

    @Autowired
    public TransactionsService(TransactionsRepository transactionsRepository,Mapper mapper, OrdersRepository ordersRepository) {
        this.transactionsRepository = transactionsRepository;
        this.mapper = mapper;
        this.ordersRepository = ordersRepository;
    }

    public List<Transactions> getAllTransactions() {
        return transactionsRepository.findAll();
    }

    public Transactions getTransactionById(String id) {
        return transactionsRepository.findById(id).get();
    }

    @Transactional
    public Transactions addTransaction(Transactions transaction) {
        return transactionsRepository.save(transaction);
    }

    public Transactions updateTransaction(String id, TransactionsDTO transactionsDTO) {
        Transactions transactions = transactionsRepository.findById(id).orElseThrow(() -> new RuntimeException("Transaction not found"));
        mapper.updateTransactions(transactions, transactionsDTO);
        return transactionsRepository.save(transactions);
    }

    public Transactions deleteTransaction(String id) {
        Optional<Transactions> transactions = transactionsRepository.findById(id);
        if (transactions.isPresent()) {
            Transactions x = transactions.get();
            x.setStatus(TransactionsEnums.NOT_PAID.getValue());
            return transactionsRepository.save(x);
        }
        return null;
    }
}
