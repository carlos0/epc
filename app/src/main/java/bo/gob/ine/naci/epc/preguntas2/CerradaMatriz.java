package bo.gob.ine.naci.epc.preguntas2;

import android.content.Context;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

import bo.gob.ine.naci.epc.R;
import bo.gob.ine.naci.epc.entidades.Encuesta;
import bo.gob.ine.naci.epc.entidades.Estado;
import bo.gob.ine.naci.epc.entidades.IdEncuesta;
import bo.gob.ine.naci.epc.entidades.IdInformante;
import bo.gob.ine.naci.epc.entidades.Pregunta;
import bo.gob.ine.naci.epc.entidades.Usuario;
import bo.gob.ine.naci.epc.fragments.FragmentEncuesta;
import bo.gob.ine.naci.epc.herramientas.Movil;
import bo.gob.ine.naci.epc.herramientas.Parametros;

public class CerradaMatriz extends PreguntaView {
    protected Map<Integer, String> opciones;
    protected RadioGroup radioGroup;
    protected ImageButton btnNext;
    HorizontalScrollView scrollView;
    TableLayout tableLayout;
    Map<Integer, Map<String, String>> preguntasMatriz;

    protected Map<Integer, CheckBox[]> checkBoxes;
    protected LinearLayout linearLayout;

    Map<Integer, Map<String, String>> respuestasEncuesta;

    protected Map<Integer, Map<String, RadioButton>> grupoRadioButton;

    protected Map<Integer, RadioButton> groupRadioSeleccion;
    protected Map<Integer, Map<String, String>> groupRespuestas;

    protected String codRes;
    protected String res;
    protected int idNuevo;
    protected String secciones;
    protected IdInformante idPadre;
    protected String tipo;

    protected Context context;

    //, Map<Integer, Map<String, String>> pregs
    public CerradaMatriz(Context context, int id, int idSeccion, final String cod, String preg, final Map<Integer, String> opciones, final String codEsp, int maxLength, int buttonsActive, String ayuda, final Boolean mostrarSeccion, String observacion, Map<Integer, Map<String, String>> preguntasMatriz, IdInformante idInformante, String secciones, IdInformante idPadre, String tipo) {
        super(context, id, idSeccion, cod, preg, ayuda, mostrarSeccion, observacion);
        this.opciones = opciones;
        this.preguntasMatriz = preguntasMatriz;
        this.secciones = secciones;
        this.idPadre = idPadre;
        this.tipo = tipo;

        this.context = context;

        btnNext = new ImageButton(getContext());
        btnNext.setImageDrawable(getResources().getDrawable(R.drawable.ic_next_preg));
        btnNext.setBackground(null);
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                evaluaMatriz();
            }
        });
        LinearLayout.LayoutParams layoutParamsButton = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParamsButton.gravity = Gravity.RIGHT;
        btnNext.setLayoutParams(layoutParamsButton);
        addView(btnNext);


        tableLayout = new TableLayout(context);

        //CABECERA
        for (Integer key : preguntasMatriz.keySet()) {
            TableRow tableRow = new TableRow(context);

            Map<String, String> respuestas = preguntasMatriz.get(key);
            Map<Integer, String> respuesta = Pregunta.getRespuestas(respuestas.get("respuesta"));

            Log.d("MATRIZ_1", String.valueOf(respuesta));

            TextView emptyTextView = new TextView(getContext());
            emptyTextView.setText("\n\n\n");
            emptyTextView.setLayoutParams(new TableRow.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            emptyTextView.setId(0);

            TableRow.LayoutParams layoutParams = (TableRow.LayoutParams) emptyTextView.getLayoutParams();
            layoutParams.weight = 1;

            tableRow.addView(emptyTextView);

            for (Integer keyRes : respuesta.keySet()) {
                TextView opcionesTextView = new TextView(getContext());
                opcionesTextView.setTextColor(getResources().getColor(R.color.color_anulado));

                Log.d("MATRIZ_1", String.valueOf(respuesta.get(keyRes)));
                String opcion = respuesta.get(keyRes);
                String[] a = opcion.split("\\|");

                opcionesTextView.setText(Html.fromHtml("   " + a[1].replace(" ", "<br>")+"   "));
                opcionesTextView.setLayoutParams(new TableRow.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.MATCH_PARENT));
                opcionesTextView.setGravity(Gravity.BOTTOM);
                opcionesTextView.setMaxLines(5);
//                opcionesTextView.setPadding(20, 8, 20, 8);
                opcionesTextView.setId(keyRes);

                tableRow.addView(opcionesTextView);
            }

            tableLayout.addView(tableRow);
            break;
        }

        //PREGUNTAS
        grupoRadioButton = new LinkedHashMap<>();
        groupRespuestas = new LinkedHashMap<>();

        TableRow.LayoutParams params = new TableRow.LayoutParams(
                TableRow.LayoutParams.WRAP_CONTENT,
                TableRow.LayoutParams.WRAP_CONTENT
        );
        params.gravity = Gravity.TOP;

        TableRow.LayoutParams paramsBotton = new TableRow.LayoutParams(
                TableRow.LayoutParams.WRAP_CONTENT,
                TableRow.LayoutParams.WRAP_CONTENT
        );
        paramsBotton.gravity = Gravity.BOTTOM;

        int i = 0;
        for (Integer key : this.preguntasMatriz.keySet()) {

            TableRow tableRow = new TableRow(context);
            tableRow.setLayoutParams(new TableRow.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));

            Map<String, String> respuestas = this.preguntasMatriz.get(key);
            Map<Integer, String> respuesta = Pregunta.getRespuestas(respuestas.get("respuesta"));

            TextView preguntaTextView = new TextView(context);
            preguntaTextView.setMaxLines(5);
            preguntaTextView.setTextColor(getResources().getColor(R.color.colorPrimary));
            Log.d("REVISION70", respuestas.get("pregunta"));
            String p = respuestas.get("pregunta");
            p = p.contains("[")? Pregunta.procesaEnunciado(idInformante, p):p;
            preguntaTextView.setText(Html.fromHtml(p.contains(":") ? p.split(":")[1] : p));
            preguntaTextView.setTextSize(Parametros.FONT_PREG);
            preguntaTextView.setLayoutParams(new TableRow.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            preguntaTextView.setGravity(Gravity.LEFT);
            preguntaTextView.setId(0);

            TableRow.LayoutParams layoutParams = (TableRow.LayoutParams) preguntaTextView.getLayoutParams();
            layoutParams.weight = 1;

            tableRow.addView(preguntaTextView);


            Map<String, RadioButton> opcionesRadioButton = new LinkedHashMap<>();
            groupRadioSeleccion = new TreeMap<>();

            for (Integer keyRes : respuesta.keySet()) {
                RadioButton radioButton = new RadioButton(context);
                radioButton.setId(Integer.parseInt(respuesta.get(keyRes).split("\\|")[0]));
                radioButton.setTag(key + "-" + respuesta.get(keyRes).split("\\|")[0] + "-" + respuesta.get(keyRes).split("\\|")[1]);

                radioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        int id = Integer.parseInt(buttonView.getTag().toString().split("-")[0]);
                        String codResp = buttonView.getTag().toString().split("-")[1];
                        String resp = buttonView.getTag().toString().split("-")[2];
                        Log.d("REVISION30", buttonView.getTag().toString());
                        if (!groupRadioSeleccion.isEmpty()) {
                            if (groupRadioSeleccion.containsKey(id)) {
                                groupRadioSeleccion.get(id).setChecked(false);
                                groupRadioSeleccion.put(id, (RadioButton) buttonView);
                            } else {
                                groupRadioSeleccion.put(id, (RadioButton) buttonView);
                            }
                        } else {
                            groupRadioSeleccion.put(id, (RadioButton) buttonView);
                        }
                        Map<String, String> aux = new HashMap<>();
                        aux.put("codigo_respuesta", codResp);
                        aux.put("respuesta", resp);
                        groupRespuestas.put(id, aux);
                        Log.d("MATRIZ_2", String.valueOf(groupRespuestas));
                        Log.d("MATRIZ_3", String.valueOf(groupRadioSeleccion));
                    }
                });
//                radioButton.setGravity(Gravity.AXIS_CLIP);
//                radioButton.setGravity(Gravity.AXIS_PULL_BEFORE);

                if(i==0) {
                    radioButton.setLayoutParams(paramsBotton);
                } else {
                    radioButton.setLayoutParams(params);
                }

                opcionesRadioButton.put(respuesta.get(keyRes).split("\\|")[0], radioButton);

                tableRow.addView(radioButton);
            }
            grupoRadioButton.put(key, opcionesRadioButton);

            tableLayout.addView(tableRow);
            i++;
        }

        Log.d("MATRIZ", String.valueOf(grupoRadioButton));

        addView(tableLayout);

    }

    private void evaluaMatriz() {
        if (groupRespuestas.size() != preguntasMatriz.size()) {
            Toast.makeText(context, "Debe Seleccionar una opcion para todas las preguntas", Toast.LENGTH_LONG).show();
        } else {
            guardar();
            FragmentEncuesta.actualizaMatriz(id);
        }
    }

    private void guardar() {
        IdEncuesta idEncuesta = new IdEncuesta(idAsignacion, correlativo, id, 1);
        int i = 1;
        String c;
        String r;
        Log.d("REVISION100", String.valueOf(groupRespuestas));
        for (int key : preguntasMatriz.keySet()) {
            c = groupRespuestas.get(key).get("codigo_respuesta");
            r = groupRespuestas.get(key).get("respuesta");
//            if(i == 0) {
            codRes = c;
            res = r;
            idNuevo = key;
//            }
            Encuesta encuesta = new Encuesta();
            idEncuesta.id_pregunta = key;
            Log.d("REVISIONespecial", c + '-' + r);
            if (!encuesta.abrir(idEncuesta)) {
                encuesta.nuevo();
                encuesta.set_id_encuesta(idEncuesta);
                encuesta.set_feccre(System.currentTimeMillis() / 1000);
                encuesta.set_usucre(Usuario.getLogin());
            } else {
                encuesta.editar();
                encuesta.set_usumod(Usuario.getLogin());
                encuesta.set_fecmod(System.currentTimeMillis() / 1000);
            }
            encuesta.set_respuesta(r);
            encuesta.set_codigo_respuesta(c);
            encuesta.set_observacion("");
            encuesta.set_estado(Estado.ELABORADO);
            encuesta.set_latitud(Movil.getGPS().split(";")[0].toString());
            encuesta.set_longitud(Movil.getGPS().split(";")[1].toString());
            encuesta.set_visible("t");
            encuesta.guardar();
            encuesta.free();
            i++;
        }
    }

    @Override
    public String getCodResp() {
        Log.d("MATRIZ_7", String.valueOf(codRes));
        return codRes.trim();
    }

    @Override
    public String getResp() {
        Log.d("MATRIZ_6", String.valueOf(res));
        return res.trim();
    }

    @Override
    public void setCodResp(Map<Integer, String> value) {
        Log.d("MATRIZ_5", String.valueOf(value));
        for (int key : value.keySet()) {
            grupoRadioButton.get(key).get(value.get(key)).setChecked(true);
        }
    }

    @Override
    public void setResp(String value) {

    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public int getIdEnd() {
        return idNuevo;
    }

//    @Override
//    public void setFocus() {
//        btnNext.requestFocus();
//    }
}