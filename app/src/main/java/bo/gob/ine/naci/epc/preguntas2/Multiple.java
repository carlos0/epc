package bo.gob.ine.naci.epc.preguntas2;

import android.content.Context;
import android.graphics.Color;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;

import java.util.Map;

import bo.gob.ine.naci.epc.entidades.Estado;
import bo.gob.ine.naci.epc.herramientas.Parametros;

/**
 * Created by INE.
 */
public class Multiple extends PreguntaView {
    protected Map<Integer, String> opciones;
    protected CheckBox[] elements;
    protected Button resp, noAplica, seNiega, noSabe;
    protected int buttonsActive;

    public Multiple(Context context, int id, int idSeccion, String cod, String preg, Map<Integer, String> opciones, int buttonsActive, String ayuda, final Boolean mostrarSeccion, String observacion) {
        super(context, id, idSeccion, cod, preg, ayuda, mostrarSeccion, observacion);
        this.opciones = opciones;
        this.buttonsActive = buttonsActive;

        int i = 0;
        elements = new CheckBox[opciones.size()];
        for (Integer key : opciones.keySet()) {
            CheckBox cb = new CheckBox(context);
            String opcion = opciones.get(key);
            String[] a = opcion.split("\\|");
            cb.setText(Html.fromHtml(a[1]));
            cb.setTextSize(Parametros.FONT_RESP);
            cb.setId(key);
            addView(cb);
            elements[i] = cb;
            i++;
        }

        LinearLayout buttons = new LinearLayout(context);
        buttons.setOrientation(LinearLayout.HORIZONTAL);
        buttons.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
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
                Multiple.this.estado = Estado.INSERTADO;
                setEnabledCB(true);
                resp.setTextColor(Color.BLUE);
                noAplica.setTextColor(Color.BLACK);
                seNiega.setTextColor(Color.BLACK);
                noSabe.setTextColor(Color.BLACK);
            }
        });
        noAplica.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Multiple.this.estado = Estado.NOAPLICA;
                setEnabledCB(false);
                resp.setTextColor(Color.BLACK);
                noAplica.setTextColor(Color.BLUE);
                seNiega.setTextColor(Color.BLACK);
                noSabe.setTextColor(Color.BLACK);
            }
        });
        seNiega.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Multiple.this.estado = Estado.SENIEGA;
                setEnabledCB(false);
                resp.setTextColor(Color.BLACK);
                noAplica.setTextColor(Color.BLACK);
                seNiega.setTextColor(Color.BLUE);
                noSabe.setTextColor(Color.BLACK);
            }
        });
        noSabe.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Multiple.this.estado = Estado.NOSABE;
                setEnabledCB(false);
                resp.setTextColor(Color.BLACK);
                noAplica.setTextColor(Color.BLACK);
                seNiega.setTextColor(Color.BLACK);
                noSabe.setTextColor(Color.BLUE);
            }
        });

        if ( buttonsActive != 0 ) {
//            addView(buttons);
            //setVisible(resp, (buttonsActive & 3) == 3);
            setVisible(noAplica, (buttonsActive & 4) == 4);
            setVisible(seNiega, (buttonsActive & 2) == 2);
            setVisible(noSabe, (buttonsActive & 1) == 1);
        }
    }

    protected void setEnabledCB(boolean flag) {
        for (CheckBox cb : elements) {
            cb.setEnabled(flag);
        }
    }

//    @Override
//    public long getIdResp() {
//        int resp = 0;
//        for (int i = 0; i < this.getChildCount(); i++) {
//            if (this.getChildAt(i) instanceof CheckBox) {
//                CheckBox cb = (CheckBox) this.getChildAt(i);
//                if (cb.isChecked()) {
//                    resp = cb.getId();
//                    break;
//                }
//            }
//        }
//        return resp;
//    }

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
                String resp = "";
                for (int i = 0; i < this.getChildCount(); i++) {
                    if (this.getChildAt(i) instanceof CheckBox) {
                        CheckBox cb = (CheckBox) this.getChildAt(i);
                        if (cb.isChecked()) {
                            String[] a = opciones.get(cb.getId()).split("\\|");
                            resp = resp + cb.getId() + "," + a[0] + ";";
                        }
                    }
                }
                if (resp.length() > 0) {
                    return resp.substring(0, resp.length() - 1);
                } else {
                    return "-1";
                }
        }
    }

    @Override
    public void setCodResp(String cod) {
        String[] a = cod.split(";");
        int[] b = new int[a.length];
        for (int i = 0; i < a.length; i++) {
            b[i] = Integer.parseInt(a[i].split(",")[0]);
        }
        for (int i = 0; i < this.getChildCount(); i++) {
            if (this.getChildAt(i) instanceof CheckBox) {
                CheckBox cb = (CheckBox) this.getChildAt(i);
                cb.setChecked(false);
                for (int e : b) {
                    if (cb.getId() == e) {
                        cb.setChecked(true);
                        break;
                    }
                }
            }
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
                String resp = "";
                for (int i = 0; i < this.getChildCount(); i++) {
                    if (this.getChildAt(i) instanceof CheckBox) {
                        CheckBox cb = (CheckBox) this.getChildAt(i);
                        if (cb.isChecked()) {
                            String[] a = opciones.get(cb.getId()).split("\\|");
                            resp = resp + a[1] + "; ";
                        }
                    }
                }
                if (resp.length() > 0) {
                    resp = resp.substring(0, resp.length() - 1);
                }
                return resp;
        }
    }

    @Override
    public void setResp(String value) {}

    @Override
    public void setFocus() { }

    @Override
    public void setEstado(Estado value) {
        super.setEstado(value);
        switch (estado) {
            case NOAPLICA:
                setEnabledCB(false);
                resp.setTextColor(Color.BLACK);
                noAplica.setTextColor(Color.BLUE);
                seNiega.setTextColor(Color.BLACK);
                noSabe.setTextColor(Color.BLACK);
                break;
            case SENIEGA:
                setEnabledCB(false);
                resp.setTextColor(Color.BLACK);
                noAplica.setTextColor(Color.BLACK);
                seNiega.setTextColor(Color.BLUE);
                noSabe.setTextColor(Color.BLACK);
                break;
            case NOSABE:
                setEnabledCB(false);
                resp.setTextColor(Color.BLACK);
                noAplica.setTextColor(Color.BLACK);
                seNiega.setTextColor(Color.BLACK);
                noSabe.setTextColor(Color.BLUE);
                break;
            default:
                setEnabledCB(true);
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
