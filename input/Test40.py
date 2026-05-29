"""
Vulnerabilidad: PathTraversal.

Caso de estudio: un endpoint de descargas pasa directamente el parametro path a
send_file(). Esto puede exponer archivos arbitrarios si el framework recibe una
ruta absoluta o con traversal. El contraste selecciona el archivo desde un mapa
cerrado de documentos publicos.
"""

from flask import request, send_file


PUBLIC_FILES = {"manual": "public/manual.pdf", "terms": "public/terms.pdf"}


def descargar_vulnerable():
    return send_file(request.args.get("path"))


def descargar_segura():
    filename = PUBLIC_FILES.get("manual", "public/manual.pdf")
    return send_file(filename)
