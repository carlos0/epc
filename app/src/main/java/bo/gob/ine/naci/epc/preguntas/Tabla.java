package bo.gob.ine.naci.epc.preguntas;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.udojava.evalex.Expression;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import bo.gob.ine.naci.epc.MyApplication;
import bo.gob.ine.naci.epc.R;
import bo.gob.ine.naci.epc.entidades.EncLog;
import bo.gob.ine.naci.epc.entidades.Estado;
import bo.gob.ine.naci.epc.entidades.IdInformante;
import bo.gob.ine.naci.epc.entidades.Pregunta;
import bo.gob.ine.naci.epc.entidades.TableDynamic;
import bo.gob.ine.naci.epc.fragments.FragmentEncuesta;
import bo.gob.ine.naci.epc.herramientas.Movil;
import bo.gob.ine.naci.epc.herramientas.Parametros;

import static bo.gob.ine.naci.epc.fragments.FragmentEncuesta.ocultar;

public class Tabla extends PreguntaView implements View.OnClickListener {
    protected TextInputLayout contenedor;
    protected TextInputEditText textbox;

    protected Context context;
    protected int posicion;
    protected int idSeccion;
    protected String cod;
    protected Boolean evaluar;
    protected String preg;
    protected Activity activity;

    protected Button add;
    protected Button delete;
    protected Map<Integer, String> buttonsActive;
    protected List<String> btnOpciones = new ArrayList<String>();
    protected List<String> idOpciones = new ArrayList<String>();
    protected String seleccion = "";
    protected String seleccionText = "";
    protected View boton;
    protected Map<String, View> guardaBotones = new HashMap<>();
    protected int maxLength;

    private ArrayList<TableRow> filas;
    private int FILAS, COLUMNAS;

    private TableLayout tableLayout;
    private EditText txtNombre;
    private EditText txtApellido;
    private String[]header;
    private ArrayList<String[]> rows=new ArrayList<>();
    private TableDynamic tableDynamic;

    private static LinearLayout popUp;
    private ScrollView scrollPopUp;
    private RelativeLayout relativeLayout;

    private String descripcion;
    private ArrayList<String> id_pregunta = new ArrayList<>();
    private ArrayList<String> id_tipo_pregunta = new ArrayList<>();
    private ArrayList<String> minimo = new ArrayList<>();
    private ArrayList<String> maximo = new ArrayList<>();
    private ArrayList<String> pregunta = new ArrayList<>();
    private ArrayList<String> respuesta = new ArrayList<>();
    private ArrayList<String> saltos = new ArrayList<>();
    private ArrayList<String> regla = new ArrayList<>();
    private ArrayList<String> omision = new ArrayList<>();

    private ArrayList<String> fila = new ArrayList<>();
    private ArrayList<String> filaI = new ArrayList<>();
    private ArrayList<String[]> filaFinal = new ArrayList<>();
    private ArrayList<String[]> filaInicial = new ArrayList<>();

    private String rdescripcion;
    private ArrayList<String> rid_pregunta = new ArrayList<>();
    private ArrayList<String> rcodigo = new ArrayList<>();
    private ArrayList<String> rrespuestaOnline = new ArrayList<>();
    private ArrayList<String> rvisible = new ArrayList<>();

    private ArrayList<String> Rpregs = new ArrayList<>();

    private IdInformante idInformante;
    private IdInformante idPadre;
    private View dialogLayout;
    private LayoutInflater inflater;
    private static PreguntaView[] pregs;
    private static View[] separadors;
    private Button[] buttons;
    private int indice = -1;
    private Boolean correcto = false;
    private Boolean eliminar = false;
    private JSONObject unidad;
    private JSONArray grupoUnidad;
    private JSONObject grupoGu;
    private JSONArray grupoGGu = new JSONArray();
    private Map<Integer,String> grupo = new LinkedHashMap<>();
    private Map<Integer,String> respGuardada = new LinkedHashMap<>();
    private String incidencia="0";
    private String incidenciaTemporal;
    private AlertDialog dialog;
    private int contador = 0;

    public Tabla(Activity activity, final Context context, IdInformante idInformante, IdInformante idPadre, final int posicion, final int id, int idSeccion, final String cod, String preg, int maxLength, final Map<Integer, String> buttonsActive, String ayuda, boolean multiple, final Boolean evaluar) {
        super(context, id, idSeccion, cod, "", ayuda);
        this.buttonsActive = buttonsActive;
        this.maxLength = maxLength;

        this.idInformante = idInformante;
        this.idPadre = idPadre;
        this.cod = cod;
        this.context = context;
        this.posicion = posicion;
        this.idSeccion = idSeccion;
        this.evaluar = evaluar;
        this.preg = preg;
        this.activity = activity;

            try {
                JSONArray jsonArrayDesc = new JSONArray(preg);
                JSONObject objetoJSONDesc = jsonArrayDesc.getJSONObject(0);
                descripcion = objetoJSONDesc.getString("descripcion");

                JSONArray jsonArray = new JSONArray(descripcion);
                for (int j = 0; j < jsonArray.length(); j++) {
                    JSONObject objetoJSON = jsonArray.getJSONObject(j);
                    id_pregunta.add(objetoJSON.getString("id_pregunta"));
                    id_tipo_pregunta.add(objetoJSON.getString("id_tipo_pregunta"));
                    minimo.add(objetoJSON.getString("minimo"));
                    maximo.add(objetoJSON.getString("maximo"));
                    pregunta.add(objetoJSON.getString("pregunta"));
                    respuesta.add(objetoJSON.getString("respuesta").equals("null")? null : objetoJSON.getString("respuesta"));
                    saltos.add(objetoJSON.getString("saltos").equals("null")? null : objetoJSON.getString("saltos"));
                    regla.add(objetoJSON.getString("regla").equals("null")? null : objetoJSON.getString("regla"));
                    omision.add(objetoJSON.getString("omision").equals("null")? null : objetoJSON.getString("omision"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        header = pregunta.toArray(new String[0]);

            adicionarTabla(getClients());



//        LinearLayout buttons = new LinearLayout(context);
//        buttons.setOrientation(LinearLayout.HORIZONTAL);
//        buttons.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
//        LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
//        layoutParams.weight = 1;
//
//        if (buttonsActive != null) {
//            for (Integer key : buttonsActive.keySet()) {
//                resp = new Button(context);
//                resp.setLayoutParams(layoutParams);
//                buttons.addView(resp);
//                String opcion = buttonsActive.get(key);
//                String[] a = opcion.split("\\|");
//                resp.setText(a[1]);
//                resp.setTextSize(Parametros.FONT_RESP);
//                resp.setId(key);
//                resp.setOnClickListener(this);
//                btnOpciones.add(a[1]);
//                idOpciones.add(a[0]);
//                guardaBotones.put(a[0],resp);
//            }
//            addView(buttons);
//        }
//        textbox.requestFocus();
    }

    private void adicionarTabla(ArrayList<String[]> data) {
        eliminar = false;
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        dialogLayout = inflater.inflate(R.layout.adapter_table, null);

        tableLayout = (TableLayout)(((LinearLayout)dialogLayout).findViewById(R.id.table));

        tableDynamic=new TableDynamic(tableLayout, context);
        tableDynamic.addHeader(header);
        tableDynamic.addData(data);
        tableDynamic.backgroundHeader(MyApplication.getContext().getResources().getColor(R.color.colorPrimary));
        tableDynamic.textColorHeader(MyApplication.getContext().getResources().getColor(R.color.color_list));
        tableDynamic.backgroundData(MyApplication.getContext().getResources().getColor(R.color.colorAccent),MyApplication.getContext().getResources().getColor(R.color.colorPrimary));
        tableDynamic.lineColor(MyApplication.getContext().getResources().getColor(R.color.colorInfo));
        tableDynamic.textColorData(MyApplication.getContext().getResources().getColor(R.color.color_list));


        add = ((LinearLayout)dialogLayout).findViewById(R.id.button);
        delete = ((LinearLayout)dialogLayout).findViewById(R.id.buttonDelete);

        add.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Rpregs = new ArrayList<>();
                grupoGu = new JSONObject();
                grupoUnidad = new JSONArray();
                fila = new ArrayList<>();

                String ultimaIncidencia=obtenerIncidencia();
//                if(incidencia.equals("0")){
                if(ultimaIncidencia.equals("0")){
                    popUp();
                } else {
                    obs.informationMessage(context, null, "INFORMACION", Html.fromHtml("Usted ya registro la incidencia final de esta boleta, para poder registrar nuevamente debe VACIAR LA TABLA y AÑADIR UN NUEVO REGISTRO"), Parametros.FONT_AYUD);
                }
//                save();
                if(evaluar) {
                    FragmentEncuesta.ejecucion(context, id, posicion, "");
                }

            }
        });
        delete.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                eliminarRegistros();
                incidencia = "0";
                if(evaluar) {
                    FragmentEncuesta.ejecucion(context, id, posicion, "");
                }
            }
        });
        addView(dialogLayout);
    }

    private void eliminarRegistros() {
        eliminar = true;
        grupoGGu = new JSONArray();
        filaI = new ArrayList<>();
        rows=new ArrayList<>();
        filaFinal = new ArrayList<>();
        refrescaTabla();
    }

    private void popUp() {
        relativeLayout = new RelativeLayout(getContext());
        relativeLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        scrollPopUp = new ScrollView(getContext());
        scrollPopUp.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
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
                if (correcto){
                    armaRespuesta();
                    refrescaTabla();
                    dialog.dismiss();
                    if(evaluar) {
                        FragmentEncuesta.ejecucion(context, id, posicion, "");
                    }
                } else {
//                    Log.d("tabla", "cancel");
                }
            }
        });
        alertDialog.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                if(evaluar) {
                    FragmentEncuesta.ejecucion(context, id, posicion, "");
                }
                dialog.dismiss();
            }
        });
        dialog = alertDialog.create();
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
            }
        });
        dialog.show();
        ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE)
                .setEnabled(false);
    }

    private void guardar(PreguntaView preguntaView) {
        PreguntaView p = preguntaView;
        if (p.getCodResp().equals("-1") || p.getCodResp().equals("-1.0")) {
            Movil.vibrate();
            p.setFocus();
            return;
        } else {
            List<String> omision = Pregunta.getOmisionId(p.getId());
            if (!omision.contains(p.getCodResp())) {
                if (p instanceof Numero) {
                    try {
                        if ((Float.parseFloat(p.getCodResp()) < Integer.valueOf(minimo.get(indice)) && Integer.valueOf(minimo.get(indice)) > 0) || (Float.parseFloat(p.getCodResp()) > Integer.valueOf(maximo.get(indice)) && Integer.valueOf(maximo.get(indice)) > 0)) {
                            Movil.vibrate();
                            p.setFocus();
                            return;
                        }
                    } catch (Exception ex) {
                        Movil.vibrate();
                        p.setFocus();
                        return;
                    }
                } else if (p instanceof Abierta) {
                    if (p.getResp().length() < Integer.valueOf(minimo.get(indice)) && Integer.valueOf(minimo.get(indice)) > 0 || (p.getResp().length() > Integer.valueOf(maximo.get(indice)) && Integer.valueOf(maximo.get(indice)) > 0)) {
                        Movil.vibrate();
                        p.setFocus();
                        return;
                    }
                }
            }
        }
        try {
            unidad = new JSONObject();
            unidad.put("id_pregunta", Integer.valueOf(id_pregunta.get(indice)));
//            unidad.put("codigo", contador + "°" + p.getCodResp());
            unidad.put("codigo", p.getCodResp());
            unidad.put("respuestaOnline", p.getResp());
            unidad.put("visible", true);

            if(!Rpregs.contains(String.valueOf(id_pregunta.get(indice)))) {
                grupoUnidad.put(unidad);
                Rpregs.add(String.valueOf(id_pregunta.get(indice)));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        grupo.put(p.getId(), p.getResp());
        siguiente(saltos.get(indice), p.getCodResp());
        if(Integer.valueOf(id_pregunta.get(indice)) == 18721){
            incidencia = p.getCodResp();
        }else if(Integer.valueOf(id_pregunta.get(indice)) == 36321){
            incidenciaTemporal = p.getCodResp();
        }
    }

    private void siguiente(String saltos, String codResp) {
        int pregDestino = 0;
        Map<Integer, String> salto = new LinkedHashMap<>();
        if (saltos != null) {
            salto = Pregunta.getSaltos(saltos);
            for (Integer key : salto.keySet()) {
                String regla = salto.get(key);
                String[] a = regla.split("°");
                String rdestino = a[0];
                String rorden = a[1];
                String rregla = a[2];
                if (evaluar(rregla, id_pregunta.get(indice), codResp)) {
                    pregDestino = Integer.valueOf(rdestino);
                    break;
                }
            }
            if (pregDestino == 0) {
                pregDestino = Integer.valueOf(id_pregunta.get(indice + 1));
            }
        } else {
            pregDestino = Integer.valueOf(id_pregunta.get(indice + 1));
        }
        siguienteVisible(pregDestino);
        if (pregDestino == -1) {
            correcto = true;
            ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE)
                    .setEnabled(true);
        }
    }

    private void siguienteVisible(int destino) {
        for(int i = indice + 1 ; i<pregs.length; i++){
            pregs[i].clearFocus();
            if (destino == pregs[i].getId()){
                ocultar(activity);
                pregs[i].setVisibility(View.VISIBLE);
                separadors[i].setVisibility(View.VISIBLE);
                buttons[i].setVisibility(View.VISIBLE);
                pregs[i].requestFocus();

            } else {
                pregs[i].setVisibility(View.GONE);
                separadors[i].setVisibility(View.GONE);
                buttons[i].setVisibility(View.GONE);
            }
        }
    }


    private void refrescaTabla() {
        if(eliminar){
            removeView(dialogLayout);
            adicionarTabla(getClients());
        }else{

            filaFinal.add(fila.toArray(new String[0]));
            tableDynamic.addItems(fila.toArray(new String[0]));
            try {
                grupoGu.put("descripcion", grupoUnidad);
                grupoGGu.put(grupoGu);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    private void dibujarPregunta() {

//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    activity.runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
                            pregs = new PreguntaView[header.length];
                            separadors = new View[header.length];
                            buttons = new Button[header.length];
//                            int i = 0;
                            for(int i = 0; i < header.length; i++) {
                                String id = id_pregunta.get(i);
                                int idtp = Integer.valueOf(id_tipo_pregunta.get(i));
                                String p = pregunta.get(i);
                                String r = respuesta.get(i);
                                String min = minimo.get(i);
                                String max = maximo.get(i);
                                String reg = regla.get(i);
                                String sal = saltos.get(i);
                                String omi = omision.get(i);

                                Map<Integer, String> omisionC;
                                if (omi != null && !omi.equals("null")) {
                                    omisionC = Pregunta.getOmision_convert(omi);
                                } else {
                                    omisionC = null;
                                }
                                switch (idtp) {
                                    case 0: {
                                        int longitud = max.length();
                                        pregs[i] = new Abierta(context, i, Integer.valueOf(id), 0, "", p, 512, omisionC, null, true, true);
                                        break;
                                    }
                                    case 1: {
                                        ArrayList<Integer> filtro = null;
                                        Map<Integer, String> respuestas = Pregunta.getRespuestas(r);
                                        pregs[i] = new Cerrada(context, i, Integer.valueOf(id), 0, "", p, respuestas, "", omisionC, filtro, null, true);
                                        break;
                                    }
                                    case 2: {
                                        int longitud = max.length();
                                        pregs[i] = new Numero(context, i, Integer.valueOf(id), 0, "", p, longitud, omisionC, null, true);
                                        break;
                                    }
                                    case 26: {
                                        ArrayList<Integer> filtro = null;
                                        Map<Integer, String> respuestas = EncLog.getConsulta(idInformante, idPadre, r);
                                        pregs[i] = new Cerrada(context, i, Integer.valueOf(id), 0, "", p, respuestas, "", omisionC, filtro, null, true);
                                        break;
                                    }
                                }
                                pregs[i].setFocusable(true);
                                pregs[i].setFocusableInTouchMode(true);

                                popUp.addView(pregs[i]);

                                buttons[i] = new Button(context);

                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                    buttons[i].setBackground(getContext().getDrawable(R.drawable.ic_next));
                                }
//                                buttons[i].setText("Observación");
                                buttons[i].setTag(i);
                                buttons[i].setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        indice = (Integer) view.getTag();
                                        guardar(pregs[indice]);
//                        observationMessage("obs", "Introduzca observación", Html.fromHtml("Escriba la nota:"), pregs[indice].getObservacion(), false);
                                    }
                                });
//                                LayoutParams layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
//                                layoutParams.gravity = Gravity.RIGHT;
//                                buttons[i].setLayoutParams(layoutParams);

                                LinearLayout linerButtons = new LinearLayout(getContext());
                                linerButtons.setOrientation(LinearLayout.HORIZONTAL);
                                linerButtons.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                layoutParams.gravity = Gravity.RIGHT;
                                buttons[i].setLayoutParams(layoutParams);

                                linerButtons.addView(buttons[i]);
                                popUp.addView(linerButtons);

                                separadors[i] = new View(getContext());
                                separadors[i].setBackgroundColor(getResources().getColor(R.color.colorSecondary_text));
                                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                lp.height = 1;
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                    separadors[i].setElevation(100);
                                }
                                popUp.addView(separadors[i], lp);

                            }
                            OcultaPreguntas();
//                        }
//                    });
//                } catch (Exception e) {
//                    //print the error here
//                }
//            }
//        }).start();
    }

    private void OcultaPreguntas() {
        for(int op = 1; op < pregs.length; op++){
            pregs[op].setVisibility(View.GONE);
            buttons[op].setVisibility(View.GONE);
            separadors[op].setVisibility(View.GONE);
        }
    }

    public void armaRespuesta() {
        for (int i = 0; i < id_pregunta.size(); i++) {
            if(grupo.containsKey(Integer.valueOf(id_pregunta.get(i)))){
                fila.add(grupo.get(Integer.valueOf(id_pregunta.get(i))));
            } else {
                fila.add("");
            }
        }
    }

    private ArrayList<String[]> getClients(){
        ArrayList<String> dato = new ArrayList<>();
        for(int i = 0; i<header.length; i++){
            dato.add("");
        }
        rows.add(dato.toArray(new String[0]));
        return rows;
    }


            public static boolean evaluar(String regla, String idPregunta, String codResp){
                boolean result = false;
                String[] aux = null;
                List<String> allMatches = new ArrayList<String>();
                String nuevoValor = regla;

                Pattern p = Pattern.compile("\\$\\d+");
                Matcher m = p.matcher(regla);
                while (m.find()) {
                    String str = m.group();
                    allMatches.add(str);
                }
                for (String str : allMatches) {
                    String val = "0";

                    String element = str.replace("$", "");
                    if (element.equals(String.valueOf(idPregunta))) {
                        val = codResp;
                    } else {
                        for(PreguntaView resp : pregs){
                            if(resp.getId() == Integer.valueOf(element)){
                                val = resp.getCodResp();
                            }
                        }
                    }
                    nuevoValor = nuevoValor.replace(str, val);
                }
                //EVALUACION
                BigDecimal eval = null;
                Expression expression = new Expression(nuevoValor);
                eval = expression.eval();
                if (Integer.parseInt(String.valueOf(eval)) > 0) {
                    result = true;
                }
                return result;
            }



    @Override
    public String getCodResp() {
        String incidenciaFinal=obtenerIncidencia();
        if(filaFinal.size() > 0){
            if(incidenciaFinal.equals("0")&&cod.equals("zz")){
                return "0";
            }else
                if(!incidenciaFinal.equals("0")&&cod.equals("zz")){
                    return "1";
                }

                    /*else {
                return incidencia;
            }*/
            return String.valueOf(filaFinal.size());
        } else {
            return "-1";
        }
    }

    @Override
    public String getResp() {
//        Log.d("envia Respuesta", String.valueOf((grupoGGu)));
//        return String.valueOf((grupoGGu)) .replace("\"", "<>");
        return String.valueOf((grupoGGu)) ;
    }

    @Override
    public void setCodResp(String value) {
        incidencia= value;

    }

    @Override
    public void setResp(String value) {
        Log.d("recibe Respuesta", value);
//        String nvalue = value.replace("<>", "\"");
//        Log.d("recibe Respuesta", value);
        recuperaDatos(value);
        removeView(dialogLayout);
        filaFinal = filaInicial;
        adicionarTabla(filaFinal);

        try {
            grupoGGu = new JSONArray(value);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void recuperaDatos(String value){
        try {
            JSONArray jsonArrayDesc = new JSONArray(value);
//            JSONObject objetoJSONDesc = jsonArrayDesc.getJSONObject(0);
//            JSONArray jsonArrayI = new JSONArray(rdescripcion);
            for (int i = 0; i < jsonArrayDesc.length(); i++) {
                JSONObject objetoJSONDesc = jsonArrayDesc.getJSONObject(i);
                rdescripcion = objetoJSONDesc.getString("descripcion");
                JSONArray jsonArray = new JSONArray(rdescripcion);
                respGuardada = new LinkedHashMap<>();
                for (int j = 0; j < jsonArray.length(); j++) {
                    JSONObject objetoJSON = jsonArray.getJSONObject(j);
                    rid_pregunta.add(objetoJSON.getString("id_pregunta"));
                    rcodigo.add(objetoJSON.getString("codigo"));
                    rrespuestaOnline.add(objetoJSON.getString("respuestaOnline"));
                    rvisible.add(objetoJSON.getString("visible"));
//                    Log.d("objetos", objetoJSON.getInt("id_pregunta")+"|"+objetoJSON.getString("respuestaOnline"));
                    respGuardada.put(objetoJSON.getInt("id_pregunta"), objetoJSON.getString("respuestaOnline"));
                }
                reordenaDatos();
            }
//            Log.d("despues DE todo", String.valueOf(filaInicial.get(0)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void reordenaDatos(){
        filaI = new ArrayList<>();
        for (int i = 0; i < id_pregunta.size(); i++) {
//            Log.d("ANTES DE FILAI", String.valueOf(id_pregunta.get(i)));

            if(respGuardada.containsKey(Integer.valueOf(id_pregunta.get(i)))){
//                Log.d("para FILAI", respGuardada.get(Integer.valueOf(id_pregunta.get(i))));
                filaI.add(respGuardada.get(Integer.valueOf(id_pregunta.get(i))));
            } else {
                filaI.add("");
            }
        }
//        Log.d("despues DE FILAI", String.valueOf(filaI));
        filaInicial.add(filaI.toArray(new String[0]));
    }

//    private ArrayList<String[]>getClients(){
//        rows.add(new String[]{"1","Pedro","Lopez"});
//        rows.add(new String[]{"2","Luis","Sanchez"});
//        rows.add(new String[]{"3","Alberto","Mercado"});
//        rows.add(new String[]{"4","Jose","Busch"});
//        return rows;
//    }

    @Override
    public void setFocus() {
//        textbox.requestFocus();
    }

    @Override
    public void setEstado(Estado value) {
        super.setEstado(value);
    }

    @Override
    public void onClick(View v) {
        String respuesta = buttonsActive.get(v.getId());
        String[] a = respuesta.split("\\|");

        if(seleccion.equals(a[0])){
            textbox.setEnabled(true);
            textbox.setText("");
            seleccion = "";
            seleccionText = "";
            v.setBackgroundColor(getResources().getColor(R.color.colorBackgroundInactive));
            contenedor.setCounterMaxLength(maxLength);
            contenedor.setEndIconVisible(true);
            boton = null;
        } else if(idOpciones.contains(seleccion)){
            boton.setBackgroundColor(getResources().getColor(R.color.colorBackgroundInactive));
            textbox.setText(a[1]);
            seleccion = a[0];
            seleccionText = a[1];
            textbox.setEnabled(false);
            v.setBackgroundColor(getResources().getColor(R.color.colorBackgroundActive));
            contenedor.setCounterMaxLength(a[1].length());
            contenedor.setEndIconVisible(false);
            boton = v;
        } else {//SI NO EXISTIERA NADA en @seleccion
            textbox.setText(a[1]);
            seleccion = a[0];
            seleccionText = a[1];
            textbox.setEnabled(false);
            v.setBackgroundColor(getResources().getColor(R.color.colorBackgroundActive));
            contenedor.setCounterMaxLength(a[1].length());
            contenedor.setEndIconVisible(false);
            boton = v;
        }
    }
    private String obtenerIncidencia(){
        String ultimaIncidencia="0";
        JSONArray temDescripcion;
        try {
            Log.d("Respuestas ",String.valueOf(grupoGGu));

            temDescripcion= grupoGGu.length()>0?grupoGGu.getJSONObject(grupoGGu.length()-1).getJSONArray("descripcion"):new JSONArray();
            ultimaIncidencia=temDescripcion.length()==0?"0":temDescripcion.getJSONObject(temDescripcion.length()-1).getInt("id_pregunta")!=18721?"0":temDescripcion.getJSONObject(temDescripcion.length()-1).getString("codigo");

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return ultimaIncidencia;
    }


}
