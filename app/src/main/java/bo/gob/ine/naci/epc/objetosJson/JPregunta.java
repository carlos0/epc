package bo.gob.ine.naci.epc.objetosJson;

import android.content.ContentValues;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class JPregunta{

    public int id_pregunta;
    public int id_seccion;
    public String ayuda;
    public int id_tipo_pregunta;
    public int minimo;
    public int maximo;
    public String catalogo;
    public int longitud;
    public String codigo_especifique;
    public String estado;
    public String usucre;
    public String feccre;
    public String codigo_pregunta;
    public String pregunta;
    public String respuesta;
    public String saltos;
    public String regla;
    public String visible;
    public String apoyo;
    public int inicial;
    public String omision;
    public String variable;
    public ArrayList<ContentValues> datos;

    public JPregunta(JSONArray jsonArray) throws JSONException {
        datos = new ArrayList<>();
        for (int j = 0; j < jsonArray.length(); j++) {

            JSONObject objetoJSON = jsonArray.getJSONObject(j);

            objetoJSON = objetoJSON.equals("") ? null : objetoJSON;

            id_pregunta = objetoJSON.getInt("id_pregunta");
            id_seccion = objetoJSON.getInt("id_seccion");
            ayuda = objetoJSON.getString("ayuda").equals("")? null : objetoJSON.getString("ayuda");
            id_tipo_pregunta = objetoJSON.getInt("id_tipo_pregunta");
            minimo = objetoJSON.getInt("minimo");
            maximo = objetoJSON.getInt("maximo");
            catalogo = objetoJSON.getString("catalogo");
            longitud = objetoJSON.getInt("longitud");
            codigo_especifique = objetoJSON.getString("codigo_especifique");
            estado = objetoJSON.getString("estado");
            usucre = objetoJSON.getString("usucre");
            feccre = objetoJSON.getString("feccre");
            codigo_pregunta = objetoJSON.getString("codigo_pregunta");
            pregunta = objetoJSON.getString("pregunta");
            respuesta = objetoJSON.getString("respuesta").equals("null")? null : objetoJSON.getString("respuesta");
            saltos = objetoJSON.getString("saltos").equals("null")? null : objetoJSON.getString("saltos");
            regla = objetoJSON.getString("regla").equals("null")? null : objetoJSON.getString("regla");
            apoyo = objetoJSON.getString("apoyo");
            inicial = objetoJSON.getInt("inicial");
            omision = objetoJSON.getString("omision");
            variable = objetoJSON.getString("variable").equals("null")? null : objetoJSON.getString("variable");

            ContentValues paquete = new ContentValues();
            paquete.put("id_pregunta", id_pregunta);
            paquete.put("id_seccion",id_seccion);
            paquete.put("ayuda", ayuda);
            paquete.put("id_tipo_pregunta", id_tipo_pregunta);
            paquete.put("minimo", minimo);
            paquete.put("maximo", maximo);
            paquete.put("catalogo", catalogo);
            paquete.put("longitud", longitud);
            paquete.put("codigo_especifique", codigo_especifique);
            paquete.put("estado", estado);
            paquete.put("usucre", usucre);
            paquete.put("feccre", feccre);
            paquete.put("codigo_pregunta", codigo_pregunta);
            paquete.put("pregunta", pregunta);
            paquete.put("respuesta", respuesta);
            paquete.put("saltos", saltos);
            paquete.put("regla", regla);
            paquete.put("apoyo", apoyo);
            paquete.put("inicial", inicial);
            paquete.put("omision", omision);
            paquete.put("variable", variable);
            datos.add(paquete);
        }
    }

}
