package bo.gob.ine.naci.epc.preguntas2;

import android.app.DatePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.ibotta.android.support.pickerdialogs.SupportedDatePickerDialog;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bo.gob.ine.naci.epc.R;
import bo.gob.ine.naci.epc.entidades.Estado;
import bo.gob.ine.naci.epc.fragments.FragmentEncuesta;
import bo.gob.ine.naci.epc.herramientas.Parametros;


/**
 * Created by INE.
 */
public class Fecha extends PreguntaView implements View.OnClickListener {
    DatePickerDialog recogerFecha;

    protected Context context;
    protected int posicion;
    protected int idSeccion;
    protected String cod;
    protected Boolean evaluar;
    protected TextView fecha;
    protected Button sfecha;
    protected Calendar calendar;

    protected int mes;
    protected int dia;
    protected int anio;
    protected DatePickerDialog.OnDateSetListener setListener;
    //    protected DatePicker.OnDateChangedListener setListener;
    protected static final String CERO = "0";
    protected static final String BARRA = "/";

    protected Button noAplica, seNiega, noSabe;
    protected ToggleButton resp;
    protected int[] nor = {0, 31, 59, 90, 120, 151, 181, 212, 243, 273, 304, 334};
    //protected int[] bis = {0, 31, 60, 91, 121, 152, 182, 213, 244, 274, 305, 335};
    protected Map<Integer, String> buttonsActive;

    protected LinearLayout buttons;
    protected List<String> btnOpciones = new ArrayList<String>();
    protected List<String> idOpciones = new ArrayList<String>();
    protected Map<String, View> guardaBotones = new HashMap<>();
    protected Map<Integer, String> guardaIdBotones = new HashMap<>();
    protected String seleccion = "";
    protected String seleccionText = "";
    protected ToggleButton boton;

    public Fecha(final Context context, final int posicion, final int id, int idSeccion, String cod, String preg, final Map<Integer, String> buttonsActive, String ayuda, final Boolean evaluar, final Boolean mostrarSeccion, String observacion) {
        super(context, id, idSeccion, cod, preg, ayuda, mostrarSeccion, observacion);
        this.buttonsActive = buttonsActive;

        this.cod = cod;
        this.context = context;
        this.posicion = posicion;
        this.idSeccion = idSeccion;
        this.evaluar = evaluar;

        calendar = Calendar.getInstance();

        mes = calendar.get(Calendar.MONTH);
        dia = calendar.get(Calendar.DAY_OF_MONTH);
        anio = calendar.get(Calendar.YEAR);

//        DatePicker datePicker = new DatePicker(context);
//        datePicker.init(anio, mes, dia, setListener);


        fecha = new TextView(context);
        fecha.setText("Presione para seleccionar la fecha");
        fecha.setTextSize(30f);
        fecha.setTextColor(context.getResources().getColor(R.color.color_texto));
        fecha.setTextAlignment(TEXT_ALIGNMENT_CENTER);
//        fecha.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                DatePickerDialog datePickerDialog = new DatePickerDialog(
//                        context, android.R.style.Theme_Holo_Dialog_MinWidth,
//                        setListener, anio, mes, dia);
//                datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//                datePickerDialog.show();
////                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
////                    obtenerFecha21();
////                } else {
////                    obtenerFecha();
////                }
//                if(evaluar) {
//                    FragmentEncuesta.actualiza(id);
//                }
//            }
//        });
        sfecha = new Button(context);
        sfecha.setText("Elegir fecha");
        sfecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        context, android.R.style.Theme_Holo_Dialog_MinWidth,
                        setListener, anio, mes, dia);
                datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                datePickerDialog.show();
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                    obtenerFecha21();
//                } else {
//                    obtenerFecha();
//                }
//                if(evaluar) {
//                    FragmentEncuesta.actualiza(id);
//                }
            }
        });

//        setListener = new DatePicker.OnDateChangedListener() {
//            @Override
//            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
//                anio = year;
//                mes = monthOfYear;
//                dia = dayOfMonth;
//                int nuevoMes = monthOfYear + 1;
//                fecha.setText(dayOfMonth + BARRA + nuevoMes + BARRA + year);
//                if(evaluar) {
//                    FragmentEncuesta.actualiza(id);
//                }
//            }
//        };
        setListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                anio = year;
                mes = month;
                dia = dayOfMonth;
                int nuevoMes = month + 1;
                fecha.setText(dayOfMonth + BARRA + nuevoMes + BARRA + year);
                if(evaluar) {
                    FragmentEncuesta.actualiza(id);
                }
            }
        };


//        valor.setOnTouchListener(new OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                if(evaluar) {
//                    FragmentEncuesta.ejecucion(context, id, posicion, "");
//                }
//                return false;
//            }
//        });

//        addView(datePicker);
        addView(fecha);
        addView(sfecha);

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

    }

    private void obtenerFecha21() {
        SupportedDatePickerDialog recogerFecha = new SupportedDatePickerDialog(context, R.style.SpinnerDatePickerDialogTheme,new SupportedDatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                //Esta variable lo que realiza es aumentar en uno el mes ya que comienza desde 0 = enero
                final int mesActual = month + 1;
                //Formateo el día obtenido: antepone el 0 si son menores de 10
                String diaFormateado = (dayOfMonth < 10) ? CERO + String.valueOf(dayOfMonth) : String.valueOf(dayOfMonth);
                //Formateo el mes obtenido: antepone el 0 si son menores de 10
                String mesFormateado = (mesActual < 10) ? CERO + String.valueOf(mesActual) : String.valueOf(mesActual);
                //Muestro la fecha con el formato deseado
                fecha.setText(diaFormateado + BARRA + mesFormateado + BARRA + year);
            }
            //Estos valores deben ir en ese orden, de lo contrario no mostrara la fecha actual
            /**
             *También puede cargar los valores que usted desee
             */
        }, anio, mes, dia);
        //Muestro el widget
        recogerFecha.show();
    }

    private void obtenerFecha() {
        DatePickerDialog recogerFecha = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                //Esta variable lo que realiza es aumentar en uno el mes ya que comienza desde 0 = enero
                final int mesActual = month + 1;
                //Formateo el día obtenido: antepone el 0 si son menores de 10
                String diaFormateado = (dayOfMonth < 10) ? CERO + String.valueOf(dayOfMonth) : String.valueOf(dayOfMonth);
                //Formateo el mes obtenido: antepone el 0 si son menores de 10
                String mesFormateado = (mesActual < 10) ? CERO + String.valueOf(mesActual) : String.valueOf(mesActual);
                //Muestro la fecha con el formato deseado
                fecha.setText(diaFormateado + BARRA + mesFormateado + BARRA + year);

            }
            //Estos valores deben ir en ese orden, de lo contrario no mostrara la fecha actual
            /**
             *También puede cargar los valores que usted desee
             */
        }, anio, mes, dia);
        //Muestro el widget
        recogerFecha.show();
    }

    @Override
    public String getCodResp() {
        int val = anio * 365 + anio / 4;
        val += nor[mes];
        val += dia;
        if (idOpciones.contains(seleccion)) {
            return seleccion;
        } else if (!fecha.getText().toString().contains("/")) {
            return "-1";
        } else {
            return String.valueOf(val);
        }
    }

    @Override
    public String getResp() {
        return fecha.getText().toString();
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

            fecha.setEnabled(false);
            sfecha.setEnabled(false);

        } else {
            fecha.setEnabled(true);
            sfecha.setEnabled(true);
            for(String opc : idOpciones) {
                botonActive = (ToggleButton) guardaBotones.get(opc);
                botonActive.setChecked(false);
                botonActive.setTextColor(getContext().getResources().getColor(R.color.colorPrimary));
            }
        }
    }

    @Override
    public void setResp(String value) {
        String[] dato = value.split("/");
        if (dato.length == 3) {
            dia = Integer.valueOf(dato[0]);
            mes = Integer.valueOf(dato[1]) - 1;
            anio = Integer.valueOf(dato[2]);
            fecha.setText(value);
        }
    }

    @Override
    public void setFocus() {
        sfecha.requestFocus();
    }

    @Override
    public void setEstado(Estado value) {
        super.setEstado(value);
    }

    public void setVisible (View v, boolean sw) {
        if(sw) {
            v.setVisibility(VISIBLE);
        } else {
            v.setVisibility(INVISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        String respuesta = buttonsActive.get(v.getId());
        String[] a = respuesta.split("\\|");

        String botonPresionado = guardaIdBotones.get(v.getId());
        ToggleButton botonActive = (ToggleButton) guardaBotones.get(botonPresionado);

        if(seleccion.equals(a[0])){
            fecha.setEnabled(true);
            sfecha.setEnabled(true);
            fecha.setText("Presione para seleccionar la fecha");
            seleccion = "";
            seleccionText = "";
            botonActive.setChecked(false);
            botonActive.setTextColor(getContext().getResources().getColor(R.color.colorPrimary));
            boton = null;
            if(evaluar) {
                FragmentEncuesta.actualiza(id);
            }
        } else if(idOpciones.contains(seleccion)){
            boton.setChecked(false);
            boton.setTextColor(getContext().getResources().getColor(R.color.colorPrimary));
            fecha.setText(a[1]);
            seleccion = a[0];
            seleccionText = a[1];
            fecha.setEnabled(false);
            sfecha.setEnabled(false);
            botonActive.setChecked(true);
            botonActive.setTextColor(getContext().getResources().getColor(R.color.colorAccent));
            boton = botonActive;
            if(evaluar) {
                FragmentEncuesta.actualiza(id);
            }
        } else {//SI NO EXISTIERA NADA en @seleccion
            fecha.setText(a[1]);
            seleccion = a[0];
            seleccionText = a[1];
            fecha.setEnabled(false);
            sfecha.setEnabled(false);
            botonActive.setChecked(true);
            botonActive.setTextColor(getContext().getResources().getColor(R.color.colorAccent));
            boton = botonActive;
            if(evaluar) {
                FragmentEncuesta.actualiza(id);
            }
        }
    }
}