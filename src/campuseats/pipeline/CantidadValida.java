package campuseats.pipeline;

import campuseats.modelo.LineaPedido;

import java.util.List;

/** Regla: la cantidad de cada plato debe ser un entero mayor que cero (CU-1.4). */
public class CantidadValida implements Filtro {

    @Override public String nombre() { return "Cantidad igual a cero"; }

    @Override
    public List<LineaPedido> aplicar(List<LineaPedido> lineas) {
        return lineas.stream().filter(l -> l.cantidad() > 0).toList();
    }
}
