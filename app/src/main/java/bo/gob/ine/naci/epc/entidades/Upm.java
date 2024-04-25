package bo.gob.ine.naci.epc.entidades;

import android.database.Cursor;
import android.database.SQLException;
import android.util.Log;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by INE.
 */
//EN DB
public class Upm extends EntidadId {
    public Upm() {
        super("cat_upm");
    }

    public static void borrar(int idUpm, int idUsuario) {
        String query = "DELETE FROM enc_encuesta\n" +
                "WHERE EXISTS(SELECT * FROM enc_informante i\n" +
                "   WHERE enc_encuesta.id_asignacion = i.id_asignacion\n" +
                "   AND enc_encuesta.correlativo = i.correlativo\n" +
                "   AND id_upm = " + idUpm + " AND id_usuario <> " + idUsuario + ")";

        conn.execSQL(query);

        query = "DELETE FROM enc_informante WHERE id_upm = " + idUpm + " AND id_usuario <> " + idUsuario;

        conn.execSQL(query);

//        Informante.reordenarLV(idUpm, new String[]{Usuario.getLogin()});
    }

    public static void borrarBoletas(int idUpm) {
        String query = "DELETE FROM enc_encuesta\n" +
                "WHERE EXISTS(SELECT * FROM enc_informante i\n" +
                "   WHERE enc_encuesta.id_asignacion = i.id_asignacion\n" +
                "   AND enc_encuesta.correlativo = i.correlativo\n" +
                "   AND id_upm = " + idUpm + "" +
                "   AND id_nivel > 0)";

        conn.execSQL(query);

        query = "DELETE FROM enc_informante WHERE id_upm = " + idUpm + " AND id_nivel > 0";

        conn.execSQL(query);

//        Informante.reordenarLV(idUpm, new String[]{Usuario.getLogin()});
    }

    public ArrayList<Map<String, Object>> obtenerListado(int idUsuario) {
        ArrayList<Map<String, Object>> list = new ArrayList<>();
        String q;
        String strIdUsuario = String.valueOf(idUsuario);
        if (strIdUsuario.equals("%")) {
            q = "AND id_usuario IN(SELECT id_usuario FROM enc_informante WHERE id_nivel = 1 AND estado <> 'ANULADO')\n";
        } else {
            q = "AND id_usuario = " + strIdUsuario + "\n";
        }
        //SE AÑADIO LO SIGUIENTE PARA OBTENER MAS DATOS DE LA UPM
        String qViviendasElaboradas = "(SELECT COUNT(*) FROM enc_informante ei WHERE ei.id_upm = a.id_upm AND id_nivel = 2 AND estado LIKE 'ELABORADO') AS qViviendasElaboradas";
        String qViviendasSeleccionadas = "(SELECT COUNT(*) FROM enc_informante ei WHERE ei.id_upm = a.id_upm AND id_nivel = 2 AND (estado LIKE 'SELECCIONADO' OR estado LIKE 'SELECCIONADO3')) AS qViviendasSeleccionadas";
        String qBoletasElaboradas = "(SELECT COUNT(*) FROM enc_informante ei WHERE ei.id_upm = a.id_upm AND id_nivel = 3 AND (estado LIKE 'ELABORADO' OR estado LIKE 'PRECONCLUIDO')) AS qBoletasElaboradas";
        String qBoletasConcluidas = "(SELECT COUNT(*) FROM enc_informante ei WHERE ei.id_upm = a.id_upm AND id_nivel = 3 AND (estado LIKE 'CONCLUIDO' OR estado LIKE 'INHABILITADO')) AS qBoletasConcluidas";
        String qViviendasAnuladas = "(SELECT COUNT(*) FROM enc_informante ei WHERE ei.id_upm = a.id_upm AND id_nivel = 2 AND estado LIKE 'ANULADO') AS qViviendasAnuladas";
        String qBoletasAnuladas = "(SELECT COUNT(*) FROM enc_informante ei WHERE ei.id_upm = a.id_upm AND id_nivel = 3 AND estado LIKE 'ANULADO') AS qBoletasAnuladas";
//        String qBoletasObservadas = "(SELECT COUNT(*) FROM enc_informante ei WHERE ei.id_upm = a.id_upm AND id_nivel = 3 AND estado LIKE 'ANULADO') AS qBoletasObservadas";
        String qBoletasObservadas = "(SELECT COUNT(*) FROM enc_informante ei WHERE ei.id_upm = a.id_upm AND id_nivel = 3  AND ei.id_asignacion||'-'||ei.correlativo in (SELECT id_asignacion||'-'||correlativo FROM enc_observacion WHERE id_asignacion = ei.id_asignacion AND id_tipo_obs = 2)) AS qBoletasObservadas";
        String qUpmsAdicionales = "(SELECT coalesce(group_concat(codigo,' ' ),'') FROM cat_upm_hijo ch WHERE ch.id_upm_padre=u.id_upm AND ch.id_incidencia=1) AS qUpmsAdicionales";
        String qUpmsRemplazo = "(SELECT coalesce(group_concat(codigo,' ' ),'') FROM cat_upm_hijo ch WHERE ch.id_upm_padre=u.id_upm AND ch.id_incidencia=2) AS qUpmsRemplazo";
        String query = "SELECT distinct a.id_asignacion, u.id_upm, codigo, nombre, u.fecinicio, u.latitud, u.longitud, u.estado, u.incidencia, u.url_pdf, a.mes, "+qViviendasElaboradas+", "+qViviendasSeleccionadas+", "+qBoletasElaboradas+", "+qBoletasConcluidas+", "+qViviendasAnuladas+", "+qBoletasAnuladas+", "+qBoletasObservadas+", "+qUpmsAdicionales+", "+qUpmsRemplazo+" \n" +
                "FROM cat_upm u, ope_asignacion a\n" +
                "WHERE u.id_upm = a.id_upm\n" +
                "AND u.estado <> 'ANULADO'\n" +
                "AND u.estado <> 'ELIMINADO'\n" + q +
                "ORDER BY a.feccre";
        Cursor cursor = conn.rawQuery(query, null);
        Log.d("Principal",query);
        if (cursor.moveToFirst()) {
            do {
                Map<String, Object> map = new LinkedHashMap<>();
                map.put("id_asignacion", cursor.getInt(0));
                map.put("id_upm", cursor.getInt(1));
                map.put("codigo", cursor.getString(2));
                map.put("nombre", cursor.getString(3));
                map.put("fecinicio", cursor.getLong(4));
                map.put("latitud", cursor.getDouble(5));
                map.put("longitud", cursor.getDouble(6));
                map.put("estado", cursor.getString(7));
                map.put("incidencia", cursor.getInt(8));
                map.put("url_pdf", cursor.getString(9));
                map.put("mes", cursor.getInt(10));
                map.put("qViviendasElaboradas", cursor.getInt(11)+cursor.getInt(12));
                map.put("qViviendasSeleccionadas", cursor.getInt(12));
                map.put("qBoletasElaboradas", cursor.getInt(13));
                map.put("qBoletasConcluidas", cursor.getInt(14));
                map.put("qViviendasAnuladas", cursor.getInt(15));
                map.put("qBoletasAnuladas", cursor.getInt(16));
                map.put("qBoletasObservadas", cursor.getInt(17));
                map.put("qUpmsAdicionales", cursor.getString(18).trim());
                map.put("qUpmsRemplazo", cursor.getString(19).trim());
                int contadorLV=cursor.getInt(11)+cursor.getInt(12);
                if(contadorLV>0){
                    if(cursor.getInt(12)>0){
                        contadorLV=1;
                    }else{
                        contadorLV=2;
                    }
                }
                map.put("estado_lv",contadorLV);
                list.add(map);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }
    public ArrayList<Map<String, Object>> obtenerListadoPrueba() {
        ArrayList<Map<String, Object>> list = new ArrayList<>();

        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id_asignacion", 1);
        map.put("id_upm", 1);
        map.put("codigo", "111-1111111111-A");
        map.put("nombre", "Ejemplo - Ejemplo - Ejemplo");
        map.put("fecinicio", 1602460800);
        map.put("latitud", 0);
        map.put("longitud", 0);
        map.put("estado", "ELABORADO");
        map.put("incidencia", 2);
        map.put("url_pdf", "https:");
        map.put("mes", 11);
        map.put("qViviendasElaboradas", 0);
        map.put("qViviendasSeleccionadas", 0);
        map.put("qBoletasElaboradas", 0);
        map.put("qBoletasConcluidas", 0);
        map.put("qViviendasAnuladas", 0);
        map.put("qBoletasAnuladas", 0);
        map.put("qBoletasObservadas", 0);
        list.add(map);

        return list;
    }

    public static Map<String, Object> obtenerDetallesUpm(int idProyecto, int idUsuario, int idUpm) {
        Map<String, Object> res = new LinkedHashMap<>();
        String q;
        String strIdUsuario = String.valueOf(idUsuario);
        if (strIdUsuario.equals("%")) {
            q = "AND id_usuario IN(SELECT id_usuario FROM enc_informante WHERE id_nivel = 1 AND estado <> 'ANULADO')\n";
        } else {
            q = "AND id_usuario = " + strIdUsuario + "\n";
        }
        //SE AÑADIO LO SIGUIENTE PARA OBTENER MAS DATOS DE LA UPM
        String qViviendasElaboradas = "(SELECT COUNT(*) FROM enc_informante ei WHERE ei.id_upm = a.id_upm AND id_nivel = 0 AND estado LIKE 'ELABORADO') AS qViviendasElaboradas";
        String qViviendasSeleccionadas = "(SELECT COUNT(*) FROM enc_informante ei WHERE ei.id_upm = a.id_upm AND id_nivel = 0 AND (estado LIKE 'SELECCIONADO' OR estado LIKE 'SELECCIONADO3')) AS qViviendasSeleccionadas";
        String qBoletasElaboradas = "(SELECT COUNT(*) FROM enc_informante ei WHERE ei.id_upm = a.id_upm AND id_nivel = 1 AND estado LIKE 'ELABORADO') AS qBoletasElaboradas";
        String qBoletasConcluidas = "(SELECT COUNT(*) FROM enc_informante ei WHERE ei.id_upm = a.id_upm AND id_nivel = 1 AND estado LIKE 'CONCLUIDO') AS qBoletasConcluidas";
        String qViviendasAnuladas = "(SELECT COUNT(*) FROM enc_informante ei WHERE ei.id_upm = a.id_upm AND id_nivel = 0 AND estado LIKE 'ANULADO') AS qViviendasAnuladas";
        String qBoletasAnuladas = "(SELECT COUNT(*) FROM enc_informante ei WHERE ei.id_upm = a.id_upm AND id_nivel = 1 AND estado LIKE 'ANULADO') AS qBoletasAnuladas";
        String query = "SELECT distinct u.id_upm, codigo, nombre, u.fecinicio, u.latitud, u.longitud, u.estado, u.incidencia, a.mes, "+qViviendasElaboradas+", "+qViviendasSeleccionadas+", "+qBoletasElaboradas+", "+qBoletasConcluidas+", "+qViviendasAnuladas+", "+qBoletasAnuladas+" \n" +
                "FROM cat_upm u, ope_asignacion a\n" +
                "WHERE u.id_upm = a.id_upm\n" +
                "AND u.id_upm = "+idUpm + "\n" +
                "AND u.id_proyecto = " + idProyecto + "\n" + q +
                "ORDER BY a.feccre";
        Cursor cursor = conn.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                res.put("id_upm", cursor.getInt(0));
                res.put("codigo", cursor.getString(1));
                res.put("nombre", cursor.getString(2));
                res.put("fecinicio", cursor.getLong(3));
                res.put("latitud", cursor.getDouble(4));
                res.put("longitud", cursor.getDouble(5));
                res.put("estado", cursor.getString(6));
                res.put("incidencia", cursor.getInt(7));
                res.put("mes", cursor.getInt(8));
                res.put("qViviendasElaboradas", cursor.getInt(9));
                res.put("qViviendasSeleccionadas", cursor.getInt(10));
                res.put("qBoletasElaboradas", cursor.getInt(11));
                res.put("qBoletasConcluidas", cursor.getInt(12));
                res.put("qViviendasAnuladas", cursor.getInt(13));
                res.put("qBoletasAnuladas", cursor.getInt(14));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return res;
    }

    public static ArrayList<String> getUrlPdf(int idUsuario) {
        ArrayList<String> res = new ArrayList<>();
        String query = "SELECT u.url_pdf\n" +
                "FROM cat_upm u, ope_asignacion a\n" +
                "WHERE u.id_upm = a.id_upm\n" +
                "AND id_usuario = " + idUsuario +"\n" +
                "AND u.url_pdf IS NOT NULL AND u.url_pdf <> ''\n" +
                "UNION\n" +
                "SELECT uh.url_pdf\n" +
                "FROM cat_upm_hijo uh\n" +
                "WHERE uh.id_upm_padre IN (SELECT u.id_upm FROM cat_upm u, ope_asignacion a WHERE u.id_upm = a.id_upm AND a.id_usuario = "+idUsuario+")\n" +
                "AND uh.url_pdf IS NOT NULL AND uh.url_pdf <> ''";

        Cursor cursor = null;
        cursor = conn.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                res.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return res;
    }

    //TODO:BRP{
    public static ArrayList<String> getCodigosUpm(int idUsuario) {
        ArrayList<String> res = new ArrayList<>();
        String query = "SELECT u.codigo\n" +
                "FROM cat_upm u, ope_asignacion a\n" +
                "WHERE u.id_upm = a.id_upm\n" +
                "AND id_usuario = " + idUsuario +"\n" +
                "UNION\n" +
                "SELECT uh.codigo\n" +
                "FROM cat_upm_hijo uh\n" +
                "WHERE uh.id_upm_padre IN (SELECT u.id_upm FROM cat_upm u, ope_asignacion a WHERE u.id_upm = a.id_upm AND a.id_usuario = "+idUsuario+")";

        Cursor cursor = null;
        cursor = conn.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                res.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return res;
    }
    //TODO:BRP}

    public static String getCodigo(int idAsignacion) {
        String res = null;
        String query = "SELECT codigo\n" +
                "FROM cat_upm u, ope_asignacion a\n" +
                "WHERE u.id_upm = a.id_upm\n" +
                "AND a.id_asignacion = " + idAsignacion;
        Cursor cursor = conn.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                res = cursor.getString(0);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return res;
    }

    public static String getCodigoUpmBoleta(int idAsignacion,int idUpmHijo) {
        String res = null;
        String query="";
        if(idUpmHijo==0){
            query = "SELECT codigo\n" +
                    "FROM cat_upm u, ope_asignacion a\n" +
                    "WHERE u.id_upm = a.id_upm\n" +
                    "AND a.id_asignacion = " + idAsignacion+"\n"+
                    "LIMIT 1";
        }else {
            query = "SELECT h.codigo\n" +
                    "FROM cat_upm u join ope_asignacion a on u.id_upm = a.id_upm AND a.id_asignacion = " + idAsignacion+"\n" +
                    "join cat_upm_hijo h on u.id_upm = h.id_upm_padre\n" +
                    "WHERE h.id_upm_hijo="+idUpmHijo+"\n"+
                    "LIMIT 1";
        }

        Cursor cursor = conn.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                res = cursor.getString(0);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return res;
    }
    public static String getCodigoUpm(int idUpm) {
        String res = null;
        String query = "SELECT codigo\n" +
                "FROM cat_upm u, ope_asignacion a\n" +
                "WHERE u.id_upm = a.id_upm\n" +
                "AND u.id_upm = " + idUpm;
        Cursor cursor = conn.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                res = cursor.getString(0);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return res;
    }
    public static String getCodUpm(int idUpm) {
        String res = null;
        String query = "SELECT codigo\n" +
                "FROM cat_upm \n" +
                "WHERE id_upm = " + idUpm + " \n" +
                "UNION\n" +
                "SELECT codigo\n" +
                "FROM cat_upm_hijo\n" +
                "WHERE id_upm_padre IN (" + idUpm + " ) ";
        Cursor cursor = conn.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                res = cursor.getString(0);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return res;
    }
    public static String getCodigoComunidad(int idAsignacion) {
        String res = null;
        String query = "SELECT nombre\n" +
                "FROM cat_upm u, ope_asignacion a\n" +
                "WHERE u.id_upm = a.id_upm\n" +
                "AND a.id_asignacion = " + idAsignacion;
        Cursor cursor = conn.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                res = cursor.getString(0);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return res;
    }
    public static int getIdUpm(int idAsignacion) {
        int res = 0;
        String query = "SELECT u.id_upm\n" +
                "FROM cat_upm u, ope_asignacion a\n" +
                "WHERE u.id_upm = a.id_upm\n" +
                "AND a.id_asignacion = " + idAsignacion;
        Cursor cursor = conn.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                res = cursor.getInt(0);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return res;
    }
    public static int obtenerInstancia(String  codigoUpm) {
        int res = -1;
        String query = " SELECT case WHEN EXISTS(SELECT 1\n" +
                " FROM (SELECT * FROM cat_upm WHERE codigo='"+codigoUpm+"') cu\n" +
                " JOIN cat_upm_hijo cuh ON cu.id_upm=cuh.id_upm_padre AND cuh.id_incidencia=2) THEN 2\n" +
                " WHEN NOT EXISTS(SELECT 1\n" +
                " FROM (SELECT * FROM cat_upm WHERE codigo='"+codigoUpm+"') cu\n" +
                " JOIN cat_upm_hijo cuh ON cu.id_upm=cuh.id_upm_padre AND cuh.id_incidencia=1) AND EXISTS(SELECT 1\n" +
                " FROM (SELECT * FROM cat_upm_hijo WHERE codigo='"+codigoUpm+"') cu) THEN 3\n" +
                " WHEN NOT EXISTS(SELECT 1\n" +
                " FROM (SELECT * FROM cat_upm WHERE codigo='"+codigoUpm+"') cu\n" +
                " JOIN cat_upm_hijo cuh ON cu.id_upm=cuh.id_upm_padre AND cuh.id_incidencia=3) AND EXISTS(SELECT 1\n" +
                " FROM (SELECT * FROM cat_upm WHERE codigo='"+codigoUpm+"') cu)  THEN 1\n" +
                " ELSE -1 END instancia";
        Cursor cursor = conn.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                res = cursor.getInt(0);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return res;
    }
    public static int getPanel(int idUpm) {
        int res = 0;
        String query = "SELECT id_panel\n" +
                "FROM cat_upm u\n" +
                "WHERE u.id_upm = " + idUpm;
        Cursor cursor = conn.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                res = Integer.parseInt(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return res;
    }

    /**
     * Realiza el borrado físico de los datos según los parámetros
     * @return
     */
    public static void borrarTodo(int idUpm, int idAsignacion) {
        try {

            String query = "UPDATE  enc_informante SET estado='ANULADO' WHERE id_asignacion IN (SELECT id_asignacion FROM ope_asignacion WHERE id_asignacion = "+idAsignacion+" AND id_upm = "+idUpm+") AND id_nivel IN(1,2) AND estado<>'ANULADO'";
            conn.execSQL(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //TODO:BRP{
    public ArrayList<Map<String, Object>> obtenerComunidad(int idUpm, boolean todo) {
        ArrayList<Map<String, Object>> list = new ArrayList<>();
        String query;

        if(todo){
            query = "SELECT ciu_com, id_com, seg_unico, geo\n" +
                    "FROM d_epc_comunidad";
        } else {
            query = "SELECT ciu_com, id_com, seg_unico, geo\n" +
                    "FROM d_epc_comunidad\n" +
                    "WHERE seg_unico IN ( \n" +
                    "SELECT codigo FROM cat_upm WHERE id_upm = " + idUpm + " \n" +
                    "UNION \n" +
                    "SELECT codigo FROM cat_upm_hijo WHERE id_upm_padre = " + idUpm + ")";
        }


        Cursor cursor = conn.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                Map<String, Object> map = new LinkedHashMap<>();
                map.put("ciu_com", cursor.getString(0));
                map.put("id_com", cursor.getString(1));
                map.put("seg_unico", cursor.getString(2));
                map.put("geo", cursor.getString(3));
                list.add(map);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    public ArrayList<Map<String, Object>> obtenerPredio(int idUpm, boolean todo) {
        ArrayList<Map<String, Object>> list = new ArrayList<>();
        String query;
        if(todo){
            query = "SELECT seg_unico, orden, cod_if, tipo, ciu_com ,geo\n" +
                    "FROM a_epc_predio";
        } else {
            query = "SELECT seg_unico, orden, cod_if, tipo, ciu_com ,geo\n" +
                    "FROM a_epc_predio\n" +
                    "WHERE seg_unico IN ( \n" +
                    "SELECT codigo FROM cat_upm WHERE id_upm = " + idUpm + " \n" +
                    "UNION \n" +
                    "SELECT codigo FROM cat_upm_hijo WHERE id_upm_padre = " + idUpm + ")";
        }
        Cursor cursor = conn.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                Map<String, Object> map = new LinkedHashMap<>();
                map.put("seg_unico", cursor.getString(0));
                map.put("orden", cursor.getString(1));
                map.put("cod_if", cursor.getString(2));
                map.put("tipo", cursor.getString(3));
                map.put("ciu_com", cursor.getString(4));
                map.put("geo", cursor.getString(5));
                list.add(map);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    public ArrayList<Map<String, Object>> obtenerManzana(int idUpm, boolean todo) {
        ArrayList<Map<String, Object>> list = new ArrayList<>();
        String query;
            if(todo){
                query = "SELECT id_manz, orden_manz, seg_unico, geo\n" +
                        "FROM a_epc_manzana";
            } else {
                query = "SELECT id_manz, orden_manz, seg_unico, geo\n" +
                        "FROM a_epc_manzana\n" +
                        "WHERE seg_unico IN ( \n" +
                        "SELECT codigo FROM cat_upm WHERE id_upm = " + idUpm + " \n" +
                        "UNION \n" +
                        "SELECT codigo FROM cat_upm_hijo WHERE id_upm_padre = " + idUpm + ")";
            }
        Cursor cursor = conn.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                Map<String, Object> map = new LinkedHashMap<>();
                map.put("id_manz", cursor.getString(0));
                map.put("orden_manz", cursor.getString(1));
                map.put("seg_unico", cursor.getString(2));
                map.put("geo", cursor.getString(3));
                list.add(map);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    public ArrayList<Map<String, Object>> obtenerSegmento(int idUpm, boolean todo) {
        ArrayList<Map<String, Object>> list = new ArrayList<>();
        String query;
                if(todo){
                    query = "SELECT seg_unico, geo\n" +
                            "FROM a_epc_segmento";
                } else {
                    query = "SELECT seg_unico, geo\n" +
                            "FROM a_epc_segmento\n" +
                            "WHERE seg_unico IN ( \n" +
                            "SELECT codigo FROM cat_upm WHERE id_upm = " + idUpm + " \n" +
                            "UNION \n" +
                            "SELECT codigo FROM cat_upm_hijo WHERE id_upm_padre = " + idUpm + ")";
                }
        Cursor cursor = conn.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                Map<String, Object> map = new LinkedHashMap<>();
                map.put("seg_unico", cursor.getString(0));
                map.put("geo", cursor.getString(1));
                list.add(map);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    public ArrayList<Map<String, Object>> obtenerSegmentoD(int idUpm, boolean todo) {
        ArrayList<Map<String, Object>> list = new ArrayList<>();
        String query;
                    if(todo){
                        query = "SELECT seg_unico, segmento, geo\n" +
                                "FROM d_epc_segmento";
                    } else {
                        query = "SELECT seg_unico, segmento, geo\n" +
                                "FROM d_epc_segmento\n" +
                                "WHERE seg_unico IN ( \n" +
                                "SELECT codigo FROM cat_upm WHERE id_upm = " + idUpm + " \n" +
                                "UNION \n" +
                                "SELECT codigo FROM cat_upm_hijo WHERE id_upm_padre = " + idUpm + ")";
                    }
        Cursor cursor = conn.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                Map<String, Object> map = new LinkedHashMap<>();
                map.put("seg_unico", cursor.getString(0));
                map.put("segmento", cursor.getString(1));
                map.put("geo", cursor.getString(2));
                list.add(map);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }
//TODO:BRP}

    public static ArrayList getCodigos(int idUpm) {
        ArrayList res = new ArrayList();
        String query = "SELECT codigo FROM cat_upm WHERE id_upm = " + idUpm + " \n" +
                "UNION \n" +
                "SELECT codigo FROM cat_upm_hijo WHERE id_upm_padre = " + idUpm;

        Cursor cursor = conn.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                res.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return res;
    }

    /**
     * Realiza el borrado físico de los datos según los parámetros
     * @return
     */
    public static boolean isSorteoEspecial(int idUpm) {
        int res = 1;
        String query = "SELECT sorteo\n" +
                "FROM cat_upm u\n" +
                "WHERE u.id_upm = " + idUpm;
        Cursor cursor = conn.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                res = Integer.parseInt(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return res>1;
    }

    public static boolean revisaRrecorrido(int idUpm, int recorrido) {
        boolean res = false;
        String query = "select max (id_respuesta) from enc_encuesta ee \n" +
                "join enc_informante ei \n" +
                "on ee.id_asignacion = ei.id_asignacion \n" +
                "and ee.correlativo = ei.correlativo \n" +
                "where ei.id_upm = " + idUpm +"\n" +
                "and ei.estado != 'ANULADO'\n" +
                "and ee.id_pregunta = 36383";
        Cursor cursor = conn.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                if(recorrido <= cursor.getInt(0)+1){
                    res = true;
                }
            } while (cursor.moveToNext());
        } else {
            res = true;
        }
        cursor.close();
        return res;
    }

    public static int editaHogar(IdInformante idInformante, int idUpm, String codUpm) {
        int res = 0;
        String query = "SELECT MAX(codigo_respuesta) \n" +
                "FROM enc_encuesta ee2\n" +
                "JOIN \n" +
                "(SELECT ei.id_asignacion,ei.correlativo \n" +
                "FROM enc_encuesta ee \n" +
                "JOIN enc_informante ei \n" +
                "ON ee.id_asignacion = ei.id_asignacion \n" +
                "AND ee.correlativo = ei.correlativo\n" +
                "WHERE id_upm = " + idUpm + "\n" +
                "AND id_pregunta = 20484\n" +
                "AND codigo_respuesta = '" + codUpm + "'\n" +
                "AND ei.estado != 'ANULADO') AS a\n" +
                "ON ee2.id_asignacion = a.id_asignacion \n" +
                "AND ee2.correlativo = a.correlativo\n" +
                "WHERE id_pregunta = 18597";
        Cursor cursor = conn.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            res = cursor.getInt(0);
        }
        cursor.close();
        return res;
    }

    public static ArrayList<Integer> verificaHogar(int idUpm, String codUpm, int numViv) {
        ArrayList<Integer> res = new ArrayList<>();

        String query = "SELECT codigo_respuesta \n" +
                "FROM enc_encuesta ee2\n" +
                "JOIN \n" +
                "(SELECT ei.id_asignacion,ei.correlativo \n" +
                "FROM enc_encuesta ee \n" +
                "JOIN enc_informante ei \n" +
                "ON ee.id_asignacion = ei.id_asignacion \n" +
                "AND ee.correlativo = ei.correlativo\n" +
                "WHERE id_upm = " + idUpm + "\n" +
                "AND id_pregunta = 20484\n" +
                "AND codigo_respuesta = '" + codUpm + "'\n" +
                "AND ei.estado != 'ANULADO') AS a\n" +
                "ON ee2.id_asignacion = a.id_asignacion \n" +
                "AND ee2.correlativo = a.correlativo\n" +
                "JOIN \n" +
                "(SELECT ei.id_asignacion,ei.correlativo \n" +
                "FROM enc_encuesta ee3 \n" +
                "JOIN enc_informante ei \n" +
                "ON ee3.id_asignacion = ei.id_asignacion \n" +
                "AND ee3.correlativo = ei.correlativo\n" +
                "WHERE id_upm = " + idUpm + "\n" +
                "AND id_pregunta = 18596\n" +
                "AND codigo_respuesta = '" + numViv + "'\n" +
                "AND ei.estado != 'ANULADO') AS b\n" +
                "ON a.id_asignacion = b.id_asignacion \n" +
                "AND a.correlativo = b.correlativo\n " +
                "WHERE id_pregunta = 18597 ORDER BY 1";
        Cursor cursor = conn.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                res.add(cursor.getInt(0));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return res;
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
    public Integer get_id_proyecto() {
        return filaActual.getInt(filaActual.getColumnIndex("id_proyecto"));
    }
    @SuppressWarnings("unused")
    public void set_id_proyecto(Integer value) {
        filaNueva.put("id_proyecto", value);
    }

    @SuppressWarnings("unused")
    public Integer get_id_departamento() {
        return filaActual.getInt(filaActual.getColumnIndex("id_departamento"));
    }
    @SuppressWarnings("unused")
    public void set_id_departamento(Integer value) {
        filaNueva.put("id_departamento", value);
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
    public String get_nombre() {
        return filaActual.getString(filaActual.getColumnIndex("nombre"));
    }
    @SuppressWarnings("unused")
    public void set_nombre(String value) {
        filaNueva.put("nombre", value);
    }

    @SuppressWarnings("unused")
    public Long get_fecinicio() {
        return filaActual.getLong(filaActual.getColumnIndex("fecinicio"));
    }
    @SuppressWarnings("unused")
    public void set_fecinicio(Long value) {
        filaNueva.put("fecinicio", value);
    }
//
//    @SuppressWarnings("unused")
//    public Long get_fecfinal() {
//        return filaActual.getLong(filaActual.getColumnIndex("fecfinal"));
//    }
//    @SuppressWarnings("unused")
//    public void set_fecfinal(Long value) {
//        filaNueva.put("fecfinal", value);
//    }
//

    @SuppressWarnings("unused")
    public String get_latitud() {
        return filaActual.getString(filaActual.getColumnIndex("latitud"));
    }
    @SuppressWarnings("unused")
    public void set_latitud(String value) {
        filaNueva.put("latitud", value);
    }

    @SuppressWarnings("unused")
    public String get_longitud() {
        return filaActual.getString(filaActual.getColumnIndex("longitud"));
    }
    @SuppressWarnings("unused")
    public void set_longitud(String value) {
        filaNueva.put("longitud", value);
    }

    @SuppressWarnings("unused")
    public Estado get_apiestado() {
        return Estado.valueOf(filaActual.getString(filaActual.getColumnIndex("estado")));
    }
    @SuppressWarnings("unused")
    public void set_apiestado(Estado value) {
        filaNueva.put("estado", value.toString());
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

//    @SuppressWarnings("unused")
//    public Integer get_reciclada() {
//        return filaActual.getInt(filaActual.getColumnIndex("reciclada"));
//    }
//    @SuppressWarnings("unused")
//    public void set_reciclada(Integer value) {
//        filaNueva.put("reciclada", value);
//    }
//    @SuppressWarnings("unused")
//    public String get_codigo_titular() {
//        return filaActual.getString(filaActual.getColumnIndex("codigo_titular"));
//    }
//    @SuppressWarnings("unused")
//    public void set_codigo_titular(String value) {
//        filaNueva.put("codigo_titular", value);
//    }
//
//    @SuppressWarnings("unused")
//    public Integer get_id_tipo() {
//        return filaActual.getInt(filaActual.getColumnIndex("id_tipo"));
//    }
//    @SuppressWarnings("unused")
//    public void set_id_tipo(Integer value) {
//        filaNueva.put("id_tipo", value);
//    }

}
