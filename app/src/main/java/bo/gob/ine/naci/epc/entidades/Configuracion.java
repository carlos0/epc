package bo.gob.ine.naci.epc.entidades;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import bo.gob.ine.naci.epc.MyApplication;
import bo.gob.ine.naci.epc.herramientas.Parametros;

public class Configuracion {
    private static ArrayList<OpcionConfiguracion> opcionesConfiguracion;

    //SE CREAN LAS OPCIONES DE CONFIGURACION INICIALES
    public static void inicializa(SharedPreferences preferences) {
        if(opcionesConfiguracion == null) {
            opcionesConfiguracion = new ArrayList<OpcionConfiguracion>();
            opcionesConfiguracion.add(new OpcionConfiguracion("Ubicación de controles", "Posición de los botones: Anterior y Siguiente", "arriba;abajo", "cfgUbicacionControles", preferences, 0));
            opcionesConfiguracion.add(new OpcionConfiguracion("Tamaño de Fuente", "Tamaño de fuente durante la encuesta", "pequeño;normal;grande", "cfgTamañoLetra", preferences, 1));
        }
    }

    public static void clear() {
        opcionesConfiguracion = null;
    }

    public static ArrayList<OpcionConfiguracion> getOpcionesConfiguracion() {
        return opcionesConfiguracion;
    }

    public static String getTitulo(int position) {
        return opcionesConfiguracion.get(position).getTitulo();
    }

    public String getDescripcion (int position) {
        return opcionesConfiguracion.get(position).getDescripcion();
    }

    public static Map<Integer, String> getOpciones(int position) {
        Map<Integer, String> map = new HashMap<>();
        int contador = 0;
        for(String str : opcionesConfiguracion.get(position).getOpciones().split(";"))
        {
            map.put(contador++, str);
        }
        return map;
    }

    public static Integer getDatoConfiguracion(int position) {
        if(opcionesConfiguracion == null) {
            inicializa(MyApplication.getContext().getSharedPreferences(Parametros.SIGLA_PROYECTO + ".xml", Context.MODE_PRIVATE));
        }
        SharedPreferences preferences = MyApplication.getContext().getSharedPreferences(Parametros.SIGLA_PROYECTO + ".xml", Context.MODE_PRIVATE);
        return preferences.getInt(opcionesConfiguracion.get(position).getSaveName(), opcionesConfiguracion.get(position).getDefVal());
    }

    public static void guardaConfiguracion(int position, int id) {
        SharedPreferences preferences = MyApplication.getContext().getSharedPreferences(Parametros.SIGLA_PROYECTO + ".xml", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(opcionesConfiguracion.get(position).getSaveName(), id);
        editor.apply();
    }
}
