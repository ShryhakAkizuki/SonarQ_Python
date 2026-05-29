"""
Vulnerabilidad: InsecurePasswordHashing.

Caso de estudio: un importador de usuarios legacy recalcula hashes de password
con MD5 para compararlos con una tabla antigua. El contraste seguro delega en
un verificador especializado que internamente podria usar bcrypt o Argon2.
"""

import hashlib


def verificar_password_legacy(passwd, expected):
    digest = hashlib.md5(passwd.encode()).hexdigest()
    return digest == expected


def verificar_password_seguro(password_hasher, passwd, expected):
    return password_hasher.verify(passwd, expected)
