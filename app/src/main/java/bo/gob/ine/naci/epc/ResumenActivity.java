package bo.gob.ine.naci.epc;

import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.text.Html;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Map;

import bo.gob.ine.naci.epc.adaptadores.AdapterEvents;
import bo.gob.ine.naci.epc.adaptadores.ResumenAdapter;
import bo.gob.ine.naci.epc.entidades.Estado;
import bo.gob.ine.naci.epc.entidades.IdInformante;
import bo.gob.ine.naci.epc.entidades.Informante;
import bo.gob.ine.naci.epc.entidades.Proyecto;
import bo.gob.ine.naci.epc.entidades.Seccion;
import bo.gob.ine.naci.epc.entidades.Usuario;
import bo.gob.ine.naci.epc.herramientas.ActionBarActivityNavigator;
import bo.gob.ine.naci.epc.herramientas.Parametros;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.app.AlertDialog;
import android.content.DialogInterface;

public class ResumenActivity extends ActionBarActivityNavigator implements AdapterEvents {
    private Toolbar toolbar;
    private ListView list;
    private IdInformante idInformante;
    private int idSeccion;
    private Map<String, Object> inf;
    private ResumenAdapter adapter;
    private String checkedItems = "-id_nivel:3-id_nivel:4";
    private ArrayList<Map<String, Object>> valores = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resumen);
//
//        getSupportActionBar().setDisplayShowHomeEnabled(true);
//        getSupportActionBar().setLogo(R.mipmap.ic_launcher_ine20);
//        getSupportActionBar().setDisplayUseLogoEnabled(true);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        list = (ListView)findViewById(R.id.list_view);

        Bundle bundle = getIntent().getExtras();
        idInformante = new IdInformante(bundle.getIntArray("IdInformante"));
        idSeccion = bundle.getInt("IdSeccion");

        cargarListado();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_resumen, menu);
        /*if (!RolPermiso.tienePermiso(Usuario.getRol(), "activity_reporte")) {
            MenuItem item = menu.findItem(R.id.action_reporte);
            item.setVisible(false);
        }*/
        MenuItem item=menu.findItem(R.id.action_informante_2);
        item.setVisible(false);
        MenuItem item2=menu.findItem(R.id.action_nivel2);
        item2.setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_nivel1:
                if(item.isChecked()){
                    item.setChecked(false);
                    removeFilter("id_nivel:3");
                }else{
                    item.setChecked(true);
                    addFilter("id_nivel:4");
                }
                return true;
            case R.id.action_nivel2: {
                if(item.isChecked()){
                    item.setChecked(false);
                    removeFilter("id_nivel:4");
                }else{
                    item.setChecked(false);
                    addFilter("id_nivel:4");
                }
                return true;
            }
            case R.id.action_informante_2: {
                Informante informante = new Informante();
                if (informante.abrir(idInformante)) {
                    if (informante.get_id_nivel() == 1) {
//                        irInformante(Informante.getUpm(idInformante), 2, idInformante);
                    } else {
//                        irInformante(Informante.getUpm(idInformante), 2, informante.get_id_informante_padre());
                    }
                    informante.free();
                    finish();
                } else {
                    errorMessage(null, null,"Error!", Html.fromHtml("No se encontró el informante."), Parametros.FONT_OBS);
                }
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    public void addFilter(String filter) {
        if (!checkedItems.contains(filter)) {
            checkedItems = checkedItems+ "-" + filter;
        }
        adapter.getFilter().filter(checkedItems);
    }

    public void removeFilter(String filter) {
        if (checkedItems.contains(filter)) {
            checkedItems = checkedItems.replace("-"+filter, "");
        }
        adapter.getFilter().filter(checkedItems);
    }

    private void cargarListado() {
        try {
            Informante informante = new Informante();
            valores = informante.resumen(idInformante, idSeccion);
            if(adapter == null) {
                adapter = new ResumenAdapter(this, valores);
                list.setAdapter(adapter);
            } else {
                adapter.setDataView(valores);
            }
            adapter.getFilter().filter(checkedItems);
        } catch (Exception exp) {
            exp.printStackTrace();
        }
    }

    public void continuar() {
        if ((Integer) inf.get("mostrar_ventana") == 0) {
//            IdEncuesta idEnc = new IdEncuesta((Integer) inf.get("id_asignacion"), (Integer) inf.get("correlativo"), (Integer) inf.get("id_pregunta"), (Integer) inf.get("fila"));
//            int idNiv = (Integer) inf.get("id_nivel");
//            irEncuesta(idEnc, idNiv);
//            finish();
        } else {
            Informante informante = new Informante();
            if (informante.abrir(new IdInformante((Integer) inf.get("id_asignacion"), (Integer) inf.get("correlativo")))) {
//                irEncuestaInicial(informante.get_id_informante(), informante.get_id_nivel(), informante.get_id_informante_padre());
            } else {
                errorMessage(null,null, "Error!", Html.fromHtml("No se encontró el informante."), Parametros.FONT_OBS);
            }
            informante.free();
        }
    }

    public void continuarConcluido() {
        if (Informante.isClosed(idInformante)) {
            informationMessage(null, null,"Error!", Html.fromHtml("La boleta está cerrada por el monitor."), Parametros.FONT_OBS);
        } else {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
            alertDialog.setTitle("Codigo");
            LinearLayout linearLayout = new LinearLayout(this);
            linearLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            linearLayout.setOrientation(LinearLayout.VERTICAL);

            TextView textMensaje = new TextView(this);
            textMensaje.setText("Introduzca código de acceso:");
            linearLayout.addView(textMensaje);

            final EditText cod = new EditText(this);
            cod.setSingleLine();
            linearLayout.addView(cod);

            alertDialog.setView(linearLayout);

            alertDialog.setCancelable(false);
            alertDialog.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    if (cod.getText().toString().equals(Proyecto.getCodigoDesbloqueo())) {
                        continuar();
                    } else {
                        errorMessage(null, null,"Error!", Html.fromHtml("Código incorrecto."), Parametros.FONT_OBS);
                    }
                }
            });
            alertDialog.setNegativeButton("Cancelar", null);

            AlertDialog dialog = alertDialog.create();
            dialog.show();
        }
    }

    @Override
    public void onItemClick(int mPosition) {
        try {
            Map<String, Object> values = (Map<String, Object>) adapter.getItem(mPosition);

            if (values.get("id_pregunta") == null) {
                Seccion seccion = new Seccion();
                if (seccion.abrir((Integer)values.get("id_seccion"))) {
                    // CLICK EN UNA SECCION
                    if (values.get("id_asignacion") == null) {
                        /*Informante informante = new Informante();
                        if (informante.abrir(idInformante)) {
                            if (informante.get_id_nivel() > (int)seccion.get_id_nivel()) {
                                idInformante = informante.get_id_informante_padre();
                                informante.free();
                                informante.abrir(idInformante);
                            }
                            if (informante.get_id_nivel() == (int)seccion.get_id_nivel()) {
                                if (idSeccion == seccion.get_id_seccion()) {
                                    idSeccion = -1;
                                    cargarListado();
                                } else {
                                    idSeccion = seccion.get_id_seccion();
                                    cargarListado();
                                }
                            } else {
                                //idInformante = Informante.getHijos(idInformante).size()==0?idInformante:Informante.getHijos(idInformante).get(0);
                                idSeccion = seccion.get_id_seccion();
                                cargarListado();
                            }
                        } else {
                            errorMessage(null, "Error!", Html.fromHtml("No se encontró el informante."), Parametros.FONT_OBS);
                        }
                        informante.free();*/
                        Informante informante = new Informante();
                        if (informante.abrir(idInformante)) {
                            if (informante.get_id_nivel() == 2) {
                                idInformante = informante.get_id_informante_padre();
                                informante.free();
                                informante.abrir(idInformante);
                            }
                            if (idSeccion == seccion.get_id_seccion()) {
                                idSeccion = -1;
                                cargarListado();
                            } else {
                                idSeccion = seccion.get_id_seccion();
                                cargarListado();
                            }
                        } else {
                            errorMessage(null, null,"Error!", Html.fromHtml("No se encontró el informante."), Parametros.FONT_OBS);
                        }
                        informante.free();


                        // CLICK EN UN MIEMBRO
                    } else {
                        if (idInformante.id_asignacion == (Integer) values.get("id_asignacion") && idInformante.correlativo == (Integer) values.get("correlativo") && (Integer) values.get("id_nivel") == 4) {
                            Informante informante = new Informante();
                            if (informante.abrir(idInformante)) {
                                idInformante = informante.get_id_informante_padre();
                                informante.free();
                            } else {
                                errorMessage(null, null, "Error!", Html.fromHtml("No se encontró el informante."), Parametros.FONT_OBS);
                            }
                        } else {
                            idInformante = new IdInformante((Integer) values.get("id_asignacion"), (Integer) values.get("correlativo"));
                        }
                        idSeccion = seccion.get_id_seccion();
                        cargarListado();
                    }
                } else {
                    errorMessage(null, null,"Error!", Html.fromHtml("No se encontró la seccion."), Parametros.FONT_OBS);
                }
                seccion.free();
                // CLICK EN UNA PREGUNTA
            } else {
                Informante informante = new Informante();
                if (informante.abrir(idInformante)) {
                    boolean flag = false;
                    if (informante.get_id_nivel() > 2) {
                        Informante informanteEntity1 = new Informante();
                        if (informanteEntity1.abrir(informante.get_id_informante_padre())) {
                            flag = (informanteEntity1.get_apiestado() == Estado.ELABORADO);
                        }
                        informanteEntity1.free();
                    }
                    if ((Integer)values.get("id_usuario") == Usuario.getUsuario()) {
                        if (informante.get_apiestado() == Estado.ELABORADO || flag) {
                            if ((Integer) values.get("mostrar_ventana") == 0) {
//                                IdEncuesta idEnc = new IdEncuesta((Integer) values.get("id_asignacion"), (Integer) values.get("correlativo"), (Integer) values.get("id_pregunta"), (Integer) values.get("fila"));
                                int idNiv = (Integer) values.get("id_nivel");
//                                irEncuesta(idEnc, idNiv);
//                                finish();
                            } else {
//                                irEncuestaInicial(informante.get_id_informante(), informante.get_id_nivel(), informante.get_id_informante_padre());
//                                finish();
                            }
                        } else {
                            inf = values;
                            continuarConcluido();
                        }
                    } else {
                        Usuario usuario = new Usuario();
                        if (usuario.abrir((Integer) values.get("id_usuario"))) {
                            errorMessage(null, null,"Error!", Html.fromHtml("Solo " + usuario.get_nombre() + " " + usuario.get_paterno() + " puede editar."), Parametros.FONT_OBS);
                        } else {
                            errorMessage(null,null, "Error!", Html.fromHtml("No se encontró el usuario."), Parametros.FONT_OBS);
                        }
                        usuario.free();
                    }
                } else {
                    errorMessage(null, null,"Error!", Html.fromHtml("No existe el informante."), Parametros.FONT_OBS);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void onLongItemClick(int mPosition) {
        Map<String, Object> values = (Map<String, Object>) adapter.getItem(mPosition);

        if (values.get("id_pregunta") == null) {
            Seccion seccion = new Seccion();
            if (seccion.abrir((Integer)values.get("id_seccion"))) {
                Informante informante = new Informante();
                if (informante.abrir(idInformante)) {
                    // NIVEL 2
                    if (informante.get_id_nivel() > seccion.get_id_nivel()) {
                        idInformante = informante.get_id_informante_padre();
                        informante.free();
                        informante.abrir(idInformante);
                        /*if ( informante.get_apiestado() == Estado.CONCLUIDO ) {
                            errorMessage(null, "Error!", Html.fromHtml("La boleta fue concluída."), Parametros.FONT_OBS);
                            return;
                        }*/
                    } else {
                        // NIVEL 1
                        if (values.get("id_asignacion") != null) {
                            /*if ( informante.get_apiestado() == Estado.CONCLUIDO ) {
                                errorMessage(null, "Error!", Html.fromHtml("La boleta fue concluída."), Parametros.FONT_OBS);
                                return;
                            }*/
                            idInformante = new IdInformante((Integer) values.get("id_asignacion"), (Integer) values.get("correlativo"));
                            informante.free();
                            informante.abrir(idInformante);
                        }
                    }
                    if ( informante.get_apiestado() == Estado.CONCLUIDO ) {
                        errorMessage(null,null, "Error!", Html.fromHtml("La boleta/miembro se encuentra CONCLUIDO, para abrirla, ingrese desde una pregunta e introduzca el código de desbloqueo."), Parametros.FONT_OBS);
                        return;
                    }
                    if (informante.get_id_nivel() == (int)seccion.get_id_nivel() && seccion.get_abierta() == 1) {
                        int idPregunta = seccion.getPrimeraPregunta(seccion.get_id_seccion());
//                        irEncuesta(new IdEncuesta(idInformante.id_asignacion, idInformante.correlativo, idPregunta, 1), informante.get_id_nivel(),idInformante,3);
                        finish();
                    }
                } else {
                    errorMessage(null, null,"Error!", Html.fromHtml("No se encontró el informante."), Parametros.FONT_OBS);
                }
                informante.free();
            } else {
                errorMessage(null, null,"Error!", Html.fromHtml("No se encontró la sección."), Parametros.FONT_OBS);
            }
            seccion.free();
        }
    }

    @Override
    public void onBackPressed() {
        Informante informante = new Informante();
        if (informante.abrir(idInformante)) {
            int idUpm = Informante.getUpm(informante.get_id_informante());
            int idNiv = informante.get_id_nivel();
            IdInformante idPad = informante.get_id_informante_padre();
            informante.free();
//            irInformante(idUpm, idNiv, idPad);
//            irInformante(idUpm);
            finish();
        } else {
            errorMessage(null, null,"Error!", Html.fromHtml("No se encontró el informante."), Parametros.FONT_OBS);
        }
    }
}