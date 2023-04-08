DROP TABLE IF EXISTS USERS CASCADE;
DROP TABLE IF EXISTS GENRE CASCADE;
DROP TABLE IF EXISTS MPA CASCADE;
DROP TABLE IF EXISTS FILM CASCADE;
DROP TABLE IF EXISTS USER_FRIEND CASCADE;
DROP TABLE IF EXISTS LIKES_MOVIE CASCADE;
DROP TABLE IF EXISTS GENRE_FILM CASCADE;
DROP TABLE IF EXISTS DIRECTORS CASCADE;
DROP TABLE IF EXISTS DIRECTORS_FILMS CASCADE;
DROP TABLE IF EXISTS REVIEWS CASCADE;
DROP TABLE IF EXISTS REVIEWS_RATINGS CASCADE;
DROP TABLE IF EXISTS FEED CASCADE;

CREATE TABLE if not exists DIRECTORS (
      director_id   BIGINT not null GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
      name_director varchar(255) not null
  );

CREATE TABLE if not exists USERS (
    user_id  BIGINT not null GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    email    varchar(255) not null,
    login    varchar(255) not null,
    name     varchar(255),
    birthday date
  );

CREATE TABLE if not exists GENRE (
      genre_id   BIGINT not null GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
      name_genre varchar(255) not null
  );

CREATE TABLE if not exists MPA (
      mpa_id   BIGINT not null GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
      name_mpa varchar(255) not null
  );

CREATE TABLE if not exists FILM (
    film_id      BIGINT not null GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name         varchar(255) not null,
    description  varchar(200) not null,
    release_date date not null,
    duration     int not null,
    rate         int,
    mpa_id       BIGINT references MPA(mpa_id)
);
CREATE TABLE if not exists REVIEWS
(
    review_id    BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    content      VARCHAR(255) NOT NULL,
    is_positive  BOOLEAN NOT NULL,
    user_id      BIGINT NOT NULL REFERENCES USERS(user_id) ON DELETE CASCADE,
    film_id      BIGINT NOT NULL REFERENCES FILM(film_id) ON DELETE CASCADE,
    useful       BIGINT
);
CREATE TABLE IF NOT EXISTS REVIEWS_RATINGS
(
    review_id    BIGINT NOT NULL REFERENCES REVIEWS(review_id) ON DELETE CASCADE,
    user_id      BIGINT NOT NULL REFERENCES USERS(user_id) ON DELETE CASCADE,
    rate         INT,
    PRIMARY KEY (review_id, user_id)
);

CREATE TABLE if not exists USER_FRIEND (
    friend_id BIGINT not null references USERS(user_id) ON DELETE CASCADE,
    user_id   BIGINT not null references USERS(user_id) ON DELETE CASCADE,

    PRIMARY KEY(friend_id,user_id)
);

CREATE TABLE if not exists LIKES_MOVIE (
    film_id BIGINT not null references FILM(film_id) ON DELETE CASCADE,
    user_id BIGINT not null references USERS(user_id) ON DELETE CASCADE,

    PRIMARY KEY(film_id,user_id)
);

CREATE TABLE if not exists GENRE_FILM (
    genre_id BIGINT not null references GENRE(genre_id) ON DELETE CASCADE,
    film_id  BIGINT not null references FILM(film_id) ON DELETE CASCADE,

    PRIMARY KEY(genre_id,film_id)
);
CREATE TABLE if not exists DIRECTORS_FILMS (
    director_id BIGINT not null references DIRECTORS(director_id) ON DELETE CASCADE,
    film_id     BIGINT not null references FILM(film_id) ON DELETE CASCADE,

    PRIMARY KEY(director_id,film_id)
);
CREATE TABLE if not exists FEED (
    event_id   BIGINT not null GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    time_stamp BIGINT,
    user_id    BIGINT REFERENCES USERS(user_id) ON DELETE CASCADE,
    event_type ENUM('LIKE', 'REVIEW', 'FRIEND'),
    operation  ENUM('REMOVE', 'ADD', 'UPDATE'),
    entity_id  BIGINT
)

