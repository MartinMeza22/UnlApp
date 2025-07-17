-- Primero insertar las carreras
INSERT INTO Carrera(id, nombre, descripcion)
VALUES
    (1, 'Tecnicatura en Desarrollo Web', '');

-- Luego insertar los usuarios (ahora la carreraID = 1 existe)
INSERT INTO Usuario(id, email, password, rol, carreraID, nombre, apellido, telefono, situacionLaboral, disponibilidadHoraria, activo)
VALUES
    (1, 'user@gmail.com', '12345678', 'ALUMNO', 1, 'Juan', 'Pérez', NULL, 'desempleado', 1, true),
    (2, 'user2@user.com', '123', 'ALUMNO', 1, 'Ana', 'Gómez', NULL, 'empleado', 2, true),
     (4, 'mmeza223@alumno.unlam.edu.ar', '12345678', 'ALUMNO', 1, 'Martín', 'Meza', NULL, 'empleado', 20, true),
    (5, 'quispeNadal@alumno.unlam.edu.ar', '12345678', 'ALUMNO', 1, 'Franco', 'Nadal', NULL, 'empleado', 40, true),
    (6, 'ramaGit@alumno.unlam.edu.ar', '12345678', 'ALUMNO', 1, 'Ramiro', 'Casanova', NULL, 'empleado', 15, true),
    (7, 'vitinha@alumno.unlam.edu.ar', '12345678', 'ALUMNO', 1, 'Tobias', 'Tejerina', NULL, 'empleado', 24, true),
    (8, 'agus@alumno.unlam.edu.ar', '12345678', 'ALUMNO', 1, 'Agustín', 'Cangelosi', NULL, 'empleado', 30, true),
    (9, 'aleSergi@alumno.unlam.edu.ar', '12345678', 'ALUMNO', 1, 'Sergio', 'Ramirez', NULL, 'empleado', 13, true);

-- se inserta un admin
INSERT INTO Usuario(id, email, password, rol, carreraID, nombre, apellido, activo, situacionLaboral)
VALUES
    (3, 'admin@unlapp.com', '123', 'ADMIN', 1, 'Admin', 'istrador', true, 'empleado');

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
    (1, 3,'2022-03-01', '2024-07-15', 8, 1, 1),
    (2, 3, '2022-03-01', '2024-07-15', 9,  2, 1),
    (3, 3, '2022-03-01', '2024-07-15', 6,  3, 1),
    (1, 3, '2022-03-01', '2024-07-15', 7,  4, 1),

    -- Cuatrimestre 2
  --  (5, '2023-08-01', '2024-12-15', 8, 'Buena evolución en programación', 5, 1),
   -- (5, '2023-08-01', '2024-12-15', 9, 'Excelente trabajo con tecnologías web', 6, 1),
    (2, 3, '2023-08-01', '2024-12-15', 7,  7, 1),
    (2, 3, '2023-08-01', '2024-12-15', 8,  8, 1),
    (1, 3, '2023-08-01', '2024-12-15', 8,  9, 1),

    -- Cuatrimestre 3
    (3,4, '2024-03-01', '2025-07-15', 2,  10, 1),
    (1,3, '2024-03-01', '2025-07-15', 8,  11, 1),
    (2,3, '2024-05-01', '2025-07-15', 9,  12, 1),
    (3,3, '2025-03-01', '2025-07-15', 8,  13, 1),
    (null,2, '2024-05-01', '2025-07-15', null,  14, 1),
    (null,2, '2025-03-01', '2025-07-15', null,  15, 1),


-- Martín
-- Cuatrimestre 1
     (3, 3,'2022-03-01', '2024-07-15', 8, 1, 4),
    (2, 3, '2022-03-01', '2024-07-15', 7,  2, 4),
    (2, 3, '2022-03-01', '2024-07-15', 9,  3, 4),
    (2, 3, '2022-03-01', '2024-07-15', 5,  4, 4),

    -- Cuatrimestre 2
    (2, 3, '2023-08-01', '2024-12-15', 9,  5, 4),
    (3, 3, '2023-08-01', '2024-12-15', 8,  6, 4),
    (2, 3, '2023-08-01', '2024-12-15', 8,  7, 4),
    (1, 3, '2023-08-01', '2024-12-15', 9,  8, 4),

    -- Cuatrimestre 3
    (3,3, '2024-03-01', '2025-07-15', 8,  10, 4),
    (2,3, '2024-03-01', '2025-07-15', 8,  11, 4),
    (3,3, '2024-03-01', '2025-07-15', 10,  12, 4),
    (NULL, 2, '2023-08-01', '2024-12-15', NULL,  13, 4),
    (3, 4, '2023-08-01', '2024-12-15', 3,  16, 4),
    (NULL,2, '2024-05-01', '2025-07-15', NULL,  18, 4);



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
--     (10, '2024-03-01', '2025-07-15', 2,  10, 1),
--     (1, '2024-03-01', '2025-07-15', 8,  11, 1),
--     (5, '2024-05-01', '2025-07-15', 9,  12, 1),
--     (10, '2025-03-01', '2025-07-15', 8,  13, 1);


-- *************************************************************************************************
-- *** INSERTS ADICIONALES A PARTIR DE AQUÍ (Nuevas Carreras y Materias) ***
-- *************************************************************************************************

-- Nuevas Inserciones para la tabla Carrera
INSERT INTO Carrera(id, nombre, descripcion)
VALUES
    (2, 'Licenciatura en Informática', 'Carrera universitaria con foco en ciencias de la computación y desarrollo de sistemas complejos.'),
    (3, 'Tecnicatura en Marketing Digital', 'Formación técnica para profesionales en estrategias y herramientas de marketing online.'),
    (4, 'Licenciatura en Abogacía', 'Estudio profundo de las leyes y el sistema jurídico para ejercer la profesión legal.');


-- #################################################################################################
-- ### Materias para 'Licenciatura en Informática' (carrera_id = 2) - Aprox. 30 Materias
-- #################################################################################################

-- Cuatrimestre 1
INSERT INTO Materia(id, nombre, descripcion, tipo, carga_horaria, cuatrimestre, activa, carrera_id, correlativa_1, correlativa_2, correlativa_3, correlativa_4, correlativa_5, correlativa_6)
VALUES
    (21, 'Programación Inicial', 'Conceptos fundamentales de programación estructurada.', 'Obligatoria', 8, 1, true, 2, NULL, NULL, NULL, NULL, NULL, NULL),
    (22, 'Cálculo I', 'Introducción a la matemática superior y el análisis.', 'Obligatoria', 6, 1, true, 2, NULL, NULL, NULL, NULL, NULL, NULL),
    (23, 'Álgebra y Geometría Analítica', 'Estudio de estructuras algebraicas y sistemas de coordenadas.', 'Obligatoria', 6, 1, true, 2, NULL, NULL, NULL, NULL, NULL, NULL),
    (24, 'Lógica y Teoría de Conjuntos', 'Fundamentos lógicos para la computación.', 'Obligatoria', 4, 1, true, 2, NULL, NULL, NULL, NULL, NULL, NULL),
    (25, 'Introducción a la Ciencia de la Computación', 'Visión general de los campos de la informática.', 'Obligatoria', 4, 1, true, 2, NULL, NULL, NULL, NULL, NULL, NULL);

-- Cuatrimestre 2
INSERT INTO Materia(id, nombre, descripcion, tipo, carga_horaria, cuatrimestre, activa, carrera_id, correlativa_1, correlativa_2, correlativa_3, correlativa_4, correlativa_5, correlativa_6)
VALUES
    (26, 'Algoritmos y Estructuras de Datos I', 'Diseño y análisis de algoritmos eficientes.', 'Obligatoria', 8, 2, true, 2, '21', '24', NULL, NULL, NULL, NULL), -- Correlativa: Programación Inicial, Lógica
    (27, 'Cálculo II', 'Continuación de cálculo con series y ecuaciones.', 'Obligatoria', 6, 2, true, 2, '22', NULL, NULL, NULL, NULL, NULL), -- Correlativa: Cálculo I
    (28, 'Física para Informática', 'Conceptos de física aplicados a la computación.', 'Obligatoria', 6, 2, true, 2, '23', NULL, NULL, NULL, NULL, NULL), -- Correlativa: Álgebra
    (29, 'Organización de Computadoras', 'Arquitectura y funcionamiento interno de las computadoras.', 'Obligatoria', 4, 2, true, 2, '25', NULL, NULL, NULL, NULL, NULL), -- Correlativa: Intro Ciencia Computación
    (30, 'Probabilidad y Estadística', 'Análisis de datos e incertidumbre.', 'Obligatoria', 6, 2, true, 2, '22', NULL, NULL, NULL, NULL, NULL); -- Correlativa: Cálculo I

-- Cuatrimestre 3
INSERT INTO Materia(id, nombre, descripcion, tipo, carga_horaria, cuatrimestre, activa, carrera_id, correlativa_1, correlativa_2, correlativa_3, correlativa_4, correlativa_5, correlativa_6)
VALUES
    (31, 'Programación Orientada a Objetos', 'Paradigma de programación basado en objetos.', 'Obligatoria', 8, 3, true, 2, '26', NULL, NULL, NULL, NULL, NULL), -- Correlativa: Algoritmos y Estructuras de Datos I
    (32, 'Ecuaciones Diferenciales', 'Resolución de problemas con tasas de cambio.', 'Obligatoria', 6, 3, true, 2, '27', NULL, NULL, NULL, NULL, NULL), -- Correlativa: Cálculo II
    (33, 'Sistemas Digitales y Circuitos', 'Diseño de circuitos lógicos.', 'Obligatoria', 6, 3, true, 2, '29', NULL, NULL, NULL, NULL, NULL), -- Correlativa: Organización de Computadoras
    (34, 'Bases de Datos II', 'Diseño y administración avanzada de bases de datos.', 'Obligatoria', 8, 3, true, 2, '26', NULL, NULL, NULL, NULL, NULL), -- Correlativa: Algoritmos y Estructuras de Datos I
    (35, 'Teoría de Lenguajes y Autómatas', 'Fundamentos teóricos de los lenguajes de programación.', 'Obligatoria', 4, 3, true, 2, '24', NULL, NULL, NULL, NULL, NULL); -- Correlativa: Lógica y Teoría de Conjuntos

-- Cuatrimestre 4
INSERT INTO Materia(id, nombre, descripcion, tipo, carga_horaria, cuatrimestre, activa, carrera_id, correlativa_1, correlativa_2, correlativa_3, correlativa_4, correlativa_5, correlativa_6)
VALUES
    (36, 'Sistemas Operativos', 'Funcionamiento y gestión de sistemas operativos.', 'Obligatoria', 8, 4, true, 2, '31', '33', NULL, NULL, NULL, NULL), -- Correlativa: POO, Sistemas Digitales
    (37, 'Redes de Computadoras', 'Principios y protocolos de comunicación en red.', 'Obligatoria', 6, 4, true, 2, '33', NULL, NULL, NULL, NULL, NULL), -- Correlativa: Sistemas Digitales
    (38, 'Ingeniería de Software I', 'Metodologías para el desarrollo de software.', 'Obligatoria', 8, 4, true, 2, '31', '34', NULL, NULL, NULL, NULL), -- Correlativa: POO, Bases de Datos II
    (39, 'Inteligencia Artificial I', 'Introducción a los conceptos y técnicas de IA.', 'Obligatoria', 6, 4, true, 2, '31', '26', NULL, NULL, NULL, NULL), -- Correlativa: POO, Algoritmos y Estructuras de Datos I
    (40, 'Compiladores', 'Principios de construcción de traductores de lenguajes.', 'Obligatoria', 6, 4, true, 2, '35', '26', NULL, NULL, NULL, NULL); -- Correlativa: Teoría de Lenguajes, Algoritmos

-- Cuatrimestre 5
INSERT INTO Materia(id, nombre, descripcion, tipo, carga_horaria, cuatrimestre, activa, carrera_id, correlativa_1, correlativa_2, correlativa_3, correlativa_4, correlativa_5, correlativa_6)
VALUES
    (41, 'Sistemas Distribuidos', 'Diseño y gestión de sistemas con múltiples componentes.', 'Obligatoria', 6, 5, true, 2, '36', '37', NULL, NULL, NULL, NULL), -- Correlativa: SO, Redes
    (42, 'Seguridad Informática', 'Protección de sistemas y datos.', 'Obligatoria', 6, 5, true, 2, '37', NULL, NULL, NULL, NULL, NULL), -- Correlativa: Redes de Computadoras
    (43, 'Desarrollo de Aplicaciones Web', 'Tecnologías para la construcción de aplicaciones web.', 'Obligatoria', 8, 5, true, 2, '38', '34', NULL, NULL, NULL, NULL), -- Correlativa: Ing. Software I, Bases de Datos II
    (44, 'Aprendizaje Automático', 'Algoritmos y modelos para el aprendizaje de máquinas.', 'Obligatoria', 8, 5, true, 2, '39', '30', NULL, NULL, NULL, NULL), -- Correlativa: IA I, Probabilidad y Estadística
    (45, 'Simulación y Modelado', 'Creación de modelos y simulación de sistemas.', 'Obligatoria', 4, 5, true, 2, '32', '30', NULL, NULL, NULL, NULL); -- Correlativa: Ecuaciones Diferenciales, Prob. y Est.

-- Cuatrimestre 6
INSERT INTO Materia(id, nombre, descripcion, tipo, carga_horaria, cuatrimestre, activa, carrera_id, correlativa_1, correlativa_2, correlativa_3, correlativa_4, correlativa_5, correlativa_6)
VALUES
    (46, 'Tesis de Licenciatura', 'Proyecto final de investigación o desarrollo.', 'Obligatoria', 12, 6, true, 2, '41', '42', '43', '44', NULL, NULL), -- Correlativas clave de los últimos cuatrimestres
    (47, 'Arquitectura de Software', 'Diseño de la estructura de sistemas complejos.', 'Obligatoria', 6, 6, true, 2, '38', NULL, NULL, NULL, NULL, NULL), -- Correlativa: Ingeniería de Software I
    (48, 'Computación Gráfica', 'Fundamentos y técnicas de la generación de imágenes.', 'Obligatoria', 6, 6, true, 2, '26', '23', NULL, NULL, NULL, NULL), -- Correlativa: Algoritmos, Álgebra
    (49, 'Bioinformática', 'Aplicación de la informática a problemas biológicos.', 'Optativa', 4, 6, true, 2, '44', NULL, NULL, NULL, NULL, NULL), -- Correlativa: Aprendizaje Automático
    (50, 'Ética y Responsabilidad Social de la Computación', 'Aspectos morales y sociales de la tecnología.', 'Obligatoria', 4, 6, true, 2, NULL, NULL, NULL, NULL, NULL, NULL); -- No correlativa directa


-- #################################################################################################
-- ### Materias para 'Tecnicatura en Marketing Digital' (carrera_id = 3) - Aprox. 18 Materias
-- #################################################################################################

-- Cuatrimestre 1
INSERT INTO Materia(id, nombre, descripcion, tipo, carga_horaria, cuatrimestre, activa, carrera_id, correlativa_1, correlativa_2, correlativa_3, correlativa_4, correlativa_5, correlativa_6)
VALUES
    (51, 'Fundamentos del Marketing Digital', 'Introducción a las bases del marketing en entornos digitales.', 'Obligatoria', 6, 1, true, 3, NULL, NULL, NULL, NULL, NULL, NULL),
    (52, 'Comunicación Estratégica Digital', 'Desarrollo de mensajes efectivos para plataformas digitales.', 'Obligatoria', 4, 1, true, 3, NULL, NULL, NULL, NULL, NULL, NULL),
    (53, 'Introducción al E-commerce', 'Principios y modelos de negocio en el comercio electrónico.', 'Obligatoria', 4, 1, true, 3, NULL, NULL, NULL, NULL, NULL, NULL);

-- Cuatrimestre 2
INSERT INTO Materia(id, nombre, descripcion, tipo, carga_horaria, cuatrimestre, activa, carrera_id, correlativa_1, correlativa_2, correlativa_3, correlativa_4, correlativa_5, correlativa_6)
VALUES
    (54, 'Optimización para Buscadores (SEO)', 'Técnicas para mejorar el posicionamiento orgánico en buscadores.', 'Obligatoria', 6, 2, true, 3, '51', NULL, NULL, NULL, NULL, NULL), -- Correlativa: Fundamentos del Marketing Digital
    (55, 'Publicidad en Motores de Búsqueda (SEM)', 'Creación y gestión de campañas de anuncios pagados.', 'Obligatoria', 6, 2, true, 3, '51', NULL, NULL, NULL, NULL, NULL), -- Correlativa: Fundamentos del Marketing Digital
    (56, 'Analítica Web y Métricas', 'Medición y análisis de datos para optimizar campañas digitales.', 'Obligatoria', 6, 2, true, 3, '51', NULL, NULL, NULL, NULL, NULL), -- Correlativa: Fundamentos del Marketing Digital
    (57, 'Diseño Básico para Marketing', 'Herramientas y principios de diseño aplicados a piezas de marketing.', 'Obligatoria', 4, 2, true, 3, NULL, NULL, NULL, NULL, NULL, NULL);

-- Cuatrimestre 3
INSERT INTO Materia(id, nombre, descripcion, tipo, carga_horaria, cuatrimestre, activa, carrera_id, correlativa_1, correlativa_2, correlativa_3, correlativa_4, correlativa_5, correlativa_6)
VALUES
    (58, 'Marketing de Contenidos y Storytelling', 'Estrategias de creación y distribución de contenido relevante.', 'Obligatoria', 6, 3, true, 3, '52', NULL, NULL, NULL, NULL, NULL), -- Correlativa: Comunicación Estratégica Digital
    (59, 'Publicidad en Redes Sociales', 'Gestión de campañas publicitarias en plataformas sociales.', 'Obligatoria', 6, 3, true, 3, '54', '55', NULL, NULL, NULL, NULL), -- Correlativa: SEO, SEM
    (60, 'Email Marketing y Automatización', 'Creación de campañas de email y flujos automatizados.', 'Obligatoria', 4, 3, true, 3, '56', NULL, NULL, NULL, NULL, NULL), -- Correlativa: Analítica Web y Métricas
    (61, 'Gestión de Comunidades Online', 'Administración de la presencia y reputación de marca en línea.', 'Obligatoria', 4, 3, true, 3, '52', NULL, NULL, NULL, NULL, NULL); -- Correlativa: Comunicación Estratégica Digital

-- Cuatrimestre 4
INSERT INTO Materia(id, nombre, descripcion, tipo, carga_horaria, cuatrimestre, activa, carrera_id, correlativa_1, correlativa_2, correlativa_3, correlativa_4, correlativa_5, correlativa_6)
VALUES
    (62, 'Estrategias Avanzadas de E-commerce', 'Optimización y crecimiento de tiendas online.', 'Obligatoria', 6, 4, true, 3, '53', '59', NULL, NULL, NULL, NULL), -- Correlativa: Intro E-commerce, Publicidad Redes Sociales
    (63, 'Inbound Marketing', 'Metodologías para atraer, convertir, cerrar y deleitar clientes.', 'Obligatoria', 6, 4, true, 3, '58', '60', NULL, NULL, NULL, NULL), -- Correlativa: Contenido, Email Marketing
    (64, 'Experiencia de Usuario (UX) para Marketing', 'Principios para mejorar la interacción del usuario en plataformas digitales.', 'Obligatoria', 4, 4, true, 3, '57', '56', NULL, NULL, NULL, NULL), -- Correlativa: Diseño Básico, Analítica Web
    (65, 'Proyecto Integrador de Marketing Digital', 'Aplicación de conocimientos en un proyecto real.', 'Obligatoria', 8, 4, true, 3, '62', '63', '64', NULL, NULL, NULL); -- Correlativas de varias avanzadas

-- #################################################################################################
-- ### Materias para 'Licenciatura en Abogacía' (carrera_id = 4) - Aprox. 38 Materias
-- #################################################################################################

-- Cuatrimestre 1
INSERT INTO Materia(id, nombre, descripcion, tipo, carga_horaria, cuatrimestre, activa, carrera_id, correlativa_1, correlativa_2, correlativa_3, correlativa_4, correlativa_5, correlativa_6)
VALUES
    (71, 'Introducción al Derecho', 'Conceptos fundamentales del sistema jurídico.', 'Obligatoria', 6, 1, true, 4, NULL, NULL, NULL, NULL, NULL, NULL),
    (72, 'Teoría del Estado', 'Estudio de la organización política y sus instituciones.', 'Obligatoria', 4, 1, true, 4, NULL, NULL, NULL, NULL, NULL, NULL),
    (73, 'Derecho Romano', 'Orígenes y evolución del derecho civil.', 'Obligatoria', 6, 1, true, 4, NULL, NULL, NULL, NULL, NULL, NULL),
    (74, 'Sociología Jurídica', 'Relación entre el derecho y la sociedad.', 'Obligatoria', 4, 1, true, 4, NULL, NULL, NULL, NULL, NULL, NULL);

-- Cuatrimestre 2
INSERT INTO Materia(id, nombre, descripcion, tipo, carga_horaria, cuatrimestre, activa, carrera_id, correlativa_1, correlativa_2, correlativa_3, correlativa_4, correlativa_5, correlativa_6)
VALUES
    (75, 'Derecho Constitucional I', 'Principios y estructura del derecho constitucional.', 'Obligatoria', 6, 2, true, 4, '71', '72', NULL, NULL, NULL, NULL), -- Correlativa: Intro al Derecho, Teoría del Estado
    (76, 'Derecho Civil Parte General', 'Normas sobre personas, bienes y hechos jurídicos.', 'Obligatoria', 8, 2, true, 4, '71', NULL, NULL, NULL, NULL, NULL), -- Correlativa: Introducción al Derecho
    (77, 'Historia del Derecho Argentino', 'Desarrollo histórico del ordenamiento jurídico nacional.', 'Obligatoria', 6, 2, true, 4, '73', NULL, NULL, NULL, NULL, NULL), -- Correlativa: Derecho Romano
    (78, 'Metodología de la Investigación Jurídica', 'Técnicas y procesos de investigación en derecho.', 'Obligatoria', 4, 2, true, 4, '71', NULL, NULL, NULL, NULL, NULL); -- Correlativa: Introducción al Derecho

-- Cuatrimestre 3
INSERT INTO Materia(id, nombre, descripcion, tipo, carga_horaria, cuatrimestre, activa, carrera_id, correlativa_1, correlativa_2, correlativa_3, correlativa_4, correlativa_5, correlativa_6)
VALUES
    (79, 'Derecho Constitucional II', 'Organización de poderes y garantías constitucionales.', 'Obligatoria', 6, 3, true, 4, '75', NULL, NULL, NULL, NULL, NULL), -- Correlativa: Derecho Constitucional I
    (80, 'Derecho Penal Parte General I', 'Teoría del delito y la pena.', 'Obligatoria', 8, 3, true, 4, '71', NULL, NULL, NULL, NULL, NULL), -- Correlativa: Introducción al Derecho
    (81, 'Obligaciones Civiles y Comerciales', 'Fuentes y efectos de las obligaciones.', 'Obligatoria', 8, 3, true, 4, '76', NULL, NULL, NULL, NULL, NULL), -- Correlativa: Derecho Civil Parte General
    (82, 'Filosofía del Derecho', 'Análisis crítico de los fundamentos del derecho.', 'Obligatoria', 4, 3, true, 4, '74', NULL, NULL, NULL, NULL, NULL); -- Correlativa: Sociología Jurídica

-- Cuatrimestre 4
INSERT INTO Materia(id, nombre, descripcion, tipo, carga_horaria, cuatrimestre, activa, carrera_id, correlativa_1, correlativa_2, correlativa_3, correlativa_4, correlativa_5, correlativa_6)
VALUES
    (83, 'Contratos Civiles y Comerciales', 'Regulación de los acuerdos entre partes.', 'Obligatoria', 8, 4, true, 4, '81', NULL, NULL, NULL, NULL, NULL), -- Correlativa: Obligaciones
    (84, 'Derecho Penal Parte Especial I', 'Delitos contra las personas.', 'Obligatoria', 8, 4, true, 4, '80', NULL, NULL, NULL, NULL, NULL), -- Correlativa: Derecho Penal Parte General I
    (85, 'Derechos Reales', 'Estudio de los derechos sobre las cosas.', 'Obligatoria', 6, 4, true, 4, '81', NULL, NULL, NULL, NULL, NULL), -- Correlativa: Obligaciones
    (86, 'Derecho Administrativo I', 'Organización y funcionamiento de la administración pública.', 'Obligatoria', 6, 4, true, 4, '79', NULL, NULL, NULL, NULL, NULL); -- Correlativa: Derecho Constitucional II

-- Cuatrimestre 5
INSERT INTO Materia(id, nombre, descripcion, tipo, carga_horaria, cuatrimestre, activa, carrera_id, correlativa_1, correlativa_2, correlativa_3, correlativa_4, correlativa_5, correlativa_6)
VALUES
    (87, 'Derecho de Familia y Sucesiones I', 'Regulación del matrimonio, parentesco y herencias.', 'Obligatoria', 8, 5, true, 4, '83', NULL, NULL, NULL, NULL, NULL), -- Correlativa: Contratos
    (88, 'Derecho Laboral y de la Seguridad Social I', 'Relaciones individuales de trabajo.', 'Obligatoria', 8, 5, true, 4, '83', NULL, NULL, NULL, NULL, NULL), -- Correlativa: Contratos
    (89, 'Derecho Procesal Civil y Comercial I', 'Procedimientos judiciales en materia civil y comercial.', 'Obligatoria', 8, 5, true, 4, '85', NULL, NULL, NULL, NULL, NULL), -- Correlativa: Derechos Reales
    (90, 'Derecho Internacional Público', 'Normas que rigen las relaciones entre Estados.', 'Obligatoria', 6, 5, true, 4, '79', NULL, NULL, NULL, NULL, NULL); -- Correlativa: Derecho Constitucional II

-- Cuatrimestre 6
INSERT INTO Materia(id, nombre, descripcion, tipo, carga_horaria, cuatrimestre, activa, carrera_id, correlativa_1, correlativa_2, correlativa_3, correlativa_4, correlativa_5, correlativa_6)
VALUES
    (91, 'Derecho de Familia y Sucesiones II', 'Aspectos avanzados de familia y herencias.', 'Obligatoria', 6, 6, true, 4, '87', NULL, NULL, NULL, NULL, NULL), -- Correlativa: Derecho de Familia y Sucesiones I
    (92, 'Derecho Laboral y de la Seguridad Social II', 'Relaciones colectivas de trabajo y seguridad social.', 'Obligatoria', 6, 6, true, 4, '88', NULL, NULL, NULL, NULL, NULL), -- Correlativa: Derecho Laboral y de la Seguridad Social I
    (93, 'Derecho Procesal Penal', 'Procedimientos en materia penal.', 'Obligatoria', 8, 6, true, 4, '84', NULL, NULL, NULL, NULL, NULL), -- Correlativa: Derecho Penal Parte Especial I
    (94, 'Derecho Financiero y Tributario', 'Regulación de los ingresos y gastos del Estado.', 'Obligatoria', 6, 6, true, 4, '86', NULL, NULL, NULL, NULL, NULL); -- Correlativa: Derecho Administrativo I

-- Cuatrimestre 7
INSERT INTO Materia(id, nombre, descripcion, tipo, carga_horaria, cuatrimestre, activa, carrera_id, correlativa_1, correlativa_2, correlativa_3, correlativa_4, correlativa_5, correlativa_6)
VALUES
    (95, 'Derecho Comercial', 'Regulación de las actividades comerciales.', 'Obligatoria', 8, 7, true, 4, '83', NULL, NULL, NULL, NULL, NULL), -- Correlativa: Contratos
    (96, 'Derecho del Consumidor', 'Protección de los derechos de los consumidores.', 'Obligatoria', 6, 7, true, 4, '95', NULL, NULL, NULL, NULL, NULL), -- Correlativa: Derecho Comercial
    (97, 'Derecho Ambiental', 'Normativa sobre la protección del medio ambiente.', 'Obligatoria', 4, 7, true, 4, '86', NULL, NULL, NULL, NULL, NULL), -- Correlativa: Derecho Administrativo I
    (98, 'Derecho Internacional Privado', 'Resolución de conflictos de leyes en el espacio.', 'Obligatoria', 6, 7, true, 4, '90', NULL, NULL, NULL, NULL, NULL); -- Correlativa: Derecho Internacional Público

-- Cuatrimestre 8
INSERT INTO Materia(id, nombre, descripcion, tipo, carga_horaria, cuatrimestre, activa, carrera_id, correlativa_1, correlativa_2, correlativa_3, correlativa_4, correlativa_5, correlativa_6)
VALUES
    (99, 'Clínica Procesal Civil', 'Casos prácticos de procedimiento civil.', 'Obligatoria', 8, 8, true, 4, '89', NULL, NULL, NULL, NULL, NULL), -- Correlativa: Derecho Procesal Civil y Comercial I
    (100, 'Clínica Procesal Penal', 'Casos prácticos de procedimiento penal.', 'Obligatoria', 8, 8, true, 4, '93', NULL, NULL, NULL, NULL, NULL), -- Correlativa: Derecho Procesal Penal
    (101, 'Derecho Informático', 'Regulación legal de las tecnologías de la información.', 'Obligatoria', 4, 8, true, 4, '71', NULL, NULL, NULL, NULL, NULL), -- Correlativa: Introducción al Derecho
    (102, 'Seminario de Derecho Comparado', 'Análisis de sistemas jurídicos de diferentes países.', 'Optativa', 4, 8, true, 4, '98', NULL, NULL, NULL, NULL, NULL); -- Correlativa: Derecho Internacional Privado

-- Cuatrimestre 9
INSERT INTO Materia(id, nombre, descripcion, tipo, carga_horaria, cuatrimestre, activa, carrera_id, correlativa_1, correlativa_2, correlativa_3, correlativa_4, correlativa_5, correlativa_6)
VALUES
    (103, 'Práctica Profesional Supervisada I', 'Primeras experiencias en el ejercicio de la abogacía.', 'Obligatoria', 12, 9, true, 4, '99', '100', NULL, NULL, NULL, NULL), -- Correlativa: Clínicas Procesales
    (104, 'Derecho de la Propiedad Intelectual', 'Regulación de creaciones de la mente.', 'Obligatoria', 6, 9, true, 4, '95', NULL, NULL, NULL, NULL, NULL), -- Correlativa: Derecho Comercial
    (105, 'Ética Profesional del Abogado', 'Principios éticos en el ejercicio de la profesión.', 'Obligatoria', 4, 9, true, 4, '71', NULL, NULL, NULL, NULL, NULL); -- Correlativa: Introducción al Derecho
-- Datos para apreciar los graficos
-- Usuarios de Licenciatura en Informática (carreraID = 2)
INSERT INTO Usuario(id, email, password, rol, carreraID, nombre, apellido, telefono, situacionLaboral, disponibilidadHoraria, activo) VALUES
                                                                                                                                          (10, 'lucas.inf@unlam.edu.ar', '123', 'ALUMNO', 2, 'Lucas', 'Fernández', NULL, 'desempleado', 10, true),
                                                                                                                                          (11, 'paula.inf@unlam.edu.ar', '123', 'ALUMNO', 2, 'Paula', 'Silva', NULL, 'desempleado', 25, true);

-- Usuarios de Tec. en Marketing Digital (carreraID = 3)
INSERT INTO Usuario(id, email, password, rol, carreraID, nombre, apellido, telefono, situacionLaboral, disponibilidadHoraria, activo) VALUES
                                                                                                                                          (12, 'rodrigo.mark@unlam.edu.ar', '123', 'ALUMNO', 3, 'Rodrigo', 'Paz', NULL, 'empleado', 20, true),
                                                                                                                                          (13, 'carla.mark@unlam.edu.ar', '123', 'ALUMNO', 3, 'Carla', 'Méndez', NULL, 'desempleado', 35, true);

-- Usuarios de Lic. en Abogacía (carreraID = 4)
INSERT INTO Usuario(id, email, password, rol, carreraID, nombre, apellido, telefono, situacionLaboral, disponibilidadHoraria, activo) VALUES
                                                                                                                                          (14, 'martina.abo@unlam.edu.ar', '123', 'ALUMNO', 4, 'Martina', 'Soto', NULL, 'empleado', 15, true),
                                                                                                                                          (15, 'jose.abo@unlam.edu.ar', '123', 'ALUMNO', 4, 'José', 'Alvarez', NULL, 'empleado', 30, true);
-- Publicaciones de carreraID = 2 (Informática)
INSERT INTO Publicacion(usuario_id, materia_id, titulo, descripcion, fechaCreacion, likes) VALUES
                                                                                               (10, 28, 'Problemas con listas en Java', '¿Cómo puedo implementar una lista enlazada desde cero?', '2025-06-10 15:00:00', 4),
                                                                                               (11, 29, '¿Qué herramientas recomiendan para análisis de vulnerabilidades?', 'Estoy explorando opciones para escaneo de seguridad web.', '2025-06-12 11:30:00', 3);

-- Publicaciones de carreraID = 3 (Marketing Digital)
INSERT INTO Publicacion(usuario_id, materia_id, titulo, descripcion, fechaCreacion, likes) VALUES
                                                                                               (12, 47, '¿Qué KPIs usan para medir engagement?', 'Estoy armando un informe y necesito buenas métricas.', '2025-06-14 16:45:00', 2),
                                                                                               (13, 48, '¿SEM o SEO para un e-commerce nuevo?', 'Busco estrategias para mejorar el tráfico desde cero.', '2025-06-18 14:20:00', 5);

-- Publicaciones de carreraID = 4 (Abogacía)
INSERT INTO Publicacion(usuario_id, materia_id, titulo, descripcion, fechaCreacion, likes) VALUES
                                                                                               (14, 87, 'Diferencia entre dolo y culpa', 'Estoy confundida con los conceptos. ¿Alguien me lo explica fácil?', '2025-06-20 10:00:00', 4),
                                                                                               (15, 88, 'Casos famosos de Derecho Civil', '¿Conocen algún caso interesante que se haya dado en Argentina?', '2025-06-22 13:40:00', 3);
