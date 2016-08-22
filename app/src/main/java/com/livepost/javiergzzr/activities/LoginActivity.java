package com.livepost.javiergzzr.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.livepost.javiergzzr.livepost.R;
import com.livepost.javiergzzr.objects.User;


/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity implements LoaderCallbacks<Cursor> {
    private Firebase mFirebaseRef;
    protected FirebaseError mFirebaseError;
    private static final String TAG = "LoginActivity";
    protected static boolean registrationCheck = false;
    protected static boolean authSuccessful = false;
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private EditText mPasswordRepeatView;
    private EditText mFirstNameView;
    private EditText mLastNameView;
    private View mProgressView;
    private View mLoginFormView;
    private View mLoginBackgroundView;
    private TextView mFeedbackBoxView;
    protected CheckBox mCheckBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFirebaseRef = new Firebase(getString(R.string.firebase_url));
        setContentView(R.layout.activity_login);
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        mCheckBox = (CheckBox) findViewById(R.id.register_checkbox);
        populateAutoComplete();
        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
        mLoginBackgroundView = findViewById(R.id.login_background);
        mFeedbackBoxView = (TextView) findViewById(R.id.feedback_box);
        mFirstNameView = (EditText) findViewById(R.id.first_name);
        mLastNameView = (EditText) findViewById(R.id.last_name);
        setFormListeners();
    }
    private void setFormListeners(){
        // Set up the login form.
        //Registration Checkbox
        registrationCheck = mCheckBox.isChecked();
        mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                registrationCheck = mCheckBox.isChecked();
                if (registrationCheck) {
                    mFirstNameView.setVisibility(View.VISIBLE);
                    mLastNameView.setVisibility(View.VISIBLE);
                    mPasswordRepeatView.setVisibility(View.VISIBLE);
                }
                else {
                    mFirstNameView.setVisibility(View.GONE);
                    mLastNameView.setVisibility(View.GONE);
                    mPasswordRepeatView.setVisibility(View.GONE);
                }
                mFirstNameView.invalidate();
                mLastNameView.invalidate();
                mPasswordRepeatView.invalidate();
            }
        });
        //Password Field
        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });
        //Repeat Password Field
        mPasswordRepeatView = (EditText) findViewById(R.id.password_confirm);
        mPasswordRepeatView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });
        //Sign In Button
        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });
        mLoginBackgroundView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
    private void populateAutoComplete() {
        getLoaderManager().initLoader(0, null, this);
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();
        String passwordRepeat = mPasswordRepeatView.getText().toString();
        String firstname = mFirstNameView.getText().toString();
        String lastname = mLastNameView.getText().toString();
        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }
        //Check for matching passwords if applicable
        if (registrationCheck && !TextUtils.isEmpty(passwordRepeat) && !isPasswordValid(passwordRepeat) && passwordRepeat != password) {
            mPasswordRepeatView.setError(getString(R.string.error_matching_password));
            focusView = mPasswordRepeatView;
            cancel = true;
        }
        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            mAuthTask = new UserLoginTask(firstname,lastname,email, password);
            mAuthTask.execute((Void) null);
        }
    }

    private boolean isEmailValid(String email) {
        if (email == null) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
        }
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 4;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        addEmailsToAutoComplete(emails);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }


    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(LoginActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        mEmailView.setAdapter(adapter);
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mEmail;
        private final String mPassword;
        private final String mFirstName;
        private final String mLastName;
        private  String mUid;
        SharedPreferences sharedPref = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        UserLoginTask(String firstName, String lastName, String email, String password) {
            mEmail = email;
            mPassword = password;
            mFirstName = firstName;
            mLastName = lastName;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            if (!registrationCheck) {
                try {
                    mFirebaseRef.authWithPassword(mEmail, mPassword, new Firebase.AuthResultHandler() {
                        @Override
                        public void onAuthenticated(AuthData authData) {
                            mFeedbackBoxView.setVisibility(View.VISIBLE);
                            mFeedbackBoxView.setText(getString(R.string.success_login));
                            authSuccessful = true;
                            mUid = authData.getUid();


                            editor.putString("username",mEmail);
                            editor.putString("uid",mUid);
                            //editor.putString("avatar",);
                            //TODO
                            editor.commit();
                            Log.i(TAG, "Login successful!");
                        }

                        @Override
                        public void onAuthenticationError(FirebaseError firebaseError) {
                            mFirebaseError = firebaseError;
                            mFeedbackBoxView.setVisibility(View.VISIBLE);
                            mFeedbackBoxView.setText(getString(R.string.error_service));
                            authSuccessful = false;
                            Log.i(TAG, "Login failed: " + mFirebaseError.getMessage());
                        }
                    });
                    Thread.sleep(2000);
                } catch (Exception e) {
                    return false;
                }
                return true;
            } else {
                try {
                    mFirebaseRef.createUser(mEmail, mPassword, new Firebase.ValueResultHandler<Map<String, Object>>() {
                        @Override
                        public void onSuccess(Map<String, Object> stringObjectMap) {
                            //If user creation was successful, store extra data object
                            mUid = stringObjectMap.get("uid").toString();
                            User u = new User(null, mFirstName, mLastName, mEmail, null, null);
                            mFirebaseRef.child("users").child(mUid).setValue(u);
                            //Write user data to shared preferences
                            editor.putString("username", mEmail);
                            editor.putString("uid", mUid);
                            authSuccessful = true;
                            //Visual feedback
                            mFeedbackBoxView.setVisibility(View.VISIBLE);
                            mFeedbackBoxView.setText(getString(R.string.success_registration));
                            Log.i(TAG, "User " + mEmail + " was registerd successfully!");
                        }

                        @Override
                        public void onError(FirebaseError firebaseError) {
                            mFirebaseError = firebaseError;
                            authSuccessful = false;
                            mFeedbackBoxView.setVisibility(View.VISIBLE);
                            switch (firebaseError.getCode()) {

                                case FirebaseError.EMAIL_TAKEN:
                                    mFeedbackBoxView.setText(getString(R.string.error_taken_email));
                                    break;
                                case FirebaseError.INVALID_EMAIL:
                                    mFeedbackBoxView.setText(getString(R.string.error_invalid_email));
                                    break;
                                default:
                                    mFeedbackBoxView.setText(mFirebaseError.getMessage());
                                    break;
                            }
                            Log.e(TAG, "Error registering User " + mEmail + " " + firebaseError.getMessage());
                        }
                    });
                    Thread.sleep(2000);
                } catch (Exception e) {
                    return false;
                }
                return authSuccessful;
            }
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);
            if (!registrationCheck) {
                if (success && authSuccessful) {
                    finish();
                } else {
                    mPasswordView.setError(getString(R.string.error_incorrect_password));
                    mPasswordView.requestFocus();
                }
            } else {
                if (authSuccessful) {
                    finish();
                } else {
                    mEmailView.setError(mFirebaseError.getMessage());
                }
            }

        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }
}

