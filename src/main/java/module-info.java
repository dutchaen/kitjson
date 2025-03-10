module com.dutchaen.kitjson {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.google.gson;
    requires java.desktop;


    opens com.dutchaen.kitjson to javafx.fxml;
    exports com.dutchaen.kitjson;
}