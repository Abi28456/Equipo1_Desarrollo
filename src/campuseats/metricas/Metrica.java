package campuseats.metricas;

/**
 * STRATEGY (interfaz): una métrica calcula UNA estadística a partir de los datos ya limpios.
 * Agregar una métrica nueva = crear una clase nueva; el servicio no cambia.
 */
public interface Metrica {
    String nombre();
    ResultadoMetrica calcular(DatosAnalisis datos);
}
