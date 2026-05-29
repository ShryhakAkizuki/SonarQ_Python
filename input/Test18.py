"""
Vulnerabilidad: WeakHashAlgorithm.

Caso de estudio: un microservicio firma callbacks internos con HMAC. La firma
vulnerable usa SHA1 como digest de HMAC para autorizar operaciones sensibles.
El caso seguro conserva el mismo flujo, pero migra el digest a SHA-256.
"""

import hashlib
import hmac


def firmar_callback_vulnerable(secret_key, body):
    signature = hmac.new(secret_key, body, digestmod=hashlib.sha1).hexdigest()
    return {"X-Signature": signature}


def firmar_callback_seguro(secret_key, body):
    signature = hmac.new(secret_key, body, digestmod=hashlib.sha256).hexdigest()
    return {"X-Signature": signature}
