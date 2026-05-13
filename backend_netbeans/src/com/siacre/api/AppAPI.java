/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.siacre.api;

import static spark.Spark.*;
import com.google.gson.Gson;
import com.siacre.dao.UsuarioDAO;
import com.siacre.dao.ClienteDAO;
import com.siacre.modelo.Usuario;
import com.siacre.modelo.SolicitudCredito;
/**
 *
 * @author roman
 */
public class AppAPI {
    private static Gson gson = new Gson();
    private static UsuarioDAO usuarioDAO = new UsuarioDAO();
    
    public static void main(String[] args){
        port(8080); // Puerto donde escuchara el backend
        
        // Endpoint para Login con BD
        post("/login", (req, res) -> {
            Usuario u = gson.fromJson(req.body(), Usuario.class);
            Usuario valid = usuarioDAO.validar(u.getCorreo(), u.getClave());
            if (valid != null) {
                res.type("application/json");
                // No devolver la clave
                valid.setClave(null);
                return "{\"status\":\"success\",\"usuario\":" + gson.toJson(valid) + "}";
            }
            res.status(401);
            return "{\"status\":\"error\",\"message\":\"Credenciales inválidas\"}";
        });
        
        // Endpoint para Crear usuario
        post("/usuarios", (req, res) -> {
            Usuario u = gson.fromJson(req.body(), Usuario.class);
            boolean ok = usuarioDAO.crear(u);
            res.type("application/json");
            if (ok) return "{\"status\":\"success\"}";
            res.status(400);
            return "{\"status\":\"error\"}";
        });
        
        // Endpoint para Editar usuario
        put("/usuarios/:id", (req, res) -> {
            int id = Integer.parseInt(req.params(":id"));
            Usuario u = gson.fromJson(req.body(), Usuario.class);
            u.setId(id);
            boolean ok = usuarioDAO.editar(u);
            res.type("application/json");
            if (ok) return "{\"status\":\"success\"}";
            res.status(400);
            return "{\"status\":\"error\"}";
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
        
        // Endpoint para listar clientes
        get("/clientes", (req, res) -> {
        res.type("application/json");
            ClienteDAO dao = new ClienteDAO();
            return gson.toJson(dao.listar());
        });
        
        // endpoint para guardar cliente
        post("/clientes/guardar_completo", (req, res) -> {
        res.type("application/json");
    
            // Usamos un Map para leer el JSON de forma flexible sin crear clases extra
            java.util.Map<String, Object> data = gson.fromJson(req.body(), java.util.Map.class);
    
            com.siacre.modelo.Cliente c = new com.siacre.modelo.Cliente();
            c.setTipoDocumento((String) data.get("tipo_documento"));
            // Convertimos a int de forma segura (Gson suele leer números como Double)
            c.setDocumento(((Double) data.get("documento")).intValue());
            c.setNombres((String) data.get("nombres"));
            c.setApellidos((String) data.get("apellidos"));
            c.setCorreo((String) data.get("correo"));
            c.setTelefono((String) data.get("telefono"));
            c.setIdUsuario(((Double) data.get("id_usuario")).intValue());

            com.siacre.modelo.SolicitudCredito s = new com.siacre.modelo.SolicitudCredito();
            s.setMonto((Double) data.get("monto"));
            s.setDatosEvaluacion((String) data.get("datos_evaluacion"));
            s.setPuntajeScore((Double) data.get("puntaje_score"));

            com.siacre.dao.ClienteDAO clienteDAO = new com.siacre.dao.ClienteDAO();
            boolean exito = clienteDAO.registrarClienteConCredito(c, s);

            if (exito) {
                return "{\"status\": \"success\", \"message\": \"Registro integral exitoso\"}";
            } else {
                res.status(500);
                return "{\"status\": \"error\", \"message\": \"Fallo en la base de datos\"}";
            }
        });

    }
}
