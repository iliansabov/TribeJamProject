package com.optic.tribejam.providers;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.optic.tribejam.models.Like;

public class LikesProvider {

    CollectionReference mCollection;

    //Creamos la coleccion en Firebase
    public LikesProvider(){
        mCollection = FirebaseFirestore.getInstance().collection("Likes");
    }
    //Creamos el documento dentro de la coleccion
    public Task<Void> create(Like like){
        DocumentReference document= mCollection.document();
        String id = document.getId();
        like.setId(id);
        return document.set(like);
    }

    //Devolvemos el like donde el post y el ususario conincidan(Consulta compuesta)
    public Query getLikeByPostAndUser(String idPost, String idUser){
        return mCollection.whereEqualTo("idPost",idPost).whereEqualTo("idUser",idUser);
    }

    //Obtenemos los likes en base al id Post
    public Query getLikesByPost(String idPost){
        return  mCollection.whereEqualTo("idPost",idPost);
    }

    //Borramos los likes
    public Task<Void> delete (String id){
        return  mCollection.document(id).delete();
    }

}
