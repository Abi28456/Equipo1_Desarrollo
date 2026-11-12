package campuseats.metricas;

import campuseats.modelo.LineaPedido;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Estrategia 2: cuántos PEDIDOS (no líneas) se pagaron en cada hora del día. */
public class PedidosPorHora implements Metrica {

    @Override public String nombre() { return "Pedidos por hora"; }

    @Override
    public ResultadoMetrica calcular(DatosAnalisis datos) {
        Map<Integer, Integer> porHora = new HashMap<>();
        for (LineaPedido l : Util.unaPorPedido(datos.lineas())) {
            if (l.pagadoEn() != null) porHora.merge(l.pagadoEn().getHour(), 1, Integer::sum);
        }
        List<Map.Entry<Integer, Integer>> orden = porHora.entrySet().stream()
                .sorted(Map.Entry.<Integer, Integer>comparingByValue().reversed()
                        .thenComparing(Map.Entry.comparingByKey()))
                .toList();

        List<String> salida = new ArrayList<>();
        if (orden.isEmpty()) {
            salida.add("Sin pedidos en el periodo.");
        } else {
            Map.Entry<Integer, Integer> pico = orden.get(0);
            salida.add("La hora pico es las " + Util.hora(pico.getKey()) + ", con " + pico.getValue() + " pedidos.");
            StringBuilder siguen = new StringBuilder("Le siguen las ");
            for (int k = 1; k <= 2 && k < orden.size(); k++) {
                if (k > 1) siguen.append(" y las ");
                siguen.append(Util.hora(orden.get(k).getKey())).append(" (").append(orden.get(k).getValue()).append(")");
            }
            salida.add(siguen.toString());
        }
        return new ResultadoMetrica(nombre(), salida);
    }
}
