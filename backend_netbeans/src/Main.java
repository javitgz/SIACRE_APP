/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author roman
 */
public class Main {
    public static void main(String[] args) {
        if (com.siacre.conexion.Conexion.conectar() != null) {
            System.out.println("El backend esta listo para recibir datos.");
        } else {
            System.out.println("Inconveniente detectado: Revisa el servicio de MySQL o la clave.");
        }
    }
}

