package bo.gob.ine.naci.epc.entidades;

import android.database.Cursor;
import android.util.Log;

import com.udojava.evalex.Expression;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by INE.
 */
//EN CSV
public class Flujo extends EntidadId {
    public Flujo() {
        super("enc_flujo");
    }
    public static Pattern p = Pattern.compile("\\$\\d+");

    public int[] anterior(IdInformante idInformante) {
        int idAnterior = -1;
        int fila = -1;
        String query = "SELECT fila, id_pregunta\n" +
                "FROM enc_encuesta\n" +
                "WHERE id_asignacion = " + idInformante.id_asignacion + "\n" +
                "AND correlativo = " + idInformante.correlativo + "\n" +
                "ORDER BY id_last DESC\n" +
                "LIMIT 1";

        Cursor cursor = conn.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                idAnterior = cursor.getInt(1);
                fila = cursor.getInt(0);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return new int[]{idAnterior, fila};
    }

    public boolean evaluar(String rpn, IdInformante idInformante) {
        boolean result;
        String[] aux = null;
        Set<String> allMatches = new HashSet<>();
        String nuevoValor = rpn;
        Matcher m = p.matcher(rpn);
        while (m.find()) {
            String str = m.group();
            allMatches.add(str);
        }
        for (String str : allMatches) {
            String val;
            String element = str.replace("$", "");
//            if(nuevoValor.contains("TOTAL")){
//                val = Encuesta.obtenerValor(idInformante, "s06a_01");
//            } else {

//            if(element.equals("36303") || element.equals("36443")){
            ///para preguntas tipo tabla
            if(element.equals("36303") || element.equals("36443")){
                Log.d("preguntas_objetos1", element);
                String value = Encuesta.obtenerValor(idInformante, "s06a_01");
//                String nvalue = value.replace("<>", "\"");
                String nvalue = value;
                Log.d("recibe Respuesta", nvalue);
                ArrayList<String> respuestas = recuperaDatosJson(nvalue, element);
                Log.d("tamaño", String.valueOf(respuestas.size()));
                if(respuestas.size()>1){
//                    aux = respuestas.toString().split(",");
                    aux = respuestas.toArray(new String[0]);
                    val = "(aux)";
                } else if(respuestas.size() == 0){
                    val = "null";
                } else {
                    val = respuestas.get(0).toString();
                }
                nuevoValor = nuevoValor.replace(str, val);
            } else {
                val = Encuesta.obtenerValorId(idInformante, element);
                if (val.contains(",")) {
                    aux = val.split(",");
                    val = "(aux)";
                }
                nuevoValor = nuevoValor.replace(str, val);
                if (nuevoValor.contains("CODIGO")) {
                    val = String.valueOf(Informante.getCodigo(idInformante));
                    nuevoValor = nuevoValor.replace("CODIGO", val);
                }
            }
//            }
        }
        //EXPRESIONES GENERALES
        if (nuevoValor.contains("RURAL")) {
            //2 rural
            //1 urbano

            String urbano = String.valueOf(Informante.getUrbano(Asignacion.getUpm(idInformante.id_asignacion)));
            nuevoValor = nuevoValor.replace("RURAL", urbano);
            Log.d("rural", nuevoValor);
        }
        if (nuevoValor.contains("PERSONAS")) {
            if (Informante.hayPersonasConcluidas(idInformante)>0) {
                nuevoValor = nuevoValor.replace("PERSONAS", String.valueOf(Informante.hayPersonasConcluidas(idInformante)));
            } else {
                nuevoValor = nuevoValor.replace("PERSONAS", "0");
            }
        }
        if (nuevoValor.contains("HAY_CARNET")) {
            if (Informante.getHaySelectCarnet(idInformante)>0) {
                nuevoValor = nuevoValor.replace("HAY_CARNET", String.valueOf(Informante.getHaySelectCarnet(idInformante)));
            } else {
                nuevoValor = nuevoValor.replace("HAY_CARNET", "0");
            }
        }
        if (nuevoValor.contains("PERSONA15")) {
            Log.d("persona15", String.valueOf(Informante.contarMayores15(idInformante)));
            if (Informante.contarMayores15(idInformante)>0) {
                nuevoValor = nuevoValor.replace("PERSONA15", String.valueOf(Informante.contarMayores15(idInformante)));
            } else {
                nuevoValor = nuevoValor.replace("PERSONA15", "0");
            }
        }
        nuevoValor = nuevoValor.replace("~", "==");
        nuevoValor = nuevoValor.replace("!(", "NOT(");
//        nuevoValor = nuevoValor.replace("TOTAL", Encuesta.obtenerValorId(idInformante, "36201"));
        //EVALUACION
        BigDecimal eval = null;
        if(nuevoValor.contains("(aux)")){
            String valorEval;
            int cont = 0;
            for(String naux : aux){
                valorEval = nuevoValor.replace("(aux)", naux);
                Expression expression = new Expression(valorEval);
                eval = expression.eval();
                if (Integer.parseInt(String.valueOf(eval)) > 0) {
                    cont++;
                }
            }
            if(aux.length == cont){
                result = true;
            } else {
                result = false;
            }
        } else {
            result = false;
            if(nuevoValor.contains("null")){
                //TODO: VERIFICAR CONSISTENCIA
                result = false;
            }else{
                Expression expression = new Expression(nuevoValor);
                eval = expression.eval();
                if (Integer.parseInt(String.valueOf(eval)) > 0) {
                    result = true;
                }
            }
        }
        Log.d("rural2", String.valueOf(result));
        return result;
    }

    public ArrayList<String> recuperaDatosJson(String value, String pregunta){
        String rdescripcion;

        ArrayList<String> rcodigo = new ArrayList<>();

        try {
            JSONArray jsonArrayDesc = new JSONArray(value);
//            JSONObject objetoJSONDesc = jsonArrayDesc.getJSONObject(0);
//            JSONArray jsonArrayI = new JSONArray(rdescripcion);
            for (int i = 0; i < jsonArrayDesc.length(); i++) {

                JSONObject objetoJSONDesc = jsonArrayDesc.getJSONObject(i);
                Log.d("recuperado", String.valueOf(objetoJSONDesc));
                rdescripcion = objetoJSONDesc.getString("descripcion");
                JSONArray jsonArray = new JSONArray(rdescripcion);
                Log.d("recuperacion1000", rdescripcion+ "tamaño"+ String.valueOf(jsonArray.length()));
//                respGuardada = new LinkedHashMap<>();
                for (int j = 0; j < jsonArray.length(); j++) {
                    JSONObject objetoJSON = jsonArray.getJSONObject(j);
                    Log.d("recuperado3", String.valueOf(objetoJSON) + "la pregunta:"+ pregunta);
                    if(objetoJSON.getString("id_pregunta").equals(pregunta)){
                        Log.d("recuperado4", String.valueOf(objetoJSON.getString("id_pregunta").equals(pregunta)));
                        rcodigo.add(objetoJSON.getString("codigo"));
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d("respuestaFinal", String.valueOf(rcodigo));
        return rcodigo;
    }

    public boolean evaluar2(String rpn, IdInformante idInformante, int fila) {
        if (rpn == null || rpn.equals("")) {
            return true;
        }
        float val1, val2;
        LinkedList<Float> pila = new LinkedList<>();
        String[] elements = rpn.split(" ");
        String[] conjunto = new String[]{};
        for (String element : elements) {
            byte fl = (byte) element.charAt(0);
            if (fl > 64 && fl < 91) {
                switch (element) {
                    case "CODIGO": {
//                        pila.add(Informante.getCodigo(idInformante));
                        break;
                    }
                    case "CONTAR": {
                        pila.add(Informante.getContar(idInformante));
                        break;
                    }
                    case "FILA": {
                        pila.add((float)fila);
                        break;
                    }
                    default: {
                        String val;
                        if (element.endsWith(".VAL")) {
                            val = Encuesta.obtenerValor(idInformante, element).split(":")[0];
                        } else {
                            if (element.endsWith(".AUX")) {
                                String valAux = Encuesta.obtenerValor(idInformante, element.replace(".AUX", ""));
                                if(valAux != null){
                                    val = valAux.split(":")[0];
                                    String cod = valAux.split(":")[1];
                                    if (cod.toUpperCase().contains("NINGUNO")) {
                                        val = "0";
                                    } else if(cod.toUpperCase().contains("SEMANAL")) {
                                        val =(Float.parseFloat(val) * 52)+"";
                                    } else if (cod.toUpperCase().contains("MENSUAL")) {
                                        val =(Float.parseFloat(val) * 12)+"";
                                    } else if (cod.toUpperCase().contains("BIMESTRAL")) {
                                        val =(Float.parseFloat(val) * 6)+"";
                                    } else if (cod.toUpperCase().contains("TRIMESTRAL")) {
                                        val =(Float.parseFloat(val) * 4)+"";
                                    } else if (cod.toUpperCase().contains("SEMESTRAL")) {
                                        val =(Float.parseFloat(val) * 2)+"";
                                    } else if (cod.toUpperCase().contains("ANUAL")) {
                                        val =(Float.parseFloat(val) * 1)+"";
                                    }
                                } else {
                                    val = "0";
                                }
                            } else {
                                if (element.endsWith(".SUMAR")) {
                                    val = Encuesta.sumarBucle(idInformante, element.substring(0, element.indexOf(".SUMAR"))).toString();
                                } else {
                                    if (element.endsWith(".CONTAR")) {
                                        val = Encuesta.contarBucle(idInformante, element.substring(0, element.indexOf(".CONTAR"))).toString();
                                    } else {
                                        if (element.endsWith(".LONGITUD")) {
                                            conjunto = Encuesta.obtenerCod(idInformante, element.substring(0, element.indexOf(".LONGITUD")), fila).split(";");
                                            val = String.valueOf(conjunto.length);
                                        } else {
                                            if (element.endsWith(".VACIO")) {
                                                conjunto = Encuesta.obtenerCod(idInformante, element.substring(0, element.indexOf(".VACIO")), fila).split(";");
                                                if (conjunto == null) {
                                                    val = "1";
                                                } else {
                                                    val = String.valueOf(conjunto[0].split(",")[0]);
                                                }
                                            }
                                            else {
                                                conjunto = Encuesta.obtenerCod(idInformante, element, fila).split(";");
                                                if (conjunto[0].equals("")) {
                                                    conjunto = new String[]{"0,0"};
                                                }
                                                val = conjunto[0].split(",")[0];
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        if (val == null || val.equals("")) {
                            pila.add(0f);
                        } else {
                            try {
                                pila.add(Float.valueOf(val));
                            } catch (Exception ex) {
                                pila.add(0f);
                            }
                        }
                    }
                }
            } else {
                if (fl > 47 && fl < 58) {
                    pila.add(Float.parseFloat(element));
                } else {
                    val2 = pila.pollLast();
                    if (element.equals("!")) {
                        pila.add((float) ((val2 == 0) ? 1 : 0));
                    } else {
                        val1 = pila.pollLast();
                        switch (element) {
                            case "+":
                                pila.add(val1 + val2);
                                break;
                            case "-":
                                pila.add(val1 - val2);
                                break;
                            case "*":
                                pila.add(val1 * val2);
                                break;
                            case "/":
                                pila.add(val1 / val2);
                                break;
                            case "\\":
                                pila.add((float)(int)(val1 / val2));
                                break;
                            case "%":
                                pila.add(val1 % val2);
                                break;
                            case "^":
                                pila.add((float) Math.pow(val1, val2));
                                break;
                            case "=":
                            case "==":
                                pila.add((float) ((val1 == val2) ? 1 : 0));
                                break;
                            case "!=":
                                pila.add((float) ((val1 != val2) ? 1 : 0));
                                break;
                            case "<=":
                                pila.add((float) ((val1 <= val2) ? 1 : 0));
                                break;
                            case ">=":
                                pila.add((float) ((val1 >= val2) ? 1 : 0));
                                break;
                            case "<":
                                pila.add((float) ((val1 < val2) ? 1 : 0));
                                break;
                            case ">":
                                pila.add((float) ((val1 > val2) ? 1 : 0));
                                break;
                            case "~":
                                float in = 0;
                                for (String c : conjunto) {
                                    if (c.equals("")) {
                                        in = 0;
                                        break;
                                    }
                                    if (val1 == Float.valueOf(c.split(",")[1])) {
                                        in = 1;
                                        break;
                                    }
                                }
                                pila.add(in);
                                break;
                            case "&&":
                                pila.add((float) ((val1 > 0 && val2 > 0) ? 1 : 0));
                                break;
                            case "||":
                                pila.add((float) ((val1 + val2 > 0) ? 1 : 0));
                                break;
                        }
                    }
                }
            }
        }
        return pila.getLast() > 0;
    }

//    public int siguienteDirecta(int idPregunta) {
//        int res = -1;
//        String query = "SELECT p1.id_pregunta\n" +
//                "FROM enc_pregunta p1, enc_pregunta p2\n" +
//                "WHERE p1.codigo_pregunta > p2.codigo_pregunta\n" +
//                "AND p2.id_pregunta = " + idPregunta + "\n" +
//                "ORDER BY UPPER(p1.codigo_pregunta)\n" +
//                "LIMIT 1";
//
//        Cursor cursor = conn.rawQuery(query, null);
//        if (cursor.moveToFirst()) {
//            do {
//                res = cursor.getInt(0);
//            } while (cursor.moveToNext());
//        }
//        cursor.close();
//        return res;
//    }
    public int siguienteDirecta(int idPregunta) {
        int res = -1;
        String query = "SELECT p1.id_pregunta,es1.id_nivel, es2.id_nivel\n" +
                "FROM enc_pregunta p1 JOIN enc_pregunta p2\n" +
                "ON p1.codigo_pregunta > p2.codigo_pregunta\n" +
                "AND p2.id_pregunta = "+idPregunta+" JOIN enc_seccion es1 on p1.id_seccion=es1.id_seccion JOIN enc_seccion es2 on p2.id_seccion=es2.id_seccion\n" +
                "WHERE es1.id_nivel=es2.id_nivel\n" +
                "ORDER BY p1.codigo_pregunta\n" +
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

    public ArrayList<Map<String, Object>> flujoDestino (int idPregunta) {
        ArrayList<Map<String, Object>> res = new ArrayList<>();
        String query = "SELECT id_pregunta, id_pregunta_destino, orden, regla, usucre, feccre, usumod, fecmod\n" +
                "FROM enc_flujo\n" +
                "WHERE id_pregunta = " + idPregunta+ "\n" +
                "AND estado LIKE 'ELABORADO'\n" +
                "ORDER BY orden, id_flujo";

        Cursor cursor = conn.rawQuery(query, null);
        while (cursor.moveToNext()) {
            Map<String, Object> map = new HashMap<>();
            map.put("id_pregunta", cursor.getInt(0));
            map.put("id_pregunta_destino", cursor.getInt(1));
            map.put("orden", cursor.getInt(2));
            map.put("regla", cursor.getString(3));
            map.put("usucre", cursor.getString(4));
            map.put("feccre", cursor.getLong(5));
            map.put("usumod", cursor.getString(6));
            map.put("fecmod", cursor.getLong(7));
            res.add(map);
        }
        cursor.close();
        return res;
    }

    public ArrayList<Map<String, Object>> flujoOrigen (int idPregunta) {
        ArrayList<Map<String, Object>> res = new ArrayList<>();
        String query = "SELECT id_pregunta, id_pregunta_destino, orden, regla, usucre, feccre, usumod, fecmod\n" +
                "FROM enc_flujo\n" +
                "WHERE id_pregunta_destino = " + idPregunta+ "\n" +
                "AND estado LIKE 'ELABORADO'\n" +
                "ORDER BY orden, id_flujo";

        Cursor cursor = conn.rawQuery(query, null);
        while (cursor.moveToNext()) {
            Map<String, Object> map = new HashMap<>();
            map.put("id_pregunta", cursor.getInt(0));
            map.put("id_pregunta_destino", cursor.getInt(1));
            map.put("orden", cursor.getInt(2));
            map.put("regla", cursor.getString(3));
            map.put("usucre", cursor.getString(4));
            map.put("feccre", cursor.getLong(5));
            map.put("usumod", cursor.getString(6));
            map.put("fecmod", cursor.getLong(7));
            res.add(map);
        }
        cursor.close();
        return res;
    }

    public Siguiente siguiente(IdInformante idInformante, int idPregunta) {
        Log.d("posicion", " "+ new Date());
        int idSiguiente = 0;
        int seccion = -1;
            if (abrir("id_pregunta = " + idPregunta, "orden, id_flujo")) {
                do {
                    if (evaluar(get_rpn(), idInformante)) {
                        idSiguiente = get_id_pregunta_destino();
                        break;
                    }
                } while (siguiente());
                Log.d("posicion", "9");
            }
            free();
            if (idSiguiente == 0) {
                idSiguiente = siguienteDirecta(idPregunta);
//                Log.d("posicion", "10");
            }

            String query = "SELECT p1.codigo_pregunta > p2.codigo_pregunta, 0, p2.id_seccion\n" +
                    "FROM enc_pregunta p1, enc_pregunta p2\n" +
                    "WHERE p1.id_pregunta = " + idPregunta + "\n" +
                    "AND p2.id_pregunta = " + idSiguiente;

            Cursor cursor = conn.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                do {
                    if (cursor.getInt(0) == 0) {
                        seccion = cursor.getInt(2);
                    }
                } while (cursor.moveToNext());
            }
            cursor.close();
        Log.d("posicion 11", idSiguiente+","+seccion+" "+new Date());
        return new Siguiente(idSiguiente, seccion);
    }

    public void updateRegla() {
        conn.beginTransaction();
        try {
            conn.execSQL("UPDATE enc_flujo\n" +
                    "SET regla = replace(regla, ':', '|'),\n" +
                    "rpn = replace(rpn, ':', '|')");
            conn.setTransactionSuccessful();
        } finally {
            conn.endTransaction();
        }
    }

    @SuppressWarnings("unused")
    public Integer get_id_flujo() {
        return filaActual.getInt(filaActual.getColumnIndex("id_flujo"));
    }
    @SuppressWarnings("unused")
    public void set_id_flujo(Integer value) {
        filaNueva.put("id_flujo", value);
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
    public Integer get_id_pregunta_destino() {
        return filaActual.getInt(filaActual.getColumnIndex("id_pregunta_destino"));
    }
    @SuppressWarnings("unused")
    public void set_id_pregunta_destino(Integer value) {
        filaNueva.put("id_pregunta_destino", value);
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
    public String get_rpn() {
        return filaActual.getString(filaActual.getColumnIndex("rpn"));
    }
    @SuppressWarnings("unused")
    public void set_rpn(String value) {
        filaNueva.put("rpn", value);
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
    public String get_apitransaccion() {
        return filaActual.getString(filaActual.getColumnIndex("apitransaccion"));
    }
    @SuppressWarnings("unused")
    public void set_apitransaccion(String value) {
        filaNueva.put("apitransaccion", value);
    }

}
