-- Primero insertar las carreras
INSERT INTO Carrera(id, nombre, descripcion)
VALUES
    (1, 'Tecnicatura en Desarrollo Web', '');

-- Luego insertar los usuarios (ahora la carreraID = 1 existe)
INSERT INTO Usuario(id, email, password, rol, carreraID, nombre, apellido, telefono, situacionLaboral, disponibilidadHoraria, activo)
VALUES
    (1, 'user@gmail.com', '12345678', 'ALUMNO', 1, 'Juan', 'Pérez', NULL, 'desempleado', 1, false),
    (2, 'user2@user.com', '123', 'ALUMNO', 1, 'Ana', 'Gómez', NULL, 'empleado', 2, false);

-- Insertar las materias del cuatrimestre 1 (sin correlativas)
INSERT INTO Materia(id, nombre, descripcion, tipo, carga_horaria, cuatrimestre, activa, carrera_id, correlativa_1, correlativa_2, correlativa_3, correlativa_4, correlativa_5, correlativa_6)
VALUES
    (1, 'Programación Básica I', '', 'Obligatoria', 8, 1, true, 1, NULL, NULL, NULL, NULL, NULL, NULL),
    (2, 'Informática General', '', 'Obligatoria', 4, 1, true, 1, NULL, NULL, NULL, NULL, NULL, NULL),
    (3, 'Matemática General', '', 'Obligatoria', 8, 1, true, 1, NULL, NULL, NULL, NULL, NULL, NULL),
    (4, 'Inglés Técnico I', '', 'Obligatoria', 4, 1, true, 1, NULL, NULL, NULL, NULL, NULL, NULL);

-- Insertar las materias del cuatrimestre 2 (con sus correlativas)
INSERT INTO Materia(id, nombre, descripcion, tipo, carga_horaria, cuatrimestre, activa, carrera_id, correlativa_1, correlativa_2, correlativa_3, correlativa_4, correlativa_5, correlativa_6)
VALUES
    (5, 'Programación Básica II', '', 'Obligatoria', 4, 2, true, 1, 1, NULL, NULL, NULL, NULL, NULL),
    (6, 'Programación Web I', '', 'Obligatoria', 4, 2, true, 1, 1, 2, NULL, NULL, NULL, NULL),
    (7, 'Base de Datos I', '', 'Obligatoria', 8, 2, true, 1, 1, 2, 3, NULL, NULL, NULL),
    (8, 'Introducción al Diseño Gráfico en la Web', '', 'Obligatoria', 4, 2, true, 1, 2, NULL, NULL, NULL, NULL, NULL),
    (9, 'Inglés Técnico II', '', 'Obligatoria', 4, 2, true, 1, 4, NULL, NULL, NULL, NULL, NULL);

-- Insertar las materias del cuatrimestre 3 (con sus correlativas)
INSERT INTO Materia(id, nombre, descripcion, tipo, carga_horaria, cuatrimestre, activa, carrera_id, correlativa_1, correlativa_2, correlativa_3, correlativa_4, correlativa_5, correlativa_6)
VALUES
    (10, 'Programación Web II', '', 'Obligatoria', 8, 3, true, 1, 5, 6, 7, NULL, NULL, NULL),
    (11, 'Diseño de Aplicaciones Web', '', 'Obligatoria', 8, 3, true, 1, 5, 6, 7, NULL, NULL, NULL),
    (12, 'Visualización e Interfaces', '', 'Obligatoria', 4, 3, true, 1, 6, 8, NULL, NULL, NULL, NULL),
    (13, 'Taller Web I', '', 'Obligatoria', 4, 3, true, 1, 5, 6, 7, NULL, NULL, NULL);

-- Insertar las materias del cuatrimestre 4 (con sus correlativas)
INSERT INTO Materia(id, nombre, descripcion, tipo, carga_horaria, cuatrimestre, activa, carrera_id, correlativa_1, correlativa_2, correlativa_3, correlativa_4, correlativa_5, correlativa_6)
VALUES
    (14, 'Base de Datos II', '', 'Obligatoria', 4, 4, true, 1, 7, 11, NULL, NULL, NULL, NULL),
    (15, 'Programación Web III', '', 'Obligatoria', 8, 4, true, 1, 10, 11, 12, NULL, NULL, NULL),
    (16, 'Tecnología de Redes', '', 'Obligatoria', 4, 4, true, 1, 12, NULL, NULL, NULL, NULL, NULL),
    (17, 'Taller Web II', '', 'Obligatoria', 4, 4, true, 1, 13, NULL, NULL, NULL, NULL, NULL),
    (18, 'Seguridad y Calidad en Aplicaciones Web', '', 'Obligatoria', 4, 4, true, 1, 10, 11, NULL, NULL, NULL, NULL);

-- Insertar las materias del cuatrimestre 5 (con sus correlativas)
INSERT INTO Materia(id, nombre, descripcion, tipo, carga_horaria, cuatrimestre, activa, carrera_id, correlativa_1, correlativa_2, correlativa_3, correlativa_4, correlativa_5, correlativa_6)
VALUES
    (19, 'Introducción a la Administración de Proyectos', '', 'Obligatoria', 4, 5, true, 1, 14, 15, 16, NULL, NULL, NULL),
    (20, 'Taller Práctico Integrador', '', 'Obligatoria', 8, 5, true, 1, 14, 15, 16, 17, 18, NULL);

-- Insertar relaciones usuario_materia para el primer usuario (Juan Pérez) - Primeros 3 cuatrimestres
INSERT INTO usuario_materia(dificultad, estado, fecha_asignacion, fecha_modificacion, nota, materia_id, usuario_id)
VALUES
    -- Cuatrimestre 1
    (1, 1,'2022-03-01', '2024-07-15', 8, 1, 1),
    (5, 1, '2022-03-01', '2024-07-15', 9,  2, 1),
    (10, 1, '2022-03-01', '2024-07-15', 6,  3, 1),
    (1, 1, '2022-03-01', '2024-07-15', 7,  4, 1),

    -- Cuatrimestre 2
  --  (5, '2023-08-01', '2024-12-15', 8, 'Buena evolución en programación', 5, 1),
   -- (5, '2023-08-01', '2024-12-15', 9, 'Excelente trabajo con tecnologías web', 6, 1),
    (10, 1, '2023-08-01', '2024-12-15', 7,  7, 1),
    (10, 1, '2023-08-01', '2024-12-15', 8,  8, 1),
    (1, 1, '2023-08-01', '2024-12-15', 8,  9, 1),

    -- Cuatrimestre 3
    (10,1, '2024-03-01', '2025-07-15', 2,  10, 1),
    (1,1, '2024-03-01', '2025-07-15', 8,  11, 1),
    (5,1, '2024-05-01', '2025-07-15', 9,  12, 1),
    (10,1, '2025-03-01', '2025-07-15', 8,  13, 1);



INSERT INTO Publicacion(usuario_id, materia_id, titulo, descripcion, fechaCreacion, likes)
VALUES
    (1, 1, 'Ayuda con estructuras condicionales', 'No me queda claro cuándo conviene usar if o switch. ¿Alguna recomendación o ejemplo claro?', '2025-06-01 14:30:00', 2);

-- Publicación 2: sobre Visualización e Interfaces (materia_id = 12)
INSERT INTO Publicacion(usuario_id, materia_id, titulo, descripcion, fechaCreacion, likes)
VALUES
    (1, 12, '¿Recomendaciones de herramientas para prototipado UI?', 'Estoy buscando herramientas para prototipar interfaces más rápido. ¿Qué usan ustedes? ¿Figma, Adobe XD, otra?', '2025-06-05 10:15:00', 5);

-- Asegurate de que PublicacionPublicacionpublicacion_id = 2 exista
INSERT INTO Comentario(usuario_id, descripcion, fechaCreacion, publicacion_id)
VALUES
    (2, 'Yo uso Figma y me resulta súper intuitivo, además tiene muchas plantillas. ¡Recomendado!', '2025-06-06 09:45:00', 2);

-- ============================================================================
-- EVENTOS - FIXED VERSION
-- ============================================================================

INSERT INTO evento(activo, completado, descripcion, fecha_creacion, fecha_modificacion, fecha_inicio, fecha_fin, tipo, titulo, ubicacion, materia_id, usuario_id)
VALUES
-- ============================================================================
-- EVENTOS PARA USUARIO 1 (Juan Pérez) - 10 eventos
-- ============================================================================
-- Academic Events
(true, false, 'Primer parcial de Programación Básica I. Temas: variables, estructuras de control, funciones básicas.', NOW(), NOW(), '2025-06-15 09:00:00', '2025-06-15 11:00:00', 'EXAMEN', 'Parcial Programación Básica I', 'Aula 101, Sede Central', 1, 1),
(true, false, 'Entrega del trabajo práctico sobre consultas SQL básicas. Incluir documentación y código.', NOW(), NOW(), '2025-06-18 23:59:00', NULL, 'TAREA', 'TP Consultas SQL - Base de Datos I', 'Campus Virtual', 7, 1),
(true, false, 'Sesión grupal de repaso para el final de Matemática General. Resolver ejercicios de límites y derivadas.', NOW(), NOW(), '2025-06-20 14:00:00', '2025-06-20 17:00:00', 'ESTUDIO', 'Estudio Grupal - Matemática Final', 'Biblioteca Central - Sala 3', 3, 1),
(true, true, 'Segunda clase de Programación Web II. Tema: React Hooks y manejo de estado.', NOW(), NOW(), '2025-06-12 10:00:00', '2025-06-12 13:00:00', 'CLASE', 'Clase React Hooks - Prog Web II', 'Laboratorio 2', 10, 1),
(true, false, 'Presentación oral del proyecto final de Diseño de Aplicaciones Web. 15 minutos por grupo.', NOW(), NOW(), '2025-06-25 16:00:00', '2025-06-25 18:00:00', 'EXAMEN', 'Presentación Proyecto Final', 'Aula Magna', 11, 1),
-- Personal Events
(true, false, 'Consulta médica anual de rutina. Llevar estudios previos y carnet de vacunación.', NOW(), NOW(), '2025-06-16 15:30:00', '2025-06-16 16:30:00', 'PERSONAL', 'Control Médico Anual', 'Hospital Italiano - Consultorio 205', NULL, 1),
(true, false, 'Entrevista laboral para puesto de desarrollador junior en startup tecnológica.', NOW(), NOW(), '2025-06-19 11:00:00', '2025-06-19 12:00:00', 'PERSONAL', 'Entrevista TechStart Solutions', 'Oficina Puerto Madero - Piso 12', NULL, 1),
(true, true, 'Sesión de ejercicios en el gimnasio. Rutina de fuerza: pecho, tríceps y hombros.', NOW(), NOW(), '2025-06-13 19:00:00', '2025-06-13 20:30:00', 'PERSONAL', 'Gimnasio - Rutina Fuerza', 'SportClub Caballito', NULL, 1),
(true, false, 'Reunión familiar para organizar cumpleaños de la abuela. Coordinar lugar, comida y regalos.', NOW(), NOW(), '2025-06-22 18:00:00', '2025-06-22 19:30:00', 'PERSONAL', 'Organizar Cumple Abuela', 'Casa de mamá', NULL, 1),
(true, false, 'Taller de programación avanzada en JavaScript. Temas: async/await, promises, APIs REST.', NOW(), NOW(), '2025-06-28 14:00:00', '2025-06-28 18:00:00', 'ESTUDIO', 'Workshop JavaScript Avanzado', 'CoderHouse - Sede Palermo', 6, 1),
-- ============================================================================
-- EVENTOS PARA USUARIO 2 (Ana Gómez) - 10 eventos
-- ============================================================================
-- Academic Events
(true, false, 'Examen final de Programación Básica I. Repaso completo de POO, herencia y polimorfismo.', NOW(), NOW(), '2025-06-17 14:00:00', '2025-06-17 17:00:00', 'EXAMEN', 'Final Programación Básica I', 'Aula 205, Sede Central', 1, 2),
(true, true, 'Clase práctica de Informática General: instalación de sistemas operativos y configuración de redes.', NOW(), NOW(), '2025-06-14 08:00:00', '2025-06-14 12:00:00', 'CLASE', 'Práctica SO y Redes', 'Laboratorio 1', 2, 2),
(true, false, 'Entrega individual del ensayo sobre "Impacto de la IA en el desarrollo web". Mínimo 2000 palabras.', NOW(), NOW(), '2025-06-21 20:00:00', NULL, 'TAREA', 'Ensayo IA en Desarrollo Web', 'Plataforma Moodle', 2, 2),
(true, false, 'Recuperatorio del primer parcial de Matemática General. Temas: funciones y límites.', NOW(), NOW(), '2025-06-24 10:00:00', '2025-06-24 12:00:00', 'EXAMEN', 'Recuperatorio Matemática', 'Aula 103', 3, 2),
(true, false, 'Sesión de estudio intensiva para Inglés Técnico I. Practicar vocabulario de programación.', NOW(), NOW(), '2025-06-26 16:00:00', '2025-06-26 18:00:00', 'ESTUDIO', 'Repaso Inglés Técnico', 'Biblioteca - Sala Idiomas', 4, 2),
-- Personal Events
(true, false, 'Reunión trimestral de trabajo. Revisión de objetivos y planificación del próximo período.', NOW(), NOW(), '2025-06-16 09:00:00',  '2025-06-16 11:00:00', 'PERSONAL', 'Reunión Trabajo - Q2 Review', 'Oficina Microcentro - Sala Juntas', NULL, 2),
(true, true, 'Cita con el dentista para limpieza dental y control de rutina. Recordar llevar radiografías.', NOW(), NOW(), '2025-06-13 14:00:00', '2025-06-13 15:00:00', 'PERSONAL', 'Dentista - Control Rutina', 'Clínica Odontológica San Martín', NULL, 2),
(true, false, 'Clase de yoga nivel intermedio. Enfocar en posturas de equilibrio y respiración consciente.', NOW(), NOW(), '2025-06-18 19:30:00', '2025-06-18 21:00:00', 'PERSONAL', 'Clase de Yoga', 'Centro Holístico Belgrano', NULL, 2),
(true, false, 'Cena con amigas de la facultad para ponernos al día y celebrar fin de parciales.', NOW(), NOW(), '2025-06-27 20:30:00', '2025-06-27 23:00:00', 'PERSONAL', 'Cena Amigas Facultad', 'Restaurante Las Violetas - Almagro', NULL, 2),
(true, false, 'Curso online de diseño UX/UI. Primera clase: fundamentos de experiencia de usuario.', NOW(), NOW(), '2025-06-29 10:00:00', '2025-06-29 13:00:00', 'ESTUDIO', 'Curso UX/UI - Clase 1', 'Online - Zoom', NULL, 2);