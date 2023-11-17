package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.Storage;

@Slf4j
@Service
public class GenreService extends BaseService<Genre> {

    public GenreService(Storage<Genre> storage) {
        super(storage);
    }

}
