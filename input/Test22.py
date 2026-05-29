"""
Vulnerabilidad: WeakCryptographyMode.

Caso de estudio: una exportacion de perfiles usa CBC con IV recibido por
parametro, pero no autentica el ciphertext ni aplica MAC. La regla debe alertar
sobre CBC sin autenticacion visible. El ejemplo seguro usa EAX con tag.
"""

from Crypto.Cipher import AES


def cifrar_perfil_vulnerable(profile_bytes, key, iv):
    cipher = AES.new(key, AES.MODE_CBC, iv=iv)
    return cipher.encrypt(profile_bytes)


def cifrar_perfil_seguro(profile_bytes, key, nonce):
    cipher = AES.new(key, AES.MODE_EAX, nonce=nonce)
    ciphertext, tag = cipher.encrypt_and_digest(profile_bytes)
    return ciphertext, tag
