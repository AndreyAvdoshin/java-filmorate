package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    UserController userController;

    User user;
    User user2;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .email("aaa@bbb.ccc")
                .login("login")
                .name("")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();
        user2 = User.builder()
                .email("aaa@bbb.eee")
                .login("PasLogin")
                .name("")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();
    }

    @Test
    void shouldGetUsers() {
        userController.create(user);
        userController.create(user2);

        Assertions.assertNotNull(userController.get(), "Список пользователей пустой");
        Assertions.assertEquals(2, userController.get().size(), "Неверное кол-во пользователей");
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
    void shouldGet200WhenDeleteUser() throws Exception {
        userController.create(user);

        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldGet404WhenDeleteIncorrectId() throws Exception {
        userController.create(user);

        mockMvc.perform(delete("/users/9999"))
                .andExpect(status().isNotFound());
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
        user = User.builder()
                .email("aaa@bbb.eee")
                .login("PasLogin")
                .name("")
                .birthday(LocalDate.of(3002, 1, 1))
                .build();

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
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldGet400WhenEmailIsIncorrect() throws Exception {
        user = User.builder()
                .email("bbb.ccc@")
                .login("PasLogin")
                .name("")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();

        mockMvc.perform(put("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldCreateFriendshipByUsersIds() throws Exception {
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk());

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user2)))
                .andExpect(status().isOk());

        mockMvc.perform(put("/users/1/friends/2")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void shouldGet400WhenIncorrectParametersCreateFriendship() throws Exception {
        mockMvc.perform(put("/users/-1/friends/2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        mockMvc.perform(put("/users/1/friends/-2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldDeleteFriendshipByUsersIds() throws Exception {
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk());

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user2)))
                .andExpect(status().isOk());

        mockMvc.perform(delete("/users/1/friends/2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void shouldGetFriendsByUserId() throws Exception {
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk());

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user2)))
                .andExpect(status().isOk());

        user2.setId(2);

        mockMvc.perform(put("/users/1/friends/2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        mockMvc.perform(get("/users/1/friends"))
                .andExpect(status().isOk())
                // Ожидаем, что возвращенный JSON содержит ожидаемый список друзей
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].email").value("aaa@bbb.eee"))
                .andExpect(jsonPath("$[0].login").value("PasLogin"))
                .andExpect(jsonPath("$[0].name").value("PasLogin"))
                .andExpect(jsonPath("$[0].birthday").value("2000-01-01"));
    }

}