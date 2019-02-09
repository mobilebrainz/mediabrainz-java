package app.mediabrainz.core.viewmodel.event;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;


public class EventObserver<T> implements Observer<Event<T>> {

    private final Observer<T> observer;

    public EventObserver(@NonNull Observer<T> observer) {
        this.observer = observer;
    }

    @Override
    public void onChanged(@Nullable Event<T> event) {
        if (event != null) {
            T content = event.getContentIfNotHandled();
            if (content != null) {
                observer.onChanged(content);
            }
        }
    }
}
