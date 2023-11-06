package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ru.yandex.practicum.filmorate.model.Mpa;

import ru.yandex.practicum.filmorate.service.MpaService;

import java.util.List;

@RestController
@RequestMapping("/mpa")
public class MpaController extends Controller<Mpa>{

    private final MpaService service;

    @Autowired
    public MpaController(MpaService service) {
        super(service);
        this.service = service;
    }

    @GetMapping
    public List<Mpa> get() {
        return service.getMpa();
    }

}
