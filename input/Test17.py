"""
Vulnerabilidad: WeakHashAlgorithm.

Caso de estudio: un servicio de descargas calcula la huella de integridad de
archivos enviados por proveedores externos. El flujo vulnerable usa MD5 para
validar que el paquete no fue modificado, lo cual no sirve para integridad
fuerte porque MD5 permite colisiones practicas. El contraste seguro usa SHA-256.
"""

import hashlib


def validar_paquete_vulnerable(package_bytes, expected_digest):
    digest = hashlib.md5(package_bytes).hexdigest()
    if digest != expected_digest:
        raise ValueError("paquete alterado")
    return True


def validar_paquete_seguro(package_bytes, expected_digest):
    digest = hashlib.sha256(package_bytes).hexdigest()
    return digest == expected_digest
