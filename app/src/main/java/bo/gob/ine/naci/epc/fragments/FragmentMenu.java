package bo.gob.ine.naci.epc.fragments;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.udojava.evalex.Expression;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import bo.gob.ine.naci.epc.EncuestaActivity;
import bo.gob.ine.naci.epc.R;
import bo.gob.ine.naci.epc.adaptadores.EncuestaAdapterRecycler;
import bo.gob.ine.naci.epc.adaptadores.IComunicaFragments;
import bo.gob.ine.naci.epc.entidades.EncLog;
import bo.gob.ine.naci.epc.entidades.Encuesta;
import bo.gob.ine.naci.epc.entidades.Estado;
import bo.gob.ine.naci.epc.entidades.Flujo;
import bo.gob.ine.naci.epc.entidades.IdEncuesta;
import bo.gob.ine.naci.epc.entidades.IdInformante;
import bo.gob.ine.naci.epc.entidades.Informante;
import bo.gob.ine.naci.epc.entidades.Pregunta;
import bo.gob.ine.naci.epc.entidades.Seleccion;
import bo.gob.ine.naci.epc.entidades.UpmHijo;
import bo.gob.ine.naci.epc.entidades.Usuario;
import bo.gob.ine.naci.epc.herramientas.ActionBarActivityMessage;
import bo.gob.ine.naci.epc.herramientas.ActionBarActivityNavigator;
import bo.gob.ine.naci.epc.herramientas.Movil;
import bo.gob.ine.naci.epc.herramientas.Parametros;
import bo.gob.ine.naci.epc.preguntas.Abierta;
import bo.gob.ine.naci.epc.preguntas.Autocompletar;
import bo.gob.ine.naci.epc.preguntas.Calendario;
import bo.gob.ine.naci.epc.preguntas.Cantidad;
import bo.gob.ine.naci.epc.preguntas.Cerrada;
import bo.gob.ine.naci.epc.preguntas.CerradaMatriz;
import bo.gob.ine.naci.epc.preguntas.CerradaOmitida;
import bo.gob.ine.naci.epc.preguntas.Decimal;
import bo.gob.ine.naci.epc.preguntas.Fecha;
import bo.gob.ine.naci.epc.preguntas.Formula;
import bo.gob.ine.naci.epc.preguntas.Gps;
import bo.gob.ine.naci.epc.preguntas.HoraMinuto;
import bo.gob.ine.naci.epc.preguntas.Memoria;
import bo.gob.ine.naci.epc.preguntas.MesAnio;
import bo.gob.ine.naci.epc.preguntas.Multiple;
import bo.gob.ine.naci.epc.preguntas.Numero;
import bo.gob.ine.naci.epc.preguntas.PreguntaView;
import bo.gob.ine.naci.epc.preguntas.Prioridad;
import bo.gob.ine.naci.epc.preguntas.ValorTipo;
import bo.gob.ine.naci.epc.preguntas.ValorTipo2;
import bo.gob.ine.naci.epc.preguntas.ValorTipoGastos;
import bo.gob.ine.naci.epc.preguntas.ValorTipoMatriz;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentMenu#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentMenu extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private View vista;
    private static ArrayList<Map<String, Object>> valores = new ArrayList<>();
    private static RecyclerView list;
    private static EncuestaAdapterRecycler adapter;
    private static LinearLayout popUp;
    private ScrollView scrollPopUp;
    private RelativeLayout relativeLayout;

    private IdInformante idInformante;
    private static IdInformante idPadre;
    private static int nivel;
    private static int idNivel;
    private IdEncuesta idEncuesta;
    private static int idSeccion;
    private static int idUpmHijo;
    private Encuesta encuesta;
    private FloatingActionButton botonEncuesta;
    private FloatingActionButton botonHogar;
    private FloatingActionButton fabIncidencia;

    private static PreguntaView[] pregs;
    private static View[] separadors;
    private Animation animation;

    private ActionBarActivityMessage mensaje;

    private Boolean nuevo = true;

    Activity activity;
    static IComunicaFragments iComunicaFragments;
    private static FragmentTransaction transaction;

    public FragmentMenu() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentList.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentMenu newInstance(String param1, String param2) {
        FragmentMenu fragment = new FragmentMenu();
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
//        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN | WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        vista = inflater.inflate(R.layout.fragment_fragment_list, container, false);

        list = (RecyclerView) vista.findViewById(R.id.fragment_list_view);

        mensaje = new ActionBarActivityMessage();

        Bundle bundle = getArguments();

        if (bundle != null) {
            idInformante = new IdInformante((int[]) bundle.getIntArray("IdInformante"));
            idPadre = new IdInformante((int[]) bundle.getIntArray("IdPadre"));
            idEncuesta = new IdEncuesta((int[]) bundle.getIntArray("IdEncuesta"));
            nivel = bundle.getInt("Nivel");
            idNivel = bundle.getInt("idNivel");
            idSeccion = bundle.getInt("IdSeccion");
            idUpmHijo = ((EncuestaActivity)activity).getIdUpmHijo();
        }
        encuesta = new Encuesta();

        botonEncuesta = vista.findViewById(R.id.botonEncuestaList);
        botonEncuesta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popUp();
            }
        });

        botonHogar = vista.findViewById(R.id.fabHogar);
        //ID PREGUNTAS QUE INICIA BOLETA....
        botonHogar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(idPadre.correlativo!=0){
                    idInformante=idPadre;
                    idPadre=new IdInformante(0,0);
                }
                iComunicaFragments.enviarDatos(idInformante, new IdEncuesta(idInformante.id_asignacion, idInformante.correlativo, 18504), 2, 3, 2, idPadre, 3);
            }
        });
        fabIncidencia = vista.findViewById(R.id.fabIncidencia);
        fabIncidencia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(idPadre.correlativo!=0){
                    idInformante=idPadre;
                    idPadre=new IdInformante(0,0);
                }
                iComunicaFragments.enviarDatos(idInformante, new IdEncuesta(idInformante.id_asignacion, idInformante.correlativo, Parametros.ID_INCIDENCIA_FINAL), 2, 3, 180, idPadre, 3);
            }
        });
        cargarListado(getActivity());
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

        if (context instanceof Activity){
            this.activity = (Activity) context;
            iComunicaFragments = (IComunicaFragments) this.activity;
        }

        if (context instanceof FragmentMenu.OnFragmentInteractionListener) {
            mListener = (FragmentMenu.OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private void popUp() {
        final String buttonPressed = "";
        relativeLayout = new RelativeLayout(getContext());
        LinearLayout.LayoutParams buttonParam=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        buttonParam.setMargins(10,10,10,50);
        relativeLayout.setLayoutParams(buttonParam);

        scrollPopUp = new ScrollView(getContext());
        scrollPopUp.setLayoutParams(buttonParam);
        popUp = new LinearLayout(getContext());
        popUp.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        popUp.setOrientation(LinearLayout.VERTICAL);
        dibujarPregunta();

        scrollPopUp.addView(popUp);
        relativeLayout.addView(scrollPopUp);


        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
        alertDialog.setView(relativeLayout);
        alertDialog.setCancelable(false);
        alertDialog.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                guardar();
//                Toast.makeText(getContext(), "Registro creado con Exito", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
        alertDialog.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
//                buttonPressed = "Cancelar";
                dialog.dismiss();
//                Toast.makeText(getContext(), "Cancelado", Toast.LENGTH_SHORT).show();
            }
        });
        AlertDialog dialog = alertDialog.create();
//        ColorDrawable back = new ColorDrawable(Color.TRANSPARENT);
//        InsetDrawable inset = new InsetDrawable(back, 20);
//        dialog.getWindow().setBackgroundDrawable(inset);

        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
//                try {
//                    switch (buttonPressed) {
//                        case "Aceptar":
                    cargarListado(getActivity());
//                    Method m = this.getClass().getMethod("cargarListado");
//                    m.invoke(this);
//                                break;
//
//                        case "Cancelar":
//                            if (methodCancelar != null) {
//                                Method m = this.getClass().getMethod(methodCancelar);
//                                m.invoke(this);
//                                break;
//                            }
//                    }
//                } catch (IllegalAccessException e) {
//                    e.printStackTrace();
//                } catch (InvocationTargetException e) {
//                    e.printStackTrace();
//                } catch (NoSuchMethodException e) {
//                    e.printStackTrace();
//                }
            }
        });
//        Window window = dialog.getWindow();

        dialog.show();

//        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);


    }

    private void dibujarPregunta(){

        final Pregunta pregunta = new Pregunta();
        animation = AnimationUtils.loadAnimation(getContext(), R.anim.item_anim_from_right);
        LayoutAnimationController controller = new LayoutAnimationController(animation,0.1f);
        popUp.setLayoutAnimation(controller);

        if (pregunta.abrir("id_seccion = " + idSeccion + " and inicial = 1", "codigo_pregunta")) {

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
//                Typeface typeface = ResourcesCompat.getFont(getContext(), R.font.quicksand_medium);
//                modulo.setTypeface(typeface);
//                //modulo.setTextColor(getResources().getColor(R.color.color_darker));
//                popUp.addView(modulo);
//            }
//            seccion.free();
            int ar = pregunta.count();
            pregs = new PreguntaView[pregunta.count()];
            separadors = new View[pregunta.count()];
            int i = 0;
            do {
                String p = pregunta.procesaEnunciado(idPadre, pregunta.get_pregunta());
                String a = pregunta.get_id_tipo_pregunta().toString();
                String b = pregunta.get_ayuda();
                Map<Integer, String> omision;
                if(pregunta.get_omision() != null && !pregunta.get_omision().equals("null")) {
                    omision = Pregunta.getOmision_convert(pregunta.get_omision());
                } else{
                    omision = null;
                }
                switch (pregunta.get_id_tipo_pregunta()) {
                    case Abierta: {
                        pregs[i] = new Abierta(getContext(), i,pregunta.get_id_pregunta(), pregunta.get_id_seccion(), pregunta.get_codigo_pregunta(), p, pregunta.get_longitud(), omision, pregunta.get_ayuda(), true, false);
                        break;
                    }
                    case Incidencia:
                    case Cerrada: {
                        ArrayList<Integer> filtro = null;
                        Map<Integer, String> respuestas = Pregunta.getRespuestas(pregunta.get_respuesta());
                        pregs[i] = new Cerrada(getContext(), i, pregunta.get_id_pregunta(), pregunta.get_id_seccion(), pregunta.get_codigo_pregunta(), p, respuestas, pregunta.get_codigo_especifique(), omision, filtro, pregunta.get_ayuda(), false);
                        break;
                    }
                    case CerradaMatriz: {
                        Map<Integer, String> respuestas = Pregunta.getRespuestas(pregunta.get_respuesta());
                        pregs[i] = new CerradaMatriz(getContext(), pregunta.get_id_pregunta(), pregunta.get_id_seccion(), pregunta.get_codigo_pregunta(), p, respuestas, pregunta.get_codigo_especifique(), pregunta.get_longitud(), 4, pregunta.get_ayuda());
                        break;
                    }
                    case CerradaOmitida: {
                        Map<Integer, String> respuestas = Pregunta.getRespuestas(pregunta.get_respuesta());
                        pregs[i] = new CerradaOmitida(getContext(), pregunta.get_id_pregunta(), pregunta.get_id_seccion(), pregunta.get_codigo_pregunta(), p, respuestas, pregunta.get_codigo_especifique(), pregunta.get_codigo_especial(), pregunta.get_ayuda(), Informante.getUpm(idInformante));
                        break;
                    }
                    case UsoVivienda:
                    case NumeroVivienda:
                    case Numero: {
                        pregs[i] = new Numero(getContext(), i, pregunta.get_id_pregunta(), pregunta.get_id_seccion(), pregunta.get_codigo_pregunta(), p, pregunta.get_longitud(), omision, pregunta.get_ayuda(), false);
                        break;
                    }
                    case Decimal: {
                        pregs[i] = new Decimal(getContext(), i, pregunta.get_id_pregunta(), pregunta.get_id_seccion(), pregunta.get_codigo_pregunta(), p, pregunta.get_longitud(), omision, pregunta.get_ayuda(), false);
                        break;
                    }
                    case Multiple: {
                        Map<Integer, String> respuestas = Pregunta.getRespuestas(pregunta.get_respuesta());
                        pregs[i] = new Multiple(getContext(), pregunta.get_id_pregunta(), pregunta.get_id_seccion(), pregunta.get_codigo_pregunta(), p, respuestas, 4, pregunta.get_ayuda());
                        break;
                    }
                    case Fecha: {
                        pregs[i] = new Fecha(getContext(), i, pregunta.get_id_pregunta(), pregunta.get_id_seccion(), pregunta.get_codigo_pregunta(), p, 4, pregunta.get_ayuda(), false);
                        break;
                    }
                    case MesAnio: {
                        pregs[i] = new MesAnio(getContext(), pregunta.get_id_pregunta(), pregunta.get_id_seccion(), pregunta.get_codigo_pregunta(), p, 4, pregunta.get_ayuda());
                        break;
                    }
                    case HoraMinuto: {
                        pregs[i] = new HoraMinuto(getContext(), i, pregunta.get_id_pregunta(), pregunta.get_id_seccion(), pregunta.get_codigo_pregunta(), p, 4, pregunta.get_ayuda(), false);
                        break;
                    }
                    case Gps: {
                        //preg = new GpsOldd(this, pregunta.get_id_pregunta(), pregunta.get_id_seccion(), pregunta.get_codigo_pregunta(), p, pregunta.get_ayuda());
                        pregs[i] = new Gps(getContext(), i, pregunta.get_id_pregunta(), pregunta.get_id_seccion(), pregunta.get_codigo_pregunta(), p, pregunta.get_ayuda(), false);
                        break;
                    }
                    case Formula: {
                        Float val = 0f;//Encuesta.evaluar(pregunta.get_rpn_formula(), idEncuesta.getIdInformante(), idEncuesta.fila, null, null, null);
                        pregs[i] = new Formula(getContext(), pregunta.get_id_pregunta(), pregunta.get_id_seccion(), pregunta.get_codigo_pregunta(), p, val, pregunta.get_ayuda(), "0");
                        break;
                    }

                    case Cantidad: {
                        pregs[i] = new Cantidad(getContext(), pregunta.get_id_pregunta(), pregunta.get_id_seccion(), pregunta.get_codigo_pregunta(), p, 4, pregunta.get_ayuda());
                        break;
                    }
//                    case CerradaBucle: {
//                        Map<Long, String> respuestas = Encuesta.getRespuestas(informante.get_id_informante(), pregunta.get_variable_bucle(), pregunta.get_rpn_formula());
//                        pregs[i] = new CerradaBucle(getContext(), pregunta.get_id_pregunta(), pregunta.get_id_seccion(), pregunta.get_codigo_pregunta(), p, respuestas, pregunta.get_codigo_especifique(), pregunta.get_codigo_especial(), pregunta.get_ayuda());
//                        break;
//                    }
                    case Autocompletar: {
                        if(pregunta.get_id_pregunta()==Parametros.ID_MANZANA_COMUNIDAD)
                        pregs[i] = new Autocompletar(getContext(), i, pregunta.get_id_pregunta(), pregunta.get_id_seccion(), pregunta.get_codigo_pregunta(), p, pregunta.get_longitud(), pregunta.get_catalogo(), omision, pregunta.get_ayuda(), true, false,true);
                        else
                            pregs[i] = new Autocompletar(getContext(), i, pregunta.get_id_pregunta(), pregunta.get_id_seccion(), pregunta.get_codigo_pregunta(), p, pregunta.get_longitud(), pregunta.get_catalogo(), omision, pregunta.get_ayuda(), true, false,false);
                        break;
                    }
                    case ValorTipo: {
                        Map<Integer, String> respuestas = Pregunta.getRespuestas(pregunta.get_respuesta());
                        pregs[i] = new ValorTipo(getContext(), i, pregunta.get_id_pregunta(), pregunta.get_id_seccion(), pregunta.get_codigo_pregunta(), p, respuestas, pregunta.get_longitud(), 4, pregunta.get_ayuda(), false);
                        break;
                    }
                    case ValorTipo2: {
                        Map<Integer, String> respuestas = Pregunta.getRespuestas(pregunta.get_respuesta());
                        pregs[i] = new ValorTipo2(getContext(), pregunta.get_id_pregunta(), pregunta.get_id_seccion(), pregunta.get_codigo_pregunta(), p, respuestas, pregunta.get_longitud(), 4, pregunta.get_ayuda());
                        break;
                    }
                    case ValorTipoGastos: {
                        Map<Integer, String> respuestas = Pregunta.getRespuestas(pregunta.get_respuesta());
                        pregs[i] = new ValorTipoGastos(getContext(), i, pregunta.get_id_pregunta(), pregunta.get_id_seccion(), pregunta.get_codigo_pregunta(), p, respuestas, pregunta.get_longitud(), 4, pregunta.get_ayuda(), pregunta.get_codigo_especifique(), false);
                        break;
                    }
                    case ValorTipoMatriz: {
                        Map<Integer, String> respuestas = Pregunta.getRespuestas(pregunta.get_respuesta());
                        pregs[i] = new ValorTipoMatriz(getContext(), pregunta.get_id_pregunta(), pregunta.get_id_seccion(), pregunta.get_codigo_pregunta(), p, respuestas, pregunta.get_longitud(), 4, pregunta.get_ayuda(), pregunta.get_codigo_especifique());
                        break;
                    }

                    case Prioridad: {
                        Map<Integer, String> respuestas;
                        if(pregunta.get_catalogo().equals("--")){
                            respuestas = Pregunta.getRespuestas(pregunta.get_respuesta());
                        }else{
                            respuestas = null;
                        }
                        pregs[i] = new Prioridad(getContext(), i, pregunta.get_id_pregunta(), pregunta.get_id_seccion(), pregunta.get_codigo_pregunta(), p, respuestas, pregunta.get_catalogo(),omision, pregunta.get_codigo_especifique(), pregunta.get_minimo(), pregunta.get_longitud(), 4, pregunta.get_ayuda(), false);
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
                        pregs[i] = new Memoria(getContext(), i, pregunta.get_id_pregunta(), pregunta.get_id_seccion(), pregunta.get_codigo_pregunta(), p, pregunta.get_longitud(), omision, values, pregunta.get_ayuda(), false);
                        break;
                    }
                    case Upm: {
                        Map<Integer, String> respuestas = UpmHijo.getRespuestasUPM(Informante.getUpm(idInformante));
                        pregs[i] = new Cerrada(getContext(), i, pregunta.get_id_pregunta(), pregunta.get_id_seccion(), pregunta.get_codigo_pregunta(), p, respuestas, pregunta.get_codigo_especifique(), omision, null, pregunta.get_ayuda(), false);
                        break;
                    }
                    case UpmHijo: {
                        Map<Integer, String> respuestas = UpmHijo.getRespuestasManzano(Informante.getUpm(idInformante));
                        pregs[i] = new Cerrada(getContext(), i, pregunta.get_id_pregunta(), pregunta.get_id_seccion(), pregunta.get_codigo_pregunta(), p, respuestas, pregunta.get_codigo_especifique(), omision, null, pregunta.get_ayuda(), false);
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

                pregs[i].startAnimation(animation);

//                layout.addView(pregs[i]);
                popUp.addView(pregs[i]);

                separadors[i] = new View(getContext());
                separadors[i].setBackgroundColor(getResources().getColor(R.color.colorSecondary_text));
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                lp.height = 1;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    separadors[i].setElevation(100);
                }

//                layout.addView(separadors[i], lp);
                popUp.addView(separadors[i], lp);

                i++;
            } while (pregunta.siguiente());
//            ocultaPreguntas();
//            inhabilitaPreguntas();
//            PreguntaView.obtenerPosicionDeLaVista(pregs[pregunta.count()-1]);
            pregs[0].setFocus();

        } else {
            mensaje.errorMessage(getContext(), null, "Error!", Html.fromHtml("No se encontró la pregunta."), Parametros.FONT_OBS);
        }
        pregunta.free();
    }

    private void agregarEncuesta() {
        ActionBarActivityNavigator act = new ActionBarActivityNavigator();
//        act.irEncuesta(new IdInformante(Asignacion.get_asignacion(idUpm, Usuario.getUsuario()), 0), new IdEncuesta(0, 0, 0, 0), nivel, 0, new IdInformante(Asignacion.get_asignacion(idUpm, Usuario.getUsuario()), 0));
        act.finish();
    }

    public static void cargarListado(Activity activity) {
        try {
            Informante informante = new Informante();
            valores = informante.obtenerListadoEncuesta(4, idPadre);
            adapter = new EncuestaAdapterRecycler(activity, valores);
            adapter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    String a = valores.get(list.getChildAdapterPosition(v)).get("id_asignacion").toString();
                    try {
                        Map<String, Object> val = valores.get(list.getChildAdapterPosition(v));
//                        irfragment de encuesta
//                        Toast.makeText(getContext(), val.get("id_upm").toString(), Toast.LENGTH_LONG).show();
                        Flujo flujo = new Flujo();
//                        Siguiente idSiguiente = flujo.siguiente(new IdInformante((Integer) val.get("id_asignacion"), (Integer) val.get("correlativo")), (Integer) val.get("ultima_preg"));
                       //obtiene la primera pregunta del nivel 4 miembro del hogar


                        accionInicioBoleta(new IdInformante((Integer) val.get("id_asignacion"), (Integer) val.get("correlativo")), new IdEncuesta((Integer) val.get("id_asignacion"), (Integer) val.get("correlativo"), 18581),4, 4, 5, new IdInformante((Integer) val.get("id_asignacion_padre"), (Integer) val.get("correlativo_padre")));
                    } catch (Exception exp) {
                        exp.printStackTrace();
                    }
                }
            });
            if (valores.size() > 0) {
                list.setVisibility(View.VISIBLE);
                list.setAdapter(adapter);
            } else{
                list.setVisibility(View.GONE);
            }

            ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
            itemTouchHelper.attachToRecyclerView(list);

        } catch (Exception exp) {
            exp.printStackTrace();
        }
    }



    private static void accionInicioBoleta(IdInformante n_informante, IdEncuesta n_encuesta, int n_nivel, int n_idNivel, int n_seccion, IdInformante n_padre){
        iComunicaFragments.enviarDatos(n_informante, n_encuesta, n_nivel, n_idNivel, n_seccion, n_padre, 3);
    }

    public void guardar() {
        try {
            Map<String, String> valores = null;
            // BUCLE QUE EVALUA LAS PREGUNTAS Y VERIFICA CONSISTENCIAS
            for (PreguntaView p : pregs) {
                if (p.getCodResp().equals("-1") || p.getCodResp().equals("-1.0")) {
                    mensaje.errorMessage(getContext(), null, "Error!", Html.fromHtml("Faltó responder: "+p.getCod()), Parametros.FONT_OBS);
                    p.setFocus();
                    return;
                } else {
                    Pregunta pregunta = new Pregunta();
                    if (pregunta.abrir(p.getId())) {
                        String asd = p.getCod();

                            List<String> omision = Pregunta.getOmisionId(p.getId());
                            if (!omision.contains(p.getCodResp())) {
                                if (p instanceof Numero || p instanceof Decimal) {
                                    try {
                                        if ((Float.parseFloat(p.getCodResp()) < pregunta.get_minimo() && pregunta.get_minimo() > 0) || (Float.parseFloat(p.getCodResp()) > pregunta.get_maximo() && pregunta.get_maximo() > 0)) {
                                            mensaje.errorMessage(getContext(), null, "Error!", Html.fromHtml(String.format("El valor de " + pregunta.get_codigo_pregunta() + " debe estar entre %d y %d", pregunta.get_minimo(), pregunta.get_maximo())), Parametros.FONT_OBS);
                                            p.setFocus();
                                            return;
                                        }
                                    } catch (Exception ex) {
                                        mensaje.errorMessage(getContext(), null, "Error!", Html.fromHtml(String.format("El valor de " + pregunta.get_codigo_pregunta() + " debe estar entre %d y %d", pregunta.get_minimo(), pregunta.get_maximo())), Parametros.FONT_OBS);
                                        p.setFocus();
                                        return;
                                    }
                                } else if (p instanceof Abierta || p instanceof Memoria || p instanceof Autocompletar) {
                                    if (p.getResp().length() < pregunta.get_minimo() && pregunta.get_minimo() > 0 || (p.getResp().length() > pregunta.get_maximo() && pregunta.get_maximo() > 0)) {
                                        mensaje.errorMessage(getContext(), null, "Error!", Html.fromHtml(String.format("La longitud de " + pregunta.get_codigo_pregunta() + " debe estar entre %d y %d", pregunta.get_minimo(), pregunta.get_maximo()) + " (Valor actual: " + p.getResp().length() + ")"), Parametros.FONT_OBS);
                                        p.setFocus();
                                        return;
                                    }
                                }
                            }

                        String av = pregunta.get_codigo_pregunta();
                        String bv = pregunta.get_pregunta();
                        String por= pregunta.get_regla();
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
                                    switch (rcodigo){
                                        case "1":
                                            mensaje.errorMessage(getContext(), null, "Error de consistencia!", Html.fromHtml(rmensaje), Parametros.FONT_OBS);
                                            p.setFocus();
                                            break;
                                        case "2":
                                            mensaje.observationMessage(getContext(), "pasarObservado", "JUSTIFIQUE", Html.fromHtml(rmensaje), p.getObservacion(), false);
                                            p.setFocus();
                                            break;
                                    }
                                }
                            }
                        }
                    } else {
                        mensaje.errorMessage(getContext(), null, "Error!", Html.fromHtml("No existe la pregunta."), Parametros.FONT_OBS);
                        return;
                    }
                    pregunta.free();
                }
            }

            Informante informante = new Informante();
            boolean flag = false;
            if (nuevo) {
                // CREA UN NUEVO INFORMANTE - SE REGISTRA LA LATITUD Y LONGITUD EN LA CREACION DEL INFORMANTE
                flag = informante.nuevo();
                informante.set_latitud(Movil.getGPS().split(";")[0].toString());
                informante.set_longitud(Movil.getGPS().split(";")[1].toString());

                // LOGRA ACCEDERSE AL INFORMANTE
                informante.set_id_informante(new IdInformante(idInformante.id_asignacion, 0));
                informante.set_id_usuario(Usuario.getUsuario());
                if (idPadre.id_asignacion > 0) {
                    informante.set_id_informante_padre(idPadre);
                }
                informante.set_id_nivel(idNivel);

                String codigo;
//                if (nuevo) {
                 // CODIGO PARA NIVEL 2
                    if (flag) {
                            codigo = "999";
                    } else {
                        codigo = informante.get_codigo();
                    }

                informante.set_codigo(codigo);
                // TERMINA DE GENERAR EL CODIGO

                informante.set_id_upm(Informante.getUpm(idInformante));
                if(idUpmHijo!=0){
                    informante.set_id_upm_hijo(idUpmHijo);
                }
                informante.set_descripcion(pregs[0].getResp());


                    informante.set_usucre(Usuario.getLogin());

                idInformante = informante.guardar(idPadre);

                if (idInformante != null) {
                    String descripcion = "";
                    for (PreguntaView preg : pregs) {

                        if (encuesta.abrir(idInformante.where() + " AND id_pregunta = " + preg.getId(), null)) {
                            encuesta.editar();
                        } else {
                            encuesta.nuevo();
                        }
                        encuesta.set_id_encuesta(new IdEncuesta(idInformante.id_asignacion, idInformante.correlativo, preg.getId()));
                        encuesta.set_codigo_respuesta(preg.getCodResp());
                        encuesta.set_respuesta(preg.getResp());
                        encuesta.set_observacion(preg.getObservacion());
                        encuesta.set_usucre(Usuario.getLogin());
                        encuesta.set_estado(preg.getEstado());
                        encuesta.set_latitud(Movil.getGPS().split(";")[0].toString());
                        encuesta.set_longitud(Movil.getGPS().split(";")[1].toString());
                        encuesta.set_visible("t");
                        encuesta.guardar();
                        List<String> omision = Pregunta.getOmisionId(preg.getId());
                        if (!omision.contains(preg.getCodResp())) {
                            descripcion = descripcion + preg.getResp() + " ";
                        }
                        Pregunta pregunta = new Pregunta();
                        pregunta.abrir(preg.getId());
                        if (!pregunta.get_apoyo().equals("null") && !pregunta.get_apoyo().equals(null)) {
                            EncLog log = new EncLog();
                            if (log.abrir(idInformante.where() + " AND id_pregunta = " + preg.getId(), null)) {
                                log.editar();
                            } else {
                                log.nuevo();
                            }
                            log.set_id_asignacion(idInformante.id_asignacion);
                            log.set_correlativo(idInformante.correlativo);
                            log.set_id_usuario(Usuario.getUsuario());
                            log.set_id_tipo(idPadre.correlativo);
                            log.set_id_pregunta(preg.getId());
                            log.set_valor1(String.valueOf(Informante.getCodigo(idInformante)));
                            log.set_valor2(preg.getCodResp());
                            log.set_descripcion(preg.getResp());
                            log.set_usucre(Usuario.getNombreUsuario());
                            log.guardar();
                            log.free();
                        }
                        pregunta.free();
                    }
                    encuesta.free();

                    if (informante.abrir(idInformante)) {
                        if (informante.editar()) {
                            informante.set_descripcion(descripcion);
                            informante.guardar();
                        }
                    }
//                    Method m = getContext().getClass().getMethod("cargarListado");
//                    m.invoke(getContext());
                } else {
                    mensaje.errorMessage(getContext(),null, "Error!", Html.fromHtml("No se pudo guardar."), Parametros.FONT_OBS);
                }
            } else {
                mensaje.errorMessage(getContext(),null, "Error!", Html.fromHtml("No se pudo crear el informante."), Parametros.FONT_OBS);
            }
            informante.free();
        } catch (Exception ex) {
            mensaje.errorMessage(getContext(), null, "Error!", Html.fromHtml(ex.getMessage()), Parametros.FONT_OBS);
        }
    }

    public static void descartar(Activity activity, IdInformante idInformante) {
        Informante informante = new Informante();
        informante.abrir(idInformante);
        informante.editar();
        informante.set_apiestado(Estado.ANULADO);
        informante.guardar();
        try{
            informante.reordenar(idInformante);
        }catch (Exception ee){
            ee.printStackTrace();
        }
        cargarListado(activity);
        informante.free();

    }

    private Map<String,String> cargar() {
        Map<String,String> res = new HashMap<>();
        for (PreguntaView p : pregs) {
            res.put("$"+p.getId(), p.getCodResp());
        }
        return res;
    }

    public boolean evaluar(String regla, Map<String, String> valores) {
        boolean result;
        List<String> allMatches = new ArrayList<String>();
        String nuevoValor = regla;

        Pattern p = Pattern.compile("\\$\\d+");
//        Pattern p = Pattern.compile("\\$[0-9]*g");
        Matcher m = p.matcher(regla);
        while (m.find()) {
            String str = m.group();
            allMatches.add(str);
        }
        for (String str : allMatches) {
            String val;
            String aq = valores.get(str);
            if (valores.get(str)==null ) {
                String element = str.replace("$","");
                val = Encuesta.obtenerValorId(idPadre, element);
                if (val.contains(",")) {
                    val = val.replace(",", "||");
                    val = "(" + val + ")";
                }
                nuevoValor = nuevoValor.replace(str, val);
            }else{
                nuevoValor = nuevoValor.replace(str, valores.get(str));
            }
        }
        if(nuevoValor.contains("SORTEADO")){
            if (Seleccion.hasSelected(Informante.getUpm(idInformante))) {
                nuevoValor = nuevoValor.replace("SORTEADO", "1");
            } else {
                nuevoValor = nuevoValor.replace("SORTEADO", "0");
            }
        }
        //FALTA EVALUAR LOS DATOS DE PREGUNTAS MULTIPLES Y PREGUNTAS DE ORDEN
        BigDecimal eval = null;
        Expression expression = new Expression(nuevoValor);
        eval = expression.eval();
        if (Integer.parseInt(String.valueOf(eval)) > 0) {
            result = true;
        } else {
            result = false;
        }
        return result;
    }

    static ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.START | ItemTouchHelper.END, 0) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {

            int fromPosition = viewHolder.getAdapterPosition();
            int toPosition = target.getAdapterPosition();
            Collections.swap(valores, fromPosition, toPosition);
            recyclerView.getAdapter().notifyItemMoved(fromPosition, toPosition);
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            Log.d("movimiento",""+direction);
        }
    };

}
