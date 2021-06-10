package com.optic.tribejam.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.optic.tribejam.R;
import com.optic.tribejam.models.User;
import com.optic.tribejam.providers.AuthProvider;
import com.optic.tribejam.providers.UserProvider;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import dmax.dialog.SpotsDialog;

public class CompleteProfileActivity extends AppCompatActivity {

    TextInputEditText mTextInputUsername;
    TextInputEditText mTextInputPhone;
    Button mButtonRegister;
    AuthProvider mAuthProvider;
    UserProvider mUserProvider;
    AlertDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_profile);

        mTextInputUsername = findViewById(R.id.textInputUsername);
        mTextInputPhone = findViewById(R.id.textInputPhone);

        mAuthProvider = new AuthProvider();
        mUserProvider = new UserProvider();

        //Dialogo de barra de carga
        mDialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Loading")
                .setCancelable(false)
                .build();

        mButtonRegister = findViewById(R.id.btnRegister);

        mButtonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();
            }
        });
    }
//Validacion de registro
    private void register() {
        String username = mTextInputUsername.getText().toString();
        String phone = mTextInputPhone.getText().toString();
        //Validacion
        if (!username.isEmpty() && !username.isEmpty()) {
            updateUser(username,phone);
        } else {
            Toast.makeText(this, "Registro Incorrecto, rellene todos los campos", Toast.LENGTH_LONG).show();
        }
    }

    /*Update de Usuario con FirebaseAuth y guardado en Cloud Firebase*/

    private void updateUser(final String username, final String phone) {
        //Obtenemos usuario de FirebaseAuth
        String id = mAuthProvider.getUid();
        //Llamamos al user
        User user= new User();
        //Establecemos los datos
        user.setUsername(username);
        user.setPhone(phone);
        user.setId(id);
        user.setTimestamp(new Date().getTime());

        mDialog.show();
        /*Actualizamos documento en Colección Users con el username*/
        mUserProvider.update(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                mDialog.dismiss();
                if (task.isSuccessful()){
                    Intent intent = new Intent(CompleteProfileActivity.this,HomeActivity.class);
                    startActivity(intent);
                }
                else{
                    Toast.makeText(CompleteProfileActivity.this, "Error:No se pudo almacenar informacion del ususario", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}