package bo.gob.ine.naci.epc.adaptadores;

import static bo.gob.ine.naci.epc.MyApplication.getContext;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
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
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.ListPopupWindow;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

import bo.gob.ine.naci.epc.BoletaActivity;
import bo.gob.ine.naci.epc.EncuestaActivity2;
import bo.gob.ine.naci.epc.R;
import bo.gob.ine.naci.epc.entidades.Encuesta;
import bo.gob.ine.naci.epc.entidades.IdInformante;
import bo.gob.ine.naci.epc.entidades.Informante;
import bo.gob.ine.naci.epc.entidades.Observacion;
import bo.gob.ine.naci.epc.entidades.RolPermiso;
import bo.gob.ine.naci.epc.entidades.Usuario;
import bo.gob.ine.naci.epc.herramientas.ActionBarActivityNavigator;
import bo.gob.ine.naci.epc.herramientas.LottieDialog;
import bo.gob.ine.naci.epc.herramientas.Parametros;
import bo.gob.ine.naci.epc.herramientas.TemplatePdf;
import bo.gob.ine.naci.epc.herramientas.ValidatorConsistencias;

public class TablaMatrizAdapterRecycler extends RecyclerView.Adapter<TablaMatrizAdapterRecycler.ViewHolder>  implements View.OnClickListener{

    private static final String TAG = "RecyclerAdapter";
    private ArrayList<Map<String, Object>> dataView;
    private Context context = null;
    private View.OnClickListener listener;
    private ListPopupWindow listPopupWindow;
    private int idNivel;
    private int lastPosition = -1;
    private Animation animation;
    private static long button1_pressed;
    private static long button2_pressed;
    private Activity activity = null;

    private OnItemDeleteListener itemDeleteListener;

    public TablaMatrizAdapterRecycler(Context context, ArrayList<Map<String, Object>> dataView) {
        this.dataView = dataView;
        this.context = context;

    }

    public interface OnItemDeleteListener {
        void onDeleteItem(int position, int idAsignacion, int correlativo, int fila);
    }

    public void setOnItemDeleteListener(OnItemDeleteListener listener) {
        this.itemDeleteListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.adapter_list_tabla_matriz, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);

        view.setOnClickListener(this);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        final Map<String, Object> objView = dataView.get(position);

        holder.tnombre.setText(objView.get("fila").toString()+ " Nombre: " + objView.get("nombre").toString());
        holder.tsexo.setText("Sexo: " + objView.get("sexo").toString());
        holder.tedad.setText("Edad: " + objView.get("edad").toString());
//        holder.btnEliminar.setVisibility(View.GONE);
        holder.btnEliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                itemDeleteListener.onDeleteItem(holder.getAdapterPosition(), Integer.parseInt(objView.get("id_asignacion").toString()),Integer.parseInt(objView.get("correlativo").toString()),Integer.parseInt(objView.get("fila").toString()));
                //((EncuestaActivity2) activity).decisionMessageDelete(activity, null, null, "Confirmar", Html.fromHtml("Se perdera la información de este registro"), Integer.valueOf(objView.get("id_asignacion").toString()), Integer.valueOf(objView.get("correlativo").toString()), 0, "bucle", Integer.parseInt(objView.get("fila").toString()));
//                Encuesta.borrarFilaBucle(Integer.parseInt(objView.get("id_asignacion").toString()),Integer.parseInt(objView.get("correlativo").toString()),Integer.parseInt(objView.get("fila").toString()));
            }
        });

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

        public TextView tnombre;
        public TextView tsexo;
        public TextView tedad;
        public ImageButton btnEliminar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tnombre = (TextView) itemView.findViewById(R.id.tnombre);
            tsexo = (TextView) itemView.findViewById(R.id.tsexo);
            tedad = (TextView) itemView.findViewById(R.id.tedad);
            btnEliminar = (ImageButton) itemView.findViewById(R.id.eliminaBoleta);

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
}
