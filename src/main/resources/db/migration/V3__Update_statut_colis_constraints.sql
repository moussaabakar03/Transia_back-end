-- Mise à jour des statuts autorisés pour les colis

ALTER TABLE colis
DROP CONSTRAINT IF EXISTS colis_statut_check;

ALTER TABLE colis
ADD CONSTRAINT colis_statut_check
CHECK (
    statut IN (
        'EN_ATTENTE_DEPOT',
        'DEPOSE_EN_AGENCE',
        'EN_TRANSIT',
        'ARRIVE_EN_AGENCE',
        'AFFECTE_AU_LIVREUR',
        'EN_COURS_LIVRAISON',
        'LIVRE',
        'RETOURNE',
        'PERDU',
        'ANNULE'
    )
);

ALTER TABLE historique_colis
DROP CONSTRAINT IF EXISTS historique_colis_ancien_statut_check;

ALTER TABLE historique_colis
ADD CONSTRAINT historique_colis_ancien_statut_check
CHECK (
    ancien_statut IS NULL
    OR ancien_statut IN (
        'EN_ATTENTE_DEPOT',
        'DEPOSE_EN_AGENCE',
        'EN_TRANSIT',
        'ARRIVE_EN_AGENCE',
        'AFFECTE_AU_LIVREUR',
        'EN_COURS_LIVRAISON',
        'LIVRE',
        'RETOURNE',
        'PERDU',
        'ANNULE'
    )
);

ALTER TABLE historique_colis
DROP CONSTRAINT IF EXISTS historique_colis_nouveau_statut_check;

ALTER TABLE historique_colis
ADD CONSTRAINT historique_colis_nouveau_statut_check
CHECK (
    nouveau_statut IN (
        'EN_ATTENTE_DEPOT',
        'DEPOSE_EN_AGENCE',
        'EN_TRANSIT',
        'ARRIVE_EN_AGENCE',
        'AFFECTE_AU_LIVREUR',
        'EN_COURS_LIVRAISON',
        'LIVRE',
        'RETOURNE',
        'PERDU',
        'ANNULE'
    )
);