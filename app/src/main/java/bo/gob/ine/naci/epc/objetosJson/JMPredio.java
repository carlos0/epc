package bo.gob.ine.naci.epc.objetosJson;

import android.content.ContentValues;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

//TODO:BRP
public class JMPredio {

    public int gid;
    public String seg_unico;
    public String orden;
    public String geo;

    public ArrayList<ContentValues> datos;

    public JMPredio(JSONArray jsonArray) throws JSONException {
        datos = new ArrayList<>();
        for (int j = 0; j < jsonArray.length(); j++) {

            JSONObject objetoJSON = jsonArray.getJSONObject(j);
            gid = objetoJSON.getInt("gid");
            seg_unico = objetoJSON.getString("seg_unico");
            orden = objetoJSON.getString("orden");
            geo = objetoJSON.getString("geo");

            ContentValues paquete = new ContentValues();
            paquete.put("gid", gid);
            paquete.put("seg_unico", seg_unico);
            paquete.put("orden", orden);
            paquete.put("geo", geo);

            datos.add(paquete);
        }


    }

}
