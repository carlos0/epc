package bo.gob.ine.naci.epc.entidades;

import android.database.Cursor;
import android.util.Log;

import com.udojava.evalex.Expression;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by INE.
 */

public class EncLog extends EntidadCorr {
    public EncLog() {
        super("enc_log");
    }

    public static int nroViviendasUPM(Integer idAsignacion) {
        int res = 0;
        String query = "SELECT CAST(valor1 AS Int), id_log\n" +
                "FROM enc_log\n" +
                "WHERE id_asignacion = " + idAsignacion + "\n" +
                "AND estado LIKE 'ELABORADO'\n" +
                "AND id_tipo = 8\n" +
                "ORDER BY id_log DESC\n" +
                "LIMIT 1";
        Cursor cursor = conn.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            res = cursor.getInt(0);
        }
        cursor.close();
        return res;
    }



    ///optimizar funcion()
    public static Map<Integer, String> getConsulta(IdInformante informante, IdInformante idPadre, String respuesta) {
        Map<Integer, String> res = new LinkedHashMap<>();
        String regla = "";
        ArrayList list = new ArrayList();
        try {
            JSONArray jsonArray = new JSONArray(respuesta);
            for (int j = 0; j < jsonArray.length(); j++) {
                JSONObject objetoJSON = jsonArray.getJSONObject(j);
                regla = objetoJSON.getString("regla");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String query = "SELECT ' id_asignacion = '|| id_asignacion || ' and correlativo = ' || correlativo || ' '\n" +
                "FROM enc_informante\n" +
                "WHERE id_asignacion_padre = " + idPadre.id_asignacion + "\n" +
                "AND correlativo_padre = " + idPadre.correlativo + "\n" +
                "AND estado <> 'ANULADO'\n" +
                "AND correlativo not in (" + informante.correlativo + ") \n" +
                "ORDER BY codigo";

//        String query = "SELECT DISTINCT ' id_asignacion = '|| id_asignacion || ' and correlativo = ' || correlativo || ' '\n" +
//                "FROM enc_log\n" +
//                "WHERE id_asignacion = " + idPadre.id_asignacion + "\n" +
//                "AND id_tipo = " + idPadre.correlativo + "\n" +
//                "AND estado LIKE 'ELABORADO'\n" +
//                "AND correlativo not in (" + informante.correlativo + ") \n" +
//                "ORDER BY id_log";
        Cursor cursor = conn.rawQuery(query, null);
        while (cursor.moveToNext()) {
            Log.d("consulta1", "entro");
            list.add(cursor.getString(0));
        }
        cursor.close();

        List<String> allMatches = new ArrayList<String>();

        Pattern p = Pattern.compile("\\$\\d+");
        Matcher m = p.matcher(regla);
        while (m.find()) {
            String str = m.group();
            allMatches.add(str);
        }
        int cont = 0;
        for (int i = 0; i < list.size(); i++) {
            String nuevoValor = regla;
            for (String str : allMatches) {
                String val = "0";

                String element = str.replace("$", "");
                String squery = "SELECT codigo_respuesta\n" +
                        "FROM enc_encuesta\n" +
                        "WHERE " + list.get(i) + " \n" +
                        "AND id_pregunta = " + element + "\n";
                Log.d("consulta1.1", squery);
                Cursor scursor = conn.rawQuery(squery, null);
                if (scursor.moveToFirst()) {
                    Log.d("consulta25", scursor.getString(0));
                    val = scursor.getString(0);
                }
//                String squery = "SELECT valor2\n" +
//                        "FROM enc_log\n" +
//                        "WHERE " + list.get(i) + "\n" +
//                        "AND id_pregunta = " + element + "\n";
//                Cursor scursor = conn.rawQuery(squery, null);
//                if (scursor.moveToFirst()) {
//                    val = scursor.getString(0);
//                }
                scursor.close();
                nuevoValor = nuevoValor.replace(str, val);
            }
            //FALTA EVALUAR LOS DATOS DE PREGUNTAS MULTIPLES Y PREGUNTAS DE ORDEN
            BigDecimal eval = null;
            Expression expression = new Expression(nuevoValor);
            eval = expression.eval();
            Log.d("consulta2", nuevoValor);
            Log.d("consulta3", list.get(i).toString());
            Log.d("consulta3.1", String.valueOf(eval));
            if(Integer.parseInt(String.valueOf(eval)) > 0){

                String pquery = "SELECT codigo, respuesta\n" +
                        "FROM enc_informante ei, enc_encuesta ee\n" +
                        "WHERE ei.id_asignacion = ee.id_asignacion AND ei.correlativo= ee.correlativo \n" +
                        "AND " + list.get(i).toString().replace("id_asignacion", "ei.id_asignacion").replace("correlativo", "ei.correlativo") + " \n" +
                        "AND id_pregunta = 18581";
//                String pquery = "SELECT valor1, descripcion\n" +
//                        "FROM enc_log\n" +
//                        "WHERE " + list.get(i) + "\n" +
//                        "AND id_pregunta = 18581 \n";
                Log.d("consulta 3.2", pquery);
                Cursor pcursor = conn.rawQuery(pquery, null);
                if (pcursor.moveToFirst()) {
                    Log.d("consulta4", pcursor.getString(0) + "|" + pcursor.getString(1));
                    res.put(cont +1, pcursor.getInt(0) + "|" + pcursor.getString(1));
                }
                pcursor.close();
                cont++;
            }
        }
        return res;
    }

    /*public static Map<Integer, String> getDocumentos () {
        Map<Integer, String> res = new LinkedHashMap<>();
        String strQuery = "SELECT id_documento, nombre\n" +
                "FROM ope_documento\n" +
                "WHERE estado = 'ELABORADO'";
        Cursor cursor = conn.rawQuery(strQuery, null);
        if (cursor.moveToFirst()) {
            do {
                res.put(cursor.getInt(0), cursor.getString(1));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return res;
    }*/

    @SuppressWarnings("unused")
    public Integer get_id_log() {
        return filaActual.getInt(filaActual.getColumnIndex("id_log"));
    }
    @SuppressWarnings("unused")
    public void set_id_log(Integer value) {
        filaNueva.put("id_log", value);
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
    public Integer get_id_tipo() {
        return filaActual.getInt(filaActual.getColumnIndex("id_tipo"));
    }
    @SuppressWarnings("unused")
    public void set_id_tipo(Integer value) {
        filaNueva.put("id_tipo", value);
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
    public Integer get_id_asignacion() {
        return filaActual.getInt(filaActual.getColumnIndex("id_asignacion"));
    }
    @SuppressWarnings("unused")
    public void set_id_asignacion(Integer value) {
        filaNueva.put("id_asignacion", value);
    }
    @SuppressWarnings("unused")
    public Integer get_correlativo() {
        return filaActual.getInt(filaActual.getColumnIndex("correlativo"));
    }
    @SuppressWarnings("unused")
    public void set_correlativo(Integer value) {
        filaNueva.put("correlativo", value);
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
    public Integer get_fila() {
        return filaActual.getInt(filaActual.getColumnIndex("fila"));
    }
    @SuppressWarnings("unused")
    public void set_fila(Integer value) {
        filaNueva.put("fila", value);
    }
    @SuppressWarnings("unused")
    public String get_valor1() {
        return filaActual.getString(filaActual.getColumnIndex("valor1"));
    }
    @SuppressWarnings("unused")
    public void set_valor1(String value) {
        filaNueva.put("valor1", value);
    }
    @SuppressWarnings("unused")
    public String get_valor2() {
        return filaActual.getString(filaActual.getColumnIndex("valor2"));
    }
    @SuppressWarnings("unused")
    public void set_valor2(String value) {
        filaNueva.put("valor2", value);
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
    public String get_usumod() {
        if (filaActual.isNull(filaActual.getColumnIndex("usumod"))) {
            return null;
        } else {
            return filaActual.getString(filaActual.getColumnIndex("usumod"));
        }
    }
    @SuppressWarnings("unused")
    public void set_usumod(String value) {
        filaNueva.put("usumod", value);
    }
    @SuppressWarnings("unused")
    public Long get_fecmod() {
        if (filaActual.isNull(filaActual.getColumnIndex("fecmod"))) {
            return null;
        } else {
            return filaActual.getLong(filaActual.getColumnIndex("fecmod"));
        }
    }
    @SuppressWarnings("unused")
    public void set_fecmod(Long value) {
        filaNueva.put("fecmod", value);
    }

}
