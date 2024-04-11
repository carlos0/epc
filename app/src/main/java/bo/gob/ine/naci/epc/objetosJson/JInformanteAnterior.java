package bo.gob.ine.naci.epc.objetosJson;

import android.content.ContentValues;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class JInformanteAnterior {

    public int id_asignacion;
    public int correlativo;
    public String id_asignacion_padre;
    public String correlativo_padre;
    public int id_upm;
    public Integer id_upm_hijo;
    public int id_nivel;
    public String codigo;
    public String descripcion;
    public String id_informante;
    public String id_informante_padre;
    public int id_base;
    public String estado;
    public ArrayList<ContentValues> datos;

    public JInformanteAnterior(JSONArray jsonArray) throws JSONException {

        datos = new ArrayList<>();
        for (int j = 0; j < jsonArray.length(); j++) {

            JSONObject objetoJSON = jsonArray.getJSONObject(j);
            id_asignacion = objetoJSON.getInt("id_asignacion");
            correlativo = objetoJSON.getInt("correlativo");
            id_asignacion_padre = objetoJSON.getString("id_asignacion_padre").equals("")? null : objetoJSON.getString("id_asignacion_padre");
            correlativo_padre = objetoJSON.getString("correlativo_padre").equals("")? null : objetoJSON.getString("correlativo_padre");
            id_upm = objetoJSON.getInt("id_upm");
            id_upm_hijo = objetoJSON.getString("id_upm_hijo").equals("")?null:objetoJSON.getInt("id_upm_hijo");
            id_nivel = objetoJSON.getInt("id_nivel");
            codigo = objetoJSON.getString("codigo");
            descripcion = objetoJSON.getString("descripcion").equals(null)? null : objetoJSON.getString("descripcion");
            id_informante = objetoJSON.getString("id_informante").equals("")?null:objetoJSON.getString("id_informante");
            id_informante_padre= objetoJSON.getString("id_informante_padre").equals("")? null :objetoJSON.getString("id_informante_padre");
            id_base= objetoJSON.getInt("id_base");
            estado= objetoJSON.getString("estado");


            ContentValues paquete = new ContentValues();
            paquete.put("id_asignacion", id_asignacion);
            paquete.put("correlativo", correlativo);
            paquete.put("id_asignacion_padre", id_asignacion_padre);
            paquete.put("correlativo_padre", correlativo_padre);
            paquete.put("id_upm", id_upm);
            paquete.put("id_upm_hijo", id_upm_hijo);
            paquete.put("id_nivel", id_nivel);
            paquete.put("codigo", codigo);
            paquete.put("descripcion", descripcion);
            paquete.put("id_informante", id_informante);
            paquete.put("id_informante_padre", id_informante_padre);
            paquete.put("id_base", id_base);
            paquete.put("estado", estado);

            datos.add(paquete);
        }


    }

}
