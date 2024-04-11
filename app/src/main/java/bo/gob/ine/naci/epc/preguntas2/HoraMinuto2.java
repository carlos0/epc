package bo.gob.ine.naci.epc.preguntas2;

import android.content.Context;
import android.graphics.Color;
import android.text.InputFilter;
import android.text.InputType;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import bo.gob.ine.naci.epc.entidades.Estado;
import bo.gob.ine.naci.epc.herramientas.Parametros;


/**
 * Created by INE.
 */
public class HoraMinuto2 extends PreguntaView {
    EditText hora;
    EditText minuto;

    protected Context context;
    protected int posicion;
    protected int idSeccion;
    protected String cod;
    protected Boolean evaluar;

    protected Button resp, noAplica, seNiega, noSabe;
    protected int buttonsActive;

    public HoraMinuto2(Context context, final int posicion, int id, int idSeccion, String cod, String preg, int maxLength, int buttonsActive, String ayuda, final Boolean evaluar, final Boolean mostrarSeccion, String observacion) {
        super(context, id, idSeccion, cod, preg, ayuda, mostrarSeccion, observacion);
        this.buttonsActive = buttonsActive;

        this.cod = cod;
        this.context = context;
        this.posicion = posicion;
        this.idSeccion = idSeccion;
        this.evaluar = evaluar;

        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.weight = 1;
        TextView anioTV = new TextView(context);
        anioTV.setText("Horas:");
        anioTV.setTextSize(Parametros.FONT_RESP);
        linearLayout.addView(anioTV);
        hora = new EditText(context);
        hora.setSingleLine();
        InputFilter[] fArray = new InputFilter[1];
        if (maxLength > 0) {
            fArray[0] = new InputFilter.LengthFilter(maxLength);
        } else {
            fArray[0] = new InputFilter.LengthFilter(2);
        }
        hora.setFilters(fArray);
        hora.setTextSize(Parametros.FONT_RESP);
        hora.setInputType(InputType.TYPE_CLASS_NUMBER);
        hora.setLayoutParams(layoutParams);
        linearLayout.addView(hora);
        TextView mesTV = new TextView(context);
        mesTV.setText("Minutos:");
        mesTV.setTextSize(Parametros.FONT_RESP);
        linearLayout.addView(mesTV);
        minuto = new EditText(context);
        minuto.setSingleLine();
        fArray = new InputFilter[1];
        fArray[0] = new InputFilter.LengthFilter(2);
        minuto.setFilters(fArray);
        minuto.setTextSize(Parametros.FONT_RESP);
        minuto.setInputType(InputType.TYPE_CLASS_NUMBER);
        minuto.setLayoutParams(layoutParams);
        linearLayout.addView(minuto);
        addView(linearLayout);

        LinearLayout buttons = new LinearLayout(context);
        buttons.setOrientation(LinearLayout.HORIZONTAL);
        buttons.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        LayoutParams layoutParams2 = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
        layoutParams2.weight = 1;
        resp = new Button(context);
        resp.setText("Resp");
        resp.setLayoutParams(layoutParams2);
        resp.setTextColor(Color.BLUE);
        buttons.addView(resp);
        noAplica = new Button(context);
        noAplica.setText("No aplica");
        noAplica.setLayoutParams(layoutParams2);
        buttons.addView(noAplica);
        seNiega = new Button(context);
        seNiega.setText("Se niega");
        seNiega.setLayoutParams(layoutParams2);
        buttons.addView(seNiega);
        noSabe = new Button(context);
        noSabe.setText("No sabe");
        noSabe.setLayoutParams(layoutParams2);
        buttons.addView(noSabe);
        resp.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                HoraMinuto2.this.estado = Estado.INSERTADO;
                hora.setEnabled(true);
                minuto.setEnabled(true);
                resp.setTextColor(Color.BLUE);
                noAplica.setTextColor(Color.BLACK);
                seNiega.setTextColor(Color.BLACK);
                noSabe.setTextColor(Color.BLACK);
            }
        });
        noAplica.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                HoraMinuto2.this.estado = Estado.NOAPLICA;
                hora.setEnabled(false);
                minuto.setEnabled(false);
                resp.setTextColor(Color.BLACK);
                noAplica.setTextColor(Color.BLUE);
                seNiega.setTextColor(Color.BLACK);
                noSabe.setTextColor(Color.BLACK);
            }
        });
        seNiega.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                HoraMinuto2.this.estado = Estado.SENIEGA;
                hora.setEnabled(false);
                minuto.setEnabled(false);
                resp.setTextColor(Color.BLACK);
                noAplica.setTextColor(Color.BLACK);
                seNiega.setTextColor(Color.BLUE);
                noSabe.setTextColor(Color.BLACK);
            }
        });
        noSabe.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                HoraMinuto2.this.estado = Estado.NOSABE;
                hora.setEnabled(false);
                minuto.setEnabled(false);
                resp.setTextColor(Color.BLACK);
                noAplica.setTextColor(Color.BLACK);
                seNiega.setTextColor(Color.BLACK);
                noSabe.setTextColor(Color.BLUE);
            }
        });

        if ( buttonsActive != 0 ) {
            addView(buttons);
            //setVisible(resp, (buttonsActive & 3) == 3);
            setVisible(noAplica, (buttonsActive & 4) == 4);
            setVisible(seNiega, (buttonsActive & 2) == 2);
            setVisible(noSabe, (buttonsActive & 1) == 1);
        }
        hora.requestFocus();
    }

    @Override
    public String getCodResp() {
        switch (estado) {
            case NOAPLICA:
                return "997";
            case SENIEGA:
                return "998";
            case NOSABE:
                return "999";
            default:
                if (minuto.getText().toString().equals("") || hora.getText().toString().equals("")) {
                    return "-1";
                }
                int minute = minuto.getText().toString().equals("") ? 0 : Integer.valueOf(minuto.getText().toString());
                if (minute > 59) {
                    return "-2";
                }
                int hour = hora.getText().toString().equals("") ? 0 : Integer.valueOf(hora.getText().toString());
                int val = hour * 60 + minute;
                return String.valueOf(val);
        }
    }

    @Override
    public String getResp() {
        switch (estado) {
            case NOAPLICA:
                return "No aplica";
            case SENIEGA:
                return "Se niega";
            case NOSABE:
                return "No sabe";
            default:
                int minut = minuto.getText().toString().equals("") ? 0 : Integer.valueOf(minuto.getText().toString());
                int hour = hora.getText().toString().equals("") ? 0 : Integer.valueOf(hora.getText().toString());
                String time = String.valueOf(hour);
                if (minut < 10) {
                    time = time + ":0" + minut;
                } else {
                    time = time + ":" + minut;
                }
                return time;
        }
    }

    @Override
    public void setResp(String value) {
        String[] fecha = value.split(":");
        if (fecha.length == 2) {
            hora.setText(fecha[0]);
            minuto.setText(fecha[1]);
        }
    }

    @Override
    public void setFocus() {
        hora.requestFocus();
    }

    @Override
    public void setEstado(Estado value) {
        super.setEstado(value);
        switch (estado) {
            case NOAPLICA:
                hora.setEnabled(false);
                minuto.setEnabled(false);
                resp.setTextColor(Color.BLACK);
                noAplica.setTextColor(Color.BLUE);
                seNiega.setTextColor(Color.BLACK);
                noSabe.setTextColor(Color.BLACK);
                break;
            case SENIEGA:
                hora.setEnabled(false);
                minuto.setEnabled(false);
                resp.setTextColor(Color.BLACK);
                noAplica.setTextColor(Color.BLACK);
                seNiega.setTextColor(Color.BLUE);
                noSabe.setTextColor(Color.BLACK);
                break;
            case NOSABE:
                hora.setEnabled(false);
                minuto.setEnabled(false);
                resp.setTextColor(Color.BLACK);
                noAplica.setTextColor(Color.BLACK);
                seNiega.setTextColor(Color.BLACK);
                noSabe.setTextColor(Color.BLUE);
                break;
            default:
                hora.setEnabled(true);
                minuto.setEnabled(true);
                resp.setTextColor(Color.BLUE);
                noAplica.setTextColor(Color.BLACK);
                seNiega.setTextColor(Color.BLACK);
                noSabe.setTextColor(Color.BLACK);
                break;
        }
    }
}