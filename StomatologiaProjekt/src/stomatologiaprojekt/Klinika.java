package stomatologiaprojekt;


public class Klinika {
    
    private int id_kliniki;
    private String miejscowosc;
    private String adres;
    private String kod_pocztowy;
    private String telefon;

    public Klinika(int id_kliniki, String miejscowosc, String adres, String kod_pocztowy, String telefon) {
        this.id_kliniki = id_kliniki;
        this.miejscowosc = miejscowosc;
        this.adres = adres;
        this.kod_pocztowy = kod_pocztowy;
        this.telefon = telefon;
    }

    public int getId_kliniki() {
        return id_kliniki;
    }

    public void setId_kliniki(int id_kliniki) {
        this.id_kliniki = id_kliniki;
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

    public String getKod_pocztowy() {
        return kod_pocztowy;
    }

    public void setKod_pocztowy(String kod_pocztowy) {
        this.kod_pocztowy = kod_pocztowy;
    }

    public String getTelefon() {
        return telefon;
    }

    public void setTelefon(String telefon) {
        this.telefon = telefon;
    }
    
    
    
}
