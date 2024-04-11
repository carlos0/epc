package bo.gob.ine.naci.epc.models;

public class PreguntaModel {
    private int id_pregunta;
    private  int id_seccion;
    private String ayuda;
    private int id_tipo_pregunta;
    private int minimo;
    private int maximo;
    private String catalogo;
    private int longitud;
    private int codigo_especifique;
    private  String estado;
    private String usucre;
    private  long feccre;
    private String codigo_pregunta;
    private  String pregunta;
    private String respuesta;
    private String saltos;
    private String regla;
    private String saltos_rpn;
    private  String regla_rpn;
    private  String apoyo;
    private Integer inicial;
    private  String omision;
    private  String variable;
    private  String formula;

    public int getId_pregunta() {
        return id_pregunta;
    }

    public void setId_pregunta(int id_pregunta) {
        this.id_pregunta = id_pregunta;
    }

    public int getId_seccion() {
        return id_seccion;
    }

    public void setId_seccion(int id_seccion) {
        this.id_seccion = id_seccion;
    }

    public String getAyuda() {
        return ayuda;
    }

    public void setAyuda(String ayuda) {
        this.ayuda = ayuda;
    }

    public int getId_tipo_pregunta() {
        return id_tipo_pregunta;
    }

    public void setId_tipo_pregunta(int id_tipo_pregunta) {
        this.id_tipo_pregunta = id_tipo_pregunta;
    }

    public int getMinimo() {
        return minimo;
    }

    public void setMinimo(int minimo) {
        this.minimo = minimo;
    }

    public int getMaximo() {
        return maximo;
    }

    public void setMaximo(int maximo) {
        this.maximo = maximo;
    }

    public String getCatalogo() {
        return catalogo;
    }

    public void setCatalogo(String catalogo) {
        this.catalogo = catalogo;
    }

    public int getLongitud() {
        return longitud;
    }

    public void setLongitud(int longitud) {
        this.longitud = longitud;
    }

    public int getCodigo_especifique() {
        return codigo_especifique;
    }

    public void setCodigo_especifique(int codigo_especifique) {
        this.codigo_especifique = codigo_especifique;
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

    public String getCodigo_pregunta() {
        return codigo_pregunta;
    }

    public void setCodigo_pregunta(String codigo_pregunta) {
        this.codigo_pregunta = codigo_pregunta;
    }

    public String getPregunta() {
        return pregunta;
    }

    public void setPregunta(String pregunta) {
        this.pregunta = pregunta;
    }

    public String getRespuesta() {
        return respuesta;
    }

    public void setRespuesta(String respuesta) {
        this.respuesta = respuesta;
    }

    public String getSaltos() {
        return saltos;
    }

    public void setSaltos(String saltos) {
        this.saltos = saltos;
    }

    public String getRegla() {
        return regla;
    }

    public void setRegla(String regla) {
        this.regla = regla;
    }

    public String getSaltos_rpn() {
        return saltos_rpn;
    }

    public void setSaltos_rpn(String saltos_rpn) {
        this.saltos_rpn = saltos_rpn;
    }

    public String getRegla_rpn() {
        return regla_rpn;
    }

    public void setRegla_rpn(String regla_rpn) {
        this.regla_rpn = regla_rpn;
    }

    public String getApoyo() {
        return apoyo;
    }

    public void setApoyo(String apoyo) {
        this.apoyo = apoyo;
    }

    public Integer getInicial() {
        return inicial;
    }

    public void setInicial(Integer inicial) {
        this.inicial = inicial;
    }

    public String getOmision() {
        return omision;
    }

    public void setOmision(String omision) {
        this.omision = omision;
    }

    public String getVariable() {
        return variable;
    }

    public void setVariable(String variable) {
        this.variable = variable;
    }

    public String getFormula() {
        return formula;
    }

    public void setFormula(String formula) {
        this.formula = formula;
    }
}
