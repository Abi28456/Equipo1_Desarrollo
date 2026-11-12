package campuseats.pipeline;

import campuseats.modelo.LineaPedido;

import java.util.ArrayList;
import java.util.List;

/** Ejecuta los filtros EN ORDEN (la salida de uno es la entrada del siguiente) e informa los descartes. */
public class Pipeline {

    private final List<Filtro> filtros;

    public Pipeline(List<Filtro> filtros) { this.filtros = List.copyOf(filtros); }

    public ResultadoPipeline ejecutar(List<LineaPedido> entrada) {
        List<LineaPedido> actual = entrada;
        List<InformeFiltro> informes = new ArrayList<>();
        for (Filtro filtro : filtros) {
            List<LineaPedido> salida = filtro.aplicar(actual);
            informes.add(new InformeFiltro(filtro.nombre(), actual.size() - salida.size()));
            actual = salida;
        }
        return new ResultadoPipeline(actual, informes);
    }
}
