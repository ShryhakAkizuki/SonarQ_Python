"""
Vulnerabilidad: BasicAuthOverHttp.

Caso de estudio: un proceso de sincronizacion construye un header Bearer y lo
envia a un endpoint HTTP. La regla debe detectar el token aunque el header se
arme en una variable intermedia. El contraste usa HTTPS.
"""

import requests


def sincronizar_token_vulnerable(token):
    headers = {"Authorization": "Bearer " + token}
    return requests.post("http://api.example.com/sync", headers=headers)


def sincronizar_token_seguro(token):
    headers = {"Authorization": "Bearer " + token}
    return requests.post("https://api.example.com/sync", headers=headers, timeout=5)
