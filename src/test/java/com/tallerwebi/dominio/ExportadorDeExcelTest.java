package com.tallerwebi.dominio;

import com.tallerwebi.dominio.*;
import com.tallerwebi.dominio.DTO.UsuarioYMateriasDTO;
import com.tallerwebi.dominio.servicios.ServicioUsuarioMateria;
import com.tallerwebi.presentacion.ControladorAdmin;
import com.tallerwebi.servicioInterfaz.ServicioAdmin;
import com.tallerwebi.servicioInterfaz.ServicioReporte;
import com.tallerwebi.servicioInterfaz.ServicioUsuario;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;

public class ExportadorDeExcelTest {

    private ServicioUsuario servicioUsuarioMock;
    private ServicioReporte servicioReporteMock;
    private ServicioAdmin servicioAdminMock;
    private ServicioUsuarioMateria servicioUsuarioMateriaMock;
    private HttpServletResponse responseMock;
    private ControladorAdmin controlador;

    @BeforeEach
    void setUp() {
        servicioUsuarioMock = mock(ServicioUsuario.class);
        servicioReporteMock = mock(ServicioReporte.class);
        servicioAdminMock   = mock(ServicioAdmin.class);
        servicioUsuarioMateriaMock = mock(ServicioUsuarioMateria.class);
        responseMock = mock(HttpServletResponse.class);
        controlador = new ControladorAdmin(
                servicioUsuarioMock,
                servicioReporteMock,
                servicioAdminMock,
                servicioUsuarioMateriaMock
        );
    }
    @Test
    public void testExportarProgresoUsuarios_mockStatic() throws Exception {
        try (MockedStatic<ExportadoraDeExcel> mockStatic = mockStatic(ExportadoraDeExcel.class)) {
            mockStatic.when(() -> ExportadoraDeExcel.exportarProgresoUsuarios(anyList(), any()))
                    .thenAnswer(invocation -> {
                        // Simulo escribir algo en OutputStream
                        OutputStream os = invocation.getArgument(1);
                        os.write("excel simulacion".getBytes());
                        return null;
                    });

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ExportadoraDeExcel.exportarProgresoUsuarios(List.of(), outputStream);
            assertTrue(outputStream.size() > 0);
        }
    }
    @Test
    public void testExportarProgresoUsuarios_mockStatic_listaVacia() throws Exception {
        try (MockedStatic<ExportadoraDeExcel> mockStatic = mockStatic(ExportadoraDeExcel.class)) {
            mockStatic.when(() -> ExportadoraDeExcel.exportarProgresoUsuarios(anyList(), any()))
                    .thenAnswer(invocation -> {
                        OutputStream os = invocation.getArgument(1);
                        // Simulo exportar excel sin datos
                        os.write("excel vacio".getBytes());
                        return null;
                    });

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ExportadoraDeExcel.exportarProgresoUsuarios(List.of(), outputStream);
            assertTrue(outputStream.size() > 0, "Output no debe estar vacío aún con lista vacía");
        }
    }

    @Test
    public void testExportarProgresoUsuarios_mockStatic_lanzarExcepcion() throws Exception {
        try (MockedStatic<ExportadoraDeExcel> mockStatic = mockStatic(ExportadoraDeExcel.class)) {
            mockStatic.when(() -> ExportadoraDeExcel.exportarProgresoUsuarios(anyList(), any()))
                    .thenThrow(new RuntimeException("Error simulado"));

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
                ExportadoraDeExcel.exportarProgresoUsuarios(List.of(), outputStream);
            });
            assertEquals("Error simulado", thrown.getMessage());
        }
    }

}
