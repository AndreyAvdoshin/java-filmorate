package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.Storage;

@Slf4j
@Service
public class GenreService extends BaseService<Genre> {

    @Autowired
    public GenreService(@Qualifier("GenreDbStorage") Storage<Genre> storage) {
        super(storage);
    }

}
