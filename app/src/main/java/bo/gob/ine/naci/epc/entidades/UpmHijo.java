package bo.gob.ine.naci.epc.entidades;

import android.database.Cursor;
import android.util.Log;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by INE.
 */
public class UpmHijo extends Entidad {

    public UpmHijo() {
        super("cat_upm_hijo");
    }

    public static Map<Integer, String> getRespuestasUPM(int idUpm) {
        Map<Integer, String> res = new LinkedHashMap<>();

        Cursor cursor = null;
        String strQuery = "SELECT id_upm, codigo||' (ORIGEN)'  codigo_opcion , codigo, 0 orden, -2 nro_registro\n" +
                "FROM cat_upm\n" +
                "WHERE id_upm = " + idUpm + "\n" +
                "UNION\n" +
                "SELECT CASE WHEN id_upm_hijo=id_upm_padre THEN id_upm_padre*100 ELSE id_upm_hijo END id_upm, codigo|| CASE WHEN id_incidencia=1 THEN ' (ADICIONAL '||nro_registro||')' ELSE ' (REEMPLAZO)' END  codigo_opcion , codigo, 1 orden, CASE WHEN id_incidencia=2 THEN -1 ELSE nro_registro END nro_registro\n" +
                "FROM cat_upm_hijo\n" +
                "WHERE id_upm_padre = " + idUpm + "\n" +
//                "AND (id_incidencia = 1 )\n" +
                "AND estado <> 'ANULADO'\n" +
                "ORDER BY orden, nro_registro";



        cursor = conn.rawQuery(strQuery, null);
        boolean flag = false;
        int key = 0;
        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndex("id_upm"));
                String cod = cursor.getString(cursor.getColumnIndex("codigo"));
                String resp = cursor.getString(cursor.getColumnIndex("codigo_opcion"));
                res.put(id, cod + "|" + resp + "|0");
                if( resp.contains("ORIGEN") ) {
                    key = id;
                }
                if( resp.contains("REEMPLAZO") ) {
                    flag = true;
                }
            } while (cursor.moveToNext());
        }
        if (flag) {
            res.remove(key);
        }
        cursor.close();
        return res;
    }

    public static int getIncidenciaUpmHijo(String codigoUpm) {
        int res = 0;
        String strQuery = "SELECT temp.id_incidencia\n" +
                        "FROM (SELECT 0 id_incidencia\n" +
                        "FROM cat_upm\n" +
                        "WHERE codigo LIKE '"+codigoUpm+"'\n" +
                        "UNION\n" +
                        "SELECT id_incidencia\n" +
                        "FROM cat_upm_hijo\n" +
                        "WHERE codigo LIKE '"+codigoUpm+"') AS temp\n" +
                        "LIMIT 1";
        Cursor cursor = conn.rawQuery(strQuery, null);
        if (cursor.moveToNext()) {
            res = cursor.getInt(0);
        }
        cursor.close();
        return res;
    }

    public static Map<Integer, String> getRespuestasManzano(int idUpm) {
        Map<Integer, String> res = new LinkedHashMap<>();

        String strQuery = "SELECT id_upm_hijo, codigo\n" +
                "FROM cat_upm_hijo\n" +
                "WHERE id_upm_padre = " + idUpm + "\n" +
                "AND id_incidencia = 3\n" +
                "ORDER BY id_upm_hijo";

        Cursor cursor = conn.rawQuery(strQuery, null);
        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndex("id_upm_hijo"));
                String cod = cursor.getString(cursor.getColumnIndex("codigo"));
                String resp = cursor.getString(cursor.getColumnIndex("codigo"));
                res.put(id, cod + "|" + resp + "|0");
            } while (cursor.moveToNext());
        }
        cursor.close();
        return res;
    }
    public static int getIdUpmHijo(String codigoUpm) {
        int res = 0;
        String strQuery =
                "SELECT id_upm_hijo\n" +
                "FROM cat_upm_hijo\n" +
                "WHERE codigo LIKE '"+codigoUpm+"'\n" +
                "LIMIT 1";
        Cursor cursor = conn.rawQuery(strQuery, null);
        if (cursor.moveToNext()) {
            res = cursor.getInt(0);
        }
        cursor.close();
        return res;
    }

    public static Map<String, String> getNombreUpm(String codigoUpm) {
        Map<String, String> res = new HashMap<>();
        String strQuery =
                "SELECT id_upm_hijo, nombre\n" +
                        "FROM cat_upm_hijo\n" +
                        "WHERE codigo LIKE '"+codigoUpm+"'\n" +
                        "UNION \n" +
                        "SELECT id_upm, nombre \n" +
                        "FROM cat_upm \n" +
                        "WHERE codigo LIKE '"+codigoUpm+"'\n" +
                        "LIMIT 1";
        Cursor cursor = conn.rawQuery(strQuery, null);
        if (cursor.moveToNext()) {
            res.put("id_upm", cursor.getString(0));
            res.put("nombre", cursor.getString(1));
        }
        cursor.close();
        return res;
    }

    public static void eliminaViviendasPorReemplazo(){
        String strQuery = "SELECT i.id_asignacion, i.correlativo\n" +
                "FROM enc_informante i \n" +
                "JOIN enc_encuesta e \n" +
                "ON i.id_asignacion = e.id_asignacion AND i.correlativo = e.correlativo\n" +
                "WHERE codigo_respuesta IN (\n" +
                "SELECT codigo FROM cat_upm_hijo WHERE estado = 'ANULADO') AND id_pregunta = 18598\n" +
                "AND i.estado = 'ELABORADO'\n" +
                "UNION\n" +
                "SELECT i.id_asignacion, i.correlativo FROM enc_informante i \n" +
                "JOIN enc_encuesta e \n" +
                "ON i.id_asignacion = e.id_asignacion AND i.correlativo = e.correlativo\n" +
                "JOIN cat_upm u \n" +
                "ON u.id_upm = i.id_upm \n" +
                "JOIN cat_upm_hijo uh \n" +
                "ON i.id_upm = uh.id_upm_padre\n" +
                "WHERE id_incidencia = 2 AND id_pregunta = 18598 AND u.codigo = e.codigo_respuesta\n" +
//                        "AND i.estado = 'ELABORADO'\n" +
                "AND i.estado != 'ANULADO'\n" +

                //TODO: ELIMINA TODOS LOS ESTADOS SI ESTA REEMPLAZADO O SELECCIONADO

                "UNION\n" +
                "SELECT i.id_asignacion, i.correlativo FROM enc_informante i \n" +
                "JOIN enc_encuesta e \n" +
                "ON i.id_asignacion = e.id_asignacion AND i.correlativo = e.correlativo\n" +
                "WHERE id_pregunta= 18598 and codigo_respuesta NOT IN (\n" +
                "SELECT codigo FROM cat_upm_hijo WHERE estado != 'ANULADO'\n" +
                "UNION\n" +
                "SELECT codigo FROM cat_upm) AND i.estado != 'ANULADO'";

        Log.d("ELIMINACION", strQuery);

        Cursor cursor = conn.rawQuery(strQuery, null);

        if (cursor.moveToNext()) {
            do {
                String query = "UPDATE enc_informante SET estado = 'ANULADO' WHERE id_asignacion = " + cursor.getInt(0) + " AND correlativo = " + cursor.getInt(1);
                conn.beginTransaction();
                try {
                    conn.execSQL(query);
                    conn.setTransactionSuccessful();
                } finally {
                    conn.endTransaction();
                }
            }while (cursor.moveToNext());
        }
        cursor.close();

    }
}
