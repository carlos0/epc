package bo.gob.ine.naci.epc.preguntas;

import android.content.Context;
import android.graphics.Color;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
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
public class Cantidad extends PreguntaView {
    protected EditText cantidad, unidad;
    protected Button resp, noAplica, seNiega, noSabe;
    protected int buttonsActive;

    public Cantidad(Context context, int id, int idSeccion, String cod, String preg, int buttonsActive, String ayuda) {
        super(context, id, idSeccion, cod, preg, ayuda);
        this.buttonsActive = buttonsActive;

        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.weight = 1;
        TextView anioTV = new TextView(context);
        anioTV.setText("Cantidad:");
        anioTV.setTextSize(Parametros.FONT_RESP);
        linearLayout.addView(anioTV);
        cantidad = new EditText(context);
        cantidad.setSingleLine();
        cantidad.setTextSize(Parametros.FONT_RESP);
        cantidad.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        cantidad.setLayoutParams(layoutParams);
        linearLayout.addView(cantidad);
        TextView mesTV = new TextView(context);
        mesTV.setText("Unidad:");
        mesTV.setTextSize(Parametros.FONT_RESP);
        linearLayout.addView(mesTV);
        unidad = new EditText(context);
        unidad.setSingleLine();
        unidad.setTextSize(Parametros.FONT_RESP);
        unidad.setFilters(new InputFilter[]{
                new InputFilter() {
                    public CharSequence filter(CharSequence src, int start,
                                               int end, Spanned dst, int dstart, int dend) {
                        if (src.equals("")) {
                            return src;
                        }
                        if (src.toString().matches("[a-záéíóúüñA-ZÁÉÍÓÚÜÑ0-9().,' ]+")) {
                            return src;
                        }
                        return "";
                    }
                }
        });
        unidad.setLayoutParams(layoutParams);
        linearLayout.addView(unidad);
        addView(linearLayout);

        LinearLayout buttons = new LinearLayout(context);
        buttons.setOrientation(LinearLayout.HORIZONTAL);
        buttons.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        layoutParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
        layoutParams.weight = 1;
        resp = new Button(context);
        resp.setText("Resp");
        resp.setLayoutParams(layoutParams);
        resp.setTextColor(Color.BLUE);
        buttons.addView(resp);
        noAplica = new Button(context);
        noAplica.setText("No aplica");
        noAplica.setLayoutParams(layoutParams);
        buttons.addView(noAplica);
        seNiega = new Button(context);
        seNiega.setText("Se niega");
        seNiega.setLayoutParams(layoutParams);
        buttons.addView(seNiega);
        noSabe = new Button(context);
        noSabe.setText("No sabe");
        noSabe.setLayoutParams(layoutParams);
        buttons.addView(noSabe);
        resp.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Cantidad.this.estado = Estado.INSERTADO;
                cantidad.setEnabled(true);
                unidad.setEnabled(true);
                if (cantidad.getText().toString().equals("No aplica") ||
                        cantidad.getText().toString().equals("Se niega") ||
                        cantidad.getText().toString().equals("No sabe")) {
                    cantidad.setText("");
                    unidad.setText("");
                }
                resp.setTextColor(Color.BLUE);
                noAplica.setTextColor(Color.BLACK);
                seNiega.setTextColor(Color.BLACK);
                noSabe.setTextColor(Color.BLACK);
            }
        });
        noAplica.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Cantidad.this.estado = Estado.NOAPLICA;
                cantidad.setEnabled(false);
                unidad.setEnabled(false);
                cantidad.setText("No aplica");
                unidad.setText("No aplica");
                resp.setTextColor(Color.BLACK);
                noAplica.setTextColor(Color.BLUE);
                seNiega.setTextColor(Color.BLACK);
                noSabe.setTextColor(Color.BLACK);
            }
        });
        seNiega.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Cantidad.this.estado = Estado.SENIEGA;
                cantidad.setEnabled(false);
                unidad.setEnabled(false);
                cantidad.setText("Se niega");
                unidad.setText("Se niega");
                resp.setTextColor(Color.BLACK);
                noAplica.setTextColor(Color.BLACK);
                seNiega.setTextColor(Color.BLUE);
                noSabe.setTextColor(Color.BLACK);
            }
        });
        noSabe.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Cantidad.this.estado = Estado.NOSABE;
                cantidad.setEnabled(false);
                unidad.setEnabled(false);
                cantidad.setText("No sabe");
                unidad.setText("No sabe");
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
        cantidad.requestFocus();
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
                float val = cantidad.getText().toString().equals("") || unidad.getText().toString().equals("") ? -1 : Float.valueOf(cantidad.getText().toString());
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
                return cantidad.getText().toString() + "-" + unidad.getText().toString();
        }
    }

    @Override
    public void setResp(String value) {
        String[] partes = value.split("-");
        if (partes.length == 2) {
            cantidad.setText(partes[0]);
            unidad.setText(partes[1]);
        }
    }

    @Override
    public void setFocus() {
        cantidad.requestFocus();
    }

    @Override
    public void setEstado(Estado value) {
        super.setEstado(value);
        switch (estado) {
            case NOAPLICA:
                cantidad.setEnabled(false);
                unidad.setEnabled(false);
                resp.setTextColor(Color.BLACK);
                noAplica.setTextColor(Color.BLUE);
                seNiega.setTextColor(Color.BLACK);
                noSabe.setTextColor(Color.BLACK);
                break;
            case SENIEGA:
                cantidad.setEnabled(false);
                unidad.setEnabled(false);
                resp.setTextColor(Color.BLACK);
                noAplica.setTextColor(Color.BLACK);
                seNiega.setTextColor(Color.BLUE);
                noSabe.setTextColor(Color.BLACK);
                break;
            case NOSABE:
                cantidad.setEnabled(false);
                unidad.setEnabled(false);
                resp.setTextColor(Color.BLACK);
                noAplica.setTextColor(Color.BLACK);
                seNiega.setTextColor(Color.BLACK);
                noSabe.setTextColor(Color.BLUE);
                break;
            default:
                cantidad.setEnabled(true);
                unidad.setEnabled(true);
                resp.setTextColor(Color.BLUE);
                noAplica.setTextColor(Color.BLACK);
                seNiega.setTextColor(Color.BLACK);
                noSabe.setTextColor(Color.BLACK);
                break;
        }
    }
}
