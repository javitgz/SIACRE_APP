/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.siacre.dao;

import com.siacre.conexion.Conexion;
import com.siacre.modelo.Cliente;
import com.siacre.modelo.SolicitudCredito;
import com.siacre.dao.ClienteDAO;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
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
    
    public List<Cliente> listar() {
    List<Cliente> lista = new ArrayList<>();
    String sql = "SELECT * FROM clientes";
    try (Connection con = Conexion.conectar(); Statement st = con.createStatement(); ResultSet rs = st.executeQuery(sql)) {
        while (rs.next()) {
            Cliente c = new Cliente();
            c.setId(rs.getInt("id"));
            c.setTipoDocumento(rs.getString("tipo_documento"));
            c.setDocumento(rs.getInt("documento"));
            c.setNombres(rs.getString("nombres"));
            c.setApellidos(rs.getString("apellidos"));
            c.setCorreo(rs.getString("correo"));
            c.setTelefono(rs.getString("telefono"));
            lista.add(c);
        }
        } catch (SQLException e) {
            System.out.println("Error al listar clientes: " + e.getMessage());
        }
        return lista;
    }
    
    // Obtener cliente por ID
    public Cliente obtenerPorId(int id) {
        String sql = "SELECT * FROM clientes WHERE id = ?";
        try (Connection con = Conexion.conectar();
            PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Cliente c = new Cliente();
                c.setId(rs.getInt("id"));
                c.setTipoDocumento(rs.getString("tipo_documento"));
                c.setDocumento(rs.getInt("documento"));
                c.setNombres(rs.getString("nombres"));
                c.setApellidos(rs.getString("apellidos"));
                c.setCorreo(rs.getString("correo"));
                c.setTelefono(rs.getString("telefono"));
                c.setIdUsuario(rs.getInt("id_usuario"));
                return c;
            }
            } catch (SQLException e) {
                System.out.println("Error al obtener cliente: " + e.getMessage());
            }
            return null;
    }

    // Editar cliente
    public boolean editar(Cliente c) {
        String sql = "UPDATE clientes SET tipo_documento=?, documento=?, nombres=?, apellidos=?, correo=?, telefono=? WHERE id=?";
        try (Connection con = Conexion.conectar();
            PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, c.getTipoDocumento());
            ps.setInt(2, c.getDocumento());
            ps.setString(3, c.getNombres());
            ps.setString(4, c.getApellidos());
            ps.setString(5, c.getCorreo());
            ps.setString(6, c.getTelefono());
            ps.setInt(7, c.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al editar cliente: " + e.getMessage());
            return false;
        }
    }

    // Eliminar cliente
    public boolean eliminar(int id) {
        String sql = "DELETE FROM clientes WHERE id=?";
        try (Connection con = Conexion.conectar();
            PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
            } catch (SQLException e) {
                System.out.println("Error al eliminar cliente: " + e.getMessage());
                return false;
            }
    }
}
