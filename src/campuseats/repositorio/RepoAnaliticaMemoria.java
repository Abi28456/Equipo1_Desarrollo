package campuseats.repositorio;

import campuseats.modelo.LineaPedido;
import campuseats.modelo.Pago;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/** Implementación en memoria: no requiere instalar ninguna base de datos. */
public class RepoAnaliticaMemoria implements RepoAnalitica {

    private final List<LineaPedido> lineas = new ArrayList<>();
    private final List<Pago> pagos = new ArrayList<>();

    @Override public void guardarLineas(List<LineaPedido> nuevas) { lineas.clear(); lineas.addAll(nuevas); }

    @Override public void guardarPagos(List<Pago> nuevos) { pagos.clear(); pagos.addAll(nuevos); }

    @Override
    public List<LineaPedido> lineasEntre(LocalDate desde, LocalDate hasta) {
        return lineas.stream()
                .filter(l -> dentro(l.registradoEn().toLocalDate(), desde, hasta))
                .toList();
    }

    @Override
    public List<Pago> pagosEntre(LocalDate desde, LocalDate hasta) {
        return pagos.stream()
                .filter(p -> dentro(p.fechaHora().toLocalDate(), desde, hasta))
                .toList();
    }

    private static boolean dentro(LocalDate f, LocalDate desde, LocalDate hasta) {
        return !f.isBefore(desde) && !f.isAfter(hasta);
    }
}
