package app.mediabrainz.fragment;

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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.Navigation;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import app.mediabrainz.R;
import app.mediabrainz.core.fragment.BaseFragment;
import app.mediabrainz.core.util.UiUtils;
import app.mediabrainz.viewmodel.LoginVM;


public class LoginFragment extends BaseFragment {

    private final String CREATE_ACCOUNT_URI = "https://musicbrainz.org/register";
    private final String FORGOT_USERNAME_URI = "https://musicbrainz.org/lost-username";
    private final String FORGOT_PASSWORD_URI = "https://musicbrainz.org/lost-password";

    private boolean isLoading;
    private LoginVM loginVM;

    private EditText usernameView;
    private EditText passwordView;
    private View loginFormView;
    protected SwipeRefreshLayout swipeRefreshLayout;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflate(R.layout.login_fragment, container);

        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        loginFormView = view.findViewById(R.id.loginFormView);
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

        swipeRefreshLayout.setEnabled(false);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        loginVM = getViewModel(LoginVM.class);
        loginVM.authEvent.observe(this, aBoolean -> {
            if (aBoolean) {
                Navigation.findNavController(loginFormView).navigate(R.id.action_loginFragment_to_startFragment);
            }
        });
        loginVM.progressld.observe(this, aBoolean -> {
            isLoading = aBoolean;
            swipeRefreshLayout.setRefreshing(aBoolean);
        });
        loginVM.errorld.observe(this, aBoolean -> {
            if (aBoolean) {
                snackbarWithAction(swipeRefreshLayout, R.string.connection_error, R.string.connection_error_retry,
                        v -> attemptLogin());
            } else if (getErrorSnackbar() != null && getErrorSnackbar().isShown()) {
                getErrorSnackbar().dismiss();
            }
        });
        loginVM.throwableld.observe(this, throwable -> {
            if (throwable != null) {
                usernameView.setError(getString(R.string.error_invalid_username));
                passwordView.setError(getString(R.string.error_invalid_password));
            }
        });

    }

    private void attemptLogin() {
        if (isLoading) {
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

}
