package stomatologiaprojekt;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class DodajKlinikeController implements Initializable {

    private DatabaseHandler handler;
    private String sql;

    @FXML
    private TextField MiejscowoscTekst;
    @FXML
    private TextField AdresTekst;
    @FXML
    private TextField KPocztowyTekst;
    @FXML
    private TextField TelefonTekst;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }

    @FXML
    private void DodajButtonAction(ActionEvent event) throws ClassNotFoundException, SQLException, InstantiationException, IllegalAccessException {
        if (MiejscowoscTekst.getText().equals("") || AdresTekst.getText().equals("") || KPocztowyTekst.getText().equals("") || TelefonTekst.getText().equals("")) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Uwaga");
            alert.setHeaderText("Nie wypełniono wymaganych pól");
            alert.setContentText("Proszę wypełnić wszystkie pola");
            alert.showAndWait();
        }
        
        else try {
            handler = DatabaseHandler.getInstance();
            sql = "{call klinika_pack.dodaj(?,?,?,?)}";
            handler.dodajKlinike(sql, MiejscowoscTekst.getText(), AdresTekst.getText(), KPocztowyTekst.getText(), TelefonTekst.getText());
            Stage stage = (Stage) MiejscowoscTekst.getScene().getWindow();
            stage.close();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Błąd");
            if (e.getErrorCode() == 20004) {
                alert.setHeaderText("Niewłaściwy telefon");
                alert.setContentText("Proszę poprawić telefon");
            } else {
                alert.setHeaderText("Błędny format");
                alert.setContentText("Proszę wprowadzić dane w odpowiednim formacie");
            }
            alert.showAndWait();
        }
    }

    @FXML
    private void AnulujButtonAction(ActionEvent event) {
        Stage stage = (Stage) MiejscowoscTekst.getScene().getWindow();
        stage.close();
    }

}
