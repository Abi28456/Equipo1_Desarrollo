package campuseats.fuentes;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * FACTORY: decide qué adaptador crear según el campo "tipo" de fuentes.json,
 * SIN cadenas de if/else. Un Map asocia cada tipo con el constructor de su adaptador.
 * Para agregar un tipo nuevo basta llamar a registrar(...): nada más se modifica.
 */
public class FabricaFuentes {

    private final Path raizProyecto;
    private final Map<String, Function<Path, FuenteDatos<?>>> registro = new HashMap<>();

    public FabricaFuentes(Path raizProyecto) {
        this.raizProyecto = raizProyecto;
        registrar("app_json", AppPedidosJsonAdapter::new);
        registrar("pasarela_csv", PasarelaCsvAdapter::new);
        registrar("menu_csv", MenuCsvAdapter::new);
    }

    public void registrar(String tipo, Function<Path, FuenteDatos<?>> constructor) {
        registro.put(tipo, constructor);
    }

    public FuenteDatos<?> crear(ConfigFuente config) {
        Function<Path, FuenteDatos<?>> constructor = registro.get(config.tipo());
        if (constructor == null) {
            throw new IllegalArgumentException("Tipo de fuente desconocido: " + config.tipo());
        }
        return constructor.apply(raizProyecto.resolve(config.ruta()));
    }
}
