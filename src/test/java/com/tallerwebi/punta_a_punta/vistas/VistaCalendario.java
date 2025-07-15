package com.tallerwebi.punta_a_punta.vistas;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.SelectOption;
import com.microsoft.playwright.options.WaitForSelectorState;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

public class VistaCalendario extends VistaWeb{
    public VistaCalendario(Page page) {
        super(page);
    }
    public void editarTituloTipoFechaYDescripcion(
            String titulo, String tipoEvento, String descripcion,
            LocalDateTime fechaDeInicio, LocalDateTime fechaDeFinalizacion) {

        // Esperar que el modal esté visible
        page.waitForSelector("#eventModal.show", new Page.WaitForSelectorOptions().setTimeout(5000));

        // Asegurarse de que los campos estén visibles y listos
        page.waitForSelector("#titulo", new Page.WaitForSelectorOptions().setState(WaitForSelectorState.VISIBLE));
        page.waitForSelector("#tipo", new Page.WaitForSelectorOptions().setState(WaitForSelectorState.VISIBLE));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

        // Título
        Locator inputTitulo = page.locator("#titulo");
        inputTitulo.click();  // ayuda a forzar el focus
        inputTitulo.fill(titulo);

        // Tipo
        page.locator("#tipo").selectOption(tipoEvento);

        // Descripción
        page.locator("#descripcion").fill(descripcion);

        // Fechas
        page.locator("#fechaInicio").fill(fechaDeInicio.format(formatter));
        page.locator("#fechaFin").fill(fechaDeFinalizacion.format(formatter));

        // Enviar
        page.locator("#eventModal button[type='submit']").click();

        // Esperar a que se cierre
        page.waitForSelector("#eventModal", new Page.WaitForSelectorOptions().setState(WaitForSelectorState.HIDDEN));
    }



}
