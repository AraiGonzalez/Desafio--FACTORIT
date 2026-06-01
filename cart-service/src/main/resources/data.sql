-- ============================================================
-- DATA.SQL — E-Commerce Cart Service
-- ============================================================

-- CLIENTES
INSERT INTO customers (name, email, is_vip, created_at) VALUES
('Juan Perez',    'juan@mail.com',    FALSE, '2024-01-15'),  -- id 1
('Maria Lopez',   'maria@mail.com',   FALSE,  '2024-01-15'),  -- id 2
('Carlos Ruiz',   'carlos@mail.com',  FALSE, '2024-02-10'),  -- id 3
('Ana Gomez',     'ana@mail.com',     FALSE,  '2024-02-10'),  -- id 4
('Pedro Torres',  'pedro@mail.com',   FALSE, '2024-03-05'),  -- id 5
('Laura Sanchez', 'laura@mail.com',   FALSE,  '2024-03-05'),  -- id 6
('Diego Flores',  'diego@mail.com',   FALSE, '2024-04-20'),  -- id 7
('Sofia Mendez',  'sofia@mail.com',   FALSE,  '2024-04-20');  -- id 8

-- PRODUCTOS
INSERT INTO products (name, price) VALUES
('Notebook HP EliteBook',    150000.00),  -- id 1
('Mouse Logitech MX Master',   3500.00),  -- id 2
('Teclado Mecanico Redragon',  8900.00),  -- id 3
('Monitor LG 24"',            75000.00),  -- id 4
('Auriculares Sony WH-1000',  12000.00);  -- id 5

-- FECHAS ESPECIALES
INSERT INTO special_dates (date) VALUES
('2024-12-25'),
('2024-12-31'),
('2025-01-01'),
('2025-05-25'),
('2025-07-09'),
('2025-10-31'),
('2025-11-11'),
('2025-12-25'),
('2025-12-31');

-- TIPOS DE CARRITO
INSERT INTO cart_types (id, code, description) VALUES
(1, 'COMMON',       'Carrito común sin promociones'),
(2, 'VIP',          'Carrito con beneficios para clientes VIP'),
(3, 'SPECIAL_DATE', 'Carrito con descuento por fecha especial');

-- CARRITOS
-- Octubre:  Laura gana VIP / Diego no llega
-- Noviembre: Juan gana VIP (2 compras) / Carlos no llega / Maria mantiene VIP / Laura sin compras → pierde VIP
-- Diciembre: Pedro fecha especial / Ana mantiene VIP
INSERT INTO carts (customer_id, cart_type_id, status, simulated_date, created_at) VALUES
(6, 1, 'COMPLETED', '2024-10-10', '2024-10-10'),  -- id 1 Laura octubre
(7, 1, 'COMPLETED', '2024-10-22', '2024-10-22'),  -- id 2 Diego octubre
(1, 1, 'COMPLETED', '2024-11-05', '2024-11-05'),  -- id 3 Juan noviembre compra 1
(1, 1, 'COMPLETED', '2024-11-20', '2024-11-20'),  -- id 4 Juan noviembre compra 2
(3, 1, 'COMPLETED', '2024-11-10', '2024-11-10'),  -- id 5 Carlos noviembre
(2, 2, 'COMPLETED', '2024-11-15', '2024-11-15'),  -- id 6 Maria noviembre VIP
(5, 3, 'COMPLETED', '2024-12-25', '2024-12-25'),  -- id 7 Pedro Navidad
(4, 2, 'COMPLETED', '2024-12-10', '2024-12-10');  -- id 8 Ana diciembre VIP

-- COMPRAS
-- Octubre:  Laura $11.000 → gana VIP | Diego $3.800 → no llega
-- Noviembre: Juan $6.000 + $5.500 = $11.500 → gana VIP | Carlos $3.200 → no llega | Maria $15.000 → mantiene VIP
-- Diciembre: Pedro $8.700 → no llega | Ana $12.000 → mantiene VIP
-- Laura sin compras en noviembre → pierde VIP en diciembre
INSERT INTO purchases (customer_id, cart_id, total, purchase_date) VALUES
(6, 1, 11000.00, '2024-10-10'),
(7, 2,  3800.00, '2024-10-22'),
(1, 3,  6000.00, '2024-11-05'),
(1, 4,  5500.00, '2024-11-20'),
(3, 5,  3200.00, '2024-11-10'),
(2, 6, 15000.00, '2024-11-15'),
(5, 7,  8700.00, '2024-12-25'),
(4, 8, 12000.00, '2024-12-10');