package bo.gob.ine.naci.epc.preguntas2;

import static com.google.android.material.textfield.TextInputLayout.BOX_BACKGROUND_FILLED;
import static com.google.android.material.textfield.TextInputLayout.END_ICON_NONE;

import android.content.Context;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bo.gob.ine.naci.epc.entidades.Estado;
import bo.gob.ine.naci.epc.herramientas.Movil;
import bo.gob.ine.naci.epc.herramientas.Parametros;
import bo.gob.ine.naci.epc.R;
import bo.gob.ine.naci.epc.fragments.FragmentEncuesta;

public class Abierta extends PreguntaView implements View.OnClickListener {
    protected TextInputLayout contenedor;
    protected TextInputEditText textbox;
    protected ImageButton btnNext;

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
    protected Map<String, View> guardaBotones = new HashMap<>();
    protected Map<Integer, String> guardaIdBotones = new HashMap<>();
    protected int maxLength;
    protected LinearLayout buttons;
    protected InputFilter[] fArray = new InputFilter[3];

    public Abierta(final Context context, final int posicion, final int id, int idSeccion, final String cod, String preg, int maxLength, final Map<Integer, String> buttonsActive, String ayuda, boolean multiple, final Boolean evaluar, final Boolean mostrarSeccion, String observacion) {
        super(context, id, idSeccion, cod, preg, ayuda, mostrarSeccion, observacion);
        this.buttonsActive = buttonsActive;
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
                if(evaluar) {
                    FragmentEncuesta.actualiza(id);
                }
            }
        });
        LinearLayout.LayoutParams layoutParamsButton = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParamsButton.gravity = Gravity.RIGHT;
        btnNext.setLayoutParams(layoutParamsButton);

        contenedor = new TextInputLayout(context);
        contenedor.setHint("Respuesta");
        contenedor.setBoxBackgroundColor(getResources().getColor(R.color.color_list));
        contenedor.setBoxBackgroundMode(TextInputLayout.BOX_BACKGROUND_OUTLINE);
        contenedor.setBoxStrokeColor(getResources().getColor(R.color.colorPrimaryDark));
        contenedor.setErrorEnabled(true);
        contenedor.setEndIconMode(TextInputLayout.END_ICON_CLEAR_TEXT);
        contenedor.setPadding(5, 0,5,0);


        textbox = new TextInputEditText(contenedor.getContext());
        textbox.setHintTextColor(getResources().getColor(R.color.colorSecondary_text));
        textbox.setTextColor(getResources().getColor(R.color.colorPrimary_text));
        textbox.setImeOptions(EditorInfo.IME_ACTION_GO);

        textbox.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_GO) {
                    if(evaluar) {
                        FragmentEncuesta.actualiza(id);
                    }
                    return true;
                }
                return false;
            }
        });

        if (multiple) {
            textbox.setMaxLines(5);
            textbox.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
            textbox.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            textbox.setSingleLine(false);
        } else {
            textbox.setSingleLine(true);
        }
        textbox.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        if (maxLength > 0) {
//            InputFilter[] fArray = new InputFilter[3];
//            InputFilter[] fArray = new InputFilter[2];
            fArray[0] = new InputFilter.LengthFilter(maxLength);
            fArray[1] = new InputFilter() {
                @Override
                public CharSequence filter(CharSequence source, int start, int end, Spanned spanned, int i2, int i3) {
                    if(cod.equals("F4_A_01") || cod.equals("F4_C_03") || cod.equals("F3_C_03") || cod.equals("F2_N_70")){
//                        if (source != null && !Parametros.BLOCK_LETTER_PERMISSION.contains("" + source)){
//                        if (source != null && Parametros.BLOCK_NUMBER_SET.contains("" + source)) {
//                            return "";
//                        }
                    } else {
                        if (source != null && Parametros.BLOCK_CHARACTER_SET.contains("" + source)) {
                            return "";
                        }
                    }
                    return null;
                }
            };
            fArray[2] = new InputFilter.AllCaps();
            contenedor.setCounterEnabled(true);
            contenedor.setCounterMaxLength(maxLength);
            textbox.setFilters(fArray);
        }
        textbox.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                textbox.requestFocus();

//                if(evaluar) {
//                    FragmentEncuesta.ejecucion(context, id, posicion, "");
//                }
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
                    if(evaluar) {
                        FragmentEncuesta.actualiza(id);
                    }

                } else {
//                    FragmentEncuesta.indexActualiza(id);
                    contenedor.setErrorEnabled(false);
                }
            }
            @Override
            public void afterTextChanged(Editable s) {
//                textbox.requestFocus();
                if(cod.equals("A_14")) {
//                    FragmentInicial.ejecucion(id, s.toString());
                }
            }
        });

//        if(Usuario.getRol() == Parametros.SUPERVISOR){
//            if(idSeccion != 449){
//                textbox.setFocusable(false);
//                textbox.setEnabled(false);
//                textbox.setCursorVisible(false);
//                textbox.setKeyListener(null);
////                contenedor.setErrorEnabled(false);
////                contenedor.setEndIconActivated(false);
//                contenedor.setEndIconVisible(false);
//                contenedor.setEndIconMode(NOT_FOCUSABLE);
//            }
//        }


        if (idSeccion == 166) {
//            LinearLayout respuesta = new LinearLayout(context);
//            LayoutParams layoutParams_respuesta = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
//            layoutParams_respuesta.weight = 1;
//            respuesta.setLayoutParams(layoutParams_respuesta);
            botones.addView(btnNext);
            textbox.setTextSize(Parametros.FONT_RESP);
            contenedor.addView(textbox);
            contenedor.setEndIconActivated(false);
            respuesta.addView(contenedor);
//            addView(respuesta);
        } else {
//            addView(btnNext);
            setBotones(btnNext, false);
            textbox.setTextSize(Parametros.FONT_RESP);
            contenedor.addView(textbox);
            contenedor.setEndIconActivated(false);
            addView(contenedor);
        }

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
//                if(Usuario.getRol() == Parametros.SUPERVISOR){
//                    if(idSeccion != 449){
//                        resp.setFocusable(false);
//                        resp.setEnabled(false);
//                        resp.setCursorVisible(false);
//                        resp.setKeyListener(null);
//                    }
//                }
            }
            if (idSeccion == 166) {
                respuesta.addView(buttons);
            } else {
                addView(buttons);
            }

        }
        if(idOpciones.size()>0){
            contenedor.setEndIconMode(END_ICON_NONE);
            contenedor.setEndIconActivated(false);
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
        return (textbox.getText().toString().trim()).replace("\n", " ");
    }

    @Override
    public void setCodResp(String value) {
        ToggleButton botonActive;
        if(idOpciones.contains(value)){
            seleccion = value;
            botonActive = (ToggleButton) guardaBotones.get(value);
            botonActive.setChecked(true);
            botonActive.setTextColor(getContext().getResources().getColor(R.color.colorAccent));
            contenedor.setEndIconVisible(false);
            boton = botonActive;

            textbox.setEnabled(false);
            contenedor.setEndIconActivated(false);
            contenedor.setEndIconActivated(false);
            contenedor.setEndIconVisible(false);
            contenedor.setBoxBackgroundMode (BOX_BACKGROUND_FILLED);

        } else {
            textbox.setEnabled(true);
            for(String opc : idOpciones) {
                botonActive = (ToggleButton) guardaBotones.get(opc);
                botonActive.setChecked(false);
                botonActive.setTextColor(getContext().getResources().getColor(R.color.colorPrimary));
            }
        }
    }

    @Override
    public void setResp(String value) {
        if(value.length() > this.maxLength) {
            contenedor.setCounterMaxLength(value.length());
            fArray[0] = new InputFilter.LengthFilter(value.length());
            textbox.setFilters(fArray);
        }
        textbox.setText(value);
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
        Log.d("REVISION501", String.valueOf(seleccion));
        String respuesta = buttonsActive.get(v.getId());
        String[] a = respuesta.split("\\|");
        Log.d("REVISION500", respuesta);
        String botonPresionado = guardaIdBotones.get(v.getId());
        ToggleButton botonActive = (ToggleButton) guardaBotones.get(botonPresionado);

        if(seleccion.equals(a[0])){
            Log.d("REVISION200", "ACTS");
            textbox.setEnabled(true);
            textbox.setText("");
            seleccion = "";
            seleccionText = "";
            botonActive.setChecked(false);
            botonActive.setTextColor(getContext().getResources().getColor(R.color.colorPrimary));
            fArray[0] = new InputFilter.LengthFilter(maxLength);
            textbox.setFilters(fArray);
            contenedor.setCounterMaxLength(maxLength);
            contenedor.setEndIconVisible(true);
            boton = null;
            Log.d("REVISION300", seleccion);
            if(evaluar) {
                FragmentEncuesta.actualiza(id);
            }
        } else if(idOpciones.contains(seleccion)){
            Log.d("REVISION200", "ACTS2");
            boton.setChecked(false);
            boton.setTextColor(getContext().getResources().getColor(R.color.colorPrimary));
            if(a[1].length() > maxLength) {
                contenedor.setCounterMaxLength(a[1].length());
                fArray[0] = new InputFilter.LengthFilter(a[1].length());
                textbox.setFilters(fArray);
            }
            textbox.setText(a[1]);
            seleccion = a[0];
            seleccionText = a[1];
            textbox.setEnabled(false);
            botonActive.setChecked(true);
            botonActive.setTextColor(getContext().getResources().getColor(R.color.colorAccent));
            contenedor.setEndIconVisible(false);
            contenedor.setEndIconActivated(false);
            boton = botonActive;
            Log.d("REVISION400", seleccion);
            if(evaluar) {
                FragmentEncuesta.actualiza(id);
            }
        } else {//SI NO EXISTIERA NADA en @seleccion
            Log.d("REVISION200", "ACTS3");
            if(a[1].length() > maxLength) {
                contenedor.setCounterMaxLength(a[1].length());
                fArray[0] = new InputFilter.LengthFilter(a[1].length());
                textbox.setFilters(fArray);
            }
            textbox.setText(a[1]);
            seleccion = a[0];
            seleccionText = a[1];
            textbox.setEnabled(false);
            botonActive.setChecked(true);
            botonActive.setTextColor(getContext().getResources().getColor(R.color.colorAccent));
            contenedor.setEndIconVisible(false);
            boton = botonActive;
            Log.d("REVISION400", seleccion);
            if(evaluar) {
                FragmentEncuesta.actualiza(id);
            }
        }
    }
}
