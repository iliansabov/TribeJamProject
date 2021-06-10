package com.optic.tribejam.providers;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.optic.tribejam.models.Post;

//Metodo de gestion de Posts de google FireBase Database
public class PostProvider {

    CollectionReference mCollection;

    //Conectamos a la coleccion Post de la base de datos de fire store
    public PostProvider(){
        mCollection = FirebaseFirestore.getInstance().collection("Post");
    }

    //Guardamos el Post en Firebase
    public Task<Void> save(Post post){
        return mCollection.document().set(post);
    }

    //Metodo para obtener los posts de forma ordenada(en flujo descendente)
    public Query getAll(){
        return mCollection.orderBy("timestamp",Query.Direction.DESCENDING);
    }
    //Metodo para obtener los posts por categoria y fecha de creacion de forma ordenada(en flujo descendente)
    public Query getPostByCategoryAndTimestamp(String category){
        return mCollection.whereEqualTo("category",category).orderBy("timestamp",Query.Direction.DESCENDING);
    }
    //Metodo para obtener los posts por titulo
    public Query getPostByTitle(String title){
        return mCollection.orderBy("title").startAt(title).endAt(title + '\uf8ff');
    }
    //Buscamos todos los post donde el id del User es igual al id que recibimos
    public Query getPostByUser(String id){ return mCollection.whereEqualTo("idUser",id); }

    //Buscamos todos los post donde el id del Post es igual al id que recibimos
    public Task<DocumentSnapshot> getPostById(String id){ return mCollection.document(id).get(); }

    //Eliminamos por id
    public Task<Void> delete (String id){
        return  mCollection.document(id).delete();
    }

}

