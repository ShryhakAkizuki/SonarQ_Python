"""
Vulnerabilidad: SQLInjectionConcat.

Este archivo prueba variantes de motores, ORMs y metodos de ejecucion SQL:
execute, executemany, executescript, raw y query. Tambien incluye patrones
seguros con parametros para confirmar que la regla no marque consultas que
separan correctamente el SQL de los datos del usuario.
"""

import sqlite3
from flask import request


def vulnerable_conn_execute():
    username = request.args.get("username")
    conn = sqlite3.connect("users.db")
    conn.execute("SELECT * FROM users WHERE username = '" + username + "'")


def vulnerable_executemany_dynamic_table():
    table_name = request.args.get("table")
    conn = sqlite3.connect("admin.db")
    cursor = conn.cursor()
    sql = f"INSERT INTO {table_name} (name) VALUES (?)"
    cursor.executemany(sql, [("alice",), ("bob",)])


def vulnerable_executescript():
    table_name = request.args.get("table")
    conn = sqlite3.connect("admin.db")
    cursor = conn.cursor()
    script = "DROP TABLE " + table_name
    cursor.executescript(script)


def vulnerable_orm_raw():
    status = request.args.get("status")
    User.objects.raw("SELECT * FROM users WHERE status = '{}'".format(status))


def vulnerable_orm_query():
    customer = request.args.get("customer")
    db.session.query(f"SELECT * FROM orders WHERE customer = '{customer}'")


def safe_named_placeholder():
    username = request.args.get("username")
    conn = sqlite3.connect("users.db")
    cursor = conn.cursor()
    cursor.execute("SELECT * FROM users WHERE username = :username", {"username": username})


def safe_insert_parameterized():
    username = request.form["username"]
    conn = sqlite3.connect("users.db")
    cursor = conn.cursor()
    cursor.execute("INSERT INTO users (username) VALUES (?)", (username,))
