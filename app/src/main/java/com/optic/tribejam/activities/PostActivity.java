package com.optic.tribejam.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.storage.UploadTask;
import com.optic.tribejam.R;
import com.optic.tribejam.models.Post;
import com.optic.tribejam.providers.AuthProvider;
import com.optic.tribejam.providers.ImageProvider;
import com.optic.tribejam.providers.PostProvider;
import com.optic.tribejam.utils.FileUtil;
import com.optic.tribejam.utils.ViewedMessageHelper;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;
import dmax.dialog.SpotsDialog;
//Activity de creacion de Post
public class PostActivity extends AppCompatActivity {

    ImageView mImageViewPost1;
    //Numero de identificación de Accion
    private final int GALLERY_REQUEST_CODE =1;
    private final int PHOTO_REQUEST_CODE =3;

    File mImageFile;
    File mImageFile2;
    Button mButtonPost;
    CircleImageView mCircleImageBack;
    ImageProvider mImageProvider;
    PostProvider mPostProvider;
    AuthProvider mAuthProvider;

    TextInputEditText mTextInputTitle;
    TextInputEditText mTextInputDescription;
    ImageView mImageViewRock;
    ImageView mImageViewJazz;
    ImageView mImageViewHiphop;
    ImageView mImageViewRap;
    TextView mTextViewCategory;
//Almacenara la selección del usuario
    String mCategory ="";
    String mTitle ="";
    String mDescription ="";

    AlertDialog mDialog;
    AlertDialog.Builder mBuilderSelector;
    CharSequence options[];

    //Foto
    String mAbsolutePhotePath;
    String mPhotoPath;
    File mPhotoFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        mImageViewPost1 = findViewById(R.id.imageViewPost1);
        mButtonPost = findViewById(R.id.btnPost);
        mImageProvider = new ImageProvider();
        mPostProvider = new PostProvider();
        mAuthProvider = new AuthProvider();

        //Loading
        mDialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Loading")
                .setCancelable(false)
                .build();

        //Selecion entre imagen de galeria o foto
        mBuilderSelector = new AlertDialog.Builder(this);
        mBuilderSelector.setTitle("Selecciona una opción");
        options = new CharSequence[]{"Imagen de Galeria","Sacar foto"};

        mTextInputTitle = findViewById(R.id.textInputAlbum);
        mTextInputDescription = findViewById(R.id.textInputDescription);
        mImageViewRock = findViewById(R.id.imageViewRock);
        mImageViewJazz = findViewById(R.id.imageViewJazz);
        mImageViewHiphop = findViewById(R.id.imageViewHiphop);
        mImageViewRap = findViewById(R.id.imageViewRap);
        mTextViewCategory = findViewById(R.id.textViewCategory);
        mCircleImageBack = findViewById(R.id.circularBackImage);

        //Boton hacia atras
        mCircleImageBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //Boton Postear
        mButtonPost.setOnClickListener(v -> clickPost());

        //Boton selcionar foto
        mImageViewPost1.setOnClickListener(v -> {
            selectOptionImage(GALLERY_REQUEST_CODE);

        });

        /*-----------Establecemos la categoria----------------------*/
        mImageViewRock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCategory="Rock";
                mTextViewCategory.setText(mCategory);
            }
        });

        mImageViewJazz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCategory="Jazz";
                mTextViewCategory.setText(mCategory);
            }
        });

        mImageViewHiphop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCategory="Hip Hop";
                mTextViewCategory.setText(mCategory);
            }
        });

        mImageViewRap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCategory="Rap";
                mTextViewCategory.setText(mCategory);
            }
        });

    }

//Dependiendo de la eleccon: tomar foto o elegir de galeria
    private void selectOptionImage( final int requestCode) {

        mBuilderSelector.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                if (i == 0){
                    openGallery(requestCode);
                }
                else if (i == 1){
                    takePhoto();
                }
            }
        });
        mBuilderSelector.show();
    }

    //Metodo de hacer foto con camara del dispositivo
    private void takePhoto() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //Guardamos la foto en File
        if (takePictureIntent.resolveActivity(getPackageManager()) != null){
            File photoFile = null;
            try {
                photoFile = createPhotoFile();
            }catch (Exception e){
                Toast.makeText(this, "Error en el archivo" + e.getMessage(), Toast.LENGTH_LONG).show();
            }

            if (photoFile != null){
                Uri photouri = FileProvider.getUriForFile(PostActivity.this,"com.optic.tribejam",photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photouri);
                startActivityForResult(takePictureIntent,PHOTO_REQUEST_CODE );
            }
        }
    }
//Metodo que crea el Archivo de la imagen
    private File createPhotoFile() throws IOException {
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File photoFile = File.createTempFile(
                new Date() +"_photo",
                ".jpg",
                storageDir
        );
        mPhotoPath = "file:" + photoFile.getAbsolutePath();
        mAbsolutePhotePath = photoFile.getAbsolutePath();
        return photoFile;
    }
//Metodo de accion del boton
    private void clickPost() {

        mTitle= mTextInputTitle.getText().toString();
        mDescription= mTextInputDescription.getText().toString();
        if (!mTitle.isEmpty() && !mDescription.isEmpty() && !mCategory.isEmpty()){
            if (mImageFile !=null){
                saveImage(mImageFile);
            }
            else if (mPhotoFile !=null){
                saveImage(mPhotoFile);
            }
            else{
                Toast.makeText(this, "Debe seleccionar una imagen de su galería", Toast.LENGTH_SHORT).show();
            }
        }
        else{
            Toast.makeText(this, "Porfavor complete todos los campos para poder realizar el post", Toast.LENGTH_SHORT).show();
        }
    }

    //Guardamos la imagen en Storage de Google
    private void saveImage(File imageFile1) {
        mDialog.show();
        mImageProvider.save(PostActivity.this,imageFile1).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                mImageProvider.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        //Obtenemos la url de la imagen en storage
                        String url = uri.toString();
                        //Generamos el Post
                        Post post = new Post();
                        //Establecemos los datos del post
                        post.setImage1(url);
                        post.setTitle(mTitle.toLowerCase());
                        post.setDescription(mDescription);
                        post.setCategory(mCategory);
                        post.setIdUser(mAuthProvider.getUid());
                        post.setTimestamp(new Date().getTime());
                        mPostProvider.save(post).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> taskSave) {
                                mDialog.dismiss();
                                if (taskSave.isSuccessful()){
                                    //PREPARAMOS EL NUEVO POST
                                    clearForm();
                                    Toast.makeText(PostActivity.this, "La imagen se almaceno correctamente", Toast.LENGTH_SHORT).show();
                                }
                                else{
                                    Toast.makeText(PostActivity.this, "Error al subir los datos del post", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull  Exception e) {
                mDialog.dismiss();
                Toast.makeText(PostActivity.this, "Error El guardado de imagen ha fallado", Toast.LENGTH_LONG).show();
            }
        });
    }
//Modulo que resetea el formulario del Post
    private void clearForm() {

        mTextInputTitle.setText("");
        mTextInputDescription.setText("");
        mTextViewCategory.setText("");
        mImageViewPost1.setImageResource(R.drawable.ic_upload);
        mTitle ="";
        mDescription ="";
        mCategory ="";
        mImageFile =null;

    }

    //Accedemos al la galleria de dispositivo
    private void openGallery(int requestCode) {
        //Establecemos una accion de apertura de galeria
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        //Establecemos la URL de la carpeta de fotos del dispositivo
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent,requestCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Seleccion de imagen desde galeria
        if (requestCode == GALLERY_REQUEST_CODE && requestCode != RESULT_OK){
            try {
                mPhotoFile = null;
                //Transformacion de uri
                mImageFile = FileUtil.from(this, data.getData());
                //Visualizamos la imagen de la galeria en el Image view 1 del Post.
                mImageViewPost1.setImageBitmap(BitmapFactory.decodeFile(mImageFile.getAbsolutePath()));
            }catch (Exception e){
                Log.d("ERROR","Se ha prodicido un error" + e.getMessage());
                Toast.makeText(this, "Se ha producido un error", Toast.LENGTH_LONG).show();
            }
        }

        //Selecion de foto
        if (requestCode == PHOTO_REQUEST_CODE && resultCode == RESULT_OK){
            mImageFile = null;
            mPhotoFile = new File(mAbsolutePhotePath);
            Picasso.with(PostActivity.this).load(mPhotoPath).into(mImageViewPost1);
        }
    }
    @Override
    protected void onStart() {
        super.onStart();
        ViewedMessageHelper.updateOnline(true, PostActivity.this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        ViewedMessageHelper.updateOnline(false, PostActivity.this);
    }
}