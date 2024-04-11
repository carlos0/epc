package bo.gob.ine.naci.epc.preguntas2;

import android.content.Context;
import android.graphics.Color;
import android.text.Html;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Map;

import bo.gob.ine.naci.epc.R;
import bo.gob.ine.naci.epc.entidades.IdInformante;
import bo.gob.ine.naci.epc.fragments.FragmentEncuesta;
import bo.gob.ine.naci.epc.herramientas.Parametros;

/**
 * Created by Rodrigo on 15/07/2016.
 */
public class SeleccionKish extends PreguntaView {
    protected IdInformante idInformante;
    protected String descripcion;
    protected int ultimoDigitoFolio;
    protected Map<Integer, String> habilitadas;
    protected int nroHabilitadas;
    protected TextView tituloTextView;
    protected TextView resultadoTextView;
    protected TextView parametrosKish;
    protected String codRespuesta;
    protected long idRespuesta;
    protected String respuesta;
    protected boolean elegida;
    protected ImageButton btnNext;
    private int cont;

    TableLayout tableLayoutKish;
//    protected int[][] matrizKish =
//            {{1,1,1,1,1,1,1,1,1,1},
//            {1,2,1,2,1,2,1,2,1,2},
//            {3,1,2,3,1,2,3,1,2,3},
//            {1,2,3,4,1,2,3,4,1,2},
//            {1,2,3,4,5,1,2,3,4,5},
//            {6,1,2,3,4,5,6,1,2,3},
//            {5,6,7,1,2,3,4,5,6,7},
//            {1,2,3,4,5,6,7,8,1,2},
//            {8,9,1,2,3,4,5,6,7,8},
//            {9,10,1,2,3,4,5,6,7,8}};
    protected int[][] matrizKish =
                    {{1,1,1,1,1,1,1,1,1,1},
                    {2,1,2,1,2,1,2,1,1,2},
                    {1,3,3,2,2,3,1,1,1,3},
                    {2,3,1,3,1,2,4,3,2,4},
                    {5,4,3,1,4,5,2,5,3,5},
                    {4,3,1,6,4,1,1,5,3,4},
                    {1,7,6,3,3,2,5,7,4,3},
                    {5,6,8,3,1,8,7,3,4,2},
                    {7,6,9,4,8,5,2,7,6,3},
                    {2,4,7,9,10,1,6,3,5,8},
                    {10,3,9,5,4,6,1,2,8,7},
                    {5,4,6,2,8,2,3,12,1,9},
                    {12,3,13,6,7,10,5,11,12,9},
                    {6,12,14,5,10,14,2,1,13,4},
                    {7,8,10,14,11,3,7,13,15,1}};


    public SeleccionKish(Context context, int id, int idSeccion, String cod, String preg, String descripcion, Map<Integer,String> habilitadas, int ultimoDigitoFolio, String ayuda, final Boolean mostrarSeccion, String observacion) {
        super(context, id, idSeccion, cod, preg, ayuda, mostrarSeccion, observacion);
        this.descripcion = descripcion;
        btnNext = new ImageButton(getContext());
        btnNext.setImageDrawable(getResources().getDrawable(R.drawable.ic_next_preg));
        btnNext.setBackground(null);
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                if(evaluar) {
                    FragmentEncuesta.actualiza(id);
//                }
            }
        });
        LinearLayout.LayoutParams layoutParamsButton = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParamsButton.gravity = Gravity.RIGHT;
        btnNext.setLayoutParams(layoutParamsButton);

        if(habilitadas.size() > 10){
            nroHabilitadas = 9;
        }else{
            nroHabilitadas = habilitadas.size() >0?habilitadas.size()- 1:0;
        }
        this.habilitadas = habilitadas;
        this.ultimoDigitoFolio = ultimoDigitoFolio;

        int numElegida = matrizKish[nroHabilitadas][ultimoDigitoFolio];


        tituloTextView = new TextView(context);
        tituloTextView.setTextSize(Parametros.FONT_PREG);
        parametrosKish = new TextView(context);
        parametrosKish.setTextSize(Parametros.FONT_PREG);
        LayoutParams layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.CENTER;
        layoutParams.setMargins(5,15,5,15);
        tituloTextView.setLayoutParams(layoutParams);
//        tituloTextView.setTypeface(null, Typeface.BOLD);
        tituloTextView.setText("PERSONAS POSIBLES PARA SELECCIÓN:");
        resultadoTextView = new TextView(context);
        resultadoTextView.setLayoutParams(layoutParams);
        resultadoTextView.setTextSize(Parametros.FONT_PREG);
//        resultadoTextView.setTypeface(null, Typeface.BOLD);
        //resultadoTextView.setTextColor(getResources().getColor(R.color.colorSeleccionada));
        addView(btnNext);

        addView(tituloTextView);
        cont=0;
        for (Integer key : habilitadas.keySet()) {
            cont++;
            String opcion = habilitadas.get(key);
            String[] a = opcion.split("\\|");
            String[] nombreComp=a[1].split(" ");
            String nombreInformante="";
            for (int i=0; i<nombreComp.length-3 ;i++){
                nombreInformante = nombreInformante+" "+nombreComp[i];
            }
            TextView habilitadasTextView = new TextView(context);
            respuesta=a[3];
            habilitadasTextView.setText(Html.fromHtml("<b>" + cont + " NOMBRE:</b> " + nombreInformante + " (" + a[3] + ")"));
            if(cont == numElegida){
                codRespuesta=a[4];
//                codRespuesta=String.valueOf(cont);
                habilitadasTextView.setTextColor(Color.BLUE);
//                definirElegidaKish(numElegida);
            }

            addView(habilitadasTextView);
        }

        parametrosKish.setText("Número de personas elegibles: " + (nroHabilitadas + 1) + ", Dígito del Folio utilizado: " + ultimoDigitoFolio);
        parametrosKish.setLayoutParams(layoutParams);
        parametrosKish.setTextSize(Parametros.FONT_PREG * 0.65f);
//        parametrosKish.setTypeface(null, Typeface.ITALIC);
        resultadoTextView.setTextSize(Parametros.FONT_PREG * 2f);
        addView(resultadoTextView);
        addView(parametrosKish);

        definirElegidaKish(numElegida);
        dibujarTablaKish(context);

    }
    public void definirElegidaKish(int numElegida)
    {
        String value = (new ArrayList<String>(habilitadas.values())).size()==0?"0|0|0|_|0":(new ArrayList<String>(habilitadas.values())).get(numElegida - 1);
        String[] a = value.split("\\|");

        if(String.valueOf(numElegida).equals(a[4])){
            resultadoTextView.setText("PERSONA ELEGIDA");
            elegida = true;
        }
        else{
            resultadoTextView.setText("PERSONA NO ELEGIDA");
            elegida = false;
        }
        this.codRespuesta ="" +a[4];
       this.respuesta =""+ a[3];
//        idRespuesta = Integer.parseInt(a[0]);
        /*Toast toast = Toast.makeText(getContext(), "codRespuesta: "+codRespuesta+" - respuesta: "+respuesta+" - idRespuesta: "+idRespuesta, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 85);
        toast.show();*/

    }
    public boolean getElegida(){
        return elegida;
    }

    public void dibujarTablaKish(Context context){
        tableLayoutKish = new TableLayout(context);
        LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER;
        tableLayoutKish.setLayoutParams(params);
        for (int i = 0; i < matrizKish.length; i++)
        {
            TableRow tableRow = new TableRow(context);
            for(int j = 0; j < matrizKish[0].length; j++) {
                TextView numeroKishTextView = new TextView(context);
                if(i == habilitadas.size()-1 && j == ultimoDigitoFolio)
                    numeroKishTextView.setTextColor(Color.BLUE);
                numeroKishTextView.setText("" + matrizKish[i][j]);
                numeroKishTextView.setPadding(20, 8, 20, 8);
                numeroKishTextView.setId(i * 10 + j);
                tableRow.addView(numeroKishTextView);
            }
            tableLayoutKish.addView(tableRow);
        }
        addView(tableLayoutKish);
    }

    @Override
    public void setCodResp(String codResp) {
//        codRespuesta=codResp;
    }

    @Override
    public String getCodResp() {

        return codRespuesta;
    }

    @Override
    public String getResp() {

        return respuesta;
    }

    @Override
    public void setResp(String value) {
//        respuesta = value;
    }

    @Override
    public void setFocus() {}
}

