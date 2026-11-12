package campuseats.servicio;

import campuseats.pipeline.InformeFiltro;

import java.util.List;

/** Resumen de lo que ocurrió al cargar: cuánto se leyó, cuánto descartó cada filtro y cuánto quedó. */
public record InformeCarga(int lineasLeidas, int pagosLeidos, int platosLeidos,
                           List<InformeFiltro> filtros, int lineasFinales, int pedidosFinales) { }
