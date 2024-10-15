package project.allmuniz.picpaysimplificado.services;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import project.allmuniz.picpaysimplificado.domain.transaction.Transaction;
import project.allmuniz.picpaysimplificado.domain.user.User;
import project.allmuniz.picpaysimplificado.dtos.TransactionDTO;
import project.allmuniz.picpaysimplificado.repositories.TransactionRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

@Service
public class TransactionService {

    private final UserService userService;
    private final TransactionRepository transactionRepository;
    private final RestTemplate restTemplate;
    private final NotificationService notificationService;

    public TransactionService(UserService userService, TransactionRepository transactionRepository, RestTemplate restTemplate, NotificationService notificationService) {
        this.userService = userService;
        this.transactionRepository = transactionRepository;
        this.restTemplate = restTemplate;
        this.notificationService = notificationService;
    }

    public Transaction createTransaction(TransactionDTO transaction) throws Exception {
        User sender = userService.findUserById(transaction.senderId());
        User receiver = userService.findUserById(transaction.receiverId());

        userService.validateTransaction(sender, transaction.value());

        boolean isAuthorized = this.authorizeTransaction(sender, transaction.value());
        if (!isAuthorized) {
            throw new Exception("Transaction not authorized");
        }

        Transaction newTransaction = new Transaction();
        newTransaction.setAmount(transaction.value());
        newTransaction.setSender(sender);
        newTransaction.setReceiver(receiver);
        newTransaction.setTimeStamp(LocalDateTime.now());

        sender.setBalance(sender.getBalance().subtract(transaction.value()));
        receiver.setBalance(receiver.getBalance().add(transaction.value()));

        this.transactionRepository.save(newTransaction);
        this.userService.saveUser(sender);
        this.userService.saveUser(receiver);

        this.notificationService.sendNotification(sender, "Transaction completed sucessfully");
        this.notificationService.sendNotification(receiver, "Transaction received sucessfully");

        return newTransaction;
    }

    public boolean authorizeTransaction(User sender, BigDecimal value) throws Exception {
        ResponseEntity<Map> authorizationResponse = restTemplate.getForEntity("https://util.devi.tools/api/v2/authorize", Map.class);
        return authorizationResponse.getStatusCode() == HttpStatus.OK;
    }
}
