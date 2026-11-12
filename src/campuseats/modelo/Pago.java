package campuseats.modelo;

import java.time.LocalDateTime;

/** Un intento de pago registrado por la pasarela. */
public record Pago(String referencia, double monto, EstadoPago estado,
                   String motivoRechazo, LocalDateTime fechaHora) { }
