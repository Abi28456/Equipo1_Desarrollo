package campuseats.fuentes;

import campuseats.modelo.EstadoPlato;
import campuseats.modelo.Plato;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/** ADAPTER: CSV del administrador, separado por coma. */
public class MenuCsvAdapter implements FuenteDatos<Plato> {

    private final Path ruta;

    public MenuCsvAdapter(Path ruta) { this.ruta = ruta; }

    @Override
    public List<Plato> leer() throws IOException {
        List<String> filas = Files.readString(ruta).replace("\uFEFF", "").lines().toList();
        List<Plato> platos = new ArrayList<>();
        for (int i = 1; i < filas.size(); i++) {
            String fila = filas.get(i).trim();
            if (fila.isEmpty()) continue;
            String[] c = fila.split(",", -1);                  // id,nombre,categoria,precio,estado
            if (c.length < 5) throw new IOException("Fila " + (i + 1) + " inválida en " + ruta.getFileName());
            platos.add(new Plato(
                    Integer.parseInt(c[0].trim()), c[1].trim(), c[2].trim(),
                    Double.parseDouble(c[3].trim()), EstadoPlato.valueOf(c[4].trim())));
        }
        return platos;
    }
}
