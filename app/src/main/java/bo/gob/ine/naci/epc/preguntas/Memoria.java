package bo.gob.ine.naci.epc.preguntas;

import android.content.Context;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;

import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bo.gob.ine.naci.epc.R;
import bo.gob.ine.naci.epc.entidades.Estado;
import bo.gob.ine.naci.epc.fragments.FragmentEncuesta;
import bo.gob.ine.naci.epc.herramientas.Movil;
import bo.gob.ine.naci.epc.herramientas.Parametros;

/**
 * Created by ainch on 11/03/2016.
 */
public class Memoria extends PreguntaView  implements View.OnClickListener {

    protected TextInputLayout contenedor;
    protected AutoCompleteTextView textbox;

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

    public Memoria(final Context context, final int posicion, final int id, int idSeccion, String cod, String preg, int maxLength, final Map<Integer, String> buttonsActive, String[] opciones, String ayuda, final Boolean evaluar) {
        super(context, id, idSeccion, cod, preg, ayuda);
        this.buttonsActive = buttonsActive;

        this.cod = cod;
        this.context = context;
        this.posicion = posicion;
        this.idSeccion = idSeccion;
        this.evaluar = evaluar;

        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, opciones);

        contenedor = new TextInputLayout(context);
        contenedor.setHint("Respuesta");
        contenedor.setBoxBackgroundColor(getResources().getColor(R.color.color_list));
        contenedor.setBoxBackgroundMode(TextInputLayout.BOX_BACKGROUND_OUTLINE);
        contenedor.setBoxStrokeColor(getResources().getColor(R.color.colorPrimaryDark));
        contenedor.setErrorEnabled(true);
        contenedor.setEndIconMode(TextInputLayout.END_ICON_CLEAR_TEXT);

        textbox = new AutoCompleteTextView(context);
        textbox.setSingleLine();
        if (maxLength > 0) {
            InputFilter[] fArray = new InputFilter[1];
            fArray[0] = new InputFilter.LengthFilter(maxLength);
            textbox.setFilters(fArray);
        }
        /*adapter.refreshData(valores);*/
        /*adapter.getFilter().filter("fdsa");*/
        textbox.setThreshold(1);
        textbox.setTextSize(Parametros.FONT_RESP);
        textbox.setAdapter(adapter);
        contenedor.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(evaluar) {
                    FragmentEncuesta.ejecucion(context, id, posicion, "");
                }
                return false;
            }
        });
        textbox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (textbox.getText().toString().trim().isEmpty()) {
                    contenedor.setError("Debe llenar esta respuesta");
                    Movil.vibrate();
                } else{
                    contenedor.setErrorEnabled(false);
                }
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        contenedor.addView(textbox);
        addView(contenedor);

        LinearLayout buttons = new LinearLayout(context);
        buttons.setOrientation(LinearLayout.HORIZONTAL);
        buttons.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
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
        textbox.requestFocus();
    }

    @Override
    public String getCodResp() {
        if (idOpciones.contains(seleccion)) {
            return seleccion;
        } else if (textbox.getText().toString().trim().length() > 0) {
            return String.valueOf(textbox.getText().toString().trim().length());
        } else {
            return "-1";
        }
    }
    @Override
    public void setCodResp(String value) {
        View vRecupera;
        if(idOpciones.contains(value)){
            seleccion = value;
            vRecupera = guardaBotones.get(value);
            vRecupera.setBackgroundColor(getResources().getColor(R.color.colorBackgroundActive));
            textbox.setEnabled(false);
            contenedor.setEndIconVisible(false);
        } else {
            textbox.setEnabled(true);
        }
    }

    @Override
    public String getResp() {
        return textbox.getText().toString();
    }

    @Override
    public void setResp(String value) {
        textbox.setText(value);
    }

    @Override
    public void setFocus() {
        textbox.requestFocus();
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
            contenedor.setEndIconVisible(true);
            boton = null;
        } else if(idOpciones.contains(seleccion)){
            boton.setBackgroundColor(getResources().getColor(R.color.colorBackgroundInactive));
            textbox.setText(a[1]);
            seleccion = a[0];
            seleccionText = a[1];
            textbox.setEnabled(false);
            v.setBackgroundColor(getResources().getColor(R.color.colorBackgroundActive));
            contenedor.setEndIconVisible(false);
            boton = v;
        } else {//SI NO EXISTIERA NADA en @seleccion
            textbox.setText(a[1]);
            seleccion = a[0];
            seleccionText = a[1];
            textbox.setEnabled(false);
            v.setBackgroundColor(getResources().getColor(R.color.colorBackgroundActive));
            contenedor.setEndIconVisible(false);
            boton = v;
        }
        if(evaluar) {
            FragmentEncuesta.ejecucion(context, id, posicion, "");
        }
    }

    public void setVisible (View v, boolean sw) {
        if(sw) {
            v.setVisibility(VISIBLE);
        } else {
            v.setVisibility(INVISIBLE);
        }
    }
}
