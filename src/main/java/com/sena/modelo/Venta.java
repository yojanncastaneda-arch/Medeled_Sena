package com.sena.modelo;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Venta {

    private int idVenta;
    private int idCliente;
    private String producto;
    private int cantidad;
    private BigDecimal precioUnitario;
    private BigDecimal total;
    private String fechaVenta;
    private String estado;

    public Venta() {
    }

    public Venta(int idVenta, int idCliente, String producto, int cantidad,
                 BigDecimal precioUnitario, String fechaVenta, String estado) {
        this.idVenta = idVenta;
        this.idCliente = idCliente;
        this.producto = producto;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.fechaVenta = fechaVenta;
        this.estado = estado;
        this.total = calcularTotal(cantidad, precioUnitario);
    }

    public static BigDecimal calcularTotal(int cantidad, BigDecimal precioUnitario) {
        if (precioUnitario == null || cantidad <= 0) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }
        return precioUnitario.multiply(BigDecimal.valueOf(cantidad)).setScale(2, RoundingMode.HALF_UP);
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
        this.total = calcularTotal(this.cantidad, this.precioUnitario);
    }

    public BigDecimal getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(BigDecimal precioUnitario) {
        this.precioUnitario = precioUnitario;
        this.total = calcularTotal(this.cantidad, this.precioUnitario);
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public String getFechaVenta() {
        return fechaVenta;
    }

    public void setFechaVenta(String fechaVenta) {
        this.fechaVenta = fechaVenta;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
