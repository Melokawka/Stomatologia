package stomatologiaprojekt;

import java.io.IOException;
import java.net.URL;
import java.sql.Date;
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

//Okno do zarządzania tabelą "stomatolog"
public class ZarzadzajStomatologamiController implements Initializable {

    private DatabaseHandler handler;

    @FXML
    private TableView<Stomatolog> tabelaStomatologow;
    @FXML
    private TableColumn<Stomatolog, Integer> idStomatologa;
    @FXML
    private TableColumn<Stomatolog, String> Imie;
    @FXML
    private TableColumn<Stomatolog, String> Nazwisko;
    @FXML
    private TableColumn<Stomatolog, String> rokZatrudnienia;
    @FXML
    private TableColumn<Stomatolog, Date> dataUrodzenia;
    @FXML
    private TableColumn<Stomatolog, String> telefon;
    @FXML
    private TableColumn<Stomatolog, String> klinika;
    @FXML
    private TableColumn<Stomatolog, String> godziny_przyjec;

    public int stomatologSelectedId;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //Ustawianie typów danych dla kolumn tabeli TableView
        try {
            idStomatologa.setCellValueFactory(new PropertyValueFactory<Stomatolog, Integer>("id_stomatologa"));
            Imie.setCellValueFactory(new PropertyValueFactory<Stomatolog, String>("imie"));
            Nazwisko.setCellValueFactory(new PropertyValueFactory<Stomatolog, String>("nazwisko"));
            rokZatrudnienia.setCellValueFactory(new PropertyValueFactory<Stomatolog, String>("rok_zatrudnienia"));
            dataUrodzenia.setCellValueFactory(new PropertyValueFactory<Stomatolog, Date>("data_urodzenia"));
            telefon.setCellValueFactory(new PropertyValueFactory<Stomatolog, String>("telefon"));
            klinika.setCellValueFactory(new PropertyValueFactory<Stomatolog, String>("klinika"));
            godziny_przyjec.setCellValueFactory(new PropertyValueFactory<Stomatolog, String>("godziny_przyjec"));

            odswiezTabeleStomatologow();

        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    //Metoda odświeżająca dane w tabeli TableView
    public void odswiezTabeleStomatologow() {
        try {
            handler = DatabaseHandler.getInstance();  //Uzyskiwanie instancji handlera bazy
            ResultSet result = handler.wyswietlStomatologa(); //Pozyskiwanie danych z tabeli "stomatolog"
            ObservableList<Stomatolog> listaStomatologow = FXCollections.observableArrayList();
            while (result.next()) { //Wstawianie rekordów tabeli "stomatolog" do listy 
                //System.out.println(result.getString("godziny_przyjec"));
                listaStomatologow.add(new Stomatolog(result.getInt("id_stomatologa"), result.getString("imie"), result.getString("nazwisko"),
                        result.getString("rok_zatrudnienia"), result.getDate("data_urodzenia"), result.getString("telefon"), result.getString("adres_kliniki"), (result.getString("godziny_przyjec").equals("R") ? "Poranne" : "Wieczorne")));
            }

            tabelaStomatologow.setItems(listaStomatologow); //Wstawienie rekordów z listy do tabeli TableView
            tabelaStomatologow.getSortOrder().add(idStomatologa); //Ustawienie domyślnego sortowania (po ID)

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    //Metoda otwierająca formularz dodawania rekordu
    @FXML
    private void DodanieStomatologa(ActionEvent event) throws SQLException, IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("DodajStomatologa.fxml"));
        Parent root1 = (Parent) fxmlLoader.load();
        Stage stage = new Stage();
        stage.setScene(new Scene(root1));
        stage.setTitle("Dodaj stomatologa");
        stage.show();

        //Po zamknięciu okna formularza tabela TableView jest odświeżana 
        stage.setOnHiding(ev -> {
            odswiezTabeleStomatologow();
        });
    }

    //Metoda usuwająca zaznaczony rekord
    @FXML
    private void UsuwanieStomatologa(ActionEvent event) throws SQLException {
        if (tabelaStomatologow.getSelectionModel().getSelectedItem() != null) { //sprawdzenie, czy jakiś rekord jest zaznaczony
            try {
                String sql = "{call stomatolog_pack.usun(?)}";
                handler.usunStomatologa(sql, tabelaStomatologow.getSelectionModel().getSelectedItem().getId_stomatologa());
                odswiezTabeleStomatologow(); //odświeżenie tabeli TableView
            } catch (SQLException e) {
                System.out.println(e.getMessage());
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Uwaga");
                alert.setHeaderText("Wybrany stomatolog jest częścią innej tabeli");
                alert.setContentText("Proszę najpierw usunąć wszystkie wizyty dla tego stomatologa");
                alert.showAndWait();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Uwaga");
            alert.setHeaderText("Nie zaznaczono stomatologa do usunięcia");
            alert.setContentText("Proszę wybrać stomatologa");
            alert.showAndWait();
        }
    }

    //Metoda otwierająca formularz edytowania rekordu
    @FXML
    private void EdytowanieStomatologa(ActionEvent event) throws IOException {
        if (tabelaStomatologow.getSelectionModel().getSelectedItem() != null) { //sprawdzenie, czy jakiś rekord jest zaznaczony
            stomatologSelectedId = tabelaStomatologow.getSelectionModel().getSelectedItem().getId_stomatologa(); //pobranie ID zaznaczonego rekordu
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("EdytujStomatologa.fxml"));
            Parent root1 = (Parent) fxmlLoader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root1));
            stage.setTitle("Edytuj stomatologa");

            //Przekazanie zaznaczonego id do formularza
            EdytujStomatologaController controller = fxmlLoader.<EdytujStomatologaController>getController();
            controller.stomatologPrzekazId(stomatologSelectedId);

            stage.show();

            //Po zamknięciu okna formularza tabela TableView jest odświeżana 
            stage.setOnHiding(ev -> {
                odswiezTabeleStomatologow();
            });

        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Uwaga");
            alert.setHeaderText("Nie zaznaczono stomatologa do edycji");
            alert.setContentText("Proszę wybrać stomatologa");
            alert.showAndWait();
        }
    }

}
