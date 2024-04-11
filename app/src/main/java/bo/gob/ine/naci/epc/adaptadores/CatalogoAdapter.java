package bo.gob.ine.naci.epc.adaptadores;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Map;

import bo.gob.ine.naci.epc.MyApplication;
import bo.gob.ine.naci.epc.R;
import bo.gob.ine.naci.epc.preguntas.PreguntaView;


/**
 * Created by INE.
 */
public class CatalogoAdapter extends BaseAdapter {
    private ArrayList<Map<String, Object>> dataView;
    private LayoutInflater inflater = null;
    private SwipeDetector swipeDetector;
    private PreguntaView pregunta;
    private Context context;

    public CatalogoAdapter(Context context, PreguntaView pregunta, ArrayList<Map<String, Object>> dataView) {
        this.dataView = dataView;
        this.context = context;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.pregunta = pregunta;
        swipeDetector = new SwipeDetector();
    }


    @Override
    public int getCount() {
        if (dataView.size() <= 0)
            return 1;
        return dataView.size();
    }

    @Override
    public Object getItem(int position) {
        return dataView.get(position);
    }

    @Override
    public long getItemId(int position) {
        if (dataView.size() > 0) {
            return (long)(int)dataView.get(position).entrySet().iterator().next().getValue();
        } else {
            return 0;
        }
    }

    public static class ViewHolder
    {
        public TextView txtValor;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        ViewHolder holder;

        if (convertView == null) {
            vi = inflater.inflate(R.layout.adapter_catalogo, null);

            holder = new ViewHolder();
            holder.txtValor = (TextView) vi.findViewById(R.id.list_value);
            holder.txtValor.setBackgroundColor(MyApplication.getContext().getResources().getColor(R.color.colorBackgroundFocus2));
//            holder.txtValor.setTextSize(Parametros.FONT_LIST);

            vi.setTag(holder);

            vi.setLongClickable(true);
        } else {
            holder = (ViewHolder) vi.getTag();
        }

        if (dataView.size() > 0) {
            Map<String, Object> objView = dataView.get(position);

            vi.setId((int) String.valueOf(objView.get("id_catalogo")).length());
            holder.txtValor.setText(objView.get("descripcion").toString());
            vi.setOnClickListener(new OnItemClickListener(position));
            vi.setOnLongClickListener(new OnLongItemClickListener(position));
            vi.setOnTouchListener(swipeDetector);
        } else {
            holder.txtValor.setText("Seleccion realizada");
        }
        return vi;
    }

    private class OnItemClickListener implements View.OnClickListener {
        private int mPosition;

        OnItemClickListener(int position) {
            mPosition = position;
        }

        @Override
        public void onClick(View arg0) {
            switch (swipeDetector.getAction()) {
                case None: // Click
                    AdapterEvents sct = (AdapterEvents) pregunta;
                    sct.onItemClick(mPosition);
                    break;
                case RL: // Swipe Right to Left
                    break;
                case LR: // Swipe Left to Right
                    break;
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
            AdapterEvents sct = (AdapterEvents) pregunta;
            sct.onLongItemClick(mPosition);
            return true;
        }
    }
}
