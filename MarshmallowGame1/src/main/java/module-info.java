module com.example.marshmallowgame1 {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;
    requires javafx.graphics;


    opens com.example.marshmallowgame1 to javafx.fxml;
    exports com.example.marshmallowgame1;
}