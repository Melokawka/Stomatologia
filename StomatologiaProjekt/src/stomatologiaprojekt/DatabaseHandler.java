package stomatologiaprojekt;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.control.Label;
import javafx.util.Duration;

public class DatabaseHandler {

    private static DatabaseHandler handler = null;
    private Connection con = null;
    private String sql = "";
    private static PreparedStatement stmt = null;
    private static CallableStatement call = null;

    //Obiekt DatabaseHandler- wywołuje metodę tworzącą połączenie z bvazą
    private DatabaseHandler() throws ClassNotFoundException, SQLException, InstantiationException, IllegalAccessException {

    }

    //Singleton- obiekt DatabaseHandler może być tylko jeden. getInstance zwraca go, a jeśli nie istnieje- tworzy, a następnie zwraca
    public static DatabaseHandler getInstance() throws ClassNotFoundException, SQLException, InstantiationException, IllegalAccessException {
        if (handler == null) {
            handler = new DatabaseHandler();
        }
        return handler;
    }

    //Metoda tworząca połączenie z bazą
    void createConnection(String sql) {
        try {
            String url = "jdbc:oracle:thin:@localhost:1521:KOSMOS";
            String user = "jakub";
            String password = "melokawka";
            Class.forName("oracle.jdbc.OracleDriver").newInstance();
            Connection con = DriverManager.getConnection(url, user, password);
            if (sql.contains("call")) {
                call = con.prepareCall(sql);
            } else {
                stmt = con.prepareStatement(sql);
            }

        } //Obsługa wyjątku w przypadku problemów z połączeniem
        catch (Exception e) {
            System.err.println("Nie połączono");
            System.err.println(e.getMessage());

        }
    }

    //Zegar i kalendarz w menu
    public static void initClock(Label zegarek, Label kalendarzyk) {

        Timeline clock = new Timeline(new KeyFrame(Duration.ZERO, e -> {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
            zegarek.setText(LocalDateTime.now().format(formatter));
        }), new KeyFrame(Duration.seconds(1)));
        clock.setCycleCount(Animation.INDEFINITE);
        clock.play();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        kalendarzyk.setText(LocalDateTime.now().format(formatter));
    }

    //Metody do dodawania, edycji, usuwania i wyświetlania rekordów dla każdej tabeli
    //RODZAJ ZABIEGU
    void dodajRodzajZabiegu(String sql, String rodzaj_zabiegu, int cena, String refundacja, int krotnosc) throws SQLException {
        createConnection(sql);
        call.setString(1, rodzaj_zabiegu);
        call.setInt(2, cena);
        call.setString(3, refundacja);
        call.setInt(4, krotnosc);
        call.execute();
    }

    void edytujRodzajZabiegu(String sql, String rodzaj_zabiegu, int cena, String refundacja, int krotnosc, int id_rodzaju_zabiegu) throws SQLException {
        createConnection(sql);
        call.setInt(1, id_rodzaju_zabiegu);
        call.setString(2, rodzaj_zabiegu);
        call.setInt(3, cena);
        call.setString(4, refundacja);
        call.setInt(5, krotnosc);
        call.execute();
    }

    void usunRodzajZabiegu(String sql, int id_rodzaju_zabiegu) throws SQLException {
        createConnection(sql);
        call.setInt(1, id_rodzaju_zabiegu);
        call.executeUpdate();
    }

    ResultSet wyswietlRodzajZabiegu() throws SQLException {
        sql = "select * from rodzaj_zabiegu";
        createConnection(sql);
        ResultSet result = stmt.executeQuery();
        return result;
    }

    //WIZAB
    ResultSet wyswietZabiegiWizyty(int id_wizyty) throws SQLException {
        sql = "SELECT rodzaj_zabiegu FROM wizyta_zabiegi INNER JOIN rodzaj_zabiegu ON wizyta_zabiegi.id_rodzaju_zabiegu = rodzaj_zabiegu.id_rodzaju_zabiegu \n"
                + "INNER JOIN wizyta ON wizyta_zabiegi.id_wizyty = wizyta.id_wizyty WHERE wizyta.id_wizyty=?";
        createConnection(sql);
        stmt.setInt(1, id_wizyty);
        ResultSet result = stmt.executeQuery();
        return result;
    }

    void usunZabiegWizyty(String sql, int id_wizyty, String zabieg) throws SQLException  {
        String sql1;
        ResultSet result;
        
        sql1 = "SELECT id_rodzaju_zabiegu FROM rodzaj_zabiegu WHERE rodzaj_zabiegu = ?";
        createConnection(sql1);
        stmt.setString(1, zabieg);
        result = stmt.executeQuery();
        result.next();
        int id_zabiegu = result.getInt(1);
        System.out.println("k1");
        createConnection(sql);
        call.setInt(1, id_wizyty);
        call.setInt(2, id_zabiegu);
        
        
        //w pierwszej kolejności usuwane są zabiegi bez refundacji, gdyż dla dorosłych jest ona limitowana na rok
        sql1 = "SELECT COUNT(refundacja) FROM wizyta_zabiegi WHERE id_wizyty = ? AND id_rodzaju_zabiegu = ? AND refundacja = '0'";
        createConnection(sql1);
        stmt.setInt(1, id_wizyty);
        stmt.setInt(2, id_zabiegu);
        result = stmt.executeQuery();
        result.next();
        int znaleziono = result.getInt(1);
        System.out.println("k2");
        System.out.println(znaleziono);
        if(znaleziono>0){ 
           call.setString(3, "0");
            System.out.println("aha");
           call.execute();
        }
        else{
            call.setString(3, "1");
           call.execute();
        }
        
        //usunięcie zabiegu mogło mieć wpływ na koszt wizyty
        odswiezCene(id_wizyty);
    }

    void dodajWiZab(String sql, int id_wizyty, String zabieg) throws SQLException {
        String sql1;
        ResultSet result;
        sql1 = "SELECT id_rodzaju_zabiegu FROM rodzaj_zabiegu WHERE rodzaj_zabiegu = ?";
        createConnection(sql1);
        stmt.setString(1, zabieg);
        result = stmt.executeQuery();
        result.next();
        int id_zabiegu = result.getInt(1);

        sql1 = "SELECT P.data_urodzenia FROM wizyta W INNER JOIN pacjent P ON P.id_pacjenta = W.id_pacjenta WHERE W.id_wizyty = ?";
        createConnection(sql1);
        stmt.setInt(1, id_wizyty);
        result = stmt.executeQuery();
        result.next();
        Date data = result.getDate(1);
        sql1 = "SELECT id_pacjenta FROM wizyta WHERE id_wizyty = ?";
        createConnection(sql1);
        stmt.setInt(1, id_wizyty);
        result = stmt.executeQuery();
        result.next();
        int id_pacjenta = result.getInt(1);
        //sprawdzamy, czy pacjent jest dorosły
        int wiek = Period.between(data.toLocalDate(), LocalDate.now()).getYears();
        String refundacja;

        //przypisujemy refundację: sprawdzamy, czy dla dzieci zabieg jest refundowany a, dla dorosłych czy na ten rok refundacja im jeszcze przysługuje
        if (wiek < 18) {
            sql1 = "SELECT refundacja_dzieci FROM rodzaj_zabiegu WHERE id_rodzaju_zabiegu = ?";
            createConnection(sql1);
            stmt.setInt(1, id_zabiegu);
            result = stmt.executeQuery();
            result.next();
            refundacja = result.getString(1);
        } else {
            sql1 = "{? = call util_pack.refundacjaprzyslugujedor(?, ?)}";
            createConnection(sql1);
            call.registerOutParameter(1, Types.CHAR);
            call.setInt(2, id_pacjenta);
            call.setInt(3, id_zabiegu);
            call.executeUpdate();
            refundacja = call.getString(1);
        }
        createConnection(sql);
        call.setInt(1, id_wizyty);
        call.setInt(2, id_zabiegu);
        call.setString(3, refundacja);

        call.execute();
        //dodanie zabiegu mogło mieć wpływ na koszt wizyty
        odswiezCene(id_wizyty);
    }

    //KLINIKA
    void dodajKlinike(String sql, String miejscowosc, String adres, String kod_pocztowy, String telefon) throws SQLException {
        createConnection(sql);
        call.setString(1, miejscowosc);
        call.setString(2, adres);
        call.setString(3, kod_pocztowy);
        call.setString(4, telefon);
        call.execute();
    }

    void edytujKlinike(String sql, String miejscowosc, String adres, String kod_pocztowy, String telefon, int id_kliniki) throws SQLException {
        createConnection(sql);
        call.setInt(1, id_kliniki);
        call.setString(2, miejscowosc);
        call.setString(3, adres);
        call.setString(4, kod_pocztowy);
        call.setString(5, telefon);
        call.execute();
    }

    void usunKlinike(String sql, int id_kliniki) throws SQLException {
        createConnection(sql);
        call.setInt(1, id_kliniki);
        call.execute();
    }

    ResultSet wyswietlKlinike() throws SQLException {
        sql = "select * from klinika";
        createConnection(sql);
        ResultSet result = stmt.executeQuery();
        return result;
    }

    //STOMATOLOG
    void dodajStomatologa(String sql, String imie, String nazwisko, String rok_zatrudnienia, Date data_urodzenia, String telefon, String klinika, String godziny_przyjec) throws SQLException {
        // zdobadz id kliniki na podstawie adresu
        String sql1 = "SELECT id_kliniki FROM klinika WHERE klinika.adres=?";
        createConnection(sql1);
        stmt.setString(1, klinika);
        ResultSet result = stmt.executeQuery();
        result.next();
        int id_kliniki = result.getInt(1);

        createConnection(sql);
        call.setString(1, imie);
        call.setString(2, nazwisko);
        call.setString(3, rok_zatrudnienia);
        CharSequence seq = data_urodzenia.toString();
        call.setString(4, LocalDate.parse(
                seq,
                DateTimeFormatter.ofPattern("uuuu-MM-dd")
        ).format(
                DateTimeFormatter.ofPattern("dd-MM-uuuu")
        ));
        call.setString(5, telefon);
        call.setInt(6, id_kliniki); // id kliniki
        call.setString(7, godziny_przyjec);
        call.execute();
    }

    void edytujStomatologa(String sql, String imie, String nazwisko, String rok_zatrudnienia, Date data_urodzenia, String telefon, String klinika, String godziny_przyjec, int id_stomatologa) throws SQLException {
        // zdobadz id kliniki na podstawie adresu
        String sql1 = "SELECT id_kliniki FROM klinika WHERE klinika.adres=?";
        createConnection(sql1);
        stmt.setString(1, klinika);
        ResultSet result = stmt.executeQuery();
        result.next();
        int id_kliniki = result.getInt(1);

        // wykonaj procedure
        createConnection(sql);
        call.setInt(1, id_stomatologa);
        call.setString(2, imie);
        call.setString(3, nazwisko);
        call.setString(4, rok_zatrudnienia);
        CharSequence seq = data_urodzenia.toString();

        call.setString(5, LocalDate.parse(
                seq,
                DateTimeFormatter.ofPattern("uuuu-MM-dd")
        ).format(
                DateTimeFormatter.ofPattern("dd-MM-uuuu")
        ));
        call.setString(6, telefon);
        call.setInt(7, id_kliniki); // id kliniki
        call.setString(8, godziny_przyjec);
        call.execute();
    }

    void usunStomatologa(String sql, int id_stomatologa) throws SQLException {
        createConnection(sql);
        call.setInt(1, id_stomatologa);
        call.execute();
    }

    ResultSet wyswietlStomatologa() throws SQLException {
        sql = "select s.id_stomatologa,s.imie,s.nazwisko,s.rok_zatrudnienia,"
                + "s.data_urodzenia,s.telefon,"
                + "k.adres as adres_kliniki,s.godziny_przyjec from stomatolog s"
                + " inner join klinika k on s.id_kliniki=k.id_kliniki";
        createConnection(sql);
        ResultSet result = stmt.executeQuery();
        return result;
    }

    //Dodatkowe metody zaznaczony(...)- ustawiają pola formularza podczas edycji rekordu tabeli zawierającej wiele kolumn
    //PACJENT
    void dodajPacjenta(String sql, String imie, String nazwisko, String plec, String pesel, Date data_urodzenia,
            String miejscowosc, String adres, String telefon, String ubezpieczenie) throws SQLException {
        createConnection(sql);
        call.setString(1, imie);
        call.setString(2, nazwisko);
        call.setString(3, plec);
        CharSequence seq = data_urodzenia.toString();
        call.setString(4, LocalDate.parse(
                seq,
                DateTimeFormatter.ofPattern("yyyy-MM-dd")
        ).format(
                DateTimeFormatter.ofPattern("dd-MM-yyyy")
        ));

        call.setString(5, pesel);
        call.setString(6, miejscowosc);
        call.setString(7, adres);
        call.setString(8, telefon);
        call.setString(9, ubezpieczenie);
        call.execute();
    }

    void edytujPacjenta(String sql, String imie, String nazwisko, String plec, String pesel, Date data_urodzenia,
            String miejscowosc, String adres, String telefon, String ubezpieczenie, int id_pacjenta) throws SQLException {
        createConnection(sql);
        call.setInt(1, id_pacjenta);
        call.setString(2, imie);
        call.setString(3, nazwisko);
        call.setString(4, plec);
        CharSequence seq = data_urodzenia.toString();
        call.setString(5, LocalDate.parse(
                seq,
                DateTimeFormatter.ofPattern("uuuu-MM-dd")
        ).format(
                DateTimeFormatter.ofPattern("dd-MM-uuuu")
        ));
        call.setString(6, pesel);
        call.setString(7, miejscowosc);
        call.setString(8, adres);
        call.setString(9, telefon);
        call.setString(10, ubezpieczenie);
        call.execute();
    }

    void usunPacjenta(String sql, int id_pacjenta) throws SQLException {
        createConnection(sql);
        call.setInt(1, id_pacjenta);
        call.execute();
    }

    ResultSet wyswietlPacjenta() throws SQLException {
        sql = "select * from pacjent";
        createConnection(sql);
        ResultSet result = stmt.executeQuery();
        return result;
    }

    ResultSet zaznaczonyPacjent(String sql, int id) throws SQLException {
        createConnection(sql);
        stmt.setInt(1, id);
        ResultSet result = stmt.executeQuery();
        return result;
    }

    //WIZYTA
    void dodajWizyte(String sql, Time godzina, String data, String pesel, String stom) throws SQLException {
        String sql1;
        ResultSet result;
        String[] dane = stom.split(" ");
        sql1 = "SELECT id_stomatologa FROM stomatolog WHERE stomatolog.imie=? AND stomatolog.nazwisko=?";
        createConnection(sql1);
        stmt.setString(1, dane[0]);
        stmt.setString(2, dane[1]);
        result = stmt.executeQuery();
        result.next();
        int id_stomatologa = result.getInt(1);
        
        String sql2;
        sql2 = "SELECT id_pacjenta FROM pacjent WHERE pesel=?";
        createConnection(sql2);
        stmt.setString(1, pesel);
        result = stmt.executeQuery();
        // jezeli resultset zwroci false to nie istnieje taki pesel
        int id_pacjenta;
        try { 
            result.next();
            id_pacjenta = result.getInt(1);
        } catch (Exception e) {
            throw new IllegalArgumentException();
        }

        createConnection(sql);
        call.setString(1, godzina.toString());
        CharSequence seq = data;
        call.setString(2, LocalDate.parse(
                seq,
                DateTimeFormatter.ofPattern("uuuu-MM-dd")
        ).format(
                DateTimeFormatter.ofPattern("dd-MM-uuuu")
        ));
        call.setInt(3, id_pacjenta);
        call.setInt(4, id_stomatologa);
        call.setInt(5, 0);
        call.setString(6, "0");
        call.execute();

        sql1 = "SELECT id_wizyty FROM wizyta WHERE godzina=? AND data_=? AND id_pacjenta=?";
        createConnection(sql1);
        stmt.setString(1, godzina.toString());
        stmt.setString(2, data);
        stmt.setInt(3, id_pacjenta);
        result = stmt.executeQuery();
        result.next();
        int id_wizyty = result.getInt(1);

        dodajWiZab("{call wizab_pack.dodaj(?,?,?)}", id_wizyty, "Do ustalenia");
    }

    void edytujWizyte(String sql, Time godzina, String data, String pesel, String stom, int id_wizyty) throws SQLException {
        String sql1;
        ResultSet result;
        String[] dane = stom.split(" ");
        sql1 = "SELECT id_stomatologa FROM stomatolog WHERE stomatolog.imie=? AND stomatolog.nazwisko=?";
        createConnection(sql1);
        stmt.setString(1, dane[0]);
        stmt.setString(2, dane[1]);
        result = stmt.executeQuery();
        result.next();
        int id_stomatologa = result.getInt(1);
        
        String sql2;
        sql2 = "SELECT id_pacjenta FROM pacjent WHERE pesel=?";
        createConnection(sql2);
        stmt.setString(1, pesel);
        result = stmt.executeQuery();
        // jezeli resultset zwroci false to nie istnieje taki pesel
        int id_pacjenta;
        try { 
            result.next();
            id_pacjenta = result.getInt(1);
        } catch (Exception e) {
            throw new IllegalArgumentException();
        }

        createConnection(sql);
        call.setInt(1, id_wizyty);
        call.setString(2, godzina.toString());
        CharSequence seq = data;
        call.setString(3, LocalDate.parse(
                seq,
                DateTimeFormatter.ofPattern("uuuu-MM-dd")
        ).format(
                DateTimeFormatter.ofPattern("dd-MM-uuuu")
        ));
        call.setInt(4, id_pacjenta);
        call.setInt(5, id_stomatologa);

        sql1 = "SELECT koszt FROM wizyta WHERE id_wizyty=?";
        createConnection(sql1);
        stmt.setInt(1, id_wizyty);
        result = stmt.executeQuery();
        result.next();
        int koszt = result.getInt(1);

        call.setInt(6, koszt);

        sql1 = "SELECT archiwalna FROM wizyta WHERE id_wizyty=?";
        createConnection(sql1);
        stmt.setInt(1, id_wizyty);
        result = stmt.executeQuery();
        result.next();
        String archiwalna = result.getString(1);

        call.setString(7, archiwalna);
        call.execute();
    }

    void usunWizyte(String sql, int id_wizyty) throws SQLException {
        createConnection(sql);
        call.setInt(1, id_wizyty);
        call.execute();
    }

    ResultSet wyswietlWizyte() throws SQLException {
        sql = "SELECT w.id_wizyty,w.godzina,w.data_, p.imie, p.nazwisko nazwiskoPac,"
                + " s.nazwisko nazwiskoStom, k.adres, w.koszt, w.archiwalna"
                + " FROM wizyta w "
                + "INNER JOIN pacjent p ON w.id_pacjenta=p.id_pacjenta "
                + "INNER JOIN stomatolog s ON w.id_stomatologa=s.id_stomatologa "
                + "INNER JOIN klinika k ON s.id_kliniki=k.id_kliniki";

        createConnection(sql);
        ResultSet result = stmt.executeQuery();
        return result;
    }

    void odswiezCene(int id_wizyty) throws SQLException {
        sql = "{? = call util_pack.sumakoszt(?)}";
        createConnection(sql);
        call.registerOutParameter(1, Types.INTEGER);
        call.setInt(2, id_wizyty);
        call.executeUpdate();
        int suma = call.getInt(1);

        sql = "UPDATE wizyta SET koszt = ? WHERE wizyta.id_wizyty = ?";
        createConnection(sql);
        stmt.setInt(1, suma);
        stmt.setInt(2, id_wizyty);
        stmt.executeQuery();

    }

    String zabiegiDlaWizyty(int id_wizyty) throws SQLException {
        sql = "SELECT rodz.rodzaj_zabiegu from wizyta_zabiegi wizab "
                + "INNER JOIN rodzaj_zabiegu rodz ON wizab.id_rodzaju_zabiegu=rodz.id_rodzaju_zabiegu "
                + "WHERE wizab.id_wizyty=?";
        createConnection(sql);
        stmt.setInt(1, id_wizyty);
        ResultSet result = stmt.executeQuery();
        String zabiegi = "";
        while (result.next()) {
            zabiegi += result.getString("rodzaj_zabiegu") + "\n";
        }
        return zabiegi;
    }

    List<String> listaZabieg() throws SQLException {
        sql = "SELECT rodzaj_zabiegu FROM rodzaj_zabiegu";
        createConnection(sql);
        ResultSet result = stmt.executeQuery();
        List<String> lista = new ArrayList<>();
        while (result.next()) {
            lista.add(result.getString(1));
        }
        return lista;
    }

    List<String> listaStomatolog() throws SQLException {
        sql = "SELECT imie, nazwisko FROM stomatolog";
        createConnection(sql);
        ResultSet result = stmt.executeQuery();
        List<String> lista = new ArrayList<>();
        while (result.next()) {
            lista.add(result.getString(1) + " " + result.getString(2));
        }
        return lista;
    }

    List<String> listaKlinika() throws SQLException {
        sql = "SELECT adres FROM klinika";
        createConnection(sql);
        ResultSet result = stmt.executeQuery();
        List<String> lista = new ArrayList<>();
        while (result.next()) {
            lista.add(result.getString(1));
        }
        return lista;
    }

    List<String> listaDostepnychGodzin(String data, String stom, int id_wizyty) throws SQLException {
        ResultSet result;
        String[] dane = stom.split(" ");
        sql = "SELECT id_stomatologa FROM stomatolog WHERE stomatolog.imie=? AND stomatolog.nazwisko=?";
        createConnection(sql);
        stmt.setString(1, dane[0]);
        stmt.setString(2, dane[1]);
        result = stmt.executeQuery();
        result.next();
        int id_stomatologa = result.getInt(1);

        List<String> lista = new ArrayList<>();

        if (id_wizyty == -1) {
            sql = "SELECT godzina FROM wizyta WHERE data_=TO_DATE(?) AND id_stomatologa=?";
            createConnection(sql);
            stmt.setString(1, data);
            stmt.setInt(2, id_stomatologa);
            result = stmt.executeQuery();
            while (result.next()) {
                lista.add(result.getString(1));
            }
        } else {
            sql = "SELECT godzina FROM wizyta WHERE data_=TO_DATE(?) AND id_stomatologa=? AND id_wizyty!=?";
            createConnection(sql);
            stmt.setString(1, data);
            stmt.setInt(2, id_stomatologa);
            stmt.setInt(3, id_wizyty);
            result = stmt.executeQuery();
            while (result.next()) {
                lista.add(result.getString(1));
            }
        }

        List<String> listaDostepnych = new ArrayList<>();
        sql = "SELECT godziny_przyjec from stomatolog WHERE id_stomatologa=?";
        createConnection(sql);
        stmt.setInt(1, id_stomatologa);
        result = stmt.executeQuery();
        result.next();
        String godziny_przyjec = result.getString(1);
        if (godziny_przyjec.equals("R")) {
            for (int i = 8; i < 14; i++) {
                String godz="";
                if(i<10) godz += "0";
                godz += i + ":00:00";
                if (lista.contains(godz)) {
                    continue;
                }
                listaDostepnych.add(godz);
            }
        } else {
            for (int i = 14; i < 20; i++) {
                String godz="";
                if(i<10) godz += "0";
                godz += i + ":00:00";
                if (lista.contains(godz)) {
                    continue;
                }
                listaDostepnych.add(godz);
            }
        }

        return listaDostepnych;
    }

    ResultSet zaznaczonaWizyta(String sql, int id) throws SQLException {
        createConnection(sql);
        stmt.setInt(1, id);
        ResultSet result = stmt.executeQuery();
        return result;
    }
    
    ResultSet znajdzPeselWizyta(String sql, int id) throws SQLException {
        createConnection(sql);
        stmt.setInt(1, id);
        ResultSet result = stmt.executeQuery();
        return result;
    }

    public static boolean validateJavaDate(String strDate) {
        /* Check if date is 'null' */
        if (strDate.trim().equals("")) {
            return true;
        } /* Date is not 'null' */ else {
            /*
	     * Set preferred date format,
	     * For example MM-dd-yyyy, MM.dd.yyyy,dd.MM.yyyy etc.*/
            SimpleDateFormat sdfrmt = new SimpleDateFormat("yyyy-MM-dd");
            sdfrmt.setLenient(false);
            /* Create Date object
	     * parse the string into date 
             */
            try {
                java.util.Date javaDate = sdfrmt.parse(strDate);
                //System.out.println(strDate+" is valid date format");
            } /* Date format is invalid */ catch (ParseException e) {
                //System.out.println(strDate+" is Invalid Date format");
                return false;
            }
            /* Return true if date format is valid */
            return true;
        }
    }

    void archiwizujJedna(String sql, int id_wizyty) throws SQLException {
        createConnection(sql);
        call.setInt(1, id_wizyty);
        call.execute();
    }
    
    void archiwizacjaWszystkich() throws SQLException{
        sql="{call util_pack.dodajuruchom(TO_DATE(?))";
        createConnection(sql);
        call.setString(1, Date.valueOf(LocalDate.now()).toString());
        call.executeUpdate();
    }

}
