package bo.gob.ine.naci.epc.entidades;

import android.database.Cursor;

/**
 * Created by INE.
 */
//EN CSV
public class Brigada extends Entidad {

    public Brigada() {
        super("seg_rolpermiso");
    }

    public static String getCodigoBrigada(int idBrigada) {
        String res = null;

        String query = "SELECT codigo_brigada\n" +
                "FROM ope_brigada\n" +
                "WHERE id_brigada = " + idBrigada + "\n" +
                "AND estado = 'ELABORADO'";
        Cursor cursor = conn.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            res = cursor.getString(0);
        }
        cursor.close();
        return res;
    }
}
