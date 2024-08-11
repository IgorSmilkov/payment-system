CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    status VARCHAR(50) NOT NULL
);

CREATE TABLE roles (
    id SERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL
);

CREATE TABLE user_roles (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY(user_id, role_id),
    FOREIGN KEY(user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY(role_id) REFERENCES roles(id) ON DELETE CASCADE
);

CREATE TABLE merchants (
    id SERIAL PRIMARY KEY,
    description VARCHAR(255) NOT NULL,
    total_transaction_sum NUMERIC(19,2) DEFAULT 0.00 NOT NULL,
    user_id BIGINT UNIQUE NOT NULL,
    FOREIGN KEY(user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE transactions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    amount NUMERIC(19,2),
    status VARCHAR(50) NOT NULL,
    customer_email VARCHAR(255) NOT NULL,
    customer_phone VARCHAR(20) NOT NULL,
    merchant_id BIGINT NOT NULL,
    reference_id UUID,
    transaction_type VARCHAR(50) NOT NULL,
    FOREIGN KEY(merchant_id) REFERENCES merchants(id),
    FOREIGN KEY(reference_id) REFERENCES transactions(id) ON DELETE CASCADE
);
