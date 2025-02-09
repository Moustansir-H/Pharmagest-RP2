package mcci.businessschool.bts.sio.slam.pharmagest.vente.service;

import mcci.businessschool.bts.sio.slam.pharmagest.vente.Vente;
import mcci.businessschool.bts.sio.slam.pharmagest.vente.dao.VenteDAO;

import java.sql.SQLException;
import java.util.List;

public class VenteService {

    private VenteDAO venteDAO;

    public VenteService() throws Exception {
        this.venteDAO = new VenteDAO();
    }

    /**
     * Récupère toutes les ventes enregistrées dans la base de données.
     *
     * @return Liste des ventes.
     */
    public List<Vente> recupererVentes() {
        return venteDAO.recupererVentes();
    }

    /**
     * Ajoute une nouvelle vente dans la base de données.
     *
     * @param vente L'objet Vente à ajouter.
     */
    public void ajouterVente(Vente vente) {
        try {
            Integer idVente = venteDAO.ajouterVente(vente);
            vente.setId(idVente);
            System.out.println("Vente ajoutée avec succès !");
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'ajout de la vente : " + e.getMessage());
        }
    }

    /**
     * Modifie une vente existante.
     *
     * @param vente L'objet Vente mis à jour.
     */
    public void modifierVente(Vente vente) {
        try {
            venteDAO.modifierVente(vente);
            System.out.println("Vente modifiée avec succès !");
        } catch (Exception e) {
            System.err.println("Erreur lors de la modification de la vente : " + e.getMessage());
        }
    }

    /**
     * Supprime une vente par son ID.
     *
     * @param id L'ID de la vente à supprimer.
     */
    public void supprimerVenteParId(Integer id) {
        try {
            boolean suppression = venteDAO.supprimerVenteParId(id);
            if (suppression) {
                System.out.println("Vente supprimée avec succès !");
            } else {
                System.out.println("Aucune vente trouvée avec l'ID " + id);
            }
        } catch (Exception e) {
            System.err.println("Erreur lors de la suppression de la vente : " + e.getMessage());
        }
    }
}
