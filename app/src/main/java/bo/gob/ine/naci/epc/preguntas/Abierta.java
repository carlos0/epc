package bo.gob.ine.naci.epc.preguntas;
import static com.google.android.material.textfield.TextInputLayout.BOX_BACKGROUND_FILLED;
import static com.google.android.material.textfield.TextInputLayout.END_ICON_NONE;

import bo.gob.ine.naci.epc.R;
import android.content.Context;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import bo.gob.ine.naci.epc.entidades.Estado;
import bo.gob.ine.naci.epc.entidades.Pregunta;
import bo.gob.ine.naci.epc.entidades.TipoPregunta;
import bo.gob.ine.naci.epc.fragments.FragmentEncuesta;
import bo.gob.ine.naci.epc.fragments.FragmentInicial;
import bo.gob.ine.naci.epc.fragments.FragmentList;
import bo.gob.ine.naci.epc.herramientas.Parametros;



    public class Abierta extends PreguntaView implements View.OnClickListener {
        protected TextInputLayout contenedor;
        protected TextInputEditText textbox;

        protected Context context;
        protected int posicion;
        protected int idSeccion;
        protected String cod;
        protected Boolean evaluar;

        protected Button resp;
        protected List<Button> resps=new ArrayList<>();
        protected Map<Integer, String> buttonsActive;
        protected List<String> btnOpciones = new ArrayList<String>();
        protected List<String> idOpciones = new ArrayList<String>();
        protected String seleccion = "";
        protected String seleccionText = "";
        protected View boton;
        protected Map<String, View> guardaBotones = new HashMap<>();
        protected int maxLength;

        public Abierta(final Context context, final int posicion, final int id, final int idSeccion, final String cod, String preg, int maxLength, final Map<Integer, String> buttonsActive, String ayuda, boolean multiple, final Boolean evaluar) {
            super(context, id, idSeccion, cod, preg, ayuda);
            this.buttonsActive = buttonsActive;
            this.maxLength = maxLength;

            this.cod = cod;
            this.context = context;
            this.posicion = posicion;
            this.idSeccion = idSeccion;
            this.evaluar = evaluar;

            contenedor = new TextInputLayout(context);
            contenedor.setHint(ayuda==null || ayuda.equals("")?"respuesta":ayuda);
            contenedor.setBoxBackgroundColor(getResources().getColor(R.color.colorPrimaryOpaco));
            contenedor.setBoxBackgroundMode(TextInputLayout.BOX_BACKGROUND_FILLED);
            contenedor.setBoxStrokeColor(getResources().getColor(R.color.colorPrimaryDark));
            contenedor.setErrorEnabled(true);
            contenedor.setEndIconMode(TextInputLayout.END_ICON_CLEAR_TEXT);

            textbox = new TextInputEditText(contenedor.getContext());
            textbox.setHintTextColor(getResources().getColor(R.color.colorSecondary_text));
            textbox.setTextColor(getResources().getColor(R.color.colorPrimary_text));
            textbox.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);
            textbox.setMaxLines(1);
            textbox.setFocusable(true);
            if (multiple) {
                textbox.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE|InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS );
                textbox.setMinLines(1);
                textbox.setMaxLines(5);
                textbox.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                textbox.setSingleLine(false);
                textbox.setVerticalScrollBarEnabled(true);
            } else {
                textbox.setSingleLine(true);
            }
            textbox.setInputType(textbox.getInputType() );
            textbox.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
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

            textbox.setFilters(newFilters);
            if (maxLength > 0) {
                contenedor.setCounterEnabled(true);
                contenedor.setCounterMaxLength(maxLength);

            }
            textbox.setOnTouchListener(new OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    textbox.requestFocus();
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
//                        Movil.vibrate();
                    } else {
                        contenedor.setErrorEnabled(false);
                    }
                }
                @Override
                public void afterTextChanged(Editable s) {
                    if(id== Pregunta.getIDpregunta(idSeccion, TipoPregunta.UsoVivienda)) {
                        FragmentInicial.ejecucion(id, s.toString());
                    }

                    if(id== 18581) {
                        Log.d("ERROR2", "aqui");
                        FragmentList.ejecucion(s.toString());
                    }

                }
            });

            textbox.setTextSize(Parametros.FONT_RESP);
            contenedor.addView(textbox);
            addView(contenedor);

            final LinearLayout buttons = new LinearLayout(context);
            buttons.setOrientation(LinearLayout.HORIZONTAL);
            buttons.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
            layoutParams.weight = 1;

            if (buttonsActive != null) {
                for (Integer key : buttonsActive.keySet()) {
                    resp = new Button(context);
                    resps.add(resp);
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
            if(idOpciones.size()>0){
                contenedor.setEndIconMode(END_ICON_NONE);
                contenedor.setEndIconActivated(false);

            }
//        textbox.requestFocus();
        }
        public  void bloquearBotones(){

                for (Button b : resps) {
                    b.setEnabled(false);
                }

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
        public String getResp() {
            return (textbox.getText().toString().toUpperCase()).replace("\n", " ");
        }

        @Override
        public void setCodResp(String value) {
            View vRecupera;
            if(idOpciones.contains(value)){
                seleccion = value;
                vRecupera = guardaBotones.get(value);
                vRecupera.setBackgroundColor(getResources().getColor(R.color.colorBackgroundActive));
                boton=vRecupera;
//            vRecupera.setBackgroundColor(Color.BLUE);
                textbox.setEnabled(false);
                contenedor.setCounterMaxLength(value.length());
                contenedor.setEndIconVisible(false);
                contenedor.setErrorEnabled(false);
                contenedor.setBoxBackgroundColor(BOX_BACKGROUND_FILLED);
                contenedor.setCounterMaxLength(value.length());
            } else {
                contenedor.setCounterEnabled(true);
                textbox.setEnabled(true);
                if(value!=null &&!value.trim().equals("")){
                    contenedor.setErrorEnabled(false);
                    contenedor.setEndIconVisible(false);
                }
                for(String opc : idOpciones) {
                    vRecupera = guardaBotones.get(opc);
                    vRecupera.setBackgroundColor(getResources().getColor(R.color.colorBackgroundInactive));
//                vRecupera.setBackgroundColor(Color.RED);

                }
            }
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
//            v.setBackgroundColor(Color.RED);
                if(v!=null)
                    v.setBackgroundColor(getResources().getColor(R.color.colorBackgroundInactive));
                contenedor.setCounterMaxLength(maxLength);
                contenedor.setEndIconVisible(true);
                boton = null;
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
            }
        }
    }