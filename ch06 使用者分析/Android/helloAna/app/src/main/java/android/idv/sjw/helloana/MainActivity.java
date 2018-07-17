package android.idv.sjw.helloana;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.Toast;

import com.google.firebase.analytics.FirebaseAnalytics;

public class MainActivity extends AppCompatActivity {

    Button button;
    Switch aSwitch;
    SeekBar seekBar;

    private FirebaseAnalytics mFirebaseAnalytics;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        button = (Button)findViewById(R.id.button2);
        aSwitch = (Switch)findViewById(R.id.switch1);
        seekBar = (SeekBar)findViewById(R.id.seekBar);


        //user Properties
        SharedPreferences properties = getSharedPreferences("properties",0);
        String likegamePropertie = properties.getString("likeGame","null");
        if (likegamePropertie.equals("null")){
            Intent intent = new Intent(this, AskLikeActivity.class);
            startActivity(intent);
        }


        //seekBar
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {}
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Bundle bundle = new Bundle();
                bundle.putString("value",seekBar.getProgress()+"");
                mFirebaseAnalytics.logEvent("seekBar",bundle);
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFirebaseAnalytics.logEvent("button",null);
            }
        });
        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mFirebaseAnalytics.logEvent("switch",null);
            }
        });
    }
}
