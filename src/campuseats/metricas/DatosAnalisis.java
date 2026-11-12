package campuseats.metricas;

import campuseats.modelo.LineaPedido;
import campuseats.modelo.Pago;

import java.util.List;

/** Lo que recibe cada métrica: líneas ya limpias y los pagos del periodo. */
public record DatosAnalisis(List<LineaPedido> lineas, List<Pago> pagos) { }

