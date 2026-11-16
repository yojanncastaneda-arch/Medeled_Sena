package com.sena.modelo;

public class Cliente {

    private int idCliente;
    private String nombre;
    private String producto;
    private String fechaCompra;
    private String estado;
    private String observaciones;

    // CONSTRUCTOR (Para crear el cliente)
    public Cliente(int idCliente, String nombre, String producto, String fechaCompra, String estado, String observaciones) {
        this.idCliente = idCliente;
        this.nombre = nombre;
        this.producto = producto;
        this.fechaCompra = fechaCompra;
        this.estado = estado;
        this.observaciones = observaciones;
    }

    // GETTERS Y SETTERS (Para leer y modificar los datos)
    public int getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(int idCliente) {
        this.idCliente = idCliente;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getProducto() {
        return producto;
    }

    public void setProducto(String producto) {
        this.producto = producto;
    }

    public String getFechaCompra() {
        return fechaCompra;
    }

    public void setFechaCompra(String fechaCompra) {
        this.fechaCompra = fechaCompra;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }
}
