"""
Vulnerabilidad: InsecureYamlLoad.

Este archivo simula un flujo real de configuracion. La carga vulnerable usa
yaml.load() sobre contenido recibido por HTTP o leido desde un archivo. Los
casos seguros usan yaml.safe_load() para entradas externas o yaml.load() con
SafeLoader cuando se requiere conservar la API.
"""

import yaml
from flask import request


def cargar_configuracion_vulnerable_http():
    contenido = request.get_data()
    return yaml.load(contenido)


def cargar_configuracion_vulnerable_archivo(path):
    file_obj = open(path, "r")
    contenido = file_obj.read()
    return yaml.full_load(contenido)


def cargar_configuracion_vulnerable_directa(path):
    return yaml.load(open(path, "r").read(), Loader=yaml.Loader)


def cargar_configuracion_segura_http():
    contenido = request.get_data()
    return yaml.safe_load(contenido)


def cargar_configuracion_segura_archivo(path):
    file_obj = open(path, "r")
    contenido = file_obj.read()
    return yaml.load(contenido, Loader=yaml.SafeLoader)
