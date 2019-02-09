package app.mediabrainz.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.Navigation;
import app.mediabrainz.R;
import app.mediabrainz.api.oauth.OAuthException;
import app.mediabrainz.core.fragment.BaseFragment;
import app.mediabrainz.core.util.UiUtils;
import app.mediabrainz.viewmodels.LoginVM;


public class LoginFragment extends BaseFragment {

    private final String CREATE_ACCOUNT_URI = "https://musicbrainz.org/register";
    private final String FORGOT_USERNAME_URI = "https://musicbrainz.org/lost-username";
    private final String FORGOT_PASSWORD_URI = "https://musicbrainz.org/lost-password";

    private EditText usernameView;
    private EditText passwordView;
    private View progressView;
    private View loginFormView;

    private LoginVM loginVM;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflate(R.layout.login_fragment, container);

        loginFormView = view.findViewById(R.id.loginFormView);
        progressView = view.findViewById(R.id.progressView);
        usernameView = view.findViewById(R.id.usernameView);
        passwordView = view.findViewById(R.id.passwordView);

        passwordView.setOnEditorActionListener((textView, id, keyEvent) -> {
            if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                attemptLogin();
                return true;
            }
            return false;
        });

        Button signInButton = view.findViewById(R.id.signInButton);
        signInButton.setOnClickListener(v -> attemptLogin());

        Button createAccountButton = view.findViewById(R.id.createAccountButton);
        createAccountButton.setOnClickListener(
                v -> startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(CREATE_ACCOUNT_URI))));

        Button forgotUsernameButton = view.findViewById(R.id.forgotUsernameButton);
        forgotUsernameButton.setOnClickListener(
                v -> startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(FORGOT_USERNAME_URI))));

        Button forgotPasswordButton = view.findViewById(R.id.forgotPasswordButton);
        forgotPasswordButton.setOnClickListener(
                v -> startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(FORGOT_PASSWORD_URI))));

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        loginVM = getViewModel(LoginVM.class);
        loginVM.authorized.observe(this, resource -> {
            if (resource == null) return;
            switch (resource.getStatus()) {
                case LOADING:
                    showProgress(true);
                    break;
                case ERROR:
                    showProgress(false);
                    Throwable t = resource.getThrowable();
                    if (t != null && t.equals(OAuthException.INVALID_AUTENTICATION_ERROR)) {
                        usernameView.setError(getString(R.string.error_invalid_username));
                        passwordView.setError(getString(R.string.error_invalid_password));
                    } else {
                        snackbarNotAction(loginFormView, R.string.login_error, Snackbar.LENGTH_LONG);
                    }
                    break;
                case SUCCESS:
                    showProgress(false);
                    //todo: navigate to startFragment ???
                    //snackbarNotAction(loginFormView, R.string.login_success, Snackbar.LENGTH_LONG);
                    Navigation.findNavController(loginFormView).navigate(R.id.action_loginFragment_to_startFragment);
                    break;
            }
        });
    }

    private void attemptLogin() {
        if (progressView.getVisibility() == View.VISIBLE) {
            return;
        }
        usernameView.setError(null);
        passwordView.setError(null);

        String username = usernameView.getText().toString().trim();
        String password = passwordView.getText().toString().trim();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(username)) {
            usernameView.setError(getString(R.string.error_field_required));
            focusView = usernameView;
            cancel = true;
        }
        if (TextUtils.isEmpty(password)) {
            passwordView.setError(getString(R.string.error_field_required));
            focusView = passwordView;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            if (getActivity() != null) {
                UiUtils.hideKeyboard(getActivity());
            }
            loginVM.authorize(username, password);
        }
    }

    private void showProgress(final boolean show) {
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        loginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        loginFormView.animate().setDuration(shortAnimTime).alpha(show ? 0 : 1)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        loginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                    }
                });

        progressView.setVisibility(show ? View.VISIBLE : View.GONE);
        progressView.animate().setDuration(shortAnimTime).alpha(show ? 1 : 0)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        progressView.setVisibility(show ? View.VISIBLE : View.GONE);
                    }
                });
    }

}
