package bo.gob.ine.naci.epc.entidades;

import android.database.Cursor;

/**
 * Created by INE.
 */
//EN CSV
public class Proyecto extends EntidadId {
    public Proyecto() {
        super("seg_proyecto");
    }

    public static String getCodigoDesbloqueo() {
        String res = null;

        String query = "SELECT codigo_desbloqueo\n" +
                "FROM seg_proyecto";
        Cursor cursor = conn.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            res = cursor.getString(0);
        }
        cursor.close();
        return res;
    }

    public static String getBoletaVersion() {
        String ver = "";

        String query = "SELECT version_boleta\n" +
                "FROM seg_proyecto";
        Cursor cursor = conn.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            ver = cursor.getString(0);
        }
        cursor.close();
        return ver;
    }

    @SuppressWarnings("unused")
    public Integer get_id_proyecto() {
        return filaActual.getInt(filaActual.getColumnIndex("id_proyecto"));
    }
    @SuppressWarnings("unused")
    public void set_id_proyecto(Integer value) {
        filaNueva.put("id_proyecto", value);
    }
    
    @SuppressWarnings("unused")
    public String get_nombre() {
        return filaActual.getString(filaActual.getColumnIndex("nombre"));
    }
    @SuppressWarnings("unused")
    public void set_nombre(String value) {
        filaNueva.put("nombre", value);
    }
    
    @SuppressWarnings("unused")
    public String get_codigo() {
        return filaActual.getString(filaActual.getColumnIndex("codigo"));
    }
    @SuppressWarnings("unused")
    public void set_codigo(String value) {
        filaNueva.put("codigo", value);
    }
    
    @SuppressWarnings("unused")
    public String get_descripcion() {
        return filaActual.getString(filaActual.getColumnIndex("descripcion"));
    }
    @SuppressWarnings("unused")
    public void set_descripcion(String value) {
        filaNueva.put("descripcion", value);
    }
    
    @SuppressWarnings("unused")
    public Long get_fecinicio() {
        return filaActual.getLong(filaActual.getColumnIndex("fecinicio"));
    }
    @SuppressWarnings("unused")
    public void set_fecinicio(Long value) {
        filaNueva.put("fecinicio", value);
    }
    @SuppressWarnings("unused")
    public Long get_fecfin() {
        return filaActual.getLong(filaActual.getColumnIndex("fecfin"));
    }
    
    @SuppressWarnings("unused")
    public void set_fecfin(Long value) {
        filaNueva.put("fecfin", value);
    }
    @SuppressWarnings("unused")
    public String get_apiestado() {
        return filaActual.getString(filaActual.getColumnIndex("estado"));
    }
    
    @SuppressWarnings("unused")
    public void set_apiestado(String value) {
        filaNueva.put("estado", value);
    }
    @SuppressWarnings("unused")
    public String get_usucre() {
        return filaActual.getString(filaActual.getColumnIndex("usucre"));
    }
    @SuppressWarnings("unused")
    public void set_usucre(String value) {
        filaNueva.put("usucre", value);
    }
    
    @SuppressWarnings("unused")
    public Long get_feccre() {
        return filaActual.getLong(filaActual.getColumnIndex("feccre"));
    }
    @SuppressWarnings("unused")
    public void set_feccre(Long value) {
        filaNueva.put("feccre", value);
    }
    
    @SuppressWarnings("unused")
    public String get_usumod() {
        return filaActual.getString(filaActual.getColumnIndex("usumod"));
    }
    @SuppressWarnings("unused")
    public void set_usumod(String value) {
        filaNueva.put("usumod", value);
    }
    
    @SuppressWarnings("unused")
    public Long get_fecmod() {
        return filaActual.getLong(filaActual.getColumnIndex("fecmod"));
    }
    @SuppressWarnings("unused")
    public void set_fecmod(Long value) {
        filaNueva.put("fecmod", value);
    }
    
    @SuppressWarnings("unused")
    public String get_color_web() {
        return filaActual.getString(filaActual.getColumnIndex("color_web"));
    }
    @SuppressWarnings("unused")
    public void set_color_web(String value) {
        filaNueva.put("color_web", value);
    }
    
    @SuppressWarnings("unused")
    public String get_color_movil() {
        return filaActual.getString(filaActual.getColumnIndex("color_movil"));
    }
    @SuppressWarnings("unused")
    public void set_color_movil(String value) {
        filaNueva.put("color_movil", value);
    }
    
    @SuppressWarnings("unused")
    public String get_color_font() {
        return filaActual.getString(filaActual.getColumnIndex("color_font"));
    }
    @SuppressWarnings("unused")
    public void set_color_font(String value) {
        filaNueva.put("color_font", value);
    }

    @SuppressWarnings("unused")
    public String get_color() {
        return filaActual.getString(filaActual.getColumnIndex("color"));
    }
    @SuppressWarnings("unused")
    public void set_color(String value) {
        filaNueva.put("color", value);
    }

    @SuppressWarnings("unused")
    public String get_codigo_desbloqueo() {
        return filaActual.getString(filaActual.getColumnIndex("codigo_desbloqueo"));
    }
    @SuppressWarnings("unused")
    public void set_codigo_desbloqueo(String value) {
        filaNueva.put("codigo_desbloqueo", value);
    }

    @SuppressWarnings("unused")
    public String get_version_boleta() {
        return filaActual.getString(filaActual.getColumnIndex("version_boleta"));
    }
    @SuppressWarnings("unused")
    public void set_version_boleta(String value) {
        filaNueva.put("version_boleta", value);
    }
}
