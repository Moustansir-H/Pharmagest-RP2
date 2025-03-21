package mcci.businessschool.bts.sio.slam.pharmagest.commande.service;

import mcci.businessschool.bts.sio.slam.pharmagest.commande.LigneDeCommande;
import mcci.businessschool.bts.sio.slam.pharmagest.commande.dao.LigneDeCommandeDao;

import java.sql.SQLException;
import java.util.List;

public class LigneDeCommandeService {
    private LigneDeCommandeDao ligneDeCommandeDao;

    public LigneDeCommandeService() throws Exception {
        this.ligneDeCommandeDao = new LigneDeCommandeDao();
    }

    // Ajouter une ligne de commande
    public void ajouterLigneDeCommande(LigneDeCommande ligneDeCommande) throws SQLException {
        if (ligneDeCommande.getQuantiteVendu() <= 0) {
            throw new IllegalArgumentException("La quantité vendue doit être supérieure à 0.");
        }
        ligneDeCommandeDao.ajouterLigneDeCommande(ligneDeCommande);
    }

    // Récupérer toutes les lignes de commande d'une commande spécifique
    public List<LigneDeCommande> recupererLignesParCommande(int commandeId) throws SQLException {
        return ligneDeCommandeDao.recupererLignesParCommande(commandeId);
    }

    // Mettre à jour la réception d'une ligne de commande
    public void mettreAJourReception(int ligneDeCommandeId, int quantiteRecue, double prixAchatReel, double prixVenteReel) throws SQLException {
        if (quantiteRecue < 0 || prixAchatReel < 0 || prixVenteReel < 0) {
            throw new IllegalArgumentException("Les valeurs reçues doivent être positives.");
        }
        ligneDeCommandeDao.mettreAJourReception(ligneDeCommandeId, quantiteRecue, prixAchatReel, prixVenteReel);
    }


    /*
    public static void main(String[] args) {
        try {
            LigneDeCommandeService ligneService = new LigneDeCommandeService();

            // ✅ 1. Création d'une ligne de commande test (Commande ID = 2, Médicament ID = 1)
            LigneDeCommande ligneTest = new LigneDeCommande(
                    new Commande(2), // Commande existante
                    new Medicament(58),  // Médicament existant
                    5,  // Quantité commandée
                    10.0,  // Prix unitaire
                    0,  // Quantité reçue (car non encore livrée)
                    0.0,  // Prix d'achat réel (sera mis à jour plus tard)
                    0.0   // Prix de vente réel (sera mis à jour plus tard)
            );
            ligneService.ajouterLigneDeCommande(ligneTest);
            System.out.println("✅ Ligne de commande ajoutée avec succès.");

            // ✅ 2. Récupération et affichage des lignes de commande de la commande ID 2
            List<LigneDeCommande> lignes = ligneService.recupererLignesParCommande(2);
            System.out.println("📢 Lignes de commande récupérées pour la commande ID 2 :");
            for (LigneDeCommande ligne : lignes) {
                System.out.println(ligne);
            }

            // ✅ 3. Mise à jour après réception (Simulation de livraison)
            if (!lignes.isEmpty()) {
                LigneDeCommande ligneARecevoir = lignes.get(0); // On prend la première ligne
                int ligneId = ligneARecevoir.getId();
                int quantiteRecue = 5;
                double prixAchatReel = 8.0;
                double prixVenteReel = 12.0;

                ligneService.mettreAJourReception(ligneId, quantiteRecue, prixAchatReel, prixVenteReel);
                System.out.println("✅ Réception de la ligne de commande mise à jour avec succès.");
            } else {
                System.out.println("⚠️ Aucune ligne de commande trouvée pour mise à jour.");
            }

        } catch (SQLException e) {
            System.err.println("❌ Erreur SQL : " + e.getMessage());
        } catch (Exception e) {
            System.err.println("❌ Erreur : " + e.getMessage());
        }
    }*/
}
