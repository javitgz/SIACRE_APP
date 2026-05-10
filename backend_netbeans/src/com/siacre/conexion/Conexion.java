/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.siacre.conexion;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
/**
 *
 * @author roman
 */
public class Conexion {
    private static final String URL = "jdbc:mysql://localhost:3306/siacre_db?serverTimeZone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = "jaratago19";
    
    public static Connection conectar(){
        Connection conn = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Conexion exitosa a SIACRE_DB");
        } catch (ClassNotFoundException | SQLException e){
            System.out.println("Error en la conexion" + e.getMessage());
        }
        return conn;
    }
}
