package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.Storage;

@Slf4j
@Service
public class DirectorService extends BaseService<Director> {
    public DirectorService(Storage<Director> storage) {
        super(storage);
    }

    public void delete(Integer id) {
        storage.delete(id);
    }
}
