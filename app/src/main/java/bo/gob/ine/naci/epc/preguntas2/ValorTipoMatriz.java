package bo.gob.ine.naci.epc.preguntas2;

import android.content.Context;
import android.text.InputType;
import android.view.Gravity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.Map;

import bo.gob.ine.naci.epc.R;

public class ValorTipoMatriz extends PreguntaView {
    protected EditText valor;
    protected Map<Integer, String> opciones;
    protected TextView txtFrecuencia;
    protected RadioGroup radioGroup;
    protected Button resp, noAplica, seNiega, noSabe;
    protected int buttonsActive;
    Context context;
    TableLayout tableLayout;

    public ValorTipoMatriz(Context context, int id, int idSeccion, String cod, String preg, final Map<Integer, String> opciones, int maxLength, int buttonsActive, String ayuda, final String codEsp, final Boolean mostrarSeccion, String observacion) {
        super(context, id, idSeccion, cod, preg, ayuda, mostrarSeccion, observacion);
        this.opciones = opciones;
        this.buttonsActive = buttonsActive;
        this.context = context;

        tableLayout = new TableLayout(context);

        for (int i = 0; i < 52; i++) {
            TableRow row = new TableRow(context);
            row.setLayoutParams(new TableLayout.LayoutParams(
                    TableLayout.LayoutParams.MATCH_PARENT,
                    TableLayout.LayoutParams.WRAP_CONTENT));

            // Columna 1: RadioButtons (Si / No)
            RadioGroup radioGroup1 = createRadioGroup();
//            radioGroup1.setBackgroundResource(R.drawable.row_border);
            row.addView(radioGroup1);

            // Columna 2: RadioButtons (Opciones)
            RadioGroup radioGroup2 = createRadioGroupWithOptions();
//            radioGroup2.setBackgroundResource(R.drawable.row_border);
            row.addView(radioGroup2);

            // Columna 3 y 4: EditText numérico
            EditText editText1 = createNumericEditText();
//            editText1.setBackgroundResource(R.drawable.row_border);
            EditText editText2 = createNumericEditText();
//            editText2.setBackgroundResource(R.drawable.row_border);
            row.addView(editText1);
            row.addView(editText2);

            // Columna 5: RadioButtons (Opciones)
            RadioGroup radioGroup3 = createRadioGroupWithOptions();
//            radioGroup3.setBackgroundResource(R.drawable.row_border);
            row.addView(radioGroup3);

            // Columna 6 y 7: EditText numérico
            EditText editText3 = createNumericEditText();
//            editText3.setBackgroundResource(R.drawable.row_border);
            EditText editText4 = createNumericEditText();
//            editText4.setBackgroundResource(R.drawable.row_border);
            row.addView(editText3);
            row.addView(editText4);

            // Columna 8: RadioButtons (Si / No)
            RadioGroup radioGroup4 = createRadioGroup();
//            radioGroup4.setBackgroundResource(R.drawable.row_border);
            row.addView(radioGroup4);

            // Columna 9: EditText numérico
            EditText editText5 = createNumericEditText();
//            editText5.setBackgroundResource(R.drawable.row_border);
            row.addView(editText5);

            // Aplicar bordes a la fila
            row.setBackgroundResource(R.drawable.row_border);

            tableLayout.addView(row);
        }
        addView(tableLayout);
    }

    private RadioGroup createRadioGroup() {
        RadioGroup radioGroup = new RadioGroup(context);
        radioGroup.setLayoutParams(new TableRow.LayoutParams(
                TableRow.LayoutParams.WRAP_CONTENT,
                TableRow.LayoutParams.WRAP_CONTENT));
        RadioButton radioButton1 = new RadioButton(context);
        radioButton1.setText("Si");
        RadioButton radioButton2 = new RadioButton(context);
        radioButton2.setText("No");
        radioGroup.addView(radioButton1);
        radioGroup.addView(radioButton2);
        return radioGroup;
    }

    private RadioGroup createRadioGroupWithOptions() {
        RadioGroup radioGroup = new RadioGroup(context);
        radioGroup.setLayoutParams(new TableRow.LayoutParams(
                TableRow.LayoutParams.WRAP_CONTENT,
                TableRow.LayoutParams.WRAP_CONTENT));
        for (int i = 0; i < 10; i++) {
            RadioButton radioButton = new RadioButton(context);
            radioButton.setText("Opción " + i);
            radioGroup.addView(radioButton);
        }
        return radioGroup;
    }

    private EditText createNumericEditText() {
        EditText editText = new EditText(context);
        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        editText.setLayoutParams(new TableRow.LayoutParams(
                TableRow.LayoutParams.WRAP_CONTENT,
                TableRow.LayoutParams.WRAP_CONTENT));
        editText.setGravity(Gravity.CENTER);
        return editText;
    }

//    private void guardar() {
//        IdEncuesta idEncuesta = new IdEncuesta(idAsignacion, correlativo, id);
//        int i = 1;
//        String c;
//        String r;
//        Log.d("REVISION100", String.valueOf(groupRespuestas));
//        for (int key : preguntasMatriz.keySet()) {
//            c = groupRespuestas.get(key).get("codigo_respuesta");
//            r = groupRespuestas.get(key).get("respuesta");
////            if(i == 0) {
//            codRes = c;
//            res = r;
//            idNuevo = key;
////            }
//            Encuesta encuesta = new Encuesta();
//            idEncuesta.id_pregunta = key;
//            Log.d("REVISIONespecial", c + '-' + r);
//            if (!encuesta.abrir(idEncuesta)) {
//                encuesta.nuevo();
//                encuesta.set_id_encuesta(idEncuesta);
//                encuesta.set_feccre(System.currentTimeMillis() / 1000);
//                encuesta.set_usucre(Usuario.getLogin());
//            } else {
//                encuesta.editar();
//                encuesta.set_usumod(Usuario.getLogin());
//                encuesta.set_fecmod(System.currentTimeMillis() / 1000);
//            }
//            encuesta.set_respuesta(r);
//            encuesta.set_codigo_respuesta(c);
//            encuesta.set_observacion("");
//            encuesta.set_estado(Estado.ELABORADO);
//            encuesta.set_latitud(Movil.getGPS().split(";")[0].toString());
//            encuesta.set_longitud(Movil.getGPS().split(";")[1].toString());
//            encuesta.set_visible("t");
//            encuesta.guardar();
//            encuesta.free();
//            i++;
//        }
//    }

    @Override
    public String getCodResp() {
        return null;
    }

    @Override
    public String getResp() {
        return null;
    }

    @Override
    public void setCodResp(String value) {

    }

    @Override
    public void setResp(String value) {

    }
}
