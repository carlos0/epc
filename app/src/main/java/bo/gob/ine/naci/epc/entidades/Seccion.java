package bo.gob.ine.naci.epc.entidades;

import android.database.Cursor;

/**
 * Created by INE.
 */
//EN CSV
public class Seccion extends EntidadId {
    public Seccion() {
        super("enc_seccion");
    }

    public int getPrimeraPregunta(Integer idSeccion) {
        int res = -1;
        String query = "SELECT id_pregunta\n" +
                "FROM enc_pregunta\n" +
                "WHERE id_seccion = " + idSeccion + "\n" +
                "ORDER BY codigo_pregunta\n" +
                "LIMIT 1";

        Cursor cursor = conn.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                res = cursor.getInt(0);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return res;
    }

    public int seccion_inicial(Integer idNivel){
        int res = 0;
        String query = "SELECT id_seccion\n" +
                "FROM enc_seccion\n" +
                "WHERE id_nivel = " + idNivel + "\n" +
                "AND id_seccion in ( 168,169,181,201,431)  "+ "\n" +
                "ORDER BY codigo\n" +
                "LIMIT 1";
        Cursor cursor = conn.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                res = cursor.getInt(0);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return res;
    }

    public int nuevaSeccion(Integer idPregunta){
        int res = 0;
        String query = "SELECT id_seccion\n" +
                "FROM enc_pregunta\n" +
                "WHERE id_pregunta = " + idPregunta + "\n" +
                "LIMIT 1";
        Cursor cursor = conn.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                res = cursor.getInt(0);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return res;
    }

    @SuppressWarnings("unused")
    public Integer get_id_seccion() {
        return filaActual.getInt(filaActual.getColumnIndex("id_seccion"));
    }
    @SuppressWarnings("unused")
    public void set_id_seccion(Integer value) {
        filaNueva.put("id_seccion", value);
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
    public Integer get_id_nivel() {
        return filaActual.getInt(filaActual.getColumnIndex("id_nivel"));
    }
    @SuppressWarnings("unused")
    public void set_id_nivel(Integer value) {
        filaNueva.put("id_nivel", value);
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
    public String get_seccion() {
        return filaActual.getString(filaActual.getColumnIndex("seccion"));
    }
    @SuppressWarnings("unused")
    public void set_seccion(String value) {
        filaNueva.put("seccion", value);
    }

    @SuppressWarnings("unused")
    public Integer get_abierta() {
        return filaActual.getInt(filaActual.getColumnIndex("abierta"));
    }
    @SuppressWarnings("unused")
    public void set_abierta(Integer value) {
        filaNueva.put("abierta", value);
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
}
