"""
Vulnerabilidad: UnsafeArchiveExtraction.

Caso de estudio: un importador procesa TARs enviados por clientes. La variante
vulnerable llama extractall() directamente. El contraste ilustra una revision
manual de nombres internos antes de extraer.
"""

import tarfile


def importar_tar_vulnerable(path):
    archive = tarfile.open(path)
    archive.extractall("/srv/imports")


def importar_tar_menos_riesgoso(path):
    archive = tarfile.open(path)
    for member in archive.getmembers():
        if member.name.startswith("../") or member.name.startswith("/"):
            raise ValueError("ruta insegura")
    return archive.getmembers()
