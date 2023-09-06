package com.prueba.api_retrofit.Interface;



import com.prueba.api_retrofit.Model.Users;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface JsonPH_Api {

    @GET("users")
    Call<List<Users>> getUsers();

    @GET("users/{id}")
    Call<Users> getUserById(@Path("id") String id);

}
