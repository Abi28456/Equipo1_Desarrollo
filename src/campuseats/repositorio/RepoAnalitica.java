package campuseats.repositorio;

import campuseats.modelo.LineaPedido;
import campuseats.modelo.Pago;

import java.time.LocalDate;
import java.util.List;

/**
 * REPOSITORY (interfaz): las métricas piden datos por rango de fechas sin saber dónde se guardan
 * (memoria, base de datos, archivo...).
 */
public interface RepoAnalitica {
    void guardarLineas(List<LineaPedido> lineas);
    void guardarPagos(List<Pago> pagos);
    List<LineaPedido> lineasEntre(LocalDate desde, LocalDate hasta);   // inclusivo
    List<Pago> pagosEntre(LocalDate desde, LocalDate hasta);           // inclusivo
}
