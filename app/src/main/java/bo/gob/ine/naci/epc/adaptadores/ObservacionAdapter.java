package bo.gob.ine.naci.epc.adaptadores;

import android.app.Activity;
import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Map;

import bo.gob.ine.naci.epc.R;
import bo.gob.ine.naci.epc.herramientas.Parametros;
//import bo.gob.ine.sice.eh2019.R;
//import bo.gob.ine.sice.eh2019.herramientas.Parametros;

/**
 * Created by INE.
 */
public class ObservacionAdapter extends RecyclerView.Adapter<ObservacionAdapter.ViewHolder> implements View.OnClickListener {
    private ArrayList<Map<String, Object>> dataView;
    private LayoutInflater inflater = null;
    private Activity activity = null;
    private SwipeDetector swipeDetector;
    private View.OnClickListener listener;

    public ObservacionAdapter(Activity activity, ArrayList<Map<String, Object>> dataView) {
        this.dataView = dataView;
        this.activity = activity;
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        swipeDetector = new SwipeDetector();
    }

//    @Override
//    public int getCount() {
//        if (dataView.size() <= 0)
//            return 1;
//        return dataView.size();
//    }

//    @Override
//    public Object getItem(int position) {
//        return dataView.get(position);
//    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.adapter_list_observacion, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);

        view.setOnClickListener(this);

        return viewHolder;
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        final Map<String, Object> objView = dataView.get(position);
        holder.txtValor.setText(String.valueOf(objView.get("codigo")));
        holder.txtTipoObservacion.setText(Html.fromHtml("<b>Tipo:</b> "+ objView.get("descripcion").toString()));
        holder.txtMensaje.setText(Html.fromHtml("<b>Observación:</b> " +objView.get("observacion").toString()));
        holder.txtCreado.setText(objView.get("usucre").toString() +" ("+ objView.get("feccre")+")" );
        switch ((Integer)objView.get("id_tipo_obs")){
            case 1:
                holder.txtTipoObservacion.setTextColor(activity.getResources().getColor(R.color.colorWarning));
                holder.txtMensaje.setTextColor(activity.getResources().getColor(R.color.colorWarning));
                holder.txtCreado.setTextColor(activity.getResources().getColor(R.color.colorWarning));
                holder.txtValor.setTextColor(activity.getResources().getColor(R.color.colorWarning));
                break;
            case 8:
                holder.txtTipoObservacion.setTextColor(activity.getResources().getColor(R.color.colorSucess));
                holder.txtMensaje.setTextColor(activity.getResources().getColor(R.color.colorSucess));
                holder.txtCreado.setTextColor(activity.getResources().getColor(R.color.colorSucess));
                holder.txtValor.setTextColor(activity.getResources().getColor(R.color.colorSucess));
                break;
            default:
                holder.txtTipoObservacion.setTextColor(activity.getResources().getColor(R.color.colorError));
                holder.txtMensaje.setTextColor(activity.getResources().getColor(R.color.colorError));
                holder.txtCreado.setTextColor(activity.getResources().getColor(R.color.colorError));
                holder.txtValor.setTextColor(activity.getResources().getColor(R.color.colorError));
                break;
        }

    }

//    @Override
//    public void onBindViewHolder(@NonNull ListadoViviendasAdapterRecycler.ViewHolder holder, int position) {
//
//    }

    @Override
    public long getItemId(int position) {
        if (dataView.size() > 0) {
            return (long)(int)dataView.get(position).entrySet().iterator().next().getValue();
        } else {
            return 0;
        }
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
    public void onClick(View view) {
        if(listener!=null){
            listener.onClick(view);
        }

    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        public LinearLayout list_it;
        public TextView txtValor;
        public TextView txtTipoObservacion;
        public TextView txtMensaje;
        public TextView txtCreado;
        public ImageView usuario;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            list_it = itemView.findViewById(R.id.list_it);

            txtValor = itemView.findViewById(R.id.list_value);
            txtValor.setTextSize(Parametros.FONT_LIST_BIG);
            txtTipoObservacion = itemView.findViewById(R.id.list_descripcion);
            txtTipoObservacion.setTextSize(Parametros.FONT_OBS);
            txtMensaje = itemView.findViewById(R.id.list_mensaje);
            txtMensaje.setTextSize(Parametros.FONT_OBS);
            txtCreado = itemView.findViewById(R.id.list_creado);
            txtCreado.setTextSize(Parametros.FONT_OBS);
//            usuario = itemView.findViewById(R.id.usuario);

//            if (dataView.size() > 0) {
//                Map<String, Object> objView = dataView.get(position);
//
//                vi.setId((int) objView.get("id_observacion"));
//                holder.txtValor.setText(objView.get("codigo").toString());
//                holder.txtTipoObservacion.setText(Html.fromHtml("<b>Tipo:</b> "+ objView.get("descripcion").toString()));
//                holder.txtMensaje.setText(Html.fromHtml("<b>Observación:</b> " +objView.get("observacion").toString()));
//                holder.txtCreado.setText(objView.get("usucre").toString() +" ("+ objView.get("feccre")+")" );
//                switch ((Integer)objView.get("id_tipo_obs")){
//                    case 1:
//                        holder.txtTipoObservacion.setTextColor(activity.getResources().getColor(R.color.colorWarning));
//                        holder.txtMensaje.setTextColor(activity.getResources().getColor(R.color.colorWarning));
//                        holder.txtCreado.setTextColor(activity.getResources().getColor(R.color.colorWarning));
//                        holder.txtValor.setTextColor(activity.getResources().getColor(R.color.colorWarning));
//                        break;
//                    case 8:
//                        holder.txtTipoObservacion.setTextColor(activity.getResources().getColor(R.color.colorSucess));
//                        holder.txtMensaje.setTextColor(activity.getResources().getColor(R.color.colorSucess));
//                        holder.txtCreado.setTextColor(activity.getResources().getColor(R.color.colorSucess));
//                        holder.txtValor.setTextColor(activity.getResources().getColor(R.color.colorSucess));
//                        break;
//                    default:
//                        holder.txtTipoObservacion.setTextColor(activity.getResources().getColor(R.color.colorError));
//                        holder.txtMensaje.setTextColor(activity.getResources().getColor(R.color.colorError));
//                        holder.txtCreado.setTextColor(activity.getResources().getColor(R.color.colorError));
//                        holder.txtValor.setTextColor(activity.getResources().getColor(R.color.colorError));
//                        break;
//                }
//                vi.setOnClickListener(new OnItemClickListener(position));
//                vi.setOnLongClickListener(new OnLongItemClickListener(position));
//                vi.setOnTouchListener(swipeDetector);
//
//            } else {
//                holder.txtValor.setText("No existen datos.");
//                holder.txtTipoObservacion.setText("");
//                holder.txtMensaje.setText("");
//
//            }
        }
    }

    //LLENA CON DATOS EL INFORMANTE ADAPTER
//    @Override
//    public View getView(int position, View convertView, ViewGroup parent) {
//        View vi = convertView;
//        ViewHolder holder;
//        try {
//            if (convertView == null) {
//                vi = inflater.inflate(R.layout.adapter_list_observacion, null);
//
//                holder = new ViewHolder();
//                holder.txtValor = (TextView) vi.findViewById(R.id.list_value);
//                holder.txtValor.setTextSize(Parametros.FONT_LIST_BIG);
//                holder.txtTipoObservacion = (TextView) vi.findViewById(R.id.list_descripcion);
//                holder.txtTipoObservacion.setTextSize(Parametros.FONT_OBS);
//                holder.txtMensaje = (TextView) vi.findViewById(R.id.list_mensaje);
//                holder.txtMensaje.setTextSize(Parametros.FONT_OBS);
//                holder.txtCreado = (TextView) vi.findViewById(R.id.list_creado);
//                holder.txtCreado.setTextSize(Parametros.FONT_OBS);
//                vi.setTag(holder);
//                //vi.setLongClickable(true);
//            } else {
//                holder = (ViewHolder) vi.getTag();
//            }
//
//            if (dataView.size() > 0) {
//                Map<String, Object> objView = dataView.get(position);
//
//                vi.setId((int) objView.get("id_observacion"));
//                holder.txtValor.setText(objView.get("codigo").toString());
//                holder.txtTipoObservacion.setText(Html.fromHtml("<b>Tipo:</b> "+ objView.get("descripcion").toString()));
//                holder.txtMensaje.setText(Html.fromHtml("<b>Observación:</b> " +objView.get("observacion").toString()));
//                holder.txtCreado.setText(objView.get("usucre").toString() +" ("+ objView.get("feccre")+")" );
//                switch ((Integer)objView.get("id_tipo_obs")){
//                    case 1:
//                        holder.txtTipoObservacion.setTextColor(activity.getResources().getColor(R.color.colorWarning));
//                        holder.txtMensaje.setTextColor(activity.getResources().getColor(R.color.colorWarning));
//                        holder.txtCreado.setTextColor(activity.getResources().getColor(R.color.colorWarning));
//                        holder.txtValor.setTextColor(activity.getResources().getColor(R.color.colorWarning));
//                        break;
//                    case 8:
//                        holder.txtTipoObservacion.setTextColor(activity.getResources().getColor(R.color.colorSucess));
//                        holder.txtMensaje.setTextColor(activity.getResources().getColor(R.color.colorSucess));
//                        holder.txtCreado.setTextColor(activity.getResources().getColor(R.color.colorSucess));
//                        holder.txtValor.setTextColor(activity.getResources().getColor(R.color.colorSucess));
//                        break;
//                    default:
//                        holder.txtTipoObservacion.setTextColor(activity.getResources().getColor(R.color.colorError));
//                        holder.txtMensaje.setTextColor(activity.getResources().getColor(R.color.colorError));
//                        holder.txtCreado.setTextColor(activity.getResources().getColor(R.color.colorError));
//                        holder.txtValor.setTextColor(activity.getResources().getColor(R.color.colorError));
//                        break;
//                }
//                vi.setOnClickListener(new OnItemClickListener(position));
//                vi.setOnLongClickListener(new OnLongItemClickListener(position));
//                vi.setOnTouchListener(swipeDetector);
//
//            } else {
//                holder.txtValor.setText("No existen datos.");
//                holder.txtTipoObservacion.setText("");
//                holder.txtMensaje.setText("");
//
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return vi;
//    }

    private class OnItemClickListener implements View.OnClickListener {
        private int mPosition;

        OnItemClickListener(int position) {
            mPosition = position;
        }

        @Override
        public void onClick(View arg0) {
            switch (swipeDetector.getAction()) {
                case None: {// Click
                    if (activity instanceof AdapterEvents) {
                        AdapterEvents sct = (AdapterEvents) activity;
                        sct.onItemClick(mPosition);
                    }
                    break;
                }
                case RL: {// Swipe Right to Left
                    if (activity instanceof AdapterMove) {
                        AdapterMove sct = (AdapterMove) activity;
                        sct.onLeft(mPosition);
                    }
                    break;
                }
                case LR: {// Swipe Left to Right
                    if (activity instanceof AdapterMove) {
                        AdapterMove sct = (AdapterMove) activity;
                        sct.onRight(mPosition);
                    }
                    break;
                }
            }
        }
    }

    private class OnLongItemClickListener implements View.OnLongClickListener {
        private int mPosition;

        OnLongItemClickListener(int position) {
            mPosition = position;
        }

        @Override
        public boolean onLongClick(View v) {
            if (activity instanceof AdapterEvents) {
                AdapterEvents sct = (AdapterEvents) activity;
                sct.onLongItemClick(mPosition);
            }
            return true;
        }
    }



}
