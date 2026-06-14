package com.siacre.conexion;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.cloud.FirestoreClient;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

/**
 * Clase para manejar la conexión a la base de datos con Firebase (Firestore)
 * Optimizada para el entorno Ant de SIACRE, eliminando dependencias de
 * properties.
 *
 * @author Javier R. Tamayo
 */
public class Conexion {

    private static boolean inicializado = false;

    static {
        // Inicializamos Firebase de forma directa al cargar la clase en memoria
        conectarFirebase();
    }

    private static void conectarFirebase() {
        if (inicializado) {
            return;
        }

        try {
            // 1. Ubicar el archivo JSON físicamente
            File rutaJson = new File("src/resources/siacre-app-firebase-adminsdk-fbsvc-ec70123c43.json");

            if (!rutaJson.exists()) {
                rutaJson = new File("resources/siacre-app-firebase-adminsdk-fbsvc-ec70123c43.json");
            }

            if (!rutaJson.exists()) {
                throw new RuntimeException("No se encontró el archivo JSON de Firebase en: " + rutaJson.getAbsolutePath());
            }

            InputStream archivoServicio = new FileInputStream(rutaJson);

            // 2. CONFIGURACIÓN CLAVE: Forzamos el uso del transporte nativo de Google APIs
            // Esto evita que Firebase intente buscar los submódulos HTTP/2 de Apache Core 5
            FirebaseOptions opciones = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(archivoServicio))
                    .setHttpTransport(com.google.api.client.googleapis.javanet.GoogleNetHttpTransport.newTrustedTransport())
                    .build();

            // 3. Inicializar la App
            FirebaseApp.initializeApp(opciones);
            inicializado = true;
            System.out.println(">>> [SIACRE BACKEND] Conexión exitosa a Firebase Cloud <<<");

        } catch (Exception e) {
            System.err.println("Error crítico al conectar con Firebase: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("No se pudo inicializar el entorno de Firebase", e);
        }
    }

    public static Firestore getFirestore() {
        if (!inicializado) {
            conectarFirebase();
        }
        return FirestoreClient.getFirestore();
    }

    /**
     * MÉTODO TEMPORAL: Permite que las clases que aún usan MySQL (como
     * ClienteDAO) sigan compilando mientras hacemos la migración paso a paso.
     */
    public static java.sql.Connection conectar() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return java.sql.DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/siacre_db?serverTimeZone=UTC&useSSL=false",
                    "root",
                    "jaratago19"
            );
        } catch (Exception e) {
            System.out.println("MySQL temporal status: " + e.getMessage());
            return null;
        }
    }
}
