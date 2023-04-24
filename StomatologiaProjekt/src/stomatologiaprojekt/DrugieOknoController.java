package stomatologiaprojekt;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;


public class DrugieOknoController implements Initializable {
    
    @FXML
    private Label zegarek;
    @FXML
    private Label kalendarzyk;
    
    //Ustawia zegar i kalendarz
    @Override
    public void initialize(URL urlo, ResourceBundle rb) {
        DatabaseHandler.initClock(zegarek,kalendarzyk);
    }   
    
    //Otwieranie nowych okien po wciśnięciu odpowiednich przycisków
    
    @FXML
    void RZabiegowEvent(ActionEvent event) throws IOException {   
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("ZarzadzajRodzajamiZabiegow.fxml"));
        Parent root1 = (Parent) fxmlLoader.load();
        Stage stage = new Stage();
        stage.setScene(new Scene(root1));  
        stage.setTitle("Zarządzaj rodzajami zabiegów");
        stage.show();
    }

    @FXML
    private void StomatologEvent(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("ZarzadzajStomatologami.fxml"));
        Parent root1 = (Parent) fxmlLoader.load();
        Stage stage = new Stage();
        stage.setScene(new Scene(root1));  
        stage.setTitle("Zarządzaj stomatologami");
        stage.show();
    }

    @FXML
    private void KlinikaEvent(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("ZarzadzajKlinikami.fxml"));
        Parent root1 = (Parent) fxmlLoader.load();
        Stage stage = new Stage();
        stage.setScene(new Scene(root1));  
        stage.setTitle("Zarządzaj klinikami");
        stage.show();
    }

    @FXML
    private void PacjentEvent(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("ZarzadzajPacjentami.fxml"));
        Parent root1 = (Parent) fxmlLoader.load();
        Stage stage = new Stage();
        stage.setScene(new Scene(root1));  
        stage.setTitle("Zarządzaj pacjentami");
        stage.show();
    }

    @FXML
    private void WizytaEvent(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("ZarzadzajWizytami.fxml"));
        Parent root1 = (Parent) fxmlLoader.load();
        Stage stage = new Stage();
        stage.setScene(new Scene(root1));  
        stage.setTitle("Zarządzaj wizytami");
        stage.show();
    }


    
}
