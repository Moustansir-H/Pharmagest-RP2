package mcci.businessschool.bts.sio.slam.pharmagest.login.controleur;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import mcci.businessschool.bts.sio.slam.pharmagest.login.service.LoginService;
import mcci.businessschool.bts.sio.slam.pharmagest.dashboard.controleur.DashboardControleur;

import java.io.IOException;


public class LoginControleur {
    @FXML
    private Button loginButton;
    @FXML
    private Label loginMessageLabel;
    @FXML
    private Button cancelButton;
    @FXML
    private TextField UsernameTxt;
    @FXML
    private PasswordField PasswordTxt;


    @FXML
    private Button retourMaintenance;



    private LoginService loginService;

    public LoginControleur() throws Exception {
        this.loginService = new LoginService();
    }


    @FXML
    public void loginButtonOnAction(ActionEvent event) throws Exception {
        if (!UsernameTxt.getText().isBlank() && !PasswordTxt.getText().isBlank()) {
            //          Validation BDD
            seLoguer();


            //loginMessageLabel.setText("You try to login!");
        } else {
            loginMessageLabel.setText("Veuillez remplir tous les champs!");
        }
    }

    @FXML
    public void cancelButtonOnAction(ActionEvent e) {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    public void seLoguer() throws IOException {
        boolean estConnecter = loginService.seConnecter(UsernameTxt.getText(), PasswordTxt.getText());

        if (estConnecter) {
            loginMessageLabel.setText("Bienvenu dans l'application");

            // Nouvelle scène
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/dashboard/Dashboard.fxml"));
            Scene nouvelleScene = new Scene(loader.load());
            // Récupérer le contrôleur
            DashboardControleur dashboardController = loader.getController();
            // Passer le nom d'utilisateur
            dashboardController.setNomUtilisateur(UsernameTxt.getText());
            // La référence de la scène actuelle
            Stage stage = (Stage) loginButton.getScene().getWindow();
            // Afficher la nouvelle scène
            stage.setScene(nouvelleScene);
            stage.setMaximized(true);
            stage.setTitle(UsernameTxt.getText());
        } else {
            loginMessageLabel.setText("Login invalide, Veuillez ressayer.");
        }
    }



    @FXML
    public void retourMaintenanceOnAction(ActionEvent e) throws IOException {
        // Nouvelle scène
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/dashboard/Dashboard.fxml"));
        Scene nouvelleScene = new Scene(loader.load());
        // La référence de la scène actuelle
        Stage stage = (Stage) retourMaintenance.getScene().getWindow();
        // Afficher la nouvelle scène
        stage.setScene(nouvelleScene);
        stage.setMaximized(true);
    }


}