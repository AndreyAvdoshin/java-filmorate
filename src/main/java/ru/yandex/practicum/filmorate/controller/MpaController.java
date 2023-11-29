package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.MpaService;

@RestController
@RequestMapping("/mpa")
public class MpaController extends Controller<Mpa> {

    private final MpaService service;

    public MpaController(MpaService service) {
        super(service);
        this.service = service;
    }

    @Override
    public void delete(int id) {
    }
}
