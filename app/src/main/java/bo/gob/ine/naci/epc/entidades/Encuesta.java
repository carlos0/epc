package bo.gob.ine.naci.epc.entidades;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import com.airbnb.lottie.L;
import com.google.android.gms.maps.model.LatLng;
import com.udojava.evalex.Expression;

import org.json.JSONArray;
import org.json.JSONException;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Matcher;

import bo.gob.ine.naci.epc.herramientas.Movil;
import bo.gob.ine.naci.epc.herramientas.Parametros;
import bo.gob.ine.naci.epc.models.EncuestaModel;

/**
 * Created by INE.
 */
//EN DB
public class Encuesta extends EntidadCorr {
    protected static final int[] nor = {0, 31, 59, 90, 120, 151, 181, 212, 243, 273, 304, 334};
    protected static final int[] bis = {0, 31, 60, 91, 121, 152, 182, 213, 244, 274, 305, 335};

    public Encuesta() {
        super("enc_encuesta");
    }

    @Override
    protected Identificador getId(Cursor filaActual) {
        return new IdEncuesta(filaActual.getInt(filaActual.getColumnIndex("id_asignacion")),
                filaActual.getInt(filaActual.getColumnIndex("correlativo")),
                filaActual.getInt(filaActual.getColumnIndex("id_pregunta")));
    }

    @Override
    protected Identificador getId(ContentValues filaNueva) {
        return new IdEncuesta(filaNueva.getAsInteger("id_asignacion"),
                filaNueva.getAsInteger("correlativo"),
                filaNueva.getAsInteger("id_pregunta"));
    }

    @Override
    public IdEncuesta guardar() {
        conn.beginTransaction();
        try {
            if (filaActual != null && (filaActual.getInt(filaActual.getColumnIndex("id_pregunta")) != Parametros.ID_INCIDENCIA_FINAL)) {
                conn.execSQL("UPDATE enc_informante\n" +
                        "SET estado = 'ELABORADO'\n" +
                        "WHERE estado IN ('CONCLUIDO','PRECONCLUIDO') \n" +
                        "AND id_asignacion = " + filaNueva.getAsString("id_asignacion") + "\n" +
                        "AND correlativo = " + filaNueva.getAsString("correlativo"));
                conn.setTransactionSuccessful();
            }

        } finally {
            conn.endTransaction();
        }

        conn.beginTransaction();
        try {
            conn.execSQL("UPDATE enc_informante\n" +
                    "SET estado = 'ELABORADO'\n" +
                    "WHERE estado IN ('CONCLUIDO','PRECONCLUIDO') \n" +
                    "AND EXISTS(SELECT 1 FROM enc_informante i\n" +
                    "    WHERE enc_informante.id_asignacion = i.id_asignacion_padre\n" +
                    "    AND enc_informante.correlativo = i.correlativo_padre\n" +
                    "    AND i.id_asignacion = " + filaNueva.getAsString("id_asignacion") + "\n" +
                    "    AND i.correlativo = " + filaNueva.getAsString("correlativo") + "\n"

                    + ")");
            conn.setTransactionSuccessful();
        } finally {
            conn.endTransaction();
        }
        ///QUITAR INCIDENCIA DE LA BOLETA DE HOGARES
        conn.beginTransaction();
        try {
            conn.execSQL("UPDATE enc_encuesta\n" +
                    "SET visible = 'f'\n" +
                    "WHERE id_asignacion*1000+correlativo \n" +
                    "IN (SELECT eip.id_asignacion*1000+eip.correlativo \n" +
                    "  FROM (SELECT id_asignacion, correlativo, id_asignacion_padre, correlativo_padre \n" +
                    "  FROM enc_informante WHERE id_asignacion=" + filaNueva.getAsString("id_asignacion") + "\n" +
                    "AND correlativo= " + filaNueva.getAsString("correlativo") + "\n" +
                    ")ei \n" +
                    "JOIN enc_informante eip ON   ei.id_asignacion_padre=eip.id_asignacion AND ei.correlativo_padre=eip.correlativo)\n" +
                    "AND id_pregunta=" + Parametros.ID_INCIDENCIA_FINAL);
            conn.setTransactionSuccessful();
        } finally {
            conn.endTransaction();
        }

        if (filaActual != null &&
                (filaActual.getString(filaActual.getColumnIndex("visible")).equals("t")
                        || filaActual.getString(filaActual.getColumnIndex("visible")).equals("true"))) {
            conn.beginTransaction();
            try {
                String query = "UPDATE enc_encuesta\n" +
                        "SET visible = 'f'\n" +
                        "WHERE id_asignacion = " + filaNueva.getAsString("id_asignacion") + "\n" +
                        "AND correlativo = " + filaNueva.getAsString("correlativo") + "\n" +
                        "AND id_pregunta in \n" +
                        "(SELECT id_pregunta FROM enc_pregunta WHERE \n" +
                        "codigo_pregunta > (select codigo_pregunta from enc_pregunta where id_pregunta = " + filaActual.getInt(filaActual.getColumnIndex("id_pregunta")) + " ) order by codigo_pregunta)";
                conn.execSQL(query);
                conn.setTransactionSuccessful();
            } finally {
                conn.endTransaction();
            }
        }
//        int last = -1;
//        String query = "SELECT coalesce(max(nullif(id_last, -1)), 0)\n" +
//                "FROM enc_encuesta\n" +
//                "WHERE id_asignacion = " + filaNueva.getAsString("id_asignacion") + "\n" +
//                "AND correlativo = " + filaNueva.getAsString("correlativo");
//
//        Cursor cursor = conn.rawQuery(query, null);
//        if (cursor.moveToFirst()) {
//            do {
//                last = cursor.getInt(0);
//            } while (cursor.moveToNext());
//        }
//        cursor.close();
//        filaNueva.put("id_last", last + 1);

        return (IdEncuesta) super.guardar();
    }

    public IdEncuesta guardar2() {
        conn.beginTransaction();
        try {
            conn.execSQL("UPDATE enc_informante\n" +
                    "SET estado = 'ELABORADO'\n" +
                    "WHERE estado = 'CONCLUIDO'\n" +
                    "AND id_asignacion = " + filaNueva.getAsString("id_asignacion") + "\n" +
                    "AND correlativo = " + filaNueva.getAsString("correlativo"));
            conn.setTransactionSuccessful();
        } finally {
            conn.endTransaction();
        }

        conn.beginTransaction();
        try {
            conn.execSQL("UPDATE enc_informante\n" +
                    "SET estado = 'ELABORADO'\n" +
                    "WHERE estado IN('CONCLUIDO','PRECONCLUIDO')\n" +
                    "AND EXISTS(SELECT 1 FROM enc_informante i\n" +
                    "    WHERE enc_informante.id_asignacion = i.id_asignacion_padre\n" +
                    "    AND enc_informante.correlativo = i.correlativo_padre\n" +
                    "    AND i.id_asignacion = " + filaNueva.getAsString("id_asignacion") + "\n" +
                    "    AND i.correlativo = " + filaNueva.getAsString("correlativo") + ")");
            conn.setTransactionSuccessful();
        } finally {
            conn.endTransaction();
        }
//        int idPregunta=filaActual.getInt(filaActual.getColumnIndex("id_pregunta"));
//
//        if (filaActual != null &&
//                (filaActual.getString(filaActual.getColumnIndex("visible")).equals("t")
//                        || filaActual.getString(filaActual.getColumnIndex("visible")).equals("true")) ) {
//            conn.beginTransaction();
//            try {
//                String query = "UPDATE enc_encuesta\n" +
//                        "SET visible = 'f'\n" +
//                        "WHERE id_asignacion = " + filaNueva.getAsString("id_asignacion") + "\n" +
//                        "AND correlativo = " + filaNueva.getAsString("correlativo") + "\n" +
//                        "AND id_pregunta in \n" +
//                        "(SELECT id_pregunta FROM enc_pregunta WHERE \n" +
//                        "codigo_pregunta > (select codigo_pregunta from enc_pregunta where id_pregunta = " + filaActual.getInt(filaActual.getColumnIndex("id_pregunta")) + " ) order by codigo_pregunta)";
//                conn.execSQL(query);
//                conn.setTransactionSuccessful();
//            } finally {
//                conn.endTransaction();
//            }
//        }
//        int last = -1;
//        String query = "SELECT coalesce(max(nullif(id_last, -1)), 0)\n" +
//                "FROM enc_encuesta\n" +
//                "WHERE id_asignacion = " + filaNueva.getAsString("id_asignacion") + "\n" +
//                "AND correlativo = " + filaNueva.getAsString("correlativo");
//
//        Cursor cursor = conn.rawQuery(query, null);
//        if (cursor.moveToFirst()) {
//            do {
//                last = cursor.getInt(0);
//            } while (cursor.moveToNext());
//        }
//        cursor.close();
//        filaNueva.put("id_last", last + 1);

        return (IdEncuesta) super.guardar();
    }

    public static void abrirBoleta(IdInformante idInformante) {

        conn.beginTransaction();
        try {
            conn.execSQL("UPDATE enc_informante\n" +
                    "SET estado = 'ELABORADO'\n" +
                    "WHERE id_asignacion = " + idInformante.id_asignacion + "\n" +
                    "AND correlativo = " + idInformante.correlativo);
            conn.setTransactionSuccessful();
        } finally {
            conn.endTransaction();
        }
        ///CONDICION DE QUITAR INCIDENCIA AL VOLVER OBSERVADA
        ///QUITAR INCIDENCIA DE LA BOLETA DE HOGARES
        conn.beginTransaction();
        try {
            conn.execSQL("UPDATE enc_encuesta\n" +
                    "SET visible = 'f'\n" +
                    "WHERE id_asignacion*1000+correlativo \n" +
                    "IN (SELECT eip.id_asignacion*1000+eip.correlativo \n" +
                    "  FROM (SELECT id_asignacion, correlativo, id_asignacion_padre, correlativo_padre \n" +
                    "  FROM enc_informante WHERE id_asignacion=" + idInformante.id_asignacion + "\n" +
                    "AND correlativo= " + idInformante.correlativo + "\n" +
                    ")ei \n" +
                    "JOIN enc_informante eip ON   ei.id_asignacion_padre=eip.id_asignacion AND ei.correlativo_padre=eip.correlativo)\n" +
                    "AND id_pregunta=" + Parametros.ID_INCIDENCIA_FINAL);
            conn.setTransactionSuccessful();
        } finally {
            conn.endTransaction();
        }

    }

    public IdEncuesta guardar(int last) {
        conn.beginTransaction();
        try {
            conn.execSQL("UPDATE enc_informante\n" +
                    "SET estado = 'ELABORADO'\n" +
                    "WHERE estado IN('CONCLUIDO', 'PRECONCLUIDO') \n" +
                    "AND id_asignacion = " + filaNueva.getAsString("id_asignacion") + "\n" +
                    "AND correlativo = " + filaNueva.getAsString("correlativo"));
            conn.setTransactionSuccessful();
        } finally {
            conn.endTransaction();
        }

        conn.beginTransaction();
        try {
            conn.execSQL("UPDATE enc_informante\n" +
                    "SET estado = 'ELABORADO'\n" +
                    "WHERE estado IN('CONCLUIDO', 'PRECONCLUIDO') \n" +
                    "AND EXISTS(SELECT * FROM enc_informante i\n" +
                    "    WHERE enc_informante.id_asignacion = i.id_asignacion_padre\n" +
                    "    AND enc_informante.correlativo = i.correlativo\n" +
                    "    AND i.id_asignacion = " + filaNueva.getAsString("id_asignacion") + "\n" +
                    "    AND i.correlativo = " + filaNueva.getAsString("correlativo") + ")");
            conn.setTransactionSuccessful();
        } finally {
            conn.endTransaction();
        }

        if (filaActual != null && filaActual.getInt(filaActual.getColumnIndex("id_last")) > 0) {
            conn.beginTransaction();
            try {
                conn.execSQL("UPDATE enc_encuesta\n" +
                        "SET id_last = -1\n" +
                        "WHERE id_asignacion = " + filaNueva.getAsString("id_asignacion") + "\n" +
                        "AND correlativo = " + filaNueva.getAsString("correlativo") + "\n" +
                        "AND id_last >= " + filaActual.getInt(filaActual.getColumnIndex("id_last")));
                conn.setTransactionSuccessful();
            } finally {
                conn.endTransaction();
            }
        }
        filaNueva.put("id_last", last);

        return (IdEncuesta) super.guardar();
    }

//    public static float evaluar(String rpn, IdInformante idInformante, int fila, String codPreg, String codResp, String resp) {
//        LinkedList<Float> pila = new LinkedList<>();
//        if (rpn == null || rpn.equals("")) {
//            return 1f;
//        }
//        float val1, val2;
//        String[] elements = rpn.split(" ");
//        String[] conjunto = new String[]{};
//        for (String element : elements) {
//            byte fl = (byte) element.charAt(0);
//            if (fl > 64 && fl < 91) {
//                switch (element) {
//                    case "FECHA": {
//                        Calendar cal = new GregorianCalendar();
//                        DateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
//                        String[] valor = df.format(cal.getTime()).split("-");
//                        int val = Integer.parseInt(valor[0]) * 365 + Integer.parseInt(valor[0]) / 4;
//                        if (Integer.parseInt(valor[0]) % 4 == 0) {
//                            val += bis[Integer.parseInt(valor[1])];
//                        } else {
//                            val += nor[Integer.parseInt(valor[1])];
//                        }
//                        val += Integer.parseInt(valor[2]);
//                        pila.add((float)val);
//                        break;
//                    }
//                    case "CODIGO": {
////                        pila.add(Informante.getCodigo(idInformante));
//                        break;
//                    }
//                    case "CONTAR": {
//                        pila.add(Informante.getContar(idInformante));
//                        break;
//                    }
//                    case "FILA": {
//                        pila.add((float)fila);
//                        break;
//                    }
//                    //case "TERMINADO": {
//                    case "INFORMANTE2_TERMINADO": {
//                        pila.add(Informante.estanTerminados(idInformante) == Informante.getContar(idInformante)? 1f: 0f);
//                        break;
//                    }
//                    default: {
//                        String val;
//                        if (element.equals(codPreg)) {
//                            conjunto = codResp.split(";");
//                            val = conjunto[0].split(",")[0];
//                        } else {
//                            if (element.endsWith(".VAL")) {
//                                if(codPreg != null) {
//                                    if (element.startsWith(codPreg)) {
//                                        val = resp.split(":")[0];
//                                    } else {
////                                        val = obtenerValor(idInformante, element.replace(".VAL", ""), fila).split(":")[0];
//                                    }
//                                } else {
////                                    val = obtenerValor(idInformante, element.replace(".VAL", ""), fila).split(":")[0];
//                                }
//                            } else {
//                                if (element.endsWith(".SUMAR")) {
//                                    val = sumarBucle(idInformante, element.substring(0, element.indexOf(".SUMAR"))).toString();
//                                } else {
//                                    if (element.endsWith(".CONTAR")) {
//                                        val = contarBucle(idInformante, element.substring(0, element.indexOf(".CONTAR"))).toString();
//                                    } else {
//                                        if (element.endsWith(".LONGITUD")) {
//                                            conjunto = obtenerCod(idInformante, element.substring(0, element.indexOf(".LONGITUD")), fila).split(";");
//                                            val = String.valueOf(conjunto.length);
//                                        } else {
//                                            if (element.endsWith(".INCIDENCIA")) {
//                                                val = String.valueOf(UpmHijo.getIncidenciaUpmHijo( conjunto[0].split(",")[0]));
//                                            } else {
//                                                if (element.endsWith(".ULTIMO_VALOR")) {
//                                                    val = ultimoValorBucle(idInformante, element.substring(0, element.indexOf(".ULTIMO_VALOR")));
//                                                } else {
//                                                    conjunto = Encuesta.obtenerCod(idInformante, element, fila).split(";");
//                                                    if (conjunto[0].equals("")) {
//                                                        conjunto = new String[]{"0,0"};
//                                                    }
//                                                    val = conjunto[0].split(",")[0];
//                                                }
//                                            }
//                                        }
//                                    }
//                                }
//                            }
//                        }
////                        if (val == null || val.equals("")) {
////                            pila.add(0f);
////                        } else {
////                            pila.add(Float.valueOf(val));
////                        }
//                        break;
//                    }
//                }
//            } else {
//                if (fl > 47 && fl < 58) {
//                    pila.add(Float.parseFloat(element));
//                } else {
//                    val2 = pila.pollLast();
//                    if (element.equals("!")) {
//                        pila.add((float) ((val2 == 0) ? 1 : 0));
//                    } else {
//                        val1 = pila.pollLast();
//                        switch (element) {
//                            case "+":
//                                pila.add(val1 + val2);
//                                break;
//                            case "-":
//                                pila.add(val1 - val2);
//                                break;
//                            case "*":
//                                pila.add(val1 * val2);
//                                break;
//                            case "/":
//                                pila.add(val1 / val2);
//                                break;
//                            case "\\":
//                                pila.add((float)(int)(val1 / val2));
//                                break;
//                            case "%":
//                                pila.add(val1 % val2);
//                                break;
//                            case "^":
//                                pila.add((float) Math.pow(val1, val2));
//                                break;
//                            case "=":
//                            case "==":
//                                pila.add((float) ((val1 == val2) ? 1 : 0));
//                                break;
//                            case "!=":
//                                pila.add((float) ((val1 != val2) ? 1 : 0));
//                                break;
//                            case "<=":
//                                pila.add((float) ((val1 <= val2) ? 1 : 0));
//                                break;
//                            case ">=":
//                                pila.add((float) ((val1 >= val2) ? 1 : 0));
//                                break;
//                            case "<":
//                                pila.add((float) ((val1 < val2) ? 1 : 0));
//                                break;
//                            case ">":
//                                pila.add((float) ((val1 > val2) ? 1 : 0));
//                                break;
//                            case "~":
//                                float in = 0;
//                                for (String c : conjunto) {
//                                    if (val1 == Float.valueOf(c.split(",")[1])) {
//                                        in = 1;
//                                        break;
//                                    }
//                                }
//                                pila.add(in);
//                                break;
//                            case "&&":
//                                pila.add((float) ((val1 > 0 && val2 > 0) ? 1 : 0));
//                                break;
//                            case "||":
//                                pila.add((float) ((val1 + val2 > 0) ? 1 : 0));
//                                break;
//                        }
//                    }
//                }
//            }
//        }
//        return pila.getLast();
//    }

    public static ArrayList<String> dibujaPila(String rpn, IdInformante idInformante, int fila, String codPreg, String codResp, String resp) {
        ArrayList<String> resultado = new ArrayList<>();
        //LinkedList<Float> pila = new LinkedList<>();
        try {
            if (rpn == null || rpn.equals("")) {
                return null;
            }
            String[] elements = rpn.split(" ");
            String[] conjunto = new String[]{};
            for (String element : elements) {
                byte fl = (byte) element.charAt(0);
                if (fl > 64 && fl < 91) {
                    switch (element) {
                        case "FECHA": {
                            Calendar cal = new GregorianCalendar();
                            DateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                            String[] valor = df.format(cal.getTime()).split("-");
                            int val = Integer.parseInt(valor[0]) * 365 + Integer.parseInt(valor[0]) / 4;
                            if (Integer.parseInt(valor[0]) % 4 == 0) {
                                val += bis[Integer.parseInt(valor[1])];
                            } else {
                                val += nor[Integer.parseInt(valor[1])];
                            }
                            val += Integer.parseInt(valor[2]);
                            //pila.add((float)val);
                            resultado.add(element + "¡" + String.valueOf(val));
                            break;
                        }
                        case "CODIGO": {
                            //pila.add(Informante.getCodigo(idInformante));
                            resultado.add(element + "¡" + String.valueOf(Informante.getCodigo(idInformante)));
                            break;
                        }
                        case "CONTAR": {
                            //pila.add(Informante.getContar(idInformante));
                            resultado.add(element + "¡" + String.valueOf(Informante.getContar(idInformante)));
                            break;
                        }
                        case "FILA": {
                            //pila.add((float)fila);
                            resultado.add(element + "¡" + String.valueOf(fila));
                            break;
                        }
                        default: {
                            String val;
                            if (element.equals(codPreg)) {
                                conjunto = codResp.split(";");
                                val = conjunto[0].split(",")[0];
                            } else {
                                if (element.endsWith(".VAL")) {
                                    if (codPreg != null) {
                                        if (element.startsWith(codPreg)) {
                                            val = resp.split(":")[0];
                                        } else {
//                                            val = obtenerValor(idInformante, element.replace(".VAL", ""), fila).split(":")[0];
                                        }
                                    } else {
//                                        val = obtenerValor(idInformante, element.replace(".VAL", ""), fila).split(":")[0];
                                    }
                                } else {
                                    if (element.endsWith(".SUMAR")) {
                                        val = sumarBucle(idInformante, element.substring(0, element.indexOf(".SUMAR"))).toString();
                                    } else {
                                        if (element.endsWith(".CONTAR")) {
                                            val = contarBucle(idInformante, element.substring(0, element.indexOf(".CONTAR"))).toString();
                                        } else {
                                            if (element.endsWith(".LONGITUD")) {
                                                conjunto = obtenerCod(idInformante, element.substring(0, element.indexOf(".LONGITUD")), fila).split(";");
                                                val = String.valueOf(conjunto.length);
                                            } else {
                                                if (element.endsWith(".INCIDENCIA")) {
                                                    val = String.valueOf(UpmHijo.getIncidenciaUpmHijo(conjunto[0].split(",")[0]));
                                                } else {
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
//                            if (val == null || val.equals("")) {
//                                //pila.add(0f);
//                                resultado.add(element+"¡0");
//                            } else {
//                                //pila.add(Float.valueOf(val));
//                                resultado.add(element+"¡"+String.valueOf(val));
//                            }
                            break;
                        }
                    }
                } else {
                    resultado.add(element + "¡" + String.valueOf(element));
                }
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return resultado;
    }

    public static Map<Long, String> getRespuestas(IdInformante idInformante, String variable, String rpn) {
        Map<Long, String> res = new LinkedHashMap<>();
        String query;
        if (rpn == null || rpn.equals("")) {
            query = "SELECT e.id_asignacion, e.correlativo, e.fila, group_concat(e.respuesta, ' - ') respuesta\n" +
                    "FROM enc_pregunta p, enc_encuesta e\n" +
                    "WHERE p.id_pregunta = e.id_pregunta\n" +
                    "AND e.id_asignacion = " + idInformante.id_asignacion + "\n" +
                    "AND e.correlativo = " + idInformante.correlativo + "\n" +
                    "AND p.codigo_pregunta IN('" + variable.replaceAll(";", "','") + "')\n" +
                    "AND e.id_last > 0\n" +
                    "GROUP BY e.id_asignacion, e.correlativo, e.fila\n" +
                    "ORDER BY e.fila";
        } else {
            String[] condicion = rpn.split(" ");
            query = "SELECT e.id_asignacion, e.correlativo, e.fila, group_concat(e.respuesta, ' - ') respuesta\n" +
                    "FROM enc_pregunta p, enc_encuesta e, enc_pregunta p2, enc_encuesta e2\n" +
                    "WHERE p.id_pregunta = e.id_pregunta\n" +
                    "AND e.id_asignacion = " + idInformante.id_asignacion + "\n" +
                    "AND e.correlativo = " + idInformante.correlativo + "\n" +
                    "AND p.codigo_pregunta IN('" + variable.replaceAll(";", "','") + "')\n" +
                    "AND e.id_last > 0\n" +
                    "AND p2.id_pregunta = e2.id_pregunta\n" +
                    "AND e2.id_asignacion = e.id_asignacion\n" +
                    "AND e2.correlativo = e.correlativo\n" +
                    "AND e2.fila = e.fila\n" +
                    "AND p2.codigo_pregunta = '" + condicion[0] + "'\n" +
                    "AND e2.id_last > 0\n" +
                    "AND CAST(e2.codigo_respuesta AS Float) " + condicion[2] + " " + condicion[1] + "\n" +
                    "GROUP BY e.id_asignacion, e.correlativo, e.fila\n" +
                    "ORDER BY e.fila";
        }

        Cursor cursor = conn.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                Long id = (long) cursor.getInt(0) * 10000000 + cursor.getInt(1) * 1000 + cursor.getInt(2);
                String cod = cursor.getString(2);
                String resp = cursor.getString(3);
                res.put(id, cod + "|" + resp);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return res;
    }

    public static String[] getRespuesta(IdInformante idInformante, String variable) {
        String[] res;
        String query = "SELECT e.id_asignacion, e.correlativo, e.id_pregunta, group_concat(e.codigo_respuesta, ',') codigo, group_concat(e.respuesta, ' - ') respuesta\n" +
                "FROM enc_pregunta p, enc_encuesta e\n" +
                "WHERE p.id_pregunta = e.id_pregunta\n" +
                "AND e.id_asignacion = " + idInformante.id_asignacion + "\n" +
                "AND e.correlativo = " + idInformante.correlativo + "\n" +
                "AND p.codigo_pregunta IN('" + variable.replaceAll(";", "','") + "')\n" +
                "AND e.visible in ('t','true')\n" +
                "GROUP BY e.id_asignacion, e.correlativo, e.id_pregunta\n" +
                "ORDER BY p.codigo_pregunta";
        Log.d("Seleccion", query);
        Cursor cursor = conn.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            res = cursor.getString(3).split(",", -1);
        } else {
            res = new String[72];
        }
        cursor.close();
        return res;
    }

    public static String[] getRespuesta(int idUpm, String variable) {
        ArrayList<String> res = new ArrayList<>();
        String query = "SELECT DISTINCT e.respuesta\n" +
                "FROM ope_asignacion a, enc_informante i, enc_pregunta p, enc_encuesta e\n" +
                "WHERE a.id_asignacion = i.id_asignacion\n" +
                "AND i.id_asignacion = e.id_asignacion\n" +
                "AND i.correlativo = e.correlativo\n" +
                "AND p.id_pregunta = e.id_pregunta\n" +
                "AND a.id_upm = " + idUpm + "\n" +
                "AND p.codigo_pregunta IN('" + variable.replaceAll(";", "','") + "')\n" +
                "AND e.visible in ('t','true')\n" +
                "AND i.estado <> 'ANULADO'\n" +
                "ORDER BY e.respuesta";

        Cursor cursor = conn.rawQuery(query, null);
        while (cursor.moveToNext()) {
            res.add(cursor.getString(0));
        }
        cursor.close();
        return res.toArray(new String[res.size()]);
    }

    public boolean unique(String codPreg, String codResp) {
        boolean res;
        String query = "SELECT e.codigo_respuesta\n" +
                "FROM enc_pregunta p, enc_encuesta e\n" +
                "WHERE p.id_pregunta = e.id_pregunta\n" +
                "AND p.codigo_pregunta = '" + codPreg + "'\n" +
                "AND e.id_asignacion = " + filaNueva.getAsInteger("id_asignacion") + "\n" +
                "AND e.correlativo = " + filaNueva.getAsInteger("correlativo") + "\n" +
                "AND e.fila <> " + filaNueva.getAsString("fila") + "\n" +
                "AND CAST(e.codigo_respuesta AS Float) = " + codResp + "\n" +
                "AND e.id_last > 0";

        Cursor cursor = conn.rawQuery(query, null);
        res = cursor.getCount() == 0;
        cursor.close();
        return res;
    }

    public String isValid(String codPreg, String codResp, String resp, boolean unique) {
        Estado e = Estado.valueOf(filaNueva.getAsString("estado"));
        if (e == Estado.NOAPLICA || e == Estado.SENIEGA || e == Estado.NOSABE) {
            return "Ok";
        }
        if (unique) {
            if (!unique(codPreg, codResp)) {
                return "Error:El valor ya existe.";
            }
        }

//        Pregunta pregunta = new Pregunta();
//        if (pregunta.abrir(filaNueva.getAsInteger("id_pregunta"))) {
//            if (evaluar(pregunta.get_rpn(), new IdInformante(filaNueva.getAsInteger("id_asignacion"), filaNueva.getAsInteger("correlativo")), filaNueva.getAsInteger("fila"), codPreg, codResp, resp) > 0) {
//                String cod = filaNueva.getAsString("codigo_respuesta");
//                if (cod.equals("-1") || cod.equals("-1.0")) {
//                    return "Error:Debe introducir/seleccionar un valor.";
//                }
//                if (filaNueva.getAsString("codigo_respuesta").equals("-2")) {
//                    return "Error:El valor esta fuera de rango.";
//                }
//                switch (pregunta.get_id_tipo_pregunta()) {
//                    case Multiple: {
//                        int n = filaNueva.getAsString("codigo_respuesta").split(";").length;
//                        if (n >= pregunta.get_minimo()) {
//                            if (pregunta.get_maximo() == 0 || n <= pregunta.get_maximo()) {
//                                return "Ok";
//                            } else {
//                                return "Error:Debe seleccionar como máximo " + pregunta.get_maximo();
//                            }
//                        } else {
//                            return "Error:Debe seleccionar al menos " + pregunta.get_minimo();
//                        }
//                    }
//                    case FechaMes:
//                    case Numero:
//                    case Decimal:
//                    case Fecha:
//                    case MesAnio:
//                        try {
//                            Double val = Double.parseDouble(filaNueva.getAsString("codigo_respuesta"));
//                            if (pregunta.get_maximo() == 0) {
//                                return "Ok";
//                            } else {
//                                if (val >= pregunta.get_minimo()) {
//                                    if (val <= pregunta.get_maximo()) {
//                                        return "Ok";
//                                    } else {
//                                        return "Error:Limite maximo: " +pregunta.get_maximo();
//                                    }
//                                } else {
//                                    return "Error:Limite minimo: " +pregunta.get_minimo();
//                                }
//                            }
//                        } catch (NumberFormatException ex) {
//                            return "Error:" + ex.getMessage();
//                        }
//                    case HoraMinuto:
//                        try {
//                            Double val = Double.parseDouble(filaNueva.getAsString("codigo_respuesta"));
//                            if (pregunta.get_maximo() == 0) {
//                                return "Ok";
//                            } else {
//                                if (val >= pregunta.get_minimo()) {
//                                    if (val <= pregunta.get_maximo()) {
//                                        return "Ok";
//                                    } else {
//                                        return "Error:Limite maximo: " +pregunta.get_maximo()+" minutos ("+pregunta.get_maximo()/60 + "hrs:"+pregunta.get_maximo()%60 + "min)";
//                                    }
//                                } else {
//                                    return "Error:Limite minimo: " +pregunta.get_minimo()+" minutos ("+pregunta.get_minimo()/60+"hrs:"+pregunta.get_minimo()%60+"min)";
//                                }
//                            }
//                        } catch (NumberFormatException ex) {
//                            return "Error:" + ex.getMessage();
//                        }
//                    case Abierta:
//                        try {
//                            int valLenght = filaNueva.getAsString("respuesta").length();
//                            if (pregunta.get_maximo() == 0) {
//                                return "Ok";
//                            } else {
//                                if (valLenght >= pregunta.get_minimo()) {
//                                    if (valLenght <= pregunta.get_maximo()) {
//                                        return "Ok";
//                                    } else {
//                                        return "Error:Longitud de texto maxima: " +pregunta.get_maximo();
//                                    }
//                                } else {
//                                    return "Error:Longitud de texto minima: " +pregunta.get_minimo();
//                                }
//                            }
//                        } catch (NumberFormatException ex) {
//                            return "Error:" + ex.getMessage();
//                        }
//                    default:
//                        return "Ok";
//                }
//            } else {
//                return pregunta.get_mensaje();
//            }
//        } else {
        return "Error:Pregunta no encontrada.";
//        }
    }

    public static Float contarBucle(IdInformante idInformante, String codPregunta) {
        Float res = 0f;
        String query = "SELECT count(*)\n" +
                "FROM enc_pregunta p, enc_encuesta e\n" +
                "WHERE p.id_pregunta = e.id_pregunta\n" +
                "AND e.id_last > 0\n" +
                "AND id_asignacion = " + idInformante.id_asignacion + "\n" +
                "AND correlativo = " + idInformante.correlativo + "\n" +
                "AND p.codigo_pregunta = '" + codPregunta + "'";
        Cursor cursor = conn.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            res = cursor.getFloat(0);
        }
        cursor.close();
        return res;
    }

    public static Float sumarBucle(IdInformante idInformante, String codPregunta) {
        Float res = 0f;
        String query = "SELECT sum(respuesta)\n" +
                "FROM enc_pregunta p, enc_encuesta e\n" +
                "WHERE p.id_pregunta = e.id_pregunta\n" +
                "AND e.id_last > 0\n" +
                "AND id_asignacion = " + idInformante.id_asignacion + "\n" +
                "AND correlativo = " + idInformante.correlativo + "\n" +
                "AND p.codigo_pregunta = '" + codPregunta + "'";
        Cursor cursor = conn.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            res = cursor.getFloat(0);
        }
        cursor.close();
        return res;
    }

    public static String obtenerCod(IdInformante idInformante, String codPregunta, int fila) {
        String res = "";
        String query = "SELECT e.codigo_respuesta\n" +
                "FROM enc_pregunta p, enc_encuesta e, enc_informante i\n" +
                "WHERE p.id_pregunta = e.id_pregunta\n" +
                "AND ((e.id_asignacion = " + idInformante.id_asignacion + "\n" +
                "        AND e.correlativo = " + idInformante.correlativo + ")\n" +
                "    OR (e.id_asignacion = i.id_asignacion_padre" +
                "        AND e.correlativo = i.correlativo_padre))\n" +
                "AND i.id_asignacion = " + idInformante.id_asignacion + "\n" +
                "AND i.correlativo = " + idInformante.correlativo + "\n" +
                "AND p.codigo_pregunta = '" + codPregunta + "'\n" +
                "AND e.id_last > 0\n" +
                "AND e.fila = " + fila;

        Cursor cursor = conn.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                res = cursor.getString(0).trim();
            } while (cursor.moveToNext());
        } else {
            cursor.close();
            query = "SELECT e.codigo_respuesta\n" +
                    "FROM enc_pregunta p, enc_encuesta e\n" +
                    "WHERE p.id_pregunta = e.id_pregunta\n" +
                    "AND e.id_asignacion = " + idInformante.id_asignacion + "\n" +
                    "AND e.correlativo = " + idInformante.correlativo + "\n" +
                    "AND p.codigo_pregunta = '" + codPregunta + "'\n" +
                    "AND e.id_last > 0\n" +
                    "ORDER BY e.fila DESC\n" +
                    "LIMIT 1";
            cursor = conn.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                do {
                    res = cursor.getString(0).trim();
                } while (cursor.moveToNext());
            }
        }
        cursor.close();
        return res;
    }

    public static String obtenerValorTabla(IdInformante idInformante, String idPregunta, String idSubPregunta) {
        String res = null;
        String query = "SELECT e.respuesta\n" +
                "FROM enc_pregunta p, enc_encuesta e\n" +
                "WHERE p.id_pregunta = e.id_pregunta\n" +
                "AND e.id_asignacion = " + idInformante.id_asignacion + "\n" +
                "AND e.correlativo = " + idInformante.correlativo + "\n" +
                "AND p.id_pregunta IN(" + idPregunta + ")\n" +
                "AND e.visible in ('t','true')";

        Cursor cursor = conn.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                res = cursor.getString(0).trim();
            } while (cursor.moveToNext());
        }
        cursor.close();


        return res;
    }

    public static String obtenerValor(IdInformante idInformante, String codPregunta) {
        String res = null;
//        String query = "SELECT e.respuesta\n" +
//                "FROM enc_pregunta p, enc_encuesta e\n" +
//                "WHERE p.id_pregunta = e.id_pregunta\n" +
//                "AND e.id_asignacion = " + idInformante.id_asignacion + "\n" +
//                "AND e.correlativo = " + idInformante.correlativo + "\n" +
//                "AND p.codigo_pregunta IN('" + codPregunta.replace(";", "','") + "')\n" +
//                "AND e.visible in ('t','true')";
//        Log.d("query1", query);
        String query = "SELECT e.respuesta FROM \n" +
                "(SELECT id_pregunta \n" +
                "FROM enc_pregunta  WHERE \n" +
                "     codigo_pregunta IN('" + codPregunta.replace(";", "','") + "')) p JOIN\n" +
                "(SELECT id_pregunta, respuesta FROM enc_encuesta \n" +
                " WHERE id_asignacion=" + idInformante.id_asignacion +
                " AND correlativo=" + idInformante.correlativo
                + " AND (visible='t' or visible='true')\n" +
                " ) e ON p.id_pregunta=e.id_pregunta \n" +
                " ";

//        Log.d("query1", query);
        Cursor cursor = conn.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                res = cursor.getString(0).trim();
//                Log.d("query2", res);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return res;
    }

    public static String obtenerValorEncuesta(IdInformante idInformante, String codPregunta) {
        String res = null;
//        String query = "SELECT e.respuesta\n" +
//                "FROM enc_pregunta p, enc_encuesta e\n" +
//                "WHERE p.id_pregunta = e.id_pregunta\n" +
//                "AND e.id_asignacion = " + idInformante.id_asignacion + "\n" +
//                "AND e.correlativo = " + idInformante.correlativo + "\n" +
//                "AND p.codigo_pregunta IN('" + codPregunta.replace(";", "','") + "')\n" +
//                "AND e.visible in ('t','true')";
//        Log.d("query1", query);
        String query = "SELECT e.respuesta FROM \n" +
                "(SELECT id_pregunta \n" +
                "FROM enc_pregunta  WHERE \n" +
                "     codigo_pregunta IN('" + codPregunta.replace(";", "','") + "')) p JOIN\n" +
                "(SELECT id_pregunta, respuesta FROM enc_encuesta \n" +
                " WHERE id_asignacion=" + idInformante.id_asignacion +
                " AND correlativo=" + idInformante.correlativo +
//                +" AND (visible='t' or visible='true')\n" +
                " ) e ON p.id_pregunta=e.id_pregunta \n" +
                " ";

//        Log.d("query1", query);
        Cursor cursor = conn.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                res = cursor.getString(0).trim();
//                Log.d("query2", res);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return res;
    }

    ///obtener valores con idPregunta
    public static String obtenerValorIdPregunta(IdInformante idInformante, String idPregunta) {
        String res = null;
        String query = "SELECT respuesta FROM enc_encuesta\n" +
                "WHERE id_asignacion=" + idInformante.id_asignacion + " AND correlativo=" + idInformante.correlativo + " AND id_pregunta= " + idPregunta + " \n" +
                " AND visible like 't%' ";

        Log.d("query1", query);
        try {
            Cursor cursor = conn.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                do {
                    res = cursor.getString(0).trim();
//                Log.d("query2", res);
                } while (cursor.moveToNext());
            }
            cursor.close();
        } catch (Exception e) {

        }

        return res;
    }

    public static String obtenerValorId(IdInformante idInformante, String codPregunta) {
        String res = "null";
        String query = "SELECT e.codigo_respuesta\n" +
                "FROM enc_encuesta e\n" +
                "WHERE e.id_asignacion = " + idInformante.id_asignacion + "\n" +
                "AND e.correlativo = " + idInformante.correlativo + "\n" +
                "AND e.id_pregunta=" + codPregunta + "\n" +
                "AND e.visible in ('t','true')";

        Cursor cursor = conn.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                res = cursor.getString(0).trim();
            } while (cursor.moveToNext());
        }
        cursor.close();
        return res;
    }

//    public static boolean fueRespondida(IdInformante idInformante, String codPregunta) {
//        String query = "SELECT e.respuesta\n" +
//                "FROM enc_pregunta p, enc_encuesta e\n" +
//                "WHERE p.id_pregunta = e.id_pregunta\n" +
//                "AND e.id_asignacion = " + idInformante.id_asignacion + "\n" +
//                "AND e.correlativo = " + idInformante.correlativo + "\n" +
//                "AND e.visible LIKE 't%'" + "\n" +
//                "AND p.codigo_pregunta IN('" + codPregunta.replace(";", "','") + "')" + "\n" +
//                "AND e.id_asignacion*1000+e.correlativo IN (SELECT id_asignacion*1000+correlativo" + "\n" +
//                " FROM enc_informante WHERE " +
//                "id_asignacion = " + idInformante.id_asignacion + "\n" +
//                "AND correlativo = " + idInformante.correlativo + "\n" +
//                "AND estado = 'PRECONCLUIDO'\n" +
//                ")\n";
//
//        Cursor cursor = conn.rawQuery(query, null);
//        boolean res = cursor.moveToFirst();
//        cursor.close();
//        return res;
//    }
public static boolean fueRespondida(IdInformante idInformante) {
    String query = "SELECT id_asignacion*1000+correlativo" + "\n" +
            " FROM enc_informante WHERE " +
            "id_asignacion = " + idInformante.id_asignacion + "\n" +
            "AND correlativo = " + idInformante.correlativo + "\n" +
            "AND estado = 'PRECONCLUIDO'\n" +
            "\n";

    Cursor cursor = conn.rawQuery(query, null);
    boolean res = cursor.moveToFirst();
    cursor.close();
    return res;
}

    public static String obtenerSeccionRespuesta(IdInformante idInformante, String codPregunta) {
        String res = "";
        String query = "SELECT s.id_seccion\n" +
                "FROM enc_pregunta p, enc_encuesta e, enc_seccion s\n" +
                "WHERE p.id_pregunta = e.id_pregunta\n" +
                "AND s.id_seccion = p.id_seccion\n" +
                "AND e.id_asignacion = " + idInformante.id_asignacion + "\n" +
                "AND e.correlativo = " + idInformante.correlativo + "\n" +
                "AND e.id_last > 0\n" +
                "AND p.codigo_pregunta IN('" + codPregunta.replace(";", "','") + "')";

        Cursor cursor = conn.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                res = cursor.getString(0).trim();
            } while (cursor.moveToNext());
        }
        cursor.close();
        return res;
    }

//    public static String obtenerValor(IdInformante idInformante, String codPregunta) {
//        String res = "";
//        String query = "SELECT e.respuesta\n" +
//                "FROM enc_pregunta p, enc_encuesta e\n" +
//                "WHERE p.id_pregunta = e.id_pregunta\n" +
//                "AND e.id_asignacion = " + idInformante.id_asignacion + "\n" +
//                "AND e.correlativo = " + idInformante.correlativo + "\n" +
//                "AND e.id_last > 0\n" +
//                "AND p.codigo_pregunta IN('" + codPregunta.replace(";", "','") + "')";
//
//        Cursor cursor = conn.rawQuery(query, null);
//        if (cursor.moveToFirst()) {
//            do {
//                res = cursor.getString(0).trim();
//            } while (cursor.moveToNext());
//        }
//        cursor.close();
//        return res;
//    }

    //DEVUELVE EL ID DE LA ULTIMA PREGUNTA RESPONDIDA
    public static int ultima2(IdInformante idInformante) {
        int res = -1;
        String query = "SELECT id_pregunta\n" +
                "FROM enc_encuesta\n" +
                "WHERE visible\n" +
                "AND id_asignacion = " + idInformante.id_asignacion + "\n" +
                "AND correlativo = " + idInformante.correlativo + "\n" +
                "ORDER BY id_last DESC\n" +
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

    public static int ultima(IdInformante idInformante) {
        int res = -1;
        String query = "SELECT ee.id_pregunta\n" +
                "FROM enc_encuesta ee, enc_pregunta ep\n" +
                "WHERE ee.visible LIKE 't'\n" +
                "AND ep.id_pregunta = ee.id_pregunta\n" +
                "AND ee.id_asignacion = " + idInformante.id_asignacion + "\n" +
                "AND ee.correlativo = " + idInformante.correlativo + "\n" +
                "ORDER BY ep.codigo_pregunta DESC\n" +
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

    public static int ultimaInicial(IdInformante idInformante) {
        int res = -1;
        String query = "SELECT ee.id_pregunta\n" +
                "FROM enc_encuesta ee, enc_pregunta ep\n" +
                "WHERE ee.visible LIKE 't%'\n" +
                "AND ep.inicial = 1\n" +
                "AND ep.id_pregunta = ee.id_pregunta\n" +
                "AND ee.id_asignacion = " + idInformante.id_asignacion + "\n" +
                "AND ee.correlativo = " + idInformante.correlativo + "\n" +
                "ORDER BY ep.codigo_pregunta DESC\n" +
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

    //DEVUELVE EL ID DE LA ULTIMA PREGUNTA RESPONDIDA
    public static String ultimoValorBucle(IdInformante idInformante, String codigoPregunta) {
        String res = "";
        String query = "SELECT e.codigo_respuesta\n" +
                "FROM enc_pregunta p, enc_encuesta e\n" +
                "WHERE e.id_last > 0\n" +
                "AND p.id_pregunta = e.id_pregunta\n" +
                "AND e.id_asignacion = " + idInformante.id_asignacion + "\n" +
                "AND e.correlativo = " + idInformante.correlativo + "\n" +
                "AND p.codigo_pregunta LIKE '" + codigoPregunta + "'\n" +
                "ORDER BY e.fila DESC\n" +
                "LIMIT 1";

        Cursor cursor = conn.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                res = cursor.getString(0);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return res;
    }


    @SuppressWarnings("unused")
    public IdEncuesta get_id_encuesta() {
        return new IdEncuesta(filaActual.getInt(filaActual.getColumnIndex("id_asignacion")),
                filaActual.getInt(filaActual.getColumnIndex("correlativo")),
                filaActual.getInt(filaActual.getColumnIndex("id_pregunta"))
        );
    }

    @SuppressWarnings("unused")
    public void set_id_encuesta(IdEncuesta value) {
        filaNueva.put("id_asignacion", value.id_asignacion);
        filaNueva.put("correlativo", value.correlativo);
        filaNueva.put("id_pregunta", value.id_pregunta);
    }

//    @SuppressWarnings("unused")
//    public Long get_id_respuesta() {
//        return filaActual.getLong(filaActual.getColumnIndex("id_respuesta"));
//    }
//    @SuppressWarnings("unused")
//    public void set_id_respuesta(Long value) {
//        filaNueva.put("id_respuesta", value);
//    }

    @SuppressWarnings("unused")
    public String get_codigo_respuesta() {
        return filaActual.getString(filaActual.getColumnIndex("codigo_respuesta"));
    }

    @SuppressWarnings("unused")
    public void set_codigo_respuesta(String value) {
        filaNueva.put("codigo_respuesta", value);
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
    public String get_observacion() {
        return filaActual.getString(filaActual.getColumnIndex("observacion"));
    }

    @SuppressWarnings("unused")
    public void set_observacion(String value) {
        filaNueva.put("observacion", value);
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
    public Integer get_id_last() {
        return filaActual.getInt(filaActual.getColumnIndex("id_last"));
    }

    @SuppressWarnings("unused")
    public void set_id_last(Integer value) {
        filaNueva.put("id_last", value);
    }

    @SuppressWarnings("unused")
    public String get_visible() {
        return filaActual.getString(filaActual.getColumnIndex("visible"));
    }

    @SuppressWarnings("unused")
    public void set_visible(String value) {
        filaNueva.put("visible", value);
    }

    @SuppressWarnings("unused")
    public Estado get_estado() {
        return Estado.valueOf(filaActual.getString(filaActual.getColumnIndex("estado")));
    }

    @SuppressWarnings("unused")
    public void set_estado(Estado value) {
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

    public static ArrayList<String> getFotos() {
        ArrayList<String> res = new ArrayList<>();
        String query = "SELECT e.respuesta\n" +
                "FROM enc_encuesta e, enc_informante i\n" +
                "WHERE e.id_last > 1\n" +
                "AND e.id_pregunta IN (36079,36018,36080,35993,36011,36051)\n" +
                "AND e.id_asignacion = i.id_asignacion\n" +
                "AND e.correlativo = i.correlativo\n" +
                "AND i.estado LIKE 'CONCLUIDO'\n" +
                "AND i.id_upm = 2846";

        Cursor cursor = conn.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                res.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return res;
    }


    public static Map<String, String> getPunto(int idAsignacion, int correlativo) {
        Map<String, String> res = new HashMap<>();
        String query = "SELECT codigo, latitud, longitud\n" +
                "FROM enc_informante\n" +
                "WHERE id_asignacion = "+ idAsignacion +"\n" +
                "AND correlativo = " + correlativo;

        Cursor cursor = conn.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                res.put("codigo", cursor.getString(0));
                res.put("latitud", cursor.getString(1));
                res.put("longitud", cursor.getString(2));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return res;
    }

    /**
     * Listado de array de la Entidad
     *
     * @param filter
     * @return
     */
    public JSONArray entityToJsonArray(String filter) {
        List<Map<String, Object>> data = obtenerListado(filter);
        JSONArray resp = new JSONArray(data);
        return resp;
    }

    public static String recuperaRespuestas(IdInformante idInformante) {

        StringBuilder str = new StringBuilder();
        String color1 = "#001e41";
        String color2 = "#50ac27";
        String color3 = "#00446c";
        String seccion = "";
        Cursor cursor = null;
        try {
            String query = "select ee.id_pregunta,EP.codigo_pregunta, EE.RESPUESTA, ep.pregunta, es.seccion\n" +
                    "FROM (select * from enc_encuesta where id_asignacion=" + idInformante.id_asignacion + " and correlativo=" + idInformante.correlativo + ") EE\n" +
                    "JOIN ENC_PREGUNTA EP ON (EE.ID_PREGUNTA=EP.ID_PREGUNTA ) JOIN ENC_SECCION ES ON EP.ID_SECCION=ES.ID_SECCION\n" +
                    "AND EE.VISIBLE like 't%'\n" +
                    "order by ep.codigo_pregunta";
            cursor = conn.rawQuery(query, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (cursor.moveToFirst()) {
            do {
                String seccionActual = cursor.getString(4) == null ? "" : cursor.getString(4);
                if (!seccion.equals(seccionActual)) {
                    str.append("<font color=" + color1 + ">");
                    str.append("<h3><b>").append("SECCION :" + seccionActual).append("</b> </h3></font> <br>");
                    seccion = seccionActual;
                }
                int idPreg = cursor.getString(0) == null ? 0 : Integer.parseInt(cursor.getString(0));
                if (idPreg == Parametros.ID_INCIDENCIA_FINAL) {
                    str.append("<font color=" + color1 + ">");
                    str.append("<b>").append("" + cursor.getString(1) == null ? "" : cursor.getString(1) + "").append("</b> -</font> ");
                    str.append("<font color=" + color3 + ">");
                    str.append("<b>").append("Pregunta de incidencia").append("</b> </font><br>");
                    str.append("<font color=" + color2 + ">");
                    str.append(": ").append("TIENE RESPUESTA INCIDENCIA FINAL").append("</font><hr><br>");
                } else if (idPreg == 36201
                ) {
                    str.append("<font color=" + color1 + ">");
                    str.append("<b>").append("" + cursor.getString(1) == null ? "" : cursor.getString(1) + "").append("</b> -</font> ");
                    str.append("<font color=" + color3 + ">");
                    str.append("<b>").append("Pregunta Tipo tabla").append("</b> </font><br>");
                    str.append("<font color=" + color2 + ">");
                    str.append(": ").append("RESPUESTA TABLA").append("</font><hr><br>");
                } else {
                    str.append("<font color=" + color1 + ">");
                    str.append("<b>").append("" + cursor.getString(1) == null ? "" : cursor.getString(1) + "").append("</b> -</font> ");
                    str.append("<font color=" + color3 + ">");
                    str.append("<b>").append("" + cursor.getString(3) == null ? "" : cursor.getString(3) + "").append("</b> </font><br>");
                    str.append("<font color=" + color2 + ">");
                    str.append(": ").append(cursor.getString(2) == null ? "" : cursor.getString(2)).append("</font><hr><br>");
//                str.append("<font color="+color3+">");
//                str.append("<b>").append("("+cursor.getString(1)==null? "":cursor.getString(3)+")").append("</b><br><hr><br>");
//
//                str.append("</font>");
                }


            } while (cursor.moveToNext());
        }
        cursor.close();

        return str.toString();
    }

    public static String recuperaRespuestasPersonas(IdInformante idInformante) {

        StringBuilder str = new StringBuilder();
        String color1 = "#001e41";
        String color2 = "#50ac27";
        String color3 = "#00446c";
        String seccion = "";
        String persona = "";
        Cursor cursor = null;
        try {
            String query = "SELECT ee.id_pregunta,ep.codigo_pregunta, ee.respuesta, ep.pregunta, es.seccion, ei.codigo,\n" +
                    "(SELECT respuesta FROM enc_encuesta WHERE id_asignacion=ee.id_asignacion and correlativo=ee.correlativo AND id_pregunta=2033) nombre, \n" +
                    "(SELECT respuesta FROM enc_encuesta WHERE id_asignacion=ee.id_asignacion and correlativo=ee.correlativo AND id_pregunta=2036) edad\n" +
                    "FROM (SELECT * FROM enc_informante WHERE id_asignacion_padre=" + idInformante.id_asignacion + " AND correlativo_padre=" + idInformante.correlativo + " AND estado <> 'ANULADO') ei \n" +
                    "JOIN  enc_encuesta ee \n" +
                    "ON (ei.id_asignacion=ee.id_asignacion AND ei.correlativo=ee.correlativo)\n" +
                    "JOIN enc_pregunta ep ON (ee.id_pregunta=ep.id_pregunta ) JOIN enc_seccion es ON ep.id_seccion=es.id_seccion\n" +
                    "AND ee.visible LIKE 't%'\n" +
                    "ORDER BY ei.codigo,ep.codigo_pregunta";
            cursor = conn.rawQuery(query, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (cursor.moveToFirst()) {
            do {

                String personaActual = cursor.getString(5) == null ? "" : cursor.getString(5);
                String nombreActual = cursor.getString(6) == null ? "" : cursor.getString(6);
                String edadActual = cursor.getString(7) == null ? "" : cursor.getString(7);
                if (!persona.equals(personaActual)) {
                    str.append("<font color=" + color1 + ">");
                    str.append("<h1><b>").append(nombreActual + " - " + personaActual + " - " + edadActual).append("</b> </h1></font> ");
                    persona = personaActual;
                }

                String seccionActual = cursor.getString(4) == null ? "" : cursor.getString(4);
                if (!seccion.equals(seccionActual)) {
                    str.append("<font color=" + color1 + ">");
                    str.append("<h3><b>").append("SECCION: " + seccionActual + " - " + nombreActual + " - " + persona + " - " + edadActual).append("</b> </h3></font> <br>");
                    seccion = seccionActual;
                }

                int idPreg = cursor.getString(0) == null ? 0 : Integer.parseInt(cursor.getString(0));
                if (idPreg == Parametros.ID_INCIDENCIA_FINAL) {
                    str.append("<font color=" + color1 + ">");
                    str.append("<b>").append("" + cursor.getString(1) == null ? "" : cursor.getString(1) + "").append("</b> -</font> ");
                    str.append("<font color=" + color3 + ">");
                    str.append("<b>").append("Pregunta de incidencia").append("</b> </font><br>");
                    str.append("<font color=" + color2 + ">");
                    str.append(": ").append("TIENE RESPUESTA INCIDENCIA FINAL").append("</font><hr><br>");
                } else if (idPreg == 36201
                ) {
                    str.append("<font color=" + color1 + ">");
                    str.append("<b>").append("" + cursor.getString(1) == null ? "" : cursor.getString(1) + "").append("</b> -</font> ");
                    str.append("<font color=" + color3 + ">");
                    str.append("<b>").append("Pregunta Tipo tabla").append("</b> </font><br>");
                    str.append("<font color=" + color2 + ">");
                    str.append(": ").append("RESPUESTA TABLA").append("</font><hr><br>");
                } else {
                    str.append("<font color=" + color1 + ">");
                    str.append("<b>").append("" + cursor.getString(1) == null ? "" : cursor.getString(1) + "").append("</b> -</font> ");
                    str.append("<font color=" + color3 + ">");
                    str.append("<b>").append("" + cursor.getString(3) == null ? "" : cursor.getString(3) + "").append("</b> </font><br>");
                    str.append("<font color=" + color2 + ">");
                    str.append(": ").append(cursor.getString(2) == null ? "" : cursor.getString(2)).append("</font><hr><br>");
//                str.append("<font color="+color3+">");
//                str.append("<b>").append("("+cursor.getString(1)==null? "":cursor.getString(3)+")").append("</b><br><hr><br>");
//
//                str.append("</font>");
                }


            } while (cursor.moveToNext());
        }
        cursor.close();

        return str.toString();
    }

    //Migracion de funciones de la aplicacion
    //cambio de comparacion con una pregunta anterior en caso de gennero

    /***
     *
     * @param idInformante
     * @return
     */
    public static String sexo_anterior(IdInformante idInformante) {
        String res = null;
        String query = "SELECT codigo_respuesta\n" +
                "FROM enc_informante ei\n" +
                "JOIN enc_informante_anterior eia on ei.id_asignacion_anterior = eia.id_asignacion AND ei.correlativo_anterior=eia.correlativo\n" +
                "JOIN enc_encuesta_anterior eea on eia.id_asignacion = eea.id_asignacion AND eia.correlativo=eea.correlativo\n" +
                "WHERE ei.id_asignacion =  " + idInformante.id_asignacion + " AND ei.correlativo=" + idInformante.correlativo + "\n" +
                "AND eea.id_pregunta = 2034";

        Cursor cursor = conn.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                res = cursor.getString(0);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return res;
    }

    /***
     *
     * @param idInformante
     * @return
     */
    public static String numeroRecidentesVivienda(IdInformante idInformante) {
        String res = null;
        String query = "SELECT codigo_respuesta\n" +
                "FROM enc_informante ei\n" +
                "JOIN enc_informante_anterior eia on ei.id_asignacion_anterior = eia.id_asignacion AND ei.correlativo_anterior=eia.correlativo\n" +
                "JOIN enc_encuesta_anterior eea on eia.id_asignacion = eea.id_asignacion AND eia.correlativo=eea.correlativo\n" +
                "WHERE ei.id_asignacion =  " + idInformante.id_asignacion + " AND ei.correlativo=" + idInformante.correlativo + "\n" +
                "AND eea.id_pregunta = 2151";

        Cursor cursor = conn.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                res = cursor.getString(0);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return res;
    }

    /**
     * @param idInformante
     * @return
     */
    public static int getContarParentesco2(IdInformante idInformante) {
        int res = 0;
        String query = "SELECT COUNT(*)\n" +
                "FROM ENC_INFORMANTE ei JOIN \n" +
                "ENC_INFORMANTE eip ON (ei.id_asignacion=" + idInformante.id_asignacion + " AND ei.correlativo=" + idInformante.correlativo + " \n" +
                "AND ei.id_asignacion_padre=eip.id_asignacion_padre AND ei.correlativo_padre=eip.correlativo_padre \n" +
                "AND eip.id_asignacion <>" + idInformante.id_asignacion + " AND eip.correlativo<>" + idInformante.correlativo + ") \n" +
                "JOIN ENC_ENCUESTA ee ON eip.id_asignacion = ee.id_asignacion\n" +
                "AND eip.correlativo=ee.correlativo AND ID_PREGUNTA =2038  AND visible like 't%' AND CODIGO_RESPUESTA='2'";
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
     * Obtiene la Fecha de nacimiento.
     * @param idInformante
     * @return
     */

    public static String fechaNacimiento(IdInformante idInformante) {
        String res = null;
        String query = "SELECT codigo_respuesta\n" +
                "FROM enc_informante ei\n" +
                "JOIN enc_informante_anterior eia on ei.id_asignacion_anterior = eia.id_asignacion AND ei.correlativo_anterior=eia.correlativo\n" +
                "JOIN enc_encuesta_anterior eea on eia.id_asignacion = eea.id_asignacion AND eia.correlativo=eea.correlativo\n" +
                "WHERE ei.id_asignacion =  " + idInformante.id_asignacion + " AND ei.correlativo=" + idInformante.correlativo + "\n" +
                "AND eea.id_pregunta = 2037";

        Cursor cursor = conn.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                res = cursor.getString(0);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return res;
    }

    /***
     *
     * @param idInformante
     * @return
     */
    public static String getEdad(IdInformante idInformante) {
        String res = null;
        String query = "SELECT codigo_respuesta\n" +
                "FROM enc_informante ei\n" +
//                "JOIN enc_informante_anterior eia on ei.id_asignacion_anterior = eia.id_asignacion AND ei.correlativo_anterior=eia.correlativo\n" +
                "JOIN enc_encuesta eea on ei.id_asignacion = eea.id_asignacion AND ei.correlativo=eea.correlativo\n" +
                "WHERE ei.id_asignacion =  " + idInformante.id_asignacion + " AND ei.correlativo=" + idInformante.correlativo + "\n" +
                "AND eea.id_pregunta = "+Parametros.ID_EDAD_PERSONAS+"";

        Cursor cursor = conn.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                res = cursor.getString(0);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return res;
    }
    public static String getNombre(IdInformante idInformante) {
        String res = "";

    try{
        String query = "SELECT respuesta\n" +
                "FROM enc_informante ei\n" +
                "JOIN enc_encuesta ee on ei.id_asignacion = ee.id_asignacion AND ei.correlativo=ee.correlativo\n" +
                "WHERE ei.id_asignacion =  " + idInformante.id_asignacion + " AND ei.correlativo=" + idInformante.correlativo + "\n" +
                "AND ee.id_pregunta = "+Parametros.ID_NOMBRE_PERSONAS+" \n"+
                "AND ei.estado <> 'ANULADO'";
        Cursor cursor = conn.rawQuery(query, null);
        Log.d("BASEBASE",query);
        if (cursor.moveToFirst()) {
            do {
                res = cursor.getString(0);
            } while (cursor.moveToNext());
        }
        cursor.close();
    }catch (Exception e){
        e.printStackTrace();
    }

        return res;
    }
    /**
     * @param idInformante
     * @return
     */
    public static String getParentesco(IdInformante idInformante) {
        String res = null;
        String query = "SELECT codigo_respuesta\n" +
                "FROM enc_informante ei\n" +
                "JOIN enc_informante_anterior eia on ei.id_asignacion_anterior = eia.id_asignacion AND ei.correlativo_anterior=eia.correlativo\n" +
                "JOIN enc_encuesta_anterior eea on eia.id_asignacion = eea.id_asignacion AND eia.correlativo=eea.correlativo\n" +
                "WHERE ei.id_asignacion =  " + idInformante.id_asignacion + " AND ei.correlativo=" + idInformante.correlativo + "\n" +
                "AND eea.id_pregunta = 2038";

        Cursor cursor = conn.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                res = cursor.getString(0);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return res;
    }

    public static void actualizaEncuesta(IdEncuesta idEncuesta, String codPreg, String secciones) {
        conn.beginTransaction();
        try {
            String query = "UPDATE enc_encuesta\n" +
                    "SET visible = 'f'\n" +
                    "WHERE id_asignacion = " + idEncuesta.id_asignacion + "\n" +
                    "AND correlativo = " + idEncuesta.correlativo + "\n" +
                    "AND id_pregunta in \n" +
                    "(SELECT id_pregunta FROM enc_pregunta WHERE id_seccion IN (" + secciones + ") AND \n" +
                    "codigo_pregunta >= '" + codPreg + "' order by codigo_pregunta)";
            Log.d("REVISION80", query);
            conn.execSQL(query);
            conn.setTransactionSuccessful();
        } finally {
            conn.endTransaction();
        }
    }

    public static Map<Integer, String> getEncuestaMatriz(IdInformante idInformante,  String codEspecifique) {
//    public static Map<Integer, Map<String, String>> getPreguntaMatriz(Integer codEspecifique) {
        Map<Integer, String> res = new TreeMap<>();
        String query = "SELECT id_pregunta, codigo_respuesta\n" +
                "FROM enc_encuesta\n" +
                "WHERE id_asignacion = " + idInformante.id_asignacion + "\n" +
                "AND correlativo = " + idInformante.correlativo + "\n" +
                "AND id_pregunta IN (\n" +
                "SELECT id_pregunta\n" +
                "FROM enc_pregunta\n" +
                "WHERE codigo_especifique = '" + codEspecifique +"')\n" +
                "ORDER BY id_pregunta";

        Log.d("MAL", String.valueOf(query));

        Cursor cursor = conn.rawQuery(query, null);
        while (cursor.moveToNext()) {
            res.put(cursor.getInt(0), cursor.getString(1));
        }

        cursor.close();
        return res;
    }

    /**
     * Realiza la validacion de informantes según reglas establecidas
     *
     * @param codPreg
     * @param codResp
     * @param aux
     * @param hayEdad
     * @param nromv
     * @param fecha
     * @param edad
     * @param parentesco
     * @param contarParentesco
     * @param nroMes
     * @return
     */
    public String isValid(String codPreg, String codResp, String aux, boolean hayEdad, String nromv, String fecha, String edad, String parentesco, int contarParentesco, String nroMes) {
        // SI ES QUE HAY EL VALOR DE EDAD, CONSISTENCIA CON EL PERIODO ANTERIOR
//        if(codPreg.equals("S1A_03A") && aux != null){
//            if(hayEdad){
//                if( Integer.parseInt(codResp.trim()) < Integer.parseInt(aux.trim()) || Integer.parseInt(codResp.trim()) > Integer.parseInt(aux.trim())+1 ){
//                    return "Error:La edad es incoherente con la del periodo anterior";
//                }
//            }
//        }
        edad = edad;
        if (codPreg.equals("S1A_03A") && edad != null) {

            if (Integer.parseInt(codResp.trim()) < Integer.parseInt(edad.trim()) || Integer.parseInt(codResp.trim()) > Integer.parseInt(edad.trim()) + 1) {
                return "Alert:La edad es diferente con el periodo anterior";
            }
        }
        if (codPreg.equals("S1A_05") && codResp.equals("02")) {
//            if(contarParentesco>0){
//                return "Error:El código Esposa/o o conviviente ya fue utilizado en un miembro del hogar anteriormente.";
//            }
        }

        if (codPreg.equals("S3A_67.2")) {
            if (!String.valueOf(codResp.split(";").length).equals(nroMes)) {
                return "Error:El número de meses declarado no es igual al número de meses que ha marcado";
            }
        }

        if (codPreg.equals("E_01") && nromv != null) {

            if (Integer.parseInt(codResp.trim()) != Integer.parseInt(nromv.trim())) {
                return "Alert:El nro de miembros es diferente con el periodo anterior";
            }
        }
//        if(codPreg.equals("S1A_04") && fecha!=null){
//
//            if( Integer.parseInt(codResp.trim()) != Integer.parseInt(fecha.trim()) ){
//                return "Alert:La fecha de nacimiento es diferente con el periodo anterior";
//            }
//        }

        if (codPreg.equals("S1A_05") && parentesco != null) {

            if (!codResp.trim().equals(parentesco.trim())) {
                return "Alert:La relación de parentesco no coincide con el de la anterior visita";
            }
        }

//        // REALIZA UN ANALISIS DE POSTCONSISTENCIAS ANTES DE CERRAR LA BOLETA
//        if(Pregunta.esUltimaPregunta(codPreg))
//        {
//            Regla regla = new Regla();
//            if ( !(regla.resumen(filaNueva.getAsInteger("id_informante")).toString().equals("No se encontraron inconsistencias."))) {
//                return "Error:" + regla.resumen(filaNueva.getAsInteger("id_informante"));
//            }
//        }

        Estado e = Estado.valueOf(filaNueva.getAsString("apiestado"));
        if (e == Estado.NOAPLICA || e == Estado.SENIEGA || e == Estado.NOSABE) {
            return "Ok";
        }
        Pregunta pregunta = new Pregunta();
        if (pregunta.abrir(filaNueva.getAsInteger("id_pregunta"))) {
//            if (evaluar(pregunta.get_regla(), filaNueva.getAsInteger("id_informante"), filaNueva.getAsInteger("fila"), codPreg, codResp) > 0) {
            if (false) {
                String cod = filaNueva.getAsString("codigo_respuesta");
                if (cod.equals("-1") || cod.equals("-1.0")) {
                    return "Error:Debe introducir/seleccionar un valor.";
                }
                if (filaNueva.getAsString("codigo_respuesta").equals("-2")) {
                    return "Error:El mes debe estar entre 0 y 12.";
                }
                switch (pregunta.get_id_tipo_pregunta()) {
                    case Multiple: {
                        int n = filaNueva.getAsString("codigo_respuesta").split(";").length;
                        if (n >= pregunta.get_minimo()) {
                            if (pregunta.get_maximo() == 0 || n <= pregunta.get_maximo()) {
                                return "Ok";
                            } else {
                                return "Error:Debe seleccionar como máximo " + pregunta.get_maximo();
                            }
                        } else {
                            return "Error:Debe seleccionar al menos " + pregunta.get_minimo();
                        }
                    }
                    case Numero:
                    case Decimal:
                    case HoraMinuto:
                    case MesAnio:
                    case Fecha:
                        try {
                            Double val = Double.parseDouble(filaNueva.getAsString("codigo_respuesta"));
                            if (pregunta.get_maximo() == 0) {
                                return "Ok";
                            } else {
                                if (val >= pregunta.get_minimo()) {
                                    if (val <= pregunta.get_maximo()) {
                                        return "Ok";
                                    } else {
                                        return "Error:Limite maximo " + pregunta.get_maximo();
                                    }
                                } else {
                                    return "Error:Limite minimo " + pregunta.get_minimo();
                                }
                            }
                        } catch (NumberFormatException ex) {
                            return "Error:" + ex.getMessage();
                        }
                    default:
                        return "Ok";
                }
            } else {
                /// parse JSON obtener mensaje
                return pregunta.get_apoyo();
            }
        } else {
            return "Error:Pregunta no encontrada.";
        }
    }

    ///Realiza la validacion de una pregunta si es valida
    public String verificaRespuesta(EncuestaModel encuestamodel) {
        String resp = null;
        Pregunta pregunta = new Pregunta();
        Encuesta encuesta = new Encuesta();
        if (!pregunta.abrir(encuestamodel.getId_pregunta())) {
            return "Error:No existe la pregunta.";

        }///comparar datos cambiados en la base de datos y el modelo
        else if (encuestamodel == null && encuestamodel.getRespuesta() == null && !encuesta.abrir(new StringBuilder("id_asignacion=")
                .append(encuestamodel.getId_asignacion())
                .append(" AND correlativo=").append(encuestamodel.getCorrelativo())
                .append(" AND id_pregunta=").append(encuestamodel.getId_pregunta()).toString(), "")) {
            return "Error:Debe introducir/seleccionar un valor.";
        } else {
            List<String> omision = Pregunta.getOmisionId(pregunta.get_id_pregunta());
            if (!omision.contains(encuestamodel.getCodigo_respuesta())) {
                if (pregunta.get_id_tipo_pregunta().ordinal() == TipoPregunta.Numero.ordinal() || pregunta.get_id_tipo_pregunta().ordinal() == TipoPregunta.Decimal.ordinal()) {
                    try {
//                        //Quemado para casos exepcionales tipo uso de vivienda 2 y 3
//                        if (!((p.getId() == 2201
//                                || p.getId() == 2202 || p.getId() == 2203) && p.getCodResp().equals("0"))
//                        ) {
                        if ((Float.parseFloat(encuestamodel.getCodigo_respuesta()) < pregunta.get_minimo() && pregunta.get_minimo() > 0) || (Float.parseFloat(encuestamodel.getCodigo_respuesta()) > pregunta.get_maximo() && pregunta.get_maximo() > 0)) {
                            return "Error:" + String.format("El valor de " + pregunta.get_codigo_pregunta() + " debe estar entre %d y %d", pregunta.get_minimo(), pregunta.get_maximo());
                        }
                    } catch (Exception ex) {

                        return "Error:" + String.format("El valor de " + pregunta.get_codigo_pregunta() + " debe estar entre %d y %d", pregunta.get_minimo(), pregunta.get_maximo());
                    }
                } else if (pregunta.get_id_tipo_pregunta().ordinal() == TipoPregunta.Abierta.ordinal() || pregunta.get_id_tipo_pregunta().ordinal() == TipoPregunta.Memoria.ordinal() || pregunta.get_id_tipo_pregunta().ordinal() == TipoPregunta.Autocompletar.ordinal() || pregunta.get_id_tipo_pregunta().ordinal() == TipoPregunta.Prioridad.ordinal()) {
                    if (encuestamodel.getRespuesta().length() < pregunta.get_minimo() && pregunta.get_minimo() > 0 || (encuestamodel.getRespuesta().length() > pregunta.get_maximo() && pregunta.get_maximo() > 0)) {
                        return "Error:" + String.format("La longitud de " + pregunta.get_codigo_pregunta() + " debe estar entre %d y %d", pregunta.get_minimo(), pregunta.get_maximo()) + " (Valor actual: " + encuestamodel.getRespuesta().length() + ")";
                    }
                } else if (pregunta.get_id_tipo_pregunta().ordinal() == TipoPregunta.Multiple.ordinal()) {
                    int n = encuestamodel.getCodigo_respuesta().split(";").length;
                    if (n >= pregunta.get_minimo()) {
                        if (pregunta.get_maximo() == 0 || n <= pregunta.get_maximo()) {
                        } else {
                            return "Error:Debe seleccionar como máximo " + pregunta.get_maximo();
                        }
                    } else {
                        return "Error:Debe seleccionar al menos " + pregunta.get_minimo();
                    }
                } else if (pregunta.get_id_tipo_pregunta().ordinal() == TipoPregunta.Fecha.ordinal()) {
                    try {
                        Double val = Double.parseDouble(encuestamodel.getCodigo_respuesta());
                        if (pregunta.get_maximo() == 0) {
                        } else {
                            if (val >= pregunta.get_minimo()) {
                                if (val <= pregunta.get_maximo()) {
                                } else {
                                    return "Error:Limite maximo " + pregunta.get_maximo();
                                }
                            } else {
                                return "Error:Limite minimo " + pregunta.get_minimo();
                            }
                        }
                    } catch (NumberFormatException ex) {
                        return "Error:" + ex.getMessage();
                    }
                }


            }
            //cierre omision
            //evaluacion de reglas

            JSONArray reglasJsonArray = null;
            try {
                reglasJsonArray = pregunta.getRegla();
            } catch (JSONException jsonException) {
                return "Error:Vererificar sintaxis de regla " + pregunta.get_codigo_pregunta();
            }
            try {
                boolean flag = false;
                int contador = 0;
//                for (int i=0;i<reglasJsonArray.length();i++) {
                String rmensaje = null;
                String rcodigo = null;
                while (contador < reglasJsonArray.length() && !flag) {
                    String rregla = reglasJsonArray.getJSONObject(contador).getString("regla");
                    rmensaje = reglasJsonArray.getJSONObject(contador).getString("mensaje");
                    rcodigo = reglasJsonArray.getJSONObject(contador).getString("codigo");


                    if (evaluar(rregla, String.valueOf(encuestamodel.getId_pregunta()), encuestamodel.getCodigo_respuesta(), encuestamodel)) {

                        flag = true;

                    }
                }
                if (flag) {
                    switch (rcodigo) {
                        case "1":
                            return "Error:Error de consistencia! " + rmensaje;


                        case "2":
                            if (encuestamodel.getObservacion() == null || encuestamodel.getObservacion().length() < 3)
                                return "Advertencia:" + rmensaje;

                    }
                }

            } catch (JSONException jsonException) {
                return "Error:Vererificar sintaxis de regla " + pregunta.get_codigo_pregunta();
            }
            pregunta.free();
            encuesta.free();
        }
        return "ok";

    }


    public static boolean evaluar(String regla, String idPregunta, String codResp, EncuestaModel encuestaModel) {
        boolean result;
        String[] aux = null;
        Set<String> allMatches = new HashSet<>();
        String nuevoValor = regla;

        nuevoValor = evaluarMultiple(nuevoValor, idPregunta, codResp, encuestaModel);
        regla = nuevoValor;
        Matcher m = Flujo.p.matcher(regla);
        while (m.find()) {
            String str = m.group();
            allMatches.add(str);
        }
        for (String str : allMatches) {
            String val;
            String element = str.replace("$", "");
            if (element.equals(String.valueOf(idPregunta))) {
                val = codResp;
            } else {
                val = Encuesta.obtenerValorId(encuestaModel.getIdInformante(), element);
            }
            if (val.contains(",")) {
                aux = val.split(",");
                Log.d("covid", String.valueOf(val));
                val = "(aux)";

            }
//            if (val.contains("/")){
//                aux = val.split("/");
//                int años;
//                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
//                    LocalDate today = LocalDate.now();
//                    LocalDate birthdate = LocalDate.of(Integer.valueOf(aux[2]),Integer.valueOf(aux[1]),Integer.valueOf(aux[0]));
//                    Period period = Period.between(birthdate, today);
//                    años = period.getYears();
//                }else{
//                    Calendar cal = Calendar.getInstance();
//                    cal.setTime(new Date());
//                    int year = cal.get(Calendar.YEAR);
//                    int month = cal.get(Calendar.MONTH) + 1;
//                    int day = cal.get(Calendar.DAY_OF_MONTH);
//
//                    Calendar c2 = new GregorianCalendar(Integer.valueOf(aux[2]),Integer.valueOf(aux[1]),Integer.valueOf(aux[0]));
//                    Calendar c1 =  new GregorianCalendar(year, month, day);
//
//                    long end = c2.getTimeInMillis();
//                    long start = c1.getTimeInMillis();
//
//                    long milliseconds = TimeUnit.MILLISECONDS.toMillis(Math.abs(end - start));
//
//                    Calendar c = Calendar.getInstance();
//                    c.setTimeInMillis(milliseconds);
//                    int mYear = c.get(Calendar.YEAR)-1970;
//                    int mMonth = c.get(Calendar.MONTH);
//                    int mDay = c.get(Calendar.DAY_OF_MONTH)-1;
//
//                    años = mYear;
//                }
//                val = String.valueOf(años);
//            }

            nuevoValor = nuevoValor.replace(str, val);

            if (nuevoValor.contains("CODIGO")) {
                val = String.valueOf(Informante.getCodigo(encuestaModel.getIdInformante()));
                nuevoValor = nuevoValor.replace("CODIGO", val);
            }
            if (nuevoValor.contains("EDAD_ANTERIOR")) {
                val = String.valueOf(Informante.getEdadAnterior(encuestaModel.getIdInformante()));
                nuevoValor = nuevoValor.replace("EDAD_ANTERIOR", val);
            }
            if (nuevoValor.contains("NRO_ANTERIOR")) {
                val = String.valueOf(Informante.getNroAnterior(encuestaModel.getIdInformante()));
                nuevoValor = nuevoValor.replace("NRO_ANTERIOR", val);
            }
            if (nuevoValor.contains("PARENTESCO_ANTERIOR")) {
                val = String.valueOf(Informante.getParentescoAnterior(encuestaModel.getIdInformante()));
                nuevoValor = nuevoValor.replace("PARENTESCO_ANTERIOR", val);
            }
        }
//        if (nuevoValor.contains("RURAL")) {
//            //1 rural
//            //0 urbano
//            String urbano = String.valueOf(Informante.urbano(Asignacion.getUpm(idInformante.id_asignacion)));
//            nuevoValor = nuevoValor.replace("RURAL", urbano);
//            Log.d("rural", nuevoValor);
//        }
        if (nuevoValor.contains("SECCION")) {
            Log.d("SECCION", nuevoValor);
            if (Encuesta.fueRespondida(encuestaModel.getIdInformante())) {
                nuevoValor = nuevoValor.replace("SECCION", "1");
            } else {
                nuevoValor = nuevoValor.replace("SECCION", "0");
            }
        }
//        's05c_10a';'s06a_00.1';'s07a_01.1';
        if (nuevoValor.contains("GASTOS")) {
            Log.d("GASTOS", nuevoValor);
            if (Encuesta.fueRespondida(encuestaModel.getIdInformante())) {
                nuevoValor = nuevoValor.replace("GASTOS", "1");
            } else {
                nuevoValor = nuevoValor.replace("GASTOS", "0");
            }
        }
        if (nuevoValor.contains("CONTAR")) {
            if (Informante.getContarPersonas(encuestaModel.getIdInformante())) {
                nuevoValor = nuevoValor.replace("CONTAR", "0");
            } else {
                nuevoValor = nuevoValor.replace("CONTAR", "1");
            }
        }
        if (nuevoValor.contains("PERSONAS")) {
            if (Informante.hayPersonasConcluidas(encuestaModel.getIdInformante()) > 0) {
                nuevoValor = nuevoValor.replace("PERSONAS", String.valueOf(Informante.hayPersonasConcluidas(encuestaModel.getIdInformante())));
            } else {
                nuevoValor = nuevoValor.replace("PERSONAS", "0");
            }
        }
        if (nuevoValor.contains("HAY_CARNET")) {
            if (Informante.getHaySelectCarnet(encuestaModel.getIdInformante()) > 0) {
                nuevoValor = nuevoValor.replace("HAY_CARNET", String.valueOf(Informante.getHaySelectCarnet(encuestaModel.getIdInformante())));
            } else {
                nuevoValor = nuevoValor.replace("HAY_CARNET", "0");
            }
        }
        if (nuevoValor.contains("PERSONA15")) {
            if (Informante.contarMayores15(encuestaModel.getIdInformante()) > 0) {
                nuevoValor = nuevoValor.replace("PERSONA15", String.valueOf(Informante.contarMayores15(encuestaModel.getIdInformante())));
            } else {
                nuevoValor = nuevoValor.replace("PERSONA15", "0");
            }
        }

        //EXPRESIONES GENERALES
        nuevoValor = nuevoValor.replace("~", "==");
        nuevoValor = nuevoValor.replace("!(", "NOT(");
        //EVALUACION
        BigDecimal eval = null;
        if (nuevoValor.contains("(aux)")) {
            String valorEval;
            int cont = 0;
            for (String naux : aux) {
                valorEval = nuevoValor.replace("(aux)", naux);
                Expression expression = new Expression(valorEval);
                eval = expression.eval();
                if (Integer.parseInt(String.valueOf(eval)) > 0) {
                    cont++;
                }
            }
            if (aux.length == cont) {
                result = true;
            } else {
                result = false;
            }
        } else {
            result = false;
            if (nuevoValor.contains("null")) {
                //TODO: VERIFICAR CONSISTENCIA
                result = false;
            } else {
                Expression expression = new Expression(nuevoValor);
                eval = expression.eval();
                if (Integer.parseInt(String.valueOf(eval)) > 0) {
                    result = true;
                }
            }
        }
        return result;
    }

    public static String evaluarMultiple(String regla, String idPregunta, String codResp, EncuestaModel encuestaModel) {
        String devuelveMultiple = regla;

        List<String> allMatches = new ArrayList<String>();
        String resp = "";
        try {
            Matcher mm = Flujo.p.matcher(regla);
            int aux = 0;
            while (mm.find()) {
                String str = mm.group();
                allMatches.add(str);
            }
            for (String str : allMatches) {
                List<String> allMatches2 = new ArrayList<String>();
                String valorFinal = str;
                String nuevoValor = str;
                Matcher m2 = Flujo.p.matcher(str);
                while (m2.find()) {
                    String str2 = m2.group();
                    allMatches2.add(str2);
                }
                for (String str2 : allMatches2) {
                    String val;
                    String element = str2.replace("$", "");
                    if (element.equals(String.valueOf(idPregunta))) {
                        val = codResp;
                    } else {
                        val = Encuesta.obtenerValorId(encuestaModel.getIdInformante(), element);
                    }
                    nuevoValor = nuevoValor.replace(str2, val);
                }

                str = nuevoValor.substring(1, nuevoValor.length() - 1).trim();
                Log.d("respuestaSUPREM", str);
                if (str.contains("==")) {
                    String[] a = str.split("==");
                    if (a[0].contains(",")) {
                        String[] b = a[0].split(",");
                        for (String c : b) {
                            if (c.equals(a[1])) {
                                aux++;
                            }
                        }
                    } else {
                        if (a[0].equals(a[1])) {
                            aux++;
                        }
                    }
                    if (aux > 0) {
                        resp = "1==1";
                    } else {
                        resp = "1!=1";
                    }

                } else if (str.contains(">")) {
                    String[] a = str.split(">");
                    if (a[0].contains(",")) {
                        String[] b = a[0].split(",");
                        for (String c : b) {
                            if (a[1].startsWith("=")) {
                                if (c.equals(a[1].replace("=", ""))) {
                                    aux++;
                                } else if (Integer.parseInt(c) > Integer.parseInt(a[1].replace("=", ""))) {
                                    aux++;
                                }
                            } else {
                                if (Integer.parseInt(c) > Integer.parseInt(a[1])) {
                                    aux++;
                                }
                            }
                        }
                        if (aux == b.length) {
                            resp = "1==1";
                        } else {
                            resp = "1!=1";
                        }
                    } else {
                        if (a[1].startsWith("=")) {
                            if (a[0].equals(a[1].replace("=", ""))) {
                                aux++;
                            } else if (Integer.parseInt(a[0]) > Integer.parseInt(a[1].replace("=", ""))) {
                                aux++;
                            }
                        } else {
                            if (Integer.parseInt(a[0]) > Integer.parseInt(a[1])) {
                                aux++;
                            }
                        }
                        if (aux == 1) {
                            resp = "1==1";
                        } else {
                            resp = "1!=1";
                        }
                    }

                } else if (str.contains("<")) {
                    String[] a = str.split("<");
                    if (a[0].contains(",")) {
                        String[] b = a[0].split(",");
                        for (String c : b) {
                            if (a[1].startsWith("=")) {
                                if (c.equals(a[1].replace("=", ""))) {
                                    aux++;
                                } else if (Integer.parseInt(c) < Integer.parseInt(a[1].replace("=", ""))) {
                                    aux++;
                                }
                            } else {
                                if (Integer.parseInt(c) < Integer.parseInt(a[1])) {
                                    aux++;
                                }
                            }
                        }
                        if (aux == b.length) {
                            resp = "1==1";
                        } else {
                            resp = "1!=1";
                        }
                    } else {
                        if (a[1].startsWith("=")) {
                            if (a[0].equals(a[1].replace("=", ""))) {
                                aux++;
                            } else if (Integer.parseInt(a[0]) < Integer.parseInt(a[1].replace("=", ""))) {
                                aux++;
                            }
                        } else {
                            if (Integer.parseInt(a[0]) < Integer.parseInt(a[1])) {
                                aux++;
                            }
                        }
                        if (aux == 1) {
                            resp = "1==1";
                        } else {
                            resp = "1!=1";
                        }
                    }
                } else if (str.contains("!=")) {
                    String[] a = str.split("!=");
                    if (a[0].contains(",")) {
                        String[] b = a[0].split(",");
                        for (String c : b) {
                            if (!c.equals(a[1])) {
                                aux++;
                            }
                        }
                        if (aux == b.length) {
                            resp = "1==1";
                        } else {
                            resp = "1!=1";
                        }
                    } else {
                        if (!a[0].equals(a[1])) {
                            aux++;
                        }
                        if (aux == 1) {
                            resp = "1==1";
                        } else {
                            resp = "1!=1";
                        }
                    }
                }
                devuelveMultiple = devuelveMultiple.replace(valorFinal, resp);
                aux = 0;
            }

        } catch (Exception e) {
            e.printStackTrace();

        }
        return devuelveMultiple;
    }
    public static  ArrayList<Map<String, Object>> obtenerListadoUltimasDirecciones(String filtro){
        ArrayList<Map<String, Object>> list = new ArrayList<>();
        try {
            String query="SELECT DISTINCT(respuesta) FROM enc_encuesta\n"+
            "WHERE id_pregunta="+Parametros.ID_PREGUNTA_AVENIDA_CALLE+" AND UPPER(respuesta) LIKE '%"+ filtro+"%'\n"+
            "ORDER by feccre desc,id_asignacion DESC, correlativo DESC\n"+
            "LIMIT 3";
            Cursor cursor = conn.rawQuery(query, null);

            if (cursor.moveToFirst()) {
                do {
                    Map<String, Object> map = new LinkedHashMap<>();
                   map.put("id_catalogo",Movil.getMd5Hash(cursor.getString(0)));
                   map.put("descripcion",cursor.getString(0));
                    list.add(map);
                } while (cursor.moveToNext());
            }
            cursor.close();

        }catch (Exception e){
            e.printStackTrace();
        }
        return list;
    }


    public ArrayList<Map<String, Object>> obtenerListadoTablaMatriz(int idAsignacion, int correlativo, int id) {
        ArrayList<Map<String, Object>> res = new ArrayList<>();
        String query;
        Cursor cursor = null;

        String nombre = "(SELECT ee.respuesta FROM enc_encuesta ee WHERE ee.id_asignacion = e.id_asignacion AND ee.correlativo = e.correlativo AND ee.id_pregunta IN (38105) AND ee.fila = e.fila AND ee.visible in ('t','true')) nombre";
        String sexo = "(SELECT ee.respuesta FROM enc_encuesta ee WHERE ee.id_asignacion = e.id_asignacion AND ee.correlativo = e.correlativo AND ee.id_pregunta IN (38109) AND ee.fila = e.fila AND ee.visible in ('t','true')) sexo";
        String edad = "(SELECT ee.respuesta FROM enc_encuesta ee WHERE ee.id_asignacion = e.id_asignacion AND ee.correlativo = e.correlativo AND ee.id_pregunta IN (38107) AND ee.fila = e.fila AND ee.visible in ('t','true')) edad";

        query = "SELECT id_asignacion, correlativo, fila, "+nombre+","+ sexo +","+edad+" \n" +
                "FROM enc_encuesta e\n" +
                "WHERE i.estado <> 'ANULADO'\n" +
                "AND i.estado <> 'INHABILITADO'\n" +
                "AND i.estado <> 'CONCLUIDO'\n" +
                "AND id_asignacion = " + idAsignacion + "\n" +
                "AND i.correlativo = " + correlativo + "\n" +
                "AND i.id_pregunta = 38105 \n" +
                "ORDER BY fila";

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
                row.put("fila", cursor.getInt(2));
                row.put("nombre", cursor.getInt(3));
                row.put("sexo", cursor.getInt(4));
                row.put("edad", cursor.getString(5));
                res.add(row);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return res;
    }
}
