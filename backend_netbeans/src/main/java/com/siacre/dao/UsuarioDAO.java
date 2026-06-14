/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.siacre.dao;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteResult;
import com.siacre.conexion.Conexion;
import com.siacre.modelo.Usuario;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Clase DAO para manejar el CRUD de Usuarios en Cloud Firestore.
 * Mantiene la misma estructura de métodos que usaba Spark Java.
 * @author roman
 */
public class UsuarioDAO {

    // CREATE - Crear Usuario en Firebase
    public boolean crear(Usuario user) {
        try {
            Firestore db = Conexion.getFirestore();
            
            // Si el objeto no trae un ID asignado, usamos el timestamp actual como ID numérico para mantener tu estructura de tipo int
            if (user.getId() == 0) {
                user.setId((int) (System.currentTimeMillis() / 1000));
            }

            // Guardamos el documento usando el ID del usuario como nombre del documento
            DocumentReference docRef = db.collection("usuarios").document(String.valueOf(user.getId()));
            ApiFuture<WriteResult> result = docRef.set(user);
            
            // Esperamos que se complete la escritura en la nube
            result.get();
            System.out.println(">>> [SIACRE DAO] Usuario creado exitosamente con ID: " + user.getId());
            return true;
        } catch (Exception e) {
            System.out.println("Error al crear usuario en Firestore: " + e.getMessage());
            return false;
        }
    }

    // READ - Leer Todos los Usuarios (Equivalente a SELECT * FROM usuarios)
    public List<Usuario> listar() {
        List<Usuario> lista = new ArrayList<>();
        try {
            Firestore db = Conexion.getFirestore();
            
            // Traemos todos los documentos dentro de la colección "usuarios"
            ApiFuture<QuerySnapshot> future = db.collection("usuarios").get();
            List<QueryDocumentSnapshot> documentos = future.get().getDocuments();
            
            for (QueryDocumentSnapshot documento : documentos) {
                // Mapeo automático del JSON de la nube a tu objeto java
                Usuario u = documento.toObject(Usuario.class);
                // Por seguridad, vaciamos la clave antes de enviarlo a la lista de la API
                u.setClave(null);
                lista.add(u);
            }
        } catch (Exception e) {
            System.out.println("Error al listar usuarios en Firestore: " + e.getMessage());
        }
        return lista;
    }

    // UPDATE - Editar Usuario
    public boolean editar(Usuario user) {
        try {
            Firestore db = Conexion.getFirestore();
            DocumentReference docRef = db.collection("usuarios").document(String.valueOf(user.getId()));
            
            // En Firestore, para actualizar de forma selectiva (como tu IF de la clave), usamos un mapa de actualización
            Map<String, Object> actualizaciones = new HashMap<>();
            actualizaciones.put("nombres", user.getNombres());
            actualizaciones.put("apellidos", user.getApellidos());
            actualizaciones.put("correo", user.getCorreo());
            
            // Si mandas una clave nueva, la actualizamos; si no, dejamos la que estaba en la BD
            if (user.getClave() != null && !user.getClave().isEmpty()) {
                actualizaciones.put("clave", user.getClave());
            }
            
            ApiFuture<WriteResult> result = docRef.update(actualizaciones);
            result.get(); // Confirmamos los cambios
            System.out.println(">>> [SIACRE DAO] Usuario editado con éxito.");
            return true;
        } catch (Exception e) {
            System.out.println("Error al editar usuario en Firestore: " + e.getMessage());
            return false;
        }
    }

    // DELETE - Eliminar Usuario
    public boolean eliminar(int id) {
        try {
            Firestore db = Conexion.getFirestore();
            ApiFuture<WriteResult> result = db.collection("usuarios").document(String.valueOf(id)).delete();
            result.get(); // Esperamos confirmación del borrado
            System.out.println(">>> [SIACRE DAO] Usuario eliminado con éxito.");
            return true;
        } catch (Exception e) {
            System.out.println("Error al eliminar usuario en Firestore: " + e.getMessage());
            return false;
        }
    }
    
    // AUTH - Validar usuario (Login)
    public Usuario validar(String correo, String clave) {
        try {
            Firestore db = Conexion.getFirestore();

            // Consulta compuesta: Filtramos por correo y clave exactamente igual que tu SELECT anterior
            ApiFuture<QuerySnapshot> query = db.collection("usuarios")
                    .whereEqualTo("correo", correo)
                    .whereEqualTo("clave", clave)
                    .get();

            List<QueryDocumentSnapshot> documentos = query.get().getDocuments();

            if (!documentos.isEmpty()) {
                QueryDocumentSnapshot documento = documentos.get(0);
                Usuario usuarioValidado = documento.toObject(Usuario.class);
                System.out.println(">>> [SIACRE DAO] Autenticación exitosa para: " + usuarioValidado.getCorreo());
                return usuarioValidado;
            } else {
                System.out.println(">>> [SIACRE DAO] Credenciales incorrectas.");
            }
        } catch (Exception e) {
            System.err.println("Error al validar credenciales en Cloud Firestore: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
}