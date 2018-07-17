package idv.sjw.haychata;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.EventListener;
import java.util.TimeZone;

import static idv.sjw.haychata.R.layout.fourm_listitem;

public class ForumListActivity extends AppCompatActivity {

    DatabaseReference rootRef;
    ListView fourmList;
    MyCustomAdapter myCustomAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forum_list);

        fourmList = (ListView)findViewById(R.id.fourmList);
        rootRef = FirebaseDatabase.getInstance().getReference().child("forum");
        updateList();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateList();
    }

    private void updateList(){
        final ArrayList<FourmAdapterItem> listForumData = new ArrayList<FourmAdapterItem>();
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot childDataSnapshot: dataSnapshot.getChildren()){
                    String subject = childDataSnapshot.child("subject").getValue().toString();
                    String lastupdate = childDataSnapshot.child("lastUpdate").getValue().toString();
                    String lastUpdateUserNickname = childDataSnapshot.child("lastUpdateUserNickname").getValue().toString();
                    String key = childDataSnapshot.getKey();
                    listForumData.add(new FourmAdapterItem(subject,Long.parseLong(lastupdate),lastUpdateUserNickname,key));
                }
                myCustomAdapter = new MyCustomAdapter(listForumData);
                fourmList.setAdapter(myCustomAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        rootRef.child("subject").addListenerForSingleValueEvent(valueEventListener);
    }

    private  class  MyCustomAdapter extends BaseAdapter{

        public ArrayList<FourmAdapterItem> listForumDataAdapter;
        private LayoutInflater layoutInflater;

        public MyCustomAdapter(ArrayList<FourmAdapterItem> listForumDataAdapter){
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
            LayoutInflater mInflater = getLayoutInflater();
            View myView = mInflater.inflate(R.layout.fourm_listitem,null);
            final FourmAdapterItem s = listForumDataAdapter.get(position);
            TextView subject = (TextView)myView.findViewById(R.id.subject);
            subject.setText(s.subject);
            TextView date = (TextView)myView.findViewById(R.id.lastUpdate);

            Date lastUpdateddate = new Date(s.lastUpdateDate);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd HH:mm");
            simpleDateFormat.setTimeZone(TimeZone.getDefault());
            String dateString = simpleDateFormat.format(lastUpdateddate);

            date.setText(dateString);
            TextView lastSpeaker = (TextView)myView.findViewById(R.id.lastUpdateUserNickname);
            lastSpeaker.setText(s.lastUpdateUserNickname);
            myView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("lickID",s.key);
                    Intent intent = new Intent();
                    intent.setClass(ForumListActivity.this,DiscActivity.class);
                    intent.putExtra("discKey",s.key);
                    intent.putExtra("subject",s.subject);
                    startActivity(intent);
                }
            });
            return myView;
        }
    }
}
