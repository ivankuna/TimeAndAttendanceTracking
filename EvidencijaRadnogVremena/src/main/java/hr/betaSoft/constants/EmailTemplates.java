package hr.betaSoft.constants;

public class EmailTemplates {

    public static final String CLOCK_IN_ERROR_EMAIL_SUBJECT = "Greška u prijavi dolaska";

    public static final String CLOCK_IN_ERROR_EMAIL_BODY = """
            Poštovani,

            Djelatnik %s (OIB: %s) je prijavio dolazak na posao %s, a nije prijavio odlazak s posla%s.

            Ova poruka je automatski generirana te Vas molimo da na nju ne odgovarate.

            %s
            %s
            %s
            """;

    public static final String CLOCK_OUT_ERROR_EMAIL_SUBJECT = "Greška u prijavi odlaska";

    public static final String CLOCK_OUT_ERROR_EMAIL_BODY = """
            Poštovani,
            
            Djelatnik %s (OIB: %s) je prijavio odlazak s posla %s, a nije prijavio dolazak nakon odlaska s posla%s.
            
            Ova poruka je automatski generirana te Vas molimo da na nju ne odgovarate.
                                  
            %s
            %s
            %s
            """;
}