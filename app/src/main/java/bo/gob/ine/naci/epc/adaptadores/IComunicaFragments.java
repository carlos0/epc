package bo.gob.ine.naci.epc.adaptadores;

import android.content.Context;
import android.text.Spanned;

import java.io.Serializable;

import bo.gob.ine.naci.epc.entidades.IdEncuesta;
import bo.gob.ine.naci.epc.entidades.IdInformante;

public interface IComunicaFragments extends Serializable {

    public void enviarDatos(IdInformante informante, IdEncuesta encuesta, int nivel, int idNivel, int seccion, IdInformante informantePadre, int tipoFragment);
    public void mensaje(int tipo, Context context, String methodAceptar, String methodCancelar, String titulo, Spanned mensaje);

}
