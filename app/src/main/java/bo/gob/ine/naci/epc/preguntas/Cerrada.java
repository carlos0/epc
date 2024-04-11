package bo.gob.ine.naci.epc.preguntas;

import android.content.Context;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import bo.gob.ine.naci.epc.R;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bo.gob.ine.naci.epc.entidades.Estado;
import bo.gob.ine.naci.epc.entidades.Pregunta;
import bo.gob.ine.naci.epc.fragments.FragmentEncuesta;
import bo.gob.ine.naci.epc.fragments.FragmentInicial;
import bo.gob.ine.naci.epc.herramientas.Parametros;

public class Cerrada extends PreguntaView  implements View.OnClickListener {
    protected final Map<Integer, String> opciones;

    protected RadioGroup radioGroup;
    protected ArrayList<Integer> filtro;
    protected EditText editText;
    protected String codEsp;

    protected Context context;
    protected int posicion;
    protected int idSeccion;
    protected String cod;
    protected Boolean evaluar;

    protected Button resp;
    protected Map<Integer, String> buttonsActive;
    protected List<String> btnOpciones = new ArrayList<String>();
    protected List<String> idOpciones = new ArrayList<String>();
    protected String seleccion = "";
    protected String seleccionText = "";
    protected View boton;
    protected Map<String, View> guardaBotones = new HashMap<> ();
    protected Map<String,Integer> seleccionado = new HashMap<>();


    public Cerrada(final Context context, final int posicion, final int id, int idSeccion, String cod, String preg, final Map<Integer, String> opciones, String codEsp, final Map<Integer, String> buttonsActive, ArrayList<Integer> filtro, String ayuda, final Boolean evaluar) {
        super(context, id, idSeccion, cod, preg, ayuda);
        this.opciones = opciones;
        this.codEsp = codEsp;
        this.buttonsActive = buttonsActive;
        this.filtro = filtro;

        this.cod = cod;
        this.context = context;
        this.posicion = posicion;
        this.idSeccion = idSeccion;
        this.evaluar = evaluar;

        radioGroup = new RadioGroup(context);
        radioGroup.setOrientation(RadioGroup.VERTICAL);
        radioGroup.setFocusable(true);

        // CREAMOS EL GRUPO DE RADIO BUTTONS
        RadioButton rb;
        for (Integer key : opciones.keySet()) {
            rb = new RadioButton(context);
            radioGroup.addView(rb);
            String opcion = opciones.get(key);
            String[] a = opcion.split("\\|");
            rb.setText(Html.fromHtml(a[1]));
            rb.setTextSize(Parametros.FONT_RESP);
            rb.setId(key);
            rb.setFocusable(true);
            seleccionado.put(a[0],key);
            if ( a[0].equals(codEsp) ) {
                editText = new EditText(context);
                editText.setSingleLine();
                editText.setTextSize(Parametros.FONT_RESP);
                editText.setTextSize(Parametros.FONT_RESP);
            }
            if (filtro != null) {
                if (!filtro.contains(Integer.parseInt(a[0]))) {
                    rb.setEnabled(false);
                }
            }
            rb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(id== Pregunta.getIdPregunta(Parametros.CODIGO_PREGUNTA_USO_DE_LA_VIVIENDA)) {
                        FragmentInicial.ejecucion(id, String.valueOf(v.getId()));
                    }
                    if(evaluar) {
                        FragmentEncuesta.ejecucion(context, id, posicion, "");
                    }
                    //refresh adapter from Manzanas
                    if(id== Pregunta.getIdPregunta(Parametros.CODIGO_PREGUNTA_UPM_SELECCIONADA)&&!getCodResp().equals("-1")){
                        FragmentInicial.refrescaManza(getCodResp());
                    }
                    if(id==15357&&getCodResp().equals("1")){
                        FragmentInicial.agregarObservacionEntrevistaCompleta();
                    }
                    //variable para descartar opcion de manzana seleccionada en la union
                    if(id== Pregunta.getIdPregunta(Parametros.CODIGO_PREGUNTA_MANZANA_COMUNIDAD)) {
                        FragmentInicial.setInvalidOption(getResp());
                    }
                }
            });
        }

//        if (opciones.size() == 1) {
//            ((RadioButton)radioGroup.getChildAt(0)).setChecked(true);
//        }
        addView(radioGroup);
        if (editText != null) {
            addView(editText);
        }

        LinearLayout buttons = new LinearLayout(context);
        buttons.setOrientation(LinearLayout.HORIZONTAL);
        buttons.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
        layoutParams.weight = 1;
        if (buttonsActive != null) {
            for (Integer key : buttonsActive.keySet()) {
                resp = new Button(context);
                resp.setLayoutParams(layoutParams);
                buttons.addView(resp);
                String opcion = buttonsActive.get(key);
                String[] a = opcion.split("\\|");
                resp.setText(a[1]);
                resp.setTextSize(Parametros.FONT_RESP);
                resp.setId(key);
                resp.setOnClickListener(this);
                btnOpciones.add(a[1]);
                idOpciones.add(a[0]);
                guardaBotones.put(a[0],resp);
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
//        if (radioGroup.getChildCount() == 1&& flag) {
//
//            RadioButton child =  ((RadioButton)radioGroup.getChildAt(0));
//            child.setChecked(true);
//            radioGroup.re
//        }
    }

    @Override
    public String getCodResp() {
        if(id == 18610 && seleccion.equals("0")) {
            return "0";
        } else {
            if (idOpciones.contains(seleccion)) {
                return seleccion;
            } else if (radioGroup.getCheckedRadioButtonId() == -1) {
                radioGroup.clearCheck();
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
    }

    @Override
    public void setCodResp(String value) {
        if(id == 18610 && value.equals("0")) {
            seleccion = "0";
        } else {
            View vRecupera;
            if (value.equals("")) {
                seleccion = "";
                radioGroup.clearCheck();
            } else if (idOpciones.contains(value)) {
                seleccion = value;
                vRecupera = guardaBotones.get(value);
                vRecupera.setBackgroundColor(getResources().getColor(R.color.colorBackgroundActive));
                setEnabledRB(false);
            } else {
                boolean flag = false;
                for (int i = 0; i < radioGroup.getChildCount(); i++) {
                    RadioButton child = (RadioButton) radioGroup.getChildAt(i);
//                Log.d("Cerrada value",value);
//                Log.d("Cerrada child",child.toString());
//                Log.d("Cerrada child",cod);
                    if (seleccionado.get(value) != null && value != null && child.getId() == seleccionado.get(value)) {

//                    if (child.isEnabled()) {
                        child.setChecked(true);
                        flag = true;
                        seleccion = value;
//                    }
                        break;
                    }
                }
                if (!flag) {
                    for (String opc : idOpciones) {
                        vRecupera = guardaBotones.get(opc);
                        vRecupera.setBackgroundColor(getResources().getColor(R.color.colorBackgroundInactive));
                    }
                }

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
        if(id == 18610 && seleccion.equals("0")) {
            return "0";
        } else {
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
    }

    @Override
    public void setFocus() {
//        radioGroup.getChildAt(radioGroup.getChildCount() - 1).requestFocus();
        int radioButtonCount = radioGroup.getChildCount();
        if (radioButtonCount > 0) {
            View lastRadioButton = radioGroup.getChildAt(radioButtonCount - 1);
            lastRadioButton.requestFocus();
        }
    }

    @Override
    public void setEstado(Estado value) {
        super.setEstado(value);
    }

    @Override
    public void onClick(View v) {
//        Log.d("Controls de radio: ",""+buttonsActive.get(v.getId()));
        int al = v.getId();
        String respuesta = buttonsActive.get(v.getId());
        if(respuesta != null) {
            String[] a = respuesta.split("\\|");
            if (seleccion.equals(a[0])) {
                seleccion = "";
                seleccionText = "";
                v.setBackgroundColor(getResources().getColor(R.color.colorBackgroundInactive));
                setEnabledRB(true);
                boton = null;
            } else if (idOpciones.contains(seleccion)) {
                boton.setBackgroundColor(getResources().getColor(R.color.colorBackgroundInactive));
                seleccion = a[0];
                seleccionText = a[1];
                v.setBackgroundColor(getResources().getColor(R.color.colorBackgroundActive));
                setEnabledRB(false);
                boton = v;
            } else {//SI NO EXISTIERA NADA en @seleccion
                seleccion = a[0];
                seleccionText = a[1];
                v.setBackgroundColor(getResources().getColor(R.color.colorBackgroundActive));
                setEnabledRB(false);
                boton = v;
            }
        }
        if(evaluar) {
            FragmentEncuesta.ejecucion(context, id, posicion, "");
        }
    }

}