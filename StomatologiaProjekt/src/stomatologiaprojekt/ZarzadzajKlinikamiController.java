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

public class ZarzadzajKlinikamiController implements Initializable {

    private DatabaseHandler handler;

    @FXML
    private TableView<Klinika> tabelaKlinik;
    @FXML
    private TableColumn<Klinika, Integer> idKliniki;
    @FXML
    private TableColumn<Klinika, String> Miejscowosc;
    @FXML
    private TableColumn<Klinika, String> Adres;
    @FXML
    private TableColumn<Klinika, String> kodPocztowy;
    @FXML
    private TableColumn<Klinika, String> Telefon;

    public int klinikaSelectedId;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            idKliniki.setCellValueFactory(new PropertyValueFactory<Klinika, Integer>("id_kliniki"));
            Miejscowosc.setCellValueFactory(new PropertyValueFactory<Klinika, String>("miejscowosc"));
            kodPocztowy.setCellValueFactory(new PropertyValueFactory<Klinika, String>("kod_pocztowy"));
            Adres.setCellValueFactory(new PropertyValueFactory<Klinika, String>("adres"));
            Telefon.setCellValueFactory(new PropertyValueFactory<Klinika, String>("telefon"));

            odswiezTabeleKlinik();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void odswiezTabeleKlinik() {
        try {
            handler = DatabaseHandler.getInstance();
            ResultSet result = handler.wyswietlKlinike();
            ObservableList<Klinika> listaKlinik = FXCollections.observableArrayList();
            while (result.next()) {
                listaKlinik.add(new Klinika(result.getInt("id_kliniki"), result.getString("miejscowosc"), result.getString("adres"),
                        result.getString("kod_pocztowy"), result.getString("telefon")));
            }

            tabelaKlinik.setItems(listaKlinik);
            tabelaKlinik.getSortOrder().add(idKliniki);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    private void DodanieKliniki(ActionEvent event) throws IOException, SQLException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("DodajKlinike.fxml"));
        Parent root1 = (Parent) fxmlLoader.load();
        Stage stage = new Stage();
        stage.setScene(new Scene(root1));
        stage.setTitle("Dodaj klinikę");
        stage.show();

        stage.setOnHiding(ev -> {
            odswiezTabeleKlinik();
        });
    }

    @FXML
    private void UsuwanieKliniki(ActionEvent event) throws SQLException {
        if (tabelaKlinik.getSelectionModel().getSelectedItem() != null) {
            try {
                String sql = "{call klinika_pack.usun(?)}";
                handler.usunKlinike(sql, tabelaKlinik.getSelectionModel().getSelectedItem().getId_kliniki());
                odswiezTabeleKlinik();
            } catch (SQLException e) {
                System.out.println(e.getMessage());
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Uwaga");
                alert.setHeaderText("Wybrana klinika jest częścią innej tabeli");
                alert.setContentText("Proszę najpierw usunąć wszystkie wizyty dla tej kliniki");
                alert.showAndWait();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Uwaga");
            alert.setHeaderText("Nie zaznaczono kliniki do usunięcia");
            alert.setContentText("Proszę wybrać klinikę");
            alert.showAndWait();
        }
    }

    @FXML
    private void EdytowanieKliniki(ActionEvent event) throws IOException {
        if (tabelaKlinik.getSelectionModel().getSelectedItem() != null) {
            klinikaSelectedId = tabelaKlinik.getSelectionModel().getSelectedItem().getId_kliniki();
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("EdytujKlinike.fxml"));
            Parent root1 = (Parent) fxmlLoader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root1));
            stage.setTitle("Edytuj klinikę");

            EdytujKlinikeController controller = fxmlLoader.<EdytujKlinikeController>getController();
            controller.klinikaPrzekazId(klinikaSelectedId);

            stage.show();

            stage.setOnHiding(ev -> {
                odswiezTabeleKlinik();
            });

        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Uwaga");
            alert.setHeaderText("Nie zaznaczono kliniki do edycji");
            alert.setContentText("Proszę wybrać klinikę");
            alert.showAndWait();
        }
    }

}
