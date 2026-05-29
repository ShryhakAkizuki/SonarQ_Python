"""
Vulnerabilidad: FileOverwriteRisk.

Caso de estudio: un formulario de carga permite decidir el nombre del archivo de
salida. Abrir esa ruta en modo escritura puede sobrescribir archivos internos si
el usuario controla el destino. El contraste fuerza un nombre generado por la
aplicacion.
"""

from flask import request


def guardar_export_vulnerable(body):
    filename = request.form.get("filename")
    with open(filename, "w") as output:
        output.write(body)


def guardar_export_seguro(body, export_id):
    filename = "exports/export-" + str(export_id) + ".txt"
    with open(filename, "w") as output:
        output.write(body)
