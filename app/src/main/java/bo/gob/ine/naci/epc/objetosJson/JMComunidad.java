package bo.gob.ine.naci.epc.objetosJson;

import android.content.ContentValues;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

//TODO:BRP
public class JMComunidad {

    public int gid;
    public String ciu_com;
    public String id_com;
    public String seg_unico;
    public String geo;

    public ArrayList<ContentValues> datos;

    public JMComunidad(JSONArray jsonArray) throws JSONException {
        datos = new ArrayList<>();
        for (int j = 0; j < jsonArray.length(); j++) {

            JSONObject objetoJSON = jsonArray.getJSONObject(j);
            gid = objetoJSON.getInt("gid");
            ciu_com = objetoJSON.getString("ciu_com");
            id_com = objetoJSON.getString("id_com");
            seg_unico = objetoJSON.getString("seg_unico");
            geo = objetoJSON.getString("geo");

            ContentValues paquete = new ContentValues();
            paquete.put("gid", gid);
            paquete.put("ciu_com", ciu_com);
            paquete.put("id_com", id_com);
            paquete.put("seg_unico", seg_unico);
            paquete.put("geo", geo);

            datos.add(paquete);
        }


    }

}
