package stomatologiaprojekt;

import java.net.URL;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;

public class EdytujPacjentaController implements Initializable {

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

    private String sql;
    public static int pacjentSelectedId;
    private DatabaseHandler handler;

    public void pacjentPrzekazId(int pacjentSelectedId) {
        this.pacjentSelectedId = pacjentSelectedId;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        DataUrPicker.setEditable(true);

        try {
            String sql = "SELECT * FROM pacjent WHERE id_pacjenta=?";
            handler = DatabaseHandler.getInstance();
            ResultSet result = handler.zaznaczonyPacjent(sql, pacjentSelectedId);
            result.next();

            ImieTekst.setText(String.valueOf(result.getString("imie")));
            nazwiskoTekst.setText(String.valueOf(result.getString("nazwisko")));
            peselTekst.setText(String.valueOf(result.getString("pesel")));
            miejscowoscTekst.setText(String.valueOf(result.getString("miejscowosc")));
            adresTekst.setText(String.valueOf(result.getString("adres")));
            telefonTekst.setText(String.valueOf(result.getString("telefon")));

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate localDate;
            if (result.getDate("data_urodzenia") == null) {
                localDate = null;
            } else {
                localDate = LocalDate.parse(String.valueOf(result.getDate("data_urodzenia")), formatter);
            }
            DataUrPicker.setValue(localDate);
            if (result.getString("plec").equals("K")) {
                RadKobieta.setSelected(true);
            } else if (result.getString("plec").equals("M")) {
                RadKobieta.setSelected(false);
                RadMezczyzna.setSelected(true);
            }
            if (result.getString("ubezpieczenie").equals("1")) {
                RadTak.setSelected(true);
                RadNie.setSelected(false);
            } else if (result.getString("ubezpieczenie").equals("0")) {
                RadTak.setSelected(false);
                RadNie.setSelected(true);
            }

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
    private void EdytujButtonAction(ActionEvent event) throws ClassNotFoundException, SQLException, InstantiationException, IllegalAccessException {
        if (ImieTekst.getText().equals("") || nazwiskoTekst.getText().equals("")) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Uwaga");
            alert.setHeaderText("Nie wypełniono wymaganych pól");
            alert.setContentText("Proszę wypełnić wymagane pola (imię, nazwisko, płeć)");
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
                sql = "{call pacjent_pack.edytuj(?,?,?,?,?,?,?,?,?,?)}";
                handler.edytujPacjenta(sql, ImieTekst.getText(), nazwiskoTekst.getText(), zaznPlec, peselTekst.getText(),
                        data, miejscowoscTekst.getText(), adresTekst.getText(), telefonTekst.getText(), UbezpieczenieGroup.getSelectedToggle() == RadTak ? "1" : "0", pacjentSelectedId);
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
