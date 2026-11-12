package campuseats.fuentes;

import campuseats.modelo.EstadoPago;
import campuseats.modelo.Pago;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * ADAPTER: CSV de la pasarela. Separador ';', monto con coma decimal (7,50)
 * y fecha dd/MM/yyyy HH:mm:ss.
 */
public class PasarelaCsvAdapter implements FuenteDatos<Pago> {

    private static final DateTimeFormatter FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    private final Path ruta;

    public PasarelaCsvAdapter(Path ruta) { this.ruta = ruta; }

    @Override
    public List<Pago> leer() throws IOException {
        List<String> filas = Files.readString(ruta).replace("\uFEFF", "").lines().toList();
        List<Pago> pagos = new ArrayList<>();
        for (int i = 1; i < filas.size(); i++) {              // la fila 0 es el encabezado
            String fila = filas.get(i).trim();
            if (fila.isEmpty()) continue;
            String[] c = fila.split(";", -1);                  // REFERENCIA;MONTO;ESTADO_TX;COD;MOTIVO;FECHA_HORA
            if (c.length < 6) throw new IOException("Fila " + (i + 1) + " inválida en " + ruta.getFileName());
            pagos.add(new Pago(
                    c[0].trim(),
                    Double.parseDouble(c[1].trim().replace(',', '.')),
                    EstadoPago.valueOf(c[2].trim()),
                    c[4].trim(),
                    LocalDateTime.parse(c[5].trim(), FECHA)));
        }
        return pagos;
    }
}
