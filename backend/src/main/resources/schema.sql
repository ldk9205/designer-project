CREATE DATABASE IF NOT EXISTS designer
  DEFAULT CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE designer;

CREATE TABLE IF NOT EXISTS designers (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    name VARCHAR(50) NOT NULL,
    phone VARCHAR(20),
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB;


CREATE TABLE IF NOT EXISTS customers (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    designer_id BIGINT NOT NULL,
    name VARCHAR(50) NOT NULL,
    phone VARCHAR(20),
    memo TEXT,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT fk_customers_designer
        FOREIGN KEY (designer_id)
        REFERENCES designers(id)
        ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS treatments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    designer_id BIGINT NOT NULL,
    customer_id BIGINT NOT NULL,
    treatment_date DATE NOT NULL,
    treatment_time TIME,
    category VARCHAR(50) NOT NULL,
    style_name VARCHAR(100),
    detail TEXT,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT fk_treatments_designer
        FOREIGN KEY (designer_id)
        REFERENCES designers(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_treatments_customer
        FOREIGN KEY (customer_id)
        REFERENCES customers(id)
        ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS treatment_images (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    treatment_id BIGINT NOT NULL,
    image_url VARCHAR(500) NOT NULL,
    original_name VARCHAR(255),
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_images_treatment
        FOREIGN KEY (treatment_id)
        REFERENCES treatments(id)
        ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS board_posts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    designer_id BIGINT NOT NULL,
    title VARCHAR(200) NOT NULL,
    content TEXT NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT fk_posts_designer
        FOREIGN KEY (designer_id)
        REFERENCES designers(id)
        ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS board_comments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    post_id BIGINT NOT NULL,
    designer_id BIGINT NOT NULL,
    content TEXT NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT fk_comments_post
        FOREIGN KEY (post_id)
        REFERENCES board_posts(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_comments_designer
        FOREIGN KEY (designer_id)
        REFERENCES designers(id)
        ON DELETE CASCADE
) ENGINE=InnoDB;
