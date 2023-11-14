package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.Storage;

@Slf4j
@Service
public class MpaService extends BaseService<Mpa> {

    @Autowired
    public MpaService(@Qualifier("MpaDbStorage") Storage<Mpa> storage) {
        super(storage);
    }

}
