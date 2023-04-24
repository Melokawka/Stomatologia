package stomatologiaprojekt;

import java.net.URL;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import static java.time.LocalDate.now;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class EdytujStomatologaController implements Initializable {

    @FXML
    private TextField ImieTekst;
    @FXML
    private TextField NazwiskoTekst;
    @FXML
    private TextField RZatrudnieniaTekst;
    @FXML
    private TextField TelefonTekst;
    @FXML
    private DatePicker DataUrPicker;
    @FXML
    private ComboBox KlinikaCombo;
    @FXML
    private ComboBox GodzinyCombo;

    private DatabaseHandler handler;

    private String sql;
    public int stomatologSelectedId;
    private int year = now().getYear();

    //Pobranie ID zaznaczonego rekordu tabeli głównej 
    public void stomatologPrzekazId(int stomatologSelectedId) {
        this.stomatologSelectedId = stomatologSelectedId;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        DataUrPicker.setEditable(true);
        ObservableList<String> options
                = FXCollections.observableArrayList(
                        "Poranne",
                        "Wieczorne"
                );
        GodzinyCombo.getItems().addAll(options);

        try {
            handler = DatabaseHandler.getInstance();
            ResultSet result = handler.wyswietlKlinike();
            while (result.next()) {
                KlinikaCombo.getItems().add(result.getString("adres"));
            }
        } catch (Exception e) {
            System.out.println("Bład wyświetlenia kliniki");
        }
    }

    //Wciśnięcie przycisku "Edytuj"
    @FXML
    private void EdytujButtonAction(ActionEvent event) throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        //Sprawdzenie, czy wszystkie pola są wypełnione
        if (ImieTekst.getText().equals("") || NazwiskoTekst.getText().equals("") || RZatrudnieniaTekst.getText().equals("") || TelefonTekst.getText().equals("")
                || DataUrPicker.getValue() == null) {
            //Jeśli nie są, pokazuje się ostrzeżenie
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Uwaga");
            alert.setHeaderText("Nie wypełniono wymaganych pól");
            alert.setContentText("Proszę wypełnić wszystkie pola");
            alert.showAndWait();
        } //Sprawdzenie, czy pola wypełnione są odpowiednim typem danych
        else if ((year - DataUrPicker.getValue().getYear()) < 18) {
            System.out.println(year - DataUrPicker.getValue().getYear());
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Błąd");
            alert.setHeaderText("Niewłaściwy wiek");
            alert.setContentText("Proszę poprawić datę urodzenia");
            alert.showAndWait();
        } else if (Integer.parseInt(RZatrudnieniaTekst.getText()) < 1950 || Integer.parseInt(RZatrudnieniaTekst.getText()) > now().getYear()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Błąd");
            alert.setHeaderText("Niewłaściwy rok zatrudnienia");
            alert.setContentText("Proszę poprawić rok zatrudnienia");
            alert.showAndWait();
        } else {
            try {
                handler = DatabaseHandler.getInstance();
                sql = "{call stomatolog_pack.edytuj(?,?,?,?,?,?,?,?)}";
                //Zmiana danych wybranego rekordu na wprowadzone
                handler.edytujStomatologa(sql, ImieTekst.getText(), NazwiskoTekst.getText(), RZatrudnieniaTekst.getText(),
                        Date.valueOf(DataUrPicker.getValue()), TelefonTekst.getText(),
                        KlinikaCombo.getSelectionModel().getSelectedItem().toString(),
                        GodzinyCombo.getSelectionModel().getSelectedIndex() == 0 ? "R" : "W", stomatologSelectedId);
                Stage stage = (Stage) ImieTekst.getScene().getWindow();
                stage.close();
            } catch (SQLException e) { //Obsługa innego błędu wprowadzania danych (np. przekroczenie ilości znaków dla VARCHAR)
                System.out.println(e.getMessage());
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Błąd");
                if (e.getErrorCode() == 20026) {
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

    //Anulowanie edytowania rekordu
    @FXML
    private void AnulujButtonAction(ActionEvent event) {
        Stage stage = (Stage) ImieTekst.getScene().getWindow();
        stage.close();
    }

}
