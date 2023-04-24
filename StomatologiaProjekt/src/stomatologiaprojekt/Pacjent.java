package stomatologiaprojekt;

import java.sql.Date;


public class Pacjent {
    
    private int id_pacjenta;
    private String imie;
    private String nazwisko;
    private String plec;
    private String pesel;
    private Date data_urodzenia;
    private String miejscowosc;
    private String adres;
    private String telefon;
    private String ubezpieczenie;

    public Pacjent(int id_pacjenta, String imie, String nazwisko, String plec, String pesel, Date data_urodzenia, String miejscowosc, String adres, String telefon, String ubezpieczenie) {
        this.id_pacjenta = id_pacjenta;
        this.imie = imie;
        this.nazwisko = nazwisko;
        this.plec = plec;
        this.pesel = pesel;
        this.data_urodzenia = data_urodzenia;
        this.miejscowosc = miejscowosc;
        this.adres = adres;
        this.telefon = telefon;
        this.ubezpieczenie = ubezpieczenie;
    }

    public int getId_pacjenta() {
        return id_pacjenta;
    }

    public void setId_pacjenta(int id_pacjenta) {
        this.id_pacjenta = id_pacjenta;
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

    public String getPlec() {
        return plec;
    }

    public void setPlec(String plec) {
        this.plec = plec;
    }

    public String getPesel() {
        return pesel;
    }

    public void setPesel(String pesel) {
        this.pesel = pesel;
    }

    public Date getData_urodzenia() {
        return data_urodzenia;
    }

    public void setData_urodzenia(Date data_urodzenia) {
        this.data_urodzenia = data_urodzenia;
    }

    public String getMiejscowosc() {
        return miejscowosc;
    }

    public void setMiejscowosc(String miejscowosc) {
        this.miejscowosc = miejscowosc;
    }

    public String getAdres() {
        return adres;
    }

    public void setAdres(String adres) {
        this.adres = adres;
    }

    public String getTelefon() {
        return telefon;
    }

    public void setTelefon(String telefon) {
        this.telefon = telefon;
    }

    public String getUbezpieczenie() {
        return ubezpieczenie;
    }

    public void setUbezpieczenie(String ubezpieczenie) {
        this.ubezpieczenie = ubezpieczenie;
    }
    
    
    
}
