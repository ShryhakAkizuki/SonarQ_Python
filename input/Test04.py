"""
Vulnerabilidad: SQLInjectionConcat.

Este archivo muestra un contraste realista entre dos implementaciones de un
flujo de autenticacion. La primera construye la consulta SQL concatenando datos
externos recibidos desde un formulario, lo que permite inyeccion SQL. La segunda
usa parametros de la base de datos, separando la consulta de los valores del
usuario; por eso el analizador no deberia reportarla.
"""

import sqlite3
from flask import request


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


def buscar_usuario_vulnerable():
    user_id = request.args.get("id")
    conn = sqlite3.connect("users.db")
    cursor = conn.cursor()
    cursor.execute(f"SELECT * FROM users WHERE id = {user_id}")
    return cursor.fetchone()


def buscar_usuario_seguro():
    user_id = request.args.get("id")
    conn = sqlite3.connect("users.db")
    cursor = conn.cursor()
    cursor.execute("SELECT * FROM users WHERE id = ?", (user_id,))
    return cursor.fetchone()
