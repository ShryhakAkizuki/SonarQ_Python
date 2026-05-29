"""
Vulnerabilidad: InsecureCookieConfig.

Este archivo simula un flujo real de autenticacion. El login vulnerable guarda
el token sin atributos suficientes y permite que la cookie viaje por canales no
seguros o sea accesible desde JavaScript. El login seguro establece HttpOnly,
Secure y SameSite. Tambien se prueba SameSite=None como configuracion insegura
si no se quiere permitir contexto cross-site.
"""

from flask import jsonify


def emitir_token_vulnerable(response, token):
    response.set_cookie("access_token", token, secure=True, samesite=None)
    return response


def emitir_preferencias_vulnerable(response, theme):
    response.set_cookie("theme", theme, httponly=True, samesite="Lax")
    return response


def emitir_token_seguro(response, token):
    response.set_cookie(
        "access_token",
        token,
        httponly=True,
        secure=True,
        samesite="Lax",
    )
    return response


def respuesta_sin_cookies():
    return jsonify({"ok": True})
