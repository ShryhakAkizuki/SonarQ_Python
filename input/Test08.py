"""
Vulnerabilidad: InsecureDeserializationPickle.

Este archivo prueba datos provenientes de archivos. Deserializar con
pickle.load() un archivo recibido, subido o abierto desde una ruta externa es
peligroso porque el contenido del archivo puede controlar objetos y funciones
ejecutadas durante la deserializacion. Tambien se incluyen lecturas seguras que
usan JSON o datos constantes para mostrar la no deteccion.
"""

import json
import pickle


def cargar_archivo_vulnerable_por_open(path):
    file_obj = open(path, "rb")
    return pickle.load(file_obj)


def cargar_archivo_vulnerable_directo(path):
    return pickle.load(open(path, "rb"))


def cargar_bytes_de_archivo_vulnerable(path):
    file_obj = open(path, "rb")
    raw = file_obj.read()
    return pickle.loads(raw)


def cargar_archivo_seguro_json(path):
    file_obj = open(path, "r")
    return json.load(file_obj)


def cargar_pickle_constante_controlado():
    trusted_blob = b"\x80\x04}\x94."
    return pickle.loads(trusted_blob)
