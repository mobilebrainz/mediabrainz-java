package app.mediabrainz.adapter.recycler;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import app.mediabrainz.MediaBrainzApp;
import app.mediabrainz.R;
import app.mediabrainz.api.coverart.CoverArtImage;
import app.mediabrainz.api.model.Rating;
import app.mediabrainz.api.model.ReleaseGroup;
import app.mediabrainz.apihandler.StringMapper;
import app.mediabrainz.core.adapter.BasePagedListAdapter;
import app.mediabrainz.core.adapter.RetryCallback;

import static app.mediabrainz.MediaBrainzApp.api;
import static app.mediabrainz.MediaBrainzApp.oauth;
import static app.mediabrainz.api.coverart.CoverArtImage.Thumbnails.SMALL_SIZE;
import static app.mediabrainz.core.util.UiUtils.showToast;


public class ReleaseGroupsAdapter extends BasePagedListAdapter<ReleaseGroup> {

    public static class ReleaseGroupsViewHolder extends RecyclerView.ViewHolder {

        static final int VIEW_HOLDER_LAYOUT = R.layout.card_release_group;

        private ImageView releaseImageView;
        private ProgressBar progressView;
        private RatingBar userRatingView;
        private TextView ratingView;
        private TextView releaseNameView;
        private TextView releaseTypeYearView;
        private LinearLayout ratingContainerView;

        private ReleaseGroupsViewHolder(View v) {
            super(v);
            releaseImageView = itemView.findViewById(R.id.releaseImageView);
            progressView = itemView.findViewById(R.id.progressView);
            releaseNameView = itemView.findViewById(R.id.releaseNameView);
            releaseTypeYearView = itemView.findViewById(R.id.releaseTypeYearView);

            ratingContainerView = itemView.findViewById(R.id.ratingContainerView);
            userRatingView = itemView.findViewById(R.id.userRatingView);
            ratingView = itemView.findViewById(R.id.ratingView);
        }

        public static ReleaseGroupsViewHolder create(ViewGroup parent) {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            View view = layoutInflater.inflate(VIEW_HOLDER_LAYOUT, parent, false);
            return new ReleaseGroupsViewHolder(view);
        }

        private void bindTo(ReleaseGroup releaseGroup) {
            releaseNameView.setText(releaseGroup.getTitle());

            setAllRating(releaseGroup);
            setUserRating(releaseGroup);

            String year = releaseGroup.getFirstReleaseDate();
            year = !TextUtils.isEmpty(year) ? year.substring(0, 4) : "";

            String type = StringMapper.mapReleaseGroupTypeString(releaseGroup);
            releaseTypeYearView.setText(year + " (" + type + ")");

            if (MediaBrainzApp.getPreferences().isLoadImagesEnabled()) {
                loadImage(releaseGroup.getId());
            } else {
                releaseImageView.setVisibility(View.VISIBLE);
            }
            ratingContainerView.setOnClickListener(v -> showRatingBar(releaseGroup));
        }

        private void showRatingBar(ReleaseGroup releaseGroup) {
            if (oauth.hasAccount()) {
                AlertDialog alertDialog = new AlertDialog.Builder(itemView.getContext()).create();
                alertDialog.show();
                Window win = alertDialog.getWindow();
                if (win != null) {
                    win.setContentView(R.layout.dialog_rating_bar);
                    RatingBar rb = win.findViewById(R.id.ratingBar);
                    View progressView = win.findViewById(R.id.progressView);
                    rb.setRating(userRatingView.getRating());
                    TextView titleTextView = win.findViewById(R.id.titleTextView);
                    titleTextView.setText(itemView.getResources().getString(R.string.rate_entity, releaseGroup.getTitle()));

                    rb.setOnRatingBarChangeListener((RatingBar ratingBar, float rating, boolean fromUser) -> {
                        if (oauth.hasAccount() && progressView.getVisibility() == View.INVISIBLE && fromUser) {
                            progressView.setVisibility(View.VISIBLE);
                            rb.setAlpha(0.3F);
                            api.postAlbumRating(
                                    releaseGroup.getId(), rating,
                                    metadata -> {
                                        progressView.setVisibility(View.INVISIBLE);
                                        rb.setAlpha(1.0F);
                                        if (metadata.getMessage().getText().equals("OK")) {
                                            userRatingView.setRating(rating);
                                            api.getAlbumRatings(
                                                    releaseGroup.getId(),
                                                    this::setAllRating,
                                                    t -> showToast(itemView.getContext(), t.getMessage()));
                                        } else {
                                            showToast(itemView.getContext(), "Error");
                                        }
                                        alertDialog.dismiss();
                                    },
                                    t -> {
                                        progressView.setVisibility(View.INVISIBLE);
                                        rb.setAlpha(1.0F);
                                        showToast(itemView.getContext(), t.getMessage());
                                        alertDialog.dismiss();
                                    });
                        } else {
                            //todo: login
                            //ActivityFactory.startLoginActivity(itemView.getContext());
                        }
                    });
                }
            } else {
                //todo: login
                //ActivityFactory.startLoginActivity(itemView.getContext());
            }
        }

        private void loadImage(String albumMbid) {
            showImageProgressLoading(true);
            api.getReleaseGroupCoverArt(
                    albumMbid,
                    coverArt -> {
                        CoverArtImage.Thumbnails thumbnails = coverArt.getFrontThumbnails();
                        if (thumbnails != null && !TextUtils.isEmpty(thumbnails.getSmall())) {
                            Picasso.get().load(thumbnails.getSmall())
                                    .resize(SMALL_SIZE, SMALL_SIZE)
                                    .into(releaseImageView, new Callback() {
                                        @Override
                                        public void onSuccess() {
                                            showImageProgressLoading(false);
                                        }

                                        @Override
                                        public void onError(Exception e) {
                                            showImageProgressLoading(false);
                                        }
                                    });
                        } else {
                            showImageProgressLoading(false);
                        }
                    },
                    t -> showImageProgressLoading(false));
        }

        private void showImageProgressLoading(boolean show) {
            if (show) {
                releaseImageView.setVisibility(View.INVISIBLE);
                progressView.setVisibility(View.VISIBLE);
            } else {
                progressView.setVisibility(View.GONE);
                releaseImageView.setVisibility(View.VISIBLE);
            }
        }

        private void setAllRating(ReleaseGroup releaseGroup) {
            Rating rating = releaseGroup.getRating();
            if (rating != null) {
                Float r = rating.getValue();
                if (r != null) {
                    ratingView.setText(itemView.getContext().getString(R.string.rating_text, r, rating.getVotesCount()));
                } else {
                    ratingView.setText(itemView.getContext().getString(R.string.rating_text, 0.0, 0));
                }
            }
        }

        private void setUserRating(ReleaseGroup releaseGroup) {
            Rating rating = releaseGroup.getUserRating();
            if (rating != null) {
                Float r = rating.getValue();
                if (r == null) r = 0f;
                userRatingView.setRating(r);
            }
        }

    }

    public ReleaseGroupsAdapter(RetryCallback retryCallback) {
        super(DIFF_CALLBACK, retryCallback);
    }

    @Override
    public int getItemViewType(int position) {
        if (hasExtraRow() && position == getItemCount() - 1) {
            return NetworkStateViewHolder.VIEW_HOLDER_LAYOUT;
        } else {
            return ReleaseGroupsViewHolder.VIEW_HOLDER_LAYOUT;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case ReleaseGroupsViewHolder.VIEW_HOLDER_LAYOUT:
                return ReleaseGroupsViewHolder.create(parent);
            case NetworkStateViewHolder.VIEW_HOLDER_LAYOUT:
                return NetworkStateViewHolder.create(parent, retryCallback);
            default:
                throw new IllegalArgumentException("unknown view type");
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case ReleaseGroupsViewHolder.VIEW_HOLDER_LAYOUT:
                ReleaseGroup releaseGroup = getItem(position);
                ((ReleaseGroupsViewHolder) holder).bindTo(releaseGroup);
                if (holderClickListener != null) {
                    holder.itemView.setOnClickListener(view -> holderClickListener.onClick(releaseGroup));
                }
                break;
            case NetworkStateViewHolder.VIEW_HOLDER_LAYOUT:
                ((NetworkStateViewHolder) holder).bindTo(networkState);
                break;
        }
    }

    private static DiffUtil.ItemCallback<ReleaseGroup> DIFF_CALLBACK = new DiffUtil.ItemCallback<ReleaseGroup>() {
        @Override
        public boolean areItemsTheSame(@NonNull ReleaseGroup oldItem, @NonNull ReleaseGroup newItem) {
            return oldItem.getId().equals(newItem.getId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull ReleaseGroup oldItem, @NonNull ReleaseGroup newItem) {
            return Objects.equals(oldItem, newItem);
        }
    };

    public interface HolderClickListener {
        void onClick(ReleaseGroup releaseGroup);
    }

    private HolderClickListener holderClickListener;

    public void setHolderClickListener(HolderClickListener holderClickListener) {
        this.holderClickListener = holderClickListener;
    }

}
