package campuseats.metricas;

import campuseats.modelo.LineaPedido;

import java.time.Duration;
import java.util.List;

/**
 * Estrategia extra: minutos que tarda la cocina entre «Pagado» y «Listo para retiro»,
 * separando la hora de almuerzo (12:00-13:59) del resto del día.
 */
public class TiempoPreparacion implements Metrica {

    @Override public String nombre() { return "Tiempo de preparacion"; }

    @Override
    public ResultadoMetrica calcular(DatosAnalisis datos) {
        double sumaAlmuerzo = 0, sumaResto = 0;
        int nAlmuerzo = 0, nResto = 0;
        for (LineaPedido l : Util.unaPorPedido(datos.lineas())) {
            if (l.pagadoEn() == null || l.listoRetiroEn() == null) continue;
            double minutos = Duration.between(l.pagadoEn(), l.listoRetiroEn()).toSeconds() / 60.0;
            int hora = l.pagadoEn().getHour();
            if (hora == 12 || hora == 13) { sumaAlmuerzo += minutos; nAlmuerzo++; }
            else { sumaResto += minutos; nResto++; }
        }
        return new ResultadoMetrica(nombre(), List.of(
                "Entre las 12:00 y las 13:59: " + Util.num(nAlmuerzo == 0 ? 0 : sumaAlmuerzo / nAlmuerzo, 1)
                        + " minutos (" + nAlmuerzo + " pedidos)",
                "Resto del dia: " + Util.num(nResto == 0 ? 0 : sumaResto / nResto, 1)
                        + " minutos (" + nResto + " pedidos)"));
    }
}
