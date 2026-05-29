"""
Vulnerabilidad: HardcodedCryptoKey.

Caso de estudio: una rutina de migracion conserva compatibilidad con datos
antiguos usando un IV fijo. Un IV constante rompe las propiedades esperadas del
cifrado. El contraste genera un nonce nuevo con secrets.token_bytes().
"""

import secrets


def obtener_iv_vulnerable():
    iv = "0000000000000000"
    return iv.encode()


def obtener_iv_seguro():
    iv = secrets.token_bytes(16)
    return iv
