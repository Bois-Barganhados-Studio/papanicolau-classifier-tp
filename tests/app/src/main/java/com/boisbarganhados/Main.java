package com.boisbarganhados;

import com.boisbarganhados.winApi.Dwmapi;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.WinDef;
import javafx.stage.Stage;
import javafx.stage.Window;
import lombok.val;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.stage.Stage;
import javafx.stage.Window;
import jfxtras.styles.jmetro.JMetro;
import jfxtras.styles.jmetro.Style;

public class Main extends Application {

    private static Stage primaryStage;

    @Override
    public void start(Stage primaryStage) throws Exception {
        Main.primaryStage = primaryStage;
        
        setDarkMode(primaryStage, false);
        primaryStage.setTitle("PAI Test");
        // use TestSemFxml to test without FXML
        var loader = new FXMLLoader(Main.class.getResource("view/ImageZoomAndMove.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root, 800, 600);
        applyJMetro(scene);
        scene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());
        primaryStage.setScene(scene);

        primaryStage.show();
    }

    public static void setRoot(String fxml) throws Exception {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("view/" + fxml));
        Parent root = loader.load();
        Scene scene = new Scene(root, 800, 600);
        scene.getStylesheets().add(Main.class.getResource("styles.css").toExternalForm());
        applyJMetro(scene);
        primaryStage.setScene(scene);
    }

    public static FXMLLoader showDialog(String fxml) throws Exception {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("view/" + fxml));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        scene.getStylesheets().add(Main.class.getResource("styles.css").toExternalForm());
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.show();
        return loader;
    }

    private static void applyJMetro(Scene scene) {
        JMetro jMetro = new JMetro(Style.DARK);
        jMetro.setScene(scene);
    }

    public static void main(String[] args) {
        launch(args);
    }

    public static WinDef.HWND getNativeHandleForStage(Stage stage) {
        try {
            val getPeer = Window.class.getDeclaredMethod("getPeer", null);
            getPeer.setAccessible(true);
            val tkStage = getPeer.invoke(stage);
            val getRawHandle = tkStage.getClass().getMethod("getRawHandle");
            getRawHandle.setAccessible(true);
            val pointer = new Pointer((Long) getRawHandle.invoke(tkStage));
            return new WinDef.HWND(pointer);
        } catch (Exception ex) {
            System.err.println("Unable to determine native handle for window");
            ex.printStackTrace();
            return null;
        }
    }

    public static void setDarkMode(Stage stage, boolean darkMode) {
        val hwnd = getNativeHandleForStage(stage);
        val dwmapi = Dwmapi.INSTANCE;
        WinDef.BOOLByReference darkModeRef = new WinDef.BOOLByReference(new WinDef.BOOL(darkMode));

        dwmapi.DwmSetWindowAttribute(hwnd, 20, darkModeRef, Native.getNativeSize(WinDef.BOOLByReference.class));

        forceRedrawOfWindowTitleBar(stage);
    }

    private static void forceRedrawOfWindowTitleBar(Stage stage) {
        val maximized = stage.isMaximized();
        stage.setMaximized(!maximized);
        stage.setMaximized(maximized);
    }
}
