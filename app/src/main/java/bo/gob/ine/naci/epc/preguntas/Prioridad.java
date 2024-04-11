package bo.gob.ine.naci.epc.preguntas;

import android.content.Context;
import android.text.Editable;
import android.text.Html;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import bo.gob.ine.naci.epc.R;
import bo.gob.ine.naci.epc.entidades.Catalogo;
import bo.gob.ine.naci.epc.entidades.Pregunta;
import bo.gob.ine.naci.epc.fragments.FragmentEncuesta;
import bo.gob.ine.naci.epc.herramientas.Parametros;


/**
 * Created by INE.
 */
public class Prioridad extends PreguntaView implements View.OnClickListener{
    protected Map<Integer, String> opciones;
    protected Button resp;
    protected Map<Integer, Integer> ids;
    protected Map<Integer, CheckBox[]> checkBoxes;
    protected EditText editText;
    protected String codEsp;
    protected Map<Integer, String> buttonsActive;
    protected String catalogo;
    protected TextInputLayout contenedor;

    protected MultiAutoCompleteTextView textbox;
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
    protected Set<String> itemsInicial = new HashSet<>();
    protected int contador = 0;
    protected boolean marcador = false;
    protected ArrayAdapter<String> adapter;
    protected int minLength;
    protected int maxLength;
    protected List<String> btnOpciones = new ArrayList<String>();
    protected List<String> idOpciones = new ArrayList<String>();
    protected Map<String, View> guardaBotones = new HashMap<>();
    protected View boton;
    protected String seleccion = "";
    protected String seleccionText = "";
    protected String opcionesDescartados = "";

    public Prioridad(final Context context, final int posicion, final int id, int idSeccion, String cod, String preg, Map<Integer, String> opciones, String catalogo,Map<Integer, String> buttonsActive ,String codEsp, final int minLength,final int maxLength, int active, String ayuda, final Boolean evaluar) {
        super(context, id, idSeccion, cod, preg, ayuda);

        this.catalogo = catalogo;
        this.opciones = opciones;
        this.codEsp = codEsp;
        this.buttonsActive = buttonsActive;
        this.codigo = "-1";
        this.valor = "";
        this.minLength = minLength;
        this.maxLength = maxLength;

        this.cod = cod;
        this.context = context;
        this.posicion = posicion;
        this.idSeccion = idSeccion;
        this.evaluar = evaluar;

        if (catalogo.equals("--")) {
            ids = new TreeMap<>();

            checkBoxes = new TreeMap<>();

            String strNum = "";
            for (int n = 1; n <= maxLength; n++) {
                strNum = strNum + "  -  " + n;
            }
            TextView txtNumeracionColumnas = new TextView(context);
            txtNumeracionColumnas.setTextSize(Parametros.FONT_RESP);
            txtNumeracionColumnas.setText(strNum.equals("") ? strNum : strNum.substring(1));
            addView(txtNumeracionColumnas);

            for (Integer key : opciones.keySet()) {
                LinearLayout linearLayout = new LinearLayout(context);
                linearLayout.setOrientation(LinearLayout.HORIZONTAL);
                checkBoxes.put(key, new CheckBox[maxLength]);
                for (int i = 0; i < maxLength; i++) {
                    checkBoxes.get(key)[i] = new CheckBox(context);
                    checkBoxes.get(key)[i].setTextSize(Parametros.FONT_RESP);
                    checkBoxes.get(key)[i].setTag(R.id.codigo, key);
                    checkBoxes.get(key)[i].setTag(R.id.elemento, i);
                    checkBoxes.get(key)[i].setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (((CheckBox) v).isChecked()) {
                                if (ids.containsKey(v.getTag(R.id.elemento)) || ids.containsValue(v.getTag(R.id.codigo))) {
                                    ((CheckBox) v).setChecked(false);
                                    Toast.makeText(context, "Ya seleccionó esta opción.", Toast.LENGTH_LONG).show();
                                } else {
                                    ids.put((Integer) v.getTag(R.id.elemento), (Integer) v.getTag(R.id.codigo));
                                }
                            } else {
                                ids.remove(v.getTag(R.id.elemento));
                            }
                            if (evaluar) {
                                FragmentEncuesta.ejecucion(context, id, posicion, "");
                            }
                        }
                    });
                    linearLayout.addView(checkBoxes.get(key)[i]);
                }
                String[] a = opciones.get(key).split("\\|");
                TextView textView = new TextView(context);
                textView.setTextSize(Parametros.FONT_RESP);
                textView.setText(Html.fromHtml(a[1]));
                linearLayout.addView(textView);
                addView(linearLayout);
                if (a[0].equals(codEsp) && editText == null) {
                    editText = new EditText(context);
                    editText.setSingleLine();
                    editText.setTextSize(Parametros.FONT_RESP);
                }
            }
            if (editText != null) {
//                addView(editText);
            }
        } else {
            contenedor = new TextInputLayout(context);
            contenedor.setHint("Respuesta");
            contenedor.setBoxBackgroundColor(getResources().getColor(R.color.color_list));
            contenedor.setBoxBackgroundMode(TextInputLayout.BOX_BACKGROUND_OUTLINE);
            contenedor.setBoxStrokeColor(getResources().getColor(R.color.colorPrimaryDark));
            contenedor.setErrorEnabled(true);
            contenedor.setEndIconMode(TextInputLayout.END_ICON_CLEAR_TEXT);
            contenedor.setHint(ayuda==null || ayuda.equals("")?"respuesta":ayuda);
            textbox = new MultiAutoCompleteTextView(context);
            textbox.setThreshold(1);
            cargarListado();
            actualizarOpciones(valor);
            adapter = new ArrayAdapter<String>(context, android.R.layout.simple_dropdown_item_1line, items);
            textbox.setAdapter(adapter);
            textbox.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());
            textbox.setFocusable(true);

            InputFilter[] fArray = new InputFilter[1];
//                fArray[0] = new InputFilter.LengthFilter(maxLength);
            fArray[0] = new InputFilter() {
                @Override
                public CharSequence filter(CharSequence source, int start, int end, Spanned spanned, int i2, int i3) {
//                        if (source != null && Parametros.BLOCK_CHARACTER_SET.contains("" + source)) {
                    if (contador == maxLength || marcador) {
                        return "";
                    }
//                        }
                    return null;
                }
            };
            textbox.setFilters(fArray);
            InputFilter[] editFilters = textbox.getFilters();
            InputFilter[] newFilters = new InputFilter[editFilters.length + 1];
            System.arraycopy(editFilters, 0, newFilters, 0, editFilters.length);
            newFilters[editFilters.length] = new InputFilter.AllCaps();

            textbox.setFilters(newFilters);

            textbox.setOnTouchListener(new OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    textbox.requestFocus();
                    if (evaluar) {
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
                        contenedor.setError("Debe seleccionar un valor");
                        marcador = false;
                        contador = 0;
//                        Movil.vibrate();
                    } else if (!buscador(s.toString().trim())&&!idOpciones.contains(seleccion)) {
                        valor = "";
                        codigo = "-1";
                        contenedor.setError("Debe seleccionar un valor valido");
                        marcador = false;
                        contador = 0;
                    } else {
                        contenedor.setErrorEnabled(false);
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                    if (buscador(s.toString().trim())&&id!= Pregunta.getIdPregunta(Parametros.CODIGO_PREGUNTA_MANZANA_UNION)) {
                        valor = s.toString();
                        codigo = obtenerDatos(s.toString().trim());
//                        if((valor.split(",", -1).length-1)!=(codigo.split(",", -1).length-1))
//                            valor=

                        contenedor.setErrorEnabled(false);

//                    autoCompleteTextView.setBackgroundColor(getResources().getColor(R.color.colorWaterSucess));
                    }else if(buscarItem(s.toString().trim())&&id== Pregunta.getIdPregunta(Parametros.CODIGO_PREGUNTA_MANZANA_UNION)){
                        valor = s.toString();
                        codigo=getCodigo(s.toString().trim());
                        contenedor.setErrorEnabled(false);
                    }
                    actualizarOpciones(s.toString());
                    adapter = new ArrayAdapter<String>(context, android.R.layout.simple_dropdown_item_1line, items);
                    textbox.setAdapter(adapter);
                }
            });
            contenedor.addView(textbox);
            addView(contenedor);
//            flexboxLayout.addView(textbox);
//            addView(flexboxLayout);
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
    private String actualizarOpciones(String respuesta){
        StringBuilder resp=new StringBuilder();
        List<String> a = Arrays.asList(respuesta.split(","));
        items.clear();
        for (String r:itemsInicial){
            boolean flag=false;
            for(String t:a){
                if(r.trim().equals(t.trim())){
                    flag=true;
                    break;
                }
            }
            if(!flag){
                items.add(r);
            }else {
                resp.append(r).append(",");
            }
        }
        items.remove(opcionesDescartados.trim());
        return resp.toString();
    }

    public void setOpcionesDescartado(String opcionesDescartado){
        this.opcionesDescartados=opcionesDescartado.trim();
    }


    private boolean buscador(String datos){
//        Log.d("mensaje", "buscar");
        Boolean respuesta = false;
            int cont = 0;
            String[] datos_sep = datos.split(",");
            String anterior = "1";
            String anterior2 = "2";
            String anterior3 = "3";
            if(datos_sep.length == 2){
                anterior = datos_sep[0].trim();
                anterior2 = datos_sep[1].trim();
            } else if(datos_sep.length == 3){
                anterior = datos_sep[0].trim();
                anterior2 = datos_sep[1].trim();
                anterior3 = datos_sep[2].trim();
            } else {
                anterior = datos_sep[0].trim();
            }
//        Log.d("anterior3", anterior3);
//        Log.d("anterior2", anterior2);
//        Log.d("anterior", anterior);
            if(anterior2.equals(anterior) || anterior3.equals(anterior) || anterior3.equals(anterior2)){
//            Log.d("mensaje", "iguales");
                contenedor.setError("Debe seleccionar idiomas diferentes");
            } else{
                for(String ds : datos_sep){
                    if (itemsInicial.contains(ds.trim())) {
                        cont++;
                    }
                }
            }

            if(cont == datos_sep.length){
                respuesta = true;
            }

        return respuesta;
    }
    private boolean buscarItem(String clave){
        boolean resp=false;
        int count=0;
        List<String> t=Arrays.asList(clave.split(","));
            for (String r:itemsInicial){
                for (String rr:t){
                    if(r.trim().equals(rr.trim()) &&!r.equals(opcionesDescartados)){
                        count++;
                    }
                }

            }
            if(count==t.size()){
                resp=true;
            }
        return resp;
    }

    private String getCodigo(String clave){
        int count=0;
        List<String> t=Arrays.asList(clave.trim().split(","));
        for (String r:itemsInicial){
            for (String rr:t){
                if(r.trim().equals(rr.trim()) &&!r.equals(opcionesDescartados)){
                    count++;
                }
            }

        }
        return String.valueOf(count==0?-1:count);
    }

    private String obtenerDatos(String datos){
        String respuesta = "";
        int cont = 0;
        String[] datos_sep = datos.split(",");

        for(String ds : datos_sep){
            String resp = String.valueOf(opcionesRespuesta.get(ds.trim()));
            respuesta += opcionesRespuesta.get(ds.trim());
//            Log.d("RESPUESTA", resp);
            if(resp.equals("995") || resp.equals("996") || resp.equals("999")){
                if(cont>0){
                    respuesta = (String) opcionesRespuesta.get(ds.trim());
                    valor=ds.trim()+",";
                    textbox.setText(valor);

                }
                marcador = true;
            }

            cont++;
            if(cont != datos_sep.length){
                respuesta += ",";
            }
        }
        contador = datos_sep.length;
        return respuesta;
    }

    private void cargarListado() {
        try {
            Catalogo c = new Catalogo(catalogo);
            if(catalogo.contains(";")){
                String a[]=catalogo.split(";");
                if(a[0].equals("cat_manzanos")){
                    valores = c.obtenerCatalogoUpmManzana(a[1]);
                }
                else if(a[0].equals("cat_comunidad_upm")){
                    valores = c.obtenerCatalogoComunidadUpm(a[1]);
                }


//                Log.d("Cat_manzanos1", "cargarListado: "+valores);
                for(int i = 0 ; i < valores.size(); i++) {
                    elementos = valores.get(i);
                    items.add(elementos.get("descripcion").toString());
                    itemsInicial.add(elementos.get("descripcion").toString());
                    idItems.add(elementos.get("codigo").toString());
                    opcionesRespuesta.put(elementos.get("descripcion").toString(), elementos.get("codigo").toString());
                }

            }else {
                valores = c.obtenerListado("");

                for(int i = 0 ; i < valores.size(); i++) {
                    elementos = valores.get(i);
                    items.add(elementos.get("descripcion").toString());
                    itemsInicial.add(elementos.get("descripcion").toString());
                    idItems.add(elementos.get("codigo").toString());
                    opcionesRespuesta.put(elementos.get("descripcion").toString(),elementos.get("codigo").toString());
                }
            }


        } catch (Exception exp) {
            exp.printStackTrace();
        }
    }

    @Override
    public String getCodResp() {
        if (idOpciones.contains(seleccion.trim())) {
            return seleccion;
        }else if(catalogo.contains(";")){
           return getCodigo(valor);
        }
        else if(catalogo.equals("--")) {
                boolean esp = false;
                String especifique = null;
                String codigo = "";
                for (int i = 0; i < ids.size(); i++) {
                    if (!ids.containsKey(i)) {
                        return "-1";
                    }
//                    Log.d("prioridad1", String.valueOf(codEsp));

                    String c = opciones.get(ids.get(i)).split("\\|")[0];
//                    Log.d("prioridad2", String.valueOf(c));
//                if (c.equals(codEsp)) {
                    if (Integer.valueOf(c) == Integer.valueOf(codEsp)) {
//                        Log.d("prioridad3", "marca");
                        esp = true;
//                    if (editText.getText().length() == 0) {
//                        return "-1";
//                    } else {
//                        especifique = editText.getText().toString();
//                    }
                    }
                    codigo += ids.get(i);
                    if(i != ids.size() - 1){
                        codigo += ",";
                    }
                }
//                Log.d("prioridad4", String.valueOf(codigo));
//                Log.d("prioridad5", String.valueOf(ids.size()));
//                Log.d("prioridad6", String.valueOf(minLength));
                if(id == 36555 || id == 18199 || id == 36710){
                    esp = true;
                }

                if (codigo.length() > 0 && ((ids.size() >= minLength && ids.size() <= maxLength) || esp)) {
                    if (especifique == null) {
//                        Log.d("prioridad", "ya salio");
                        return codigo;

                    } else {
                        if (especifique.length() == 0) {
                            return "-1";
                        } else {
                            return codigo + especifique;
                        }
                    }
                } else {
                    return "-1";
                }
            }else{
//                Log.d("idiomas", String.valueOf(marcador));
                String[] resp = codigo.split(",");
                if(codigo.contains("999")){
                    if(resp.length>1){
                        return codigo;
                    }else{
                        return "-1";
                    }
                } else {
                    if((resp.length >= minLength&&resp.length<=maxLength) || marcador){
                        return codigo;
                    }else {
                        return "-1";
                    }

                }
            }
    }

    @Override
    public void setCodResp(String cod) {
        View vRecupera;
        if(cod.equals("-1")){
            codigo=cod;
            textbox.setEnabled(true);
            textbox.setText("");
            seleccion = "";
            seleccionText = "";
//            v.setBackgroundColor(Color.RED);
            contenedor.setCounterMaxLength(maxLength);
            contenedor.setEndIconVisible(true);
            boton = null;
            contenedor.setErrorEnabled(false);
            for(String opc : idOpciones) {
                vRecupera = guardaBotones.get(opc);
                vRecupera.setBackgroundColor(getResources().getColor(R.color.colorBackgroundInactive));
//                vRecupera.setBackgroundColor(Color.RED);

            }
            return;
        }
        if(idOpciones.contains(cod)){
            seleccion = cod;
            codigo=cod;
            vRecupera = guardaBotones.get(cod);
            vRecupera.setBackgroundColor(getResources().getColor(R.color.colorBackgroundActive));
//            llenado = false;
            textbox.setEnabled(false);
            contenedor.setEndIconVisible(false);
            contenedor.setErrorEnabled(false);
        } else if(catalogo.equals("--")) {
            String[] a = cod.split(",");
            ids.clear();
            int e = 0;
//            Log.d("LONGITUD",""+a.length);
            for (int i = 0; i < a.length - e; i++) {
                Integer id = Integer.parseInt(a[i]);
                if (id > 0) {
                    ids.put(i, id);
                    checkBoxes.get(id)[i].setChecked(true);
                    if (a[i].equals(codEsp)) {
                        editText.setText(a[a.length - 1]);
                        e = 1;
                    }
                }
            }
        }else{
            textbox.setEnabled(true);
            for(String opc : idOpciones) {
                vRecupera = guardaBotones.get(opc);
                vRecupera.setBackgroundColor(getResources().getColor(R.color.colorBackgroundInactive));
//                vRecupera.setBackgroundColor(Color.RED);

            }
        }
    }

    @Override
    public String getResp() {
        String resp = "";
        if(idOpciones.contains(seleccion)){
            int position=idOpciones.indexOf(seleccion);
            return btnOpciones.get(position);
        }
        else if(catalogo.equals("--")) {
            for (Integer key : ids.keySet()) {
                resp += opciones.get(ids.get(key)).split("\\|")[1] + "; ";
            }
            if (resp.length() > 0) {
                resp = resp.substring(0, resp.length() - 2);
            }
            return resp;
        }else{
            return valor;
        }
    }

    @Override
    public void setResp(String value) {
        if(!catalogo.equals("--")) {
            this.valor = value;
            textbox.setText(valor);
        }
    }

    @Override
    public void setFocus() {
        textbox.requestFocus();
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
//            v.setBackgroundColor(Color.RED);
            if(v!=null)
                v.setBackgroundColor(getResources().getColor(R.color.colorBackgroundInactive));
            contenedor.setCounterMaxLength(maxLength);
            contenedor.setEndIconVisible(true);
            boton = null;
            contenedor.setErrorEnabled(false);
        } else if(idOpciones.contains(seleccion)){
            if(boton!=null)
                boton.setBackgroundColor(getResources().getColor(R.color.colorBackgroundInactive));

//            boton.setBackgroundColor(Color.RED);
            textbox.setText(a[1]);
            seleccion = a[0];
            seleccionText = a[1];
            textbox.setEnabled(false);
            if(v!=null)
                v.setBackgroundColor(getResources().getColor(R.color.colorBackgroundActive));
//            v.setBackgroundColor(Color.BLUE);
            contenedor.setCounterMaxLength(a[1].length());
            contenedor.setEndIconVisible(false);
            boton = v;
            contenedor.setErrorEnabled(false);
        } else {//SI NO EXISTIERA NADA en @seleccion
            textbox.setText(a[1]);
            seleccion = a[0];
            seleccionText = a[1];
            textbox.setEnabled(false);
            if(v!=null)
                v.setBackgroundColor(getResources().getColor(R.color.colorBackgroundActive));
//            v.setBackgroundColor(Color.BLUE);
            contenedor.setCounterMaxLength(a[1].length());
            contenedor.setEndIconVisible(false);
            boton = v;
            contenedor.setErrorEnabled(false);
        }
    }
}
