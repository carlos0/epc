
package bo.gob.ine.naci.epc;

import android.os.Bundle;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import bo.gob.ine.naci.epc.entidades.Asignacion;
import bo.gob.ine.naci.epc.entidades.Encuesta;
import bo.gob.ine.naci.epc.entidades.Estado;
import bo.gob.ine.naci.epc.entidades.Flujo;
import bo.gob.ine.naci.epc.entidades.IdEncuesta;
import bo.gob.ine.naci.epc.entidades.IdInformante;
import bo.gob.ine.naci.epc.entidades.Informante;
import bo.gob.ine.naci.epc.entidades.Pregunta;
import bo.gob.ine.naci.epc.entidades.Seleccion;
import bo.gob.ine.naci.epc.entidades.Siguiente;
import bo.gob.ine.naci.epc.entidades.TipoPregunta;
import bo.gob.ine.naci.epc.entidades.UpmHijo;
import bo.gob.ine.naci.epc.entidades.Usuario;
import bo.gob.ine.naci.epc.herramientas.ActionBarActivityProcess;
import bo.gob.ine.naci.epc.herramientas.Movil;
import bo.gob.ine.naci.epc.herramientas.Parametros;
import bo.gob.ine.naci.epc.preguntas.Abierta;
import bo.gob.ine.naci.epc.preguntas.Autocompletar;
import bo.gob.ine.naci.epc.preguntas.Decimal;
import bo.gob.ine.naci.epc.preguntas.Memoria;
import bo.gob.ine.naci.epc.preguntas.Numero;
import bo.gob.ine.naci.epc.preguntas.PreguntaView;


public class EncuestaInicialActivity extends ActionBarActivityProcess implements View.OnClickListener{

    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private ImageView imagen;

    private IdInformante idInformante;
    private int idNivel;
    private int idUpmHijo;
    private IdInformante idPadre;

    private Encuesta encuesta;
    private Pregunta tempPregunta;
    private PreguntaView[] pregs;
    private int indice = -1;
    private Button[] buttons;
    private ExtendedFloatingActionButton botonGuardar;
    private ActionBarDrawerToggle toogle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_encuesta_inicial);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        imagen = findViewById(R.id.logo);
        imagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        NavigationView navigationView = findViewById(R.id.navigation_view);
        if (navigationView != null) {
            setupNavigationDrawerContent(navigationView);
        }
        setupNavigationDrawerContent(navigationView);

        toogle = new ActionBarDrawerToggle(this,drawerLayout,R.string.open,R.string.close);
        drawerLayout.addDrawerListener(toogle);
        toogle.syncState();

        Bundle bundle = getIntent().getExtras();
        idInformante = new IdInformante(bundle.getIntArray("IdInformante"));
        idNivel = bundle.getInt("IdNivel");
        idPadre = new IdInformante(bundle.getIntArray("IdPadre"));
        idUpmHijo = bundle.getInt("idUpmHijo");

        getSupportActionBar().setDisplayShowHomeEnabled(true);

        botonGuardar = findViewById(R.id.botonGuardar);
        botonGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guardar();
            }
        });


        encuesta = new Encuesta();

        LinearLayout layout = (LinearLayout) findViewById(R.id.layout_encuesta_inicial);
        final Pregunta pregunta = new Pregunta();

        if (pregunta.abrir("id_pregunta in (18596,18597,20484,36384,20486,36385,20485)", "codigo_pregunta")) {
//        if (pregunta.abrir("id_seccion = (select id_seccion from enc_seccion where id_nivel = " + idNivel + ")", "spss_codigo_pregunta")) {
//            if (pregunta.get_instruccion() != null) {
//                informationMessage(null, "Lea con atención.", Html.fromHtml(pregunta.get_instruccion()), Parametros.FONT_OBS);
//            }
            pregs = new PreguntaView[pregunta.count()];
            buttons = new Button[pregunta.count()];
            int i = 0;
            do {
                String p = pregunta.procesaEnunciado(idPadre, pregunta.get_pregunta());
                String a = pregunta.get_id_tipo_pregunta().toString();
                String b = pregunta.get_ayuda();
                switch (pregunta.get_id_tipo_pregunta()) {
                    case Abierta: {
//                        pregs[i] = new Abierta(this, pregunta.get_id_pregunta(), pregunta.get_id_seccion(), pregunta.get_codigo_pregunta(), p, pregunta.get_longitud(), pregunta.get_codigo_especial(), pregunta.get_ayuda(), true);
                        break;
                    }
                    case Cerrada: {
                        // EFSR y EFVG
                        ArrayList<Integer> filtro = null;
                        /*if(pregunta.get_id_pregunta() == 35844) {
                            if(Usuario.getFoto().equals("1")) {
                                filtro = new ArrayList<Integer>(Arrays.asList(1));
                            } else if(Usuario.getFoto().equals("2")) {
                                filtro = new ArrayList<Integer>(Arrays.asList(2));
                            }
                        }*/
//                        Map<Integer, String> respuestas = Respuesta.getRespuestas(pregunta.get_id_pregunta());
//                        Map<Integer, String> respuestas = Pregunta.getRespuestas(pregunta.get_respuesta());
//                        pregs[i] = new Cerrada(this, pregunta.get_id_pregunta(), pregunta.get_id_seccion(), pregunta.get_codigo_pregunta(), p, respuestas, pregunta.get_codigo_especifique(), pregunta.get_codigo_especial(), filtro, pregunta.get_ayuda());
                        break;
                    }
                    case CerradaOmitida: {
//                        Map<Integer, String> respuestas = Pregunta.getRespuestas(pregunta.get_respuesta());
//                        pregs[i] = new CerradaOmitida(this, pregunta.get_id_pregunta(), pregunta.get_id_seccion(), pregunta.get_codigo_pregunta(), p, respuestas, pregunta.get_codigo_especifique(), pregunta.get_codigo_especial(), pregunta.get_ayuda(), Informante.getUpm(idInformante));
                        break;
                    }
                    case UsoVivienda:
                    case NumeroVivienda:
                    case Numero: {
//                        pregs[i] = new Numero(this, pregunta.get_id_pregunta(), pregunta.get_id_seccion(), pregunta.get_codigo_pregunta(), p, pregunta.get_longitud(), pregunta.get_codigo_especial(), pregunta.get_ayuda());
                        break;
                    }
                    case Decimal: {
//                        pregs[i] = new Decimal(this, pregunta.get_id_pregunta(), pregunta.get_id_seccion(), pregunta.get_codigo_pregunta(), p, pregunta.get_longitud(), pregunta.get_codigo_especial(), pregunta.get_ayuda());
                        break;
                    }
                    case Multiple: {
//                        Map<Integer, String> respuestas = Pregunta.getRespuestas(pregunta.get_respuesta());
//                        pregs[i] = new Multiple(this, pregunta.get_id_pregunta(), pregunta.get_id_seccion(), pregunta.get_codigo_pregunta(), p, respuestas, pregunta.get_codigo_especial(), pregunta.get_ayuda());
                        break;
                    }
                    case Fecha: {
//                        pregs[i] = new Fecha(this, pregunta.get_id_pregunta(), pregunta.get_id_seccion(), pregunta.get_codigo_pregunta(), p, pregunta.get_codigo_especial(), pregunta.get_ayuda());
                        break;
                    }
                    case Memoria: {
                        String[] values = Encuesta.getRespuesta(Informante.getUpm(idInformante), pregunta.get_codigo_pregunta());
//                        pregs[i] = new Memoria(this, pregunta.get_id_pregunta(), pregunta.get_id_seccion(), pregunta.get_codigo_pregunta(), p, pregunta.get_longitud(), omi, values, pregunta.get_ayuda());
                        break;
                    }
                    case Upm: {
                        Map<Integer, String> respuestas = UpmHijo.getRespuestasUPM(Informante.getUpm(idInformante));
//                        pregs[i] = new Cerrada(this, pregunta.get_id_pregunta(), pregunta.get_id_seccion(), pregunta.get_codigo_pregunta(), p, respuestas, pregunta.get_codigo_especifique(), pregunta.get_codigo_especial(), null, pregunta.get_ayuda());
                        break;
                    }
                    case Autocompletar: {
//                        pregs[i] = new Autocompletar(this, pregunta.get_id_pregunta(), pregunta.get_id_seccion(), pregunta.get_codigo_pregunta(), p, pregunta.get_longitud(), pregunta.get_catalogo(), pregunta.get_codigo_especial(), pregunta.get_ayuda(), true);
                        break;
                    }
                    case UpmHijo: {
                        Map<Integer, String> respuestas = UpmHijo.getRespuestasManzano(Informante.getUpm(idInformante));
//                        pregs[i] = new Cerrada(this, pregunta.get_id_pregunta(), pregunta.get_id_seccion(), pregunta.get_codigo_pregunta(), p, respuestas, pregunta.get_codigo_especifique(), pregunta.get_codigo_especial(), null, pregunta.get_ayuda());
                        break;
                    }
                    // MEJORAR - REVISAR...
//                    case Consulta: {
//                        Informante informante = new Informante();
//                        if(informante.abrir(idPadre)){
//                            Map<Integer, String> respuestas = Respuesta.getRespuestasQuery(idInformante.id_asignacion, idInformante.correlativo, idPadre.id_asignacion, idPadre.correlativo, Informante.getUpm(idInformante), informante.get_codigo(), informante.get_descripcion(), pregunta.get_variable());
//                            pregs[i] = new Cerrada(this, pregunta.get_id_pregunta(), pregunta.get_id_seccion(), pregunta.get_codigo_pregunta(), p, respuestas, pregunta.get_codigo_especifique(), pregunta.get_codigo_especial(), null, pregunta.get_ayuda());
//                            informante.free();
//                        } else {
//                            errorMessage("finish", "Error!", Html.fromHtml("No se logró abrir el informante (Error al crear la pregunta CONSULTA)"), Parametros.FONT_OBS);
//                        }
//                        break;
//                    }
                }
                if (encuesta.abrir(idInformante.where() + " AND id_pregunta = " + pregunta.get_id_pregunta() + " AND fila = 1", null)) {
                    try {
                        switch (pregunta.get_id_tipo_pregunta()) {
                            case Upm:
                            case UpmHijo:
                            case Consulta:
                            case CerradaOmitida:
                            case Cerrada:
//                                pregs[i].setIdResp(encuesta.get_id_respuesta());
                                break;
                            case Multiple:
                            case Autocompletar:
                                pregs[i].setCodResp(encuesta.get_codigo_respuesta());
                                break;
                        }
                        pregs[i].setResp(encuesta.get_respuesta());
                        pregs[i].setObservacion(encuesta.get_observacion());
//                        pregs[i].setEstado(encuesta.get_apiestado());
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
//                if (RolPermiso.tienePermiso(Usuario.getRol(), "testing")) {
//                    pregs[i].setOnLongClickListener(this);
//                }

                encuesta.free();
                layout.addView(pregs[i]);
                buttons[i] = new Button(this);
                buttons[i].setText("Observación");
                buttons[i].setTag(i);
                buttons[i].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        indice = (Integer) view.getTag();
//                        observationMessage("obs", "Introduzca observación", Html.fromHtml("Escriba la nota:"), pregs[indice].getObservacion(), false);
                    }
                });

//                if ( i== 0 || i== 1 || i== 2 ) {
//                    layout.addView(buttons[i]);
//                }
                View separador = new View(this);
                separador.setBackgroundColor(getResources().getColor(R.color.colorSecondary_text));
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                lp.height=1;
                layout.addView(separador, lp);

                i++;
            } while (pregunta.siguiente());

            inhabilitaPreguntas();
            pregs[0].setFocus();
        } else {
            errorMessage(this, "finish", "Error!", Html.fromHtml("No se encontraron preguntas."), Parametros.FONT_OBS);
        }
        pregunta.free();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(toogle.onOptionsItemSelected(item)){
            return true;
        }
//
//        switch (item.getItemId()) {
//            case android.R.id.home:
//                drawerLayout.openDrawer(GravityCompat.START);
//                return true;
//        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("unused")
    public void obs() {
        pregs[indice].setObservacion(observation);
    }

    @SuppressWarnings("unused")
//    public void detallePreguntaFlujo() {
//        StringBuilder str = new StringBuilder();
//        switch (id){
//            case 1:
//                str.append("<b>ID - CODIGO: </b>").append(tempPregunta.get_id_pregunta()+" - "+tempPregunta.get_codigo_pregunta()).append("<br>");
//                str.append("<b>PREGUNTA SIN ETIQUETAS: </b>").append(Html.fromHtml(tempPregunta.get_pregunta()).toString()).append("<br>");
//                str.append("<b>TIPO PREGUNTA: </b>").append(tempPregunta.get_id_tipo_pregunta().toString()).append("<br>");
//                str.append("<b>MINIMO - MAXIMO: </b>").append(tempPregunta.get_minimo()+" - "+tempPregunta.get_maximo()).append("<br>");
//                str.append("<b>LONGITUD: </b>").append(tempPregunta.get_longitud()).append("<br>");
//                str.append("<b>AYUDA: </b>").append(tempPregunta.get_ayuda()).append("<br>");
//                str.append("<b>CATALOGO: </b>").append(tempPregunta.get_catalogo()).append("<br>");
//                str.append("<b>VARIABLE: </b>").append(tempPregunta.get_variable()).append("<br>");
//                str.append("<b>BUCLE - VARIABLE BUCLE: </b>").append(tempPregunta.get_bucle()+" - "+tempPregunta.get_variable_bucle()).append("<br>");
//                str.append("<b>CODIGO ESPECIAL: </b>").append(tempPregunta.get_codigo_especial()).append("<br>");
//                str.append("<b>FORMULA: </b>").append(tempPregunta.get_formula()).append("<br>");
//                str.append("<b>REGLA: </b>").append(tempPregunta.get_regla()==null?null:tempPregunta.get_regla().replace("<","&lt;").replace(">","&gt;")).append("<br>");
//                str.append("<b>MENSAJE: </b>").append(tempPregunta.get_mensaje()).append("<br>");
//                str.append("<b>USUCRE - FECCRE: </b>").append(tempPregunta.get_usucre()+" - "+Movil.dateExtractor(tempPregunta.get_feccre())).append("<br>");
//                str.append("<b>USUMOD - FECMOD: </b>").append(tempPregunta.get_usumod()+" - "+Movil.dateExtractor(tempPregunta.get_fecmod())).append("<br>");
//                informationMessage(null, "PREGUNTA", Html.fromHtml(str.toString()), Parametros.FONT_OBS);
//                break;
//            case 2:
//                Flujo flujo = new Flujo();
//                Pregunta preg_aux = new Pregunta();
//
//                //OBTIENE LA PREGUNTA DIRECTA SIGUIENTE
//                String siguienteDirecta = "";
//                if(preg_aux.abrir(flujo.siguienteDirecta(tempPregunta.get_id_pregunta()))) {
//                    siguienteDirecta = preg_aux.get_codigo_pregunta()+" ("+preg_aux.get_id_pregunta()+")";
//                } else {
//                    siguienteDirecta = "No se encontró la pregunta.";
//                }
//                //OBTIENE LOS POSIBLES DESTINOS
//                StringBuilder strFlujoDestino = new StringBuilder();
//                for( Map<String, Object> map1: flujo.flujoDestino(tempPregunta.get_id_pregunta())) {
//                    Pregunta p1 = new Pregunta();
//                    String codPreg = p1.abrir((Integer) map1.get("id_pregunta_destino"))? p1.get_codigo_pregunta() : "";
//                    if(codPreg.equals("")) {
//                        strFlujoDestino .append("NO SE ENCONTRO LA PREGUNTA DEL FLUJO<br>");
//                    } else {
//                        strFlujoDestino.append("<font color='blue'><b>").append(map1.get("orden") + ". DESTINO: ").append(codPreg).append(" (").append(map1.get("id_pregunta_destino")+")</b></font><br>");
//                        strFlujoDestino.append(map1.get("regla").toString().replace("<","&lt;").replace(">","&gt;")).append("<br>");
//                    }
//                }
//                //OBTIENE LOS POSIBLES ORIGENES
//                StringBuilder strFlujoOrigen = new StringBuilder();
//                for( Map<String, Object> map2: flujo.flujoOrigen(tempPregunta.get_id_pregunta())) {
//                    Pregunta p2 = new Pregunta();
//                    String codPreg = p2.abrir((Integer) map2.get("id_pregunta"))? p2.get_codigo_pregunta() : "";
//                    if(codPreg.equals("")) {
//                        strFlujoOrigen .append("NO SE ENCONTRO LA PREGUNTA DEL FLUJO<br>");
//                    } else {
//                        strFlujoOrigen.append("<font color='red'><b>").append(map2.get("orden") + ". ORIGEN: ").append(codPreg).append(" (").append(map2.get("id_pregunta_destino")+")</b></font><br>");
//                        strFlujoOrigen.append(map2.get("regla").toString().replace("<","&lt;").replace(">","&gt;")).append("<br>");
//                    }
//                }
//                str.append("<b>PREGUNTA ANTERIOR: </b>").append("No hay preguntas anteriores").append("<br>");
//                str.append("<b>SIGUIENTE PREGUNTA (CORRELATIVA): </b>").append(siguienteDirecta).append("<br>");
//                str.append("<b>SALTOS A OTRAS PREGUNTAS: </b><br>").append(strFlujoDestino.toString()==""? "SIN FLUJOS" : strFlujoDestino).append("<br>");
//                str.append("<b>SALTOS DESDE OTRAS PREGUNTAS: </b><br>").append(strFlujoOrigen.toString()==""? "SIN FLUJOS" : strFlujoOrigen).append("<br>");
//                informationMessage(null, "FLUJOS ("+tempPregunta.get_codigo_pregunta()+")", Html.fromHtml(str.toString()), Parametros.FONT_OBS);
//                break;
//            case 3:
//                int i;
//                for (i=0 ; i<pregs.length; i++) {
//                    if(tempPregunta.get_id_pregunta() == pregs[i].getId()) {
//                        break;
//                    }
//                }
//                str.append("<b>TIPO PREGUNTA: </b>").append(tempPregunta.get_id_tipo_pregunta().toString()).append("<br>");
//                str.append("<b>respuesta: </b>").append(pregs[i].getResp()).append("<br>");
//                str.append("<b>codigo_respuesta: </b>").append(pregs[i].getCodResp()).append("<br>");
//                str.append("<b>id_respuesta: </b>").append(pregs[i].getIdResp()).append("<br>");
//                str.append("<b>estado: </b>").append(pregs[i].getEstado()).append("<br>");
//                informationMessage(null, "RESPUESTAS", Html.fromHtml(str.toString()), Parametros.FONT_OBS);
//                break;
//            case 4:
//                if (tempPregunta.get_regla() != null) {
//                    ArrayList<String> map = dibujaPila(tempPregunta.get_rpn(), cargar());
//
//                    str.append("<b>REGLA: </b>").append(tempPregunta.get_regla().replace("<","&lt;").replace(">","&gt;")).append("<br>");
//                    str.append("<b>RPN: </b>").append(tempPregunta.get_rpn().replace("<","&lt;").replace(">","&gt;")).append("<br><br>");
//                    for (String val : map) {
//                        String part[] = val.split("\\¡");
//                        if (part[0].equals(part[1])) {
//                            str.append("<b>"+part[0].replace("<","&lt;").replace(">","&gt;")+"</b><br>");
//                        } else {
//                            str.append("<b>"+part[0].replace("<","&lt;").replace(">","&gt;")+": </b>").append(part[1]).append("<br>");
//                        }
//                    }
//                    str.append("<font color='blue'><b>").append("RESULTADO: ").append(evaluar(tempPregunta.get_rpn(), cargar())).append("</b></font>");
//                    informationMessage(null, "CONSISTENCIA", Html.fromHtml(str.toString()), Parametros.FONT_OBS);
//                } else {
//                    errorMessage(null, "CONSISTENCIA", Html.fromHtml("La pregunta no realiza el análisis de consistencias"), Parametros.FONT_OBS);
//                }
//                break;
//            case 5:
//                informationMessage(null, "EN DESARROLLO...", Html.fromHtml("Esta opción aun no fue implementada"), Parametros.FONT_OBS);
//                break;
//        }
//        tempPregunta.free();
//    }

    public void inhabilitaPreguntas() {
        if (idNivel == 0) {
            int idPreguntaVoe = Pregunta.getIDpregunta(0, TipoPregunta.NumeroVivienda);
            for (int i = 0; i<pregs.length; i++) {
                if (pregs[i].getId() == idPreguntaVoe) {
                    PreguntaView.setViewAndChildrenEnabled(pregs[i], false);
                    break;
                }
            }
        }
    }

    private void insertaRespuestas() {
        if (idNivel == 0) {
            int idPreguntaVoe = Pregunta.getIDpregunta(0, TipoPregunta.NumeroVivienda);
            for (int i = 0; i<pregs.length; i++) {
                if (pregs[i].getId() == idPreguntaVoe) {
                    pregs[i].setEstado(Estado.INSERTADO);
                    if (pregs[10].getResp().equals("1")) {
                        pregs[i].setResp("1");
                    } else {
                        pregs[i].setResp("0");
                    }
                    break;
                }
            }
        }
    }

    public void guardar() {
        insertaRespuestas();
        try {
            Map<String, String> valores = null;
            // BUCLE QUE EVALUA LAS PREGUNTAS Y VERIFICA CONSISTENCIAS
            for (PreguntaView p : pregs) {
                if (p.getCodResp().equals("-1") || p.getCodResp().equals("-1.0")) {
                    errorMessage(this, null, "Error!", Html.fromHtml("Faltó responder: "+p.getCod()), Parametros.FONT_OBS);
                    p.setFocus();
                    return;
                } else {
                    Pregunta pregunta = new Pregunta();
                    if (pregunta.abrir(p.getId())) {
                        if (p.getEstado() != Estado.NOAPLICA && p.getEstado() != Estado.SENIEGA && p.getEstado() != Estado.NOSABE) {
                            if (p instanceof Numero || p instanceof Decimal) {
                                try {
                                    if ((Float.parseFloat(p.getCodResp()) < pregunta.get_minimo() && pregunta.get_minimo() > 0) || (Float.parseFloat(p.getCodResp()) > pregunta.get_maximo() && pregunta.get_maximo() > 0)) {
                                        errorMessage(this, null, "Error!", Html.fromHtml(String.format("El valor de "+pregunta.get_codigo_pregunta()+" debe estar entre %d y %d", pregunta.get_minimo(), pregunta.get_maximo())), Parametros.FONT_OBS);
                                        p.setFocus();
                                        return;
                                    }
                                } catch (Exception ex) {
                                    errorMessage(this, null, "Error!", Html.fromHtml(String.format("El valor de "+pregunta.get_codigo_pregunta()+" debe estar entre %d y %d", pregunta.get_minimo(), pregunta.get_maximo())), Parametros.FONT_OBS);
                                    p.setFocus();
                                    return;
                                }
                            } else if (p instanceof Abierta || p instanceof Memoria || p instanceof Autocompletar) {
                                if (p.getResp().length() < pregunta.get_minimo() && pregunta.get_minimo() > 0 || (p.getResp().length() > pregunta.get_maximo() && pregunta.get_maximo() > 0)) {
                                    errorMessage(this, null, "Error!", Html.fromHtml(String.format("La longitud de "+pregunta.get_codigo_pregunta()+" debe estar entre %d y %d", pregunta.get_minimo(), pregunta.get_maximo())+" (Valor actual: "+p.getResp().length()+")"), Parametros.FONT_OBS);
                                    p.setFocus();
                                    return;
                                }
                            }
                        }
//                        if (pregunta.get_rpn() != null) {
//                            if (valores == null) {
//                                valores = cargar();
//                            }
//                            if (evaluar(pregunta.get_rpn(), valores) == 0) {
//                                errorMessage(this, null, "Error de consistencia!", Html.fromHtml(pregunta.get_mensaje().substring(6)), Parametros.FONT_OBS);
//                                p.setFocus();
//                                return;
//                            }
//                        }
                    } else {
                        errorMessage(this, null, "Error!", Html.fromHtml("No existe la pregunta."), Parametros.FONT_OBS);
                        return;
                    }
                    pregunta.free();
                }
            }

            Informante informante = new Informante();
            boolean flag = false;
            if (idInformante.correlativo == 0) {
                // CREA UN NUEVO INFORMANTE - SE REGISTRA LA LATITUD Y LONGITUD EN LA CREACION DEL INFORMANTE
                flag = informante.nuevo();
                informante.set_latitud(Movil.getGPS().split(";")[0].toString());
                informante.set_longitud(Movil.getGPS().split(";")[1].toString());
            }
            else {
                // SE EDITA UN INFORMANTE EXISTENTE
                informante.abrir(idInformante);
            }
            if (flag || informante.editar()) {
                // LOGRA ACCEDERSE AL INFORMANTE
                informante.set_id_informante(idInformante);
                informante.set_id_usuario(Usuario.getUsuario());
                if (idPadre.id_asignacion > 0) {
                    informante.set_id_informante_padre(idPadre);
                }
                informante.set_id_nivel(idNivel);

                // GENERA EL CODIGO (EL CODIGO SOLAMENTE SE GENERA PARA LAS BOLETAS DE NIVEL 1)
                String codigo;
                if (idNivel == 1) {
                    // QUEMADO - OBTIENE EL CODIGO DE UPM COMO VARIABLE DE CAT_UPM
                    /*if (flag) {
                        codigo = Upm.getCodigo(idInformante.id_asignacion);
                    } else {
                        Upm upm = new Upm();
                        if (informante.get_id_upm() == null) {
                            if (upm.abrir(Informante.getUpm(idInformante))) {
                                codigo = upm.get_codigo();
                            } else {
                                errorMessage(null, "Error!", Html.fromHtml("No se encontró la UPM. Error al generar el código."), Parametros.FONT_OBS);
                                return;
                            }
                        } else {
                            if (upm.abrir(informante.get_id_upm())) {
                                codigo = upm.get_codigo();
                            } else {
                                errorMessage(null, "Error!", Html.fromHtml("No se encontró la UPM. Error al generar el código."), Parametros.FONT_OBS);
                                return;
                            }
                        }
                        upm.free();
                    }*/

                    // BUSCA UNA PREGUNTA DE TIPO UPM EN LAS PREGUNTAS MOSTRAR_VENTANA = 1 PARA UTILIZARLA COMO BASE EN LA CREACION DEL FOLIO
                    /*codigo = Upm.getCodigo(idInformante.id_asignacion
                    int idPreguntaUpm = Pregunta.getIDpregunta(1, TipoPregunta.Upm);
                    for (int i = 0; i<pregs.length; i++) {
                        if (pregs[i].getId() == idPreguntaUpm) {
                            codigo = pregs[i].getCodResp();
                            break;
                        }
                    }*/

                    // OBTIENE EL VALOR DESDE UNA PREGUNTA ESPECIFICA
                    codigo = "";

                    if (codigo == null) {
                        errorMessage(this, null, "Error!", Html.fromHtml("No se encontró la UPM. Error al generar el código."), Parametros.FONT_OBS);
                        return;
                    } else {
                        // EH-17
//                        switch (pregs[0].getCodResp().length()) {
//                            case 1:
//                                codigo = codigo + "-00" + pregs[0].getCodResp();
//                                break;
//                            case 2:
//                                codigo = codigo + "-0" + pregs[0].getCodResp();
//                                break;
//                            default:
//                                codigo = codigo + "-" + pregs[0].getCodResp();
//                                break;
//                        }
                        // EFSR y EFVG
//                        codigo = codigo + "/" + pregs[0].getCodResp()+ "/" + pregs[1].getCodResp();
                        // ECP5
                        /*codigo = codigo + "/" + pregs[0].getCodResp()+ "/" + pregs[1].getCodResp();*/
                    }
                    if (Informante.exists(codigo, idInformante)) {
                        informante.free();
                        errorMessage(this, null, "Error!", Html.fromHtml("El folio ya existe."), Parametros.FONT_OBS);
                        return;
                    }
                    // QUEMADO - EN UNA ENCUESTA QUE HAGA USO DEL FORMULARIO DEL LISTADO DE VIVIENDAS, PERMITE LA VERIFICACIÓN DEL VOE DEPENDIENDO DE LA CANTIDAD DE VIVIENDAS OCUPADAS DECLARADA
                    /*if( RolPermiso.tienePermiso(Usuario.getRol(), "control_asignacion") && (Integer.parseInt(pregs[6].getCodResp()) == 1 || Integer.parseInt(pregs[6].getCodResp()) == 3) ) {
                        Set<Integer> seleccion = Seleccion.seleccion(0, Log.nroViviendasUPM(Asignacion.get_asignacion(Informante.getUpm(idInformante), Usuario.getUsuario())));
                        int idPreguntaNroVivienda = Pregunta.getIDpregunta(1, TipoPregunta.NumeroVivienda);
                        int resp = 0;
                        for (int j = 0; j<pregs.length; j++) {
                            if ( pregs[j].getId() == idPreguntaNroVivienda ) {
                                resp = Integer.parseInt(pregs[j].getCodResp());
                                break;
                            }
                        }
                        if ( !seleccion.contains(resp) ) {
                            informante.free();
                            errorMessage(null, "Error!", Html.fromHtml("El 'NUMERO DE LA VIVIENDA' no es aceptado, Debe introducir:<br>"+seleccion.toString()), Parametros.FONT_OBS);
                            return;
                        }
                    }*/
                } else { // CODIGO PARA NIVEL 2
                    if (flag) {
                        codigo = "999";
                    } else {
                        codigo = informante.get_codigo();
                    }
                }
                informante.set_codigo(codigo);
                // TERMINA DE GENERAR EL CODIGO

                informante.set_id_upm(Informante.getUpm(idInformante));
                informante.set_descripcion(pregs[0].getResp());

                if (flag) {
                    informante.set_usucre(Usuario.getLogin());
                } else {
                    informante.set_usumod(Usuario.getLogin());
                    informante.set_fecmod(System.currentTimeMillis() / 1000);
                }
                switch (idNivel) {
                    case 2:
                        idInformante = informante.guardarLV(idInformante.id_asignacion);
                        break;
                    case 3:
                        idInformante = (IdInformante)informante.guardar();
                        break;
                    default:
                        idInformante = informante.guardar(idPadre);
                        break;
                }
                if (idInformante != null) {
                    String descripcion = "";
                    for (PreguntaView preg : pregs) {
                        if (encuesta.abrir(idInformante.where() + " AND id_pregunta = " + preg.getId() + " AND fila = 1", null)) {
                            encuesta.editar();
                        } else {
                            encuesta.nuevo();
                        }
                        encuesta.set_id_encuesta(new IdEncuesta(idInformante.id_asignacion, idInformante.correlativo, preg.getId(), 1));
//                        encuesta.set_id_respuesta(preg.getIdResp());
                        encuesta.set_codigo_respuesta(preg.getCodResp());
                        encuesta.set_respuesta(preg.getResp());
                        encuesta.set_observacion(preg.getObservacion());
                        encuesta.set_usucre(Usuario.getLogin());
//                        encuesta.set_apiestado(preg.getEstado());
                        encuesta.set_latitud(Movil.getGPS().split(";")[0].toString());
                        encuesta.set_longitud(Movil.getGPS().split(";")[1].toString());
                        encuesta.guardar();
                        if (!(preg.getCodResp().equals("997") || preg.getCodResp().equals("998") || preg.getCodResp().equals("999"))) {
                            descripcion = descripcion + preg.getResp() + " ";
                        }
                    }
                    finish();
                    encuesta.free();
                    if (informante.abrir(idInformante)) {
                        if (informante.editar()) {
                            informante.set_codigo(idInformante.id_asignacion+"-"+idInformante.correlativo);
                            informante.set_descripcion(descripcion);
                            informante.guardar();
                        }
                    }
                    int idPregunta = Encuesta.ultimaInicial(idInformante);
                    Siguiente idSiguiente = new Flujo().siguiente(idInformante, idPregunta);
                    if (idSiguiente.idSiguiente > 0) {
                        Pregunta pregunta = new Pregunta();
                        if (pregunta.abrir(idSiguiente.idSiguiente)) {
                            if (idNivel == 0 || idNivel == -1) {
                                accionFinalBoleta();
                            } else {
                                if (informante.abrir(idInformante)) {
                                    if (informante.get_id_upm() == null) {
//                                        irInformante(Informante.getUpm(idInformante), idNivel, idPadre);
                                    } else {
                                        if(idNivel == 1) {
                                            // SI ES NIVEL 1 CONTINUA LA ENCUESTA
//                                            irEncuesta(new IdEncuesta(idInformante.id_asignacion, idInformante.correlativo, idSiguiente.idSiguiente, idSiguiente.fila), idNivel);
                                        } else if (idNivel == 2) {
                                            // SI ES NIVEL 2, RETORNA AL LISTADO DE MIEMBROS
//                                            irInformante(informante.get_id_upm(), idNivel, idPadre);
                                        }
                                    }
                                } else {
                                    errorMessage(this, null, "Error!", Html.fromHtml("No se encontró el informante."), Parametros.FONT_OBS);
                                }
                                finish();
                            }
                        } else {
                            errorMessage(this, null, "Error!", Html.fromHtml("No se encontró la pregunta."), Parametros.FONT_OBS);
                        }
                        pregunta.free();
                    } else {
                        errorMessage(this, null, "Error!", Html.fromHtml("Final inesperado."), Parametros.FONT_OBS);
                    }
                } else {
                    errorMessage(this, null, "Error!", Html.fromHtml("No se pudo guardar."), Parametros.FONT_OBS);
                }
            } else {
                errorMessage(this, null, "Error!", Html.fromHtml("No se pudo crear el informante."), Parametros.FONT_OBS);
            }
            informante.free();
        } catch (Exception ex) {
            errorMessage(this, null, "Error!", Html.fromHtml(ex.getMessage()), Parametros.FONT_OBS);
        }
    }

    private void accionFinalBoleta() {
        /*if (RolPermiso.tienePermiso(Usuario.getRol(), "consolidacion_automatica")) {
            if (Movil.isNetworkAvailable()) {
                String file = DataBase.backup(idInformante.id_asignacion+"-"+idInformante.correlativo);
                if (file == null) {
                    errorMessage(null, "Error!", Html.fromHtml("La boleta finalizó, pero no se pudo consolidar. Prueba la consolidación desde el menú principal."), Parametros.FONT_OBS);
                } else {
                    try {
                        startThree();
                        successMethod = "vivienda";
                        errorMethod = "vivienda";
                        finalMethod = null;
                        new UploadHttpFile().execute(Parametros.URL_UPLOAD_SICE, Parametros.DIR_BAK, file, "Cabecera creada con éxito. La información de "+Usuario.getLogin()+" se subió correctamente.");
                    } catch (Exception ex) {
                        errorMessage("vivienda", "Error!", Html.fromHtml(ex.getMessage()), Parametros.FONT_OBS);
                    }
                }
            } else {
                errorMessage("vivienda", "Fin", Html.fromHtml("La cabecera se creó, pero no se pudo consolidar. Prueba la consolidación desde el menú principal cuando tengas conexión a Internet."), Parametros.FONT_OBS);
            }
        } else {*/
            vivienda();
            //informationMessage(null, "Fin", Html.fromHtml(getString(R.string.cabecera_creada)), Parametros.FONT_OBS);
        //}
    }

    public void vivienda (){
        irListadoViviendas(Informante.getUpm(idInformante));
        finish();
    }

    private Map<String,String> cargar() {
        Map<String,String> res = new HashMap<>();
        for (PreguntaView p : pregs) {
            res.put(p.getCod(), p.getCodResp());
        }
        return res;
    }

    private float evaluar(String rpn, Map<String, String> valores) {
        LinkedList<Float> pila = new LinkedList<>();
        float val1, val2;
        String[] elements = rpn.split(" ");
        for (String element : elements) {
            byte fl = (byte) element.charAt(0);
            if (fl > 64 && fl < 91) {
                switch (element) {
                    case "CODIGO": {
                        if (idInformante.correlativo == 0) {
                            pila.add(Informante.getContar(idPadre) + 1);
                        } else {
//                            pila.add(Informante.getCodigo(idInformante));
                        }
                        break;
                    }
                    case "CONTAR": {
                        if (idInformante.correlativo == 0) {
                            pila.add(Informante.getContar(idPadre) + 1);
                        } else {
                            pila.add(Informante.getContar(idPadre));
                        }
                        break;
                    }
                    case "SORTEADO": {
                        if (Seleccion.hasSelected(Informante.getUpm(idInformante))) {
                            pila.add(1f);
                        } else {
                            pila.add(0f);
                        }
                        break;
                    }
                    default: {
                        if (element.endsWith(".INCIDENCIA")) {
                            pila.add(Float.valueOf(UpmHijo.getIncidenciaUpmHijo(valores.get(element.replace(".INCIDENCIA", "")))));
                        } else{
                            if ( valores.get(element)==null ) {
                                String[] conjunto = new String[]{};
                                conjunto = Encuesta.obtenerCod(idPadre, element, 1).split(";");
                                if (conjunto[0].equals("")) {
                                    conjunto = new String[]{"0,0"};
                                }
                                String val = conjunto[0].split(",")[0];
                                if (val == null || val.equals("")) {
                                    pila.add(0f);
                                } else {
                                    pila.add(Float.valueOf(val));
                                }
                            } else {
                                pila.add(Float.valueOf(valores.get(element)));
                            }
                        }
                        break;
                    }
                }
            } else {
                if (fl > 47 && fl < 58) {
                    pila.add(Float.parseFloat(element));
                } else {
                    val2 = pila.pollLast();
                    if (element.equals("!")) {
                        pila.add((float) ((val2 == 0) ? 1 : 0));
                    } else {
                        val1 = pila.pollLast();
                        switch (element) {
                            case "+":
                                pila.add(val1 + val2);
                                break;
                            case "-":
                                pila.add(val1 - val2);
                                break;
                            case "*":
                                pila.add(val1 * val2);
                                break;
                            case "/":
                                pila.add(val1 / val2);
                                break;
                            case "%":
                                pila.add(val1 % val2);
                                break;
                            case "^":
                                pila.add((float) Math.pow(val1, val2));
                                break;
                            case "=":
                            case "==":
                                pila.add((float) ((val1 == val2) ? 1 : 0));
                                break;
                            case "!=":
                                pila.add((float) ((val1 != val2) ? 1 : 0));
                                break;
                            case "<=":
                                pila.add((float) ((val1 <= val2) ? 1 : 0));
                                break;
                            case ">=":
                                pila.add((float) ((val1 >= val2) ? 1 : 0));
                                break;
                            case "<":
                                pila.add((float) ((val1 < val2) ? 1 : 0));
                                break;
                            case ">":
                                pila.add((float) ((val1 > val2) ? 1 : 0));
                                break;
                            case "&&":
                                pila.add((float) ((val1 > 0 && val2 > 0) ? 1 : 0));
                                break;
                            case "||":
                                pila.add((float) ((val1 + val2 > 0) ? 1 : 0));
                                break;
                        }
                    }
                }
            }
        }
        return pila.getLast();
    }

    private ArrayList<String> dibujaPila(String rpn, Map<String, String> valores) {
        ArrayList<String> resultado = new ArrayList<>();
        //LinkedList<Float> pila = new LinkedList<>();
        float val1, val2;
        String[] elements = rpn.split(" ");
        for (String element : elements) {
            byte fl = (byte) element.charAt(0);
            if (fl > 64 && fl < 91) {
                switch (element) {
                    case "CODIGO": {
                        if (idInformante.correlativo == 0) {
                            //pila.add(Informante.getContar(idPadre) + 1);
                            resultado.add(element+"¡"+String.valueOf(Informante.getContar(idPadre) + 1));
                        } else {
                            //pila.add(Informante.getCodigo(idInformante));
                            resultado.add(element+"¡"+String.valueOf(Informante.getCodigo(idInformante)));
                        }
                        break;
                    }
                    case "CONTAR": {
                        if (idInformante.correlativo == 0) {
                            //pila.add(Informante.getContar(idPadre) + 1);
                            resultado.add(element+"¡"+String.valueOf(Informante.getContar(idPadre) + 1));
                        } else {
                            //pila.add(Informante.getContar(idPadre));
                            resultado.add(element+"¡"+String.valueOf(Informante.getContar(idPadre)));
                        }
                        break;
                    }
                    case "SORTEADO": {
                        if (Seleccion.hasSelected(Informante.getUpm(idInformante))) {
                            //pila.add(1f);
                            resultado.add(element+"¡1");
                        } else {
                            //pila.add(0f);
                            resultado.add(element+"¡0");
                        }
                        break;
                    }
                    default: {
                        if (element.endsWith(".INCIDENCIA")) {
                            //pila.add(Float.valueOf(UpmHijo.getIncidenciaUpmHijo(valores.get(element.replace(".INCIDENCIA", "")))));
                            resultado.add(element+"¡"+String.valueOf(UpmHijo.getIncidenciaUpmHijo(valores.get(element.replace(".INCIDENCIA", "")))));
                        } else{
                            //pila.add(Float.valueOf(valores.get(element)));
                            resultado.add(element+"¡"+valores.get(element));
                        }
                        break;
                    }
                }
            } else {
                resultado.add(element+"¡"+String.valueOf(element));
            }
        }
        return resultado;
    }

    @Override
    public void onBackPressed() {
        Asignacion asignacion = new Asignacion();
        asignacion.abrir(idInformante.id_asignacion);
        if (idNivel == 1 || idNivel == 2) {
            irListadoViviendas(asignacion.get_id_upm());
        } else {
//            irInformante(asignacion.get_id_upm(), idNivel, idPadre);
        }
        asignacion.free();
        finish();
    }

//    @Override
//    public boolean onLongClick(View v) {
//        tempPregunta = new Pregunta();
//        if (tempPregunta.abrir(v.getId())) {
//            Map<Integer, String> map = new LinkedHashMap<>();
//            map.put(1, "VER DETALLE DE PREGUNTA");
//            map.put(2, "VER DETALLE DE FLUJOS");
//            map.put(3, "VER RESPUESTAS ACTUALES");
//            map.put(4, "VER ANALISIS DE CONSISTENCIAS");
//            /*map.put(5, "VER ANALISIS DE FLUJOS");*/
//            StringBuilder str = new StringBuilder();
//            str.append("<b>ID - CODIGO: </b>").append(tempPregunta.get_id_pregunta() + " - " + tempPregunta.get_codigo_pregunta()).append("<br>");
//            str.append("<b>USUCRE - FECCRE: </b>").append(tempPregunta.get_usucre() + " - " + Movil.dateExtractor(tempPregunta.get_feccre())).append("<br>");
//            str.append("<b>USUMOD - FECMOD: </b>").append(tempPregunta.get_usumod() + " - " + tempPregunta.get_fecmod() == null || tempPregunta.get_fecmod().equals("") ? "" : Movil.dateExtractor(tempPregunta.get_fecmod())).append("<br>");
//            selectionMessage("detallePreguntaFlujo", "Selección", Html.fromHtml(str.toString()), map, 1);
//        } else {
//            errorMessage(null, "Error!", Html.fromHtml("No se pudo encontrar la pregunta"), Parametros.FONT_OBS);
//        }
//        return true;
//    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
//            case R.id.botonAnterior:
//                Asignacion asignacion = new Asignacion();
//                asignacion.abrir(idInformante.id_asignacion);
//                if (idNivel == 0 || idNivel == -1) {
//                    irListadoViviendas(asignacion.get_id_upm());
//                } else {
//                    if (idInformante.correlativo == 0) {
////                        irInformante(asignacion.get_id_upm(), idNivel, idPadre);
//                    } else {
//                        Informante informante = new Informante();
//                        if (informante.abrir(idInformante)) {
//                            if (idNivel == 2) {
////                                irInformante(asignacion.get_id_upm(), idNivel, idPadre);
//                            } else {
////                                irInformante(informante.get_id_upm(), idNivel, idPadre);
//                            }
//                        } else {
////                            irInformante(asignacion.get_id_upm(), idNivel, idPadre);
//                        }
//                        informante.free();
//                    }
//                }
//                asignacion.free();
//                finish();
////                return true;
//                break;
//            case R.id.botonGuardar:
//                guardar();
//                break;
        }
    }

    private void setupNavigationDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {

                        }
                        return true;
                    }
                });
    }

}
