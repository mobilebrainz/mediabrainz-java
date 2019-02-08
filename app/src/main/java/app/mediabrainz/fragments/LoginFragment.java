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
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import app.mediabrainz.R;
import app.mediabrainz.api.oauth.OAuthException;
import app.mediabrainz.util.UiUtils;
import app.mediabrainz.viewmodels.LoginVM;

import static app.mediabrainz.MediaBrainzApp.oauth;


public class LoginFragment extends Fragment {

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
        View view = inflater.inflate(R.layout.login_fragment, container, false);

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
        loginVM = ViewModelProviders.of(this).get(LoginVM.class);

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
            showProgress(true);

            // todo: to viewModel
            oauth.authorize(
                    username, password,
                    () -> {
                        showProgress(false);
                        //todo: navigate to ???
                        Snackbar.make(loginFormView, getText(R.string.login_success), Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    },
                    t -> {
                        showProgress(false);
                        if (t.equals(OAuthException.INVALID_AUTENTICATION_ERROR)) {
                            usernameView.setError(getString(R.string.error_invalid_username));
                            passwordView.setError(getString(R.string.error_invalid_password));
                        } else {
                            Snackbar.make(loginFormView, getText(R.string.login_error), Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                        }
                    });
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
