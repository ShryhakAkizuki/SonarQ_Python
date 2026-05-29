"""
Vulnerabilidad: DangerousFileDelete.

Caso de estudio: una tarea de limpieza borra arboles completos con rmtree()
usando un destino recibido del formulario. Esto puede destruir directorios
arbitrarios si no hay validacion. El contraste selecciona un directorio interno.
"""

import shutil
from flask import request


CLEANUP_TARGETS = {"cache": "var/cache/session", "tmp": "var/tmp/uploads"}


def limpiar_arbol_vulnerable():
    target = request.form.get("target")
    shutil.rmtree(target)


def limpiar_arbol_seguro():
    target = CLEANUP_TARGETS.get(request.form.get("target"), "var/tmp/uploads")
    shutil.rmtree(target)
