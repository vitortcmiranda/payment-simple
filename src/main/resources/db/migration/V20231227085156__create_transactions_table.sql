CREATE TABLE IF NOT EXISTS transactions (
    id UUID NOT NULL PRIMARY KEY,
    sender_id UUID NOT NULL,
    receiver_id UUID NOT NULL,
    amount NUMERIC(15, 2),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE
);

