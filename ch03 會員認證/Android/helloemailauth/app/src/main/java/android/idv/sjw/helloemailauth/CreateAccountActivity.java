package android.idv.sjw.helloemailauth;

import android.content.Context;
import android.os.Bundle;
import android.app.Activity;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;

public class CreateAccountActivity extends Activity {

    EditText newAccount,displayName,newPassword,newPasswordC;

    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);
        newAccount = (EditText)findViewById(R.id.newAccount);
        newPassword = (EditText)findViewById(R.id.newPassword);
        newPasswordC = (EditText)findViewById(R.id.newPasswordConfirm);
        displayName = (EditText)findViewById(R.id.displayName);
        mAuth = FirebaseAuth.getInstance();
    }

    public void createAccount(View v){
        String account = newAccount.getText().toString();
        String password = newPassword.getText().toString();
        String passwordC = newPasswordC.getText().toString();
        if(account.isEmpty() || account.equals("")){
            Toast.makeText(CreateAccountActivity.this,"請輸入帳號",Toast.LENGTH_LONG).show();
            return;
        }
        if(password.equals(passwordC) == false){
            Toast.makeText(CreateAccountActivity.this,"兩次密碼不同，請確認密碼",Toast.LENGTH_LONG).show();
            return;
        }

        mAuth.createUserWithEmailAndPassword(account,password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                Toast.makeText(CreateAccountActivity.this
                        ,"己成功建立帳號",Toast.LENGTH_LONG).show();

                //設定 DisplayName
                String displayNameString = displayName.getText().toString();
                UserProfileChangeRequest userProfileChangeRequest =
                        new UserProfileChangeRequest.Builder().setDisplayName(displayNameString).build();
                mAuth.getCurrentUser().updateProfile(userProfileChangeRequest);
                CreateAccountActivity.this.finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(CreateAccountActivity.this
                        ,"無法建立帳號:"+e.getLocalizedMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }
}
