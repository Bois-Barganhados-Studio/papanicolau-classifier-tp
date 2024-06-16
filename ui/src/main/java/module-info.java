module boisbarganhados {
    requires transitive javafx.graphics;
    requires transitive lombok;
    requires transitive javafx.swing;
    requires transitive org.bytedeco.opencv;
    requires javafx.controls;
    requires transitive javafx.fxml;
    requires java.desktop;
    requires atlantafx.base;
    requires org.kordamp.ikonli.core;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.ikonli.material2;
    requires org.kordamp.ikonli.materialdesign2;
    requires org.bytedeco.javacv;

    opens boisbarganhados to javafx.fxml;
    exports boisbarganhados;
}