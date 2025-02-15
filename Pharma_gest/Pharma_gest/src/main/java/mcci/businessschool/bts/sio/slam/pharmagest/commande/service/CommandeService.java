package mcci.businessschool.bts.sio.slam.pharmagest.commande.service;

import mcci.businessschool.bts.sio.slam.pharmagest.commande.Commande;
import mcci.businessschool.bts.sio.slam.pharmagest.commande.dao.CommandeDao;
import mcci.businessschool.bts.sio.slam.pharmagest.pharmacien.Pharmacien;
import mcci.businessschool.bts.sio.slam.pharmagest.pharmacien.dao.PharmacienDao;

import java.util.List;

public class CommandeService {
    private final CommandeDao commandeDao;

    public CommandeService() throws Exception {
        this.commandeDao = new CommandeDao();
    }

    // 🔹 Récupérer toutes les commandes
    public List<Commande> recupererToutesLesCommandes() {
        return commandeDao.recupererToutesLesCommandes();
    }

    // 🔹 Récupérer une commande par son ID
    public Commande recupererCommandeParId(int idCommande) {
        return commandeDao.recupererCommandeParId(idCommande);
    }

    // 🔹 Ajouter une nouvelle commande
    public Integer ajouterCommande(Commande commande) {
        return commandeDao.ajouterCommande(commande);
    }

    // 🔹 Modifier une commande existante
    public void modifierCommande(Commande commande) {
        commandeDao.modifierCommande(commande);
    }

    // 🔹 Supprimer une commande par ID
    public void supprimerCommande(int idCommande) {
        commandeDao.supprimerCommande(idCommande);
    }

    public static void main(String[] args) {
        try {
            // Initialisation des DAO
            CommandeDao commandeDao = new CommandeDao();
            PharmacienDao pharmacienDao = new PharmacienDao();

            // 🔹 Étape 1 : Ajouter un pharmacien (si nécessaire)
            int pharmacienId = 1; // Remplace par un ID existant ou insère un nouveau pharmacien
            Pharmacien pharmacien = pharmacienDao.recupererPharmacienParId(pharmacienId);

            if (pharmacien == null) {
                pharmacienDao.ajouterPharmacien(pharmacienId);
                pharmacien = pharmacienDao.recupererPharmacienParId(pharmacienId);
            }

            // 🔹 Étape 2 : Ajouter une commande
            Commande nouvelleCommande = new Commande(0, 150.75, pharmacien);
            Integer idCommandeAjoutee = commandeDao.ajouterCommande(nouvelleCommande);
            System.out.println("Commande ajoutée avec ID : " + idCommandeAjoutee);

            // 🔹 Étape 3 : Récupérer toutes les commandes
            List<Commande> commandes = commandeDao.recupererToutesLesCommandes();
            System.out.println("\n🔹 Liste des commandes après ajout :");
            for (Commande c : commandes) {
                System.out.println("ID: " + c.getId() + ", Montant: " + c.getMontant() + ", Pharmacien ID: " + c.getPharmacien().getId());
            }

            // 🔹 Étape 4 : Modifier la commande ajoutée
            if (idCommandeAjoutee != null) {
                Commande commandeModifiee = new Commande(idCommandeAjoutee, 200.00, pharmacien);
                commandeDao.modifierCommande(commandeModifiee);
                System.out.println("\n✅ Commande modifiée !");
            }

            // 🔹 Étape 5 : Vérifier la modification
            Commande commandeRecuperee = commandeDao.recupererCommandeParId(idCommandeAjoutee);
            System.out.println("\n🔹 Commande après modification :");
            System.out.println("ID: " + commandeRecuperee.getId() + ", Montant: " + commandeRecuperee.getMontant());

            /* 🔹 Étape 6 : Supprimer la commande
            commandeDao.supprimerCommande(idCommandeAjoutee);
            System.out.println("\n❌ Commande supprimée avec succès !");*/

            // 🔹 Étape 7 : Vérifier la suppression
            commandes = commandeDao.recupererToutesLesCommandes();
            System.out.println("\n🔹 Liste des commandes après suppression :");
            for (Commande c : commandes) {
                System.out.println("ID: " + c.getId() + ", Montant: " + c.getMontant());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
