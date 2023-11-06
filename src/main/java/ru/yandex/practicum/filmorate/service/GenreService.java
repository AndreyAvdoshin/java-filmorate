package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.Storage;

import java.util.List;

@Service
public class GenreService extends BaseService<Genre>{

    @Autowired
    public GenreService(@Qualifier("GenreDBStorage") Storage<Genre> storage) {
        super(storage);
    }

    public List<Genre> getGenres() {
        return storage.getAll();
    }

}
