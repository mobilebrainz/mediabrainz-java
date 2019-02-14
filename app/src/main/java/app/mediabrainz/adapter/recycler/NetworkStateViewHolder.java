package app.mediabrainz.adapter.recycler;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;
import app.mediabrainz.R;
import app.mediabrainz.core.adapter.RetryCallback;
import app.mediabrainz.core.viewmodel.NetworkState;
import app.mediabrainz.core.viewmodel.event.Status;


public class NetworkStateViewHolder extends RecyclerView.ViewHolder {

    static final int VIEW_HOLDER_LAYOUT = R.layout.item_network_state;

    private TextView errorMessageTextView;
    private Button retryLoadingButton;
    private ProgressBar loadingProgressBar;

    private NetworkStateViewHolder(View itemView, RetryCallback retryCallback) {
        super(itemView);
        errorMessageTextView = itemView.findViewById(R.id.errorMessageTextView);
        loadingProgressBar = itemView.findViewById(R.id.loadingProgressBar);
        retryLoadingButton = itemView.findViewById(R.id.retryLoadingButton);
        retryLoadingButton.setOnClickListener(v -> retryCallback.retry());
    }

    public void bindTo(NetworkState networkState) {
        //errorView message
        errorMessageTextView.setVisibility(networkState.getMessage() != null ? View.VISIBLE : View.GONE);
        if (networkState.getMessage() != null) {
            errorMessageTextView.setText(networkState.getMessage());
        }

        //loading and retry
        retryLoadingButton.setVisibility(networkState.getStatus() == Status.ERROR ? View.VISIBLE : View.GONE);
        loadingProgressBar.setVisibility(networkState.getStatus() == Status.LOADING ? View.VISIBLE : View.GONE);
    }

    public static NetworkStateViewHolder create(ViewGroup parent, RetryCallback retryCallback) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(VIEW_HOLDER_LAYOUT, parent, false);
        return new NetworkStateViewHolder(view, retryCallback);
    }

}
