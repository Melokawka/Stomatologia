package stomatologiaprojekt;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class EdytujWizyteController implements Initializable {

    @FXML
    private TextField peselPacjentaTekst;
    @FXML
    private ComboBox idStomatologaTekst;
    @FXML
    private DatePicker DataPicker;
    @FXML
    private ComboBox<String> godzinaCombo;

    private DatabaseHandler handler;

    private String sql;
    public static int wizytaSelectedId;

    public void wizytaPrzekazId(int wizytaSelectedId) {
        this.wizytaSelectedId = wizytaSelectedId;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        DataPicker.setEditable(true);

        try {
            handler = DatabaseHandler.getInstance();
            idStomatologaTekst.setItems(FXCollections.observableList(handler.listaStomatolog()));
            String sql = "SELECT wizyta.id_wizyty,wizyta.godzina,wizyta.data_, pacjent.id_pacjenta AS idPac,"
                    + " stomatolog.imie AS imStom, stomatolog.nazwisko AS nazStom"
                    + " FROM wizyta "
                    + " INNER JOIN pacjent ON wizyta.id_pacjenta=pacjent.id_pacjenta "
                    + " INNER JOIN stomatolog ON wizyta.id_stomatologa=stomatolog.id_stomatologa "
                    + " WHERE id_wizyty=?";

            ResultSet result = handler.zaznaczonaWizyta(sql, wizytaSelectedId);
            result.next();
            idStomatologaTekst.setValue(String.valueOf(result.getString("imStom")) + " " + String.valueOf(result.getString("nazStom")));
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate localDate = LocalDate.parse(String.valueOf(result.getDate("data_")), formatter);
            DataPicker.setValue(localDate);

            godzinaCombo.setItems(FXCollections.observableList(handler.listaDostepnychGodzin(DataPicker.getValue().toString(), idStomatologaTekst.getValue().toString(), wizytaSelectedId)));
            godzinaCombo.setValue(result.getString("godzina"));
            // znajdz pesel pacjenta klinika.id_pacjenta
            int przekazId = result.getInt("idPac");
            sql = "SELECT pesel FROM pacjent WHERE id_pacjenta = ?";
            result = handler.znajdzPeselWizyta(sql, przekazId);
            result.next();
            peselPacjentaTekst.setText(result.getString(1));

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
    private void EdytujButtonAction(ActionEvent event) throws ParseException, ClassNotFoundException, SQLException, InstantiationException, IllegalAccessException {

        if (godzinaCombo.getValue() == null || peselPacjentaTekst.getText().equals("")
                || idStomatologaTekst.getValue().equals("") || DataPicker.getValue() == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Uwaga");
            alert.setHeaderText("Nie wypełniono wymaganych pól");
            alert.setContentText("Proszę wypełnić wszystkie pola");
            alert.showAndWait();
        } else {
            try {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
                long msec = simpleDateFormat.parse(godzinaCombo.getValue()).getTime();
                Time godz = new Time(msec);
                handler = DatabaseHandler.getInstance();
                sql = "{call wizyta_pack.edytuj(?,?,?,?,?,?,?)}";
                handler.edytujWizyte(sql, godz, DataPicker.getValue().toString(), peselPacjentaTekst.getText(),
                        idStomatologaTekst.getValue().toString(), wizytaSelectedId);
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
            godzinaCombo.setItems(FXCollections.observableList(handler.listaDostepnychGodzin(DataPicker.getValue().toString(), idStomatologaTekst.getValue().toString(), wizytaSelectedId)));
            godzinaCombo.setDisable(false);
        }
    }

    @FXML
    private void StomatologChanged(ActionEvent event) throws SQLException {
        godzinaCombo.getItems().clear();
        if (DataPicker.getValue() != null) {
            godzinaCombo.setItems(FXCollections.observableList(handler.listaDostepnychGodzin(DataPicker.getValue().toString(), idStomatologaTekst.getValue().toString(), wizytaSelectedId)));
            godzinaCombo.setDisable(false);
        }

    }
}
