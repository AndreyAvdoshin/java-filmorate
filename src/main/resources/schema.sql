DROP TABLE likes IF EXISTS;
DROP TABLE friendship IF EXISTS;
DROP TABLE film_genre IF EXISTS;
DROP TABLE films IF EXISTS;
DROP TABLE mpa IF EXISTS;
DROP TABLE genres IF EXISTS;
DROP TABLE users IF EXISTS;



CREATE TABLE IF NOT EXISTS users (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    login VARCHAR(100) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    birthday DATE,
    created TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS genres (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    created TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO genres (name) VALUES ('Комедия');
INSERT INTO genres (name) VALUES ('Драма');
INSERT INTO genres (name) VALUES ('Мультфильм');
INSERT INTO genres (name) VALUES ('Триллер');
INSERT INTO genres (name) VALUES ('Документальный');
INSERT INTO genres (name) VALUES ('Боевик');

CREATE TABLE IF NOT EXISTS mpa (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    created TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO mpa (name) VALUES ('G');
INSERT INTO mpa (name) VALUES ('PG');
INSERT INTO mpa (name) VALUES ('PG-13');
INSERT INTO mpa (name) VALUES ('R');
INSERT INTO mpa (name) VALUES ('NC-17');

CREATE TABLE IF NOT EXISTS films (
	id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
	title VARCHAR(200) NOT NULL,
	description VARCHAR(200) NOT NULL,
	release_date DATE NOT NULL,
	duration INTEGER NOT NULL,
	mpa_id INTEGER NOT NULL REFERENCES mpa (id),
	created TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);


CREATE TABLE IF NOT EXISTS film_genre (
	id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
	film_id BIGINT REFERENCES films (id),
	genre_id INTEGER REFERENCES genres (id)
);

CREATE TABLE IF NOT EXISTS friendship (
	id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
	user_id BIGINT REFERENCES users (id),
	friend_id BIGINT REFERENCES users (id),
	created TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS likes (
	id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
	user_id BIGINT REFERENCES users (id),
	films_id BIGINT REFERENCES films (id)
);