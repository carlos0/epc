package bo.gob.ine.naci.epc.CallBacks;

import android.graphics.Canvas;
import android.graphics.Color;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import bo.gob.ine.naci.epc.R;
import bo.gob.ine.naci.epc.adaptadores.CallBackItemTouch;
import bo.gob.ine.naci.epc.adaptadores.ListadoViviendasAdapterRecycler;

public class MyItemTouchHelperCallback extends ItemTouchHelper.Callback {
    CallBackItemTouch callBackItemTouch;

    public MyItemTouchHelperCallback(CallBackItemTouch callBackItemTouch) {
        this.callBackItemTouch = callBackItemTouch;
    }

    @Override
    public boolean isLongPressDragEnabled() {
        return true;
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return true;
    }

    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        final int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        final int swipeFlagas = ItemTouchHelper.START | ItemTouchHelper.END;

        return  makeMovementFlags(dragFlags,swipeFlagas);
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        callBackItemTouch.itemTouchMode(viewHolder.getAdapterPosition(),
                target.getAdapterPosition());
        return true;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

        callBackItemTouch.onSwiped(viewHolder,viewHolder.getAdapterPosition());
    }

    @Override
    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        //super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        if(actionState == ItemTouchHelper.ACTION_STATE_DRAG){
            super.onChildDraw(c, recyclerView, viewHolder,dX,dX,actionState,isCurrentlyActive);

        } else {
            final View foregroundView = ((ListadoViviendasAdapterRecycler.ViewHolder)viewHolder).viewB;
            getDefaultUIUtil().onDrawOver(c,recyclerView,foregroundView,dX,dY,actionState,isCurrentlyActive);
        }
    }

    @Override
    public void onChildDrawOver(@NonNull Canvas c, @NonNull RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        if(actionState!=ItemTouchHelper.ACTION_STATE_DRAG) {
            final View foregroundView = ((ListadoViviendasAdapterRecycler.ViewHolder)viewHolder).viewF;
            getDefaultUIUtil().onDraw(c,recyclerView,foregroundView,dX,dY,actionState,isCurrentlyActive);
        }

    }

    @Override
    public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        final View foregroundView = ((ListadoViviendasAdapterRecycler.ViewHolder)viewHolder).viewF;
        foregroundView.setBackgroundColor(ContextCompat.getColor(((ListadoViviendasAdapterRecycler.ViewHolder)viewHolder).viewF.getContext(), R.color.color_list));
        getDefaultUIUtil().clearView(foregroundView);
    }

    @Override
    public void onSelectedChanged(@Nullable RecyclerView.ViewHolder viewHolder, int actionState) {
        super.onSelectedChanged(viewHolder, actionState);
        if(viewHolder!=null){
            final  View foregroundView = ((ListadoViviendasAdapterRecycler.ViewHolder)viewHolder).viewF;
            if(actionState==ItemTouchHelper.ACTION_STATE_DRAG){
                foregroundView.setBackgroundColor(Color.LTGRAY);

            }
            getDefaultUIUtil().onSelected(foregroundView);
        }
    }

    @Override
    public int convertToAbsoluteDirection(int flags, int layoutDirection) {
        return super.convertToAbsoluteDirection(flags, layoutDirection);
    }
}
