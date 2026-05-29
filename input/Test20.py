"""
Vulnerabilidad: InsecureRandomForSecrets.

Caso de estudio: un job administrativo crea contrasenas temporales para cuentas
de soporte. La variante vulnerable usa random.choice() sobre un alfabeto fijo;
la variante segura usa secrets.choice() y conserva el mismo formato esperado.
"""

import random
import secrets
import string


ALPHABET = string.ascii_letters + string.digits


def crear_password_temporal_vulnerable():
    password = "".join(random.choice(ALPHABET) for _ in range(20))
    return password


def crear_password_temporal_segura():
    password = "".join(secrets.choice(ALPHABET) for _ in range(20))
    return password
