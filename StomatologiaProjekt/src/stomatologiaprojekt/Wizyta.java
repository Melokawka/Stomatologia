package stomatologiaprojekt;

import java.sql.Date;
import java.sql.Time;


public class Wizyta {
    
    private int id_wizyty;
    private Time godzina;
    private Date data_;
    private int id_pacjenta;
    private int id_zabiegu;
    private int id_stomatologa;
    private int id_kliniki;

    public Wizyta(int id_wizyty, Time godzina, Date data_, int id_pacjenta, int id_zabiegu, int id_stomatologa, int id_kliniki) {
        this.id_wizyty = id_wizyty;
        this.godzina = godzina;
        this.data_ = data_;
        this.id_pacjenta = id_pacjenta;
        this.id_zabiegu = id_zabiegu;
        this.id_stomatologa = id_stomatologa;
        this.id_kliniki = id_kliniki;
    }

    public int getId_wizyty() {
        return id_wizyty;
    }

    public void setId_wizyty(int id_wizyty) {
        this.id_wizyty = id_wizyty;
    }

    public Time getGodzina() {
        return godzina;
    }

    public void setGodzina(Time godzina) {
        this.godzina = godzina;
    }

    public Date getData_() {
        return data_;
    }

    public void setData_(Date data_) {
        this.data_ = data_;
    }

    public int getId_pacjenta() {
        return id_pacjenta;
    }

    public void setId_pacjenta(int id_pacjenta) {
        this.id_pacjenta = id_pacjenta;
    }

    public int getId_zabiegu() {
        return id_zabiegu;
    }

    public void setId_zabiegu(int id_zabiegu) {
        this.id_zabiegu = id_zabiegu;
    }

    public int getId_stomatologa() {
        return id_stomatologa;
    }

    public void setId_stomatologa(int id_stomatologa) {
        this.id_stomatologa = id_stomatologa;
    }

    public int getId_kliniki() {
        return id_kliniki;
    }

    public void setId_kliniki(int id_kliniki) {
        this.id_kliniki = id_kliniki;
    }
}
