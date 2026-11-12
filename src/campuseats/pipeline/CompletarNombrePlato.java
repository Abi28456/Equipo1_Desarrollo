package campuseats.pipeline;

import campuseats.modelo.LineaPedido;
import campuseats.modelo.Plato;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Paso de ENRIQUECIMIENTO (va después de limpiar): agrega el nombre del plato desde el menú.
 * - Solo toma el NOMBRE; el precio se queda el del pedido (CU-7.2).
 * - Los platos RETIRADOS no se filtran: se conservan en el historial (CU-7.3).
 */
public class CompletarNombrePlato implements Filtro {

    private final Map<Integer, String> nombres = new HashMap<>();

    public CompletarNombrePlato(List<Plato> menu) {
        for (Plato p : menu) nombres.put(p.id(), p.nombre());
    }

    @Override public String nombre() { return "Completar nombre del plato (menú)"; }

    @Override
    public List<LineaPedido> aplicar(List<LineaPedido> lineas) {
        return lineas.stream()
                .map(l -> l.conNombre(nombres.getOrDefault(l.platoId(), "Plato #" + l.platoId())))
                .toList();
    }
}
