package bo.gob.ine.naci.epc.objetosJson;

import android.content.ContentValues;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class JRol {

    public int id_rol;
    public String sigla;
    public String descripcion;
    public String estado;
    public String usucre;
    public int feccre;
    public String usumod;
    public String fecmod;
    public ArrayList<ContentValues> datos;

    public JRol(JSONArray jsonArray) throws JSONException {
        datos = new ArrayList<>();
        for (int j = 0; j < jsonArray.length(); j++) {

            JSONObject objetoJSON = jsonArray.getJSONObject(j);
            id_rol = objetoJSON.getInt("id_rol");
            sigla = objetoJSON.getString("sigla");
            descripcion = objetoJSON.getString("descripcion");
            estado = objetoJSON.getString("estado");
            usucre = objetoJSON.getString("usucre");
            feccre = objetoJSON.getInt("feccre");
            usumod = objetoJSON.getString("usumod");
            fecmod = objetoJSON.getString("fecmod");


            ContentValues paquete = new ContentValues();
            paquete.put("id_rol", id_rol);
            paquete.put("sigla", sigla);
            paquete.put("descripcion", descripcion);
            paquete.put("estado", estado);
            paquete.put("usucre", usucre);
            paquete.put("feccre", feccre);
            paquete.put("usumod", usumod);
            paquete.put("fecmod", fecmod);

            datos.add(paquete);
        }

    }

}
