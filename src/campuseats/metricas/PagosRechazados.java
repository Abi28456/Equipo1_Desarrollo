package campuseats.metricas;

import campuseats.modelo.EstadoPago;

import java.util.List;

/** Estrategia extra: porcentaje de transacciones RECHAZADAS por la pasarela. */
public class PagosRechazados implements Metrica {

    @Override public String nombre() { return "Pagos rechazados"; }

    @Override
    public ResultadoMetrica calcular(DatosAnalisis datos) {
        long total = datos.pagos().size();
        long rechazados = datos.pagos().stream().filter(p -> p.estado() == EstadoPago.RECHAZADA).count();
        double pct = total == 0 ? 0 : 100.0 * rechazados / total;
        return new ResultadoMetrica(nombre(), List.of(
                rechazados + " de " + total + " transacciones (" + Util.num(pct, 1) + " %)"));
    }
}
