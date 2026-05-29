"""
Vulnerabilidad: BasicAuthOverHttp.

Caso de estudio: un cliente de administracion consulta datos usando Basic Auth.
La variante vulnerable manda usuario y password sobre HTTP. El contraste usa el
mismo esquema de autenticacion sobre HTTPS y con timeout.
"""

import requests


def consultar_admin_vulnerable(user, password):
    return requests.get("http://admin.example.com/account", auth=(user, password))


def consultar_admin_seguro(user, password):
    return requests.get("https://admin.example.com/account", auth=(user, password), timeout=5)
