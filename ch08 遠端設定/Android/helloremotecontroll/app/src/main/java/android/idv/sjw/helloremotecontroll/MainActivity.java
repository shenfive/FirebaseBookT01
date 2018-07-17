package android.idv.sjw.helloremotecontroll;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    TextView helloworld;

    private FirebaseAnalytics mFirebaseAnalytics;
    private FirebaseRemoteConfig mFirebaseRemoteConfig;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        helloworld = (TextView)findViewById(R.id.helloworld);

        // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        //取得遠端設定實體
        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();

        //設定為測試模式，產品實作不可以加入這一段
        mFirebaseRemoteConfig.setConfigSettings(
                new FirebaseRemoteConfigSettings
                        .Builder()
                        .setDeveloperModeEnabled(BuildConfig.DEBUG)
                        .build());

        //設定預設值
        HashMap<String, Object> defaults = new HashMap<>();
        defaults.put("titleBackgroudColor","#ffffff");
        mFirebaseRemoteConfig.setDefaults(defaults);

        //取得更新並回傳 Task
        final Task<Void> fetch = mFirebaseRemoteConfig.fetch(0);

        //加入回傳監聽處理
        fetch.addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                mFirebaseRemoteConfig.activateFetched();
                String colorString = mFirebaseRemoteConfig.getString("titleBackgroudColor");
                Log.d("color",colorString+"///");
                helloworld.setBackgroundColor(Color.parseColor(colorString));
            }
        });
    }
}
