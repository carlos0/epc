package bo.gob.ine.naci.epc.entidades;

import android.database.Cursor;

/**
 * Created by INE.
 */
//EN CSV
public class RolPermiso extends Entidad {

    public RolPermiso() {
        super("seg_rolpermiso");
    }

    public static boolean tienePermiso(int idRol, String permiso) {
        String query = "SELECT *\n" +
                "FROM seg_rolpermiso\n" +
                "WHERE id_rol = " + idRol + "\n" +
                "AND descripcion = '" + permiso + "'";
        Cursor cursor = conn.rawQuery(query, null);
        boolean res = cursor.getCount() == 1;
        cursor.close();
        return res;
    }

    public static boolean usuarioTienePermiso(int idUsuario, String permiso) {
        String query = "SELECT *\n" +
                "FROM seg_rolpermiso p, seg_usuariorestriccion r\n" +
                "WHERE p.id_rol = r.id_rol\n" +
                "AND r.id_usuario = " + idUsuario + "\n" +
                "AND p.descripcion = '" + permiso + "'";
        Cursor cursor = conn.rawQuery(query, null);
        boolean res = cursor.getCount() == 1;
        cursor.close();
        return res;
    }
}
