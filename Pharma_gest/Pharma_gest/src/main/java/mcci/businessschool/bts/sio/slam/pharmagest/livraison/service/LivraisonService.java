package mcci.businessschool.bts.sio.slam.pharmagest.livraison.service;

import mcci.businessschool.bts.sio.slam.pharmagest.commande.Commande;
import mcci.businessschool.bts.sio.slam.pharmagest.commande.LigneDeCommande;
import mcci.businessschool.bts.sio.slam.pharmagest.commande.dao.LigneDeCommandeDao;
import mcci.businessschool.bts.sio.slam.pharmagest.livraison.Livraison;
import mcci.businessschool.bts.sio.slam.pharmagest.livraison.dao.LivraisonDao;
import mcci.businessschool.bts.sio.slam.pharmagest.medicament.dao.MedicamentDao;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class LivraisonService {
    private LivraisonDao livraisonDao;
    private LigneDeCommandeDao ligneDeCommandeDao;
    private MedicamentDao medicamentDao;

    public LivraisonService() throws Exception {
        this.livraisonDao = new LivraisonDao();
        this.ligneDeCommandeDao = new LigneDeCommandeDao();
        this.medicamentDao = new MedicamentDao();
    }

    // ✅ Ajouter une livraison après validation d'une commande
    public void creerLivraison(Commande commande) throws SQLException {
        if (commande.getFournisseur() == null) {
            throw new SQLException("❌ Erreur : Impossible de créer une livraison, le fournisseur est introuvable.");
        }

        Livraison livraison = new Livraison(
                LocalDate.now(),
                "En attente",
                commande,
                commande.getFournisseur() // ✅ Fournisseur correctement passé
        );
        livraisonDao.ajouterLivraison(livraison);
        System.out.println("✅ Livraison créée avec succès pour la commande ID " + commande.getId());
    }


    // ✅ Récupérer toutes les livraisons
    public void afficherToutesLesLivraisons() {
        try {
            List<Livraison> livraisons = livraisonDao.recupererToutesLesLivraisons();
            System.out.println("\n📢 Liste des livraisons enregistrées :");
            if (livraisons.isEmpty()) {
                System.out.println("⚠️ Aucune livraison trouvée.");
            } else {
                for (Livraison l : livraisons) {
                    System.out.println("📦 " + l);
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la récupération des livraisons : " + e.getMessage());
        }
    }

    // ✅ Mettre à jour le statut d'une livraison
    public void mettreAJourStatutLivraison(int livraisonId, String nouveauStatut) throws SQLException {
        try {
            livraisonDao.mettreAJourStatutLivraison(livraisonId, nouveauStatut);

            // ✅ Vérifier si la mise à jour a bien eu lieu
            Livraison livraison = livraisonDao.recupererLivraisonParId(livraisonId);
            if (livraison == null) {
                throw new SQLException("❌ Erreur : Livraison ID " + livraisonId + " introuvable en base !");
            }

            if ("Livrée".equalsIgnoreCase(nouveauStatut)) {
                System.out.println("📦 Livraison ID " + livraisonId + " livrée. Mise à jour du stock des médicaments...");

                int commandeId = livraison.getCommande().getId();
                List<LigneDeCommande> lignesDeCommande = ligneDeCommandeDao.recupererLignesParCommande(commandeId);

                if (lignesDeCommande.isEmpty()) {
                    throw new SQLException("❌ Aucune ligne de commande trouvée pour la commande ID " + commandeId);
                }

                for (LigneDeCommande ligne : lignesDeCommande) {
                    int medicamentId = ligne.getMedicament().getId();
                    int quantiteRecue = ligne.getQuantiteRecue();

                    // ✅ Correction : Si quantiteRecue est 0, prendre quantiteVendu comme base
                    if (quantiteRecue == 0) {
                        quantiteRecue = ligne.getQuantiteVendu(); // On suppose que toute la commande est reçue
                        System.out.println("🔄 Correction : Quantité reçue mise à jour = " + quantiteRecue);
                    }

                    if (quantiteRecue > 0) {
                        System.out.println("🔄 Mise à jour du stock pour Médicament ID " + medicamentId + " (+ " + quantiteRecue + " unités)");
                        medicamentDao.mettreAJourStock(medicamentId, quantiteRecue);
                    } else {
                        System.out.println("⚠️ Médicament ID " + medicamentId + " n'a toujours pas de quantité reçue, stock inchangé.");
                    }
                }

            }

        } catch (SQLException e) {
            System.err.println("❌ Erreur SQL lors de la mise à jour du statut de la livraison : " + e.getMessage());
            throw e; // On lève l'exception pour la gestion plus haut
        } catch (Exception e) {
            System.err.println("❌ Erreur inattendue : " + e.getMessage());
            throw new SQLException("❌ Une erreur inattendue est survenue lors de la mise à jour de la livraison.", e);
        }
    }



    /*
    public static void main(String[] args) {
        try {
            LivraisonService livraisonService = new LivraisonService();
            CommandeDao commandeDao = new CommandeDao();

            System.out.println("\n📢 📦 Test de la gestion des livraisons 📦 📢");

            // ✅ Étape 1 : Vérifier s'il y a déjà des livraisons en base
            livraisonService.afficherToutesLesLivraisons();

            // ✅ Étape 2 : Récupérer une commande existante (dernière en base)
            List<Commande> commandes = commandeDao.recupererToutesLesCommandes();
            if (commandes.isEmpty()) {
                System.out.println("⚠️ Aucune commande en base, impossible de créer une livraison !");
                return;
            }

            Commande commande = commandes.get(commandes.size() - 1); // Dernière commande

            // ✅ Étape 3 : Créer une livraison pour cette commande
            livraisonService.creerLivraison(commande);

            // ✅ Étape 4 : Afficher les livraisons après l'ajout
            livraisonService.afficherToutesLesLivraisons();

        } catch (Exception e) {
            System.err.println("❌ Erreur lors du test des livraisons : " + e.getMessage());
            e.printStackTrace();
        }
    }*/
    /*
    public static void main(String[] args) {
        try {
            LivraisonService livraisonService = new LivraisonService();
            Scanner scanner = new Scanner(System.in);

            System.out.println("\n📢 📦 Test de la mise à jour du statut d'une livraison 📦 📢");

            // ✅ Afficher les livraisons avant mise à jour
            livraisonService.afficherToutesLesLivraisons();

            // ✅ Demander à l'utilisateur de choisir une livraison à modifier
            System.out.print("\nEntrez l'ID de la livraison à mettre à jour : ");
            int livraisonId = scanner.nextInt();
            scanner.nextLine();  // Consommer la ligne restante

            // ✅ Modifier le statut de la livraison
            System.out.print("Entrez le nouveau statut (ex: 'Livrée') : ");
            String nouveauStatut = scanner.nextLine();

            livraisonService.mettreAJourStatutLivraison(livraisonId, nouveauStatut);

            // ✅ Afficher les livraisons après mise à jour
            livraisonService.afficherToutesLesLivraisons();

        } catch (Exception e) {
            System.err.println("❌ Erreur lors de la mise à jour de la livraison : " + e.getMessage());
        }
    }*/
}
