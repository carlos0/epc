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
public class MesAnio extends PreguntaView {
    EditText anio;
    EditText mes;
    protected Button resp, noAplica, seNiega, noSabe;
    protected int buttonsActive;

    public MesAnio(Context context, int id, int idSeccion, String cod, String preg, int buttonsActive, String ayuda, final Boolean mostrarSeccion, String observacion) {
        super(context, id, idSeccion, cod, preg, ayuda, mostrarSeccion, observacion);
        this.buttonsActive = buttonsActive;

        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.weight = 1;
        TextView anioTV = new TextView(context);
        anioTV.setText("Años:");
        anioTV.setTextSize(Parametros.FONT_RESP);
        linearLayout.addView(anioTV);
        anio = new EditText(context);
        anio.setSingleLine();
        InputFilter[] fArray = new InputFilter[1];
        fArray[0] = new InputFilter.LengthFilter(4);
        anio.setFilters(fArray);
        anio.setTextSize(Parametros.FONT_RESP);
        anio.setInputType(InputType.TYPE_CLASS_NUMBER);
        anio.setLayoutParams(layoutParams);
        linearLayout.addView(anio);
        TextView mesTV = new TextView(context);
        mesTV.setText("Meses:");
        mesTV.setTextSize(Parametros.FONT_RESP);
        linearLayout.addView(mesTV);
        mes = new EditText(context);
        mes.setSingleLine();
        fArray = new InputFilter[1];
        fArray[0] = new InputFilter.LengthFilter(2);
        mes.setFilters(fArray);
        mes.setTextSize(Parametros.FONT_RESP);
        mes.setInputType(InputType.TYPE_CLASS_NUMBER);
        mes.setLayoutParams(layoutParams);
        linearLayout.addView(mes);
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
                MesAnio.this.estado = Estado.INSERTADO;
                anio.setEnabled(true);
                mes.setEnabled(true);
                resp.setTextColor(Color.BLUE);
                noAplica.setTextColor(Color.BLACK);
                seNiega.setTextColor(Color.BLACK);
                noSabe.setTextColor(Color.BLACK);
            }
        });
        noAplica.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                MesAnio.this.estado = Estado.NOAPLICA;
                anio.setEnabled(false);
                mes.setEnabled(false);
                resp.setTextColor(Color.BLACK);
                noAplica.setTextColor(Color.BLUE);
                seNiega.setTextColor(Color.BLACK);
                noSabe.setTextColor(Color.BLACK);
            }
        });
        seNiega.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                MesAnio.this.estado = Estado.SENIEGA;
                anio.setEnabled(false);
                mes.setEnabled(false);
                resp.setTextColor(Color.BLACK);
                noAplica.setTextColor(Color.BLACK);
                seNiega.setTextColor(Color.BLUE);
                noSabe.setTextColor(Color.BLACK);
            }
        });
        noSabe.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                MesAnio.this.estado = Estado.NOSABE;
                anio.setEnabled(false);
                mes.setEnabled(false);
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

        anio.requestFocus();
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
                if (mes.getText().toString().equals("") || anio.getText().toString().equals("")) {
                    return "-1";
                }
                int month = mes.getText().toString().equals("") ? 0 : Integer.valueOf(mes.getText().toString());
                if (month > 12) {
                    return "-2";
                }
                int year = anio.getText().toString().equals("") ? 0 : Integer.valueOf(anio.getText().toString());
                int val = year * 12 + month;
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
                int month = mes.getText().toString().equals("") ? 0 : Integer.valueOf(mes.getText().toString());
                int year = anio.getText().toString().equals("") ? 0 : Integer.valueOf(anio.getText().toString());
                String fecha = String.valueOf(year);
                if (month < 10) {
                    fecha = fecha + "-0" + month;
                } else {
                    fecha = fecha + "-" + month;
                }
                return fecha;
        }
    }

    @Override
    public void setResp(String value) {
        String[] fecha = value.split("-");
        if (fecha.length == 2) {
            anio.setText(fecha[0]);
            mes.setText(fecha[1]);
        }
    }

    @Override
    public void setFocus() {
        anio.requestFocus();
    }

    @Override
    public void setEstado(Estado value) {
        super.setEstado(value);
        switch (estado) {
            case NOAPLICA:
                anio.setEnabled(false);
                mes.setEnabled(false);
                resp.setTextColor(Color.BLACK);
                noAplica.setTextColor(Color.BLUE);
                seNiega.setTextColor(Color.BLACK);
                noSabe.setTextColor(Color.BLACK);
                break;
            case SENIEGA:
                anio.setEnabled(false);
                mes.setEnabled(false);
                resp.setTextColor(Color.BLACK);
                noAplica.setTextColor(Color.BLACK);
                seNiega.setTextColor(Color.BLUE);
                noSabe.setTextColor(Color.BLACK);
                break;
            case NOSABE:
                anio.setEnabled(false);
                mes.setEnabled(false);
                resp.setTextColor(Color.BLACK);
                noAplica.setTextColor(Color.BLACK);
                seNiega.setTextColor(Color.BLACK);
                noSabe.setTextColor(Color.BLUE);
                break;
            default:
                anio.setEnabled(true);
                mes.setEnabled(true);
                resp.setTextColor(Color.BLUE);
                noAplica.setTextColor(Color.BLACK);
                seNiega.setTextColor(Color.BLACK);
                noSabe.setTextColor(Color.BLACK);
                break;
        }
    }

    public void setVisible (View v, boolean sw) {
        if(sw) {
            v.setVisibility(VISIBLE);
        } else {
            v.setVisibility(INVISIBLE);
        }
    }
}
