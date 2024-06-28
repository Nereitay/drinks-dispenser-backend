drop table if exists machines;
drop table if exists products;
drop table if exists machine_products;
drop table if exists coins;
CREATE TABLE machines
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    name       VARCHAR(50),
    location   VARCHAR(255),
    status     INT       DEFAULT 0 comment '0 - AVAILABLE, 1 - OUT_OF_ORDER, 2 - MAINTENANCE',
    operator   VARCHAR(255) DEFAULT 'USER001',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
CREATE TABLE products
(
    id    BIGINT AUTO_INCREMENT PRIMARY KEY,
    name  VARCHAR(100) UNIQUE NOT NULL,
    price DOUBLE       NOT NULL,
    type  VARCHAR(50) DEFAULT 'DRINK'
);
CREATE TABLE machine_products
(
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    machine_id      BIGINT       NOT NULL,
    product_id      BIGINT       NOT NULL,
    stock           INT          NOT NULL,
    expiration_date DATE         NOT NULL,
    operator        VARCHAR(255) DEFAULT 'USER001',
    updated_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
alter table machine_products
    add unique index unique_machine_product_expirationdate (machine_id, product_id, expiration_date);
CREATE TABLE coins
(
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    machine_id   BIGINT       NOT NULL,
    denomination VARCHAR(50)  NOT NULL,
    value        DOUBLE       NOT NULL,
    quantity     INT          NOT NULL,
    operator     VARCHAR(255) DEFAULT 'USER001',
    updated_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
alter table coins
    add unique index unique_machine_value (machine_id, value);


