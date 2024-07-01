insert into machines (id, name, location, status, operator)
values (1, 'Drink Dispenser 001', 'Plant 1', 0, 'Kiwi'),
       (2, 'Drink Dispenser 002', 'Plant 1', 1, 'Kiwi');
insert into coins (id, machine_id, denomination, value, quantity)
values (null, 1,'FIVE_CENTS', 0.05, 2),
       (null, 1,'TEN_CENTS', 0.10, 2),
       (null, 1,'TWENTY_CENTS', 0.20, 0),
       (null, 1,'FIFTY_CENTS', 0.50, 0),
       (null, 1,'ONE_EURO', 1.00, 0),
       (null, 1,'TWO_EURO', 2.00, 0);
insert into products (id, name, price)
values (1, 'Coke', 2),
       (2, 'Redbull', 2.25),
       (3, 'Water', 0.50),
       (4, 'Orange Juice', 1.95);
insert into machine_products (id, machine_id, product_id, stock, expiration_date, operator)
values (null, 1, 1, 5, '2025-01-01', 'Kiwi'),
       (null, 1, 2, 3, '2025-01-01', 'Kiwi'),
       (null, 1, 3, 10, '2025-01-01', 'Kiwi'),
       (null, 1, 4, 1, '2025-01-01', 'Kiwi'),
       (null, 1, 4, 1, '2024-06-01', 'Kiwi');