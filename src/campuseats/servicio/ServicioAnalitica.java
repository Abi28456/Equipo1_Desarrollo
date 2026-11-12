package campuseats.servicio;

import campuseats.fuentes.ConfigFuente;
import campuseats.fuentes.FabricaFuentes;
import campuseats.fuentes.FuenteDatos;
import campuseats.metricas.DatosAnalisis;
import campuseats.metricas.Metrica;
import campuseats.metricas.ResultadoMetrica;
import campuseats.modelo.LineaPedido;
import campuseats.modelo.Pago;
import campuseats.modelo.Plato;
import campuseats.pipeline.CantidadValida;
import campuseats.pipeline.CompletarNombrePlato;
import campuseats.pipeline.PagoAprobadoYMontoIgual;
import campuseats.pipeline.Pipeline;
import campuseats.pipeline.ResultadoPipeline;
import campuseats.pipeline.SinPedidosRepetidos;
import campuseats.pipeline.SoloPagados;
import campuseats.pipeline.Filtro;
import campuseats.repositorio.RepoAnalitica;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * FACADE: la ÚNICA puerta de entrada al módulo. Solo dos métodos públicos:
 *   cargar()  -> lee (Adapter + Factory), limpia (Pipeline) y guarda (Repository)
 *   resumen() -> calcula las métricas (Strategy) sobre los datos guardados
 *
 * Inversión de dependencias: el repositorio y las métricas llegan por el constructor,
 * así que este servicio depende de INTERFACES (RepoAnalitica, Metrica), no de clases concretas.
 */
public class ServicioAnalitica {

    private final RepoAnalitica repo;
    private final List<Metrica> metricas;
    private final Path configFuentes;
    private final FabricaFuentes fabrica;

    public ServicioAnalitica(RepoAnalitica repo, List<Metrica> metricas, Path raizProyecto) {
        this.repo = repo;
        this.metricas = List.copyOf(metricas);
        this.configFuentes = raizProyecto.resolve("config/fuentes.json");
        this.fabrica = new FabricaFuentes(raizProyecto);
    }

    public InformeCarga cargar() throws IOException {
        // 1) Leer: la fábrica crea el adaptador correcto para cada fuente de fuentes.json
        List<Object> leidos = new ArrayList<>();
        for (ConfigFuente config : ConfigFuente.leerTodas(configFuentes)) {
            FuenteDatos<?> fuente = fabrica.crear(config);
            leidos.addAll(fuente.leer());
        }
        List<LineaPedido> lineas = deTipo(leidos, LineaPedido.class);
        List<Pago> pagos = deTipo(leidos, Pago.class);
        List<Plato> platos = deTipo(leidos, Plato.class);

        // 2) Limpiar: el orden importa -> primero se descarta, al final se completa
        Pipeline pipeline = new Pipeline(List.<Filtro>of(
                new SinPedidosRepetidos(),
                new CantidadValida(),
                new SoloPagados(),
                new PagoAprobadoYMontoIgual(pagos),
                new CompletarNombrePlato(platos)));
        ResultadoPipeline limpio = pipeline.ejecutar(lineas);

        // 3) Guardar
        repo.guardarLineas(limpio.lineas());
        repo.guardarPagos(pagos);

        int pedidos = (int) limpio.lineas().stream().map(LineaPedido::numeroPedido).distinct().count();
        return new InformeCarga(lineas.size(), pagos.size(), platos.size(),
                limpio.informes(), limpio.lineas().size(), pedidos);
    }

    public List<ResultadoMetrica> resumen(LocalDate desde, LocalDate hasta) {
        DatosAnalisis datos = new DatosAnalisis(repo.lineasEntre(desde, hasta), repo.pagosEntre(desde, hasta));
        List<ResultadoMetrica> resultados = new ArrayList<>();
        for (Metrica metrica : metricas) resultados.add(metrica.calcular(datos));
        return resultados;
    }

    private static <T> List<T> deTipo(List<Object> datos, Class<T> tipo) {
        return datos.stream().filter(tipo::isInstance).map(tipo::cast).toList();
    }
}
