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

    // URLs sin auth
    private static final List<String> EXCLUDED_PATHS = Arrays.asList(
        "/login",
        "/validar-login", 
        "/nuevo-usuario",
        "/registrarme",
        "/pruebaDeDatos",
        "/grafico",
        "/logout",
        "/",
        "/css",
        "/js",
        "/webjars"
    );

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestURI = request.getRequestURI();
        String contextPath = request.getContextPath();
        
        // Borrar contexto del path
        String path = requestURI.substring(contextPath.length());
        
        // Permitir acceso a EXCLUDED_PATHS
        if (shouldExcludePath(path)) {
            return true;
        }
        
        // si hay ID esta logueado
        HttpSession session = request.getSession(false);
        Object userId = session != null ? session.getAttribute("ID") : null;
        
        if (userId == null) {
            // Uso no hay id, redirect a login
            response.sendRedirect(contextPath + "/login");
            return false;
        }
        
        // Si hay, dejamos pasar
        return true;
    }
    
    private boolean shouldExcludePath(String path) {
        return EXCLUDED_PATHS.stream().anyMatch(excludedPath -> 
            path.equals(excludedPath) || path.startsWith(excludedPath + "/")
        );
    }
}
