package app.mediabrainz.adapter.recycler.expandedRecycler;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;
import app.mediabrainz.R;


public abstract class BaseItemViewHolder extends RecyclerView.ViewHolder {

    protected View containerView;

    public BaseItemViewHolder(View itemView, boolean visible) {
        super(itemView);
        containerView = itemView.findViewById(R.id.containerView);
        setVisibility(visible);
    }

    public void setVisibility(boolean visible) {
        containerView.setVisibility(visible ? View.VISIBLE : View.GONE);
    }
}
