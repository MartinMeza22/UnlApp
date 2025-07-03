package com.tallerwebi.punta_a_punta.vistas;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

import java.util.List;

public class VistaProgreso extends VistaWeb {

    public VistaProgreso(Page page) {
        super(page);
    }

    private Locator getMateriaCard(String nombreMateria) {
        return page.locator(".materia-card:has(.materia-name:text-is('" + nombreMateria + "'))");
    }

    public void cursarMateria(String nombreMateria) {
        getMateriaCard(nombreMateria).locator("button:text-is('Cursar')").click();
    }

    public void abrirModalDeEdicion(String nombreMateria) {
        getMateriaCard(nombreMateria).locator(".edit-button").click();
    }

    public void editarNotaYDificultadEnModal(String nota, String dificultad) {
        page.locator("#modalMateriaNota").fill(nota);
        page.locator("#modalMateriaDificultad").selectOption(dificultad);
    }

    public void guardarCambiosModal() {
        page.locator("#materiaEditModal button[name='action'][value='guardarCambios']").click();
    }

    public String obtenerEstadoMateria(String nombreMateria) {
        return getMateriaCard(nombreMateria).locator("span[class*='badge-aprobado'], span[class*='badge-cursando'], span[class*='badge-desaprobado'], span[class*='badge-pendiente']").first().textContent();
    }


    public String obtenerNotaDeMateria(String nombreMateria) {
        return getMateriaCard(nombreMateria).locator(".materia-nota > span").textContent();
    }

    public void dejarMateria(String nombreMateria) {
        getMateriaCard(nombreMateria).locator("button:text-is('Dejar materia')").click();
    }

    public Boolean botonCursarEstaDisponible(String nombreMateria) {
        return getMateriaCard(nombreMateria).locator("button:text-is('Cursar')").isVisible();
    }

    public void filtrar(String valor) {
        page.locator("#condicionFilterInput").selectOption(valor);
    }

    public Locator obtenerMaterias() {
        return page.locator(".materia-card");
    }

    public Boolean todasLasMateriasVisibleTienenEstado(String estadoEsperado) {
        List<Locator> materias = obtenerMaterias().all();
        if (materias.isEmpty()) {
            return false;
        }
        for (Locator materia : materias) {
            String estadoActual = materia.locator("span[class*='badge-']").first().textContent().trim();
            if (!estadoActual.equalsIgnoreCase(estadoEsperado)) {
                return false;
            }
        }
        return true;

    }
}
