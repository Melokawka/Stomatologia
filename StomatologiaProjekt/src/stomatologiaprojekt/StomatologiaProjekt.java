package stomatologiaprojekt;

import java.sql.SQLException;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class StomatologiaProjekt extends Application {
    
    //Start aplikacji- otwieranie okna logowania
    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("Logowanie.fxml"));

        Scene scene = new Scene(root);

        stage.setScene(scene);
        stage.setTitle("Logowanie");
        stage.setResizable(false);
        stage.show();
    }
    public static void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException, SQLException {
        launch(args);
    }
    
}