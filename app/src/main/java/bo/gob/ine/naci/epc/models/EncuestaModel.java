package bo.gob.ine.naci.epc.models;

import bo.gob.ine.naci.epc.entidades.IdInformante;

public class EncuestaModel {

    private int id_asignacion;
    private int correlativo;
    private int id_pregunta;
    private  String codigo_respuesta;
    private  String respuesta;
    private String observacion;
    private String latitud;
    private String longitud;
    private String visible;
    private String estado;
    private String usucre;
    private  long feccre;
    private String usumod;
    private  Long fecmod;

    public int getId_asignacion() {
        return id_asignacion;
    }

    public void setId_asignacion(int id_asignacion) {
        this.id_asignacion = id_asignacion;
    }

    public int getCorrelativo() {
        return correlativo;
    }

    public void setCorrelativo(int correlativo) {
        this.correlativo = correlativo;
    }

    public int getId_pregunta() {
        return id_pregunta;
    }

    public void setId_pregunta(int id_pregunta) {
        this.id_pregunta = id_pregunta;
    }

    public String getCodigo_respuesta() {
        return codigo_respuesta;
    }

    public void setCodigo_respuesta(String codigo_respuesta) {
        this.codigo_respuesta = codigo_respuesta;
    }

    public String getRespuesta() {
        return respuesta;
    }

    public void setRespuesta(String respuesta) {
        this.respuesta = respuesta;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }

    public String getLatitud() {
        return latitud;
    }

    public void setLatitud(String latitud) {
        this.latitud = latitud;
    }

    public String getLongitud() {
        return longitud;
    }

    public void setLongitud(String longitud) {
        this.longitud = longitud;
    }

    public String getVisible() {
        return visible;
    }

    public void setVisible(String visible) {
        this.visible = visible;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getUsucre() {
        return usucre;
    }

    public void setUsucre(String usucre) {
        this.usucre = usucre;
    }

    public long getFeccre() {
        return feccre;
    }

    public void setFeccre(long feccre) {
        this.feccre = feccre;
    }

    public String getUsumod() {
        return usumod;
    }

    public void setUsumod(String usumod) {
        this.usumod = usumod;
    }

    public Long getFecmod() {
        return fecmod;
    }

    public void setFecmod(Long fecmod) {
        this.fecmod = fecmod;
    }
    public IdInformante getIdInformante(){
        return new IdInformante(this.id_asignacion,this.correlativo);
    }
}
