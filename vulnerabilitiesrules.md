# Reglas de análisis de vulnerabilidades para el proyecto

Este documento resume las reglas que ya están implementadas en el proyecto y propone 20 reglas adicionales que encajan con la propuesta de **Análisis de Vulnerabilidades en Python** descrita en [ProjectProposal.md](ProjectProposal.md).

## Reglas ya implementadas

1. **HardcodedCredentials**: detecta credenciales, claves, tokens o secretos escritos directamente en asignaciones, por ejemplo `password = "..."` o `api_key: str = "..."`.
2. **CyclomaticComplexity**: analiza la complejidad ciclomática de funciones y lambdas, y también avisa cuando una función tiene demasiados parámetros. Aunque no es una vulnerabilidad directa, sí ayuda a detectar código difícil de mantener y revisar.

## Reglas que se podrían implementar

1. **SQLInjectionConcat**: detecta consultas SQL construidas por concatenación, interpolación o formateo de cadenas antes de enviarlas al motor de base de datos.
2. **SQLInjectionFString**: identifica el uso de `f-string` dentro de consultas SQL, especialmente cuando incorporan datos provenientes del usuario.
3. **UnsafeEvalExec**: marca el uso de `eval()`, `exec()` o `compile()` sobre entradas dinámicas o no confiables.
4. **ShellInjection**: detecta llamadas a `os.system()`, `subprocess.call()`, `subprocess.run()` o `Popen()` con `shell=True` y datos externos.
5. **InsecureDeserializationPickle**: alerta cuando se usa `pickle.load()` o `pickle.loads()` sobre datos no confiables.
6. **InsecureYamlLoad**: identifica el uso de `yaml.load()` sin un cargador seguro, por ejemplo en lugar de `yaml.safe_load()`.
7. **WeakHashAlgorithm**: reporta el uso de algoritmos débiles como MD5 o SHA1 para integridad, firmas o almacenamiento de contraseñas.
8. **InsecureRandomForSecrets**: detecta `random` o `randint()` cuando se usa para generar tokens, contraseñas, claves o códigos de seguridad.
9. **TLSVerificationDisabled**: marca peticiones HTTP o clientes TLS con `verify=False`, certificados deshabilitados o validación de TLS omitida.
10. **HardcodedDatabaseCredentials**: identifica credenciales de base de datos escritas en el código, como usuario, contraseña, host o URI de conexión embebida.
11. **PathTraversal**: detecta construcción insegura de rutas usando datos del usuario sin normalización o validación previa.
12. **FileOverwriteRisk**: alerta cuando el código abre archivos en modos de escritura o sobrescritura usando rutas derivadas de entrada externa.
13. **InsecureTempFileUsage**: marca el uso inseguro de archivos temporales en directorios predecibles o con nombres no aleatorios.
14. **MissingInputValidation**: identifica flujos donde entradas de usuario llegan a operaciones sensibles sin validación, filtrado o saneamiento.
15. **CommandInjectionViaFormat**: detecta comandos del sistema construidos con concatenación, `format()` o `f-string` antes de ejecutarse.
16. **SecretInLogs**: avisa cuando variables sensibles, tokens o credenciales se registran en logs, prints o trazas de depuración.
17. **InsecureCookieConfig**: detecta cookies creadas sin atributos de seguridad como `HttpOnly`, `Secure` o `SameSite` cuando correspondan.
18. **WeakCryptographyMode**: marca modos inseguros en criptografía simétrica, por ejemplo CBC sin autenticación o uso incorrecto de padding.
19. **DefaultCredentialsUsage**: identifica uso de credenciales por defecto, cuentas conocidas o valores triviales en clientes, servicios o ejemplos.
20. **ExposedSensitiveConfiguration**: detecta secretos, endpoints internos, claves o configuraciones sensibles hardcodeadas en archivos de configuración o módulos fuente.

## Notas de implementación

- Las reglas propuestas se pueden modelar como clases independientes, siguiendo el patrón actual de `AnalysisRule`.
- Cada regla debería generar incidencias con severidad, línea, columna y mensaje explicativo.
- Para mantener coherencia con la propuesta, conviene priorizar primero las reglas de mayor impacto: credenciales, inyección SQL, ejecución de comandos, deserialización insegura y secretos expuestos.