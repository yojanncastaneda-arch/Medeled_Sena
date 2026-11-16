package com.sena.modelo;

import java.math.BigDecimal;

public class Venta {

    private int idVenta;
    private int idCliente;
    private String producto;
    private int cantidad;
    private BigDecimal precioUnitario;
    private String fechaVenta;
    private String estado;

    public Venta(int idVenta, int idCliente, String producto, int cantidad, BigDecimal precioUnitario, String fechaVenta, String estado) {
        this.idVenta = idVenta;
        this.idCliente = idCliente;
        this.producto = producto;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.fechaVenta = fechaVenta;
        this.estado = estado;
    }

    public int getIdVenta() {
        return idVenta;
    }

    public void setIdVenta(int idVenta) {
        this.idVenta = idVenta;
    }

    public int getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(int idCliente) {
        this.idCliente = idCliente;
    }

    public String getProducto() {
        return producto;
    }

    public void setProducto(String producto) {
        this.producto = producto;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public BigDecimal getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(BigDecimal precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

    public String getFechaVenta() {
        return fechaVenta;
    }

    public void setFechaVenta(String fechaVenta) {
        this.fechaVenta = fechaVenta;
    }

    // Total de la venta (cantidad x precio unitario); no se guarda, se calcula
    public BigDecimal getTotal() {
        return precioUnitario.multiply(BigDecimal.valueOf(cantidad));
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
