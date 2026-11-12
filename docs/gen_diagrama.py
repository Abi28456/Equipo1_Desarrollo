#!/usr/bin/env python3
"""Genera diagrama_clases.dot / .png (Graphviz) del módulo CampusEats."""
import subprocess, html

FONT = "Liberation Sans"

def clase(nombre, attrs=(), ops=(), estereotipo=None, abstracta=False, fill="#FFFFFF"):
    """Nodo UML con 3 compartimentos (nombre / atributos / operaciones)."""
    cab = ""
    if estereotipo:
        cab = f'<FONT POINT-SIZE="9">«{html.escape(estereotipo)}»</FONT><BR/>'
    n = html.escape(nombre)
    n = f"<I>{n}</I>" if abstracta else n
    filas = [f'<TR><TD ALIGN="CENTER" BGCOLOR="{fill}">{cab}<B>{n}</B></TD></TR>']
    if attrs:
        a = "<BR ALIGN=\"LEFT\"/>".join(html.escape(x) for x in attrs) + "<BR ALIGN=\"LEFT\"/>"
        filas.append(f'<TR><TD ALIGN="LEFT" BGCOLOR="white"><FONT POINT-SIZE="9">{a}</FONT></TD></TR>')
    if ops:
        o = "<BR ALIGN=\"LEFT\"/>".join(html.escape(x) for x in ops) + "<BR ALIGN=\"LEFT\"/>"
        filas.append(f'<TR><TD ALIGN="LEFT" BGCOLOR="white"><FONT POINT-SIZE="9">{o}</FONT></TD></TR>')
    return ('<<TABLE BORDER="1" CELLBORDER="0" CELLSPACING="0" CELLPADDING="4" COLOR="#34495E">'
            + "".join(filas) + "</TABLE>>")

out = []
def nodo(id_, **kw):
    out.append(f'  {id_} [shape=plain, label={clase(**kw)}];')

def nota(id_, texto, color):
    t = html.escape(texto).replace("\n", '<BR ALIGN="LEFT"/>') + '<BR ALIGN="LEFT"/>'
    out.append(f'  {id_} [shape=note, style=filled, fillcolor="{color}", color="#7F8C8D", '
               f'fontname="{FONT}", fontsize=10, label=<{t}>];')

CL = {"ad": "#DCEBF8", "pi": "#DDF2DD", "st": "#FDE9CF", "re": "#EBDDF3", "fa": "#FFF6C2", "mo": "#EEEEEE"}

out.append('digraph G {')
out.append(f'  graph [rankdir=LR, fontname="{FONT}", fontsize=12, nodesep=0.14, ranksep=0.75, compound=true, pad=0.2, splines=polyline];')
out.append(f'  node [fontname="{FONT}", fontsize=10];')
out.append(f'  edge [fontname="{FONT}", fontsize=9, color="#34495E"];')

# ---------- Main + FACADE ----------
nodo("Main", nombre="Main", ops=["+main(String[]): void"], fill="#FFFFFF")
out.append('  subgraph cluster_fa { label="Patrón FACADE"; style="filled,rounded"; fillcolor="%s"; color="#B7950B"; fontsize=12; fontname="%s";' % (CL["fa"], FONT))
nodo("Servicio", nombre="ServicioAnalitica",
     attrs=["-repo: RepoAnalitica", "-metricas: List<Metrica>", "-fabrica: FabricaFuentes"],
     ops=["+cargar(): InformeCarga", "+resumen(desde, hasta): List<ResultadoMetrica>"], fill="#FFF0A0")
nota("nFa", "FACADE: única puerta de entrada.\nSolo 2 métodos públicos; repo y métricas\nse inyectan por el constructor (DIP).", "#FFFBDF")
out.append('  {rank=same; nFa; Servicio;}')
out.append('  }')

# ---------- ADAPTER + FACTORY ----------
out.append('  subgraph cluster_ad { label="Patrones ADAPTER + FACTORY  (paquete fuentes)"; style="filled,rounded"; fillcolor="%s"; color="#2E86C1"; fontsize=12; fontname="%s";' % (CL["ad"], FONT))
nodo("Fabrica", nombre="FabricaFuentes",
     attrs=["-registro: Map<String, Function<Path, FuenteDatos<?>>>"],
     ops=["+registrar(tipo, constructor): void", "+crear(ConfigFuente): FuenteDatos<?>"], fill="#BFD9F0")
nodo("Config", nombre="ConfigFuente", estereotipo="record", attrs=["tipo: String", "ruta: String"], fill="#BFD9F0")
nodo("FuenteDatos", nombre="FuenteDatos<T>", estereotipo="interface", ops=["+leer(): List<T>"], abstracta=True, fill="#9CC4E8")
nodo("AdApp", nombre="AppPedidosJsonAdapter", ops=["+leer(): List<LineaPedido>"], fill="#E8F2FB")
nodo("AdPas", nombre="PasarelaCsvAdapter", ops=["+leer(): List<Pago>"], fill="#E8F2FB")
nodo("AdMenu", nombre="MenuCsvAdapter", ops=["+leer(): List<Plato>"], fill="#E8F2FB")
nota("nAd", "ADAPTER: cada archivo (JSON, CSV con ;, CSV con ,)\nse traduce al modelo (LineaPedido, Pago, Plato)\ndetrás de FuenteDatos<T>.\nFACTORY: FabricaFuentes elige el adaptador\nsegún \"tipo\" de fuentes.json, sin if/else.", "#F3F9FE")
out.append('  {rank=same; nAd; Fabrica;}')
out.append('  }')

# ---------- PIPELINE ----------
out.append('  subgraph cluster_pi { label="Patrón PIPELINE (Pipes & Filters)  (paquete pipeline)"; style="filled,rounded"; fillcolor="%s"; color="#27AE60"; fontsize=12; fontname="%s";' % (CL["pi"], FONT))
nodo("Pipeline", nombre="Pipeline", attrs=["-filtros: List<Filtro>"], ops=["+ejecutar(List<LineaPedido>): ResultadoPipeline"], fill="#BDE5BD")
nodo("Filtro", nombre="Filtro", estereotipo="interface",
     ops=["+nombre(): String", "+aplicar(List<LineaPedido>): List<LineaPedido>"], abstracta=True, fill="#9ED89E")
for i, (n, m) in enumerate([("SinPedidosRepetidos", "regla CU-1.2"), ("CantidadValida", "regla CU-1.4"),
                            ("SoloPagados", "regla CU-2.1"), ("PagoAprobadoYMontoIgual", "regla CU-2.1"),
                            ("CompletarNombrePlato", "enriquece (menú)")]):
    nodo(f"F{i}", nombre=n, fill="#EAF7EA")
nota("nPi", "PIPELINE: cada Filtro aplica UNA regla;\nPipeline los ejecuta en orden e informa\ncuántas líneas descartó cada uno.", "#F4FBF4")
out.append('  {rank=same; nPi; Pipeline;}')
out.append('  }')

# ---------- STRATEGY ----------
out.append('  subgraph cluster_st { label="Patrón STRATEGY  (paquete metricas)"; style="filled,rounded"; fillcolor="%s"; color="#E67E22"; fontsize=12; fontname="%s";' % (CL["st"], FONT))
nodo("Metrica", nombre="Metrica", estereotipo="interface",
     ops=["+nombre(): String", "+calcular(DatosAnalisis): ResultadoMetrica"], abstracta=True, fill="#F9CD94")
for i, n in enumerate(["PlatosTop", "PedidosPorHora", "TicketPromedio", "TiempoPreparacion", "PagosRechazados"]):
    nodo(f"M{i}", nombre=n, fill="#FEF4E6")
nodo("Datos", nombre="DatosAnalisis", estereotipo="record", attrs=["lineas: List<LineaPedido>", "pagos: List<Pago>"], fill="#FBDDB5")
nota("nSt", "STRATEGY: cada Metrica es un algoritmo\nintercambiable. Agregar una métrica\n= crear una clase nueva.", "#FFF8EE")
out.append('  {rank=same; nSt; Metrica;}')
out.append('  }')

# ---------- REPOSITORY ----------
out.append('  subgraph cluster_re { label="Patrón REPOSITORY  (paquete repositorio)"; style="filled,rounded"; fillcolor="%s"; color="#8E44AD"; fontsize=12; fontname="%s";' % (CL["re"], FONT))
nodo("Repo", nombre="RepoAnalitica", estereotipo="interface",
     ops=["+guardarLineas(List<LineaPedido>): void", "+guardarPagos(List<Pago>): void",
          "+lineasEntre(desde, hasta): List<LineaPedido>", "+pagosEntre(desde, hasta): List<Pago>"], abstracta=True, fill="#D5B8E6")
nodo("RepoMem", nombre="RepoAnaliticaMemoria", attrs=["-lineas: List<LineaPedido>", "-pagos: List<Pago>"], ops=["(implementa los 4 métodos)"], fill="#F4EAF9")
nota("nRe", "REPOSITORY: las métricas piden datos\npor rango de fechas sin saber dónde\nse guardan (hoy: memoria).", "#FAF4FD")
out.append('  {rank=same; nRe; Repo;}')
out.append('  }')

# ---------- MODELO ----------
out.append('  subgraph cluster_mo { label="Modelo (records)  (paquete modelo)"; style="filled,rounded"; fillcolor="%s"; color="#7F8C8D"; fontsize=12; fontname="%s";' % (CL["mo"], FONT))
nodo("Linea", nombre="LineaPedido", estereotipo="record",
     attrs=["numeroPedido, platoId, nombrePlato", "cantidad, precioUnitario, totalPedido", "registradoEn, pagadoEn, listoRetiroEn"], fill="#DDDDDD")
nodo("Pago", nombre="Pago", estereotipo="record", attrs=["referencia, monto, estado: EstadoPago", "motivoRechazo, fechaHora"], fill="#DDDDDD")
nodo("Plato", nombre="Plato", estereotipo="record", attrs=["id, nombre, categoria", "precioActual, estado: EstadoPlato"], fill="#DDDDDD")
out.append('  }')

# ---------- RELACIONES ----------
E = out.append
E('  // Main -> Facade')
E('  Main -> Servicio [arrowhead=vee, style=dashed];')
E('  // Facade -> piezas (asociación / dependencia)')
E('  Servicio -> Fabrica [arrowhead=vee, style=dashed, label="usa"];')
E('  Servicio -> Pipeline [arrowhead=vee, style=dashed, label="crea y usa"];')
E('  Servicio -> Metrica [arrowhead=vee, label="metricas  1..*"];')
E('  Servicio -> Repo [arrowhead=vee, label="repo"];')
E('  // Adapter / Factory')
E('  Fabrica -> FuenteDatos [arrowhead=vee, style=dashed, label="crea"];')
E('  Fabrica -> Config [arrowhead=vee, style=dashed];')
for a in ("AdApp", "AdPas", "AdMenu"):
    E(f'  FuenteDatos -> {a} [arrowhead=none, arrowtail=empty, dir=back, style=dashed];')
E('  // Pipeline')
E('  Pipeline -> Filtro [arrowhead=odiamond, dir=back, label="filtros  0..*", arrowtail=odiamond, arrowhead=none];')
for i in range(5):
    E(f'  Filtro -> F{i} [arrowhead=none, arrowtail=empty, dir=back, style=dashed];')
E('  F3 -> Pago [arrowhead=vee, style=dashed, label="usa"];')
E('  F4 -> Plato [arrowhead=vee, style=dashed, label="usa"];')
E('  Filtro -> Linea [arrowhead=vee, style=dashed];')
E('  // Strategy')
for i in range(5):
    E(f'  Metrica -> M{i} [arrowhead=none, arrowtail=empty, dir=back, style=dashed];')
E('  Metrica -> Datos [arrowhead=vee, style=dashed, label="recibe"];')
E('  // Repository')
E('  Repo -> RepoMem [arrowhead=none, arrowtail=empty, dir=back, style=dashed];')
E('  // Notas')
for nn, tgt in (("nFa", "Servicio"), ("nAd", "Fabrica"), ("nPi", "Pipeline"), ("nSt", "Metrica"), ("nRe", "Repo")):
    E(f'  {nn} -> {tgt} [arrowhead=none, style=dotted, color="#7F8C8D", constraint=false];')
E('}')

open("diagrama_clases.dot", "w", encoding="utf-8").write("\n".join(out))
subprocess.run(["dot", "-Tpng", "-Gdpi=170", "diagrama_clases.dot", "-o", "diagrama_clases.png"], check=True)
subprocess.run(["dot", "-Tsvg", "diagrama_clases.dot", "-o", "diagrama_clases.svg"], check=True)
print("OK")
