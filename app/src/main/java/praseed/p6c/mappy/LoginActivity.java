package praseed.p6c.mappy;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener{
    private static final int RC_SIGN_IN = 9001;
    private FirebaseAuth mAuth ;
    private FirebaseUser mFbUser;
    private ProgressDialog mProgressDialog;
    private ProgressBar progressBar;
    GoogleApiClient mGoogleApiClient;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private String userName , userEmail;
    DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        // Build a GoogleApiClient with access to the Google Sign-In API and the
        // options specified by gso.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this , this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                mFbUser = mAuth.getCurrentUser();
                if (mFbUser != null) {
                    String refreshedToken = FirebaseInstanceId.getInstance().getToken();
                    mRootRef.child(Constants.tokkenRef).child(mFbUser.getUid()).setValue(refreshedToken);
                    String photoUri = "null";
                    if(mFbUser.getPhotoUrl() != null){photoUri = mFbUser.getPhotoUrl().toString();}
                    // User is signed in
                    UserObj newUser = new UserObj(mFbUser.getDisplayName(), photoUri, mFbUser.getUid(), mFbUser.getEmail());
                    mRootRef.child(Constants.userRef).child(mFbUser.getUid()).setValue(newUser);
                    startMainACtivity();
                    finish();

                }
                else {
                }
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
        mFbUser = mAuth.getCurrentUser();
        if(mFbUser == null){
            signin();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mAuthListener != null){
            //Log.d(TAG, "onStop: removed");
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    private void signin() {
        progressBar.setVisibility(View.VISIBLE);
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            progressBar.setVisibility(View.GONE);
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        if(result.isSuccess()){

            GoogleSignInAccount acct = result.getSignInAccount();
            //Log.d(TAG, "LoginA LoginSucess: "+ acct);
            AuthWithGoogle(acct);
        }else {
            Toast.makeText(LoginActivity.this, R.string.no_internet_message, Toast.LENGTH_SHORT).show();
            startMainACtivity();
            //Log.d(TAG, "LoginA LoginFailed: ");
        }
    }

    private void AuthWithGoogle(GoogleSignInAccount acct) {
        //Log.d(TAG, "LoginA AuthWithGoogle: " + " ID:" + acct.getId());
        showProgressDialog();
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                //Log.d(TAG, "LoginA Auth onComplete: ");
                if (!task.isSuccessful()) {
                    Toast.makeText(LoginActivity.this, R.string.no_internet_message, Toast.LENGTH_SHORT).show();
                    //startMainACtivity();
                    //Log.d(TAG, "LoginA Auth onComplete: Failed ");
                } else {

                    //Log.d(TAG, "LoginA onComplete: ");

                }
                hideProgressDialog();
            }
        });
    }

    public void showProgressDialog() {
        if (mProgressDialog == null) {
            //Log.d(TAG, "LoginA showProgressDialog: ");
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage("Signing In...");
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        //Log.d(TAG, "LoginA hideProgressDialog: ");
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    private void startMainACtivity() {
        Intent intent = new Intent(LoginActivity.this,MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
