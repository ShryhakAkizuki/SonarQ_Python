"""
Test integral de reglas implementadas.

Este archivo contiene un ejemplo robusto que ejercita todas las reglas actuales:

1. HardcodedCredentials:
   Detecta secretos escritos directamente en variables o atributos.

2. SQLInjectionConcat:
   Detecta consultas SQL construidas con concatenacion, f-strings o format()
   antes de pasarlas a execute(), executescript(), raw() o query().

3. InsecureDeserializationPickle:
   Detecta pickle.load(), pickle.loads() y pickle.Unpickler() cuando reciben
   datos provenientes de request, input, sockets o archivos externos.

4. CyclomaticComplexity:
   Detecta funciones con complejidad ciclomatica elevada y exceso de
   parametros.

5. InsecureYamlLoad:
   Detecta yaml.load(), yaml.full_load() y yaml.unsafe_load() cuando no se usa
   safe_load() o un SafeLoader explicito.

6. InsecureCookieConfig:
   Detecta cookies creadas sin HttpOnly, Secure o SameSite.

El archivo tambien incluye ejemplos seguros para evidenciar contraste: SQL
parametrizado, JSON para datos externos y pickle usado solo con datos internos
controlados.
"""

import json
import pickle
import socket
import sqlite3
import yaml
from flask import request


# ---------------------------------------------------------------------------
# HardcodedCredentials - deberian generar alarma
# ---------------------------------------------------------------------------

DB_PASSWORD = "prod_db_password_2026"
API_KEY = "sk_live_ABC123456789XYZ"
service_token = "service_token_prod_value"


# ---------------------------------------------------------------------------
# SQLInjectionConcat - casos vulnerables y seguros
# ---------------------------------------------------------------------------

def login_vulnerable():
    username = request.form["username"]
    password = request.form["password"]
    conn = sqlite3.connect("users.db")
    cursor = conn.cursor()

    query = (
        "SELECT * FROM users WHERE username = '"
        + username
        + "' AND password = '"
        + password
        + "'"
    )
    cursor.execute(query)
    return cursor.fetchone()


def login_seguro():
    username = request.form["username"]
    password = request.form["password"]
    conn = sqlite3.connect("users.db")
    cursor = conn.cursor()

    query = "SELECT * FROM users WHERE username = ? AND password = ?"
    cursor.execute(query, (username, password))
    return cursor.fetchone()


def buscar_orden_vulnerable():
    order_id = request.args.get("order_id")
    conn = sqlite3.connect("orders.db")
    cursor = conn.cursor()
    cursor.execute(f"SELECT * FROM orders WHERE id = {order_id}")
    return cursor.fetchone()


def borrar_tabla_vulnerable():
    table_name = request.args.get("table")
    conn = sqlite3.connect("admin.db")
    cursor = conn.cursor()
    script = "DROP TABLE " + table_name
    cursor.executescript(script)


def reporte_orm_vulnerable():
    status = request.args.get("status")
    return User.objects.raw("SELECT * FROM users WHERE status = '{}'".format(status))


def buscar_orden_seguro():
    order_id = request.args.get("order_id")
    conn = sqlite3.connect("orders.db")
    cursor = conn.cursor()
    cursor.execute("SELECT * FROM orders WHERE id = ?", (order_id,))
    return cursor.fetchone()


# ---------------------------------------------------------------------------
# InsecureDeserializationPickle - casos vulnerables y seguros
# ---------------------------------------------------------------------------

TRUSTED_PICKLE_BLOB = b"\x80\x04}\x94."


def importar_config_vulnerable():
    payload = request.get_data()
    return pickle.loads(payload)


def importar_config_segura_json():
    payload = request.get_data()
    return json.loads(payload.decode("utf-8"))


def cargar_sesion_desde_archivo_vulnerable(path):
    file_obj = open(path, "rb")
    return pickle.load(file_obj)


def cargar_pickle_controlado():
    return pickle.loads(TRUSTED_PICKLE_BLOB)


def cargar_socket_vulnerable():
    client = socket.socket()
    payload = client.recv(4096)
    return pickle.loads(payload)


def unpickler_vulnerable(path):
    file_obj = open(path, "rb")
    loader = pickle.Unpickler(file_obj)
    return loader.load()


# ---------------------------------------------------------------------------
# InsecureYamlLoad - casos vulnerables y seguros
# ---------------------------------------------------------------------------

def cargar_yaml_vulnerable_http():
    contenido = request.get_data()
    return yaml.load(contenido)


def cargar_yaml_full_load_vulnerable(path):
    file_obj = open(path, "r")
    contenido = file_obj.read()
    return yaml.full_load(contenido)


def cargar_yaml_seguro_http():
    contenido = request.get_data()
    return yaml.safe_load(contenido)


def cargar_yaml_seguro_loader(path):
    file_obj = open(path, "r")
    contenido = file_obj.read()
    return yaml.load(contenido, Loader=yaml.SafeLoader)


# ---------------------------------------------------------------------------
# InsecureCookieConfig - casos vulnerables y seguros
# ---------------------------------------------------------------------------

def emitir_cookie_vulnerable(response, token):
    response.set_cookie("session", token)
    return response


def emitir_cookie_vulnerable_parcial(response, token):
    response.set_cookie("session", token, httponly=True, secure=True)
    return response


def emitir_cookie_segura(response, token):
    response.set_cookie("session", token, httponly=True, secure=True, samesite="Lax")
    return response


# ---------------------------------------------------------------------------
# CyclomaticComplexity - complejidad y exceso de parametros
# ---------------------------------------------------------------------------

def evaluar_transaccion(usuario, monto, pais, moneda, canal, riesgo, historial, dispositivo):
    resultado = "pendiente"

    if usuario is None:
        resultado = "rechazada"
    elif monto <= 0:
        resultado = "rechazada"
    elif monto > 10000:
        if riesgo == "alto" and historial == "nuevo":
            resultado = "revision"
        elif pais not in ("CO", "MX", "PE"):
            resultado = "revision"
    elif moneda not in ("COP", "USD", "EUR"):
        resultado = "rechazada"

    if canal == "web" or canal == "mobile":
        resultado = "aprobada" if resultado == "pendiente" else resultado

    for alerta in historial:
        if alerta == "fraude":
            resultado = "rechazada"
        elif alerta == "contracargo":
            resultado = "revision"

    while riesgo == "medio":
        if dispositivo == "desconocido":
            resultado = "revision"
        break

    try:
        score = int(monto)
    except ValueError:
        score = 0
    except TypeError:
        score = 0

    if score > 5000 and pais == "CO":
        resultado = "revision"

    return resultado


def clasificar_eventos(eventos):
    salida = []
    for evento in eventos:
        if evento.get("tipo") == "login":
            salida.append("auth")
        elif evento.get("tipo") == "pago":
            if evento.get("monto", 0) > 1000 and evento.get("riesgo") != "bajo":
                salida.append("review")
            else:
                salida.append("payment")
        elif evento.get("tipo") == "admin":
            salida.append("admin")
    return salida
