package idv.sjw.haychata;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    private FirebaseAnalytics mFirebaseAnalytics;

    TextView message;
    EditText nickname;
    SharedPreferences nameSetting;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Obtain the FirebaseAnalytics instance.

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        //匿名登入
        FirebaseAuth.getInstance().signInAnonymously();

        message = (TextView)findViewById(R.id.msg);
        nickname = (EditText)findViewById(R.id.nickname);

        //若能取得之前用過的 nickname 就把它放入輸入欄
        nameSetting =getSharedPreferences("nameSetting",0);
        nickname.setText(nameSetting.getString("name",""));
    }

    public void enterForum(View v){

        //取得輸入的暱稱，並限制至少需兩個字元，否則給使用者錯誤訊息, 並暫停處理
        final String nicknameStrig = nickname.getText().toString();
        if (nicknameStrig.length() < 1) {
            Toast.makeText(this,"錯誤:暱稱至少兩字元",Toast.LENGTH_SHORT).show();
            message.setText("錯誤:暱稱至少兩字元");
            return;
        }

        // 檢查是否登入成功
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null){
            //進入下一個 Activity
            nameSetting.edit().putString("name",nicknameStrig).commit();
            Intent intent = new Intent();
            intent.setClass(MainActivity.this,ForumListActivity.class);
            startActivity(intent);


            DatabaseReference onlineRef = FirebaseDatabase
                    .getInstance()
                    .getReference("forum/online/"+FirebaseAuth.getInstance().getCurrentUser().getUid());
            onlineRef.setValue(nicknameStrig);
            onlineRef.onDisconnect().removeValue();



        }else{
            Toast.makeText(this,"錯誤:無法成功連線",Toast.LENGTH_SHORT).show();
            message.setText("錯誤:錯誤:無法成功連線");
            return;
        }
    }
}
