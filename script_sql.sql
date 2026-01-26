CREATE DATABASE IF NOT EXISTS location_voitures 
    CHARACTER SET utf8mb4 
    COLLATE utf8mb4_unicode_ci;

USE location_voitures;

DROP TABLE IF EXISTS locations;
DROP TABLE IF EXISTS clients;
DROP TABLE IF EXISTS voitures;

CREATE TABLE voitures (
    id INT AUTO_INCREMENT PRIMARY KEY,
    marque VARCHAR(100) NOT NULL,
    modele VARCHAR(100) NOT NULL,
    type_vehicule VARCHAR(50) NOT NULL,
    url_image VARCHAR(255),
    prix_par_jour DECIMAL(10,2) NOT NULL,
    disponible TINYINT(1) NOT NULL DEFAULT 1,
    annee INT NOT NULL,
    type_carburant VARCHAR(50) NOT NULL,
    nombre_places INT NOT NULL,
    transmission VARCHAR(50) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE clients (
    id INT AUTO_INCREMENT PRIMARY KEY,
    prenom VARCHAR(100) NOT NULL,
    nom VARCHAR(100) NOT NULL,
    numero_telephone VARCHAR(20) NOT NULL,
    numero_permis VARCHAR(50) NOT NULL,
    date_expiration_permis DATE NOT NULL,
    adresse TEXT,
    email VARCHAR(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE locations (
    id INT AUTO_INCREMENT PRIMARY KEY,
    id_voiture INT NOT NULL,
    id_client INT NOT NULL,
    date_debut DATE NOT NULL,
    date_fin DATE NOT NULL,
    prix_total DECIMAL(10,2) NOT NULL,
    statut VARCHAR(20) NOT NULL DEFAULT 'EN_COURS',
    date_creation TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_voiture) REFERENCES voitures(id) ON DELETE CASCADE,
    FOREIGN KEY (id_client) REFERENCES clients(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO voitures (marque, modele, type_vehicule, url_image, prix_par_jour, disponible, annee, type_carburant, nombre_places, transmission) VALUES
('Peugeot', '208', 'Berline', '', 35.00, 1, 2023, 'Essence', 5, 'Manuelle'),
('Renault', 'Clio', 'Berline', '', 30.00, 1, 2023, 'Diesel', 5, 'Automatique'),
('Citroën', 'C3', 'Berline', '', 32.00, 1, 2022, 'Essence', 5, 'Manuelle'),
('Peugeot', '3008', 'SUV', '', 55.00, 1, 2023, 'Diesel', 5, 'Automatique'),
('Renault', 'Captur', 'SUV', '', 50.00, 1, 2023, 'Essence', 5, 'Automatique'),
('BMW', 'Série 3', 'Sport', '', 80.00, 1, 2023, 'Essence', 5, 'Automatique'),
('Mercedes', 'Classe A', 'Sport', '', 85.00, 1, 2023, 'Diesel', 5, 'Automatique'),
('Dacia', 'Duster', '4x4', '', 45.00, 1, 2022, 'Diesel', 5, 'Manuelle'),
('Renault', 'Kangoo', 'Utilitaire', '', 40.00, 1, 2022, 'Diesel', 2, 'Manuelle'),
('Tesla', 'Model 3', 'Électrique', '', 95.00, 1, 2023, 'Électrique', 5, 'Automatique');

SELECT COUNT(*) AS nombre_voitures FROM voitures;

SELECT * FROM voitures;
