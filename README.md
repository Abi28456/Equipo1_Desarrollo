# Equipo1_Desarrollo

## CampusEats 

### Qué hace

1. Lee las fuentes definidas en `config/fuentes.json`.
2. Limpia las líneas de pedido: elimina duplicados, cantidades no válidas, pedidos sin pago y pedidos cuyo pago fue rechazado o tiene un monto distinto; completa nombres de platos cuando corresponde.
3. Calcula estadísticas para el periodo definido en `src/campuseats/Main.java` (actualmente, del 19 al 30 de octubre de 2026):
   - Platos más vendidos por unidades.
   - Pedidos agrupados por hora y hora pico.
   - Ticket promedio por pedido.
   - Tiempo promedio de preparación, separado entre las 12:00–13:59 y el resto del día.
   - Porcentaje de pagos rechazados.
4. Imprime el informe en la terminal.

`resultados/consola.txt` contiene una muestra de salida; al ejecutar el programa, el informe se muestra en la terminal.

### Fuentes de datos

Las rutas de entrada se configuran en `config/fuentes.json`:

| Tipo | Archivo predeterminado | Contenido |
|---|---|---|
| `app_json` | `datos/pedidos_app.json` | Líneas de pedido |
| `pasarela_csv` | `datos/pagos_pasarela.csv` | Transacciones de pago |
| `menu_csv` | `datos/menu_admin.csv` | Platos del menú |

Las rutas se interpretan desde la carpeta raíz del proyecto. Para usar otros archivos, actualiza este JSON conservando los tipos de fuente admitidos.

### Estructura del proyecto

```text
config/                 Configuración de las fuentes
datos/                  Archivos de pedidos, pagos y menú
docs/                   Diagrama de clases
resultados/             Muestra de salida
src/campuseats/
  fuentes/              Lectura de datos y adaptadores
  modelo/               Entidades del dominio
  pipeline/             Filtros de limpieza
  repositorio/          Almacenamiento en memoria
  servicio/             Coordinación de carga y análisis
  metricas/             Métricas del informe
ejecutar.bat            Compilar y ejecutar en Windows
ejecutar.sh             Compilar y ejecutar en Linux/macOS
```

### Diseño

El proyecto utiliza los patrones **Adapter** y **Factory** para leer distintas fuentes, **Pipeline** para limpiar los datos, **Strategy** para calcular métricas, **Repository** para acceder a los datos y **Facade** para coordinar el análisis.
