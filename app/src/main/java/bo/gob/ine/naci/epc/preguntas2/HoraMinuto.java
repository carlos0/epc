package bo.gob.ine.naci.epc.preguntas2;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;

import bo.gob.ine.naci.epc.entidades.Estado;
import bo.gob.ine.naci.epc.fragments.FragmentEncuesta;


/**
 * Created by INE.
 */
public class HoraMinuto extends PreguntaView {
    DatePickerDialog recogerFecha;

    protected Context context;
    protected int posicion;
    protected int idSeccion;
    protected String cod;
    protected Boolean evaluar;
    protected TextView tiempo;
    protected Calendar calendar;

    protected int hora;
    protected int minuto;
    protected TimePickerDialog.OnTimeSetListener setListener;
    protected static final String CERO = "0";
    protected static final String PUNTO = ":";

    protected Button resp, noAplica, seNiega, noSabe;
    protected int[] nor = {0, 31, 59, 90, 120, 151, 181, 212, 243, 273, 304, 334};
    //protected int[] bis = {0, 31, 60, 91, 121, 152, 182, 213, 244, 274, 305, 335};
    protected int buttonsActive;

    public HoraMinuto(final Context context, final int posicion, final int id, int idSeccion, String cod, String preg, int buttonsActive, String ayuda, final Boolean evaluar, final Boolean mostrarSeccion, String observacion) {
        super(context, id, idSeccion, cod, preg, ayuda, mostrarSeccion, observacion);
        this.buttonsActive = buttonsActive;

        this.cod = cod;
        this.context = context;
        this.posicion = posicion;
        this.idSeccion = idSeccion;
        this.evaluar = evaluar;

        calendar = Calendar.getInstance();

        hora = calendar.get(Calendar.HOUR_OF_DAY);
        minuto = calendar.get(Calendar.MINUTE);

        tiempo = new TextView(context);
        tiempo.setText("Presione para seleccionar la hora");
        tiempo.setTextAlignment(TEXT_ALIGNMENT_CENTER);
        tiempo.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                TimePickerDialog timePickerDialog = new TimePickerDialog(
                        context, android.R.style.Theme_Holo_Dialog_MinWidth,
                        setListener, hora, minuto, true);
                timePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                timePickerDialog.show();
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                    obtenerFecha21();
//                } else {
//                    obtenerFecha();
//                }
                if(evaluar) {
                    FragmentEncuesta.ejecucion(context, id, posicion, "");
                }
            }
        });

        setListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                hora = hourOfDay;
                minuto = minute;
                tiempo.setText(hourOfDay + PUNTO + minute);
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

        addView(tiempo);

    }

    @Override
    public String getCodResp() {
        if(tiempo.getText().toString().contains(":")){
            int conver;
            String[] dato = tiempo.getText().toString().split(":");
            conver = Integer.valueOf(dato[0]) * 60;
            conver = conver + Integer.valueOf(dato[1]);
            return String.valueOf(conver);
        }else {
            return "-1";
        }
    }

    @Override
    public String getResp() {
        return tiempo.getText().toString();
    }

    @Override
    public void setCodResp(String value) {

    }

    @Override
    public void setResp(String value) {
        String[] dato = value.split(":");
        if (dato.length == 2) {
            hora = Integer.valueOf(dato[0]);
            minuto = Integer.valueOf(dato[1]);
            tiempo.setText(value);
        }
    }

    @Override
    public void setFocus() {
        tiempo.requestFocus();
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
}