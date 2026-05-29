"""
Vulnerabilidad: UnsafeFilePermissions.

Caso de estudio: un instalador deja un directorio de datos con permisos 777 para
evitar problemas operativos. Esto permite lectura y escritura por usuarios no
autorizados. El contraste usa permisos restringidos.
"""

import os


def preparar_directorio_vulnerable(path):
    os.chmod(path, 0o777)


def preparar_directorio_seguro(path):
    os.chmod(path, 0o750)
