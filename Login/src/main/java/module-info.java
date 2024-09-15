module tuto.login {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens tuto.login to javafx.fxml;
    exports tuto.login;
}