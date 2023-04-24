package stomatologiaprojekt;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

public class ZarzadzajWizyty_zabiegiController implements Initializable {

    @FXML
    private TableColumn<String, String> zabiegiColumn;
    @FXML
    private TableView<String> ZabiegiTable;

    private DatabaseHandler handler;
    private String sql;
    public static int wizytaSelectedId;
    @FXML
    private ComboBox<String> zabiegiCombo;

    public void wizytaPrzekazId(int wizytaSelectedId) {
        this.wizytaSelectedId = wizytaSelectedId;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            handler = DatabaseHandler.getInstance();
            zabiegiCombo.setItems(FXCollections.observableList(handler.listaZabieg()));

            zabiegiColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue()));

            odswiezTabeleRodzZabiegow();

        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    public void odswiezTabeleRodzZabiegow() {
        try {
            handler = DatabaseHandler.getInstance();
            ResultSet result = handler.wyswietZabiegiWizyty(wizytaSelectedId);
            ObservableList<String> listaRodzZabiegow = FXCollections.observableArrayList();
            while (result.next()) {
                listaRodzZabiegow.add(result.getString("rodzaj_zabiegu"));
            }

            ZabiegiTable.setItems(listaRodzZabiegow);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    private void DodajButtonAction(ActionEvent event) throws ClassNotFoundException, SQLException, InstantiationException, IllegalAccessException, ParseException {
        if (zabiegiCombo.getValue() == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Uwaga");
            alert.setHeaderText("Nie wybrano zabiegu");
            alert.setContentText("Proszę wybrać zabieg");
            alert.showAndWait();
        } else {
            try {
                handler = DatabaseHandler.getInstance();
                sql = "{call wizab_pack.dodaj(?,?,?)}";
                
                // jezeli istnieje defaultowe pole "do ustalenia" to je usun
                if (ZabiegiTable.getItems().contains("Do ustalenia")) {
                    handler.usunZabiegWizyty("{call wizab_pack.usun(?,?,?)}", wizytaSelectedId, "Do ustalenia");
                    odswiezTabeleRodzZabiegow();
                }
                
                handler.dodajWiZab(sql, wizytaSelectedId, zabiegiCombo.getValue());
                odswiezTabeleRodzZabiegow();
            } catch (SQLException e) {
                System.out.println(e.getLocalizedMessage());
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
    private void UsunButtonAction(ActionEvent event) throws SQLException {
        if (ZabiegiTable.getSelectionModel().getSelectedItem() != null) {
            String sql = "{call wizab_pack.usun(?,?,?)}";
            handler.usunZabiegWizyty(sql, wizytaSelectedId, ZabiegiTable.getSelectionModel().getSelectedItem());
            odswiezTabeleRodzZabiegow();

        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Uwaga");
            alert.setHeaderText("Nie zaznaczono zabiegu do usunięcia");
            alert.setContentText("Proszę wybrać zabieg");
            alert.showAndWait();
        }
    }

    @FXML
    private void AnulujButtonAction(ActionEvent event) {
        Stage stage = (Stage) ZabiegiTable.getScene().getWindow();
        stage.close();
    }

}
