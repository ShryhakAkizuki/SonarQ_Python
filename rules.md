# Reglas de analisis de vulnerabilidades para el proyecto

Este documento resume las reglas implementadas actualmente en el analizador
estatico de Python construido con ANTLR + Java. Cada regla sigue el patron del
proyecto: una clase independiente que extiende `AnalysisRule`, se registra en
`RulesListener` y reporta incidencias con severidad, ubicacion y descripcion.

## Reglas implementadas

### 1. HardcodedCredentials

Detecta credenciales, claves, tokens o secretos escritos directamente en el
codigo fuente.

- **Clase:** `HardcodedCredentialsRule`
- **Severidad:** `CRITICAL`
- **Detecta:** asignaciones como `password = "..."`, `api_key = "..."`,
  `client_secret = "..."`, atributos como `user.token = "..."` y targets con
  claves sensibles.
- **Evita falsos positivos:** ignora valores demasiado cortos, placeholders,
  valores dinamicos, concatenaciones y f-strings con interpolacion.
- **Tests relacionados:** `input/Test02.py`, `input/Test10.py`

### 2. SQLInjectionConcat

Detecta consultas SQL construidas dinamicamente antes de enviarlas al motor de
base de datos.

- **Clase:** `SQLInjectionConcatRule`
- **Severidad:** `CRITICAL`
- **Detecta:** `cursor.execute(...)`, `conn.execute(...)`, `executemany`,
  `executescript`, `raw` y `query` cuando el argumento SQL se construye con
  concatenacion, f-string, `.format()` o formateo con `%`.
- **Seguimiento simple:** marca variables que almacenan SQL dinamico y reporta
  cuando luego se ejecutan, por ejemplo `query = "SELECT..." + user_input` y
  despues `cursor.execute(query)`.
- **No reporta:** consultas parametrizadas como
  `cursor.execute("SELECT ... WHERE id = ?", (id,))`.
- **Tests relacionados:** `input/Test04.py`, `input/Test05.py`,
  `input/Test06.py`, `input/Test10.py`

### 3. InsecureDeserializationPickle

Detecta deserializacion insegura con `pickle` cuando los datos provienen de
fuentes externas o no confiables.

- **Clase:** `InsecureDeserializationPickleRule`
- **Severidad:** `HIGH`
- **Detecta:** `pickle.load(...)`, `pickle.loads(...)` y `pickle.Unpickler(...)`
  usados sobre datos provenientes de `request`, `input()`, sockets, archivos
  abiertos con `open(...)` o variables derivadas de esas fuentes.
- **Seguimiento simple:** propaga variables no confiables, por ejemplo
  `payload = request.get_data()` seguido de `pickle.loads(payload)`.
- **No reporta:** uso de JSON para datos externos o `pickle` aplicado sobre
  blobs internos controlados por la aplicacion.
- **Tests relacionados:** `input/Test07.py`, `input/Test08.py`,
  `input/Test09.py`, `input/Test10.py`

### 4. InsecureYamlLoad

Detecta carga insegura de YAML cuando se usa una API capaz de construir objetos
Python no seguros.

- **Clase:** `InsecureYamlLoadRule`
- **Severidad:** `HIGH`
- **Detecta:** `yaml.load(...)` sin `SafeLoader`, `yaml.full_load(...)`,
  `yaml.unsafe_load(...)`, imports con alias como `import yaml as y`, y formas
  directas como `from yaml import load`.
- **No reporta:** `yaml.safe_load(...)` ni `yaml.load(..., Loader=yaml.SafeLoader)`
  o `yaml.CSafeLoader`.
- **Tests relacionados:** `input/Test11.py`, `input/Test12.py`,
  `input/Test13.py`, `input/Test10.py`

### 5. InsecureCookieConfig

Detecta cookies creadas sin atributos de seguridad esenciales.

- **Clase:** `InsecureCookieConfigRule`
- **Severidad:** `HIGH`
- **Detecta:** llamadas como `response.set_cookie(...)`,
  `response.set_signed_cookie(...)` o `client.cookies.set(...)` cuando falta
  alguno de estos atributos: `HttpOnly`, `Secure` o `SameSite`.
- **No reporta:** cookies con `httponly=True`, `secure=True` y
  `samesite="Lax"` o `samesite="Strict"`.
- **Tests relacionados:** `input/Test14.py`, `input/Test15.py`,
  `input/Test16.py`, `input/Test10.py`

### 6. CyclomaticComplexity

Analiza complejidad ciclomatica y exceso de parametros.

- **Clase:** `CyclomaticComplexityRule`
- **Severidad:** `MEDIUM`, `HIGH` o `LOW` segun el caso.
- **Detecta:** funciones y lambdas con demasiados caminos de decision, usando
  estructuras como `if`, `elif`, `while`, `for`, `except`, `case`, operadores
  `and`/`or`, ternarios y filtros en comprehensions.
- **Tambien detecta:** funciones regulares con mas de 7 parametros.
- **Tests relacionados:** `input/Test03.py`, `input/Test10.py`

## Tests disponibles

- `Test01.py`: ejemplo inicial con credenciales hardcodeadas y SQL vulnerable.
- `Test02.py`: cobertura amplia de credenciales hardcodeadas.
- `Test03.py`: cobertura de complejidad ciclomatica.
- `Test04.py` a `Test06.py`: SQL injection por construccion dinamica de queries.
- `Test07.py` a `Test09.py`: deserializacion insegura con pickle.
- `Test10.py`: test integral que ejercita todas las reglas implementadas.
- `Test11.py` a `Test13.py`: carga insegura de YAML.
- `Test14.py` a `Test16.py`: configuracion insegura de cookies.

## Comandos de prueba

Compilar:

```powershell
javac -cp ANTLR\antlr-4.13.1-complete.jar -d out gen\*.java src\*.java
```

Ejecutar un test individual:

```powershell
java -cp "out;ANTLR\antlr-4.13.1-complete.jar" Main input\Test10.py
```

Ejecutar otros tests cambiando el nombre del archivo:

```powershell
java -cp "out;ANTLR\antlr-4.13.1-complete.jar" Main input\Test14.py
java -cp "out;ANTLR\antlr-4.13.1-complete.jar" Main input\Test15.py
java -cp "out;ANTLR\antlr-4.13.1-complete.jar" Main input\Test16.py
```

Nota: cuando se detectan incidencias `CRITICAL` o `HIGH`, el programa termina
con codigo de salida `1`. Esto es esperado en los tests vulnerables.

## Reglas pendientes sugeridas

Estas reglas siguen siendo candidatas para futuras iteraciones. La lista esta
pensada como un backlog amplio para cubrir varias familias de vulnerabilidades:
ejecucion dinamica, comandos del sistema, criptografia, archivos, red,
configuracion, logging y exposicion de secretos.

### Ejecucion dinamica y comandos

1. **UnsafeEvalExec:** detecta `eval()`, `exec()` o `compile()` cuando reciben
   datos externos, variables no confiables o cadenas construidas dinamicamente.
   Riesgo: ejecucion arbitraria de codigo Python.
2. **ShellInjection:** detecta `os.system()`, `subprocess.run()`,
   `subprocess.call()`, `Popen()` o APIs similares cuando usan `shell=True` o
   comandos construidos con concatenacion, f-strings o `.format()`.
3. **CommandInjectionViaFormat:** variante enfocada en comandos del sistema
   construidos por interpolacion antes de ejecutarse, incluso cuando no aparece
   `shell=True` de forma explicita.
4. **UnsafeDynamicImport:** detecta `__import__()`, `importlib.import_module()`
   o carga dinamica de modulos usando nombres provenientes de entrada externa.
5. **UnsafeTemplateRendering:** detecta plantillas renderizadas desde strings o
   archivos controlados por usuario, por ejemplo `render_template_string()` con
   entrada externa.

### Deserializacion y parsing inseguro

6. **InsecureJsonPickleUsage:** detecta librerias como `jsonpickle.decode()` o
   mecanismos equivalentes que reconstruyen objetos desde datos no confiables.
7. **UnsafeMarshalDeserialization:** detecta `marshal.load()` o
   `marshal.loads()` sobre archivos, sockets o requests externos.
8. **UnsafeDillCloudpickle:** detecta `dill.load(s)` y `cloudpickle.load(s)`
   sobre fuentes externas, similares en impacto a `pickle`.
9. **XMLExternalEntity:** detecta parsing XML inseguro con `xml.etree`,
   `minidom`, `sax` o `lxml` sin protecciones contra XXE o entity expansion.
10. **UnsafeConfigParserInterpolation:** detecta carga de configuraciones con
    interpolacion peligrosa o valores de configuracion usados luego en sinks
    sensibles.

### Criptografia y secretos

11. **WeakHashAlgorithm:** reporta MD5, SHA1 u otros hashes debiles usados para
    seguridad, integridad fuerte, firmas o almacenamiento de contrasenas.
12. **InsecureRandomForSecrets:** detecta `random`, `randint()`, `choice()` o
    generadores no criptograficos usados para tokens, claves, codigos OTP o
    contrasenas.
13. **WeakCryptographyMode:** detecta modos inseguros como ECB, CBC sin
    autenticacion, IVs constantes, nonces reutilizados o padding manual riesgoso.
14. **HardcodedCryptoKey:** detecta claves criptograficas, IVs, salts o secretos
    embebidos directamente en el codigo.
15. **InsecurePasswordHashing:** detecta contrasenas procesadas con hashes
    rapidos o sin salt, y recomienda algoritmos como bcrypt, scrypt o Argon2.
16. **JWTWeakConfiguration:** detecta JWT con `verify=False`, algoritmos
    inseguros como `none`, secretos hardcodeados o validacion incompleta de
    expiracion/audiencia.

### Red, TLS y autenticacion

17. **TLSVerificationDisabled:** detecta `verify=False`, certificados no
    validados, contexts SSL permisivos o hostname checking deshabilitado.
18. **InsecureHttpUsage:** detecta URLs `http://` para autenticacion, APIs,
    tokens, webhooks o transmision de datos sensibles.
19. **RequestsWithoutTimeout:** detecta llamadas de red sin `timeout`, lo que
    puede causar bloqueos o denegacion de servicio.
20. **BasicAuthOverHttp:** detecta autenticacion basica o tokens enviados sobre
    HTTP sin TLS.
21. **OpenRedirect:** detecta redirecciones construidas con parametros de
    usuario sin validacion de dominio o ruta permitida.

### Archivos, rutas y sistema operativo

22. **PathTraversal:** detecta rutas construidas con entrada externa sin
    normalizacion ni validacion, especialmente antes de `open()`, `send_file()`
    o lectura de archivos.
23. **FileOverwriteRisk:** detecta escritura, sobrescritura o borrado de archivos
    usando rutas derivadas de datos no confiables.
24. **InsecureTempFileUsage:** detecta archivos temporales predecibles,
    directorios compartidos o uso inseguro de `mktemp()`.
25. **UnsafeArchiveExtraction:** detecta `zipfile.extractall()` o
    `tarfile.extractall()` sin validar rutas internas, con riesgo de Zip Slip.
26. **UnsafeFilePermissions:** detecta permisos demasiado amplios como `0o777`,
    `chmod(..., 0o666)` o archivos sensibles creados sin restriccion.
27. **DangerousFileDelete:** detecta `os.remove()`, `shutil.rmtree()` o borrado
    recursivo con rutas controladas por usuario.

### Web, sesiones y datos de usuario

28. **MissingCSRFProtection:** detecta endpoints de escritura sin proteccion CSRF
    en frameworks como Flask, Django o formularios propios.
29. **DebugModeEnabled:** detecta `debug=True`, `app.run(debug=True)` o
    configuraciones equivalentes activas.
30. **InsecureCorsConfig:** detecta CORS permisivo como `origins="*"`,
    credenciales habilitadas con origen abierto o headers excesivamente amplios.
31. **MissingInputValidation:** detecta flujos donde entrada de usuario llega a
    operaciones sensibles sin validacion, parseo estricto o allowlists.
32. **MassAssignmentRisk:** detecta asignacion directa de `request.json`,
    `request.form` o diccionarios externos a modelos internos.
33. **SensitiveDataExposureResponse:** detecta respuestas HTTP que devuelven
    tokens, passwords, claves privadas, stack traces o datos internos.
34. **InsecureSessionConfig:** detecta configuraciones de sesion sin expiracion,
    cookies de sesion inseguras, secretos de sesion hardcodeados o sesiones
    firmadas con claves debiles.

### Logging, errores y exposicion de informacion

35. **SecretInLogs:** detecta secretos, tokens, cookies, passwords o headers de
    autorizacion enviados a `print`, `logging`, trazas o excepciones.
36. **VerboseErrorDisclosure:** detecta stack traces, mensajes de excepcion o
    informacion interna devuelta directamente al usuario.
37. **ExposedSensitiveConfiguration:** detecta endpoints internos, URIs de base
    de datos, claves de servicios, buckets, regiones o configuraciones sensibles
    hardcodeadas.
38. **SourceMapOrDebugArtifactExposure:** detecta referencias a artefactos de
    depuracion, archivos `.env`, backups o rutas internas servidas por la app.

### Bases de datos y almacenamiento

39. **HardcodedDatabaseCredentials:** detecta usuario, password, host, puerto o
    URIs de conexion escritos directamente en el codigo.
40. **NoSQLInjection:** detecta consultas MongoDB/NoSQL construidas con objetos o
    operadores provenientes directamente del usuario.
41. **LDAPInjection:** detecta filtros LDAP construidos con concatenacion o
    interpolacion de entrada externa.
42. **TemplateQueryInjection:** detecta consultas en ORMs o motores de busqueda
    construidas como strings dinamicos, por ejemplo filtros Elasticsearch o SQL
    raw fuera de `execute()`.
43. **UnsafeMigrationOrAdminOperation:** detecta operaciones destructivas como
    `DROP`, `TRUNCATE`, borrados masivos o migraciones expuestas a entrada
    externa.

### Dependencias y configuracion de entorno

44. **DependencyConfusionRisk:** detecta instalacion dinamica de paquetes,
    repositorios no confiables o referencias a dependencias sin pinning en
    configuraciones del proyecto.
45. **InsecureSubprocessEnvironment:** detecta secretos pasados por variables de
    entorno a procesos hijos o entornos heredados sin control.
46. **UnsafeEnvironmentDefaults:** detecta defaults inseguros como
    `SECRET_KEY="dev"`, `ALLOWED_HOSTS=["*"]` o credenciales por defecto.
47. **MissingSecurityHeaders:** detecta respuestas web sin headers como
    `Content-Security-Policy`, `X-Frame-Options`, `Referrer-Policy` o
    `Strict-Transport-Security`.
48. **InsecureDependencyDownload:** detecta descargas de ejecutables, scripts o
    dependencias por HTTP o sin verificacion de integridad.

### Priorizacion sugerida

Para continuar el proyecto, conviene priorizar primero reglas con alto impacto y
patrones faciles de detectar en AST:

1. `UnsafeEvalExec`
2. `ShellInjection`
3. `TLSVerificationDisabled`
4. `WeakHashAlgorithm`
5. `PathTraversal`
6. `SecretInLogs`
