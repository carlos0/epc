package bo.gob.ine.naci.epc.herramientas;

import android.database.Cursor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import bo.gob.ine.naci.epc.entidades.Encuesta;
import bo.gob.ine.naci.epc.entidades.EntidadId;
import bo.gob.ine.naci.epc.entidades.IdInformante;
import bo.gob.ine.naci.epc.entidades.Pregunta;
import bo.gob.ine.naci.epc.entidades.Usuario;

public class ValidatorConsistencias extends EntidadId {
    private String sqlConsistencias = "";
    public ValidatorConsistencias() {
        super("enc_observaciones");
    }

    public void onloadConsistencias() {
        this.sqlConsistencias =" FROM (";
        try{
            ArrayList<Map<String, String>> res = new ArrayList<>();
            String query = "SELECT id_pregunta, pregunta FROM  enc_pregunta WHERE estado = 'ELABORADO' AND id_seccion  = '447'";
            Cursor cursor = conn.rawQuery(query, null);
            while (cursor.moveToNext()) {
                Map<String, String> map = new HashMap<>();
                map.put("id_pregunta", cursor.getString(0));
                map.put("pregunta", cursor.getString(1));
                res.add(map);
                this.sqlConsistencias =  this.sqlConsistencias +"\n"+ cursor.getString(1) + "\n UNION ALL";
            }
            cursor.close();
            int longitud = this.sqlConsistencias.length();
            int posicion = this.sqlConsistencias.lastIndexOf("UNION ALL");
            if (posicion >= 0 && longitud > posicion + 8) {
                this.sqlConsistencias = this.sqlConsistencias.substring(0, posicion);
            }
            this.sqlConsistencias = this.sqlConsistencias + " ) a";
        }catch (Exception e)
        {
            this.sqlConsistencias ="";
        }
    }
    public void dropView() {
        conn.beginTransaction();
        try {
            conn.execSQL("DROP VIEW IF EXISTS eh23_consistencias;");
            conn.setTransactionSuccessful();
        } finally {
            conn.endTransaction();
        }
    }
    public void createView(int vIda, int vIdc) {
        conn.beginTransaction();
        try {
            conn.execSQL("CREATE VIEW eh23_consistencias AS\n" +
                    "SELECT eii.id_asignacion||''-''||eii.correlativo||''|''||eii.id_asignacion_padre||''-''||eii.correlativo_padre as ida,\n" +
                    "eii.id_asignacion,\n" +
                    "eii.correlativo,\n" +
                    "eii.id_asignacion_padre,\n" +
                    "eii.correlativo_padre,\n" +
                    "codigo_pregunta,\n" +
                    "ee.id_pregunta,\n" +
                    "(CASE WHEN id_tipo_pregunta IN (0,6) THEN ee.respuesta ELSE ee.codigo_respuesta END) AS ida2, \n" +
                    "ee.observacion as justifique \n" +
                    "from enc_informante ei\n" +
                    "JOIN enc_informante eii ON eii.id_asignacion_padre=ei.id_asignacion and eii.correlativo_padre=ei.correlativo\n" +
                    "JOIN enc_encuesta ee ON ee.id_asignacion=eii.id_asignacion and ee.correlativo=eii.correlativo and ee.visible like  't%' \n" +
                    "JOIN enc_pregunta ep ON ep.id_pregunta=ee.id_pregunta\n" +
                    "where\n" +
                    "ep.id_pregunta IN (2483,13601,14270,14267,14271,14275,18119,18122,18126,18137,18139,18167,18168,18171,18172,18173,18174,18175,18176,18177,18178,18179,18186,18187,18189,18203,18206,18213,18214,18219,18220,18222,18223,18228,18229,18230,18231,18240,18241,18242,18245,18251,18252,18253,18268,18269,18272,18276,18278,18283,18290,18291,18306,18308,18314,18317,18319,18322,18323,18328,18330,18331,18332,18364,18383,18386,18408,18457,18458,18553,18580,18581,18584,18585,18617,18618,18619,20515,36323,36324,36342,36354,36355,36356,18364,18383,18386,18408,18457,18458,18553,18580,18581,18584,18585,18617,18618,18619,20515,36323,36324,36354,36355,36356,36466,36449,36451,36455,36465,36467,36468,36469,36470,36471,36473,36474,36475,36476,36477,36478,36479,36486,36548,36549,36551,36466,36449,36451,36455,36465,36467,36468,36469,36470,36471,36473,36474,36475,36476,36477,36478,36479,18205,36548,36549,36551,36555,36556,36561,36677,36694,36695,36696,36702,36847,36848,36849,36850,36851,36877,36880,36920,36921,36926,36928,36978,36966,36975,36977)\n" +
                    "and ep.estado='ELABORADO'\n" +
                    "and eii.id_asignacion=" + vIda + "\n" +
                    "and eii.correlativo=" + vIdc + ";");
            conn.setTransactionSuccessful();
        } finally {
            conn.endTransaction();
        }
    }
    public String getConsistencias2() {

        this.onloadConsistencias();
        String color = "#e20017";
        StringBuilder str = new StringBuilder();
        ArrayList<Map<String, String>> res = new ArrayList<>();
        String query = "SELECT\n" +
                "a.nro_obs,\n" +
                "a.id_asignacion,\n" +
                "a.correlativo,\n" +
                "a.id_asignacion_padre,\n" +
                "a.correlativo_padre,\n" +
                "a.persona,\n" +
                "a.id_pregunta,\n" +
                "a.respuesta,\n" +
                "a.criterio,\n" +
                "a.observacion,\n" +
                "a.codigo_pregunta\n" +
                this.sqlConsistencias;
        Cursor cursor = conn.rawQuery(query, null);
        while (cursor.moveToNext()) {
            Map<String, String> map = new HashMap<>();
            map.put("nro_obs", cursor.getString(0));
            map.put("id_asignacion", cursor.getString(1));
            map.put("correlativo", cursor.getString(2));
            map.put("id_asignacion_padre", cursor.getString(3));
            map.put("correlativo_padre", cursor.getString(4));
            map.put("persona", cursor.getString(5));
            map.put("id_pregunta", cursor.getString(6));
            map.put("respuestas", cursor.getString(7));
            map.put("criterio", cursor.getString(8));
            map.put("observacion", cursor.getString(9));
            map.put("codigo_pregunta", cursor.getString(10));
            Encuesta encuesta =new Encuesta();
            if (encuesta.abrir("id_asignacion = " + cursor.getString(1) + " AND correlativo = " + cursor.getString(2) + " AND id_pregunta = " + cursor.getString(6), null)) {
                if(!encuesta.get_observacion().equals("") || encuesta.get_observacion() != null   )
                    if (encuesta.get_observacion().length()<3)
                    {
                        res.add(map);
                        String vDes = Encuesta.getNombre(new IdInformante(Integer.valueOf(map.get("id_asignacion")),Integer.valueOf(map.get("correlativo"))));
                        String vObs = "Cod:"+ map.get("nro_obs") +"; Obs:" +map.get("observacion")  +"; Criterio:"+ map.get("criterio");
                        str.append("<font color="+color+">");
                        str.append("<b>").append("("+"Boletas Observadas-"+vDes+")").append("</b><br>");
                        str.append("Obs.: ").append(vObs).append("<br>");
                        str.append("Preg.: ").append(Pregunta.getPregunta(Integer.valueOf(map.get("id_pregunta"))));
                        str.append("<i>Creado por: "+"SYSTEMEH2023").append("</i><br><br>");
                        str.append("</font>");
                    }
            }
            encuesta.free();



        }
        cursor.close();
        return str.toString();
    }
    public ArrayList<Map<String, String>> getConsistencias() {
        this.onloadConsistencias();
        ArrayList<Map<String, String>> res = new ArrayList<>();
        String query = "SELECT\n" +
                "a.nro_obs,\n" +
                "a.id_asignacion,\n" +
                "a.correlativo,\n" +
                "a.id_asignacion_padre,\n" +
                "a.correlativo_padre,\n" +
                "a.persona,\n" +
                "a.id_pregunta,\n" +
                "a.respuesta,\n" +
                "a.criterio,\n" +
                "a.observacion,\n" +
                "a.codigo_pregunta\n" +
                this.sqlConsistencias;
        Cursor cursor = conn.rawQuery(query, null);
        while (cursor.moveToNext()) {
            Map<String, String> map = new HashMap<>();
            map.put("nro_obs", cursor.getString(0));
            map.put("id_asignacion", cursor.getString(1));
            map.put("correlativo", cursor.getString(2));
            map.put("id_asignacion_padre", cursor.getString(3));
            map.put("correlativo_padre", cursor.getString(4));
            map.put("persona", cursor.getString(5));
            map.put("id_pregunta", cursor.getString(6));
            map.put("respuestas", cursor.getString(7));
            map.put("criterio", cursor.getString(8));
            map.put("observacion", cursor.getString(9));
            map.put("codigo_pregunta", cursor.getString(10));
            Encuesta encuesta =new Encuesta();
            if (encuesta.abrir("id_asignacion = " + cursor.getString(1) + " AND correlativo = " + cursor.getString(2) + " AND id_pregunta = " + cursor.getString(6), null)) {
                if(!encuesta.get_observacion().equals("") || encuesta.get_observacion() != null   )
                    if (encuesta.get_observacion().length()<3)
                        res.add(map);
            }
            encuesta.free();
        }
        cursor.close();
        return res;
    }
    public void loadConsistenciasObs() {
        this.onloadConsistencias();
        int idUsuario = Usuario.getUsuario();
        ArrayList<Map<String, String>> res = new ArrayList<>();
        String query = "SELECT\n" +
                "a.nro_obs,\n" +
                "a.id_asignacion,\n" +
                "a.correlativo,\n" +
                "a.id_asignacion_padre,\n" +
                "a.correlativo_padre,\n" +
                "a.persona,\n" +
                "a.id_pregunta,\n" +
                "a.respuesta,\n" +
                "a.criterio,\n" +
                "a.observacion,\n" +
                "a.codigo_pregunta\n" +
                this.sqlConsistencias;
        Cursor cursor = conn.rawQuery(query, null);
        while (cursor.moveToNext()) {
            Map<String, String> map = new HashMap<>();
            map.put("nro_obs", cursor.getString(0));
            map.put("id_asignacion", cursor.getString(1));
            map.put("correlativo", cursor.getString(2));
            map.put("id_asignacion_padre", cursor.getString(3));
            map.put("correlativo_padre", cursor.getString(4));
            map.put("persona", cursor.getString(5));
            map.put("id_pregunta", cursor.getString(6));
            map.put("respuestas", cursor.getString(7));
            map.put("criterio", cursor.getString(8));
            map.put("observacion", cursor.getString(9));
            map.put("codigo_pregunta", cursor.getString(10));
            Encuesta encuesta =new Encuesta();
            if (encuesta.abrir("id_asignacion = " + cursor.getString(1) + " AND correlativo = " + cursor.getString(2) + " AND id_pregunta = " + cursor.getString(6), null)) {
                if(!encuesta.get_observacion().equals("") || encuesta.get_observacion() != null   )
                    if (encuesta.get_observacion().length()<3)
                    {
                        String insertSQL = "INSERT INTO enc_observacion(id_tipo_obs,id_usuario,id_asignacion,correlativo," +
                                "observacion, respuesta, usucre," +
                                "estado, id_pregunta," +
                                "id_asignacion_hijo, correlativo_hijo, justificacion) VALUES (" +
                                "2, '" + idUsuario + "', " + cursor.getString(3) + ", " + cursor.getString(4) + ", " +
                                "'Cod:"+ cursor.getString(0) +";Obs:"+cursor.getString(9)+"',"
                                +" (SELECT ep.respuesta FROM enc_encuesta ep " +
                                "   WHERE ep.id_asignacion ="+cursor.getString(1)+" " +
                                "       AND ep.correlativo = "+ cursor.getString(2) +" " +
                                "       AND ep.id_pregunta = "+cursor.getString(6)+")"+","+
                                "'SYSTEMEH2023',"+
                                "'ELABORADO', " + cursor.getString(6) + ", " +
                              cursor.getString(1) + ", " + cursor.getString(2) + ", '')";
                        conn.execSQL(insertSQL);
                    }
            }
            encuesta.free();
        }
        cursor.close();
    }
    public void loadConsistenciasObs1() {
        this.onloadConsistencias();
        conn.beginTransaction();
        try {
            int idUsuario = Usuario.getUsuario();
            String sql = "INSERT INTO enc_observacion(id_tipo_obs,id_usuario,id_asignacion,correlativo,\n" +
                    " observacion, respuesta, usucre,\n" +
                    " estado, id_pregunta,\n" +
                    " id_asignacion_hijo, correlativo_hijo, justificacion)\n" +
                    " SELECT " +
                    " 2," + idUsuario + ",a.id_asignacion_padre,a.correlativo_padre,\n" +
                   /* " 'Cod:'|| a.nro_obs ||';Obs:' ||a.observacion || ';Criterio:' || a.criterio  ,\n" +*/
                    " 'Cod:'|| a.nro_obs ||';Obs:' || a.observacion  ,\n" +
                    "  (SELECT ep.respuesta FROM enc_encuesta ep WHERE ep.id_asignacion = a.id_asignacion AND ep.correlativo = a.correlativo AND ep.id_pregunta = a.id_pregunta) , 'SYSTEMEH2023',\n" +
                    "  'ELABORADO', a.id_pregunta,\n" +
                    "  a.id_asignacion, a.correlativo,''\n" +
                    this.sqlConsistencias;
            conn.execSQL(sql);
            conn.setTransactionSuccessful();
        } catch (Exception e)
        {
            System.out.println("Error->"+e.getMessage());
        }
        finally {
            conn.endTransaction();
        }
    }
    public void deleteConsistenciasObs(int vIda, int vIdc) {
        conn.beginTransaction();
        try {
            String sql = "DELETE FROM enc_observacion " +
                    "  WHERE usucre = 'SYSTEMEH2023'  " +
                    "  AND id_asignacion_hijo =" + vIda +
                    "  AND correlativo_hijo =" + vIdc + ";";
            conn.execSQL(sql);
            conn.setTransactionSuccessful();
        } finally {
            conn.endTransaction();
        }
    }
}
