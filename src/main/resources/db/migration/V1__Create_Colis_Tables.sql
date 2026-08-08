-- Création de la table colis
CREATE TABLE IF NOT EXISTS colis (
    id UUID PRIMARY KEY,
    numero_suivi VARCHAR(50) UNIQUE NOT NULL,
    expediteur_id UUID NOT NULL,
    nom_destinataire VARCHAR(255) NOT NULL,
    adresse_destinataire TEXT NOT NULL,
    telephone_destinataire VARCHAR(20) NOT NULL,
    poids DOUBLE PRECISION NOT NULL,
    longueur DOUBLE PRECISION NOT NULL,
    largeur DOUBLE PRECISION NOT NULL,
    hauteur DOUBLE PRECISION NOT NULL,
    statut VARCHAR(50) NOT NULL,
    livreur_id UUID,
    remarques TEXT,
    date_creation TIMESTAMP NOT NULL,
    date_livraison TIMESTAMP,
    mode_depot VARCHAR(50) NOT NULL,
    adresse_collecte TEXT,
    telephone_collecte VARCHAR(20),
    date_heure_collecte_souhaitee TIMESTAMP,
    latitude_destinataire DOUBLE PRECISION,
    longitude_destinataire DOUBLE PRECISION,
    latitude_collecte DOUBLE PRECISION,
    longitude_collecte DOUBLE PRECISION,
    tournee_id UUID,
    creer_par VARCHAR(255),
    modifier_par VARCHAR(255),
    date_creation VARCHAR(255),
    date_modification VARCHAR(255),
    FOREIGN KEY (expediteur_id) REFERENCES users(id),
    FOREIGN KEY (livreur_id) REFERENCES users(id),
    FOREIGN KEY (tournee_id) REFERENCES tournee(id)
);

-- Création de la table historique_colis
CREATE TABLE IF NOT EXISTS historique_colis (
    id UUID PRIMARY KEY,
    colis_id UUID NOT NULL,
    ancien_statut VARCHAR(50),
    nouveau_statut VARCHAR(50) NOT NULL,
    date_changement TIMESTAMP NOT NULL,
    utilisateur_id UUID,
    commentaire TEXT,
    creer_par VARCHAR(255),
    modifier_par VARCHAR(255),
    date_creation VARCHAR(255),
    date_modification VARCHAR(255),
    FOREIGN KEY (colis_id) REFERENCES colis(id) ON DELETE CASCADE,
    FOREIGN KEY (utilisateur_id) REFERENCES users(id)
);

-- Création de la table tournee
CREATE TABLE IF NOT EXISTS tournee (
    id UUID PRIMARY KEY,
    date_tournee DATE NOT NULL,
    livreur_id UUID NOT NULL,
    zone VARCHAR(255),
    statut VARCHAR(50) NOT NULL,
    creer_par VARCHAR(255),
    modifier_par VARCHAR(255),
    date_creation VARCHAR(255),
    date_modification VARCHAR(255),
    FOREIGN KEY (livreur_id) REFERENCES users(id)
);

-- Ajout d'index pour optimiser les requêtes
CREATE INDEX idx_colis_statut ON colis(statut);
CREATE INDEX idx_colis_livreur ON colis(livreur_id);
CREATE INDEX idx_colis_expediteur ON colis(expediteur_id);
CREATE INDEX idx_colis_numero_suivi ON colis(numero_suivi);
CREATE INDEX idx_colis_tournee ON colis(tournee_id);
CREATE INDEX idx_historique_colis_id ON historique_colis(colis_id);
CREATE INDEX idx_historique_date_changement ON historique_colis(date_changement);
CREATE INDEX idx_tournee_livreur ON tournee(livreur_id);
CREATE INDEX idx_tournee_date ON tournee(date_tournee);
CREATE INDEX idx_tournee_statut ON tournee(statut);
