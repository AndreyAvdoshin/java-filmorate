package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class UserControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    UserController userController;

    User user;

    @BeforeEach
    void setUp() {
        user = new User("aaa@bbb.ccc", "login", LocalDate.of(2000, 1, 1));
    }

    @Test
    void shouldGetUsers() {
        userController.createUser(user);

        User user2 = new User("aaa@bbb.eee", "PasLogin", LocalDate.of(2002, 1, 1));
        userController.createUser(user2);

        Assertions.assertNotNull(userController.getUsers(), "Список пользователей пустой");
        Assertions.assertEquals(2, userController.getUsers().size(), "Неверное кол-во пользователей");
    }

    @Test
    void shouldCreateUserAndGet200() throws Exception {
        user.setName("Andrei");
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Andrei"))
                .andExpect(jsonPath("$.email").value("aaa@bbb.ccc"))
                .andExpect(jsonPath("$.login").value("login"))
                .andExpect(jsonPath("$.birthday").value("2000-01-01"));
    }

    @Test
    void shouldUpdateUserAndGet200() throws Exception {
        user.setName("Ivan");
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk());

        user.setId(1);
        user.setName("Andrei");

        mockMvc.perform(put("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Andrei"))
                .andExpect(jsonPath("$.email").value("aaa@bbb.ccc"))
                .andExpect(jsonPath("$.login").value("login"))
                .andExpect(jsonPath("$.birthday").value("2000-01-01"));
    }

    @Test
    void shouldCreateUserWithEmptyNameAndGet200() throws Exception {
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("login"))
                .andExpect(jsonPath("$.email").value("aaa@bbb.ccc"))
                .andExpect(jsonPath("$.login").value("login"))
                .andExpect(jsonPath("$.birthday").value("2000-01-01"));
    }

    @Test
    void shouldGet400WhenBirthdayIsIncorrect() throws Exception {
        user = new User("aaa@bbb.eee", "PasLogin", LocalDate.of(3002, 1, 1));

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldGet500WhenUpdateIncorrectId() throws Exception {
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk());

        user.setName("Andrei");
        user.setId(9999);

        mockMvc.perform(put("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void shouldGet400WhenEmailIsIncorrect() throws Exception {
        user = new User("bbb.ccc@", "login", LocalDate.of(2000, 1, 1));

        mockMvc.perform(put("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isBadRequest());
    }
}