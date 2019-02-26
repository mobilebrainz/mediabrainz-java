package app.mediabrainz.fragment;


import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.os.ConfigurationCompat;
import app.mediabrainz.R;
import app.mediabrainz.api.model.Artist;
import app.mediabrainz.api.model.RelationExtractor;
import app.mediabrainz.api.model.Url;
import app.mediabrainz.viewmodel.WikiVM;


public class ArtistWikiFragment extends BaseArtistFragment {

    public static final String TAG = "ArtistWikiF";

    private WikiVM wikiVM;
    private String lang;
    private String buttonLang;
    private Map<String, String> urlMap;

    private WebView webView;
    private Button langButton;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflate(R.layout.web_view_fragment, container);

        webView = layout.findViewById(R.id.webView);
        swipeRefreshLayout = layout.findViewById(R.id.swipeRefreshLayout);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                view.loadUrl(request.getUrl().toString());
                return true;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                swipeRefreshLayout.setRefreshing(true);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                swipeRefreshLayout.setRefreshing(false);
                if (buttonLang != null) {
                    langButton.setText(buttonLang.equals("en") ? lang : "en");
                    langButton.setVisibility(View.VISIBLE);
                } else {
                    langButton.setVisibility(View.GONE);
                }
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                showInfoSnackbar(R.string.wiki_error);
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        webView.setOnKeyListener((v, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_BACK
                    && event.getAction() == MotionEvent.ACTION_UP
                    && webView.canGoBack()) {
                webView.goBack();
                return true;
            }
            return false;
        });

        lang = ConfigurationCompat.getLocales(getResources().getConfiguration()).get(0).getLanguage();
        langButton = layout.findViewById(R.id.langButton);
        langButton.setOnClickListener(v -> {
            if (urlMap != null) {
                buttonLang = buttonLang.equals("en") ? lang : "en";
                webView.loadUrl(urlMap.get(buttonLang));
            }
        });
        return layout;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        wikiVM = getViewModel(WikiVM.class);
        wikiVM.noresultsld.observe(this, aBoolean -> {
            if (aBoolean) showInfoSnackbar(R.string.no_results);
        });
        wikiVM.progressld.observe(this, aBoolean -> swipeRefreshLayout.setRefreshing(aBoolean));
        wikiVM.urlMap.observe(this, map -> {
            urlMap = map;
            String url = null;
            if (urlMap.containsKey(lang)) {
                if (buttonLang == null) {
                    buttonLang = lang;
                }
                url = urlMap.get(lang);
            } else if (urlMap.containsKey("en")) {
                url = urlMap.get("en");
            }
            if (url != null) {
                webView.loadUrl(url);
            }
        });

        wikiVM.errorld.observe(this, aBoolean -> {
            isError = aBoolean;
            if (aBoolean) {
                showErrorSnackbar(R.string.connection_error, R.string.connection_error_retry, v -> artistVM.refreshArtist());
            } else {
                dismissErrorSnackbar();
            }
        });
    }

    @Override
    protected void show(Artist artist) {
        boolean isEmpty = true;
        List<Url> urls = new RelationExtractor(artist).getUrls();
        if (urls != null && !urls.isEmpty()) {
            for (Url link : urls) {
                String resource = link.getResource();
                if (link.getType().equalsIgnoreCase("wikidata")) {
                    int pageSplit = resource.lastIndexOf("/") + 1;
                    String wikidataQ = resource.substring(pageSplit);
                    if (!TextUtils.isEmpty(wikidataQ)) {
                        wikiVM.getUrlMap(wikidataQ, lang);
                        isEmpty = false;
                    }
                    break;
                }
            }
        }
        if (isEmpty) {
            showInfoSnackbar(R.string.no_results);
        }
    }

}
