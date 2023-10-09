package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController extends Controller<User> {

    @Override
    public User create(@Valid @RequestBody User user) {
        // проверка на пустое имя
        if (user.getName() == null) {
            user.setName(user.getLogin());
        }
        super.create(user);
        return user;
    }
}
