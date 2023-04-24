package stomatologiaprojekt;


public class Rodzaj_zabiegu {
    
    private int id_rodzaju_zabiegu;
    private String rodzaj_zabiegu;
    private int cena;
    private String refundacjaDzieci;
    private int krotnoscRefundacji;

    public Rodzaj_zabiegu(int id_rodzaju_zabiegu, String rodzaj_zabiegu, int cena, String refundacjaDzieci, int krotnoscRefundacji) {
        this.id_rodzaju_zabiegu = id_rodzaju_zabiegu;
        this.rodzaj_zabiegu = rodzaj_zabiegu;
        this.cena = cena;
        this.refundacjaDzieci=refundacjaDzieci;
        this.krotnoscRefundacji=krotnoscRefundacji;
    }

    public int getId_rodzaju_zabiegu() {
        return id_rodzaju_zabiegu;
    }

    public void setId_rodzaju_zabiegu(int id_rodzaju_zabiegu) {
        this.id_rodzaju_zabiegu = id_rodzaju_zabiegu;
    }

    public String getRodzaj_zabiegu() {
        return rodzaj_zabiegu;
    }

    public void setRodzaj_zabiegu(String rodzaj_zabiegu) {
        this.rodzaj_zabiegu = rodzaj_zabiegu;
    }

    public int getCena() {
        return cena;
    }

    public void setCena(int cena) {
        this.cena = cena;
    }

    public String getRefundacjaDzieci() {
        return refundacjaDzieci;
    }

    public void setRefundacjaDzieci(String refundacjaDzieci) {
        this.refundacjaDzieci = refundacjaDzieci;
    }

    public int getKrotnoscRefundacji() {
        return krotnoscRefundacji;
    }

    public void setKrotnoscRefundacji(int krotnoscRefundacji) {
        this.krotnoscRefundacji = krotnoscRefundacji;
    }
    
    
    
}
