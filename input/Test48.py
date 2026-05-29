"""
Vulnerabilidad: UnsafeFilePermissions.

Caso de estudio: un proceso crea carpetas compartidas para adjuntos internos.
La variante vulnerable usa mode=0o777 en mkdir(). El contraste crea la carpeta
con permisos limitados para el usuario/grupo de la aplicacion.
"""

import os


def crear_adjuntos_vulnerable(path):
    os.mkdir(path, mode=0o777)


def crear_adjuntos_seguro(path):
    os.mkdir(path, mode=0o750)
