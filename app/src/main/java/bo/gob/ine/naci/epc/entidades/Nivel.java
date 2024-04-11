package bo.gob.ine.naci.epc.entidades;

import android.database.Cursor;

//EN CSV
public class Nivel extends EntidadId {
    public Nivel() {
        super("enc_nivel");
    }

    public String tipo_nivel(Integer idSeccion){
        String res = "";
        String query = "SELECT tipo\n" +
                "FROM enc_nivel en, enc_seccion es\n" +
                "WHERE en.id_nivel = es.id_nivel\n" +
                "AND es.id_seccion = " + idSeccion + "\n" +
                "ORDER BY codigo\n" +
                "LIMIT 1";

        Cursor cursor = conn.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                res = cursor.getString(0);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return res;
    }

    public int nivel_inicial(){
        int res = 0;
        String query = "SELECT nivel\n" +
                "FROM enc_nivel\n" +
                "WHERE nivel > 1\n" +
                "ORDER BY nivel\n" +
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

    public int idNivel_inicial(int nivel){
        int res = 0;
        String query = "SELECT id_nivel\n" +
                "FROM enc_nivel\n" +
                "WHERE nivel = " + nivel + "\n" +
                "AND id_nivel_padre = 0\n" +
                "ORDER BY id_nivel\n" +
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

    public int getIdNivel(Integer idPregunta){
        int res = 0;
        String query = "SELECT id_nivel\n" +
                "FROM enc_seccion s, enc_pregunta p\n" +
                "WHERE s.id_seccion = p.id_seccion\n" +
                "AND p.id_pregunta = " + idPregunta + "\n" +
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
    public Integer get_id_nivel() {
        return filaActual.getInt(filaActual.getColumnIndex("id_nivel"));
    }
    @SuppressWarnings("unused")
    public void set_id_nivel(Integer value) {
        filaNueva.put("id_nivel", value);
    }

    @SuppressWarnings("unused")
    public Integer get_id_nivel_padre() {
        return filaActual.getInt(filaActual.getColumnIndex("id_nivel_padre"));
    }
    @SuppressWarnings("unused")
    public void set_id_nivel_padre(Integer value) {
        filaNueva.put("id_nivel_padre", value);
    }

    @SuppressWarnings("unused")
    public Integer get_nivel() {
        return filaActual.getInt(filaActual.getColumnIndex("nivel"));
    }
    @SuppressWarnings("unused")
    public void set_nivel(Integer value) {
        filaNueva.put("nivel", value);
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
    public String get_tipo() {
        return filaActual.getString(filaActual.getColumnIndex("tipo"));
    }
    @SuppressWarnings("unused")
    public void set_tipo(String value) {
        filaNueva.put("tipo", value);
    }

    @SuppressWarnings("unused")
    public Integer get_id_rol() {
        return filaActual.getInt(filaActual.getColumnIndex("id_rol"));
    }
    @SuppressWarnings("unused")
    public void set_id_rol(Integer value) {
        filaNueva.put("id_rol", value);
    }


}
