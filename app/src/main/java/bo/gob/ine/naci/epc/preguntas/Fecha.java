package bo.gob.ine.naci.epc.preguntas;

import android.app.DatePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import com.ibotta.android.support.pickerdialogs.SupportedDatePickerDialog;

import java.util.Calendar;

import bo.gob.ine.naci.epc.R;
import bo.gob.ine.naci.epc.entidades.Estado;
import bo.gob.ine.naci.epc.fragments.FragmentEncuesta;


/**
 * Created by INE.
 */
public class Fecha extends PreguntaView {
    DatePickerDialog recogerFecha;

    protected Context context;
    protected int posicion;
    protected int idSeccion;
    protected String cod;
    protected Boolean evaluar;
    protected TextView fecha;
    protected Calendar calendar;

    protected int mes;
    protected int dia;
    protected int anio;
    protected DatePickerDialog.OnDateSetListener setListener;
    protected static final String CERO = "0";
    protected static final String BARRA = "/";

    protected Button resp, noAplica, seNiega, noSabe;
    protected int[] nor = {0, 31, 59, 90, 120, 151, 181, 212, 243, 273, 304, 334};
    //protected int[] bis = {0, 31, 60, 91, 121, 152, 182, 213, 244, 274, 305, 335};
    protected int buttonsActive;

    public Fecha(final Context context, final int posicion, final int id, int idSeccion, String cod, String preg, int buttonsActive, String ayuda, final Boolean evaluar) {
        super(context, id, idSeccion, cod, preg, ayuda);
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

        fecha = new TextView(context);
        fecha.setText("Presione para selccionar la fecha");
        fecha.setTextAlignment(TEXT_ALIGNMENT_CENTER);
        fecha.setOnClickListener(new OnClickListener() {
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
                if(evaluar) {
                    FragmentEncuesta.ejecucion(context, id, posicion, "");
                }
            }
        });

        setListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                anio = year;
                mes = month;
                dia = dayOfMonth;
                int nuevoMes = month + 1;
                fecha.setText(dayOfMonth + BARRA + nuevoMes + BARRA + year);
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

        addView(fecha);

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
//        if(fecha.getText().toString().contains("/")){
//            return fecha.getText().toString().trim();
//        } else {
//            return "-1";
//        }
        int val = anio * 365 + anio / 4;
                /*if (valor.getYear() % 4 == 0) {
                    val += bis[valor.getMonth()];
                } else {*/
        val += nor[mes];
        //}
        val += dia;
        if (!fecha.getText().toString().contains("/")){
            return "-1";
        }
        return String.valueOf(val);
    }

    @Override
    public String getResp() {
        return fecha.getText().toString();
    }

    @Override
    public void setCodResp(String value) {

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
        fecha.requestFocus();
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