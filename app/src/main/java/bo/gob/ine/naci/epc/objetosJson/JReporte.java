package bo.gob.ine.naci.epc.objetosJson;

import android.content.ContentValues;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class JReporte {

    public int id_reporte;
    public int id_proyecto;
    public String nombre;
    public int id_tiporeporte;
    public String vista_funcion;
    public String estado;
    public String usucre;
    public int feccre;
    public String usumod;
    public String fecmod;
    public ArrayList<ContentValues> datos;

    public JReporte(JSONArray jsonArray) throws JSONException {
        datos = new ArrayList<>();
        for (int j = 0; j < jsonArray.length(); j++) {

            JSONObject objetoJSON = jsonArray.getJSONObject(j);
            id_reporte = objetoJSON.getInt("id_reporte");
            id_proyecto = objetoJSON.getInt("id_proyecto");
            nombre = objetoJSON.getString("nombre");
            id_tiporeporte = objetoJSON.getInt("id_tiporeporte");
            vista_funcion = objetoJSON.getString("vista_funcion");
            estado = objetoJSON.getString("estado");
            usucre = objetoJSON.getString("usucre");
            feccre = objetoJSON.getInt("feccre");
            usumod = objetoJSON.getString("usumod");
            fecmod = objetoJSON.getString("fecmod");


            ContentValues paquete = new ContentValues();
            paquete.put("id_reporte", id_reporte);
            paquete.put("id_proyecto", id_proyecto);
            paquete.put("nombre", nombre);
            paquete.put("id_tiporeporte", id_tiporeporte);
            paquete.put("vista_funcion", vista_funcion);
            paquete.put("estado", estado);
            paquete.put("usucre", usucre);
            paquete.put("feccre", feccre);
            paquete.put("usumod", usumod);
            paquete.put("fecmod", fecmod);

            datos.add(paquete);
        }


    }

}
