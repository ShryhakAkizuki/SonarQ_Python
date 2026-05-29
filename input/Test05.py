"""
Vulnerabilidad: SQLInjectionConcat.

Este archivo prueba el seguimiento simple de origen de la cadena SQL. La
consulta puede construirse en una variable usando datos externos y ejecutarse
despues con cursor.execute(), conn.execute() u otro metodo similar. Aunque la
llamada de ejecucion reciba solo el nombre de la variable, la consulta sigue
siendo vulnerable si fue formada por concatenacion, interpolacion o formateo.
"""

import sqlite3
from flask import request


def variable_from_concat():
    search = request.args.get("q")
    conn = sqlite3.connect("catalog.db")
    cursor = conn.cursor()
    sql = "SELECT * FROM products WHERE name LIKE '%" + search + "%'"
    cursor.execute(sql)


def variable_from_f_string():
    category = request.args.get("category")
    conn = sqlite3.connect("catalog.db")
    cursor = conn.cursor()
    statement = f"SELECT * FROM products WHERE category = '{category}'"
    cursor.execute(statement)


def variable_from_format_then_alias():
    email = request.args.get("email")
    conn = sqlite3.connect("users.db")
    cursor = conn.cursor()
    base_query = "SELECT * FROM users WHERE email = '{}'".format(email)
    query_to_run = base_query
    cursor.execute(query_to_run)


def variable_from_percent_format():
    user_id = request.args.get("id")
    conn = sqlite3.connect("users.db")
    cursor = conn.cursor()
    query = "DELETE FROM users WHERE id = %s" % user_id
    cursor.execute(query)


def safe_reassigned_before_execute():
    unsafe_id = request.args.get("id")
    conn = sqlite3.connect("users.db")
    cursor = conn.cursor()
    query = "SELECT * FROM users WHERE id = " + unsafe_id
    query = "SELECT * FROM users WHERE active = 1"
    cursor.execute(query)


def safe_concat_not_executed():
    user_id = request.args.get("id")
    query = "SELECT * FROM users WHERE id = " + user_id
    print(query)
