package stomatologiaprojekt;

import java.sql.Date;
import java.sql.Time;

//Ponieważ tabela "Wizyta" składa się w większości z ID innych tabel, 
//w tabeli pokazującej dane wizyt pokazane będą dane czytelniejsze dla użytkownika (imiona, nazwiska itp.)

//Klasa przedstawiająca model danych tabeli "Wizyta" z kolumnami tabel powiązanych z nią relacjami
public class WizytaModelDanych {
    private int id_wizyty;
    private Time godzina;
    private Date data_;
    private String imie;
    private String nazwisko;
    private String nazwiskoStom;
    private String adres;
    private int koszt;    
    private String rodzaj_zabiegu;
    private String archiwalna;
    
    
    public WizytaModelDanych(int id_wizyty, Time godzina, Date data_, String imie, String nazwisko, String nazwiskoStom, String adres, int koszt, String archiwalna) {
        this.id_wizyty = id_wizyty;
        this.godzina = godzina;
        this.data_ = data_;
        this.imie = imie;
        this.nazwisko = nazwisko;
        this.nazwiskoStom = nazwiskoStom;
        this.adres = adres;
        this.koszt = koszt;
        this.archiwalna=archiwalna;
    }
    
    public int getId_wizyty() {
        return id_wizyty;
    }

    public Time getGodzina() {
        return godzina;
    }

    public Date getData_() {
        return data_;
    }

    public String getImie() {
        return imie;
    }

    public String getNazwisko() {
        return nazwisko;
    }

    public String getRodzaj_zabiegu() {
        return rodzaj_zabiegu;
    }

    public String getNazwiskoStom() {
        return nazwiskoStom;
    }

    public String getAdres() {
        return adres;
    }

    public int getKoszt() {
        return koszt;
    }

    public String getArchiwalna() {
        return archiwalna;
    }

    public void setRodzaj_zabiegu(String rodzaj_zabiegu) {
        this.rodzaj_zabiegu = rodzaj_zabiegu;
    }


}
