package com.prueba.api_retrofit;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.prueba.api_retrofit.Interface.JsonPH_Api;
import com.prueba.api_retrofit.Model.Users;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    ListView lvFirebase;
    TextView jsonTextView;
    Button btn_fireB;
    Button btn_users;
    Button btn_eliminar;
    EditText txtFiltro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        jsonTextView = findViewById(R.id.jsonTextView);
        btn_fireB = findViewById(R.id.btn_fireB);
        btn_users = findViewById(R.id.btn_users);
        btn_eliminar = findViewById(R.id.btn_eliminar);
        txtFiltro = findViewById(R.id.txtFiltro);
        lvFirebase = findViewById(R.id.lvFirebase);

        listarUsers();

        btn_eliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eliminarUser();
            }
        });

        btn_fireB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guardarUsers();
            }
        });

        btn_users.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                jsonTextView.setText("");
                if (txtFiltro.getText().toString().trim().isEmpty()){
                    getUsers();
                }
                else {
                String id = txtFiltro.getText().toString().trim();
                    getUserById(id);
                }
            }
        });
    }

    private void getUsers(){
        String url = "https://jsonplaceholder.typicode.com/";
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        JsonPH_Api jsonPH_api = retrofit.create(JsonPH_Api.class);
        Call<List<Users>> call = jsonPH_api.getUsers();
        call.enqueue(new Callback<List<Users>>() {
            @Override
            public void onResponse(Call<List<Users>> call, Response<List<Users>> response) {
                if (!response.isSuccessful()){
                    jsonTextView.setText(response.code());
                    return;
                }
                List<Users> usersList = response.body();
                for (Users user: usersList){

                    // aca podria cargar un ArraList<Users>
                    String content = "";
                    content += "Id: "+ user.getId() + "\n";
                    content += "Name: "+ user.getName() + "\n";
                    content += "UserName: "+ user.getUsername() + "\n";
                    content += "Phone: "+ user.getPhone() + "\n\n";
                    jsonTextView.append(content);
                }
            }

            @Override
            public void onFailure(Call<List<Users>> call, Throwable t) {
                Toast.makeText(MainActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getUserById(String id){
        String url = "https://jsonplaceholder.typicode.com/";
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create()).build();

        JsonPH_Api jsonPH_api = retrofit.create(JsonPH_Api.class);
        Call<Users> call = jsonPH_api.getUserById(id);
                call.enqueue(new Callback<Users>() {

                    @Override
                    public void onResponse(Call<Users> call, Response<Users> response) {
                        jsonTextView.setText("");
                        if(!response.isSuccessful()){
                            jsonTextView.setText(response.code());
                        }
                        Users user = response.body();
                        String content = "";
                        content += "Id: "+ user.getId() + "\n";
                        content += "Name: "+ user.getName() + "\n";
                        content += "UserName: "+ user.getUsername() + "\n";
                        content += "Phone: "+ user.getPhone() + "\n\n";
                        jsonTextView.append(content);
                        txtFiltro.setText("");

                    }

                    @Override
                    public void onFailure(Call<Users> call, Throwable t) {
                        txtFiltro.setText("");
                        Toast.makeText(MainActivity.this, "Usuario no encontrado, "+t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void guardarUsers(){
        String id = txtFiltro.getText().toString().trim() ;
        if (jsonTextView.getText().toString().trim().isEmpty() || id.isEmpty() || jsonTextView.getText().toString().trim().equalsIgnoreCase("") || id.equalsIgnoreCase("") ){
            Toast.makeText(this, "Debe seleccionar un id", Toast.LENGTH_SHORT).show();
        } else {
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference dbref = db.getReference().child("Users");
        dbref.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean res = false;
                for (DataSnapshot x : snapshot.getChildren()){
                    if (x.child("id").getValue().toString().trim().equalsIgnoreCase(id)){
                        res = true;
                        Toast.makeText(MainActivity.this, "El usuario (" + id + ") ya existe en la base de datos", Toast.LENGTH_SHORT).show();
                        break;
                     }
                   }
                    if (res == false){
                        String url = "https://jsonplaceholder.typicode.com/";
                        Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl(url)
                            .addConverterFactory(GsonConverterFactory.create()).build();
                        JsonPH_Api jsonPH_api = retrofit.create(JsonPH_Api.class);
                        Call<Users> call = jsonPH_api.getUserById(id);
                        call.enqueue(new Callback<Users>() {

                            @Override
                            public void onResponse(Call<Users> call, Response<Users> response) {
                                if(!response.isSuccessful()){
                                    jsonTextView.setText(response.code());
                                }
                                Users user = response.body();
                                try{
                                    dbref.push().setValue(user);
                                    Toast.makeText(MainActivity.this, "Usuario registrado exitosamente", Toast.LENGTH_SHORT).show();
                                    txtFiltro.setText("");
                                    listarUsers();
                                }catch (Exception e){
                                    Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<Users> call, Throwable t) {
                                Toast.makeText(MainActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {}
            });
        }
    }

    public void listarUsers(){

        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference dbref = db.getReference(Users.class.getSimpleName());

        ArrayList<Users> arr = new ArrayList<Users>();
        ArrayAdapter<Users> adapter = new ArrayAdapter<Users>(MainActivity.this, android.R.layout.simple_list_item_1, arr);
        lvFirebase.setAdapter(adapter);

        dbref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Users users = snapshot.getValue(Users.class);
                arr.add(users);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {adapter.notifyDataSetChanged();}

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {adapter.notifyDataSetChanged();}

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {}

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void eliminarUser(){


        String id = txtFiltro.getText().toString().trim() ;
        if (jsonTextView.getText().toString().trim().isEmpty() || id.isEmpty() || jsonTextView.getText().toString().trim().equalsIgnoreCase("") || id.equalsIgnoreCase("") ){
            Toast.makeText(this, "Debe seleccionar un id", Toast.LENGTH_SHORT).show();
        } else {
            FirebaseDatabase db = FirebaseDatabase.getInstance();
            DatabaseReference dbref = db.getReference().child("Users");
            dbref.addListenerForSingleValueEvent(new ValueEventListener() {

                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    boolean res = false;
                    for (DataSnapshot x : snapshot.getChildren()){

                        if (x.child("id").getValue().toString().trim().equalsIgnoreCase(id)){

                            res = true;

                            // verificar con AlertDialog
//                            AlertDialog.Builder a = new AlertDialog.Builder(MainActivity.this);
//                            a.setCancelable(false);
//                            a.setTitle("Comfirmar Eliminar");
//                            a.setMessage("Está seguro que quiere eliminar el usuario (" + x.child("nombre").getValue().toString() + ")?");
//                            a.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialogInterface, int i) {
//                                    dialogInterface.dismiss();
//
//                                }
//                            });
//                            a.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
//
//                                @Override
//                                public void onClick(DialogInterface dialogInterface, int i) {
//
//                                    x.getRef().removeValue();
//                                    listarUsers();
//                                    Toast.makeText(MainActivity.this, "Usuario eliminado correctamente", Toast.LENGTH_SHORT).show();
//                                    txtFiltro.setText("");
//                                }
//                            });
//                            a.show();

                            x.getRef().removeValue();
                            listarUsers();
                            Toast.makeText(MainActivity.this, "Usuario eliminado correctamente", Toast.LENGTH_SHORT).show();
                            txtFiltro.setText("");

                            break;
                        }
                    }


                    if (res == false){
                        // elemento no encontrado
                        Toast.makeText(MainActivity.this, "El usuario (" + id + ") no existe en la base de datos", Toast.LENGTH_SHORT).show();
                        }
                    }


                @Override
                public void onCancelled(@NonNull DatabaseError error) {}
            });
        }

    }









}