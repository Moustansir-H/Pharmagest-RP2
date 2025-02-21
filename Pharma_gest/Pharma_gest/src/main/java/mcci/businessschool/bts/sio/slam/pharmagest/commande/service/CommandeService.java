package mcci.businessschool.bts.sio.slam.pharmagest.commande.service;

import mcci.businessschool.bts.sio.slam.pharmagest.commande.Commande;
import mcci.businessschool.bts.sio.slam.pharmagest.commande.LigneDeCommande;
import mcci.businessschool.bts.sio.slam.pharmagest.commande.dao.CommandeDao;
import mcci.businessschool.bts.sio.slam.pharmagest.commande.dao.LigneDeCommandeDao;
import mcci.businessschool.bts.sio.slam.pharmagest.fournisseur.Fournisseur;
import mcci.businessschool.bts.sio.slam.pharmagest.livraison.service.LivraisonService;
import mcci.businessschool.bts.sio.slam.pharmagest.medicament.dao.MedicamentDao;
import mcci.businessschool.bts.sio.slam.pharmagest.pharmacien.Pharmacien;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommandeService {
    private CommandeDao commandeDao;
    private LigneDeCommandeDao ligneDeCommandeDao;
    private MedicamentDao medicamentDao;
    private LivraisonService livraisonService;


    public CommandeService() throws Exception {
        this.commandeDao = new CommandeDao();
        this.ligneDeCommandeDao = new LigneDeCommandeDao();
        this.medicamentDao = new MedicamentDao();
        this.livraisonService = new LivraisonService();
    }

    // Ajouter une nouvelle commande et retourner son ID
    public int ajouterCommande(Commande commande) throws SQLException {
        if (commande.getMontantTotal() < 0) {
            throw new IllegalArgumentException("Le montant total doit être positif.");
        }
        return commandeDao.ajouterCommande(commande);
    }

    // Récupérer toutes les commandes
    public List<Commande> recupererToutesLesCommandes() throws SQLException {
        return commandeDao.recupererToutesLesCommandes();
    }

    // Valider une commande en mettant à jour son montant total

    public void validerCommande(int commandeId) throws SQLException {
        // Vérifier si la commande contient des lignes
        List<LigneDeCommande> lignes = ligneDeCommandeDao.recupererLignesParCommande(commandeId);
        if (lignes.isEmpty()) {
            throw new SQLException("❌ Impossible de valider une commande sans lignes de commande.");
        }

        // Calcul du montant total
        double montantTotal = 0.0;
        for (LigneDeCommande ligne : lignes) {
            montantTotal += ligne.getQuantiteVendu() * ligne.getPrixUnitaire();
        }

        // Mise à jour du montant total de la commande
        commandeDao.mettreAJourMontantCommande(commandeId, montantTotal);

        // Création automatique de la livraison
        Commande commande = commandeDao.recupererCommandeParId(commandeId);
        if (commande.getFournisseur() == null) {
            throw new SQLException("❌ Erreur : Le fournisseur de la commande ID " + commandeId + " est introuvable.");
        }
        livraisonService.creerLivraison(commande);


        System.out.println("✅ Commande ID " + commandeId + " validée avec un montant total de " + montantTotal + " €.");
    }

    public Map<Fournisseur, List<LigneDeCommande>> preparerNouvelleCommande() throws SQLException {
        Map<Fournisseur, List<LigneDeCommande>> commandesParFournisseur = new HashMap<>();
        List<Fournisseur> fournisseurs = commandeDao.recupererFournisseursAvecMedicamentsSousSeuil();

        for (Fournisseur fournisseur : fournisseurs) {
            List<LigneDeCommande> lignes = medicamentDao.recupererMedicamentsSousSeuilParFournisseur(fournisseur.getId());
            commandesParFournisseur.put(fournisseur, lignes);
        }

        return commandesParFournisseur;
    }


    public int ajouterCommandeAvecLignes(Pharmacien pharmacien, Fournisseur fournisseur, List<LigneDeCommande> lignes) throws SQLException {
        if (lignes.isEmpty()) {
            throw new SQLException("❌ Impossible de créer une commande sans lignes de commande.");
        }

        Commande nouvelleCommande = new Commande(0.0, pharmacien, fournisseur, new ArrayList<>());
        int commandeId = commandeDao.ajouterCommande(nouvelleCommande);

        for (LigneDeCommande ligne : lignes) {
            ligne.setCommande(new Commande(commandeId));
            ligneDeCommandeDao.ajouterLigneDeCommande(ligne);
        }

        return commandeId;
    }

/*
    public static void main(String[] args) {
        try {
            CommandeService commandeService = new CommandeService();
            Scanner scanner = new Scanner(System.in);

            System.out.println("\n📢 📋 Test de création d'une commande 📋 📢");

            // ✅ Étape 1 : Préparer la commande en récupérant les médicaments sous seuil
            Map<Fournisseur, List<LigneDeCommande>> commandesParFournisseur = commandeService.preparerNouvelleCommande();

            if (commandesParFournisseur.isEmpty()) {
                System.out.println("⚠️ Aucun médicament sous seuil. Aucune commande à créer.");
                return;
            }

            // ✅ Étape 2 : Sélection du fournisseur
            System.out.println("\n📌 Fournisseurs disponibles :");
            int index = 1;
            for (Fournisseur fournisseur : commandesParFournisseur.keySet()) {
                System.out.println(index + ". " + fournisseur.getNom());
                index++;
            }

            System.out.print("\nEntrez le numéro du fournisseur pour la commande : ");
            int choix = scanner.nextInt();
            scanner.nextLine(); // Consommer la ligne restante

            if (choix < 1 || choix > commandesParFournisseur.size()) {
                System.out.println("❌ Sélection invalide.");
                return;
            }

            Fournisseur fournisseurSelectionne = (Fournisseur) commandesParFournisseur.keySet().toArray()[choix - 1];
            List<LigneDeCommande> lignesACommander = commandesParFournisseur.get(fournisseurSelectionne);

            // ✅ Étape 3 : Création de la commande
            Pharmacien pharmacien = new Pharmacien(1); // ID fixe pour le test
            int commandeId = commandeService.ajouterCommandeAvecLignes(pharmacien, fournisseurSelectionne, lignesACommander);

            System.out.println("✅ Commande créée avec ID : " + commandeId);
            System.out.println("📦 Médicaments commandés :");
            for (LigneDeCommande ligne : lignesACommander) {
                System.out.println("💊 " + ligne.getMedicament().getNom() + " | Quantité : " + ligne.getQuantiteVendu());
            }

        } catch (SQLException e) {
            System.err.println("❌ Erreur SQL : " + e.getMessage());
        } catch (Exception e) {
            System.err.println("❌ Erreur : " + e.getMessage());
        }
    }*/

    /*
    public static void main(String[] args) {
        try {
            CommandeService commandeService = new CommandeService();
            LigneDeCommandeService ligneService = new LigneDeCommandeService();

            // ✅ 1. Création d'une commande test (Pharmacien ID = 1, Fournisseur ID = 1)
            Commande commandeTest = new Commande(0.0, new Pharmacien(1), new Fournisseur(1, "FournisseurTest"), new ArrayList<>());
            int commandeId = commandeService.ajouterCommande(commandeTest);
            System.out.println("✅ Commande ajoutée avec succès, ID : " + commandeId);

            // ✅ 2. Ajout d'une ligne de commande avant validation
            LigneDeCommande ligneTest = new LigneDeCommande(
                    new Commande(commandeId),
                    new Medicament(58),  // ID d'un médicament existant
                    5,  // Quantité commandée
                    10.0,  // Prix unitaire
                    0,  // Quantité reçue (car non encore livrée)
                    0.0,  // Prix d'achat réel (sera mis à jour plus tard)
                    0.0   // Prix de vente réel (sera mis à jour plus tard)
            );
            ligneService.ajouterLigneDeCommande(ligneTest);
            System.out.println("✅ Ligne de commande ajoutée avec succès.");

            // ✅ 3. Récupération et affichage de toutes les commandes
            List<Commande> commandes = commandeService.recupererToutesLesCommandes();
            System.out.println("📢 Commandes récupérées :");
            for (Commande commande : commandes) {
                System.out.println(commande);
            }

            // ✅ 4. Validation de la commande (mise à jour du montant total)
            commandeService.validerCommande(commandeId);
            System.out.println("✅ Commande ID " + commandeId + " validée avec succès.");

        } catch (SQLException e) {
            System.err.println("❌ Erreur SQL : " + e.getMessage());
        } catch (Exception e) {
            System.err.println("❌ Erreur : " + e.getMessage());
        }

    }*/

    /*
    public static void main(String[] args) {
        try {
            CommandeService commandeService = new CommandeService();
            CommandeDao commandeDao = new CommandeDao();
            LivraisonDao livraisonDao = new LivraisonDao();
            Scanner scanner = new Scanner(System.in);

            System.out.println("\n📢 📋 Test de validation d'une commande 📋 📢");

            // ✅ Afficher les commandes en attente
            List<Commande> commandes = commandeDao.recupererToutesLesCommandes();
            if (commandes.isEmpty()) {
                System.out.println("⚠️ Aucune commande en base.");
                return;
            }

            System.out.println("\n📌 Commandes disponibles :");
            for (Commande commande : commandes) {
                System.out.println("ID : " + commande.getId() + " | Fournisseur : " + commande.getFournisseur().getNom() +
                        " | Montant : " + commande.getMontantTotal() + " € | Statut : " + commande.getStatut());
            }

            // ✅ Sélectionner une commande à valider
            System.out.print("\nEntrez l'ID de la commande à valider : ");
            int commandeId = scanner.nextInt();

            // ✅ Validation de la commande
            commandeService.validerCommande(commandeId);
            System.out.println("✅ Commande ID " + commandeId + " validée avec succès.");

            // ✅ Vérifier si la livraison a été créée
            System.out.println("\n📢 Vérification des livraisons après validation...");
            System.out.println("📦 Livraisons enregistrées :");
            livraisonDao.recupererToutesLesLivraisons().forEach(System.out::println);

        } catch (SQLException e) {
            System.err.println("❌ Erreur SQL lors de la validation : " + e.getMessage());
        } catch (Exception e) {
            System.err.println("❌ Erreur inattendue : " + e.getMessage());
        }
    }*/
}
