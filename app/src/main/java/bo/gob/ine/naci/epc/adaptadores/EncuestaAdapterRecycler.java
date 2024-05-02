package bo.gob.ine.naci.epc.adaptadores;

import android.app.Activity;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.ListPopupWindow;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Map;

import bo.gob.ine.naci.epc.EncuestaActivity2;
import bo.gob.ine.naci.epc.MyApplication;
import bo.gob.ine.naci.epc.R;
import bo.gob.ine.naci.epc.herramientas.Parametros;

public class EncuestaAdapterRecycler extends RecyclerView.Adapter<EncuestaAdapterRecycler.ViewHolder>  implements View.OnClickListener{

    private static final String TAG = "RecyclerAdapter";
    private ArrayList<Map<String, Object>> dataView;
    private Activity activity = null;
    private View.OnClickListener listener;
    private ListPopupWindow listPopupWindow;

    public EncuestaAdapterRecycler(Activity activity, ArrayList<Map<String, Object>> dataView) {
        this.dataView = dataView;
        this.activity = activity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.adapter_list_encuesta, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);

        view.setOnClickListener(this);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        final Map<String, Object> objView = dataView.get(position);
//        vi.setId((int) objView.get("id_upm"));
        holder.main_codigo.setTextSize(Parametros.FONT_LIST_BIG);
        holder.main_codigo.setText((String) objView.get("codigo"));
        holder.nombrePersona.setText((String)objView.get("nombrePersona"));
        holder.edadPersona.setText((String)objView.get("edadPersona") + " AÑOS");
        if(String.valueOf(objView.get("estado")).equals("CONCLUIDO")) {
            holder.main_mensaje.setTextColor(MyApplication.getContext().getResources().getColor(R.color.color_concluido));
            holder.main_mensaje.setText("BOLETA CONCLUIDA");
        }else{
            holder.main_mensaje.setText( "BOLETA INCOMPLETA");
            holder.main_mensaje.setTextColor(MyApplication.getContext().getResources().getColor(R.color.color_anulado));
        }
        holder.main_mensaje.setVisibility(View.VISIBLE);
        switch (Integer.parseInt(String.valueOf(objView.get("genero")==null?"3":objView.get("genero")).equals("--")?"3":objView.get("genero").toString())){

            case 2:
                if(!objView.get("edadPersona").toString().equals("--")) {
                    if (Integer.parseInt(objView.get("edadPersona").toString()) < 12) {
                        holder.imagenPersona.setImageResource(R.drawable.i_nino);
                        holder.imagenPersona.setPadding(0, 100, 0, 0);
                    } else {
                        holder.imagenPersona.setImageResource(R.drawable.i_hombre);
                    }
                } else {
                    holder.imagenPersona.setImageResource(R.drawable.ic_add_lv);
                }
                break;
            case 1:
                if(!objView.get("edadPersona").toString().equals("--")) {
                    if (Integer.parseInt(objView.get("edadPersona").toString()) < 12) {
                        holder.imagenPersona.setImageResource(R.drawable.i_nina);
                        holder.imagenPersona.setPadding(0, 100, 0, 0);
                    } else {
                        holder.imagenPersona.setImageResource(R.drawable.i_mujer);
                    }
                } else {
                    holder.imagenPersona.setImageResource(R.drawable.ic_add_lv);
                }
                break;
            default:
                holder.imagenPersona.setImageResource(R.drawable.ic_add_lv);
                break;
        }
        holder.btnEliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((EncuestaActivity2)activity).decisionMessageDelete(activity, null, null, "Confirmar", Html.fromHtml("Se perdera la información de la persona"), Integer.valueOf(objView.get("id_asignacion").toString()), Integer.valueOf(objView.get("correlativo").toString()), 0,"persona");
            }
        });
//        holder.porcentaje_avance.setText((Float) objView.get("porcentaje_avance")+"%");
//        if(Integer.parseInt(String.valueOf(objView.get("id_tipo_obs")))>0){
//            holder.main_descripcion.setBackgroundColor(MyApplication.getContext().getResources().getColor(R.color.colorWaterError));
//        }else {
//            holder.main_descripcion.setBackgroundColor(MyApplication.getContext().getResources().getColor(R.color.color_list));
//        }
        holder.porcentaje_avance.setVisibility(View.GONE);
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

        public TextView main_codigo;
        public TextView nombrePersona;
        public TextView edadPersona;
        public TextView main_mensaje;
        public TextView main_estado;
        public ImageButton btnEliminar;
        public TextView porcentaje_avance;
        public ImageView imagenPersona;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            main_codigo = itemView.findViewById(R.id.main_codigo);
            nombrePersona = itemView.findViewById(R.id.nombrePersona);
            edadPersona = itemView.findViewById(R.id.edadPersona);
            main_mensaje = itemView.findViewById(R.id.main_mensaje);
            main_estado = itemView.findViewById(R.id.main_estado);
            btnEliminar = (ImageButton) itemView.findViewById(R.id.eliminaPersona);
            porcentaje_avance = itemView.findViewById(R.id.porcentaje_avance);
            imagenPersona = itemView.findViewById(R.id.imagenPersona);

        }
    }
}
