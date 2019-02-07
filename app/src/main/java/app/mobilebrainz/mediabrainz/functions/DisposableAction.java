package app.mobilebrainz.mediabrainz.functions;

import io.reactivex.disposables.Disposable;


public interface DisposableAction {
    Disposable run();
}
