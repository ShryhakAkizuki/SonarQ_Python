"""
Vulnerabilidad: JWTWeakConfiguration.

Caso de estudio: un gateway acepta tokens JWT de aplicaciones moviles. Durante
una depuracion se dejo la verificacion de firma deshabilitada, lo que permite
aceptar tokens manipulados. El contraste seguro exige algoritmo y audiencia.
"""

import jwt


def leer_token_vulnerable(raw_token):
    claims = jwt.decode(raw_token, options={"verify_signature": False})
    return claims.get("sub")


def leer_token_seguro(raw_token, public_key):
    claims = jwt.decode(
        raw_token,
        public_key,
        algorithms=["RS256"],
        audience="mobile-app",
    )
    return claims.get("sub")
