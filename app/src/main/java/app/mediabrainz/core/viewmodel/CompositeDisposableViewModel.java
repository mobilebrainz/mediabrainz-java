package app.mediabrainz.core.viewmodel;


import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;


public abstract class CompositeDisposableViewModel extends ViewModel {

    public final MutableLiveData<Boolean> progressld = new MutableLiveData<>();
    public final MutableLiveData<Boolean> noresultsld = new MutableLiveData<>();
    public final MutableLiveData<Boolean> errorld = new MutableLiveData<>();
    public final MutableLiveData<Throwable> throwableld = new MutableLiveData<>();

    protected final CompositeDisposable compositeDisposable = new CompositeDisposable();

    protected boolean dispose(@NonNull Disposable disposable) {
        return compositeDisposable.add(disposable);
    }

    protected void initLoading() {
        noresultsld.setValue(false);
        errorld.setValue(false);
        throwableld.setValue(null);
        progressld.setValue(true);
    }

    protected void setError(Throwable t) {
        progressld.setValue(false);
        errorld.setValue(true);
        throwableld.setValue(t);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.dispose();
    }

}
