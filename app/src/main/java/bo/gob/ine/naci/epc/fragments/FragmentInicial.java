package bo.gob.ine.naci.epc.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.udojava.evalex.Expression;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import bo.gob.ine.naci.epc.ListadoViviendasActivity;
import bo.gob.ine.naci.epc.MyApplication;
import bo.gob.ine.naci.epc.R;
import bo.gob.ine.naci.epc.adaptadores.IComunicaFragments;
import bo.gob.ine.naci.epc.entidades.Asignacion;
import bo.gob.ine.naci.epc.entidades.Catalogo;
import bo.gob.ine.naci.epc.entidades.Encuesta;
import bo.gob.ine.naci.epc.entidades.Estado;
import bo.gob.ine.naci.epc.entidades.IdEncuesta;
import bo.gob.ine.naci.epc.entidades.IdInformante;
import bo.gob.ine.naci.epc.entidades.Informante;
import bo.gob.ine.naci.epc.entidades.Pregunta;
import bo.gob.ine.naci.epc.entidades.Seccion;
import bo.gob.ine.naci.epc.entidades.Seleccion;
import bo.gob.ine.naci.epc.entidades.Siguiente;
import bo.gob.ine.naci.epc.entidades.TipoPregunta;
import bo.gob.ine.naci.epc.entidades.Upm;
import bo.gob.ine.naci.epc.entidades.UpmHijo;
import bo.gob.ine.naci.epc.entidades.Usuario;
import bo.gob.ine.naci.epc.herramientas.ActionBarActivityMessage;
import bo.gob.ine.naci.epc.herramientas.Movil;
import bo.gob.ine.naci.epc.herramientas.Parametros;
import bo.gob.ine.naci.epc.preguntas.Abierta;
import bo.gob.ine.naci.epc.preguntas.Autocompletar;
import bo.gob.ine.naci.epc.preguntas.Autocompletar2;
import bo.gob.ine.naci.epc.preguntas.Calendario;
import bo.gob.ine.naci.epc.preguntas.Cantidad;
import bo.gob.ine.naci.epc.preguntas.Cerrada;
import bo.gob.ine.naci.epc.preguntas.CerradaInformante;
import bo.gob.ine.naci.epc.preguntas.CerradaMatriz;
import bo.gob.ine.naci.epc.preguntas.CerradaOmitida;
import bo.gob.ine.naci.epc.preguntas.Decimal;
import bo.gob.ine.naci.epc.preguntas.Fecha;
import bo.gob.ine.naci.epc.preguntas.Formula;
import bo.gob.ine.naci.epc.preguntas.Foto;
import bo.gob.ine.naci.epc.preguntas.Gps;
import bo.gob.ine.naci.epc.preguntas.HoraMinuto;
import bo.gob.ine.naci.epc.preguntas.Memoria;
import bo.gob.ine.naci.epc.preguntas.MesAnio;
import bo.gob.ine.naci.epc.preguntas.Multiple;
import bo.gob.ine.naci.epc.preguntas.Numero;
import bo.gob.ine.naci.epc.preguntas.PreguntaView;
import bo.gob.ine.naci.epc.preguntas.Prioridad;
import bo.gob.ine.naci.epc.preguntas.SeleccionKish;
import bo.gob.ine.naci.epc.preguntas.ValorTipo;
import bo.gob.ine.naci.epc.preguntas.ValorTipo2;
import bo.gob.ine.naci.epc.preguntas.ValorTipoGastos;
import bo.gob.ine.naci.epc.preguntas.ValorTipoMatriz;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentInicial#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentInicial extends Fragment implements View.OnTouchListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = "";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private View vista;
    private static LinearLayout layout;
    private ScrollView scrollView;
    private int seccionId;
    private Siguiente idSiguiente;
    private PreguntaView preg;
    private Encuesta encuesta;
    private static Informante informante;
    private Pregunta pregunta;

    private static IdInformante idInformante;
    private static IdInformante idPadre;
    private int nivel;
    private static int idNivel;
    private IdEncuesta idEncuesta;
    private static int idSeccion;
    private FloatingActionButton botonGuardar;

    private static PreguntaView[] pregs;
    private static Context context;
    private static View[] separadors;
    //    private Button[] buttons;
    private static Button[] buttons;
    private int indice = -1;
    private Animation animation;
    private boolean nuevo = false;

    private static Activity activity;
    private IComunicaFragments iComunicaFragments;
    private static ActionBarActivityMessage mensaje;

    private int idBase = -1;
    private static IdInformante idInformanteAnterior;
    private static int nroRevisita;
    private static int idUpmHijo;


    public FragmentInicial() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentInicial.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentInicial newInstance(String param1, String param2) {
        FragmentInicial fragment = new FragmentInicial();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        context = this.getContext();
        activity = this.getActivity();
        vista = inflater.inflate(R.layout.fragment_fragment_inicial, container, false);

        layout = (LinearLayout) vista.findViewById(R.id.layout_encuesta_inicial);

        mensaje = new ActionBarActivityMessage();

        Bundle bundle = getArguments();

        if (bundle != null) {
            idInformante = new IdInformante((int[]) bundle.getIntArray("IdInformante"));
            idPadre = new IdInformante((int[]) bundle.getIntArray("IdPadre"));
            idEncuesta = new IdEncuesta((int[]) bundle.getIntArray("IdEncuesta"));
            nivel = bundle.getInt("Nivel");
            idNivel = bundle.getInt("idNivel");
            idSeccion = bundle.getInt("IdSeccion");
            idUpmHijo = bundle.getInt("idUpmHijo");
            Asignacion a = new Asignacion();
            if (a.abrir(idInformante.id_asignacion)) {
                nroRevisita = a.get_revisita();
                a.free();
            }
            Informante informanteActual = new Informante();
            if (informanteActual.abrir(idInformante)) {
                idInformanteAnterior = informanteActual.get_id_informante_anterior();
                if (idInformanteAnterior != null)
                    idBase = 1;
                else if (informanteActual.get_id_id_informante_anterior() != null) {
//                    idIdInformanteAnterior=informanteActual.get_id_id_informante_anterior();
                    idBase = 2;
                }
                informanteActual.free();
            } else {
                idInformanteAnterior = new IdInformante(0, 0);
            }


        }
        encuesta = new Encuesta();

        botonGuardar = vista.findViewById(R.id.botonGuardar);
        botonGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                botonGuardar.setEnabled(false);
//                try {
//                    Thread.sleep(1000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
                guardar();

            }
        });

        evaluador();

        return vista;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof Activity) {
            this.activity = (Activity) context;
            iComunicaFragments = (IComunicaFragments) this.activity;
        }

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        iComunicaFragments = null;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int a = v.getId();
        return false;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private void evaluador() {
        if (idSeccion == Parametros.ID_SECCION_RESERVADA) {
            nuevaBoleta();
        } else {
//            graficador(idSeccion);
        }
    }

    private void graficador(int seccionActual) {
        dibujarSeccionCompleta(seccionActual);
    }

    private void nuevaBoleta() {
        nuevo = true;
        if (idNivel == Parametros.LV_CABECERA || idNivel == Parametros.LV_VIVIENDAS) {
            Seccion seccion = new Seccion();
            idSeccion = seccion.seccion_inicial(idNivel);
        } else {
//            Nivel nivel_inicial = new Nivel();
//            idNivel = nivel_inicial.idNivel_inicial(nivel);
            Seccion seccion = new Seccion();
            idSeccion = seccion.seccion_inicial(idNivel);
        }
        dibujarPregunta();
    }

    private void dibujarSeccionCompleta(int seccionActual) {
        do {
//            dibujarPregunta(idEncuesta);
        } while (pregunta.siguiente());
    }

    private void dibujarPregunta() {
        final Pregunta pregunta = new Pregunta();
//        animation = AnimationUtils.loadAnimation(getContext(), R.anim.item_anim_from_right);
//        LayoutAnimationController controller = new LayoutAnimationController(animation, 0.1f);
//        layout.setLayoutAnimation(controller);

//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    getActivity().runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
        Log.d("IdSeccion", "" + idSeccion);
        if (pregunta.abrir("id_seccion = " + idSeccion + " and inicial = 1", "codigo_pregunta")) {
//            if (pregunta.get_instruccion() != null) {
//                informationMessage(null, "Lea con atención.", Html.fromHtml(pregunta.get_instruccion()), Parametros.FONT_OBS);
//            }
            Seccion seccion = new Seccion();
            if (seccion.abrir(pregunta.get_id_seccion())) {
                TextView modulo = new TextView(getContext());
                modulo.setText(Html.fromHtml(seccion.get_seccion()));
                modulo.setTextSize(25);
                modulo.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                modulo.setGravity(View.TEXT_ALIGNMENT_CENTER);
                modulo.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
//                Typeface typeface = ResourcesCompat.getFont(getContext(), R.font.quicksand_medium);
//                modulo.setTypeface(typeface);
                //modulo.setTextColor(getResources().getColor(R.color.color_darker));
                layout.addView(modulo);
            }
            seccion.free();
            int ar = pregunta.count();
            pregs = new PreguntaView[pregunta.count()];
            separadors = new View[pregunta.count()];
            buttons = new Button[pregunta.count()];
            int i = 0;
            do {
//                                    String p = pregunta.procesaEnunciado(idPadre, pregunta.get_pregunta())+">"+i;
                String p = pregunta.procesaEnunciado(idPadre, pregunta.get_pregunta());
                String a = pregunta.get_id_tipo_pregunta().toString();
//                p = p + "posicion:" + i;
                p = p;
                String b = pregunta.get_ayuda();
                String codigoPregunta=pregunta.get_variable();
                Map<Integer, String> omision;
                if (pregunta.get_omision() != null && !pregunta.get_omision().equals("null")) {
                    omision = Pregunta.getOmision_convert(pregunta.get_omision());
                } else {
                    omision = null;
                }
                switch (pregunta.get_id_tipo_pregunta()) {
                    case Abierta: {
//                                            pregs[i] = new Abierta(getContext(), i, pregunta.get_id_pregunta(), pregunta.get_id_seccion(), pregunta.get_codigo_pregunta(), p, pregunta.get_longitud(), omision, pregunta.get_ayuda(), true, false);
                        pregs[i] = new Abierta(getContext(), i, pregunta.get_id_pregunta(), pregunta.get_id_seccion(), codigoPregunta, p, pregunta.get_longitud(), omision, pregunta.get_ayuda(), true, false);
                        break;
                    }
                    case Incidencia:
                    case Cerrada: {
                        Map<Integer, String> respuestas;
                        if (pregunta.get_catalogo().equals("cat_manzanos")) {
                            Upm upm = new Upm();
                            String codigoUpm;
                            if(i==2){
                                codigoUpm = pregs[1].getCodResp()!= "-1"?pregs[1].getCodResp() : upm.getCodigo(idInformante.id_asignacion);
                            } else {
                                codigoUpm = upm.getCodigo(idInformante.id_asignacion);
                            }
                            upm.free();
                            Catalogo c = new Catalogo("cat_manzanos");
                            respuestas = c.obtenerCatalogoUpmManzanaLV(codigoUpm);
                            c.free();
                        } else {
                            respuestas = Pregunta.getRespuestas(pregunta.get_respuesta());
                        }
                        ArrayList<Integer> filtro = null;
                        pregs[i] = new Cerrada(getContext(), i, pregunta.get_id_pregunta(), pregunta.get_id_seccion(), codigoPregunta, p, respuestas, pregunta.get_codigo_especifique(), omision, filtro, pregunta.get_ayuda(), false);
                        break;
                    }
                    case CerradaMatriz: {
                        Map<Integer, String> respuestas = Pregunta.getRespuestas(pregunta.get_respuesta());
                        pregs[i] = new CerradaMatriz(getContext(), pregunta.get_id_pregunta(), pregunta.get_id_seccion(), codigoPregunta, p, respuestas, pregunta.get_codigo_especifique(), pregunta.get_longitud(), 4, pregunta.get_ayuda());
                        break;
                    }
                    case CerradaOmitida: {
                        Map<Integer, String> respuestas = Pregunta.getRespuestas(pregunta.get_respuesta());
                        pregs[i] = new CerradaOmitida(getContext(), pregunta.get_id_pregunta(), pregunta.get_id_seccion(), codigoPregunta, p, respuestas, pregunta.get_codigo_especifique(), pregunta.get_codigo_especial(), pregunta.get_ayuda(), Informante.getUpm(idInformante));
                        break;
                    }
                    case UsoVivienda:
                    case NumeroVivienda:
                    case Numero: {
                        if(!pregunta.get_catalogo().equals("--")){
                            pregs[i] = new Autocompletar2(context, i, pregunta.get_id_pregunta(), pregunta.get_id_seccion(), codigoPregunta, p, pregunta.get_longitud(), pregunta.get_catalogo(), omision, pregunta.get_ayuda(), true, false, false);
                        }else {
                            pregs[i] = new Numero(context, i, pregunta.get_id_pregunta(), pregunta.get_id_seccion(), codigoPregunta, p, pregunta.get_longitud(), omision, pregunta.get_ayuda(), false);

                        } break;
                    }
                    case Decimal: {
                        pregs[i] = new Decimal(getContext(), i, pregunta.get_id_pregunta(), pregunta.get_id_seccion(),codigoPregunta, p, pregunta.get_longitud(), omision, pregunta.get_ayuda(), false);
                        break;
                    }
                    case Multiple: {
                        Map<Integer, String> respuestas = Pregunta.getRespuestas(pregunta.get_respuesta());
                        pregs[i] = new Multiple(getContext(), pregunta.get_id_pregunta(), pregunta.get_id_seccion(), codigoPregunta, p, respuestas, 4, pregunta.get_ayuda());
                        break;
                    }
                    case Fecha: {
                        pregs[i] = new Fecha(getContext(), i, pregunta.get_id_pregunta(), pregunta.get_id_seccion(), codigoPregunta, p, 4, pregunta.get_ayuda(), false);
                        break;
                    }
                    case MesAnio: {
                        pregs[i] = new MesAnio(getContext(), pregunta.get_id_pregunta(), pregunta.get_id_seccion(), codigoPregunta, p, 4, pregunta.get_ayuda());
                        break;
                    }
                    case HoraMinuto: {
                        pregs[i] = new HoraMinuto(getContext(), i, pregunta.get_id_pregunta(), pregunta.get_id_seccion(),codigoPregunta, p, 4, pregunta.get_ayuda(), false);
                        break;
                    }
                    case Gps: {
                        //preg = new GpsOldd(this, pregunta.get_id_pregunta(), pregunta.get_id_seccion(), pregunta.get_codigo_pregunta(), p, pregunta.get_ayuda());
                        pregs[i] = new Gps(getContext(), i, pregunta.get_id_pregunta(), pregunta.get_id_seccion(), codigoPregunta, p, pregunta.get_ayuda(), false);
                        break;
                    }
                    case Formula: {
                        Float val = 0f;//Encuesta.evaluar(pregunta.get_rpn_formula(), idEncuesta.getIdInformante(), idEncuesta.fila, null, null, null);
                        pregs[i] = new Formula(getContext(), pregunta.get_id_pregunta(), pregunta.get_id_seccion(), codigoPregunta, p, val, pregunta.get_ayuda(), "0");
                        break;
                    }
                    case Fotografia: {
                        pregs[i] = new Foto(getActivity(), pregunta.get_id_pregunta(), pregunta.get_id_seccion(), codigoPregunta, p, informante.get_id_informante(), pregunta.get_id_pregunta(), pregunta.get_ayuda());
                        break;
                    }
                    case CerradaInformante: {
                        Map<Long, String> respuestas;
                        if (informante.get_id_nivel() == 2) {
                            respuestas = Informante.getRespuestas(informante.get_id_informante_padre());
                        } else {
                            respuestas = Informante.getRespuestas(informante.get_id_informante());
                        }
                        preg = new CerradaInformante(getContext(), pregunta.get_id_pregunta(), pregunta.get_id_seccion(), codigoPregunta, p, respuestas, pregunta.get_codigo_especifique(), 4, pregunta.get_ayuda());
                        break;
                    }
                    case CerradaInformanteHijos: { // Informante con solo los hijos
                        Map<Long, String> respuestas;
//                        if (informante.get_id_nivel() == 2) {
////                            respuestas = Informante.getRespuestasHijos(informante.get_id_informante_padre(), pregunta.get_rpn_formula(), informante.get_id_informante().id_asignacion * 10000 + informante.get_id_informante().correlativo);
//                        } else {
                        respuestas = new TreeMap<>();
//                            mensaje.errorMessage(getContext(), null, "Error!", Html.fromHtml("No se puede utilizar esta pregunta en nivel 1."), Parametros.FONT_OBS);
//                        }
                        preg = new CerradaInformante(getContext(), pregunta.get_id_pregunta(), pregunta.get_id_seccion(), codigoPregunta, p, respuestas, pregunta.get_codigo_especifique(), 4, pregunta.get_ayuda());
                        break;
                    }
                    case Cantidad: {
                        pregs[i] = new Cantidad(getContext(), pregunta.get_id_pregunta(), pregunta.get_id_seccion(), codigoPregunta, p, 4, pregunta.get_ayuda());
                        break;
                    }
//                    case CerradaBucle: {
//                        Map<Long, String> respuestas = Encuesta.getRespuestas(informante.get_id_informante(), pregunta.get_variable_bucle(), pregunta.get_rpn_formula());
//                        pregs[i] = new CerradaBucle(getContext(), pregunta.get_id_pregunta(), pregunta.get_id_seccion(), pregunta.get_codigo_pregunta(), p, respuestas, pregunta.get_codigo_especifique(), pregunta.get_codigo_especial(), pregunta.get_ayuda());
//                        break;
//                    }
                    case Autocompletar: {
                        String catalogo;
                        if (pregunta.get_catalogo().equals("cat_manzanos") || pregunta.get_catalogo().equals("cat_comunidad_upm")) {
                            Upm upm = new Upm();
                            String codigoUpm = upm.getCodigo(idInformante.id_asignacion);
                            upm.free();
                            catalogo = pregunta.get_catalogo() + ";" + Catalogo.getCatUpmManzana(codigoUpm);
                        } else {
                            catalogo = pregunta.get_catalogo();
                        }

                        if (pregunta.get_id_pregunta() == Parametros.ID_MANZANA_COMUNIDAD)
                            pregs[i] = new Autocompletar(getContext(), i, pregunta.get_id_pregunta(), pregunta.get_id_seccion(), codigoPregunta, p, pregunta.get_longitud(), catalogo, omision, pregunta.get_ayuda(), true, false, true);
                        else if (pregunta.get_id_pregunta() == Parametros.ID_PREGUNTA_AVENIDA_CALLE) {
                            pregs[i] = new Autocompletar2(getContext(), i, pregunta.get_id_pregunta(), pregunta.get_id_seccion(), codigoPregunta, p, pregunta.get_longitud(), catalogo, omision, pregunta.get_ayuda(), true, false, false);
                        } else
                            pregs[i] = new Autocompletar(getContext(), i, pregunta.get_id_pregunta(), pregunta.get_id_seccion(), codigoPregunta, p, pregunta.get_longitud(), catalogo, omision, pregunta.get_ayuda(), true, false, false);
                        break;
                    }

                    case ValorTipo: {
                        Map<Integer, String> respuestas = Pregunta.getRespuestas(pregunta.get_respuesta());
                        pregs[i] = new ValorTipo(getContext(), i, pregunta.get_id_pregunta(), pregunta.get_id_seccion(), codigoPregunta, p, respuestas, pregunta.get_longitud(), 4, pregunta.get_ayuda(), false);
                        break;
                    }
                    case ValorTipo2: {
                        Map<Integer, String> respuestas = Pregunta.getRespuestas(pregunta.get_respuesta());
                        pregs[i] = new ValorTipo2(getContext(), pregunta.get_id_pregunta(), pregunta.get_id_seccion(), codigoPregunta, p, respuestas, pregunta.get_longitud(), 4, pregunta.get_ayuda());
                        break;
                    }
                    case ValorTipoGastos: {
                        Map<Integer, String> respuestas = Pregunta.getRespuestas(pregunta.get_respuesta());
                        pregs[i] = new ValorTipoGastos(getContext(), i, pregunta.get_id_pregunta(), pregunta.get_id_seccion(), codigoPregunta, p, respuestas, pregunta.get_longitud(), 4, pregunta.get_ayuda(), pregunta.get_codigo_especifique(), false);
                        break;
                    }
                    case ValorTipoMatriz: {
                        Map<Integer, String> respuestas = Pregunta.getRespuestas(pregunta.get_respuesta());
                        pregs[i] = new ValorTipoMatriz(getContext(), pregunta.get_id_pregunta(), pregunta.get_id_seccion(), codigoPregunta, p, respuestas, pregunta.get_longitud(), 4, pregunta.get_ayuda(), pregunta.get_codigo_especifique());
                        break;
                    }
                    case SeleccionKish: {
                        Map<Integer, String> respuestas;
                        int penultimo = informante.folio(informante.get_id_informante());
                        if (pregunta.get_codigo_pregunta().toString().equals("H12_A_00")) {
                            respuestas = Informante.getSelectKisha(informante.get_id_informante());
                        } else {
                            respuestas = Informante.getSelectKisha2(informante.get_id_informante());
                        }
                        preg = new SeleccionKish(getContext(), pregunta.get_id_pregunta(), pregunta.get_id_seccion(), codigoPregunta, p, "prueba", respuestas, penultimo, pregunta.get_ayuda());
                        break;
                    }
                    case Prioridad: {
                        Map<Integer, String> respuestas;
                        if (pregunta.get_catalogo().equals("--")) {
                            respuestas = Pregunta.getRespuestas(pregunta.get_respuesta());
                        } else {
                            respuestas = null;
                        }
                        String catalogo;
                        if (pregunta.get_catalogo().equals("cat_manzanos") || pregunta.get_catalogo().equals("cat_comunidad_upm")) {
                            Upm upm = new Upm();
                            String codigoUpm = upm.getCodigo(idInformante.id_asignacion);
                            upm.free();
                            catalogo = pregunta.get_catalogo() + ";" + codigoUpm;
                        } else {
                            catalogo = pregunta.get_catalogo();
                        }
                        pregs[i] = new Prioridad(getContext(), i, pregunta.get_id_pregunta(), pregunta.get_id_seccion(), codigoPregunta, p, respuestas, catalogo, omision,pregunta.get_codigo_especifique(), pregunta.get_minimo(), pregunta.get_longitud(), 4, pregunta.get_ayuda(), false);
                        break;
                    }
                    case Calendario: {
                        String[] values;
//                        if (pregunta.get_formula() == null) {
                        values = new String[72];
//                        } else {
//                            values = Encuesta.getRespuesta(informante.get_id_informante(), pregunta.get_formula());
//                        }
                        GregorianCalendar fechaContacto;
//                        if (pregunta.get_revision() == null) {
                        fechaContacto = new GregorianCalendar();
//                        } else {
//                            String[] f = Encuesta.obtenerValor(idEncuesta.getIdInformante(), pregunta.get_revision(), 1).split("-");
//                            fechaContacto = new GregorianCalendar(Integer.parseInt(f[2]), Integer.parseInt(f[1]) - 1, Integer.parseInt(f[0]));
//                        }
                        pregs[i] = new Calendario(getContext(), pregunta.get_id_pregunta(), pregunta.get_id_seccion(), pregunta.get_codigo_pregunta(), pregunta.get_pregunta(), values, fechaContacto, pregunta.get_ayuda());
                        break;
                    }
                    case Memoria: {
                        String[] values = Encuesta.getRespuesta(Informante.getUpm(idInformante), pregunta.get_codigo_pregunta());
                        pregs[i] = new Memoria(getContext(), i, pregunta.get_id_pregunta(), pregunta.get_id_seccion(), codigoPregunta, p, pregunta.get_longitud(), omision, values, pregunta.get_ayuda(), false);
                        break;
                    }
                    case Upm: {
                        Map<Integer, String> respuestas = UpmHijo.getRespuestasUPM(Informante.getUpm(idInformante));
                        pregs[i] = new Cerrada(getContext(), i, pregunta.get_id_pregunta(), pregunta.get_id_seccion(), codigoPregunta, p, respuestas, pregunta.get_codigo_especifique(), omision, null, pregunta.get_ayuda(), false);
                        break;
                    }
                    case UpmHijo: {
                        Map<Integer, String> respuestas = UpmHijo.getRespuestasManzano(Informante.getUpm(idInformante));
                        pregs[i] = new Cerrada(getContext(), i, pregunta.get_id_pregunta(), pregunta.get_id_seccion(), codigoPregunta, p, respuestas, pregunta.get_codigo_especifique(), omision, null, pregunta.get_ayuda(), false);
                        break;
                    }
//                    case Consulta: {
//                        Map<Integer, String> respuestas = Pregunta.getRespuestasQuery(informante.get_id_informante().id_asignacion, informante.get_id_informante().correlativo, informante.get_id_informante_padre().id_asignacion, informante.get_id_informante_padre().correlativo, informante.get_id_upm(), informante.get_descripcion(), informante.get_codigo(), pregunta.get_variable(), idEncuesta.fila);
//                        preg = new Cerrada(getContext(), pregunta.get_id_pregunta(), pregunta.get_id_seccion(), pregunta.get_codigo_pregunta(), p, respuestas, pregunta.get_codigo_especifique(), pregunta.get_codigo_especial(), null, pregunta.get_ayuda());
//                        break;
//                    }
//                    case CerradaConsulta: {
//                        Map<Integer, String> respuestasConsulta = Pregunta.getRespuestasQuery(informante.get_id_informante().id_asignacion, informante.get_id_informante().correlativo, informante.get_id_informante_padre().id_asignacion, informante.get_id_informante_padre().correlativo, informante.get_id_upm(), informante.get_descripcion(), informante.get_codigo(), pregunta.get_variable(), idEncuesta.fila);
//                        Map<Integer, String> respuestas = Pregunta.getRespuestas(pregunta.get_respuesta());
//                        preg = new CerradaConsulta(getContext(), pregunta.get_id_pregunta(), pregunta.get_id_seccion(), pregunta.get_codigo_pregunta(), p, respuestas, respuestasConsulta, pregunta.get_codigo_especial(), null, pregunta.get_ayuda());
//                        break;
//                    }
                    case Tabla: {
//                        pregs[i] = new Abierta(getContext(), pregunta.get_id_pregunta(), pregunta.get_id_seccion(), pregunta.get_codigo_pregunta(), p, pregunta.get_longitud(), omision, pregunta.get_ayuda(), true);
                        break;
                    }
                }
                // ABRE LA RESPUESTA EN CASO DE QUE HAYA SIDO ALMACENADA
                if (encuesta.abrir(idInformante.where() + " AND id_pregunta = " + pregunta.get_id_pregunta(), null)) {
                    try {
                        switch (pregunta.get_id_tipo_pregunta()) {
//                            case Cerrada:
//                            case CerradaBucle:
//                            case CerradaInformante:
//                            case CerradaMatriz:
//                            case ValorTipo:
//                            case ValorTipo2:
//                            case ValorTipoMatriz:
//                            case Upm:
//                            case UpmHijo:
//                            case Incidencia:
//                            case Consulta:
//                            case CerradaConsulta:
//                                pregs[i].setIdResp(encuesta.get_id_respuesta());
//                                break;
////                                                case Multiple:
//                                                case Autocompletar:
//                                                    pregs[i].setCodResp(encuesta.get_codigo_respuesta());
//                                                case Prioridad:
                            case Calendario:
                                pregs[i].setCodResp(encuesta.get_codigo_respuesta());
                                break;
                            default:
                                pregs[i].setCodResp(encuesta.get_codigo_respuesta());
                                pregs[i].setResp(encuesta.get_respuesta());
                        }
                        String af = encuesta.get_codigo_respuesta();
                        String ab = encuesta.get_respuesta();
//                                            pregs[i].setCodResp(encuesta.get_codigo_respuesta());
                        pregs[i].setObservacion(encuesta.get_observacion());
                        pregs[i].setEstado(encuesta.get_estado());

                        int idNivelPregunta = Informante.obtenerNivelPregunta(encuesta.get_id_encuesta().id_pregunta);
                        boolean esObservada = false;

                        esObservada = Informante.esPreguntaObservada(encuesta.get_id_encuesta().getIdInformante(), pregunta.get_id_pregunta(), idNivelPregunta);

                        if (esObservada) {
                            pregs[i].setBackgroundColor(getResources().getColor(R.color.colorWaterError));
//                            pregs[i].setObservado(true);
                        }
//                        idEncuesta.fila = encuesta.get_id_encuesta().fila;
                    } catch (Exception ex) {
                        Log.d("preguntaError", pregs[i].getCod().toString());
                        ex.printStackTrace();
                    }
                }


                encuesta.free();
//                pregs[i].startAnimation(animation);
                layout.addView(pregs[i]);

// agregar los botones de observacion

                buttons[i] = new Button(getContext());
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    buttons[i].setBackground(getContext().getDrawable(R.drawable.ic_edit_note));
                }
//                                buttons[i].setText("Observación");
                buttons[i].setTag(i);
                buttons[i].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            indice = Integer.parseInt(String.valueOf(view.getTag()));
                            Log.d("indiceindice: ", indice + "");
                            mensaje.observationMessageEncIni(activity, getContext(), "obsIni", "Introduzca observación", Html.fromHtml("Escriba la nota:"), pregs[indice].getObservacion(), false, indice);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                });
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                layoutParams.gravity = Gravity.RIGHT;
                buttons[i].setLayoutParams(layoutParams);
                layout.addView(buttons[i]);
                if (idNivel == Parametros.LV_VIVIENDAS || idNivel == Parametros.LV_CABECERA) {
                    buttons[i].setVisibility(View.GONE);
                }


                separadors[i] = new View(getContext());
                separadors[i].setBackgroundColor(getResources().getColor(R.color.colorSecondary_text));
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                lp.height = 1;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    separadors[i].setElevation(100);
                }

                layout.addView(separadors[i], lp);
                //ADICION DE DOS PREGUNTAS PARA BLOQUEO EN REVISITA
//                                    if( ((pregunta.get_id_pregunta() == 2193 || pregunta.get_id_pregunta() == 2194 || pregunta.get_id_pregunta() == 2195 || pregunta.get_id_pregunta() == 2196 || pregunta.get_id_pregunta() == 2197 || pregunta.get_id_pregunta() == 2198 || pregunta.get_id_pregunta() == 2199 ||pregunta.get_id_pregunta() == 15357 ||pregunta.get_id_pregunta() == 15350 ||pregunta.get_id_pregunta() == 15365 ||pregunta.get_id_pregunta() == 15355 || pregunta.get_id_pregunta() == 15007) && ( idInformanteAnterior.correlativo!=0)) ) {
//                                        PreguntaView.setViewAndChildrenEnabled(pregs[i], false);
//                                    }
                ////Bloqueos de los campos en el primer nivel
//                                    if(informante.abrir(idInformante)&&idInformante.correlativo!=0&& informante.get_id_informante_anterior()!=null){
//                                        idInformante=informante.get_id_informante_anterior();
//                                    }else{
//                                        idInformante=new IdInformante(0,0);
//                                    }
//                                    informante.free();

                i++;
            } while (pregunta.siguiente());

            inhabilitaPreguntas();

            ///salto de preguntas iniciales
            if (idNivel == 2) {

//                if (!pregs[1].getCodResp().equals("-1")) {
//                    try {
//                        pregs[2].setCodResp(String.valueOf(Upm.obtenerInstancia(pregs[1].getCodResp())));
//
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//                                    PreguntaView.setViewAndChildrenEnabled(pregs[2], false);
                if (pregs[13].getCodResp().equals("3") || pregs[13].getCodResp().equals("2")) {
                    ejecucion(Pregunta.getIdPregunta(Parametros.CODIGO_PREGUNTA_USO_DE_LA_VIVIENDA), pregs[13].getCodResp());
                } else if (pregs[13].getCodResp().equals("1")) {
                    ///ocultar preguntas
                    if (Seleccion.hasSelected(Upm.getIdUpm(idInformante.id_asignacion)))
                        PreguntaView.setViewAndChildrenEnabled(pregs[13], false);

                }

                if (!pregs[3].getCodResp().equals("-1")&&!pregs[3].getResp().trim().equals("U") ) {
                    ocultarUnionManzanas();
                }

                try{
                    PreguntaView.setViewAndChildrenEnabled(pregs[Parametros.PID_VIVIENDA_OMITIDA],false);
//                    ((View)pregs[Parametros.PID_VOE]).setVisibility(View.GONE);
                }catch (Exception e){

                }
                try{
                    PreguntaView.setViewAndChildrenEnabled(pregs[Parametros.PID_VOE],false);
//                    ((View)pregs[21]).setVisibility(View.GONE);
                }catch (Exception e){

                }
                try{
                    if(Seleccion.hasSelected(Upm.getIdUpm(idInformante.id_asignacion))&&idInformante.correlativo!=0){
                        //TODO: CONTROL DE OMITIDA
                        if(!Informante.esOmitida(idInformante.id_asignacion, idInformante.correlativo))
                            PreguntaView.setViewAndChildrenEnabled(pregs[2], false);
                    }
                }catch (Exception e){

                }

            }

            pregs[0].setFocus();

        } else {
            mensaje.errorMessage(getContext(), null, "Error!", Html.fromHtml("No se encontró la pregunta."), Parametros.FONT_OBS);
        }
        pregunta.free();
//                        }
//                    });
//                } catch (Exception e) {
//                    //print the error here
//                }
//            }
//        }).start();
    }

    private static void dibujarPregunta1(int i) {
        final Pregunta pregunta = new Pregunta();
//        animation = AnimationUtils.loadAnimation(getContext(), R.anim.item_anim_from_right);
//        LayoutAnimationController controller = new LayoutAnimationController(animation, 0.1f);
//        layout.setLayoutAnimation(controller);

//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    getActivity().runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
        String cond = (i==7)?"A_07":"A_01";
        if (pregunta.abrir("id_seccion = " + idSeccion + " and inicial = 1 AND codigo_pregunta>'"+cond+"'", "codigo_pregunta")) {

//            if (pregunta.get_instruccion() != null) {
//                informationMessage(null, "Lea con atención.", Html.fromHtml(pregunta.get_instruccion()), Parametros.FONT_OBS);
//            }
//            Seccion seccion = new Seccion();
//            if (seccion.abrir(pregunta.get_id_seccion())) {
//                TextView modulo = new TextView(getContext());
//                modulo.setText(Html.fromHtml(seccion.get_seccion()));
//                modulo.setTextSize(25);
//                modulo.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
//                modulo.setGravity(View.TEXT_ALIGNMENT_CENTER);
//                modulo.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
////                Typeface typeface = ResourcesCompat.getFont(getContext(), R.font.quicksand_medium);
////                modulo.setTypeface(typeface);
//                //modulo.setTextColor(getResources().getColor(R.color.color_darker));
//                layout.addView(modulo);
//            }
//            seccion.free();


            do {
//                String p = pregunta.procesaEnunciado(idPadre, pregunta.get_pregunta())+">"+i;
                String p = pregunta.procesaEnunciado(idPadre, pregunta.get_pregunta());
                String a = pregunta.get_id_tipo_pregunta().toString();
//                p = p + "posicion:" + i;
                p = p;
                String b = pregunta.get_ayuda();
                String codigoPregunta=pregunta.get_variable();
                Map<Integer, String> omision;
                if (pregunta.get_omision() != null && !pregunta.get_omision().equals("null")) {
                    omision = Pregunta.getOmision_convert(pregunta.get_omision());
                } else {
                    omision = null;
                }
                switch (pregunta.get_id_tipo_pregunta()) {
                    case Abierta: {
//                                            pregs[i] = new Abierta(getContext(), i, pregunta.get_id_pregunta(), pregunta.get_id_seccion(), pregunta.get_codigo_pregunta(), p, pregunta.get_longitud(), omision, pregunta.get_ayuda(), true, false);
                        pregs[i] = new Abierta(context, i, pregunta.get_id_pregunta(), pregunta.get_id_seccion(), codigoPregunta, p, pregunta.get_longitud(), omision, pregunta.get_ayuda(), true, false);
                        break;
                    }
                    case Incidencia:
                    case Cerrada: {
                        Map<Integer, String> respuestas;
                        if (pregunta.get_catalogo().equals("cat_manzanos")) {
                            Upm upm = new Upm();
                            String codigoUpm;
                            if(i==2){
                                codigoUpm = pregs[1].getCodResp()!= "-1"?pregs[1].getCodResp() : upm.getCodigo(idInformante.id_asignacion);
                            } else {
                                codigoUpm = upm.getCodigo(idInformante.id_asignacion);
                            }
                            upm.free();
                            Catalogo c = new Catalogo("cat_manzanos");
                            respuestas = c.obtenerCatalogoUpmManzanaLV(codigoUpm);
                            c.free();
                        } else {
                            respuestas = Pregunta.getRespuestas(pregunta.get_respuesta());
                        }
                        ArrayList<Integer> filtro = null;
//                        Map<Integer, String> respuestas = Pregunta.getRespuestas(pregunta.get_respuesta());
                        pregs[i] = new Cerrada(context, i, pregunta.get_id_pregunta(), pregunta.get_id_seccion(), codigoPregunta, p, respuestas, pregunta.get_codigo_especifique(), omision, filtro, pregunta.get_ayuda(), false);
                        break;
                    }
                    case CerradaMatriz: {
                        Map<Integer, String> respuestas = Pregunta.getRespuestas(pregunta.get_respuesta());
                        pregs[i] = new CerradaMatriz(context, pregunta.get_id_pregunta(), pregunta.get_id_seccion(), codigoPregunta, p, respuestas, pregunta.get_codigo_especifique(), pregunta.get_longitud(), 4, pregunta.get_ayuda());
                        break;
                    }
                    case CerradaOmitida: {
                        Map<Integer, String> respuestas = Pregunta.getRespuestas(pregunta.get_respuesta());
                        pregs[i] = new CerradaOmitida(context, pregunta.get_id_pregunta(), pregunta.get_id_seccion(), codigoPregunta, p, respuestas, pregunta.get_codigo_especifique(), pregunta.get_codigo_especial(), pregunta.get_ayuda(), Informante.getUpm(idInformante));
                        break;
                    }
                    case UsoVivienda:
                    case NumeroVivienda:
                    case Numero: {
                        if(!pregunta.get_catalogo().equals("--")){
                            pregs[i] = new Autocompletar2(context, i, pregunta.get_id_pregunta(), pregunta.get_id_seccion(), codigoPregunta, p, pregunta.get_longitud(), pregunta.get_catalogo(), omision, pregunta.get_ayuda(), true, false, false);
                        }else {
                            pregs[i] = new Numero(context, i, pregunta.get_id_pregunta(), pregunta.get_id_seccion(), codigoPregunta, p, pregunta.get_longitud(), omision, pregunta.get_ayuda(), false);

                        }
                        break;
                    }
                    case Decimal: {
                        pregs[i] = new Decimal(context, i, pregunta.get_id_pregunta(), pregunta.get_id_seccion(), codigoPregunta, p, pregunta.get_longitud(), omision, pregunta.get_ayuda(), false);
                        break;
                    }
                    case Multiple: {
                        Map<Integer, String> respuestas = Pregunta.getRespuestas(pregunta.get_respuesta());
                        pregs[i] = new Multiple(context, pregunta.get_id_pregunta(), pregunta.get_id_seccion(), codigoPregunta, p, respuestas, 4, pregunta.get_ayuda());
                        break;
                    }
                    case Fecha: {
                        pregs[i] = new Fecha(context, i, pregunta.get_id_pregunta(), pregunta.get_id_seccion(), codigoPregunta, p, 4, pregunta.get_ayuda(), false);
                        break;
                    }
                    case MesAnio: {
                        pregs[i] = new MesAnio(context, pregunta.get_id_pregunta(), pregunta.get_id_seccion(), codigoPregunta, p, 4, pregunta.get_ayuda());
                        break;
                    }
                    case HoraMinuto: {
                        pregs[i] = new HoraMinuto(context, i, pregunta.get_id_pregunta(), pregunta.get_id_seccion(), codigoPregunta, p, 4, pregunta.get_ayuda(), false);
                        break;
                    }
                    case Gps: {
                        //preg = new GpsOldd(this, pregunta.get_id_pregunta(), pregunta.get_id_seccion(), pregunta.get_codigo_pregunta(), p, pregunta.get_ayuda());
                        pregs[i] = new Gps(context, i, pregunta.get_id_pregunta(), pregunta.get_id_seccion(), codigoPregunta, p, pregunta.get_ayuda(), false);
                        break;
                    }
                    case Formula: {
                        Float val = 0f;//Encuesta.evaluar(pregunta.get_rpn_formula(), idEncuesta.getIdInformante(), idEncuesta.fila, null, null, null);
                        pregs[i] = new Formula(context, pregunta.get_id_pregunta(), pregunta.get_id_seccion(), codigoPregunta, p, val, pregunta.get_ayuda(), "0");
                        break;
                    }
                    case Fotografia: {
                        pregs[i] = new Foto((Activity) context, pregunta.get_id_pregunta(), pregunta.get_id_seccion(), codigoPregunta, p, informante.get_id_informante(), pregunta.get_id_pregunta(), pregunta.get_ayuda());
                        break;
                    }
                    case CerradaInformante: {
                        Map<Long, String> respuestas;
                        if (informante.get_id_nivel() == 2) {
                            respuestas = Informante.getRespuestas(informante.get_id_informante_padre());
                        } else {
                            respuestas = Informante.getRespuestas(informante.get_id_informante());
                        }
                        pregs[i] = new CerradaInformante(context, pregunta.get_id_pregunta(), pregunta.get_id_seccion(), codigoPregunta, p, respuestas, pregunta.get_codigo_especifique(), 4, pregunta.get_ayuda());
                        break;
                    }
                    case CerradaInformanteHijos: { // Informante con solo los hijos
                        Map<Long, String> respuestas;
//                        if (informante.get_id_nivel() == 2) {
////                            respuestas = Informante.getRespuestasHijos(informante.get_id_informante_padre(), pregunta.get_rpn_formula(), informante.get_id_informante().id_asignacion * 10000 + informante.get_id_informante().correlativo);
//                        } else {
                        respuestas = new TreeMap<>();
//                            mensaje.errorMessage(getContext(), null, "Error!", Html.fromHtml("No se puede utilizar esta pregunta en nivel 1."), Parametros.FONT_OBS);
//                        }
                        pregs[i] = new CerradaInformante(context, pregunta.get_id_pregunta(), pregunta.get_id_seccion(), codigoPregunta, p, respuestas, pregunta.get_codigo_especifique(), 4, pregunta.get_ayuda());
                        break;
                    }
                    case Cantidad: {
                        pregs[i] = new Cantidad(context, pregunta.get_id_pregunta(), pregunta.get_id_seccion(), codigoPregunta, p, 4, pregunta.get_ayuda());
                        break;
                    }
//                    case CerradaBucle: {
//                        Map<Long, String> respuestas = Encuesta.getRespuestas(informante.get_id_informante(), pregunta.get_variable_bucle(), pregunta.get_rpn_formula());
//                        pregs[i] = new CerradaBucle(getContext(), pregunta.get_id_pregunta(), pregunta.get_id_seccion(), pregunta.get_codigo_pregunta(), p, respuestas, pregunta.get_codigo_especifique(), pregunta.get_codigo_especial(), pregunta.get_ayuda());
//                        break;
//                    }
                    case Autocompletar: {
                        String catalogo;
                        if (pregunta.get_catalogo().equals("cat_manzanos") || pregunta.get_catalogo().equals("cat_comunidad_upm")) {
                            Upm upm = new Upm();
                            String codigoUpm = upm.getCodigo(idInformante.id_asignacion);
                            catalogo = pregunta.get_catalogo() + ";" + Catalogo.getCatUpmManzana(codigoUpm);
                        } else {
                            catalogo = pregunta.get_catalogo();
                        }
                        if (pregunta.get_id_pregunta() == Parametros.ID_MANZANA_COMUNIDAD)
                            pregs[i] = new Autocompletar(context, i, pregunta.get_id_pregunta(), pregunta.get_id_seccion(), codigoPregunta, p, pregunta.get_longitud(), catalogo, omision, pregunta.get_ayuda(), true, false, true);
                        else if (pregunta.get_id_pregunta() == Parametros.ID_PREGUNTA_AVENIDA_CALLE) {
                            pregs[i] = new Autocompletar2(context, i, pregunta.get_id_pregunta(), pregunta.get_id_seccion(), codigoPregunta, p, pregunta.get_longitud(), catalogo, omision, pregunta.get_ayuda(), true, false, false);
                        } else
                            pregs[i] = new Autocompletar(context, i, pregunta.get_id_pregunta(), pregunta.get_id_seccion(), codigoPregunta, p, pregunta.get_longitud(), catalogo, omision, pregunta.get_ayuda(), true, false, false);

                        break;
                    }

                    case ValorTipo: {
                        Map<Integer, String> respuestas = Pregunta.getRespuestas(pregunta.get_respuesta());
                        pregs[i] = new ValorTipo(context, i, pregunta.get_id_pregunta(), pregunta.get_id_seccion(), codigoPregunta, p, respuestas, pregunta.get_longitud(), 4, pregunta.get_ayuda(), false);
                        break;
                    }
                    case ValorTipo2: {
                        Map<Integer, String> respuestas = Pregunta.getRespuestas(pregunta.get_respuesta());
                        pregs[i] = new ValorTipo2(context, pregunta.get_id_pregunta(), pregunta.get_id_seccion(), codigoPregunta, p, respuestas, pregunta.get_longitud(), 4, pregunta.get_ayuda());
                        break;
                    }
                    case ValorTipoGastos: {
                        Map<Integer, String> respuestas = Pregunta.getRespuestas(pregunta.get_respuesta());
                        pregs[i] = new ValorTipoGastos(context, i, pregunta.get_id_pregunta(), pregunta.get_id_seccion(), codigoPregunta, p, respuestas, pregunta.get_longitud(), 4, pregunta.get_ayuda(), pregunta.get_codigo_especifique(), false);
                        break;
                    }
                    case ValorTipoMatriz: {
                        Map<Integer, String> respuestas = Pregunta.getRespuestas(pregunta.get_respuesta());
                        pregs[i] = new ValorTipoMatriz(context, pregunta.get_id_pregunta(), pregunta.get_id_seccion(), codigoPregunta, p, respuestas, pregunta.get_longitud(), 4, pregunta.get_ayuda(), pregunta.get_codigo_especifique());
                        break;
                    }
                    case SeleccionKish: {
                        Map<Integer, String> respuestas;
                        int penultimo = informante.folio(informante.get_id_informante());
                        if (pregunta.get_codigo_pregunta().toString().equals("H12_A_00")) {
                            respuestas = Informante.getSelectKisha(informante.get_id_informante());
                        } else {
                            respuestas = Informante.getSelectKisha2(informante.get_id_informante());
                        }
                        pregs[i] = new SeleccionKish(context, pregunta.get_id_pregunta(), pregunta.get_id_seccion(), codigoPregunta, p, "prueba", respuestas, penultimo, pregunta.get_ayuda());
                        break;
                    }
                    case Prioridad: {
                        Map<Integer, String> respuestas=new HashMap<>();
                        if (pregunta.get_catalogo().equals("--")) {
                            respuestas = Pregunta.getRespuestas(pregunta.get_respuesta());
                        }
                        String catalogo;
                        if (pregunta.get_catalogo().equals("cat_manzanos") || pregunta.get_catalogo().equals("cat_comunidad_upm")) {
                            Upm upm = new Upm();
                            String codigoUpm = upm.getCodigo(idInformante.id_asignacion);
                            catalogo = pregunta.get_catalogo() + ";" + codigoUpm;
                        } else {
                            catalogo = pregunta.get_catalogo();
                        }
                        pregs[i] = new Prioridad(context, i, pregunta.get_id_pregunta(), pregunta.get_id_seccion(), codigoPregunta, p, respuestas, catalogo,omision, pregunta.get_codigo_especifique(), pregunta.get_minimo(), pregunta.get_longitud(), 4, pregunta.get_ayuda(), false);
                        break;
                    }
                    case Calendario: {
                        String[] values;
//                        if (pregunta.get_formula() == null) {
                        values = new String[72];
//                        } else {
//                            values = Encuesta.getRespuesta(informante.get_id_informante(), pregunta.get_formula());
//                        }
                        GregorianCalendar fechaContacto;
//                        if (pregunta.get_revision() == null) {
                        fechaContacto = new GregorianCalendar();
//                        } else {
//                            String[] f = Encuesta.obtenerValor(idEncuesta.getIdInformante(), pregunta.get_revision(), 1).split("-");
//                            fechaContacto = new GregorianCalendar(Integer.parseInt(f[2]), Integer.parseInt(f[1]) - 1, Integer.parseInt(f[0]));
//                        }
                        pregs[i] = new Calendario(context, pregunta.get_id_pregunta(), pregunta.get_id_seccion(), codigoPregunta, pregunta.get_pregunta(), values, fechaContacto, pregunta.get_ayuda());
                        break;
                    }
                    case Memoria: {
                        String[] values = Encuesta.getRespuesta(Informante.getUpm(idInformante), pregunta.get_codigo_pregunta());
                        pregs[i] = new Memoria(context, i, pregunta.get_id_pregunta(), pregunta.get_id_seccion(), codigoPregunta, p, pregunta.get_longitud(), omision, values, pregunta.get_ayuda(), false);
                        break;
                    }
                    case Upm: {
                        Map<Integer, String> respuestas = UpmHijo.getRespuestasUPM(Informante.getUpm(idInformante));
                        pregs[i] = new Cerrada(context, i, pregunta.get_id_pregunta(), pregunta.get_id_seccion(), codigoPregunta, p, respuestas, pregunta.get_codigo_especifique(), omision, null, pregunta.get_ayuda(), false);
                        break;
                    }
                    case UpmHijo: {
                        Map<Integer, String> respuestas = UpmHijo.getRespuestasManzano(Informante.getUpm(idInformante));
                        pregs[i] = new Cerrada(context, i, pregunta.get_id_pregunta(), pregunta.get_id_seccion(), codigoPregunta, p, respuestas, pregunta.get_codigo_especifique(), omision, null, pregunta.get_ayuda(), false);
                        break;
                    }
//                    case Consulta: {
//                        Map<Integer, String> respuestas = Pregunta.getRespuestasQuery(informante.get_id_informante().id_asignacion, informante.get_id_informante().correlativo, informante.get_id_informante_padre().id_asignacion, informante.get_id_informante_padre().correlativo, informante.get_id_upm(), informante.get_descripcion(), informante.get_codigo(), pregunta.get_variable(), idEncuesta.fila);
//                        preg = new Cerrada(getContext(), pregunta.get_id_pregunta(), pregunta.get_id_seccion(), pregunta.get_codigo_pregunta(), p, respuestas, pregunta.get_codigo_especifique(), pregunta.get_codigo_especial(), null, pregunta.get_ayuda());
//                        break;
//                    }
//                    case CerradaConsulta: {
//                        Map<Integer, String> respuestasConsulta = Pregunta.getRespuestasQuery(informante.get_id_informante().id_asignacion, informante.get_id_informante().correlativo, informante.get_id_informante_padre().id_asignacion, informante.get_id_informante_padre().correlativo, informante.get_id_upm(), informante.get_descripcion(), informante.get_codigo(), pregunta.get_variable(), idEncuesta.fila);
//                        Map<Integer, String> respuestas = Pregunta.getRespuestas(pregunta.get_respuesta());
//                        preg = new CerradaConsulta(getContext(), pregunta.get_id_pregunta(), pregunta.get_id_seccion(), pregunta.get_codigo_pregunta(), p, respuestas, respuestasConsulta, pregunta.get_codigo_especial(), null, pregunta.get_ayuda());
//                        break;
//                    }
                    case Tabla: {
//                        pregs[i] = new Abierta(getContext(), pregunta.get_id_pregunta(), pregunta.get_id_seccion(), pregunta.get_codigo_pregunta(), p, pregunta.get_longitud(), omision, pregunta.get_ayuda(), true);
                        break;
                    }
                }
                // ABRE LA RESPUESTA EN CASO DE QUE HAYA SIDO ALMACENADA
//                if (encuesta.abrir(idInformante.where() + " AND id_pregunta = " + pregunta.get_id_pregunta(), null)) {
//                    try {
//                        switch (pregunta.get_id_tipo_pregunta()) {
////                            case Cerrada:
////                            case CerradaBucle:
////                            case CerradaInformante:
////                            case CerradaMatriz:
////                            case ValorTipo:
////                            case ValorTipo2:
////                            case ValorTipoMatriz:
////                            case Upm:
////                            case UpmHijo:
////                            case Incidencia:
////                            case Consulta:
////                            case CerradaConsulta:
////                                pregs[i].setIdResp(encuesta.get_id_respuesta());
////                                break;
//////                                                case Multiple:
////                                                case Autocompletar:
////                                                    pregs[i].setCodResp(encuesta.get_codigo_respuesta());
////                                                case Prioridad:
//                            case Calendario:
//                                pregs[i].setCodResp(encuesta.get_codigo_respuesta());
//                                break;
//                            default:
//                                pregs[i].setCodResp(encuesta.get_codigo_respuesta());
//                                pregs[i].setResp(encuesta.get_respuesta());
//                        }
//                        String af = encuesta.get_codigo_respuesta();
//                        String ab = encuesta.get_respuesta();
////                                            pregs[i].setCodResp(encuesta.get_codigo_respuesta());
//                        pregs[i].setObservacion(encuesta.get_observacion());
//                        pregs[i].setEstado(encuesta.get_estado());
//
//                        int idNivelPregunta = Informante.obtenerNivelPregunta(encuesta.get_id_encuesta().id_pregunta);
//                        boolean esObservada = false;
//
//                        esObservada = Informante.esPreguntaObservada(encuesta.get_id_encuesta().getIdInformante(), pregunta.get_id_pregunta(), idNivelPregunta);
//
//                        if (esObservada) {
//                            pregs[i].setBackgroundColor(getResources().getColor(R.color.colorWaterError));
////                            pregs[i].setObservado(true);
//                        }
////                        idEncuesta.fila = encuesta.get_id_encuesta().fila;
//                    } catch (Exception ex) {
//                        Log.d("preguntaError",pregs[i].getCod().toString());
//                        ex.printStackTrace();
//                    }
//                }
//
//
//                encuesta.free();
//                pregs[i].startAnimation(animation);
                layout.addView(pregs[i]);

// agregar los botones de observacion

                buttons[i] = new Button(context);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    buttons[i].setBackground(context.getDrawable(R.drawable.ic_edit_note));
                }
//                                buttons[i].setText("Observación");
                buttons[i].setTag(i);
                buttons[i].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            int indice = Integer.parseInt(String.valueOf(view.getTag()));
                            Log.d("indiceindice: ", indice + "");
                            mensaje.observationMessageEncIni(activity, context, "obsIni", "Introduzca observación", Html.fromHtml("Escriba la nota:"), pregs[indice].getObservacion(), false, indice);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                });
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                layoutParams.gravity = Gravity.RIGHT;
                buttons[i].setLayoutParams(layoutParams);
                layout.addView(buttons[i]);
                if (idNivel == Parametros.LV_VIVIENDAS || idNivel == Parametros.LV_CABECERA) {
                    buttons[i].setVisibility(View.GONE);
                }


                separadors[i] = new View(context);
                separadors[i].setBackgroundColor(activity.getResources().getColor(R.color.colorSecondary_text));
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                lp.height = 1;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    separadors[i].setElevation(100);
                }
                if (pregs[i].getId() == Pregunta.getIdPregunta(Parametros.CODIGO_PREGUNTA_VIVIENDA_OMITIDA)) {
                    PreguntaView.setViewAndChildrenEnabled(pregs[i], false);
                    if (!Seleccion.hasSelected(Informante.getUpm(idInformante))) {
                        try {
                            pregs[i].setCodResp("997");
                            pregs[i].setResp("NA");
                            ((View) pregs[i]).setVisibility(View.GONE);
                            ((View) buttons[i]).setVisibility(View.GONE);
                            ((View) separadors[i]).setVisibility(View.GONE);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        try {
                            pregs[i].setCodResp("1");
                            pregs[i].setResp("1");

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
//                    PreguntaView.setViewAndChildrenEnabled(pregs[i], false);
                }

                layout.addView(separadors[i], lp);
                //ADICION DE DOS PREGUNTAS PARA BLOQUEO EN REVISITA
//                                    if( ((pregunta.get_id_pregunta() == 2193 || pregunta.get_id_pregunta() == 2194 || pregunta.get_id_pregunta() == 2195 || pregunta.get_id_pregunta() == 2196 || pregunta.get_id_pregunta() == 2197 || pregunta.get_id_pregunta() == 2198 || pregunta.get_id_pregunta() == 2199 ||pregunta.get_id_pregunta() == 15357 ||pregunta.get_id_pregunta() == 15350 ||pregunta.get_id_pregunta() == 15365 ||pregunta.get_id_pregunta() == 15355 || pregunta.get_id_pregunta() == 15007) && ( idInformanteAnterior.correlativo!=0)) ) {
//                                        PreguntaView.setViewAndChildrenEnabled(pregs[i], false);
//                                    }
                ////Bloqueos de los campos en el primer nivel
//                                    if(informante.abrir(idInformante)&&idInformante.correlativo!=0&& informante.get_id_informante_anterior()!=null){
//                                        idInformante=informante.get_id_informante_anterior();
//                                    }else{
//                                        idInformante=new IdInformante(0,0);
//                                    }
//                                    informante.free();

                i++;
            } while (pregunta.siguiente());
//            ocultaPreguntas();
//            inhabilitaPreguntas();
//            PreguntaView.obtenerPosicionDeLaVista(pregs[pregunta.count()-1]);
            ///salto de preguntas iniciales
            if (idNivel == 2) {

//                if (!pregs[1].getCodResp().equals("-1")) {
//                    try {
//                        pregs[2].setCodResp(String.valueOf(Upm.obtenerInstancia(pregs[1].getCodResp())));
//
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//                                    PreguntaView.setViewAndChildrenEnabled(pregs[2], false);
                if (pregs[13].getCodResp().equals("3") || pregs[13].getCodResp().equals("2")) {
                    ejecucion(Pregunta.getIdPregunta(Parametros.CODIGO_PREGUNTA_USO_DE_LA_VIVIENDA), pregs[13].getCodResp());
                } else if (pregs[13].getCodResp().equals("1")) {
                    ///ocultar preguntas
//                    if (Seleccion.hasSelected(Upm.getIdUpm(idInformante.id_asignacion)))
//                        PreguntaView.setViewAndChildrenEnabled(pregs[12], false);

                }
                try{
                    ((View)pregs[Parametros.PID_TOTAL_HM]).setVisibility(View.GONE);
                }catch (Exception e){

                }
                try{
                    PreguntaView.setViewAndChildrenEnabled(pregs[Parametros.PID_VIVIENDA_OMITIDA],false);
                    ((View)pregs[Parametros.PID_VIVIENDA_OMITIDA]).setVisibility(View.GONE);
                }catch (Exception e){

                }
                try{
                    PreguntaView.setViewAndChildrenEnabled(pregs[Parametros.PID_VOE],false);
                    ((View)pregs[Parametros.PID_VOE]).setVisibility(View.GONE);
                }catch (Exception e){

                }
                try{
                    if(Seleccion.hasSelected(Upm.getIdUpm(idInformante.id_asignacion))&&idInformante.correlativo!=0){
                        PreguntaView.setViewAndChildrenEnabled(pregs[2], false);
                    }
                }catch (Exception e){

                }
                if (!pregs[3].getCodResp().equals("-1")&&!pregs[3].getResp().trim().equals("U") ) {
                    ocultarUnionManzanas();
                }



            }

            pregs[0].setFocus();

        } else {
            mensaje.errorMessage(context, null, "Error!", Html.fromHtml("No se encontró la pregunta."), Parametros.FONT_OBS);
        }
        pregunta.free();
//                        }
//                    });
//                } catch (Exception e) {
//                    //print the error here
//                }
//            }
//        }).start();
    }

    public static void ocultarUnionManzanas() {


        try{
            ((View)pregs[4]).setVisibility(View.GONE);
            pregs[4].setResp("2");
            pregs[4].setCodResp("2");


        }catch (Exception e){

        }
        try{
            ((View)pregs[5]).setVisibility(View.GONE);
            pregs[5].setResp("NA");
            pregs[5].setCodResp("997");

        }catch (Exception e){

        }
        Log.d("UNION", pregs[4].getResp());
        Log.d("UNION", pregs[5].getResp());

    }

    public static void mostrarUnionManzanas() {

        try{
            ((View)pregs[4]).setVisibility(View.VISIBLE);
            pregs[4].setResp("");
            pregs[4].setCodResp("-1");


        }catch (Exception e){

        }
        try{
            ((View)pregs[5]).setVisibility(View.VISIBLE);
            pregs[5].setResp("");
            pregs[5].setCodResp("-1");

        }catch (Exception e){

        }
    }


    public static void ejecucion(int idPregunta, String valor) {
        int idPreguntaUso = Pregunta.getIdPregunta(Parametros.CODIGO_PREGUNTA_USO_DE_LA_VIVIENDA);
        if (idPreguntaUso == idPregunta) {
            if (idNivel == Parametros.LV_VIVIENDAS) {
                if (valor.equals("2") || valor.equals("3")) {

//                    if (valor.equals("3")) {
//                        try {
////                            setBolqButtons();
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                    }

                    for (int i = 14; i < 27; i++) {
                        try {
                            Log.d("PREGUNTA_AUTOGUARDADO", String.valueOf(pregs[i].getId()));
                            pregs[i].setEstado(Estado.INSERTADO);
                            try {
                                switch (i) {
                                    case 14:
                                        pregs[i].setResp("0");
                                        pregs[i].setCodResp("0");
                                        break;
                                    case 15:
                                        pregs[i].setResp("0");
                                        pregs[i].setCodResp("0");
                                        break;
                                    case 16:
                                        pregs[i].setResp("0");
                                        pregs[i].setCodResp("0");
                                        break;
                                    case 17:
                                        pregs[i].setResp("0");
                                        pregs[i].setCodResp("0");
                                        break;
                                    case 18:
                                        pregs[i].setResp("NA");
                                        pregs[i].setCodResp("997");
                                        break;
                                    case 19:
                                        pregs[i].setResp("NA");
                                        pregs[i].setCodResp("997");
                                        break;
                                    case 20:
                                        pregs[i].setResp("NA");
                                        pregs[i].setCodResp("997");
                                        break;
                                    case 21:
                                        pregs[i].setResp("NA");
                                        pregs[i].setCodResp("997");
                                        break;
                                    case 24:
                                        break;
                                    case 25:
                                        break;
                                    case 26:
                                        pregs[i].setResp("0");
                                        pregs[i].setCodResp("0");
                                        break;
                                    case 27:
                                        break;
                                    default:
                                        pregs[i].setResp("1");
                                        pregs[i].setCodResp("1");
                                        break;
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
//                            if (pregs[i] instanceof ViewGroup) {
//                                ViewGroup viewGroup = (ViewGroup) pregs[i];
//                                for (int j = 0; j < viewGroup.getChildCount(); j++) {
//                                    View child = viewGroup.getChildAt(j);
////                                    setViewAndChildrenEnabled(child, enabled);
//                                    viewGroup.removeView(child);
//                                }
//                            } else{
                            try {
                                if(i != 25 && i != 28) {
                                    layout.removeView(pregs[i]);
                                    layout.removeView(separadors[i]);
                                    layout.removeView(buttons[i]);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

//                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    try {
                        pregs[Parametros.PID_REFERENCIA_VOE].setCodResp("997");
                        pregs[Parametros.PID_REFERENCIA_VOE].setResp("NA");
                        pregs[Parametros.PID_REFERENCIA_VOE].setEstado(Estado.NOAPLICA);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
//                    try {
//                        PreguntaView.setViewAndChildrenEnabled(pregs[Parametros.PID_INCIDENCIA_VOE], false);
//                        layout.removeView(pregs[Parametros.PID_INCIDENCIA_VOE]);
//                        layout.removeView(separadors[24]);
//                        layout.removeView(buttons[24]);
//                    }catch (Exception e){
//
//                    }

                } else if (valor.equals("1")) {
                    for (int i = 26; i < pregs.length; i++) {
                        layout.removeView(pregs[i]);
                        layout.removeView(separadors[i]);
                        layout.removeView(buttons[i]);
                    }
                    final int idPreguntaVoe = Pregunta.getIdPregunta(Parametros.CODIGO_PREGUNTA_NRO_ORDEN_VIVIENDA);
                    final int idPreguntaOmitida = Pregunta.getIdPregunta(Parametros.CODIGO_PREGUNTA_VIVIENDA_OMITIDA);
                    for (int i = 14; i < pregs.length; i++) {
                        try {
                            if (i == Parametros.PID_VIVIENDA_OMITIDA) {

                            } else if (idInformante.correlativo == 0) {
                                try {
                                    pregs[i].setResp("");
                                    pregs[i].setCodResp("");
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                            if (pregs[i] != null && pregs[i].getId() == idPreguntaVoe) {
//                                PreguntaView.setViewAndChildrenEnabled(pregs[i], false);
                                if (pregs[i].getCodResp().equals("-1") || pregs[i].getCodResp().equals("0")) {
                                    pregs[i].setResp("1");
                                    pregs[i].setCodResp("1");
                                }
                            }
                            if (pregs[i].getId() == Pregunta.getIdPregunta(Parametros.CODIGO_PREGUNTA_TOTAL_PERSONAS)) {
//                                PreguntaView.setViewAndChildrenEnabled(pregs[i], false);
                                try {
                                    pregs[i].setResp(String.valueOf(Integer.parseInt(pregs[15].getCodResp()) + Integer.parseInt(pregs[16].getCodResp())));
                                    pregs[i].setCodResp(String.valueOf(Integer.parseInt(pregs[15].getCodResp()) + Integer.parseInt(pregs[16].getCodResp())));
                                    ((View) pregs[i]).setVisibility(View.GONE);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                            try {
                                layout.removeView(pregs[i]);
                                layout.removeView(separadors[i]);
                                layout.removeView(buttons[i]);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            try {
                                layout.addView(pregs[i]);
                                layout.addView(separadors[i]);
                                layout.addView(buttons[i]);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    if (pregs[Parametros.PID_VOE] != null) {
                        PreguntaView.setViewAndChildrenEnabled(pregs[Parametros.PID_VOE], false);
                    }
                    if (pregs[Parametros.PID_VIVIENDA_OMITIDA] != null) {
                        PreguntaView.setViewAndChildrenEnabled(pregs[Parametros.PID_VIVIENDA_OMITIDA], false);
                    }
                    if (pregs[Parametros.PID_INCIDENCIA_VOE] != null) {
                        PreguntaView.setViewAndChildrenEnabled(pregs[Parametros.PID_INCIDENCIA_VOE], true);
                    }

                    try {
                        if (idInformante.correlativo == 0)
                            pregs[Parametros.PID_INCIDENCIA_VOE].setCodResp("");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private void ocultaPreguntas() {
        for (int i = 1; i < pregs.length; i++) {
//            pregs[i].setVisible(pregs[i], false);
            layout.setVisibility(View.GONE);
            pregs[1].setVisible(pregs[1], false);
            layout.removeView(pregs[i]);
//            PreguntaView.setViewAndChildrenEnabled(pregs[i], false);
        }
        layout.addView(pregs[6]);
    }

    public void inhabilitaPreguntas() {
        if (idNivel == Parametros.LV_VIVIENDAS) {
            int idPreguntaVoe = Pregunta.getIDpregunta(idSeccion, TipoPregunta.NumeroVivienda);
            int idPregunta_ciudad_comunidad = Pregunta.getIdPregunta(Parametros.CODIGO_PREGUNTA_CIUDAD_COMUNIDAD);
            int idPreguntaOmitida = Pregunta.getIdPregunta(Parametros.CODIGO_PREGUNTA_VIVIENDA_OMITIDA);

            for (int i = 0; i < pregs.length; i++) {
                if ( Seleccion.hasSelected(Upm.getIdUpm(idInformante.id_asignacion)) &&idInformante.correlativo!=0) {
                    //TODO: CONTROL DE OMITIDA
                    if(!Informante.esOmitida(idInformante.id_asignacion, idInformante.correlativo))
                        PreguntaView.setViewAndChildrenEnabled(pregs[i], false);
                }
                if (pregs[i].getId() == idPreguntaVoe) {
                    PreguntaView.setViewAndChildrenEnabled(pregs[i], false);
                }
//                if (Usuario.getRol() == Parametros.ENCUESTADOR && pregs[i].getId() == idPreguntaOmitida) {
                if (pregs[i].getId() == idPreguntaOmitida) {
                    PreguntaView.setViewAndChildrenEnabled(pregs[i], false);
                    if (!Seleccion.hasSelected(Informante.getUpm(idInformante))) {
                        try {
                            pregs[i].setCodResp("997");
                            pregs[i].setResp("NA");
                            ((View) pregs[i]).setVisibility(View.GONE);
                            ((View) buttons[i]).setVisibility(View.GONE);
                            ((View) separadors[i]).setVisibility(View.GONE);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else if(idInformante.correlativo==0) {
                        try {
                            pregs[i].setCodResp("1");
                            pregs[i].setResp("1");

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
//                    PreguntaView.setViewAndChildrenEnabled(pregs[i], false);
                }

                if (pregs[i].getId() == Pregunta.getIdPregunta(Parametros.CODIGO_PREGUNTA_TOTAL_PERSONAS)) {
                    try {
                        ((View) pregs[i]).setVisibility(View.GONE);
                        ((View) buttons[i]).setVisibility(View.GONE);
                        ((View) separadors[i]).setVisibility(View.GONE);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                if (pregs[i].getId() == idPregunta_ciudad_comunidad) {
                    PreguntaView.setViewAndChildrenEnabled(pregs[i], false);
                    try {
                        Encuesta enc_com = new Encuesta();
                        if (!enc_com.abrirLV(idInformante.where() + " AND id_pregunta = " + idPregunta_ciudad_comunidad, null)) {
                            pregs[i].setCodResp(String.valueOf(Informante.getUpm(idInformante)));
                            pregs[i].setResp(Upm.getCodigoComunidad(idInformante.id_asignacion));
                        }
                        enc_com.free();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    PreguntaView.setViewAndChildrenEnabled(pregs[i], false);
                }
                if(pregs[i].getId()==Pregunta.getIdPregunta(Parametros.CODIGO_PREGUNTA_MANZANA_COMUNIDAD)&&idInformante.correlativo!=0){
                    try {
                        ((Prioridad)pregs[5]).setOpcionesDescartado(pregs[i].getResp());
                    }catch (Exception e){

                    }
                }

            }

        } else if (idNivel == Parametros.LV_CABECERA) {
            for (int i = 1; i < pregs.length; i++) {
                if (pregs[i].getCodResp().equals("-1")) {
                    pregs[i].setEstado(Estado.INSERTADO);
                    pregs[i].setResp("0");
                    if (i == 9) {
                        try {
                            pregs[i].setResp("-");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                PreguntaView.setViewAndChildrenEnabled(pregs[i], false);
            }
            ///habilita para nivel de cabecera de LV
            PreguntaView.setViewAndChildrenEnabled(pregs[8], true);
        }
    }

    private void insertaRespuestas() {
        if (idNivel == 2) {
            int idPreguntaNroVoe = Pregunta.getIdPregunta(Parametros.CODIGO_PREGUNTA_NRO_ORDEN_VIVIENDA);
            for (int i = 0; i < pregs.length; i++) {
//                pregs[Parametros.PID_VIVIENDA_OMITIDA].setEstado(Estado.INSERTADO);
//                pregs[Parametros.PID_VIVIENDA_OMITIDA].setResp("0");
                if (pregs[i].getId() == idPreguntaNroVoe) {
                    pregs[i].setEstado(Estado.INSERTADO);
                    try {
                        if (Seleccion.hasSelected(Informante.getUpm(idInformante))) {
//                            if (idInformante.correlativo == 0) {
                            if (pregs[13].getCodResp().equals("1") && pregs[i].getCodResp().equals("1")&&idInformante.correlativo==0) {
                                ///get last VOE
                                int lastVoe = Informante.obtenerNroUltimoVoe(Informante.getUpm(idInformante));
                                pregs[i].setCodResp(String.valueOf(lastVoe + 1));
                                pregs[i].setResp(String.valueOf(lastVoe + 1));
                            }

                        } else {

                            if (pregs[13].getCodResp().equals("1")) {
                                if (pregs[i].getCodResp().equals("-1") || pregs[i].getCodResp().equals("0")) {
                                    pregs[i].setCodResp("1");
                                    pregs[i].setResp("1");
                                }
                            } else {
                                try {
                                    pregs[i].setCodResp("0");
                                    pregs[i].setResp("0");

                                }catch (Exception e){
                                    e.printStackTrace();
                                }

                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                }
            }
            try{
                int sum;
                int a=0;
                int b=0;
                try{
                    a=Integer.parseInt(pregs[15].getResp());
                }catch (Exception e){

                }
                try{
                    b=Integer.parseInt(pregs[16].getResp());
                }catch (Exception e){

                }
                pregs[Parametros.PID_TOTAL_HM].setResp(String.valueOf(a+b));
            }catch (Exception e){

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
                    p.setFocus();
                    mensaje.errorMessage(getContext(), null, "Error!", Html.fromHtml("Faltó responder: " + p.getCod()), Parametros.FONT_OBS);
                    botonGuardar.setEnabled(true);
                    return;

                } else {
                    Pregunta pregunta = new Pregunta();
                    if (pregunta.abrir(p.getId())) {
                        String asd = p.getCod();
                        if (!p.getCod().startsWith("AA")) {
                            List<String> omision = Pregunta.getOmisionId(p.getId());
                            if (!omision.contains(p.getCodResp())) {
                                if (p instanceof Numero || p instanceof Decimal) {
                                    try {
                                        //Quemado para casos exepcionales tipo uso de vivienda 2 y 3
                                        if (!((Integer.valueOf(p.getId()) == 18610 || Integer.valueOf(p.getId()) == 18611 || Integer.valueOf(p.getId()) == 18613 || Integer.valueOf(p.getId()) == 18616 || Integer.valueOf(p.getId()) == 37708 || Integer.valueOf(p.getId()) == 37709 || Integer.valueOf(p.getId()) == 37710) && p.getCodResp().equals("0"))) {
                                            if ((Float.parseFloat(p.getCodResp()) < pregunta.get_minimo() && pregunta.get_minimo() > 0) || (Float.parseFloat(p.getCodResp()) > pregunta.get_maximo() && pregunta.get_maximo() > 0)) {
                                                p.setFocus();
                                                mensaje.errorMessage(getContext(), null, "Error!", Html.fromHtml(String.format("El valor de " + pregunta.get_variable() + " debe estar entre %d y %d", pregunta.get_minimo(), pregunta.get_maximo())), Parametros.FONT_OBS);
                                                botonGuardar.setEnabled(true);
                                                return;
                                            }
                                        }

                                    } catch (Exception ex) {
                                        p.setFocus();
                                        mensaje.errorMessage(getContext(), null, "Error!", Html.fromHtml(String.format("El valor de " + pregunta.get_variable() + " debe estar entre %d y %d", pregunta.get_minimo(), pregunta.get_maximo())), Parametros.FONT_OBS);
                                        botonGuardar.setEnabled(true);
                                        return;
                                    }
                                } else if (p instanceof Abierta || p instanceof Memoria || p instanceof Autocompletar) {
                                    if (p.getResp().length() < pregunta.get_minimo() && pregunta.get_minimo() > 0 || (p.getResp().length() > pregunta.get_maximo() && pregunta.get_maximo() > 0)) {
                                        p.setFocus();
                                        mensaje.errorMessage(getContext(), null, "Error!", Html.fromHtml(String.format("La longitud de " + pregunta.get_variable() + " debe estar entre %d y %d", pregunta.get_minimo(), pregunta.get_maximo()) + " (Valor actual: " + p.getResp().length() + ")"), Parametros.FONT_OBS);
                                        botonGuardar.setEnabled(true);
                                        return;
                                    }
                                }

                                if(Integer.valueOf(p.getId()) == 18609){
                                    if (Seleccion.hasSelected(Informante.getUpm(idInformante)) && !p.getCodResp().equals("1")) {
                                        p.setFocus();
                                        mensaje.errorMessage(getContext(), null, "Error!", Html.fromHtml(String.format("Si es una VIVIENDA OMITIDA, el Uso de la Vivienda, " + pregunta.get_variable() + " debe ser VIVIENDA OCUPADA")), Parametros.FONT_OBS);
                                        botonGuardar.setEnabled(true);
                                        return;
                                    }
                                }
                            }
                        }
                        if(p.getCod().equals(Parametros.CODIGO_PREGUNTA_RECORRIDO_MANZANA)){
                            int manzano = Integer.parseInt(p.getCodResp());
                            boolean recorrido = recorridoManzana(manzano);
                            if(!recorrido){
                                mensaje.errorMessage(getContext(), null, "Error!", Html.fromHtml("Revisa el manzano, este debe ser llenado de manera correlativa segun vayas trabajando." ), Parametros.FONT_OBS);
                                return;
                            }
                        }
                        String av = pregunta.get_codigo_pregunta();
                        String bv = pregunta.get_pregunta();
                        String por = pregunta.get_regla();
                        if (pregunta.get_regla() != null) {
                            if (valores == null) {
                                valores = cargar();
                            }

                            Map<Integer, String> reglas = Pregunta.getRegla_conver(pregunta.get_regla());
                            for (Integer key : reglas.keySet()) {
                                String regla = reglas.get(key);
                                String[] a = regla.split("°");
                                String rregla = a[0];
                                String rmensaje = a[1];
                                String rcodigo = a[2];

                                if (!evaluar(rregla, valores)) {
                                    p.setFocus();
                                    mensaje.errorMessage(getContext(), null, "Error de consistencia!", Html.fromHtml(rmensaje), Parametros.FONT_OBS);
                                    inhabilitaPreguntas();
                                    botonGuardar.setEnabled(true);
                                    return;
                                }
                            }
                        }
                    } else {
                        mensaje.errorMessage(getContext(), null, "Error!", Html.fromHtml("No existe la pregunta."), Parametros.FONT_OBS);
                        botonGuardar.setEnabled(true);
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
            } else {
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
//                if (nuevo) {
                if (idNivel == 3) {
                    codigo = pregs[2].getCodResp();
                    if (codigo == null) {
                        mensaje.errorMessage(getContext(), null, "Error!", Html.fromHtml("No se encontró la UPM. Error al generar el código."), Parametros.FONT_OBS);
                        botonGuardar.setEnabled(true);
                        return;
                    } else {
                        // EH-17
                        switch (pregs[0].getCodResp().length()) {
                            case 1:
                                codigo = codigo + "-00" + pregs[0].getCodResp();
                                break;
                            case 2:
                                codigo = codigo + "-0" + pregs[0].getCodResp();
                                break;
                            default:
                                codigo = codigo + "-" + pregs[0].getCodResp();
                                break;
                        }
                        codigo = codigo + pregs[1].getCodResp();
                    }
                    if (Informante.exists(codigo, idInformante)) {
                        informante.free();
                        mensaje.errorMessage(getContext(), null, "Error!", Html.fromHtml("El folio ya existe."), Parametros.FONT_OBS);
                        botonGuardar.setEnabled(true);
                        return;
                    }
                } else { // CODIGO PARA NIVEL 2
                    if (flag) {
                        if (idNivel == 1) {
                            codigo = "0";
                        } else {
                            codigo = "999";
                        }
                    } else {
                        codigo = informante.get_codigo();
                    }
                }
                informante.set_codigo(codigo);
                // TERMINA DE GENERAR EL CODIGO

                informante.set_id_upm(Informante.getUpm(idInformante));
                if (idNivel > 2 && idUpmHijo != 0) {
                    informante.set_id_upm_hijo(idUpmHijo);
                } else if (idNivel == 2 || idNivel == 3) {
                    int idUpmHijoTemp = 0;

                    idUpmHijoTemp = idNivel == 2 ? UpmHijo.getIdUpmHijo(pregs[1].getCodResp()) : UpmHijo.getIdUpmHijo(pregs[2].getCodResp());
                    if (idUpmHijoTemp != 0)
                        informante.set_id_upm_hijo(idUpmHijoTemp);
                }
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
                        Informante.actualizaCodigoInformante(idInformante.id_asignacion,idNivel);
                        break;
                    case 3:
                        idInformante = (IdInformante) informante.guardar();
                        break;
                    default:
                        idInformante = informante.guardar(idPadre);
                        Informante.actualizaCodigoInformante(idInformante.id_asignacion,idNivel);
                        break;
                }
                if (idInformante != null) {
                    String descripcion = "";
                    int jj = 0;
                    for (PreguntaView preg : pregs) {

                        if (idNivel == 2) {
                            if (encuesta.abrirLV(idInformante.where() + " AND id_pregunta = " + preg.getId(), null)) {
                                encuesta.editar();
                            } else {
                                encuesta.nuevo();
                            }
                        } else {
                            if (encuesta.abrir(idInformante.where() + " AND id_pregunta = " + preg.getId(), null)) {
                                encuesta.editar();
                            } else {
                                encuesta.nuevo();
                            }
                        }
                        encuesta.set_id_encuesta(new IdEncuesta(idInformante.id_asignacion, idInformante.correlativo, preg.getId(), 1));
                        encuesta.set_codigo_respuesta(preg.getCodResp());
                        encuesta.set_respuesta(preg.getResp());
                        encuesta.set_observacion(preg.getObservacion());
                        encuesta.set_usucre(Usuario.getLogin());
                        encuesta.set_estado(preg.getEstado());
                        encuesta.set_latitud(Movil.getGPS().split(";")[0].toString());
                        encuesta.set_longitud(Movil.getGPS().split(";")[1].toString());
                        encuesta.set_visible("t");
                        if(idNivel==2){
                            if (!pregs[13].getCodResp().equals("1") && ((jj > 13 && jj < 25) || jj==26 )) {
                                encuesta.set_visible("f");

                            }
                            if (jj == Parametros.PID_VIVIENDA_OMITIDA && !Seleccion.hasSelected(Upm.getIdUpm(idInformante.id_asignacion))) {
                                encuesta.set_visible("f");
                            }
                            if((jj==5||jj==4)&&!pregs[3].getCodResp().trim().equals("10")){
                                encuesta.set_visible("f");
                            }
                        }
                        Log.d("GUARDAR",preg.getId() + " - " + preg.getCodResp() + " - "+preg.getResp());

                        encuesta.guardar2();

                        List<String> omision = Pregunta.getOmisionId(preg.getId());
                        if (!omision.contains(preg.getCodResp())) {
                            descripcion = descripcion + preg.getResp() + " ";
                        }

                        jj++;
                    }
//                    activity.finish();
                    encuesta.free();

                    if (informante.abrir(idInformante)) {
                        if (informante.editar()) {
//                            informante.set_codigo(idInformante.id_asignacion+"-"+idInformante.correlativo);
                            informante.set_descripcion(descripcion);
                            informante.guardar();
                        }
                    }
                    int idPregunta = Encuesta.ultimaInicial(idInformante);
//                    Siguiente idSiguiente = new Flujo().siguiente(idInformante, idPregunta);
                    if (idPregunta > 0) {
                        Pregunta pregunta = new Pregunta();
                        if (pregunta.abrir(idPregunta)) {
                            if (idNivel == 1 || idNivel == 2) {
                                accionFinalBoleta();
                            } else {
//                                int nuevoIdNivel =  nuevoNivel.getIdNivel(idSiguiente.idSiguiente);
                                //DEFAULT ES 4
                                int nuevoIdNivel = 4;
                                Map<String, Object> values = Informante.obtenerPreguntaInicialNivel4(nuevoIdNivel);
                                accionInicioBoleta(idInformante, new IdEncuesta(idInformante.id_asignacion, idInformante.correlativo, Integer.parseInt(values.get("id_pregunta").toString()), 1), nivel, nuevoIdNivel, Integer.parseInt(values.get("id_seccion").toString()), idInformante);

                            }
                        } else {
                            mensaje.errorMessage(getContext(), null, "Error!", Html.fromHtml("No se encontró la pregunta."), Parametros.FONT_OBS);
                            botonGuardar.setEnabled(true);
                        }
                        pregunta.free();
                    } else {
                        mensaje.errorMessage(getContext(), null, "Error!", Html.fromHtml(idPregunta + "Final inesperado."), Parametros.FONT_OBS);
                        botonGuardar.setEnabled(true);
                    }
                } else {
                    mensaje.errorMessage(getContext(), null, "Error!", Html.fromHtml("No se pudo guardar."), Parametros.FONT_OBS);
                    botonGuardar.setEnabled(true);
                }
            } else {
                mensaje.errorMessage(getContext(), null, "Error!", Html.fromHtml("No se pudo crear el informante."), Parametros.FONT_OBS);
                botonGuardar.setEnabled(true);
            }
            informante.free();
        } catch (Exception ex) {
            ex.printStackTrace();
            mensaje.errorMessage(getContext(), null, "Error!", Html.fromHtml(ex.getMessage()), Parametros.FONT_OBS);
            botonGuardar.setEnabled(true);
        }
    }

    private void accionInicioBoleta(IdInformante n_informante, IdEncuesta n_encuesta, int n_nivel, int n_idNivel, int n_seccion, IdInformante n_padre) {
        iComunicaFragments.enviarDatos(n_informante, n_encuesta, n_nivel, n_idNivel, n_seccion, n_padre, 2);
        botonGuardar.setEnabled(true);
//        getActivity().finish();
    }

    private void accionFinalBoleta() {
        int voes = Informante.getContarLV(idPadre);
        if (voes >= 12 || Upm.isSorteoEspecial(Upm.getIdUpm(idInformante.id_asignacion))) {
            if (Seleccion.hasSelected(Informante.getUpm(idInformante))) {
                Informante.actualizaOmitida(idInformante);
                Informante.actualizaVoeOmitida(idInformante);
                Seleccion.sortearOmitidas(Upm.getCodigo(idInformante.id_asignacion).endsWith("A") ? 1 : 0, Upm.getIdUpm(idInformante.id_asignacion));
            }
        }
        Seleccion.ordenarLV(Upm.getIdUpm(idInformante.id_asignacion));
//        if (!Seleccion.hasSelected(Upm.getIdUpm(idInformante.id_asignacion))) {

        mensaje.decisionMessageFinalizar(getActivity(), "crear_nueva_boleta", "vivienda", "Vivienda registrada", Html.fromHtml("agregar otra vivienda en la manzana?"));
//        } else {
//            vivienda(getActivity());
//        }
        botonGuardar.setEnabled(true);
    }

    public static void vivienda(Context context) {
        Bundle bundle = new Bundle();
        bundle.putInt("IdUpm", Informante.getUpm(idInformante));
        Intent listado = new Intent(MyApplication.getContext(), ListadoViviendasActivity.class);
        listado.putExtras(bundle);
        ((Activity) context).startActivity(listado);
        ((Activity) context).finish();
    }

    public static void crear_nueva_boleta() {
        idInformante.correlativo = 0;
        int inicio =Seleccion.hasSelected(Upm.getIdUpm(idInformante.id_asignacion))?1:7;
        //limpiarFormulario
        for (int i = inicio; i <= pregs.length; i++) {
            try {
                if (i != Parametros.PID_VIVIENDA_OMITIDA) {
                    pregs[i].setResp("");
                    pregs[i].setCodResp("");
                    pregs[i].setCodResp("-1");
                }
                layout.removeView(pregs[i]);
                layout.removeView(buttons[i]);
                layout.removeView(separadors[i]);
            } catch (Exception ee) {

            }

        }
        dibujarPregunta1(inicio);

        focusAnimate(inicio);
    }

    private static void focusAnimate(int i) {
        ScrollView scrollView=(ScrollView) layout.getParent();
        if (i<2){
            scrollView.fullScroll(ScrollView.FOCUS_UP);
        }else {
            i++;
        }
        for (int j = 0; j < i; j++) {
            pregs[j].setFocus();
        }

    }

    private Map<String, String> cargar() {
        Map<String, String> res = new HashMap<>();
        for (PreguntaView p : pregs) {
            res.put("$" + p.getId(), p.getCodResp());
        }
        return res;
    }

    public boolean evaluar(String regla, Map<String, String> valores) {
        boolean result;
        List<String> allMatches = new ArrayList<String>();
        String nuevoValor = regla;

        Pattern p = Pattern.compile("\\$\\d+");
        Matcher m = p.matcher(regla);
        while (m.find()) {
            String str = m.group();
            allMatches.add(str);
        }
        for (String str : allMatches) {
            String val;
            String aq = valores.get(str);
            if (valores.get(str) == null) {
                String element = str.replace("$", "");
                val = Encuesta.obtenerValorId(idPadre, element);
                if (val.contains(",")) {
                    val = val.replace(",", "||");
                    val = "(" + val + ")";
                }
                nuevoValor = nuevoValor.replace(str, val);
            } else {
                nuevoValor = nuevoValor.replace(str, valores.get(str));
            }
        }

        if (nuevoValor.contains("SORTEADO")) {
            if (Seleccion.hasSelected(Informante.getUpm(idInformante))) {
                nuevoValor = nuevoValor.replace("SORTEADO", "1");
            } else {
                nuevoValor = nuevoValor.replace("SORTEADO", "0");
            }
        }
        //FALTA EVALUAR LOS DATOS DE PREGUNTAS MULTIPLES Y PREGUNTAS DE ORDEN
        BigDecimal eval = null;
        Log.d("EVALUAR",nuevoValor);
        Expression expression = new Expression(nuevoValor);
        eval = expression.eval();
        Log.d("EVALUADO", String.valueOf(eval));
        if (Integer.parseInt(String.valueOf(eval)) > 0) {
            result = true;
        } else {
            result = false;
        }
        return result;
    }

    public void ocultar() {
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            //Aquí esta la magia
            InputMethodManager input = (InputMethodManager) (getActivity().getSystemService(Context.INPUT_METHOD_SERVICE));
            input.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }


    //    @SuppressWarnings("unused")
    public static void obsIni(String obs, int indiceIni) {
        if (obs.length() > 3) {
            pregs[indiceIni].setObservacion(obs);
        }
    }

    public static void refrescaManza(String codigo) {

        if (codigo.equals("")) {
            return;
        }

        Pregunta preguntaAux = new Pregunta();
        if (preguntaAux.abrir(Parametros.ID_MANZANA_COMUNIDAD)) {
            Map<Integer, String> respuestas;
            Catalogo c = new Catalogo("cat_manzanos");
            respuestas = c.obtenerCatalogoUpmManzanaLV(codigo);
            c.free();

            PreguntaView a = new Cerrada(context, 1, preguntaAux.get_id_pregunta(), preguntaAux.get_id_seccion(), preguntaAux.get_variable(), preguntaAux.get_pregunta(), respuestas, preguntaAux.get_codigo_especifique(), null, null, preguntaAux.get_ayuda(), false);
            ViewGroup vparent = (ViewGroup) pregs[2].getParent();

            if (vparent == null) {
                return;
            }

            final int index = vparent.indexOfChild(pregs[2]);
            try {

                vparent.addView(a, index);
                vparent.removeView(pregs[2]);

            } catch (Exception e) {
                e.printStackTrace();
            }
            pregs[2] = a;
            if (Seleccion.hasSelected(Upm.getIdUpm(idInformante.id_asignacion)) && idInformante.correlativo != 0) {
                PreguntaView.setViewAndChildrenEnabled(pregs[2], false);
            }
            preguntaAux.free();
        }

        try {
            Map<String, String> nombreUpm = UpmHijo.getNombreUpm(codigo);
            pregs[0].setCodResp(nombreUpm.get("id_upm"));
            pregs[0].setResp(nombreUpm.get("nombre"));
            PreguntaView.setViewAndChildrenEnabled(pregs[0], false);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            if (idNivel == 2 && !pregs[13].getCodResp().equals("-1")) {
                ejecucion(pregs[13].getId(), pregs[13].getCodResp());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static boolean recorridoManzana(int recorrido){
        Upm upm = new Upm();
        int idUpm = upm.getIdUpm(idInformante.id_asignacion);
        boolean res = upm.revisaRrecorrido(idUpm, recorrido);
        upm.free();

        return res;
    }

    public static void agregarObservacionEntrevistaCompleta() {
        pregs[Parametros.PID_REFERENCIA_VOE].setResp("SIN OBSERVACIÓN");
        pregs[Parametros.PID_INCIDENCIA_VOE].setFocus();
//        if(!pregs[12].equals("1")){
//
//
//        }
    }
    public static void setInvalidOption(String valor) {
        ((Prioridad)pregs[5]).setOpcionesDescartado(valor);
//        if(!pregs[12].equals("1")){
//
//
//        }
    }
//    public static void setBolqButtons() {
//        ((Abierta) pregs[5]).bloquearBotones();
//    }

}
