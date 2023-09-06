package com.prueba.api_retrofit.Model;

public class Users {

   private int id;
   private String name;
   private String username;
   private String phone;

    public Users() {
    }

    public Users(int id, String name, String username, String phone) {
        this.id = id;
        this.name = name;
        this.username = username;
        this.phone = phone;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Override
    public String toString() {
        return "Nombre: "+name + "\n"+
                "Id: "+id+"\n"+
                "Username: "+ username + "\n"+
                "Phone: " + phone;
    }
}
