/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.medeled_cmr;
import com.sena.dao.ClienteDAO;
import com.sena.modelo.Cliente;
/**
 *
 * @author USER
 */
public class Medeled_cmr {
    

    public static void main(String[] args) {
     ClienteDAO dao = new ClienteDAO();
Cliente c1 = new Cliente(0, "Andres lopez", "Lavadora", "2026-09-01", "pendiente", "Reclamo por ruido");
//dao.insertar(c1);
//dao.listar();
Cliente c2 = new Cliente(3, "Carlos Pérez", "Lavadora", "2026-09-01", "resuelto", "Se cambió la pieza");
//dao.actualizar(c2);
//dao.eliminar(2);
    }
}

