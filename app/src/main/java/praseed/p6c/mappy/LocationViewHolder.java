package praseed.p6c.mappy;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

/**
 * Created by praseedm on 10/25/2016.
 */
public class LocationViewHolder extends RecyclerView.ViewHolder {
    public TextView nameView, latView, longView, accView;

    public LocationViewHolder(View itemView) {
        super(itemView);
        nameView = (TextView) itemView.findViewById(R.id.itemName);
        latView = (TextView) itemView.findViewById(R.id.itemLat);
        longView = (TextView) itemView.findViewById(R.id.itemLong);
        accView = (TextView) itemView.findViewById(R.id.itemAcc);
    }
}
