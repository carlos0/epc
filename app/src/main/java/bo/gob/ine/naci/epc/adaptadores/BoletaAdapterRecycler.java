package bo.gob.ine.naci.epc.adaptadores;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.ListPopupWindow;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

import bo.gob.ine.naci.epc.BoletaActivity;
import bo.gob.ine.naci.epc.MainActivity;
import bo.gob.ine.naci.epc.R;
import bo.gob.ine.naci.epc.entidades.Observacion;
import bo.gob.ine.naci.epc.herramientas.LottieDialog;
import bo.gob.ine.naci.epc.herramientas.TemplatePdf;
import bo.gob.ine.naci.epc.entidades.IdInformante;
import bo.gob.ine.naci.epc.entidades.Informante;
import bo.gob.ine.naci.epc.entidades.RolPermiso;
import bo.gob.ine.naci.epc.entidades.Usuario;
import bo.gob.ine.naci.epc.herramientas.Parametros;
import bo.gob.ine.naci.epc.herramientas.ActionBarActivityNavigator;
import bo.gob.ine.naci.epc.herramientas.ValidatorConsistencias;

import static bo.gob.ine.naci.epc.MyApplication.getContext;

public class BoletaAdapterRecycler extends RecyclerView.Adapter<BoletaAdapterRecycler.ViewHolder>  implements View.OnClickListener{

    private static final String TAG = "RecyclerAdapter";
    private ArrayList<Map<String, Object>> dataView;
    private Activity activity = null;
    private View.OnClickListener listener;
    private ListPopupWindow listPopupWindow;
    private int idNivel;
    private int lastPosition = -1;
    private Animation animation;
    private static long button1_pressed;
    private static long button2_pressed;

    private LottieDialog lottie;

//    private String[]header= {"CODIGO","CODIGO PREGUNTAS","PREGUNTAS","CODIGO RESPUESTAS","RESPUESTAS"};
    private String[]header= {"CODIGO","CODIGO PREGUNTAS","PREGUNTAS","RESPUESTAS","OBSERVACION"};
    private TemplatePdf templatePdf;

    public BoletaAdapterRecycler(Activity activity, ArrayList<Map<String, Object>> dataView, int idNivel) {
        this.dataView = dataView;
        this.activity = activity;
        this.idNivel = idNivel;

        lottie = new LottieDialog(activity);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.adapter_list_informante, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);

        view.setOnClickListener(this);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        final Map<String, Object> objView = dataView.get(position);
//        vi.setId((int) objView.get("id_upm"));
        holder.txtValor.setText("Folio: " + objView.get("codigo").toString());
        String incidencia;
//       String codigoIncidencia= Informante.obtenerUltimaIncidenciaBoleta(Integer.parseInt(objView.get("id_asignacion").toString()),Integer.parseInt(objView.get("correlativo").toString()));
        String codigoIncidencia = Informante.obtenerUltimaIncidenciaBoleta(Integer.parseInt(objView.get("id_asignacion").toString()), Integer.parseInt(objView.get("correlativo").toString()));

        switch (Integer.valueOf(codigoIncidencia)) {
            case 1:
                incidencia = "ENTREVISTA COMPLETA";
                break;
            case 2:
                incidencia = "ENTREVISTA INCOMPLETA";
                break;
            case 3:
                incidencia = "TEMPORALMENTE AUSENTE";
                break;
            case 4:
                incidencia = "INFORMANTE NO CALIFICADO";
                break;
            case 5:
                incidencia = "FALTA DE CONTACTO";
                break;
            case 6:
                incidencia = "RECHAZO";
                break;
            case 7:
                incidencia = "VIVIENDA DESOCUPADA";
                break;
            default:
                incidencia = "SIN INCIDENCIA";
                break;
        }

        switch (objView.get("estado").toString()) {
            case "ELABORADO":
                holder.txtEstado.setText(Html.fromHtml(incidencia + "<br>" + "(ELABORADO)"));
                holder.txtEstado.setTextColor(getContext().getResources().getColor(R.color.color_anulado));
                if (Integer.parseInt(objView.get("id_tipo_obs").toString()) == 0 || Integer.parseInt(objView.get("id_tipo_obs").toString()) == 1 || Integer.parseInt(objView.get("id_tipo_obs").toString()) == 8) {
                    holder.btnObservacion.setVisibility(View.GONE);
//                        holder.btnObservacionSuper.setVisibility(View.GONE);
                } else {
                    if (RolPermiso.tienePermiso(Usuario.getRol(), "observacion")) {
                        holder.btnObservacion.setVisibility(View.VISIBLE);
                    } else {
                        holder.btnObservacion.setVisibility(View.GONE);
//                            holder.btnObservacionSuper.setVisibility(View.GONE);
                    }
                }
                holder.btnObservacionSuper.setVisibility(View.GONE);
                break;
            case "CONCLUIDO":
                holder.txtEstado.setText(Html.fromHtml(incidencia + "<br>" + "(CONCLUIDO)"));
                if (Integer.parseInt(objView.get("id_tipo_obs").toString()) != 2 && Integer.parseInt(objView.get("id_tipo_obs").toString()) != 8) {
                    holder.txtEstado.setTextColor(getContext().getResources().getColor(R.color.colorWarning));
                    holder.btnObservacion.setVisibility(View.GONE);
//                        holder.btnObservacionSuper.setVisibility(View.GONE);
                } else if (Integer.parseInt(objView.get("id_tipo_obs").toString()) == 8) {
                    holder.txtEstado.setTextColor(getContext().getResources().getColor(R.color.color_concluido));
                    holder.btnObservacion.setVisibility(View.GONE);
//                        holder.btnObservacionSuper.setVisibility(View.GONE);
                } else {
                    holder.txtEstado.setTextColor(getContext().getResources().getColor(R.color.colorWarning));
                    if (RolPermiso.tienePermiso(Usuario.getRol(), "observacion")) {
                        holder.btnObservacion.setVisibility(View.VISIBLE);
                    } else {
                        holder.btnObservacion.setVisibility(View.GONE);
//                            holder.btnObservacionSuper.setVisibility(View.GONE);
                    }
                }
                holder.btnObservacionSuper.setVisibility(View.GONE);
                holder.list_item.setBackgroundColor(getContext().getResources().getColor(R.color.color_list));
//                    holder.list_it.setEnabled(false);
//                    holder.btnEliminar.setEnabled(false);
                break;
            case "PRECONCLUIDO":
                holder.txtEstado.setText(Html.fromHtml(incidencia + "<br>" + "(REVISION)"));
                if (Integer.parseInt(objView.get("id_tipo_obs").toString()) != 2 && Integer.parseInt(objView.get("id_tipo_obs").toString()) != 8) {
                    holder.txtEstado.setTextColor(getContext().getResources().getColor(R.color.colorWarning));
                    holder.btnObservacion.setVisibility(View.GONE);
                } else if (Integer.parseInt(objView.get("id_tipo_obs").toString()) == 8) {
                    holder.txtEstado.setTextColor(getContext().getResources().getColor(R.color.color_concluido));
                    holder.btnObservacion.setVisibility(View.GONE);
                } else {
                    holder.txtEstado.setTextColor(getContext().getResources().getColor(R.color.colorWarning));
                    if (RolPermiso.tienePermiso(Usuario.getRol(), "observacion")) {
                        holder.btnObservacion.setVisibility(View.VISIBLE);
                    } else {
                        holder.btnObservacion.setVisibility(View.GONE);
                    }
                }
                if (Integer.parseInt(Informante.obtenerIncidenciaFinalBoleta(Integer.parseInt(objView.get("id_asignacion").toString()), Integer.parseInt(objView.get("correlativo").toString()))) > 0) {

                    holder.btnObservacionSuper.setVisibility(View.VISIBLE);
                } else {
                    holder.btnObservacionSuper.setVisibility(View.GONE);
                }
                holder.list_item.setBackgroundColor(getContext().getResources().getColor(R.color.colorSucess));
                break;
        }

        holder.txtDescripcion.setText(Html.fromHtml(objView.get("preguntas_respuestas_iniciales").toString()));


        holder.btnEliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((BoletaActivity) activity).decisionMessageDelete(activity, null, null, "Confirmar", Html.fromHtml("Se perdera la información de la vivienda"), Integer.valueOf(objView.get("id_asignacion").toString()), Integer.valueOf(objView.get("correlativo").toString()), Integer.valueOf(objView.get("id_upm").toString()), "hogar");
            }
        });

        holder.btnMapa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((BoletaActivity) activity).irMap2((Integer) objView.get("id_upm"), 3, (Integer) objView.get("id_asignacion"),(Integer) objView.get("correlativo"));
            }
        });

        holder.btnObservacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((BoletaActivity) activity).informationMessageObs(null, "HISTORIAL: " + objView.get("codigo"), Html.fromHtml(String.valueOf(objView.get("observacion_historial"))), Parametros.FONT_OBS);
            }
        });

        holder.btnObservacionSuper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Screem Loocked
                startThree();

                ArrayList supervisores = new ArrayList();
                supervisores.add("YRE_123_ABC");
                supervisores.add("ABL_RWG_PMY");
                supervisores.add("ADN_WVV_USU");
                supervisores.add("AJS_DBT_CMN");
                supervisores.add("BFP_PGU_VCN");
                supervisores.add("BIL_JQY_DDY");
                supervisores.add("BRE_ESN_UKG");
                supervisores.add("BUT_YBO_TCL");
                supervisores.add("BZM_VFL_VCO");
                supervisores.add("CBL_QZU_KPU");
                supervisores.add("CEW_OKB_KIC");
                supervisores.add("CZZ_ADP_FFM");
                supervisores.add("DDP_HCJ_YBN");
                supervisores.add("DMR_LJD_RGV");
                supervisores.add("DMU_EFY_ATQ");
                supervisores.add("DSA_PVX_OBK");
                supervisores.add("DXN_BLE_FVO");
                supervisores.add("EDL_QRL_EXN");
                supervisores.add("EZV_JNG_JQW");
                supervisores.add("FBR_AIR_UYH");
                supervisores.add("FNM_PRG_UFG");
                supervisores.add("FZC_VZF_MGL");
                supervisores.add("HWN_MDP_LFN");
                supervisores.add("IBZ_CAI_XYL");
                supervisores.add("IDK_KYZ_YII");
                supervisores.add("IGL_URH_RYJ");
                supervisores.add("KCX_MGY_IWB");
                supervisores.add("LIW_XLK_YZL");
                supervisores.add("LRJ_MOH_QDN");
                supervisores.add("LYB_PCM_TDW");
                supervisores.add("MRH_WEV_KAE");
                supervisores.add("MWW_UNM_VRN");
                supervisores.add("MXQ_WJA_HUU");
                supervisores.add("MYI_DWD_TMU");
                supervisores.add("NGY_FXW_WQS");
                supervisores.add("NIQ_JRX_PYK");
                supervisores.add("OSQ_EGJ_JZF");
                supervisores.add("OVT_YHN_NMP");
                supervisores.add("PFT_CYZ_KHU");
                supervisores.add("PML_DTU_BKN");
                supervisores.add("POU_VWK_LLW");
                supervisores.add("PPF_WNZ_VVC");
                supervisores.add("PWX_CGV_JSE");
                supervisores.add("QCR_KZS_YQE");
                supervisores.add("QQB_QKZ_ORE");
                supervisores.add("QTD_GGL_NLN");
                supervisores.add("QVX_ZPH_LYO");
                supervisores.add("REY_XSG_HNB");
                supervisores.add("RIO_NIC_ASS");
                supervisores.add("SHX_RZX_FCD");
                supervisores.add("TAL_JNC_EAQ");
                supervisores.add("TLX_GQR_OCC");
                supervisores.add("TPT_OFQ_SKD");
                supervisores.add("TRI_RNV_IOV");
                supervisores.add("TRK_HVB_SME");
                supervisores.add("TSG_HIG_OWA");
                supervisores.add("TTG_NOB_YOD");
                supervisores.add("UEF_UYU_JNI");
                supervisores.add("UEL_SWW_FVI");
                supervisores.add("VJC_EKK_POW");
                supervisores.add("VLD_SOW_BQO");
                supervisores.add("VSR_FQU_UBR");
                supervisores.add("XGO_LHA_XHX");
                supervisores.add("XJM_WTJ_TVC");
                supervisores.add("XLX_UNJ_ZIC");
                supervisores.add("YLR_UQS_YBN");
                supervisores.add("YUN_EOR_UFW");
                supervisores.add("ZGK_BJC_MOF");
                supervisores.add("ZME_OUM_PSM");
                supervisores.add("ZNQ_UZA_RHU");
                supervisores.add("ZTK_AMT_ZDS");
                supervisores.add("ZWQ_QEL_XRY");
                supervisores.add("XKU_SDF_FLO");
                supervisores.add("XTA_CHT_ISK");
                supervisores.add("XUQ_TNR_QFC");
                supervisores.add("TSS_UVA_UMR");
                supervisores.add("KCA_GMA_ETU");

                ValidatorConsistencias validatorConsistencias = new ValidatorConsistencias();
                IdInformante idInf = new IdInformante(Integer.valueOf(objView.get("id_asignacion").toString()), Integer.valueOf(objView.get("correlativo").toString()));
                Informante inf = new Informante();
                ArrayList<IdInformante> lstInf = Informante.getHijos(idInf);
                String resObs="";
                int cObs = 0;
                for (IdInformante element : lstInf) {
                    System.out.println(element.toString());
                    int vida = element.id_asignacion;
                    int vidc = element.correlativo;
                    validatorConsistencias.dropView();
                    validatorConsistencias.deleteConsistenciasObs(vida,vidc);
                    validatorConsistencias.createView(vida,vidc);
                    ArrayList<Map<String, String>> lsti = validatorConsistencias.getConsistencias();
                    String ctro="";
                    if (lsti.size()>0)
                    {
                        cObs= cObs+ lsti.size();
                        validatorConsistencias.loadConsistenciasObs();
                    }

                    ctro =Observacion.preparaHistorial(new IdInformante(vida, vidc));
                    if (ctro.equals("") || ctro == null ){
                        resObs = resObs+"<br>"+ validatorConsistencias.getConsistencias2();
                    }
                    else{
                        resObs = resObs+"<br>"+ Observacion.preparaHistorial(new IdInformante(vida, vidc));
                    }
                }
                //screem  locked
                endThree();

                if (cObs > 0) {
                    ((BoletaActivity) activity).informationMessageObs(null,
                            "ERROR DE CONSISTENCIAS " +
                                    "\nObservaciones("+cObs+"):" ,
                            Html.fromHtml(String.valueOf(resObs)),
                            Parametros.FONT_OBS);
                } else {
                    if (String.valueOf(objView.get("id_tipo_obs")).equals("0")) {
                        ((BoletaActivity) activity).observationSupervisorTab(activity, null, "APROBAR BOLETA", Html.fromHtml("SUPERVISOR(A) ingrese su codigo para aprobar esta boleta"), "", false, Integer.valueOf(objView.get("id_asignacion").toString()), Integer.valueOf(objView.get("correlativo").toString()), Integer.valueOf(objView.get("id_upm").toString()), supervisores, new IdInformante(Integer.valueOf(objView.get("id_asignacion").toString()), Integer.valueOf(objView.get("correlativo").toString())));
                    } else {
                        supervisores.add("APROBAR");
                        ((BoletaActivity) activity).observationSupervisorTab(activity, null, "APROBAR BOLETA", Html.fromHtml("Ingrese la palabra APROBAR para aprobar esta boleta"), "", false, Integer.valueOf(objView.get("id_asignacion").toString()), Integer.valueOf(objView.get("correlativo").toString()), Integer.valueOf(objView.get("id_upm").toString()), supervisores, new IdInformante(Integer.valueOf(objView.get("id_asignacion").toString()), Integer.valueOf(objView.get("correlativo").toString())));
                    }
                }
            }
        });
        holder.btnVerDato.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (button1_pressed + 3000 > System.currentTimeMillis()) {

                } else {
                    button1_pressed = System.currentTimeMillis();
//                    ((BoletaActivity)activity).informationMessageObs(null, "RESPUESTAS: "+objView.get("codigo"), Html.fromHtml(String.valueOf(objView.get("encuesta_respuestas"))), Parametros.FONT_OBS);
                    ((ActionBarActivityNavigator) activity).irResumen(new IdInformante(Integer.valueOf(objView.get("id_asignacion").toString()), Integer.valueOf(objView.get("correlativo").toString())), 0);

                }


            }
        });
        holder.btnVerDatoPersona.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (button2_pressed + 3000 > System.currentTimeMillis()) {

                } else {
                    button2_pressed = System.currentTimeMillis();
                    ((BoletaActivity) activity).informationMessageObs(null, "RESPUESTAS: " + objView.get("codigo"), Html.fromHtml(String.valueOf(objView.get("encuesta_respuestas_personas"))), Parametros.FONT_OBS);

                }
            }
        });
        holder.list_revision_super.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new pdfView().execute(Integer.parseInt(objView.get("id_asignacion").toString()), Integer.parseInt(objView.get("correlativo").toString()));
//                pdfView(Integer.parseInt(objView.get("id_asignacion").toString()), Integer.parseInt(objView.get("correlativo").toString()));
            }
        });
//        setAnimation(holder.itemView, position);
    }


    @Override
    public int getItemCount() {
        return dataView.size();
    }

    public Object getItem(int position) {
        return dataView.get(position);
    }

    public void setOnClickListener(View.OnClickListener listener){
        this.listener = listener;
    }

    @Override
    public void onClick(View v) {
        if(listener!=null){
            listener.onClick(v);
        }

    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public LinearLayout list_item;
        public TextView informante_text_view;
        public TextView txtValor;
        public TextView txtDescripcion;
        public ImageButton btnObservacion;
        public Button btnObservacionSuper;
        public TextView txtEstado;
        public ImageButton btnEliminar;
        public ImageButton btnMapa;

        public Button btnVerDato;
        public Button btnVerDatoPersona;
        public Button list_revision_super;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            list_item = (LinearLayout) itemView.findViewById(R.id.item_boleta);
            txtValor = (TextView) itemView.findViewById(R.id.list_value);
            txtValor.setTextSize(Parametros.FONT_LIST_BIG);
            txtDescripcion = (TextView) itemView.findViewById(R.id.list_description);
            txtDescripcion.setTextSize(Parametros.FONT_LIST_SMALL);
            btnObservacion = (ImageButton) itemView.findViewById(R.id.list_observacion);
//            btnObservacion.setTextSize(Parametros.FONT_OBS);
            btnObservacionSuper = (Button) itemView.findViewById(R.id.list_observacion_super);
//            btnObservacion.setTextSize(Parametros.FONT_OBS);
            txtEstado = (TextView) itemView.findViewById(R.id.list_state);
            txtEstado.setTextSize(Parametros.FONT_LIST_SMALL);
            informante_text_view = (TextView) itemView.findViewById(R.id.informante_text_view);
            btnEliminar = (ImageButton) itemView.findViewById(R.id.eliminaBoleta);
            btnMapa = (ImageButton) itemView.findViewById(R.id.ver_mapa_segmento);

            btnVerDato = (Button) itemView.findViewById(R.id.list_revision);
            btnVerDatoPersona = (Button) itemView.findViewById(R.id.list_revision_person);
            list_revision_super = (Button) itemView.findViewById(R.id.list_revision_super);

        }
    }

    private void setAnimation(View viewToAnimate, int position) {
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > lastPosition) {
            animation = AnimationUtils.loadAnimation(getContext(), R.anim.item_anim_from_right);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }

    public class pdfView extends AsyncTask<Integer, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            startThree();
            if(!lottie.isShowing()){
                lottie.show();
            }
        }

        @Override
        protected String doInBackground(Integer... params) {
            templatePdf = new TemplatePdf(activity, params[0], params[1]);
            templatePdf.openDocument();
            templatePdf.addTitle(String.valueOf(R.string.title_main), "Gestion 2023", new Date());
            Informante informante = new Informante();
            Map<String, ArrayList<Map<String, Object>>> datos = informante.obtenerListadoPdf(new IdInformante(params[0], params[1]));
            Log.d("valores", String.valueOf(datos));

            for (String key : datos.keySet()) {
                templatePdf.addParagraph(key);
                templatePdf.createTable(header, getClients(datos.get(key)));
            }
            templatePdf.closeDocument();

            return "ok";
        }

        @Override
        protected void onPostExecute(String mensaje) {
            lottie.dismiss();
            endThree();

            templatePdf.viewPDF();
        }
    }

    public ArrayList<String[]>getClients(ArrayList<Map<String, Object>> datos){

        ArrayList<String[]> rows = new ArrayList<>();
        for(int i = 0; i< datos.size(); i++) {
            Map<String, Object> d = datos.get(i);
            Log.d("valores", d.get("codigo").toString()+ d.get("codigo_pregunta").toString()+ d.get("pregunta").toString()+ d.get("respuesta").toString()+d.get("observacion").toString());
            rows.add(new String[]{d.get("codigo").toString(), d.get("codigo_pregunta").toString(), Html.fromHtml(d.get("pregunta").toString()).toString(), d.get("respuesta").toString(),d.get("observacion").toString()});
        }
        return rows;
    }

    public void startThree() {
        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    //DESBLOQUEA LA ORIENTACION DE LA PANTALLA
    public void endThree() {
        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

//    public void consistenciaAutomatica(){
//        ((BoletaActivity) activity).observationSupervisorTab(activity, null, "APROBAR BOLETA", Html.fromHtml("LAS CONSISTENCIAS SE EJECUTARON DE MANERA EXITOSA SIN ERRORES, PUEDE APROBAR LA BOLETA"), "", false, 0, 0, 0, 0,null, null));
//    }

}
