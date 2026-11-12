package campuseats.fuentes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/** Una entrada de config/fuentes.json: {"tipo": "...", "ruta": "..."}. */
public record ConfigFuente(String tipo, String ruta) {

    public static List<ConfigFuente> leerTodas(Path archivo) throws IOException {
        Map<String, Object> raiz = JsonMini.objeto(JsonMini.parsear(Files.readString(archivo)));
        List<ConfigFuente> fuentes = new ArrayList<>();
        for (Object o : JsonMini.lista(raiz.get("fuentes"))) {
            Map<String, Object> f = JsonMini.objeto(o);
            fuentes.add(new ConfigFuente(JsonMini.texto(f.get("tipo")), JsonMini.texto(f.get("ruta"))));
        }
        return fuentes;
    }
}
