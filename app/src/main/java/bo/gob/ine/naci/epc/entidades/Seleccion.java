package bo.gob.ine.naci.epc.entidades;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import bo.gob.ine.naci.epc.herramientas.Parametros;

/**
 * Created by INE.
 */
//EN CSV
public class Seleccion extends Entidad {
    public Seleccion() {
        super("cat_seleccion");
    }

    public static boolean deshacer(int idUpm) {
        int idPregunta = Pregunta.getIDpregunta(169, TipoPregunta.NumeroVivienda);
        ContentValues paquete = new ContentValues();
        paquete.put("respuesta", 1);
        conn.update("enc_encuesta", paquete, "id_asignacion = ? AND id_pregunta = ? AND (codigo_respuesta > 0 AND codigo_respuesta < 997)", new String[]{String.valueOf(Asignacion.get_asignacion(idUpm, Usuario.usuario)), String.valueOf(idPregunta)});

        boolean res = false;
        String query = "UPDATE enc_informante\n" +
                "SET estado = 'ELABORADO',\n" +
                "descripcion = (SELECT group_concat(e.respuesta, ' ')\n" +
                "    FROM enc_encuesta e, enc_pregunta p\n" +
                "    WHERE e.id_pregunta = p.id_pregunta\n" +
                "    AND e.id_asignacion = enc_informante.id_asignacion\n" +
                "    AND e.correlativo = enc_informante.correlativo)\n" +
                "WHERE id_nivel = 2\n" +
                "AND estado <> 'ANULADO'\n" +
                "AND id_upm = " + idUpm;
        Cursor cursor = conn.rawQuery(query, null);
        if (cursor.moveToNext()) {
            res = cursor.getInt(0) > 0;
        }
        cursor.close();
        Log.d("seleccionado", String.valueOf(res));
        return res;
    }

    public static int obtenerNroViviendas() {
        String query = "SELECT MIN(nro_viviendas)\n" +
                "FROM cat_seleccion\n" +
                "LIMIT 1";
        int nroViviendas = 0;
        Cursor cursor = conn.rawQuery(query, null);
        if (cursor.moveToNext()) {
            nroViviendas = cursor.getInt(0);
        }
        cursor.close();
        return nroViviendas;
    }
    public static int obtenerNroViviendasNoOmitidas(int idUpm) {
        int resp=0;
        int idPreguntaVoe=Pregunta.getIdPregunta(Parametros.CODIGO_PREGUNTA_NRO_ORDEN_VIVIENDA);
        int idPreguntaOmitidas = Pregunta.getIdPregunta(Parametros.CODIGO_PREGUNTA_VIVIENDA_OMITIDA);
        String query = "SELECT i.id_asignacion, i.correlativo, e1.id_asignacion,e1.correlativo, e1.id_pregunta, e2.codigo_respuesta, e1.codigo_respuesta\n" +
                "                                FROM enc_informante i LEFT JOIN  enc_encuesta e1 ON   i.id_asignacion IN (SELECT ope_asignacion.id_asignacion FROM ope_asignacion WHERE id_upm = "+idUpm+")  AND i.id_asignacion = e1.id_asignacion AND i.correlativo=e1.correlativo AND e1.id_pregunta = "+idPreguntaVoe+" AND e1.visible like 't%'\n" +
                "                                LEFT JOIN enc_encuesta e2 ON i.id_asignacion = e2.id_asignacion AND i.correlativo=e2.correlativo AND e2.id_pregunta ="+idPreguntaOmitidas+" AND e2.visible like 't%'\n" +
                "                                WHERE CAST(e1.respuesta AS Int) > 0\n" +
                "                                AND I.id_upm = "+idUpm+" \n" +
                "                                AND (CAST(e2.codigo_respuesta AS Int) != 1 OR e2.codigo_respuesta IS NULL )\n" +
                "                                AND i.estado <> 'ANULADO'\n" +
                "                                ORDER BY CAST(codigo AS Int)";
        try {
            Cursor cursor = conn.rawQuery(query, null);
            resp= cursor.getCount();
            cursor.close();

        }catch (Exception e){

        }
        return resp;
    }


    public static Set<Integer> obtenerSeleccionViviendasNoOmitidas(int idUpm) {
        Set<Integer> res = new TreeSet<>();
        int resp=0;
        int idPreguntaVoe=Pregunta.getIdPregunta(Parametros.CODIGO_PREGUNTA_NRO_ORDEN_VIVIENDA);
        int idPreguntaOmitidas = Pregunta.getIdPregunta(Parametros.CODIGO_PREGUNTA_VIVIENDA_OMITIDA);
//        String query = "SELECT i.id_asignacion, i.correlativo, e1.id_asignacion,e1.correlativo, e1.id_pregunta, e2.codigo_respuesta, e1.codigo_respuesta\n" +
//                "                                FROM enc_informante i, enc_encuesta e1,enc_encuesta e2\n" +
//                "                                WHERE i.id_asignacion = e1.id_asignacion AND i.correlativo=e1.correlativo AND i.id_asignacion = e2.id_asignacion AND i.correlativo=e2.correlativo\n" +
//                "                                AND e1.id_pregunta = "+idPreguntaVoe+"\n" +
//                "                AND e2.id_pregunta ="+idPreguntaOmitidas+"\n" +
//                "                                AND CAST(e1.respuesta AS Int) > 0\n" +
//                "                AND CAST(e2.codigo_respuesta AS Int) != 1\n" +
//                "                                AND i.id_asignacion IN (SELECT ope_asignacion.id_asignacion FROM ope_asignacion WHERE id_upm = "+idUpm+")\n" +
//                "                                AND i.estado <> 'ANULADO'\n" +
//                "                                ORDER BY CAST(codigo AS Int)";
                                                String query = "SELECT i.id_asignacion, i.correlativo, e1.id_asignacion,e1.correlativo, e1.id_pregunta, e2.codigo_respuesta, e1.codigo_respuesta\n" +
                                                        "                                FROM enc_informante i LEFT JOIN  enc_encuesta e1 ON   i.id_asignacion IN (SELECT ope_asignacion.id_asignacion FROM ope_asignacion WHERE id_upm = "+idUpm+")  AND i.id_asignacion = e1.id_asignacion AND i.correlativo=e1.correlativo AND e1.id_pregunta = "+idPreguntaVoe+" AND e1.visible like 't%'\n" +
                                                        "                                LEFT JOIN enc_encuesta e2 ON i.id_asignacion = e2.id_asignacion AND i.correlativo=e2.correlativo AND e2.id_pregunta ="+idPreguntaOmitidas+" AND e2.visible like 't%'\n" +
                                                        "                                WHERE CAST(e1.respuesta AS Int) > 0\n" +
                                                        "                                AND I.id_upm = "+idUpm+" \n" +
                                                        "                                AND (CAST(e2.codigo_respuesta AS Int) != 1 OR e2.codigo_respuesta IS NULL )\n" +
                                                        "                                AND i.estado <> 'ANULADO'\n" +
                                                        "                                ORDER BY CAST(codigo AS Int)";
        try {
            Cursor cursor = conn.rawQuery(query, null);
            while (cursor.moveToNext()){
                res.add(cursor.getInt(6));
            }
            cursor.close();
        }catch (Exception e){

        }
        return res;
    }

    public static String obtenerNroViviendasOmitidas(int idUpm) {
        String resp="";
        int idPreguntaOmitidas = Pregunta.getIdPregunta(Parametros.CODIGO_PREGUNTA_VIVIENDA_OMITIDA);
        int idPregunta = Pregunta.getIdPregunta(Parametros.CODIGO_PREGUNTA_NRO_ORDEN_VIVIENDA);
        List<String> rlist=new ArrayList<>();
        String query = "SELECT  e1.respuesta\n" +
                "FROM enc_informante i JOIN enc_encuesta e \n" +
                "ON i.id_asignacion=e.id_asignacion AND i.correlativo=e.correlativo AND e.id_pregunta= "+idPreguntaOmitidas+" AND i.id_upm="+idUpm+"\n" +
                "JOIN enc_encuesta e1 ON i.id_asignacion=e1.id_asignacion AND i.correlativo=e1.correlativo AND e1.id_pregunta= "+idPregunta+" AND i.id_upm="+idUpm+"\n" +
                "WHERE CAST(e.codigo_respuesta AS INT)=1 AND i.estado<>'ANULADO' AND e.visible LIKE 't%'\n" +
                "ORDER BY CAST(e1.respuesta AS Int)";
        try {
            Cursor cursor = conn.rawQuery(query, null);
            while (cursor.moveToNext()){
                resp=resp.length()==0?cursor.getString(0):resp+","+cursor.getString(0);
            }

        }catch (Exception e){
            e.printStackTrace();
        }finally {
            return resp;
        }

    }

    public static boolean hasSelected(int idUpm) {
        boolean res = false;
        String query = "SELECT count(*)\n" +
                "FROM enc_informante\n" +
                "WHERE id_nivel = 2\n" +
                "AND estado LIKE 'SELECCIONADO%'\n" +
                "AND id_upm = " + idUpm;
        Log.d("SELECCION: ",query);
        Cursor cursor = conn.rawQuery(query, null);
        if (cursor.moveToNext()) {
            res = cursor.getInt(0) > 0;
        }
        cursor.close();
        return res;
    }

    /*public static Set<Integer> seleccion(int urbano, int total) {
        Set<Integer> res = new TreeSet<>();
        String query = "SELECT viv01, viv02, viv03, viv04, viv05, viv06, viv07, viv08, viv09, viv10, viv11, viv12, viv13, viv14, viv15, viv16\n" +
                "FROM cat_seleccion\n" +
                "WHERE urbano = " + urbano + "\n" +
                "AND nro_viviendas = " + total;
        Cursor cursor = conn.rawQuery(query, null);
        if (cursor.moveToNext()) {


            for (int i = 0; i < 16; i++) {
                res.add(cursor.getInt(i));
            }
        }
        cursor.close();
        return res;
    }*/

    public static Set<Integer> seleccion(int urbano, int total) {
        Set<Integer> res = new TreeSet<>();
        String query = "SELECT viv01, viv02, viv03, viv04, viv05, viv06, viv07, viv08, viv09, viv10, viv11, viv12\n" +
                "FROM cat_seleccion\n" +
                "WHERE urbano = " + urbano + "\n" +
                "AND nro_viviendas = " + total;
        Cursor cursor = conn.rawQuery(query, null);
        if (cursor.moveToNext()) {
            for (int i = 0; i < 12; i++) {
                res.add(cursor.getInt(i));
            }
        }
        cursor.close();
        return res;
    }
    public static Set<Integer> seleccionRemplazo(int urbano, int total, Map<Integer,Integer> voeRemplazo) {
        Set<Integer> res = new TreeSet<>();
        String query = "SELECT viv01, viv02, viv03, viv04, viv05, viv06, viv07, viv08, viv09, viv10, viv11, viv12\n" +
                "FROM cat_seleccion\n" +
                "WHERE urbano = " + urbano + "\n" +
                "AND nro_viviendas = " + total;
        Cursor cursor = conn.rawQuery(query, null);
        if (cursor.moveToNext()) {
            for (int i = 0; i < 12; i++) {
                int temp=voeRemplazo.get(cursor.getInt(i))==null?-1:voeRemplazo.get(cursor.getInt(i));
                if(temp>0)
                    res.add(temp);
                else
                    res.add(cursor.getInt(i));
            }
        }
        cursor.close();
        return res;
    }


    public static Map<Integer,Integer> getVoeReemplazo(int idUpm) {
        Map<Integer,Integer> res = new HashMap();
        String query = "SELECT voe_seleccionada,voe_remplazo\n" +
                "FROM ope_voes_remplazo\n" +
                "WHERE id_upm = " + idUpm + "\n" ;
        Cursor cursor = conn.rawQuery(query, null);
        while (cursor.moveToNext()){
            res.put(cursor.getInt(0),cursor.getInt(1));
        }
        cursor.close();
        return res;
    }

//    public static void preSorteo(int idUpm) {
//
//        String query = "select ROW_NUMBER() OVER(order by ee.codigo_respuesta) AS rowNum, \n" +
//                "ee.id_asignacion, \n" +
//                "ee.correlativo, \n" +
//                "ei.id_usuario \n" +
//                "from enc_informante ei \n" +
//                "join enc_encuesta ee on ei.id_asignacion = ee.id_asignacion and ei.correlativo = ee.correlativo \n" +
//                "join enc_encuesta ee1 on ei.id_asignacion = ee1.id_asignacion and ei.correlativo = ee1.correlativo \n" +
//                "join enc_encuesta ee2 on ei.id_asignacion = ee2.id_asignacion and ei.correlativo = ee2.correlativo \n" +
//                "join enc_pregunta ep on ee.id_pregunta=ep.id_pregunta \n" +
//                "where id_upm = " + idUpm + " \n" +
//                "and ee.id_pregunta = 36834 and ee1.id_pregunta = 18604  and ee2.id_pregunta = 18606 and id_nivel = 2 and ei.estado='ELABORADO' \n" +
//                "order by cast (ee.codigo_respuesta as INT) ,cast (ee1.codigo_respuesta as INT),cast (ee2.codigo_respuesta as INT)";
//        Cursor cursor = conn.rawQuery(query, null);
//        conn.beginTransaction();
//        try {
//            while (cursor.moveToNext()) {
//                ContentValues paquete = new ContentValues();
//                paquete.put("codigo", cursor.getString(0));
//                conn.update("enc_informante", paquete, "id_asignacion = ? AND correlativo = ?", new String[]{cursor.getString(1), cursor.getString(2)});
//            }
//            conn.setTransactionSuccessful();
//            cursor.close();
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        } finally {
//            conn.endTransaction();
//        }
//    }


    public static void sortear(int urbano, int idUpm) {
        int idPregunta = Pregunta.getIdPregunta(Parametros.CODIGO_PREGUNTA_NRO_ORDEN_VIVIENDA);
//        int idPregunta = Pregunta.getIDpregunta(18, TipoPregunta.NumeroVivienda);

        String query = "SELECT i.id_asignacion, i.correlativo\n" +
                "FROM enc_informante i, enc_encuesta e\n" +
                "WHERE i.id_asignacion = e.id_asignacion\n" +
                "AND i.correlativo = e.correlativo\n" +
                "AND e.id_pregunta = " + idPregunta + "\n" +
                "AND CAST(e.respuesta AS Int) > 0\n" +
                "AND i.id_upm = " + idUpm + "\n" +
                "AND i.estado <> 'ANULADO'\n" +
                "ORDER BY CAST(codigo AS Int)";
        Cursor cursor = conn.rawQuery(query, null);
        Set sel = seleccion(urbano, cursor.getCount());
        conn.beginTransaction();
        try {
            int i = 1;
            while (cursor.moveToNext()) {
                ContentValues paquete = new ContentValues();
                paquete.put("respuesta", i);
                conn.update("enc_encuesta", paquete, "id_asignacion = ? AND correlativo = ? AND id_pregunta = ?", new String[]{cursor.getString(0), cursor.getString(1), String.valueOf(idPregunta)});
                if (sel.contains(i)) {
                    paquete.clear();
                    paquete.put("estado", Estado.SELECCIONADO.toString());

                    conn.execSQL("UPDATE enc_informante SET estado = 'SELECCIONADO' WHERE id_asignacion= " + cursor.getString(0)+ " AND correlativo = "+ cursor.getString(1));


//                    int cu = conn.update("enc_informante", paquete, "id_asignacion = ? AND correlativo = ?", new String[]{cursor.getString(0), cursor.getString(1)});


                    String a = cursor.getString(0);
                    String b = cursor.getString(1);
//                    int asv = cu;
                    String aS = cursor.getString(0);
                    String bS = cursor.getString(1);
                }
                if (sel.contains(10000 + i)) {
                    paquete.clear();
                    paquete.put("estado", Estado.SELECCIONADO2.toString());
                    conn.update("enc_informante", paquete, "id_asignacion = ? AND correlativo = ?", new String[]{cursor.getString(0), cursor.getString(1)});
                }
                i++;
            }
//            conn.setTransactionSuccessful();
            cursor.close();
            conn.execSQL("UPDATE enc_informante\n" +
                    //"SET descripcion = (SELECT group_concat(CASE id_tipo_pregunta WHEN 19 THEN '<b><font color=\"blue\">' || respuesta || '</font></b>' ELSE respuesta END, ' ')\n" +
                    "SET descripcion = (SELECT group_concat(e.respuesta, ' ')\n" +
                    "FROM enc_encuesta e, enc_pregunta p\n" +
                    "WHERE e.id_pregunta = p.id_pregunta\n" +
                    "AND e.id_asignacion = enc_informante.id_asignacion\n" +
                    "AND e.correlativo = enc_informante.correlativo)\n" +
                    "WHERE id_nivel = 2\n" +
                    "AND estado <> 'ANULADO'" +
                    "AND id_upm = " + idUpm);
            conn.setTransactionSuccessful();
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            conn.endTransaction();
        }

    }

    public static void sortearOmitidas(int urbano, int idUpm) {
        int idPreguntaUsoVivienda = Pregunta.getIdPregunta(Parametros.CODIGO_PREGUNTA_USO_DE_LA_VIVIENDA);
        int idPreguntaVoe = Pregunta.getIdPregunta(Parametros.CODIGO_PREGUNTA_NRO_ORDEN_VIVIENDA);
        int idRecorridoUpm =Pregunta.getIdPregunta(Parametros.CODIGO_PREGUNTA_UPM_SELECCIONADA);
        int idRecorridoManzana =Pregunta.getIdPregunta(Parametros.CODIGO_PREGUNTA_RECORRIDO_MANZANA);
        int idOrdenPredio=Pregunta.getIdPregunta(Parametros.CODIGO_PREGUNTA_NRO_ORDEN_PREDIO);
        int idOrdenVivienda=Pregunta.getIdPregunta(Parametros.CODIGO_PREGUNTA_NRO_ORDEN_VIV);
        int idPreguntaOmitidas = Pregunta.getIdPregunta(Parametros.CODIGO_PREGUNTA_VIVIENDA_OMITIDA);
        int i, j;
        Set sel=null;
        conn.beginTransaction();
        try {
            conn.execSQL("UPDATE enc_informante\n" +
                    "SET estado = 'ELABORADO'" +"\n" +
                    "WHERE id_upm = " + idUpm+" \n"+
                "AND id_nivel=2 AND estado<>'ANULADO'");
            conn.setTransactionSuccessful();
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            conn.endTransaction();
        }

        //ordenacion de viviendas
        //reordena los numeros de viviendas llevando al ultimo las voes omitidas
        String query2 = "WITH t AS(SELECT i.id_asignacion,i.correlativo,i.codigo, coalesce(cast(e.codigo_respuesta as INT),997) omitida, (SELECT codigo_respuesta FROM enc_encuesta WHERE id_asignacion=i.id_asignacion AND correlativo=i.correlativo AND id_pregunta="+idPreguntaUsoVivienda+" LIMIT 1) uso_vivienda, CASE WHEN id_upm_hijo is not null THEN (SELECT CASE WHEN id_incidencia=2 THEN -1 ELSE nro_registro END FROM cat_upm_hijo WHERE cat_upm_hijo.id_upm_hijo=i.id_upm_hijo LIMIT 1) ELSE -2 END nro_upm " +
                "FROM\n" +
                "(SELECT id_asignacion,correlativo,codigo,estado, id_upm_hijo FROM enc_informante  WHERE id_upm="+idUpm+" AND estado<>'ANULADO' and id_nivel=2 AND id_asignacion in(SELECT id_asignacion FROM ope_asignacion )) i \n" +
                "LEFT JOIN (SELECT id_asignacion,correlativo,codigo_respuesta from enc_encuesta WHERE id_pregunta= "+idPreguntaOmitidas+" AND visible like 't%') e on i.id_asignacion=e.id_asignacion AND i.correlativo=e.correlativo \n" +
                "LEFT JOIN (SELECT id_asignacion,correlativo,codigo_respuesta from enc_encuesta WHERE id_pregunta= "+idRecorridoUpm+" AND visible like 't%') e0 on i.id_asignacion=e0.id_asignacion AND i.correlativo=e0.correlativo \n" +
                "LEFT JOIN (SELECT id_asignacion,correlativo,codigo_respuesta from enc_encuesta WHERE id_pregunta= "+idRecorridoManzana+" AND visible like 't%') e1 on i.id_asignacion=e1.id_asignacion AND i.correlativo=e1.correlativo \n" +
                "LEFT JOIN (SELECT id_asignacion,correlativo,codigo_respuesta from enc_encuesta WHERE id_pregunta= "+idOrdenPredio+" AND visible like 't%') e2 on i.id_asignacion=e2.id_asignacion AND i.correlativo=e2.correlativo \n" +
                "LEFT JOIN (SELECT id_asignacion,correlativo,codigo_respuesta from enc_encuesta WHERE id_pregunta= "+idOrdenVivienda+" AND visible like 't%') e3 on i.id_asignacion=e3.id_asignacion AND i.correlativo=e3.correlativo \n" +
                //criterio de ordenacion
                "ORDER by coalesce(cast(e.codigo_respuesta as INT),997) DESC, nro_upm,CAST(e1.codigo_respuesta as INT),CAST(e2.codigo_respuesta as INT),CAST(e3.codigo_respuesta as INT),CAST(i.codigo as INT))\n" +
                "SELECT id_asignacion,correlativo,codigo,omitida,uso_vivienda,nro_upm, (SELECT count(*) FROM t WHERE t.uso_vivienda='1' AND t.omitida=997) total_viviendas FROM t";
        Cursor cursor = conn.rawQuery(query2, null);


        i = 1;
        j=1;
        conn.beginTransaction();
        try {

            while (cursor.moveToNext()) {
                if(sel==null){
                    sel=seleccion(urbano, Integer.parseInt(cursor.getString(6)));
                }
                ContentValues paquete = new ContentValues();
                paquete.put("codigo", i);
                conn.update("enc_informante", paquete, "id_asignacion = ? AND correlativo = ? AND id_nivel=2", new String[]{cursor.getString(0),cursor.getString(1)});
                i++;
                if(Integer.parseInt(cursor.getString(4))==1){
                    paquete.clear();
                    paquete.put("respuesta", j);
                    paquete.put("codigo_respuesta", j);
                    conn.update("enc_encuesta", paquete, "id_asignacion = ? AND correlativo = ? AND id_pregunta = ?", new String[]{cursor.getString(0), cursor.getString(1),String.valueOf(idPreguntaVoe)});

                    if(Upm.isSorteoEspecial(idUpm)&&Integer.parseInt(cursor.getString(6))<12){
                        paquete.clear();
                        paquete.put("estado", Estado.SELECCIONADO.toString());
                        conn.update("enc_informante", paquete, "id_asignacion = ? AND correlativo = ?", new String[]{cursor.getString(0),cursor.getString(1)});
                    }else {
                        if (sel.contains(j)) {
                            paquete.clear();
                            paquete.put("estado", Estado.SELECCIONADO.toString());
                            conn.update("enc_informante", paquete, "id_asignacion = ? AND correlativo = ?", new String[]{cursor.getString(0),cursor.getString(1)});
                        }else if(Integer.parseInt(cursor.getString(3))==1){
                            paquete.clear();
                            paquete.put("estado", Estado.SELECCIONADO.toString());
                            conn.update("enc_informante", paquete, "id_asignacion = ? AND correlativo = ?", new String[]{cursor.getString(0),cursor.getString(1)});
                        }
                    }
                    j++;
                }
            }
            cursor.close();
            conn.setTransactionSuccessful();
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            conn.endTransaction();
        }
        conn.beginTransaction();
        try {
            conn.execSQL("UPDATE enc_informante\n" +
                    //"SET descripcion = (SELECT group_concat(CASE id_tipo_pregunta WHEN 19 THEN '<b><font color=\"blue\">' || respuesta || '</font></b>' ELSE respuesta END, ' ')\n" +
                    "SET descripcion = (SELECT group_concat(e.respuesta, ' ')\n" +
                    "FROM enc_encuesta e, enc_pregunta p\n" +
                    "WHERE e.id_pregunta = p.id_pregunta\n" +
                    "AND e.id_asignacion = enc_informante.id_asignacion\n" +
                    "AND e.correlativo = enc_informante.correlativo)\n" +
                    "WHERE id_nivel = 2\n" +
                    "AND estado <> 'ANULADO'" +
                    "AND id_upm = " + idUpm);
            conn.setTransactionSuccessful();
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            conn.endTransaction();
        }

    }

    public static String seleccionViviendas(int urbano, int idUpm) {
        int idPreguntaUsoVivienda = Pregunta.getIdPregunta(Parametros.CODIGO_PREGUNTA_USO_DE_LA_VIVIENDA);
        int idPreguntaVoe = Pregunta.getIdPregunta(Parametros.CODIGO_PREGUNTA_NRO_ORDEN_VIVIENDA);
        int idRecorridoUpm =Pregunta.getIdPregunta(Parametros.CODIGO_PREGUNTA_UPM_SELECCIONADA);
        int idRecorridoManzana =Pregunta.getIdPregunta(Parametros.CODIGO_PREGUNTA_RECORRIDO_MANZANA);
        int idOrdenPredio=Pregunta.getIdPregunta(Parametros.CODIGO_PREGUNTA_NRO_ORDEN_PREDIO);
        int idOrdenVivienda=Pregunta.getIdPregunta(Parametros.CODIGO_PREGUNTA_NRO_ORDEN_VIV);
        int idPreguntaOmitidas = Pregunta.getIdPregunta(Parametros.CODIGO_PREGUNTA_VIVIENDA_OMITIDA);
        int idNombreJefeHogar =Pregunta.getIdPregunta(Parametros.CODIGO_PREGUNTA_NOMBRE_JEFE_HOGAR);
        int i, j;
        Set sel=null;

        //ordenacion de viviendas
        //reordena los numeros de viviendas llevando al ultimo las voes omitidas
        String query2 = "WITH t AS(SELECT i.id_asignacion,i.correlativo,i.codigo, coalesce(cast(e.codigo_respuesta as INT),997) omitida, (SELECT codigo_respuesta FROM enc_encuesta WHERE id_asignacion=i.id_asignacion AND correlativo=i.correlativo AND id_pregunta="+idPreguntaUsoVivienda+" LIMIT 1) uso_vivienda, CASE WHEN id_upm_hijo is not null THEN (SELECT CASE WHEN id_incidencia=2 THEN -1 ELSE nro_registro END FROM cat_upm_hijo WHERE cat_upm_hijo.id_upm_hijo=i.id_upm_hijo LIMIT 1) ELSE -2 END nro_upm " +
                ", e4.respuesta nombre FROM\n" +
                "(SELECT id_asignacion,correlativo,codigo,estado, id_upm_hijo FROM enc_informante  WHERE id_upm="+idUpm+" AND estado<>'ANULADO' and id_nivel=2 AND id_asignacion in(SELECT id_asignacion FROM ope_asignacion )) i \n" +
                "LEFT JOIN (SELECT id_asignacion,correlativo,codigo_respuesta from enc_encuesta WHERE id_pregunta= "+idPreguntaOmitidas+" AND visible like 't%') e on i.id_asignacion=e.id_asignacion AND i.correlativo=e.correlativo \n" +
                "LEFT JOIN (SELECT id_asignacion,correlativo,codigo_respuesta from enc_encuesta WHERE id_pregunta= "+idRecorridoUpm+" AND visible like 't%') e0 on i.id_asignacion=e0.id_asignacion AND i.correlativo=e0.correlativo \n" +
                "LEFT JOIN (SELECT id_asignacion,correlativo,codigo_respuesta from enc_encuesta WHERE id_pregunta= "+idRecorridoManzana+" AND visible like 't%') e1 on i.id_asignacion=e1.id_asignacion AND i.correlativo=e1.correlativo \n" +
                "LEFT JOIN (SELECT id_asignacion,correlativo,codigo_respuesta from enc_encuesta WHERE id_pregunta= "+idOrdenPredio+" AND visible like 't%') e2 on i.id_asignacion=e2.id_asignacion AND i.correlativo=e2.correlativo \n" +
                "LEFT JOIN (SELECT id_asignacion,correlativo,codigo_respuesta from enc_encuesta WHERE id_pregunta= "+idOrdenVivienda+" AND visible like 't%') e3 on i.id_asignacion=e3.id_asignacion AND i.correlativo=e3.correlativo \n" +
                "LEFT JOIN (SELECT id_asignacion,correlativo,respuesta from enc_encuesta WHERE id_pregunta= "+idNombreJefeHogar+" AND visible like 't%') e4 on i.id_asignacion=e4.id_asignacion AND i.correlativo=e4.correlativo \n" +
                //criterio de ordenacion
                "ORDER by coalesce(cast(e.codigo_respuesta as INT),997) DESC, nro_upm,CAST(e1.codigo_respuesta as INT),CAST(e2.codigo_respuesta as INT),CAST(e3.codigo_respuesta as INT),CAST(i.codigo as INT))\n" +
                "SELECT id_asignacion,correlativo,codigo,omitida,uso_vivienda,nro_upm, (SELECT count(*) FROM t WHERE t.uso_vivienda='1' AND t.omitida=997) total_viviendas, nombre  FROM t";
        Log.d("PRESELECCION", query2);
        Cursor cursor = conn.rawQuery(query2, null);

        i = 1;
        j=1;
        StringBuilder strPreguntasRespuestasIniciales = new StringBuilder();
        strPreguntasRespuestasIniciales.append("<b><font color=#0C407A>" + "COD" + "</font></b>").append(": ").append("NOMBRE JEFE/A DEL HOGAR SELECCIONADO").append("<br>");
        conn.beginTransaction();
        try {

            while (cursor.moveToNext()) {
                if(sel==null){
                    sel=seleccion(urbano, Integer.parseInt(cursor.getString(6)));
                }
                ContentValues paquete = new ContentValues();
//                paquete.put("codigo", i);
//                conn.update("enc_informante", paquete, "id_asignacion = ? AND correlativo = ?", new String[]{cursor.getString(0),cursor.getString(1)});

                if(Integer.parseInt(cursor.getString(4))==1){
                    paquete.clear();
//                    paquete.put("respuesta", j);
//                    paquete.put("codigo_respuesta", j);
//                    conn.update("enc_encuesta", paquete, "id_asignacion = ? AND correlativo = ? AND id_pregunta = ?", new String[]{cursor.getString(0), cursor.getString(1),String.valueOf(idPreguntaVoe)});
                    if (sel.contains(j)) {
                        strPreguntasRespuestasIniciales.append("<b><font color=#0C407A>" +j + "</font></b>").append(" : ").append(cursor.getString(7)).append("<br>");
//                        paquete.clear();
//                        paquete.put("estado", Estado.SELECCIONADO.toString());
//                        conn.update("enc_informante", paquete, "id_asignacion = ? AND correlativo = ?", new String[]{cursor.getString(0),cursor.getString(1)});

                    }else if(Integer.parseInt(cursor.getString(3))==1){
//                        paquete.clear();
//                        paquete.put("estado", Estado.SELECCIONADO.toString());
//                        conn.update("enc_informante", paquete, "id_asignacion = ? AND correlativo = ?", new String[]{cursor.getString(0),cursor.getString(1)});

                    }
                    j++;
                }
                i++;
            }
            cursor.close();
            conn.setTransactionSuccessful();
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            conn.endTransaction();
        }
       return strPreguntasRespuestasIniciales.toString();


    }

    public static void ordenarLV(int idUpm) {
        int idPreguntaUsoVivienda = Pregunta.getIdPregunta(Parametros.CODIGO_PREGUNTA_USO_DE_LA_VIVIENDA);
        int idPreguntaVoe = Pregunta.getIdPregunta(Parametros.CODIGO_PREGUNTA_NRO_ORDEN_VIVIENDA);
        int idRecorridoUpm =Pregunta.getIdPregunta(Parametros.CODIGO_PREGUNTA_UPM_SELECCIONADA);
        int idRecorridoManzana =Pregunta.getIdPregunta(Parametros.CODIGO_PREGUNTA_RECORRIDO_MANZANA);
        int idOrdenPredio=Pregunta.getIdPregunta(Parametros.CODIGO_PREGUNTA_NRO_ORDEN_PREDIO);
        int idOrdenVivienda=Pregunta.getIdPregunta(Parametros.CODIGO_PREGUNTA_NRO_ORDEN_VIV);
        int idPreguntaOmitidas = Pregunta.getIdPregunta(Parametros.CODIGO_PREGUNTA_VIVIENDA_OMITIDA);
        int i;

        //ordenacion de viviendas
        //reordena los numeros de viviendas llevando al ultimo las voes omitidas
        String query2 = "WITH t AS(SELECT i.id_asignacion,i.correlativo,i.codigo, coalesce(cast(e.codigo_respuesta as INT),997) omitida, (SELECT codigo_respuesta FROM enc_encuesta WHERE id_asignacion=i.id_asignacion AND correlativo=i.correlativo AND id_pregunta="+idPreguntaUsoVivienda+" LIMIT 1) uso_vivienda, CASE WHEN id_upm_hijo is not null THEN (SELECT CASE WHEN id_incidencia=2 THEN -1 ELSE nro_registro END FROM cat_upm_hijo WHERE cat_upm_hijo.id_upm_hijo=i.id_upm_hijo LIMIT 1) ELSE -2 END nro_upm " +
                "FROM\n" +
                "(SELECT id_asignacion,correlativo,codigo,estado, id_upm_hijo FROM enc_informante  WHERE id_upm="+idUpm+" AND estado<>'ANULADO' and id_nivel=2 AND id_asignacion in(SELECT id_asignacion FROM ope_asignacion )) i \n" +
                "LEFT JOIN (SELECT id_asignacion,correlativo,codigo_respuesta from enc_encuesta WHERE id_pregunta= "+idPreguntaOmitidas+" AND visible like 't%') e on i.id_asignacion=e.id_asignacion AND i.correlativo=e.correlativo \n" +
                "LEFT JOIN (SELECT id_asignacion,correlativo,codigo_respuesta from enc_encuesta WHERE id_pregunta= "+idRecorridoUpm+" AND visible like 't%') e0 on i.id_asignacion=e0.id_asignacion AND i.correlativo=e0.correlativo \n" +
                "LEFT JOIN (SELECT id_asignacion,correlativo,codigo_respuesta from enc_encuesta WHERE id_pregunta= "+idRecorridoManzana+" AND visible like 't%') e1 on i.id_asignacion=e1.id_asignacion AND i.correlativo=e1.correlativo \n" +
                "LEFT JOIN (SELECT id_asignacion,correlativo,codigo_respuesta from enc_encuesta WHERE id_pregunta= "+idOrdenPredio+" AND visible like 't%') e2 on i.id_asignacion=e2.id_asignacion AND i.correlativo=e2.correlativo \n" +
                "LEFT JOIN (SELECT id_asignacion,correlativo,codigo_respuesta from enc_encuesta WHERE id_pregunta= "+idOrdenVivienda+" AND visible like 't%') e3 on i.id_asignacion=e3.id_asignacion AND i.correlativo=e3.correlativo \n" +
                //criterio de ordenacion
                "ORDER by coalesce(cast(e.codigo_respuesta as INT),997) DESC, nro_upm,CAST(e1.codigo_respuesta as INT),CAST(e2.codigo_respuesta as INT),CAST(e3.codigo_respuesta as INT),CAST(i.codigo as INT))\n" +
                "SELECT id_asignacion,correlativo,codigo,omitida,uso_vivienda,nro_upm, (SELECT count(*) FROM t WHERE t.uso_vivienda='1' AND t.omitida=997) total_viviendas FROM t";
        Cursor cursor = conn.rawQuery(query2, null);


        i = 1;
        conn.beginTransaction();
        try {

            while (cursor.moveToNext()) {
                ContentValues paquete = new ContentValues();
                paquete.put("codigo", i);
                conn.update("enc_informante", paquete, "id_asignacion = ? AND correlativo = ? AND id_nivel=2", new String[]{cursor.getString(0),cursor.getString(1)});
                i++;
            }
            cursor.close();
            conn.setTransactionSuccessful();
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            conn.endTransaction();
        }


    }

    public static int getNroViviendasObjetoDeEstudio (int idUpm) {
//        int idPregunta = Pregunta.getIDpregunta(169, TipoPregunta.NumeroVivienda);
        int idPregunta = Pregunta.getIdPregunta(Parametros.CODIGO_PREGUNTA_NRO_ORDEN_VIVIENDA);
        String query = "SELECT i.id_asignacion, i.correlativo\n" +
                "FROM enc_informante i, enc_encuesta e\n" +
                "WHERE i.id_asignacion = e.id_asignacion\n" +
                "AND i.correlativo = e.correlativo\n" +
                "AND e.id_pregunta = " + idPregunta + "\n" +
                "AND CAST(e.respuesta AS Int) > 0\n" +
                "AND i.id_upm = " + idUpm + "\n" +
                "AND i.estado <> 'ANULADO'\n" +
                "ORDER BY CAST(codigo AS Int)";
        Cursor cursor = conn.rawQuery(query, null);
        int size = cursor.getCount();
        cursor.close();
        return size;
    }
}