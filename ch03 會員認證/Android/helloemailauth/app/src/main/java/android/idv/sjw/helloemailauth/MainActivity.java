package android.idv.sjw.helloemailauth;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {
    private FirebaseAnalytics mFirebaseAnalytics;
    private FirebaseAuth mAuth;

    EditText account,password;
    Button forgaetPassword,login,createNewAccount;
    TextView loginStatus;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        account = (EditText)findViewById(R.id.account);
        password = (EditText)findViewById(R.id.password);
        forgaetPassword = (Button)findViewById(R.id.forgetPassword);
        login = (Button)findViewById(R.id.loginButton);
        createNewAccount = (Button)findViewById(R.id.createAccount);
        loginStatus = (TextView) findViewById(R.id.loginStatus);


        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        mAuth = FirebaseAuth.getInstance();

        mAuth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (mAuth.getCurrentUser() != null){
                    Toast.makeText(MainActivity.this,"登入成功",Toast.LENGTH_LONG).show();
                    createNewAccount.setVisibility(View.INVISIBLE);
                    forgaetPassword.setVisibility(View.INVISIBLE);
                    account.setVisibility(View.INVISIBLE);
                    password.setVisibility(View.INVISIBLE);
                    login.setText("登出");
                    FirebaseUser user = mAuth.getCurrentUser();
                    loginStatus.setText("登入狀態:己登入\n\n帳號:\n"+ user.getEmail()
                            + "\n\n顯示名稱:" + user.getDisplayName()
                            + "\n是否己確認電子郵件" + user.isEmailVerified());
                    if(!user.isEmailVerified()){
                        user.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(MainActivity.this,
                                        "電子郵件確認信己發出，請檢查你的電子郵件",Toast.LENGTH_LONG).show();

                            }
                        });
                    }
                }else{
                    createNewAccount.setVisibility(View.VISIBLE);
                    forgaetPassword.setVisibility(View.VISIBLE);
                    account.setVisibility(View.VISIBLE);
                    password.setVisibility(View.VISIBLE);
                    login.setText("登入");
                    loginStatus.setText("登入狀態:未登入");
                }
            }
        });
    }



    public void login(View v){
        //若己登入就登出
        if (mAuth.getCurrentUser() != null){
            mAuth.signOut();
            return;
        }
        
        //取得帳密
        String inAccount = account.getText().toString();
        String inPassword = password.getText().toString();

        //檢查帳密正確性
        if( inAccount.equals("") || inAccount.isEmpty() ||
                inPassword.equals("") || inPassword.isEmpty()){
            Toast.makeText(MainActivity.this,"請輸入帳密",Toast.LENGTH_LONG).show();
            return;
        }

        //登入
        mAuth.signInWithEmailAndPassword(inAccount,inPassword)
            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){
                            //登入成功後要做的事
                    }else{
                        Toast.makeText(MainActivity.this,"登入失敗",Toast.LENGTH_LONG).show();
                    }
                }
            });
    }

    //忘記密碼
    public void forgetPassword(View v){
        String inAccount = account.getText().toString();
        //檢查是否有錯誤
        if( inAccount.equals("") || inAccount.isEmpty()){
            Toast.makeText(MainActivity.this,"請輸入帳號",Toast.LENGTH_LONG).show();
            return;
        }
        mAuth.sendPasswordResetEmail(inAccount)
            .addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(MainActivity.this,
                        "密碼重設郵件己發出，請檢查你的電子郵件",Toast.LENGTH_LONG).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this,
                    "錯誤:"+e.getLocalizedMessage(),Toast.LENGTH_LONG).show();
            }
        });

    }

    public void goCreateAccount(View v){
        Intent intent = new Intent();
        intent.setClass(this,CreateAccountActivity.class);
        startActivity(intent);
    }

}
