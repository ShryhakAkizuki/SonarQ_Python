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

### 7. WeakHashAlgorithm

Detecta algoritmos de hash debiles usados en operaciones de seguridad.

- **Clase:** `WeakHashAlgorithmRule`
- **Severidad:** `HIGH`
- **Detecta:** `hashlib.md5(...)`, `hashlib.sha1(...)`, aliases directos y
  `hmac.new(..., digestmod=hashlib.sha1/md5)`.
- **Tests relacionados:** `input/Test17.py`, `input/Test18.py`

### 8. InsecureRandomForSecrets

Detecta generadores no criptograficos usados para crear secretos.

- **Clase:** `InsecureRandomForSecretsRule`
- **Severidad:** `HIGH`
- **Detecta:** `random`, `randint()`, `choice()`, `choices()`, `randrange()`,
  `getrandbits()` o `randbytes()` asignados a tokens, claves, OTPs, PINs o
  contrasenas.
- **Tests relacionados:** `input/Test19.py`, `input/Test20.py`

### 9. WeakCryptographyMode

Detecta modos de cifrado inseguros o sin autenticacion visible.

- **Clase:** `WeakCryptographyModeRule`
- **Severidad:** `CRITICAL` para ECB, `HIGH` para CBC sin autenticacion visible,
  `MEDIUM` para padding manual riesgoso.
- **Detecta:** `MODE_ECB`, `MODE_CBC` sin HMAC/autenticacion evidente y padding
  manual PKCS7.
- **Tests relacionados:** `input/Test21.py`, `input/Test22.py`

### 10. HardcodedCryptoKey

Detecta material criptografico embebido directamente en el codigo.

- **Clase:** `HardcodedCryptoKeyRule`
- **Severidad:** `CRITICAL`
- **Detecta:** asignaciones literales a nombres como `aes_key`, `secret_key`,
  `private_key`, `iv`, `nonce` o `salt`.
- **Tests relacionados:** `input/Test23.py`, `input/Test24.py`

### 11. InsecurePasswordHashing

Detecta contrasenas procesadas con hashes rapidos o sin salt claro.

- **Clase:** `InsecurePasswordHashingRule`
- **Severidad:** `HIGH` para hashes rapidos, `MEDIUM` para PBKDF2 sin salt claro
  o con salt vacio.
- **Detecta:** `md5`, `sha1` o `sha256` aplicados a variables de password.
- **Tests relacionados:** `input/Test25.py`, `input/Test26.py`

### 12. JWTWeakConfiguration

Detecta configuraciones debiles al emitir o validar JWT.

- **Clase:** `JWTWeakConfigurationRule`
- **Severidad:** `HIGH` o `CRITICAL`
- **Detecta:** `jwt.decode(..., verify=False)`, `verify_signature=False`,
  algoritmo `none` y secretos hardcodeados en `jwt.encode(...)`.
- **Tests relacionados:** `input/Test27.py`, `input/Test28.py`

### 13. TLSVerificationDisabled

Detecta desactivacion de validacion TLS/certificados.

- **Clase:** `TLSVerificationDisabledRule`
- **Severidad:** `HIGH`
- **Detecta:** `verify=False`, `ssl._create_unverified_context()`,
  `CERT_NONE` y `check_hostname=False`.
- **Tests relacionados:** `input/Test29.py`, `input/Test30.py`

### 14. InsecureHttpUsage

Detecta HTTP claro en endpoints sensibles.

- **Clase:** `InsecureHttpUsageRule`
- **Severidad:** `HIGH`
- **Detecta:** URLs `http://` asociadas a login, autenticacion, APIs, tokens,
  secretos o webhooks.
- **Tests relacionados:** `input/Test31.py`, `input/Test32.py`

### 15. RequestsWithoutTimeout

Detecta llamadas de red sin timeout explicito.

- **Clase:** `RequestsWithoutTimeoutRule`
- **Severidad:** `MEDIUM`
- **Detecta:** `requests` y `httpx` con metodos como `get`, `post`, `put`,
  `delete`, `patch` o `request` sin argumento `timeout`.
- **Tests relacionados:** `input/Test33.py`, `input/Test34.py`

### 16. BasicAuthOverHttp

Detecta credenciales o tokens enviados sobre HTTP sin TLS.

- **Clase:** `BasicAuthOverHttpRule`
- **Severidad:** `HIGH`
- **Detecta:** `auth=...`, headers `Authorization`, `Basic`, `Bearer` o tokens
  usados en llamadas con URL `http://`.
- **Tests relacionados:** `input/Test35.py`, `input/Test36.py`

### 17. OpenRedirect

Detecta redirecciones construidas con parametros de usuario.

- **Clase:** `OpenRedirectRule`
- **Severidad:** `HIGH`
- **Detecta:** `redirect(...)` con `request.args`, `request.form` o variables
  derivadas sin allowlist visible.
- **Tests relacionados:** `input/Test37.py`, `input/Test38.py`

### 18. PathTraversal

Detecta rutas de lectura construidas desde entrada externa sin validacion.

- **Clase:** `PathTraversalRule`
- **Severidad:** `HIGH`
- **Detecta:** `open(...)`, `send_file(...)` y `send_from_directory(...)` con
  rutas provenientes de `request`, `input`, `sys.argv` o variables derivadas.
- **Tests relacionados:** `input/Test39.py`, `input/Test40.py`

### 19. FileOverwriteRisk

Detecta escritura o reemplazo de archivos con rutas no confiables.

- **Clase:** `FileOverwriteRiskRule`
- **Severidad:** `HIGH`
- **Detecta:** `open(..., "w/a/wb")`, `os.rename`, `os.replace` y
  `shutil.move` con rutas controladas por usuario.
- **Tests relacionados:** `input/Test41.py`, `input/Test42.py`

### 20. InsecureTempFileUsage

Detecta creacion insegura de archivos temporales.

- **Clase:** `InsecureTempFileUsageRule`
- **Severidad:** `HIGH` para `mktemp`, `MEDIUM` para rutas temporales
  predecibles.
- **Detecta:** `tempfile.mktemp()`, uso manual de `/tmp/...` y rutas temporales
  construidas por concatenacion o formato.
- **Tests relacionados:** `input/Test43.py`, `input/Test44.py`

### 21. UnsafeArchiveExtraction

Detecta extraccion de archivos comprimidos sin validacion de rutas internas.

- **Clase:** `UnsafeArchiveExtractionRule`
- **Severidad:** `HIGH`
- **Detecta:** `extractall()` y `extract()` sobre ZIP/TAR u objetos de archivo
  comprimido.
- **Tests relacionados:** `input/Test45.py`, `input/Test46.py`

### 22. UnsafeFilePermissions

Detecta permisos demasiado amplios en archivos o directorios.

- **Clase:** `UnsafeFilePermissionsRule`
- **Severidad:** `HIGH`
- **Detecta:** `chmod`, `mkdir` u operaciones similares con `0o777`, `0o666`
  o sus equivalentes decimales comunes.
- **Tests relacionados:** `input/Test47.py`, `input/Test48.py`

### 23. DangerousFileDelete

Detecta borrado de archivos con rutas controladas por el usuario.

- **Clase:** `DangerousFileDeleteRule`
- **Severidad:** `HIGH`
- **Detecta:** `os.remove`, `os.unlink`, `shutil.rmtree` y aliases directos con
  rutas externas o variables derivadas.
- **Tests relacionados:** `input/Test49.py`, `input/Test50.py`

## Tests disponibles

- `Test01.py`: ejemplo inicial con credenciales hardcodeadas y SQL vulnerable.
- `Test02.py`: cobertura amplia de credenciales hardcodeadas.
- `Test03.py`: cobertura de complejidad ciclomatica.
- `Test04.py` a `Test06.py`: SQL injection por construccion dinamica de queries.
- `Test07.py` a `Test09.py`: deserializacion insegura con pickle.
- `Test10.py`: test integral que ejercita todas las 23 reglas implementadas,
  incluyendo credenciales, SQL, deserializacion, YAML, cookies, complejidad,
  criptografia, red/TLS/autenticacion y archivos/sistema operativo.
- `Test11.py` a `Test13.py`: carga insegura de YAML.
- `Test14.py` a `Test16.py`: configuracion insegura de cookies.
- `Test17.py` y `Test18.py`: hashes debiles con MD5/SHA1.
- `Test19.py` y `Test20.py`: uso de `random` para secretos.
- `Test21.py` y `Test22.py`: modos criptograficos ECB/CBC inseguros.
- `Test23.py` y `Test24.py`: claves, IVs o salts criptograficos hardcodeados.
- `Test25.py` y `Test26.py`: hashing inseguro de contrasenas.
- `Test27.py` y `Test28.py`: configuraciones JWT debiles.
- `Test29.py` y `Test30.py`: verificacion TLS deshabilitada.
- `Test31.py` y `Test32.py`: HTTP en endpoints sensibles.
- `Test33.py` y `Test34.py`: llamadas de red sin timeout.
- `Test35.py` y `Test36.py`: autenticacion o tokens sobre HTTP.
- `Test37.py` y `Test38.py`: redirecciones abiertas.
- `Test39.py` y `Test40.py`: path traversal.
- `Test41.py` y `Test42.py`: riesgo de sobrescritura de archivos.
- `Test43.py` y `Test44.py`: temporales inseguros.
- `Test45.py` y `Test46.py`: extraccion insegura de archivos comprimidos.
- `Test47.py` y `Test48.py`: permisos de archivo demasiado amplios.
- `Test49.py` y `Test50.py`: borrado peligroso de archivos.
- `Test51.py` y `Test52.py`: casos seguros de control para red y criptografia.

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
java -cp "out;ANTLR\antlr-4.13.1-complete.jar" Main input\Test17.py
java -cp "out;ANTLR\antlr-4.13.1-complete.jar" Main input\Test50.py
java -cp "out;ANTLR\antlr-4.13.1-complete.jar" Main input\Test52.py
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
3. `SecretInLogs`
4. `DebugModeEnabled`
5. `MissingCSRFProtection`
6. `XMLExternalEntity`
