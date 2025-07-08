package com.tallerwebi.config;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Arrays;
import java.util.List;

@Component
public class AuthenticationInterceptor implements HandlerInterceptor {

    // Lista actualizada con todas las rutas públicas necesarias
    private static final List<String> EXCLUDED_PATHS = Arrays.asList(
            "/login",
            "/validar-login",
            "/nuevo-usuario",
            "/registrarme",
            "/verificar-token",
            "/registrarme/paso2",
            "/logout",
            "/",
            "/css",
            "/js",
            "/webjars",
            "/uploads" // Se añade para que las imágenes se vean
    );

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestURI = request.getRequestURI();
        String contextPath = request.getContextPath();
        String path = requestURI.substring(contextPath.length());

        // Si la ruta es pública, dejamos pasar sin verificar nada más.
        if (shouldExcludePath(path)) {
            return true;
        }

        HttpSession session = request.getSession(false);

        // Si no hay sesión o no hay ID, el usuario no está logueado. Lo redirigimos.
        if (session == null || session.getAttribute("ID") == null) {
            response.sendRedirect(contextPath + "/login");
            return false;
        }


        // Si llegamos acá, el usuario SÍ está logueado. Ahora verificamos su rol.
        String rol = (String) session.getAttribute("ROL");

        // Si es un ADMIN
        if ("ADMIN".equals(rol)) {
            // Si intenta acceder a una ruta que NO es de admin, lo redirigimos a su panel.
            // Le permitimos el acceso a /logout para que pueda cerrar sesión.
            if (!path.startsWith("/admin") && !path.equals("/home") && !path.startsWith("/foro") && !path.equals("/logout")) {
                response.sendRedirect(contextPath + "/home");
                return false;
            }
        }
        // Si NO es un ADMIN (es un ALUMNO)
        else {
            // Si intenta acceder a una ruta de admin, lo redirigimos a su home.
            if (path.startsWith("/admin")) {
                response.sendRedirect(contextPath + "/home");
                return false;
            }
        }

        // Si el usuario está logueado y tiene permiso para esta ruta, dejamos que continúe.
        return true;
    }

    private boolean shouldExcludePath(String path) {
        return EXCLUDED_PATHS.stream().anyMatch(excludedPath ->
                path.equals(excludedPath) || path.startsWith(excludedPath + "/")
        );
    }
}