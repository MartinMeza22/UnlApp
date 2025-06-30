package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.*;
import com.tallerwebi.dominio.excepcion.CodigoVerificacionExpirado;
import com.tallerwebi.dominio.excepcion.CodigoVerificacionIncorrecto;
import com.tallerwebi.dominio.excepcion.UsuarioExistente;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
public class ControladorLogin {

    private RepositorioLogin repositorioLogin;

    @Autowired
    private ServicioMateria servicioMateria;
    private RepositorioUsuario repositorioUsuario;
    private ServicioEmail servicioEmail;
    private ServicioUsuarioMateria servicioUsuarioMateria;
    private ServicioCarrera servicioCarrera;

    @Autowired
    public ControladorLogin(RepositorioLogin repositorioLogin, ServicioEmail servicioEmail, RepositorioUsuario repositorioUsuario, ServicioUsuarioMateria servicioUsuarioMateria, ServicioCarrera servicioCarrera) {
        this.repositorioLogin = repositorioLogin;
        this.servicioEmail = servicioEmail;
        this.repositorioUsuario = repositorioUsuario;
        this.servicioUsuarioMateria = servicioUsuarioMateria;
        this.servicioCarrera = servicioCarrera;
    }

    @RequestMapping("/login")
    public ModelAndView irALogin() {

        ModelMap modelo = new ModelMap();
        modelo.put("datosLogin", new DatosLogin());
        return new ModelAndView("login", modelo);
    }

    @RequestMapping(path = "/validar-login", method = RequestMethod.POST)
    public ModelAndView validarLogin(@ModelAttribute("datosLogin") DatosLogin datosLogin, HttpServletRequest request) {
        ModelMap model = new ModelMap();

        Usuario usuarioBuscado = repositorioLogin.consultarUsuario(datosLogin.getEmail(), datosLogin.getPassword());
        if (usuarioBuscado == null) {
            model.put("error", "Usuario o clave incorrecta");
        } else if (!usuarioBuscado.getActivo()) {
            model.put("error", "Tu cuenta no ha sido activada. Por favor, verifica tu email");
        } else {
            request.getSession().setAttribute("ROL", usuarioBuscado.getRol());
            request.getSession().setAttribute("NOMBRE", usuarioBuscado.getNombre());
            request.getSession().setAttribute("ID", usuarioBuscado.getId());
            return new ModelAndView("redirect:/home");
        }
        return new ModelAndView("login", model);

    }

    @RequestMapping(path = "/registrarme", method = RequestMethod.POST)
    public ModelAndView registrarme(@ModelAttribute("usuario") Usuario usuario, HttpServletRequest request) {
        ModelMap model = new ModelMap();
        try {
            // --- LÍNEAS PARA DEBUGUEAR ---
            System.out.println("DEBUG: Usuario.email recibido: " + usuario.getEmail());
            System.out.println("DEBUG: Usuario.nombre recibido: " + usuario.getNombre());
            System.out.println("DEBUG: Usuario.apellido recibido: " + usuario.getApellido());
            System.out.println("DEBUG: Usuario.carreraID recibido: " + usuario.getCarreraID());
            System.out.println("DEBUG: Usuario.rol recibido: " + usuario.getRol());
            System.out.println("DEBUG: Usuario.situacionLaboral recibido: " + usuario.getSituacionLaboral());
            System.out.println("DEBUG: Usuario.disponibilidadHoraria recibido: " + usuario.getDisponibilidadHoraria());
            // ---------------------------------------------------

            usuario.setActivo(false);
            repositorioLogin.registrar(usuario);
            Usuario usuarioBuscado = repositorioLogin.consultarUsuario(usuario.getEmail(), usuario.getPassword());
            request.getSession().setAttribute("ID", usuarioBuscado.getId()); // <-- NUEVO
            System.out.println(usuario);
            this.servicioEmail.guardarYEnviarCodigoDeVerificacion(usuario);
            model.put("usuario", usuario);
            return new ModelAndView("verificar-token", model);
            // return mostrarFormularioDeMaterias(model);
        } catch (UsuarioExistente e) {
            model.put("error", "El usuario ya existe");
            return new ModelAndView("nuevo-usuario", model);
        } catch (Exception e){
            model.put("error", "Error al registrar el nuevo usuario");
            return new ModelAndView("nuevo-usuario", model);
        }
    }

    @RequestMapping(path = "/verificar-token", method = RequestMethod.POST)
    public ModelAndView verificarToken(@RequestParam(name = "codigo") String codigo, @RequestParam(name = "idUser") Long idUser) {
        ModelMap model = new ModelMap();

        try {

            servicioEmail.verificarCodigo(idUser, codigo);
            model.put("idUser", idUser);
            return new ModelAndView("redirect:/registrarme/paso2", model);

        } catch (CodigoVerificacionIncorrecto e) {

            model.put("error", e.getMessage());
            Usuario usuario = this.servicioUsuarioMateria.obtenerUsuario(idUser);
            model.put("usuario", usuario);
            return new ModelAndView("verificar-token", model);

        } catch (CodigoVerificacionExpirado e) {

            model.put("error", e.getMessage());
            return new ModelAndView("nuevo-usuario", model);

        } catch (Exception e) {

            model.put("error", "Error al verificar el código: " + e.getMessage());
            Usuario usuario = this.repositorioUsuario.buscarPorId(idUser);
            model.put("usuario", usuario);
            return new ModelAndView("verificar-token", model);

        }
    }

    @RequestMapping(path = "/nuevo-usuario", method = RequestMethod.GET)
    public ModelAndView nuevoUsuario() {
        ModelMap model = new ModelMap();//key / value
        List<Carrera> carreras = this.servicioCarrera.obtenerTodasLasCarreras();
        model.put("usuario", new Usuario()); //
        model.put("carreras", carreras);
        return new ModelAndView("nuevo-usuario", model);
    }

    @RequestMapping(path = "/", method = RequestMethod.GET)
    public ModelAndView inicio() {
        return new ModelAndView("redirect:/login");
    }

    @RequestMapping(path = "/registrarme/paso2", method = {RequestMethod.POST, RequestMethod.GET})
    public ModelAndView mostrarFormularioDeMaterias(ModelMap model,@RequestParam(name = "idUser") Long idUser) {
        Long idCarrera = this.servicioUsuarioMateria.obtenerUsuario(idUser).getCarreraID();
       // List<Materia> materias = servicioMateria.obtenerTodasLasMateriasPorNombre();
        List<Materia> materias = this.servicioMateria.obtenerMateriasPorCarrera(idCarrera.toString());
        model.addAttribute("materias", materias);
        return new ModelAndView("registroMateriasUsuario", model);
    }

    @RequestMapping(path = "/logout", method = RequestMethod.GET)
    public ModelAndView logout(HttpServletRequest request) {
        if (request.getSession(false) != null) {
            request.getSession().invalidate();
        }
        return new ModelAndView("redirect:/login");
    }

}

