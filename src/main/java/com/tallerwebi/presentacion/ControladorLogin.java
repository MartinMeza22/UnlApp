package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.servicios.ServicioEmail;
import com.tallerwebi.dominio.servicios.ServicioMateria;
import com.tallerwebi.dominio.servicios.ServicioUsuarioMateria;
import com.tallerwebi.repositorioInterfaz.RepositorioLogin;
import com.tallerwebi.repositorioInterfaz.RepositorioUsuario;
import com.tallerwebi.dominio.*;
import com.tallerwebi.dominio.excepcion.CodigoVerificacionExpirado;
import com.tallerwebi.dominio.excepcion.CodigoVerificacionIncorrecto;
import com.tallerwebi.dominio.excepcion.UsuarioExistente;
import com.tallerwebi.servicioInterfaz.ServicioCarrera;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
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
    public ControladorLogin(RepositorioLogin repositorioLogin, ServicioEmail servicioEmail, RepositorioUsuario repositorioUsuario, ServicioUsuarioMateria servicioUsuarioMateria, ServicioCarrera servicioCarrera, ServicioMateria servicioMateriaMock) {
        this.repositorioLogin = repositorioLogin;
        this.servicioEmail = servicioEmail;
        this.repositorioUsuario = repositorioUsuario;
        this.servicioUsuarioMateria = servicioUsuarioMateria;
        this.servicioCarrera = servicioCarrera;
        this.servicioMateria = servicioMateriaMock;
    }
    //Testeado
    @RequestMapping("/login")
    public ModelAndView irALogin() {

        ModelMap modelo = new ModelMap();
        modelo.put("datosLogin", new DatosLogin());
        return new ModelAndView("login", modelo);
    }
    //Testeado
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
    //Testeado
    @RequestMapping(path = "/registrarme", method = RequestMethod.POST)
    public ModelAndView registrarme(@ModelAttribute("usuario") Usuario usuario, HttpServletRequest request) {
        ModelMap model = new ModelMap();
        // Validaciones manuales campo por campo
        if (usuario.getEmail() == null || usuario.getEmail().trim().isEmpty()) {
            model.put("error", "El email es obligatorio");
            return new ModelAndView("nuevo-usuario", model);
        }
        if (!usuario.getEmail().endsWith("@alumno.unlam.edu.ar")) {
            model.put("error", "El email debe ser institucional (@alumno.unlam.edu.ar)");
            return new ModelAndView("nuevo-usuario", model);
        }
        if (usuario.getPassword() == null || usuario.getPassword().trim().isEmpty()) {
            model.put("error", "La contraseña es obligatoria");
            return new ModelAndView("nuevo-usuario", model);
        }

        if (usuario.getNombre() == null || usuario.getNombre().trim().isEmpty()) {
            model.put("error", "El nombre es obligatorio");
            return new ModelAndView("nuevo-usuario", model);
        }


        if (usuario.getApellido() == null || usuario.getApellido().trim().isEmpty()) {
            model.put("error", "El apellido es obligatorio");
            return new ModelAndView("nuevo-usuario", model);
        }

        if (usuario.getCarreraID() == null) {
            model.put("error", "Debés seleccionar una carrera");
            return new ModelAndView("nuevo-usuario", model);
        }

        if (usuario.getSituacionLaboral() == null || usuario.getSituacionLaboral().trim().isEmpty()) {
            model.put("error", "La situación laboral es obligatoria");
            return new ModelAndView("nuevo-usuario", model);
        }

        if (usuario.getDisponibilidadHoraria() == null) {
            model.put("error", "La disponibilidad horaria es obligatoria");
            return new ModelAndView("nuevo-usuario", model);
        }

        try {
            usuario.setRol("ALUMNO");
            usuario.setActivo(false);
            repositorioLogin.registrar(usuario);
            Usuario usuarioBuscado = repositorioLogin.consultarUsuario(usuario.getEmail(), usuario.getPassword());
            request.getSession().setAttribute("ID", usuarioBuscado.getId());

            this.servicioEmail.guardarYEnviarCodigoDeVerificacion(usuario);
            model.put("usuario", usuario);
            return new ModelAndView("verificar-token", model);
        } catch (UsuarioExistente e) {
            model.put("error", "El usuario ya existe");
            return new ModelAndView("nuevo-usuario", model);
        } catch (Exception e){
            model.put("error", "Error al registrar el nuevo usuario");
            return new ModelAndView("nuevo-usuario", model);
        }
    }

    //Testeado
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
            Usuario usuario = this.servicioUsuarioMateria.obtenerUsuario(idUser);
            model.put("usuario", usuario);
            return new ModelAndView("nuevo-usuario", model);

        } catch (Exception e) {

            model.put("error", "Error al verificar el código: " + e.getMessage());
            Usuario usuario = this.repositorioUsuario.buscarPorId(idUser);
            model.put("usuario", usuario);
            return new ModelAndView("verificar-token", model);

        }
    }
    //Testeado
    @RequestMapping(path = "/nuevo-usuario", method = RequestMethod.GET)
    public ModelAndView nuevoUsuario() {
        ModelMap model = new ModelMap();//key / value
        List<Carrera> carreras = this.servicioCarrera.obtenerTodasLasCarreras();
        model.put("usuario", new Usuario()); //
        model.put("carreras", carreras);
        return new ModelAndView("nuevo-usuario", model);
    }
    //Testeado
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

    //Testeado
    @RequestMapping(path = "/logout", method = RequestMethod.GET)
    public ModelAndView logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);  // Traigo la sesion sin crearla
        if (session != null) {
            session.invalidate();  // la invalido si existe
        }
        return new ModelAndView("redirect:/login");
    }


}

