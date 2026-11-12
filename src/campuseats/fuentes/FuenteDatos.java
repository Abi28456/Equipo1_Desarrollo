package campuseats.fuentes;

import java.io.IOException;
import java.util.List;

/**
 * ADAPTER (interfaz destino): el resto del programa solo conoce esta interfaz.
 * Cada archivo, sin importar su formato, entrega objetos del modelo.
 */
public interface FuenteDatos<T> {
    List<T> leer() throws IOException;
}
