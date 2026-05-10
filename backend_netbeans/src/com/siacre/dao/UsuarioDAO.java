/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.siacre.dao;

import com.siacre.conexion.Conexion;
import com.siacre.modelo.Usuario;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
/**
 *
 * @author roman
 */
public class UsuarioDAO {
    // CREATE - Crear Usuario
    public boolean crear(Usuario user) {
        String sql = "INSERT INTO usuarios (nombres, apellidos, correo, clave, id_rol) VALUES (?,?,?,?,?)";
        try (Connection con = Conexion.conectar(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, user.getNombres());
            ps.setString(2, user.getApellidos());
            ps.setString(3, user.getCorreo());
            ps.setString(4, user.getClave());
            ps.setInt(5, user.getId_rol());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al crear: " + e.getMessage());
            return false;
        }
    }

    // READ - Leer Usuarios
    public List<Usuario> listar() {
        List<Usuario> lista = new ArrayList<>();
        String sql = "SELECT * FROM usuarios";
        try (Connection con = Conexion.conectar(); Statement st = con.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Usuario u = new Usuario();
                u.setId(rs.getInt("id"));
                u.setNombres(rs.getString("nombres"));
                u.setCorreo(rs.getString("correo"));
                // ... setear los demás campos
                lista.add(u);
            }
        } catch (SQLException e) {
            System.out.println("Error al listar: " + e.getMessage());
        }
        return lista;
    }

    // UPDATE - Editar Usuario
    public boolean editar(Usuario user) {
        String sql = "UPDATE usuarios SET nombres=?, apellidos=?, correo=? WHERE id=?";
        try (Connection con = Conexion.conectar(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, user.getNombres());
            ps.setString(2, user.getApellidos());
            ps.setString(3, user.getCorreo());
            ps.setInt(4, user.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    // DELETE - Eliminar Usuario
    public boolean eliminar(int id) {
        String sql = "DELETE FROM usuarios WHERE id=?";
        try (Connection con = Conexion.conectar(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }
}
