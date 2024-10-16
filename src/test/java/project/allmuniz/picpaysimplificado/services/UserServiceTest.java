package project.allmuniz.picpaysimplificado.services;

import project.allmuniz.picpaysimplificado.domain.user.User;
import project.allmuniz.picpaysimplificado.domain.user.UserType;
import project.allmuniz.picpaysimplificado.dtos.UserDTO;
import project.allmuniz.picpaysimplificado.repositories.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCreateUser() {
        UserDTO userDTO = new UserDTO("Allan", "Muniz", "123456789", new BigDecimal("1000.00"), "allan.muniz@example.com", "password123", UserType.COMMON);
        User user = new User(userDTO);

        when(userRepository.save(any(User.class))).thenReturn(user);

        User createdUser = userService.createUser(userDTO);

        assertNotNull(createdUser);
        assertEquals("Allan", createdUser.getFirstName());
        assertEquals("Muniz", createdUser.getLastName());
        assertEquals("123456789", createdUser.getDocument());
        assertEquals(new BigDecimal("1000.00"), createdUser.getBalance());
        assertEquals("allan.muniz@example.com", createdUser.getEmail());
        assertEquals("password123", createdUser.getPassword());
        assertEquals(UserType.COMMON, createdUser.getUserType());

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    public void testFindUserByIdUserFound() throws Exception {
        Long userId = 1L;
        User user = new User(new UserDTO("Allan", "Muniz", "123456789", new BigDecimal("1000.00"), "allan.muniz@example.com", "password123", UserType.COMMON));

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        User foundUser = userService.findUserById(userId);

        assertNotNull(foundUser);
        assertEquals("Allan", foundUser.getFirstName());
        assertEquals("Muniz", foundUser.getLastName());
        assertEquals("123456789", foundUser.getDocument());
        assertEquals(new BigDecimal("1000.00"), foundUser.getBalance());
        assertEquals("allan.muniz@example.com", foundUser.getEmail());
        assertEquals("password123", foundUser.getPassword());
        assertEquals(UserType.COMMON, foundUser.getUserType());

        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    public void testFindUserByIdUserNotFound() {
        Long userId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(Exception.class, () -> {
            userService.findUserById(userId);
        });

        assertEquals("Usuário não encontrado", exception.getMessage());

        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    public void testGetAllUsersReturnsListOfUsers() {
        User user1 = new User(new UserDTO("Allan", "Muniz", "123456789", new BigDecimal("1000.00"), "allan.muniz@example.com", "password123", UserType.COMMON));
        User user2 = new User(new UserDTO("Maria", "Silva", "987654321", new BigDecimal("500.00"), "maria.silva@example.com", "password456", UserType.MERCHANT));

        List<User> userList = Arrays.asList(user1, user2);

        when(userRepository.findAll()).thenReturn(userList);

        List<User> result = userService.getAllUsers();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Allan", result.get(0).getFirstName());
        assertEquals("Maria", result.get(1).getFirstName());

        verify(userRepository, times(1)).findAll();
    }

    @Test
    public void testGetAllUsers_ReturnsEmptyList() {
        when(userRepository.findAll()).thenReturn(Arrays.asList());

        List<User> result = userService.getAllUsers();

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(userRepository, times(1)).findAll();
    }

    @Test
    public void testValidateTransactionSuccessfulTransaction() throws Exception {
        User sender = new User(new UserDTO("Allan", "Muniz", "123456789", new BigDecimal("1000.00"), "allan.muniz@example.com", "password123", UserType.COMMON));
        BigDecimal amount = new BigDecimal("500.00");

        userService.validateTransaction(sender, amount);
    }

    @Test
    public void testValidateTransactionInsufficientBalance() {
        User sender = new User(new UserDTO("Allan", "Muniz", "123456789", new BigDecimal("100.00"), "allan.muniz@example.com", "password123", UserType.COMMON));
        BigDecimal amount = new BigDecimal("500.00");

        Exception exception = assertThrows(Exception.class, () -> {
            userService.validateTransaction(sender, amount);
        });

        assertEquals("Saldo insuficiente", exception.getMessage());
    }

    @Test
    public void testValidateTransactionUserIsMerchant() {
        User sender = new User(new UserDTO("Maria", "Silva", "987654321", new BigDecimal("1000.00"), "maria.silva@example.com", "password456", UserType.MERCHANT));
        BigDecimal amount = new BigDecimal("500.00");

        Exception exception = assertThrows(Exception.class, () -> {
            userService.validateTransaction(sender, amount);
        });

        assertEquals("Usuário do tipo Lojista não está autorizado a realizar transação", exception.getMessage());
    }

    @Test
    public void testValidateTransactionUserIsMerchantInsufficientBalance() {
        User sender = new User(new UserDTO("Maria", "Silva", "987654321", new BigDecimal("100.00"), "maria.silva@example.com", "password456", UserType.MERCHANT));
        BigDecimal amount = new BigDecimal("500.00");

        Exception exception = assertThrows(Exception.class, () -> {
            userService.validateTransaction(sender, amount);
        });

        assertEquals("Usuário do tipo Lojista não está autorizado a realizar transação", exception.getMessage());
    }
}
