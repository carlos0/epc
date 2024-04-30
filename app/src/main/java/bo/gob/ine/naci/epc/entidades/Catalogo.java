package bo.gob.ine.naci.epc.entidades;

import android.database.Cursor;
import android.util.Log;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Queue;

/**
 * Created by INE.
 */
//EN CSV
public class Catalogo extends EntidadId {
    String catalogo;

    public Catalogo(String catalogo) {
        super("cat_catalogo");
        this.catalogo = catalogo;
    }

    @Override
    public ArrayList<Map<String, Object>> obtenerListado(String filtro) {
        filtro = "catalogo = '" + catalogo + "' " + filtro;
        return super.obtenerListado(filtro);
    }

    public static String getCatUpmManzana(String codigoUpm) {
        String res = null;
        String query = "SELECT seg_unico, orden_manz\n" +
                "FROM a_epc_manzana m\n" +
                "WHERE m.seg_unico = '" + codigoUpm+"'";
        Log.d("TEST", query);
        Cursor cursor = conn.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                res = cursor.getString(0);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return res;
    }

    public static String getCatUpmManzanaDinamico(String codigoUpm) {
        String res = null;
        String query = "SELECT num_upm, manzano\n" +
                "FROM cat_manzanas_comunidad cuc\n" +
                "WHERE cuc.num_upm = '" + codigoUpm+"'";
        Cursor cursor = conn.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                res = cursor.getString(0);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return res;
    }

    public ArrayList<Map<String, Object>> obtenerCatalogoUpmManzana(String numUpm) {
        ArrayList<Map<String, Object>> res = new ArrayList<>();
        try {
            String query = "SELECT num_upm codigo, manzano descripcion\n" +
                    "FROM cat_manzanas_comunidad cmc\n" +
                    "WHERE cmc.num_upm = '"+numUpm+"'\n"+
                    "AND NOT EXISTS(SELECT * FROM cat_upm_hijo WHERE id_upm_padre IN (SELECT ID_UPM FROM cat_upm  where codigo= '"+numUpm+"') AND id_incidencia = 2)\n"+
                    "UNION\n"+
                    "SELECT num_upm codigo, manzano descripcion\n" +
                    "FROM  (SELECT ID_UPM, CODIGO FROM cat_upm  where codigo='" + numUpm+ "') CU\n"+
                    "JOIN CAT_UPM_HIJO CUH ON CU.ID_UPM=CUH.ID_UPM_PADRE\n"+
                    "JOIN CAT_MANZANAS_COMUNIDAD CMC ON CUH.CODIGO=CMC.NUM_UPM\n";

            Cursor cursor = conn.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                do {
                    Map<String, Object> row = new LinkedHashMap<>();
                    row.put("codigo", cursor.getString(0));
                    row.put("descripcion", cursor.getString(1));
                    res.add(row);
                } while (cursor.moveToNext());
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }


//    public ArrayList<Map<String, Object>> obtenerCatalogoUpmManzana(String numUpm,String descartar) {
//        ArrayList<Map<String, Object>> res = new ArrayList<>();
//        try {
//            String query = "SELECT num_upm codigo, manzano descripcion\n" +
//                    "FROM cat_manzanas_comunidad cmc\n" +
//                    "WHERE cmc.num_upm = '"+numUpm+"' AND manzano NOT IN('"+descartar.replace(",","\',\'")+"')\n"+
//                    "UNION\n"+
//                    "SELECT num_upm codigo, manzano descripcion\n" +
//                    "FROM  (SELECT ID_UPM, CODIGO FROM cat_upm  where codigo='" + numUpm+ "') CU\n"+
//                    "JOIN CAT_UPM_HIJO CUH ON CU.ID_UPM=CUH.ID_UPM_PADRE\n"+
//                    "JOIN CAT_MANZANAS_COMUNIDAD CMC ON CUH.CODIGO=CMC.NUM_UPM" +"\n"+
//                    "WHERE manzano NOT IN ('"+descartar.replace(",","\',\'")+"')";
//
//            Cursor cursor = conn.rawQuery(query, null);
//            if (cursor.moveToFirst()) {
//                do {
//                    Map<String, Object> row = new LinkedHashMap<>();
//                    row.put("codigo", cursor.getString(0));
//                    row.put("descripcion", cursor.getString(1));
//                    res.add(row);
//                } while (cursor.moveToNext());
//            }
//            cursor.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return res;
//    }

    public ArrayList<Map<String, Object>> obtenerCatalogoUpmManzanaDinamico(String numUpm) {
        ArrayList<Map<String, Object>> res = new ArrayList<>();
        try {
            String query = "SELECT num_upm codigo, manzano descripcion\n" +
                    "FROM cat_manzanas_comunidad cmc\n" +
                    "WHERE cmc.num_upm = '"+numUpm+"'\n";

            Cursor cursor = conn.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                do {
                    Map<String, Object> row = new LinkedHashMap<>();
                    row.put("codigo", cursor.getString(0));
                    row.put("descripcion", cursor.getString(1));
                    res.add(row);
                } while (cursor.moveToNext());
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    public Map<Integer, String> obtenerCatalogoManzanaDispersa(String numUpm) {
        Map<Integer, String> res = new LinkedHashMap<>();
        int j = 0;
        try {
            String query = "SELECT orden_manz codigo, orden_manz descripcion\n" +
                    "FROM a_epc_manzana m\n" +
                    "WHERE m.seg_unico = '" + numUpm+"'";

            Cursor cursor = conn.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                do {
//                    res.put(j+1, cursor.getString(0) + "|" + cursor.getString(1) + "|" + 1);
                    res.put(j+1, j+1 + "|" + cursor.getString(1) + "|" + 1);
                    j++;
                } while (cursor.moveToNext());
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    public Map<Integer, String> obtenerCatalogoUpmManzanaLV(String numUpm) {
        Map<Integer, String> res = new LinkedHashMap<>();
        int j = 0;
        try {
            String query = "SELECT num_upm codigo, manzano descripcion\n" +
                    "FROM cat_manzanas_comunidad cmc\n" +
                    "WHERE cmc.num_upm = '"+numUpm+"'\n";

            Cursor cursor = conn.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                do {
//                    res.put(j+1, cursor.getString(0) + "|" + cursor.getString(1) + "|" + 1);
                    res.put(j+1, j+1 + "|" + cursor.getString(1) + "|" + 1);
                    j++;
                } while (cursor.moveToNext());
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    public ArrayList<Map<String, Object>> obtenerCatalogoComunidadUpm(String numUpm) {
        ArrayList<Map<String, Object>> res = new ArrayList<>();
        try {
            String query = "SELECT num_upm codigo, comunidad descripcion\n" +
                    "FROM cat_comunidad_upm cmc\n" +
                    "WHERE cmc.num_upm = '"+numUpm+"'\n"+
                    "UNION\n"+
                    "SELECT '"+numUpm+"' codigo, comunidad descripcion\n" +
                    "FROM  (SELECT ID_UPM, CODIGO FROM cat_upm  where codigo='" + numUpm+ "') cu\n"+
                    "JOIN cat_upm_hijo cuh ON cu.id_upm=cuh.id_upm_padre\n"+
                    "JOIN cat_comunidad_upm ccu ON cuh.codigo=ccu.num_upm\n";

            Cursor cursor = conn.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                do {
                    Map<String, Object> row = new LinkedHashMap<>();
                    row.put("codigo", cursor.getString(0));
                    row.put("descripcion", cursor.getString(1));
                    res.add(row);
                } while (cursor.moveToNext());
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

}
