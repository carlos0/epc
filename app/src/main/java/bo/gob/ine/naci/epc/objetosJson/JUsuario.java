package bo.gob.ine.naci.epc.objetosJson;

import android.content.ContentValues;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class JUsuario {

    public int id_usuario;
    public int id_departamento;
    public String login;
    public String password;
    public String nombre;
    public String telefono;
    public String foto;
    public String estado;
    public String usucre;
    public String feccre;
    public String id_rol;
    public String id_brigada;
    public ArrayList<ContentValues> datos;

    public JUsuario(JSONArray jsonArray) throws JSONException {
        datos = new ArrayList<>();
        for (int j = 0; j < jsonArray.length(); j++) {

            JSONObject objetoJSON = jsonArray.getJSONObject(j);
            id_usuario = objetoJSON.getInt("id_usuario");
            id_departamento = objetoJSON.getInt("id_departamento");
            login = objetoJSON.getString("login");
            password = objetoJSON.getString("password");
            nombre = objetoJSON.getString("nombre");
            telefono = objetoJSON.getString("telefono");
            foto = objetoJSON.getString("foto");
            estado = objetoJSON.getString("estado");
            usucre = objetoJSON.getString("usucre");
            feccre = objetoJSON.getString("feccre");
            id_rol = objetoJSON.getString("id_rol");
            id_brigada = objetoJSON.getString("id_brigada");

            ContentValues paquete = new ContentValues();
            paquete.put("id_usuario", id_usuario);
            paquete.put("id_departamento", id_departamento);
            paquete.put("login", login);
            paquete.put("password", password);
            paquete.put("nombre", nombre);
            paquete.put("telefono", telefono);
            paquete.put("foto", foto);
            paquete.put("estado", estado);
            paquete.put("usucre", usucre);
            paquete.put("feccre", feccre);
            paquete.put("id_rol", id_rol);
            paquete.put("id_brigada", id_brigada);

            datos.add(paquete);
        }


    }

}
