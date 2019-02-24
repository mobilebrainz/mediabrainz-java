package app.mediabrainz.core.ui;


import android.graphics.drawable.Drawable;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import androidx.annotation.Nullable;
import app.mediabrainz.core.functions.Action;


public class RequestListenerCallback implements RequestListener<Drawable> {

    private Action progressAction;
    private Action errorAction;

    public RequestListenerCallback(Action progressAction) {
        this.progressAction = progressAction;
    }

    public RequestListenerCallback(Action progressAction, Action errorAction) {
        this.progressAction = progressAction;
        this.errorAction = errorAction;
    }

    @Override
    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
        progressAction.run();
        return false;
    }

    @Override
    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
        progressAction.run();
        if (errorAction != null) {
            errorAction.run();
        }
        return false;
    }
}
