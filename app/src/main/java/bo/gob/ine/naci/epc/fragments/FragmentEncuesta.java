package bo.gob.ine.naci.epc.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.udojava.evalex.Expression;

import org.json.JSONException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import bo.gob.ine.naci.epc.BoletaActivity;
import bo.gob.ine.naci.epc.MyApplication;
import bo.gob.ine.naci.epc.R;
import bo.gob.ine.naci.epc.adaptadores.IComunicaFragments2;
import bo.gob.ine.naci.epc.entidades.Asignacion;
import bo.gob.ine.naci.epc.entidades.EncLog;
import bo.gob.ine.naci.epc.entidades.Encuesta;
import bo.gob.ine.naci.epc.entidades.Estado;
import bo.gob.ine.naci.epc.entidades.Flujo;
import bo.gob.ine.naci.epc.entidades.IdEncuesta;
import bo.gob.ine.naci.epc.entidades.IdInformante;
import bo.gob.ine.naci.epc.entidades.Informante;
import bo.gob.ine.naci.epc.entidades.Observacion;
import bo.gob.ine.naci.epc.entidades.Pregunta;
import bo.gob.ine.naci.epc.entidades.Regla;
import bo.gob.ine.naci.epc.entidades.Seccion;
import bo.gob.ine.naci.epc.entidades.Siguiente;
import bo.gob.ine.naci.epc.entidades.TableDynamicObservacion;
import bo.gob.ine.naci.epc.entidades.UpmHijo;
import bo.gob.ine.naci.epc.entidades.Usuario;
import bo.gob.ine.naci.epc.herramientas.Movil;
import bo.gob.ine.naci.epc.herramientas.Parametros;
import bo.gob.ine.naci.epc.herramientas.ValidatorConsistencias;
import bo.gob.ine.naci.epc.preguntas2.Abierta;
import bo.gob.ine.naci.epc.preguntas2.Autocompletar;
import bo.gob.ine.naci.epc.preguntas2.Calendario;
import bo.gob.ine.naci.epc.preguntas2.Cantidad;
import bo.gob.ine.naci.epc.preguntas2.Cerrada;
import bo.gob.ine.naci.epc.preguntas2.CerradaInformante;
import bo.gob.ine.naci.epc.preguntas2.CerradaMatriz;
import bo.gob.ine.naci.epc.preguntas2.CerradaOmitida;
import bo.gob.ine.naci.epc.preguntas2.Decimal;
import bo.gob.ine.naci.epc.preguntas2.Fecha;
import bo.gob.ine.naci.epc.preguntas2.Formula;
import bo.gob.ine.naci.epc.preguntas2.Foto;
import bo.gob.ine.naci.epc.preguntas2.Gps;
import bo.gob.ine.naci.epc.preguntas2.HoraMinuto;
import bo.gob.ine.naci.epc.preguntas2.Memoria;
import bo.gob.ine.naci.epc.preguntas2.MesAnio;
import bo.gob.ine.naci.epc.preguntas2.Multiple;
import bo.gob.ine.naci.epc.preguntas2.Numero;
import bo.gob.ine.naci.epc.preguntas2.PreguntaView;
import bo.gob.ine.naci.epc.preguntas2.Prioridad;
import bo.gob.ine.naci.epc.preguntas2.SeleccionKish;
import bo.gob.ine.naci.epc.preguntas2.Tabla;
import bo.gob.ine.naci.epc.preguntas2.TablaMatriz;
import bo.gob.ine.naci.epc.preguntas2.ValorTipo;
import bo.gob.ine.naci.epc.preguntas2.ValorTipo2;
import bo.gob.ine.naci.epc.preguntas2.ValorTipoGastos;
import bo.gob.ine.naci.epc.preguntas2.ValorTipoMatriz;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentEncuesta#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentEncuesta extends Fragment implements View.OnTouchListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private View vista;
    private View vistaExpandible;
    private static LinearLayout layout;
    private static MaterialButtonToggleGroup seccionButtons;
    private ScrollView scrollView;
    private int seccionId;
    private static Siguiente idSiguiente;
    private PreguntaView preg;
    private static Encuesta encuesta;
    private static Informante informante;
    private Pregunta pregunta;

    private RelativeLayout relativeLayout;

    private static IdInformante idInformante;
    private static IdInformante idPadre;
    private static int nivel;
    private static int idNivel;
    private static IdEncuesta idEncuesta;
    private static int idSeccion;
    private static int idUpmHijo;
    private static FloatingActionButton botonGuardar;
    private static FloatingActionButton botonAvance;

    private static PreguntaView[] pregs;
    private static View[] separadors;
    private static Button[] buttons;
    private static Button[] buttonOk;
    private static int indice = -1;
    private static Animation animation;
    private boolean nuevo = false;
    private static LinearLayout.LayoutParams lp;

    private static Activity activity;
    private static IComunicaFragments2 iComunicaFragments;

    private static Handler mHandler = new Handler();
    private static int cursor = -1;
    private Boolean lista = false;
    private int seccion_ant;

    private static PreguntaView pregunti;

    private static boolean dataInicial = false;

    protected ProgressBar progresoActual = null;
    protected PreguntaView preguntaActual = null;
    protected static PreguntaView preguntaActualStatic = null;
    protected View separdorActual = null;
    protected LinearLayout buttonActual = null;
    protected Button buttonCalendarioActual = null;

    private ProgressDialog progressDialog = null;

    protected ProgressDialog progressDialogActual = null;

    protected Map<Integer, PreguntaView> gPreguntaView = new HashMap<>();

//    protected Map<Integer, LinearLayout> gButtons = new HashMap<>();

    protected Map<Integer, ProgressBar> gProgressBar = new HashMap<>();
    protected Map<Integer, View> gSepardor = new HashMap<>();
    protected boolean mostrarSeccion = false;

    protected static Boolean hiloActivo = false;

    protected static MutableLiveData<Integer> listen;
    protected static int index = 0;
    protected static int indice_guardar = 0;
    protected static int indice_avance = 0;
    protected static boolean edicion_activada[] = new boolean[1];
    protected static boolean pasar_justificacion[] = new boolean[1];


    protected static TextView nombSeccion;
    private View dialogLayout;

    private static final int LIMIT_GUARDAR = 1;
    private static final int LIMIT_AVANCE = 30;
    private int buttonPressed = 1;
    //VAR
    private int idBase = -1;
    private static IdInformante idInformanteAnterior;
    private static int nroRevisita;
    private static Pattern m = Pattern.compile("\\[(.*?)\\]");
    private TextView textViewDato;

    //TODO:BRP
    private Map<Integer, Map<String, String>> preguntaRespuesta = new HashMap<>();
    private List<String> seccionesList = new ArrayList<>();
    private List<String> seccionesButtonList = new ArrayList<>();

    private Map<Integer, LinearLayout> gLayoutSecciones = new HashMap<>();
    private Map<Integer, MaterialButton> gButtonSecciones = new HashMap<>();
    private Map<Integer, MaterialButton> gToogleSecciones = new HashMap<>();

    protected Context context;
    protected Map<Integer, Pregunta> arrayPreg;

    protected String titleMsj = "";
    protected Spanned msj = Html.fromHtml("");

    protected String secciones;
    protected String tipo;

    protected int fila = 1;

    public FragmentEncuesta() {
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
    public static FragmentEncuesta newInstance(String param1, String param2) {
        FragmentEncuesta fragment = new FragmentEncuesta();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        vista = inflater.inflate(R.layout.fragment_fragment_encuesta, container, false);

        scrollView = (ScrollView) vista.findViewById(R.id.scroll_encuesta_boleta);
        layout = (LinearLayout) vista.findViewById(R.id.layout_encuesta_boleta);
        seccionButtons = (MaterialButtonToggleGroup) vista.findViewById(R.id.seccionButtons);
        textViewDato = (TextView) vista.findViewById(R.id.textViewDato);

        Bundle bundle = getArguments();

        if (bundle != null) {
            idEncuesta = new IdEncuesta((int[]) bundle.getIntArray("IdEncuesta"));
            Log.d("REVISION0", idEncuesta.where());
            if(bundle.containsKey("fila")){
                fila = bundle.getInt("fila");
                Log.d("FILA_NUEVA", String.valueOf(fila));
            }

            idInformante = new IdInformante(idEncuesta.id_asignacion, idEncuesta.correlativo);
            Informante inf = new Informante();
            inf.abrir(idInformante);
            idPadre = inf.get_id_informante_padre().id_asignacion == 0 ? idInformante : inf.get_id_informante_padre();
            inf.free();

            Pregunta preg = new Pregunta();
            preg.abrir(idEncuesta.id_pregunta);
            idSeccion = preg.get_id_seccion();
            preg.free();

            Seccion secc = new Seccion();
            secc.abrir(idSeccion);
            idNivel = secc.get_id_nivel();
            secc.free();

            idUpmHijo = 0;
            seccionesList.clear();
            seccionesButtonList.clear();

        }
        encuesta = new Encuesta();
        informante = new Informante();
        informante.abrir(idInformante);

        botonGuardar = vista.findViewById(R.id.botonGuardarEncuesta);
        botonAvance = vista.findViewById(R.id.botonGuardarEncuestaAvance);
        indice_avance = 0;
        indice_guardar = 0;
        buttonPressed = 1;

        listen = new MutableLiveData<>();
        listen.observeForever(new Observer<Integer>() {
            @Override
            public void onChanged(Integer s) {
                Log.d("cambio", String.valueOf(listen.getValue()));
                Log.d("id_a", String.valueOf(idInformante.id_asignacion));
                Log.d("corr", String.valueOf(idInformante.correlativo));

                preguntaActual = gPreguntaView.get(listen.getValue());
                Log.d("REVISION01", idEncuesta.where());
                separdorActual = gSepardor.get(listen.getValue());
                seccion_ant = preguntaActual.getIdSeccion();
                Log.d("cambio", String.valueOf(listen.getValue()));
                idEncuesta.id_pregunta = preguntaActual.getId();
                Log.d("REVISION02", idEncuesta.where());
                Log.d("REVISION03", preguntaActual.getCodResp() + "-" + preguntaActual.getResp() + "-" + preguntaActual.getCod());
                new procesa().execute(preguntaActual.getId(), 1);

            }
        });

        startThree();

        LayoutInflater inflaterNom = getLayoutInflater();
        dialogLayout = inflaterNom.inflate(R.layout.adapter_expandible, null);
        nombSeccion = (TextView) (((RelativeLayout) dialogLayout).findViewById(R.id.expanded_seccion));

        animation = AnimationUtils.loadAnimation(getContext(), R.anim.item_anim_from_right);
        LayoutAnimationController controller = new LayoutAnimationController(animation, 0.1f);
//        layout.setLayoutAnimation(controller);

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Procesando...");

        preguntaActual = null;

        Log.d("secciones", String.valueOf(idSeccion));
        switch (idSeccion) {
            case 202:
                secciones = "202, 203, 204";
//                secciones = "158,160, 159, 161, 162, 164";
                tipo = "PERSONAS";
                break;
            case 201:
                secciones = "201";
//                secciones = "157, 432, 166, 165, 445";
                tipo = "HOGARES";
                break;
            default:
                secciones = String.valueOf(idSeccion);
                tipo = "NORMAL";
                break;
        }
        Log.d("secciones", String.valueOf(secciones));
        gLayoutSecciones = new HashMap<>();

        context = getContext();

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
            iComunicaFragments = (IComunicaFragments2) this.activity;
        }

        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return true;
//        }
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private void evaluador() {
        startThree();
//        new procesador().execute(idEncuesta.id_pregunta);
        new procesa().execute(idEncuesta.id_pregunta, 0);
    }

    public class procesa extends AsyncTask<Integer, String, String> {
        protected PreguntaView pProc = preguntaActual;
        protected String respCondicion = "";
        protected String gPreguntasDibujar = null;
        protected Map<Integer, String> gPreguntasDibujarSeccion = null;
        long tiempoInicio = System.currentTimeMillis();
        protected ArrayList<Integer> nextPreguntas = new ArrayList<>();
        protected ArrayList<Integer> nextSecciones = new ArrayList<>();

        @Override
        protected void onPreExecute() {
//            super.onPreExecute();

            hiloActivo = true;
//            preguntaRespuesta.clear();
            gPreguntasDibujar = null;

            if (!progressDialog.isShowing()) {
                progressDialog.show();
            }
        }

        @Override
        protected String doInBackground(Integer... params) {
            long tiempoInicio = System.currentTimeMillis();

            Log.d("REVISION1", String.valueOf(idEncuesta.id_pregunta));
            String resp = "";
            int id_pregunta = 0;
            boolean bucle = false;
            String accion = "";

            String tCodResp = null;
            String tResp = null;
            String tObs = null;

            Map<String, String> mPregunta;

            boolean nuevo = false;

            if (params[1] == 012) {

                Log.d("REVISION10", String.valueOf(idEncuesta.where()));
                Pregunta entidadPreg = new Pregunta();
//                id_seccion IN (" + secciones + ")
                Log.d("REVISION3", secciones);
                if (entidadPreg.abrir("id_seccion IN (" + secciones + ") AND id_pregunta IN (SELECT id_pregunta FROM enc_encuesta WHERE id_asignacion = " + idEncuesta.id_asignacion + " AND correlativo = " + idEncuesta.correlativo + " AND visible like 't%')", "codigo_pregunta")) {
                    do {
                        gPreguntasDibujar = gPreguntasDibujar == null ? "" + entidadPreg.get_id_pregunta() : gPreguntasDibujar + "," + entidadPreg.get_id_pregunta();
                        idEncuesta.id_pregunta = entidadPreg.get_id_pregunta();
                    } while (entidadPreg.siguiente());
                    entidadPreg.free();
                }
                Log.d("REVISION4", gPreguntasDibujar == null ? "null" : gPreguntasDibujar);

                String revision5 = null;
                Encuesta encuestaDibujar = new Encuesta();
                if (encuestaDibujar.abrir("id_asignacion = " + idEncuesta.id_asignacion + " AND correlativo = " + idEncuesta.correlativo + " AND id_pregunta IN (SELECT id_pregunta FROM enc_pregunta WHERE id_seccion IN (" + secciones + ")) AND visible like 't%'", "")) {
                    do {
                        Map<String, String> dPregunta = new HashMap<>();
                        dPregunta.put("respuesta", encuestaDibujar.get_respuesta());
                        dPregunta.put("codigo_respuesta", encuestaDibujar.get_codigo_respuesta());
                        dPregunta.put("observacion", encuestaDibujar.get_observacion());
                        preguntaRespuesta.put(encuestaDibujar.get_id_encuesta().id_pregunta, dPregunta);
                        revision5 = revision5 == null ? "" + encuestaDibujar.get_id_encuesta().id_pregunta : revision5 + "," + encuestaDibujar.get_id_encuesta().id_pregunta;
                    } while (encuestaDibujar.siguiente());
                }
                encuestaDibujar.free();

                Log.d("REVISION5", revision5 == null ? "null" : revision5);
            }
            do {
//                rmensaje="";
                id_pregunta = idEncuesta.id_pregunta;
//                id_pregunta = id_pregunta==0?params[0]:id_pregunta;
//                id_asignacion = idInformante.id_asignacion;
//                correlativo = idInformante.correlativo;
                Log.d("idAA", String.valueOf(idInformante.id_asignacion));
                Log.d("corrAA", String.valueOf(idInformante.correlativo));
//                IdEncuesta idEncuestaProcesa = new IdEncuesta(id_asignacion, correlativo, id_pregunta);
                IdEncuesta idEncuestaProcesa = idEncuesta;
                Log.d("REVISION6", idEncuestaProcesa.where());

//                id_preguntaS = 0;

                if (pProc == null) {
                    if (encuesta.abrir(idEncuestaProcesa.where(), null)) {
                        Log.d("FILA_NUEVA", "EXISTE");
                        tResp = encuesta.get_respuesta();
                        tCodResp = encuesta.get_codigo_respuesta();
                        tObs = encuesta.get_observacion();
                        nuevo = false;
                        accion = "EVALUAR";
                        Log.d("REVISION001", idEncuestaProcesa.id_pregunta + "-" + tCodResp + "-" + tResp + "-" + tObs);

                    } else {
                        Log.d("FILA_NUEVA", String.valueOf(fila));
                        accion = "NUEVO";
                    }

                    gPreguntasDibujar = gPreguntasDibujar == null ? "" + id_pregunta : gPreguntasDibujar + "," + id_pregunta;
                } else {
                    tCodResp = pProc.getCodResp();
                    tResp = pProc.getResp();
                    tObs = pProc.getObservacion();
                    Log.d("REVISION0001", idEncuestaProcesa.id_pregunta + "-" + tCodResp + "-" + tResp + "-" + tObs);

                    id_pregunta = pProc.getIdEnd();
                    idEncuesta.id_pregunta = pProc.getIdEnd();
                    idEncuestaProcesa = idEncuesta;

                    if (encuesta.abrir(idEncuestaProcesa.where(), null)) {
                        Log.d("REVISION60", idEncuestaProcesa.where());
                        nuevo = false;
                    } else {
                        Log.d("REVISION61", idEncuestaProcesa.where());
                        nuevo = true;
                    }
                    accion = "MODIFICAR";
                }

                switch (accion) {
                    case "NUEVO":
                        preguntaRespuesta.put(id_pregunta, null);
                        bucle = false;
                        titleMsj = "NUEVO";
                        msj = Html.fromHtml("");
                        pProc = null;
                        encuesta.free();
                        break;
                    case "EVALUAR":
                        Log.d("REVISION002", "AQUI");
                        pregunta = new Pregunta();
                        if (pregunta.abrir(id_pregunta)) {
                            if (isValid(pregunta, tCodResp, tResp)) {
                                if (pregunta.get_regla() != null) {
                                    if (isEval(pregunta.get_regla(), String.valueOf(id_pregunta), tCodResp, tObs)) {
//                                            nextPreguntas = Pregunta.getNextPreguntas(pregunta.get_codigo_pregunta(), secciones, idEncuesta.id_asignacion, idEncuesta.correlativo);
//                                            nextPreguntas = Pregunta.getNextPreguntas(preguntaActual.getCod(), secciones);
                                        guardar(nuevo, idEncuestaProcesa, tCodResp, tResp, tObs, null, tipo, secciones, pregunta.get_codigo_pregunta(), fila);
                                        if (isNext(idInformante, id_pregunta, pregunta.get_codigo_pregunta())) {
                                            bucle = true;
                                        } else {
                                            bucle = false;
                                        }
                                    } else {
                                        bucle = false;
                                        pregunta.free();
                                        encuesta.free();
                                        pProc = null;
                                        break;
                                    }
                                } else {
//                                        nextPreguntas = Pregunta.getNextPreguntas(pregunta.get_codigo_pregunta(), secciones, idEncuesta.id_asignacion, idEncuesta.correlativo);
//                                        nextPreguntas = Pregunta.getNextPreguntas(preguntaActual.getCod(), secciones);
                                    guardar(nuevo, idEncuestaProcesa, tCodResp, tResp, tObs, null, tipo, secciones, pregunta.get_codigo_pregunta(), fila);
                                    if (isNext(idInformante, id_pregunta, pregunta.get_codigo_pregunta())) {
                                        bucle = true;
                                    } else {
                                        bucle = false;
                                    }
                                }
                            } else {
                                bucle = false;
                            }
                            mPregunta = new HashMap<>();
                            mPregunta.put("respuesta", tResp);
                            mPregunta.put("codigo_respuesta", tCodResp);
                            mPregunta.put("observacion", tObs);

                            preguntaRespuesta.put(id_pregunta, mPregunta);
                        } else {
                            titleMsj = "STOP";
                            msj = Html.fromHtml("No existe la pregunta.");
                            //caso especial
                            preguntaRespuesta.put(id_pregunta, null);
                            bucle = false;
                            resp = "STOP";
                        }
                        pregunta.free();
                        encuesta.free();
                        pProc = null;
                        break;
                    case "MODIFICAR":
                        if (isNotEmpty(tCodResp, tCodResp)) {
                            pregunta = new Pregunta();
                            if (pregunta.abrir(id_pregunta)) {
                                if (isValid(pregunta, tCodResp, tResp)) {
                                    if (pregunta.get_regla() != null) {
                                        if (isEval(pregunta.get_regla(), String.valueOf(id_pregunta), tCodResp, tObs)) {
//                                                nextPreguntas = Pregunta.getNextPreguntas(pregunta.get_codigo_pregunta(), secciones, idEncuesta.id_asignacion, idEncuesta.correlativo);
                                            nextPreguntas = Pregunta.getNextPreguntas(preguntaActual.getCod(), secciones);
                                            nextSecciones = Pregunta.getNextSecciones(preguntaActual.getIdSeccion(), secciones);
                                            guardar(nuevo, idEncuestaProcesa, tCodResp, tResp, tObs, null, tipo, secciones, pregunta.get_codigo_pregunta(), fila);
                                            if (isNext(idInformante, id_pregunta, pregunta.get_codigo_pregunta())) {
                                                bucle = true;
                                                pProc = null;
                                            } else {
                                                bucle = false;
                                            }
                                        } else {
                                            bucle = false;
                                            encuesta.free();
                                            break;
                                        }
                                    } else {
//                                            nextPreguntas = Pregunta.getNextPreguntas(pregunta.get_codigo_pregunta(), secciones, idEncuesta.id_asignacion, idEncuesta.correlativo);
                                        nextPreguntas = Pregunta.getNextPreguntas(preguntaActual.getCod(), secciones);
                                        nextSecciones = Pregunta.getNextSecciones(preguntaActual.getIdSeccion(), secciones);
                                        guardar(nuevo, idEncuestaProcesa, tCodResp, tResp, tObs, null, tipo, secciones, pregunta.get_codigo_pregunta(), fila);
                                        if (isNext(idInformante, id_pregunta, pregunta.get_codigo_pregunta())) {
                                            bucle = true;
                                            pProc = null;
                                        } else {
                                            bucle = false;
                                        }
                                    }
                                } else {
                                    resp = "ERROR";
//                                    encuesta.free();
                                    bucle = false;
                                    nextPreguntas = Pregunta.getNextPreguntas(preguntaActual.getCod(), secciones);
                                    nextSecciones = Pregunta.getNextSecciones(preguntaActual.getIdSeccion(), secciones);
                                }
                                mPregunta = new HashMap<>();
                                mPregunta.put("codigo_respuesta", tCodResp);
                                mPregunta.put("respuesta", tResp);
                                mPregunta.put("observacion", tObs);

                                preguntaRespuesta.put(id_pregunta, mPregunta);
                            } else {
                                titleMsj = "STOP";
                                msj = Html.fromHtml("No existe la pregunta.");
                                //caso especial
                                preguntaRespuesta.put(id_pregunta, null);
                                bucle = false;
                                resp = "STOP";
                                nextPreguntas = Pregunta.getNextPreguntas(preguntaActual.getCod(), secciones);
                                nextSecciones = Pregunta.getNextSecciones(preguntaActual.getIdSeccion(), secciones);
                            }
                            pregunta.free();
                        } else {
                            bucle = false;
                            Encuesta.actualizaEncuesta(idEncuesta, preguntaActual.getCod(), secciones);
                            nextPreguntas = Pregunta.getNextPreguntas(preguntaActual.getCod(), secciones);
                            nextSecciones = Pregunta.getNextSecciones(preguntaActual.getIdSeccion(), secciones);
                        }
                        encuesta.free();
                        break;
                }
                publishProgress(String.valueOf(id_pregunta));

            } while (bucle);

            long tiempoFin = System.currentTimeMillis();
            long tiempoTranscurrido = tiempoFin - tiempoInicio;
            Log.d("TIEMPO1", "El proceso tardó " + tiempoTranscurrido + " milisegundos.");

            return resp;

        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);

            progressDialog.setMessage(values[0]);
        }

        @Override
        protected void onPostExecute(String mensaje) {
            Log.d("REVISION13", String.valueOf(mensaje));
            Log.d("REVISION14", String.valueOf(titleMsj));
            long tiempoInicio2 = System.currentTimeMillis();
            String condicion = "";

            Log.d("REVISION11", String.valueOf(gLayoutSecciones));
            Log.d("REVISION13", String.valueOf(gPreguntaView));

            if (!nextPreguntas.isEmpty()) {
//                Log.d("REVISION12", String.valueOf(nextPreguntas));
                Log.d("REVISION12", String.valueOf(gPreguntaView));
                for (int p : nextPreguntas) {
                    if (gPreguntaView.containsKey(p)) {
                        int sec = gPreguntaView.get(p).getIdSeccion();
                        LinearLayout secLin = gLayoutSecciones.get(sec);
                        Object obj = secLin;
                        if (obj != null) {
                            secLin.removeView(gPreguntaView.get(p));
//                        secLin.removeView(gButtons.get(p));
                            secLin.removeView(gSepardor.get(p));

//                    layout.removeView(gPreguntaView.get(p));
//                    layout.removeView(gButtons.get(p));
//                    layout.removeView(gSepardor.get(p));

                            gPreguntaView.remove(p);
//                        gButtons.remove(p);
                            gSepardor.remove(p);

                            Log.d("REVISION17", String.valueOf(sec + "-" + secLin.getChildCount()));
                            if (secLin.getChildCount() == 0) {
                                layout.removeView(gButtonSecciones.get(sec));
                                layout.removeView(gLayoutSecciones.get(sec));
                                gButtonSecciones.remove(sec);
                                gLayoutSecciones.remove(sec);
                                seccionButtons.removeView(gToogleSecciones.get(sec));
                                gToogleSecciones.remove(sec);
                                seccionesButtonList.remove(String.valueOf(sec));
                                seccionesList.remove(String.valueOf(sec));
                            }

                        }


                    }
                }
                Log.d("REVISION28", String.valueOf(gButtonSecciones.keySet()));
                if (!gLayoutSecciones.keySet().containsAll(nextSecciones)) {
                    for (int keySec : nextSecciones) {
                        if (!gLayoutSecciones.containsKey(keySec)) {
                            Log.d("REVISION29", String.valueOf(keySec));
                            layout.removeView(gButtonSecciones.get(keySec));
                            gButtonSecciones.remove(keySec);
                            seccionButtons.removeView(gToogleSecciones.get(keySec));
                            gToogleSecciones.remove(keySec);
                            seccionesButtonList.remove(String.valueOf(keySec));
                            seccionesList.remove(String.valueOf(keySec));
                        }
                    }
                }
            }

            if (mensaje.equalsIgnoreCase("STOP")) {
                gPreguntasDibujar = gPreguntasDibujar.replaceAll(",\\d+$", "");
            }

            if (gPreguntasDibujar != null && pProc == null) {
                String[] conver = gPreguntasDibujar.split(",");
                int preg = Integer.parseInt(conver[conver.length - 1]);
                Pregunta pregunta1 = new Pregunta();
                pregunta1.abrir(preg);
                int ultimaSeccion = pregunta1.get_id_seccion();
                pregunta1.free();

                Map<Integer, String> gPreguntaSeccion = Pregunta.getPreguntasSeccion(gPreguntasDibujar);

//            respCondicion = "id_pregunta IN (" + gPreguntasDibujar +" )";

                if (preguntaActual != null) {
                    if (!gPreguntaSeccion.containsKey(preguntaActual.getIdSeccion())) {
                        gButtonSecciones.get(preguntaActual.getIdSeccion()).setBackgroundTintList(ColorStateList.valueOf(getContext().getResources().getColor(R.color.colorPrimary)));
                        gButtonSecciones.get(preguntaActual.getIdSeccion()).setTextColor(getContext().getResources().getColor(R.color.color_list));
                        gLayoutSecciones.get(preguntaActual.getIdSeccion()).setVisibility(View.GONE);
                    }
                }

                for (int key : gPreguntaSeccion.keySet()) {
                    respCondicion = "id_pregunta IN (" + gPreguntaSeccion.get(key) + " )";
                    Log.d("REVISION15", String.valueOf(respCondicion));
                    Log.d("REVISION19", String.valueOf(preguntaActual != null ? preguntaActual.getIdSeccion() : "null"));
                    Log.d("REVISION19", String.valueOf(preguntaActual != null ? preguntaActual.getCod() : "null"));
                    Log.d("REVISION20", String.valueOf(key));
                    if (key != ultimaSeccion) {
                        if (preguntaActual != null) {
                            if (key == preguntaActual.getIdSeccion()) {
                                graficaButtonSeccion(true, false, key, respCondicion);
                            } else {
                                graficaButtonSeccion(false, false, key, respCondicion);
                            }
                        } else {
                            graficaButtonSeccion(false, false, key, respCondicion);
                        }
                    } else {
                        graficaButtonSeccion(false, true, key, respCondicion);
                    }
                }

            }
//            preguntaActual.setFocus();
            Log.d("TITLEMSJ", titleMsj);
            switch (titleMsj) {
                case "STOP":
                    iComunicaFragments.mensaje(3, getContext(), "volverInicio", null, "ERROR", msj, new IdEncuesta(idInformante.id_asignacion, idInformante.correlativo, 0, fila));
                    progressDialog.dismiss();
                    break;
                case "FIN_BUCLE":
                    preguntaActual.setFocus();
//                    iComunicaFragments.mensaje(3, activity, "finBucle", null, titleMsj, Html.fromHtml("Terminaste los datos de la persona"), new IdEncuesta(idInformante.id_asignacion, idInformante.correlativo, Parametros.ID_PREG_MORTALIDAD, fila));
                    iComunicaFragments.enviarDatos(new IdEncuesta(idInformante.id_asignacion, idInformante.correlativo,Parametros.ID_PREG_MORTALIDAD, fila), 3, false);
                    progressDialog.dismiss();
                    break;
                case "FIN_BOLETA":
                    preguntaActual.setFocus();
//                    scrollView.smoothScrollTo(0, preguntaActual.getTop());
                    iComunicaFragments.mensaje(3, activity, "terminar", null, titleMsj, Html.fromHtml("Terminaste la vivienda"), new IdEncuesta(idInformante.id_asignacion, idInformante.correlativo, preguntaActual.getId(), fila));
                    progressDialog.dismiss();
                    break;
                case "FIN_HOGAR":
                    preguntaActual.setFocus();
//                    scrollView.smoothScrollTo(0, preguntaActual.getTop());
                    iComunicaFragments.mensaje(3, activity, "volverInicio", null, titleMsj, Html.fromHtml("Terminaste los datos del hogar"), new IdEncuesta(idInformante.id_asignacion, idInformante.correlativo, preguntaActual.getId(), fila));
                    progressDialog.dismiss();
                    break;
                case "FIN_PERSONA":
                case "FIN_CUESTIONARIO":
                    preguntaActual.setFocus();
//                    scrollView.smoothScrollTo(0, preguntaActual.getTop());
                    iComunicaFragments.mensaje(3, activity, "volverInicio", null, titleMsj, Html.fromHtml("Terminaste los datos de la persona"), new IdEncuesta(idInformante.id_asignacion, idInformante.correlativo, preguntaActual.getId(), fila));
                    progressDialog.dismiss();
                    break;

                case "OBS":
                case "JUSTIFIQUE":
                    preguntaActual.setFocus();
//                    scrollView.smoothScrollTo(0, preguntaActual.getTop());
                    observationMessageEnc(activity, context, "pasarObservado", titleMsj, msj, preguntaActual.getObservacion(), preguntaActual.getId());
                    progressDialog.dismiss();
                    break;
                case "ERROR":
                case "ERROR_VACIO":
                case "ERROR_MAX_MIN":
                case "ERROR_CONSISTENCIA":
                case "ERROR_JSON":
                case "ERROR_FLUJO":
                    preguntaActual.setFocus();
//                    scrollView.smoothScrollTo(0, preguntaActual.getTop());
                    iComunicaFragments.mensaje(2, getContext(), null, null, titleMsj, msj, new IdEncuesta(idInformante.id_asignacion, idInformante.correlativo, preguntaActual.getId(), fila));
                    progressDialog.dismiss();
                    break;
                case "NUEVO":
                    preguntaActual.setFocus();
//                    scrollView.smoothScrollTo(0, preguntaActual.getTop());
                    progressDialog.dismiss();
                    break;
                default:
                    progressDialog.dismiss();
                    break;
            }


            hiloActivo = false;

            long tiempoFin2 = System.currentTimeMillis();
            long tiempoTranscurrido2 = tiempoFin2 - tiempoInicio2;
            Log.d("TIEMPO2", "El proceso tardó " + tiempoTranscurrido2 + " milisegundos.");
            long tiempoFin = System.currentTimeMillis();
            long tiempoTranscurrido = tiempoFin - tiempoInicio;
            Log.d("TIEMPO0", "El proceso tardó " + tiempoTranscurrido + " milisegundos.");
        }
    }

    public boolean isNotEmpty(String tCodResp, String tCodPreg) {
        boolean resp = true;
        if (tCodResp.equals("-1") || tCodResp.equals("-1.0")) {
            titleMsj = "ERROR_VACIO";
            msj = Html.fromHtml("Debe introducir/seleccionar un valor en : " + tCodPreg);
            resp = false;
        }
        return resp;
    }

    public boolean isValid(Pregunta pregunta, String tCodResp, String tResp) {
        boolean resp = true;
        List<String> omision = Pregunta.getOmisionId2(pregunta.get_omision());
        if (!omision.contains(tCodResp)) {
            switch (pregunta.get_id_tipo_pregunta()) {
                case Numero:
                case Decimal:
                case MesAnio:
                    if ((Float.parseFloat(tCodResp) < pregunta.get_minimo() && pregunta.get_minimo() > 0) || (Float.parseFloat(tCodResp) > pregunta.get_maximo() && pregunta.get_maximo() > 0)) {
                        titleMsj = "ERROR_MAX_MIN";
                        msj = Html.fromHtml(String.format("El valor de " + pregunta.get_codigo_pregunta() + " debe estar entre %d y %d", pregunta.get_minimo(), pregunta.get_maximo()));
                        resp = false;
                    }
                    break;
                case Abierta:
                case Memoria:
                case Autocompletar:
                case Multiple:
                    if (tResp.length() < pregunta.get_minimo() && pregunta.get_minimo() > 0 || (tResp.length() > pregunta.get_maximo() && pregunta.get_maximo() > 0)) {
                        titleMsj = "ERROR_MAX_MIN";
                        msj = Html.fromHtml(String.format("La longitud de " + pregunta.get_codigo_pregunta() + " debe estar entre %d y %d", pregunta.get_minimo(), pregunta.get_maximo()) + " (Valor actual: " + tResp.length() + ")");
                        resp = false;
                    }
                    break;
                default:
                    break;
            }
        }
        return resp;
    }

    public boolean isEval(String json, String id_pregunta, String tCodResp, String tObs) {
        boolean resp = false;
        boolean stop = false;
        try {
            List<Regla> reglas = Regla.fromJson(json);
            int i = 0;
            for (Regla regla : reglas) {
                if (evaluar(regla.getRegla(), id_pregunta, tCodResp)) {
                    switch (regla.getCodigo()) {
                        case "1":
                            titleMsj = "ERROR_CONSISTENCIA";
                            msj = Html.fromHtml(regla.getMensaje());
                            stop = true;
                            break;

                        case "2":
                            if (tObs != null && tObs.length() > 3) {
                                resp = true;
                            } else {
                                titleMsj = "JUSTIFIQUE";
                                msj = Html.fromHtml(regla.getMensaje());
                                stop = true;
                            }
                            break;
                    }
                    if (stop) {
                        resp = false;
                        break;
                    }
                } else {
                    i++;
                    if (i == reglas.size()) {
                        resp = true;
                    }
                }
            }
        } catch (JSONException e) {
//            e.printStackTrace();
            titleMsj = "ERROR_JSON";
            msj = Html.fromHtml(String.valueOf(e));
            resp = false;
        }

        return resp;
    }

    public void guardar(boolean nuevo, IdEncuesta idEncuesta, String tCodResp, String tResp, String tObs, Estado estado, String tipo, String secciones, String codPreg, int fila) {
        Log.d("REVISION50", nuevo + "-" + idEncuesta.id_asignacion + "-" + idEncuesta.correlativo + "-" + idEncuesta.id_pregunta + "-" + tCodResp + "-" + tResp + "-" + tObs + "-" + estado);
        Log.d("REVISION50", Usuario.getLogin());

        if (nuevo) {
            encuesta.nuevo();
            encuesta.set_id_encuesta(idEncuesta);
            encuesta.set_feccre(System.currentTimeMillis() / 1000);
            encuesta.set_usucre(Usuario.getLogin());
        } else {

            encuesta.editar();
            encuesta.set_usumod(Usuario.getLogin());
            encuesta.set_fecmod(System.currentTimeMillis() / 1000);
        }
        encuesta.set_respuesta(tResp);
        encuesta.set_codigo_respuesta(tCodResp);
        encuesta.set_observacion(tObs);
        if (estado != null) {
            encuesta.set_estado(estado);
        }
        encuesta.set_latitud(Movil.getGPS().split(";")[0].toString());
        encuesta.set_longitud(Movil.getGPS().split(";")[1].toString());
        encuesta.set_visible("t");

        encuesta.set_fila(fila);

        encuesta.guardar();

//        encuesta.free();
    }

    public boolean isNext(IdInformante idInformante, int idPregunta, String tCodPreg) {
        boolean resp = false;

        Flujo flujo = new Flujo();
        Siguiente idSiguiente = flujo.siguiente(idInformante, idPregunta);
        Log.d("siguiente", String.valueOf(idSiguiente.idSiguiente));

        if (idSiguiente.idSiguiente > 0) {
            Pregunta pregunta = new Pregunta();
            if (pregunta.abrir(idSiguiente.idSiguiente)) {

                idEncuesta.id_pregunta = idSiguiente.idSiguiente;

                resp = true;
            } else {
                titleMsj = "ERROR_FLUJO";
                msj = Html.fromHtml("No se encontró la pregunta.");

                resp = false;
            }
        } else {
            if (idSiguiente.idSiguiente == -2) {
                if (Integer.valueOf(idEncuesta.id_pregunta) == 99999) {
                    String message = informante.concluir(tCodPreg);
                    titleMsj = "FIN_BOLETA";
                    msj = Html.fromHtml("");
                    resp = false;
                }
            } else if(idSiguiente.idSiguiente == -10){
                titleMsj = "FIN_BUCLE";
                msj = Html.fromHtml("");

                resp = false;
            } else {
                if (idNivel == 3) {
                    String message = informante.concluir(tCodPreg);
                    Log.d("message", "message: " + message);
                    Log.d("pregs", tCodPreg);

                    titleMsj = "FIN_HOGAR";
                    msj = Html.fromHtml("");

                    resp = false;
                } else {

                    String message;

                    message = informante.concluirPersona(tCodPreg);
                    titleMsj = "FIN_PERSONA";
                    Log.d("message", "message: " + message);

                    msj = Html.fromHtml("");
                    resp = false;
                }
            }
        }
        return resp;
    }

    public void graficaButtonSeccion(boolean seccionActual, boolean ultimaSeccion, final int seccionId, final String condicion) {
        Seccion seccion = new Seccion();

        Log.d("REVISION20", seccionActual + " Y " + ultimaSeccion);
        if (seccion.abrir(seccionId)) {
            String seccionCod = seccion.get_codigo();
            if (!seccionesButtonList.contains(String.valueOf(seccionId))) {
                MaterialButton seccionButton = new MaterialButton(new ContextThemeWrapper(getContext(), R.style.Widget_MaterialComponents_Button_OutlinedButton));
                seccionButton.setText(Html.fromHtml("<b>" + seccionCod + "<br>" + seccion.get_seccion() + "</b>"));
                seccionButton.setTextColor(getContext().getResources().getColor(R.color.color_list));
                seccionButton.setId(seccionId);
                seccionButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        LinearLayout subLayout = (LinearLayout) view.getTag();
                        if (subLayout.getVisibility() == View.GONE) {
                            seccionButton.setBackgroundTintList(ColorStateList.valueOf(getContext().getResources().getColor(R.color.color_list)));
                            seccionButton.setTextColor(getContext().getResources().getColor(R.color.colorPrimary));
                            subLayout.setVisibility(View.VISIBLE);
                            Log.d("REVISION16", String.valueOf(condicion));
                            if (!gLayoutSecciones.containsKey(seccionId)) {
                                dibujaPregunta(subLayout, condicion);
                                gLayoutSecciones.put(seccionId, subLayout);
                            }
                        } else {
                            seccionButton.setBackgroundTintList(ColorStateList.valueOf(getContext().getResources().getColor(R.color.colorPrimary)));
                            seccionButton.setTextColor(getContext().getResources().getColor(R.color.color_list));
                            subLayout.setVisibility(View.GONE);
                        }
                    }
                });
                seccionButton.setBackgroundTintList(ColorStateList.valueOf(getContext().getResources().getColor(R.color.colorPrimary)));

                LinearLayout subLayout = new LinearLayout(getContext());
                subLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                subLayout.setOrientation(LinearLayout.VERTICAL);
                seccionButton.setTag(subLayout);
                subLayout.setVisibility(View.GONE);

                layout.addView(seccionButton);
                layout.addView(subLayout);

                if (ultimaSeccion) {
                    dibujaPregunta(subLayout, condicion);
                    gLayoutSecciones.put(seccionId, subLayout);
                    subLayout.setVisibility(View.VISIBLE);
                    seccionButton.setBackgroundTintList(ColorStateList.valueOf(getContext().getResources().getColor(R.color.color_list)));
                    seccionButton.setTextColor(getContext().getResources().getColor(R.color.colorPrimary));
                }

                graficaSeccion(idInformante, seccionId);

                seccionesButtonList.add(String.valueOf(seccionId));
                gButtonSecciones.put(seccionId, seccionButton);
            } else {
                if (ultimaSeccion || seccionActual) {
                    LinearLayout subLayoutSelect = gLayoutSecciones.get(seccionId);
                    dibujaPregunta(subLayoutSelect, condicion);
                    gLayoutSecciones.put(seccionId, subLayoutSelect);
                    gButtonSecciones.get(seccionId).setBackgroundTintList(ColorStateList.valueOf(getContext().getResources().getColor(R.color.color_list)));
                    gButtonSecciones.get(seccionId).setTextColor(getContext().getResources().getColor(R.color.colorPrimary));
                }
                if (seccionActual) {
                    gButtonSecciones.get(seccionId).setBackgroundTintList(ColorStateList.valueOf(getContext().getResources().getColor(R.color.colorPrimary)));
                    gButtonSecciones.get(seccionId).setTextColor(getContext().getResources().getColor(R.color.color_list));
                    gLayoutSecciones.get(seccionId).setVisibility(View.GONE);
                }

            }
        }
    }

    public void graficaSeccion(IdInformante idInformante, final int seccionId) {
        Seccion seccion = new Seccion();

        if (seccion.abrir(seccionId)) {
            String seccionCod = seccion.get_codigo();
//            MaterialButton seccionButton = new MaterialButton(getContext());
            //BOTONESBOTONES
            Log.d("REVISION99", seccionesList + "-" + seccionCod);
            if (!seccionesList.contains(String.valueOf(seccionId))) {

                LinearLayout.LayoutParams layoutParamsButtonS = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                MaterialButton seccionButton = new MaterialButton(new ContextThemeWrapper(getContext(), R.style.Widget_MaterialComponents_Button_OutlinedButton));
                seccionButton.setText(seccionCod);
                seccionButton.setTextColor(getContext().getResources().getColor(R.color.colorPrimary));
                seccionButton.setId(seccionId);
                seccionButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {

                            MaterialButton materialButtonFocus = gButtonSecciones.get(seccionId);
//                            materialButtonFocus.setFocusableInTouchMode(true);
                            materialButtonFocus.setFocusable(true);
                            materialButtonFocus.requestFocus();
                            scrollView.smoothScrollTo(0, materialButtonFocus.getTop());
//                            Log.d("ClickSeccion",String.valueOf(seccionId)+" "+Pregunta.getFirstIdPreguntaSeccion(seccionId,idInformante));
//                            PreguntaView preguntaFocus = gPreguntaView.get(Pregunta.getFirstIdPreguntaSeccion(seccionId,idInformante));
//                            preguntaFocus.setFocusableInTouchMode(true);
//                            preguntaFocus.setFocusable(true);
//                            preguntaFocus.requestFocus();

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                });
                seccionButton.setLayoutParams(layoutParamsButtonS);
                seccionButton.setBackgroundTintList(ColorStateList.valueOf(getContext().getResources().getColor(R.color.color_list)));
                seccionButtons.addView(seccionButton);
                gToogleSecciones.put(seccionId, seccionButton);
                seccionesList.add(String.valueOf(seccionId));
            } else {
                if (seccionButtons != null) {
                    seccionButtons.clearChecked();
                }
            }

        }
    }

    public void dibujaPregunta(LinearLayout layout, String condicion) {
        ArrayList<Integer> noDibujar = new ArrayList<>();
        //PARA BUTTON
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.RIGHT;

        //PARA SEPARADOR
        ViewGroup.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.height = 1;

        final Pregunta pregunta = new Pregunta();

        if (pregunta.abrir(condicion, "codigo_pregunta")) {

            do {
                if (!noDibujar.contains(Integer.valueOf(pregunta.get_id_pregunta()))) {
                    int i = 0;
                    String p = pregunta.procesaEnunciado(idInformante, pregunta.get_pregunta());

//                    String obs = Informante.getObservaciones(idInformante, pregunta.get_id_pregunta());
                    String obs = "";

                    if (seccion_ant != pregunta.get_id_seccion()) {
                        mostrarSeccion = true;
                    } else {
                        mostrarSeccion = false;
                    }

                    Map<Integer, String> omision;
                    String preOmision = pregunta.get_omision();
                    if (preOmision != null && !preOmision.equals("null") && !preOmision.equals("")) {
                        omision = Pregunta.getOmision_convert(pregunta.get_omision());
                    } else {
                        omision = null;
                    }

                    switch (pregunta.get_id_tipo_pregunta()) {
                        /***/
                        case Abierta: {
                            pregunti = new Abierta(getContext(), i, pregunta.get_id_pregunta(), pregunta.get_id_seccion(), pregunta.get_codigo_pregunta(), p, pregunta.get_longitud(), omision, pregunta.get_ayuda(), true, true, mostrarSeccion, obs);
                            break;
                        }
                        /***/
                        case Incidencia:
                        case Cerrada: {
                            List<Integer> nombres = new ArrayList<>(Arrays.asList(14708));
                            if (!pregunta.get_codigo_especifique().equals("0")) {
//                            if (pregunta.get_id_pregunta() == 14708) {
                                noDibujar.clear();
                                Map<Integer, Map<String, String>> preguntasMatriz = Pregunta.getPreguntaMatriz(pregunta.get_codigo_especifique());
                                noDibujar.addAll(preguntasMatriz.keySet());
//                                    Map<Integer, Map<String, String>> preguntasMatriz = Pregunta.getPreguntaMatriz(pregunta.get_id_pregunta());
//                                Map<Integer, String> respuestas = Pregunta.getRespuestas(pregunta.get_respuesta());
                                p = p.contains(":") ? p.split(":")[0] + ":" : p;
                                pregunti = new CerradaMatriz(getContext(), pregunta.get_id_pregunta(), pregunta.get_id_seccion(), pregunta.get_codigo_pregunta(), p, null, pregunta.get_codigo_especifique(), pregunta.get_longitud(), 4, pregunta.get_ayuda(), mostrarSeccion, obs, preguntasMatriz, idInformante, secciones, idPadre, tipo);
                            } else {
                                ArrayList<Integer> filtro = null;
                                Map<Integer, String> respuestas;
                                if (pregunta.get_codigo_pregunta().equals("ZA10_01")) {
                                    //devolver dinámico para cada uno
                                    respuestas = Informante.getSelectCarnet(idInformante);
                                } else {
                                    respuestas = Pregunta.getRespuestas(pregunta.get_respuesta());
                                }
//                                            Map<Integer, String> respuestas = Pregunta.getRespuestas(pregunta.get_respuesta());
                                pregunti = new Cerrada(getContext(), i, pregunta.get_id_pregunta(), pregunta.get_id_seccion(), pregunta.get_codigo_pregunta(), p, respuestas, pregunta.get_codigo_especifique(), omision, filtro, pregunta.get_ayuda(), true, mostrarSeccion, obs);
                            }
                            break;
                        }
                        case CerradaMatriz: {
                            Map<Integer, Map<String, String>> preguntasMatriz = Pregunta.getPreguntaMatriz(pregunta.get_codigo_especifique());
                            Map<Integer, String> respuestas = Pregunta.getRespuestas(pregunta.get_respuesta());
                            pregunti = new CerradaMatriz(getContext(), pregunta.get_id_pregunta(), pregunta.get_id_seccion(), pregunta.get_codigo_pregunta(), p, null, pregunta.get_codigo_especifique(), pregunta.get_longitud(), 4, pregunta.get_ayuda(), mostrarSeccion, obs, preguntasMatriz, idInformante, secciones, idPadre, tipo);
                            break;
                        }
                        case CerradaOmitida: {
                            Map<Integer, String> respuestas = Pregunta.getRespuestas(pregunta.get_respuesta());
                            pregunti = new CerradaOmitida(getContext(), pregunta.get_id_pregunta(), pregunta.get_id_seccion(), pregunta.get_codigo_pregunta(), p, respuestas, pregunta.get_codigo_especifique(), pregunta.get_codigo_especial(), pregunta.get_ayuda(), Informante.getUpm(idInformante), mostrarSeccion, obs);
                            break;
                        }
                        /***/
                        case UsoVivienda:
                        case NumeroVivienda:
                        case Numero: {
                            pregunti = new Numero(getContext(), i, pregunta.get_id_pregunta(), pregunta.get_id_seccion(), pregunta.get_codigo_pregunta(), p, pregunta.get_longitud(), omision, pregunta.get_ayuda(), true, mostrarSeccion, obs);
                            break;
                        }
                        /***/
                        case Decimal: {
                            pregunti = new Decimal(getContext(), i, pregunta.get_id_pregunta(), pregunta.get_id_seccion(), pregunta.get_codigo_pregunta(), p, pregunta.get_longitud(), omision, pregunta.get_ayuda(), true, mostrarSeccion, obs);
                            break;
                        }
                        case Multiple: {
                            Map<Integer, String> respuestas = Pregunta.getRespuestas(pregunta.get_respuesta());
                            pregunti = new Multiple(getContext(), pregunta.get_id_pregunta(), pregunta.get_id_seccion(), pregunta.get_codigo_pregunta(), p, respuestas, 4, pregunta.get_ayuda(), mostrarSeccion, obs);
                            break;
                        }
                        /***/
                        case Fecha: {
                            pregunti = new Fecha(getContext(), i, pregunta.get_id_pregunta(), pregunta.get_id_seccion(), pregunta.get_codigo_pregunta(), p, omision, pregunta.get_ayuda(), true, mostrarSeccion, obs);
                            break;
                        }
                        case MesAnio: {
                            pregunti = new MesAnio(getContext(), pregunta.get_id_pregunta(), pregunta.get_id_seccion(), pregunta.get_codigo_pregunta(), p, 4, pregunta.get_ayuda(), mostrarSeccion, obs);
                            break;
                        }
                        case HoraMinuto: {
                            pregunti = new HoraMinuto(getContext(), i, pregunta.get_id_pregunta(), pregunta.get_id_seccion(), pregunta.get_codigo_pregunta(), p, 4, pregunta.get_ayuda(), true, mostrarSeccion, obs);
                            break;
                        }
                        /***/
                        case Gps: {
                            //preg = new GpsOldd(this, pregunta.get_id_pregunta(), pregunta.get_id_seccion(), pregunta.get_codigo_pregunta(), p, pregunta.get_ayuda());
                            pregunti = new Gps(getContext(), i, pregunta.get_id_pregunta(), pregunta.get_id_seccion(), pregunta.get_codigo_pregunta(), p, pregunta.get_ayuda(), true, mostrarSeccion, obs);
                            break;
                        }
                        case Formula: {
//                            Float val = 0f;//Encuesta.evaluar(pregunta.get_rpn_formula(), idEncuesta.getIdInformante(), idEncuesta.fila, null, null, null);
                            Float val = 0f;
                            try {
                                String value = Pregunta.procesaEnunciadoFormula(idInformante, pregunta.get_respuesta());
                                val = Float.parseFloat(value);

                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            pregunti = new Formula(getContext(), pregunta.get_id_pregunta(), pregunta.get_id_seccion(), pregunta.get_codigo_pregunta(), p, val, pregunta.get_ayuda(), "0", mostrarSeccion, obs);
                            break;
                        }
                        case Fotografia: {
                            pregunti = new Foto(getActivity(), pregunta.get_id_pregunta(), pregunta.get_id_seccion(), pregunta.get_codigo_pregunta(), p, informante.get_id_informante(), pregunta.get_id_pregunta(), pregunta.get_ayuda(), mostrarSeccion, obs);
                            break;
                        }
                        case CerradaInformante: {
                            Map<Long, String> respuestas;
                            if (informante.get_id_nivel() == 2) {
                                respuestas = Informante.getRespuestas(informante.get_id_informante_padre());
                            } else {
                                respuestas = Informante.getRespuestas(informante.get_id_informante());
                            }
                            pregunti = new CerradaInformante(getContext(), pregunta.get_id_pregunta(), pregunta.get_id_seccion(), pregunta.get_codigo_pregunta(), p, respuestas, pregunta.get_codigo_especifique(), 4, pregunta.get_ayuda(), mostrarSeccion, obs);
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
                            pregunti = new CerradaInformante(getContext(), pregunta.get_id_pregunta(), pregunta.get_id_seccion(), pregunta.get_codigo_pregunta(), p, respuestas, pregunta.get_codigo_especifique(), 4, pregunta.get_ayuda(), mostrarSeccion, obs);
                            break;
                        }
                        case Cantidad: {
                            pregs[i] = new Cantidad(getContext(), pregunta.get_id_pregunta(), pregunta.get_id_seccion(), pregunta.get_codigo_pregunta(), p, 4, pregunta.get_ayuda(), mostrarSeccion, obs);
                            break;
                        }
//                    case CerradaBucle: {
//                        Map<Long, String> respuestas = Encuesta.getRespuestas(informante.get_id_informante(), pregunta.get_variable_bucle(), pregunta.get_rpn_formula());
//                        pregs[i] = new CerradaBucle(getContext(), pregunta.get_id_pregunta(), pregunta.get_id_seccion(), pregunta.get_codigo_pregunta(), p, respuestas, pregunta.get_codigo_especifique(), pregunta.get_codigo_especial(), pregunta.get_ayuda());
//                        break;
//                    }
                        /***/
                        case Autocompletar: {
                            pregunti = new Autocompletar(getContext(), i, pregunta.get_id_pregunta(), pregunta.get_id_seccion(), pregunta.get_codigo_pregunta(), p, pregunta.get_longitud(), pregunta.get_catalogo(), omision, pregunta.get_ayuda(), true, true, mostrarSeccion, obs);
                            break;
                        }
                        /***/
                        case ValorTipo: {
                            Map<Integer, String> respuestas = Pregunta.getRespuestas(pregunta.get_respuesta());
                            pregunti = new ValorTipo(getContext(), i, pregunta.get_id_pregunta(), pregunta.get_id_seccion(), pregunta.get_codigo_pregunta(), p, respuestas, pregunta.get_longitud(), 4, pregunta.get_ayuda(), true, mostrarSeccion, obs);
                            break;
                        }
                        case ValorTipo2: {
                            Map<Integer, String> respuestas = Pregunta.getRespuestas(pregunta.get_respuesta());
                            pregunti = new ValorTipo2(getContext(), pregunta.get_id_pregunta(), pregunta.get_id_seccion(), pregunta.get_codigo_pregunta(), p, respuestas, pregunta.get_longitud(), 4, pregunta.get_ayuda(), mostrarSeccion, obs);
                            break;
                        }
                        /***/
                        case ValorTipoGastos: {
                            Map<Integer, String> respuestas = Pregunta.getRespuestas(pregunta.get_respuesta());
//                            List<String> respuestas = Pregunta.getRespuestasList(pregunta.get_respuesta());
                            pregunti = new ValorTipoGastos(getContext(), i, pregunta.get_id_pregunta(), pregunta.get_id_seccion(), pregunta.get_codigo_pregunta(), p, respuestas, pregunta.get_longitud(), 4, pregunta.get_ayuda(), pregunta.get_codigo_especifique(), true, mostrarSeccion, obs);
                            break;
                        }
                        case ValorTipoMatriz: {
                            Map<Integer, String> respuestas = Pregunta.getRespuestas(pregunta.get_respuesta());
                            pregunti = new ValorTipoMatriz(getContext(), pregunta.get_id_pregunta(), pregunta.get_id_seccion(), pregunta.get_codigo_pregunta(), p, respuestas, pregunta.get_longitud(), 4, pregunta.get_ayuda(), pregunta.get_codigo_especifique(), mostrarSeccion, obs);
                            break;
                        }
                        /***/
                        case SeleccionKish: {
                            Map<Integer, String> respuestas;
                            int penultimo = informante.folio(idInformante);
                            if (pregunta.get_codigo_pregunta().toString().equals("s09_1800")) {
                                respuestas = Informante.getSelectKisha(idInformante);
                            } else {
                                respuestas = Informante.getSelectKisha2(idInformante);
                            }
                            pregunti = new SeleccionKish(getContext(), pregunta.get_id_pregunta(), pregunta.get_id_seccion(), pregunta.get_codigo_pregunta(), p, "prueba", respuestas, penultimo, pregunta.get_ayuda(), mostrarSeccion, obs);
                            break;
                        }
                        /***/
                        case Prioridad: {
                            Map<Integer, String> respuestas;
                            if (pregunta.get_catalogo().equals("--")) {
                                respuestas = Pregunta.getRespuestas(pregunta.get_respuesta());
                            } else {
                                respuestas = null;
                            }
                            pregunti = new Prioridad(getContext(), i, pregunta.get_id_pregunta(), pregunta.get_id_seccion(), pregunta.get_codigo_pregunta(), p, respuestas, pregunta.get_catalogo(), pregunta.get_codigo_especifique(), pregunta.get_minimo(), pregunta.get_longitud(), 4, pregunta.get_ayuda(), true, mostrarSeccion, obs);
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
                            pregunti = new Calendario(getContext(), pregunta.get_id_pregunta(), pregunta.get_id_seccion(), pregunta.get_codigo_pregunta(), pregunta.get_pregunta(), values, fechaContacto, pregunta.get_ayuda(), mostrarSeccion, obs);
                            break;
                        }
                        /***/
                        case Memoria: {
                            String[] values = Encuesta.getRespuesta(Informante.getUpm(idInformante), pregunta.get_codigo_pregunta());
                            pregunti = new Memoria(getContext(), i, pregunta.get_id_pregunta(), pregunta.get_id_seccion(), pregunta.get_codigo_pregunta(), p, pregunta.get_longitud(), omision, values, pregunta.get_ayuda(), true, mostrarSeccion, obs);
                            break;
                        }
                        /***/
                        case Upm: {
                            Map<Integer, String> respuestas = UpmHijo.getRespuestasUPM(Informante.getUpm(idInformante));
                            pregunti = new Cerrada(getContext(), i, pregunta.get_id_pregunta(), pregunta.get_id_seccion(), pregunta.get_codigo_pregunta(), p, respuestas, pregunta.get_codigo_especifique(), omision, null, pregunta.get_ayuda(), true, mostrarSeccion, obs);
                            break;
                        }
                        case UpmHijo: {
                            Map<Integer, String> respuestas = UpmHijo.getRespuestasManzano(Informante.getUpm(idInformante));
                            pregunti = new Cerrada(getContext(), i, pregunta.get_id_pregunta(), pregunta.get_id_seccion(), pregunta.get_codigo_pregunta(), p, respuestas, pregunta.get_codigo_especifique(), omision, null, pregunta.get_ayuda(), true, mostrarSeccion, obs);
                            break;
                        }
                        /***/
                        case Consulta: {
                            Map<Integer, String> respuestas = EncLog.getConsulta(idInformante, idPadre, pregunta.get_respuesta());
                            Log.d("REVISION90", String.valueOf(respuestas));
                            pregunti = new Cerrada(getContext(), i, pregunta.get_id_pregunta(), pregunta.get_id_seccion(), pregunta.get_codigo_pregunta(), p, respuestas, pregunta.get_codigo_especifique(), omision, null, pregunta.get_ayuda(), true, mostrarSeccion, obs);
                            break;
                        }
//                    case CerradaConsulta: {
//                        Map<Integer, String> respuestasConsulta = Pregunta.getRespuestasQuery(informante.get_id_informante().id_asignacion, informante.get_id_informante().correlativo, informante.get_id_informante_padre().id_asignacion, informante.get_id_informante_padre().correlativo, informante.get_id_upm(), informante.get_descripcion(), informante.get_codigo(), pregunta.get_variable(), idEncuesta.fila);
//                        Map<Integer, String> respuestas = Pregunta.getRespuestas(pregunta.get_respuesta());
//                        preg = new CerradaConsulta(getContext(), pregunta.get_id_pregunta(), pregunta.get_id_seccion(), pregunta.get_codigo_pregunta(), p, respuestas, respuestasConsulta, pregunta.get_codigo_especial(), null, pregunta.get_ayuda());
//                        break;
//                    }
                        /***/
                        case Tabla: {
                            pregunti = new Tabla(getActivity(), getContext(), idInformante, idPadre, i, pregunta.get_id_pregunta(), pregunta.get_id_seccion(), pregunta.get_codigo_pregunta(), p, pregunta.get_longitud(), omision, pregunta.get_ayuda(), true, true, mostrarSeccion, obs);
                            break;
                        }
                        case TablaMatriz: {
                            pregunti = new TablaMatriz(getContext(), i, pregunta.get_id_pregunta(), pregunta.get_id_seccion(), pregunta.get_codigo_pregunta(), p, pregunta.get_longitud(), omision, pregunta.get_ayuda(), true, true, mostrarSeccion, obs, idInformante.id_asignacion, idInformante.correlativo);
                            break;
                        }
                    }

                    Log.d("getCodPreg", pregunta.get_codigo_pregunta());
                    Map<String, String> thisRespuesta = preguntaRespuesta.get(pregunta.get_id_pregunta());
                    if (thisRespuesta != null) {
                        Log.d("getCodResp1", thisRespuesta.get("codigo_respuesta"));
                        Log.d("getResp1", thisRespuesta.get("respuesta"));
                        try {
//                        switch (pregunta.get_id_tipo_pregunta()){
//                            case CerradaMatriz:
                            if (!pregunta.get_codigo_especifique().equals("0")) {
                                Map<Integer, String> respuestaMatriz = Encuesta.getEncuestaMatriz(idInformante, pregunta.get_codigo_especifique());
                                pregunti.setCodResp(respuestaMatriz);
                                pregunti.setObservacion(thisRespuesta.get("observacion"));
                                pregunti.setEstado(Estado.ELABORADO);
                            } else {

//                                break;
//                            default:
                                pregunti.setCodResp(thisRespuesta.get("codigo_respuesta"));
                                pregunti.setResp(thisRespuesta.get("respuesta"));
                                Log.d("incidencia marca", thisRespuesta.get("observacion"));
                                pregunti.setObservacion(thisRespuesta.get("observacion"));
                                pregunti.setEstado(Estado.ELABORADO);
//                                break;
//                        }
                            }

                            int idNivelPregunta = Informante.obtenerNivelPregunta(pregunta.get_id_pregunta());
                            boolean esObservada = false;

                            esObservada = Informante.esPreguntaObservada(idInformante, pregunta.get_id_pregunta(), idNivelPregunta);

                            if (esObservada) {
                                pregunti.setColorObservacion(false);
                                ImageButton buttonObs = new ImageButton(getContext());
                                buttonObs.setImageDrawable(getResources().getDrawable(R.drawable.ic_notifications));
                                buttonObs.setBackground(null);
                                buttonObs.setTag(pregunti.getId());
                                String vObs = "";
                                try {
                                    //vObs =pregunti.getObservacion();
                                    //if (vObs.equals("") ){
                                    vObs = Informante.getObservacionesHijo(idInformante, pregunta.get_id_pregunta());
                                    //}
                                } catch (Exception e) {
                                    vObs = "";
                                }
                                String fVObs = vObs;
                                buttonObs.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        MaterialAlertDialogBuilder mat = new MaterialAlertDialogBuilder(getContext());
                                        mat.setTitle(Html.fromHtml("<span style=\"color: #14375B;\"><strong>OBSERVACIONES</strong></span>"));
                                        mat.setMessage(Html.fromHtml(fVObs));
                                        mat.setIcon(R.drawable.ic_notifications);
                                        mat.setPositiveButton("Entendido", null);
                                        mat.create();
                                        mat.show();
                                    }
                                });
                                pregunti.setBotones(buttonObs, false);
                            }

                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                    pregunti.setIdAsignacion(idInformante.id_asignacion);
                    pregunti.setCorrelativo(idInformante.correlativo);

//                    LinearLayout botones = new LinearLayout(getContext());
//                    botones.setOrientation(LinearLayout.HORIZONTAL);
//                    botones.setLayoutParams(layoutParams);

                    ImageButton button = new ImageButton(getContext());
                    button.setImageDrawable(getResources().getDrawable(R.drawable.ic_edit_note));
                    button.setBackground(null);
                    button.setTag(pregunti.getId());
                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            observationMessageEnc(activity, getContext(), "obs", "OBSERVACIÓN", Html.fromHtml("Escriba la observación:"), gPreguntaView.get((Integer) view.getTag()).getObservacion(), (Integer) view.getTag());
                        }
                    });

                    if (pregunta.get_codigo_especifique().equals("0")) {
                        pregunti.setBotones(button, true);
                    }

                    Log.d("REVISION40", pregunti.getCod());
                    layout.addView(pregunti);

//                    if (pregunti.getId() == Integer.valueOf(99999)) {
//
//                        TableLayout tableLayout = new TableLayout(getContext());
//                        try {
//                            List<Map<String, Object>> dataMap = Observacion.obtenerDatosTablaVariablesDinamicas(idInformante);
//                            String[] header = dataMap.get(0).keySet().toArray(new String[0]);
//                            ArrayList<String[]> data = new ArrayList<>();
//                            for (Map<String, Object> it : dataMap) {
//                                ArrayList<String> a = new ArrayList<>();
//                                for (String e : header) {
//                                    a.add(String.valueOf(it.get(e)));
//                                }
//                                data.add(a.toArray(new String[0]));
//                            }
//                            TableDynamicObservacion tableDynamic = new TableDynamicObservacion(tableLayout, context);
//                            tableDynamic.addHeader(header);
//                            tableDynamic.addData(data);
//                            tableDynamic.backgroundHeader(MyApplication.getContext().getResources().getColor(R.color.colorPrimary));
//                            tableDynamic.textColorHeader(MyApplication.getContext().getResources().getColor(R.color.color_list));
//                            tableDynamic.backgroundData(MyApplication.getContext().getResources().getColor(R.color.colorInfo), MyApplication.getContext().getResources().getColor(R.color.colorPrimary));
//                            tableDynamic.lineColor(MyApplication.getContext().getResources().getColor(R.color.color_list));
//                            tableDynamic.textColorData(MyApplication.getContext().getResources().getColor(R.color.color_list));
//
//                            HorizontalScrollView horizontalScrollView = new HorizontalScrollView(getContext());
//                            horizontalScrollView.addView(tableLayout);
//
//                            TextView textView = new TextView(getContext());
//                            textView.setText(R.string.title_table_miembros);
//
//                            layout.addView(textView);
//                            layout.addView(horizontalScrollView);
//                        } catch (Exception e) {
////                                e.printStackTrace();
//                        }
//                    }

                    View separador = new View(getContext());
                    separador.setBackgroundColor(getResources().getColor(R.color.colorSecondary_text));
                    layout.addView(separador, lp);

//                        ProgressBar progressBar = new ProgressBar(getContext(), null, android.R.attr.progressBarStyleHorizontal);
//                        progressBar.setVisibility(View.GONE);
//                        layout.addView(progressBar);

//                        gProgressBar.put(pregunta.get_id_pregunta(), progressBar);
                    gPreguntaView.put(pregunta.get_id_pregunta(), pregunti);
//                    gButtons.put(pregunta.get_id_pregunta(), botones);
                    gSepardor.put(pregunta.get_id_pregunta(), separador);

                    preguntaActual = pregunti;
                    preguntaActualStatic = pregunti;
//                        progresoActual = progressBar;

                    index = pregunta.get_id_pregunta();
                    seccion_ant = pregunta.get_id_seccion();
                }
            } while (pregunta.siguiente());
        }
        // Consistencias Limpieza
        ValidatorConsistencias validatorConsistencias = new ValidatorConsistencias();
        IdInformante idInf = new IdInformante(Integer.valueOf(idInformante.id_asignacion), Integer.valueOf(idInformante.correlativo));
        Informante inf = new Informante();
        ArrayList<IdInformante> lstInf = Informante.getHijos(idInf);
        int cObs = 0;
        for (IdInformante element : lstInf) {
            int vida = element.id_asignacion;
            int vidc = element.correlativo;
            validatorConsistencias.deleteConsistenciasObs(vida, vidc);
        }
    }

    public static void ejecucion(Context context_preg, int idPregunta, int posicion, String valor) {
        cursor = posicion;
    }

    public void obs(int idObs, String observation) {
//        pregs[indice].setObservacion(observation);
        gPreguntaView.get(idObs).setObservacion(observation);
        preguntaActual = gPreguntaView.get(idObs);
        actualiza(idObs);
    }

    //    @SuppressWarnings("unused")
    public void pasarObservado(Activity activity, Context context, String obs, int idObs) {
        if (obs.length() > 3) {
            Encuesta encuestaObs = new Encuesta();
            Log.d("obs", obs);
            Log.d("obs1", idInformante.where());
            Log.d("obs2", String.valueOf(idObs));
            if (encuestaObs.abrir(idInformante.where() + " AND id_pregunta = " + idObs, null)) {
                encuestaObs.editar();
                encuestaObs.set_observacion(obs);
                encuestaObs.set_respuesta(preguntaActual.getResp());
                encuestaObs.set_codigo_respuesta(preguntaActual.getCodResp());
                encuestaObs.set_visible("t");
                encuestaObs.set_fila(fila);
                encuestaObs.guardar();
                actualiza(idObs);
            } else {
                encuestaObs.nuevo();
                encuestaObs.set_id_encuesta(new IdEncuesta(idInformante.id_asignacion, idInformante.correlativo, preguntaActual.getId(), fila));
                encuestaObs.set_usucre(Usuario.getLogin());
                encuestaObs.set_respuesta(preguntaActual.getResp());
                encuestaObs.set_codigo_respuesta(preguntaActual.getCodResp());
                encuestaObs.set_observacion(obs);
                encuestaObs.set_visible("t");
                encuestaObs.set_fila(fila);
                encuestaObs.guardar();
                actualiza(idObs);
            }
            encuestaObs.free();

        } else {
            Toast.makeText(context, "La observacion debe contener mas de 3 letras", Toast.LENGTH_SHORT).show();
        }
    }

    public static boolean evaluar(String regla, String idPregunta, String codResp) {
        boolean result;
        String[] aux = null;
        Set<String> allMatches = new HashSet<>();
        String nuevoValor = regla;

        if (nuevoValor.contains("[")) {
            nuevoValor = evaluarMultiple(nuevoValor, idPregunta, codResp);
            regla = nuevoValor;
        }

        Matcher m = Flujo.p.matcher(regla);
        while (m.find()) {
            String str = m.group();
            allMatches.add(str);
        }
        for (String str : allMatches) {
            String val;
            String element = str.replace("$", "");
            if (element.equals(String.valueOf(idPregunta))) {
                val = codResp;
            } else {
                val = Encuesta.obtenerValorId(idInformante, element);
            }
            if (val.contains(",")) {
                aux = val.split(",");
                Log.d("covid", String.valueOf(val));
                val = "(aux)";

            }

            nuevoValor = nuevoValor.replace(str, val);

            if (nuevoValor.contains("CODIGO")) {
                val = String.valueOf(Informante.getCodigo(idInformante));
                nuevoValor = nuevoValor.replace("CODIGO", val);
            }
            if (nuevoValor.contains("EDAD_ANTERIOR")) {
                val = String.valueOf(Informante.getEdadAnterior(idInformante));
                nuevoValor = nuevoValor.replace("EDAD_ANTERIOR", val);
            }
            if (nuevoValor.contains("NRO_ANTERIOR")) {
                val = String.valueOf(Informante.getNroAnterior(idInformante));
                nuevoValor = nuevoValor.replace("NRO_ANTERIOR", val);
            }
            if (nuevoValor.contains("PARENTESCO_ANTERIOR")) {
                val = String.valueOf(Informante.getParentescoAnterior(idInformante));
                nuevoValor = nuevoValor.replace("PARENTESCO_ANTERIOR", val);
            }
            if (nuevoValor.contains("FECHA_BOLETA")) {
                val = Informante.getCodigoFechaCreacionBoleta(idInformante);
                nuevoValor = nuevoValor.replace("FECHA_BOLETA", val);
            }
        }
        if (nuevoValor.contains("RURAL")) {
            //1 rural
            //0 urbano
            String urbano = String.valueOf(Informante.getUrbano(Asignacion.getUpm(idInformante.id_asignacion)));
            nuevoValor = nuevoValor.replace("RURAL", urbano);
            Log.d("rural", nuevoValor);
        }
        if (nuevoValor.contains("SECCION")) {
            Log.d("SECCION", nuevoValor);
            if (Encuesta.fueRespondida(idInformante)) {
                nuevoValor = nuevoValor.replace("SECCION", "1");
            } else {
                nuevoValor = nuevoValor.replace("SECCION", "0");
            }
        }
        if (nuevoValor.contains("GASTOS")) {
            Log.d("GASTOS", nuevoValor);
            if (Encuesta.fueRespondida(idInformante)) {
                nuevoValor = nuevoValor.replace("GASTOS", "1");
            } else {
                nuevoValor = nuevoValor.replace("GASTOS", "0");
            }
        }
        if (nuevoValor.contains("CONTAR")) {
            if (Informante.getContarPersonas(idInformante)) {
                nuevoValor = nuevoValor.replace("CONTAR", "0");
            } else {
                nuevoValor = nuevoValor.replace("CONTAR", "1");
            }
        }
        if (nuevoValor.contains("PERSONAS")) {
            if (Informante.hayPersonasConcluidas(idInformante) > 0) {
                nuevoValor = nuevoValor.replace("PERSONAS", String.valueOf(Informante.hayPersonasConcluidas(idInformante)));
            } else {
                nuevoValor = nuevoValor.replace("PERSONAS", "0");
            }
        }
        if (nuevoValor.contains("HAY_CARNET")) {
            if (Informante.getHaySelectCarnet(idInformante) > 0) {
                nuevoValor = nuevoValor.replace("HAY_CARNET", String.valueOf(Informante.getHaySelectCarnet(idInformante)));
            } else {
                nuevoValor = nuevoValor.replace("HAY_CARNET", "0");
            }
        }
        if (nuevoValor.contains("PERSONA15")) {
            if (Informante.contarMayores15(idInformante) > 0) {
                nuevoValor = nuevoValor.replace("PERSONA15", String.valueOf(Informante.contarMayores15(idInformante)));
            } else {
                nuevoValor = nuevoValor.replace("PERSONA15", "0");
            }
        }

        //EXPRESIONES GENERALES
        nuevoValor = nuevoValor.replace("~", "==");
        nuevoValor = nuevoValor.replace("!(", "NOT(");
        //EVALUACION
        BigDecimal eval = null;
        if (nuevoValor.contains("(aux)")) {
            String valorEval;
            int cont = 0;
            for (String naux : aux) {
                valorEval = nuevoValor.replace("(aux)", naux);
                Expression expression = new Expression(valorEval);
                eval = expression.eval();
                if (Integer.parseInt(String.valueOf(eval)) > 0) {
                    cont++;
                }
            }
            if (aux.length == cont) {
                result = true;
            } else {
                result = false;
            }
        } else {
            result = false;
            if (nuevoValor.contains("null")) {
                //TODO: VERIFICAR CONSISTENCIA
                result = false;
            } else {
                Log.d("REGLA_BRP2", nuevoValor);
                Expression expression = new Expression(nuevoValor);
                eval = expression.eval();
                if (Integer.parseInt(String.valueOf(eval)) > 0) {
                    result = true;
                }
                Log.d("REGLA_BRP3", String.valueOf(eval));
            }
        }

        return result;
    }

    public static String evaluarMultiple(String regla, String idPregunta, String codResp) {
        String devuelveMultiple = regla;

        List<String> allMatches = new ArrayList<String>();
        String resp = "";
        try {
            Matcher mm = m.matcher(regla);
            int aux = 0;
            while (mm.find()) {
                String str = mm.group();
                allMatches.add(str);
            }
            for (String str : allMatches) {
                List<String> allMatches2 = new ArrayList<String>();
                String valorFinal = str;
                String nuevoValor = str;
//                Pattern p = Pattern.compile("\\$\\d+");
                Matcher m2 = Flujo.p.matcher(str);
                while (m2.find()) {
                    String str2 = m2.group();
                    allMatches2.add(str2);
                }
                for (String str2 : allMatches2) {
                    String val;
                    String element = str2.replace("$", "");
                    if (element.equals(String.valueOf(idPregunta))) {
                        val = codResp;
                    } else {
                        val = Encuesta.obtenerValorId(idInformante, element);
                    }
                    nuevoValor = nuevoValor.replace(str2, val);
                }

                str = nuevoValor.substring(1, nuevoValor.length() - 1).trim();
                Log.d("respuestaSUPREM", str);
                if (str.contains("==")) {
                    String[] a = str.split("==");
                    if (a[0].contains(",")) {
                        String[] b = a[0].split(",");
                        for (String c : b) {
                            if (c.equals(a[1])) {
                                aux++;
                            }
                        }
                    } else {
                        if (a[0].equals(a[1])) {
                            aux++;
                        }
                    }
                    if (aux > 0) {
                        resp = "1==1";
                    } else {
                        resp = "1!=1";
                    }

                } else if (str.contains(">")) {
                    String[] a = str.split(">");
                    if (a[0].contains(",")) {
                        String[] b = a[0].split(",");
                        for (String c : b) {
                            if (a[1].startsWith("=")) {
                                if (c.equals(a[1].replace("=", ""))) {
                                    aux++;
                                } else if (Integer.parseInt(c) > Integer.parseInt(a[1].replace("=", ""))) {
                                    aux++;
                                }
                            } else {
                                if (Integer.parseInt(c) > Integer.parseInt(a[1])) {
                                    aux++;
                                }
                            }
                        }
                        if (aux == b.length) {
                            resp = "1==1";
                        } else {
                            resp = "1!=1";
                        }
                    } else {
                        if (a[1].startsWith("=")) {
                            if (a[0].equals(a[1].replace("=", ""))) {
                                aux++;
                            } else if (Integer.parseInt(a[0]) > Integer.parseInt(a[1].replace("=", ""))) {
                                aux++;
                            }
                        } else {
                            if (Integer.parseInt(a[0]) > Integer.parseInt(a[1])) {
                                aux++;
                            }
                        }
                        if (aux == 1) {
                            resp = "1==1";
                        } else {
                            resp = "1!=1";
                        }
                    }

                } else if (str.contains("<")) {
                    String[] a = str.split("<");
                    if (a[0].contains(",")) {
                        String[] b = a[0].split(",");
                        for (String c : b) {
                            if (a[1].startsWith("=")) {
                                if (c.equals(a[1].replace("=", ""))) {
                                    aux++;
                                } else if (Integer.parseInt(c) < Integer.parseInt(a[1].replace("=", ""))) {
                                    aux++;
                                }
                            } else {
                                if (Integer.parseInt(c) < Integer.parseInt(a[1])) {
                                    aux++;
                                }
                            }
                        }
                        if (aux == b.length) {
                            resp = "1==1";
                        } else {
                            resp = "1!=1";
                        }
                    } else {
                        if (a[1].startsWith("=")) {
                            if (a[0].equals(a[1].replace("=", ""))) {
                                aux++;
                            } else if (Integer.parseInt(a[0]) < Integer.parseInt(a[1].replace("=", ""))) {
                                aux++;
                            }
                        } else {
                            if (Integer.parseInt(a[0]) < Integer.parseInt(a[1])) {
                                aux++;
                            }
                        }
                        if (aux == 1) {
                            resp = "1==1";
                        } else {
                            resp = "1!=1";
                        }
                    }
                } else if (str.contains("!=")) {
                    String[] a = str.split("!=");
                    if (a[0].contains(",")) {
                        String[] b = a[0].split(",");
                        for (String c : b) {
                            if (!c.equals(a[1])) {
                                aux++;
                            }
                        }
                        if (aux == b.length) {
                            resp = "1==1";
                        } else {
                            resp = "1!=1";
                        }
                    } else {
                        if (!a[0].equals(a[1])) {
                            aux++;
                        }
                        if (aux == 1) {
                            resp = "1==1";
                        } else {
                            resp = "1!=1";
                        }
                    }
                }
                devuelveMultiple = devuelveMultiple.replace(valorFinal, resp);
                aux = 0;
            }

        } catch (Exception e) {
            e.printStackTrace();

        }
        Log.d("REGLA_BRP", devuelveMultiple);
        return devuelveMultiple;
    }

    public static void actualiza(int idPregunta) {
        if (!hiloActivo) {
            Log.d("accion2", String.valueOf(idPregunta));
            listen.setValue(idPregunta);
        }
    }

    public static void dibujaTablaMatriz(int filaM) {
        Log.d("FILA", String.valueOf(filaM));
        iComunicaFragments.enviarDatosTablaMatriz(new IdEncuesta(idEncuesta.id_asignacion, idEncuesta.correlativo,Parametros.ID_PREG_BUCLE, filaM), filaM,10, false);
    }

    public static void actualizaMatriz(int idPregunta) {
        if (!hiloActivo) {
            Log.d("accion2", String.valueOf(idPregunta));
            listen.setValue(idPregunta);
        }
    }

    public void observationMessageEnc(final Activity activity, final Context context, final String method, String titulo, Spanned mensaje, String obsText, int idObs) {
//        LayoutInflater inflater = (LayoutInflater)activity.getLayoutInflater();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogLayout = inflater.inflate(R.layout.adapter_observacion, null);

        MaterialAlertDialogBuilder matDiag = new MaterialAlertDialogBuilder(context);
        matDiag.setView(dialogLayout);

        TextView titleContenido = (TextView) (((LinearLayout) dialogLayout).findViewById(R.id.observacionTitle));
        titleContenido.setText(Html.fromHtml(titulo));
        titleContenido.setTextSize(Parametros.FONT_PREG);

        TextView aboutContenido = (TextView) (((LinearLayout) dialogLayout).findViewById(R.id.observacionTextView));
        aboutContenido.setText(Html.fromHtml(String.valueOf(mensaje)));
        aboutContenido.setTextSize(Parametros.FONT_RESP);

        final TextInputEditText editText = (TextInputEditText) (((LinearLayout) dialogLayout).findViewById(R.id.observacion));
        editText.setText(obsText);
        editText.setTextColor(getContext().getResources().getColor(R.color.colorPrimary_text));
        InputFilter characterFilter = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned spanned, int i2, int i3) {
                if (source != null && Parametros.BLOCK_CHARACTER_SET.contains("" + source)) {
                    return "";
                }
                return null;
            }
        };
        editText.setFilters(new InputFilter[]{characterFilter});
        matDiag.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String observation = editText.getText().toString();
                Log.d("observation", observation);
//                if(method.equals("obs")){
                obs(idObs, observation);
//                    Log.d("Pasaa1",observation);
//                }else{
                Log.d("Pasaa2", observation);
//                    try {
//                        preguntaActual.setObservacion(observation);
//                    }catch (Exception e){
//
//                    }
//                pasarObservado(activity, context, observation, idObs);
//                }
                dialog.dismiss();
            }
        });
        matDiag.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        matDiag.create();
        matDiag.show();

    }

    public static void ocultar(Activity activity) {

        View view = activity.getCurrentFocus();
        if (view != null) {
            //Aquí esta la magia
            InputMethodManager input = (InputMethodManager) (activity.getSystemService(Context.INPUT_METHOD_SERVICE));
            input.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    //BLOQUEA LA ORIENTACION DE LA PANTALLA
    public void startThree() {
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    //DESBLOQUEA LA ORIENTACION DE LA PANTALLA
    public void endThree() {
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    public static void resumen() {

        if (idNivel == 3) {
            //SECCION INICIAL HOGAR
            if (idSeccion == 2) {

                Bundle bundle = new Bundle();
                bundle.putInt("IdUpm", Informante.getUpm(idInformante));

                Intent informante = new Intent(activity, BoletaActivity.class);
                informante.putExtras(bundle);
                informante.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                activity.startActivity(informante);
                activity.finish();
            }
//            }else{
//            iComunicaFragments.enviarDatos(idInformante, new IdEncuesta(idInformante.id_asignacion, idInformante.correlativo, 2033), 2, 4, 158, idPadre, 2);
//                iComunicaFragments.enviarDatos(idInformante, new IdEncuesta(idInformante.id_asignacion, idInformante.correlativo, 18581), 2, 4, 158, idPadre, 2);
        } else {
//            iComunicaFragments.enviarDatos(idPadre, new IdEncuesta(idPadre.id_asignacion,idPadre.correlativo,18581), 2, idNivel, idSeccion, idPadre, 2);
        }

//        iComunicaFragments.enviarDatos(idPadre, new IdEncuesta(idPadre.id_asignacion,idPadre.correlativo,18581), 2, idNivel, idSeccion, idPadre, 2);
    }



}
