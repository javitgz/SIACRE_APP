/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
import com.siacre.dao.ClienteDAO;
import com.siacre.modelo.Cliente;
import com.siacre.modelo.SolicitudCredito;
/**
 *
 * @author roman
 */
public class Main {    
    public static void main(String[] args) {
    Cliente nuevoCliente = new Cliente();
    nuevoCliente.setTipoDocumento("CC");
    nuevoCliente.setDocumento(12345678);
    nuevoCliente.setNombres("Juan");
    nuevoCliente.setApellidos("Prueba");
    nuevoCliente.setIdUsuario(1); // El admin que ya existe

    SolicitudCredito nuevaSol = new SolicitudCredito();
    nuevaSol.setMonto(5000000);
    nuevaSol.setDatosEvaluacion("Info Cualitativa: Contrato Indefinido. Cuantitativa: Score 750");
    nuevaSol.setPuntajeScore(75.5);

    ClienteDAO dao = new ClienteDAO();
    if(dao.registrarClienteConCredito(nuevoCliente, nuevaSol)) {
        System.out.println("EXITO: Cliente y Credito guardados en base de datos.");
    } else {
        System.out.println("ERROR: La transacción falló.");
    }
}
}

