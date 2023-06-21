module com.pokergame {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.pokergame to javafx.fxml;
    exports com.pokergame;
}