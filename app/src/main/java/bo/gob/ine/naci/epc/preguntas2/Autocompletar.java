package bo.gob.ine.naci.epc.preguntas2;

import android.content.Context;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.ToggleButton;

import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bo.gob.ine.naci.epc.R;
import bo.gob.ine.naci.epc.entidades.Catalogo;
import bo.gob.ine.naci.epc.entidades.Estado;
import bo.gob.ine.naci.epc.fragments.FragmentEncuesta;
import bo.gob.ine.naci.epc.herramientas.Parametros;


public class Autocompletar extends PreguntaView implements View.OnClickListener {

    protected TextInputLayout contenedor;
    protected AutoCompleteTextView textbox;
//    protected TextInputEditText valor;

    protected String catalogo;
    protected ToggleButton resp;
    protected Map<Integer, String> buttonsActive;
    protected String codigo;
    protected String valor;

    protected Context context;
    protected int posicion;
    protected int idSeccion;
    protected String cod;
    protected Boolean evaluar;

    protected ArrayList<Map<String, Object>> valores = new ArrayList<>();
    protected Map<String,Object> elementos = null;
    protected Map<String,Object> opcionesRespuesta = new HashMap<>();
    protected List<String> idItems = new ArrayList<String>();
    protected List<String> items = new ArrayList<String>();
    protected Map<String, View> guardaBotones = new HashMap<>();
    protected Map<Integer, String> guardaIdBotones = new HashMap<>();

    protected List<String> btnOpciones = new ArrayList<String>();
    protected List<String> idOpciones = new ArrayList<String>();
    protected String seleccion = "";
    protected String seleccionText = "";
    protected ToggleButton boton;
    protected Boolean llenado = true;
    protected LinearLayout buttons;

    public Autocompletar(final Context context, final int posicion, final int id, int idSeccion, final String cod, String preg, int maxLength, String catalogo, final Map<Integer, String> buttonsActive, String ayuda, boolean multiple, final Boolean evaluar, final Boolean mostrarSeccion, String observacion) {
        super(context, id, idSeccion, cod, preg, ayuda, mostrarSeccion, observacion);
        this.catalogo = catalogo;
        this.buttonsActive = buttonsActive;
        this.codigo = "-1";
        this.valor = "";

        this.cod = cod;
        this.context = context;
        this.posicion = posicion;
        this.idSeccion = idSeccion;
        this.evaluar = evaluar;

        contenedor = new TextInputLayout(context);
        contenedor.setHint(ayuda==null || ayuda.equals("")?"respuesta":ayuda);
        contenedor.setBoxBackgroundColor(getResources().getColor(R.color.color_list));
        contenedor.setBoxBackgroundMode(TextInputLayout.BOX_BACKGROUND_OUTLINE);
        contenedor.setBoxStrokeColor(getResources().getColor(R.color.colorPrimaryDark));
        contenedor.setErrorEnabled(true);
        contenedor.setEndIconMode(TextInputLayout.END_ICON_CLEAR_TEXT);

        textbox = new AutoCompleteTextView(context);
        textbox.setThreshold(1);
        textbox.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);
        cargarListado();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_dropdown_item_1line, items);
        textbox.setAdapter(adapter);
        textbox.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
//                if(evaluar) {
//                    FragmentEncuesta.actualiza(id);
//                }
                return false;
            }
        });
        textbox.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View arg1, int pos, long ids) {
                if(evaluar) {
                    FragmentEncuesta.actualiza(id);
                }
            }
        });
        textbox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (textbox.getText().toString().trim().isEmpty()) {
                    valor = "";
                    codigo = "-1";
                    contenedor.setError("Debe llenar esta respuesta");
//                    Movil.vibrate();
                } else if (!items.contains(s.toString())) {
                    valor = "";
                    codigo = "-1";
                    contenedor.setError("Debe seleccionar una opcion correcta");
                    if(!btnOpciones.contains(s.toString())) {
                        Log.d("opcion2", s.toString());
                        valor = "";
                        codigo = "-1";
                        contenedor.setError("Debe seleccionar una opcion correcta");
                    } else {
                        Log.d("opcion4", s.toString());
                        contenedor.setErrorEnabled(false);
                    }
                }else{
                    contenedor.setErrorEnabled(false);
                }
            }
            @Override
            public void afterTextChanged(Editable s) {
                if (items.contains(s.toString())) {
                    valor = s.toString();
                    codigo = opcionesRespuesta.get(s.toString()).toString();
                    contenedor.setErrorEnabled(false);

//                    autoCompleteTextView.setBackgroundColor(getResources().getColor(R.color.colorWaterSucess));
                }
            }
        });
        contenedor.addView(textbox);


//        if (maxLength > 0) {
//            InputFilter[] fArray = new InputFilter[2] ;
//            fArray[0] = new InputFilter.LengthFilter(maxLength);
//            fArray[1] = new InputFilter() {
//                @Override
//                public CharSequence filter(CharSequence source, int start, int end, Spanned spanned, int i2, int i3) {
//                    if(source != null && Parametros.BLOCK_CHARACTER_SET.contains(""+source)){
//                        return "";
//                    }
//                    return null;
//                }
//            };
//            buscador.setFilters(fArray);
//        }
        addView(contenedor);

        buttons = new LinearLayout(context);
        buttons.setFocusable(true);
        buttons.setFocusableInTouchMode(true);
        buttons.setOrientation(HORIZONTAL);
        buttons.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
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
                guardaBotones.put(a[0],resp);
                guardaIdBotones.put(key, a[0]);
            }
            addView(buttons);
        }

//        textbox.requestFocus();
    }

    private void cargarListado() {
        try {
            Catalogo c = new Catalogo(catalogo);
            if(catalogo.contains(";")){
                Log.d("catalogo_manzanas",catalogo);
                String a[]=catalogo.split(";");
                if(a[0].equals("cat_manzanos"))
                    valores = c.obtenerCatalogoUpmManzana(a[1]);
                else if(a[0].equals("cat_comunidad_upm"))
                    valores = c.obtenerCatalogoComunidadUpm(a[1]);

                Log.d("Cat_manzanos1", "cargarListado: "+valores);
                for(int i = 0 ; i < valores.size(); i++) {
                    elementos = valores.get(i);
                    items.add(elementos.get("descripcion").toString());
                    idItems.add(elementos.get("codigo").toString());
                    opcionesRespuesta.put(elementos.get("descripcion").toString(), elementos.get("codigo").toString());
                }

            } else {
                Log.d("catalogo_sin_coma",catalogo);
                valores = c.obtenerListado("");

                for (int i = 0; i < valores.size(); i++) {
                    elementos = valores.get(i);
                    items.add(elementos.get("descripcion").toString());
                    idItems.add(elementos.get("codigo").toString());
                    opcionesRespuesta.put(elementos.get("descripcion").toString(), elementos.get("codigo").toString());
                }
            }
        } catch (Exception exp) {
            exp.printStackTrace();
        }
    }

//    private void cargarListado() {
//        try {
//            Catalogo c = new Catalogo(catalogo);
//            valores = c.obtenerListado("");
//
//            for(int i = 0 ; i < valores.size(); i++) {
//                elementos = valores.get(i);
//                items.add(elementos.get("descripcion").toString());
//                idItems.add(elementos.get("codigo").toString());
//                opcionesRespuesta.put(elementos.get("descripcion").toString(),elementos.get("codigo").toString());
//            }
//
//        } catch (Exception exp) {
//            exp.printStackTrace();
//        }
//    }


    @Override
    public String getCodResp() {
        if (idOpciones.contains(seleccion)) {
            return seleccion;
        } else {
            return codigo;
        }
    }

    @Override
    public String getResp() {
        return (textbox.getText().toString().toUpperCase()).replace("\n", " ");
    }

    @Override
    public void setCodResp(String value) throws Exception {
        this.codigo = value;
        ToggleButton botonActive;
//        View vRecupera;
        if(idOpciones.contains(value)){
            seleccion = value;
            botonActive = (ToggleButton) guardaBotones.get(value);
            botonActive.setChecked(true);
            botonActive.setTextColor(getContext().getResources().getColor(R.color.colorAccent));
            boton = botonActive;
            llenado = false;
            textbox.setEnabled(false);
            contenedor.setEndIconActivated(false);
            contenedor.setEndIconVisible(false);
        } else {
            textbox.setEnabled(true);
        }
    }

    @Override
    public void setResp(String value) {
        this.valor = value;
        textbox.setText(valor);
    }

    @Override
    public void setFocus() {
        if (buttonsActive != null) {
            buttons.requestFocus();
        } else {
            textbox.requestFocus();
        }
    }

    @Override
    public void setEstado(Estado value) {
        super.setEstado(value);
    }

    @Override
    public void onClick(View v) {
        String respuesta = buttonsActive.get(v.getId());
        String[] a = respuesta.split("\\|");

        String botonPresionado = guardaIdBotones.get(v.getId());
        ToggleButton botonActive = (ToggleButton) guardaBotones.get(botonPresionado);

        if(seleccion.equals(a[0])){
            textbox.setEnabled(true);
            textbox.setText("");
            contenedor.setErrorEnabled(false);
            seleccion = "";
            seleccionText = "";
            botonActive.setChecked(false);
            botonActive.setTextColor(getContext().getResources().getColor(R.color.colorPrimary));
            contenedor.setEndIconVisible(true);
            boton = null;
            FragmentEncuesta.actualiza(id);
        } else if(idOpciones.contains(seleccion)){
            boton.setChecked(false);
            boton.setTextColor(getContext().getResources().getColor(R.color.colorPrimary));
            textbox.setText(a[1]);
            contenedor.setErrorEnabled(false);
            seleccion = a[0];
            seleccionText = a[1];
            textbox.setEnabled(false);
            contenedor.setEndIconVisible(false);
            botonActive.setChecked(true);
            botonActive.setTextColor(getContext().getResources().getColor(R.color.colorAccent));
            boton = botonActive;
            FragmentEncuesta.actualiza(id);

        } else {//SI NO EXISTIERA NADA en @seleccion
            textbox.setText(a[1]);
            seleccion = a[0];
            seleccionText = a[1];
            textbox.setEnabled(false);
            contenedor.setErrorEnabled(false);
            contenedor.setEndIconVisible(false);
            botonActive.setChecked(true);
            botonActive.setTextColor(getContext().getResources().getColor(R.color.colorAccent));
            boton = botonActive;
            FragmentEncuesta.actualiza(id);
        }
    }
}
