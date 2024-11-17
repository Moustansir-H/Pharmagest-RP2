module mcci.businessschool.bts.sio.slam.pharmagest {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires javafx.base;

    opens mcci.businessschool.bts.sio.slam.pharmagest.maintenance to javafx.fxml;
    exports mcci.businessschool.bts.sio.slam.pharmagest.maintenance;

    opens mcci.businessschool.bts.sio.slam.pharmagest.utilisateur.controleur to javafx.fxml;
    exports mcci.businessschool.bts.sio.slam.pharmagest.utilisateur.controleur;
    opens mcci.businessschool.bts.sio.slam.pharmagest.utilisateur to javafx.base;
    exports mcci.businessschool.bts.sio.slam.pharmagest;
    opens mcci.businessschool.bts.sio.slam.pharmagest to javafx.fxml;
    exports mcci.businessschool.bts.sio.slam.pharmagest.login.controleur;
    opens mcci.businessschool.bts.sio.slam.pharmagest.login.controleur to javafx.fxml;
    exports mcci.businessschool.bts.sio.slam.pharmagest.login.service;
    opens mcci.businessschool.bts.sio.slam.pharmagest.login.service to javafx.fxml;
}
