package hr.betaSoft.test;

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
    private String pocetakRada1;
    private String zavrsetakRada1;
    private String ukupnoSatiRada1;
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

    public AttendanceData(int i, String ponedjeljak, String time, String time1, String time2) {
    }
}

