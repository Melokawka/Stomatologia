package stomatologiaprojekt;

import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import java.net.URL;
import java.sql.Date;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class DodajPacjentaController implements Initializable {

    @FXML
    private TextField ImieTekst;
    @FXML
    private TextField nazwiskoTekst;
    @FXML
    private TextField peselTekst;
    @FXML
    private TextField miejscowoscTekst;
    @FXML
    private TextField adresTekst;
    @FXML
    private TextField telefonTekst;
    @FXML
    private DatePicker DataUrPicker;
    @FXML
    private RadioButton RadKobieta;
    @FXML
    private ToggleGroup PlecGroup;
    @FXML
    private RadioButton RadMezczyzna;
    @FXML
    private RadioButton RadTak;
    @FXML
    private ToggleGroup UbezpieczenieGroup;
    @FXML
    private RadioButton RadNie;

    private DatabaseHandler handler;
    private String sql;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        DataUrPicker.setEditable(true);
    }

    @FXML
    private void DodajButtonAction(ActionEvent event) throws ClassNotFoundException, SQLException, InstantiationException, IllegalAccessException {
        if (ImieTekst.getText().equals("") || nazwiskoTekst.getText().equals("")) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Uwaga");
            alert.setHeaderText("Nie wypełniono wymaganych pól");
            alert.setContentText("Proszę wypełnić wszystkie pola");
            alert.showAndWait();
        } else {
            try {
                String zaznPlec;
                if (PlecGroup.getSelectedToggle() == RadKobieta) zaznPlec = "K";
                 else zaznPlec = "M";
                
                Date data = null;
                if (DataUrPicker.getValue() != null) 
                    data = Date.valueOf(DataUrPicker.getValue());
                
                handler = DatabaseHandler.getInstance();
                sql = "{call pacjent_pack.dodaj(?,?,?,?,?,?,?,?,?)}";
                handler.dodajPacjenta(sql, ImieTekst.getText(), nazwiskoTekst.getText(), zaznPlec, peselTekst.getText(),
                        data, miejscowoscTekst.getText(), adresTekst.getText(), telefonTekst.getText(), UbezpieczenieGroup.getSelectedToggle() == RadTak ? "1" : "0");
                Stage stage = (Stage) ImieTekst.getScene().getWindow();
                stage.close();
            } catch (SQLException e) {
                System.out.println(e.getMessage());
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Błąd");
                if (e.getErrorCode() == 20014) {
                    alert.setHeaderText("Niewłaściwy telefon");
                    alert.setContentText("Proszę poprawić telefon");
                } else {
                    alert.setHeaderText("Błędny format");
                    alert.setContentText("Proszę wprowadzić dane w odpowiednim formacie");
                }
                alert.showAndWait();
            }
        }
    }

    @FXML
    private void AnulujButtonAction(ActionEvent event) {
        Stage stage = (Stage) ImieTekst.getScene().getWindow();
        stage.close();
    }

}
