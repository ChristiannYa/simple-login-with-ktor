CREATE TYPE user_type AS ENUM ('user', 'contributor', 'admin');
CREATE TYPE task_priority AS ENUM('low', 'medium', 'high', 'vital');

CREATE TABLE users
(
    id            UUID PRIMARY KEY UNIQUE NOT NULL,
    name          VARCHAR(50)             NOT NULL,
    email         VARCHAR(120) UNIQUE     NOT NULL,
    password_hash VARCHAR(64)             NOT NULL,
    user_type     user_type,
    is_premium    BOOLEAN                 NOT NULL DEFAULT false,
    created_at    TIMESTAMP               NOT NULL DEFAULT NOW()
);

CREATE TABLE tasks
(
    id          SERIAL PRIMARY KEY,
    user_id     UUID REFERENCES users (id),
    name        VARCHAR(50),
    description VARCHAR(100),
    priority    task_priority,
    created_at  TIMESTAMP NOT NULL DEFAULT NOW()
);
