# -------- Casos que si generan alarma --------

password = "secreto123"        # "secreto123" tiene 11 caracteres con comillas -> alarma
API_KEY = "abc123"             # "API_KEY" contiene "api_key" en minúsculas -> alarma
token: str = "xyz789"          # variable con "token" y cadena larga -> alarma
user_passwd = "admin"          # contiene "passwd" -> alarma
a, password = "x", "12345"     # star_targets(0) = "a", que no contiene keyword -> alarma
token = f"valor{var}"          #
api_key = "clave" + "secreta"  # no es un literal simple (contiene +) -> no alarma
pwd = """"""                   # longitud total 6? Depende. Si es vacía con triples comillas, puede tener longitud 6 pero contenido vacío. El código original solo mira longitud total, así que podría dar alarma aunque sea vacío. No es deseable pero por tu pregunta: no queremos alarma para cadenas sin contenido significativo. Con longitud >4 y isStringLit true, SÍ daría alarma. Así que para NO alarma, usar cadena vacía con comillas simples: pwd = "" -> longitud 2.

# -------- Casos que no generan alarma --------
x = "secreto"                  # "x" no contiene ninguna palabra de KEYS -> no alarma
password = 12345               # no es string -> no alarma
pwd = "ab"                     # "ab" con comillas tiene longitud 4 -> no alarma
secret = ""                    # longitud 2 -> no alarma



