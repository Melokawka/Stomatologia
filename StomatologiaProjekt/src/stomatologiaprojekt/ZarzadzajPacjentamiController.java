package stomatologiaprojekt;

import java.io.IOException;
import java.net.URL;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TextField;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

public class ZarzadzajPacjentamiController implements Initializable {

    private DatabaseHandler handler;

    @FXML
    private TableView<Pacjent> tabelaPacjentow;
    @FXML
    private TextField pacjentFind;

    @FXML
    private TableColumn<Pacjent, Integer> idPacjenta;
    @FXML
    private TableColumn<Pacjent, String> Imie;
    @FXML
    private TableColumn<Pacjent, String> Nazwisko;
    @FXML
    private TableColumn<Pacjent, String> Plec;
    @FXML
    private TableColumn<Pacjent, String> Pesel;
    @FXML
    private TableColumn<Pacjent, Date> DataUr;
    @FXML
    private TableColumn<Pacjent, String> Miejscowosc;
    @FXML
    private TableColumn<Pacjent, String> Adres;
    @FXML
    private TableColumn<Pacjent, String> Telefon;
    @FXML
    private TableColumn<Pacjent, String> Ubezpieczenie;

    public int pacjentSelectedId;
    private ObservableList<Pacjent> listaPacjentow = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            idPacjenta.setCellValueFactory(new PropertyValueFactory<Pacjent, Integer>("id_pacjenta"));
            Imie.setCellValueFactory(new PropertyValueFactory<Pacjent, String>("imie"));
            Nazwisko.setCellValueFactory(new PropertyValueFactory<Pacjent, String>("nazwisko"));
            Plec.setCellValueFactory(new PropertyValueFactory<Pacjent, String>("plec"));
            Pesel.setCellValueFactory(new PropertyValueFactory<Pacjent, String>("pesel"));
            DataUr.setCellValueFactory(new PropertyValueFactory<Pacjent, Date>("data_urodzenia"));
            Miejscowosc.setCellValueFactory(new PropertyValueFactory<Pacjent, String>("miejscowosc"));
            Adres.setCellValueFactory(new PropertyValueFactory<Pacjent, String>("adres"));
            Telefon.setCellValueFactory(new PropertyValueFactory<Pacjent, String>("telefon"));
            Ubezpieczenie.setCellValueFactory(new PropertyValueFactory<Pacjent, String>("ubezpieczenie"));

            odswiezTabelePacjentow();

        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    public void odswiezTabelePacjentow() {
        try {
            listaPacjentow.clear();
            handler = DatabaseHandler.getInstance();
            ResultSet result = handler.wyswietlPacjenta();

            while (result.next()) {
                listaPacjentow.add(new Pacjent(result.getInt("id_pacjenta"), result.getString("imie"), result.getString("nazwisko"),
                        result.getString("plec"), result.getString("pesel"), result.getDate("data_urodzenia"), result.getString("miejscowosc"),
                        result.getString("adres"), result.getString("telefon"), (result.getString("ubezpieczenie").equals("1") ? "tak" : "nie")));
            }

            tabelaPacjentow.setItems(listaPacjentow);
            tabelaPacjentow.getSortOrder().add(idPacjenta);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    private void ZnajdzPacjenta(KeyEvent event) {
        FilteredList<Pacjent> sortListaPacjentow = new FilteredList<>(listaPacjentow, p -> true);

        pacjentFind.textProperty().addListener((observable, oldValue, newValue) -> {
            sortListaPacjentow.setPredicate(Pacjent -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String lowerCaseFilter = newValue.toLowerCase();

                if (Pacjent.getImie().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                } else if (Pacjent.getNazwisko().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                } else if (String.valueOf(Pacjent.getMiejscowosc()).toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                } else if (String.valueOf(Pacjent.getPesel()).toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                } else if (String.valueOf(Pacjent.getAdres()).toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                } else if (Pacjent.getTelefon().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                } else if (Pacjent.getUbezpieczenie().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }
                return false;
            });
        });

        SortedList<Pacjent> sortedData = new SortedList<>(sortListaPacjentow);

        sortedData.comparatorProperty().bind(tabelaPacjentow.comparatorProperty());

        tabelaPacjentow.setItems(sortedData);

    }

    @FXML
    private void DodaniePacjenta(ActionEvent event) throws SQLException, IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("DodajPacjenta.fxml"));
        Parent root1 = (Parent) fxmlLoader.load();
        Stage stage = new Stage();
        stage.setScene(new Scene(root1));
        stage.setTitle("Dodaj pacjenta");
        stage.show();

        stage.setOnHiding(ev -> {
            odswiezTabelePacjentow();
        });
    }

    @FXML
    private void UsuwaniePacjenta(ActionEvent event) throws SQLException {
        if (tabelaPacjentow.getSelectionModel().getSelectedItem() != null) {
            try {
                String sql = "{call pacjent_pack.usun(?)}";
                handler.usunPacjenta(sql, tabelaPacjentow.getSelectionModel().getSelectedItem().getId_pacjenta());
                odswiezTabelePacjentow();
            }   catch(SQLException e) {
                System.out.println(e.getMessage());
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Uwaga");
                alert.setHeaderText("Wybrany pacjent jest częścią innej tabeli");
                alert.setContentText("Proszę najpierw usunąć wszystkie wizyty dla tego pacjenta");
                alert.showAndWait();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Uwaga");
            alert.setHeaderText("Nie zaznaczono pacjenta do usunięcia");
            alert.setContentText("Proszę wybrać pacjenta");
            alert.showAndWait();
        }
    }

    @FXML
    private void EdytowaniePacjenta(ActionEvent event) throws IOException {
        if (tabelaPacjentow.getSelectionModel().getSelectedItem() != null) {
            pacjentSelectedId = tabelaPacjentow.getSelectionModel().getSelectedItem().getId_pacjenta();
            EdytujPacjentaController.pacjentSelectedId = pacjentSelectedId;

            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("EdytujPacjenta.fxml"));
            Parent root1 = (Parent) fxmlLoader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root1));
            stage.setTitle("Edytuj Pacjenta");

            EdytujPacjentaController controller = fxmlLoader.<EdytujPacjentaController>getController();
            controller.pacjentPrzekazId(pacjentSelectedId);

            stage.show();

            stage.setOnHiding(ev -> {
                odswiezTabelePacjentow();
            });
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Uwaga");
            alert.setHeaderText("Nie zaznaczono pacjenta do edycji");
            alert.setContentText("Proszę zaznaczyć pacjenta");
            alert.showAndWait();
        }
    }

}
