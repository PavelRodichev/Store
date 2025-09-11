INSERT INTO categories (category_name)
VALUES ('Electronics'),
       ('Clothing'),
       ('Books'),
       ('Home & Garden'),
       ('Sports'),
       ('Toys'),
       ('Beauty'),
       ('Food');

-- changeset rodichev:2
-- comment: Insert test users
INSERT INTO users (username, email, password, first_name, last_name, role)
VALUES ('admin', 'admin@example.com', '$2a$10$Fak3Hash3xamp1e', 'Admin', 'User', 'ADMIN'),
       ('john_doe', 'john.doe@example.com', '$2a$10$Fak3Hash3xamp1e', 'John', 'Doe', 'USER'),
       ('jane_smith', 'jane.smith@example.com', '$2a$10$Fak3Hash3xamp1e', 'Jane', 'Smith', 'USER'),
       ('mike_johnson', 'mike.johnson@example.com', '$2a$10$Fak3Hash3xamp1e', 'Mike', 'Johnson', 'USER'),
       ('sarah_wilson', 'sarah.wilson@example.com', '$2a$10$Fak3Hash3xamp1e', 'Sarah', 'Wilson', 'USER'),
       ('alex_brown', 'alex.brown@example.com', '$2a$10$Fak3Hash3xamp1e', 'Alex', 'Brown', 'USER');

-- changeset rodichev:3
INSERT INTO products (name, product_article, product_description, product_price, product_amount, category_id, image_url) VALUES
-- Electronics
('MacBook Pro 16"', 'MBP16-001', 'Apple MacBook Pro 16 inch with M2 Pro chip', 2499.99, 15, 1, '/images/macbook-pro.jpg'),
('iPhone 15 Pro', 'IP15P-002', 'Latest iPhone with advanced camera system', 999.99, 50, 1, '/images/iphone15.jpg'),
('Samsung Galaxy S23', 'SGS23-003', 'Android flagship smartphone', 899.99, 30, 1, '/images/galaxy-s23.jpg'),
('Sony WH-1000XM5', 'SONY-XM5', 'Noise cancelling wireless headphones', 349.99, 25, 1, '/images/sony-headphones.jpg'),

-- Clothing
('Cotton T-Shirt', 'CTS-101', '100% cotton premium t-shirt', 29.99, 100, 2, '/images/tshirt.jpg'),
('Jeans Classic', 'JEANS-102', 'Classic blue denim jeans', 79.99, 75, 2, '/images/jeans.jpg'),
('Winter Jacket', 'JACKET-103', 'Warm winter jacket for cold weather', 129.99, 40, 2, '/images/jacket.jpg'),
('Running Shoes', 'SHOES-104', 'Professional running shoes', 89.99, 60, 2, '/images/shoes.jpg'),

-- Books
('The Great Gatsby', 'BOOK-201', 'Classic novel by F. Scott Fitzgerald', 12.99, 200, 3, '/images/gatsby.jpg'),
('Clean Code', 'BOOK-202', 'Software development best practices', 45.99, 80, 3, '/images/cleancode.jpg'),
('Harry Potter Set', 'BOOK-203', 'Complete Harry Potter book series', 199.99, 35, 3, '/images/harrypotter.jpg'),

-- Home & Garden
('Coffee Maker', 'HOME-301', 'Automatic drip coffee maker', 49.99, 45, 4, '/images/coffeemaker.jpg'),
('Gardening Tools Set', 'GARDEN-302', 'Complete gardening tools set', 79.99, 30, 4, '/images/gardening-tools.jpg');