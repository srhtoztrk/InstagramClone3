package com.serhatozturk.instagramclone.view;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.serhatozturk.instagramclone.R;
import com.serhatozturk.instagramclone.databinding.ActivityUploadBinding;

import java.util.HashMap;
import java.util.UUID;

public class UploadActivity extends AppCompatActivity {

    Bitmap selectedImage;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    Uri imageData;

    private FirebaseAuth auth;
    private FirebaseFirestore firebaseFirestore;

    private ActivityUploadBinding binding;

    ActivityResultLauncher<Intent> activityResultLauncher;
    ActivityResultLauncher<String> permissionLauncher;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUploadBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        registerLauncher();


        firebaseStorage=FirebaseStorage.getInstance();
        auth=FirebaseAuth.getInstance();
        firebaseFirestore=FirebaseFirestore.getInstance();
        storageReference=firebaseStorage.getReference();




    }

    public void uploadBtnOnclick(View view){
        if (imageData != null) {

            UUID uuid=UUID.randomUUID();
            String imageName="images/" +uuid+ ".jpg";


            storageReference.child(imageName).putFile(imageData).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    //download url

                    StorageReference newReferance=firebaseStorage.getReference(imageName);
                    newReferance.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {

                            String downloadUrl=uri.toString();

                            String comment=binding.commentText.getText().toString();

                            FirebaseUser user=auth.getCurrentUser();
                            String userEmail= user.getEmail();

                            HashMap<String, Object> postaData=new HashMap<>(); // anahtar string, değerler object olsun.
                            postaData.put("usermail", userEmail);
                            postaData.put("downloadurl",downloadUrl);
                            postaData.put("comment",comment);
                            postaData.put("date", FieldValue.serverTimestamp());

                            firebaseFirestore.collection("Post").add(postaData).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    Intent intent=new Intent(UploadActivity.this,FeedActicvity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(UploadActivity.this,e.getLocalizedMessage(),Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                   Toast.makeText(UploadActivity.this,e.getLocalizedMessage(),Toast.LENGTH_LONG).show();
                }
            });
        }

        //--------------------bu kısmı ben yazdım hata olabilir alt taraffff-------------------------------------
        else {



            String comment = binding.commentText.getText().toString();


            FirebaseUser user = auth.getCurrentUser();
            String userEmail = user.getEmail();

            HashMap<String, Object> postaData = new HashMap<>(); // anahtar string, değerler object olsun.
            postaData.put("usermail", userEmail);

            postaData.put("comment", comment);
            postaData.put("date", FieldValue.serverTimestamp());


            firebaseFirestore.collection("Post").add(postaData).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {

                    Intent intent = new Intent(UploadActivity.this, FeedActicvity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    Toast.makeText(UploadActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();

                }
            });













        }
        //--------------------bu kısmı ben yazdım hata olabilir üst taraffff-------------------------------------


        }

    public void selectImage(View view){

        //izin varmı kontrol

        if(ContextCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            //izin neden isteniyor

            if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)){

                Snackbar.make(view,"Galeri İçin İzin Lazım",Snackbar.LENGTH_INDEFINITE).setAction("İzin Ver", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //izin istenecek
                        permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);


                    }
                }).show();

            }  else {
                //izin istenecek
                permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);

            }


        } else {

            Intent intentToGallery=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            activityResultLauncher.launch(intentToGallery);


        }


    }

    private void registerLauncher(){
        activityResultLauncher=registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {

                if(result.getResultCode()== Activity.RESULT_OK){
                    Intent intentFromResult=result.getData();

                    if(intentFromResult != null){

                       imageData=  intentFromResult.getData();



                       try{
                           if(Build.VERSION.SDK_INT >= 28){
                               ImageDecoder.Source source=ImageDecoder.createSource(UploadActivity.this.getContentResolver(),imageData);
                               selectedImage = ImageDecoder.decodeBitmap(source);
                               binding.imageView2.setImageBitmap(selectedImage);
                           }
                           else{

                               selectedImage=MediaStore.Images.Media.getBitmap(UploadActivity.this.getContentResolver(), imageData);
                               binding.imageView2.setImageBitmap(selectedImage);
                           }
                       }
                       catch(Exception e){
                           e.printStackTrace();
                       }

                    }

                }

            }
        });

        permissionLauncher=registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
            @Override
            public void onActivityResult(Boolean result) {

                if(result){
                    Intent intentToGallery=new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    activityResultLauncher.launch(intentToGallery);


                }
                else{
                    Toast.makeText(UploadActivity.this,"izin gerekli",Toast.LENGTH_LONG).show();
                }

            }
        });

    }
}