package com.optic.tribejam.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.optic.tribejam.R;
import com.optic.tribejam.adapters.CommentAdapter;
import com.optic.tribejam.adapters.SliderAdapter;
import com.optic.tribejam.models.Comment;
import com.optic.tribejam.models.FCMBody;
import com.optic.tribejam.models.FCMResponse;
import com.optic.tribejam.models.SliderItem;
import com.optic.tribejam.providers.AuthProvider;
import com.optic.tribejam.providers.CommentsProvider;
import com.optic.tribejam.providers.LikesProvider;
import com.optic.tribejam.providers.NotificationProvider;
import com.optic.tribejam.providers.PostProvider;
import com.optic.tribejam.providers.TokenProvider;
import com.optic.tribejam.providers.UserProvider;
import com.optic.tribejam.utils.RelativeTime;
import com.optic.tribejam.utils.ViewedMessageHelper;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

//Activity de Post individual completo
public class PostDetailActivity extends AppCompatActivity {

    SliderView mSliderView;
    SliderAdapter mSliderAdapter;
    List<SliderItem> mSliderItems = new ArrayList<>();

    PostProvider mPostProvider;
    UserProvider mUserProvider;
    CommentsProvider mCommentsProvider;
    AuthProvider mAuthProvider;
    CommentAdapter mCommentAdapter;
    LikesProvider mLikesProvider;
    NotificationProvider mNotificationProvider;
    TokenProvider mTokenProvider;

    //Almacena el id del post
    String mExtraPostId;

    //Variables del xml
    TextView mTextViewTitle;
    TextView mTextViewDescription;
    TextView mTextViewPhone;
    TextView mTextViewUsername;
    TextView mTextViewNameCategory;
    TextView mTextViewRelativeTime;
    TextView mTextViewLikes;
    ImageView mImageViewCategory;
    CircleImageView mCircleImageViewProfile;
    Button mButtonShowProfile;
    FloatingActionButton mFabComent;
    RecyclerView mRecyclerView;
    Toolbar mToolbar;

    String mIdUser="";

    ListenerRegistration mListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);

        mSliderView = findViewById(R.id.imageSlider);

        mTextViewTitle = findViewById(R.id.textViewTitle);
        mTextViewDescription= findViewById(R.id.textViewDescription);
        mTextViewUsername = findViewById(R.id.textViewUsername);
        mTextViewPhone = findViewById(R.id.textViewPhone);
        mTextViewNameCategory = findViewById(R.id.textViewNameCategory);
        mTextViewRelativeTime = findViewById(R.id.textViewRelativeTime);
        mTextViewLikes = findViewById(R.id.textViewLikes);
        mImageViewCategory = findViewById(R.id.imageViewCategory);
        mCircleImageViewProfile = findViewById(R.id.circleImageProfile);
        mButtonShowProfile = findViewById(R.id.btnShowProfile);
        mFabComent = findViewById(R.id.fabComent);
        mRecyclerView =findViewById(R.id.recylerViewComments);

        mToolbar =findViewById(R.id.toolBar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Mostramos las targetas una debajo de otra
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(PostDetailActivity.this);
        mRecyclerView.setLayoutManager(linearLayoutManager);

        mPostProvider = new PostProvider();
        mUserProvider = new UserProvider();
        mCommentsProvider = new CommentsProvider();
        mAuthProvider = new AuthProvider();
        mLikesProvider = new LikesProvider();
        mNotificationProvider = new NotificationProvider();
        mTokenProvider = new TokenProvider();

        mExtraPostId = getIntent().getStringExtra("id");

        mFabComent.setOnClickListener(v -> showDialogComment());

        mButtonShowProfile.setOnClickListener(v -> goToShowProfile());

        getPost();
        
        getNumberLikes();

    }

    //Obtenemos numero de likes en tiempo real
    private void getNumberLikes() {
        mListener = mLikesProvider.getLikesByPost(mExtraPostId).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException error) {
                if (queryDocumentSnapshots !=null){
                    if (!queryDocumentSnapshots.isEmpty()){
                        int numberLikes= queryDocumentSnapshots.size();
                        if (numberLikes ==0){
                            mTextViewLikes.setText( " No tiene me gustas");
                        }
                        else if (numberLikes ==1){
                            mTextViewLikes.setText(numberLikes + " Me gusta");
                        }
                        else {
                            mTextViewLikes.setText(numberLikes + " Me gustas");
                        }
                    }
                }

            }
        });


    }
//Sobrescritura de Metodo de Android on start de la actividad
    @Override
    protected void onStart() {
        super.onStart();
        //Consulta a nuestro Post provider
        Query query = mCommentsProvider.getCommentsByPost(mExtraPostId);
        FirestoreRecyclerOptions<Comment> options = new FirestoreRecyclerOptions.Builder<Comment>()
                .setQuery(query, Comment.class)
                .build();

        mCommentAdapter = new CommentAdapter(options,PostDetailActivity.this);
        mRecyclerView.setAdapter(mCommentAdapter);
        mCommentAdapter.startListening();
        ViewedMessageHelper.updateOnline(true, PostDetailActivity.this);
    }

    //Sobrescritura de Metodo de Android on stop de la actividad
    @Override
    protected void onStop() {
        super.onStop();
        mCommentAdapter.stopListening();
    }
    //Sobrescritura de Metodo de Android on pause la actividad
    @Override
    protected void onPause() {
        super.onPause();
        ViewedMessageHelper.updateOnline(false, PostDetailActivity.this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mListener !=null){
            mListener.remove();
        }
    }

    //MODULO DE COMENTARIO
    private void showDialogComment() {
        AlertDialog.Builder alert = new AlertDialog.Builder(PostDetailActivity.this);
        alert.setTitle("¡COMENTARIO!");
        alert.setMessage("Escribe tu comentario");

        final EditText editText = new EditText(PostDetailActivity.this);
        editText.setHint("Texto");

        //Estetica del alert
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
          LinearLayout.LayoutParams.MATCH_PARENT,
          LinearLayout.LayoutParams.WRAP_CONTENT
        );

        params.setMargins(36,0,36,36);
        editText.setLayoutParams(params);

        RelativeLayout container = new RelativeLayout(PostDetailActivity.this);
        RelativeLayout.LayoutParams relativeParams = new RelativeLayout.LayoutParams(
          RelativeLayout.LayoutParams.MATCH_PARENT,
          RelativeLayout.LayoutParams.WRAP_CONTENT
        );

        container.setLayoutParams(relativeParams);
        container.addView(editText);

        alert.setView(container);

        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
              String value = editText.getText().toString();
              if (!value.isEmpty()) {
                  createComment(value);
              }
              else {
                  Toast.makeText(PostDetailActivity.this, "Debe ingresar el comentario", Toast.LENGTH_SHORT).show();
              }
            }
        });

        alert.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {

            }
        });

        alert.show();

    }
//AÑADIMOS EL COMENTARIO AL POST
    private void createComment(final String value) {
        Comment comment = new Comment();
        comment.setComment(value);
        comment.setIdPost(mExtraPostId);
        comment.setIdUser(mAuthProvider.getUid());
        comment.setTimestamp(new Date().getTime());
        mCommentsProvider.create(comment).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    sendNotification(value);
                    Toast.makeText(PostDetailActivity.this, "El comentario se creo correctamente", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(PostDetailActivity.this, "El comentario NO se creo", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void sendNotification(final String comment) {
        if (mIdUser == null){
            return;
        }
        mTokenProvider.getToken(mIdUser).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()){
                    if (documentSnapshot.contains("token")){
                        String token = documentSnapshot.getString("token");
                        Map<String, String> data = new HashMap<>();
                        data.put("title","NUEVO COMENTARIO");
                        data.put("body",comment);
                        FCMBody body = new FCMBody(token,"high","4500s",data);
                        mNotificationProvider.sendNotification(body).enqueue(new Callback<FCMResponse>() {
                            @Override
                            public void onResponse(Call<FCMResponse> call, Response<FCMResponse> response) {
                                if (response.body() != null){
                                    if (response.body().getSuccess() == 1){
                                        Toast.makeText(PostDetailActivity.this, "La notificacion se envio", Toast.LENGTH_SHORT).show();
                                    }
                                    else{
                                        Toast.makeText(PostDetailActivity.this, "No se pudo enviar la notificacion", Toast.LENGTH_SHORT).show();
                                    }
                                }
                                else{
                                    Toast.makeText(PostDetailActivity.this, "No se pudo enviar la notificacion", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<FCMResponse> call, Throwable t) {

                            }
                        });
                    }
                }
                else {
                    Toast.makeText(PostDetailActivity.this, "Token no existe", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    private void goToShowProfile() {
        if (!mIdUser.equals("")) {
            Intent intent = new Intent(PostDetailActivity.this, UserProfileActivity.class);
            intent.putExtra("idUser", mIdUser);
            startActivity(intent);
        }
        else {
            Toast.makeText(this, "El id del Usuario aun no se carga", Toast.LENGTH_SHORT).show();
        }
    }

    //Configuracion de slider
    private void instanceSlider(){
        //Instanciamos el Slider View
        mSliderAdapter = new SliderAdapter(PostDetailActivity.this,mSliderItems);
        mSliderView.setSliderAdapter(mSliderAdapter);
        //Configuracion Animacion del Slider View
        mSliderView.setIndicatorAnimation(IndicatorAnimationType.THIN_WORM);
        mSliderView.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
        mSliderView.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_RIGHT);
        mSliderView.setIndicatorSelectedColor(Color.WHITE);
        mSliderView.setIndicatorUnselectedColor(Color.GRAY);
        mSliderView.setScrollTimeInSec(3);
        mSliderView.setAutoCycle(true);
        mSliderView.startAutoCycle();

    }

    //Obtenemos los datos del post y los Subimos al activity detail post
    private void getPost(){
        mPostProvider.getPostById(mExtraPostId).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                        if (documentSnapshot.contains("image1")) {
                            String image1 = documentSnapshot.getString("image1");
                            SliderItem item = new SliderItem();
                            item.setImageUrl(image1);
                            mSliderItems.add(item);
                        }
                        if (documentSnapshot.contains("title")) {
                            String title = documentSnapshot.getString("title");
                            assert title != null;
                            mTextViewTitle.setText(title.toUpperCase());
                        }
                        if (documentSnapshot.contains("description")) {
                            String description = documentSnapshot.getString("description");
                            mTextViewDescription.setText(description);
                        }
                        if (documentSnapshot.contains("category")) {
                            String category = documentSnapshot.getString("category");
                            mTextViewNameCategory.setText(category);

                            assert category != null;
                            switch (category) {
                                case "Rock":
                                    Picasso.with(PostDetailActivity.this);
                                    mImageViewCategory.setImageResource(R.drawable.ic_rock);
                                    break;
                                case "Jazz":
                                    Picasso.with(PostDetailActivity.this);
                                    mImageViewCategory.setImageResource(R.drawable.ic_jazz);
                                    break;
                                case "Hip Hop":
                                    Picasso.with(PostDetailActivity.this);
                                    mImageViewCategory.setImageResource(R.drawable.ic_hiphop);
                                    break;
                                case "Rap":
                                    Picasso.with(PostDetailActivity.this);
                                    mImageViewCategory.setImageResource(R.drawable.ic_rap_);
                                    break;
                            }
                        }
                        if (documentSnapshot.contains("idUser")) {
                            mIdUser = documentSnapshot.getString("idUser");
                            getUserInfo(mIdUser);
                        }
                        if (documentSnapshot.contains("timestamp")) {
                            long timestamp = documentSnapshot.getLong("timestamp");
                            String relativeTime = RelativeTime.getTimeAgo(timestamp, PostDetailActivity.this);
                            mTextViewRelativeTime.setText(relativeTime);
                        }


                        instanceSlider();

                }
            }
        });
    }

    //Obtenemos el username, el telefono y la imagen de perfil de la collecion user
    private void getUserInfo(String idUser) {
        mUserProvider.getUser(idUser).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()){
                    if (documentSnapshot.contains("username")){
                        String username = documentSnapshot.getString("username");
                        mTextViewUsername.setText(username);
                    }
                    if (documentSnapshot.contains("phone")){
                        String phone = documentSnapshot.getString("phone");
                        mTextViewPhone.setText(phone);
                    }
                    if (documentSnapshot.contains("image_profile")){
                        String imageProfile = documentSnapshot.getString("image_profile");
                        Picasso.with(PostDetailActivity.this).load(imageProfile).into(mCircleImageViewProfile);
                    }
                }
            }
        });
    }
}