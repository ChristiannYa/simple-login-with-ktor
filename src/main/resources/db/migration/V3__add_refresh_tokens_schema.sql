CREATE TABLE refresh_tokens
(
    id           UUID PRIMARY KEY,
    user_id      UUID        NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    hash         VARCHAR(64) NOT NULL UNIQUE,
    expires_at   TIMESTAMP   NOT NULL,
    created_at   TIMESTAMP   NOT NULL,
    last_used_at TIMESTAMP,
    revoked_at   TIMESTAMP
);