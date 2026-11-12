package campuseats.pipeline;

import campuseats.modelo.LineaPedido;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Regla: cada pedido tiene un número único (CU-1.2). Se conserva solo la primera aparición. */
public class SinPedidosRepetidos implements Filtro {

    @Override public String nombre() { return "Pedidos repetidos"; }

    @Override
    public List<LineaPedido> aplicar(List<LineaPedido> lineas) {
        Map<String, Integer> primeraAparicion = new HashMap<>();
        for (LineaPedido l : lineas) {
            primeraAparicion.merge(l.numeroPedido(), l.indiceRegistro(), Math::min);
        }
        return lineas.stream()
                .filter(l -> l.indiceRegistro() == primeraAparicion.get(l.numeroPedido()))
                .toList();
    }
}
