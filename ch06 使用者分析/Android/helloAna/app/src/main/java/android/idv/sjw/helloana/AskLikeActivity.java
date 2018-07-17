package android.idv.sjw.helloana;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.analytics.FirebaseAnalytics;

public class AskLikeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ask_like);
    }
    public void onLike(View v){
        SharedPreferences properties = getSharedPreferences("properties",0);
        properties.edit().putString("likeGame","true").commit();
        FirebaseAnalytics.getInstance(this).setUserProperty("likeGame","true");
        finish();
    }
    public void onNotLike(View v){
        SharedPreferences properties = getSharedPreferences("properties",0);
        properties.edit().putString("likeGame","false").commit();
        FirebaseAnalytics.getInstance(this).setUserProperty("likeGame","false");
        finish();
    }
}
