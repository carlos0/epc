package bo.gob.ine.naci.epc.objetosJson;

import android.content.ContentValues;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class JTipoObservacion {

    public int id_tipo_obs;
    public String cod_tipo;
    public String descripcion;
    public String usucre;
    public int feccre;
    public String usumod;
    public String fecmod;
    public String estado;
    public ArrayList<ContentValues> datos;

    public JTipoObservacion(JSONArray jsonArray) throws JSONException {

        datos = new ArrayList<>();
        for (int j = 0; j < jsonArray.length(); j++) {

            JSONObject objetoJSON = jsonArray.getJSONObject(j);
            id_tipo_obs = objetoJSON.getInt("id_tipo_obs");
            cod_tipo = objetoJSON.getString("cod_tipo");
            descripcion = objetoJSON.getString("descripcion");
            usucre = objetoJSON.getString("usucre");
            feccre = objetoJSON.getInt("feccre");
            usumod = objetoJSON.getString("usumod");
            fecmod = objetoJSON.getString("fecmod");
            estado = objetoJSON.getString("estado");


            ContentValues paquete = new ContentValues();
            paquete.put("id_tipo_obs", id_tipo_obs);
            paquete.put("cod_tipo", cod_tipo);
            paquete.put("descripcion", descripcion);
            paquete.put("usucre", usucre);
            paquete.put("feccre", feccre);
            paquete.put("usumod", usumod);
            paquete.put("fecmod", fecmod);
            paquete.put("estado", estado);

            datos.add(paquete);
        }


    }

}
