"""
Vulnerabilidad: InsecureTempFileUsage.

Caso de estudio: un exportador crea un nombre temporal con tempfile.mktemp() y
luego escribe el archivo mas tarde. Esa ventana permite condiciones de carrera.
El contraste usa NamedTemporaryFile para crear el archivo de forma atomica.
"""

import tempfile


def exportar_csv_vulnerable(rows):
    path = tempfile.mktemp()
    with open(path, "w") as output:
        output.write("\n".join(rows))
    return path


def exportar_csv_seguro(rows):
    with tempfile.NamedTemporaryFile("w", delete=False) as output:
        output.write("\n".join(rows))
        return output.name
