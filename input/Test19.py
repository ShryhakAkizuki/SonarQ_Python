"""
Vulnerabilidad: InsecureRandomForSecrets.

Caso de estudio: una vista de recuperacion de cuenta genera codigos OTP para
enviar por correo. El flujo vulnerable usa random.randint(), predecible para
secretos. El flujo seguro usa secrets.randbelow(), pensado para valores de
seguridad.
"""

import random
import secrets


def enviar_otp_vulnerable(mailer, user):
    otp_code = str(random.randint(100000, 999999))
    mailer.send(user.email, "Codigo de acceso", otp_code)
    return otp_code


def enviar_otp_seguro(mailer, user):
    otp_code = str(100000 + secrets.randbelow(900000))
    mailer.send(user.email, "Codigo de acceso", otp_code)
    return otp_code
