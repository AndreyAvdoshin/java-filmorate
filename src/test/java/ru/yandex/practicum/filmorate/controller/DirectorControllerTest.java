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
import ru.yandex.practicum.filmorate.model.Director;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class DirectorControllerTest {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    DirectorController directorController;
    Director director;

    @BeforeEach
    public void setUp() {
        director = Director.builder()
                .name("Новый режиссер")
                .build();
    }

    @Test
    void shouldGetDirectors() {
        directorController.create(director);

        Director director2 = Director.builder()
                .name("Второй режиссер")
                .build();
        directorController.create(director2);

        Assertions.assertNotNull(directorController.get(), "Список режиссеров пустой");
        Assertions.assertEquals(2, directorController.get().size(),
                "Запрос всех режиссеров возвращается неверно");
    }

    @Test
    void shouldCreateDirectorAndGet200() throws Exception {
        mockMvc.perform(post("/directors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(director)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Новый режиссер"));
    }

    @Test
    void shouldUpdateDirectorAndGet200() throws Exception {
        directorController.create(director);

        director.setId(1);
        director.setName("Иное имя режиссера");

        mockMvc.perform(put("/directors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(director)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Иное имя режиссера"));
    }

    @Test
    void shouldGet200WhenDeleteDirector() throws Exception {
        directorController.create(director);

        mockMvc.perform(delete("/directors/1"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldGet400WhenNameIsNull() throws Exception {
        director = Director.builder()
                .name("")
                .build();

        mockMvc.perform(post("/directors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(director)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldGet404WhenUpdateIncorrectId() throws Exception {
        directorController.create(director);

        director.setId(9999);
        mockMvc.perform(put("/directors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(director)))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldGet404WhenDeleteIncorrectId() throws Exception {
        directorController.create(director);

        mockMvc.perform(delete("/directors/9999"))
                .andExpect(status().isNotFound());
    }
}
