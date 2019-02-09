package app.mediabrainz.core.viewmodel.event;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


public class Event<T> {

    private final T content;
    private boolean hasBeenHandled;
    private boolean consumed;

    public Event(@NonNull T content) {
        this.content = content;
        this.consumed = true;
    }

    public Event(@NonNull T content, boolean consumed) {
        this.content = content;
        this.consumed = consumed;
    }

    public boolean isHasBeenHandled() {
        return hasBeenHandled;
    }

    @Nullable
    public T getContentIfNotHandled() {
        if (consumed) {
            if (hasBeenHandled) {
                return null;
            } else {
                hasBeenHandled = true;
                return content;
            }
        } else {
            return content;
        }
    }

    @NonNull
    public T peekContent() {
        return content;
    }
}
