package com.optic.tribejam.fragments;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.optic.tribejam.R;
import com.optic.tribejam.activities.EditProfileActivity;
import com.optic.tribejam.adapters.MyPostsAdapter;
import com.optic.tribejam.adapters.PostsAdapter;
import com.optic.tribejam.models.Post;
import com.optic.tribejam.providers.AuthProvider;
import com.optic.tribejam.providers.PostProvider;
import com.optic.tribejam.providers.UserProvider;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


public class ProfileFragment extends Fragment {

    LinearLayout mLinearLayoutEditProfile;
    View mView;

    TextView mTextViewUsername;
    TextView mTextViewPhone;
    TextView mTextViewEmail;
    TextView mTextViewPostNumber;
    TextView mTextViewPostExist;
    ImageView mImageViewCover;
    CircleImageView mCircleImagerofile;
    RecyclerView mRecyclerView;

    UserProvider mUserProvider;
    AuthProvider mAuthProvider;
    PostProvider mPostProvider;

    MyPostsAdapter mMyPostsAdapter;

    ListenerRegistration mListener;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       mView = inflater.inflate(R.layout.fragment_profile, container, false);
       mLinearLayoutEditProfile = mView.findViewById(R.id.linearLayoutEditProfile);
       mTextViewEmail = mView.findViewById(R.id.textViewEmail);
       mTextViewUsername = mView.findViewById(R.id.textViewUsername);
       mTextViewPhone = mView.findViewById(R.id.textViewPhone);
       mTextViewPostExist = mView.findViewById(R.id.textViewPostExist);
       mTextViewPostNumber = mView.findViewById(R.id.textViewPostNumber);
       mImageViewCover = mView.findViewById(R.id.imageViewCover);
       mCircleImagerofile= mView.findViewById(R.id.circleImageProfile);
       mRecyclerView= mView.findViewById(R.id.recylerViewMyPost);

        //Mostramos las targetas de comentario una debajo de otra
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(linearLayoutManager);

       mLinearLayoutEditProfile.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               goToEditProfile();
           }
       });

        mUserProvider = new UserProvider();
        mAuthProvider = new AuthProvider();
        mPostProvider = new PostProvider();

        getUser();
        getPostNumber();
        checkIdExistPost();
       return mView;
    }

    private void checkIdExistPost() {
        mListener = mPostProvider.getPostByUser(mAuthProvider.getUid()).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshot, @Nullable FirebaseFirestoreException error) {
                    if (queryDocumentSnapshot !=null){
                        int numberPost = queryDocumentSnapshot.size();
                        if (numberPost > 0){
                            mTextViewPostExist.setText("Publicaciones:");
                            mTextViewPostExist.setTextColor(Color.MAGENTA);
                        }
                        else{
                            mTextViewPostExist.setText("No hay Publicaciones");
                            mTextViewPostExist.setTextColor(Color.GRAY);
                        }
                }
            }
        });
    }


    @Override
    public void onStart() {
        super.onStart();
        //Consulta a nuestro Post provider
        Query query = mPostProvider.getPostByUser(mAuthProvider.getUid());
        FirestoreRecyclerOptions<Post> options = new FirestoreRecyclerOptions.Builder<Post>()
                .setQuery(query, Post.class)
                .build();

        mMyPostsAdapter = new MyPostsAdapter(options,getContext());
        mRecyclerView.setAdapter(mMyPostsAdapter);
        mMyPostsAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        mMyPostsAdapter.stopListening();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mListener !=null){
            mListener.remove();
        }
    }

    private void goToEditProfile() {
        Intent intent = new Intent(getContext(), EditProfileActivity.class);
        startActivity(intent);
    }

    //Metodo de conteo de Posts
    private void getPostNumber(){
        mPostProvider.getPostByUser(mAuthProvider.getUid()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                //Contamos los post comparandolos con el tamaño de la coleccion
                int numberPost = queryDocumentSnapshots.size();
                mTextViewPostNumber.setText(String.valueOf(numberPost));
            }
        });
    }

    //Obtenemos el usuario de la base de datos y los cargamos en nuestro perfil
    private void getUser(){
        mUserProvider.getUser(mAuthProvider.getUid()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()){
                    if (documentSnapshot.contains("email")){
                        String email = documentSnapshot.getString("email");
                        mTextViewEmail.setText(email);
                    }
                    if (documentSnapshot.contains("phone")){
                        String phone = documentSnapshot.getString("phone");
                        mTextViewPhone.setText(phone);
                    }
                    if (documentSnapshot.contains("username")){
                        String username = documentSnapshot.getString("username");
                        mTextViewUsername.setText(username);
                    }
                    if (documentSnapshot.contains("image_profile")){
                        String imageProfile = documentSnapshot.getString("image_profile");
                        if (imageProfile != null){
                            if (!imageProfile.isEmpty()){
                                Picasso.with(getContext()).load(imageProfile).into(mCircleImagerofile);
                            }
                        }
                    }
                    if (documentSnapshot.contains("image_cover")){
                        String imageCover = documentSnapshot.getString("image_cover");
                        if (imageCover != null){
                            if (!imageCover.isEmpty()){
                                Picasso.with(getContext()).load(imageCover).into(mImageViewCover);
                            }
                        }
                    }
                }
            }
        });
    }

}