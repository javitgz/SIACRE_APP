/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.siacre.api;

import static spark.Spark.*;
import com.google.gson.Gson;
import com.siacre.dao.UsuarioDAO;
import com.siacre.modelo.Usuario;
/**
 *
 * @author roman
 */
public class AppAPI {
    private static Gson gson = new Gson();
    private static UsuarioDAO usuarioDAO = new UsuarioDAO();
    
    public static void main(String[] args){
        port(8080); // Puerto donde escuchara el backend
        
        // Endpoint para el Login
        post("/login", (req, res) -> {
            Usuario u = gson.fromJson(req.body(), Usuario.class);
           // Aqui llamamos al metodo en DAO que valide correo y contraseña
           if(u.getCorreo().equals("admin@siacre.com") && u.getClave().equals("admin123")){
               return "{\"status\": \"success\", \"message\": \"Bienvenido\"}";
           }
           res.status(401);
           return "{\"status\": \"error\", \"message\": \"Credenciales invalidas\"}";
        });
        
        // Endpoint para listar usuarios
        get("/usuarios", (req, res) -> {
            res.type("application/json");
            return gson.toJson(usuarioDAO.listar());
        });
        
        // Endpoint para eliminar usuario
        delete("/usuarios/:id", (req, res) ->{
            int id = Integer.parseInt(req.params(":id"));
            if (id == 1) return "{\"status\": \"error\", \"message\": \"No se puede eliminar al admin\"}";
            
            if (usuarioDAO.eliminar(id)){
                return "{\"status\": \"success\"}";
            }
            return "{\"status\": \"error\"}";
        });
    }
}
