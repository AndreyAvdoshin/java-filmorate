package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorService;

@Slf4j
@RestController
@RequestMapping("/directors")
public class DirectorController extends Controller<Director> {
    private final DirectorService service;

    public DirectorController(DirectorService service) {
        super(service);
        this.service = service;
    }
}
