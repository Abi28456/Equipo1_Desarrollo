package campuseats.metricas;

import campuseats.modelo.LineaPedido;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/** Ayudas comunes a las métricas. */
final class Util {

    private Util() { }

    /**
     * Los datos tienen una fila por PLATO, no por pedido: si se cuentan filas, un pedido con
     * tres platos contaría tres veces. Esto deja una sola línea representante por pedido.
     */
    static Collection<LineaPedido> unaPorPedido(List<LineaPedido> lineas) {
        Map<String, LineaPedido> porPedido = new LinkedHashMap<>();
        for (LineaPedido l : lineas) porPedido.putIfAbsent(l.numeroPedido(), l);
        return porPedido.values();
    }

    /** Número con coma decimal (formato de la cafetería: 4,94). */
    static String num(double valor, int decimales) {
        return String.format(Locale.US, "%." + decimales + "f", valor).replace('.', ',');
    }

    static String hora(int h) { return String.format("%02d:00", h); }
}

