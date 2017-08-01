package naveen.com.googlesignin;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.*;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {

    private static final int REQ_CODE = 9001;

    private LinearLayout prof_section;
    private Button signout;
    private SignInButton signin;
    private TextView profileName, ProfileEmail;
    private ImageView prof_pic;
    private GoogleApiClient googleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        prof_section = (LinearLayout) findViewById(R.id.LinearLayout_profileDetails);
        signin = (SignInButton) findViewById(R.id.bn_login);
        signout = (Button) findViewById(R.id.bn_logout);
        profileName = (TextView) findViewById(R.id.DisplayName);
        ProfileEmail = (TextView) findViewById(R.id.Email_ID);
        prof_pic = (ImageView) findViewById(R.id.imageView_profilePic);

        signin.setOnClickListener(this);
        signout.setOnClickListener(this);

        prof_section.setVisibility(View.GONE);


        GoogleSignInOptions signInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        googleApiClient = new GoogleApiClient.Builder(this).enableAutoManage(this, this).addApi(Auth.GOOGLE_SIGN_IN_API, signInOptions).build();


    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.bn_login:

                signIn();
                break;

            case R.id.bn_logout:
                signOut();

                break;

        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private void signIn() {

        Intent intent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        startActivityForResult(intent, REQ_CODE);
    }

    private void signOut() {

        Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(@NonNull Status status) {
                updateUI(false);
            }
        });

    }

    private void handleResult(GoogleSignInResult result) {

        if (result.isSuccess()) {

            GoogleSignInAccount account = result.getSignInAccount();
            String Name = account.getDisplayName();
            String email = account.getEmail();
            String img_url = account.getPhotoUrl().toString();

            profileName.setText(Name);
            ProfileEmail.setText(email);
            Glide.with(this)
                    .load(img_url)
                    .into(prof_pic);
            updateUI(true);
        } else {
            updateUI(false);
        }

    }

    private void updateUI(boolean isLogin) {

        if (isLogin) {

            prof_section.setVisibility(View.VISIBLE);
            signin.setVisibility(View.GONE);
        } else {
            prof_section.setVisibility(View.GONE);
            signin.setVisibility(View.VISIBLE);

        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQ_CODE) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleResult(result);
        }

    }
}
