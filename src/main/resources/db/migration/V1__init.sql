-- User table
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255),
    email VARCHAR(255) UNIQUE,
    password VARCHAR(255),
    role VARCHAR(50),
    is_active BOOLEAN DEFAULT TRUE
);

-- Product table
CREATE TABLE products (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255),
    description TEXT,
    price DOUBLE PRECISION,
    stock DOUBLE PRECISION,
    category VARCHAR(100),
    vendor_id BIGINT,
    CONSTRAINT fk_vendor FOREIGN KEY (vendor_id) REFERENCES users(id)
);

-- Order table
CREATE TABLE orders (
    id BIGSERIAL PRIMARY KEY,
    order_status VARCHAR(50),
    buyer_id BIGINT,
    total_amount DOUBLE PRECISION,
    CONSTRAINT fk_buyer FOREIGN KEY (buyer_id) REFERENCES users(id)
);

-- OrderItem table
CREATE TABLE order_items (
    id BIGSERIAL PRIMARY KEY,
    order_id BIGINT,
    product_id BIGINT,
    quantity INTEGER,
    CONSTRAINT fk_order FOREIGN KEY (order_id) REFERENCES orders(id),
    CONSTRAINT fk_product FOREIGN KEY (product_id) REFERENCES products(id)
);

-- PasswordResetToken table
CREATE TABLE password_reset_tokens (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255),
    token VARCHAR(255),
    expiry_date TIMESTAMP
);