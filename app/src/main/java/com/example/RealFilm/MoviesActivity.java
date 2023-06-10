package com.example.RealFilm;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

public class MoviesActivity extends AppCompatActivity {

    private TextView textView_top;
    private GridLayout show_layout;
    private ProgressBar progressBar;
    private DatabaseReference mDatabase;
    private ImageButton btn_back;
    private Map singleUser;
    private String idMovies;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movies);

        textView_top = findViewById(R.id.textView_top);
        show_layout = findViewById(R.id.show_layout);
        progressBar = findViewById(R.id.progressBar);
        btn_back = findViewById(R.id.btn_back);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        get();
        btnBackOnClick();
    }



    private void get(){
        Intent intent = getIntent();
        String str = intent.getStringExtra("id");
        switch (str){
            case "Phim đề cử":
                browserData(" ", "name");
                break;
            case "Phim hành động":
                browserData("Hành động", "category");
                break;
            case "Phim mới":
                DateFormat dateFormat = new SimpleDateFormat("yyyy");
                Date date = new Date();
                String year = dateFormat.format(date);
                browserData(year, "year");
                break;
            case "Phim chiến tranh":
                browserData("Chiến tranh", "category");
                break;
            case "Phim tâm lý":
                browserData("Tâm lý", "category");
                break;
            case "Phim viễn tưởng":
                browserData("Viễn tưởng", "category");
                break;
            case "Phim tình cảm":
                browserData("Tình cảm", "category");
                break;
            case "Phim hoạt hình":
                browserData("Hoạt hình", "category");
                break;
            case "Phim hài hước":
                browserData("Hài hước", "category");
                break;
            case "Phim Việt Nam":
                browserData("Việt Nam", "nation");
                break;
            case "Phim Thái Lan":
                browserData("Thái Lan", "nation");
                break;
            case "Phim Hàn Quốc":
                browserData("Hàn Quốc", "nation");
                break;
            case "Phim Mỹ":
                browserData("Mỹ", "nation");
                break;
            case "Phim Hồng Kông":
                browserData("Hồng Kông", "nation");
                break;
            case "Phim Âu":
                browserData("Âu", "nation");
                break;
            case "Danh sách phim yêu thích":
                getFavourite();
                break;
            case "Danh sách phim đã bình luận":
                getComments();
                break;
            case "Danh sách phim đã đánh giá":
                getStars();
                break;
        }
        textView_top.setText(str);
    }
    private void getStars(){
        Intent intent = getIntent();
        String userID = intent.getStringExtra("userID");
        mDatabase.child("Users").child(userID).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()){
                    if (task.getResult().exists()){
                        DataSnapshot dataSnapshot = task.getResult();
                        String nameMovies = String.valueOf(dataSnapshot.child("stars").child("nameMovies").getValue());
                        System.out.println("nameMovies: " + nameMovies);
                        String[] b = nameMovies.split(",");
                        for (String item : b) {
                            browserData(item, "name");
                        }
                    }
                }
            }
        });
    }
    private void getComments(){
        Intent intent = getIntent();
        String userID = intent.getStringExtra("userID");
        mDatabase.child("Users").child(userID).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()){
                    if (task.getResult().exists()){
                        DataSnapshot dataSnapshot = task.getResult();
                        String favourite = String.valueOf(dataSnapshot.child("comments").getValue());
                        String[] b = favourite.split(",");
                        for (String item : b) {
                            browserData(item, "name");
                        }

                    }
                }
            }
        });
    }

    private void getFavourite(){
        Intent intent = getIntent();
        String userID = intent.getStringExtra("userID");
        mDatabase.child("Users").child(userID).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()){
                    if (task.getResult().exists()){
                        DataSnapshot dataSnapshot = task.getResult();
                        String favourite = String.valueOf(dataSnapshot.child("favourite").getValue());
                        String[] b = favourite.split(",");
                        for (String item : b) {
                            browserData(item, "name");
                        }

                    }
                }
            }
        });
    }

    private void btnBackOnClick(){
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void browserData(String query, String folder){
        progressBar.setVisibility(View.VISIBLE);
        mDatabase.child("Movies").addListenerForSingleValueEvent( new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()){
                    collectData((Map<String,Object>) dataSnapshot.getValue(), query, folder);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

    }
    private void collectData(Map<String,Object> listresult, String text, String folder) {

        ArrayList<String> listAfter = new ArrayList<String>();

        for (Map.Entry<String, Object> entry : listresult.entrySet()){

            singleUser = (Map) entry.getValue();
            String value = singleUser.get(folder).toString();

            if (value.toLowerCase().contains(text.toLowerCase())) {
                idMovies = singleUser.get("name").toString();
                listAfter.add(idMovies);

            }
        }

        Object[] arr = listAfter.toArray();
        if (arr.length > 0){
            for (int i = 0; i < arr.length; i ++){
                getInfor(arr[i].toString());
            }
        } else {
            progressBar.setVisibility(View.INVISIBLE);
            LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, dpToPx(45));
            params2.setMargins(0, dpToPx(10), 0, 0);
            TextView textView = new TextView(this);
            textView.setLayoutParams(params2);
            textView.setText("Không tìm thấy kết quả!");
            textView.setTextSize(18);
            show_layout.addView(textView);
        }
    }
    private void getInfor(String IDMOVIES){
        mDatabase.child("Movies").child(IDMOVIES).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()){
                    if (task.getResult().exists()){

                        DataSnapshot dataSnapshot = task.getResult();
                        String nameMovies = String.valueOf(dataSnapshot.child("name").getValue());

                        FirebaseStorage storage = FirebaseStorage.getInstance();
                        StorageReference listRef = storage.getReference().child("Images").child(nameMovies);
                        listRef.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
                            @Override
                            public void onSuccess(ListResult listResult) {
                                for(StorageReference file:listResult.getItems()){
                                    file.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            StorageReference storageReference = storage.getReferenceFromUrl(String.valueOf(uri));
                                            String link = storageReference.getName();
                                            if (link.equals(nameMovies + "_3x4")){
                                                String poster_link= "";
                                                poster_link = uri.toString();
                                                setLayoutSearch(nameMovies, poster_link);
                                            }
                                        }
                                    }).addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {

                                        }
                                    });
                                }
                            }
                        });
                    }
                }
            }
        });
    }
    private void setLayoutSearch(String text, String poster_link){
        LinearLayout parent = new LinearLayout(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(dpToPx(10), dpToPx(10), dpToPx(10), 0);
        parent.setLayoutParams(params);
        parent.setOrientation(LinearLayout.VERTICAL);
        parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MoviesActivity.this ,MoviesInformationActivity.class);
                intent.putExtra("id",  text);
                startActivity(intent);
            }
        });

        CardView cardView1 = new CardView(this);
        cardView1.setLayoutParams(new CardView.LayoutParams(dpToPx(162), dpToPx(217)));
        cardView1.setRadius(dpToPx(15));
        cardView1.setCardBackgroundColor(getResources().getColor(R.color.white));

        CardView.LayoutParams params3 = new CardView.LayoutParams(dpToPx(157), dpToPx(212), Gravity.CENTER);
        CardView cardView2 = new CardView(this);
        cardView2.setLayoutParams(params3);
        cardView2.setRadius(dpToPx(15));

        ImageView imageView = new ImageView(this);
        Glide.with(getApplicationContext()).load(poster_link).into(imageView);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        imageView.setBackgroundResource(R.drawable.null_image34);

        LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, dpToPx(45));
        params2.setMargins(0, dpToPx(10), 0, 0);
        TextView textView = new TextView(this);
        textView.setLayoutParams(params2);
        textView.setText(text);
        textView.setTextSize(18);
        textView.setMaxLines(2);
        textView.setEllipsize(TextUtils.TruncateAt.END);
        textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        textView.setTextColor(getResources().getColor(R.color.white));

        show_layout.addView(parent);
        parent.addView(cardView1);
        cardView1.addView(cardView2);
        cardView2.addView(imageView);
        parent.addView(textView);

        progressBar.setVisibility(View.INVISIBLE);
    }
    public static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }
}