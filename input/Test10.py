"""
Test integral de reglas implementadas.

Caso de estudio: una plataforma administrativa concentra login, consultas SQL,
importacion de archivos, cookies de sesion, criptografia, llamadas a servicios
externos y gestion de adjuntos. El archivo mezcla flujos vulnerables y algunos
contrastes seguros para evidenciar que el analizador cubre todas las reglas del
proyecto sin depender de ejemplos aislados.

Reglas cubiertas:
HardcodedCredentials, SQLInjectionConcat, InsecureDeserializationPickle,
InsecureYamlLoad, InsecureCookieConfig, CyclomaticComplexity,
WeakHashAlgorithm, InsecureRandomForSecrets, WeakCryptographyMode,
HardcodedCryptoKey, InsecurePasswordHashing, JWTWeakConfiguration,
TLSVerificationDisabled, InsecureHttpUsage, RequestsWithoutTimeout,
BasicAuthOverHttp, OpenRedirect, PathTraversal, FileOverwriteRisk,
InsecureTempFileUsage, UnsafeArchiveExtraction, UnsafeFilePermissions y
DangerousFileDelete.
"""

import hashlib
import hmac
import json
import os
import pickle
import random
import secrets
import shutil
import socket
import sqlite3
import ssl
import tarfile
import tempfile
import zipfile

import httpx
import jwt
import requests
import yaml
from Crypto.Cipher import AES
from flask import redirect, request, send_file


DB_PASSWORD = "prod_db_password_2026"
API_KEY = "sk_live_ABC123456789XYZ"
service_token = "service_token_prod_value"
aes_key = "0123456789abcdef0123456789abcdef"
iv = "0000000000000000"

REPORTS = {"daily": "daily.pdf", "monthly": "monthly.pdf"}
DESTINOS = {"avatar": "public/avatar.png", "banner": "public/banner.png"}
ATTACHMENTS = {"invoice": "uploads/invoice.pdf", "photo": "uploads/photo.png"}
TRUSTED_PICKLE_BLOB = b"\x80\x04}\x94."


# ---------------------------------------------------------------------------
# SQLInjectionConcat - vulnerable y seguro
# ---------------------------------------------------------------------------

def login_vulnerable():
    username = request.form["username"]
    password = request.form["password"]
    conn = sqlite3.connect("users.db")
    cursor = conn.cursor()
    query = "SELECT * FROM users WHERE username = '" + username + "' AND password = '" + password + "'"
    cursor.execute(query)
    return cursor.fetchone()


def login_seguro():
    username = request.form["username"]
    password = request.form["password"]
    conn = sqlite3.connect("users.db")
    cursor = conn.cursor()
    cursor.execute("SELECT * FROM users WHERE username = ? AND password = ?", (username, password))
    return cursor.fetchone()


def borrar_tabla_vulnerable():
    table_name = request.args.get("table")
    conn = sqlite3.connect("admin.db")
    cursor = conn.cursor()
    script = "DROP TABLE " + table_name
    cursor.executescript(script)


# ---------------------------------------------------------------------------
# Deserializacion, YAML y cookies
# ---------------------------------------------------------------------------

def importar_config_pickle_vulnerable():
    payload = request.get_data()
    return pickle.loads(payload)


def importar_config_json_segura():
    payload = request.get_data()
    return json.loads(payload.decode("utf-8"))


def cargar_pickle_socket_vulnerable():
    client = socket.socket()
    payload = client.recv(4096)
    return pickle.loads(payload)


def cargar_yaml_vulnerable():
    contenido = request.get_data()
    return yaml.load(contenido)


def cargar_yaml_seguro():
    contenido = request.get_data()
    return yaml.safe_load(contenido)


def emitir_cookie_vulnerable(response, token):
    response.set_cookie("session", token)
    return response


def emitir_cookie_segura(response, token):
    response.set_cookie("session", token, httponly=True, secure=True, samesite="Lax")
    return response


# ---------------------------------------------------------------------------
# Criptografia, hashes, JWT y secretos
# ---------------------------------------------------------------------------

def validar_checksum_vulnerable(package_bytes, expected_digest):
    digest = hashlib.md5(package_bytes).hexdigest()
    return digest == expected_digest


def firmar_callback_vulnerable(secret_key, body):
    return hmac.new(secret_key, body, digestmod=hashlib.sha1).hexdigest()


def crear_otp_vulnerable():
    otp_code = str(random.randint(100000, 999999))
    return otp_code


def crear_otp_seguro():
    otp_code = str(100000 + secrets.randbelow(900000))
    return otp_code


def cifrar_identificador_vulnerable(customer_id):
    cipher = AES.new(aes_key.encode(), AES.MODE_ECB)
    return cipher.encrypt(customer_id)


def cifrar_perfil_vulnerable(profile_bytes):
    cipher = AES.new(aes_key.encode(), AES.MODE_CBC, iv=iv.encode())
    return cipher.encrypt(profile_bytes)


def almacenar_password_vulnerable(password):
    return hashlib.sha256(password.encode()).hexdigest()


def emitir_jwt_vulnerable(payload):
    return jwt.encode(payload, "jwt-secret-hardcoded", algorithm="none")


def leer_jwt_vulnerable(raw_token):
    return jwt.decode(raw_token, options={"verify_signature": False})


# ---------------------------------------------------------------------------
# Red, TLS, autenticacion y redireccion
# ---------------------------------------------------------------------------

def consultar_factura_tls_vulnerable(invoice_id):
    url = "https://billing.example.com/api/invoices/" + invoice_id
    return requests.get(url, verify=False)


def contexto_ssl_vulnerable():
    context = ssl._create_unverified_context()
    context.check_hostname = False
    return context


def construir_login_http_vulnerable(tenant):
    login_url = "http://api.example.com/login?tenant=" + tenant
    return login_url


def notificar_webhook_http_vulnerable(token, event):
    payload = {"token": token, "event": event}
    return requests.post("http://hooks.example.com/webhook", data=payload)


def consultar_estado_sin_timeout(url):
    return requests.get(url)


def enviar_lote_sin_timeout(endpoint, payload):
    return httpx.post(endpoint, json=payload)


def consultar_admin_http_vulnerable(user, password):
    return requests.get("http://admin.example.com/account", auth=(user, password))


def sincronizar_token_http_vulnerable(token):
    headers = {"Authorization": "Bearer " + token}
    return requests.post("http://api.example.com/sync", headers=headers)


def redirect_vulnerable():
    next_url = request.args.get("next")
    return redirect(next_url)


# ---------------------------------------------------------------------------
# Archivos, rutas, temporales, permisos y borrado
# ---------------------------------------------------------------------------

def leer_reporte_vulnerable():
    name = request.args.get("file")
    path = os.path.join("reports", name)
    return open(path).read()


def descargar_vulnerable():
    return send_file(request.args.get("path"))


def guardar_export_vulnerable(body):
    filename = request.form.get("filename")
    with open(filename, "w") as output:
        output.write(body)


def publicar_archivo_vulnerable(tmp_name):
    destination = request.args.get("dest")
    os.replace(tmp_name, destination)


def exportar_csv_temporal_vulnerable(rows):
    path = tempfile.mktemp()
    with open(path, "w") as output:
        output.write("\n".join(rows))
    return path


def ruta_temporal_predecible(user_id):
    path = "/tmp/export-" + user_id
    return path


def instalar_zip_vulnerable(path):
    with zipfile.ZipFile(path) as archive:
        archive.extractall("/srv/templates")


def importar_tar_vulnerable(path):
    archive = tarfile.open(path)
    archive.extractall("/srv/imports")


def preparar_directorio_vulnerable(path):
    os.chmod(path, 0o777)
    os.mkdir(path + "/public", mode=0o777)


def borrar_adjunto_vulnerable():
    filename = request.args.get("file")
    os.remove(filename)


def limpiar_arbol_vulnerable():
    target = request.form.get("target")
    shutil.rmtree(target)


# ---------------------------------------------------------------------------
# Controles seguros para contraste
# ---------------------------------------------------------------------------

def flujo_seguro_basico(password, salt, package_bytes, expected_digest, url):
    digest = hashlib.sha256(package_bytes).hexdigest()
    password_digest = hashlib.pbkdf2_hmac("sha256", password.encode(), salt, 200000)
    response = requests.get(url, timeout=5, verify=True)
    return digest == expected_digest, password_digest, response


def leer_reporte_seguro():
    filename = REPORTS.get("daily", "daily.pdf")
    path = os.path.join("reports", filename)
    return open(path).read()


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
