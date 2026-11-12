package campuseats.metricas;

import campuseats.modelo.LineaPedido;

import java.util.Collection;
import java.util.List;

/** Estrategia 3 (propuesta del equipo): ticket promedio = total vendido / número de pedidos. */
public class TicketPromedio implements Metrica {

    @Override public String nombre() { return "Ticket promedio"; }

    @Override
    public ResultadoMetrica calcular(DatosAnalisis datos) {
        Collection<LineaPedido> pedidos = Util.unaPorPedido(datos.lineas());
        double suma = pedidos.stream().mapToDouble(LineaPedido::totalPedido).sum();
        double promedio = pedidos.isEmpty() ? 0 : suma / pedidos.size();
        return new ResultadoMetrica(nombre(), List.of(
                Util.num(promedio, 2) + " dolares por pedido (" + pedidos.size() + " pedidos)"));
    }
}
