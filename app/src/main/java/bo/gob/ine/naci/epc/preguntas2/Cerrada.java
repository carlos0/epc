package bo.gob.ine.naci.epc.preguntas2;

import android.content.Context;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ToggleButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bo.gob.ine.naci.epc.R;
import bo.gob.ine.naci.epc.entidades.Estado;
import bo.gob.ine.naci.epc.herramientas.Parametros;
import bo.gob.ine.naci.epc.fragments.FragmentEncuesta;

public class Cerrada extends PreguntaView  implements View.OnClickListener {
    protected Map<Integer, String> opciones;

    protected RadioGroup radioGroup;
    protected ArrayList<Integer> filtro;
    protected EditText editText;
    protected String codEsp;

    protected Context context;
    protected int posicion;
    protected int idSeccion;
    protected String cod;
    protected Boolean evaluar;

    protected ToggleButton resp;
    protected Map<Integer, String> buttonsActive;
    protected List<String> btnOpciones = new ArrayList<String>();
    protected List<String> idOpciones = new ArrayList<String>();
    protected String seleccion = "";
    protected String seleccionText = "";
    protected ToggleButton boton;
    protected Map<String, View> guardaBotones = new HashMap<> ();
    protected Map<Integer, String> guardaIdBotones = new HashMap<>();
    protected Map<String,Integer> seleccionado = new HashMap<>();
    protected List<String> rbOpciones = new ArrayList<String>();


    public Cerrada(final Context context, final int posicion, final int id, int idSeccion, String cod, String preg, Map<Integer, String> opciones, String codEsp, final Map<Integer, String> buttonsActive, ArrayList<Integer> filtro, String ayuda, final Boolean evaluar, final Boolean mostrarSeccion, String observacion) {
        super(context, id, idSeccion, cod, preg, ayuda, mostrarSeccion, observacion);
        this.opciones = opciones;
        this.codEsp = String.valueOf(-1);
        codEsp = String.valueOf(-1);
        this.buttonsActive = buttonsActive;
        this.filtro = filtro;

        this.cod = cod;
        this.context = context;
        this.posicion = posicion;
        this.idSeccion = idSeccion;
        this.evaluar = evaluar;

        radioGroup = new RadioGroup(context);
        radioGroup.setOrientation(VERTICAL);
//        radioGroup.setGravity(Gravity.START);
        radioGroup.setFocusable(true);
        radioGroup.setFocusableInTouchMode(true);

        // CREAMOS EL GRUPO DE RADIO BUTTONS
        RadioButton rb;
        for (Integer key : opciones.keySet()) {
            rb = new RadioButton(context);
            radioGroup.addView(rb);
            String opcion = opciones.get(key);
            Log.d("OPCION", opcion);
            String[] a = opcion.split("\\|");
            rb.setText(Html.fromHtml(a[1]));
            rb.setTextSize(Parametros.FONT_RESP);
            rb.setId(key);
            rb.setGravity(Gravity.AXIS_PULL_BEFORE);

            seleccionado.put(a[0], key);
            if (a[0].equals(codEsp)) {
                editText = new EditText(context);
                editText.setSingleLine();
                editText.setTextSize(Parametros.FONT_RESP);
            }
            if (filtro != null) {
                if (!filtro.contains(Integer.parseInt(a[0]))) {
                    rb.setEnabled(false);
                }
            }
            rb.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {

//                    ((RadioButton) v).setChecked(true);
                    RadioButton child = (RadioButton) v;
                    child.setChecked(true);

                    if (evaluar) {
                        FragmentEncuesta.actualiza(id);
//                        FragmentEncuesta.ejecucion(context, id, posicion, "");
                    }
                }
            });

//            if(Usuario.getRol() == Parametros.SUPERVISOR){
//                if(idSeccion != 449){
//                    rb.setClickable(false);
//                }
//            }
        }

        if (opciones.size() == 1) {
            ((RadioButton) radioGroup.getChildAt(0)).setChecked(true);
        }

        if (idSeccion == 166) {
//            LinearLayout respuesta = new LinearLayout(context);
//            LayoutParams layoutParams_respuesta = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
//            layoutParams_respuesta.weight = 1;
//            respuesta.setLayoutParams(layoutParams_respuesta);
            respuesta.addView(radioGroup);
//            addView(respuesta);
        } else {
            addView(radioGroup);
        }


        if (editText != null) {
            addView(editText);
        }

        LinearLayout buttons = new LinearLayout(context);
        buttons.setOrientation(HORIZONTAL);
        buttons.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
        layoutParams.weight = 1;
        if (buttonsActive != null) {
            for (Integer key : buttonsActive.keySet()) {
                resp = new ToggleButton(context);
                resp.setTextColor(getContext().getResources().getColor(R.color.colorPrimary));
                resp.setLayoutParams(layoutParams);
                buttons.addView(resp);
                String opcion = buttonsActive.get(key);
                String[] a = opcion.split("\\|");
                resp.setText(a[1]);
                resp.setTextOff(a[1]);
                resp.setTextOn(a[1]);
                resp.setTextSize(Parametros.FONT_RESP);
                resp.setId(key);
                resp.setOnClickListener(this);
                btnOpciones.add(a[1]);
                idOpciones.add(a[0]);
                guardaBotones.put(a[0], resp);
                guardaIdBotones.put(key, a[0]);
//                if(Usuario.getRol() == Parametros.SUPERVISOR){
//                    if(idSeccion != 449){
//                        resp.setFocusable(false);
//                        resp.setEnabled(false);
//                        resp.setCursorVisible(false);
//                        resp.setKeyListener(null);
//                    }
//                }
            }
            addView(buttons);
        }
    }


    public void setEnabledRB(boolean flag) {
        for (int i = 0; i < radioGroup.getChildCount(); i++) {
            RadioButton child = (RadioButton)radioGroup.getChildAt(i);
            // CONTROL QUE UNICAMENTE FUNCIONARA PARA LA PREGUNTA BA03_09
            if (filtro == null) {
                child.setEnabled(flag);
            }
            if (child.isChecked() && !flag) {
                child.setChecked(false);
            }
        }
    }

    @Override
    public String getCodResp() {
        if (idOpciones.contains(seleccion)) {
            return seleccion;
        } else if (radioGroup.getCheckedRadioButtonId() == -1||(editText!=null&&editText.getText().toString().trim().length()==0)) {
            return "-1";
        } else {
            String codigo = opciones.get(radioGroup.getCheckedRadioButtonId()).split("\\|")[0];
            if (codigo.equals(codEsp)) {
                if (editText.getText().toString().trim().length() == 0) {
                    return "-1";
                } else {
                    return codigo.trim();
                }
            } else {
                return codigo.trim();
            }
        }
    }

    @Override
    public void setCodResp(String value) {
        ToggleButton botonActive;
        if(idOpciones.contains(value)){
            seleccion = value;
            botonActive = (ToggleButton) guardaBotones.get(value);
            botonActive.setChecked(true);
            botonActive.setTextColor(getContext().getResources().getColor(R.color.colorAccent));
            boton = botonActive;
            setEnabledRB(false);
        } else {
            for (int i = 0; i < radioGroup.getChildCount(); i++) {
                RadioButton child = (RadioButton) radioGroup.getChildAt(i);
                if (child.getId() == seleccionado.get(value)) {
                    if (child.isEnabled()) {
                        child.setChecked(true);
                    }
                    break;
                }
            }
            for(String opc : idOpciones) {
                botonActive = (ToggleButton) guardaBotones.get(opc);
                botonActive.setChecked(false);
                botonActive.setTextColor(getContext().getResources().getColor(R.color.colorPrimary));
            }
        }
    }


    @Override
    public void setResp(String value) {
        if (editText != null) {
            String codigo = opciones.get(radioGroup.getCheckedRadioButtonId()).split("\\|")[0];
            if (codigo.equals(codEsp)) {
                editText.setText(value);
            }
        }
    }

    @Override
    public String getResp() {
        if (idOpciones.contains(seleccion)) {
            return seleccion;
        } else if (radioGroup.getCheckedRadioButtonId() == -1) {
            return "";
        } else {
            String[] sel = opciones.get(radioGroup.getCheckedRadioButtonId()).split("\\|");
            if (sel[0].equals(codEsp)) {
                return editText.getText().toString();
            } else {
                return sel[1];
            }
        }
    }

//    @Override
//    public void setFocus() {
//        radioGroup.requestFocus();
//    }

    @Override
    public void setEstado(Estado value) {
        super.setEstado(value);
    }

    @Override
    public void onClick(View v) {
        int al = v.getId();
        String respuesta = buttonsActive.get(v.getId());

        String botonPresionado = guardaIdBotones.get(v.getId());
        ToggleButton botonActive = (ToggleButton) guardaBotones.get(botonPresionado);

        if(respuesta != null) {
            String[] a = respuesta.split("\\|");
            if (seleccion.equals(a[0])) {
                seleccion = "";
                seleccionText = "";
                botonActive.setChecked(false);
                botonActive.setTextColor(getContext().getResources().getColor(R.color.colorPrimary));
                setEnabledRB(true);
                boton = null;
                if(evaluar) {
                    FragmentEncuesta.actualiza(id);
                }
            } else if (idOpciones.contains(seleccion)) {
                boton.setChecked(false);
                boton.setTextColor(getContext().getResources().getColor(R.color.colorPrimary));
                seleccion = a[0];
                seleccionText = a[1];
                botonActive.setChecked(true);
                botonActive.setTextColor(getContext().getResources().getColor(R.color.colorAccent));
                setEnabledRB(false);
                boton = botonActive;
                if(evaluar) {
                    FragmentEncuesta.actualiza(id);
                }
            } else {//SI NO EXISTIERA NADA en @seleccion
                seleccion = a[0];
                seleccionText = a[1];
                botonActive.setChecked(true);
                botonActive.setTextColor(getContext().getResources().getColor(R.color.colorAccent));
                setEnabledRB(false);
                boton = botonActive;
                if(evaluar) {
                    FragmentEncuesta.actualiza(id);
                }
            }
        }
        if(evaluar) {
//            FragmentEncuesta.ejecucion(context, id, posicion, "");
        }
    }

}
//
//import android.content.Context;
//import android.text.Html;
//import android.util.Log;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.LinearLayout;
//import android.widget.RadioButton;
//import android.widget.RadioGroup;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import bo.gob.ine.naci.ece2.R;
//import bo.gob.ine.naci.ece2.entidades.Estado;
//import bo.gob.ine.naci.ece2.fragments.FragmentEncuesta;
//import bo.gob.ine.naci.ece2.herramientas.Parametros;
//
//public class Cerrada extends PreguntaView implements View.OnClickListener {
//    protected final Map<Integer, String> opciones;
//
//    protected RadioGroup radioGroup;
//    protected ArrayList<Integer> filtro;
//    protected EditText editText;
//    protected String codEsp;
//
//    protected Context context;
//    protected int posicion;
//    protected int idSeccion;
//    protected String cod;
//    protected Boolean evaluar;
//
//    protected Button resp;
//    protected Map<Integer, String> buttonsActive;
//    protected List<String> btnOpciones = new ArrayList<String>();
//    protected List<String> idOpciones = new ArrayList<String>();
//    protected String seleccion = "";
//    protected String seleccionText = "";
//    protected View boton;
//    protected Map<String, View> guardaBotones = new HashMap<> ();
//    protected Map<String,Integer> seleccionado = new HashMap<>();
//
//
//    public Cerrada(final Context context, final int posicion, final int id, int idSeccion, String cod, String preg, final Map<Integer, String> opciones, String codEsp, final Map<Integer, String> buttonsActive, ArrayList<Integer> filtro, String ayuda, final Boolean evaluar, final Boolean mostrarSeccion, String observacion) {
//        super(context, id, idSeccion, cod, preg, ayuda, mostrarSeccion, observacion);
//        this.opciones = opciones;
//        this.codEsp = codEsp;
//        this.buttonsActive = buttonsActive;
//        this.filtro = filtro;
//
//        this.cod = cod;
//        this.context = context;
//        this.posicion = posicion;
//        this.idSeccion = idSeccion;
//        this.evaluar = evaluar;
//
//        radioGroup = new RadioGroup(context);
//        radioGroup.setOrientation(RadioGroup.VERTICAL);
//
//        // CREAMOS EL GRUPO DE RADIO BUTTONS
//        RadioButton rb;
//        for (Integer key : opciones.keySet()) {
//            rb = new RadioButton(context);
//            radioGroup.addView(rb);
//            String opcion = opciones.get(key);
//            String[] a = opcion.split("\\|");
//            rb.setText(Html.fromHtml(a[1]));
//            rb.setTextSize(Parametros.FONT_RESP);
//            rb.setId(key);
//            seleccionado.put(a[0],key);
//            if ( a[0].equals(codEsp) ) {
//                editText = new EditText(context);
//                editText.setSingleLine();
//                editText.setTextSize(Parametros.FONT_RESP);
//                editText.setTextSize(Parametros.FONT_RESP);
//            }
//            if (filtro != null) {
//                if (!filtro.contains(Integer.parseInt(a[0]))) {
//                    rb.setEnabled(false);
//                }
//            }
//            rb.setOnClickListener(new OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    RadioButton child = (RadioButton) v;
//                    child.setChecked(true);
//                    if(evaluar) {
//                        FragmentEncuesta.actualiza(id);
//                    }
//                }
//            });
//        }
//
//        if (opciones.size() == 1) {
//            ((RadioButton)radioGroup.getChildAt(0)).setChecked(true);
//        }
//        addView(radioGroup);
//        if (editText != null) {
//            addView(editText);
//        }
//
//        LinearLayout buttons = new LinearLayout(context);
//        buttons.setOrientation(LinearLayout.HORIZONTAL);
//        buttons.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
//        LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
//        layoutParams.weight = 1;
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
//    }
//
//
//    public void setEnabledRB(boolean flag) {
//        for (int i = 0; i < radioGroup.getChildCount(); i++) {
//            RadioButton child = (RadioButton)radioGroup.getChildAt(i);
//            // CONTROL QUE UNICAMENTE FUNCIONARA PARA LA PREGUNTA BA03_09
//            if (filtro == null) {
//                child.setEnabled(flag);
//            }
//            if (child.isChecked() && !flag) {
//                child.setChecked(false);
//            }
//
//        }
////        if (radioGroup.getChildCount() == 1&& flag) {
////
////            RadioButton child =  ((RadioButton)radioGroup.getChildAt(0));
////            child.setChecked(true);
////            radioGroup.re
////        }
//    }
//
//    @Override
//    public String getCodResp() {
//        if (idOpciones.contains(seleccion)) {
//            return seleccion;
//        } else if (radioGroup.getCheckedRadioButtonId() == -1) {
//            return "-1";
//        } else {
//            String codigo = opciones.get(radioGroup.getCheckedRadioButtonId()).split("\\|")[0];
//            if (codigo.equals(codEsp)) {
//                if (editText.getText().toString().trim().length() == 0) {
//                    return "-1";
//                } else {
//                    return codigo.trim();
//                }
//            } else {
//                return codigo.trim();
//            }
//        }
//    }
//
//    @Override
//    public void setCodResp(String value) {
//        View vRecupera;
//        if(idOpciones.contains(value)){
//            seleccion = value;
//            vRecupera = guardaBotones.get(value);
//            vRecupera.setBackgroundColor(getResources().getColor(R.color.colorBackgroundActive));
//            setEnabledRB(false);
//        } else {
//            for (int i = 0; i < radioGroup.getChildCount(); i++) {
//                RadioButton child = (RadioButton) radioGroup.getChildAt(i);
//                Log.d("Cerrada value",value);
//                Log.d("Cerrada child",child.toString());
//                Log.d("Cerrada child",cod);
//                if (value !=null && child.getId() == seleccionado.get(value)) {
//                    if (child.isEnabled()) {
//                        child.setChecked(true);
//                    }
//                    break;
//                }
//            }
//            for(String opc : idOpciones) {
//                vRecupera = guardaBotones.get(opc);
//                vRecupera.setBackgroundColor(getResources().getColor(R.color.colorBackgroundInactive));
//            }
//        }
//    }
//
//
//    @Override
//    public void setResp(String value) {
//        if (editText != null) {
//            String codigo = opciones.get(radioGroup.getCheckedRadioButtonId()).split("\\|")[0];
//            if (codigo.equals(codEsp)) {
//                editText.setText(value);
//            }
//        }
//    }
//
//    @Override
//    public String getResp() {
//        if (idOpciones.contains(seleccion)) {
//            return seleccion;
//        } else if (radioGroup.getCheckedRadioButtonId() == -1) {
//            return "";
//        } else {
//            String[] sel = opciones.get(radioGroup.getCheckedRadioButtonId()).split("\\|");
//            if (sel[0].equals(codEsp)) {
//                return editText.getText().toString();
//            } else {
//                return sel[1];
//            }
//        }
//    }
//
//    @Override
//    public void setFocus() {
//    }
//
//    @Override
//    public void setEstado(Estado value) {
//        super.setEstado(value);
//    }
//
//    @Override
//    public void onClick(View v) {
//        Log.d("Controls de radio: ",""+buttonsActive.get(v.getId()));
//        int al = v.getId();
//        String respuesta = buttonsActive.get(v.getId());
//        if(respuesta != null) {
//            String[] a = respuesta.split("\\|");
//            if (seleccion.equals(a[0])) {
//                seleccion = "";
//                seleccionText = "";
//                v.setBackgroundColor(getResources().getColor(R.color.colorBackgroundInactive));
//                setEnabledRB(true);
//                boton = null;
//                if(evaluar) {
//                    FragmentEncuesta.actualiza(id);
//                }
//            } else if (idOpciones.contains(seleccion)) {
//                boton.setBackgroundColor(getResources().getColor(R.color.colorBackgroundInactive));
//                seleccion = a[0];
//                seleccionText = a[1];
//                v.setBackgroundColor(getResources().getColor(R.color.colorBackgroundActive));
//                setEnabledRB(false);
//                boton = v;
//                if(evaluar) {
//                    FragmentEncuesta.actualiza(id);
//                }
//            } else {//SI NO EXISTIERA NADA en @seleccion
//                seleccion = a[0];
//                seleccionText = a[1];
//                v.setBackgroundColor(getResources().getColor(R.color.colorBackgroundActive));
//                setEnabledRB(false);
//                boton = v;
//                if(evaluar) {
//                    FragmentEncuesta.actualiza(id);
//                }
//            }
//        }
////        if(evaluar) {
////            FragmentEncuesta.ejecucion(context, id, posicion, "");
////        }
//    }
//
//}