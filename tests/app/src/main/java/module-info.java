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
    requires transitive com.sun.jna;
    requires transitive com.sun.jna.platform;

    opens com.boisbarganhados to javafx.fxml, javafx.stage, javafx.graphics;
    opens com.boisbarganhados.view to javafx.fxml;
    opens com.boisbarganhados.winApi to javafx.fxml, javafx.stage, javafx.graphics, com.sun.jna;
    exports com.boisbarganhados;
    exports com.boisbarganhados.view;
    exports com.boisbarganhados.winApi;
}
