package com.optic.tribejam.providers;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.optic.tribejam.models.Message;

import java.util.HashMap;
import java.util.Map;

public class MessageProvider {

    CollectionReference mCollection;
    //Obtenemos  la coleccion
    public MessageProvider() {
        mCollection = FirebaseFirestore.getInstance().collection("Messages");
    }
    //Creamos la coleccion
    public Task<Void> create(Message message) {
        DocumentReference document = mCollection.document();
        message.setId(document.getId());
        return document.set(message);
    }
    //Obtenemos mensajes de la coleccion filtrados por el id de Chat
    public Query getMessageByChat(String idChat) {
        return mCollection.whereEqualTo("idChat", idChat).orderBy("timestamp", Query.Direction.ASCENDING);
    }
    //Obtenemos mensajes de la coleccion filtrados por el id de Chat y del Emisor
    public Query getMessagesByChatAndSender(String idChat, String idSender) {
        return mCollection.whereEqualTo("idChat", idChat).whereEqualTo("idSender", idSender).whereEqualTo("viewed", false);
    }
    //Obtenemos los tres ultimos mensajes de la coleccion filtrados por el id de Chat y del Emisor
    public Query getLastThreeMessagesByChatAndSender(String idChat, String idSender) {
        return mCollection
                .whereEqualTo("idChat", idChat)
                .whereEqualTo("idSender", idSender)
                .whereEqualTo("viewed", false)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(3);
    }
    //Obtenemos el ultimo mensaje
    public Query getLastMessage(String idChat) {
        return mCollection.whereEqualTo("idChat", idChat).orderBy("timestamp", Query.Direction.DESCENDING).limit(1);
    }
    //Obtenemos el ultimo mensaje del Emisor
    public Query getLastMessageSender(String idChat, String idSender) {
        return mCollection.whereEqualTo("idChat", idChat).whereEqualTo("idSender", idSender).orderBy("timestamp", Query.Direction.DESCENDING).limit(1);
    }
    //Actualizamos el estado de los mesajes
    public Task<Void> updateViewed(String idDocument, boolean state) {
        Map<String, Object> map = new HashMap<>();
        map.put("viewed", state);
        return mCollection.document(idDocument).update(map);
    }

}
