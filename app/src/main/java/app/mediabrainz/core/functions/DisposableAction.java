package app.mediabrainz.core.functions;

import io.reactivex.disposables.Disposable;


public interface DisposableAction {
    Disposable run();
}
