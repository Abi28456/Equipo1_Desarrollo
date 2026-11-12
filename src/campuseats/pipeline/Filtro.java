package campuseats.pipeline;

import campuseats.modelo.LineaPedido;

import java.util.List;

/**
 * PIPELINE (Pipes & Filters): un filtro aplica UNA sola regla de limpieza.
 * Recibe la lista de líneas y devuelve la lista sin las que no cumplen la regla.
 */
public interface Filtro {
    String nombre();
    List<LineaPedido> aplicar(List<LineaPedido> lineas);
}
