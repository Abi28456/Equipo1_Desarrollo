package campuseats.pipeline;

import campuseats.modelo.EstadoPago;
import campuseats.modelo.LineaPedido;
import campuseats.modelo.Pago;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Regla: un pedido pagado debe tener un pago APROBADO en la pasarela
 * y el monto cobrado debe ser igual al total del pedido (CU-2.1).
 * Un pedido puede tener varios intentos (RECHAZADA y luego APROBADA): basta uno válido.
 */
public class PagoAprobadoYMontoIgual implements Filtro {

    private final Set<String> pagosValidos = new HashSet<>();   // "referencia|centavos"

    public PagoAprobadoYMontoIgual(List<Pago> pagos) {
        for (Pago p : pagos) {
            if (p.estado() == EstadoPago.APROBADA) {
                pagosValidos.add(clave(p.referencia(), p.monto()));
            }
        }
    }

    @Override public String nombre() { return "Pago no aprobado o monto distinto al total"; }

    @Override
    public List<LineaPedido> aplicar(List<LineaPedido> lineas) {
        return lineas.stream()
                .filter(l -> pagosValidos.contains(clave(l.numeroPedido(), l.totalPedido())))
                .toList();
    }

    private static String clave(String referencia, double monto) {
        return referencia + "|" + Math.round(monto * 100);       // se compara en centavos
    }
}
