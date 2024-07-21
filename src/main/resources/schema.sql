CREATE TABLE IF NOT EXISTS users (
    email VARCHAR(254) UNIQUE,
    password VARCHAR(128),
    salt VARCHAR(88)
);