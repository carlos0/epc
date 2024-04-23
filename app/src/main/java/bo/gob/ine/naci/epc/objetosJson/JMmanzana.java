package bo.gob.ine.naci.epc.objetosJson;

import android.content.ContentValues;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

//TODO:BRP
public class JMmanzana {

    public int gid;
    public String id_manz;
    public String orden_manz;
    public String seg_unico;
    public String geo;

    public ArrayList<ContentValues> datos;

    public JMmanzana(JSONArray jsonArray) throws JSONException {
        datos = new ArrayList<>();
        for (int j = 0; j < jsonArray.length(); j++) {

            JSONObject objetoJSON = jsonArray.getJSONObject(j);
            gid = objetoJSON.getInt("gid");
            id_manz = objetoJSON.getString("id_manz");
            orden_manz = objetoJSON.getString("orden_manz");
            seg_unico = objetoJSON.getString("seg_unico");
            geo = objetoJSON.getString("geo");

            ContentValues paquete = new ContentValues();
            paquete.put("gid", gid);
            paquete.put("id_manz", id_manz);
            paquete.put("orden_manz", orden_manz);
            paquete.put("seg_unico", seg_unico);
            paquete.put("geo", geo);

            datos.add(paquete);
        }


    }

}
