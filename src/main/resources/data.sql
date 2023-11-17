INSERT INTO roles (id, name) VALUES (1, 'MANAGER');
INSERT INTO roles (id, name) VALUES (2, 'CLIENT');

INSERT INTO users (id, first_name, last_name, email, password, balance, role_id) VALUES (1, 'Mike', 'Brown', 'mike@mail.com', '$2a$10$CdEJ2PKXgUCIwU4pDQWICuiPjxb1lysoX7jrN.Y4MTMoY9pjfPALO', 2000, 1);
INSERT INTO users (id, first_name, last_name, email, password, balance, role_id) VALUES (2, 'Nick', 'Green', 'nick@mail.com', '$2a$10$CJgEoobU2gm0euD4ygru4ukBf9g8fYnPrMvYk.q0GMfOcIDtUhEwC', 2000, 2);
INSERT INTO users (id, first_name, last_name, email, password, balance, role_id) VALUES (3, 'Nora', 'White', 'nora@mail.com', '$2a$10$yYQaJrHzjOgD5wWCyelp0e1Yv1KEKeqUlYfLZQ1OQvyUrnEcX/rOy', 3000, 2);

INSERT INTO products (id, name, amount, price) VALUES (1, 'Iphone 12 pro', 5, '35000');
INSERT INTO products (id, name, amount, price) VALUES (2, 'Iphone 13 pro', 6, '45000');
INSERT INTO products (id, name, amount, price) VALUES (3, 'Iphone 14 pro', 6, '55000');