# =============================================================================
#  Referencia de puntos de decisión contados:
#    +1  if / elif / while / for / except / except* / case / guard
#    +N  operadores 'and' / 'or'  (N = número de operadores en la expresión)
#    +1  expresión ternaria  (x if cond else y)
#    +N  cláusulas 'if' en comprehensions  (N = número de filtros)
# =============================================================================


# ─────────────────────────────────────────────────────────────────────────────
# CASO 1 — Sin ramas
# ─────────────────────────────────────────────────────────────────────────────
def trivial(x):
    return x * 2


# ─────────────────────────────────────────────────────────────────────────────
# CASO 2 — Un if simple (el else no cuenta)
# ─────────────────────────────────────────────────────────────────────────────
def solo_if(x):
    if x > 0:         # +1
        return x
    else:             # no cuenta
        return -x


# ─────────────────────────────────────────────────────────────────────────────
# CASO 3 — if + elif + elif
# ─────────────────────────────────────────────────────────────────────────────
def clasificar(nota):
    if nota >= 9:       # +1
        return "A"
    elif nota >= 7:     # +1
        return "B"
    elif nota >= 5:     # +1
        return "C"
    else:               # no cuenta
        return "F"


# ─────────────────────────────────────────────────────────────────────────────
# CASO 4 — Bucles for y while (el else de bucle no cuenta)
# ─────────────────────────────────────────────────────────────────────────────
def recorrer(items, limite):
    resultado = []
    for item in items:      # +1
        resultado.append(item)
    else:                   # no cuenta
        pass
    while len(resultado) > limite:  # +1
        resultado.pop()
    return resultado


# ─────────────────────────────────────────────────────────────────────────────
# CASO 5 — Manejo de excepciones
# ─────────────────────────────────────────────────────────────────────────────
def parsear(valor):
    try:
        return int(valor)
    except ValueError:      # +1
        return 0
    except TypeError:       # +1
        return -1
    finally:                # no cuenta
        pass


# ─────────────────────────────────────────────────────────────────────────────
# CASO 6 — Operadores booleanos and / or
# ─────────────────────────────────────────────────────────────────────────────
def validar(a, b, c, d):
    if a > 0 and b > 0 and c > 0:  # +1 if, +2 and
        return True
    if d or a:                      # +1 if, +1 or
        return False
    return None


# ─────────────────────────────────────────────────────────────────────────────
# CASO 7 — Expresiones ternarias
# ─────────────────────────────────────────────────────────────────────────────
def signo(x):
    etiqueta = "positivo" if x > 0 else "no positivo"    # +1 ternario
    return 1 if x > 0 else (-1 if x < 0 else 0)          # +1 ternario externo


# ─────────────────────────────────────────────────────────────────────────────
# CASO 8 — Comprehension con múltiples filtros 'if'
# ─────────────────────────────────────────────────────────────────────────────
def filtrar(data):
    return [
        x * 2
        for x in data           # +1 for_stmt
        if x > 0                # +1 for_if_clause
        if x < 100              # +1 for_if_clause
    ]


# ─────────────────────────────────────────────────────────────────────────────
# CASO 9 — match / case con guard
# ─────────────────────────────────────────────────────────────────────────────
def despachar(cmd):
    match cmd:
        case "start":                       # +1 case_block
            return "iniciando"
        case "stop":                        # +1 case_block
            return "deteniendo"
        case x if x.startswith("run"):      # +1 case_block, +1 guard
            return f"ejecutando {x}"
        case _:                             # no se cuenta (_  es wildcard)
            return "desconocido"


# ─────────────────────────────────────────────────────────────────────────────
# CASO 10 — Funciones anidadas
# La CC de 'inner' NO se acumula en 'outer'. Cada una tiene su scope.
# ─────────────────────────────────────────────────────────────────────────────
def outer(x, y):
    if x > 0:           # +1  →  outer.CC = 2

        def inner(data):
            result = []
            if not data:        # +1  →  inner.CC = 2
                return result
            for item in data:   # +1  →  inner.CC = 3
                result.append(item)
            while len(result) > 10:  # +1  →  inner.CC = 4
                result.pop()
            return result       # inner reportada como CC=4, ok

    return x + y        # outer reportada como CC=2, ok


# ─────────────────────────────────────────────────────────────────────────────
# CASO 11 — Lambda con scope propio
# La lambda NO altera la CC de 'calcular'.
# ─────────────────────────────────────────────────────────────────────────────
def calcular(items):
    return sorted(items, key=lambda x: x if x >= 0 else -x) #                          lambda: +1 ternario → CC=2 (ok, sin issue)

# ─────────────────────────────────────────────────────────────────────────────
# CASO 12 — Función con CC justo en el límite (CC = 10, no reporta)
# ─────────────────────────────────────────────────────────────────────────────
def en_el_limite(a, b, c, e):
    if a:               # +1
        pass
    elif b:             # +1
        pass
    elif c:             # +1
        pass
    for i in range(10): # +1
        pass
    while e:            # +1
        pass
    try:
        pass
    except Exception:   # +1
        pass
    x = 1 if a else 0   # +1 ternario
    if a and b:         # +1 if, +1 and  → total = 10
        pass
    return x


# ─────────────────────────────────────────────────────────────────────────────
# CASO 13 — Supera CC_MEDIUM (umbral = 10)
# ─────────────────────────────────────────────────────────────────────────────
def compleja_medium(token, mode, state, flags, ctx):
    if token == "A":            # +1
        pass
    elif token == "B":          # +1
        if mode and state:      # +1 if, +1 and
            pass
    elif token == "C":          # +1
        pass
    for c in token:             # +1
        pass
    while state:                # +1
        try:
            pass
        except TypeError:       # +1
            pass
    result = 1 if flags else 0  # +1 ternario
    if mode or ctx:             # +1 if, +1 or  → total acumulado = 11
        pass
    return result


# ─────────────────────────────────────────────────────────────────────────────
# CASO 14 — Supera CC_HIGH (umbral = 20) y contiene muchos parametros
# ─────────────────────────────────────────────────────────────────────────────
def muy_compleja(a, b, c, d, e, f, g, h):
    if a:                           # +1
        if b and c and d:           # +1 if, +3 and
            pass
        elif e:                     # +1 elif
            pass
    elif b:                         # +1 elif
        if f or g:                  # +1 if, +1 or
            pass
        elif h:                     # +1 elif
            if a or b:              # +1 if, +1 or
                pass
    elif c:                         # +1 elif
        pass
    for i in range(a):              # +1
        if d:                       # +1
            pass
    for j in range(b):              # +1
        while c:                    # +1
            try:
                pass
            except ValueError:      # +1
                pass
    while d:                        # +1
        try:
            pass
        except TypeError:           # +1
            pass
    x = a if b else c               # +1 ternario
    y = d if e else f               # +1 ternario
    if g:                           # +1 if  →  total acumulado = 22
        pass
    return x, y