package bo.gob.ine.naci.epc.objetosJson;

import android.content.ContentValues;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class JDocumento {

    public int id_documento;
    public int id_proyecto;
    public String nombre;
    public String path;
    public String descripcion;
    public String estado;
    public String usucre;
    public int feccre;
    public String usumod;
    public String fecmod;
    public ArrayList<ContentValues> datos;

    public JDocumento(JSONArray jsonArray) throws JSONException {
        datos = new ArrayList<>();
        for (int j = 0; j < jsonArray.length(); j++) {

            JSONObject objetoJSON = jsonArray.getJSONObject(j);
            id_documento = objetoJSON.getInt("id_documento");
            id_proyecto = objetoJSON.getInt("id_proyecto");
            nombre = objetoJSON.getString("nombre");
            path = objetoJSON.getString("path");
            descripcion = objetoJSON.getString("descripcion");
            estado = objetoJSON.getString("estado");
            usucre = objetoJSON.getString("usucre");
            feccre = objetoJSON.getInt("feccre");
            usumod = objetoJSON.getString("usumod");
            fecmod = objetoJSON.getString("fecmod");


            ContentValues paquete = new ContentValues();
            paquete.put("id_documento", id_documento);
            paquete.put("id_proyecto", id_proyecto);
            paquete.put("nombre", nombre);
            paquete.put("path", path);
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
