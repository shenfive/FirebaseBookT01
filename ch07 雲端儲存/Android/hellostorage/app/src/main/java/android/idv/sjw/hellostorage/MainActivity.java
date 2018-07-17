package android.idv.sjw.hellostorage;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.ContentResolver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telecom.Call;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private FirebaseAnalytics mFirebaseAnalytics;
    private FirebaseAuth mFirebaseAuth;
    private DatabaseReference mPicDataReference;
    private FirebaseStorage mFirebaseStorage;

    ProgressBar progressBar;
    Button button;
    ImageView imageView;
    GridView gridView;
    ArrayList<PictureItem> pictureItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //初始化
        button = (Button)findViewById(R.id.button2);
        imageView = (ImageView)findViewById(R.id.imageView);
        gridView = (GridView)findViewById(R.id.gridView);
        progressBar = (ProgressBar)findViewById(R.id.progressBar2);
        pictureItems = new ArrayList<>();
        progressBar.setVisibility(View.INVISIBLE);
        mPicDataReference = FirebaseDatabase.getInstance().getReference().child("pic");
        mFirebaseStorage = FirebaseStorage.getInstance();
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        // 匿名登入 Firebase
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseAuth.signInAnonymously();

        mPicDataReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String path = dataSnapshot.child("path").getValue().toString();
                PictureItem pictureItem = new PictureItem(path);
                pictureItems.add(pictureItem);
                MyPictureListAdapter myPictureListAdapter = new MyPictureListAdapter();
                myPictureListAdapter.pictureItems = pictureItems;
                gridView.setAdapter(myPictureListAdapter);
            }
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) { }
            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) { }
            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) { }
            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });

        //擷取照片按鈕監聽器
        button.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");//開啟Pictures畫面Type設定為image
                intent.setAction(Intent.ACTION_GET_CONTENT);//使用Intent.ACTION_GET_CONTENT這個Action
                startActivityForResult(intent, 0);//取得照片後返回此畫面
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        //當使用者按下確定後
        if (resultCode == RESULT_OK) {

            Uri uri = data.getData();//取得圖檔的路徑位置
            ContentResolver cr = this.getContentResolver();//抽象資料的接口
            try {
                //由抽象資料接口轉換圖檔路徑為Bitmap
                final Bitmap bitmap = BitmapFactory.decodeStream(cr.openInputStream(uri));
                //使用 UUID 為上傳檔名
                String fileName=UUID.randomUUID().toString() +".jpg";
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
                imageView.setImageBitmap(bitmap);
                byte[] imageData = baos.toByteArray();
                StorageMetadata storageMetadata = new StorageMetadata
                        .Builder()
                        .setCustomMetadata("MyKey","MyValue")
                        .build();
                final StorageReference mountainsRef = mFirebaseStorage
                        .getReference()
                        .child("pic")
                        .child(mFirebaseAuth.getCurrentUser().getUid())
                        .child(fileName);
                UploadTask uploadTask = mountainsRef.putBytes(imageData,storageMetadata);
                progressBar.setVisibility(View.VISIBLE);

                uploadTask.addOnFailureListener(new OnFailureListener() {
                    //監聽上傳成功訊息
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Toast.makeText(MainActivity.this,"上傳失敗!",Toast.LENGTH_LONG).show();
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    //監聽上傳失敗訊息
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Toast.makeText(MainActivity.this,"上傳成功!",Toast.LENGTH_LONG).show();
                        HashMap picData = new HashMap();
                        picData.put("uid",mFirebaseAuth.getCurrentUser().getUid());
                        picData.put("link",taskSnapshot.getDownloadUrl().getPath());
                        picData.put("path",mountainsRef.getPath());
                        mPicDataReference.push().setValue(picData);
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    //監聽上傳過程訊息
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        int progressPersentage = (int)((taskSnapshot.getBytesTransferred()*100)/(taskSnapshot.getTotalByteCount()));
                        Log.d("uploading",progressPersentage+"%");
                        progressBar.setProgress(progressPersentage);
                    }
                });

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }



    //Gird View 用的 Adapter

    private class MyPictureListAdapter extends BaseAdapter{
        public ArrayList<PictureItem> pictureItems;
        @Override
        public int getCount() {
            return pictureItems.size();
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
            View myView = mInflater.inflate(R.layout.image_grid,null);
            final PictureItem pictureItem = pictureItems.get(position);
            ImageView imageView = (ImageView)myView.findViewById(R.id.imageView2);
            //使用 FirebaseUI 下載圖片
            Glide.with(MainActivity.this)
                    .using(new FirebaseImageLoader())
                    .load(mFirebaseStorage.getReference().child(pictureItem.imagePath))
                    .into(imageView);
            return myView;
        }
    }
}
