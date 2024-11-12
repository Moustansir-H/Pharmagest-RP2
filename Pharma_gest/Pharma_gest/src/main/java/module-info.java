module mcci.businessschool.bts.sio.slam.pharmagest {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    //requires javafx.base;

    //opens model to javafx.base;
    opens mcci.businessschool.bts.sio.slam.pharmagest.login to javafx.fxml;
    exports mcci.businessschool.bts.sio.slam.pharmagest.login;
}
