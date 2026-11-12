package campuseats.pipeline;

import campuseats.modelo.LineaPedido;

import java.util.List;

public record ResultadoPipeline(List<LineaPedido> lineas, List<InformeFiltro> informes) { }
