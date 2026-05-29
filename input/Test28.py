"""
Vulnerabilidad: JWTWeakConfiguration.

Caso de estudio: un servicio interno emite JWT para tareas administrativas. La
variante vulnerable firma con secreto literal y algoritmo none. El caso seguro
usa un secreto externo y algoritmo HMAC explicito.
"""

import os
import jwt


def emitir_token_admin_vulnerable(payload):
    return jwt.encode(payload, "jwt-secret-hardcoded", algorithm="none")


def emitir_token_admin_seguro(payload):
    secret = os.environ["JWT_SECRET"]
    return jwt.encode(payload, secret, algorithm="HS256")
