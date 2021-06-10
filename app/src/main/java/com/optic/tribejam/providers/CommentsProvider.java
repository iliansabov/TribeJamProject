package com.optic.tribejam.providers;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.optic.tribejam.models.Comment;

//Metodo de Creacion de Collecion de Comentarios
public class CommentsProvider {

    CollectionReference mCollection;

    //Conectamos a la coleccion Comments de la base de datos de fire store
    public CommentsProvider(){ mCollection = FirebaseFirestore.getInstance().collection("Comments"); }

    //Guardamos el Comment en Firebase
    public Task<Void> create(Comment comment){
        return mCollection.document().set(comment);
    }

    //Buscamos todos los Comentarios donde el id del Post es igual al id que recibimos
    public Query getCommentsByPost(String idPost){ return mCollection.whereEqualTo("idPost",idPost); }
}
