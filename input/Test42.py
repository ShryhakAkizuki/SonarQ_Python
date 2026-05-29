"""
Vulnerabilidad: FileOverwriteRisk.

Caso de estudio: un flujo de publicacion reemplaza un archivo temporal por el
destino indicado desde el request. Si el destino es controlado por el usuario,
os.replace() puede sobrescribir archivos no esperados. El contraste resuelve el
destino desde una tabla interna.
"""

import os
from flask import request


DESTINOS = {"avatar": "public/avatar.png", "banner": "public/banner.png"}


def publicar_archivo_vulnerable(tmp_name):
    destination = request.args.get("dest")
    os.replace(tmp_name, destination)


def publicar_archivo_seguro(tmp_name):
    destination = DESTINOS.get("avatar", "public/avatar.png")
    os.replace(tmp_name, destination)
