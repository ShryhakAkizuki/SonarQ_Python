"""
Vulnerabilidad: InsecurePasswordHashing.

Caso de estudio: un modulo de registro almacena la huella de la contrasena con
SHA-256 directo. Ese hash rapido facilita ataques offline. El contraste usa
PBKDF2 con salt y muchas iteraciones como minimo aceptable.
"""

import hashlib
import os


def almacenar_password_vulnerable(password):
    return hashlib.sha256(password.encode()).hexdigest()


def almacenar_password_seguro(password):
    salt = os.urandom(16)
    digest = hashlib.pbkdf2_hmac("sha256", password.encode(), salt, 200000)
    return salt, digest
