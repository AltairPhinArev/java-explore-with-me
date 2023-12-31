drop table if exists requests;
drop table if exists events_compilations;
drop table if exists compilations;
drop table if exists comments;
drop table if exists events;
drop table if exists users;
drop table if exists categories;

CREATE TABLE IF NOT EXISTS users
(
    id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    email varchar(256) UNIQUE,
    login varchar(256)
);

CREATE TABLE IF NOT EXISTS categories
(
    id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name varchar(128) UNIQUE
);

CREATE TABLE IF NOT EXISTS events
(
    id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    annotation VARCHAR(2000) NOT NULL,
    category_id BIGINT NOT NULL,
    confirmed_requests INTEGER,
    created TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    description VARCHAR(7000) NOT NULL,
    event_date TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    initiator_id BIGINT NOT NULL,
    location_lat REAL NOT NULL,
    location_lon REAL NOT NULL,
    paid BOOL NOT NULL,
    participant_limit INTEGER,
    published_on TIMESTAMP WITHOUT TIME ZONE,
    request_moderation BOOL,
    state VARCHAR(10) NOT NULL,
    title VARCHAR(128) NOT NULL,
    views BIGINT,

    CONSTRAINT pk_event PRIMARY KEY (id),
    FOREIGN KEY (category_id)
    REFERENCES categories (id) ON DELETE CASCADE,
    FOREIGN KEY (initiator_id)
    REFERENCES users (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS requests
(
    id           BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    event_id     BIGINT NOT NULL,
    requester_id BIGINT NOT NULL,
    created      TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    status       VARCHAR(10) NOT NULL,

    CONSTRAINT pk_request PRIMARY KEY (id),
    CONSTRAINT UQ_EVENT_WITH_REQUESTER UNIQUE (event_id, requester_id),
    FOREIGN KEY (event_id)
    REFERENCES events (id) ON DELETE CASCADE,
    FOREIGN KEY (requester_id)
    REFERENCES users (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS compilations
(
    id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    title VARCHAR(128) NOT NULL,
    pinned BOOL NOT NULL,

    CONSTRAINT pk_compilations PRIMARY KEY (id),
    CONSTRAINT UQ_COMPILATIONS_TITLE UNIQUE (title)
);

CREATE TABLE IF NOT EXISTS events_compilations
(
    event_id BIGINT REFERENCES events (id) ON DELETE CASCADE,
    compilation_id BIGINT REFERENCES compilations (id) ON DELETE CASCADE,

    PRIMARY KEY (event_id, compilation_id),
    CONSTRAINT UQ_EVENT_WITH_compilation UNIQUE (event_id, compilation_id)
);

CREATE TABLE IF NOT EXISTS comments
(
  comment_id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
  user_id BIGINT REFERENCES users (id),
  event_id BIGINT REFERENCES events (id),
  description varchar(512),
  created_On TIMESTAMP WITHOUT TIME ZONE NOT NULL,

  CONSTRAINT comment_id PRIMARY KEY (comment_id),
  FOREIGN KEY (event_id) REFERENCES events (id),
  FOREIGN KEY (user_id) REFERENCES users (id)
);