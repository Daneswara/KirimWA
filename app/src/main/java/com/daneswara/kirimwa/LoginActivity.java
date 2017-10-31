package com.daneswara.kirimwa;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
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
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.daneswara.kirimwa.object.Device;
import com.daneswara.kirimwa.object.User;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity implements LoaderCallbacks<Cursor>, GoogleApiClient.OnConnectionFailedListener {

    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;
    private static final String TAG = "Login Service";
    SweetAlertDialog prosses_login;
    private FirebaseFirestore db;

    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */
    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "foo@example.com:hello", "bar@example.com:world"
    };
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
//    private UserLoginTask mAuthTask = null;
    private FirebaseAuth mAuth;


    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private View mSignUpFormView;

    private static final int RC_SIGN_IN = 9001;
    private GoogleApiClient mGoogleApiClient;

//    String id_device;
    int count_device = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            Intent keluar = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(keluar);
            finish();
        } else {
            setContentView(R.layout.activity_login);
            // Set up the login form.
            mEmailView = findViewById(R.id.email);
            populateAutoComplete();

//            TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
//            id_device = telephonyManager.getDeviceId();
//            if (id_device == null) {
//                Toast.makeText(LoginActivity.this, "ID Device is empty, you can't login", Toast.LENGTH_SHORT).show();
//            }
            mPasswordView = findViewById(R.id.password);
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

            Button mEmailSignInButton = findViewById(R.id.email_sign_in_button);
            mEmailSignInButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    attemptLogin();
                }
            });
            // Configure Google Sign In
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build();
            Button mGoogle = findViewById(R.id.sign_in_google);
            mGoogle.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                    startActivityForResult(signInIntent, RC_SIGN_IN);
                }
            });

            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .enableAutoManage(this, this)
                    .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                    .build();


            mLoginFormView = findViewById(R.id.login_form);
            mSignUpFormView = findViewById(R.id.signup);
            mProgressView = findViewById(R.id.login_progress);
            mSignUpFormView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent regis = new Intent(LoginActivity.this, RegisterActivity.class);
                    startActivity(regis);
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {
                // Google Sign In failed, update UI appropriately
                // ...
            }
        }

    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        prosses_login = new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.PROGRESS_TYPE);
        prosses_login.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        prosses_login.setTitleText("Loading");
        prosses_login.setCancelable(false);
        prosses_login.show();

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            prosses_login.dismissWithAnimation();
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            db.collection("users").document(mAuth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    final String token = FirebaseInstanceId.getInstance().getToken();
                                    if (!task.getResult().exists()) {
                                        User newuser = new User(mAuth.getCurrentUser().getEmail(), 1);
                                        db.collection("users").document(mAuth.getCurrentUser().getUid()).set(newuser).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                e.printStackTrace();
                                            }
                                        });
                                        Map<String, Object> message = new HashMap<>();
                                        message.put("campaign", true);
                                        message.put("uid", mAuth.getCurrentUser().getUid());
                                        db.collection("device").document(token).set(message).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                e.printStackTrace();
                                            }
                                        });
                                        FirebaseMessaging.getInstance().subscribeToTopic("news");
                                        Auth.GoogleSignInApi.signOut(mGoogleApiClient);
                                        Intent masuk = new Intent(LoginActivity.this, MainActivity.class);
                                        startActivity(masuk);
                                        finish();
                                    } else {
                                        final User cekuser = task.getResult().toObject(User.class);
                                        db.collection("device").whereEqualTo("uid", mAuth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                boolean cek = true;
                                                for (DocumentSnapshot document : task.getResult()) {
                                                    if(document.getId().equals(token)){
                                                    //Device dev = document.toObject(Device.class);
                                                    //if (dev.id_device.equals(id_device)) {
                                                        cek = false;
//                                                        db.collection("users").document(mAuth.getCurrentUser().getUid()).collection("device").document(id_device).set(new Device(token, id_device)).addOnFailureListener(new OnFailureListener() {
//                                                            @Override
//                                                            public void onFailure(@NonNull Exception e) {
//                                                                e.printStackTrace();
//                                                            }
//                                                        });
                                                        FirebaseMessaging.getInstance().subscribeToTopic("news");
                                                        Auth.GoogleSignInApi.signOut(mGoogleApiClient);
                                                        Intent masuk = new Intent(LoginActivity.this, MainActivity.class);
                                                        startActivity(masuk);
                                                        finish();
                                                    }
                                                }
                                                if (cek) {
                                                    if (task.getResult().size() < cekuser.tipe) {
                                                        new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.WARNING_TYPE)
                                                                .setTitleText("Attention!")
                                                                .setContentText("Do you want to add this device to your account?")
                                                                .setConfirmText("Yes, please!")
                                                                .showCancelButton(true)
                                                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                                    @Override
                                                                    public void onClick(SweetAlertDialog sDialog) {
                                                                        Map<String, Object> message = new HashMap<>();
                                                                        message.put("campaign", true);
                                                                        message.put("uid", mAuth.getCurrentUser().getUid());
                                                                        db.collection("device").document(token).set(message).addOnFailureListener(new OnFailureListener() {
                                                                            @Override
                                                                            public void onFailure(@NonNull Exception e) {
                                                                                e.printStackTrace();
                                                                            }
                                                                        });
                                                                        sDialog.dismissWithAnimation();
                                                                        FirebaseMessaging.getInstance().subscribeToTopic("news");
                                                                        Auth.GoogleSignInApi.signOut(mGoogleApiClient);
                                                                        Intent masuk = new Intent(LoginActivity.this, MainActivity.class);
                                                                        startActivity(masuk);
                                                                        finish();
                                                                    }
                                                                })
                                                                .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                                    @Override
                                                                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                                                                        mAuth.signOut();
                                                                        Auth.GoogleSignInApi.signOut(mGoogleApiClient);
                                                                        sweetAlertDialog.dismissWithAnimation();
                                                                    }
                                                                })
                                                                .show();
                                                    } else {
                                                        mAuth.signOut();
                                                        Auth.GoogleSignInApi.signOut(mGoogleApiClient);
                                                        new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.ERROR_TYPE)
                                                                .setTitleText("Please upgrade your account!")
                                                                .setContentText("You only can login with this account for " + cekuser.tipe + " device")
                                                                .show();
                                                    }
                                                }
                                            }
                                        });
                                    }
                                }
                            });
                        } else {
                            // If sign in fails, display a message to the user.
                            prosses_login.dismissWithAnimation();
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });
    }

    private void populateAutoComplete() {
        if (!mayRequestContacts()) {
            return;
        }

        getLoaderManager().initLoader(0, null, this);
    }

    private boolean mayRequestContacts() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
            Snackbar.make(mEmailView, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
                        }
                    });
        } else {
            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
        }
        return false;
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                populateAutoComplete();
            }
        }
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
//        if (mAuthTask != null) {
//            return;
//        }

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password)) {
            mPasswordView.setError(getString(R.string.error_field_required));
            focusView = mPasswordView;
            cancel = true;
        } else if (!isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
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
            //showProgress(true);
            prosses_login = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
            prosses_login.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
            prosses_login.setTitleText("Loading");
            prosses_login.setCancelable(false);
            prosses_login.show();
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                db.collection("users").document(mAuth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        final String token = FirebaseInstanceId.getInstance().getToken();
                                        if (!task.getResult().exists()) {
                                            User newuser = new User(mAuth.getCurrentUser().getEmail(), 1);
                                            db.collection("users").document(mAuth.getCurrentUser().getUid()).set(newuser).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    e.printStackTrace();
                                                }
                                            });
                                            Map<String, Object> message = new HashMap<>();
                                            message.put("campaign", true);
                                            message.put("uid", mAuth.getCurrentUser().getUid());
                                            db.collection("device").document(token).set(message).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    e.printStackTrace();
                                                }
                                            });
                                            FirebaseMessaging.getInstance().subscribeToTopic("news");
                                            Auth.GoogleSignInApi.signOut(mGoogleApiClient);
                                            Intent masuk = new Intent(LoginActivity.this, MainActivity.class);
                                            startActivity(masuk);
                                            finish();
                                        } else {
                                            final User cekuser = task.getResult().toObject(User.class);
                                            db.collection("device").whereEqualTo("uid", mAuth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                    boolean cek = true;
                                                    for (DocumentSnapshot document : task.getResult()) {
                                                        if(document.getId().equals(token)){
                                                            //Device dev = document.toObject(Device.class);
                                                            //if (dev.id_device.equals(id_device)) {
                                                            cek = false;
//                                                        db.collection("users").document(mAuth.getCurrentUser().getUid()).collection("device").document(id_device).set(new Device(token, id_device)).addOnFailureListener(new OnFailureListener() {
//                                                            @Override
//                                                            public void onFailure(@NonNull Exception e) {
//                                                                e.printStackTrace();
//                                                            }
//                                                        });
                                                            FirebaseMessaging.getInstance().subscribeToTopic("news");
                                                            Auth.GoogleSignInApi.signOut(mGoogleApiClient);
                                                            Intent masuk = new Intent(LoginActivity.this, MainActivity.class);
                                                            startActivity(masuk);
                                                            finish();
                                                        }
                                                    }
                                                    if (cek) {
                                                        if (task.getResult().size() < cekuser.tipe) {
                                                            new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.WARNING_TYPE)
                                                                    .setTitleText("Attention!")
                                                                    .setContentText("Do you want to add this device to your account?")
                                                                    .setConfirmText("Yes, please!")
                                                                    .showCancelButton(true)
                                                                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                                        @Override
                                                                        public void onClick(SweetAlertDialog sDialog) {
                                                                            Map<String, Object> message = new HashMap<>();
                                                                            message.put("campaign", true);
                                                                            message.put("uid", mAuth.getCurrentUser().getUid());
                                                                            db.collection("device").document(token).set(message).addOnFailureListener(new OnFailureListener() {
                                                                                @Override
                                                                                public void onFailure(@NonNull Exception e) {
                                                                                    e.printStackTrace();
                                                                                }
                                                                            });
                                                                            sDialog.dismissWithAnimation();
                                                                            FirebaseMessaging.getInstance().subscribeToTopic("news");
                                                                            Auth.GoogleSignInApi.signOut(mGoogleApiClient);
                                                                            Intent masuk = new Intent(LoginActivity.this, MainActivity.class);
                                                                            startActivity(masuk);
                                                                            finish();
                                                                        }
                                                                    })
                                                                    .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                                        @Override
                                                                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                                                                            mAuth.signOut();
                                                                            Auth.GoogleSignInApi.signOut(mGoogleApiClient);
                                                                            sweetAlertDialog.dismissWithAnimation();
                                                                        }
                                                                    })
                                                                    .show();
                                                        } else {
                                                            mAuth.signOut();
                                                            Auth.GoogleSignInApi.signOut(mGoogleApiClient);
                                                            new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.ERROR_TYPE)
                                                                    .setTitleText("Please upgrade your account!")
                                                                    .setContentText("You only can login with this account for " + cekuser.tipe + " device")
                                                                    .show();
                                                        }
                                                    }
                                                }
                                            });
                                        }
                                    }
                                });
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "signInWithEmail:failure", task.getException());
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    prosses_login.dismissWithAnimation();
                    if (e instanceof FirebaseAuthInvalidUserException) {
                        Toast.makeText(LoginActivity.this, "This User Not Found , Create A New Account", Toast.LENGTH_SHORT).show();
                        mEmailView.setError(getString(R.string.error_incorrect_email));
                        mEmailView.requestFocus();
                    }
                    if (e instanceof FirebaseAuthInvalidCredentialsException) {
                        Toast.makeText(LoginActivity.this, "The Password Is Invalid, Please Try Valid Password", Toast.LENGTH_SHORT).show();
                        mPasswordView.setError(getString(R.string.error_incorrect_password));
                        mPasswordView.requestFocus();
                    }
                    if (e instanceof FirebaseNetworkException) {
                        mEmailView.requestFocus();
                        Toast.makeText(LoginActivity.this, "Please Check Your Connection", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 5;
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
            mSignUpFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                    mSignUpFormView.setVisibility(show ? View.GONE : View.VISIBLE);
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
            mSignUpFormView.setVisibility(show ? View.GONE : View.VISIBLE);
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

    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(LoginActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        mEmailView.setAdapter(adapter);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
        Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
    }


    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }

}

