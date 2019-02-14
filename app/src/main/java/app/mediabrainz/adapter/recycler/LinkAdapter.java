package app.mediabrainz.adapter.recycler;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import androidx.annotation.NonNull;
import app.mediabrainz.R;
import app.mediabrainz.api.model.Url;


public class LinkAdapter extends BaseRecyclerViewAdapter<LinkAdapter.LinkViewHolder> {

    private List<Url> urls;

    public static class LinkViewHolder extends BaseRecyclerViewAdapter.BaseViewHolder {

        static final int VIEW_HOLDER_LAYOUT = R.layout.card_link;

        private ImageView iconView;
        private TextView typeView;
        private TextView linkView;

        public static LinkViewHolder create(ViewGroup parent) {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            View view = layoutInflater.inflate(VIEW_HOLDER_LAYOUT, parent, false);
            return new LinkViewHolder(view);
        }

        private LinkViewHolder(View v) {
            super(v);
            iconView = v.findViewById(R.id.iconView);
            typeView = v.findViewById(R.id.typeView);
            linkView = v.findViewById(R.id.linkView);
        }

        public void bindTo(Url url) {
            typeView.setText(url.getPrettyType());
            linkView.setText(url.getPrettyUrl());

            String t = url.getType().toLowerCase();
            int iconId = R.drawable.ic_link_24_dark;
            switch (t) {
                case "youtube":
                    iconId = R.drawable.ic_youtube_24_light;
                    break;
                case "official homepage":
                    iconId = R.drawable.ic_home_24_dark;
                    break;
                case "imdb":
                    iconId = R.drawable.ic_film_24;
                    break;
                case "fanpage":
                    iconId = R.drawable.ic_community_24_dark;
                    break;
                case "online community":
                    iconId = R.drawable.ic_community_24_dark;
                    break;
                case "wikipedia":
                    iconId = R.drawable.ic_wikipedia_24;
                    break;
                case "lyrics":
                    iconId = R.drawable.ic_lyrics_24;
                    break;
                case "download for free":
                    iconId = R.drawable.ic_download_24;
                    break;
                case "soundcloud":
                    iconId = R.drawable.ic_soundcloud_24;
                    break;
            }
            String r = url.getResource();
            if (t.startsWith("streaming")) {
                iconId = R.drawable.ic_streaming_24;
            } else if (t.startsWith("purchase")) {
                iconId = R.drawable.ic_basket_24;
            } else if (r.contains("twitter")) {
                iconId = R.drawable.ic_twitter_24;
            } else if (r.contains("facebook")) {
                iconId = R.drawable.ic_facebook_24;
            } else if (t.contains("discog")) {
                iconId = R.drawable.ic_album_24_dark;
            } else if (r.contains("vimeo")) {
                iconId = R.drawable.ic_vimeo_24;
            }
            iconView.setImageResource(iconId);
        }
    }

    public LinkAdapter(List<Url> urls) {
        this.urls = urls;
    }

    @Override
    public void onBind(LinkViewHolder holder, final int position) {
        holder.bindTo(urls.get(position));
    }

    @Override
    public int getItemCount() {
        return urls.size();
    }

    @NonNull
    @Override
    public LinkViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return LinkViewHolder.create(parent);
    }
}
