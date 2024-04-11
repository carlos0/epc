package bo.gob.ine.naci.epc.entidades;

import android.database.SQLException;

/**
 * Created by INE.
 */
//EN DB
public class EntidadId extends Entidad {
    public EntidadId(String nombreTabla) {
        super(nombreTabla);
    }

    public boolean abrir(int id) {
        return abrir(fields[0] + " = " + id, null);
    }

    public int guardar() {
        if (filaNueva == null) {
            return 0;
        } else {
            if (filaActual == null) {
                conn.beginTransaction();
                int id = 0;
                try {
                    id = (int)conn.insertOrThrow(nombreTabla, null, filaNueva);
                    conn.setTransactionSuccessful();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                } finally {
                    conn.endTransaction();
                }
                filaNueva = null;
                return id;
            } else {
                conn.beginTransaction();
                int id = 0;
                try {
                    id = filaActual.getInt(filaActual.getColumnIndex(fields[0]));
                    String where = fields[0] + " = " + id;
                    conn.update(nombreTabla, filaNueva, where, null);
                    conn.setTransactionSuccessful();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                } finally {
                    conn.endTransaction();
                }
                filaActual.close();
                filaActual = null;
                filaNueva = null;
                return id;
            }
        }
    }
}
