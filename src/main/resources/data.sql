-- Primero insertar las carreras
INSERT INTO Carrera(id, nombre, descripcion)
VALUES
    (1, 'Tecnicatura en Desarrollo Web', '');

-- Luego insertar los usuarios (ahora la carreraID = 1 existe)
INSERT INTO Usuario(id, email, password, rol, carreraID, nombre, apellido, telefono, situacionLaboral, disponibilidadHoraria, activo)
VALUES
    (1, 'user@uni.edu', '123', 'ALUMNO', 1, 'Juan', 'Pérez', NULL, 'desempleado', 1, false),
    (2, 'user1@uni.edu', '123', 'ALUMNO', 1, 'Ana', 'Gómez', NULL, 'empleado', 2, false);

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