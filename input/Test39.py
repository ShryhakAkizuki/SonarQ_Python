"""
Vulnerabilidad: PathTraversal.

Caso de estudio: un visor de reportes recibe el nombre de archivo desde la URL
y lo concatena con el directorio base. Sin normalizar ni validar el resultado,
un atacante puede intentar ../ para leer archivos fuera de reports. El contraste
usa una allowlist de reportes validos.
"""

import os
from flask import request


REPORTS = {"daily": "daily.pdf", "monthly": "monthly.pdf"}


def leer_reporte_vulnerable():
    name = request.args.get("file")
    path = os.path.join("reports", name)
    return open(path).read()


def leer_reporte_seguro():
    filename = REPORTS.get("daily", "daily.pdf")
    path = os.path.join("reports", filename)
    return open(path).read()
