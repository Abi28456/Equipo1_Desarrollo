# Equipo1_Desarrollo

## CampusEats · Módulo de analítica

Programa en Java (JDK 17+, sin librerías externas) que lee, limpia y analiza los pedidos de la cafetería.
Patrones: **Adapter, Factory, Pipeline, Strategy, Repository y Facade**.

### Ejecutar
- Linux/macOS: `./ejecutar.sh`
- Windows: `ejecutar.bat`

Manual: `javac -encoding UTF-8 -d out $(find src -name "*.java")` y `java -cp out campuseats.Main .`

### Carpetas
- `config/fuentes.json` – qué archivos leer
- `datos/` – pedidos_app.json, pagos_pasarela.csv, menu_admin.csv
- `src/campuseats/` – código (modelo, fuentes, pipeline, metricas, repositorio, servicio)
- `docs/` – diagrama de clases UML
- `resultados/` – salida de consola
