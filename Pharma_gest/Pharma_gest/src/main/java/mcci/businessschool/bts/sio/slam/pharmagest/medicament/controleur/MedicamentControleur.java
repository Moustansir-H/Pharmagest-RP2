package mcci.businessschool.bts.sio.slam.pharmagest.medicament.controleur;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import org.w3c.dom.Node;

import java.io.IOException;

public class MedicamentControleur {

    @FXML
    private Button retourMedicament;
    @FXML
    private Button crudmedicamentButton;


    @FXML
    public void retourMedicamentOnAction(ActionEvent e) throws IOException {
        // Nouvelle scène
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/maintenance/Maintenance.fxml"));
        Scene nouvelleScene = new Scene(loader.load());

        // La référence de la scène actuelle
        Stage stage = (Stage) retourMedicament.getScene().getWindow();

        // Afficher la nouvelle scène
        stage.setScene(nouvelleScene);
    }


    @FXML
    public void crudMedicamentOnAction(ActionEvent e) throws IOException {
        // Nouvelle scène
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/medicament/Crudmedicament.fxml"));
        Scene nouvelleScene = new Scene(loader.load());

        // La référence de la scène actuelle
        Stage stage = (Stage) crudmedicamentButton.getScene().getWindow();

        // Afficher la nouvelle scène
        stage.setScene(nouvelleScene);
    }

}

