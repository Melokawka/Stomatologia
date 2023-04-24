package stomatologiaprojekt;

import static java.lang.Integer.parseInt;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class EdytujRodzajZabieguController implements Initializable {

    private DatabaseHandler handler;
    private String sql;

    @FXML
    private TextField NazwaText;
    @FXML
    private TextField CenaText;
    @FXML
    private ComboBox RefundacjaText = new ComboBox();
    @FXML
    private TextField KrotnoscText;

    private int rZabieguSelectedId;

    public void rZabieguPrzekazId(int rZabieguSelectedId) {
        this.rZabieguSelectedId = rZabieguSelectedId;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        ObservableList<String> options
                = FXCollections.observableArrayList(
                        "tak",
                        "nie"
                );
        RefundacjaText.getItems().addAll(options);
    }

    @FXML
    private void EdytujButtonAction(ActionEvent event) throws ClassNotFoundException, SQLException, InstantiationException, IllegalAccessException {
        if (NazwaText.getText().equals("") || CenaText.getText().equals("")) {
            Alert alert = new Alert(AlertType.WARNING);
            alert.setTitle("Uwaga");
            alert.setHeaderText("Nie wypełniono wymaganych pól");
            alert.setContentText("Proszę wypełnić wszystkie pola");
            alert.showAndWait();
        } else if (Integer.parseInt(CenaText.getText()) < 0) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Błąd");
            alert.setHeaderText("Ujemna cena");
            alert.setContentText("Proszę poprawić cenę");
            alert.showAndWait();
        } else {
            try {
                handler = DatabaseHandler.getInstance();
                sql = "{call rodzzab_pack.edytuj(?,?,?,?,?)}";
                handler.edytujRodzajZabiegu(sql, NazwaText.getText(), parseInt(CenaText.getText()), (RefundacjaText.getSelectionModel().getSelectedIndex() == 0 ? "1" : "0"), parseInt(KrotnoscText.getText()), rZabieguSelectedId);
                Stage stage = (Stage) NazwaText.getScene().getWindow();
                stage.close();
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
        Stage stage = (Stage) NazwaText.getScene().getWindow();
        stage.close();
    }

}
