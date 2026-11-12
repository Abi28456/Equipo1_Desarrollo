package campuseats.modelo;

import java.time.LocalDateTime;

/**
 * Una LÍNEA es un plato dentro de un pedido (un pedido con 3 platos = 3 líneas).
 * Es un record: inmutable. Para "completar" el nombre se crea una copia nueva.
 */
public record LineaPedido(
        String numeroPedido,
        int indiceRegistro,          // posición del pedido en el archivo (distingue un pedido repetido)
        int platoId,
        String nombrePlato,          // null hasta que lo completa el filtro CompletarNombrePlato
        int cantidad,
        double precioUnitario,       // precio con el que se hizo el pedido (NO el del menú actual)
        double totalPedido,
        LocalDateTime registradoEn,
        LocalDateTime pagadoEn,      // null si el pedido nunca pasó por PAGADO
        LocalDateTime listoRetiroEn) // null si nunca llegó a LISTO_RETIRO
{
    public LineaPedido conNombre(String nombre) {
        return new LineaPedido(numeroPedido, indiceRegistro, platoId, nombre, cantidad,
                precioUnitario, totalPedido, registradoEn, pagadoEn, listoRetiroEn);
    }
}
