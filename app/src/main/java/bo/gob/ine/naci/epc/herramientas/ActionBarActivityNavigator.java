package bo.gob.ine.naci.epc.herramientas;

import android.content.Intent;
import android.os.Bundle;

import bo.gob.ine.naci.epc.BoletaActivity;
import bo.gob.ine.naci.epc.EncuestaActivity;
import bo.gob.ine.naci.epc.EncuestaActivity2;
import bo.gob.ine.naci.epc.EncuestaInicialActivity;
import bo.gob.ine.naci.epc.ListadoViviendasActivity;
import bo.gob.ine.naci.epc.MainActivity;
import bo.gob.ine.naci.epc.MapActivity;
import bo.gob.ine.naci.epc.MapActivity2;
import bo.gob.ine.naci.epc.ResumenActivity;
import bo.gob.ine.naci.epc.entidades.IdEncuesta;
import bo.gob.ine.naci.epc.entidades.IdInformante;

public class ActionBarActivityNavigator extends ActionBarActivityMessage {

    protected void irPrincipal() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }
    protected void irInformante(int idUpm,int idUpmHijo, int cod) {
        Bundle bundle = new Bundle();
        bundle.putInt("IdUpm", idUpm);
        bundle.putInt("idUpmHijo", idUpmHijo);

        Intent informante = new Intent(getApplicationContext(), BoletaActivity.class);
        informante.putExtras(bundle);
        startActivityForResult(informante, cod);
//        overridePendingTransition(R.anim.left_in, R.anim.left_out);
    }

    public void irListadoViviendas(int idUpm) {
        Bundle bundle = new Bundle();
        bundle.putInt("IdUpm", idUpm);

        Intent listado = new Intent(getApplicationContext(), ListadoViviendasActivity.class);
        listado.putExtras(bundle);
        startActivity(listado);
    }
//
//    protected void irConsistencia(IdInformante idBoleta) {
//        Bundle bundle = new Bundle();
//        bundle.putIntArray("IdBoleta", idBoleta.toArray());
//
//        Intent consistencia = new Intent(getApplicationContext(), ConsistenciaActivity.class);
//        consistencia.putExtras(bundle);
//        startActivity(consistencia);
//    }
//
    protected void irEncuestaInicial(IdInformante idInformante, int idNivel, IdInformante idPadre,int idUpmHijo) {
        Bundle bundle = new Bundle();
        bundle.putIntArray("IdInformante", idInformante.toArray());
        bundle.putInt("IdNivel", idNivel);
        bundle.putIntArray("IdPadre", idPadre.toArray());
        bundle.putInt("idUpmHijo", idUpmHijo);

        Intent encuestaInicial = new Intent(getApplicationContext(), EncuestaInicialActivity.class);
        encuestaInicial.putExtras(bundle);
        startActivity(encuestaInicial);
//        overridePendingTransition(R.anim.left_in, R.anim.left_out);
    }

//    protected void irEncuestaInicial(IdInformante idInformante) {
//        Bundle bundle = new Bundle();
//        bundle.putIntArray("IdInformante", idInformante.toArray());
//
//        Intent encuestaInicial = new Intent(getApplicationContext(), EncuestaActivity.class);
//        encuestaInicial.putExtras(bundle);
//        startActivity(encuestaInicial);
//    }


//    protected void irInformanteAnterior(int idNivel, int idUpm, IdInformante idPadre, IdInformante idPadreAnterior) {
//        Bundle bundle = new Bundle();
//        bundle.putInt("IdNivel", idNivel);
//        bundle.putInt("IdUpm", idUpm);
//        bundle.putIntArray("IdPadre", idPadre.toArray());
//        bundle.putIntArray("IdPadreAnterior", idPadreAnterior.toArray());
//
//        Intent informanteAnterior = new Intent(getApplicationContext(), InformanteAnteriorActivity.class);
//        informanteAnterior.putExtras(bundle);
//        startActivity(informanteAnterior);
//    }
//
    public void irEncuesta(IdInformante idInformante, IdEncuesta idEncuesta, int nivel, int idNivel, int idSeccion, IdInformante idPadre, int tipoFragment, int idUpmHijo) {
        Bundle bundle = new Bundle();
        bundle.putIntArray("IdInformante", idInformante.toArray());
        bundle.putIntArray("IdEncuesta", idEncuesta.toArray());
        bundle.putInt("Nivel", nivel);
        bundle.putInt("idNivel", idNivel);
        bundle.putInt("IdSeccion", idSeccion);
        bundle.putIntArray("IdPadre", idPadre.toArray());
        bundle.putInt("tipoFragment", tipoFragment);
        bundle.putInt("idUpmHijo", idUpmHijo);

        Intent encuesta = new Intent(getApplicationContext(), EncuestaActivity.class);
        encuesta.putExtras(bundle);
        startActivity(encuesta);
    }

    public void irEncuesta2(IdEncuesta idEncuesta, int tipoFragment, boolean directo) {
        Bundle bundle = new Bundle();
//        bundle.putIntArray("IdInformante", idInformante.toArray());
        bundle.putIntArray("IdEncuesta", idEncuesta.toArray());
//        bundle.putInt("Nivel", nivel);
//        bundle.putInt("idNivel", idNivel);
//        bundle.putInt("IdSeccion", idSeccion);
//        bundle.putIntArray("IdPadre", idPadre.toArray());
        bundle.putInt("tipoFragment", tipoFragment);
        bundle.putBoolean("directo", directo);

        Intent encuesta = new Intent(getApplicationContext(), EncuestaActivity2.class);
        encuesta.putExtras(bundle);
        startActivity(encuesta);
    }

    public void irResumen(IdInformante idInformante, int idSeccion) {
        Bundle bundle = new Bundle();
        bundle.putIntArray("IdInformante", idInformante.toArray());
        bundle.putInt("IdSeccion", idSeccion);

        Intent resumen = new Intent(getApplicationContext(), ResumenActivity.class);
        resumen.putExtras(bundle);
        startActivity(resumen);
    }
//
    public void irMap(int idUpm) {
        Bundle bundle = new Bundle();
        bundle.putInt("idUpm", idUpm);
        Intent map = new Intent(getApplicationContext(), MapActivity.class);
        map.putExtras(bundle);
        startActivity(map);
    }
    //TODO:BRP{
    public void irMap2(int idUpm, int tipo, int idAsignacion, int correlativo) {
        Bundle bundle = new Bundle();
        bundle.putInt("idUpm", idUpm);
        bundle.putInt("tipo", tipo);
        bundle.putInt("idAsignacion", idAsignacion);
        bundle.putInt("correlativo", correlativo);
        Intent map = new Intent(getApplicationContext(), MapActivity2.class);
        map.putExtras(bundle);
        startActivity(map);
    }
    //TODO:BRP}
//
//    public void irObservacion() {
//        Intent intentNotificacion = new Intent(getApplicationContext(), ObservacionActivity.class);
//        startActivity(intentNotificacion);
//    }
//
//    public void irConfiguracion() {
//        Intent intentConfiguracion = new Intent(getApplicationContext(), ConfiguracionActivity.class);
//        startActivity(intentConfiguracion);
//    }
//
//    public void irSend(String usuario, String filePath) {
//        Bundle bundle = new Bundle();
//        bundle.putString("filePath", filePath);
//        Intent sendActivity = new Intent(getApplicationContext(), SendActivity.class);
//        sendActivity.putExtras(bundle);
//        startActivity(sendActivity);
//    }
//
//    public void irReporte(int idReporte) {
//        Bundle bundle = new Bundle();
//        bundle.putInt("idReporte", idReporte);
//        Intent reporteActivity = new Intent(getApplicationContext(), ReporteActivity.class);
//        reporteActivity.putExtras(bundle);
//        startActivity(reporteActivity);
//    }
}
