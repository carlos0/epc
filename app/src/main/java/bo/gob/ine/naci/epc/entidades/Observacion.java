package bo.gob.ine.naci.epc.entidades;

import android.database.Cursor;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import bo.gob.ine.naci.epc.herramientas.Movil;
import bo.gob.ine.naci.epc.herramientas.Parametros;

public class Observacion extends Entidad {
    public Observacion() {
        super("enc_observacion");
    }

    public static ArrayList<Map<String, Object>> obtenerObservacionesRecientes(String discriminante, int idUpm) {
        ArrayList<Map<String, Object>> list = new ArrayList<>();
        String query =  "SELECT o.id_observacion, eto.id_tipo_obs, eto.descripcion, i.id_asignacion, i.correlativo, i.codigo, o.observacion, o.usucre, o.feccre\n" +
                "FROM enc_observacion o, cat_tipo_obs eto, enc_informante i\n" +
                "WHERE o.id_tipo_obs = eto.id_tipo_obs\n" +
                "AND i.id_asignacion = o.id_asignacion\n" +
                "AND i.correlativo = o.correlativo\n" +
                "AND i.id_nivel = 3 AND i.estado <> 'ANULADO' AND i.id_upm IN ("+ idUpm +")\n" +
                "AND eto.id_tipo_obs NOT IN ("+discriminante+")\n" +
                "AND o.feccre = (SELECT MAX(feccre) FROM enc_observacion WHERE id_asignacion = i.id_asignacion AND correlativo = i.correlativo)\n" +
                "ORDER BY i.codigo";
        Cursor cursor = conn.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                Map<String, Object> map = new LinkedHashMap<>();
                map.put("id_observacion", cursor.getInt(0));
                map.put("id_tipo_obs", cursor.getInt(1));
                map.put("descripcion", cursor.getString(2));
                map.put("id_asignacion", cursor.getInt(3));
                map.put("correlativo", cursor.getInt(4));
                map.put("codigo", cursor.getString(5));
                map.put("observacion", cursor.getString(6)==null? "":cursor.getString(6));
                map.put("usucre", cursor.getString(7));
                map.put("feccre", Movil.dateExtractor(cursor.getLong(8)));
                map.put("observacion_historial", preparaHistorial(new IdInformante(cursor.getInt(3), cursor.getInt(4))));
                list.add(map);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    public ArrayList<Map<String, Object>> obtenerObservacionesRecientesPrueba(String discriminante) {
        ArrayList<Map<String, Object>> list = new ArrayList<>();

        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id_observacion", 1);
        map.put("id_tipo_obs", 1);
        map.put("descripcion", "descripcion ejemplo");
        map.put("id_asignacion", 1);
        map.put("correlativo", 2);
        map.put("codigo", "111-1111111111-A");
        map.put("observacion","observacion ejemplo");
        map.put("usucre", "");
        map.put("feccre", 1602460800);
        map.put("observacion_historial", "observacion historial");
        list.add(map);

        return list;
    }

    public static ArrayList<Map<String, Object>> obtenerHistorialSuper(IdInformante idInformante) {
        ArrayList<Map<String, Object>> res = new ArrayList<>();
        Cursor cursor = null;
        try {
            String query = "SELECT o.id_observacion, o.id_tipo_obs, co.descripcion, o.observacion, o.id_pregunta, o.usucre, o.feccre\n" +
                    "FROM enc_observacion o, cat_tipo_obs co, enc_informante i\n" +
                    "WHERE o.estado LIKE 'ELABORADO'\n" +
                    "AND o.id_tipo_obs = co.id_tipo_obs\n" +
                    "AND i.id_asignacion = "+idInformante.id_asignacion+" AND i.correlativo = "+idInformante.correlativo+"\n" +
                    "AND ((o.id_asignacion = i.id_asignacion AND o.correlativo = i.correlativo) OR (o.id_asignacion_hijo = i.id_asignacion AND o.correlativo_hijo = i.correlativo))\n" +
                    "AND i.estado <> 'ANULADO'\n" +
                    "ORDER BY o.feccre";
            cursor = conn.rawQuery(query, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (cursor.moveToFirst()) {
            do {
                Map<String, Object> row = new LinkedHashMap<>();
                row.put("id_observacion", cursor.getInt(0));
                row.put("id_tipo_obs", cursor.getInt(1));
                row.put("descripcion", cursor.getString(2));
                row.put("observacion", cursor.getString(3)==null? "":cursor.getString(3));
                row.put("id_pregunta", cursor.getInt(4));
                row.put("usucre", cursor.getString(5));
                row.put("feccre", Movil.dateExtractor(cursor.getLong(6)));
                res.add(row);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return res;
    }

     public static ArrayList<Map<String, Object>> obtenerHistorial(IdInformante idInformante) {
        ArrayList<Map<String, Object>> res = new ArrayList<>();
         Cursor cursor = null;
         try {
             String query = "SELECT o.id_observacion, o.id_tipo_obs, co.descripcion, o.observacion, o.id_pregunta, o.usucre, o.feccre,COALESCE((SELECT respuesta FROM enc_encuesta WHERE enc_encuesta.id_asignacion=o.id_asignacion_hijo AND enc_encuesta.correlativo=o.correlativo_hijo AND id_pregunta="+ Parametros.ID_PREG_PERSONAS +"),'')\n" +
                     "FROM enc_observacion o, cat_tipo_obs co, enc_informante i\n" +
                     "WHERE o.estado LIKE 'ELABORADO'\n" +
                     "AND o.id_tipo_obs = co.id_tipo_obs\n" +
                     "AND i.id_asignacion = "+idInformante.id_asignacion+" AND i.correlativo = "+idInformante.correlativo+"\n" +
                     "AND ((o.id_asignacion = i.id_asignacion AND o.correlativo = i.correlativo) OR (o.id_asignacion_hijo = i.id_asignacion AND o.correlativo_hijo = i.correlativo))\n" +
                     "AND i.estado <> 'ANULADO'\n" +
                     "ORDER BY o.feccre";
             cursor = conn.rawQuery(query, null);
         } catch (Exception e) {
             e.printStackTrace();
         }
         if (cursor.moveToFirst()) {
            do {
                Map<String, Object> row = new LinkedHashMap<>();
                row.put("id_observacion", cursor.getInt(0));
                row.put("id_tipo_obs", cursor.getInt(1));
                row.put("descripcion", cursor.getString(2)+((cursor.getString(7).equals(""))?"":("-"+cursor.getString(7))));
                row.put("observacion", cursor.getString(3)==null? "":cursor.getString(3));
                row.put("id_pregunta", cursor.getInt(4));
                row.put("usucre", cursor.getString(5));
                row.put("feccre", Movil.dateExtractor(cursor.getLong(6)));
                res.add(row);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return res;
    }

    public static ArrayList<Map<String, Object>> obtenerDatosTabla(IdInformante idInformante) {
        ArrayList<Map<String, Object>> res = new ArrayList<>();
        Cursor cursor = null;
        try {
            String query = "SELECT  CASE WHEN o.id_asignacion_hijo<>0 THEN  COALESCE((SELECT respuesta FROM enc_encuesta WHERE enc_encuesta.id_asignacion=o.id_asignacion_hijo AND enc_encuesta.correlativo=o.correlativo_hijo AND id_pregunta=18581),'') ELSE i.codigo END codigo_boleta,UPPER(preg.codigo_pregunta),  o.observacion AS observacion_monitoreo, o.respuesta respuesta_observada/*,COALESCE(justificacion,''), COALESCE(e.codigo_respuesta,'') codigo_respuesta*/,COALESCE(e.respuesta,'') respuesta, COALESCE(e.observacion,'') justificacion, CASE WHEN (TRIM(o.respuesta)<>TRIM(COALESCE(e.respuesta,'')) OR TRIM(COALESCE(e.observacion,''))<>'') THEN 'SI' ELSE 'NO' END CORREGIDO,\n" +
                    " CASE WHEN (select count(*)>0 and ee.visible like 'f%'\n" +
                    "FROM (SELECT enc_encuesta.* FROM ENC_ENCUESTA WHERE id_asignacion=i.id_asignacion and correlativo =i.correlativo AND id_pregunta=o.id_pregunta) ee JOIN enc_pregunta pp ON ee.id_pregunta=pp.id_pregunta\n" +
                    "WHERE exists(\n" +
                    "select p.codigo_pregunta FROM \n" +
                    "(SELECT id_pregunta FROM ENC_ENCUESTA WHERE id_asignacion=ee.id_asignacion and correlativo =ee.correlativo  AND visible like 't%') e1 JOIN enc_pregunta p on e1.id_pregunta=p.id_pregunta\n" +
                    "where p.codigo_pregunta>pp.codigo_pregunta\n" +
                    ")) THEN 'SI' ELSE 'NO' END solapado\n" +
                    "                     FROM enc_observacion o join  cat_tipo_obs co ON o.id_tipo_obs = co.id_tipo_obs JOIN enc_informante i ON  ((CASE WHEN o.id_asignacion_hijo<>0 THEN o.id_asignacion_hijo ELSE o.id_asignacion END = i.id_asignacion AND CASE WHEN o.id_asignacion_hijo<>0 THEN o.correlativo_hijo ELSE o.correlativo END = i.correlativo) OR (o.id_asignacion_hijo = i.id_asignacion AND o.correlativo_hijo = i.correlativo)) AND o.id_asignacion="+idInformante.id_asignacion+" and o.correlativo="+idInformante.correlativo+" JOIN enc_encuesta  e ON (CASE WHEN o.id_asignacion_hijo<> 0 THEN o.id_asignacion_hijo ELSE o.id_asignacion END )=e.id_asignacion AND (CASE WHEN o.correlativo_hijo<> 0 THEN o.correlativo_hijo ELSE o.correlativo END )=e.correlativo AND e.id_pregunta=o.id_pregunta JOIN enc_pregunta preg ON preg.id_pregunta=o.id_pregunta\n" +
                    "                     WHERE o.estado LIKE 'ELABORADO'\n" +
                    "                     AND i.estado <> 'ANULADO'\n" +
                    "                     ORDER BY i.id_nivel,CASE WHEN i.id_nivel=3 THEN 0 ELSE CAST(i.codigo as INT) END,preg.codigo_pregunta";
            cursor = conn.rawQuery(query, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (cursor.moveToFirst()) {
            do {
                Map<String, Object> row = new LinkedHashMap<>();
                row.put("boleta", cursor.getString(0));
                row.put("codigo_pregunta", cursor.getString(1));
                row.put("observacion_monitoreo", cursor.getString(2)+((cursor.getString(7).equals(""))?"":("-"+cursor.getString(7))));
                row.put("respuesta_observada", cursor.getString(3)==null? "":cursor.getString(3));
                row.put("respuesta_actual", cursor.getString(4));
                row.put("justificacion", cursor.getString(5));
                row.put("corregido", cursor.getString(6));
                row.put("bajo_salto", cursor.getString(7));
                res.add(row);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return res;
    }

    public static ArrayList<Map<String, Object>> obtenerDatosTablaVariablesDinamicas(IdInformante idInformante) {
        ArrayList<Map<String, Object>> res = new ArrayList<>();
        Cursor cursor = null;
        try {
            String query = "\n" +
                    "WITH A AS(\n" +
                    "SELECT codigo, (SELECT respuesta FROM ENC_ENCUESTA WHERE id_asignacion=i.id_asignacion AND correlativo=i.correlativo AND id_pregunta=18581 AND visible like 't%') nombre,\n" +
                    "(SELECT respuesta FROM ENC_ENCUESTA WHERE id_asignacion=i.id_asignacion AND correlativo=i.correlativo AND id_pregunta=18172 AND  visible like 't%') genero,\n" +
                    "(SELECT respuesta FROM ENC_ENCUESTA WHERE id_asignacion=i.id_asignacion AND correlativo=i.correlativo AND id_pregunta=18173 AND  visible like 't%') edad,\n" +
                    "(SELECT respuesta FROM ENC_ENCUESTA WHERE id_asignacion=i.id_asignacion AND correlativo=i.correlativo AND id_pregunta=18175 AND  visible like 't%') parentesco,\n" +
                    "(SELECT respuesta FROM ENC_ENCUESTA WHERE id_asignacion=i.id_asignacion AND correlativo=i.correlativo AND id_pregunta=18176 AND  visible like 't%') esposo,\n" +
                    "(SELECT codigo_respuesta FROM ENC_ENCUESTA WHERE id_asignacion=i.id_asignacion AND correlativo=i.correlativo AND id_pregunta=18176 AND  visible like 't%') codigo_esposo,\n" +
                    "(SELECT respuesta FROM ENC_ENCUESTA WHERE id_asignacion=i.id_asignacion AND correlativo=i.correlativo AND id_pregunta=18177 AND  visible like 't%') padre_padrastro,\n" +
                    "(SELECT codigo_respuesta FROM ENC_ENCUESTA WHERE id_asignacion=i.id_asignacion AND correlativo=i.correlativo AND id_pregunta=18177 AND  visible like 't%') codigo_padre_padrastro,\n" +
                    "(SELECT respuesta FROM ENC_ENCUESTA WHERE id_asignacion=i.id_asignacion AND correlativo=i.correlativo AND id_pregunta=18178 AND  visible like 't%') madre_madrastra,\n" +
                    "(SELECT codigo_respuesta FROM ENC_ENCUESTA WHERE id_asignacion=i.id_asignacion AND correlativo=i.correlativo AND id_pregunta=18178 AND  visible like 't%') codigo_madre_madrastra,\n" +
                    "\n" +
                    "(SELECT respuesta FROM ENC_ENCUESTA WHERE id_asignacion=i.id_asignacion AND correlativo=i.correlativo AND id_pregunta=18504 AND  visible like 't%') encargado_compra,\n" +
                    "(SELECT codigo_respuesta FROM ENC_ENCUESTA WHERE id_asignacion=i.id_asignacion AND correlativo=i.correlativo AND id_pregunta=18504 AND  visible like 't%') codigo_encargado_compra,\n" +
                    "(SELECT respuesta FROM ENC_ENCUESTA WHERE id_asignacion=i.id_asignacion AND correlativo=i.correlativo AND id_pregunta=36557 AND  visible like 't%') kish_uno,\n" +
                    "(SELECT codigo_respuesta FROM ENC_ENCUESTA WHERE id_asignacion=i.id_asignacion AND correlativo=i.correlativo AND id_pregunta=36557 AND  visible like 't%') codigo_kish_uno,\n" +
                    "(SELECT respuesta FROM ENC_ENCUESTA WHERE id_asignacion=i.id_asignacion AND correlativo=i.correlativo AND id_pregunta=36831 AND  visible like 't%') kish_dos,\n" +
                    "(SELECT codigo_respuesta FROM ENC_ENCUESTA WHERE id_asignacion=i.id_asignacion AND correlativo=i.correlativo AND id_pregunta=36831 AND  visible like 't%') codigo_kish_dos,\n" +
                    "(SELECT respuesta FROM ENC_ENCUESTA WHERE id_asignacion=i.id_asignacion AND correlativo=i.correlativo AND id_pregunta=36947 AND  visible like 't%') persona_cuidado\n" +
                    "FROM enc_informante i\n" +
                    "WHERE id_asignacion="+idInformante.id_asignacion+" \n" +
                    "AND COALESCE(correlativo_padre,correlativo)="+idInformante.correlativo+" \n" +
                    "AND estado<>'ANULADO' \n" +
                    "AND id_nivel IN (3,4)\n" +
                    "ORDER BY CASE WHEN id_nivel =3 THEN 1000 ELSE CAST(codigo AS INT) END\n" +
                    "), AA AS(SELECT * FROM A)\n" +
                    " SELECT COALESCE(codigo,''), COALESCE(nombre,''), COALESCE(genero,''), COALESCE(edad,''), \n" +
                    " COALESCE(parentesco,''),\n" +
                    "  CASE WHEN esposo='0' OR esposo IS NULL THEN '' ELSE esposo END esposo_esposa,\n" +
                    "      CASE WHEN padre_padrastro='0' OR padre_padrastro IS NULL THEN '' ELSE padre_padrastro END padre_padrastro, \n" +
                    "      CASE WHEN madre_madrastra='0' OR madre_madrastra IS NULL THEN '' ELSE madre_madrastra END madre_madrastra,\n" +
                    "      COALESCE(persona_cuidado,'') persona_que_cuido,\n" +
                    "      CASE WHEN encargado_compra IS NULL THEN '' ELSE encargado_compra END encargado_compra,\n" +
                    "      CASE WHEN kish_uno IS NULL THEN '' ELSE kish_uno END  kish_1,\n" +
                    "      CASE WHEN kish_dos IS NULL THEN '' ELSE kish_dos END  kish_2            \n" +
                    "  \n" +
                    "FROM AA";
            cursor = conn.rawQuery(query, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (cursor.moveToFirst()) {
            do {
                Map<String, Object> row = new LinkedHashMap<>();
                row.put("codigo", cursor.getString(0));
                row.put("nombre", cursor.getString(1));
                row.put("genero", cursor.getString(2));
                row.put("edad", cursor.getString(3));
                row.put("parentesco", cursor.getString(4));
                row.put("esposo_esposa", cursor.getString(5));
                row.put("padre_padrastro", cursor.getString(6));
                row.put("madre_madrastra", cursor.getString(7));
//                row.put("persona_que_cuido", cursor.getString(8));
//                row.put("encargado_compras", cursor.getString(9));
//                row.put("kish_1", cursor.getString(10));
//                row.put("kish_2", cursor.getString(11));
                res.add(row);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return res;
    }

    public static String preparaHistorial (IdInformante idInformante) {
        ArrayList<Map<String, Object>> obsData;

        obsData= Observacion.obtenerHistorial(idInformante);
        StringBuilder str = new StringBuilder();
        for (Map<String, Object> map : obsData) {
            String color = "";
            switch ((Integer )map.get("id_tipo_obs")){
                case 1:
                    color = "#c9b416"; //WARNING COLOR
                    break;
                case 8:
                    color = "#50ac27"; //SUCCESS COLOR
                    break;
                default:
                    color = "#e20017"; //ERROR COLOR
                    break;
            }
            str.append("<font color="+color+">");
            str.append("<b>").append("("+map.get("descripcion")+")").append("</b><br>");
            str.append("Obs.: ").append(map.get("observacion")).append("<br>");
            if((Integer)map.get("id_pregunta") != 0) {
                str.append("Preg.: ").append(Pregunta.getPregunta((Integer)map.get("id_pregunta")));
            }
            str.append("<i>Creado por: "+ map.get("usucre")).append(" ("+ map.get("feccre") +")").append("</i><br><br>");
            str.append("</font>");
        }
        return str.toString();
    }

    public static int countObs(int idAsignacion, String discriminante) {
        String query =  "SELECT COUNT(*)\n" +
                "FROM enc_observacion o, cat_tipo_obs eto, enc_informante i\n" +
                "WHERE o.id_tipo_obs = eto.id_tipo_obs\n" +
                "AND i.id_asignacion = o.id_asignacion\n" +
                "AND i.correlativo = o.correlativo\n" +
                "AND i.id_nivel = 3 AND i.estado <> 'ANULADO' AND i.id_upm IN (SELECT id_upm FROM cat_upm)\n" +
                "AND eto.id_tipo_obs NOT IN ("+discriminante+")\n" +
                "AND o.feccre = (SELECT MAX(feccre) FROM enc_observacion WHERE id_asignacion = i.id_asignacion AND correlativo = i.correlativo)\n" +
                "ORDER BY i.codigo";
        Cursor cursor = conn.rawQuery(query, null);
        int res = 0;
        if (cursor.moveToFirst()) {
            res = cursor.getInt(0);
        }
        cursor.close();
        return res;
    }

    @SuppressWarnings("unused")
    public Integer get_id_observacion() {
        return filaActual.getInt(filaActual.getColumnIndex("id_observacion"));
    }
    @SuppressWarnings("unused")
    public void set_id_observacion(Integer value) {
        filaNueva.put("id_observacion", value);
    }
    @SuppressWarnings("unused")
    public Integer get_id_tipo_obs() {
        return filaActual.getInt(filaActual.getColumnIndex("id_tipo_obs"));
    }
    @SuppressWarnings("unused")
    public void set_id_tipo_obs(Integer value) {
        filaNueva.put("id_tipo_obs", value);
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
    public String get_apiestado() {
        return filaActual.getString(filaActual.getColumnIndex("estado"));
    }
    @SuppressWarnings("unused")
    public void set_apiestado(String value) {
        filaNueva.put("estado", value);
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
    public String get_observacion() {
        return filaActual.getString(filaActual.getColumnIndex("observacion"));
    }
    @SuppressWarnings("unused")
    public void set_observacion(String value) {
        filaNueva.put("observacion", value);
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
