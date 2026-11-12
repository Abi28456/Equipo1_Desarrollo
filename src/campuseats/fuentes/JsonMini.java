package campuseats.fuentes;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Lector JSON mínimo (sin librerías externas) para que el proyecto compile con solo el JDK.
 * Si prefieren Jackson, solo hay que cambiar el interior de AppPedidosJsonAdapter y ConfigFuente.
 */
final class JsonMini {

    private final String s;
    private int i;

    private JsonMini(String s) { this.s = s; }

    static Object parsear(String texto) {
        JsonMini p = new JsonMini(texto.replace("\uFEFF", ""));
        Object v = p.valor();
        p.espacios();
        if (p.i != p.s.length()) throw p.error("contenido extra");
        return v;
    }

    // ---- accesos tipados ----
    @SuppressWarnings("unchecked")
    static Map<String, Object> objeto(Object o) { return (Map<String, Object>) o; }

    @SuppressWarnings("unchecked")
    static List<Object> lista(Object o) { return (List<Object>) o; }

    static String texto(Object o) { return (String) o; }

    static double numero(Object o) { return ((Number) o).doubleValue(); }

    // ---- análisis ----
    private Object valor() {
        espacios();
        if (i >= s.length()) throw error("fin inesperado");
        char c = s.charAt(i);
        return switch (c) {
            case '{' -> obj();
            case '[' -> arreglo();
            case '"' -> cadena();
            case 't' -> { literal("true"); yield Boolean.TRUE; }
            case 'f' -> { literal("false"); yield Boolean.FALSE; }
            case 'n' -> { literal("null"); yield null; }
            default -> num();
        };
    }

    private Map<String, Object> obj() {
        Map<String, Object> m = new LinkedHashMap<>();
        i++; espacios();
        if (s.charAt(i) == '}') { i++; return m; }
        while (true) {
            espacios();
            String clave = cadena();
            espacios(); esperar(':');
            m.put(clave, valor());
            espacios();
            if (s.charAt(i) == ',') { i++; continue; }
            esperar('}');
            return m;
        }
    }

    private List<Object> arreglo() {
        List<Object> l = new ArrayList<>();
        i++; espacios();
        if (s.charAt(i) == ']') { i++; return l; }
        while (true) {
            l.add(valor());
            espacios();
            if (s.charAt(i) == ',') { i++; continue; }
            esperar(']');
            return l;
        }
    }

    private String cadena() {
        esperar('"');
        StringBuilder sb = new StringBuilder();
        while (s.charAt(i) != '"') {
            char c = s.charAt(i++);
            if (c != '\\') { sb.append(c); continue; }
            char e = s.charAt(i++);
            switch (e) {
                case 'n' -> sb.append('\n');
                case 't' -> sb.append('\t');
                case 'r' -> sb.append('\r');
                case 'b' -> sb.append('\b');
                case 'f' -> sb.append('\f');
                case 'u' -> { sb.append((char) Integer.parseInt(s.substring(i, i + 4), 16)); i += 4; }
                default -> sb.append(e);                       // \" \\ \/
            }
        }
        i++;
        return sb.toString();
    }

    private Number num() {
        int ini = i;
        while (i < s.length() && "+-0123456789.eE".indexOf(s.charAt(i)) >= 0) i++;
        if (ini == i) throw error("valor no reconocido");
        String t = s.substring(ini, i);
        return (t.contains(".") || t.contains("e") || t.contains("E")) ? (Number) Double.valueOf(t) : (Number) Long.valueOf(t);
    }

    private void literal(String l) {
        if (!s.startsWith(l, i)) throw error("se esperaba " + l);
        i += l.length();
    }

    private void esperar(char c) {
        if (i >= s.length() || s.charAt(i) != c) throw error("se esperaba '" + c + "'");
        i++;
    }

    private void espacios() {
        while (i < s.length() && Character.isWhitespace(s.charAt(i))) i++;
    }

    private IllegalArgumentException error(String msg) {
        return new IllegalArgumentException("JSON inválido (posición " + i + "): " + msg);
    }
}
