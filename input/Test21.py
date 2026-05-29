"""
Vulnerabilidad: WeakCryptographyMode.

Caso de estudio: un componente legado cifra identificadores de clientes antes
de guardarlos en una tabla auxiliar. El modo ECB filtra patrones entre bloques
iguales. El contraste seguro usa GCM, que cifra y autentica el mensaje.
"""

from Crypto.Cipher import AES


def cifrar_identificador_vulnerable(customer_id, key):
    cipher = AES.new(key, AES.MODE_ECB)
    return cipher.encrypt(customer_id)


def cifrar_identificador_seguro(customer_id, key, nonce):
    cipher = AES.new(key, AES.MODE_GCM, nonce=nonce)
    ciphertext, tag = cipher.encrypt_and_digest(customer_id)
    return ciphertext, tag
