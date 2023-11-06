package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.Storage;

import java.util.List;

@Service
public class MpaService extends BaseService<Mpa> {

    @Autowired
    public MpaService(@Qualifier("MpaDBStorage") Storage<Mpa> storage) {
        super(storage);
    }

    public List<Mpa> getMpa() {
        return storage.getAll();
    }
}
