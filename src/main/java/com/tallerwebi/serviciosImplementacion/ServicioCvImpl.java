package com.tallerwebi.serviciosImplementacion;

import com.tallerwebi.dominio.Usuario;
import com.tallerwebi.dominio.UsuarioMateria;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class ServicioCvImpl {
    private final ClienteGemini clienteGemini; // Este es el cliente que usó tu amigo

    @Autowired
    public ServicioCvImpl(ClienteGemini clienteGemini) {
        this.clienteGemini = clienteGemini;
    }

    public String generarCVHarvard(Usuario usuario, List<UsuarioMateria> materiasAprobadas) {
        String prompt = armarPrompt(usuario, materiasAprobadas);
        return clienteGemini.enviarPrompt(prompt); // o generarTextoCV()
    }

    private String armarPrompt(Usuario usuario, List<UsuarioMateria> materias) {
        StringBuilder sb = new StringBuilder();
        sb.append("Genera un currículum modelo Harvard para:\n");
        sb.append("Nombre: ").append(usuario.getNombre()).append(" ").append(usuario.getApellido()).append("\n");
        sb.append("Email: ").append(usuario.getEmail()).append("\n");
        sb.append("Carrera: ").append(usuario.getCarrera().getNombre()).append("\n");
        sb.append("Materias Aprobadas:\n");

        for (UsuarioMateria m : materias) {
            sb.append("- ").append(m.getMateria().getNombre()).append("\n");
        }

        sb.append("Usá formato claro, profesional y conciso.");
        return sb.toString();
    }
}
