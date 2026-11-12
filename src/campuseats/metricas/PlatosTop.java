package campuseats.metricas;

import campuseats.modelo.LineaPedido;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Estrategia 1: los N platos más vendidos (suma de UNIDADES, no de filas). */
public class PlatosTop implements Metrica {

    private final int cuantos;

    public PlatosTop(int cuantos) { this.cuantos = cuantos; }

    @Override public String nombre() { return "Platos mas vendidos"; }

    @Override
    public ResultadoMetrica calcular(DatosAnalisis datos) {
        Map<Integer, Integer> unidades = new HashMap<>();
        Map<Integer, String> nombres = new HashMap<>();
        for (LineaPedido l : datos.lineas()) {
            unidades.merge(l.platoId(), l.cantidad(), Integer::sum);
            nombres.putIfAbsent(l.platoId(), l.nombrePlato());
        }
        List<Map.Entry<Integer, Integer>> orden = unidades.entrySet().stream()
                .sorted(Map.Entry.<Integer, Integer>comparingByValue().reversed()
                        .thenComparing(Map.Entry.comparingByKey()))
                .limit(cuantos)
                .toList();

        List<String> salida = new ArrayList<>();
        int puesto = 1;
        for (Map.Entry<Integer, Integer> e : orden) {
            salida.add(puesto++ + ". " + nombres.get(e.getKey()) + ", " + e.getValue() + " unidades");
        }
        return new ResultadoMetrica(nombre(), salida);
    }
}
