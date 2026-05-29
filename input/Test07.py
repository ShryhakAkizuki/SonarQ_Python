"""
Vulnerabilidad: InsecureDeserializationPickle.

Este archivo contrasta un flujo web vulnerable con uno seguro. El caso
vulnerable deserializa directamente bytes recibidos desde una peticion HTTP con
pickle.loads(), lo que puede ejecutar codigo arbitrario si el payload fue
preparado por un atacante. El caso seguro usa JSON para datos externos y deja
pickle solo para un valor constante controlado por la aplicacion.
"""

import json
import pickle
from flask import request


TRUSTED_CACHE = b"\x80\x04}\x94."


def importar_perfil_vulnerable():
    payload = request.get_data()
    profile = pickle.loads(payload)
    return profile


def importar_perfil_vulnerable_directo():
    return pickle.loads(request.data)


def importar_perfil_seguro_json():
    payload = request.get_data()
    profile = json.loads(payload.decode("utf-8"))
    return profile


def cargar_cache_controlado():
    data = pickle.loads(TRUSTED_CACHE)
    return data
