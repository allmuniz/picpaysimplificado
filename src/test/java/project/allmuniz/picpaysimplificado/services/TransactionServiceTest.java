package project.allmuniz.picpaysimplificado.services;

import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import project.allmuniz.picpaysimplificado.domain.transaction.Transaction;
import project.allmuniz.picpaysimplificado.domain.user.User;
import project.allmuniz.picpaysimplificado.domain.user.UserType;
import project.allmuniz.picpaysimplificado.dtos.TransactionDTO;
import project.allmuniz.picpaysimplificado.dtos.UserDTO;
import project.allmuniz.picpaysimplificado.repositories.TransactionRepository;

import java.math.BigDecimal;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class TransactionServiceTest {

    @Mock
    private UserService userService;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private NotificationService notificationService;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private TransactionService transactionService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCreateTransactionSuccessfulTransaction() throws Exception {
        TransactionDTO transactionDTO = new TransactionDTO(new BigDecimal("100.00"), 1L, 2L);
        User sender = new User(new UserDTO("Allan", "Muniz", "123456789", new BigDecimal("200.00"), "allan.muniz@example.com", "password123", UserType.COMMON));
        User receiver = new User(new UserDTO("Maria", "Silva", "987654321", new BigDecimal("100.00"), "maria.silva@example.com", "password456", UserType.COMMON));

        when(userService.findUserById(transactionDTO.senderId())).thenReturn(sender);
        when(userService.findUserById(transactionDTO.receiverId())).thenReturn(receiver);
        when(restTemplate.getForEntity(any(String.class), eq(Map.class))).thenReturn(new ResponseEntity<>(HttpStatus.OK));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Transaction createdTransaction = transactionService.createTransaction(transactionDTO);

        assertNotNull(createdTransaction);
        assertEquals(transactionDTO.value(), createdTransaction.getAmount());
        assertEquals(sender.getBalance(), new BigDecimal("100.00"));
        assertEquals(receiver.getBalance(), new BigDecimal("200.00"));

        verify(transactionRepository, times(1)).save(any(Transaction.class));
        verify(userService, times(1)).saveUser(sender);
        verify(userService, times(1)).saveUser(receiver);
        verify(notificationService, times(1)).sendNotification(sender, "Transaction completed sucessfully");
        verify(notificationService, times(1)).sendNotification(receiver, "Transaction received sucessfully");
    }

    @SneakyThrows
    @Test
    public void testCreateTransactionUserNotFound() {
        TransactionDTO transactionDTO = new TransactionDTO(new BigDecimal("100.00"), 1L, 2L);
        when(userService.findUserById(transactionDTO.senderId())).thenThrow(new Exception("Usuário não encontrado"));

        Exception exception = assertThrows(Exception.class, () -> {
            transactionService.createTransaction(transactionDTO);
        });

        assertEquals("Usuário não encontrado", exception.getMessage());
    }

    @Test
    public void testCreateTransactionUnauthorizedTransaction() throws Exception {
        TransactionDTO transactionDTO = new TransactionDTO(new BigDecimal("100.00"), 1L, 2L);
        User sender = new User(new UserDTO("Allan", "Muniz", "123456789", new BigDecimal("200.00"), "allan.muniz@example.com", "password123", UserType.COMMON));
        User receiver = new User(new UserDTO("Maria", "Silva", "987654321", new BigDecimal("100.00"), "maria.silva@example.com", "password456", UserType.COMMON));

        when(userService.findUserById(transactionDTO.senderId())).thenReturn(sender);
        when(userService.findUserById(transactionDTO.receiverId())).thenReturn(receiver);
        when(restTemplate.getForEntity(any(String.class), eq(Map.class))).thenReturn(new ResponseEntity<>(HttpStatus.UNAUTHORIZED));

        Exception exception = assertThrows(Exception.class, () -> {
            transactionService.createTransaction(transactionDTO);
        });

        assertEquals("Transaction not authorized", exception.getMessage());
    }
}
