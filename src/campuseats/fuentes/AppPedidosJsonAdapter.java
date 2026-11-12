package campuseats.fuentes;

import campuseats.modelo.LineaPedido;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** ADAPTER: traduce el JSON de la app (un objeto por pedido) a una lista de LineaPedido. */
public class AppPedidosJsonAdapter implements FuenteDatos<LineaPedido> {

    /** Una cantidad no entera se marca con 0: el filtro CantidadValida la descartará. */
    private static final int CANTIDAD_INVALIDA = 0;

    private final Path ruta;

    public AppPedidosJsonAdapter(Path ruta) { this.ruta = ruta; }

    @Override
    public List<LineaPedido> leer() throws IOException {
        List<Object> pedidos = JsonMini.lista(JsonMini.parsear(Files.readString(ruta)));
        List<LineaPedido> lineas = new ArrayList<>();
        int indice = 0;
        for (Object p : pedidos) {
            Map<String, Object> pedido = JsonMini.objeto(p);
            String numero = JsonMini.texto(pedido.get("numeroPedido"));
            LocalDateTime registrado = LocalDateTime.parse(JsonMini.texto(pedido.get("registradoEn")));
            double total = JsonMini.numero(pedido.get("total"));
            Map<String, LocalDateTime> hitos = hitos(pedido.get("historialEstados"));

            for (Object l : JsonMini.lista(pedido.get("lineas"))) {
                Map<String, Object> linea = JsonMini.objeto(l);
                lineas.add(new LineaPedido(
                        numero, indice,
                        (int) JsonMini.numero(linea.get("platoId")),
                        null,
                        cantidad(linea.get("cantidad")),
                        JsonMini.numero(linea.get("precioUnitario")),
                        total, registrado,
                        hitos.get("PAGADO"),
                        hitos.get("LISTO_RETIRO")));
            }
            indice++;
        }
        return lineas;
    }

    /** Primera fecha en la que el pedido llegó a cada estado. */
    private static Map<String, LocalDateTime> hitos(Object historial) {
        Map<String, LocalDateTime> resultado = new HashMap<>();
        if (historial == null) return resultado;
        for (Object h : JsonMini.lista(historial)) {
            Map<String, Object> cambio = JsonMini.objeto(h);
            resultado.putIfAbsent(JsonMini.texto(cambio.get("estado")),
                    LocalDateTime.parse(JsonMini.texto(cambio.get("fechaHora"))));
        }
        return resultado;
    }

    private static int cantidad(Object valor) {
        if (valor == null) return CANTIDAD_INVALIDA;
        double d = JsonMini.numero(valor);
        return (d == Math.rint(d) && Math.abs(d) < Integer.MAX_VALUE) ? (int) d : CANTIDAD_INVALIDA;
    }
}
