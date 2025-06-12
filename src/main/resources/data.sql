-- Primero insertar las carreras
INSERT INTO Carrera(id, nombre, descripcion)
VALUES
    (1, 'Tecnicatura en Desarrollo Web', '');

-- Luego insertar los usuarios (ahora la carreraID = 1 existe)
INSERT INTO Usuario(id, email, password, rol, carreraID, nombre, apellido, telefono, situacionLaboral, disponibilidadHoraria, activo)
VALUES
    (1, 'user@user.com', '123', 'ALUMNO', 1, 'Juan', 'Pérez', NULL, 'desempleado', 1, false),
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
INSERT INTO usuario_materia(dificultad, fecha_asignacion, fecha_modificacion, nota, materia_id, usuario_id)
VALUES
    -- Cuatrimestre 1
    (1, '2022-03-01', '2024-07-15', 8, 1, 1),
    (5, '2022-03-01', '2024-07-15', 9,  2, 1),
    (10, '2022-03-01', '2024-07-15', 6,  3, 1),
    (1, '2022-03-01', '2024-07-15', 7,  4, 1),

    -- Cuatrimestre 2
  --  (5, '2023-08-01', '2024-12-15', 8, 'Buena evolución en programación', 5, 1),
   -- (5, '2023-08-01', '2024-12-15', 9, 'Excelente trabajo con tecnologías web', 6, 1),
    (10, '2023-08-01', '2024-12-15', 7,  7, 1),
    (10, '2023-08-01', '2024-12-15', 8,  8, 1),
    (1, '2023-08-01', '2024-12-15', 8,  9, 1),

    -- Cuatrimestre 3
    (10, '2024-03-01', '2025-07-15', 2,  10, 1),
    (1, '2024-03-01', '2025-07-15', 8,  11, 1),
    (5, '2024-05-01', '2025-07-15', 9,  12, 1),
    (10, '2025-03-01', '2025-07-15', 8,  13, 1);


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