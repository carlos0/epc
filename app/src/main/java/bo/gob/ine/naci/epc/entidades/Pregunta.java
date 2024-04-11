package bo.gob.ine.naci.epc.entidades;

import android.database.Cursor;

import com.udojava.evalex.Expression;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by INE.
 */
//EN CSV
public class Pregunta extends EntidadId {
    public Pregunta() {
        super("enc_pregunta");
    }

    public static int cierre() {
        int res = 0;
        /*String query = "SELECT id_pregunta\n" +
                "FROM enc_pregunta\n" +
                "WHERE codigo_pregunta LIKE 'Z%'\n" +
                "ORDER BY codigo_pregunta\n" +
                "LIMIT 1";*/

        String query = "SELECT id_pregunta\n" +
                "FROM enc_pregunta\n" +
                "WHERE id_seccion IN (SELECT id_seccion FROM enc_seccion WHERE abierta = 1 AND id_nivel=1)\n" +
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

    public void updateRegla() {
        conn.beginTransaction();
        try {
            conn.execSQL("UPDATE enc_pregunta\n" +
                    "SET regla = replace(regla, ':', '|'),\n" +
                    "rpn = replace(rpn, ':', '|'),\n" +
                    "formula = replace(formula, ':', '|'),\n" +
                    "rpn_formula = replace(rpn_formula, ':', '|')");
            conn.setTransactionSuccessful();
        } finally {
            conn.endTransaction();
        }
    }

    public static ArrayList<Map<String, Integer>> preguntas(int idNivel) {
        ArrayList<Map<String, Integer>> res = new ArrayList<>();
        String query = "SELECT id_pregunta, id_tipo_pregunta, minimo, maximo, respuestas\n" +
                "FROM enc_pregunta\n" +
                "WHERE id_seccion IN (SELECT id_seccion FROM enc_seccion WHERE abierta = 1 AND id_nivel=" + idNivel + ") \n" +
                "ORDER BY codigo_pregunta";

        Cursor cursor = conn.rawQuery(query, null);
        while (cursor.moveToNext()) {
            Map<String, Integer> map = new HashMap<>();
            map.put("id_pregunta", cursor.getInt(0));
            map.put("id_tipo_pregunta", cursor.getInt(1));
            map.put("minimo", cursor.getInt(2));
            map.put("maximo", cursor.getInt(3));
            map.put("respuestas", cursor.getInt(4));
            res.add(map);
        }
        cursor.close();
        return res;
    }

    //PROCESA EL ENUNCIADO Y REEMPLAZA LA SIGUIENTE SINTAXIS: [CODIGO_PREGUNTA]
    public static String procesaEnunciado(IdInformante idInformante, String pregunta) {
        Set<String> allMatches = new HashSet<>();
        try {
            Matcher m = Pattern.compile("\\[(.*?)\\]").matcher(pregunta);
            while (m.find()) {
                String str = m.group();
                allMatches.add(str);
            }
            for (String str : allMatches) {
                String val = Encuesta.obtenerValor(idInformante, str.substring(1, str.length()-1).trim());
                pregunta = pregunta.replace(str, val==null?"ERROR":val);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "[ERROR]";
        }
        return pregunta;
    }
    //PROCESA EL ENUNCIADO Y REEMPLAZA LA SIGUIENTE SINTAXIS: [CODIGO_PREGUNTA]
    public static String procesaEnunciadoVariable(IdInformante idInformante, String pregunta, String variable) {
        String preguntafinal="";
        List<String> allMatches = new ArrayList<String>();
        String regla=null;
        String mensaje1=null;
        String mensaje2=null;
        JSONObject variableJson;
        JSONArray jsonArray;
        if(variable!=null) {
            try {
                jsonArray = new JSONArray(variable);
            } catch (Exception e) {
                return pregunta;
            }
            try {
                variableJson = jsonArray.getJSONObject(0);
            } catch (Exception e) {
                return pregunta;
            }

        try {
            regla=variableJson.getString("regla");
            mensaje1=variableJson.getString("mensaje1");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            mensaje2=variableJson.getString("mensaje2");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        }
        //procesar mensajes
        if(mensaje1!=null){
            Set<String> allMatches2 = new HashSet<>();
            String valorFinal = mensaje1;
            String nuevoValor = mensaje1;
//            Pattern p = Pattern.compile("\\$\\d+");
            Matcher m2 = Flujo.p.matcher(mensaje1);
            while (m2.find()) {
                String str2 = m2.group();
                allMatches2.add(str2);
            }
            for (String str2 : allMatches2) {
                String val;
                String element = str2.replace("$", "");
//                if (element.equals(String.valueOf(idPregunta))) {
//                    val = codResp;
//                } else {
                    val = Encuesta.obtenerValorIdPregunta(idInformante, element);
//                }
                if(val!=null)
                mensaje1 = nuevoValor.replace(str2, val);
            }

        }
        if(mensaje2 !=null){
            Set<String> allMatches2 = new HashSet<>();
            String valorFinal = mensaje2;
            String nuevoValor = mensaje2;
//            Pattern p = Pattern.compile("\\$\\d+");
            Matcher m2 = Flujo.p.matcher(mensaje2);
            while (m2.find()) {
                String str2 = m2.group();
                allMatches2.add(str2);
            }
            for (String str2 : allMatches2) {
                String val;
                String element = str2.replace("$", "");
//                if (element.equals(String.valueOf(idPregunta))) {
//                    val = codResp;
//                } else {
                val = Encuesta.obtenerValorIdPregunta(idInformante, element);
//                }
                if(val!=null)
                mensaje2 = nuevoValor.replace(str2, val);
            }
        }

            if(mensaje1!=null&&regla!=null){
            Flujo flujo=new Flujo();
            if (flujo.evaluar(regla,idInformante)) {
                pregunta = pregunta.replace("[NOMBRE]", mensaje1);
            }
            else {
                if(mensaje2 !=null)
                pregunta = pregunta.replace("[NOMBRE]", mensaje2);
            }
            }

        return pregunta;
    }

    public static String procesaEnunciadoFormula(IdInformante idInformante, final String formula) throws JSONException {

            String result=new JSONObject(formula).getString("formula");
            String mensaje2=new JSONObject(formula).getString("formula");
            Set<String> allMatches2 = new HashSet<>();
            boolean flag=false;
//            Pattern p = Pattern.compile("\\$\\d+");
            Matcher m2 = Flujo.p.matcher(result);
            while (m2.find()) {
                String str2 = m2.group();
                allMatches2.add(str2);
            }
            for (String str2 : allMatches2) {
                String val;
                String element = str2.replace("$", "");
//                if (element.equals(String.valueOf(idPregunta))) {
//                    val = codResp;
//                } else {
                val = Encuesta.obtenerValorIdPregunta(idInformante, element);
//                }
                if(val!=null) {
                    mensaje2 = mensaje2.replace(str2, val);
                flag=true;
                }
            }
            if(flag){
                Expression ee=new Expression(mensaje2);
               return ee.eval().toString();
            }


        return mensaje2;
    }

    //variable de variable en pregunta
//    public  static  String obtenerValorVariable(IdInformante idInformante, String pregunta, String variable){
//        if (variable.contains("?")) {
//            String[] part = variable.split("\\?");
//            String[] sino = part[1].split(":");
//            if (Encuesta.evaluar(part[0], idInformante, fila, pregunta.get_codigo_pregunta(), null) > 0) {
//                pregunta = pregunta.replace("[NOMBRE]", sino[0]);
//            }
//            else {
//                pregunta = pregunta.replace("[NOMBRE]", sino[1]);
//            }
//            if (sino.length == 3) {
//                pregunta = pregunta.replace("[NOMBRE]", Encuesta.obtenerValor(idInformante, sino[2].trim(), fila));
//            }
//        } else {
//            if(variable.contains(";")){
//                String[] part2=variable.split(";");
//                String val2 = Encuesta.obtenerValor(idInformante, part2[1], fila);
//                if (val2 != null) {
//                    pregunta = pregunta.replace("[NOMBRE2]", val2);
//                } else {
//                    val2 = Encuesta.obtenerValor(idInformante, val2, 1);
//                    if (val2 != null) {
//                        pregunta = pregunta.replace("[NOMBRE2]", val2);
//                    }
//                }
//                variable=part2[0];
//            }
//            String val = Encuesta.obtenerValor(idInformante, variable, fila);
//            if (val != null) {
//                pregunta = pregunta.replace("[NOMBRE]", val);
//            } else {
//                val = Encuesta.obtenerValor(idInformante, variable, 1);
//                if (val != null) {
//                    pregunta = pregunta.replace("[NOMBRE]", val);
//                }
//            }
//        }
//
//        return pregunta;
//
//    }

    public static String getPregunta(Integer id_pregunta) {
        String res = "";
        String query = "SELECT '<i><b>'|| p.codigo_pregunta ||'</b>, '|| p.pregunta ||'</i>'\n" +
                "FROM enc_pregunta p\n" +
                "WHERE id_pregunta = " + id_pregunta + "\n" +
                "AND estado <> 'ANULADO'";

        Cursor cursor = conn.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            res = cursor.getString(0);
        }
        cursor.close();
        return res;
    }

    /***
     * Obtiene el id de la pregunta
     * @param codigoPregunta parametro codificado en la boleta
     * @return id
     */
    public static int getIdPregunta(String codigoPregunta) {
        int res = -1;
        String query = "SELECT id_pregunta \n" +
                "FROM enc_pregunta p\n" +
                "WHERE codigo_pregunta = '" + codigoPregunta + "'\n" +
                "AND estado <> 'ANULADO'";

        Cursor cursor = conn.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            res = cursor.getInt(0);
        }
        cursor.close();
        return res;
    }
    public static int getFirstIdPreguntaSeccion(final int idSeccion, final IdInformante idInformante) {
        int res = -1;
        String query = "SELECT p.id_pregunta \n" +
                "FROM (SELECT * FROM enc_pregunta WHERE id_seccion="+idSeccion+" AND estado='ELABORADO') p JOIN (SELECT * FROM enc_encuesta WHERE visible like 't%' AND id_asignacion="+idInformante.id_asignacion+" AND correlativo="+idInformante.correlativo+") e ON p.id_pregunta=e.id_pregunta\n" +
                "ORDER BY codigo_pregunta \n" +
                "LIMIT 1";

      try{
          Cursor cursor = conn.rawQuery(query, null);
          if (cursor.moveToFirst()) {
              res = cursor.getInt(0);
          }
          cursor.close();
      }catch (Exception e){
          e.printStackTrace();
      }

        return res;
    }


    public static int getIDpregunta(int idSeccion, TipoPregunta tipoPregunta) {
        String query = "SELECT id_pregunta\n" +
                "FROM enc_pregunta\n" +
                "WHERE id_tipo_pregunta = " +tipoPregunta.ordinal() +"\n" +
                "AND id_seccion IN (" + idSeccion + ") \n" +
                "ORDER BY codigo_pregunta DESC\n" +
                "LIMIT 1";
        int idPregunta = 0;
        Cursor cursor = conn.rawQuery(query, null);
        if (cursor.moveToNext()) {
            idPregunta = cursor.getInt(0);
        }
        cursor.close();
        return idPregunta;
    }

    public static Map<Integer, String> getRespuestas(String respuestas) {
        Map<Integer, String> res = new LinkedHashMap<>();
        int codigo;
        String respuesta;
        try {
            JSONArray jsonArray;
            if(respuestas==null){
                jsonArray = new JSONArray();
            }else {
                jsonArray = new JSONArray(respuestas);
            }
            for (int j = 0; j < jsonArray.length(); j++) {
                JSONObject objetoJSON = jsonArray.getJSONObject(j);
                codigo = objetoJSON.getInt("codigo");
                respuesta = objetoJSON.getString("respuesta");
                res.put(j+1, codigo + "|" + respuesta + "|" + 1);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return res;
    }
    public static List<String> getRespuestasList(String respuestas) {
        List<String> res = new ArrayList();
        int codigo;
        String respuesta;
        try {
            JSONArray jsonArray;
            if(respuestas==null){
                jsonArray = new JSONArray();
            }else {
                jsonArray = new JSONArray(respuestas);
            }
            for (int j = 0; j < jsonArray.length(); j++) {
                JSONObject objetoJSON = jsonArray.getJSONObject(j);
                codigo = objetoJSON.getInt("codigo");
                respuesta = objetoJSON.getString("respuesta");
//                res.put(j+1, codigo + "|" + respuesta + "|" + 1);
                res.add(respuesta);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return res;
    }

    public static Map<Integer, String> getSaltos(String respuestas) {
        Map<Integer, String> res = new LinkedHashMap<>();
        int destino;
        int orden;
        String regla;
        String rpn;
        try {
            JSONArray jsonArray = new JSONArray(respuestas);
            for (int j = 0; j < jsonArray.length(); j++) {
                JSONObject objetoJSON = jsonArray.getJSONObject(j);
                destino = objetoJSON.getInt("destino");
                orden = objetoJSON.getInt("orden");
                regla = objetoJSON.getString("regla");
                res.put(j+1, destino + "°" + orden + "°" + regla);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return res;
    }

    public static Map<Integer, String> getRegla_conver(String reglas) {
        Map<Integer, String> res = new LinkedHashMap<>();
        String regla;
        String mensaje;
        String codigo;
        try {
            JSONArray jsonArray = new JSONArray(reglas);
            for (int j = 0; j < jsonArray.length(); j++) {
                JSONObject objetoJSON = jsonArray.getJSONObject(j);
                regla = objetoJSON.getString("regla");
                mensaje = objetoJSON.getString("mensaje");
                codigo = objetoJSON.getString("codigo");
                res.put(j+1, regla + "°" + mensaje + "°" + codigo);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return res;
    }

    //TODO:BRP{
    public static  ArrayList<Map<String, String>> getRegla_conver2(String reglas) {
        ArrayList<Map<String, String>> res = new ArrayList<>();
        String regla;
        String mensaje;
        String codigo;
        try {
            JSONArray jsonArray = new JSONArray(reglas);
            for (int j = 0; j < jsonArray.length(); j++) {
                Map<String, String> map = new HashMap<>();
                JSONObject objetoJSON = jsonArray.getJSONObject(j);
                map.put("regla", objetoJSON.getString("regla"));
                map.put("mensaje", objetoJSON.getString("mensaje"));
                map.put("codigo", objetoJSON.getString("codigo"));
                res.add(map);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return res;
    }
    //TODO:BRP}

    public static Map<Integer, String> getOmision_convert(String omision) {
        Map<Integer, String> res = new LinkedHashMap<>();
        Integer id;
        String codigo;
        try {
            JSONArray jsonArray = new JSONArray(omision);
            for (int j = 0; j < jsonArray.length(); j++) {
                JSONObject objetoJSON = jsonArray.getJSONObject(j);
                id = objetoJSON.getInt("id");
                codigo = objetoJSON.getString("codigo");
                res.put(j+1, id + "|" + codigo);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return res;
    }

    public static List<String> getOmisionId(int idPregunta) {
        List<String> res = new ArrayList<String>();
        Integer id;
        String query = "SELECT omision\n" +
                "FROM enc_pregunta\n" +
                "WHERE id_pregunta = " + idPregunta;
        String omision = "";
        Cursor cursor = conn.rawQuery(query, null);
        if (cursor.moveToNext()) {
            omision = cursor.getString(0);
        }
        cursor.close();
        if(!omision.equals("null") && !omision.equals("")) {
            try {
                JSONArray jsonArray = new JSONArray(omision);
                for (int j = 0; j < jsonArray.length(); j++) {
                    JSONObject objetoJSON = jsonArray.getJSONObject(j);
                    id = objetoJSON.getInt("id");
                    res.add(id.toString());
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return res;
    }

    public static List<String> getOmisionId2(String omision) {
        List<String> res = new ArrayList<String>();
        Integer id;
        if(!omision.equals("null") && !omision.equals("")) {
            try {
                JSONArray jsonArray = new JSONArray(omision);
                for (int j = 0; j < jsonArray.length(); j++) {
                    JSONObject objetoJSON = jsonArray.getJSONObject(j);
                    id = objetoJSON.getInt("id");
                    res.add(id.toString());
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return res;
    }

    public static int getCountPregs(int idNivel) {
        int res = 0;

        String query = "SELECT count(*)\n" +
                "FROM enc_pregunta\n" +
                "WHERE id_seccion in \n" +
                "(SELECT id_seccion \n" +
                "FROM enc_seccion \n" +
                "WHERE id_nivel = " + idNivel + ")";

        Cursor cursor = conn.rawQuery(query, null);
        if (cursor.moveToNext()) {
            res = cursor.getInt(0);
        }
        cursor.close();

        return res;
    }

    public static String getPreguntas(IdInformante idInformante, int idSeccion) {
        String res= "";

        String query = "SELECT ee.id_pregunta\n" +
                "FROM enc_encuesta ee, enc_pregunta ep\n" +
                "WHERE ee.id_pregunta = ep.id_pregunta\n" +
                "AND ee.id_asignacion = " + idInformante.id_asignacion +"\n" +
                "AND ee.correlativo = " + idInformante.correlativo +"\n" +
                "AND ep.id_seccion = " + idSeccion + "\n" +
                "AND ee.visible in ('true', 't')\n" +
                "ORDER BY ep.codigo_pregunta";

        Cursor cursor = conn.rawQuery(query, null);
        while (cursor.moveToNext()) {
            res += cursor.getString(0);
            res += ",";
        }
        cursor.close();

        return res;
    }

    public static int getCountPregs(IdInformante idInformante, int idSeccion) {
        int res= 0;

        String query = "SELECT COUNT(*)\n" +
                "FROM enc_encuesta ee, enc_pregunta ep\n" +
                "WHERE ee.id_pregunta = ep.id_pregunta\n" +
                "AND ee.id_asignacion = " + idInformante.id_asignacion +"\n" +
                "AND ee.correlativo = " + idInformante.correlativo +"\n" +
                "AND ep.id_seccion = " + idSeccion;

        Cursor cursor = conn.rawQuery(query, null);
        while (cursor.moveToNext()) {
            res = cursor.getInt(0);
        }
        cursor.close();

        return res;
    }


    public static Map<Integer, String> getRespuestasQuery(int id_asignacion, int correlativo, int id_asignacion_padre, int correlativo_padre, int id_upm, String codigo, String descripcion, String respuestasQuery, int fila) {
        List<String> allMatches = new ArrayList<String>();
        Matcher m = Pattern.compile("\\[(.*?)\\]").matcher(respuestasQuery);
        while (m.find()) {
            String str = m.group();
            allMatches.add(str);
        }
        for (String str : allMatches) {
            String nombre_columna = str.substring(1, str.length()-1).trim();
            String valor = "";
            switch (nombre_columna) {
                case "id_asignacion":
                    valor = String.valueOf(id_asignacion);
                    break;
                case "correlativo":
                    valor = String.valueOf(correlativo);
                    break;
                case "id_asignacion_padre":
                    valor = String.valueOf(id_asignacion_padre);
                    break;
                case "correlativo_padre":
                    valor = String.valueOf(correlativo_padre);
                    break;
                case "id_upm":
                    valor = String.valueOf(id_upm);
                    break;
                case "codigo":
                    valor = codigo;
                    break;
                case "descripcion":
                    valor = descripcion;
                    break;
                case "fila":
                    valor = String.valueOf(fila);
                    break;
            }
            respuestasQuery = respuestasQuery.replace(str, valor==null?"ERROR":valor);
        }


        Map<Integer, String> res = new LinkedHashMap<>();
        Cursor cursor = null;
        try {
            cursor = conn.rawQuery(respuestasQuery, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndex("id_respuesta"));
                String cod = cursor.getString(cursor.getColumnIndex("codigo_respuesta"));
                String resp = cursor.getString(cursor.getColumnIndex("respuesta"));
                res.put(id, cod + "|" + resp + "|0");
            } while (cursor.moveToNext());
        }
        cursor.close();
        return res;
    }

    public static ArrayList<Integer> getNextPreguntas(String codPregunta, String seccion) {
        ArrayList<Integer> res = new ArrayList<>();
        String query = "SELECT id_pregunta\n" +
                "FROM enc_pregunta p\n" +
                "WHERE codigo_pregunta > '"+codPregunta+"' \n"+
                "AND id_seccion IN (" + seccion +")";

        Cursor cursor = conn.rawQuery(query, null);
        while (cursor.moveToNext()) {
            res.add(cursor.getInt(0));
        }
        cursor.close();
        return res;
    }
    public static ArrayList<Integer> getNextSecciones(int idSeccion, String seccion) {
        ArrayList<Integer> res = new ArrayList<>();
        String query = "SELECT id_seccion\n" +
                "FROM enc_seccion p\n" +
                "WHERE codigo > (select codigo from enc_seccion where id_seccion = "+idSeccion+" )\n"+
                "AND id_seccion IN (" + seccion +")";

        Cursor cursor = conn.rawQuery(query, null);
        while (cursor.moveToNext()) {
            res.add(cursor.getInt(0));
        }
        cursor.close();
        return res;
    }

    public static ArrayList<Integer> getNextPreguntas(String codPregunta, String seccion, int idAsignacion, int correlativo) {
        ArrayList<Integer> res = new ArrayList<>();
        String query = "SELECT e.id_pregunta\n" +
                "FROM enc_encuesta e\n" +
                "JOIN enc_pregunta p ON e.id_pregunta = p.id_pregunta\n" +
                "WHERE codigo_pregunta > '"+codPregunta+"'\n" +
                "AND id_seccion IN (" + seccion +")\n" +
                "AND id_asignacion = "+idAsignacion+" \n" +
                "AND correlativo = "+correlativo+"\n" +
                "AND visible LIKE 't%'";

        Cursor cursor = conn.rawQuery(query, null);
        while (cursor.moveToNext()) {
            res.add(cursor.getInt(0));
        }
        cursor.close();
        return res;
    }

    public static Map<Integer, String> getPreguntasSeccion(String preguntas) {
        Map<Integer, String> res = new LinkedHashMap<>();

        String query = "SELECT p.id_seccion, GROUP_CONCAT(id_pregunta, ',') AS variables_concatenadas\n" +
                "FROM enc_pregunta p\n" +
                "JOIN enc_seccion s\n" +
                "ON p.id_seccion = s.id_seccion\n" +
                "WHERE id_pregunta IN ("+preguntas+") \n" +
                "GROUP BY p.id_seccion\n" +
                "ORDER BY codigo";

        Cursor cursor = conn.rawQuery(query, null);
        while (cursor.moveToNext()) {
            res.put(cursor.getInt(0), cursor.getString(1));
        }
        cursor.close();
        return res;
    }

    ///Modelo para datos default para encuesta inicial



    public ArrayList<Map<String, Object>> obtenerListadoCabecera() {
        ArrayList<Map<String, Object>> result=null;
        result= super.obtenerListado("", "");

        return result;
    }

    public static Map<Integer, Map<String, String>> getPreguntaMatriz(String codEspecifique) {
//    public static Map<Integer, Map<String, String>> getPreguntaMatriz(Integer codEspecifique) {
        Map<Integer, Map<String, String>> res = new LinkedHashMap<>();
        String query = "SELECT id_pregunta, codigo_pregunta, pregunta, respuesta\n" +
                "FROM enc_pregunta\n" +
                "WHERE codigo_especifique = '" + codEspecifique +"'\n" +
                "ORDER BY codigo_pregunta";

        Cursor cursor = conn.rawQuery(query, null);
        while (cursor.moveToNext()) {
            Map<String, String> pos = new HashMap<>();
            pos.put("codigo_pregunta", cursor.getString(1));
            pos.put("pregunta", cursor.getString(2));
            pos.put("respuesta", cursor.getString(3));

            res.put(cursor.getInt(0), pos);
        }
        cursor.close();
        return res;
    }

    @SuppressWarnings("unused")
    public Integer get_id_pregunta() {
        return filaActual.getInt(filaActual.getColumnIndex("id_pregunta"));
    }
    @SuppressWarnings("unused")
    public void set_id_pregunta(Integer value) {
        filaNueva.put("id_pregunta", value);
    }

    @SuppressWarnings("unused")
    public Integer get_id_seccion() {
        if (filaActual.isNull(filaActual.getColumnIndex("id_seccion"))) {
            return null;
        } else {
            return filaActual.getInt(filaActual.getColumnIndex("id_seccion"));
        }
    }
    @SuppressWarnings("unused")
    public void set_id_seccion(Integer value) {
        filaNueva.put("id_seccion", value);
    }

    @SuppressWarnings("unused")
    public String get_codigo_pregunta() {
        if(filaActual.getInt(filaActual.getColumnIndex("id_pregunta"))==2193){
            return "A_01";
        }else  if(filaActual.getInt(filaActual.getColumnIndex("id_pregunta"))==15007){
            return "UPM";
        }

        return filaActual.getString(filaActual.getColumnIndex("codigo_pregunta"));
    }
    @SuppressWarnings("unused")
    public void set_codigo_pregunta(String value) {
        filaNueva.put("codigo_pregunta", value);
    }

    @SuppressWarnings("unused")
    public String get_respuesta() {
        return filaActual.getString(filaActual.getColumnIndex("respuesta"));
    }
    @SuppressWarnings("unused")
    public void set_respuesta(String value) {
        filaNueva.put("respuesta", value);
    }


    @SuppressWarnings("unused")
    public String get_pregunta() {
        return filaActual.getString(filaActual.getColumnIndex("pregunta"));
    }
    @SuppressWarnings("unused")
    public void set_pregunta(String value) {
        filaNueva.put("pregunta", value);
    }

    @SuppressWarnings("unused")
    public String get_ayuda() {
        if (filaActual.isNull(filaActual.getColumnIndex("ayuda"))) {
            return null;
        } else {
            return filaActual.getString(filaActual.getColumnIndex("ayuda"));
        }
    }
    @SuppressWarnings("unused")
    public void set_ayuda(String value) {
        filaNueva.put("ayuda", value);
    }

    @SuppressWarnings("unused")
    public TipoPregunta get_id_tipo_pregunta() {
        return TipoPregunta.values()[filaActual.getInt(filaActual.getColumnIndex("id_tipo_pregunta"))];
    }
    @SuppressWarnings("unused")
    public void set_id_tipo_pregunta(TipoPregunta value) {
        filaNueva.put("id_tipo_pregunta", value.ordinal());
    }

    @SuppressWarnings("unused")
    public Integer get_minimo() {
        return filaActual.getInt(filaActual.getColumnIndex("minimo"));
    }
    @SuppressWarnings("unused")
    public void set_minimo(Integer value) {
        filaNueva.put("minimo", value);
    }

    @SuppressWarnings("unused")
    public Long get_maximo() {
        return filaActual.getLong(filaActual.getColumnIndex("maximo"));
    }
    @SuppressWarnings("unused")
    public void set_maximo(Long value) {
        filaNueva.put("maximo", value);
    }

    @SuppressWarnings("unused")
    public String get_catalogo() {
        return filaActual.getString(filaActual.getColumnIndex("catalogo"));
    }
    @SuppressWarnings("unused")
    public void set_catalogo(String value) {
        filaNueva.put("catalogo", value);
    }

    @SuppressWarnings("unused")
    public Integer get_longitud() {
        if (filaActual.isNull(filaActual.getColumnIndex("longitud"))) {
            return null;
        } else {
            return filaActual.getInt(filaActual.getColumnIndex("longitud"));
        }
    }
    @SuppressWarnings("unused")
    public void set_longitud(Integer value) {
        filaNueva.put("longitud", value);
    }

    @SuppressWarnings("unused")
    public String get_codigo_especifique() {
        return filaActual.getString(filaActual.getColumnIndex("codigo_especifique"));
    }
    @SuppressWarnings("unused")
    public void set_codigo_especifique(String value) {
        filaNueva.put("codigo_especifique", value);
    }

    @SuppressWarnings("unused")
    public Integer get_inicial() {
        return filaActual.getInt(filaActual.getColumnIndex("inicial"));
    }
    @SuppressWarnings("unused")
    public void set_inicial(Integer value) {
        filaNueva.put("inicial", value);
    }

    @SuppressWarnings("unused")
    public String get_apoyo() {
        return filaActual.getString(filaActual.getColumnIndex("apoyo"));
    }
    @SuppressWarnings("unused")
    public void set_apoyo(String value) {
        filaNueva.put("apoyo", value);
    }

    @SuppressWarnings("unused")
    public String get_omision() {
        return filaActual.getString(filaActual.getColumnIndex("omision"));
    }
    @SuppressWarnings("unused")
    public void set_omision(Integer value) {
        filaNueva.put("omision", value);
    }

    @SuppressWarnings("unused")
    public String get_regla() {
        return filaActual.getString(filaActual.getColumnIndex("regla"));
    }
    @SuppressWarnings("unused")
    public void set_regla(String value) {
        filaNueva.put("regla", value);
    }

    @SuppressWarnings("unused")
    public String get_saltos() {
        return filaActual.getString(filaActual.getColumnIndex("saltos"));
    }
    @SuppressWarnings("unused")
    public void set_saltos(String value) {
        filaNueva.put("saltos", value);
    }

    @SuppressWarnings("unused")
    public String get_estado() {
        return filaActual.getString(filaActual.getColumnIndex("estado"));
    }
    @SuppressWarnings("unused")
    public void set_estado(String value) {
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
    public String get_variable() {
        return filaActual.getString(filaActual.getColumnIndex("variable"));
    }
    public void set_variable(String value) {
        filaNueva.put("variable", value);
    }
    public String get_formula() {
        return filaActual.getString(filaActual.getColumnIndex("formula"));
    }
    public void set_formula(String value) {
        filaNueva.put("formula", value);
    }
    public int get_codigo_especial() {
        return 4;
    }


    ////datos procesados
    public JSONArray getRegla() throws JSONException {
       return   !filaActual.getString(filaActual.getColumnIndex("regla")).equals("")&&filaActual.getString(filaActual.getColumnIndex("regla")).equals("null")?new JSONArray(filaActual.getString(filaActual.getColumnIndex("regla"))):null;
    }
    public JSONArray getRespuesta() throws JSONException {
        return   !filaActual.getString(filaActual.getColumnIndex("respuesta")).equals("")&&filaActual.getString(filaActual.getColumnIndex("respuesta")).equals("null")?new JSONArray(filaActual.getString(filaActual.getColumnIndex("respuesta"))):null;
    }
    public JSONArray getSaltos() throws JSONException {
        return   !filaActual.getString(filaActual.getColumnIndex("saltos")).equals("")&&filaActual.getString(filaActual.getColumnIndex("saltos")).equals("null")?new JSONArray(filaActual.getString(filaActual.getColumnIndex("saltos"))):null;
    }
    public JSONArray getOmision() throws JSONException {
        return   !filaActual.getString(filaActual.getColumnIndex("omision")).equals("")&&filaActual.getString(filaActual.getColumnIndex("omision")).equals("null")?new JSONArray(filaActual.getString(filaActual.getColumnIndex("omision"))):null;
    }
    public JSONArray getVariable() throws JSONException {
        return   !filaActual.getString(filaActual.getColumnIndex("variable")).equals("")&&filaActual.getString(filaActual.getColumnIndex("variable")).equals("null")?new JSONArray(filaActual.getString(filaActual.getColumnIndex("variable"))):null;
    }

}
