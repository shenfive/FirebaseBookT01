package android.idv.sjw.hellophonenumbersignin;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseException;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    String verifiID;
    EditText phoneNumber,verifyCode;
    TextView message;
    private FirebaseAnalytics mFirebaseAnalytics;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        phoneNumber = (EditText)findViewById(R.id.phoneNumber);
        verifyCode = (EditText)findViewById(R.id.verifyCode);
        message = (TextView)findViewById(R.id.messageTexe);

        // Firebase
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        mAuth = FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser() != null){
            message.setText("登入成功,門號:"+mAuth.getCurrentUser().getPhoneNumber());
        }
    }

    public void requestCheckCode(View v){
        PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                Log.d("onVerificationCompleted",phoneAuthCredential.toString());
            }
            @Override
            public void onVerificationFailed(FirebaseException e) {
                Log.d("error",e.getLocalizedMessage());
            }
            @Override
            public void onCodeSent(String verificationId,
                                   PhoneAuthProvider.ForceResendingToken token) {
                Toast.makeText(MainActivity.this,"請注意收取簡訊",Toast.LENGTH_LONG).show();
                verifiID = verificationId;
            }
        };
        String phone = phoneNumber.getText().toString();
        PhoneAuthProvider
                .getInstance()
                .verifyPhoneNumber(phone,60, TimeUnit.SECONDS,this,mCallbacks);
    }

    public void singWithSMSCode(View v){
        String code = verifyCode.getText().toString();
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verifiID, code);
        mAuth.signInWithCredential(credential)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                String phoneNum = authResult.getUser().getPhoneNumber();
                String uid = authResult.getUser().getUid();
                Toast.makeText(MainActivity.this,"登入成功,門號:"+phoneNum+"\n"+uid,Toast.LENGTH_LONG).show();
                message.setText("登入成功,門號:"+phoneNum);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this,e.getLocalizedMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }
    public  void  singOut(View v){
        mAuth.signOut();
        message.setText("請登入");
    }
}
