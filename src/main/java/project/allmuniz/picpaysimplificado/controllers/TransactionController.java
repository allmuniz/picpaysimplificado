package project.allmuniz.picpaysimplificado.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import project.allmuniz.picpaysimplificado.domain.transaction.Transaction;
import project.allmuniz.picpaysimplificado.dtos.TransactionDTO;
import project.allmuniz.picpaysimplificado.services.TransactionService;

@RestController
@RequestMapping("/transaction")
@Tag(name = "Transaction", description = "Transactions Manager")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping
    @Operation(description = "Create",
            summary = "Transaction create")
    public ResponseEntity<Transaction> createTransaction(@RequestBody TransactionDTO transaction) throws Exception {
        Transaction newTransaction = this.transactionService.createTransaction(transaction);
        return new ResponseEntity<>(newTransaction, HttpStatus.OK);
    }
}
