package stomatologiaprojekt;

import java.io.IOException;
import java.net.URL;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

public class ZarzadzajWizytamiController implements Initializable {

    @FXML
    private TextField wizytaFind;
    @FXML
    private TableView<WizytaModelDanych> tabelaWizyt;
    @FXML
    private TableColumn<WizytaModelDanych, Integer> idWizyty;
    @FXML
    private TableColumn<WizytaModelDanych, Time> Godzina;
    @FXML
    private TableColumn<WizytaModelDanych, Date> DataUr;
    @FXML
    private TableColumn<WizytaModelDanych, String> Imie;
    @FXML
    private TableColumn<WizytaModelDanych, String> Nazwisko;
    @FXML
    private TableColumn<WizytaModelDanych, String> NazwiskoStom;
    @FXML
    private TableColumn<WizytaModelDanych, String> Adres;
    @FXML
    private TableColumn<WizytaModelDanych, Integer> Koszt;
    @FXML
    private TableColumn<WizytaModelDanych, String> Archiwalna;

    private DatabaseHandler handler;
    private ObservableList<WizytaModelDanych> listaWizyt = FXCollections.observableArrayList();
    public int wizytaSelectedId;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            idWizyty.setCellValueFactory(new PropertyValueFactory<>("id_wizyty"));
            Godzina.setCellValueFactory(new PropertyValueFactory<>("godzina"));
            DataUr.setCellValueFactory(new PropertyValueFactory<>("data_"));
            Imie.setCellValueFactory(new PropertyValueFactory<>("imie"));
            Nazwisko.setCellValueFactory(new PropertyValueFactory<>("nazwisko"));
            NazwiskoStom.setCellValueFactory(new PropertyValueFactory<>("nazwiskoStom"));
            Adres.setCellValueFactory(new PropertyValueFactory<>("adres"));
            Koszt.setCellValueFactory(new PropertyValueFactory<>("koszt"));
            Archiwalna.setCellValueFactory(new PropertyValueFactory<>("archiwalna"));

            odswiezTabeleWizyt();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    //Funkcjonalność przeszukiwania tabeli dla dowolnych danych z dowolnej kolumny
    @FXML
    private void ZnajdzWizyte(KeyEvent event) {
        FilteredList<WizytaModelDanych> sortListaWizyt = new FilteredList<>(listaWizyt, p -> true); //Lista wizyt z możliwością filtrowania

        wizytaFind.textProperty().addListener((observable, oldValue, newValue) -> { //Przeszukiwanie w czasie rzeczywistym (podczas wprowadzania)
            sortListaWizyt.setPredicate(WizytaModelDanych -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String lowerCaseFilter = newValue.toLowerCase();

                if (String.valueOf(WizytaModelDanych.getId_wizyty()).toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                } else if (String.valueOf(WizytaModelDanych.getGodzina()).toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                } else if (String.valueOf(WizytaModelDanych.getData_()).toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                } else if (String.valueOf(WizytaModelDanych.getImie()).toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                } else if (String.valueOf(WizytaModelDanych.getNazwisko()).toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                } else if (String.valueOf(WizytaModelDanych.getNazwiskoStom()).toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                } else if (String.valueOf(WizytaModelDanych.getAdres()).toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                } else if (String.valueOf(WizytaModelDanych.getKoszt()).toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                } else if (String.valueOf(WizytaModelDanych.getArchiwalna()).toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }
                return false;
            });
        });

        SortedList<WizytaModelDanych> sortedData = new SortedList<>(sortListaWizyt);

        sortedData.comparatorProperty().bind(tabelaWizyt.comparatorProperty());

        tabelaWizyt.setItems(sortedData); //Pokazywanie w tabeli wyszukanych rekordów

    }

    public void odswiezTabeleWizyt() {
        try {
            listaWizyt.clear();
            handler = DatabaseHandler.getInstance();
            ResultSet result = handler.wyswietlWizyte();
            while (result.next()) {
                WizytaModelDanych wizyta = new WizytaModelDanych(result.getInt("id_wizyty"), result.getTime("godzina"), result.getDate("data_"),
                        result.getString("imie"), result.getString("nazwiskoPac"),
                        result.getString("nazwiskoStom"), result.getString("adres"),
                        result.getInt("koszt"), (result.getString("archiwalna").equals("1") ? "tak" : "nie"));
                listaWizyt.add(wizyta);
            }

            tabelaWizyt.setItems(listaWizyt);
            tabelaWizyt.getSortOrder().add(idWizyty);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    private void DodanieWizyty(ActionEvent event) throws SQLException, IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("DodajWizyte.fxml"));
        Parent root1 = (Parent) fxmlLoader.load();
        Stage stage = new Stage();
        stage.setScene(new Scene(root1));
        stage.setTitle("Dodaj wizytę");
        stage.show();

        stage.setOnHiding(ev -> {
            odswiezTabeleWizyt();
        });
    }

    @FXML
    private void UsuwanieWizyty(ActionEvent event) throws SQLException {
        if (tabelaWizyt.getSelectionModel().getSelectedItem() != null) {
            try {
                String sql = "{call wizyta_pack.usun(?)}";
                handler.usunWizyte(sql, tabelaWizyt.getSelectionModel().getSelectedItem().getId_wizyty());
                odswiezTabeleWizyt();
            } catch (SQLException e) {
                System.out.println(e.getMessage());
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Uwaga");
                alert.setHeaderText("Wybrana wizyta ma przypisane do siebie zabiegi.");
                alert.setContentText("Proszę najpierw usunąć wszystkie zabiegi dla tej wizyty.");
                alert.showAndWait();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Uwaga");
            alert.setHeaderText("Nie zaznaczono wizyty do usunięcia");
            alert.setContentText("Proszę wybrać wizytę");
            alert.showAndWait();
        }
    }

    @FXML
    private void EdytowanieWizyty(ActionEvent event) throws IOException {
        if (tabelaWizyt.getSelectionModel().getSelectedItem() != null) {
            wizytaSelectedId = tabelaWizyt.getSelectionModel().getSelectedItem().getId_wizyty();
            EdytujWizyteController.wizytaSelectedId = wizytaSelectedId;

            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("EdytujWizyte.fxml"));

            Parent root1 = (Parent) fxmlLoader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root1));
            stage.setTitle("Edytuj wizytę");
            EdytujWizyteController controller = fxmlLoader.<EdytujWizyteController>getController();
            controller.wizytaPrzekazId(wizytaSelectedId);

            stage.show();

            stage.setOnHiding(ev -> {
                odswiezTabeleWizyt();
            });
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Uwaga");
            alert.setHeaderText("Nie zaznaczono wizyty do edycji");
            alert.setContentText("Proszę wybrać wizytę");
            alert.showAndWait();
        }
    }

    @FXML
    private void ArchiwizacjaWizyty(ActionEvent event) throws SQLException {
        if (tabelaWizyt.getSelectionModel().getSelectedItem() != null) {
            ButtonType ok = new ButtonType("Archiwizuj", ButtonBar.ButtonData.YES);
            ButtonType cancel = new ButtonType("Anuluj", ButtonBar.ButtonData.CANCEL_CLOSE);
            Alert alert = new Alert(AlertType.CONFIRMATION, "Czy na pewno chcesz zarchiwizować tę wizytę?", ok, cancel);
            alert.setTitle("Uwaga");
            alert.setHeaderText("Archiwizacja wizyty");
            alert.showAndWait();
            if (alert.getResult() == ok) {
                String sql = "{call util_pack.ArchiwizujJedna(?)}";
                handler.archiwizujJedna(sql, tabelaWizyt.getSelectionModel().getSelectedItem().getId_wizyty());
                odswiezTabeleWizyt();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Uwaga");
            alert.setHeaderText("Nie zaznaczono wizyty do archiwizacji");
            alert.setContentText("Proszę wybrać wizytę");
            alert.showAndWait();
        }
    }

    @FXML
    private void ZarzadzajZabiegami(ActionEvent event) throws IOException {
        if (tabelaWizyt.getSelectionModel().getSelectedItem() != null) {
            wizytaSelectedId = tabelaWizyt.getSelectionModel().getSelectedItem().getId_wizyty();
            ZarzadzajWizyty_zabiegiController.wizytaSelectedId = wizytaSelectedId;

            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("ZarzadzajWizyty_zabiegi.fxml"));

            Parent root1 = (Parent) fxmlLoader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root1));
            stage.setTitle("Edytuj wizytę");
            ZarzadzajWizyty_zabiegiController controller = fxmlLoader.<ZarzadzajWizyty_zabiegiController>getController();
            controller.wizytaPrzekazId(wizytaSelectedId);

            stage.show();

            stage.setOnHiding(ev -> {
                odswiezTabeleWizyt();
            });
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Uwaga");
            alert.setHeaderText("Nie zaznaczono wizyty do zarządzania zabiegami");
            alert.setContentText("Proszę wybrać wizytę");
            alert.showAndWait();
        }
    }

}
