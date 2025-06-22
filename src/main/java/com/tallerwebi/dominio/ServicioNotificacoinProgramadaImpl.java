package com.tallerwebi.dominio;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ServicioNotificacoinProgramadaImpl implements ServicioNotificacionProgramada {

    private RepositorioEvento repositorioEvento;
    private ServicioEmail servicioEmail;

    public ServicioNotificacoinProgramadaImpl(RepositorioEvento repositorioEvento, ServicioEmail servicioEmail) {
        this.repositorioEvento = repositorioEvento;
        this.servicioEmail = servicioEmail;
    }

    @Scheduled(cron = "0 0 * * * *")
    @Transactional
    @Override
    public void verificarEventosParaNotificar() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime minNotificationTime = now.plusHours(23);
        LocalDateTime maxNotificationTime = now.plusHours(25);

        List<Evento> eventosPendientes = repositorioEvento.buscarEventosParaNotificar(minNotificationTime, maxNotificationTime);

        if (eventosPendientes != null && !eventosPendientes.isEmpty()) {
            System.out.println("Eventos encontrados para notificar: " + eventosPendientes.size());
            for (Evento evento : eventosPendientes) {
                try {
                    if (evento.getNotificarRecordatorio() != null && evento.getNotificarRecordatorio() && !evento.getYaNotificado()) {
                        Usuario usuario = evento.getUsuario();
                        Materia materia = evento.getMateria();

                        if (usuario != null && materia != null) {
                            String subject = "Recordatorio: " + evento.getTitulo();
                            String body = "Hola " + usuario.getNombre() + ",\n" +
                                    "\n" +
                                    "Te recordamos que el evento **" + evento.getTitulo() + "** se llevará a cabo el día " + evento.getFechaInicio().toLocalDate() +
                                    " a las " + evento.getFechaInicio().toLocalTime() + " hs." +
                                    ", como parte de la cursada de " + materia.getNombre() + ".\n" +
                                    "Esperamos contar con tu participación. ¡Nos vemos pronto!\n" +
                                    "\n" +
                                    "Saludos, el equipo de UnlApp";

                            servicioEmail.enviarEmailAUsuario(usuario.getEmail(), subject, body);

                            evento.setYaNotificado(true);
                            repositorioEvento.actualizar(evento);
                        }

                    }
                } catch (Exception e) {
                    System.err.println("Error al enviar notificación para evento " + evento.getId() + ": " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }
}
