package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.Storage;

@Slf4j
@Service
public class MpaService extends BaseService<Mpa> {
    public MpaService(Storage<Mpa> storage) {
        super(storage);
    }

}
