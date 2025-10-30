package app.blockchain;

import app.model.entity.EventoVehicular;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public final class EventHash {
    private EventHash() {}

    public static String sha256Evento(EventoVehicular ev, String vin) {
        String json = "{"
                + "\"vin\":\"" + lower(vin) + "\","
                + "\"fecha\":\"" + ev.getFechaEvento().toString() + "\","
                + "\"tipo\":\"" + (ev.getTipoEvento()==null? "": ev.getTipoEvento().name()) + "\","
                + "\"titulo\":\"" + n(ev.getTitulo()) + "\","
                + "\"descripcion\":\"" + n(ev.getDescripcion()) + "\","
                + "\"km\":" + ev.getKilometrajeEvento()
                + "}";

        return sha256Hex(json);
    }

    private static String n(String s){ return s==null? "" : s.trim(); }
    private static String lower(String s){ return s==null? "" : s.trim().toLowerCase(); }

    private static String sha256Hex(String s) {
        try {
            var md = MessageDigest.getInstance("SHA-256");
            byte[] d = md.digest(s.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder(d.length*2);
            for (byte b: d) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (Exception e) { throw new RuntimeException(e); }
    }

}
