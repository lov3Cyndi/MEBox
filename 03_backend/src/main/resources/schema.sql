-- MEBox Database Schema for MySQL 8.0

CREATE DATABASE IF NOT EXISTS mebox CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE mebox;

-- Users Table
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    avatar VARCHAR(500),
    theme VARCHAR(20) DEFAULT 'overwatch',
    role VARCHAR(20) DEFAULT 'USER',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Heroes Table
CREATE TABLE IF NOT EXISTS heroes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    role VARCHAR(20),
    description TEXT,
    portrait VARCHAR(500),
    story TEXT,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Maps Table
CREATE TABLE IF NOT EXISTS maps (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    type VARCHAR(20),
    description TEXT,
    image VARCHAR(500),
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Videos Table
CREATE TABLE IF NOT EXISTS videos (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(100) NOT NULL,
    description TEXT,
    url VARCHAR(500) NOT NULL,
    thumbnail VARCHAR(500),
    user_id BIGINT NOT NULL,
    hero_id BIGINT,
    map_id BIGINT,
    game_type VARCHAR(50),
    like_count INT DEFAULT 0,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (hero_id) REFERENCES heroes(id) ON DELETE SET NULL,
    FOREIGN KEY (map_id) REFERENCES maps(id) ON DELETE SET NULL
);

-- Comments Table
CREATE TABLE IF NOT EXISTS comments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    video_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    content TEXT NOT NULL,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (video_id) REFERENCES videos(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Ratings Table
CREATE TABLE IF NOT EXISTS ratings (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    target_type VARCHAR(20) NOT NULL,
    target_id BIGINT NOT NULL,
    overall_rating DECIMAL(3,2),
    damage_rating DECIMAL(3,2),
    survival_rating DECIMAL(3,2),
    utility_rating DECIMAL(3,2),
    comment TEXT,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    UNIQUE KEY uk_user_target (user_id, target_type, target_id)
);

-- Hero Media Table
CREATE TABLE IF NOT EXISTS hero_media (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    hero_id BIGINT NOT NULL,
    type VARCHAR(20) NOT NULL,
    title VARCHAR(100),
    url VARCHAR(500) NOT NULL,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (hero_id) REFERENCES heroes(id) ON DELETE CASCADE
);

-- Hero Relations Table
CREATE TABLE IF NOT EXISTS hero_relations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    hero_id BIGINT NOT NULL,
    related_hero_id BIGINT NOT NULL,
    relation_type VARCHAR(50),
    description TEXT,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (hero_id) REFERENCES heroes(id) ON DELETE CASCADE,
    FOREIGN KEY (related_hero_id) REFERENCES heroes(id) ON DELETE CASCADE
);

-- Video Likes Table
CREATE TABLE IF NOT EXISTS video_likes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    video_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (video_id) REFERENCES videos(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    UNIQUE KEY uk_video_user (video_id, user_id)
);

-- Sample Data
-- 默认管理员账户: admin / admin123 (角色为 ADMIN)
INSERT INTO users (username, email, password, role) VALUES
('admin', 'admin@mebox.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 'ADMIN');

INSERT INTO heroes (name, role, description, portrait) VALUES
('D.va', '支援型', '韩国釜山的顶尖机甲驾驶员', 'https://ow.blizzard.cn/static/images/heroes/dva-portrait.png'),
('士兵：76', '输出型', '神秘守望先锋成员的复仇之路', 'https://ow.blizzard.cn/static/images/heroes/soldier-76-portrait.png'),
('莱因哈特', '重装型', '德国老兵，正义与荣耀的骑士', 'https://ow.blizzard.cn/static/images/heroes/reinhardt-portrait.png'),
('源氏', '输出型', '半机械忍者，寻求救赎之路', 'https://ow.blizzard.cn/static/images/heroes/genji-portrait.png'),
('路霸', '重装型', '澳大利亚的清理工', 'https://ow.blizzard.cn/static/images/heroes/roadhog-portrait.png');

INSERT INTO maps (name, type, description, image) VALUES
('釜山', '占领要点', '韩国釜山 defending the city', 'https://ow.blizzard.cn/static/images/maps/busan.png'),
('漓江塔', '控制要点', '中国桂林的科技中心', 'https://ow.blizzard.cn/static/images/maps/lijiang-tower.png'),
('国王大道', '攻击/护送', '英国伦敦的街头', 'https://ow.blizzard.cn/static/images/maps/kings-row.png'),
('好莱坞', '攻击/护送', '美国洛杉矶的电影片场', 'https://ow.blizzard.cn/static/images/maps/hollywood.png');
