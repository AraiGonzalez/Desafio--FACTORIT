-- ============================================================
-- SCHEMA.SQL — E-Commerce Cart Service
-- ============================================================

-- CLIENTES
CREATE TABLE IF NOT EXISTS customers (
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    name       VARCHAR(100)  NOT NULL,
    email      VARCHAR(150)  NOT NULL UNIQUE,
    is_vip     BOOLEAN       NOT NULL DEFAULT FALSE,
    created_at DATE          NOT NULL
);

-- TIPOS DE CARRITO
CREATE TABLE IF NOT EXISTS cart_types (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    code        VARCHAR(20)  NOT NULL UNIQUE,  -- COMMON, VIP, SPECIAL_DATE
    description VARCHAR(100) NOT NULL
);

-- CARRITOS
CREATE TABLE IF NOT EXISTS carts (
    id             BIGINT AUTO_INCREMENT PRIMARY KEY,
    customer_id    BIGINT NOT NULL,
    cart_type_id   BIGINT NOT NULL,
    status         VARCHAR(20) NOT NULL DEFAULT 'OPEN',
    simulated_date DATE,
    created_at     DATE NOT NULL,

    FOREIGN KEY (customer_id) REFERENCES customers(id),
    FOREIGN KEY (cart_type_id) REFERENCES cart_types(id)
);

-- PRODUCTOS
CREATE TABLE IF NOT EXISTS products (
    id    BIGINT AUTO_INCREMENT PRIMARY KEY,
    name  VARCHAR(150)   NOT NULL,
    price DECIMAL(10,2)  NOT NULL
);

-- FECHAS ESPECIALES
CREATE TABLE IF NOT EXISTS special_dates (
    id   BIGINT AUTO_INCREMENT PRIMARY KEY,
    date DATE NOT NULL UNIQUE
);

-- ITEMS DEL CARRITO
CREATE TABLE IF NOT EXISTS cart_items (
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    cart_id    BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    quantity   INT NOT NULL DEFAULT 1,
    CONSTRAINT chk_quantity CHECK (quantity > 0),

    FOREIGN KEY (cart_id) REFERENCES carts(id),
    FOREIGN KEY (product_id) REFERENCES products(id)
);

-- COMPRAS
CREATE TABLE IF NOT EXISTS purchases (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    customer_id   BIGINT NOT NULL,
    cart_id       BIGINT NOT NULL,
    total         DECIMAL(10,2) NOT NULL,
    purchase_date DATE NOT NULL,

    FOREIGN KEY (customer_id) REFERENCES customers(id),
    FOREIGN KEY (cart_id) REFERENCES carts(id)
);