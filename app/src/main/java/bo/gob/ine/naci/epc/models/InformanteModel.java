package bo.gob.ine.naci.epc.models;

public class InformanteModel {
    private int id_asignacion;
    private int correlativo;
    private Integer id_asignacion_padre;
    private Integer correlativo_padre;
    private int id_usuario;
    private int id_upm;
    private Integer id_upm_hijo;
    private int id_nivel;
    private String latitud;
    private String longitud;
    private String codigo;
    private String descripcion;
    private String estado;
    private String usucre;
    private long feccre;
    private String usumod;
    private long fecmod;
    private Integer id_informante_anterior;
    private String codigo_anterior;
    private Integer id_asignacion_anterior;
    private Integer correlativo_anterior;

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

    public Integer getId_asignacion_padre() {
        return id_asignacion_padre;
    }

    public void setId_asignacion_padre(Integer id_asignacion_padre) {
        this.id_asignacion_padre = id_asignacion_padre;
    }

    public Integer getCorrelativo_padre() {
        return correlativo_padre;
    }

    public void setCorrelativo_padre(Integer correlativo_padre) {
        this.correlativo_padre = correlativo_padre;
    }

    public int getId_usuario() {
        return id_usuario;
    }

    public void setId_usuario(int id_usuario) {
        this.id_usuario = id_usuario;
    }

    public int getId_upm() {
        return id_upm;
    }

    public void setId_upm(int id_upm) {
        this.id_upm = id_upm;
    }

    public Integer getId_upm_hijo() {
        return id_upm_hijo;
    }

    public void setId_upm_hijo(Integer id_upm_hijo) {
        this.id_upm_hijo = id_upm_hijo;
    }

    public int getId_nivel() {
        return id_nivel;
    }

    public void setId_nivel(int id_nivel) {
        this.id_nivel = id_nivel;
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

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
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

    public long getFecmod() {
        return fecmod;
    }

    public void setFecmod(long fecmod) {
        this.fecmod = fecmod;
    }

    public Integer getId_informante_anterior() {
        return id_informante_anterior;
    }

    public void setId_informante_anterior(Integer id_informante_anterior) {
        this.id_informante_anterior = id_informante_anterior;
    }

    public String getCodigo_anterior() {
        return codigo_anterior;
    }

    public void setCodigo_anterior(String codigo_anterior) {
        this.codigo_anterior = codigo_anterior;
    }

    public Integer getId_asignacion_anterior() {
        return id_asignacion_anterior;
    }

    public void setId_asignacion_anterior(Integer id_asignacion_anterior) {
        this.id_asignacion_anterior = id_asignacion_anterior;
    }

    public Integer getCorrelativo_anterior() {
        return correlativo_anterior;
    }

    public void setCorrelativo_anterior(Integer correlativo_anterior) {
        this.correlativo_anterior = correlativo_anterior;
    }
}
