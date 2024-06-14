module hr.k33zo.hanabi {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.naming;
    requires java.rmi;

    exports hr.k33zo.hanabi.chat.service to java.rmi;
    opens hr.k33zo.hanabi to javafx.fxml;
    exports hr.k33zo.hanabi;
    exports hr.k33zo.hanabi.controller;
    opens hr.k33zo.hanabi.controller to javafx.fxml;
}