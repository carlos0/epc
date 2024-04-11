package bo.gob.ine.naci.epc.objetosJson;

import android.content.ContentValues;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class JProyecto {

    public int id_proyecto;
    public String nombre;
    public String codigo;
    public String descripcion;
    public int fecinicio;
    public int fecfin;
    public String estado;
    public String usucre;
    public int feccre;
    public String usumod;
    public String fecmod;
    public String color;
    public String codigo_desbloqueo;
    public String version_boleta;

    public ArrayList<ContentValues> datos;

    public JProyecto(JSONArray jsonArray) throws JSONException {
        datos = new ArrayList<>();
        for (int j = 0; j < jsonArray.length(); j++) {

            JSONObject objetoJSON = jsonArray.getJSONObject(j);
            id_proyecto = objetoJSON.getInt("id_proyecto");
            nombre = objetoJSON.getString("nombre");
            codigo = objetoJSON.getString("codigo");
            descripcion = objetoJSON.getString("descripcion");
            fecinicio = objetoJSON.getInt("fecinicio");
            fecfin = objetoJSON.getInt("fecfin");
            estado = objetoJSON.getString("estado");
            usucre = objetoJSON.getString("usucre");
            feccre = objetoJSON.getInt("feccre");
            usumod = objetoJSON.getString("usumod");
            fecmod = objetoJSON.getString("fecmod");
            color = objetoJSON.getString("color");
            codigo_desbloqueo = objetoJSON.getString("codigo_desbloqueo");
            version_boleta = objetoJSON.getString("version_boleta");


            ContentValues paquete = new ContentValues();
            paquete.put("id_proyecto", id_proyecto);
            paquete.put("nombre", nombre);
            paquete.put("codigo", codigo);
            paquete.put("descripcion", descripcion);
            paquete.put("fecinicio", fecinicio);
            paquete.put("fecfin", fecfin);
            paquete.put("estado", estado);
            paquete.put("usucre", usucre);
            paquete.put("feccre", feccre);
            paquete.put("usumod", usumod);
            paquete.put("fecmod", fecmod);
            paquete.put("color", color);
            paquete.put("codigo_desbloqueo", codigo_desbloqueo);
            paquete.put("version_boleta", version_boleta);

            datos.add(paquete);
        }

    }

}
