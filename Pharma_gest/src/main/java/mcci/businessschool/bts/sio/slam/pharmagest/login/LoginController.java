package mcci.businessschool.bts.sio.slam.pharmagest.login;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import mcci.businessschool.bts.sio.slam.pharmagest.database.DatabaseConnection;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;


public class LoginController {
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
    private Button quitButton;
    @FXML
    private Button changeUserButton;
    @FXML
    private Button confirmDex;
    @FXML
    private Button annulDex;
    @FXML
    private Button maintenanceButton;
    @FXML
    private Button retourMaintenance;
    @FXML
    private Button venteButton;
    @FXML
    private Button caisseButton;
    //Utilisateur
   /* @FXML
    private TableView tableView;
    @FXML
    private Button AjoutUser;
    @FXML
    private Button SupUser;
    @FXML
    private Button ModUser;
    @FXML
    private TextField nomUtilisateur;
    @FXML
    private TextField prenomUtilisateur;
    @FXML
    private TextField mailUtilisateur;*/


    @FXML
    public void loginButtonOnAction(ActionEvent event) throws IOException {
        if (!UsernameTxt.getText().isBlank() && !PasswordTxt.getText().isBlank()) {
            //          Validation BDD
            validateLogin();


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

    public void validateLogin() {
        DatabaseConnection connectNow = new DatabaseConnection();
        Connection connectDB = connectNow.getConnection();

        String verifyLogin = "SELECT count(0) FROM public.\"Utilisateur\" " + "WHERE \"Username\" ='" + UsernameTxt.getText() + "'AND \"Password\" = '" + PasswordTxt.getText() + "';";

        try {
            Statement statement = connectDB.createStatement();
            ResultSet queryResult = statement.executeQuery(verifyLogin);

            while (queryResult.next()) {
                if (queryResult.getInt(1) == 1) {
                    loginMessageLabel.setText("Bienvenu dans l'application");


                    // Nouvelle scène
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("Dashboard.fxml"));
                    Scene nouvelleScene = new Scene(loader.load());
                    // La référence de la scène actuelle
                    Stage stage = (Stage) loginButton.getScene().getWindow();
                    // Afficher la nouvelle scène
                    stage.setScene(nouvelleScene);
                    stage.setTitle(UsernameTxt.getText());


                } else {
                    loginMessageLabel.setText("Login invalide, Veuillez ressayer.");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void quitButtonOnAction(ActionEvent e) throws IOException {
        // Nouvelle scène
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Deconnexion.fxml"));
        Scene nouvelleScene = new Scene(loader.load());
        // La référence de la scène actuelle
        Stage stage = (Stage) changeUserButton.getScene().getWindow();
        // Afficher la nouvelle scène
        stage.setScene(nouvelleScene);
    }

    @FXML
    public void changeUserButtonOnAction(ActionEvent e) throws IOException {
        // Nouvelle scène
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Login.fxml"));
        Scene nouvelleScene = new Scene(loader.load());
        // La référence de la scène actuelle
        Stage stage = (Stage) changeUserButton.getScene().getWindow();
        // Afficher la nouvelle scène
        stage.setScene(nouvelleScene);
        stage.setTitle("Login");

    }

    @FXML
    public void annulDexButtonOnAction(ActionEvent e) throws IOException {
        // Nouvelle scène
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Dashboard.fxml"));
        Scene nouvelleScene = new Scene(loader.load());
        // La référence de la scène actuelle
        Stage stage = (Stage) annulDex.getScene().getWindow();
        // Afficher la nouvelle scène
        stage.setScene(nouvelleScene);
    }

    @FXML
    public void confirmDexOnAction(ActionEvent e) throws IOException {
        Stage stage = (Stage) confirmDex.getScene().getWindow();
        stage.close();
    }

    @FXML
    public void maintenanceButtonOnAction(ActionEvent e) throws IOException {
        // Nouvelle scène
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Maintenance.fxml"));
        Scene nouvelleScene = new Scene(loader.load());
        // La référence de la scène actuelle
        Stage stage = (Stage) maintenanceButton.getScene().getWindow();
        // Afficher la nouvelle scène
        stage.setScene(nouvelleScene);
    }

    @FXML
    public void retourMaintenanceOnAction(ActionEvent e) throws IOException {
        // Nouvelle scène
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Dashboard.fxml"));
        Scene nouvelleScene = new Scene(loader.load());
        // La référence de la scène actuelle
        Stage stage = (Stage) retourMaintenance.getScene().getWindow();
        // Afficher la nouvelle scène
        stage.setScene(nouvelleScene);
    }

    @FXML
    public void venteButtonOnAction(ActionEvent e) throws IOException {
        // Nouvelle scène
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Vente.fxml"));
        Scene nouvelleScene = new Scene(loader.load());
        // La référence de la scène actuelle
        Stage stage = (Stage) venteButton.getScene().getWindow();
        // Afficher la nouvelle scène
        stage.setScene(nouvelleScene);
    }

    @FXML
    public void caisseButtonOnAction(ActionEvent e) throws IOException {
        // Nouvelle scène
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Caisse.fxml"));
        Scene nouvelleScene = new Scene(loader.load());
        // La référence de la scène actuelle
        Stage stage = (Stage) caisseButton.getScene().getWindow();
        // Afficher la nouvelle scène
        stage.setScene(nouvelleScene);
    }
}