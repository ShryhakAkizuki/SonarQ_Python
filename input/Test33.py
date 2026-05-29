"""
Vulnerabilidad: RequestsWithoutTimeout.

Caso de estudio: el dashboard consulta un API remoto para mostrar estado de
ordenes. Sin timeout, una conexion colgada puede bloquear workers completos. El
contraste agrega timeout manteniendo la misma llamada.
"""

import requests


def consultar_estado_vulnerable(url):
    response = requests.get(url)
    return response.json()


def consultar_estado_seguro(url):
    response = requests.get(url, timeout=3)
    return response.json()
