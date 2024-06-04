module com.boisbarganhados {
    requires transitive javafx.controls;
    requires transitive javafx.fxml;
    requires transitive javafx.graphics;
    requires transitive org.jfxtras.styles.jmetro;
    requires transitive javafx.swing;
    requires transitive java.desktop;
    requires transitive lombok;
    requires transitive org.bytedeco.opencv;
    requires transitive org.bytedeco.javacv;

    opens com.boisbarganhados to javafx.fxml;
    opens com.boisbarganhados.view to javafx.fxml;
    exports com.boisbarganhados;
    exports com.boisbarganhados.view;
}
