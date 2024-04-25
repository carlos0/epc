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
    public String cod_if;
    public String tipo;
    public String ciu_com;
    public String geo;

    public ArrayList<ContentValues> datos;

    public JMPredio(JSONArray jsonArray) throws JSONException {
        datos = new ArrayList<>();
        for (int j = 0; j < jsonArray.length(); j++) {

            JSONObject objetoJSON = jsonArray.getJSONObject(j);
            gid = objetoJSON.getInt("gid");
            seg_unico = objetoJSON.getString("seg_unico");
            orden = objetoJSON.getString("orden");
            cod_if = objetoJSON.getString("cod_if");
            tipo = objetoJSON.getString("tipo");
            ciu_com = objetoJSON.getString("ciu_com");
            geo = objetoJSON.getString("geo");

            ContentValues paquete = new ContentValues();
            paquete.put("gid", gid);
            paquete.put("seg_unico", seg_unico);
            paquete.put("orden", orden);
            paquete.put("cod_if", cod_if);
            paquete.put("tipo", tipo);
            paquete.put("ciu_com", ciu_com);
            paquete.put("geo", geo);

            datos.add(paquete);
        }


    }

}
