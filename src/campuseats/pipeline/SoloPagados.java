package campuseats.pipeline;

import campuseats.modelo.LineaPedido;

import java.util.List;

/** Regla: solo cuentan como venta los pedidos que ya pasaron por «Pagado» (CU-2.1). */
public class SoloPagados implements Filtro {

    @Override public String nombre() { return "Pedidos que no fueron pagados"; }

    @Override
    public List<LineaPedido> aplicar(List<LineaPedido> lineas) {
        return lineas.stream().filter(l -> l.pagadoEn() != null).toList();
    }
}
