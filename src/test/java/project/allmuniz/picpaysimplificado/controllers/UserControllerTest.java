package project.allmuniz.picpaysimplificado.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import project.allmuniz.picpaysimplificado.domain.user.User;
import project.allmuniz.picpaysimplificado.domain.user.UserType;
import project.allmuniz.picpaysimplificado.dtos.UserDTO;
import project.allmuniz.picpaysimplificado.services.UserService;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

public class UserControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();

        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    public void testCreateUserSuccess() throws Exception {
        UserDTO userDTO = new UserDTO("Allan", "Muniz", "123456789", BigDecimal.valueOf(1000.00), "allan.muniz@example.com", "password123", UserType.COMMON);
        User newUser = new User(userDTO);

        when(userService.createUser(any(UserDTO.class))).thenReturn(newUser);

        String jsonRequest = objectMapper.writeValueAsString(userDTO);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(newUser)));

        verify(userService, times(1)).createUser(any(UserDTO.class));
    }

    @Test
    public void testGetAllUsersSuccess() throws Exception {
        User user1 = new User(new UserDTO("Allan", "Muniz", "123456789", BigDecimal.valueOf(1000.00), "allan.muniz@example.com", "password123", UserType.COMMON));
        User user2 = new User(new UserDTO("Maria", "Silva", "987654321", BigDecimal.valueOf(2000.00), "maria.silva@example.com", "password456", UserType.COMMON));
        List<User> users = Arrays.asList(user1, user2);

        when(userService.getAllUsers()).thenReturn(users);

        mockMvc.perform(get("/users")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(users)));

        verify(userService, times(1)).getAllUsers();
    }
}

