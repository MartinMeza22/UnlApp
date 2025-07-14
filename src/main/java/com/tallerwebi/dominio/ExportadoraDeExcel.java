package com.tallerwebi.dominio;


import com.tallerwebi.dominio.Usuario;
import com.tallerwebi.dominio.UsuarioMateria;
import com.tallerwebi.dominio.Materia;
import com.tallerwebi.dominio.DTO.UsuarioYMateriasDTO;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.OutputStream;
import java.util.List;

public class ExportadoraDeExcel {

    public static void exportarProgresoUsuarios(List<UsuarioYMateriasDTO> usuarios, OutputStream outputStream){
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet hoja = workbook.createSheet("Progreso");

            // Encabezado
            Row header = hoja.createRow(0);
            header.createCell(0).setCellValue("Usuario");
            header.createCell(1).setCellValue("Carrera");
            header.createCell(2).setCellValue("Materia");
            header.createCell(3).setCellValue("Estado");
            header.createCell(4).setCellValue("Nota");
            header.createCell(5).setCellValue("Dificultad");

            int fila = 1;
            for (UsuarioYMateriasDTO umDTO : usuarios) {
                Usuario usuario = umDTO.getUsuario();
                for (UsuarioMateria um : umDTO.getMaterias()) {

                    Row row = hoja.createRow(fila++);

                    Materia materia = um.getMateria();

                    row.createCell(0).setCellValue(usuario.getNombre());
                    row.createCell(1).setCellValue(usuario.getCarrera().getNombre());
                    row.createCell(2).setCellValue(materia.getNombre());
                    row.createCell(3).setCellValue(estadoComoTexto(um.getEstado()));
                    row.createCell(4).setCellValue(um.getNota() != null ? String.valueOf(um.getNota()) : "-");
                    row.createCell(5).setCellValue(um.getDificultad() != null ? String.valueOf(um.getDificultad()) : "-");
                }
            }

            for (int i = 0; i < 6; i++) {
                hoja.autoSizeColumn(i);
            }

            workbook.write(outputStream);

        } catch (Exception e) {
            throw new RuntimeException("Error al generar Excel: " + e.getMessage(), e);
        }
    }

    private static String estadoComoTexto(Integer estado) {
        switch (estado) {
            case 1: return "Aprobada";
            case 2: return "Cursando";
            case 3: return "Desaprobada";
            default: return "Sin estado";
        }
    }


}
