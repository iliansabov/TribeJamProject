package com.optic.tribejam.providers;

/*Método que almacena los datos en Google Storage*/

import android.content.Context;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.optic.tribejam.utils.CompressorBitmapImage;

import java.io.File;
import java.util.Date;

public class ImageProvider {

    StorageReference mStorage;

    public ImageProvider(){
        mStorage= FirebaseStorage.getInstance().getReference();
    }

    /*Buscamos, Comprimimos y Guardamos la Imagen en el Storage*/
    public UploadTask save(Context context, File file){

        byte[] imageByte = CompressorBitmapImage.getImage(context,file.getPath(),500,500);
        StorageReference storage = mStorage.child(new Date() +".jpg");
        mStorage = storage;
        UploadTask task = storage.putBytes(imageByte);

        return task;
    }
//Metodo para obtener la url de la imagen guardada en Storage
    public StorageReference getStorage(){
        return mStorage;
    }

}
