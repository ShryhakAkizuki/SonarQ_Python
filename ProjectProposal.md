# Propuesta de Análisis de Vulnerabilidades en Python

## Título
La propuesta se denomina **Análisis de Vulnerabilidades en Python**.

## Descripción del proyecto
Esta propuesta plantea el desarrollo de una herramienta de análisis estático orientada a detectar vulnerabilidades en código fuente escrito en Python. Su finalidad es identificar de manera temprana patrones inseguros que puedan comprometer la confidencialidad, la integridad o la disponibilidad de una aplicación, antes de que el software llegue a entornos de prueba o producción.

La propuesta se centra en el análisis automático del código para localizar problemas frecuentes de seguridad, como credenciales expuestas, inyección SQL, uso inseguro de datos proporcionados por el usuario, configuraciones débiles o prácticas que aumentan el riesgo de explotación. Además de señalar la presencia de estos problemas, la herramienta busca explicar por qué representan un riesgo y qué alternativas más seguras podrían aplicarse.

Desde el punto de vista académico, la propuesta se apoya en los fundamentos de lenguajes de programación y compiladores, ya que parte de la idea de inspeccionar el código, reconocer estructuras relevantes y evaluar si esas estructuras coinciden con patrones vulnerables. Con ello, el proyecto no se limita a una revisión superficial, sino que busca simular el comportamiento de un analizador de seguridad capaz de apoyar tareas de prevención y mejora continua del código.

## Funcionalidades principales
- Detección de vulnerabilidades comunes en código Python.
- Identificación de credenciales, claves o secretos expuestos en el código fuente.
- Localización de patrones relacionados con inyección SQL y otros usos inseguros de datos.
- Generación de reportes claros con severidad, ubicación y descripción del riesgo.
- Apoyo a la revisión temprana del código antes de su despliegue.
- Enfoque extensible para incorporar nuevas reglas de análisis según crezcan las necesidades del proyecto.

## Proyectos similares existentes
La propuesta se relaciona con herramientas reales de análisis estático y seguridad de código que ya se utilizan en la industria:

- **SonarQube**: plataforma de análisis de calidad y seguridad que detecta errores, vulnerabilidades y malas prácticas en múltiples lenguajes.
- **Semgrep**: motor de análisis estático basado en reglas que permite buscar patrones de código inseguro de forma rápida y configurable.
- **CodeQL**: herramienta de análisis profundo de código usada para encontrar vulnerabilidades y flujos inseguros en grandes bases de código.
- **Bandit**: analizador estático centrado específicamente en Python, diseñado para detectar problemas de seguridad comunes.
- **PyLint**: herramienta de inspección estática para Python que ayuda a encontrar errores, inconsistencias y problemas de estilo.

Estas referencias permiten situar la propuesta dentro de un ecosistema real de herramientas de análisis de código, aunque con un enfoque académico centrado en la detección de vulnerabilidades en Python.