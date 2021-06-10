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
import com.google.firebase.auth.AuthResult;
import com.optic.tribejam.R;
import com.optic.tribejam.models.User;
import com.optic.tribejam.providers.AuthProvider;
import com.optic.tribejam.providers.UserProvider;

import java.util.Date;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;
import dmax.dialog.SpotsDialog;

public class RegusterActivity extends AppCompatActivity {

    CircleImageView mCircleImageViewBack;
    TextInputEditText mTextInputUsername;
    TextInputEditText mTextInputEmail;
    TextInputEditText mTextInputPhone;
    TextInputEditText mTextInputPassword;
    TextInputEditText mTextInputConfirmPassword;
    Button mButtonRegister;
    AuthProvider mAuthProvider;
    UserProvider mUserProvider;
    AlertDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reguster);

        mCircleImageViewBack = findViewById(R.id.circularBackImage);
        mTextInputUsername = findViewById(R.id.textInputUsername);
        mTextInputEmail = findViewById(R.id.textInputEmail);
        mTextInputPhone = findViewById(R.id.textInputPhone);
        mTextInputPassword = findViewById(R.id.textInputPassword);
        mTextInputConfirmPassword = findViewById(R.id.textInputConfirmPassword);

        mAuthProvider = new AuthProvider();
        mUserProvider = new UserProvider();

        //Dialogo de barra de carga
        mDialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Loading")
                .setCancelable(false)
                .build();

        mButtonRegister = findViewById(R.id.btnRegister);
        //Boton de Registro
        mButtonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();
            }
        });
        //Boton de retroceso
        mCircleImageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }
            //Metodo de registro del ususario
    private void register(){
        String username= Objects.requireNonNull(mTextInputUsername.getText()).toString();
        String email= Objects.requireNonNull(mTextInputEmail.getText()).toString();
        String phone= Objects.requireNonNull(mTextInputPhone.getText()).toString();
        String password= Objects.requireNonNull(mTextInputPassword.getText()).toString();
        String confirmPassword= Objects.requireNonNull(mTextInputConfirmPassword.getText()).toString();
            //Validacion
        if(!username.isEmpty() && !email.isEmpty()&& !phone.isEmpty() && !password.isEmpty()&& !confirmPassword.isEmpty()){
            if(isEmailValid(email)){
                if (password.equals(confirmPassword)){
                    if (password.length()>=6){
                        createUser(username,email,phone,password);
                    }
                    else{
                        Toast.makeText(this, "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
                }
            }
            else{
                Toast.makeText(this, "El email no es valido", Toast.LENGTH_LONG).show();
            }
        }
        else {
            Toast.makeText(this, "Registro Incorrecto, rellene todos los campos", Toast.LENGTH_LONG).show();
        }
    }

    /*Creación de Usuario con FirebaseAuth y guardandolo el Cloud Firebase*/

    private void createUser(final String username, final String email,final String phone, final String password){
        mDialog.show();
        /*Se crea el usuario en FirebaseAuth y se guarda la contraseña*/
        mAuthProvider.register(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    /*Coger ID de Firebase Authentication*/
                    String id = mAuthProvider.getUid();
                    //Llamamos la usuario
                    User user = new User();
                    //Guardamos los datos
                    user.setId(id);
                    user.setEmail(email);
                    user.setPhone(phone);
                    user.setUsername(username);
                    user.setPassword(password);
                    //Guardamos la fecha de creacion del ususario
                    user.setTimestamp(new Date().getTime());

                    //Al completar
                    mUserProvider.create(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            mDialog.dismiss();
                            if (task.isSuccessful()){
                                //Redirigimos al usurio a la Home Activity
                                Intent intent = new Intent(RegusterActivity.this,HomeActivity.class);
                                //Limpiamos historial de pantallas
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                            else{
                                Toast.makeText(RegusterActivity.this, "Error: no se ha podido almacenado el usuario", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
                else{
                    mDialog.dismiss();
                    Toast.makeText(RegusterActivity.this, "No se pudo registrar el usuario", Toast.LENGTH_LONG).show();
                }
            }
        });
    }


    /*
    * EMAIL VALIDATION
    * */
    public boolean isEmailValid(String email){
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern =Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

}