package com.optic.tribejam.providers;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.optic.tribejam.models.User;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

//Metodo de gestion de Users de google FireBase Database
public class UserProvider {

    private CollectionReference mCollection;

    public UserProvider(){
        //Reverenciamos directamenta la collección qen la que vamos a almacenar los datos de los usuarios
        mCollection = FirebaseFirestore.getInstance().collection("Users");
    }

    //Obtenemos la informacion de la base datos si pasamos un id de ususario
    public Task<DocumentSnapshot> getUser(String id){
        return  mCollection.document(id).get();
    }

    //Creacion de colecionen
    public Task<Void> create(User user){
        return mCollection.document(user.getId()).set(user);
    }

    //Obtenemos la informacion de la base datos del estado del usuario
    public DocumentReference getUserRealtime(String id) {
        return mCollection.document(id);
    }

    //Update de coleccion
    public Task<Void> update(User user){
        Map<String, Object> map = new HashMap<>();
        map.put("username",user.getUsername());
        map.put("phone",user.getPhone());
        map.put("timestamp",new Date().getTime());
        map.put("image_profile",user.getImageProfile());
        map.put("image_cover",user.getImageCover());
        return mCollection.document(user.getId()).update(map);
    }
    //Update del Estado del Usuario
    public Task<Void> updateOnline(String idUser, boolean status) {
        Map<String, Object> map = new HashMap<>();
        map.put("online", status);
        map.put("lastConnect", new Date().getTime());
        return mCollection.document(idUser).update(map);
    }
}
