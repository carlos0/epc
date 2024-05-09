package bo.gob.ine.naci.epc.preguntas2;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.core.content.res.ResourcesCompat;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.Map;

import bo.gob.ine.naci.epc.R;
import bo.gob.ine.naci.epc.entidades.Estado;
import bo.gob.ine.naci.epc.herramientas.ActionBarActivityMessage;
import bo.gob.ine.naci.epc.herramientas.Parametros;

public abstract class PreguntaView extends LinearLayout {
    protected int id;
    protected int idSeccion;
    protected String cod;
    protected String preg;
    protected TextView pregTextView;
    protected TextView seccionTextView;
    protected TextView groupTextView;
    protected String observacion;
    protected String ayuda;
    protected String obsPreg;
    protected Estado estado;
//    protected Button btnAyuda;
    protected ImageButton btnAyuda;
    protected ImageButton btnObservacion;
    protected ActionBarActivityMessage obs = new ActionBarActivityMessage();
    int idAsignacion = 0;
    int correlativo = 0;
    protected boolean observado;
    LinearLayout pregunta;
    LinearLayout respuesta;
    LinearLayout botones;


    public PreguntaView(final Context context, int id, int idSeccion, final String cod, String preg, final String ayuda, boolean mostrarSeccion, final String obs) {
        //CONSTRUCTOR PARA UNA PREGUNTA
        super(context);

        if(idSeccion == 166){
            setOrientation(LinearLayout.VERTICAL);
            setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            setClickable(true);

            this.id = id;
            this.idSeccion = idSeccion;
            this.cod = cod;
            this.preg = preg;
            this.ayuda = (ayuda == "") ? null : ayuda;
            this.obsPreg = obs;
            observacion = "";
            estado = Estado.INSERTADO;
            this.observado = false;

            if(preg.startsWith("*")) {
                groupTextView = new TextView(context);
                groupTextView.setText(Html.fromHtml("<b><font color = #FFFFFF>" + preg.split("\\*")[1].toUpperCase() + "</font></b>"));
                groupTextView.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                groupTextView.setTextSize(Parametros.FONT_PREG);
                LayoutParams lp_text = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
                lp_text.gravity = TEXT_ALIGNMENT_CENTER;
                groupTextView.setLayoutParams(lp_text);
                addView(groupTextView);
            }

            LinearLayout preguntarespuesta = new LinearLayout(context);
            preguntarespuesta.setWeightSum(2);
            preguntarespuesta.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            preguntarespuesta.setPadding(20, 20, 20, 0);
            preguntarespuesta.setOrientation(LinearLayout.HORIZONTAL);

            pregunta = new LinearLayout(context);
            LayoutParams layoutParams_pregunta = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
            layoutParams_pregunta.weight = 1;
            pregunta.setLayoutParams(layoutParams_pregunta);

            preg = preg.startsWith("*") ? preg.split("\\*")[2] : preg;
            pregTextView = new TextView(context);
//            pregTextView.setText(Html.fromHtml("<b><font color = #FF5252>" + cod.toUpperCase() + "</font></b>: " + preg));
            pregTextView.setText(Html.fromHtml(preg));
            pregTextView.setTextColor(getResources().getColor(R.color.colorPrimary));
            pregTextView.setTextSize(Parametros.FONT_PREG);
            Typeface typeface = ResourcesCompat.getFont(context, R.font.quicksand_medium);
            pregTextView.setTypeface(typeface);

            LayoutParams layoutParams_txt = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
            layoutParams_txt.weight = 1;
            layoutParams_txt.setMargins(0, 0, 0, 0);
            pregunta.addView(pregTextView, layoutParams_txt);
//            addView(pregTextView, layoutParams_txt);

            LinearLayout.LayoutParams layoutParams_btn = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            layoutParams_btn.gravity = Gravity.END;
            botones = new LinearLayout(getContext());
            botones.setOrientation(LinearLayout.HORIZONTAL);
            botones.setLayoutParams(layoutParams_btn);

            if (ayuda != null) {
                btnAyuda = new ImageButton(context);
                btnAyuda.setImageDrawable(getResources().getDrawable(R.drawable.ic_info_outline_black_24dp));
                btnAyuda.setBackground(null);
                btnAyuda.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        MaterialAlertDialogBuilder mat = new MaterialAlertDialogBuilder(context);
                        mat.setTitle(Html.fromHtml("<span style=\"color: " + R.color.colorPrimary + ";\"><strong>AYUDA - </strong>"+ cod.toUpperCase() +"</span>"));
                        mat.setMessage(Html.fromHtml(ayuda).toString());
                        mat.setIcon(R.drawable.info);
                        mat.setPositiveButton("Entendido", null);
                        mat.create();
                        mat.show();
                    }
                });
                LayoutParams layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                layoutParams.gravity = Gravity.RIGHT;
                btnAyuda.setLayoutParams(layoutParams);
                botones.addView(btnAyuda);
            }

            respuesta = new LinearLayout(context);
            respuesta.setOrientation(LinearLayout.VERTICAL);
            LayoutParams layoutParams_respuesta = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
            layoutParams_respuesta.weight = 1;
            respuesta.setLayoutParams(layoutParams_respuesta);

            preguntarespuesta.addView(pregunta);
            preguntarespuesta.addView(respuesta);
            addView(preguntarespuesta);
        } else {
            setOrientation(LinearLayout.VERTICAL);
            setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            setPadding(80, 20, 80, 20);
            setClickable(true);

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            setElevation(100);
//        }
//        setBackground(getResources().getDrawable(R.drawable.list_encuesta_activity_bg));

            this.id = id;
            this.idSeccion = idSeccion;
            this.cod = cod;
            this.preg = preg;
            this.ayuda = ayuda == null ? ayuda : ((ayuda == "" || ayuda.equals("null")) ? null : ayuda);
            this.obsPreg = obs;
            observacion = "";
            estado = Estado.INSERTADO;
            this.observado = false;

            LinearLayout a = new LinearLayout(context);

//            if (mostrarSeccion) {
//                Seccion seccion = new Seccion();
//                if (seccion.abrir(idSeccion)) {
//                    seccionTextView = new TextView(context);
//                    seccionTextView.setText(Html.fromHtml("<b><font color = #FF5252>" + seccion.get_seccion() + "</font></b>"));
//                    seccionTextView.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
//                    seccionTextView.setTextSize(Parametros.FONT_PREG);
//                    addView(seccionTextView);
//                }
//                seccion.free();
//            }

            pregTextView = new TextView(context);
//        pregTextView.setText(Html.fromHtml("<br><b>"+cod.toUpperCase()+"</b>: " + preg));
            pregTextView.setText(Html.fromHtml("<b><font color = #FF5252>" + cod.toUpperCase() + "</font></b>: " + preg));
//        pregTextView.setText(cod.toUpperCase()+ " " + preg);
            pregTextView.setTextColor(getResources().getColor(R.color.colorPrimary));
            pregTextView.setTextSize(Parametros.FONT_PREG);
            Typeface typeface = ResourcesCompat.getFont(context, R.font.quicksand_medium);
            pregTextView.setTypeface(typeface);

            LayoutParams layoutParams_txt = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            layoutParams_txt.setMargins(0, 0, 0, 25);
            addView(pregTextView, layoutParams_txt);

            LinearLayout.LayoutParams layoutParams_btn = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            layoutParams_btn.gravity = Gravity.END;
            botones = new LinearLayout(getContext());
            botones.setOrientation(LinearLayout.HORIZONTAL);
            botones.setLayoutParams(layoutParams_btn);

            if (this.ayuda != null) {
                Log.d("INFO3", ayuda);
                btnAyuda = new ImageButton(context);
                btnAyuda.setImageDrawable(getResources().getDrawable(R.drawable.ic_info_outline_black_24dp));
                btnAyuda.setBackground(null);
                btnAyuda.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        MaterialAlertDialogBuilder mat = new MaterialAlertDialogBuilder(context);
                        mat.setTitle(Html.fromHtml("<span style=\"color: " + R.color.colorPrimary + ";\"><strong>AYUDA - </strong>"+ cod.toUpperCase() +"</span>"));
                        mat.setMessage(Html.fromHtml(ayuda).toString());
                        mat.setIcon(R.drawable.info);
                        mat.setPositiveButton("Entendido", null);
                        mat.create();
                        mat.show();
                    }
                });


                final String tooltipText = "Este es un tooltip personalizado";
// Crea una vista de texto para el tooltip
                final TextView tooltipView = new TextView(context);
                tooltipView.setText(tooltipText);
                final PopupWindow popupWindow = new PopupWindow(tooltipView,
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);

// Asigna un fondo al tooltip (esto es opcional)
                popupWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.blue_button_background));

// Establece un retraso en milisegundos antes de mostrar el tooltip (opcional)
                popupWindow.setAnimationStyle(android.R.style.Animation_Dialog);
                popupWindow.setOutsideTouchable(true);

// Establece el oyente de clic en el ImageButton para mostrar el tooltip
                btnAyuda.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        // Calcula la posición en la pantalla para mostrar el tooltip cerca del ImageButton
                        int[] location = new int[2];
                        btnAyuda.getLocationOnScreen(location);

                        // Muestra el tooltip cerca del ImageButton
                        popupWindow.showAtLocation(btnAyuda, Gravity.NO_GRAVITY, location[0], location[1] - tooltipView.getHeight());
                        return true;
                    }
                });

                LayoutParams layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                layoutParams.gravity = Gravity.RIGHT;
                layoutParams.setMargins(0, 0, 0, 0);
                btnAyuda.setLayoutParams(layoutParams);

//            Animation anim = new AlphaAnimation(0.0f, 1.0f);
//            anim.setDuration(300);
//            anim.setStartOffset(20);
//            anim.setRepeatMode(Animation.REVERSE);
//            anim.setRepeatCount(5000);
//            btnAyuda.startAnimation(anim);
//                addView(btnAyuda);

                botones.addView(btnAyuda);
            }
        }
    }

    public void setVisible (View v, boolean sw) {
        if(sw) {
            v.setVisibility(VISIBLE);
        } else {
            v.setVisibility(INVISIBLE);
        }
    }

    public void destroyAll(){

    }

    public static void setViewAndChildrenEnabled(View view, boolean enabled) {
        try {
            view.setEnabled(enabled);
//            view.setBackgroundColor(MyApplication.getContext().getResources().getColor(R.color.colorWaterError));
            if (view instanceof ViewGroup) {
                ViewGroup viewGroup = (ViewGroup) view;
                for (int i = 0; i < viewGroup.getChildCount(); i++) {
                    View child = viewGroup.getChildAt(i);
                    setViewAndChildrenEnabled(child, enabled);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setBotones(ImageButton imageButton, boolean mostrar) {
        botones.addView(imageButton);
        if (mostrar)
            addView(botones);
    }

    public void setColorObservacion(boolean activo) {
        if(activo){
            setBackgroundColor(getContext().getResources().getColor(R.color.color_corregido));
        } else {
            setBackgroundColor(getContext().getResources().getColor(R.color.color_observacion));
        }
    }

    public void setColorFoco(boolean activo) {
        if(!observado){
            if(activo){
                setBackgroundColor(getContext().getResources().getColor(R.color.colorBackgroundFocus));
            } else {
                setBackgroundColor(getContext().getResources().getColor(R.color.colorBackgroundNotFocus));
            }
        }

    }

    public boolean getObs() {
        if(this.obsPreg != null && !this.obsPreg.equals("") && !this.obsPreg.equals("null")) {
            return true;
        } else {
            return false;
        }
    }

    public void setIdAsignacion(int idAsignacion) {
        this.idAsignacion = idAsignacion;
    }
    public void setObservado(boolean obs) {
        this.observado=obs;
    }

    public void setCorrelativo(int correlativo) {
        this.correlativo = correlativo;
    }

    public int getIdAsignacion() {
        return idAsignacion;
    }

    public int getCorrelativo() {
        return correlativo;
    }

    public void onActivityResult(Intent data) throws Exception{
        throw new Exception("No aplicable.");
    }

    public static void conFoco(ScrollView scrollView, View v){
        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        v.setLayoutParams(layoutParams);
        v.setBackgroundColor(Color.CYAN);
//        v.requestFocus();
//        Point point = obtenerPosicionDeLaVista(v);
        scrollView.scrollTo(0, scrollView.getBottom());
    }

    public static void sinFoco(View v){
        LayoutParams layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        v.setLayoutParams(layoutParams);
        v.setBackgroundColor(Color.WHITE);
    }

    public static Point obtenerPosicionDeLaVista(View view){
        int[] localizacion = new int[2];
        int[] localizacion2 = new int[2];
        view.getLocationInWindow(localizacion);
        view.getLocationOnScreen(localizacion2);
        int a=localizacion2[0];
        int b=localizacion2[1];
        int c=localizacion[0];
        int d=localizacion[1];
        return new Point(localizacion[0], localizacion[1]);
    }

    public int getId() {
        return id;
    }

    public int getIdEnd() {
        return id;
    }

    public int getIdSeccion() {
        return idSeccion;
    }

    public String getCod() {
        return cod;
    }

    public String getPreg() {
        return preg;
    }

//    public long getIdResp() {
//        return 0;
//    }
//
//    public void setIdResp(long id) throws Exception {
//        throw new Exception("No aplicable.");
//    }

    public void setCodResp(String cod) throws Exception {
        throw new Exception("No aplicable.");
    }

    public void setCodResp(Map<Integer, String> cod) throws Exception {
        throw new Exception("No aplicable.");
    }

    public abstract String getCodResp();

    public abstract String getResp();

    public abstract void setResp(String value);

    public String getObservacion() {
        return observacion;
    }

    public void setFocus(){
        requestFocus();
    }

    public void setObservacion(String value) {
        observacion = value;
    }

    public Estado getEstado() {
        return estado;
    }

    public void setEstado(Estado value) {
        estado = value;
    }

    public TextView getPregTextView() {
        return pregTextView;
    }

}
