package app.mediabrainz.core.presenter;


import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProviders;


public abstract class BaseFragmentPresenter {

    private Fragment fragment;

    public BaseFragmentPresenter(Fragment fragment) {
        this.fragment = fragment;
    }

    public Fragment getFragment() {
        return fragment;
    }

    protected <T extends ViewModel> T getFragmentViewModel(@NonNull Class<T> modelClass) {
        return ViewModelProviders.of(fragment).get(modelClass);
    }

    protected <T extends ViewModel> T getActivityViewModel(@NonNull Class<T> modelClass) {
        return ViewModelProviders.of(Objects.requireNonNull(fragment.getActivity())).get(modelClass);
    }

}
