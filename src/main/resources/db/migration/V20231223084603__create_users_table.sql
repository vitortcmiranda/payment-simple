CREATE TABLE IF NOT EXISTS users (
    id UUID NOT NULL PRIMARY KEY,
    first_name TEXT NOT NULL,
    last_name TEXT NOT NULL,
    document TEXT NOT NULL UNIQUE,
    user_type TEXT NOT NULL,
    email TEXT NOT NULL UNIQUE,
    balance NUMERIC(15, 2),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE
);

CREATE INDEX idx_users_document ON users(document);
CREATE INDEX idx_users_email ON users(email);
