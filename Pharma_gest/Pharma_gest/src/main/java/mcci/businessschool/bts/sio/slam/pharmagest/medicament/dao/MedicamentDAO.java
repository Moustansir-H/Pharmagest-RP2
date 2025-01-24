package mcci.businessschool.bts.sio.slam.pharmagest.medicament.dao;

import mcci.businessschool.bts.sio.slam.pharmagest.database.DatabaseConnection;
import mcci.businessschool.bts.sio.slam.pharmagest.famille.Famille;
import mcci.businessschool.bts.sio.slam.pharmagest.famille.dao.FamilleDao;
import mcci.businessschool.bts.sio.slam.pharmagest.fournisseur.Fournisseur;
import mcci.businessschool.bts.sio.slam.pharmagest.fournisseur.dao.FournisseurDao;
import mcci.businessschool.bts.sio.slam.pharmagest.medicament.Medicament;
import mcci.businessschool.bts.sio.slam.pharmagest.unite.Unite;
import mcci.businessschool.bts.sio.slam.pharmagest.unite.dao.UniteDao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class MedicamentDAO {
    private final FamilleDao familleDao;
    private final FournisseurDao fournisseurDao;
    private final UniteDao uniteDao;
    private Connection baseDeDonneeConnexion;

    public MedicamentDAO() throws Exception {
        this.baseDeDonneeConnexion = DatabaseConnection.getConnexion();
        this.familleDao = new FamilleDao();
        this.fournisseurDao = new FournisseurDao();
        this.uniteDao = new UniteDao();
    }

    // Méthode pour récupérer tous les médicaments
    public List<Medicament> recupererMedicaments() {
        String selectMedicamentSql = "SELECT id, nom, forme, prixachat, prixvente, stock, seuilcommande, qtemax, " +
                "famille_id, fournisseur_id, unite_id FROM medicament";

        List<Medicament> medicaments = new ArrayList<>();

        try (Statement stmt = baseDeDonneeConnexion.createStatement();
             ResultSet rs = stmt.executeQuery(selectMedicamentSql)) {

            while (rs.next()) {
                String nom = rs.getString("nom");
                String forme = rs.getString("forme");
                double prixAchat = rs.getDouble("prixachat");
                double prixVente = rs.getDouble("prixvente");
                int stock = rs.getInt("stock");
                int seuilCommande = rs.getInt("seuilcommande");
                int qteMax = rs.getInt("qtemax");

                int familleId = rs.getInt("famille_id");
                int fournisseurId = rs.getInt("fournisseur_id");
                int uniteId = rs.getInt("unite_id");

                // Récupérer les relations via les DAO
                Famille famille = familleDao.getFamilleById(familleId);
                Fournisseur fournisseur = fournisseurDao.getFournisseurById(fournisseurId);
                Unite unite = uniteDao.recupererUniteeParId(uniteId);

                // Créer une instance de Medicament
                Medicament medicament = new Medicament(
                        nom, forme, prixAchat, prixVente, stock, seuilCommande, qteMax,
                        famille, fournisseur, unite
                );

                medicaments.add(medicament); // Ajouter le médicament à la liste
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des médicaments : " + e.getMessage());
        }

        return medicaments;
    }

    public Integer ajouterMedicament(Medicament medicament) throws SQLException {
        String insertSQL = """
                    INSERT INTO medicament 
                    (nom, forme, prixachat, prixvente, stock, seuilcommande, qtemax, famille_id, fournisseur_id, unite_id) 
                    VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?) 
                    RETURNING id
                """;

        try (PreparedStatement stmt = baseDeDonneeConnexion.prepareStatement(insertSQL)) {
            // Définir les paramètres de la requête
            stmt.setString(1, medicament.getNom());
            stmt.setString(2, medicament.getForme());
            stmt.setDouble(3, medicament.getPrixAchat());
            stmt.setDouble(4, medicament.getPrixVente());
            stmt.setInt(5, medicament.getStock());
            stmt.setInt(6, medicament.getSeuilCommande());
            stmt.setInt(7, medicament.getQteMax());
            stmt.setInt(8, medicament.getFamille().getId()); // Id de la famille
            stmt.setInt(9, medicament.getFournisseur().getId()); // Id du fournisseur
            stmt.setInt(10, medicament.getUnite().getId()); // Id de l'unité

            // Exécuter la requête
            try (ResultSet resultSet = stmt.executeQuery()) {
                if (resultSet.next()) {
                    // Récupérer l'ID généré
                    int id = resultSet.getInt("id");
                    System.out.println("Médicament ajouté avec succès !");
                    return id;
                } else {
                    throw new SQLException("Erreur lors de l'ajout du médicament, aucun ID retourné.");
                }
            }
        }
    }

    public void modifierMedicament(Medicament medicament) throws SQLException {
        String updateSQL = """
                    UPDATE medicament 
                    SET nom = ?, 
                        forme = ?, 
                        prixachat = ?, 
                        prixvente = ?, 
                        stock = ?, 
                        seuilcommande = ?, 
                        qtemax = ?, 
                        famille_id = ?, 
                        fournisseur_id = ?, 
                        unite_id = ? 
                    WHERE id = ?
                """;

        // Obtenir les IDs à partir des noms
        int familleId = familleDao.getFamilleIdByName(medicament.getFamille().getNom());
        int fournisseurId = fournisseurDao.getFournisseurIdByName(medicament.getFournisseur().getNom());
        int uniteId = uniteDao.getUniteIdByName(medicament.getUnite().getNomUnite());

        try (PreparedStatement stmt = baseDeDonneeConnexion.prepareStatement(updateSQL)) {
            // Remplir les paramètres
            stmt.setString(1, medicament.getNom());
            stmt.setString(2, medicament.getForme());
            stmt.setDouble(3, medicament.getPrixAchat());
            stmt.setDouble(4, medicament.getPrixVente());
            stmt.setInt(5, medicament.getStock());
            stmt.setInt(6, medicament.getSeuilCommande());
            stmt.setInt(7, medicament.getQteMax());
            stmt.setInt(8, familleId);
            stmt.setInt(9, fournisseurId);
            stmt.setInt(10, uniteId);
            stmt.setInt(11, medicament.getId()); // ID du médicament à modifier

            // Exécuter la requête
            int lignesModifiees = stmt.executeUpdate();

            if (lignesModifiees > 0) {
                System.out.println("Médicament modifié avec succès !");
            } else {
                System.out.println("Aucun médicament trouvé avec id = " + medicament.getId());
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la modification du médicament : " + e.getMessage());
        }
    }

    public void supprimerMedicamentParId(Integer id) {
        String deleteSQL = "DELETE FROM medicament WHERE id = ?";

        try (PreparedStatement stmt = baseDeDonneeConnexion.prepareStatement(deleteSQL)) {
            stmt.setInt(1, id);

            int ligneSupprimee = stmt.executeUpdate();

            if (ligneSupprimee > 0) {
                System.out.println("Médicament supprimé avec succès !");
            } else {
                System.out.println("Aucun médicament trouvé avec id = " + id);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression du médicament : " + e.getMessage());
        }
    }
    

    // Méthode main pour tester la suppression depuis la console
    public static void main(String[] args) throws Exception {
        // Créer un objet MedicamentDAO
        MedicamentDAO medicamentDao = new MedicamentDAO();

        // Créer un Scanner pour lire l'entrée de l'utilisateur
        Scanner scanner = new Scanner(System.in);

        try {
            // Demander à l'utilisateur d'entrer l'ID du médicament à supprimer
            System.out.print("Entrez l'ID du médicament à supprimer : ");
            int medicamentId = scanner.nextInt();

            // Appeler la méthode pour supprimer le médicament
            medicamentDao.supprimerMedicamentParId(medicamentId);

        } catch (Exception e) {
            System.err.println("Erreur lors de la saisie : " + e.getMessage());
        } finally {
            // Fermer le scanner
            scanner.close();
        }
    }

    /* Modification
    public static void main(String[] args) throws Exception {
        // Initialisation du scanner pour lire les entrées depuis la console
        Scanner scanner = new Scanner(System.in);

        // Initialisation de l'objet MedicamentDAO
        MedicamentDAO medicamentDao = new MedicamentDAO();

        try {
            // Demander à l'utilisateur de saisir l'ID du médicament à modifier
            System.out.print("Entrez l'ID du médicament à modifier : ");
            int medicamentId = scanner.nextInt();
            scanner.nextLine();  // Consommer la nouvelle ligne

            // Demander les informations du médicament
            System.out.print("Entrez le nouveau nom du médicament : ");
            String nom = scanner.nextLine();

            System.out.print("Entrez la nouvelle forme du médicament : ");
            String forme = scanner.nextLine();

            System.out.print("Entrez le nouveau prix d'achat : ");
            double prixAchat = scanner.nextDouble();

            System.out.print("Entrez le nouveau prix de vente : ");
            double prixVente = scanner.nextDouble();

            System.out.print("Entrez le nouveau stock : ");
            int stock = scanner.nextInt();

            System.out.print("Entrez le nouveau seuil de commande : ");
            int seuilCommande = scanner.nextInt();

            System.out.print("Entrez la nouvelle quantité maximale : ");
            int qteMax = scanner.nextInt();
            scanner.nextLine();  // Consommer la nouvelle ligne

            // Demander les IDs des entités liées
            System.out.print("Entrez le nom de la famille du médicament : ");
            String familleNom = scanner.nextLine();

            System.out.print("Entrez le nom du fournisseur : ");
            String fournisseurNom = scanner.nextLine();

            System.out.print("Entrez le nom de l'unité : ");
            String uniteNom = scanner.nextLine();

            // Récupérer les IDs des entités par leurs noms
            FamilleDao familleDao = new FamilleDao();
            FournisseurDao fournisseurDao = new FournisseurDao();
            UniteDao uniteDao = new UniteDao();

            int familleId = familleDao.getFamilleIdByName(familleNom);
            int fournisseurId = fournisseurDao.getFournisseurIdByName(fournisseurNom);
            int uniteId = uniteDao.getUniteIdByName(uniteNom);

            // Créer les objets Famille, Fournisseur et Unite
            Famille famille = new Famille(familleId, familleNom);
            Fournisseur fournisseur = new Fournisseur(fournisseurId, fournisseurNom, "AdresseTest", "ContactTest");
            Unite unite = new Unite(uniteId, uniteNom);

            // Créer l'objet Medicament à modifier
            Medicament medicament = new Medicament(
                    medicamentId,         // ID du médicament existant
                    nom,                  // Nouveau nom
                    forme,                // Nouvelle forme
                    prixAchat,            // Nouveau prix d'achat
                    prixVente,            // Nouveau prix de vente
                    stock,                // Nouveau stock
                    seuilCommande,        // Nouveau seuil de commande
                    qteMax,               // Nouvelle quantité max
                    famille,              // Famille associée
                    fournisseur,          // Fournisseur associé
                    unite                 // Unité associée
            );

            // Tester la méthode modifierMedicament
            medicamentDao.modifierMedicament(medicament);

        } catch (SQLException e) {
            System.err.println("Erreur lors de la modification du médicament : " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Erreur : " + e.getMessage());
        } finally {
            // Fermer le scanner
            scanner.close();
        }
    }*/



    /* Méthode main pour tester
    public static void main(String[] args) {
        try {
            MedicamentDAO medicamentDao = new MedicamentDAO();
            List<Medicament> medicaments = medicamentDao.recupererMedicaments();

            // Vérifier si la liste est vide
            if (medicaments.isEmpty()) {
                System.out.println("Aucun médicament trouvé dans la base de données.");
            } else {
                // Affichage de la liste complète des médicaments directement
                medicaments.forEach(System.out::println); // Utilisation de la méthode toString de chaque objet
            }
        } catch (Exception e) {
            System.err.println("Erreur lors du test : " + e.getMessage());
        }
    }*/

    /*Ajout automatique de medoc
    public static void main(String[] args) {
        try {
            // Initialiser la connexion et l'objet MedicamentDAO
            MedicamentDAO medicamentDao = new MedicamentDAO();

            // Exemple : Récupérer les IDs des entités nécessaires
            FamilleDao familleDao = new FamilleDao();
            FournisseurDao fournisseurDao = new FournisseurDao();
            UniteDao uniteDao = new UniteDao();

            int familleId = familleDao.getFamilleIdByName("Antibiotique");
            int fournisseurId = fournisseurDao.getFournisseurIdByName("Kevin");
            int uniteId = uniteDao.getUniteIdByName("Crème");

            // Créer les objets Famille, Fournisseur et Unite
            Famille famille = new Famille(familleId, "FamilleTest");
            Fournisseur fournisseur = new Fournisseur(fournisseurId, "FournisseurTest", "AdresseTest", "ContactTest");
            Unite unite = new Unite(uniteId, "UniteTest");

            // Créer un objet Medicament
            Medicament medicament = new Medicament(
                    "Paracetamol",    // nom
                    "Comprimé",       // forme
                    10.50,            // prixAchat
                    15.75,            // prixVente
                    100,              // stock
                    10,               // seuilCommande
                    200,              // qteMax
                    famille,          // famille
                    fournisseur,      // fournisseur
                    unite             // unite
            );

            // Appeler la méthode ajouterMedicament
            Integer medicamentId = medicamentDao.ajouterMedicament(medicament);

            // Afficher le résultat
            if (medicamentId != null) {
                System.out.println("Médicament ajouté avec succès ! ID généré : " + medicamentId);
            } else {
                System.out.println("Échec de l'ajout du médicament.");
            }

        } catch (Exception e) {
            System.err.println("Erreur : " + e.getMessage());
            e.printStackTrace();
        }
    }*/

    /* Ajout dans console
    public static void main(String[] args) {
        try {
            // Initialiser la connexion et l'objet MedicamentDAO
            MedicamentDAO medicamentDao = new MedicamentDAO();

            // Initialiser les DAOs
            FamilleDao familleDao = new FamilleDao();
            FournisseurDao fournisseurDao = new FournisseurDao();
            UniteDao uniteDao = new UniteDao();

            // Créer un Scanner pour la saisie
            java.util.Scanner scanner = new java.util.Scanner(System.in);

            // Demander à l'utilisateur de saisir les informations nécessaires
            System.out.print("Entrez le nom du médicament : ");
            String nom = scanner.nextLine();

            System.out.print("Entrez la forme du médicament (ex: Comprimé, Gélule) : ");
            String forme = scanner.nextLine();

            System.out.print("Entrez le prix d'achat : ");
            double prixAchat = scanner.nextDouble();

            System.out.print("Entrez le prix de vente : ");
            double prixVente = scanner.nextDouble();

            System.out.print("Entrez le stock disponible : ");
            int stock = scanner.nextInt();

            System.out.print("Entrez le seuil de commande : ");
            int seuilCommande = scanner.nextInt();

            System.out.print("Entrez la quantité maximale : ");
            int qteMax = scanner.nextInt();
            scanner.nextLine(); // Consommer la ligne restante après les entiers

            // Demander le nom de la famille
            System.out.print("Entrez le nom de la famille du médicament : ");
            String familleNom = scanner.nextLine();
            int familleId = familleDao.getFamilleIdByName(familleNom);

            // Demander le nom du fournisseur
            System.out.print("Entrez le nom du fournisseur : ");
            String fournisseurNom = scanner.nextLine();
            int fournisseurId = fournisseurDao.getFournisseurIdByName(fournisseurNom);

            // Demander le nom de l'unité
            System.out.print("Entrez le nom de l'unité (ex: Boîte, Crème) : ");
            String uniteNom = scanner.nextLine();
            int uniteId = uniteDao.getUniteIdByName(uniteNom);

            // Créer les objets Famille, Fournisseur, et Unite avec les IDs récupérés
            Famille famille = new Famille(familleId, familleNom);
            Fournisseur fournisseur = new Fournisseur(fournisseurId, fournisseurNom, "Adresse inconnue", "Contact inconnu");
            Unite unite = new Unite(uniteId, uniteNom);

            // Créer un objet Medicament avec les informations saisies
            Medicament medicament = new Medicament(
                    nom,              // nom
                    forme,            // forme
                    prixAchat,        // prixAchat
                    prixVente,        // prixVente
                    stock,            // stock
                    seuilCommande,    // seuilCommande
                    qteMax,           // qteMax
                    famille,          // famille
                    fournisseur,      // fournisseur
                    unite             // unite
            );

            // Appeler la méthode ajouterMedicament
            Integer medicamentId = medicamentDao.ajouterMedicament(medicament);

            // Afficher le résultat
            if (medicamentId != null) {
                System.out.println("Médicament ajouté avec succès ! ID généré : " + medicamentId);
            } else {
                System.out.println("Échec de l'ajout du médicament.");
            }

            // Fermer le scanner
            scanner.close();

        } catch (Exception e) {
            System.err.println("Erreur : " + e.getMessage());
            e.printStackTrace();
        }
    }
    */

}
