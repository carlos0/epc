package bo.gob.ine.naci.epc.preguntas;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import bo.gob.ine.naci.epc.R;
import bo.gob.ine.naci.epc.entidades.Estado;
import bo.gob.ine.naci.epc.herramientas.ActionBarActivityMessage;
import bo.gob.ine.naci.epc.herramientas.Parametros;
import cn.pedant.SweetAlert.SweetAlertDialog;

public abstract class PreguntaView extends LinearLayout {
    protected int id;
    protected int idSeccion;
    protected String cod;
    protected String preg;
    protected TextView pregTextView;
    protected String observacion;
    protected String ayuda;
    protected Estado estado;
//    protected Button btnAyuda;
    protected ImageButton btnAyuda;
    protected ImageButton btnObservacion;
    protected ActionBarActivityMessage obs = new ActionBarActivityMessage();


    public PreguntaView(final Context context, int id, int idSeccion, final String cod, String preg, final String ayuda) {
        //CONSTRUCTOR PARA UNA PREGUNTA
        super(context);
        setOrientation(LinearLayout.VERTICAL);
        setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        setPadding(80,20,80,20);
        setClickable(true);

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            setElevation(100);
//        }
//        setBackground(getResources().getDrawable(R.drawable.list_encuesta_activity_bg));

        this.id = id;
        this.idSeccion = idSeccion;
        this.cod = cod;
        this.preg = preg;
        this.ayuda = (ayuda=="")?null:ayuda;
        observacion = "";
        estado = Estado.INSERTADO;

        LinearLayout a = new LinearLayout(context);

        pregTextView = new TextView(context);
//        pregTextView.setText(Html.fromHtml("<br><b>"+cod.toUpperCase()+"</b>: " + preg));
        pregTextView.setText(Html.fromHtml("<b><font color = #FF5252>" + cod.toUpperCase()+"</font></b>: " + preg));
//        pregTextView.setText(cod.toUpperCase()+ " " + preg);
        pregTextView.setTextColor(getResources().getColor(R.color.colorPrimary));
        pregTextView.setTextSize(Parametros.FONT_PREG);
        pregTextView.setFocusable(true);
//        Typeface typeface = ResourcesCompat.getFont(context, R.font.quicksand_medium);
//        pregTextView.setTypeface(typeface);

        LayoutParams layoutParams_txt = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        layoutParams_txt.setMargins(0,0,0,25);
        addView(pregTextView, layoutParams_txt);

        if(ayuda != null) {
            btnAyuda = new ImageButton(context);
            btnAyuda.setImageDrawable(getResources().getDrawable(R.drawable.ic_info_outline_black_24dp));
            btnAyuda.setBackground(null);
            btnAyuda.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    LinearLayout.LayoutParams layoutParamsButton = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    String a = context.toString();
                    SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(context, SweetAlertDialog.CUSTOM_IMAGE_TYPE);
                    sweetAlertDialog.setTitleText("AYUDA - "+cod);
                    sweetAlertDialog.setContentText(Html.fromHtml(ayuda).toString());
                    sweetAlertDialog.setCustomImage(R.drawable.info);
                    sweetAlertDialog.setCancelable(false);
                    sweetAlertDialog.show();
                    Button buttonConfirm = sweetAlertDialog.findViewById(R.id.confirm_button);
                    buttonConfirm.setLayoutParams(layoutParamsButton);
                }
            });
            LayoutParams layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            layoutParams.gravity = Gravity.RIGHT;
            btnAyuda.setLayoutParams(layoutParams);

//            Animation anim = new AlphaAnimation(0.0f, 1.0f);
//            anim.setDuration(300);
//            anim.setStartOffset(20);
//            anim.setRepeatMode(Animation.REVERSE);
//            anim.setRepeatCount(5000);
//            btnAyuda.startAnimation(anim);
            addView(btnAyuda);


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

    public int getIdSeccion() {
        return idSeccion;
    }

    public String getCod() {
        return cod;
    }

    public String getPreg() {
        return preg;
    }

    public void setPreg(String txtPregunta) {
        pregTextView.setText(Html.fromHtml(txtPregunta));
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

    public abstract String getCodResp();

    public abstract String getResp();

    public abstract void setResp(String value);

    public void setFocus(){
        requestFocus();
    };

    public String getObservacion() {
        return observacion;
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
