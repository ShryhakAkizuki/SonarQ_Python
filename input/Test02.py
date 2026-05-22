# ===================================================================
# Casos de prueba para un analizador de credenciales hardcodeadas
# ===================================================================

# ------------------- DEBERÍAN GENERAR ALARMA (CRITICAL) -------------------

# 1. Asignación directa con palabra clave clara y valor no trivial
password = "s3cr3tP@ssw0rd"
# → nombre contiene "password", valor cadena con longitud significativa

# 2. API key con nombre en mayúsculas y guión bajo
API_KEY = "a1b2c3d4e5f6g7h8i9j0"
# → contiene "api_key" (normalizado a minúsculas), valor real

# 3. Token en atributo de objeto
user.token = "ghp_abc123DEF456xyz"
# → "token" en el lado izquierdo, valor con longitud > 2

# 4. Diccionario con clave sensible
config["secret"] = "mysecretvalue"
# → el texto del target contiene "secret", valor literal

# 5. Asignación con anotación de tipo
pwd: str = "admin123"
# → target "pwd" contiene palabra clave, valor cadena

# 6. Cadena con prefijo raw (r) - sigue siendo literal
access_key = r"AKIAIOSFODNN7EXAMPLE"
# → "access_key" es palabra clave, valor raw literal

# 7. Cadena con prefijo bytes (b) - literal binario
client_secret = b"6Ld_2gUaAAAAAC5L5xP"
# → "client_secret", valor bytes literal (debe tratarse como cadena)

# 8. f-string sin interpolación (constante)
auth_key = f"fixed_auth_key_123"
# → no tiene placeholders, es equivalente a literal

# 9. Variable con subpalabra dentro de otra
user_password = "my_password_value"
# → contiene "password" como subcadena, debe detectarse

# 10. Asignación a un atributo con nombre compuesto
request.auth_token = "token_value_xyz"
# → "auth_token" contiene "token"

# 11. Cadena con comillas triples (pero sin saltos de línea significativos)
encryption_key = """aes_key_1234567890"""
# → sigue siendo literal de cadena, longitud suficiente

# 12. Variable en mayúsculas con guiones bajos
PRIVATE_KEY = "-----BEGIN RSA PRIVATE KEY-----\nMII..."
# → contiene "private_key"

# 13. Asignación a través de subscript con índice variable (pero el índice es literal)
db["passwd"] = "db_password_123"
# → el target textual completo contiene "passwd"

# ------------------- ✅ NO DEBERÍAN GENERAR ALARMA -------------------

# 1. Valor vacío o demasiado corto
password = ""
# → sin contenido significativo (longitud 0)

pwd = "a"
# → demasiado corto (1 carácter, probable placeholder)

token = "12"
# → longitud 2, demasiado corto para credencial real

# 2. Lado izquierdo sin palabra clave
x = "supersecret"
# → no hay "password", "token", etc.

# 3. Valor no es un literal de cadena (número, booleano, None)
password = 12345
api_key = True
secret = None
# → las credenciales suelen ser cadenas

# 4. Concatenación de cadenas (no es un literal simple)
token = "prefix" + "suffix"
# → expresión dinámica, no literal puro

# 5. f-string con interpolación de variables (valor dinámico)
password = f"{user_input}_default"
# → contiene placeholders, no hardcodeado

# 6. Constante con nombre que incluye "example" o "test" (opcional, según política)
EXAMPLE_PASSWORD = "changeme"
# → por convención, "EXAMPLE_" suele ser seguro; se podría reportar como INFO

# 7. Asignación múltiple (solo se revisa el primer target en implementación simple)
a, password = "user", "realpass"
# → primer target "a" no es sensible; el segundo se pierde. Para evitarlo, la regla debería iterar todos los targets.

# 8. Índice numérico (sin palabra clave)
data[3] = "mypassword"
# → el target textual "data[3]" no contiene palabra clave; si el índice fuera "password" sí alertaría.

# 9. Cadena muy larga pero con comillas triples que es docstring (contexto diferente)
# Nota: si la variable se llama "password", igual alertaría; aquí el contexto es una función
def func():
    """password = 'not real'"""   # no es asignación, es docstring
    pass
# → no es una asignación, el analizador solo mira nodos AssignmentContext

# 10. Valor que es un literal numérico en hexadecimal o binario
token = 0x1234
# → no es cadena

# 11. Cadena con escape que representa caracteres no imprimibles (pero sigue siendo literal)
pwd = "\x00\x01\x02"
# → es literal pero contenido no parece credencial; la longitud >2 pero podría ser falso positivo.
# Depende de la política: se podría alertar o no. Aquí se asume que no (porque no es texto legible).

# 12. Asignación a un atributo de clase (dentro de un método) con valor corto
self.temp_password = "tmp"
# → "tmp" tiene longitud 3, podría considerarse corto (depende del umbral). Si umbral >=3, no alerta.

# 13. Valor que es una llamada a función o expresión aritmética
api_key = get_secret()
password = base64.b64decode("cGFzc3dvcmQ=")
# → no es literal, es dinámico

# 14. Asignación a una variable que comienza con "test_"
test_password = "dummy"
# → nombres con "test_" a menudo se ignoran (opcional)