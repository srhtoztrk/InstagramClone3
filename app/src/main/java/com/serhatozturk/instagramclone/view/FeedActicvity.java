package com.serhatozturk.instagramclone.view;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.serhatozturk.instagramclone.R;
import com.serhatozturk.instagramclone.adapter.PostAdapter;
import com.serhatozturk.instagramclone.databinding.ActivityFeedActicvityBinding;
import com.serhatozturk.instagramclone.model.post;

import java.util.ArrayList;
import java.util.Map;

public class FeedActicvity extends AppCompatActivity {

    private ActivityFeedActicvityBinding binding;
    private FirebaseAuth auth;
    private FirebaseFirestore firebaseFirestore;
    ArrayList<post> postArrayList;
    PostAdapter postAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFeedActicvityBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        auth=FirebaseAuth.getInstance();
        firebaseFirestore=FirebaseFirestore.getInstance();
        postArrayList =new ArrayList<>();
        getData();

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        postAdapter=new PostAdapter(postArrayList);
        binding.recyclerView.setAdapter(postAdapter);





    }


 private void getData(){


       // firebaseFirestore.collection("Post").whereEqualTo("usermail","nilzturk@gmail.com").addSnapshotListener(new EventListener<QuerySnapshot>() {
     firebaseFirestore.collection("Post").orderBy("date", Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {

     @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                if(error!= null){
                    Toast.makeText(FeedActicvity.this,error.getLocalizedMessage(),Toast.LENGTH_LONG).show();

                }
                if(value!=null){
                 for(DocumentSnapshot snapshot: value.getDocuments()){
                     Map<String, Object> data=snapshot.getData();

                   String userEmail= (String) data.get("usermail");
                   String comment= (String) data.get("comment");
                   String downloadUrl= (String) data.get("downloadurl");

                   post pst= new post(userEmail,comment,downloadUrl);
                   postArrayList.add(pst);

                 }

                 postAdapter.notifyDataSetChanged();  // değişikleri göstermek.

                }



            }
        });



 }



   @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.optionsmenu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {


        if (item.getItemId() == R.id.add_post) {
            Intent intentU=new Intent(FeedActicvity.this,UploadActivity.class);
            startActivity(intentU);


        } else if (item.getItemId() == R.id.sign_out) {

            auth.signOut();


            Intent intentToMain=new Intent(FeedActicvity.this,MainActivity.class);
            startActivity(intentToMain);
            finish();



        }

        return super.onOptionsItemSelected(item);
    }



}