package bo.gob.ine.naci.epc.preguntas;

import android.content.Context;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import bo.gob.ine.naci.epc.R;
import bo.gob.ine.naci.epc.adaptadores.AdapterEvents;
import bo.gob.ine.naci.epc.adaptadores.CatalogoAdapter;
import bo.gob.ine.naci.epc.entidades.Catalogo;
import bo.gob.ine.naci.epc.entidades.Encuesta;
import bo.gob.ine.naci.epc.entidades.Estado;
import bo.gob.ine.naci.epc.entidades.Pregunta;
import bo.gob.ine.naci.epc.fragments.FragmentEncuesta;
import bo.gob.ine.naci.epc.fragments.FragmentInicial;
import bo.gob.ine.naci.epc.herramientas.Movil;
import bo.gob.ine.naci.epc.herramientas.Parametros;

public class Autocompletar2 extends PreguntaView implements AdapterEvents, View.OnClickListener {

    protected TextInputLayout contenedor;
    protected AutoCompleteTextView textbox;
//    protected TextInputEditText valor;
    protected ListView list;

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
    protected ArrayList<Map<String, Object>> valoresInicial = new ArrayList<>();
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
    protected CatalogoAdapter adapter;

    public Autocompletar2(final Context context, final int posicion, final int id, int idSeccion, final String cod, String preg, int maxLength, String catalogo, final Map<Integer, String> buttonsActive, String ayuda, boolean multiple, final Boolean evaluar,final  boolean dinamico) {
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
        textbox.setFocusable(true);
//        if()
//        cargarListado("");
//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, , items);
//        adapter=new CatalogoAdapter(context,this,valores);
//        textbox.setAdapter(adapter);
        if(!catalogo.equals("catalogo_direcciones")){
            textbox.setInputType(InputType.TYPE_CLASS_NUMBER);
        }

        textbox.setOnTouchListener(new OnTouchListener() {
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
                    valor = "";
                    codigo = "-1";
                    contenedor.setError("Debe llenar esta respuesta");
//                    textbox.setBackgroundColor(getResources().getColor(R.color.color_list));
//                    Movil.vibrate();
                }else if(textbox.getText().toString().trim().length()>0&&catalogo.equals("catalogo_direcciones")){
                    cargarListado(textbox.getText().toString().trim());
                }else {
                    contenedor.setErrorEnabled(false);
                }
            }
            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length()>0&&catalogo.equals("catalogo_direcciones")) {

                    valor = s.toString();
                    codigo = String.valueOf(Movil.getMd5Hash(s.toString()));
                    contenedor.setErrorEnabled(false);


//                        cargarListado(s.toString());

//                    autoCompleteTextView.setBackgroundColor(getResources().getColor(R.color.colorWaterSucess));
                } else if(s.toString().length()>0&&buscar(s.toString())){
                    valor = s.toString();
                    codigo = String.valueOf(Movil.getMd5Hash(s.toString()));
                    contenedor.setErrorEnabled(false);
                   clearList();
                }else {
                    cargarListado(s.toString());
                }

                //variables de seleccion uso de vivienda
                if(id== Pregunta.getIdPregunta(Parametros.CODIGO_PREGUNTA_USO_DE_LA_VIVIENDA)) {

                    FragmentInicial.ejecucion(id, s.toString().trim());
                }
//                //variable para descartar opcion de manzana seleccionada en la union
//                if(id== Pregunta.getIdPregunta(Parametros.CODIGO_PREGUNTA_MANZANA_COMUNIDAD)) {
//                    FragmentInicial.setInvalidOption(valor);
//                }

            }
        });
        InputFilter[] fArray = new InputFilter[2];
        fArray[0] = new InputFilter.LengthFilter(maxLength);
        fArray[1] = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned spanned, int i2, int i3) {
                if (source != null && Parametros.BLOCK_CHARACTER_SET.contains("" + source)) {
                    return "";
                }
                return null;
            }
        };
        textbox.setFilters(fArray);
        InputFilter[] editFilters = textbox.getFilters();
        InputFilter[] newFilters = new InputFilter[editFilters.length + 1];
        System.arraycopy(editFilters, 0, newFilters, 0, editFilters.length);
        newFilters[editFilters.length] = new InputFilter.AllCaps();

        textbox.setFilters(newFilters);;
        contenedor.addView(textbox);
        LinearLayout.LayoutParams layoutParamss = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int)(300 * context.getResources().getDisplayMetrics().density)){
        };
        list = new ListView(context);
//        list.setBackgroundColor(MyApplication.getContext().getResources().getColor(R.color.colorPrimary_light));
        list.setScrollContainer(true);
        list.setLayoutParams(layoutParamss);
        list.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
        list.setSelector(R.drawable.selector_catalogo);
        contenedor.addView(list);
        cargarListado(textbox.getText().toString().trim());



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

//    private void cargarListado( String clave) {
//        try {
//            Catalogo c = new Catalogo(catalogo);
//            if(catalogo.contains(";")){
//                String a[]=catalogo.split(";");
//                if(a[0].equals("cat_manzanos")){
//                    valores = dinamico?c.obtenerCatalogoUpmManzanaDinamico(a[1]):c.obtenerCatalogoUpmManzana(a[1]);
//                }
//                else if(a[0].equals("cat_comunidad_upm"))
//                    valores = c.obtenerCatalogoComunidadUpm(a[1]);
//
////                Log.d("Cat_manzanos1", "cargarListado: "+valores);
//                for(int i = 0 ; i < valores.size(); i++) {
//                    elementos = valores.get(i);
//                    items.add(elementos.get("descripcion").toString());
//                    idItems.add(elementos.get("codigo").toString());
//                    opcionesRespuesta.put(elementos.get("descripcion").toString(), elementos.get("codigo").toString());
//                }
//
//            } else {
//
//                valores = c.obtenerListado("");
//
//                for (int i = 0; i < valores.size(); i++) {
//                    elementos = valores.get(i);
//                    items.add(elementos.get("descripcion").toString());
//                    idItems.add(elementos.get("codigo").toString());
//                    opcionesRespuesta.put(elementos.get("descripcion").toString(), elementos.get("codigo").toString());
//                }
//            }
//        } catch (Exception exp) {
//            exp.printStackTrace();
//        }
//    }

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
        private void cargarListado(String clave) {
        try {

            Catalogo c = new Catalogo(catalogo);
            if(catalogo.equals("catalogo_direcciones")){
                valores=  Encuesta.obtenerListadoUltimasDirecciones(clave);
            }else{
                if(valor.equals(clave)){
                    valores = c.obtenerListado(" AND UPPER(descripcion) LIKE '%" + clave.toUpperCase() + "%' LIMIT 3");
                    valoresInicial=c.obtenerListado(" AND UPPER(descripcion) LIKE '%" + clave.toUpperCase() + "%' LIMIT 3");
                }else {
                    valores.clear();
                }

            }


            if(valores.size()==0&&textbox.getText().toString().length()==0){
                valor = "";
                codigo = "-1";
//                textbox.setBackgroundColor(getResources().getColor(R.color.color_list));
            }


            contenedor.removeView(list);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int)(valores.size()* 55 * context.getResources().getDisplayMetrics().density));
            list.setLayoutParams(layoutParams);

            CatalogoAdapter adapter = new CatalogoAdapter(context, this, valores);
            list.setAdapter(adapter);
            contenedor.addView(list);

        } catch (Exception exp) {
            exp.printStackTrace();
        }
    }

    private void clearList(){
        valores.clear();
        contenedor.removeView(list);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int)(valores.size()* 55 * context.getResources().getDisplayMetrics().density));
        list.setLayoutParams(layoutParams);
        CatalogoAdapter adapter = new CatalogoAdapter(context, this, valores){

        };
        list.setAdapter(adapter);
        contenedor.addView(list);
    }
    private boolean buscar(String clave){
        boolean rep=false;
        Iterator<Map<String,Object>> iterator=valoresInicial.iterator();
        Map<String,Object> e=null;
        while (iterator.hasNext()){
            e=iterator.next();

                if(e.get("descripcion").toString().trim().equals(clave.trim())){
                    return true;
                }

        }
        return rep;
    }

    @Override
    public String getCodResp() {
        if (idOpciones.contains(seleccion)) {
            return seleccion;
        } else if(textbox.getText().toString().trim().length()>0&&catalogo.equals("catalogo_direcciones")){
            return String.valueOf(Movil.getMd5Hash(textbox.getText().toString()));
        }else if(buscar(valor.trim())){
            return String.valueOf(valor.trim());
        }

        return "-1";
    }

    @Override
    public String getResp() {
        return (textbox.getText().toString().trim().toUpperCase()).replace("\n", " ");
    }

    @Override
    public void setCodResp(String value) throws Exception {
        this.codigo = value;
        seleccion=value;
        View vRecupera;
        if(idOpciones.contains(value)){
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
        if(textbox.getText().toString().trim().length()>0&&catalogo.equals("catalogo_direcciones")){
            clearList();
        }
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
        }
    }
     @Override
    public void onItemClick(int mPosition) {
        Map<String, Object> values = valores.get(mPosition);
        codigo = String.valueOf(((String)values.get("descripcion")).length());
        this.cod= String.valueOf(((String)values.get("descripcion")).length());
        valor = (String)values.get("descripcion");
        textbox.setText(valor);
        seleccion=String.valueOf(valor.length());
        clearList();
//         if(id== Pregunta.getIdPregunta(Parametros.CODIGO_PREGUNTA_USO_DE_LA_VIVIENDA)) {
//             FragmentInicial.ejecucion(id, valor);
//         }
//         //variable para descartar opcion de manzana seleccionada en la union
//         if(id== Pregunta.getIdPregunta(Parametros.CODIGO_PREGUNTA_MANZANA_COMUNIDAD)) {
//             FragmentInicial.setInvalidOption(valor);
//         }
//        textbox.setBackgroundColor(getResources().getColor(R.color.colorWaterSucess));
    }

    @Override
    public void onLongItemClick(int mPosition) {

    }
}
