CREATE DATABASE IF NOT EXISTS raf_novosti;
USE raf_novosti;

CREATE TABLE IF NOT EXISTS users (
    id         INT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(100) NOT NULL,
    last_name  VARCHAR(100) NOT NULL,
    email      VARCHAR(255) NOT NULL UNIQUE,
    type       ENUM('ADMIN', 'CONTENT_CREATOR') NOT NULL,
    status     ENUM('ACTIVE', 'INACTIVE') NOT NULL DEFAULT 'ACTIVE',
    password   VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS categories (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(255) NOT NULL UNIQUE,
    description TEXT NOT NULL
);


CREATE TABLE IF NOT EXISTS news (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    title       VARCHAR(500) NOT NULL,
    content     LONGTEXT NOT NULL,
    created_at  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    visit_count INT NOT NULL DEFAULT 0,
    author_id   INT NOT NULL,
    category_id INT NOT NULL,
    FOREIGN KEY (author_id)   REFERENCES users(id),
    FOREIGN KEY (category_id) REFERENCES categories(id)
);

CREATE TABLE IF NOT EXISTS tags (
    id   INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE
);


CREATE TABLE IF NOT EXISTS news_tags (
    news_id INT NOT NULL,
    tag_id  INT NOT NULL,
    PRIMARY KEY (news_id, tag_id),
    FOREIGN KEY (news_id) REFERENCES news(id) ON DELETE CASCADE,
    FOREIGN KEY (tag_id)  REFERENCES tags(id) ON DELETE CASCADE
);


CREATE TABLE IF NOT EXISTS comments (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    author_name VARCHAR(255) NOT NULL,
    content     TEXT NOT NULL,
    created_at  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    likes       INT NOT NULL DEFAULT 0,
    dislikes    INT NOT NULL DEFAULT 0,
    news_id     INT NOT NULL,
    FOREIGN KEY (news_id) REFERENCES news(id) ON DELETE CASCADE
);


CREATE TABLE IF NOT EXISTS news_reactions (
    id            INT AUTO_INCREMENT PRIMARY KEY,
    news_id       INT NOT NULL,
    session_id    VARCHAR(255) NOT NULL,
    reaction      ENUM('LIKE', 'DISLIKE') NOT NULL,
    UNIQUE KEY uq_news_reaction (news_id, session_id),
    FOREIGN KEY (news_id) REFERENCES news(id) ON DELETE CASCADE
);


CREATE TABLE IF NOT EXISTS comment_reactions (
    id         INT AUTO_INCREMENT PRIMARY KEY,
    comment_id INT NOT NULL,
    session_id VARCHAR(255) NOT NULL,
    reaction   ENUM('LIKE', 'DISLIKE') NOT NULL,
    UNIQUE KEY uq_comment_reaction (comment_id, session_id),
    FOREIGN KEY (comment_id) REFERENCES comments(id) ON DELETE CASCADE
);


CREATE TABLE IF NOT EXISTS news_visits (
    news_id    INT NOT NULL,
    session_id VARCHAR(255) NOT NULL,
    PRIMARY KEY (news_id, session_id),
    FOREIGN KEY (news_id) REFERENCES news(id) ON DELETE CASCADE
);


-- ============================================================
-- ============================================================
-- ============================================================
-- ============================================================
-- ============================================================


INSERT INTO users (first_name, last_name, email, type, status, password)
VALUES ('Admin', 'Adminovic', 'admin@rafnovosti.rs', 'ADMIN', 'ACTIVE',
        '240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9');