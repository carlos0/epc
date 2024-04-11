package bo.gob.ine.naci.epc.preguntas;

import android.content.Context;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
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
import bo.gob.ine.naci.epc.entidades.Catalogo;
import bo.gob.ine.naci.epc.entidades.Estado;
import bo.gob.ine.naci.epc.entidades.Pregunta;
import bo.gob.ine.naci.epc.fragments.FragmentInicial;
import bo.gob.ine.naci.epc.herramientas.Parametros;


public class Autocompletar extends PreguntaView implements View.OnClickListener {

    protected TextInputLayout contenedor;
    protected AutoCompleteTextView textbox;
//    protected TextInputEditText valor;

    protected String catalogo;
    protected Button resp;
    protected Map<Integer, String> buttonsActive;
    protected String codigo;
    protected String valor;

    protected Context context;
    protected int posicion;
    protected int idSeccion;
    protected String cod;
    protected Boolean evaluar;
    protected Boolean dinamico;

    protected ArrayList<Map<String, Object>> valores = new ArrayList<>();
    protected Map<String,Object> elementos = null;
    protected Map<String,Object> opcionesRespuesta = new HashMap<>();
    protected List<String> idItems = new ArrayList<String>();
    protected List<String> items = new ArrayList<String>();
    protected Map<String, View> guardaBotones = new HashMap<>();

    protected List<String> btnOpciones = new ArrayList<String>();
    protected List<String> idOpciones = new ArrayList<String>();
    protected String seleccion = "";
    protected String seleccionText = "";
    protected View boton;
    protected Boolean llenado = true;

    public Autocompletar(final Context context, final int posicion, final int id, int idSeccion, final String cod, String preg, int maxLength, String catalogo, final Map<Integer, String> buttonsActive, String ayuda, boolean multiple, final Boolean evaluar,final  boolean dinamico) {
        super(context, id, idSeccion, cod, preg, ayuda);
        this.catalogo = catalogo;
        this.buttonsActive = buttonsActive;
        this.codigo = "-1";
        this.valor = "";

        this.cod = cod;
        this.context = context;
        this.posicion = posicion;
        this.idSeccion = idSeccion;
        this.evaluar = evaluar;
        this.dinamico=dinamico;

        contenedor = new TextInputLayout(context);
        contenedor.setHint(ayuda==null || ayuda.equals("")?"respuesta":ayuda);
        contenedor.setBoxBackgroundColor(getResources().getColor(R.color.color_list));
        contenedor.setBoxBackgroundMode(TextInputLayout.BOX_BACKGROUND_OUTLINE);
        contenedor.setBoxStrokeColor(getResources().getColor(R.color.colorPrimaryDark));
        contenedor.setErrorEnabled(true);
        contenedor.setEndIconMode(TextInputLayout.END_ICON_CLEAR_TEXT);

        textbox = new AutoCompleteTextView(context);
        textbox.setThreshold(1);
        textbox.setInputType(InputType.TYPE_CLASS_TEXT |InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);
        cargarListado();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_dropdown_item_1line, items);
        textbox.setAdapter(adapter);
        textbox.setFocusable(true);
        textbox.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(evaluar) {
//                    FragmentEncuesta.ejecucion(context, id, posicion, "");
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
                    valor = "";
                    codigo = "-1";
                    contenedor.setError("Debe llenar esta respuesta");
                    if(id==Pregunta.getIdPregunta(Parametros.CODIGO_PREGUNTA_UNION_DIVISION)) {
                        FragmentInicial.mostrarUnionManzanas();
                    }
//                    Movil.vibrate();
                } else if (!items.contains(s.toString())) {
                    valor = "";
                    codigo = "-1";
                    contenedor.setError("Debe seleccionar una opcion correcta");
                }else if(!btnOpciones.contains(s.toString())){
                    valor = "";
                    codigo = "-1";
                    contenedor.setError("Debe seleccionar una opcion correcta");
                }else{
                    contenedor.setErrorEnabled(false);
                }
            }
            @Override
            public void afterTextChanged(Editable s) {
                Log.d("text", btnOpciones + "-" + s.toString());
                if (items.contains(s.toString())) {
                    valor = s.toString();
                    codigo = opcionesRespuesta.get(s.toString()).toString();
                    contenedor.setErrorEnabled(false);
                    if(id==Pregunta.getIdPregunta(Parametros.CODIGO_PREGUNTA_UNION_DIVISION)){
//                        hides the fields
                        if(!s.toString().equals("U")){
                            FragmentInicial.ocultarUnionManzanas();
                        }else {
                            FragmentInicial.mostrarUnionManzanas();
                        }

                    }
                    contenedor.setErrorEnabled(false);
//                    autoCompleteTextView.setBackgroundColor(getResources().getColor(R.color.colorWaterSucess));
                }else if(btnOpciones.contains(s.toString())){
                    if(id==Pregunta.getIdPregunta(Parametros.CODIGO_PREGUNTA_MANZANA_UNION)){
//                        hides the fields
                        if(s.toString().equals("NA")){
                            FragmentInicial.ocultarUnionManzanas();
                        }else {
                            FragmentInicial.mostrarUnionManzanas();
                        }

                    }

                    contenedor.setErrorEnabled(false);

                    textbox.setEnabled(false);
                    contenedor.setEndIconVisible(false);
                }

//                //variables de seleccion uso de vivienda
//                if(id== Pregunta.getIdPregunta(Parametros.CODIGO_PREGUNTA_USO_DE_LA_VIVIENDA)) {
//                    FragmentInicial.ejecucion(id, s.toString());
//                }
//                //variable para descartar opcion de manzana seleccionada en la union
//                if(s.length()>0&& id== Pregunta.getIdPregunta(Parametros.CODIGO_PREGUNTA_MANZANA_COMUNIDAD)) {
//                    FragmentInicial.setInvalidOption(s.toString());
//                }

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
        InputFilter[] editFilters = textbox.getFilters();
        InputFilter[] newFilters = new InputFilter[editFilters.length + 1];
        System.arraycopy(editFilters, 0, newFilters, 0, editFilters.length);
        newFilters[editFilters.length] = new InputFilter.AllCaps();

        textbox.setFilters(newFilters);
        addView(contenedor);

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

//        textbox.requestFocus();
    }

    private void cargarListado() {
        try {
            Catalogo c = new Catalogo(catalogo);
            if(catalogo.contains(";")){
                String a[]=catalogo.split(";");
                if(a[0].equals("cat_manzanos")){
                    valores = dinamico?c.obtenerCatalogoUpmManzanaDinamico(a[1]):c.obtenerCatalogoUpmManzana(a[1]);
                }
                else if(a[0].equals("cat_comunidad_upm"))
                    valores = c.obtenerCatalogoComunidadUpm(a[1]);

//                Log.d("Cat_manzanos1", "cargarListado: "+valores);
                for(int i = 0 ; i < valores.size(); i++) {
                    elementos = valores.get(i);
                    items.add(elementos.get("descripcion").toString());
                    idItems.add(elementos.get("codigo").toString());
                    opcionesRespuesta.put(elementos.get("descripcion").toString(), elementos.get("codigo").toString());
                }

            } else {

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
        View vRecupera;
        if(idOpciones.contains(value)){
           if( id==Pregunta.getIdPregunta(Parametros.CODIGO_PREGUNTA_MANZANA_UNION)){
               FragmentInicial.ocultarUnionManzanas();
            }
            seleccion = value;
            vRecupera = guardaBotones.get(value);
            vRecupera.setBackgroundColor(getResources().getColor(R.color.colorBackgroundActive));
            llenado = false;
            textbox.setEnabled(false);
            contenedor.setEndIconVisible(false);
            contenedor.setErrorEnabled(false);
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
            contenedor.setErrorEnabled(false);
            seleccion = "";
            seleccionText = "";
            v.setBackgroundColor(getResources().getColor(R.color.colorBackgroundInactive));
            contenedor.setEndIconVisible(true);
            boton = null;
            if(id==Pregunta.getIdPregunta(Parametros.CODIGO_PREGUNTA_UNION_DIVISION)){
//                        hides the fields

                    FragmentInicial.mostrarUnionManzanas();

            }
        } else if(idOpciones.contains(seleccion)){
            boton.setBackgroundColor(getResources().getColor(R.color.colorBackgroundInactive));
            textbox.setText(a[1]);
            contenedor.setErrorEnabled(false);
            seleccion = a[0];
            seleccionText = a[1];
            textbox.setEnabled(false);
            contenedor.setEndIconVisible(false);
            v.setBackgroundColor(getResources().getColor(R.color.colorBackgroundActive));
            boton = v;


        } else {//SI NO EXISTIERA NADA en @seleccion
            textbox.setText(a[1]);
            seleccion = a[0];
            seleccionText = a[1];
            textbox.setEnabled(false);
            contenedor.setErrorEnabled(false);
            contenedor.setEndIconVisible(false);
            v.setBackgroundColor(getResources().getColor(R.color.colorBackgroundActive));
            boton = v;
            if(id==Pregunta.getIdPregunta(Parametros.CODIGO_PREGUNTA_UNION_DIVISION)){
//                        hides the fields
                if(a[1].equals("NA")){
                    FragmentInicial.ocultarUnionManzanas();
                }else {
                    FragmentInicial.mostrarUnionManzanas();
                }
            }
        }
    }
}
