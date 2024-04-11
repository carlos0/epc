package bo.gob.ine.naci.epc.entidades;

import android.database.Cursor;

/**
 * Created by INE.
 */
//EN BD
public class Asignacion extends EntidadId {
    public Asignacion() {
        super("ope_asignacion");
    }

    public static Integer get_asignacion(int idUpm, int idUsuario) {
        Integer res = null;
        String query = "SELECT id_asignacion\n" +
                "FROM ope_asignacion\n" +
                "WHERE id_upm = " + idUpm + "\n" +
                "AND estado LIKE 'ELABORADO'\n" +
                "AND id_usuario = " + idUsuario;

        Cursor cursor = conn.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                res = cursor.getInt(0);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return res;
    }

    public static Integer getUpm(int idAsignacion) {
        Integer res = null;
        String query = "SELECT id_upm\n" +
                "FROM ope_asignacion\n" +
                "WHERE id_asignacion = " + idAsignacion;

        Cursor cursor = conn.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                res = cursor.getInt(0);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return res;
    }

    public static IdInformante getInformanteCabeceraViviendas(int idAsignacion) {
        IdInformante idInformante = null;
        String query = "SELECT id_asignacion, correlativo\n" +
                "FROM enc_informante\n" +
                "WHERE id_asignacion = " + idAsignacion + "\n" +
                "AND id_nivel = 1\n" +
                "AND estado <> 'ANULADO'\n" +
                "ORDER BY id_asignacion, correlativo\n" +
                "LIMIT 1";

        Cursor cursor = conn.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                idInformante = new IdInformante(cursor.getInt(0), cursor.getInt(1));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return idInformante;
    }
////    Antes de suprimir la cabecera
//    public static boolean verificaViviendas(int idAsignacion) {
//        boolean res = false;
//        String query = "SELECT i.id_asignacion\n" +
//                "FROM ope_asignacion a JOIN enc_informante i ON a.id_asignacion="+idAsignacion+" AND a.id_asignacion=i.id_asignacion\n" +
//                "WHERE i.id_nivel = 1\n" +
//                "AND i.estado <> 'ANULADO'";
//        Cursor cursor = conn.rawQuery(query, null);
//        if (cursor.moveToFirst()) {
//            res = true;
//        }
//        cursor.close();
//        return res;
//    }

    public static boolean verificaViviendas(int idAsignacion) {
        boolean res = false;
        String query = "SELECT i.id_asignacion\n" +
                "FROM ope_asignacion a JOIN enc_informante i ON a.id_asignacion="+idAsignacion+" AND a.id_asignacion=i.id_asignacion\n" +
                "WHERE i.id_nivel = 2\n" +
                "AND i.estado <> 'ANULADO'";
        Cursor cursor = conn.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            res = true;
        }
        cursor.close();
        return res;
    }

    public static boolean verificaBoletas(int idAsignacion) {
        boolean res = false;
        String query = "SELECT i.id_asignacion\n" +
                "FROM ope_asignacion a JOIN enc_informante i ON a.id_asignacion="+idAsignacion+" AND a.id_asignacion=i.id_asignacion\n" +
                "WHERE i.id_nivel = 3\n" +
                "AND i.estado <> 'ANULADO'";
        Cursor cursor = conn.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            res = true;
        }
        cursor.close();
        return res;
    }

    /**
     * Verifica si hay informantes anteriores para recuperar en la visita actual
     * @param idAsignacion
     * @return
     */
    public static boolean tieneViviendasAnteriores(int idAsignacion) {
        String query = "SELECT i.id_asignacion\n" +
                "FROM ope_asignacion a, enc_informante i, enc_informante_anterior ia\n" +
                "WHERE a.id_asignacion = "+idAsignacion+"\n" +
                "AND a.id_asignacion = i.id_asignacion\n" +
                "AND i.id_nivel = 2\n" +
                "AND i.id_asignacion_anterior IS NOT NULL\n" +
                "AND ia.id_base=2\n" +
                "AND ia.id_asignacion = i.id_asignacion_anterior\n" +
                "AND ia.correlativo = i.correlativo_anterior\n" +
                "AND i.estado <> 'ANULADO'";
        Cursor cursor = conn.rawQuery(query, null);
        boolean res = cursor.moveToFirst();
        cursor.close();
        return res;
    }

    public static boolean tieneBoletasAnteriores(int idAsignacion) {
        String query = "SELECT i.id_asignacion\n" +
                "FROM ope_asignacion a, enc_informante i, enc_informante_anterior ia\n" +
                "WHERE a.id_asignacion = "+idAsignacion+"\n" +
                "AND a.id_asignacion = i.id_asignacion\n" +
                "AND i.id_nivel = 3\n" +
                "AND i.id_asignacion_anterior IS NOT NULL\n" +
                "AND ia.id_base=2 \n" +
                "AND ia.id_asignacion = i.id_asignacion_anterior\n" +
                "AND ia.correlativo = i.correlativo_anterior\n" +
                "AND i.estado <> 'ANULADO'";
        Cursor cursor = conn.rawQuery(query, null);
        boolean res = cursor.moveToFirst();
        cursor.close();
        return res;
    }

    /**
     * Verifica boletas anteriores de la base 1
     * @return
     */
    public static boolean tieneViviendasAnteriores1(int idAsignacion) {
        String query = "SELECT i.id_asignacion\n" +
                "FROM ope_asignacion a, enc_informante i, enc_informante_anterior ia\n" +
                "WHERE a.id_asignacion = "+idAsignacion+"\n" +
                "AND a.id_asignacion = i.id_asignacion\n" +
                "AND i.id_nivel = 2\n" +
                "AND i.id_asignacion_anterior IS NOT NULL\n" +
                "AND ia.id_base=1\n" +
                "AND ia.id_asignacion = i.id_asignacion_anterior\n" +
                "AND ia.correlativo = i.correlativo_anterior\n" +
                "AND i.estado <> 'ANULADO'";
        Cursor cursor = conn.rawQuery(query, null);
        boolean res = cursor.moveToFirst();
        cursor.close();
        return res;
    }
    public static boolean tieneBoletasAnteriores1(int idAsignacion) {
        String query = "SELECT i.id_asignacion\n" +
                "FROM ope_asignacion a, enc_informante i, enc_informante_anterior ia\n" +
                "WHERE a.id_asignacion = "+idAsignacion+"\n" +
                "AND a.id_asignacion = i.id_asignacion\n" +
                "AND i.id_nivel = 3\n" +
                "AND i.id_asignacion_anterior IS NOT NULL\n" +
                "AND ia.idbase=1 \n" +
                "AND ia.id_asignacion = i.id_asignacion_anterior\n" +
                "AND ia.correlativo = i.correlativo_anterior\n" +
                "AND i.estado <> 'ANULADO'";
        Cursor cursor = conn.rawQuery(query, null);
        boolean res = cursor.moveToFirst();
        cursor.close();
        return res;
    }
    @SuppressWarnings("unused")
    public Integer get_id_asignacion() {
        return filaActual.getInt(filaActual.getColumnIndex("id_asignacion"));
    }
    @SuppressWarnings("unused")
    public void set_id_asignacion(Integer value) {
        filaNueva.put("id_asignacion", value);
    }

    @SuppressWarnings("unused")
    public Integer get_id_usuario() {
        return filaActual.getInt(filaActual.getColumnIndex("id_usuario"));
    }
    @SuppressWarnings("unused")
    public void set_id_usuario(Integer value) {
        filaNueva.put("id_usuario", value);
    }

    @SuppressWarnings("unused")
    public Integer get_id_upm() {
        return filaActual.getInt(filaActual.getColumnIndex("id_upm"));
    }
    @SuppressWarnings("unused")
    public void set_id_upm(Integer value) {
        filaNueva.put("id_upm", value);
    }

    @SuppressWarnings("unused")
    public Integer get_gestion() {
        return filaActual.getInt(filaActual.getColumnIndex("gestion"));
    }
    @SuppressWarnings("unused")
    public void set_gestion(Integer value) {
        filaNueva.put("gestion", value);
    }

    @SuppressWarnings("unused")
    public Integer get_mes() {
        return filaActual.getInt(filaActual.getColumnIndex("mes"));
    }
    @SuppressWarnings("unused")
    public void set_mes(Integer value) {
        filaNueva.put("mes", value);
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
    public Integer get_revisita() {
        return filaActual.getInt(filaActual.getColumnIndex("revisita"));
    }
    @SuppressWarnings("unused")
    public void set_revisita(Integer value) {
        filaNueva.put("revisita", value);
    }
}
