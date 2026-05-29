"""
Vulnerabilidad: DangerousFileDelete.

Caso de estudio: un endpoint elimina adjuntos usando el nombre enviado en query
string. Si el usuario controla la ruta, puede borrar archivos fuera del ambito
esperado. El contraste usa una tabla de adjuntos conocidos.
"""

import os
from flask import request


ATTACHMENTS = {"invoice": "uploads/invoice.pdf", "photo": "uploads/photo.png"}


def borrar_adjunto_vulnerable():
    filename = request.args.get("file")
    os.remove(filename)


def borrar_adjunto_seguro():
    filename = ATTACHMENTS.get(request.args.get("id"), "uploads/photo.png")
    os.remove(filename)
