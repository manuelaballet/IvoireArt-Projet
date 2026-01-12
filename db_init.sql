CREATE DATABASE IF NOT EXISTS ivoireart CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE ivoireart;

CREATE TABLE IF NOT EXISTS products (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    price DECIMAL(12,2) NOT NULL,
    description VARCHAR(1000),
    image_url VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS artisans (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    title VARCHAR(255) NOT NULL,
    bio VARCHAR(1000),
    image_url VARCHAR(255)
);

INSERT INTO products (name, price, description, image_url) VALUES
('Pagne Wax Premium', 45000, 'Motifs traditionnels par nos maitres tisserands', '/images/Pagne Wax.jpg'),
('Collier Traditionnel', 28000, 'Or et perles traditionnelles authentiques', '/images/collier traditionnel.jpg'),
('Sculpture Dan', 95000, 'Oeuvre unique en bois precieux', '/images/Sculpture Dan.jpg'),
('Robe Pagne Sur-Mesure', 75000, 'Design moderne et tissu africain authentique', '/images/robe pagne.jpg'),
('Sac a main en rafia', 32000, 'Sac artisanal tresse a la main', '/images/sac rafia.jpg'),
('Bracelet Senoufo', 18500, 'Perles traditionnelles colorees', '/images/bracelet senoufo.jpg')
ON DUPLICATE KEY UPDATE name = VALUES(name), price = VALUES(price), description = VALUES(description), image_url = VALUES(image_url);

INSERT INTO artisans (name, title, bio, image_url) VALUES
('Bijoutiere Talentueuse', 'Creatrice de bijoux', 'Specialiste des bijoux traditionnels ivoiriens en perles et metaux precieux.', '/images/bijoutier.jpg'),
('Couturiere Creative', 'Tisserande / Styliste', 'Experte en pagnes wax et creations sur-mesure pour toutes occasions.', '/images/couturiere.jpg'),
('Sculpteur Maitre', 'Artisan Bois', 'Createur de sculptures traditionnelles ivoiriennes, uniques et authentiques.', '/images/sculpteur.jpg')
ON DUPLICATE KEY UPDATE name = VALUES(name), title = VALUES(title), bio = VALUES(bio), image_url = VALUES(image_url);
