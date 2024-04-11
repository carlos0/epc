package bo.gob.ine.naci.epc.entidades;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;

/**
 * Created by INE.
 */
public class EntidadCorr extends Entidad {
    public EntidadCorr(String nombreTabla) {
        super(nombreTabla);
    }

    public boolean abrir(Identificador id) {
        return abrir(id.where(), null);
    }

    protected Identificador getId(Cursor filaActual) {
        return null;
    }

    protected Identificador getId(ContentValues filaNueva) {
        return null;
    }

    public Identificador  guardar() {
        if (filaNueva == null) {
            return null;
        } else {
            if (filaActual == null) {
                conn.beginTransaction();
                Identificador id = null;
                try {
                    id = getId(filaNueva);
//                    conn.insertOrThrow(nombreTabla, null, filaNueva);
                    Long a = conn.insertOrThrow(nombreTabla, null, filaNueva);
                    Long e = a;
                    conn.setTransactionSuccessful();
                } catch (SQLException ex) {
                    id = null;
                    ex.printStackTrace();
                } finally {
                    conn.endTransaction();
                }
                filaNueva = null;
                return id;
            } else {
                conn.beginTransaction();
                Identificador id = null;
                try {
                    id = getId(filaActual);
                    conn.update(nombreTabla, filaNueva, id.where(), null);
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

    public Identificador guardar2() {
        if (filaNueva == null) {
            return null;
        } else {
            if (filaActual == null) {
                conn.beginTransaction();
                Identificador id = null;
                try {
                    id = getId(filaNueva);
                    conn.insertOrThrow(nombreTabla, null, filaNueva);
                    conn.setTransactionSuccessful();
                } catch (SQLException ex) {
                    id = null;
                    ex.printStackTrace();
                } finally {
                    conn.endTransaction();
                }
                filaNueva = null;
                return id;
            } else {
                conn.beginTransaction();
                Identificador id = null;
                try {
                    id = getId(filaActual);
                    conn.update(nombreTabla, filaNueva, id.where(), null);
                    conn.setTransactionSuccessful();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                } finally {
                    conn.endTransaction();
                }
                filaNueva = null;
                return id;
            }
        }
    }
}