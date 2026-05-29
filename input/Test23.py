"""
Vulnerabilidad: HardcodedCryptoKey.

Caso de estudio: un adaptador de pagos cifra metadatos antes de enviarlos al
proveedor. La clave AES queda embebida como literal en el repositorio. El caso
seguro obtiene el material criptografico desde una variable de entorno.
"""

import os
from Crypto.Cipher import AES


def cifrar_pago_vulnerable(payload):
    aes_key = "0123456789abcdef0123456789abcdef"
    cipher = AES.new(aes_key.encode(), AES.MODE_GCM)
    return cipher.encrypt(payload)


def cifrar_pago_seguro(payload):
    aes_key = os.environ["PAYMENT_AES_KEY"].encode()
    cipher = AES.new(aes_key, AES.MODE_GCM)
    return cipher.encrypt(payload)
