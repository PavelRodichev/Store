ALTER SEQUENCE IF EXISTS orders_id_seq RESTART WITH 1; --сброс
-- Очистка
DELETE
FROM order_items;
DELETE
FROM orders;
DELETE
FROM products;
DELETE
FROM categories;
DELETE
FROM users;

INSERT INTO categories (category_name)
VALUES ('Electronics'),
       ('Clothing'),
       ('Books'),
       ('Home & Garden'),
       ('Sports'),
       ('Toys'),
       ('Beauty'),
       ('Food');


INSERT INTO users (username, email, password, first_name, last_name, role)
VALUES ('admin', 'admin@example.com', '$2a$10$Fak3Hash3xamp1e', 'Admin', 'User', 'ADMIN'),
       ('john_doe', 'john.doe@example.com', '$2a$10$Fak3Hash3xamp1e', 'John', 'Doe', 'USER'),
       ('jane_smith', 'jane.smith@example.com', '$2a$10$Fak3Hash3xamp1e', 'Jane', 'Smith', 'USER'),
       ('mike_johnson', 'mike.johnson@example.com', '$2a$10$Fak3Hash3xamp1e', 'Mike', 'Johnson', 'USER'),
       ('sarah_wilson', 'sarah.wilson@example.com', '$2a$10$Fak3Hash3xamp1e', 'Sarah', 'Wilson', 'USER'),
       ('alex_brown', 'alex.brown@example.com', '$2a$10$Fak3Hash3xamp1e', 'Alex', 'Brown', 'USER');

INSERT INTO products (name, product_article, product_description, product_price, product_amount, category_id, image,
                      is_available)
VALUES
-- Electronics
('MacBook Pro 16"', 'MBP16-001', 'Apple MacBook Pro 16 inch with M2 Pro chip', 2499.99, 15, 1,
 '/images/macbook-pro.jpg', TRUE),
('iPhone 15 Pro', 'IP15P-002', 'Latest iPhone with advanced camera system', 999.99, 50, 1, '/images/iphone15.jpg',
 TRUE),
('Samsung Galaxy S23', 'SGS23-003', 'Android flagship smartphone', 899.99, 30, 1, '/images/galaxy-s23.jpg', TRUE),
('Sony WH-1000XM5', 'SONY-XM5', 'Noise cancelling wireless headphones', 349.99, 25, 1, '/images/sony-headphones.jpg',
 TRUE),

-- Clothing
('Cotton T-Shirt', 'CTS-101', '100% cotton premium t-shirt', 29.99, 100, 2, '/images/tshirt.jpg', TRUE),
('Jeans Classic', 'JEANS-102', 'Classic blue denim jeans', 79.99, 75, 2, '/images/jeans.jpg', TRUE),
('Winter Jacket', 'JACKET-103', 'Warm winter jacket for cold weather', 129.99, 40, 2, '/images/jacket.jpg', TRUE),
('Running Shoes', 'SHOES-104', 'Professional running shoes', 89.99, 60, 2, '/images/shoes.jpg', TRUE),

-- Books
('The Great Gatsby', 'BOOK-201', 'Classic novel by F. Scott Fitzgerald', 12.99, 200, 3, '/images/gatsby.jpg', TRUE),
('Clean Code', 'BOOK-202', 'Software development best practices', 45.99, 80, 3, '/images/cleancode.jpg', TRUE),
('Harry Potter Set', 'BOOK-203', 'Complete Harry Potter book series', 199.99, 35, 3, '/images/harrypotter.jpg', TRUE),

-- Home & Garden (исправлена последняя строка)
('Coffee Maker', 'HOME-301', 'Automatic drip coffee maker', 49.99, 45, 4, '/images/coffeemaker.jpg', TRUE),
('Gardening Tools Set', 'GARDEN-302', 'Complete gardening tools set', 79.99, 30, 4, '/images/gardening-tools.jpg',
 TRUE);

-- Вставка заказов
INSERT INTO orders (user_id, total_amount, address, order_status, order_date)
VALUES (2, 3399.98, '123 Main St, New York, NY', 'CREATED', '2025-11-01 10:00:00'),     -- John Doe: MacBook + iPhone
       (3, 109.98, '456 Oak Ave, Los Angeles, CA', 'COMPLETED', '2025-11-02 14:30:00'), -- Jane Smith: T-Shirt + Jeans
       (4, 349.99, '789 Pine Rd, Chicago, IL', 'COMPLETED', '2025-11-03 09:15:00'),     -- Mike Johnson: Headphones
       (5, 245.97, '321 Elm St, Houston, TX', 'CREATED',
        '2025-11-04 16:45:00'),                                                         -- Sarah Wilson: Winter Jacket + Running Shoes
       (6, 45.99, '654 Maple Dr, Phoenix, AZ', 'CREATED', '2025-11-05 11:20:00'); -- Alex Brown: Clean Code book

INSERT INTO order_items (order_id, product_id, quantity, product_price, product_name, product_article)
VALUES
-- Order 1: John Doe (MacBook + iPhone)
(1, 1, 1, 2499.99, 'MacBook Pro 16"', 'MBP16-001'),
(1, 2, 1, 899.99, 'iPhone 15 Pro', 'IP15P-002'),

-- Order 2: Jane Smith (T-Shirt + Jeans)
(2, 5, 1, 29.99, 'Cotton T-Shirt', 'CTS-101'),
(2, 6, 1, 79.99, 'Jeans Classic', 'JEANS-102'),

-- Order 3: Mike Johnson (Headphones)
(3, 4, 1, 349.99, 'Sony WH-1000XM5', 'SONY-XM5'),

-- Order 4: Sarah Wilson (Winter Jacket + Running Shoes)
(4, 7, 1, 129.99, 'Winter Jacket', 'JACKET-103'),
(4, 8, 1, 89.99, 'Running Shoes', 'SHOES-104'),

-- Order 5: Alex Brown (Clean Code book)
(5, 10, 1, 45.99, 'Clean Code', 'BOOK-202');