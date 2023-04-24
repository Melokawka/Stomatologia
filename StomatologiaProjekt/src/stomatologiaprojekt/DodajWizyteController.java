package stomatologiaprojekt;

import java.net.URL;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.sql.Time;
import javafx.collections.FXCollections;

public class DodajWizyteController implements Initializable {

    @FXML
    private TextField peselPacjentaTekst;
    @FXML
    private ComboBox idStomatologaTekst;
    @FXML
    private DatePicker DataPicker;
    private DatabaseHandler handler;
    private String sql;
    @FXML
    private ComboBox<String> godzinaCombo;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        DataPicker.setEditable(true);
        
        try {
            handler = DatabaseHandler.getInstance();

            idStomatologaTekst.setItems(FXCollections.observableList(handler.listaStomatolog()));

            godzinaCombo.setDisable(true);

        } catch (SQLException ex) {
            ex.printStackTrace();
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        } catch (InstantiationException ex) {
            ex.printStackTrace();
        } catch (IllegalAccessException ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    private void DodajButtonAction(ActionEvent event) throws ClassNotFoundException, SQLException, InstantiationException, IllegalAccessException, ParseException {
        if (godzinaCombo.getValue() == null || peselPacjentaTekst.getText().equals("")
                || idStomatologaTekst.getValue().equals("") || DataPicker.getValue() == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Uwaga");
            alert.setHeaderText("Nie wypełniono wymaganych pól");
            alert.setContentText("Proszę wypełnić wszystkie pola");
            alert.showAndWait();
        } else {
            try {
                //Konwertowanie wybranej godziny do typu sql.Time
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss"); //Ustawienie formatu godziny:minuty
                long msec = simpleDateFormat.parse(godzinaCombo.getValue()).getTime();
                Time godz = new Time(msec);
                handler = DatabaseHandler.getInstance();
                sql = "{call wizyta_pack.dodaj(?,?,?,?,?,?)}";
                handler.dodajWizyte(sql, godz, DataPicker.getValue().toString(), peselPacjentaTekst.getText(),
                        idStomatologaTekst.getValue().toString());
                Stage stage = (Stage) DataPicker.getScene().getWindow();
                stage.close();
            } catch (IllegalArgumentException e2) {
                System.out.println("IllegalArgumentException, nieistniejacy pesel");
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Błąd");
                alert.setHeaderText("Błędny pesel");
                alert.setContentText("Podany pesel nie istnieje");
                alert.showAndWait();
            } catch (SQLException e) {
                System.out.println(e.getMessage());
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Błąd");
                alert.setHeaderText("Błędny format");
                alert.setContentText("Proszę wprowadzić dane w odpowiednim formacie");
                alert.showAndWait();
            }
        }
    }

    @FXML
    private void AnulujButtonAction(ActionEvent event) {
        Stage stage = (Stage) DataPicker.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void DataChanged(ActionEvent event) throws SQLException {
        godzinaCombo.getItems().clear();
        if (idStomatologaTekst.getValue() != null) {
            godzinaCombo.setItems(FXCollections.observableList(handler.listaDostepnychGodzin(DataPicker.getValue().toString(), idStomatologaTekst.getValue().toString(), -1)));
            godzinaCombo.setDisable(false);
        }
    }

    @FXML
    private void StomatologChanged(ActionEvent event) throws SQLException {
        godzinaCombo.getItems().clear();
        if (DataPicker.getValue() != null) {
            godzinaCombo.setItems(FXCollections.observableList(handler.listaDostepnychGodzin(DataPicker.getValue().toString(), idStomatologaTekst.getValue().toString(), -1)));
            godzinaCombo.setDisable(false);
        }

    }

}
