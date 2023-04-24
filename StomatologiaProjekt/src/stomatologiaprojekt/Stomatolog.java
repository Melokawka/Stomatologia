package stomatologiaprojekt;

import java.sql.Date;

//Klasa POJO dla danych z tabeli "stomatolog"
public class Stomatolog {
    
    //Kolumny tabeli "stomatolog" przedstawione jako pola klasy o danych będącymi odpowiednikami typów danych SQL w Javie
    private int id_stomatologa;
    private String imie;
    private String nazwisko;
    private String rok_zatrudnienia;
    private Date data_urodzenia;
    private String telefon;
    private String klinika;
    private String godziny_przyjec;

    //Konstruktor obiektu (rekordu)
    public Stomatolog(int id_stomatologa, String imie, String nazwisko, String rok_zatrudnienia, Date data_urodzenia, String telefon, String klinika, String godziny_przyjec) {
        this.id_stomatologa = id_stomatologa;
        this.imie = imie;
        this.nazwisko = nazwisko;
        this.rok_zatrudnienia = rok_zatrudnienia;
        this.data_urodzenia = data_urodzenia;
        this.telefon = telefon;
        this.klinika=klinika;
        this.godziny_przyjec=godziny_przyjec;
    }
    
    //gettery i settery pól

    public int getId_stomatologa() {
        return id_stomatologa;
    }

    public void setId_stomatologa(int id_stomatologa) {
        this.id_stomatologa = id_stomatologa;
    }

    public String getImie() {
        return imie;
    }

    public void setImie(String imie) {
        this.imie = imie;
    }

    public String getNazwisko() {
        return nazwisko;
    }

    public void setNazwisko(String nazwisko) {
        this.nazwisko = nazwisko;
    }

    public String getRok_zatrudnienia() {
        return rok_zatrudnienia;
    }

    public void setRok_zatrudnienia(String rok_zatrudnienia) {
        this.rok_zatrudnienia = rok_zatrudnienia;
    }

    public Date getData_urodzenia() {
        return data_urodzenia;
    }

    public void setData_urodzenia(Date data_urodzenia) {
        this.data_urodzenia = data_urodzenia;
    }

    public String getTelefon() {
        return telefon;
    }

    public void setTelefon(String telefon) {
        this.telefon = telefon;
    }

    public String getKlinika() {
        return klinika;
    }

    public void setKlinika(String klinika) {
        this.klinika = klinika;
    }

    public String getGodziny_przyjec() {
        return godziny_przyjec;
    }

    public void setGodziny_przyjec(String godziny_przyjec) {
        this.godziny_przyjec = godziny_przyjec;
    }
    
    
    
}
