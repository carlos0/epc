package bo.gob.ine.naci.epc.entidades;

import android.content.ContentValues;
import android.database.Cursor;
import android.text.Html;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.TreeMap;

import bo.gob.ine.naci.epc.herramientas.Parametros;

public class Informante extends EntidadCorr {
    protected String[] encFields;
    protected String[] encTypes;
    public static String PARAM_VIVIENDAS_OCUPADAS="viviendas_ocupadas";
    public static String PARAM_VIVIENDAS_DESOCUPADAS="viviendas_desocupadas";
    public static String PARAM_VIVIENDAS_OTROS="viviendas_otros";
    public static String PARAM_TOTAL_HOGARES="total_hogares";
    public static String PARAM_TOTAL_HOMBRRES="total_hombres";
    public static String PARAM_TOTAL_MUJERES="total_mujeres";
    public static String PARAM_VIVIENDAS_OMITIDAS="viviendas_omitidas";
    public static String PARAM_VIVIENDAS_OCUPADAS_MARCO="viviendas_ocupadas_marco";
    public static String PARAM_VIVIENDAS_DESOCUPADAS_MARCO="viviendas_desocupadas_marco";

    public Informante() {
        super("enc_informante");

        Cursor cursor = conn.rawQuery("PRAGMA table_info(enc_encuesta)", null);
        int n = cursor.getCount();
        encFields = new String[n];
        encTypes = new String[n];
        int i = 0;
        while (cursor.moveToNext()) {
            encFields[i] = cursor.getString(1);
            encTypes[i] = cursor.getString(2);
            if (encTypes[i].contains("(")) {
                encTypes[i] = encTypes[i].substring(0, encTypes[i].indexOf("("));
            }
            i++;
        }
        cursor.close();
    }

    @Override
    protected Identificador getId(Cursor filaActual) {
        return new IdInformante(filaActual.getInt(filaActual.getColumnIndex("id_asignacion")),
                filaActual.getInt(filaActual.getColumnIndex("correlativo")));
    }

    @Override
    protected Identificador getId(ContentValues filaNueva) {
        String query = "SELECT coalesce(max(correlativo), 0) + 1\n" +
                "FROM enc_informante\n" +
                "WHERE id_asignacion = " + filaNueva.getAsInteger("id_asignacion");
        Cursor cursor = conn.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            filaNueva.put("correlativo", cursor.getInt(0));
        }
        cursor.close();
        return new IdInformante(filaNueva.getAsInteger("id_asignacion"), filaNueva.getAsInteger("correlativo"));
    }

    public String concluir(String codigoPregunta) {
        String res = "";
        String resp = codigoPregunta;
        boolean flag;
        String pquery = "SELECT *\n" +
                "FROM enc_observacion\n" +
                "WHERE id_asignacion = " + get_id_informante().id_asignacion + " AND correlativo = " + get_id_informante().correlativo + " \n" +
                "AND id_tipo_obs = 2";
        Cursor cursor = conn.rawQuery(pquery, null);
        flag = cursor.moveToFirst();
        cursor.close();
//
//        int nivel = get_id_nivel();
//        Cursor cursor = conn.rawQuery(query, null);
//        if (cursor.moveToFirst()) {
//            do {
//                nivel = cursor.getInt(0);
//            } while (cursor.moveToNext());
//        }
//        cursor.close();
//        if (nivel > get_id_nivel()) {
//            query = "SELECT estado, descripcion\n" +
//                    "FROM enc_informante\n" +
//                    "WHERE id_asignacion_padre = " + get_id_informante().id_asignacion + "\n" +
//                    "AND correlativo_padre = " + get_id_informante().correlativo + "\n" +
//                    "AND estado <> 'ANULADO'";
//
//            cursor = conn.rawQuery(query, null);
//            if (cursor.moveToFirst()) {
//                do {
//                    if (!cursor.getString(0).equals("CONCLUIDO")) {
//                        res = "No se concluyó la información de " + cursor.getString(1) +" o no colocó la INCIDENCIA que corresponde para la persona.<br>- Para eliminar un miembro, haga click sostenido y seleccione ELIMINAR<br>- Para llenar la INCIDENCIA ingrese a la sección abierta de INCIDENCIA de miembro desde el RESUMEN";
//                        break;
//                    }
//                } while (cursor.moveToNext());
//            }
//            cursor.close();
//        }
//        if (res.equals("")) {

        conn.beginTransaction();
        String query;
        try {
//            if (flag) {
//                query = "UPDATE enc_informante SET estado = 'CONCLUIDO' WHERE id_asignacion = " + get_id_informante().id_asignacion + " AND correlativo = " + get_id_informante().correlativo;
//            } else {
            query = "UPDATE enc_informante SET estado = 'PRECONCLUIDO' WHERE id_asignacion = " + get_id_informante().id_asignacion + " AND correlativo = " + get_id_informante().correlativo +" AND id_nivel=3";
//            }
            conn.execSQL(query);
            conn.setTransactionSuccessful();

//            conn.execSQL("UPDATE enc_encuesta\n" +
//                    "SET visible = 't'\n" +
//                    "WHERE id_pregunta = 99999\n" +
//                    "AND id_asignacion = " + get_id_informante().id_asignacion + "\n" +
//                    "AND correlativo = " + get_id_informante().correlativo);
//            conn.setTransactionSuccessful();
        } finally {
            conn.endTransaction();
        }
//        }
        return res;
    }

    public String concluirPersona(String codigoPregunta) {
        String res = "";
        String resp = codigoPregunta;

        conn.beginTransaction();
        try {
            String query = "UPDATE enc_informante SET estado = 'CONCLUIDO' WHERE id_asignacion = " + get_id_informante().id_asignacion + " AND correlativo = " + get_id_informante().correlativo +" AND id_nivel=4 ";
            conn.execSQL(query);
            conn.setTransactionSuccessful();
        } finally {
            conn.endTransaction();
        }
//        }
        return res;
    }

    public Map<String, ArrayList<Map<String, Object>>> obtenerListadoPdf(IdInformante idPadre) {
        Map<String, ArrayList<Map<String, Object>>> res = new HashMap<>();
        String query;
        Cursor cursor = null;
        String grupo = "";
        ArrayList<Map<String, Object>> arra = new ArrayList<>();
        String vrespuesta1= "(CASE WHEN e.id_pregunta IN (18269,18272, 18278) AND INSTR(e.respuesta, '<br/>') > 0 THEN  SUBSTR(e.respuesta, 1, INSTR(e.respuesta, '<br/>') - 1) ELSE e.respuesta END) as respuesta";
        String vrespuesta2= "(CASE WHEN e1.id_pregunta IN (18269,18272, 18278) AND INSTR(e1.respuesta, '<br/>') > 0 THEN  SUBSTR(e1.respuesta, 1, INSTR(e1.respuesta, '<br/>') - 1) ELSE e1.respuesta END) as respuesta";
        String nombre = "(SELECT ee.respuesta FROM enc_encuesta ee WHERE ee.id_asignacion = i1.id_asignacion AND ee.correlativo = i1.correlativo AND ee.id_pregunta IN (18581) AND ee.visible in ('t','true'))";

        query = "SELECT i.id_asignacion, i.correlativo, i.codigo, p.codigo_pregunta, p.pregunta, "+vrespuesta1+", e.observacion\n" +
                "FROM enc_informante i \n" +
                "JOIN enc_encuesta e ON i.id_asignacion = e.id_asignacion AND i.correlativo = e.correlativo\n" +
                "JOIN enc_pregunta p ON e.id_pregunta = p.id_pregunta\n" +
                "WHERE i.id_asignacion = " + idPadre.id_asignacion + "\n" +
                "AND i.correlativo = " + idPadre.correlativo + "\n" +
                "AND i.estado NOT IN ('ANULADO','INHABILITADO')\n" +
                "AND e.visible like 't%'\n" +
                "UNION\n" +
                "SELECT i1.id_asignacion, i1.correlativo, " + nombre + " , p1.codigo_pregunta, p1.pregunta, "+vrespuesta2+", e1.observacion\n" +
                "FROM enc_informante i1 \n" +
                "JOIN enc_encuesta e1 ON i1.id_asignacion = e1.id_asignacion AND i1.correlativo = e1.correlativo\n" +
                "JOIN enc_pregunta p1 ON e1.id_pregunta = p1.id_pregunta\n" +
                "WHERE i1.id_asignacion_padre = " + idPadre.id_asignacion + "\n" +
                "AND i1.correlativo_padre = " + idPadre.correlativo + "\n" +
                "AND i1.estado NOT IN ('ANULADO','INHABILITADO')\n" +
                "AND e1.visible like 't%'\n" +
                "ORDER BY codigo";

        try {
            cursor = conn.rawQuery(query, null);
            int i = 0;
            if (cursor.moveToFirst()) {
                do {
                    if(grupo.equals(cursor.getString(2))||i==0){
                        Log.d("point", "hol");
                        Map<String, Object> row = new LinkedHashMap<>();
                        row.put("id_asignacion", cursor.getInt(0));
                        row.put("correlativo", cursor.getInt(1));
                        row.put("codigo", cursor.getString(2));
                        row.put("codigo_pregunta", cursor.getString(3));
                        row.put("pregunta", cursor.getString(4));
                        row.put("respuesta", cursor.getString(5));
                        row.put("observacion", cursor.getString(6));
                        arra.add(row);
                    } else {
                        res.put(grupo, arra);
                        arra = new ArrayList<>();
                    }
                    grupo=cursor.getString(2);
                    i++;
                } while (cursor.moveToNext());
                res.put(grupo, arra);
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    public static void supervisor(IdInformante idInformante) {
        Log.d("supervisor3", "aqui3");
        String res = "";
        conn.beginTransaction();
        try {
            String query = "UPDATE enc_informante SET estado = 'CONCLUIDO' WHERE id_asignacion = " + idInformante.id_asignacion + " AND correlativo = " + idInformante.correlativo + " AND estado = 'PRECONCLUIDO'";
            conn.execSQL(query);
            conn.setTransactionSuccessful();
        } finally {
            conn.endTransaction();
            Log.d("supervisor4", "aqui4");
        }

    }

    public static ArrayList<IdInformante> getHijos(IdInformante idPadre) {
        ArrayList<IdInformante> res = new ArrayList<IdInformante>();
        String strQuery = "SELECT i.id_asignacion, i.correlativo\n" +
                "FROM enc_informante i\n" +
                "WHERE i.id_asignacion_padre = " + idPadre.id_asignacion + "\n" +
                "AND i.correlativo_padre = " + idPadre.correlativo + "\n" +
                "AND i.estado <> 'ANULADO'";
        Cursor cursor = conn.rawQuery(strQuery, null);
        if (cursor.moveToFirst()) {
            do {
                res.add(new IdInformante(cursor.getInt(0), cursor.getInt(1)));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return res;
    }

    public static int contar(IdInformante idPadre) {
        int res = 0;
        String query = "SELECT count(*)\n" +
                "FROM enc_informante\n" +
                "WHERE estado <> 'ANULADO'\n" +
                "AND id_asignacion_padre = " + idPadre.id_asignacion + "\n" +
                "AND correlativo_padre = " + idPadre.correlativo;
        Cursor cursor = conn.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                res = cursor.getInt(0);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return res;
    }


    public static int contar2(int idAsignacion) {
        int res = 0;
        String query = "SELECT count(*)\n" +
                "FROM enc_informante\n" +
                "WHERE id_nivel = 2\n" +
                "AND estado <> 'ANULADO'\n" +
                "AND id_asignacion = " + idAsignacion;
        Cursor cursor = conn.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                res = cursor.getInt(0);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return res;
    }

    /**
     * Obtiente mayores a 14 anios
     *
     * @param idInformante
     * @return
     */
    public static Map<Integer, String> getSelectMayores14(IdInformante idInformante) {

        Map<Integer, String> res = new LinkedHashMap<>();

        String strQuery = "SELECT i.id_asignacion*1000 + i.correlativo,  i.codigo || '|' || e.respuesta || '|' || i.codigo\n" +
                "FROM enc_informante i \n" +
                "JOIN enc_encuesta e \n" +
                "ON (i.id_asignacion = e.id_asignacion AND i.correlativo = e.correlativo) \n" +
                "WHERE i.estado <> 'ANULADO' \n" +
                "AND i.id_asignacion_padre =" + idInformante.id_asignacion + "\n" +
                "AND i.correlativo_padre =" + idInformante.correlativo + " \n" +
                "AND e.id_pregunta = 18581 \n" +
                "AND (SELECT CAST(respuesta AS Int) \n" +
                "FROM enc_encuesta \n" +
                "WHERE id_asignacion = i.id_asignacion \n" +
                "AND correlativo = i.correlativo \n" +
                "AND id_pregunta = 18173) >= 14";
        Log.d("mayores: ", strQuery);

        Cursor cursor = conn.rawQuery(strQuery, null);
        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(0);


                String resp = cursor.getString(1);
                res.put(id, resp);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return res;
    }

    public static int contarMayores15(IdInformante idInformante) {

        int resp = 0;

        String strQuery = "SELECT COUNT(*)\n" +
                "FROM enc_informante i \n" +
                "JOIN enc_encuesta e \n" +
                "ON (i.id_asignacion = e.id_asignacion AND i.correlativo = e.correlativo) \n" +
                "WHERE i.estado <> 'ANULADO' \n" +
                "AND i.id_asignacion_padre =" + idInformante.id_asignacion + "\n" +
                "AND i.correlativo_padre =" + idInformante.correlativo + " \n" +
                "AND e.id_pregunta = 18581 \n" +
                "AND (SELECT CAST(respuesta AS Int) \n" +
                "FROM enc_encuesta \n" +
                "WHERE id_asignacion = i.id_asignacion \n" +
                "AND correlativo = i.correlativo \n" +
                "AND id_pregunta = 18173) >= 15";

        Cursor cursor = conn.rawQuery(strQuery, null);
        if (cursor.moveToFirst()) {
            resp = cursor.getInt(0);

        }
        cursor.close();
        return resp;
    }


    public static int getCodigo(IdInformante id) {
        int res = 0;
        String query = "SELECT codigo\n" +
                "FROM enc_informante\n" +
                "WHERE estado <> 'ANULADO'\n" +
                "AND id_asignacion = " + id.id_asignacion + "\n" +
                "AND correlativo = " + id.correlativo;
        Cursor cursor = conn.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                res = cursor.getInt(0);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return res;
    }
    public static String getCodigoString(IdInformante id) {
        String res = "";
        String query = "SELECT codigo\n" +
                "FROM enc_informante\n" +
                "WHERE estado <> 'ANULADO'\n" +
                "AND id_asignacion = " + id.id_asignacion + "\n" +
                "AND correlativo = " + id.correlativo;
        Cursor cursor = conn.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                res = cursor.getString(0);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return res;
    }
    public static int getEdadAnterior(IdInformante id) {
        int res = -1;
        String query = "SELECT eea.codigo_respuesta\n" +
                "FROM (SELECT * FROM " +
                "enc_informante  "+"WHERE estado <> 'ANULADO'\n" +
                "AND id_asignacion = " + id.id_asignacion + "\n" +
                "AND correlativo = " + id.correlativo
                +") ei JOIN enc_informante_anterior eia ON ei.id_asignacion_anterior=eia.id_asignacion AND ei.correlativo_anterior=eia.correlativo\n" +
                "JOIN enc_encuesta_anterior eea ON eia.id_asignacion=eea.id_asignacion AND eia.correlativo=eea.correlativo AND eea.id_pregunta=2036\n";
        Log.d("quuuu",query);
        Cursor cursor = conn.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                res = cursor.getInt(0);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return res;
    }
    public static int getNroAnterior(IdInformante id) {
        int res = -1;
        String query = "SELECT eea.codigo_respuesta\n" +
                "FROM (SELECT * FROM " +
                "enc_informante  "+"WHERE estado <> 'ANULADO'\n" +
                "AND id_asignacion = " + id.id_asignacion + "\n" +
                "AND correlativo = " + id.correlativo
                +") ei JOIN enc_informante_anterior eia ON ei.id_asignacion_anterior=eia.id_asignacion AND ei.correlativo_anterior=eia.correlativo\n" +
                "JOIN enc_encuesta_anterior eea ON eia.id_asignacion=eea.id_asignacion AND eia.correlativo=eea.correlativo AND eea.id_pregunta=2151\n";
        Log.d("quuuu",query);
        Cursor cursor = conn.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                res = cursor.getInt(0);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return res;
    }
    public static int getParentescoAnterior(IdInformante id) {
        int res = -1;
        String query = "SELECT eea.codigo_respuesta\n" +
                "FROM (SELECT * FROM " +
                "enc_informante  "+"WHERE estado <> 'ANULADO'\n" +
                "AND id_asignacion = " + id.id_asignacion + "\n" +
                "AND correlativo = " + id.correlativo
                +") ei JOIN enc_informante_anterior eia ON ei.id_asignacion_anterior=eia.id_asignacion AND ei.correlativo_anterior=eia.correlativo\n" +
                "JOIN enc_encuesta_anterior eea ON eia.id_asignacion=eea.id_asignacion AND eia.correlativo=eea.correlativo AND eea.id_pregunta=2038\n";
        Log.d("quuuu",query);
        Cursor cursor = conn.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                res = cursor.getInt(0);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return res;
    }

    public static float getContar(IdInformante idPadre) {
        int res = 0;
        String query = "SELECT count(*)\n" +
                "FROM enc_informante\n" +
                "WHERE estado <> 'ANULADO'\n" +
                "AND id_asignacion_padre = " + idPadre.id_asignacion + "\n" +
                "AND correlativo_padre = " + idPadre.correlativo;
        Cursor cursor = conn.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                res = cursor.getInt(0);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return res;
    }

    public static boolean getContarPersonas(IdInformante idPadre) {
        String query = "SELECT *\n" +
                "FROM enc_informante\n" +
                "WHERE id_asignacion_padre = " + idPadre.id_asignacion + "\n" +
                "AND correlativo_padre = " + idPadre.correlativo + "\n" +
                "AND (estado <> 'CONCLUIDO' and estado <> 'ANULADO')";
        Cursor cursor = conn.rawQuery(query, null);
        boolean res = cursor.moveToFirst();
        cursor.close();
        return res;
    }

    public static float estanTerminados(IdInformante idPadre) {
        int idPreguntaIncidencia = Pregunta.getIDpregunta(2, TipoPregunta.Incidencia);
        int res = 0;
        String query = "SELECT count(*)\n" +
                "FROM enc_informante i\n" +
                "WHERE i.estado <> 'ANULADO'\n" +
                "AND i.id_asignacion_padre = " + idPadre.id_asignacion + "\n" +
                "AND i.correlativo_padre = " + idPadre.correlativo + "\n" +
                "AND (SELECT CAST(respuesta AS Int) FROM enc_encuesta WHERE id_asignacion = i.id_asignacion AND correlativo = i.correlativo AND id_pregunta = " + idPreguntaIncidencia + ") = 1";
        Cursor cursor = conn.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                res = cursor.getInt(0);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return res;
    }

    public static int hayPersonasConcluidas(IdInformante idPadre) {
        int res = 0;
        String query = "SELECT count(*)\n" +
                "FROM enc_informante\n" +
                "WHERE id_asignacion_padre=" + idPadre.id_asignacion + "\n" +
                "AND correlativo_padre= " + idPadre.correlativo + "\n" +
                "AND estado = 'CONCLUIDO'\n";

        Cursor cursor = conn.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            res = cursor.getInt(0);
        }
        cursor.close();
        return res;
    }

    public static Integer getUpm(IdInformante idInformante) {
        Integer res = null;
        String query = "SELECT id_upm\n" +
                "FROM enc_informante\n" +
                "WHERE id_asignacion = " + idInformante.id_asignacion + "\n" +
                "AND id_upm IS NOT NULL\n" +
                "UNION\n" +
                "SELECT id_upm\n" +
                "FROM enc_informante\n" +
                "WHERE id_asignacion_padre = " + idInformante.id_asignacion + "\n" +
                "AND id_upm IS NOT NULL";

        Cursor cursor = conn.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                res = cursor.getInt(0);
            } while (cursor.moveToNext());
        }
        cursor.close();
        if (res == null) {
            return Asignacion.getUpm(idInformante.id_asignacion);
        } else {
            return res;
        }
    }

    public IdInformante guardar(IdInformante id) {
        IdInformante r = (IdInformante) super.guardar();
        int a = r.id_asignacion;
        int b = r.correlativo;
        if (r.correlativo > 0) {
            String query = "UPDATE enc_informante\n" +
                    "SET codigo = (SELECT count(*) FROM enc_informante i WHERE i.estado <> 'ANULADO' AND i.id_asignacion_padre = enc_informante.id_asignacion_padre AND i.correlativo_padre = enc_informante.correlativo_padre AND CAST(i.codigo AS Int) <= CAST(enc_informante.codigo AS Int))\n" +
                    "WHERE estado <> 'ANULADO'\n" +
                    "AND id_asignacion_padre = " + id.id_asignacion + "\n" +
                    "AND correlativo_padre = " + id.correlativo;
            conn.beginTransaction();
            try {
                conn.execSQL(query);
                conn.setTransactionSuccessful();
            } finally {
                conn.endTransaction();
            }
        }
        return r;
    }

//    public IdInformante guardarLV(int idAsignacion) {
//        IdInformante r = (IdInformante)super.guardar();
//        if (r.correlativo > 0) {
//            String query = "UPDATE enc_informante\n" +
//                    "SET codigo = (SELECT count(*) FROM enc_informante i WHERE i.id_nivel = 2 AND i.estado <> 'ANULADO'\n" +
//                    "   AND i.id_upm = enc_informante.id_upm AND CAST(i.codigo AS Int) <= CAST(enc_informante.codigo AS Int))\n" +
//                    "WHERE id_nivel = 2 AND estado <> 'ANULADO'\n" +
//                    "AND id_upm = " + Asignacion.getUpm(idAsignacion);
//            conn.beginTransaction();
//            try {
//                conn.execSQL(query);
//                conn.setTransactionSuccessful();
//            } finally {
//                conn.endTransaction();
//            }
//        }
//        return r;
//    }

    public IdInformante guardarLV(int idAsignacion) {
        IdInformante r = (IdInformante) super.guardar();
        if (r.correlativo > 0) {
            try {
                String query1 = "SELECT ei1.id_asignacion, ei1.correlativo, ei1.codigo, COUNT(ei2.codigo) as nuevo_codigo\n" +
                        "    FROM (SELECT id_asignacion, correlativo, codigo\n" +
                        "     FROM enc_informante\n" +
                        "    WHERE estado <> 'ANULADO'\n" +
                        "AND id_upm = " + Asignacion.getUpm(idAsignacion) + "\n" +
                        "AND id_nivel = 2\n" +
                        "    ) ei1 JOIN \n" +
                        "    (SELECT id_asignacion, correlativo, codigo\n" +
                        "     FROM enc_informante\n" +
                        "    WHERE estado <> 'ANULADO'\n" +
                        "AND id_upm = " + Asignacion.getUpm(idAsignacion) + "\n" +
                        " AND id_nivel = 2\n" +
                        "    ) ei2 \n" +
                        "    ON CAST(ei1.codigo AS INT) > CAST(ei2.codigo AS INT) \n" +
                        "                        OR ((CAST(ei1.codigo AS INT) = CAST(ei2.codigo AS INT) AND ei1.correlativo >= ei2.correlativo))\n" +
                        "    GROUP BY 1,2, CAST(ei1.codigo AS INT)\n" +
                        "    ORDER BY CAST(ei2.codigo AS INT)";
                Cursor cursor = conn.rawQuery(query1, null);
                conn.beginTransaction();
                try {
                    int i = 1;
                    while (cursor.moveToNext()) {
                        ContentValues paquete = new ContentValues();
                        paquete.put("codigo", cursor.getString(3));
                        conn.update("enc_informante", paquete, "id_asignacion = ? AND correlativo = ? AND id_nivel=2 ", new String[]{cursor.getString(0), cursor.getString(3)});

                        i++;
                    }
                    conn.setTransactionSuccessful();

                } catch (Exception ex) {
                    ex.printStackTrace();
                } finally {
                    conn.endTransaction();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return r;
    }
    ///niveles distinto de -2
    public IdInformante guardarInformante(int idAsignacion, int idNivel) {
        IdInformante r = (IdInformante) super.guardar();
        if (r.correlativo > 0) {
            try {
                String query1 = "SELECT ei1.id_asignacion, ei1.correlativo, ei1.codigo, COUNT(ei2.codigo) as nuevo_codigo\n" +
                        "    FROM (SELECT id_asignacion, correlativo, codigo\n" +
                        "     FROM enc_informante\n" +
                        "    WHERE estado <> 'ANULADO'\n" +
                        "AND id_upm = " + Asignacion.getUpm(idAsignacion) + "\n" +
                        "AND id_nivel = "+idNivel+"\n" +
                        "    ) ei1 JOIN \n" +
                        "    (SELECT id_asignacion, correlativo, codigo\n" +
                        "     FROM enc_informante\n" +
                        "    WHERE estado <> 'ANULADO'\n" +
                        "AND id_upm = " + Asignacion.getUpm(idAsignacion) + "\n" +
                        " AND id_nivel = "+idNivel+"\n" +
                        "    ) ei2 \n" +
                        "    ON CAST(ei1.codigo AS INT) > CAST(ei2.codigo AS INT) \n" +
                        "                        OR ((CAST(ei1.codigo AS INT) = CAST(ei2.codigo AS INT) AND ei1.correlativo >= ei2.correlativo))\n" +
                        "    GROUP BY 1,2, CAST(ei1.codigo AS INT)\n" +
                        "    ORDER BY CAST(ei2.codigo AS INT)";
                Cursor cursor = conn.rawQuery(query1, null);
                conn.beginTransaction();
                try {
                    int i = 1;
                    while (cursor.moveToNext()) {
                        ContentValues paquete = new ContentValues();
                        paquete.put("codigo", cursor.getString(3));
                        conn.update("enc_informante", paquete, "id_asignacion = ? AND correlativo = ? ", new String[]{cursor.getString(0), cursor.getString(3)});

                        i++;
                    }
                    conn.setTransactionSuccessful();

                } catch (Exception ex) {
                    ex.printStackTrace();
                } finally {
                    conn.endTransaction();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return r;
    }

    ////reordenacion de lvs
    public void reordenarLV(IdInformante r) {

        if (r.correlativo > 0) {
            try {
                String query1 = "SELECT ei1.id_asignacion, ei1.correlativo, ei1.codigo, COUNT(ei2.codigo) as nuevo_codigo\n" +
                        "    FROM (SELECT id_asignacion, correlativo, codigo\n" +
                        "     FROM enc_informante\n" +
                        "    WHERE estado <> 'ANULADO'\n" +
                        "AND id_upm = " + Asignacion.getUpm(r.id_asignacion) + "\n" +
                        "AND id_nivel = 2\n" +
                        "    ) ei1 JOIN \n" +
                        "    (SELECT id_asignacion, correlativo, codigo\n" +
                        "     FROM enc_informante\n" +
                        "    WHERE estado <> 'ANULADO'\n" +
                        "AND id_upm = " + Asignacion.getUpm(r.id_asignacion) + "\n" +
                        " AND id_nivel = 2\n" +
                        "    ) ei2 \n" +
                        "    ON CAST(ei1.codigo AS INT) > CAST(ei2.codigo AS INT) \n" +
                        "                        OR ((CAST(ei1.codigo AS INT) = CAST(ei2.codigo AS INT) AND ei1.correlativo >= ei2.correlativo))\n" +
                        "    GROUP BY 1,2, CAST(ei1.codigo AS INT)\n" +
                        "    ORDER BY CAST(ei2.codigo AS INT)";
                Log.d("QueryGeneradoO", query1);

                Cursor cursor = conn.rawQuery(query1, null);
                conn.beginTransaction();
                try {
                    int i = 1;
                    while (cursor.moveToNext()) {
                        ContentValues paquete = new ContentValues();
                        paquete.put("codigo", cursor.getString(3));
                        conn.update("enc_informante", paquete, "id_asignacion = ? AND correlativo = ? ", new String[]{cursor.getString(0), cursor.getString(1)});

                        i++;
                    }
                    conn.setTransactionSuccessful();

                } catch (Exception ex) {
                    ex.printStackTrace();
                } finally {
                    conn.endTransaction();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void modificaCodigo(IdInformante idInformante) {

        String query = "UPDATE enc_informante\n" +
                "SET codigo = (SELECT count(*) FROM enc_informante i WHERE i.id_nivel = 4 AND i.estado <> 'ANULADO'\n" +
                "   AND i.id_upm = enc_informante.id_upm AND CAST(i.codigo AS Int) <= CAST(enc_informante.codigo AS Int))\n" +
                "WHERE id_nivel = 4 AND estado <> 'ANULADO'\n" +
                "AND id_asignacion_padre = " + idInformante.id_asignacion + "\n" +
                "AND correlativo_padre = " + idInformante.correlativo + "\n";
        conn.beginTransaction();
        try {
            conn.execSQL(query);
            conn.setTransactionSuccessful();
        } finally {
            conn.endTransaction();
        }

    }

    public static void actualizaCodigoInformante(int idAsignacion,int idNivel) {
        try {
            String query1 = "SELECT ei1.id_asignacion, ei1.correlativo, ei1.codigo, COUNT(ei2.codigo) as nuevo_codigo\n" +
                    "    FROM (SELECT id_asignacion, correlativo, codigo\n" +
                    "     FROM enc_informante\n" +
                    "    WHERE estado <> 'ANULADO'\n" +
                    "AND id_upm = " + Asignacion.getUpm(idAsignacion) + "\n" +
                    "AND id_nivel = "+idNivel+"\n" +
                    "    ) ei1 JOIN \n" +
                    "    (SELECT id_asignacion, correlativo, codigo\n" +
                    "     FROM enc_informante\n" +
                    "    WHERE estado <> 'ANULADO'\n" +
                    "AND id_upm = " + Asignacion.getUpm(idAsignacion) + "\n" +
                    " AND id_nivel = "+idNivel+"\n" +
                    "    ) ei2 \n" +
                    "    ON CAST(ei1.codigo AS INT) > CAST(ei2.codigo AS INT) \n" +
                    "                        OR ((CAST(ei1.codigo AS INT) = CAST(ei2.codigo AS INT) AND ei1.correlativo >= ei2.correlativo))\n" +
                    "    GROUP BY 1,2, CAST(ei1.codigo AS INT)\n";

            Cursor cursor = conn.rawQuery(query1, null);
            conn.beginTransaction();
            try {
                int i = 1;
                while (cursor.moveToNext()) {
                    ContentValues paquete = new ContentValues();
                    paquete.put("codigo", cursor.getString(3));
                    conn.update("enc_informante", paquete, "id_asignacion = ? AND correlativo = ? ", new String[]{cursor.getString(0), cursor.getString(1)});
                    i++;
                }
                conn.setTransactionSuccessful();

            } catch (Exception ex) {
                ex.printStackTrace();
            } finally {
                conn.endTransaction();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void actualizaOmitida(IdInformante idInformante) {
        int idPregunta = Pregunta.getIDpregunta(2, TipoPregunta.NumeroVivienda);

        String query = "UPDATE enc_informante\n" +
                "SET estado = 'SELECCIONADO' \n" +
                "WHERE id_nivel = 2 AND estado <> 'ANULADO'\n" +
                "AND id_asignacion = " + idInformante.id_asignacion + "\n" +
                "AND correlativo = " + idInformante.correlativo + "\n" +
                "AND id_asignacion = " + idInformante.id_asignacion + "\n" +
                "AND correlativo = " + idInformante.correlativo ;
        conn.beginTransaction();
        try {
            conn.execSQL(query);
            conn.setTransactionSuccessful();
        } finally {
            conn.endTransaction();
        }
    }

    public static void actualizaVoeOmitida(IdInformante idInformante) {
        int idPregunta = Pregunta.getIdPregunta(Parametros.CODIGO_PREGUNTA_NRO_ORDEN_VIVIENDA);

        String query = "UPDATE enc_encuesta\n" +
                "SET respuesta = (SELECT count(*) FROM enc_informante i, enc_encuesta e WHERE i.estado <> 'ANULADO'\n" +
                "AND i.id_asignacion = e.id_asignacion\n" +
                "AND i.correlativo = e.correlativo\n" +
                "AND e.id_pregunta = " + idPregunta + "\n" +
                "AND CAST(e.respuesta AS Int) > 0\n" +
                "AND i.id_upm = " + Asignacion.getUpm(idInformante.id_asignacion) + ") \n" +
                "WHERE id_asignacion = " + idInformante.id_asignacion + "\n" +
                "AND correlativo = " + idInformante.correlativo + "\n" +
                "AND id_pregunta = " + idPregunta;
        conn.beginTransaction();
        try {
            conn.execSQL(query);
            conn.setTransactionSuccessful();
        } finally {
            conn.endTransaction();
        }
    }

    public static int getContarLV(IdInformante id) {
        int res = 0;
        String query = "SELECT count(*)\n" +
                "FROM enc_informante\n" +
                "WHERE id_nivel = 2 AND estado = 'SELECCIONADO'\n" +
                "AND id_asignacion = " + id.id_asignacion + "\n" ;
//                "AND correlativo_padre = " + idPadre.correlativo;
        Cursor cursor = conn.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                res = cursor.getInt(0);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return res;
    }

    public static Map<Long, String> getRespuestas(IdInformante idInformante) {
        Map<Long, String> res = new LinkedHashMap<>();
        String strQuery = "SELECT id_asignacion, correlativo, codigo, descripcion\n" +
                "FROM enc_informante\n" +
                "WHERE id_asignacion_padre = " + idInformante.id_asignacion + "\n" +
                "AND correlativo_padre = " + idInformante.correlativo + "\n" +
                "AND estado <> 'ANULADO'";
        Cursor cursor = conn.rawQuery(strQuery, null);
        if (cursor.moveToFirst()) {
            do {
                long id = cursor.getInt(cursor.getColumnIndex("id_asignacion")) * 10000 + cursor.getInt(cursor.getColumnIndex("correlativo"));
                String cod = cursor.getString(cursor.getColumnIndex("codigo"));
                String resp = cursor.getString(cursor.getColumnIndex("descripcion"));
                res.put(id, cod + "|" + resp);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return res;
    }

    public static Map<Long, String> getRespuestasHijos(IdInformante idInformante, String pregunta_madre, int id_madre) {
        Map<Long, String> res = new LinkedHashMap<>();
        String strQuery = "SELECT i.id_asignacion, i.correlativo, i.codigo, i.descripcion\n" +
                "FROM enc_informante i, enc_encuesta e, enc_pregunta p\n" +
                "WHERE i.id_asignacion = e.id_asignacion\n" +
                "AND i.correlativo = e.correlativo\n" +
                "AND e.id_pregunta = p.id_pregunta\n" +
                "AND e.id_respuesta = " + id_madre + "\n" +
                "AND e.codigo_respuesta NOT IN('997', '998', '999')\n" +
                "AND p.codigo_pregunta = '" + pregunta_madre + "'\n" +
                "AND i.id_asignacion_padre = " + idInformante.id_asignacion + "\n" +
                "AND i.correlativo_padre = " + idInformante.correlativo + "\n" +
                "AND i.estado <> 'ANULADO'";
        Cursor cursor = conn.rawQuery(strQuery, null);
        if (cursor.moveToFirst()) {
            do {
                long id = cursor.getInt(cursor.getColumnIndex("id_asignacion")) * 10000 + cursor.getInt(cursor.getColumnIndex("correlativo"));
                Integer fila = cursor.getInt(cursor.getColumnIndex("fila"));
                String resp = cursor.getString(cursor.getColumnIndex("descripcion"));
                res.put(id, fila + "|" + resp);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return res;
    }

//    public Map<Integer, IdInformante> jerarquia(IdInformante idInformante) {
//        Map<Integer, IdInformante> res = new TreeMap<>();
//        int niv = 1;
//        do {
//            String query = "SELECT id_asignacion_padre, correlativo_padre, id_nivel\n" +
//                    "FROM enc_informante\n" +
//                    "WHERE id_asignacion = " + idInformante.id_asignacion + "\n" +
//                    "AND correlativo = " + idInformante.correlativo + "\n" +
//                    "AND estado <> 'ANULADO'";
//
//            Cursor cursor = conn.rawQuery(query, null);
//            if (cursor.moveToFirst()) {
//                do {
//                    niv = cursor.getInt(2);
//                    idInformante = new IdInformante(cursor.getInt(0), cursor.getInt(1));
//                    res.put(niv, idInformante);
//                } while (cursor.moveToNext());
//            }
//            cursor.close();
//        } while (niv > 1);
//        return res;
//    }

    public ArrayList<Map<String, Object>> obtenerListadoBoleta(int idUpm,int idUpmHijo, int idNivel) {
        ArrayList<Map<String, Object>> res = new ArrayList<>();
        String query;
        Cursor cursor = null;
        Seccion seccion = new Seccion();
        int idSeccion = seccion.seccion_inicial(idNivel);
//        int idIncidencia = Pregunta.getIDpregunta(idSeccion, TipoPregunta.Incidencia);
        StringBuilder condicionUpm=new StringBuilder();
//        if (idUpmHijo == 0) {
//            condicionUpm.append("AND i.id_upm_hijo is null\n");
//        } else {
//            condicionUpm.append("AND i.id_upm_hijo = ").append(idUpmHijo).append("\n");
//        }

        query = "SELECT i.id_asignacion, i.correlativo, i.id_asignacion_padre, i.correlativo_padre, i.id_nivel, codigo, i.descripcion, i.id_usuario, i.estado, i.id_upm, i.usucre, i.feccre, i.usumod, i.fecmod, (SELECT o.id_tipo_obs FROM enc_observacion o WHERE i.id_asignacion = o.id_asignacion AND i.correlativo = o.correlativo ORDER BY o.feccre DESC LIMIT 1), (SELECT e.codigo_respuesta FROM enc_encuesta e WHERE e.id_asignacion = i.id_asignacion AND e.correlativo = i.correlativo AND e.id_pregunta = " + Parametros.ID_INCIDENCIA_FINAL + " AND e.visible in ('t','true')), i.id_upm_hijo\n" +
                "FROM enc_informante i\n" +
                "WHERE i.estado <> 'ANULADO'\n" +
                "AND i.estado <> 'INHABILITADO'\n" +
                "AND i.estado <> 'CONCLUIDO'\n" +
                "AND i.id_nivel = " + idNivel + "\n" +
                "AND i.id_upm = " + idUpm + "\n" +
                condicionUpm.toString()+
                "ORDER BY i.feccre";

        try {
            cursor = conn.rawQuery(query, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (cursor.moveToFirst()) {
            do {
                Map<String, Object> row = new LinkedHashMap<>();
                row.put("id_asignacion", cursor.getInt(0));
                row.put("correlativo", cursor.getInt(1));
                row.put("id_asignacion_padre", cursor.getInt(2));
                row.put("correlativo_padre", cursor.getInt(3));
                row.put("id_nivel", cursor.getInt(4));
                row.put("codigo", cursor.getString(5));
                row.put("descripcion", cursor.getString(6));
                row.put("id_usuario", cursor.getInt(7));
                row.put("estado", cursor.getString(8));
                row.put("id_upm", cursor.getInt(9));
                row.put("usucre", cursor.getString(10));
                row.put("feccre", cursor.getLong(11));
                row.put("usumod", cursor.getString(12));
                row.put("fecmod", cursor.getLong(13));
                //int idtobs = (Integer)cursor.getInt(14) == null?-1: cursor.getInt(14);
                row.put("id_tipo_obs", (Integer) cursor.getInt(14) == null ? 0 : cursor.getInt(14));
                row.put("observacion_historial", Observacion.preparaHistorial(new IdInformante(cursor.getInt(0), cursor.getInt(1))));
                row.put("encuesta_respuestas", Encuesta.recuperaRespuestas(new IdInformante(cursor.getInt(0), cursor.getInt(1))));
                row.put("encuesta_respuestas_personas", Encuesta.recuperaRespuestasPersonas(new IdInformante(cursor.getInt(0), cursor.getInt(1))));
                row.put("preguntas_respuestas_iniciales", Informante.getPreguntasRespuestasIniciales(idSeccion, new IdInformante(cursor.getInt(0), cursor.getInt(1)), "'DV_01','DV_02','DV_03','DV_03_1','DV_04','DV_04_1','DV_05','DV_05_1','DV_05_2','DV_05_2E','DV_08','DV_09','DV_10','DV_12','DV_13','DV_15','DV_16','DV_17'", false, 0/*idNivel==1?18721:0*/));
                row.put("incidencia", cursor.getString(15) == null ? 0 : cursor.getString(15));
                row.put("id_upm_hijo", cursor.getString(16) == null ? 0 : cursor.getInt(16));
                res.add(row);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return res;
    }

    public ArrayList<Map<String, Object>> obtenerListadoEncuesta(int idNivel, IdInformante idPadre) {
        ArrayList<Map<String, Object>> res = new ArrayList<>();
        String query;
        Cursor cursor = null;

        String nombre = "(SELECT e.respuesta FROM enc_encuesta e WHERE e.id_asignacion = i.id_asignacion AND e.correlativo = i.correlativo AND id_pregunta IN (18581) AND e.visible in ('t','true'))";
        String edad = "(SELECT e.respuesta FROM enc_encuesta e WHERE e.id_asignacion = i.id_asignacion AND e.correlativo = i.correlativo AND id_pregunta IN (18173) AND e.visible in ('t','true'))";
        String genero = "(SELECT e.codigo_respuesta FROM enc_encuesta e WHERE e.id_asignacion = i.id_asignacion AND e.correlativo = i.correlativo AND id_pregunta IN (18172) AND e.visible in ('t','true'))";
//        String nombre = "(SELECT e.respuesta FROM enc_encuesta e WHERE e.id_asignacion = i.id_asignacion AND e.correlativo = i.correlativo AND id_pregunta IN (18581))";
//        String edad = "(SELECT e.respuesta FROM enc_encuesta e WHERE e.id_asignacion = i.id_asignacion AND e.correlativo = i.correlativo AND id_pregunta IN (18173))";
//        String genero = "(SELECT e.codigo_respuesta FROM enc_encuesta e WHERE e.id_asignacion = i.id_asignacion AND e.correlativo = i.correlativo AND id_pregunta IN (18172))";
        String observacion = "(SELECT count(*) FROM enc_observacion o WHERE i.id_asignacion = o.id_asignacion_hijo AND i.correlativo = o.correlativo_hijo)";
        String preguntas = "(SELECT count(*) FROM enc_encuesta e WHERE e.id_asignacion = i.id_asignacion AND e.correlativo = i.correlativo AND e.visible in ('t','true'))";

        query = "SELECT i.id_asignacion, i.correlativo, i.id_asignacion_padre, i.correlativo_padre, CAST(i.codigo AS Int) codigo, i.descripcion, i.estado," + nombre + " , " + edad + " , " + genero + " , " + observacion + " , " + preguntas + " \n" +
                "FROM enc_informante i\n" +
                "WHERE i.id_asignacion_padre = " + idPadre.id_asignacion + "\n" +
                "AND i.correlativo_padre = " + idPadre.correlativo + "\n" +
                "AND i.estado NOT IN ('ANULADO','VERIFICADO')\n" +
                "ORDER BY CAST(codigo AS Int)";
        try {
            cursor = conn.rawQuery(query, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (cursor.moveToFirst()) {
            do {
                Map<String, Object> row = new LinkedHashMap<>();
                row.put("id_asignacion", cursor.getInt(0));
                row.put("correlativo", cursor.getInt(1));
                row.put("id_asignacion_padre", cursor.getInt(2));
                row.put("correlativo_padre", cursor.getInt(3));
                row.put("codigo", cursor.getString(4));
                row.put("descripcion", cursor.getString(5));
                row.put("estado", cursor.getString(6));
                row.put("nombrePersona", cursor.getString(7) == null ? "--" : cursor.getString(7));
                row.put("edadPersona", cursor.getString(8) == null ? "--" : cursor.getString(8));
                row.put("genero", cursor.getString(9) == null ? "--" : cursor.getString(9));
                row.put("observacion", (Integer) cursor.getInt(10) == null ? 0 : cursor.getInt(10));
                row.put("preguntas", (Integer) cursor.getInt(11) == null ? 0 : cursor.getInt(11));
                res.add(row);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return res;
    }

    public ArrayList<Map<String, Object>> obtenerListadoViviendas(int idUpm, boolean hasSelected, int revisita) {
        ArrayList<Map<String, Object>> res = new ArrayList<>();
        try {
            String upm = "(SELECT respuesta FROM enc_encuesta WHERE id_asignacion = i.id_asignacion AND correlativo = i.correlativo AND id_pregunta = 18598) AS upm";
            String manzana = "(SELECT respuesta FROM enc_encuesta WHERE id_asignacion = i.id_asignacion AND correlativo = i.correlativo AND id_pregunta = 18600) AS manzana";
            String predio = "(SELECT respuesta FROM enc_encuesta WHERE id_asignacion = i.id_asignacion AND correlativo = i.correlativo AND id_pregunta = 18604) AS predio";
            String descripcion = "(SELECT respuesta FROM enc_encuesta WHERE id_asignacion = i.id_asignacion AND correlativo = i.correlativo AND id_pregunta = 18603) AS descripcion";
            String uso = "(SELECT respuesta FROM enc_encuesta WHERE id_asignacion = i.id_asignacion AND correlativo = i.correlativo AND id_pregunta = 18609) AS uso";
            String vivienda = "(SELECT respuesta FROM enc_encuesta WHERE id_asignacion = i.id_asignacion AND correlativo = i.correlativo AND id_pregunta = 18606) AS vivienda";
            String hombres = "(SELECT respuesta FROM enc_encuesta WHERE id_asignacion = i.id_asignacion AND correlativo = i.correlativo AND id_pregunta = 18611) AS hombres";
            String mujeres = "(SELECT respuesta FROM enc_encuesta WHERE id_asignacion = i.id_asignacion AND correlativo = i.correlativo AND id_pregunta = 18613) AS mujeres";
            String orden = "(SELECT e.respuesta FROM enc_encuesta e WHERE e.id_asignacion = i.id_asignacion AND e.correlativo = i.correlativo AND e.id_pregunta = "+Pregunta.getIdPregunta(Parametros.CODIGO_PREGUNTA_NRO_ORDEN_VIVIENDA)+" AND e.visible LIKE 't%') AS orden";
            String rol = "(SELECT id_rol FROM seg_usuario WHERE id_usuario = i.id_usuario) AS rol";
            String recorrido = "(SELECT e.respuesta FROM enc_encuesta e WHERE e.id_asignacion = i.id_asignacion AND e.correlativo = i.correlativo AND e.visible LIKE 't%' AND e.id_pregunta = "+Pregunta.getIdPregunta(Parametros.CODIGO_PREGUNTA_RECORRIDO_MANZANA)+") AS recorrido";
            String omitida = "(SELECT e.respuesta FROM enc_encuesta e WHERE e.id_asignacion = i.id_asignacion AND e.correlativo = i.correlativo AND e.visible LIKE 't%' AND e.id_pregunta = "+Pregunta.getIdPregunta(Parametros.CODIGO_PREGUNTA_VIVIENDA_OMITIDA)+") AS omitida";
            String orden_vivienda = "(SELECT respuesta FROM enc_encuesta WHERE id_asignacion = i.id_asignacion AND correlativo = i.correlativo AND id_pregunta = 18606) AS orden_vivienda";
            String jefe = "(SELECT respuesta FROM enc_encuesta WHERE id_asignacion = i.id_asignacion AND correlativo = i.correlativo AND id_pregunta = 18614) AS jefe";
            String hogar = "(SELECT respuesta FROM enc_encuesta WHERE id_asignacion = i.id_asignacion AND correlativo = i.correlativo AND id_pregunta = 18610) AS hogar";

            String query = "SELECT i.id_asignacion, i.correlativo, i.id_asignacion_padre, i.correlativo_padre, i.id_usuario, i.id_upm, CAST(i.codigo AS Int) codigo, i.descripcion, i.estado, i.usucre, i.feccre, "+orden+", "+rol+", "+recorrido+"," + omitida +","+ upm+","+manzana+","+predio+","+descripcion+","+uso+","+vivienda+","+hombres+","+mujeres+","+ orden_vivienda + ","+ jefe + ","+ hogar +"\n" +
                    "FROM enc_informante i JOIN ope_asignacion a ON i.id_upm=a.id_upm\n" +
                    "WHERE i.id_upm = " + idUpm + "\n" +
                    "AND i.estado <> 'ANULADO'\n" +
//                    "AND  a.revisita = " + revisita + "\n" +
                    "AND  i.id_asignacion = a.id_asignacion\n" +
                    "AND i.id_nivel = 2\n" +"ORDER BY cast(codigo as int)";
            Cursor cursor = conn.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                do {
                    Map<String, Object> row = new LinkedHashMap<>();
                    row.put("id_asignacion", cursor.getInt(0));
                    row.put("correlativo", cursor.getInt(1));
                    row.put("id_asignacion_padre", cursor.getInt(2));
                    row.put("correlativo_padre", cursor.getInt(3));
                    row.put("id_usuario", cursor.getInt(4));
                    row.put("id_upm", cursor.getInt(5));
                    row.put("codigo", cursor.getInt(6));
                    row.put("descripcion", cursor.getString(7));
                    row.put("estado", cursor.getString(8));
                    row.put("usucre", cursor.getString(9));
                    row.put("feccre", cursor.getLong(10));
                    row.put("id_rol", cursor.getLong(12));
                    row.put("recorrido_manzana",cursor.getString(13));
                    int voe = cursor.getInt(11);
                    String val;
                    if (hasSelected) {
                        val = voe == 0 ?(String.valueOf(cursor.getString(14)).equals("1")?"OMITIDA":"NO ENTRÓ A LA SELECCIÓN" ): "VOE: " + voe;
                    } else {
                        val = voe == 0 ? "NO ENTRARÁ A LA SELECCIÓN" : "ENTRARÁ A LA SELECCIÓN";
                    }
                    row.put("nro_orden_vivienda", val);
                    row.put("omitida",cursor.getString(14));
                    row.put("upm",cursor.getString(15));
                    row.put("manzana",cursor.getString(16));
                    row.put("predio",cursor.getString(17));
                    row.put("descripcion",cursor.getString(18));
                    row.put("uso",cursor.getString(19));
                    row.put("vivienda",cursor.getString(20));
                    row.put("hombres",cursor.getString(21));
                    row.put("mujeres",cursor.getString(22));
                    row.put("orden", cursor.getString(23));
                    row.put("jefe", cursor.getString(24));
                    row.put("hogar", cursor.getString(25));

                    res.add(row);
                } while (cursor.moveToNext());
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    public ArrayList<Map<String, Object>> obtenerListadoViviendasPrueba(int idUpm, boolean hasSelected) {
        ArrayList<Map<String, Object>> res = new ArrayList<>();
        try {

            Map<String, Object> row = new LinkedHashMap<>();
            row.put("id_asignacion", 2);
            row.put("correlativo", 2);
            row.put("id_asignacion_padre", 1);
            row.put("correlativo_padre", 1);
            row.put("id_usuario", 1);
            row.put("id_upm", 1);
            row.put("codigo", "111-1111111111-A");
            row.put("descripcion", "descripcion");
            row.put("estado", "ELABORADO");
            row.put("usucre", "");
            row.put("feccre", 1602460800);
            row.put("id_rol", 8);
            row.put("nro_orden_vivienda", "ENTRARÁ AL SORTEO");
            row.put("preguntas_respuestas_iniciales", "TUTORIAL: Datos de ejemplo");
            res.add(row);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    public static Map<String, Object> obtenerPreguntaInicialNivel4(int idNivel) {
        Map<String, Object> res = null;
        try {
            String query = "SELECT ep.id_pregunta, es.id_seccion FROM enc_pregunta ep\n" +
                    "JOIN enc_seccion es ON ep.inicial=1 AND ep.id_seccion=es.id_seccion AND es.id_nivel=" + idNivel + "\n" +
                    "ORDER by codigo_pregunta \n" +
                    "LIMIT 1";
            Cursor cursor = conn.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                res = new HashMap<>();
                res.put("id_pregunta", cursor.getString(0));
                res.put("id_seccion", cursor.getString(1));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    public static float porcentaje_avance(int preguntas, IdInformante idInformante) {
        int res = 0;
        float porcentaje = 0f;
        String query = "SELECT count(*)\n" +
                "FROM enc_encuesta\n" +
                "WHERE id_asignacion = " + idInformante.id_asignacion + "\n" +
                "AND correlativo = " + idInformante.correlativo + "\n" +
                "AND visible in ('t', 'true')";
        Cursor cursor = conn.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                res = cursor.getInt(0);
                if (res > 0) {
                    porcentaje = (res * 100) / preguntas;
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        return porcentaje;
    }

    public static String getPreguntasRespuestasInicialesLv(int seccion, IdInformante idInformante, String restringidas, boolean mostrarSoloCodigo, int idPreguntaIncidencia) {
        String query = "SELECT 2 AS orden, p.codigo_pregunta, p.pregunta, e.respuesta\n" +
                "FROM enc_pregunta p, enc_encuesta e\n" +
                "WHERE p.id_pregunta = e.id_pregunta\n" +
                "AND p.estado = 'ELABORADO'\n" +
                "AND e.visible like  't%'\n" +
                "AND p.inicial = 1\n" +
                "AND p.id_seccion = " + seccion + "\n" +
                "AND p.codigo_pregunta NOT IN (" + restringidas + ")\n" +
                "AND e.id_asignacion = " + idInformante.id_asignacion + "\n" +
                "AND e.correlativo = " + idInformante.correlativo + "\n" +
                "OR p.id_pregunta = " + idPreguntaIncidencia + "\n" +
                "UNION \n" +
                "SELECT 1 AS orden, p.codigo_pregunta, 'UPM TRABAJADO', e.respuesta\n" +
                "FROM enc_pregunta p, enc_encuesta e\n" +
                "WHERE p.id_pregunta = e.id_pregunta\n" +
                "AND e.visible like  't%'\n" +
                "AND p.estado = 'ELABORADO'\n" +
                "AND p.inicial = 1\n" +
                "AND p.id_seccion = " + seccion + "\n" +
                "AND p.codigo_pregunta IN ('"+Parametros.CODIGO_PREGUNTA_UPM_SELECCIONADA+"')\n" +
                "AND e.id_asignacion = " + idInformante.id_asignacion + "\n" +
                "AND e.correlativo = " + idInformante.correlativo + "\n" +
                "ORDER BY orden, p.codigo_pregunta";

        Cursor cursor = conn.rawQuery(query, null);
        StringBuilder strPreguntasRespuestasIniciales = new StringBuilder();
        if (cursor.moveToFirst()) {
            do {
                String str;
                if (mostrarSoloCodigo) {
                    str = cursor.getString(1);
                } else {
                    str = Html.fromHtml(cursor.getString(2)).toString();
                    str = str.length() > 30 ? str.substring(0, 30) + "..." : str;
                }
                strPreguntasRespuestasIniciales.append("<b><font color=#0C407A>" + str + "</font></b>").append(": ").append(cursor.getString(3)).append("<br>");
            } while (cursor.moveToNext());
            strPreguntasRespuestasIniciales.delete(strPreguntasRespuestasIniciales.length() - 4, strPreguntasRespuestasIniciales.length());
        }
        cursor.close();
        return strPreguntasRespuestasIniciales.toString();
    }

    public static String getPreguntasRespuestasIniciales(int seccion, IdInformante idInformante, String restringidas, boolean mostrarSoloCodigo, int idPreguntaIncidencia) {
        String query = "SELECT p.codigo_pregunta, p.pregunta, e.respuesta\n" +
                "FROM enc_pregunta p, enc_encuesta e\n" +
                "WHERE p.id_pregunta = e.id_pregunta\n" +
                "AND p.estado = 'ELABORADO'\n" +
                "AND p.inicial = 1\n" +
                "AND p.id_seccion = " + seccion + "\n" +
                "AND p.codigo_pregunta NOT IN (" + restringidas + ")\n" +
                "AND e.id_asignacion = " + idInformante.id_asignacion + "\n" +
                "AND e.correlativo = " + idInformante.correlativo + "\n" +
                "OR p.id_pregunta = " + idPreguntaIncidencia + "\n" +
                "ORDER BY p.codigo_pregunta" + "\n" +
                "LIMIT 5";

        Cursor cursor = conn.rawQuery(query, null);
        StringBuilder strPreguntasRespuestasIniciales = new StringBuilder();
        if (cursor.moveToFirst()) {
            do {
                String str;
                if (mostrarSoloCodigo) {
                    str = cursor.getString(0);
                } else {
                    str = Html.fromHtml(cursor.getString(1)).toString();
                    str = str.length() > 30 ? str.substring(0, 30) + "..." : str;
                }
                strPreguntasRespuestasIniciales.append("<b><font color=#0C407A>" + str + "</font></b>").append(": ").append(cursor.getString(2)).append("<br>");
            } while (cursor.moveToNext());
            strPreguntasRespuestasIniciales.delete(strPreguntasRespuestasIniciales.length() - 4, strPreguntasRespuestasIniciales.length());
        }
        cursor.close();
        return strPreguntasRespuestasIniciales.toString();
    }

    public static String getResumenVoe(int idUpm) {
        int idPreguntaNombre=Pregunta.getIdPregunta(Parametros.CODIGO_PREGUNTA_NOMBRE_JEFE_HOGAR);
        int idPreguntaVoe=Pregunta.getIdPregunta(Parametros.CODIGO_PREGUNTA_NRO_ORDEN_VIVIENDA);
        String omitida = "(SELECT e.respuesta FROM enc_encuesta e WHERE e.id_asignacion = i.id_asignacion AND e.correlativo = i.correlativo AND e.visible LIKE 't%' AND e.id_pregunta = "+Pregunta.getIdPregunta(Parametros.CODIGO_PREGUNTA_VIVIENDA_OMITIDA)+") AS omitida";
        String query = "SELECT e1.respuesta,e2.codigo_respuesta,"+omitida+"\n" +
                "from (SELECT id_asignacion,correlativo, codigo\n" +
                "FROM enc_informante WHERE id_upm="+idUpm+" AND estado='SELECCIONADO') i \n" +
                "JOIN (SELECT id_asignacion,correlativo,respuesta FROM enc_encuesta WHERE id_pregunta="+idPreguntaNombre+" AND visible like 't%') e1 on i.id_asignacion=e1.id_asignacion AND i.correlativo =e1.correlativo\n" +
                "JOIN (SELECT id_asignacion,correlativo,codigo_respuesta FROM enc_encuesta WHERE id_pregunta="+idPreguntaVoe+" AND visible like 't%') e2 on i.id_asignacion=e2.id_asignacion AND i.correlativo =e2.correlativo\n" +
                "ORDER BY CAST(e2.codigo_respuesta AS INT)";

        Cursor cursor = conn.rawQuery(query, null);
        StringBuilder strPreguntasRespuestasIniciales = new StringBuilder();
        strPreguntasRespuestasIniciales.append("<b><font color=#0C407A>" + "N°" + "</font></b>").append(": ").append("NOMBRE JEFE FAMILIA").append("<br>");
        if (cursor.moveToFirst()) {
            int limitOmitida = 0;
            do {
                if(cursor.getString(2)!=null){
                    limitOmitida++;
                }
                String text = cursor.getString(2)==null?cursor.getString(0):"<font color=#E64949>"+cursor.getString(0)+"</font>";
                strPreguntasRespuestasIniciales.append("<b><font color=#0C407A>" + cursor.getString(1) + "</font></b>").append(" : ").append(text).append("<br>");
                if(limitOmitida==3){
                    break;
                }
            } while (cursor.moveToNext());
//            strPreguntasRespuestasIniciales.delete(strPreguntasRespuestasIniciales.length() - 4, strPreguntasRespuestasIniciales.length());
        }
        cursor.close();
        return strPreguntasRespuestasIniciales.toString();
    }

//    public ArrayList<Map<String, Object>> resumen(IdInformante idInformante, int idSeccion) {
//        ArrayList<Map<String, Object>> res = null;
//        Cursor cursor = null;
//        try {
//            res = new ArrayList<>();
//            Map<Integer, IdInformante> jerarquia = jerarquia(idInformante);
//            boolean flag = false;
//            String subquery1, subquery2;
//            if (jerarquia.containsKey(2)) {
//                //SI EL INFORMANTE ES DE NIVEL 2
//                flag = true;
//                subquery1 = "(SELECT COUNT(*) FROM enc_encuesta e\n" +
//                        "JOIN enc_informante i ON e.id_asignacion = i.id_asignacion AND e.correlativo = i.correlativo AND i.id_usuario = " + Usuario.getUsuario() +"\n" +
//                        "AND (i.id_asignacion = "+jerarquia.get(2).id_asignacion +" AND i.correlativo = "+jerarquia.get(2).correlativo+")\n" +
//                        "WHERE e.id_pregunta IN (SELECT p.id_pregunta FROM enc_pregunta p WHERE p.id_seccion = s.id_seccion)\n" +
//                        "AND e.id_last > -1)";
//                subquery2 = "(SELECT id_pregunta FROM enc_observacion o WHERE o.id_tipo_obs <> 8 AND o.id_asignacion = "+jerarquia.get(2).id_asignacion+" AND o.correlativo = "+jerarquia.get(2).correlativo+" AND o.estado LIKE 'ELABORADO' AND id_pregunta IN (SELECT p.id_pregunta FROM enc_pregunta p WHERE s.id_seccion = p.id_seccion) LIMIT 1)";
//            }else {
//                subquery1 = "(SELECT COUNT(*) FROM enc_encuesta e\n" +
//                        "JOIN enc_informante i ON e.id_asignacion = i.id_asignacion AND e.correlativo = i.correlativo AND i.id_usuario = " + Usuario.getUsuario() +"\n" +
//                        "AND (i.id_asignacion = "+idInformante.id_asignacion +" AND i.correlativo = "+idInformante.correlativo+")\n" +
//                        "WHERE e.id_pregunta IN (SELECT p.id_pregunta FROM enc_pregunta p WHERE p.id_seccion = s.id_seccion)\n" +
//                        "AND e.id_last > -1)";
//                subquery2 = "(SELECT id_pregunta FROM enc_observacion o WHERE o.id_tipo_obs <> 8 AND o.id_asignacion = "+idInformante.id_asignacion+" AND o.correlativo = "+idInformante.correlativo+" AND o.estado LIKE 'ELABORADO' AND id_pregunta IN (SELECT p.id_pregunta FROM enc_pregunta p WHERE s.id_seccion = p.id_seccion) LIMIT 1)";
//            }
//
//            //INICIAMOS LA QUERY PRINCIPAL QUE SELECCIONARA LAS SECCIONES
//            String query = "SELECT 1 ord, null ord1, null ord2, id_seccion, codigo, seccion, null id_asignacion, null correlativo, null descripcion, id_nivel, null id_pregunta, null mostrar_ventana, null codigo_pregunta, null pregunta, null respuesta, null observacion, null fila, null id_usuario, null id_last, "+ subquery1 +" nro_respuestas,"+ subquery2 +" cod_obs, abierta\n" +
//                    "FROM enc_seccion s\n" +
//                    "WHERE s.id_nivel > 0\n";
//
//            Seccion seccion = new Seccion();
//            int d;
//            if (seccion.abrir(idSeccion)) {
//                d = seccion.get_id_nivel();
//            } else {
//                d = -1;
//            }
//            seccion.free();
//            if(flag) { //SI EL INFORMANTE ES DE NIVEL 2
//                //OBTIENE LOS INFORMANTES DE NIVEL 2, EN CASO LOS HAYA
//                query += "UNION\n" +
//                        "SELECT 2 ord, i.id_asignacion ord1, i.correlativo ord2, s.id_seccion, s.codigo, s.seccion, i.id_asignacion, i.correlativo, i.descripcion, i.id_nivel, null id_pregunta, null mostrar_ventana, null codigo_pregunta, null pregunta, null respuesta, null observacion, null fila, id_usuario, null id_last, (SELECT COUNT(*) FROM enc_encuesta WHERE id_asignacion = i.id_asignacion AND correlativo = i.correlativo AND id_last > -1 AND id_pregunta IN (SELECT p.id_pregunta FROM enc_pregunta p WHERE p.id_seccion = s.id_seccion)) nro_respuestas, (SELECT o.id_pregunta FROM enc_observacion o WHERE o.id_tipo_obs <> 8 AND o.id_asignacion_hijo = i.id_asignacion AND o.correlativo_hijo = i.correlativo AND o.estado LIKE 'ELABORADO' AND o.id_pregunta IN (SELECT p.id_pregunta FROM enc_pregunta p WHERE s.id_seccion = p.id_seccion) LIMIT 1) cod_obs, 0 abierta\n" +
//                        "FROM enc_seccion s, enc_informante i\n" +
//                        "WHERE s.id_nivel >= i.id_nivel\n" +
//                        "AND i.id_asignacion_padre = " + jerarquia.get(2).id_asignacion + "\n" +
//                        "AND i.correlativo_padre = " + jerarquia.get(2).correlativo + "\n" +
//                        "AND s.id_seccion = "+idSeccion+"\n" +
//                        "AND i.estado <> 'ANULADO'\n";
//            } else if(d == 2) {
//                query += "UNION\n" +
//                        "SELECT 2 ord, i.id_asignacion ord1, i.correlativo ord2, s.id_seccion, s.codigo, s.seccion, i.id_asignacion, i.correlativo, i.descripcion, i.id_nivel, null id_pregunta, null mostrar_ventana, null codigo_pregunta, null pregunta, null respuesta, null observacion, null fila, id_usuario, null id_last, (SELECT COUNT(*) FROM enc_encuesta WHERE id_asignacion = i.id_asignacion AND correlativo = i.correlativo AND id_last > -1 AND id_pregunta IN (SELECT p.id_pregunta FROM enc_pregunta p WHERE p.id_seccion = s.id_seccion)) nro_respuestas, (SELECT o.id_pregunta FROM enc_observacion o WHERE o.id_tipo_obs <> 8 AND o.id_asignacion_hijo = i.id_asignacion AND o.correlativo_hijo = i.correlativo AND o.estado LIKE 'ELABORADO' AND o.id_pregunta IN (SELECT p.id_pregunta FROM enc_pregunta p WHERE s.id_seccion = p.id_seccion) LIMIT 1) cod_obs, 0 abierta\n" +
//                        "FROM enc_seccion s, enc_informante i\n" +
//                        "WHERE s.id_nivel >= i.id_nivel\n" +
//                        "AND i.id_asignacion_padre = " + idInformante.id_asignacion + "\n" +
//                        "AND i.correlativo_padre = " + idInformante.correlativo + "\n" +
//                        "AND s.id_seccion = "+idSeccion+"\n" +
//                        "AND i.estado <> 'ANULADO'\n";
//            }
//
//            //OBTIENE LAS RESPUESTAS DEL INFORMANTE
//            query += "UNION\n" +
//                    "SELECT 2 ord, i.id_asignacion ord1, i.correlativo ord2, p.id_seccion, s.codigo, null seccion, e.id_asignacion, e.correlativo, null descripcion, p.id_nivel, p.id_pregunta, p.mostrar_ventana, p.codigo_pregunta, p.pregunta, e.respuesta, e.observacion, e.fila, id_usuario, e.id_last, null nro_respuestas, (SELECT o.id_pregunta FROM enc_observacion o WHERE o.id_tipo_obs <> 8 AND ((o.id_asignacion = "+idInformante.id_asignacion+" AND o.correlativo = "+idInformante.correlativo+") OR (o.id_asignacion_hijo = "+idInformante.id_asignacion+" AND o.correlativo_hijo = "+idInformante.correlativo+")) AND o.estado LIKE 'ELABORADO' AND o.id_pregunta = (SELECT pa.id_pregunta FROM enc_pregunta pa WHERE pa.id_pregunta = p.id_pregunta) LIMIT 1) cod_obs, abierta\n" +
//                    "FROM enc_seccion s, enc_pregunta p, enc_encuesta e, enc_informante i\n" +
//                    "WHERE s.id_seccion = p.id_seccion\n" +
//                    "AND p.id_pregunta = e.id_pregunta\n" +
//                    "AND e.id_asignacion = i.id_asignacion\n" +
//                    "AND e.correlativo = i.correlativo\n" +
//                    "AND e.id_last > -1\n" +
//                    "AND e.id_asignacion = " + idInformante.id_asignacion + "\n" +
//                    "AND e.correlativo = " + idInformante.correlativo + "\n" +
//                    "AND p.id_seccion = " + idSeccion + "\n" +
//                    "AND i.estado <> 'ANULADO'\n" +
//                    "ORDER BY codigo, ord, ord1, ord2, id_last";
//            //"ORDER BY codigo, ord, ord1, ord2, id_asignacion, correlativo, codigo_pregunta, fila";
//
//            cursor = conn.rawQuery(query, null);
//            if (cursor.moveToFirst()) {
//                do {
//                    Map<String, Object> row = new LinkedHashMap<>();
//                    row.put("id_seccion", cursor.isNull(3) ? null : cursor.getInt(3));
//                    row.put("seccion", cursor.isNull(5) ? null : cursor.getString(5).replace("<br />", " ").replace("<br>", " "));
//                    row.put("id_asignacion", cursor.isNull(6) ? null : cursor.getInt(6));
//                    row.put("correlativo", cursor.isNull(7) ? null : cursor.getInt(7));
//                    row.put("descripcion", cursor.isNull(8) ? null : cursor.getString(8));
//                    row.put("id_nivel", cursor.isNull(9) ? null : cursor.getInt(9));
//                    row.put("id_pregunta", cursor.isNull(10) ? null : cursor.getInt(10));
//                    row.put("mostrar_ventana", cursor.isNull(11) ? 0 : cursor.getInt(11));
//                    row.put("codigo_pregunta", cursor.isNull(12) ? null : cursor.getString(12));
//                    row.put("pregunta", cursor.isNull(13) ? null : (Pregunta.procesaEnunciado(new IdInformante(cursor.getInt(6), cursor.getInt(7)), cursor.getInt(16), cursor.getString(13)).replace("<br />","")).replace("<br>","") );
//                    row.put("respuesta", cursor.isNull(14) ? null : cursor.getString(14));
//                    row.put("observacion", cursor.isNull(15) ? null : cursor.getString(15));
//                    row.put("fila", cursor.isNull(16) ? null : cursor.getInt(16));
//                    row.put("id_usuario", cursor.isNull(17) ? null : cursor.getInt(17));
//                    row.put("nro_respuestas", cursor.isNull(19)||(cursor.getInt(9)==2 && cursor.isNull(6))? "" : cursor.getInt(19)+" resp.");
//                    row.put("cod_obs", cursor.isNull(20) ? null : cursor.getInt(20));
//                    row.put("abierta", cursor.isNull(21) ? null : cursor.getInt(21));
//                    res.add(row);
//                } while (cursor.moveToNext());
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        cursor.close();
//        return res;
//    }
//

    /**
     * Verifica si el informante indicado tiene hijos.
     *
     * @param idPadre Identificador del informante padre.
     * @return Verdadero si el informante tiene hijos o falso en caso contrario.
     */
    public boolean tieneHijos(IdInformante idPadre) {
        boolean res = false;
        String query = "SELECT id_asignacion, correlativo\n" +
                "FROM enc_informante\n" +
                "WHERE id_asignacion_padre = " + idPadre.id_asignacion + "\n" +
                "AND correlativo_padre = " + idPadre.correlativo + "\n" +
                "AND estado <> 'ANULADO'\n" +
                "ORDER BY id_asignacion, correlativo\n" +
                "LIMIT 1";

        Cursor cursor = conn.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                res = true;
            } while (cursor.moveToNext());
        }
        cursor.close();
        return res;
    }

    /**
     * Verifica si existen informantes pendientes de ser encuestados.
     *
     * @param idPadre Identificador del informante padre.
     * @return Verdadero si el informante tiene hijos o falso en caso contrario.
     */
    public Siguiente tienePendiente(IdInformante idPadre) {
        Siguiente res = null;
        String query = "SELECT id_asignacion, correlativo, id_nivel\n" +
                "FROM enc_informante\n" +
                "WHERE id_asignacion_padre = " + idPadre.id_asignacion + "\n" +
                "AND correlativo_padre = " + idPadre.correlativo + "\n" +
                "AND estado <> 'ANULADO'\n" +
                "ORDER BY id_asignacion, correlativo";

        Flujo flujoEntity = new Flujo();
        Cursor cursor = conn.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                IdInformante id = new IdInformante(cursor.getInt(0), cursor.getInt(1));
                int idPregunta = Encuesta.ultima(id);
                Siguiente idSiguiente = flujoEntity.siguiente(id, idPregunta);
//                if (idSiguiente.idSiguiente > 0 && idSiguiente.nivel == cursor.getInt(2)) {
//                    res = new Siguiente(new IdInformante(cursor.getInt(0), cursor.getInt(1)),
//                            idSiguiente.idSiguiente, idSiguiente.nivel);
//                    break;
//                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        return res;
    }

    // devuelve el nombre en string
    public static String getSelectKish(IdInformante idpadre) {
        String resp = "";
        Map<Integer, String> res = new LinkedHashMap<>();

        String strQuery = "SELECT i.id_asignacion + i.correlativo,e.respuesta \n" +
                "FROM enc_informante i \n" +
                "JOIN enc_encuesta e \n" +
                "ON (i.id_asignacion = e.id_asignacion AND i.correlativo = e.correlativo) \n" +
                "WHERE i.estado <> 'ANULADO' \n" +
                "AND i.id_asignacion_padre =" + idpadre.id_asignacion + " \n" +
                "AND i.correlativo_padre =" + idpadre.correlativo + " \n" +
                "AND e.id_pregunta = 18581 \n" +
                "AND (SELECT CAST(respuesta AS Int) \n" +
                "FROM enc_encuesta \n" +
                "WHERE id_asignacion = i.id_asignacion \n" +
                "AND correlativo = i.correlativo \n" +
                "AND id_pregunta = 18173) >= 15";

        Cursor cursor = conn.rawQuery(strQuery, null);

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(0);
                resp = cursor.getString(1);
                res.put(id, resp);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return resp;
    }

    public static Map<Integer, String> getSelectKisha(IdInformante idInformante) {

        Map<Integer, String> res = new LinkedHashMap<>();

        String strQuery = "SELECT i.id_asignacion + i.correlativo, i.id_asignacion || '|' || i.correlativo || '|' || e.codigo_respuesta || '|' || e.respuesta || '|' || i.codigo\n" +
                "FROM enc_informante i \n" +
                "JOIN enc_encuesta e \n" +
                "ON (i.id_asignacion = e.id_asignacion AND i.correlativo = e.correlativo) \n" +
                "WHERE i.estado <> 'ANULADO' \n" +
                "AND i.id_asignacion_padre =" + idInformante.id_asignacion + "\n" +
                "AND i.correlativo_padre =" + idInformante.correlativo + " \n" +
                "AND e.id_pregunta = 18581 \n" +
                "AND (SELECT CAST(respuesta AS Int) \n" +
                "FROM enc_encuesta \n" +
                "WHERE id_asignacion = i.id_asignacion \n" +
                "AND correlativo = i.correlativo \n" +
                "AND id_pregunta = 18173) >= 15 \n" +
                "ORDER BY CAST(i.codigo as Int)";

        Cursor cursor = conn.rawQuery(strQuery, null);
        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(0);
                String resp = cursor.getString(1);
                res.put(id, resp);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return res;
    }

    public static Map<Integer, String> getSelectKisha2(IdInformante idInformante) {

        Map<Integer, String> res = new LinkedHashMap<>();

        String strQuery = "SELECT i.id_asignacion + i.correlativo, i.id_asignacion || '|' || i.correlativo || '|' || e.codigo_respuesta || '|' || e.respuesta || '|' || i.codigo\n" +
                "FROM enc_informante i \n" +
                "JOIN enc_encuesta e \n" +
                "ON (i.id_asignacion = e.id_asignacion AND i.correlativo = e.correlativo) \n" +
                "WHERE i.estado <> 'ANULADO' \n" +
                "AND i.id_asignacion_padre =" + idInformante.id_asignacion + "\n" +
                "AND i.correlativo_padre =" + idInformante.correlativo + " \n" +
                "AND e.id_pregunta = 18581 \n" +
                "AND (SELECT CAST(respuesta AS Int) \n" +
                "FROM enc_encuesta \n" +
                "WHERE id_asignacion = i.id_asignacion \n" +
                "AND correlativo = i.correlativo \n" +
                "AND id_pregunta = 18173) >= 15 \n" +
                "AND i.codigo NOT IN (SELECT codigo_respuesta \n" +
                "FROM enc_encuesta \n" +
                "WHERE id_asignacion = i.id_asignacion_padre \n" +
                "AND correlativo = i.correlativo_padre \n" +
                "AND id_pregunta = 36557) order by CAST(i.codigo AS INT)";

        Cursor cursor = conn.rawQuery(strQuery, null);
        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(0);
                String resp = cursor.getString(1);
                res.put(id, resp);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return res;
    }

    public static Map<Integer, String> getSelectCarnet(IdInformante idInformante) {

        Map<Integer, String> res = new LinkedHashMap<>();

        String strQuery = "\n" +
                "SELECT i.codigo, i.codigo|| '|' || e.respuesta || '|' || i.codigo\n" +
                "                FROM enc_informante i \n" +
                "                JOIN enc_encuesta e \n" +
                "                ON i.id_asignAcion = e.id_asignacion AND i.correlativo=e.correlativo \n" +
                "                WHERE i.estado <> 'ANULADO' \n" +
                "                AND i.id_asignacion_padre=" + idInformante.id_asignacion + " AND i.correlativo_padre =" + idInformante.correlativo + "  \n" +
                "                AND e.id_pregunta = 2033 \n" +
//                "AND i.codigo NOT IN (SELECT codigo_respuesta \n" +
//                "FROM enc_encuesta \n" +
//                "WHERE id_asignacion = "+ idInformante.id_asignacion + " AND  correlativo=i.correlativo  \n" +
//                "AND id_pregunta = 15342) \n"+
                "                AND (SELECT CAST(codigo_respuesta AS Int) \n" +
                "                FROM enc_encuesta \n" +
                "                WHERE id_asignacion = i.id_asignacion AND correlativo=i.correlativo \n" +
                "                AND id_pregunta = 14992) = 1 \n" +
                "                AND i.id_asignacion*1000+i.correlativo NOT IN (SELECT id_asignacion*1000+ correlativo \n" +
                "                FROM enc_encuesta \n" +
                "                WHERE id_asignacion=" + idInformante.id_asignacion + " AND  correlativo=" + idInformante.correlativo + "\n" +
                "                AND id_pregunta = 15342 ) ORDER BY i.id_asignacion, i.correlativo";
        Log.d("query: ", String.valueOf(strQuery));
        Cursor cursor = conn.rawQuery(strQuery, null);
        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(0);
                String resp = cursor.getString(1);
                res.put(id, resp);
                Log.d("seleccion: ", String.valueOf(resp));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return res;
    }


    public static IdInformante getSelectCarnetId(IdInformante idInformante) {

        IdInformante result=null;

        String strQuery = "\n" +
                "SELECT i.id_asignacion, i.correlativo\n" +
                "                FROM enc_informante i \n" +
                "                JOIN enc_encuesta e \n" +
                "                ON i.id_asignAcion = e.id_asignacion AND i.correlativo=e.correlativo \n" +
                "                WHERE i.estado <> 'ANULADO' \n" +
                "                AND i.id_asignacion_padre=" + idInformante.id_asignacion + " AND i.correlativo_padre =" + idInformante.correlativo + "  \n" +
                "                AND e.id_pregunta = 2033 \n" +
                "AND i.codigo NOT IN (SELECT codigo_respuesta \n" +
                "FROM enc_encuesta \n" +
                "WHERE id_asignacion = "+ idInformante.id_asignacion + " AND  correlativo=i.correlativo  \n" +
                "AND id_pregunta = 15342) \n"+
                "                AND (SELECT CAST(codigo_respuesta AS Int) \n" +
                "                FROM enc_encuesta \n" +
                "                WHERE id_asignacion = i.id_asignacion AND correlativo=i.correlativo \n" +
                "                AND id_pregunta = 14992) = 1 \n" +
                "                AND i.id_asignacion*1000+i.correlativo NOT IN (SELECT id_asignacion*1000+ correlativo \n" +
                "                FROM enc_encuesta \n" +
                "                WHERE id_asignacion=" + idInformante.id_asignacion + " AND  correlativo=" + idInformante.correlativo + "\n" +
                "                AND id_pregunta = 15342 )  ORDER BY i.id_asignacion, i.correlativo LIMIT 1";

        Cursor cursor = conn.rawQuery(strQuery, null);
        if (cursor.moveToFirst()) {
            return new IdInformante(cursor.getInt(0),cursor.getInt(1));
        }
        cursor.close();
        return new IdInformante(0,0);
    }

    public static int getHaySelectCarnet(IdInformante idInformante) {

        int res=0;

        String strQuery = "\n" +
                "SELECT i.codigo, i.codigo|| '|' || e.respuesta || '|' || i.codigo\n" +
                "                FROM enc_informante i \n" +
                "                JOIN enc_encuesta e \n" +
                "                ON i.id_asignAcion = e.id_asignacion AND i.correlativo=e.correlativo \n" +
                "                WHERE i.estado <> 'ANULADO' \n" +
                "                AND i.id_asignacion_padre=" + idInformante.id_asignacion + " AND i.correlativo_padre =" + idInformante.correlativo + "  \n" +
                "                AND e.id_pregunta = 2033 \n" +
//                "                AND i.codigo NOT IN (SELECT codigo_respuesta \n" +
//                "                FROM enc_encuesta \n" +
//                "                WHERE id_asignacion = "+ idInformante.id_asignacion + " AND  correlativo=i.correlativo  \n" +
//                "                AND id_pregunta = 15342) \n"+
                "                AND (SELECT CAST(codigo_respuesta AS Int) \n" +
                "                FROM enc_encuesta \n" +
                "                WHERE id_asignacion = i.id_asignacion AND correlativo=i.correlativo \n" +
                "                AND id_pregunta = 14992) = 1 \n" +
                "                AND i.id_asignacion*1000+i.correlativo NOT IN (SELECT id_asignacion*1000+ correlativo \n" +
                "                FROM enc_encuesta \n" +
                "                WHERE id_asignacion=" + idInformante.id_asignacion + " AND  correlativo=" + idInformante.correlativo + "\n" +
                "                AND id_pregunta = 15342 )";

        Cursor cursor = conn.rawQuery(strQuery, null);
        res= cursor.getColumnCount();
        return res;
    }


    //devuelve el penultimo digito del Folio
    public Integer folio(IdInformante idInformante) {
        int penultimo = 1;
        String query = "" +
                "SELECT e.respuesta FROM enc_encuesta e JOIN enc_informante i ON e.id_asignacion = i.id_asignacion AND e.correlativo = i.correlativo\n" +
                "WHERE e.id_pregunta = 18596\n" +
                "AND i.id_asignacion = " + idInformante.id_asignacion + "\n" +
                "AND i.correlativo = " + idInformante.correlativo + "\n";
        Cursor cursor = conn.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            penultimo = Integer.parseInt(cursor.getString(0).replace(" ", ""));
            if (penultimo > 9) {
                penultimo = penultimo % 10;
            }
        }
        cursor.close();
        return penultimo;
    }


    //    /**
//     * Función encargada de registrar la información de una base en SQLite dentro de la base activa para enc_informante.
//     * @param path Ubicación del archivo SQLite.
//     * @param idUpm Identificador de la UPM.
//     * @param idUsuario Identificador del usuario que envía el archivo.
//     * @param idInformante Identificador de la boleta que se recibe.
//     * @param idNivel Nivel de datos que serán transmitidos.
//     * @return Cantidad de boletas insertadas/actualizadas.
//     * @throws Exception
//     */
//    public int insertData(String path, int idUpm, int idUsuario, IdInformante idInformante, int idNivel) throws Exception {
//        String message = null;
//        // Recupera la información de la base de datos recibida en path
//        String query = "SELECT i.*, CASE i.id_nivel WHEN 1 THEN 0 ELSE CAST(i.codigo AS Int) END nro\n" +
//                "FROM enc_informante i\n" +
//                "WHERE i.id_upm = " + idUpm;
//        if (idInformante.correlativo != 0) { // Solo una boleta con posible intercambio del propietario.
//            query += "\nAND ((i.id_asignacion = " + idInformante.id_asignacion + "\n" +
//                    "AND i.correlativo = " + idInformante.correlativo + ")\n" +
//                    "OR (i.id_asignacion_padre = " + idInformante.id_asignacion + "\n" +
//                    "AND i.correlativo_padre = " + idInformante.correlativo + "))";
//        }
//        if (idNivel == 0) { // Dependiendo del nivel viviendas o boletas.
//            query += "\nAND i.id_nivel = 0";
//        } else {
//            query += "\nAND i.id_nivel > 0";
//        }
//        query += "\nORDER BY nro";
//
//        DataBaseReader dbr = new DataBaseReader(context, path);
//        SQLiteDatabase cr = dbr.getReadableDatabase();
//
//        Cursor readerEmisor = cr.rawQuery(query, null);
//        ContentValues paquete;
//
//        int cant = 0;
//        int cont = 0;
//        conn.beginTransaction();
//        try {
//            while (readerEmisor.moveToNext()) {
//                paquete = new ContentValues();
//                for (int i = 0; i < fields.length; i++) {
//                    if (!readerEmisor.isNull(i)) {
//                        switch (types[i]) {
//                            case "integer":
//                                paquete.put(fields[i], readerEmisor.getInt(i));
//                                break;
//                            case "long":
//                                paquete.put(fields[i], readerEmisor.getLong(i));
//                                break;
//                            case "numeric":
//                            case "decimal":
//                                paquete.put(fields[i], readerEmisor.getDouble(i));
//                                break;
//                            case "nvarchar":
//                            case "text":
//                                paquete.put(fields[i], readerEmisor.getString(i));
//                                break;
//                            default:
//                                paquete.put(fields[i], readerEmisor.getString(i));
//                                break;
//                        }
//                    }
//                }
//                Cursor cursor = conn.query(nombreTabla, new String[]{fields[0], fields[1], fields[4]}, fields[0] + " = " + readerEmisor.getInt(0) + " AND " + fields[1] + " = " + readerEmisor.getInt(1), null, null, null, null);
//                int c = cursor.getCount();
//                int id = 0;
//                if (cursor.moveToNext()) {
//                    id = cursor.getInt(2); // Identificador del usuario propietario del registro.
//                }
//                cursor.close();
//                if (c == 1) {
//                    if (readerEmisor.getInt(readerEmisor.getColumnIndex("id_usuario")) == Usuario.getUsuario()) { // Informante emisor pertenece a destinatario.
//                        if (id == idUsuario) { // Informante destinatario pertenece a emisor.
//                            paquete.clear();
//                            paquete.put("id_usuario", Usuario.getUsuario()); // Actualiza dueño a destinatario.
//                            conn.update(nombreTabla, paquete, fields[0] + " = " + readerEmisor.getInt(0) + " AND " + fields[1] + " = " + readerEmisor.getInt(1), null);
//                        }
//                    } else { // Informante emisor no pertenece a destinatario.
//                        if (readerEmisor.getInt(readerEmisor.getColumnIndex("id_usuario")) == idUsuario && id != Usuario.getUsuario()) {
//                            if (readerEmisor.getInt(readerEmisor.getColumnIndex("id_nivel")) % 2 == 0) { // Informante que posee número de fila mantiene el codigo del destinatario.
//                                paquete.remove("codigo");
//                            }
//                            conn.update(nombreTabla, paquete, fields[0] + " = " + readerEmisor.getInt(0) + " AND " + fields[1] + " = " + readerEmisor.getInt(1), null);
//                            insertResp(path, new IdInformante(readerEmisor.getInt(0), readerEmisor.getInt(1))); // Solo si informante emisor no pertenece a destinatario.
//                        }
//                    }
//                } else {
//                    if (readerEmisor.getInt(readerEmisor.getColumnIndex("id_nivel")) == 2 ||
//                            readerEmisor.getInt(readerEmisor.getColumnIndex("id_usuario")) == idUsuario ||
//                            readerEmisor.getInt(readerEmisor.getColumnIndex("id_usuario")) == Usuario.getUsuario()) {
//                        if (readerEmisor.getInt(readerEmisor.getColumnIndex("id_nivel")) % 2 == 0) { // Nuevo registro debe enumerarse al final.
//                            paquete.put("codigo", String.valueOf(cont + 999));
//                            cont++;
//                        }
//                    /*if (paquete.get("estado").toString().equals("ELABORADO")) {
//                        paquete.put("estado", "EDITADO");
//                    }*/
//                        conn.insertOrThrow(nombreTabla, null, paquete);
//                        insertResp(path, new IdInformante(readerEmisor.getInt(0), readerEmisor.getInt(1))); // Solo si informante emisor no se encuentra registrado aún.
//                    }
//                }
//                cant++;
//                paquete.clear();
//            }
//            readerEmisor.close();
//            // Actualiza el codigo del informante
//            if (idNivel == 0) {
//                query = "UPDATE enc_informante\n" +
//                        "SET codigo = (SELECT count(*) FROM enc_informante i WHERE i.estado <> 'ANULADO' AND i.id_nivel = 0 AND i.id_upm = enc_informante.id_upm AND CAST(i.codigo AS Int) <= CAST(enc_informante.codigo AS Int))\n" +
//                        "WHERE estado <> 'ANULADO'\n" +
//                        "AND id_nivel = 0";
//            } else {
//                query = "UPDATE enc_informante\n" +
//                        "SET codigo = (SELECT count(*) FROM enc_informante i WHERE i.estado <> 'ANULADO' AND i.id_asignacion_padre = enc_informante.id_asignacion_padre AND i.correlativo_padre = enc_informante.correlativo_padre AND CAST(i.codigo AS Int) <= CAST(enc_informante.codigo AS Int))\n" +
//                        "WHERE estado <> 'ANULADO'\n" +
//                        "AND id_nivel = 2";
//            }
//            conn.execSQL(query);
//            conn.setTransactionSuccessful();
//        } catch (SQLException ex) {
//            ex.printStackTrace();
//            message = ex.getMessage() + "<br/>" + nombreTabla;
//        } finally {
//            conn.endTransaction();
//        }
//        cr.close();
//        dbr.close();
//        if (message == null) {
//            return cant;
//        } else {
//            throw new Exception(message);
//        }
//    }
//
//    /**
//     * Función encargada de registrar la información de una base en SQLite dentro de la base activa para enc_encuesta.
//     * @param path Ubicación del archivo SQLite.
//     * @param idInformante Identificador de la boleta que se recibe.
//     * @return Cantidad de registros/respuestas insertadas/actualizadas.
//     * @throws Exception
//     */
//    public int insertResp(String path, IdInformante idInformante) throws Exception {
//        String query = "SELECT e.*\n" +
//                "FROM enc_encuesta e, enc_informante i\n" +
//                "WHERE e.id_asignacion = i.id_asignacion\n" +
//                "AND e.correlativo = i.correlativo\n" +
//                "AND i.estado <> 'ANULADO'\n" +
//                "AND i.id_asignacion = ?\n" +
//                "AND i.correlativo = ?";
//
//        DataBaseReader dbr = new DataBaseReader(context, path);
//        SQLiteDatabase cr = dbr.getReadableDatabase();
//
//        Cursor reader = cr.rawQuery(query, new String[]{String.valueOf(idInformante.id_asignacion), String.valueOf(idInformante.correlativo)});
//        ContentValues paquete;
//
//        int cant = 0;
//        while (reader.moveToNext()) {
//            paquete = new ContentValues();
//            for (int i = 0; i < encFields.length; i++) {
//                if (!reader.isNull(i)) {
//                    switch (encTypes[i]) {
//                        case "integer":
//                            paquete.put(encFields[i], reader.getInt(i));
//                            break;
//                        case "bigint":
//                            paquete.put(encFields[i], reader.getLong(i));
//                            break;
//                        case "nvarchar":
//                            paquete.put(encFields[i], reader.getString(i));
//                            break;
//                        case "text":
//                            paquete.put(encFields[i], reader.getString(i));
//                            break;
//                        case "long":
//                            paquete.put(encFields[i], reader.getLong(i));
//                            break;
//                        case "numeric":
//                            paquete.put(encFields[i], reader.getDouble(i));
//                            break;
//                        default:
//                            paquete.put(encFields[i], reader.getString(i));
//                            break;
//                    }
//                }
//            }
//            Cursor cursor = conn.query("enc_encuesta", new String[]{encFields[0], encFields[1], encFields[2], encFields[3]}, encFields[0] + " = " + reader.getInt(0) + " AND " + encFields[1] + " = " + reader.getInt(1) + " AND " + encFields[2] + " = " + reader.getInt(2) + " AND " + encFields[3] + " = " + reader.getInt(3), null, null, null, null);
//            int c = cursor.getCount();
//            cursor.close();
//            if (c == 1) {
//                conn.update("enc_encuesta", paquete, encFields[0] + " = " + reader.getInt(0) + " AND " + encFields[1] + " = " + reader.getInt(1) + " AND " + encFields[2] + " = " + reader.getInt(2) + " AND " + encFields[3] + " = " + reader.getInt(3), null);
//            } else {
//                conn.insertOrThrow("enc_encuesta", null, paquete);
//            }
//            cant++;
//            paquete.clear();
//        }
//        reader.close();
//        return cant;
//    }
//
//    public static int reordenarLV(int idUpm, String[] logins) {
//        int codigo = 1;
//        conn.beginTransaction();
//        try {
//            for (String login : logins) {
//                String query = "SELECT id_asignacion, correlativo\n" +
//                        "FROM enc_informante i, seg_usuario u\n" +
//                        "WHERE i.id_usuario = u.id_usuario\n" +
//                        "AND id_nivel = 0\n" +
//                        "AND i.estado <> 'ANULADO'\n" +
//                        "AND id_upm = " + idUpm + "\n" +
//                        "AND login = '" + login + "'\n" +
//                        "ORDER BY CAST(codigo AS Int)";
//                Cursor cursor = conn.rawQuery(query, null);
//                while (cursor.moveToNext()) {
//                    ContentValues paquete = new ContentValues();
//                    paquete.put("codigo", String.valueOf(codigo));
//                    conn.update("enc_informante", paquete, "id_asignacion = " + cursor.getInt(0) + " AND correlativo = " + cursor.getInt(1), null);
//                    codigo++;
//                }
//                cursor.close();
//            }
//            conn.setTransactionSuccessful();
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        } finally {
//            conn.endTransaction();
//        }
//        return --codigo;
//    }
//
    public static int contarBoletas(int idUpm) {
        String query = "SELECT *\n" +
                "FROM enc_informante\n" +
                "WHERE id_nivel = 3\n" +
                "AND id_upm = " + idUpm;

        Cursor cursor = conn.rawQuery(query, null);
        int res = cursor.getCount();
        cursor.close();
        return res;
    }

    public static int contarSeleccionados(int idUpm) {
        String query = "SELECT *\n" +
                "FROM enc_informante\n" +
                "WHERE id_nivel = 2\n" +
                "AND id_upm = " + idUpm + "\n" +
                "AND estado LIKE 'SELECCIONADO%'";

        Cursor cursor = conn.rawQuery(query, null);
        int res = cursor.getCount();
        cursor.close();
        return res;
    }

    //
//    public static int valorVOE(IdInformante idInformante) {
//        int res = -1;
//        String query = "SELECT e.respuesta\n" +
//                "FROM enc_pregunta p, enc_encuesta e\n" +
//                "WHERE p.id_pregunta = e.id_pregunta\n" +
//                "AND p.id_tipo_pregunta = 19\n" +
//                "AND p.id_proyecto = " + Usuario.getProyecto() + "\n" +
//                "AND e.id_asignacion = " + idInformante.id_asignacion + "\n" +
//                "AND e.correlativo = " + idInformante.correlativo;
//        Cursor cursor = conn.rawQuery(query, null);
//        if (cursor.moveToNext()) {
//            res = cursor.getInt(0);
//        }
//        cursor.close();
//        return res;
//    }
//
    // VERIFICA SI LA ASIGNACION TIENEN ALGUN INFORMANTE DE NIVEL -1
    public static boolean listadoViviendasInicializado(int idAsignacion) {
        boolean res = false;
        String query = "SELECT i.codigo\n" +
                "FROM enc_informante i\n" +
                "WHERE i.id_asignacion = " + idAsignacion + "\n" +
                "AND i.id_nivel = 1\n" +
                "AND i.estado <> 'ANULADO'";
        Cursor cursor = conn.rawQuery(query, null);
        if (cursor.moveToNext()) {
            res = cursor.getInt(0) >= 0;
        }
        cursor.close();
        return res;
    }
//
//    public static void generarBoletas(int idUpm) {
//        int idAsignacion = Asignacion.get_asignacion(idUpm, Usuario.getUsuario());
//        int correlativo = 1;
//        String query = "SELECT coalesce(max(correlativo), 0) + 1 correlativo\n" +
//                "FROM enc_informante\n" +
//                "WHERE id_asignacion = " + idAsignacion;
//
//        Cursor cursor = conn.rawQuery(query, null);
//        if (cursor.moveToNext()) {
//            correlativo = cursor.getInt(0);
//        }
//        cursor.close();
//
//        query = "SELECT i.id_asignacion, i.correlativo, u.codigo || '-' || substr('00' || e.respuesta, -3, 3) folio, i.id_usuario, e.respuesta, i.estado\n" +
//                "FROM cat_upm u, enc_informante i, enc_encuesta e, enc_pregunta p\n" +
//                "WHERE u.id_upm = i.id_upm\n" +
//                "AND i.id_asignacion = e.id_asignacion\n" +
//                "AND i.correlativo = e.correlativo\n" +
//                "AND e.id_pregunta = p.id_pregunta\n" +
//                "AND i.id_nivel = 0\n" +
//                "AND i.id_upm = " + idUpm + "\n" +
//                "AND p.id_tipo_pregunta = 19\n" +
//                "AND i.estado LIKE 'SELECCIONADO%'";
//
//        conn.beginTransaction();
//        try {
//            cursor = conn.rawQuery(query, null);
//            while (cursor.moveToNext()) {
//                query = "SELECT e.respuesta\n" +
//                        "FROM enc_pregunta p, enc_encuesta e\n" +
//                        "WHERE p.id_pregunta = e.id_pregunta\n" +
//                        "AND p.codigo_pregunta = 'A_08'\n" +
//                        "AND id_asignacion = " + cursor.getInt(0) + "\n" +
//                        "AND correlativo = " + cursor.getInt(1);
//
//                int nroHogares;
//                Cursor cursor2 = conn.rawQuery(query, null);
//                if (cursor2.moveToNext()) {
//                    nroHogares = cursor2.getInt(0);
//                } else {
//                    throw new Exception("No se encontró el número de hogares.");
//                }
//                cursor2.close();
//
//                for (int i = 1; i <= nroHogares; i++) {
//                    query = "SELECT e.respuesta\n" +
//                            "FROM enc_pregunta p, enc_encuesta e\n" +
//                            "WHERE p.id_pregunta = e.id_pregunta\n" +
//                            "AND p.codigo_pregunta = 'A_13'\n" +
//                            "AND id_asignacion = " + cursor.getInt(0) + "\n" +
//                            "AND correlativo = " + cursor.getInt(1);
//
//                    cursor2 = conn.rawQuery(query, null);
//                    String jefe = "";
//                    if (cursor2.moveToNext()) {
//                        jefe = cursor2.getString(0);
//                    }
//                    cursor2.close();
//
//                    ContentValues paquete = new ContentValues();
//                    paquete.put("id_asignacion", idAsignacion);
//                    paquete.put("correlativo", correlativo);
//                    paquete.put("id_usuario", cursor.getInt(3));
//                    paquete.put("id_upm", idUpm);
//                    paquete.put("id_nivel", 1);
//                    paquete.put("codigo", cursor.getString(2) + i);
//                    if (cursor.getString(5).equals("SELECCIONADO")) {
//                        paquete.put("descripcion", jefe + " - " + Usuario.getLogin(cursor.getInt(3)) + " Varon: NO");
//                    } else {
//                        paquete.put("descripcion", jefe + " - " + Usuario.getLogin(cursor.getInt(3)) + " Varon: SI");
//                    }
//                    paquete.put("usucre", Usuario.getLogin());
//                    conn.insertOrThrow("enc_informante", null, paquete);
//
//                    query = "SELECT id_pregunta\n" +
//                            "FROM enc_pregunta\n" +
//                            "WHERE id_proyecto = " + Usuario.getProyecto() + "\n" +
//                            "AND id_nivel = 1\n" +
//                            "AND mostrar_ventana = 1\n" +
//                            "ORDER BY codigo_pregunta";
//
//                    int c = 1;
//                    cursor2 = conn.rawQuery(query, null);
//                    while (cursor2.moveToNext()) {
//                        paquete.clear();
//                        paquete.put("id_asignacion", idAsignacion);
//                        paquete.put("correlativo", correlativo);
//                        paquete.put("id_pregunta", cursor2.getInt(0));
//                        paquete.put("fila", 1);
//                        paquete.put("id_respuesta", 0);
//                        paquete.put("codigo_respuesta", "0");
//                        switch (c) {
//                            case 1:
//                                paquete.put("respuesta", cursor.getInt(4));
//                                break;
//                            case 2:
//                                paquete.put("respuesta", i);
//                                break;
//                            case 3: {
//                                query = "SELECT e.id_respuesta, e.codigo_respuesta, e.respuesta\n" +
//                                        "FROM enc_pregunta p, enc_encuesta e\n" +
//                                        "WHERE p.id_pregunta = e.id_pregunta\n" +
//                                        "AND p.codigo_pregunta = 'A_000'\n" +
//                                        "AND id_asignacion = " + cursor.getInt(0) + "\n" +
//                                        "AND correlativo = " + cursor.getInt(1);
//
//                                Cursor cursor3 = conn.rawQuery(query, null);
//                                if (cursor3.moveToNext()) {
//                                    paquete.put("id_respuesta", cursor3.getInt(0));
//                                    paquete.put("codigo_respuesta", cursor3.getString(1));
//                                    paquete.put("respuesta", cursor3.getString(2));
//                                } else {
//                                    paquete.put("respuesta", "");
//                                }
//                                cursor3.close();
//                                break;
//                            }
//                            /*case 4: {
//                                query = "SELECT r2.id_respuesta, e.codigo_respuesta, e.respuesta\n" +
//                                        "FROM enc_pregunta p, enc_encuesta e, enc_respuesta r, enc_respuesta r2\n" +
//                                        "WHERE p.id_pregunta = e.id_pregunta\n" +
//                                        "AND e.id_respuesta = r.id_respuesta\n" +
//                                        "AND r.codigo = r2.codigo\n" +
//                                        "AND r2.id_pregunta = 16788\n" +
//                                        "AND p.codigo_pregunta = 'A_001'\n" +
//                                        "AND id_asignacion = " + cursor.getInt(0) + "\n" +
//                                        "AND correlativo = " + cursor.getInt(1);
//
//                                Cursor cursor3 = conn.rawQuery(query, null);
//                                if (cursor3.moveToNext()) {
//                                    paquete.put("id_respuesta", cursor3.getInt(0));
//                                    paquete.put("codigo_respuesta", cursor3.getString(1));
//                                    paquete.put("respuesta", cursor3.getString(2));
//                                } else {
//                                    paquete.put("respuesta", "");
//                                }
//                                cursor3.close();
//                                break;
//                            }*/
//                            case 4: {
//                                query = "SELECT r2.id_respuesta, e.codigo_respuesta, e.respuesta\n" +
//                                        "FROM enc_pregunta p, enc_encuesta e, enc_respuesta r, enc_respuesta r2\n" +
//                                        "WHERE p.id_pregunta = e.id_pregunta\n" +
//                                        "AND e.id_respuesta = r.id_respuesta\n" +
//                                        "AND r.codigo = r2.codigo\n" +
//                                        "AND r2.id_pregunta = 16789\n" +
//                                        "AND p.codigo_pregunta = 'A_002'\n" +
//                                        "AND id_asignacion = " + cursor.getInt(0) + "\n" +
//                                        "AND correlativo = " + cursor.getInt(1);
//
//                                Cursor cursor3 = conn.rawQuery(query, null);
//                                if (cursor3.moveToNext()) {
//                                    paquete.put("id_respuesta", cursor3.getInt(0));
//                                    paquete.put("codigo_respuesta", cursor3.getString(1));
//                                    paquete.put("respuesta", cursor3.getString(2));
//                                } else {
//                                    paquete.put("respuesta", "");
//                                }
//                                cursor3.close();
//                                break;
//                            }
//                            case 5: {
//                                query = "SELECT e.respuesta\n" +
//                                        "FROM enc_pregunta p, enc_encuesta e\n" +
//                                        "WHERE p.id_pregunta = e.id_pregunta\n" +
//                                        "AND p.codigo_pregunta = 'A_00A'\n" +
//                                        "AND id_asignacion = " + cursor.getInt(0) + "\n" +
//                                        "AND correlativo = " + cursor.getInt(1);
//
//                                Cursor cursor3 = conn.rawQuery(query, null);
//                                if (cursor3.moveToNext()) {
//                                    paquete.put("respuesta", cursor3.getString(0));
//                                } else {
//                                    paquete.put("respuesta", "");
//                                }
//                                cursor3.close();
//                                break;
//                            }
//                            case 6:
//                                paquete.put("respuesta", jefe);
//                                break;
//                            case 7:
//                                if (cursor.getString(5).equals("SELECCIONADO")) {
//                                    paquete.put("id_respuesta", 14171);
//                                    paquete.put("codigo_respuesta", "02");
//                                    paquete.put("respuesta", "NO");
//                                } else {
//                                    paquete.put("id_respuesta", 14170);
//                                    paquete.put("codigo_respuesta", "01");
//                                    paquete.put("respuesta", "SI");
//                                }
//                                break;
//                            default:
//                                paquete.put("respuesta", "");
//                                break;
//                        }
//                        paquete.put("observacion", "");
//                        paquete.put("id_last", c);
//                        paquete.put("usucre", Usuario.getLogin());
//                        conn.insertOrThrow("enc_encuesta", null, paquete);
//                        c++;
//                    }
//                    cursor2.close();
//                    correlativo++;
//                }
//            }
//            cursor.close();
//
//            conn.setTransactionSuccessful();
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        } finally {
//            conn.endTransaction();
//        }
//    }

    public static boolean exists(String codigo, IdInformante idInformante) {
        String strQuery = "SELECT id_asignacion\n" +
                "FROM enc_informante\n" +
                "WHERE id_nivel = 3\n" +
                "AND estado <> 'ANULADO'\n" +
                "AND codigo = '" + codigo + "'\n" +
                "AND id_asignacion || '_' || correlativo <> '" + idInformante.id_asignacion + "_" + idInformante.correlativo + "'";
        Cursor cursor = conn.rawQuery(strQuery, null);
        boolean flag = cursor.moveToFirst();
        cursor.close();
        return flag;
    }

    //Verifica existencia de cabecera
    public static boolean existeCabecera( IdInformante idInformante) {
        String strQuery = "SELECT id_asignacion\n" +
                "FROM enc_informante\n" +
                "WHERE id_nivel = 1\n" +
                "AND estado <> 'ANULADO'\n" +
                "AND id_asignacion =" + idInformante.id_asignacion +" AND correlativo<>0";
        Cursor cursor = conn.rawQuery(strQuery, null);
        boolean flag = cursor.moveToFirst();
        cursor.close();
        return flag;
    }
    public static String getObservacionUPM( int idUpm) {
        int idPreguntaObservacion=Pregunta.getIdPregunta(Parametros.CODIGO_PREGUNTA_OBSERVACION_UPM);
        String resp="";
        String strQuery = "SELECT e.respuesta\n" +
                "FROM (SELECT id_asignacion, correlativo\n" +
                "FROM enc_informante\n" +
                "WHERE id_upm = "+idUpm+"\n" +
                "AND id_nivel= 1\n" +
                "AND estado <> 'ANULADO') i\n" +
                "JOIN enc_encuesta e ON i.id_asignacion=e.id_asignacion AND i.correlativo=e.correlativo AND e.id_pregunta="+idPreguntaObservacion+" LIMIT 1\n";
        Cursor cursor = conn.rawQuery(strQuery, null);
        if(cursor.moveToFirst()){
                resp=cursor.getString(0);
        }
        cursor.close();
        return resp;
    }
    public static boolean isClosed(IdInformante idInformante) {
        String strQuery = "SELECT i.id_asignacion\n" +
                "FROM enc_informante i, enc_observacion o\n" +
                "WHERE i.estado <> 'ANULADO'\n" +
                "AND i.id_asignacion = " + idInformante.id_asignacion + "\n" +
                "AND i.correlativo = " + idInformante.correlativo + "\n" +
                "AND i.id_asignacion = o.id_asignacion\n" +
                "AND i.correlativo = o.correlativo\n" +
                "AND o.id_tipo_obs = 8\n" +
                "AND o.feccre = (SELECT MAX(obs.feccre) FROM enc_observacion obs WHERE obs.id_asignacion = i.id_asignacion AND obs.correlativo = i.correlativo)";
        Cursor cursor = conn.rawQuery(strQuery, null);
        boolean flag = cursor.moveToFirst();
        cursor.close();
        return flag;
    }

    public static String duplicados(int idUpm) {
        Cursor cursor = null;
        String res = null;
        String strQuery = "SELECT codigo\n" +
                "FROM enc_informante\n" +
                "WHERE id_nivel = 3\n" +
                "AND estado <> 'ANULADO'\n" +
                "AND id_upm = " + idUpm + "\n" +
                "GROUP BY codigo\n" +
                "HAVING count(*) > 1";
        cursor = conn.rawQuery(strQuery, null);
        res = null;
        while (cursor.moveToNext()) {
            if (res == null) {
                res = "Folios Duplicados:";
            }
            res += "\n" + cursor.getString(0);
        }
        cursor.close();
        return res;
    }

    public static int obtenerNroUltimoVoe(int idUpm) {
        Cursor cursor = null;
        int res = 0;
        String strQuery = "SELECT MAX(CAST(ee.respuesta AS int)) valor\n" +
                "FROM\n" +
                "(SELECT * \n" +
                "FROM enc_informante \n" +
                "WHERE id_nivel=2 \n" +
//                "\tAND estado LIKE 'SELECCIONADO%' \n" +
                "AND estado != 'ANULADO' \n" +
                "AND id_upm=" + idUpm + ") ei JOIN enc_encuesta ee\n" +
                "ON ei.id_asignacion=ee.id_asignacion AND ei.correlativo=ee.correlativo\n" +
                "AND  ee.id_pregunta=" + Pregunta.getIdPregunta(Parametros.CODIGO_PREGUNTA_NRO_ORDEN_VIVIENDA)+ " AND ee.codigo_respuesta != '0'";
        cursor = conn.rawQuery(strQuery, null);
        if (cursor.moveToFirst()) {
            res = cursor.getInt(0);
        }
        cursor.close();
        return res;
    }

    public static Map<Integer, String> obtenerIndexConsolidacion(int idUsuario) {
        Map<Integer, String> res = new LinkedHashMap<>();
        int index = 0;
        String strQuery = "select 'ei.id_asignacion = '|| id_asignacion ||' and ei.correlativo = '|| correlativo \n" +
                "from enc_informante \n" +
                "where id_usuario = " + idUsuario;
        Cursor cursor = conn.rawQuery(strQuery, null);
        while (cursor.moveToNext()) {
            res.put(index, cursor.getString(0));
            index++;
        }
        cursor.close();
        return res;
    }

    public static String obtenerDatosConsolidacion(String condicion) {
        String res = "";
        try {
            String query = "SELECT ('[{'||'\"id_asignacion\":\"'||id_asignacion||  \n" +
                    "                    '\",\"correlativo\":\"'||correlativo|| '\",\"id_asignacion_padre\":\"'|| ifnull(id_asignacion_padre,'')|| \n" +
                    "                    '\",\"correlativo_padre\":\"'|| ifnull(correlativo_padre,'')|| \n" +
                    "                    '\",\"id_usuario\":\"'|| id_usuario|| \n" +
                    "                    '\",\"id_upm\":\"'|| id_upm||\n" +
                    "                    '\",\"id_nivel\":\"'|| id_nivel||\n" +
                    "                    '\",\"latitud\":\"'|| latitud||\n" +
                    "                    '\",\"longitud\":\"'|| longitud|| '\",\"codigo\":\"'|| codigo|| '\",\"descripcion\":\"'|| REPLACE(REPLACE(REPLACE(REPLACE(ifnull(descripcion, ''), '\"', ''),CHaR(10),' ') ,CHaR(13),' ') ,' ',' ')|| '\",\"estado\":\"'|| estado|| '\",\"usucre\":\"'|| usucre|| '\",\"feccre\":\"'|| feccre|| '\",\"usumod\":\"'|| ifnull(usumod, '')|| '\",\"fecmod\":\"'|| ifnull(fecmod, '') ||'\"}'\n" +
                    "||',['||(select group_concat(('{\"id_asignacion\":\"'||ee.id_asignacion|| \n" +
                    "'\",\"correlativo\":\"'||ee.correlativo||\n" +
                    "'\",\"id_pregunta\":\"'|| ee.id_pregunta||\n" +
                    "'\",\"codigo_respuesta\":\"'|| ee.codigo_respuesta||\n" +
                    "'\",\"respuesta\":\"'|| REPLACE(REPLACE(REPLACE(replace(ee.respuesta, '\"',''),CHaR(10),' ') ,CHaR(13),' ') ,' ',' ')||\n" +
//                    "'\",\"observacion\":\"'|| ifnull(ee.observacion, '')||\n" +
                    "'\",\"observacion\":\"'|| REPLACE(REPLACE(REPLACE(replace(ee.observacion, '\"',''),CHaR(10),' ') ,CHaR(13),' ') ,' ',' ')||\n" +
                    "'\",\"latitud\":\"'|| ee.latitud||\n" +
                    "'\",\"longitud\":\"'|| ee.longitud|| \n" +
                    "'\",\"visible\":\"'|| ee.visible||\n" +
                    "'\",\"estado\":\"'|| ee.estado|| '\",\"usucre\":\"'|| ee.usucre|| '\",\"feccre\":\"'|| ee.feccre|| '\",\"usumod\":\"'|| ifnull(usumod, '')|| '\",\"fecmod\":\"'|| ifnull(fecmod, '') ||'\"}'), ',')\n" +
                    "FROM enc_encuesta ee where ee.id_asignacion=ei.id_asignacion and  ee.correlativo = ei.correlativo) || ']]') as respuesta from enc_informante ei where " + condicion;

            Cursor cursor = conn.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                do {
                    res = cursor.getString(0);
                } while (cursor.moveToNext());
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }


    /**
     * Obtiene los datos del informante y su respuesta.
     *
     * @param filter List<Map<String, Object>> entidad con lista de registros de la tabla
     * @return Json array
     */
    public JSONArray listadoInformanteRespuestas(String filter) {
        List<Map<String, Object>> data = obtenerListado(filter);
        JSONArray resp = new JSONArray(data);
        for (int i = 0; i < resp.length(); i++
        ) {


        }
        return resp;
    }

    public static boolean esOmitida(int idAsignacion, int correlativo) {
        Cursor cursor = null;
        boolean res = false;
        String strQuery = "SELECT COUNT(*) FROM\n" +
                "(SELECT * \n" +
                "FROM enc_informante \n" +
                "WHERE id_nivel=2 \n" +
                "AND estado LIKE '%ELABORADO%' \n" +
                "AND id_asignacion= " + idAsignacion + " and correlativo = " + correlativo + ") ei JOIN enc_encuesta ee\n" +
                "ON ei.id_asignacion=ee.id_asignacion AND ei.correlativo=ee.correlativo\n" +
                "AND ee.id_pregunta=" + Pregunta.getIdPregunta(Parametros.CODIGO_PREGUNTA_VIVIENDA_OMITIDA) +" \n" +
                "AND ee.codigo_respuesta = '1'";

        Log.d("esOmitida", strQuery);
        cursor = conn.rawQuery(strQuery, null);
        if (cursor.moveToFirst()) {
            res = cursor.getInt(0)>0;
        }
        cursor.close();
        return res;
    }


    @SuppressWarnings("unused")
    public IdInformante get_id_informante() {
        return new IdInformante(filaActual.getInt(filaActual.getColumnIndex("id_asignacion")), filaActual.getInt(filaActual.getColumnIndex("correlativo")));
    }

    @SuppressWarnings("unused")
    public void set_id_informante(IdInformante value) {
        filaNueva.put("id_asignacion", value.id_asignacion);
        filaNueva.put("correlativo", value.correlativo);
    }

    //para id_informante de base tipo 1

    @SuppressWarnings("unused")
    public Long get_id_id_informante_anterior() {
        return filaActual.getLong(filaActual.getColumnIndex("id_asignacion"));
    }

    @SuppressWarnings("unused")
    public void set_id_id_informante_anterior(Long value) {
        if (value == null)
            filaNueva.putNull("id_informante_anterior");
        else
            filaNueva.put("id_informante_anterior", value);

    }

    @SuppressWarnings("unused")
    public IdInformante get_id_informante_padre() {
        if (filaActual.isNull(filaActual.getColumnIndex("id_asignacion_padre"))) {
            return new IdInformante(0, 0);
        } else {
            return new IdInformante(filaActual.getInt(filaActual.getColumnIndex("id_asignacion_padre")), filaActual.getInt(filaActual.getColumnIndex("correlativo_padre")));
        }
    }

    @SuppressWarnings("unused")
    public void set_id_informante_padre(IdInformante value) {
        if (value == null || value.id_asignacion == 0) {
            filaNueva.putNull("id_asignacion_padre");
            filaNueva.putNull("correlativo_padre");
        } else {
            filaNueva.put("id_asignacion_padre", value.id_asignacion);
            filaNueva.put("correlativo_padre", value.correlativo);
        }
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
        if (filaActual.isNull(filaActual.getColumnIndex("id_upm"))) {
            return null;
        } else {
            return filaActual.getInt(filaActual.getColumnIndex("id_upm"));
        }
    }

    @SuppressWarnings("unused")
    public void set_id_upm(Integer value) {
        filaNueva.put("id_upm", value);
    }
    @SuppressWarnings("unused")
    public Integer get_id_upm_hijo() {
        if (filaActual.isNull(filaActual.getColumnIndex("id_upm_hijo"))) {
            return 0;
        } else {
            return filaActual.getInt(filaActual.getColumnIndex("id_upm_hijo"));
        }
    }

    @SuppressWarnings("unused")
    public void set_id_upm_hijo(Integer value) {
        filaNueva.put("id_upm_hijo", value);
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
    public Estado get_apiestado() {
        return Estado.valueOf(filaActual.getString(filaActual.getColumnIndex("estado")));
    }

    @SuppressWarnings("unused")
    public void set_apiestado(Estado value) {
        filaNueva.put("estado", value.toString());
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
    public IdInformante get_id_informante_anterior() {
        if (filaActual.isNull(filaActual.getColumnIndex("id_asignacion_anterior"))) {
            return new IdInformante(0, 0);
        } else {
            return new IdInformante(filaActual.getInt(filaActual.getColumnIndex("id_asignacion_anterior")), filaActual.getInt(filaActual.getColumnIndex("correlativo_anterior")));
        }
    }

    @SuppressWarnings("unused")
    public void set_id_informante_anterior(IdInformante value) {
        if (value == null || value.id_asignacion == 0) {
            filaNueva.putNull("id_asignacion_anterior");
            filaNueva.putNull("correlativo_anterior");
        } else {
            filaNueva.put("id_asignacion_anterior", value.id_asignacion);
            filaNueva.put("correlativo_anterior", value.correlativo);
        }
    }

    @SuppressWarnings("unused")
    public String get_codigo_anterior() {
        return filaActual.getString(filaActual.getColumnIndex("codigo_anterior"));
    }

    @SuppressWarnings("unused")
    public void set_codigo_anterior(String value) {
        filaNueva.put("codigo_anterior", value);
    }

    public static int maximaFilaFinal(IdInformante idInformante) {
        int res = 0;
        String query = "SELECT MAX(fila)\n" +
                "FROM enc_encuesta\n" +
                "WHERE id_asignacion = " + idInformante.id_asignacion + "\n" +
                "AND correlativo = " + idInformante.correlativo + "\n" +
                "AND id_pregunta IN (SELECT id_pregunta FROM enc_pregunta WHERE codigo_pregunta LIKE 'Z%' AND id_nivel = 1)";
        Cursor cursor = conn.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                res = cursor.getInt(0);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return res;
    }

    public static int getInformantesRespuesta(int idUpm, String codigoPregunta, boolean contar, Integer codigoRespuesta) {
        String query;
        int idPregunta = getIdPregunta(codigoPregunta);
        List<String> omisionId = Pregunta.getOmisionId(idPregunta);
        String omision = omisionId.toString().replace("[", "").replace("]", "");
        if (contar) {
            query = "SELECT COUNT(CAST(e.codigo_respuesta AS Int))\n" +
                    "FROM enc_informante i, enc_pregunta p, enc_encuesta e\n" +
                    "WHERE i.id_upm = " + idUpm + "\n" +
                    "AND e.visible like 't%'\n" +
                    "AND p.id_pregunta = e.id_pregunta\n" +
                    "AND i.id_asignacion = e.id_asignacion\n" +
                    "AND i.correlativo = e.correlativo\n" +
                    "AND i.estado <> 'ANULADO'\n" +
                    "AND p.codigo_pregunta = '" + codigoPregunta + "'\n" +
                    "AND CAST(e.codigo_respuesta As Int) = " + codigoRespuesta + "\n" +
                    "AND e.estado = 'INSERTADO'";
        } else {
            query = "SELECT SUM(CAST(e.codigo_respuesta AS Int))\n" +
                    "FROM enc_informante i, enc_pregunta p, enc_encuesta e\n" +
                    "WHERE i.id_upm = " + idUpm + "\n" +
                    "AND e.visible like 't%'\n" +
                    "AND p.id_pregunta = e.id_pregunta\n" +
                    "AND i.id_asignacion = e.id_asignacion\n" +
                    "AND i.correlativo = e.correlativo\n" +
                    "AND i.estado <> 'ANULADO'\n" +
                    "AND p.codigo_pregunta = '" + codigoPregunta + "'\n" +
                    "AND e.codigo_respuesta not in (" + omision + ")\n" +
                    "AND e.estado = 'INSERTADO'";
        }
        int res = 0;

        Cursor cursor = null;
        try {
            cursor = conn.rawQuery(query, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (cursor.moveToFirst()) {
            do {
                res = cursor.getInt(0);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return res;
    }

    public static void inhabilitaBoletas() {
        conn.beginTransaction();
        try {
            conn.execSQL("UPDATE enc_informante\n" +
                    "SET estado = 'INHABILITADO'\n" +
                    "WHERE estado LIKE 'CONCLUIDO'\n" +
                    "AND id_nivel = 3");
            conn.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            conn.endTransaction();
        }
    }

    public static void eliminaBoletas(IdInformante idInformante) {
        conn.beginTransaction();
        try {
            String sql = "UPDATE enc_informante\n" +
                    "SET estado = 'ANULADO'\n" +
                    "WHERE id_asignacion = " + idInformante.id_asignacion + " \n" +
                    "AND coalesce (correlativo_padre,correlativo) = " + idInformante.correlativo;
            conn.execSQL(sql);

            conn.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            conn.endTransaction();
        }
    }

    public static void habilitaBoletas() {
        try {
            String query = "SELECT i.id_asignacion, i.correlativo\n" +
                    "FROM enc_informante i\n" +
                    "WHERE i.estado IN( 'INHABILITADO','CONCLUIDO') AND i.id_nivel=3 \n" +
                    "AND i.id_asignacion*10000+i.correlativo IN (\n" +
                    "SELECT o.id_asignacion*10000+o.correlativo \n" +
                    "FROM enc_observacion o \n" +
                    "WHERE (o.id_tipo_obs=2 OR o.id_tipo_obs=21 OR o.id_tipo_obs=24) \n" +
                    "AND o.feccre = (SELECT MAX(feccre) \n" +
                    "FROM enc_observacion o \n" +
                    "WHERE o.id_asignacion*10000+o.correlativo = i.id_asignacion*10000+i.correlativo)\n" +
                    ")\n" +
                    "UNION\n" +
                    "SELECT iii.id_asignacion, iii.correlativo\n" +
                    "FROM(SELECT i.id_asignacion, i.correlativo\n" +
                    "    FROM enc_informante i\n" +
                    "    WHERE i.estado IN( 'INHABILITADO','CONCLUIDO') AND i.id_nivel=3 ) ii \n" +
                    "JOIN (SELECT i.id_asignacion, i.correlativo,i.id_asignacion_padre,i.correlativo_padre\n" +
                    "    FROM enc_informante i\n" +
                    "    WHERE i.id_nivel=4) iii  ON ii.id_asignacion=iii.id_asignacion_padre AND ii.correlativo=iii.correlativo_padre\t\n" +
                    "    AND iii.id_asignacion*10000+iii.correlativo IN (\n" +
                    "SELECT o.id_asignacion_hijo*10000+o.correlativo_hijo \n" +
                    "FROM enc_observacion o \n" +
                    "WHERE (o.id_tipo_obs=2 OR o.id_tipo_obs=21 OR o.id_tipo_obs=24)\n" +
                    "AND o.id_asignacion_hijo<>0\n" +
                    "AND o.feccre = (SELECT MAX(feccre) \n" +
                    "FROM enc_observacion o \n" +
                    "WHERE o.id_asignacion*10000+o.correlativo = ii.id_asignacion*10000+ii.correlativo)\n" +
                    ")";
            Cursor cursor = conn.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                do {
                    // QUEMADO
                    Encuesta.abrirBoleta(new IdInformante(cursor.getInt(0), cursor.getInt(1)));
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // AUTOMATICAMENTE CORRIGE EL CODIGO DEL INFORMANTE (FOLIO) UTILIZANDO LAS RESPUESTAS DE: CODIGO DE UPM - NRO DE VIVIENDA - NRO DE HOGAR
//    public static void corrigeFolioInformante() {
//        try {
//            ArrayList<Map<String, Object>> foliosParaCorregir = new Informante().obtenerListado("enc_informante.id_asignacion*10000+enc_informante.correlativo IN (SELECT i1.id_asignacion*10000+i1.correlativo\n" +
//                    "FROM enc_informante i1, enc_encuesta e\n" +
//                    "WHERE i1.id_nivel = 1\n" +
//                    "AND i1.id_asignacion = e.id_asignacion\n" +
//                    "AND i1.correlativo = e.correlativo\n" +
//                    "AND e.id_pregunta = 20484\n" +
//                    "AND i1.estado <> 'ANULADO'\n" +
//                    "AND substr(i1.codigo, 1, 17) <> e.codigo_respuesta)");
//            ContentValues paquete = new ContentValues();
//            for (Map<String, Object> values : foliosParaCorregir) {
//                paquete.clear();
//                String codigo = "";
//                String query = "SELECT ( (SELECT e1.codigo_respuesta FROM enc_encuesta e1 WHERE e1.id_asignacion = i.id_asignacion AND e1.correlativo = i.correlativo AND e1.id_pregunta = 20484)||'-'||\n" +
//                        "(CASE (SELECT length(e.codigo_respuesta) FROM enc_encuesta e WHERE e.id_asignacion = i.id_asignacion AND e.correlativo = i.correlativo AND e.id_pregunta = 18596)\n" +
//                        "WHEN 1 THEN\n" +
//                        "'00'||(SELECT e1.codigo_respuesta FROM enc_encuesta e1 WHERE e1.id_asignacion = i.id_asignacion AND e1.correlativo = i.correlativo AND e1.id_pregunta = 18596)\n" +
//                        "WHEN 2 THEN\n" +
//                        "'0'||(SELECT e1.codigo_respuesta FROM enc_encuesta e1 WHERE e1.id_asignacion = i.id_asignacion AND e1.correlativo = i.correlativo AND e1.id_pregunta = 18596)\n" +
//                        "ELSE\n" +
//                        "(SELECT e1.codigo_respuesta FROM enc_encuesta e1 WHERE e1.id_asignacion = i.id_asignacion AND e1.correlativo = i.correlativo AND e1.id_pregunta = 18596)\n" +
//                        "END)||\n" +
//                        "(SELECT e1.codigo_respuesta FROM enc_encuesta e1 WHERE e1.id_asignacion = i.id_asignacion AND e1.correlativo = i.correlativo AND e1.id_pregunta = 18597) ) AS cod, i.id_asignacion, i.correlativo\n" +
//                        "FROM enc_informante i, enc_encuesta e\n" +
//                        "WHERE i.id_nivel = 1\n" +
//                        "AND i.id_asignacion = e.id_asignacion\n" +
//                        "AND i.correlativo = e.correlativo\n" +
//                        "AND e.id_pregunta = 20484\n" +
//                        "AND i.id_asignacion = " + values.get("id_asignacion") + "\n" +
//                        "AND i.correlativo = " + values.get("correlativo") + "\n" +
//                        "AND substr(i.codigo, 1, 17) <> e.codigo_respuesta";
//                Cursor cursor = conn.rawQuery(query, null);
//                if (cursor.moveToNext()) {
//                    codigo = cursor.getString(0);
//                }
//                cursor.close();
//                paquete.put("codigo", codigo);
//                conn.update("enc_informante", paquete, "id_asignacion = ? AND correlativo = ?", new String[]{values.get("id_asignacion").toString(), values.get("correlativo").toString()});
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    public static int getIdPregunta(String codigo_pregunta) {
        int res = 0;
        String query = "SELECT id_pregunta\n" +
                "FROM enc_pregunta\n" +
                "WHERE codigo_pregunta LIKE '" + codigo_pregunta + "'";
        Cursor cursor = conn.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                res = cursor.getInt(0);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return res;
    }

    /***
     * Realiza la actualizacion de las sumas de los datos del LV's
     * @param idAsignacion
     * @param codigoPregunta
     */
    public static void Sumatotales(int idAsignacion, String[] codigoPregunta) {
        int suma = 0;
        int idPregunta = 0;
        IdInformante idInformante = Asignacion.getInformanteCabeceraViviendas(idAsignacion);
        int idUpm = Asignacion.getUpm(idAsignacion);
        for (String grupo : codigoPregunta) {
            String cod_preg = grupo.split(";")[0];
            String cod_preg_resp = grupo.split(";")[1];
            if (cod_preg.equals("AA_02")) {
                suma = getInformantesRespuesta(idUpm, cod_preg_resp, true, 1);
            } else if (cod_preg.equals("AA_03")) {
                suma = getInformantesRespuesta(idUpm, cod_preg_resp, true, 2);
            } else if (cod_preg.equals("AA_04")) {
                suma = getInformantesRespuesta(idUpm, cod_preg_resp, true, 3);
            }else if (cod_preg.equals("AA_08")) {
                suma = getInformantesRespuesta(idUpm, cod_preg_resp, true, 1);
            } else {
                suma = getInformantesRespuesta(idUpm, cod_preg_resp, false, null);
            }
            idPregunta = getIdPregunta(cod_preg);
            conn.beginTransaction();
            try {
                conn.execSQL("UPDATE enc_encuesta\n" +
                        "SET respuesta = '" + suma + "', codigo_respuesta = '" + suma + "'\n" +
                        "WHERE id_asignacion = " + idInformante.id_asignacion + "\n" +
                        "AND correlativo = " + idInformante.correlativo + "\n" +
                        "AND id_pregunta = " + idPregunta);
                conn.setTransactionSuccessful();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                conn.endTransaction();
            }
        }

    }

    public static String obtenerUltimaIncidenciaBoleta(int idAsignacion, int correlativo) {

        String res = "0";
        String query = "SELECT e.codigo_respuesta\n" +
                "FROM enc_encuesta e\n" +
                "WHERE e.id_asignacion = " + idAsignacion + "\n" +
                "AND e.correlativo = " + correlativo + "\n" +
                "AND e.id_pregunta = " + Parametros.ID_INCIDENCIA_FINAL + "\n" +
//                "AND e.visible = LIKE 't%'\n" +
                "LIMIT 1";
        Cursor cursor = conn.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            res = cursor.getString(0);
        }
        cursor.close();
//        JSONArray arrayIncidencias=new JSONArray();
//        if(!res.equals("0")){
//            try {
//                res=obtenerIncidencia(new JSONArray(res));
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }
        return res;
    }

    public static String obtenerIncidenciaFinalBoleta(int idAsignacion, int correlativo) {

        String res = "0";
        String query = "SELECT e.codigo_respuesta\n" +
                "FROM enc_encuesta e\n" +
                "WHERE e.id_asignacion = " + idAsignacion + "\n" +
                "AND e.correlativo = " + correlativo + "\n" +
                "AND e.id_pregunta = " + Parametros.ID_INCIDENCIA_FINAL + "\n" +
                "AND e.visible  LIKE 't%'\n" +
                "LIMIT 1";
        Cursor cursor = conn.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            res = cursor.getString(0);
        }

        cursor.close();
        JSONArray arrayIncidencias = new JSONArray();
//        if(!res.equals("0")){
//            try {
//                res=obtenerIncidenciaFinal(new JSONArray(res));
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }
        return res;
    }

    /**
     * Obtiene el codigo de la ultima incidencia tempora o final registrada
     *
     * @param grupoGGu
     * @return
     */
    private static String obtenerIncidencia(JSONArray grupoGGu) {
        String ultimaIncidencia = "0";
        JSONArray temDescripcion;
        try {
//            Log.d("INCIDENCIAS ",String.valueOf(grupoGGu));

            temDescripcion = grupoGGu.length() > 0 ? grupoGGu.getJSONObject(grupoGGu.length() - 1).getJSONArray("descripcion") : new JSONArray();
//            Log.d("incidencia Temporal",temDescripcion.getJSONObject(2).getString("codigo"));
            ultimaIncidencia = temDescripcion.length() == 0 ? "0" : temDescripcion.getJSONObject(temDescripcion.length() - 1).getInt("id_pregunta") != 18721 ? temDescripcion.getJSONObject(temDescripcion.length() - 2).getString("codigo") : temDescripcion.getJSONObject(temDescripcion.length() - 1).getString("codigo");
//            Log.d("incidencia Temporal",ultimaIncidencia);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return ultimaIncidencia;
    }

    /**
     * Obtiene el codigo de la incidencia final 0 si no existe
     *
     * @param grupoGGu
     * @return
     */
    private static String obtenerIncidenciaFinal(JSONArray grupoGGu) {
        String ultimaIncidencia = "0";
        JSONArray temDescripcion;
        try {
//            Log.d("INCIDENCIAS ",String.valueOf(grupoGGu));

            temDescripcion = grupoGGu.length() > 0 ? grupoGGu.getJSONObject(grupoGGu.length() - 1).getJSONArray("descripcion") : new JSONArray();
//            Log.d("incidencia Temporal",temDescripcion.getJSONObject(2).getString("codigo"));
            ultimaIncidencia = temDescripcion.length() == 0 ? "0" : temDescripcion.getJSONObject(temDescripcion.length() - 1).getInt("id_pregunta") != 18721 ? "0" : temDescripcion.getJSONObject(temDescripcion.length() - 1).getString("codigo");
//            Log.d("incidencia Temporal",ultimaIncidencia);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return ultimaIncidencia;
    }

    /**
     * Verifica si una pregunta tiene observacion
     *
     * @param idInformante
     * @param idPregunta
     * @param nivel
     * @return
     */
    public static boolean esPreguntaObservada(IdInformante idInformante, int idPregunta, int nivel) {
        boolean res = false;
        String query = "";
        if (nivel == 3) {
            query = "SELECT id_asignacion, correlativo,id_pregunta \n" +
                    "from enc_observacion \n" +
                    "WHERE id_asignacion=" + idInformante.id_asignacion + "\n" +
                    "AND correlativo=" + idInformante.correlativo + " \n" +
                    "AND id_tipo_obs=2 \n" +
                    "AND id_pregunta=" + idPregunta;
        } else if (nivel == 4) {
            query = "SELECT id_asignacion_hijo,correlativo_hijo,id_pregunta \n" +
                    "from enc_observacion \n" +
                    "WHERE id_asignacion_hijo=" + idInformante.id_asignacion + " \n" +
                    "AND correlativo_hijo=" + idInformante.correlativo + " \n" +
                    "AND id_tipo_obs=2\n" +
                    "AND id_pregunta=" + idPregunta;
        } else {
            return false;
        }
        Cursor cursor = conn.rawQuery(query, null);
        if (cursor != null && cursor.moveToFirst()) {
            res = true;
        }
        cursor.close();
        return res;
    }

    /**
     * obtiene el nivel de una pregunta con parametro idPregunta
     *
     * @param idPregunta
     * @return
     */
    public static int obtenerNivelPregunta(int idPregunta) {
        int res = 0;
        String query = "SELECT id_nivel \n" +
                "FROM (SELECT id_seccion \n" +
                "FROM enc_pregunta \n" +
                "WHERE id_pregunta=" + idPregunta + ") ep\n" +
                "JOIN enc_seccion es on ep.id_seccion=es.id_seccion";
        Cursor cursor = conn.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            res = cursor.getInt(0);
            ;
        }
        cursor.close();
        return res;
    }

    public static String getObservaciones(IdInformante idInformante, int idPregunta) {
        String resp = "";
        try {
            String query = "SELECT observacion\n" +
                    "FROM enc_observacion\n" +
                    "WHERE id_asignacion = " + idInformante.id_asignacion + " \n" +
                    "AND correlativo = " + idInformante.correlativo + " \n" +
                    "AND id_pregunta = " + idPregunta;
            Cursor cursor = conn.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                do {
                    // QUEMADO
//                    Log.d("OBSERVACION", String.valueOf(cursor.getInt(0)));
                    resp = cursor.getString(0);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resp;
    }
    public static String getObservacionesHijo(IdInformante idInformante, int idPregunta) {
        String resp = "";
        try {
            String query = "SELECT observacion\n" +
                    "FROM enc_observacion\n" +
                    "WHERE id_asignacion_hijo = " + idInformante.id_asignacion + " \n" +
                    "AND correlativo_hijo = " + idInformante.correlativo + " \n" +
                    "AND id_pregunta = " + idPregunta;
            Cursor cursor = conn.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                do {
                    resp = cursor.getString(0);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resp;
    }    
    public ArrayList<Map<String, Object>> resumen(IdInformante idInformante, int idSeccion) {
        ArrayList<Map<String, Object>> res = null;
        Cursor cursor = null;
        try {
            res = new ArrayList<>();
//            Map<Integer, IdInformante> jerarquia = jerarquia(idInformante);
            boolean flag = false;
            String subquery1, subquery2;
//            if (jerarquia.containsKey(3)) {
//                //SI EL INFORMANTE ES DE NIVEL 2
//                flag = true;
//                subquery1 = "(SELECT COUNT(*) FROM enc_encuesta e\n" +
//                        "JOIN enc_informante i ON e.id_asignacion = i.id_asignacion AND e.correlativo = i.correlativo AND i.id_usuario = " + Usuario.getUsuario() +"\n" +
////                        "AND (i.id_asignacion = "+jerarquia.get(3).id_asignacion +" AND i.correlativo = "+jerarquia.get(3).correlativo+")\n" +
//                        "AND (i.id_asignacion = "+idInformante.id_asignacion +" AND i.correlativo = "+idInformante.correlativo+"\n" +
//                        "WHERE e.id_pregunta IN (SELECT p.id_pregunta FROM enc_pregunta p WHERE p.id_seccion = s.id_seccion)\n" +
//                        "AND e.visible LIKE 't%')";
//                subquery2 = "(SELECT id_pregunta FROM enc_observacion o WHERE o.id_tipo_obs <> 8 AND o.id_asignacion = "+jerarquia.get(3).id_asignacion+" AND o.correlativo = "+jerarquia.get(3).correlativo+" AND o.estado LIKE 'ELABORADO' AND id_pregunta IN (SELECT p.id_pregunta FROM enc_pregunta p WHERE s.id_seccion = p.id_seccion) LIMIT 1)";
//            }else {
                subquery1 = "(SELECT COUNT(*) FROM enc_encuesta e\n" +
                        "JOIN enc_informante i ON e.id_asignacion = i.id_asignacion AND e.correlativo = i.correlativo AND i.id_usuario = " + Usuario.getUsuario() +"\n" +
//                        "AND (i.id_asignacion = "+idInformante.id_asignacion +" AND i.correlativo = "+idInformante.correlativo+")\n" +
                        "AND (i.id_asignacion = "+idInformante.id_asignacion +" AND i.correlativo = "+idInformante.correlativo+")\n" +
                        "WHERE e.id_pregunta IN (SELECT p.id_pregunta FROM enc_pregunta p WHERE p.id_seccion = s.id_seccion)\n" +
                        "AND e.visible LIKE 't%')";
                subquery2 = "(SELECT id_pregunta FROM enc_observacion o WHERE o.id_tipo_obs <> 8 AND o.id_asignacion = "+idInformante.id_asignacion+" AND o.correlativo = "+idInformante.correlativo+" AND o.estado LIKE 'ELABORADO' AND id_pregunta IN (SELECT p.id_pregunta FROM enc_pregunta p WHERE s.id_seccion = p.id_seccion) LIMIT 1)";
//            }

//            String var=determinaTipoBoleta(idInformante.id_asignacion,idInformante.correlativo);
            //INICIAMOS LA QUERY PRINCIPAL QUE SELECCIONARA LAS SECCIONES
            String query = "SELECT 1 ord, null ord1, null ord2, id_seccion, codigo, seccion, null id_asignacion, null correlativo, null descripcion, id_nivel, null id_pregunta, null mostrar_ventana, null codigo_pregunta, null pregunta, null respuesta, null observacion, null fila, null id_usuario, null id_last, "+ subquery1 +" nro_respuestas,"+ subquery2 +" cod_obs, abierta\n" +
                    "FROM enc_seccion s\n" +
                    "WHERE s.id_nivel > 2 \n";

            Seccion seccion = new Seccion();
            int d;
            if (seccion.abrir(idSeccion)) {
                d = seccion.get_id_nivel();
            } else {
                d = -1;
            }
            seccion.free();
//            if(flag) { //SI EL INFORMANTE ES DE NIVEL 2
                //OBTIENE LOS INFORMANTES DE NIVEL 2, EN CASO LOS HAYA
//                query += "UNION\n" +
//                        "SELECT 2 ord, i.id_asignacion ord1, i.correlativo ord2, s.id_seccion, s.codigo, s.seccion, i.id_asignacion, i.correlativo, i.descripcion, i.id_nivel, null id_pregunta, null mostrar_ventana, null codigo_pregunta, null pregunta, null respuesta, null observacion, null fila, id_usuario, null id_last, (SELECT COUNT(*) FROM enc_encuesta WHERE id_asignacion = i.id_asignacion AND correlativo = i.correlativo AND id_last > -1 AND id_pregunta IN (SELECT p.id_pregunta FROM enc_pregunta p WHERE p.id_seccion = s.id_seccion)) nro_respuestas, (SELECT o.id_pregunta FROM enc_observacion o WHERE o.id_tipo_obs <> 8 AND o.id_asignacion_hijo = i.id_asignacion AND o.correlativo_hijo = i.correlativo AND o.apiestado LIKE 'ELABORADO' AND o.id_pregunta IN (SELECT p.id_pregunta FROM enc_pregunta p WHERE s.id_seccion = p.id_seccion) LIMIT 1) cod_obs, 0 abierta\n" +
//                        "FROM enc_seccion s, enc_informante i\n" +
//                        "WHERE s.id_nivel >= i.id_nivel\n" +
//                        "AND i.id_asignacion_padre = " + jerarquia.get(3).id_asignacion + "\n" +
//                        "AND i.correlativo_padre = " + jerarquia.get(3).correlativo + "\n" +
//                        "AND s.id_seccion = "+idSeccion+"\n" +
//                        "AND i.estado <> 'ANULADO'\n";
//            } else if(d == 2) {
                query += "UNION\n" +
                        "SELECT 2 ord, i.id_asignacion ord1, i.correlativo ord2, s.id_seccion, s.codigo, s.seccion, i.id_asignacion, i.correlativo, i.descripcion, i.id_nivel, null id_pregunta, null mostrar_ventana, null codigo_pregunta, null pregunta, null respuesta, null observacion, null fila, id_usuario, null visible, (SELECT COUNT(*) FROM enc_encuesta WHERE id_asignacion = i.id_asignacion AND correlativo = i.correlativo AND visible LIKE 't%' AND id_pregunta IN (SELECT p.id_pregunta FROM enc_pregunta p WHERE p.id_seccion = s.id_seccion)) nro_respuestas, (SELECT o.id_pregunta FROM enc_observacion o WHERE o.id_tipo_obs <> 8 AND o.id_asignacion_hijo = i.id_asignacion AND o.correlativo_hijo = i.correlativo AND o.estado LIKE 'ELABORADO' AND o.id_pregunta IN (SELECT p.id_pregunta FROM enc_pregunta p WHERE s.id_seccion = p.id_seccion) LIMIT 1) cod_obs, 0 abierta\n" +
                        "FROM enc_seccion s, enc_informante i\n" +
                        "WHERE s.id_nivel >= i.id_nivel\n" +
                        "AND i.id_asignacion_padre = " + idInformante.id_asignacion + "\n" +
                        "AND i.correlativo_padre = " + idInformante.correlativo + "\n" +
                        "AND s.id_seccion = "+idSeccion+"\n" +
                        "AND i.estado <> 'ANULADO'\n";
//            }

            //OBTIENE LAS RESPUESTAS DEL INFORMANTE
            query += "UNION\n" +
                    "SELECT 2 ord, i.id_asignacion ord1, i.correlativo ord2, p.id_seccion, s.codigo, null seccion, e.id_asignacion, e.correlativo, null descripcion, s.id_nivel, p.id_pregunta, p.inicial, p.codigo_pregunta, p.pregunta, e.respuesta, e.observacion, 1 fila, id_usuario, e.visible, null nro_respuestas, (SELECT o.id_pregunta FROM enc_observacion o WHERE o.id_tipo_obs <> 8 AND ((o.id_asignacion = "+idInformante.id_asignacion+" AND o.correlativo = "+idInformante.correlativo+") OR (o.id_asignacion_hijo = "+idInformante.id_asignacion+" AND o.correlativo_hijo = "+idInformante.correlativo+")) AND o.estado LIKE 'ELABORADO' AND o.id_pregunta = (SELECT pa.id_pregunta FROM enc_pregunta pa WHERE pa.id_pregunta = p.id_pregunta) LIMIT 1) cod_obs, abierta\n" +
                    "FROM enc_seccion s, enc_pregunta p, enc_encuesta e, enc_informante i\n" +
                    "WHERE s.id_seccion = p.id_seccion\n" +
                    "AND p.id_pregunta = e.id_pregunta\n" +
                    "AND e.id_asignacion = i.id_asignacion\n" +
                    "AND e.correlativo = i.correlativo\n" +
                    "AND e.visible LIKE 't%'\n" +
                    "AND e.id_asignacion = " + idInformante.id_asignacion + "\n" +
                    "AND e.correlativo = " + idInformante.correlativo + "\n" +
                    "AND p.id_seccion = " + idSeccion + "\n" +
                    "AND i.estado <> 'ANULADO'\n" +
                    "ORDER BY codigo, ord, ord1, ord2";
            //"ORDER BY codigo, ord, ord1, ord2, id_asignacion, correlativo, codigo_pregunta, fila";

            cursor = conn.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                do {
                    Map<String, Object> row = new LinkedHashMap<>();
                    row.put("id_seccion", cursor.isNull(3) ? null : cursor.getInt(3));
                    row.put("seccion", cursor.isNull(5) ? null : cursor.getString(5).replace("<br />", " ").replace("<br>", " "));
                    row.put("id_asignacion", cursor.isNull(6) ? null : cursor.getInt(6));
                    row.put("correlativo", cursor.isNull(7) ? null : cursor.getInt(7));
                    row.put("descripcion", cursor.isNull(8) ? null : cursor.getString(8));
                    row.put("id_nivel", cursor.isNull(9) ? null : cursor.getInt(9));
                    row.put("id_pregunta", cursor.isNull(10) ? null : cursor.getInt(10));
                    row.put("mostrar_ventana", cursor.isNull(11) ? 0 : cursor.getInt(11));
                    row.put("codigo_pregunta", cursor.isNull(12) ? null : cursor.getString(12));
                    row.put("pregunta", cursor.isNull(13) ? null : (Pregunta.procesaEnunciado(new IdInformante(cursor.getInt(6), cursor.getInt(7)),  cursor.getString(13)).replace("<br />","")).replace("<br>","") );
                    row.put("respuesta", cursor.isNull(14) ? null : cursor.getString(14));
                    row.put("observacion", cursor.isNull(15) ? null : cursor.getString(15));
                    row.put("fila", cursor.isNull(16) ? null : cursor.getInt(16));
                    row.put("id_usuario", cursor.isNull(17) ? null : cursor.getInt(17));
                    row.put("nro_respuestas", cursor.isNull(19)||(cursor.getInt(9)==4 && cursor.isNull(6))? cursor.isNull(7)?0:-1 : cursor.getInt(19)+" resp.");
                    row.put("cod_obs", cursor.isNull(20) ? null : cursor.getInt(20));
                    row.put("abierta", cursor.isNull(21) ? null : cursor.getInt(21));
                    res.add(row);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(cursor!=null)
            cursor.close();

        return res;
    }
    public Map<Integer, IdInformante> jerarquia(IdInformante idInformante) {
        Map<Integer, IdInformante> res = new TreeMap<>();
        int niv = 2;
//        do {
            String query = "SELECT id_asignacion_padre, correlativo_padre, id_nivel\n" +
                    "FROM enc_informante\n" +
                    "WHERE id_asignacion = " + idInformante.id_asignacion + "\n" +
                    "AND correlativo = " + idInformante.correlativo + "\n" +
                    "AND estado <> 'ANULADO'";

            Cursor cursor = conn.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                do {
                    niv = cursor.getInt(2);
                    idInformante = new IdInformante(cursor.getInt(0), cursor.getInt(1));
                    res.put(niv, idInformante);
                } while (cursor.moveToNext());
            }
            cursor.close();
//        } while (niv > 1);
        return res;
    }
    public static String determinaTipoBoleta(int idAsignacion, int correlativo) {
        String res = "";
        String query = "SELECT e.respuesta\n" +
                "FROM enc_pregunta p left join enc_encuesta e on (p.id_pregunta=e.id_pregunta)\n" +
                "WHERE p.id_pregunta = 16863\n" +
                "AND p.id_proyecto = 1\n" +
                "AND e.id_asignacion = " + idAsignacion + "\n" +
                "AND e.correlativo = " + correlativo;
        Cursor cursor = conn.rawQuery(query, null);
        if (cursor.moveToNext()) {
            res = cursor.getString(0);
            res=res.substring(0,1);
            if(res.toUpperCase().equals("1")){
                res="TR";
            }else{
                res="TE";
            }

        }

        cursor.close();
        return res;
    }
//Resumen para cabecera de lv
public static  Map<String, Object> obtenerCabaceraListadoViviendas(int idAsignacion) {
    Map<String, Object> res = null;
    Cursor cursor = null;
    try {
        res = new HashMap<>();
        String query=new String();
        //OBTIENE LAS RESPUESTAS DEL INFORMANTE
        query += "WITH tab_enc AS\n" +
                "(SELECT * FROM\n" +
                "(SELECT * FROM enc_informante WHERE id_asignacion="+idAsignacion+" AND id_nivel=2 AND estado<>'ANULADO') ei \n" +
                " JOIN enc_encuesta ee ON ei.id_asignacion=ee.id_asignacion AND ei.correlativo=ee.correlativo ), tab_viviendas AS\n" +
                "( SELECT cu.viviendas_ocupadas_marco, cu.viviendas_desocupadas_marco FROM\n" +
                " (SELECT * FROM ope_asignacion WHERE id_asignacion="+idAsignacion+") oa JOIN cat_upm cu ON oa.id_upm=cu.id_upm \n" +
                " UNION\n" +
                "  SELECT cuh.viviendas_ocupadas_marco, cuh.viviendas_desocupadas_marco FROM\n" +
                " (SELECT * FROM ope_asignacion WHERE id_asignacion="+idAsignacion+") oa JOIN cat_upm cu ON oa.id_upm=cu.id_upm JOIN cat_upm_hijo cuh ON cuh.id_upm_padre=cu.id_upm\n" +
                ")"+
                "SELECT\n" +
                "(SELECT CAST(codigo_respuesta as INT) FROM enc_encuesta WHERE id_asignacion ="+idAsignacion+" AND id_pregunta = 36265) viviendas_ocupadas,\n" +
                "(SELECT CAST(codigo_respuesta as INT) FROM enc_encuesta WHERE id_asignacion ="+idAsignacion+" AND id_pregunta = 36266) viviendas_desocupadas,\n" +
                "(SELECT CAST(codigo_respuesta as INT) FROM enc_encuesta WHERE id_asignacion ="+idAsignacion+" AND id_pregunta = 36267) viviendas_otros, \n" +
                "(SELECT CAST(codigo_respuesta as INT) FROM enc_encuesta WHERE id_asignacion ="+idAsignacion+" AND id_pregunta = 36313) total_hogares,\n" +
                "(SELECT CAST(codigo_respuesta as INT) FROM enc_encuesta WHERE id_asignacion ="+idAsignacion+" AND id_pregunta = 36314) total_hombres,\n" +
                "(SELECT CAST(codigo_respuesta as INT) FROM enc_encuesta WHERE id_asignacion ="+idAsignacion+" AND id_pregunta = 36315) total_mujeres,\n" +
                "(SELECT CAST(codigo_respuesta as INT) FROM enc_encuesta WHERE id_asignacion ="+idAsignacion+" AND id_pregunta = 36423) viviendas_omitidas,\n" +
                "(SELECT sum(viviendas_ocupadas_marco) FROM tab_viviendas)viviendas_ocupadas_marco,\n" +
                "(SELECT sum(viviendas_desocupadas_marco) FROM tab_viviendas)viviendas_desocupadas_marco";
//                "SELECT(SELECT count(*) FROM tab_enc  where id_pregunta="+Pregunta.getIdPregunta(Parametros.CODIGO_PREGUNTA_USO_DE_LA_VIVIENDA)+" AND visible like 't%' AND CAST( codigo_respuesta as INT)=1) viviendas_ocupadas,(SELECT count(*) FROM tab_enc  where id_pregunta="+Pregunta.getIdPregunta(Parametros.CODIGO_PREGUNTA_USO_DE_LA_VIVIENDA)+" AND visible like 't%' AND CAST( codigo_respuesta as INT)=2)viviendas_desocupadas,\n" +
//                "(SELECT count(*) FROM tab_enc  where id_pregunta="+Pregunta.getIdPregunta(Parametros.CODIGO_PREGUNTA_USO_DE_LA_VIVIENDA)+" AND visible like 't%' AND CAST( codigo_respuesta as INT)=3) viviendas_otros, \n" +
//                "(SELECT sum(CAST(codigo_respuesta as INT)) FROM tab_enc  where id_pregunta="+Pregunta.getIdPregunta(Parametros.CODIGO_PREGUNTA_NRO_HOGARES)+" AND visible like 't%') total_hogares,(SELECT sum(CAST(codigo_respuesta as INT)) FROM tab_enc  where id_pregunta="+Pregunta.getIdPregunta(Parametros.CODIGO_PREGUNTA_NRO_DE_HOMBRES)+" AND visible like 't%' )total_hombres,\n" +
//                "(SELECT sum(CAST(codigo_respuesta as INT)) FROM tab_enc  where id_pregunta="+Pregunta.getIdPregunta(Parametros.CODIGO_PREGUNTA_NRO_DE_MUJERES)+" AND visible like 't%')total_mujeres,\n" +
//                "(SELECT count(*) FROM tab_enc  where id_pregunta="+Pregunta.getIdPregunta(Parametros.CODIGO_PREGUNTA_VIVIENDA_OMITIDA)+" AND visible like 't%' AND CAST( codigo_respuesta as INT)=1) viviendas_omitidas,\n"+
//        "(SELECT sum(viviendas_ocupadas_marco) FROM tab_viviendas\n" +
//                " )viviendas_ocupadas_marco,\n" +
//                " (SELECT sum(viviendas_desocupadas_marco) FROM tab_viviendas\n" +
//                " )viviendas_desocupadas_marco";
//        //"ORDER BY codigo, ord, ord1, ord2, id_asignacion, correlativo, codigo_pregunta, fila";

        cursor = conn.rawQuery(query, null);
        if (cursor.moveToFirst()) {


            res.put(PARAM_VIVIENDAS_OCUPADAS, cursor.getInt(0));
            res.put(PARAM_VIVIENDAS_DESOCUPADAS, cursor.getInt(1));
            res.put(PARAM_VIVIENDAS_OTROS, cursor.getInt(2));
            res.put(PARAM_TOTAL_HOGARES, cursor.getInt(3));
            res.put(PARAM_TOTAL_HOMBRRES, cursor.getInt(4));
            res.put(PARAM_TOTAL_MUJERES, cursor.getInt(5));
            res.put(PARAM_VIVIENDAS_OMITIDAS, cursor.getInt(6));
            res.put(PARAM_VIVIENDAS_OCUPADAS_MARCO, cursor.getInt(7));
            res.put(PARAM_VIVIENDAS_DESOCUPADAS_MARCO, cursor.getInt(8));


        }
    } catch (Exception e) {
        e.printStackTrace();
    }
    if(cursor!=null)
        cursor.close();
    return res;
}
    public void reordenar(IdInformante id) {

        if (id.correlativo > 0) {

            try {
                String query1 =  "SELECT ei1.id_asignacion, ei1.correlativo, ei1.codigo, COUNT(ei2.codigo) as nuevo_codigo\n" +
                        "    FROM (SELECT id_asignacion, correlativo, codigo\n" +
                        "     FROM enc_informante\n" +
                        "    WHERE estado <> 'ANULADO'\n" +
                        "AND id_asignacion_padre = " + id.id_asignacion + "\n" +
                        "AND correlativo_padre = (SELECT correlativo_padre from enc_informante where id_asignacion=" + id.id_asignacion+" AND correlativo=" + id.correlativo+")"+ "\n" +
                        "    ) ei1 JOIN \n" +
                        "    (SELECT id_asignacion, correlativo, codigo\n" +
                        "     FROM enc_informante\n" +
                        "    WHERE estado <> 'ANULADO'\n" +
                        "AND id_asignacion_padre = " + id.id_asignacion + "\n" +
                        "AND correlativo_padre = (SELECT correlativo_padre from enc_informante where id_asignacion=" + id.id_asignacion+" AND correlativo=" + id.correlativo+")"+ "\n"+
                        "    ) ei2 \n" +
                        "    ON CAST(ei1.codigo AS INT) > CAST(ei2.codigo AS INT) \n" +
                        "                        OR ((CAST(ei1.codigo AS INT) = CAST(ei2.codigo AS INT) AND ei1.correlativo >= ei2.correlativo))\n" +
                        "    GROUP BY 1,2, CAST(ei1.codigo AS INT)\n" ;

                Cursor cursor = conn.rawQuery(query1, null);
                conn.beginTransaction();
                try {
                    int i = 1;
                    while (cursor.moveToNext()) {
                        ContentValues paquete = new ContentValues();
                        paquete.put("codigo", cursor.getString(3));
                        conn.update("enc_informante", paquete, "id_asignacion = ? AND correlativo = ? ", new String[]{cursor.getString(0), cursor.getString(1)});

                        i++;
                    }
                    conn.setTransactionSuccessful();

                } catch (Exception ex) {
                    ex.printStackTrace();
                } finally {
                    conn.endTransaction();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }


        }
    }
   public  static  String revisarDatosOrdenUpm(int idUpm){
        String res=null;
            String query="WITH T1 AS(SELECT i.id_asignacion, i.correlativo,codigo, \n" +
                    "CASE WHEN id_upm_hijo is not null THEN (SELECT CASE WHEN id_incidencia=2 THEN -1 ELSE nro_registro END FROM cat_upm_hijo WHERE cat_upm_hijo.id_upm_hijo=i.id_upm_hijo LIMIT 1) ELSE -2 END nro_upm,\n" +
                    "(SELECT e.respuesta FROM enc_encuesta e WHERE e.id_asignacion = i.id_asignacion AND e.correlativo = i.correlativo AND e.id_pregunta =18598 AND codigo_respuesta NOT IN(SELECT codigo FROM cat_upm UNION SELECT CODIGO FROM cat_upm_hijo) )upm_erronea,\n" +
                    "(SELECT e.respuesta FROM enc_encuesta e WHERE e.id_asignacion = i.id_asignacion AND e.correlativo = i.correlativo AND e.id_pregunta =18598 AND codigo_respuesta IN(SELECT codigo FROM cat_upm UNION SELECT CODIGO FROM cat_upm_hijo) )upm,\n" +
                    "(SELECT e.respuesta FROM enc_encuesta e WHERE e.id_asignacion = i.id_asignacion AND e.correlativo = i.correlativo AND e.id_pregunta =36383 )manzana,\n" +
                    "cast((SELECT e.respuesta FROM enc_encuesta e WHERE e.id_asignacion = i.id_asignacion AND e.correlativo = i.correlativo AND e.id_pregunta = 18604)as int) predio, \n" +
                    "cast((SELECT e.respuesta FROM enc_encuesta e WHERE e.id_asignacion = i.id_asignacion AND e.correlativo = i.correlativo AND e.id_pregunta = 18606)as int) vivienda\n" +
                    "FROM enc_informante i JOIN ope_asignacion a ON i.id_upm=a.id_upm\n" +
                    "WHERE i.id_upm = "+idUpm+"\n" +
                    "AND i.estado <> 'ANULADO'\n" +
                    "AND  i.id_asignacion = a.id_asignacion\n" +
                    "AND i.id_nivel = 2 \n" +
                    "ORDER BY nro_upm,manzana,cast(predio as int),cast(vivienda as int)),T2 AS(SELECT DISTINCT upm, manzana,predio FROM T1), T3 AS( SELECT *,CASE WHEN(SELECT COUNT(predio) FROM T2 WHERE PREDIO<=A.PREDIO AND T2.upm=A.upm AND T2.manzana=A.manzana )=predio THEN 0 ELSE 1 END AS nro FROM T2 A ) /*SELECT * from t3*/, T4 AS (SELECT T3.upm, T3.manzana FROM T3 LIMIT 1 ), T5 AS (SELECT upm,manzana, predio, vivienda, count(*) nro FROM T1 group by 1,2,3,4 HAVING nro>1 LIMIT 1), T6 AS(SELECT  upm,manzana, predio, vivienda, CASE WHEN(SELECT COUNT(*) FROM T1 WHERE vivienda<=A.vivienda AND predio=A.predio AND manzana=A.manzana AND upm=A.upm)=vivienda THEN 0 ELSE 1 END AS nro FROM T1 as A) , T7 AS (SELECT * FROM T6 WHERE nro>0 LIMIT 1 )\n" +
                    "SELECT 1,(SELECT upm_erronea FROM t1 WHERE upm_erronea IS NOT NULL LIMIT 1) upm_erronea, (SELECT 'UPM: '||T3.upm||'-> MZN: ' ||T3.manzana FROM T3 WHERE T3.nro =1 LIMIT 1 ) predio, (SELECT 'UPM: '||upm||'-> MZN: '||manzana||'-> PREDIO: '|| predio||'-> VIV: '|| vivienda FROM T5) vivienda_duplicada, (SELECT 'UPM: '||upm||'-> MZN: ' ||manzana||'-> PREDIO: '||predio FROM T6 WHERE nro>0 LIMIT 1 ) vivienda_orden\n";
                    try{
                        Cursor cursor = conn.rawQuery(query, null);
                        if (cursor.moveToNext()) {
                            res= cursor.getString(1);
                            if(res!=null){
                                return "UPM No es Adicional o remplazo "+res;
                            }
                            res=cursor.getString(2);
                            if(res!=null){
                                return "correlatividad de predios "+res;
                            }
                            res=cursor.getString(3);
                            if(res!=null){
                                return "vivienda duplicada "+res;
                            }
                            res=cursor.getString(4);
                            if(res!=null){
                                return "correlatividad de vivienda "+res;
                            }

                        }

                        cursor.close();

                    }catch (Exception e){
                        e.printStackTrace();
                    }
                        return res;


   }

    public static String getCodigoFechaCreacionBoleta(IdInformante idInformante){
        int[] nor = {0, 31, 59, 90, 120, 151, 181, 212, 243, 273, 304, 334};
        String resp="-1";
        Informante informante=new Informante();
        if(informante.abrir("id_asignacion="+idInformante.id_asignacion+" AND correlativo="+idInformante.correlativo,"")){

            Calendar fi = Calendar.getInstance(TimeZone.getTimeZone("BOT"));
            fi.setTimeInMillis(informante.get_feccre() * 1000);
            SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy", new Locale("es"));
            String ff=format.format(fi.getTime());
            int val =Integer.parseInt(ff.split("/")[2]) * 365 + Integer.parseInt(ff.split("/")[2]) / 4;
                /*if (valor.getYear() % 4 == 0) {
                    val += bis[valor.getMonth()];
                } else {*/
            val += nor[Integer.parseInt(ff.split("/")[1])-1];
            //}
            val += Integer.parseInt(ff.split("/")[0]);
            informante.free();
            return String.valueOf(val);
        }
        return resp;
    }

    public static String getCodigoFechaCreacionBoletaFormat(IdInformante idInformante){
        int[] nor = {0, 31, 59, 90, 120, 151, 181, 212, 243, 273, 304, 334};
        String resp="-1";
        Informante informante=new Informante();
        if(informante.abrir("id_asignacion="+idInformante.id_asignacion+" AND correlativo="+idInformante.correlativo,"")){

            Calendar fi = Calendar.getInstance(TimeZone.getTimeZone("BOT"));
            fi.setTimeInMillis(informante.get_feccre() * 1000);
            SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy", new Locale("es"));
            resp=format.format(fi.getTime());
        }
        return resp;
    }
    public static int  getUrbano(int id) {
        int res = 1;
        String query = "SELECT urbano\n" +
                "FROM cat_upm\n" +
                "WHERE estado = 'ELABORADO'\n" +
                "AND id_upm = " + id + "\n";
        Cursor cursor = conn.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                res = cursor.getInt(0);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return res;
    }

}