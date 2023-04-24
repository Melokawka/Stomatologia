CREATE TABLE klinika (
    id_kliniki int GENERATED BY DEFAULT ON NULL AS IDENTITY PRIMARY KEY NOT NULL,
    miejscowosc varchar2(15) NOT NULL,
    adres varchar2(25) NOT NULL,
    kod_pocztowy char(6) NOT NULL,
    telefon char(9) NOT NULL
);

CREATE TABLE pacjent (
    id_pacjenta NUMBER GENERATED BY DEFAULT ON NULL as IDENTITY PRIMARY KEY NOT NULL,
    imie varchar2(40) NOT NULL,
    nazwisko varchar2(40) NOT NULL,
    plec char(1) NOT NULL,
    data_urodzenia date NOT NULL,
    pesel char(11) NOT NULL,
    miejscowosc varchar2(40) NOT NULL,
    adres varchar2(40) NOT NULL,
    telefon char(9) NOT NULL,
    ubezpieczenie char(1) NOT NULL
);

CREATE TABLE rodzaj_zabiegu (
    id_rodzaju_zabiegu NUMBER GENERATED BY DEFAULT ON NULL as IDENTITY PRIMARY KEY NOT NULL,
    rodzaj_zabiegu varchar2(40) NOT NULL,
    cena int NOT NULL,
    refundacja_dzieci char(1) NOT NULL,
    refundacja_dorosli_rok int NOT NULL
);

CREATE TABLE stomatolog (
    id_stomatologa NUMBER GENERATED BY DEFAULT ON NULL as IDENTITY PRIMARY KEY NOT NULL,
    imie varchar2(20) NOT NULL,
    nazwisko varchar2(20) NOT NULL,
    rok_zatrudnienia char(4) NOT NULL,
    data_urodzenia date NOT NULL,
    telefon char(9) NOT NULL,
    id_kliniki int NOT NULL,
    godziny_przyjec char(1) NOT NULL,
    CONSTRAINT fk_klidostom
      FOREIGN KEY(id_kliniki) 
	  REFERENCES klinika(id_kliniki)
);

CREATE TABLE wizyta (
    id_wizyty NUMBER GENERATED BY DEFAULT ON NULL as IDENTITY PRIMARY KEY NOT NULL,
    godzina char(8) NOT NULL,
    data_ date NOT NULL,
    id_pacjenta int NOT NULL,
    id_stomatologa int NOT NULL,
    koszt int NOT NULL,
    archiwalna char(1) NOT NULL,
    CONSTRAINT fk_pacdowiz
      FOREIGN KEY(id_pacjenta) 
	  REFERENCES pacjent(id_pacjenta),
    CONSTRAINT id_stomdowiz
      FOREIGN KEY(id_stomatologa) 
	  REFERENCES stomatolog(id_stomatologa)
);   

CREATE TABLE wizyta_zabiegi (
    id_wizyty int NOT NULL,
    id_rodzaju_zabiegu int NOT NULL,
    refundacja char(1) NOT NULL,
    CONSTRAINT fk_wizdowizab
      FOREIGN KEY(id_wizyty) 
	  REFERENCES wizyta(id_wizyty),
    CONSTRAINT id_rodzdowizab
      FOREIGN KEY(id_rodzaju_zabiegu) 
	  REFERENCES rodzaj_zabiegu(id_rodzaju_zabiegu)
);

CREATE TABLE uruchom (
    data_uruchom date PRIMARY KEY
    );