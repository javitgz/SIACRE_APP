/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.siacre.modelo;

/**
 *
 * @author roman
 */
public class SolicitudCredito {
    private int id;
    private double monto;
    private String datosEvaluacion; // Aquí va la info cualitativa/cuantitativa
    private double puntajeScore;
    private int estado; // 0: Pendiente, 1: Aprobado
    private int idCliente;

    public SolicitudCredito() {}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getMonto() {
        return monto;
    }

    public void setMonto(double monto) {
        this.monto = monto;
    }

    public String getDatosEvaluacion() {
        return datosEvaluacion;
    }

    public void setDatosEvaluacion(String datosEvaluacion) {
        this.datosEvaluacion = datosEvaluacion;
    }

    public double getPuntajeScore() {
        return puntajeScore;
    }

    public void setPuntajeScore(double puntajeScore) {
        this.puntajeScore = puntajeScore;
    }

    public int getEstado() {
        return estado;
    }

    public void setEstado(int estado) {
        this.estado = estado;
    }

    public int getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(int idCliente) {
        this.idCliente = idCliente;
    }
    
    
}
