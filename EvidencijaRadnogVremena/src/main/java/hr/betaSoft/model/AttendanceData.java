package hr.betaSoft.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceData {

    private int datum;
    private String dan;
    private String pocetakRada;
    private String zavrsetakRada;
    private String ukupnoSatiRada;
    private String nocniRad;
    private String radNedjeljom;
    private String radBlagdanom;
    private String prekovremeniRad;
    private String smjenskiRad;
    private String dvokratniRad;
    private String terenskiRad;
    private String satiPripravnosti;
    private String neradniDaniIBlagdani;
    private String godisnjiOdmor;
    private String bolovanje;
    private String placeniDopusti;
    private String neplaceniDopusti;
    private String opravdaniIzostanak;

    public AttendanceData(int datum, String dan, String pocetakRada, String zavrsetakRada, String ukupnoSatiRada) {
        this.datum = datum;
        this.dan = dan;
        this.pocetakRada = pocetakRada;
        this.zavrsetakRada = zavrsetakRada;
        this.ukupnoSatiRada = ukupnoSatiRada;
    }

    public AttendanceData(int datum, String dan, String pocetakRada, String zavrsetakRada, String ukupnoSatiRada,
                          String nocniRad, String radNedjeljom, String radBlagdanom, String prekovremeniRad,
                          String smjenskiRad, String dvokratniRad, String terenskiRad, String satiPripravnosti) {
        this.datum = datum;
        this.dan = dan;
        this.pocetakRada = pocetakRada;
        this.zavrsetakRada = zavrsetakRada;
        this.ukupnoSatiRada = ukupnoSatiRada;
        this.nocniRad = nocniRad;
        this.radNedjeljom = radNedjeljom;
        this.radBlagdanom = radBlagdanom;
        this.prekovremeniRad = prekovremeniRad;
        this.smjenskiRad = smjenskiRad;
        this.dvokratniRad = dvokratniRad;
        this.terenskiRad = terenskiRad;
        this.satiPripravnosti = satiPripravnosti;
    }
}

