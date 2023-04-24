package stomatologiaprojekt;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

public class ZarzadzajRodzajamiController implements Initializable {

    private DatabaseHandler handler;

    @FXML
    private TableView<Rodzaj_zabiegu> tabelaRodzZabiegow;
    @FXML
    private TableColumn<Rodzaj_zabiegu, Integer> idRodzajuZabiegu;
    @FXML
    private TableColumn<Rodzaj_zabiegu, String> rodzajZabiegu;
    @FXML
    private TableColumn<Rodzaj_zabiegu, Integer> cenaZabiegu;
    @FXML
    private TableColumn<Rodzaj_zabiegu, Integer> refundacjaDzieci;
    @FXML
    private TableColumn<Rodzaj_zabiegu, Integer> krotnoscRefundacji;

    public int rZabieguSelectedId;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            idRodzajuZabiegu.setCellValueFactory(new PropertyValueFactory<Rodzaj_zabiegu, Integer>("id_rodzaju_zabiegu"));
            rodzajZabiegu.setCellValueFactory(new PropertyValueFactory<Rodzaj_zabiegu, String>("rodzaj_zabiegu"));
            cenaZabiegu.setCellValueFactory(new PropertyValueFactory<Rodzaj_zabiegu, Integer>("cena"));
            refundacjaDzieci.setCellValueFactory(new PropertyValueFactory<Rodzaj_zabiegu, Integer>("refundacjaDzieci"));
            krotnoscRefundacji.setCellValueFactory(new PropertyValueFactory<Rodzaj_zabiegu, Integer>("krotnoscRefundacji"));

            odswiezTabeleRodzZabiegow();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void odswiezTabeleRodzZabiegow() {
        try {
            handler = DatabaseHandler.getInstance();
            ResultSet result = handler.wyswietlRodzajZabiegu();
            ObservableList<Rodzaj_zabiegu> listaRodzZabiegow = FXCollections.observableArrayList();
            while (result.next()) {
                listaRodzZabiegow.add(new Rodzaj_zabiegu(result.getInt("id_rodzaju_zabiegu"),
                        result.getString("rodzaj_zabiegu"), result.getInt("cena"),
                        (result.getString("refundacja_dzieci").equals("1") ? "tak" : "nie"), result.getInt("refundacja_dorosli_rok")));
            }

            tabelaRodzZabiegow.setItems(listaRodzZabiegow);
            tabelaRodzZabiegow.getSortOrder().add(idRodzajuZabiegu);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    public void DodanieZabiegu(ActionEvent event) throws IOException, SQLException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("DodajRodzajZabiegu.fxml"));
        Parent root1 = (Parent) fxmlLoader.load();
        Stage stage = new Stage();
        stage.setScene(new Scene(root1));
        stage.setTitle("Dodaj zabieg");
        stage.show();

        stage.setOnHiding(ev -> {
            odswiezTabeleRodzZabiegow();
        });
    }

    @FXML
    private void UsuwanieZabiegu(ActionEvent event) throws SQLException {
        if (tabelaRodzZabiegow.getSelectionModel().getSelectedItem() != null) {
            try {
                String sql = "{call rodzzab_pack.usun(?)}";
                handler.usunRodzajZabiegu(sql, tabelaRodzZabiegow.getSelectionModel().getSelectedItem().getId_rodzaju_zabiegu());
                odswiezTabeleRodzZabiegow();
            }   catch(SQLException e) {
                System.out.println(e.getMessage());
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Uwaga");
                alert.setHeaderText("Wybrany zabieg jest częścią innej tabeli");
                alert.setContentText("Proszę najpierw usunąć wszystkie wizyty dla tego zabiegu");
                alert.showAndWait();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Uwaga");
            alert.setHeaderText("Nie zaznaczono zabiegu do usunięcia");
            alert.setContentText("Proszę wybrać zabieg");
            alert.showAndWait();
        }
    }

    @FXML
    private void EdytowanieZabiegu(ActionEvent event) throws IOException {
        if (tabelaRodzZabiegow.getSelectionModel().getSelectedItem() != null) {
            rZabieguSelectedId = tabelaRodzZabiegow.getSelectionModel().getSelectedItem().getId_rodzaju_zabiegu();
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("EdytujRodzajZabiegu.fxml"));
            Parent root1 = (Parent) fxmlLoader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root1));
            stage.setTitle("Edytuj zabieg");

            EdytujRodzajZabieguController controller = fxmlLoader.<EdytujRodzajZabieguController>getController();
            controller.rZabieguPrzekazId(rZabieguSelectedId);

            stage.show();

            stage.setOnHiding(ev -> {
                odswiezTabeleRodzZabiegow();
            });

        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Uwaga");
            alert.setHeaderText("Nie zaznaczono zabiegu do edycji");
            alert.setContentText("Proszę wybrać zabieg");
            alert.showAndWait();
        }
    }

}
