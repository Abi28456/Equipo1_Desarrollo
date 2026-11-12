package campuseats;

import campuseats.metricas.Metrica;
import campuseats.metricas.PagosRechazados;
import campuseats.metricas.PedidosPorHora;
import campuseats.metricas.PlatosTop;
import campuseats.metricas.ResultadoMetrica;
import campuseats.metricas.TicketPromedio;
import campuseats.metricas.TiempoPreparacion;
import campuseats.pipeline.InformeFiltro;
import campuseats.repositorio.RepoAnalitica;
import campuseats.repositorio.RepoAnaliticaMemoria;
import campuseats.servicio.InformeCarga;
import campuseats.servicio.ServicioAnalitica;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

/** Punto de entrada: arma las piezas y usa SOLO ServicioAnalitica. */
public class Main {

    public static void main(String[] args) throws Exception {
        PrintStream out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
        Path raiz = Path.of(args.length > 0 ? args[0] : ".");

        RepoAnalitica repo = new RepoAnaliticaMemoria();
        List<Metrica> metricas = List.of(
                new PlatosTop(5),
                new PedidosPorHora(),
                new TicketPromedio(),        // propuesta del equipo
                new TiempoPreparacion(),     // extra (demuestra que Strategy es extensible)
                new PagosRechazados());      // extra
        ServicioAnalitica servicio = new ServicioAnalitica(repo, metricas, raiz);

        InformeCarga informe = servicio.cargar();
        out.println("=== CampusEats ====");
        out.println("Leido: " + informe.lineasLeidas() + " lineas de pedido, "
                + informe.pagosLeidos() + " pagos, " + informe.platosLeidos() + " platos");
        out.println();
        out.println("--- Limpieza de los datos ---");
        for (InformeFiltro f : informe.filtros()) {
            out.printf("%-45s %d%n", f.nombre(), f.descartadas());
        }
        out.println("Quedan al final: " + informe.lineasFinales() + " lineas de " + informe.pedidosFinales() + " pedidos");

        LocalDate desde = LocalDate.of(2026, 10, 19), hasta = LocalDate.of(2026, 10, 30);
        out.println();
        out.println("--- Estadisticas (del 19 al 30 de octubre) ---");
        for (ResultadoMetrica r : servicio.resumen(desde, hasta)) {
            out.println("[" + r.nombre() + "]");
            r.lineas().forEach(l -> out.println("  " + l));
        }
    }
}
