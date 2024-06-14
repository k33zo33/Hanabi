module hr.k33zo.hanabi {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.naming;


    opens hr.k33zo.hanabi to javafx.fxml;
    exports hr.k33zo.hanabi;
    exports hr.k33zo.hanabi.controller;
    opens hr.k33zo.hanabi.controller to javafx.fxml;
}