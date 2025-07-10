package com.tallerwebi.dominio;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Locale;


public class MateriasConPromedios {
    private Materia materia;
    private Double promedioDificultad;
    private Double promedioNota;
    private Long cantidadUsuarios;


    // Constructors
    public MateriasConPromedios() {}

    public MateriasConPromedios(Materia materia, Double promedioDificultad, Double promedioNota, Long cantidadUsuarios) {
        this.materia = materia;
        this.promedioDificultad = promedioDificultad;
        this.promedioNota = promedioNota;
        this.cantidadUsuarios = cantidadUsuarios;
    }

    // Getters and Setters
    public Materia getMateria() {
        return materia;
    }

    public void setMateria(Materia materia) {
        this.materia = materia;
    }

    public Double getPromedioDificultad() {
        return promedioDificultad;
    }

    public void setPromedioDificultad(Double promedioDificultad) {
        this.promedioDificultad = promedioDificultad;
    }

    public Double getPromedioNota() {
        return promedioNota;
    }

    public void setPromedioNota(Double promedioNota) {
        this.promedioNota = promedioNota;
    }

    public Long getCantidadUsuarios() {
        return cantidadUsuarios;
    }

    public void setCantidadUsuarios(Long cantidadUsuarios) {
        this.cantidadUsuarios = cantidadUsuarios;
    }

    // Utility methods for formatted display
    public String getPromedioDificultadFormateado() {
        if (promedioDificultad == null) {
            return "N/A";
        }
        return String.format(Locale.US, "%.1f", promedioDificultad);
    }

    public String getPromedioNotaFormateado() {
        if (promedioNota == null) {
            return "N/A";
        }
        return String.format(Locale.US,"%.1f", promedioNota);
    }

    // Convert difficulty average to descriptive text
    public String getDificultadTexto() {
        if (promedioDificultad == null) {
            return "N/A";
        }
        
        if (promedioDificultad <= 3.0) {
            return "Fácil";
        } else if (promedioDificultad >= 4.0 && promedioDificultad <= 6.0) {
            return "Moderada";
        } else if (promedioDificultad >= 7.0) {
            return "Difícil";
        } else {
            // For values between 3.0 and 4.0, or 6.0 and 7.0
            return "Moderada";
        }
    }

    // Get CSS class for difficulty styling
    public String getDificultadCssClass() {
        if (promedioDificultad == null) {
            return "badge-sin-datos";
        }
        
        if (promedioDificultad <= 3.0) {
            return "badge-facil";
        } else if (promedioDificultad >= 4.0 && promedioDificultad <= 6.0) {
            return "badge-moderada";
        } else if (promedioDificultad >= 7.0) {
            return "badge-dificil";
        } else {
            return "badge-moderada";
        }
    }

    public boolean tienePromedios() {
        return promedioDificultad != null && promedioNota != null && cantidadUsuarios != null && cantidadUsuarios > 0;
    }

    // Delegate methods to access Materia properties directly
    public Long getId() {
        return materia != null ? materia.getId() : null;
    }

    public String getNombre() {
        return materia != null ? materia.getNombre() : null;
    }

    public String getDescripcion() {
        return materia != null ? materia.getDescripcion() : null;
    }

    public String getTipo() {
        return materia != null ? materia.getTipo() : null;
    }

    public Integer getCargaHoraria() {
        return materia != null ? materia.getCargaHoraria() : null;
    }

    public Integer getCuatrimestre() {
        return materia != null ? materia.getCuatrimestre() : null;
    }

    public String getCorrelativa1() {
        return materia != null ? materia.getCorrelativa1() : null;
    }

    public String getCorrelativa2() {
        return materia != null ? materia.getCorrelativa2() : null;
    }

    public String getCorrelativa3() {
        return materia != null ? materia.getCorrelativa3() : null;
    }

    public String getCorrelativa4() {
        return materia != null ? materia.getCorrelativa4() : null;
    }

    public String getCorrelativa5() {
        return materia != null ? materia.getCorrelativa5() : null;
    }

    public String getCorrelativa6() {
        return materia != null ? materia.getCorrelativa6() : null;
    }

    public Boolean getActiva() {
        return materia != null ? materia.getActiva() : null;
    }

    public Carrera getCarrera() {
        return materia != null ? materia.getCarrera() : null;
    }

    public Long getCarreraId() {
        return materia != null ? materia.getCarreraId() : null;
    }

    @Override
    public String toString() {
        return "MateriasConPromedios{" +
                "materia=" + (materia != null ? materia.getNombre() : "null") +
                ", promedioDificultad=" + promedioDificultad +
                ", promedioNota=" + promedioNota +
                ", cantidadUsuarios=" + cantidadUsuarios +
                '}';
    }
}
