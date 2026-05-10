/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.siacre.dao;

import com.siacre.conexion.Conexion;
import com.siacre.modelo.Cliente;
import com.siacre.modelo.SolicitudCredito;
import java.sql.*;
/**
 *
 * @author roman
 */
public class ClienteDAO {
    public boolean registrarClienteConCredito(Cliente cliente, SolicitudCredito solicitud) {
        Connection con = null;
        PreparedStatement psCliente = null;
        PreparedStatement psCredito = null;
        ResultSet rs = null;

        String sqlCliente = "INSERT INTO clientes (tipo_documento, documento, nombres, apellidos, correo, telefono, id_usuario) VALUES (?,?,?,?,?,?,?)";
        String sqlCredito = "INSERT INTO solicitudCredito (monto, datos_evaluacion, puntaje_score, estado, id_cliente) VALUES (?,?,?,?,?)";

        try {
            con = Conexion.conectar();
            con.setAutoCommit(false); // Iniciamos Transacción

            // 1. Insertar Cliente
            psCliente = con.prepareStatement(sqlCliente, Statement.RETURN_GENERATED_KEYS);
            psCliente.setString(1, cliente.getTipoDocumento());
            psCliente.setInt(2, cliente.getDocumento());
            psCliente.setString(3, cliente.getNombres());
            psCliente.setString(4, cliente.getApellidos());
            psCliente.setString(5, cliente.getCorreo());
            psCliente.setString(6, cliente.getTelefono());
            psCliente.setInt(7, cliente.getIdUsuario());
            
            int filasCliente = psCliente.executeUpdate();
            
            if (filasCliente > 0) {
                rs = psCliente.getGeneratedKeys();
                if (rs.next()) {
                    int idGenerado = rs.getInt(1); // Obtenemos el ID del cliente recién creado

                    // 2. Insertar Solicitud de Crédito vinculada al cliente
                    psCredito = con.prepareStatement(sqlCredito);
                    psCredito.setDouble(1, solicitud.getMonto());
                    psCredito.setString(2, solicitud.getDatosEvaluacion());
                    psCredito.setDouble(3, solicitud.getPuntajeScore());
                    psCredito.setInt(4, 0); // Estado inicial: Pendiente
                    psCredito.setInt(5, idGenerado);
                    
                    psCredito.executeUpdate();
                }
            }

            con.commit(); // Si todo sale bien, guardamos cambios
            return true;

        } catch (SQLException e) {
            try {
                if (con != null) con.rollback(); // Si algo falla, revertimos todo
            } catch (SQLException ex) {
                System.out.println("Error en Rollback: " + ex.getMessage());
            }
            System.out.println("Error al registrar: " + e.getMessage());
            return false;
        } finally {
            // Cerrar conexiones
            try {
                if (rs != null) rs.close();
                if (psCliente != null) psCliente.close();
                if (psCredito != null) psCredito.close();
                if (con != null) con.close();
            } catch (SQLException e) {}
        }
    }
}
