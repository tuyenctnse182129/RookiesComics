package application.aicomic.controllers;

import application.aicomic.dataAccess.TransactionRequest;
import application.aicomic.dataAccess.TransactionsDTO;
import application.aicomic.enums.TransactionsEnums;
import application.aicomic.models.Transactions;
import application.aicomic.repositories.TransactionsRepository;
import application.aicomic.services.OrdersService;
import application.aicomic.services.TransactionsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/transaction")
@RestController
public class TransactionsController {
    @Autowired
    private TransactionsService transactionsService;
    @Autowired
    private TransactionsRepository transactionsRepository;
    @Autowired
    private OrdersService orderService;

    // Inject TransactionsService
    public TransactionsController(TransactionsService transactionsService, TransactionsRepository transactionsRepository, OrdersService orderService) {
        this.transactionsService = transactionsService;
        this.transactionsRepository = transactionsRepository;
        this.orderService = orderService;
    }

    @GetMapping
    public List<Transactions> getAllTransactions() {
        return transactionsService.getAllTransactions();
    }

    @PostMapping
    public Transactions addTransaction(@RequestBody Transactions transactions) {
        return transactionsService.addTransaction(transactions);
    }

    @PutMapping("/{id}")
    public Transactions updateTransaction(@PathVariable String id, @RequestBody TransactionsDTO transactionsDTO) {
        return transactionsService.updateTransaction(id, transactionsDTO);
    }

    @GetMapping("/{id}")
    public Transactions getTransactionById(@PathVariable String id) {
        return transactionsService.getTransactionById(id);
    }

    @DeleteMapping("/{id}")
    public Transactions deleteTransaction(@PathVariable String id) {
        return transactionsService.deleteTransaction(id);
    }

}