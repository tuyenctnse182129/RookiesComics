package application.aicomic.controllers;

import application.aicomic.dataAccess.TransactionsDTO;
import application.aicomic.models.Transactions;
import application.aicomic.repositories.TransactionsRepository;
import application.aicomic.services.OrdersService;
import application.aicomic.services.TransactionsService;
import org.springframework.beans.factory.annotation.Autowired;
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

    @GetMapping("/getAll")
    public List<Transactions> getAllTransactions() {
        return transactionsService.getAllTransactions();
    }

    @PostMapping("/post")
    public Transactions addTransaction(@RequestBody Transactions transactions) {
        return transactionsService.addTransaction(transactions);
    }

    @PutMapping("/update")
    public Transactions updateTransaction(@PathVariable String id, @RequestBody TransactionsDTO transactionsDTO) {
        return transactionsService.updateTransaction(id, transactionsDTO);
    }

    @GetMapping("/getById")
    public Transactions getTransactionById(@PathVariable String id) {
        return transactionsService.getTransactionById(id);
    }

    @DeleteMapping("/delete")
    public Transactions deleteTransaction(@PathVariable String id) {
        return transactionsService.deleteTransaction(id);
    }
}