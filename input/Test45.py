"""
Vulnerabilidad: UnsafeArchiveExtraction.

Caso de estudio: un panel permite subir ZIPs con plantillas de reportes. Usar
extractall() sin revisar cada nombre interno expone a Zip Slip. El contraste
muestra una validacion previa de prefijos peligrosos antes de extraer.
"""

import zipfile


def instalar_plantilla_vulnerable(path):
    with zipfile.ZipFile(path) as archive:
        archive.extractall("/srv/templates")


def instalar_plantilla_menos_riesgosa(path):
    with zipfile.ZipFile(path) as archive:
        for member in archive.namelist():
            if member.startswith("../") or member.startswith("/"):
                raise ValueError("ruta insegura")
        return archive.namelist()
