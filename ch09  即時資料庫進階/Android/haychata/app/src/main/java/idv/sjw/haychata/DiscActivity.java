package idv.sjw.haychata;

import android.content.SearchRecentSuggestionsProvider;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;

public class DiscActivity extends AppCompatActivity {

    TextView subject,nickname;
    ListView disclist;
    EditText message;
    DatabaseReference discDBRef,mRef,subjectRef;

    ArrayList<DiscContent>  discContents;
    DiscCustomAdapter discCustomAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_disc);

        subject = (TextView)findViewById(R.id.subject);
        disclist = (ListView)findViewById(R.id.disclist);
        nickname = (TextView)findViewById(R.id.nickname);
        message = (EditText)findViewById(R.id.message);

        Bundle bundle = getIntent().getExtras();

        discContents = new ArrayList<DiscContent>();


        //設定標題
        subject.setText(bundle.getString("subject"));

        //設定名稱
        SharedPreferences nameSetting =getSharedPreferences("nameSetting",0);
        nickname.setText(nameSetting.getString("name",""));

        mRef = FirebaseDatabase.getInstance().getReference().child("forum");
        String key = bundle.getString("discKey");
        Log.d("key",key);
        discDBRef = mRef.child("disc").child(key);
        subjectRef = mRef.child("subject").child(key);
                ChildEventListener childEventListener = new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        String content = dataSnapshot.child("content").getValue().toString();
                        String nickname = dataSnapshot.child("nickname").getValue().toString();
                        String timestamp = dataSnapshot.child("timestamp").getValue().toString();
                        Long date = Long.parseLong(timestamp);
                        discContents.add(new DiscContent(content,nickname,date,dataSnapshot.getKey()));
                        Collections.sort(discContents,new TimeCompartor());
                        discCustomAdapter = new DiscCustomAdapter(discContents);
                        disclist.setAdapter(discCustomAdapter);
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                    }
                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {
                    }
                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                };
                Query query = discDBRef.orderByChild("timestamp").limitToLast(5);
                query.addChildEventListener(childEventListener);


    }
    static class TimeCompartor implements Comparator {
        public int compare(Object object1, Object object2) {// 实现接口中的方法
            DiscContent p1 = (DiscContent) object1; // 强制转换
            DiscContent p2 = (DiscContent) object2;
            return p2.date.compareTo(p1.date);
        }
    }

    // 新增留
    public void newMessage(View v){
        String megText = message.getText().toString();

        if (!(megText.equals(""))){

            message.setText("");
            DatabaseReference newMsgRef = discDBRef.push();
            HashMap msg = new HashMap();
            msg.put("content",megText);
            msg.put("nickname",nickname.getText().toString());
            msg.put("timestamp",ServerValue.TIMESTAMP);
            newMsgRef.setValue(msg);
            subjectRef.child("lastUpdateUserNickname").setValue(nickname.getText().toString());
            subjectRef.child("lastUpdate").setValue(ServerValue.TIMESTAMP);
        }
    }

    private  class  DiscCustomAdapter extends BaseAdapter {

        public ArrayList<DiscContent> listForumDataAdapter;
        private LayoutInflater layoutInflater;

        public DiscCustomAdapter(ArrayList<DiscContent> listForumDataAdapter){
            this.listForumDataAdapter = listForumDataAdapter;
        }

        @Override
        public int getCount() {
            return listForumDataAdapter.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final DiscContent s = listForumDataAdapter.get(position);
            LayoutInflater mInflater = getLayoutInflater();
            View myView = mInflater.inflate(R.layout.disc_content,null);
            ((TextView)myView.findViewById(R.id.disccontent)).setText(s.content);
            ((TextView)myView.findViewById(R.id.nickname)).setText(s.nickname);
            Date date = new Date(s.date);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd HH:mm");
            simpleDateFormat.setTimeZone(TimeZone.getDefault());
            String dateString = simpleDateFormat.format(date);
            ((TextView)myView.findViewById(R.id.disctime)).setText(dateString);
            return myView;
        }
    }
}
