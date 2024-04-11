package bo.gob.ine.naci.epc.preguntas2;

import android.content.Context;
import android.text.Editable;
import android.text.Html;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import bo.gob.ine.naci.epc.R;
import bo.gob.ine.naci.epc.entidades.Catalogo;
import bo.gob.ine.naci.epc.fragments.FragmentEncuesta;
import bo.gob.ine.naci.epc.herramientas.Parametros;


/**
 * Created by INE.
 */
public class Prioridad extends PreguntaView {
    protected Map<Integer, String> opciones;
    protected Map<Integer, Integer> ids;
    protected Map<Integer, CheckBox[]> checkBoxes;
    protected EditText editText;
    protected String codEsp;
    protected int buttonsActive;
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
    protected int contador = 0;
    protected boolean marcador = false;
    protected ArrayAdapter<String> adapter;
    protected int minLength;
    protected int maxLength;
    protected ImageButton btnNext;
    protected LinearLayout linearLayout;

    public Prioridad(final Context context, final int posicion, final int id, int idSeccion, String cod, String preg, Map<Integer, String> opciones, String catalogo, String codEsp, final int minLength,final int maxLength, int buttonsActive, String ayuda, final Boolean evaluar, final Boolean mostrarSeccion, String observacion) {
        super(context, id, idSeccion, cod, preg, ayuda, mostrarSeccion, observacion);

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

        btnNext = new ImageButton(getContext());
        btnNext.setImageDrawable(getResources().getDrawable(R.drawable.ic_next_preg));
        btnNext.setBackground(null);
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentEncuesta.actualiza(id);
            }
        });
        LinearLayout.LayoutParams layoutParamsButton = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParamsButton.gravity = Gravity.RIGHT;
        btnNext.setLayoutParams(layoutParamsButton);
        addView(btnNext);
        setFocusable(true);
        setFocusableInTouchMode(true);

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
                linearLayout = new LinearLayout(context);
                linearLayout.setOrientation(LinearLayout.HORIZONTAL);
                checkBoxes.put(key, new CheckBox[maxLength]);
                for (int i = 0; i < maxLength; i++) {
                    checkBoxes.get(key)[i] = new CheckBox(context);
                    checkBoxes.get(key)[i].setTextSize(Parametros.FONT_RESP);
                    checkBoxes.get(key)[i].setTag(R.id.codigo, key);
                    checkBoxes.get(key)[i].setTag(R.id.elemento, i);
                    checkBoxes.get(key)[i].setOnClickListener(new OnClickListener() {
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
                            if(evaluar) {
//                                FragmentEncuesta.actualiza(id);
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

            textbox = new MultiAutoCompleteTextView(context);
            textbox.setThreshold(1);
            cargarListado();
            adapter = new ArrayAdapter<String>(context, android.R.layout.simple_dropdown_item_1line, items);
            textbox.setAdapter(adapter);
            textbox.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());

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
            textbox.setOnTouchListener(new OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    textbox.requestFocus();
//                    if(evaluar) {
//                        FragmentEncuesta.actualiza(id);
//                    }
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
                        contenedor.setError("Debe seleccionar un idioma");
                        marcador = false;
                        contador = 0;
//                        Movil.vibrate();
                    } else if (!buscador(s.toString().trim())) {
                        valor = "";
                        codigo = "-1";
                        contenedor.setError("Debe seleccionar un idioma valido");
                        marcador = false;
                        contador = 0;
                    } else {
                        contenedor.setErrorEnabled(false);
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (buscador(s.toString().trim())) {
                        valor = s.toString();
                        codigo = obtenerDatos(s.toString().trim());
//                        if((valor.split(",", -1).length-1)!=(codigo.split(",", -1).length-1))
//                            valor=
                        contenedor.setErrorEnabled(false);

//                    autoCompleteTextView.setBackgroundColor(getResources().getColor(R.color.colorWaterSucess));
                    }
                }
            });
            contenedor.addView(textbox);
            addView(contenedor);
//            flexboxLayout.addView(textbox);
//            addView(flexboxLayout);
        }
    }


    private boolean buscador(String datos){
        Log.d("mensaje", "buscar");
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
        Log.d("anterior3", anterior3);
        Log.d("anterior2", anterior2);
        Log.d("anterior", anterior);
        if(anterior2.equals(anterior) || anterior3.equals(anterior) || anterior3.equals(anterior2)){
            Log.d("mensaje", "iguales");
            contenedor.setError("Debe seleccionar idiomas diferentes");
        } else{
            for(String ds : datos_sep){
                if (items.contains(ds.trim())) {
                    cont++;
                }
            }
        }

        if(cont == datos_sep.length){
            respuesta = true;
        }
        return respuesta;
    }

    private String obtenerDatos(String datos){
        String respuesta = "";
        int cont = 0;
        String[] datos_sep = datos.split(",");

        for(String ds : datos_sep){
            String resp = String.valueOf(opcionesRespuesta.get(ds.trim()));
            respuesta += opcionesRespuesta.get(ds.trim());
            Log.d("RESPUESTA", resp);
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
            valores = c.obtenerListado("");

            for(int i = 0 ; i < valores.size(); i++) {
                elementos = valores.get(i);
                items.add(elementos.get("descripcion").toString());
                idItems.add(elementos.get("codigo").toString());
                opcionesRespuesta.put(elementos.get("descripcion").toString(),elementos.get("codigo").toString());
            }

        } catch (Exception exp) {
            exp.printStackTrace();
        }
    }

    @Override
    public String getCodResp() {
            if(catalogo.equals("--")) {
                boolean esp = false;
                String especifique = null;
                String codigo = "";
                for (int i = 0; i < ids.size(); i++) {
                    if (!ids.containsKey(i)) {
                        return "-1";
                    }
                    Log.d("prioridad1", String.valueOf(codEsp));

                    String c = opciones.get(ids.get(i)).split("\\|")[0];
                    Log.d("prioridad2", String.valueOf(c));
//                if (c.equals(codEsp)) {
                    if (Integer.valueOf(c) == Integer.valueOf(codEsp)) {
                        Log.d("prioridad3", "marca");
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
                Log.d("prioridad4", String.valueOf(codigo));
                Log.d("prioridad5", String.valueOf(ids.size()));
                Log.d("prioridad6", String.valueOf(minLength));
                if(id == 36555 || id == 18199 || id == 36710){
                    esp = true;
                }

                if (codigo.length() > 0 && ((ids.size() >= minLength && ids.size() <= maxLength) || esp)) {
                    if (especifique == null) {
                        Log.d("prioridad", "ya salio");
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

                String[] resp = codigo.split(",");
                Log.d("idiomas", String.valueOf(marcador)+"-"+codigo+"-"+codigo);
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
        if(catalogo.equals("--")) {
            String[] a = cod.split(",");
            ids.clear();
            int e = 0;
            Log.d("LONGITUD",""+a.length);
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
            this.codigo = cod;
            textbox.setEnabled(true);
        }
    }

    @Override
    public String getResp() {
        String resp = "";
        if(catalogo.equals("--")) {
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
        requestFocus();
    }
}
