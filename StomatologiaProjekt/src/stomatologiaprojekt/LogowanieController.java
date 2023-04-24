package stomatologiaprojekt;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Objects;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;


public class LogowanieController implements Initializable {
    HashMap<String,String> passwords = new HashMap<>();
    @FXML
    private Label label;
    @FXML
    private TextField login;
    @FXML
    private PasswordField haslo;
    @FXML
    private Label label1;
    @FXML
    private Label incorrect;
    
    private DatabaseHandler handler;
    
    //Wciśnięcie przycisku logowania
    @FXML
    private void handleButtonAction(ActionEvent event) throws IOException, ClassNotFoundException, SQLException, InstantiationException, IllegalAccessException {
        openWindow(event,login.getText(),haslo.getText());
    }
    
    //Obsługa logowania poprzez wciśnięcie klawisza enter
    @FXML
    private void handleEnter(KeyEvent event) throws IOException, ClassNotFoundException, SQLException, InstantiationException, IllegalAccessException {
        if(event.getCode() == KeyCode.ENTER) {
                openWindow(event,login.getText(),haslo.getText());
        }
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //Wstawianie loginów i haseł do hashmapy
        passwords.put("admin","admin");
        passwords.put("","");
        //Ukrycie informacji o błędzie
        incorrect.setVisible(false);
    }
    
    //Otwieranie okna menu
    private void openWindow(Event event, String login, String haslo) throws IOException, ClassNotFoundException, SQLException, InstantiationException, IllegalAccessException {
        String compare=passwords.get(login);
        //Sprawdzenie, czy login i hasło są zgodne
        if(Objects.equals(compare,haslo)){
            
            handler=DatabaseHandler.getInstance();
            handler.archiwizacjaWszystkich();
            
            
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("DrugieOkno.fxml"));
            Parent root1 = (Parent) fxmlLoader.load();
            Stage stage = new Stage();
            
            stage.setScene(new Scene(root1)); 
            stage.setTitle("Menu");
            stage.setResizable(false);
            stage.show();

            ((Node)(event.getSource())).getScene().getWindow().hide();
        }
        //Gdy logowanie nie powiedzie się, pokazuje się informacja o niepoprawnych danych
        else incorrect.setVisible(true);
    }
}
