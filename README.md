# SonarQ_Python - Analizador de Seguridad para Python

## Tabla de Contenido

1. [Descripción](#descripción)
2. [Prerrequisitos](#prerrequisitos)
   - [Software Requerido](#software-requerido)
   - [Librerías Python](#librerías-python-para-archivos-de-prueba)
   - [Estructura de Directorios](#estructura-de-directorios)
3. [Compilación](#compilación)
   - [Paso 1: Generar el Parser](#paso-1-generar-el-parser-de-python-si-no-existe-gen)
   - [Paso 2: Compilar el Código Java](#paso-2-compilar-el-código-java)
   - [Paso 3: Verificar la Compilación](#paso-3-verificar-la-compilación)
4. [Uso](#uso)
   - [Comando Básico](#comando-básico)
   - [Ejemplos de Uso](#ejemplos-de-uso)
5. [Archivos de Prueba Incluidos](#archivos-de-prueba-incluidos)
   - [Archivos de Demostración Simple](#archivos-de-demostración-simple)
   - [Archivos de Presentación Completos](#archivos-de-presentación-completos)
6. [Reglas de Seguridad Detectadas](#reglas-de-seguridad-detectadas)
   - [1. SQL Injection via String Concatenation](#1-sql-injection-via-string-concatenation)
   - [2. Insecure YAML Load](#2-insecure-yaml-load)
   - [3. Weak Hash Algorithm](#3-weak-hash-algorithm)
   - [4. Insecure Random for Secrets](#4-insecure-random-for-secrets)
   - [5. Weak Cryptography Mode](#5-weak-cryptography-mode)
7. [Solución de Problemas](#solución-de-problemas)
8. [Notas Importantes](#notas-importantes)
   - [Propósito Académico](#propósito-académico)
   - [Archivos de Prueba](#archivos-de-prueba)
   - [Limitaciones](#limitaciones)
   - [Recomendaciones](#recomendaciones)
9. [Ejemplos de Entrada y Salida](#ejemplos-de-entrada-y-salida)
10. [Contacto y Contribuciones](#contacto-y-contribuciones)
11. [Referencias](#referencias)

---

## Descripción

Las vulnerabilidades de seguridad y la deuda técnica son dos de las principales causas de fallos en software en producción. Detectarlas manualmente es lento e inconsistente.

Este proyecto implementa un analizador estático para Python inspirado en SonarQube, el cual analiza un archivo .py, recorre su árbol sintáctico con ANTLR4 para parsear código Python 3.13 y aplicar **38 reglas** de análisis organizadas en 7 categorías. y reporta tanto vulnerabilidades de seguridad, como problemas de calidad clasificados por severidad, sin ejecutar el código.


### 🔐 1. Autenticación & Credenciales (4 reglas)
- **HardcodedCredentials** - Credenciales hardcodeadas en código fuente
- **HardcodedCryptoKey** - Claves criptográficas, IVs, nonces hardcodeados
- **BasicAuthOverHttp** - Autenticación o tokens enviados sobre HTTP sin TLS
- **JWTWeakConfiguration** - JWT con verificación deshabilitada o algoritmo 'none'

### 🌐 2. Red & Protocolo (4 reglas)
- **InsecureHttpUsage** - URLs HTTP para transmitir datos sensibles
- **TLSVerificationDisabled** - Verificación TLS/certificados deshabilitada
- **RequestsWithoutTimeout** - Llamadas de red sin timeout explícito
- **InsecureCookieConfig** - Cookies sin HttpOnly, Secure o SameSite

### 💉 3. Inyección & Datos (5 reglas)
- **SQLInjectionConcat** - SQL injection mediante concatenación de strings
- **PathTraversal** - Rutas construidas con entrada externa sin validación
- **OpenRedirect** - Redirecciones construidas con parámetros de usuario
- **InsecureDeserializationPickle** - Deserialización insegura con pickle
- **InsecureYamlLoad** - Carga YAML insegura (load/full_load/unsafe_load)

### 🔒 4. Criptografía (4 reglas)
- **WeakHashAlgorithm** - Algoritmos de hash débiles (MD5, SHA-1)
- **InsecurePasswordHashing** - Contraseñas con hash rápido sin salt
- **WeakCryptographyMode** - Modos criptográficos inseguros (ECB, CBC sin auth)
- **InsecureRandomForSecrets** - Generador no criptográfico para secretos

### 📁 5. Sistema de Archivos (5 reglas)
- **UnsafeFilePermissions** - Permisos de archivo demasiado amplios (777, 666)
- **InsecureTempFileUsage** - Archivos temporales predecibles o inseguros
- **FileOverwriteRisk** - Escritura de archivos con rutas no confiables
- **DangerousFileDelete** - Borrado de archivos con rutas controladas por usuario
- **UnsafeArchiveExtraction** - Extracción de archivos sin validar rutas (Zip Slip)

### 📊 6. Calidad de Código (6 reglas)
- **CyclomaticComplexity** - Complejidad ciclomática excesiva (>10 MEDIUM, >20 HIGH)
- **LongMethod** - Métodos con demasiadas líneas (>50 HIGH, >100 CRITICAL)
- **LongParameterList** - Funciones con demasiados parámetros (>5 LOW, >7 MEDIUM)
- **DeepNesting** - Anidamiento excesivo de bloques (>4 MEDIUM, >6 HIGH)
- **CodeDuplication** - Bloques de código duplicados (≥5 líneas)
- **MagicNumbers** - Números literales sin constantes nombradas

### 🧹 7. Mantenibilidad (4 reglas)
- **GlobalVariable** - Uso de variables globales o nonlocal
- **UnusedImports** - Imports que no se usan en el código
- **DeadCode** - Código que nunca se ejecuta (after return, if False)
- **TodoComments** - Comentarios TODO/FIXME/HACK/XXX/BUG

**Total: 38 reglas** (23 seguridad + 10 calidad + 5 soporte)

---

## Prerrequisitos

### Software Requerido
- **Java JDK 11 o superior**
- **ANTLR 4.13.1** (incluido en `ANTLR/`)
- **Python 3.x** (para ejecutar los archivos de prueba)

### Librerías Python (para archivos de prueba)
```bash
pip install pyyaml pycryptodome
```

### Estructura de Directorios
```
SonarQ_Python/
├── ANTLR/                      # JARs de ANTLR4
│   ├── antlr-4.13.1-complete.jar
│   └── antlr-runtime-4.13.1.jar
├── gen/                        # Parser generado por ANTLR
│   ├── PythonLexer.java
│   ├── PythonParser.java
│   └── ...
├── src/                        # Código fuente Java (38 reglas)
│   ├── Main.java               # Punto de entrada
│   ├── RulesListener.java      # Orquestador de reglas
│   ├── AnalysisRule.java       # Clase base para reglas
│   ├── Issue.java              # Modelo de incidencia
│   ├── Severity.java           # Niveles de severidad
│   ├── SecurityRuleUtil.java   # Utilidades compartidas
│   │
│   ├── # Reglas de Seguridad (23 reglas)
│   ├── SQLInjectionConcatRule.java
│   ├── InsecureYamlLoadRule.java
│   ├── WeakHashAlgorithmRule.java
│   ├── InsecureRandomForSecretsRule.java
│   ├── WeakCryptographyModeRule.java
│   ├── HardcodedCredentialsRule.java
│   ├── HardcodedCryptoKeyRule.java
│   ├── InsecureDeserializationPickleRule.java
│   ├── InsecurePasswordHashingRule.java
│   ├── InsecureCookieConfigRule.java
│   ├── JWTWeakConfigurationRule.java
│   ├── TLSVerificationDisabledRule.java
│   ├── InsecureHttpUsageRule.java
│   ├── BasicAuthOverHttpRule.java
│   ├── RequestsWithoutTimeoutRule.java
│   ├── OpenRedirectRule.java
│   ├── PathTraversalRule.java
│   ├── FileOverwriteRiskRule.java
│   ├── DangerousFileDeleteRule.java
│   ├── InsecureTempFileUsageRule.java
│   ├── UnsafeArchiveExtractionRule.java
│   ├── UnsafeFilePermissionsRule.java
│   │
│   └── # Reglas de Calidad de Código (10 reglas)
│       ├── CyclomaticComplexityRule.java
│       ├── LongMethodRule.java
│       ├── LongParameterListRule.java
│       ├── DeepNestingRule.java
│       ├── CodeDuplicationRule.java
│       ├── MagicNumbersRule.java
│       ├── GlobalVariableRule.java
│       ├── UnusedImportsRule.java
│       ├── DeadCodeRule.java
│       └── TodoCommentsRule.java
├── input/                      # Archivos Python de prueba
│   ├── TestDemoVulnerable.py
│   ├── TestDemoSeguro.py
│   ├── TestPresentacionVulnerable.py
│   └── TestPresentacionSeguro.py
├── out/                        # Clases Java compiladas
└── grammar/                    # Gramática ANTLR de Python
    ├── PythonLexer.g4
    └── PythonParser.g4
```

---

## Compilación

### Paso 1: Generar el Parser de Python (si no existe `gen/`)
```powershell
# Desde el directorio raíz del proyecto
java -jar ANTLR/antlr-4.13.1-complete.jar -Dlanguage=Java -visitor -listener -o gen grammar/PythonParser.g4 grammar/PythonLexer.g4
```

### Paso 2: Compilar el Código Java
```powershell
# Configurar encoding UTF-8 para PowerShell
[Console]::OutputEncoding = [System.Text.Encoding]::UTF8
chcp 65001

# Compilar todas las clases Java
javac -encoding UTF-8 -cp "ANTLR/*;gen" -d out src/*.java
```

### Paso 3: Verificar la Compilación
```powershell
# Debe existir el directorio out/ con las clases compiladas
dir out
```

---

## Uso

### Comando Básico
```powershell
java -cp "out;ANTLR/*;gen" Main input/archivo.py
```

**Nota para Linux/Mac:** Usar `:` en lugar de `;` como separador de classpath:
```bash
java -cp "out:ANTLR/*:gen" Main input/archivo.py
```

### Ejemplos de Uso

#### 1. Analizar Código Vulnerable (Demo Simple)
```powershell
java -cp "out;ANTLR/*;gen" Main input/TestDemoVulnerable.py
```

**Salida Esperada:**
```
Analizando: input/TestDemoVulnerable.py

Issues encontrados (ordenados por severidad):

[CRITICAL ] SQLInjectionConcat            linea 18:4 - Consulta SQL construida por concatenacion antes de ejecutarse [60min]

Total: 1 issues
Tiempo estimado de corrección: 1h
```

#### 2. Analizar Código Seguro (Demo Simple)
```powershell
java -cp "out;ANTLR/*;gen" Main input/TestDemoSeguro.py
```

**Salida Esperada:**
```
Analizando: input/TestDemoSeguro.py

Issues encontrados (ordenados por severidad):

Total: 0 issues
Tiempo estimado de corrección: 0min
```

#### 3. Analizar Código Vulnerable Completo
```powershell
java -cp "out;ANTLR/*;gen" Main input/TestPresentacionVulnerable.py
```

**Salida Esperada:**
```
Analizando: input/TestPresentacionVulnerable.py

Issues encontrados (ordenados por severidad):

[CRITICAL ] SQLInjectionConcat            linea 37:12 - Consulta SQL construida por concatenacion antes de ejecutarse [60min]
[CRITICAL ] SQLInjectionConcat            linea 42:12 - Consulta SQL construida por f-string con interpolacion antes de ejecutarse [60min]
[CRITICAL ] SQLInjectionConcat            linea 47:12 - Consulta SQL construida por format() antes de ejecutarse [60min]
[CRITICAL ] SQLInjectionConcat            linea 53:12 - Consulta SQL construida por formateo con % antes de ejecutarse [60min]
[CRITICAL ] SQLInjectionConcat            linea 155:4 - Consulta SQL construida por concatenacion antes de ejecutarse [60min]
[CRITICAL ] WeakCryptographyMode          linea 102:17 - Modo criptografico inseguro ECB usado para cifrado [60min]
[CRITICAL ] WeakCryptographyMode          linea 204:13 - Modo criptografico inseguro ECB usado para cifrado [60min]
[HIGH     ] InsecureYamlLoad              linea 65:16 - Carga YAML insegura: yaml.load() sin SafeLoader/safe_load [30min]
[HIGH     ] InsecureYamlLoad              linea 132:16 - Carga YAML insegura: yaml.load() sin SafeLoader/safe_load [30min]
[HIGH     ] InsecureYamlLoad              linea 136:16 - Carga YAML insegura: yaml.full_load() permite construir objetos Python mas alla de tipos seguros [30min]
[HIGH     ] InsecureYamlLoad              linea 140:16 - Carga YAML insegura: yaml.unsafe_load() permite deserializacion insegura de objetos [30min]
[HIGH     ] WeakHashAlgorithm             linea 73:16 - Algoritmo de hash debil usado en contexto de seguridad: MD5/SHA1 [30min]
[HIGH     ] WeakHashAlgorithm             linea 76:16 - Algoritmo de hash debil usado en contexto de seguridad: MD5/SHA1 [30min]
[HIGH     ] WeakHashAlgorithm             linea 185:4 - Algoritmo de hash debil usado en contexto de seguridad: MD5/SHA1 [30min]
[HIGH     ] WeakHashAlgorithm             linea 188:4 - Algoritmo de hash debil usado en contexto de seguridad: MD5/SHA1 [30min]
[HIGH     ] WeakHashAlgorithm             linea 191:4 - Algoritmo de hash debil usado en contexto de seguridad: MD5/SHA1 [30min]
[HIGH     ] InsecureRandomForSecrets      linea 86:12 - Generador no criptografico usado para crear secreto en 'session_token' [30min]
[HIGH     ] InsecureRandomForSecrets      linea 89:12 - Generador no criptografico usado para crear secreto en 'api_key' [30min]
[HIGH     ] InsecureRandomForSecrets      linea 92:12 - Generador no criptografico usado para crear secreto en 'reset_code' [30min]
[HIGH     ] InsecureRandomForSecrets      linea 95:12 - Generador no criptografico usado para crear secreto en 'otp' [30min]
[HIGH     ] InsecureRandomForSecrets      linea 169:4 - Generador no criptografico usado para crear secreto en 'secret_key' [30min]
[HIGH     ] InsecureRandomForSecrets      linea 170:4 - Generador no criptografico usado para crear secreto en 'password' [30min]
[HIGH     ] InsecureRandomForSecrets      linea 171:4 - Generador no criptografico usado para crear secreto en 'pin' [30min]
[HIGH     ] InsecureRandomForSecrets      linea 172:4 - Generador no criptografico usado para crear secreto en 'nonce' [30min]
[HIGH     ] WeakCryptographyMode          linea 107:17 - Modo CBC usado sin autenticacion visible del ciphertext [30min]

Total: 25 issues
Tiempo estimado de corrección: 16h 30min
```

#### 4. Analizar Código Seguro Completo
```powershell
java -cp "out;ANTLR/*;gen" Main input/TestPresentacionSeguro.py
```

**Salida Esperada:**
```
Analizando: input/TestPresentacionSeguro.py

Issues encontrados (ordenados por severidad):

Total: 0 issues
Tiempo estimado de corrección: 0min
```

---

## Archivos de Prueba Incluidos

### Archivos de Demostración Simple

#### `TestDemoVulnerable.py`
Ejemplo simple de **SQL Injection** mediante concatenación de strings.
- **Línea 18:** Construcción vulnerable de query SQL
- **Vulnerabilidad:** `query = "SELECT * FROM users WHERE username = '" + username + "'"`

#### `TestDemoSeguro.py`
Versión corregida usando **consultas parametrizadas**.
- **Línea 18:** Uso seguro de placeholders
- **Corrección:** `query = "SELECT * FROM users WHERE username = ?"`

### Archivos de Presentación Completos

#### `TestPresentacionVulnerable.py`
Función compleja con **múltiples vulnerabilidades** de seguridad:
1. **SQL Injection** (líneas 37, 42, 47, 53, 155)
   - Concatenación directa: `"SELECT * FROM users WHERE username = '" + username + "'"`
   - F-strings: `f"INSERT INTO users VALUES ('{username}')"`
   - format(): `"DELETE FROM users WHERE id = {}".format(user_id)`
   - % formatting: `"UPDATE users SET password = '%s'" % password`

2. **Insecure YAML Load** (líneas 65, 132, 136, 140)
   - `yaml.load(f)` sin SafeLoader
   - `yaml.full_load(f)` permite objetos complejos
   - `yaml.unsafe_load(f)` deserialización insegura

3. **Weak Hash Algorithm** (líneas 73, 76, 185, 188, 191)
   - `hashlib.md5()` para contraseñas
   - `hashlib.sha1()` para tokens

4. **Insecure Random for Secrets** (líneas 86, 89, 92, 95, 169-172)
   - `random.randint()` para tokens de sesión
   - `random.choice()` para PINs
   - `random.randbytes()` para nonces

5. **Weak Cryptography Mode** (líneas 102, 107, 204)
   - `AES.MODE_ECB` (inseguro)
   - `AES.MODE_CBC` sin autenticación

#### `TestPresentacionSeguro.py`
Versión corregida con **implementaciones seguras**:
1. **SQL Injection:** Consultas parametrizadas con `?`
2. **YAML Load:** `yaml.safe_load()` o `yaml.load(f, Loader=yaml.SafeLoader)`
3. **Hash Algorithm:** `hashlib.sha256()`, `hashlib.sha512()`, `hashlib.blake2b()`
4. **Random for Secrets:** `secrets.token_hex()`, `secrets.token_urlsafe()`, `secrets.randbits()`
5. **Cryptography Mode:** `AES.MODE_GCM` con autenticación integrada

---

## Alcance del Proyecto

### Reglas Implementadas (38 Total)

#### A. Reglas de Seguridad (23 reglas)

1. **SQLInjectionConcatRule** - SQL Injection mediante concatenación
2. **InsecureYamlLoadRule** - Carga insegura de YAML
3. **WeakHashAlgorithmRule** - Algoritmos de hash débiles (MD5, SHA1)
4. **InsecureRandomForSecretsRule** - Random no criptográfico para secretos
5. **WeakCryptographyModeRule** - Modos de cifrado inseguros (ECB, CBC sin auth)
6. **HardcodedCredentialsRule** - Credenciales hardcodeadas
7. **HardcodedCryptoKeyRule** - Claves criptográficas hardcodeadas
8. **InsecureDeserializationPickleRule** - Deserialización insegura con Pickle
9. **InsecurePasswordHashingRule** - Hash de contraseñas sin salt
10. **InsecureCookieConfigRule** - Cookies sin atributos de seguridad
11. **JWTWeakConfigurationRule** - JWT con configuración débil
12. **TLSVerificationDisabledRule** - Verificación TLS deshabilitada
13. **InsecureHttpUsageRule** - HTTP para datos sensibles
14. **BasicAuthOverHttpRule** - Autenticación sobre HTTP
15. **RequestsWithoutTimeoutRule** - Llamadas de red sin timeout
16. **OpenRedirectRule** - Redirecciones abiertas
17. **PathTraversalRule** - Path traversal sin validación
18. **FileOverwriteRiskRule** - Sobrescritura de archivos peligrosa
19. **DangerousFileDeleteRule** - Borrado de archivos peligroso
20. **InsecureTempFileUsageRule** - Archivos temporales inseguros
21. **UnsafeArchiveExtractionRule** - Extracción de archivos sin validar (Zip Slip)
22. **UnsafeFilePermissionsRule** - Permisos de archivo demasiado amplios
23. **DeepNestingRule** - Anidamiento excesivo (>4 niveles)

#### B. Reglas de Calidad de Código (10 reglas)

24. **CyclomaticComplexityRule** - Complejidad ciclomática alta (>10)
25. **LongMethodRule** - Métodos largos (>50 líneas)
26. **LongParameterListRule** - Listas de parámetros largas (>5)
27. **DeepNestingRule** - Anidamiento profundo (>4 niveles)
28. **CodeDuplicationRule** - Código duplicado (≥5 líneas)
29. **MagicNumbersRule** - Números mágicos sin constantes
30. **GlobalVariableRule** - Uso de variables globales
31. **UnusedImportsRule** - Imports no utilizados
32. **DeadCodeRule** - Código muerto (unreachable)
33. **TodoCommentsRule** - Comentarios TODO/FIXME/HACK/BUG

### Características del Analizador

- **Análisis estático completo** sin ejecutar el código
- **38 reglas** de análisis (23 seguridad + 10 calidad + 5 adicionales)
- **5 niveles de severidad**: CRITICAL, HIGH, MEDIUM, LOW, INFO
- **Cálculo de deuda técnica** en formato SonarQube (días, horas, minutos)
- **Soporte Python 3.13** con gramática ANTLR actualizada
- **Detección de patrones complejos** (taint analysis básico)
- **Reportes detallados** con línea, columna y tiempo estimado

---

## Reglas de Seguridad Detalladas

### 1. SQL Injection via String Concatenation
**Severidad:** CRITICAL (60min)

**Detecta:**
- Concatenación con `+`
- F-strings con interpolación
- Método `.format()`
- Formateo con `%`

**Ejemplo Vulnerable:**
```python
query = "SELECT * FROM users WHERE id = " + user_id
cursor.execute(query)
```

**Corrección:**
```python
query = "SELECT * FROM users WHERE id = ?"
cursor.execute(query, (user_id,))
```

### 2. Insecure YAML Load
**Severidad:** HIGH (30min)

**Detecta:**
- `yaml.load()` sin SafeLoader
- `yaml.full_load()`
- `yaml.unsafe_load()`

**Ejemplo Vulnerable:**
```python
config = yaml.load(file)
```

**Corrección:**
```python
config = yaml.safe_load(file)
# o
config = yaml.load(file, Loader=yaml.SafeLoader)
```

### 3. Weak Hash Algorithm
**Severidad:** HIGH (30min)

**Detecta:**
- `hashlib.md5()`
- `hashlib.sha1()`
- `hashlib.new('md5')`

**Ejemplo Vulnerable:**
```python
password_hash = hashlib.md5(password.encode()).hexdigest()
```

**Corrección:**
```python
password_hash = hashlib.sha256(password.encode()).hexdigest()
# Mejor aún: usar bcrypt o argon2
```

### 4. Insecure Random for Secrets
**Severidad:** HIGH (30min)

**Detecta:**
- `random.randint()` para tokens, keys, passwords
- `random.choice()` para secretos
- `random.randbytes()` para nonces

**Ejemplo Vulnerable:**
```python
token = str(random.randint(100000, 999999))
api_key = random.randbytes(32)
```

**Corrección:**
```python
import secrets
token = secrets.token_hex(32)
api_key = secrets.token_bytes(32)
```

### 5. Weak Cryptography Mode
**Severidad:** CRITICAL (60min) para ECB, HIGH (30min) para CBC sin auth

**Detecta:**
- `AES.MODE_ECB`
- `AES.MODE_CBC` sin HMAC

**Ejemplo Vulnerable:**
```python
cipher = AES.new(key, AES.MODE_ECB)
encrypted = cipher.encrypt(data)
```

**Corrección:**
```python
from Crypto.Random import get_random_bytes
nonce = get_random_bytes(12)
cipher = AES.new(key, AES.MODE_GCM, nonce=nonce)
encrypted, auth_tag = cipher.encrypt_and_digest(data)
```

---

## Solución de Problemas

### Error: ClassNotFoundException
```
Error: Could not find or load main class Main
```
**Solución:** Verificar que las clases estén compiladas en `out/`:
```powershell
javac -encoding UTF-8 -cp "ANTLR/*;gen" -d out src/*.java
```

### Error: Parser no encontrado
```
Exception in thread "main" java.lang.NoClassDefFoundError: PythonParser
```
**Solución:** Generar el parser con ANTLR:
```powershell
java -jar ANTLR/antlr-4.13.1-complete.jar -Dlanguage=Java -visitor -listener -o gen grammar/PythonParser.g4 grammar/PythonLexer.g4
```

### Error: Separador de classpath incorrecto
**Windows:** Usar `;` como separador
```powershell
java -cp "out;ANTLR/*;gen" Main input/test.py
```

**Linux/Mac:** Usar `:` como separador
```bash
java -cp "out:ANTLR/*:gen" Main input/test.py
```

### Error: Encoding UTF-8
Si aparecen caracteres extraños en la salida:
```powershell
[Console]::OutputEncoding = [System.Text.Encoding]::UTF8
chcp 65001
```

---

## Notas Importantes

### Propósito Académico
Este es un proyecto académico para demostración de análisis de seguridad estático. No reemplaza herramientas profesionales como:
- **Bandit** (análisis de seguridad Python)
- **SonarQube** (análisis de calidad y seguridad)
- **Semgrep** (análisis estático multi-lenguaje)

### Archivos de Prueba
Los archivos `TestDemoVulnerable.py` y `TestPresentacionVulnerable.py` contienen **vulnerabilidades intencionales** con fines educativos. **NO usar este código en producción.**

### Limitaciones
- Análisis estático básico (no ejecuta el código)
- No detecta todas las vulnerabilidades posibles
- Puede generar falsos positivos en casos complejos
- No analiza dependencias externas

### Recomendaciones
1. Usar este analizador como herramienta educativa
2. Complementar con herramientas profesionales
3. Realizar revisiones de código manual
4. Implementar pruebas de seguridad automatizadas
5. Seguir las mejores prácticas de OWASP

---

## Ejemplos de Entrada y Salida

### Ejemplo 1: Archivo Limpio

**Entrada:** `input/ejemplo_limpio.py`
```python
import sqlite3

def buscar_usuario(username):
    conn = sqlite3.connect('db.sqlite')
    cursor = conn.cursor()
    cursor.execute("SELECT * FROM users WHERE username = ?", (username,))
    return cursor.fetchall()
```

**Comando:**
```powershell
java -cp "out;ANTLR/*;gen" Main input/ejemplo_limpio.py
```

**Salida:**
```
Analizando: input/ejemplo_limpio.py

Issues encontrados (ordenados por severidad):

Total: 0 issues
Tiempo estimado de corrección: 0min
```

### Ejemplo 2: Archivo con Vulnerabilidades

**Entrada:** `input/ejemplo_vulnerable.py`
```python
import sqlite3
import hashlib
import random

def procesar_usuario(username, password):
    # SQL Injection
    conn = sqlite3.connect('db.sqlite')
    query = "SELECT * FROM users WHERE username = '" + username + "'"
    conn.execute(query)
    
    # Weak Hash
    pwd_hash = hashlib.md5(password.encode()).hexdigest()
    
    # Insecure Random
    token = str(random.randint(100000, 999999))
    
    return token
```

**Comando:**
```powershell
java -cp "out;ANTLR/*;gen" Main input/ejemplo_vulnerable.py
```

**Salida:**
```
Analizando: input/ejemplo_vulnerable.py

Issues encontrados (ordenados por severidad):

[CRITICAL ] SQLInjectionConcat            linea 9:4 - Consulta SQL construida por concatenacion antes de ejecutarse [60min]
[HIGH     ] WeakHashAlgorithm             linea 12:4 - Algoritmo de hash debil usado en contexto de seguridad: MD5/SHA1 [30min]
[HIGH     ] InsecureRandomForSecrets      linea 15:4 - Generador no criptografico usado para crear secreto en 'token' [30min]

Total: 3 issues
Tiempo estimado de corrección: 2h
```

---

## Contacto y Contribuciones

Este proyecto fue desarrollado como parte de un análisis de seguridad académico para Python.

**Versión:** 1.0  
**Fecha:** Junio 2026  
**Licencia:** MIT

---

## Referencias

- [OWASP Top 10](https://owasp.org/www-project-top-ten/)
- [ANTLR 4 Documentation](https://github.com/antlr/antlr4/blob/master/doc/index.md)
- [Python Security Best Practices](https://python.readthedocs.io/en/stable/library/security_warnings.html)
- [CWE - Common Weakness Enumeration](https://cwe.mitre.org/)