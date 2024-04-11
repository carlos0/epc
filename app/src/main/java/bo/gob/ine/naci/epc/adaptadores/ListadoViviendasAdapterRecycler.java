package bo.gob.ine.naci.epc.adaptadores;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.ListPopupWindow;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Map;

import bo.gob.ine.naci.epc.MyApplication;
import bo.gob.ine.naci.epc.R;
import bo.gob.ine.naci.epc.herramientas.Parametros;

import static bo.gob.ine.naci.epc.MyApplication.getContext;

public class ListadoViviendasAdapterRecycler extends RecyclerView.Adapter<ListadoViviendasAdapterRecycler.ViewHolder> implements View.OnClickListener {

    private static final String TAG = "RecyclerAdapter";
    private ArrayList<Map<String, Object>> dataView;
    private Activity activity = null;
    private View.OnClickListener listener;
    onLongItemClickListener  olistener;
    private ListPopupWindow listPopupWindow;
    private int lastPosition = -1;
    private Animation animation;

    public ListadoViviendasAdapterRecycler(Activity activity, ArrayList<Map<String, Object>> dataView) {
        this.dataView = dataView;
        this.activity = activity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.adapter_list_listado_viviendas, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);

        view.setOnClickListener(this);


        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, @SuppressLint("RecyclerView") final int position) {

        final Map<String, Object> objView = dataView.get(position);
//        vi.setId((int) objView.get("id_upm"));
        holder.txtValor.setText(objView.get("codigo").toString());

        holder.txtDescripcion.setVisibility(View.VISIBLE);
        String estado = objView.get("estado").toString();

        holder.txtValor.setText(objView.get("codigo").toString());
        holder.txtDescripcion.setText(Html.fromHtml("<span style=\"color: #643188;\"><strong> AVENIDA/ CALLE/ CALLEJON/ COMUNIDAD O LOCALIDAD: </strong></span>" + objView.get("descripcion").toString()));
        //estado
        holder.txtNroViviendaObjetoEstudio.setText(String.valueOf(objView.get("omitida")).trim().equals("1")?"OMITIDA":
                (objView.get("nro_orden_vivienda").toString().startsWith("VOE")?
                        (estado.equals("ELABORADO")?"NO ENTRÓ A LA SELECCIÓN":estado):
                        objView.get("nro_orden_vivienda").toString()));
//        objView.get("nro_orden_vivienda").toString().startsWith("VOE")?(estado.equals("ELABORADO")?"NO ENTRÓ A LA SELECCIÓN":estado):objView.get("nro_orden_vivienda").toString()

        //voe

        holder.txtNroManzana.setText(objView.get("recorrido_manzana").toString());

//        String part [] = estado.split(";");
//        if (part.length > 1) {
//            holder.txtEstado.setText(part[0]+" ("+part[1]+")");
//        } else {

//        }

        if (objView.get("estado").toString().startsWith("ELABORADO")) {
            holder.contenedor.setBackgroundResource(R.drawable.list_encuesta_activity_bg2);
//            holder.list_upm_content.setBackgroundResource(R.drawable.ic_lv);
//            holder.list_predio_content.setBackgroundResource(R.drawable.ic_lv);
//            holder.list_voe_content.setBackgroundResource(R.drawable.ic_lv);
            holder.txtNroViviendaObjetoEstudio.setTextColor(MyApplication.getContext().getResources().getColor(R.color.colorPrimary));
        } else {
            holder.contenedor.setBackgroundResource(R.drawable.list_seleccionada_bg);
//            holder.list_upm_content.setBackgroundResource(R.drawable.ic_seleccionado);
//            holder.list_predio_content.setBackgroundResource(R.drawable.ic_seleccionado);
//            holder.list_voe_content.setBackgroundResource(R.drawable.ic_seleccionado);
            holder.txtNroViviendaObjetoEstudio.setTextColor(MyApplication.getContext().getResources().getColor(R.color.colorPrimary));

//            holder.viewF.setBackgroundColor(MyApplication.getContext().getResources().getColor(R.color.colorWaterSucess));
        }

        if(String.valueOf(objView.get("omitida")).trim().equals("1")) {
            holder.contenedor.setBackgroundResource(R.drawable.list_seleccionada_bg);
        }

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (olistener != null) {
                    olistener.ItemLongClicked(v, position);
                }

                return true;
            }
        });

        holder.lv_codigo.setText(Html.fromHtml(objView.get("upm").toString()+"<br><strong>MANZANA:</strong> "+objView.get("manzana").toString()));
//        holder.list_voe_content.setVisibility(objView.get("nro_orden_vivienda").toString().startsWith("VOE")?View.VISIBLE:View.GONE);
        holder.lv_predio.setText(objView.get("nro_orden_vivienda").toString().startsWith("VOE")?Html.fromHtml("<strong>Predio:</strong> " + objView.get("predio").toString()+"<br>"+objView.get("nro_orden_vivienda").toString()):Html.fromHtml("<strong>Predio:</strong> " + objView.get("predio").toString()));
//        holder.txtNumeroVoe.setText(objView.get("nro_orden_vivienda").toString().startsWith("VOE")?objView.get("nro_orden_vivienda").toString():"");
//        holder.lv_manzana.setText(Html.fromHtml("<strong>MANZANA:</strong> "+objView.get("manzana").toString()));
        Log.d("USO",objView.get("uso").toString() );
//        holder.lv_uso.setText(objView.get("uso").toString().substring(2));
        holder.list_orden.setText(Html.fromHtml("<span style=\"color: #643188;\"><strong>Orden de Vivienda:</strong></span> " + objView.get("orden").toString()));
        holder.list_jefe.setText(Html.fromHtml("<span style=\"color: #643188;\"><strong>JEFE/A DE HOGAR:</strong></span> " + objView.get("jefe").toString()));
        holder.count_hogar.setText(objView.get("hogar").toString());
        holder.count_hombre.setText(objView.get("hombres").toString());
        holder.count_mujer.setText(objView.get("mujeres").toString());

//        setAnimation(holder.itemView, position);
    }

    @Override
    public int getItemCount() {
        if(dataView == null){
            return 0;
        } else {
            return dataView.size();
        }
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

    public void setOnLongItemClickListener(onLongItemClickListener onLongItemClickListener) {
        olistener = onLongItemClickListener;
    }

    public interface onLongItemClickListener {
        void ItemLongClicked(View v, int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public RelativeLayout viewB;
        public RelativeLayout viewF;

        public LinearLayout list_it;
        public LinearLayout list_it2;

        public LinearLayout contenedor;
        public LinearLayout list_upm_content;
        public LinearLayout list_predio_content;
        public LinearLayout list_voe_content;

        public TextView txtValor;
        public TextView txtNumeroVoe;
        public TextView txtNroManzana;
        public TextView txtDescripcion;
        public TextView list_jefe;
        public TextView txtNroViviendaObjetoEstudio;

        public TextView lv_codigo;
        public TextView lv_predio;
        public TextView lv_manzana;
        public TextView lv_uso;
        public TextView count_hogar;
        public TextView count_hombre;
        public TextView count_mujer;
        public TextView list_orden;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            lv_codigo = itemView.findViewById(R.id.lv_codigo);
            lv_codigo.setTextSize(Parametros.FONT_LIST_BIG);
            lv_predio = itemView.findViewById(R.id.lv_predio);
            lv_predio.setTextSize(Parametros.FONT_LIST_BIG);
            lv_manzana = itemView.findViewById(R.id.lv_manzana);
            lv_manzana.setTextSize(Parametros.FONT_LIST_BIG);
            lv_uso = itemView.findViewById(R.id.lv_uso);
            lv_uso.setTextSize(Parametros.FONT_LIST_BIG);
            list_orden = itemView.findViewById(R.id.list_orden);
            list_orden.setTextSize(Parametros.FONT_LIST_BIG);
            count_hogar = itemView.findViewById(R.id.count_hogar);
            count_hombre = itemView.findViewById(R.id.count_hombre);
            count_mujer = itemView.findViewById(R.id.count_mujer);

            list_jefe = itemView.findViewById(R.id.list_jefe);
            list_jefe.setTextSize(Parametros.FONT_LIST_SMALL);

            list_it = itemView.findViewById(R.id.list_it_lv);
            list_it2 = itemView.findViewById(R.id.list_it_lv2);

            contenedor = itemView.findViewById(R.id.contenedor);
            list_voe_content = itemView.findViewById(R.id.list_voe_content);
            list_upm_content = itemView.findViewById(R.id.list_upm_content);
            list_predio_content = itemView.findViewById(R.id.list_predio_content);

            txtValor = itemView.findViewById(R.id.list_value);
            txtValor.setTextSize(Parametros.FONT_LIST_BIG);
            txtDescripcion = itemView.findViewById(R.id.list_descripcion);
            txtDescripcion.setTextSize(Parametros.FONT_LIST_SMALL);

            txtNumeroVoe = itemView.findViewById(R.id.list_voe);
            txtNumeroVoe.setTextSize(Parametros.FONT_LIST_BIG);
            txtNroManzana = itemView.findViewById(R.id.list_mzn);
            txtNroManzana.setTextSize(Parametros.FONT_LIST_BIG);

            txtNroViviendaObjetoEstudio = itemView.findViewById(R.id.list_nro_vivienda_objeto_estudio);
            txtNroViviendaObjetoEstudio.setTextSize(Parametros.FONT_LIST_BIG);

//            viewF = itemView.findViewById(R.id.list_it_lv);
//            viewB = itemView.findViewById(R.id.view_background);
            list_it = itemView.findViewById(R.id.list_it_lv);
            list_it2 = itemView.findViewById(R.id.list_it_lv2);
//            txtState = itemView.findViewById(R.id.list_state);
//            txtState.setTextSize(Parametros.FONT_LIST_BIG);
            txtValor = itemView.findViewById(R.id.list_value);
            txtValor.setTextSize(Parametros.FONT_LIST_BIG);
            txtDescripcion = itemView.findViewById(R.id.list_descripcion);
            txtDescripcion.setTextSize(Parametros.FONT_LIST_SMALL);
            txtNumeroVoe = itemView.findViewById(R.id.list_voe);
            txtNumeroVoe.setTextSize(Parametros.FONT_LIST_BIG);
            txtNroManzana = itemView.findViewById(R.id.list_mzn);
            txtNroManzana.setTextSize(Parametros.FONT_LIST_BIG);


            txtNroViviendaObjetoEstudio = itemView.findViewById(R.id.list_nro_vivienda_objeto_estudio);
            txtNroViviendaObjetoEstudio.setTextSize(Parametros.FONT_LIST_BIG);
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

    public void removeItem(int position){
        dataView.remove(position);
        notifyItemRemoved(position);

    }

    public void restoredItem(Map<String, Object> item, int position){
        dataView.add(position,item);
        notifyItemInserted(position);
    }
}
