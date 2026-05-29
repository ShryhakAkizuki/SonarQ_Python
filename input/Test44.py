"""
Vulnerabilidad: InsecureTempFileUsage.

Caso de estudio: un job arma manualmente rutas bajo /tmp usando un identificador
externo. Los nombres predecibles en directorios compartidos pueden provocar
colisiones o ataques de enlace simbolico. El contraste usa mkstemp().
"""

import os
import tempfile


def ruta_temporal_vulnerable(user_id):
    path = "/tmp/export-" + user_id
    return path


def ruta_temporal_segura():
    fd, path = tempfile.mkstemp(prefix="export-")
    os.close(fd)
    return path
