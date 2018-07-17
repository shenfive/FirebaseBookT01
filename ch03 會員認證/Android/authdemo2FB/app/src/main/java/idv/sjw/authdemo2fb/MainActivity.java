package idv.sjw.authdemo2fb;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {


    LoginButton loginButton;
    TextView message;

    CallbackManager callbackManager;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);

        message =(TextView)findViewById(R.id.message);  //訊息欄
        loginButton = (LoginButton)findViewById(R.id.login_button);
        loginButton.setReadPermissions("email", "public_profile"); //設定權限

        callbackManager = CallbackManager.Factory.create();
        mAuth = FirebaseAuth.getInstance();

        // 登入回傳資訊處理
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                //成功登入 Facebook 時的處理
                AccessToken token = loginResult.getAccessToken();
                AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
                mAuth.signInWithCredential(credential)
                        .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull com.google.android.gms.tasks.Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // 成功登入顯示使用者訊
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    message.setText(user.getDisplayName());
                                } else {
                                    // 登入失敗顯示
                                    Toast.makeText(MainActivity.this, "登入失",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
            @Override
            public void onCancel() {
                // App code 取消登入時的處理
            }
            @Override
            public void onError(FacebookException exception) {
                // App code 登入錯誤時的處理
            }
        });

        // Facebook 登出監聽
        Profile fbProfile = Profile.getCurrentProfile();
        AccessTokenTracker accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken,
                                                       AccessToken currentAccessToken) {
                if (currentAccessToken == null) {
                    mAuth.signOut();
                    message.setText("請登入");
                    //deleteContact();//This is my code
                }
            }
        };

    }

    // Instent 回傳資訊處理
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

}
