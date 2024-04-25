package bo.gob.ine.naci.epc;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class BottomSheet extends BottomSheetDialogFragment {

    private LinearLayout layout;
    private LinearLayout bottomSheetLinearLayout;
    private static BottomSheet instance;

    // Método para establecer el layout personalizado
    public void setLayout(LinearLayout layout) {
        this.layout = layout;
    }

    public LinearLayout getLayout() {
        return layout;
    }

    public static synchronized BottomSheet getInstance(){
        if(instance==null){
            instance = new BottomSheet();
        }
        return instance;
    }



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_layout, container, false);

//         Aquí puedes encontrar el LinearLayout en tu layout bottom_sheet_layout
        LinearLayout bottomSheetLayout = view.findViewById(R.id.bottomSheetLayout);
        bottomSheetLayout = view.findViewById(R.id.bottomSheetLayout);


        // Agregar el layout personalizado al bottomSheetLayout
        if (layout != null && layout.getParent() == null) {
            // Agregar el layout personalizado al bottomSheetLayout
            bottomSheetLayout.addView(layout);
        }

        return view;
    }
}
