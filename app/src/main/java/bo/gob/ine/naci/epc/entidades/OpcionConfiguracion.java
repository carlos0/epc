package bo.gob.ine.naci.epc.entidades;

import android.content.Context;
import android.content.SharedPreferences;

import bo.gob.ine.naci.epc.MyApplication;
import bo.gob.ine.naci.epc.herramientas.Parametros;

public class OpcionConfiguracion {
    private String titulo;
    private String descripcion;
    private String opciones;
    private String saveName;
    private int value;
    private int defVal;

    public OpcionConfiguracion(String titulo, String descripcion, String opciones, String saveName, SharedPreferences preferences, int defVal) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.opciones = opciones;
        this.saveName = saveName;
        this.defVal = defVal;
        value = preferences.getInt(saveName, defVal);
    }

    public void guardar (SharedPreferences.Editor editor, int id) {
        editor.putInt(saveName, id);
        editor.apply();
    }

    public int getValor() {
        SharedPreferences preferences = MyApplication.getContext().getSharedPreferences(Parametros.SIGLA_PROYECTO + ".xml", Context.MODE_PRIVATE);
        return preferences.getInt(saveName, defVal);
    }

    public String getOpcion(int pos) {
        return opciones.split(";")[pos];
    }

    public String getDatoActual() {
        return getOpcion(getValor());
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getOpciones() {
        return opciones;
    }

    public void setOpciones(String opciones) {
        this.opciones = opciones;
    }

    public String getSaveName() {
        return saveName;
    }

    public void setSaveName(String saveName) {
        this.saveName = saveName;
    }

    public int getDefVal() {
        return defVal;
    }

    public void setDefVal(int defVal) {
        this.defVal = defVal;
    }
}
