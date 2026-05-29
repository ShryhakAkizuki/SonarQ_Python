"""
Vulnerabilidad: InsecureCookieConfig.

Este archivo prueba cookies creadas con Flask. Una cookie de sesion o
autenticacion debe usar HttpOnly para reducir robo por XSS, Secure para evitar
envio por HTTP sin TLS y SameSite para mitigar CSRF. Los primeros casos omiten
uno o varios atributos; los ultimos muestran configuraciones seguras.
"""

from flask import make_response


def login_vulnerable_sin_atributos(token):
    response = make_response("ok")
    response.set_cookie("session", token)
    return response


def login_vulnerable_sin_samesite(token):
    response = make_response("ok")
    response.set_cookie("session", token, httponly=True, secure=True)
    return response


def login_vulnerable_sin_secure(token):
    response = make_response("ok")
    response.set_cookie("session", token, httponly=True, samesite="Lax")
    return response


def login_seguro_lax(token):
    response = make_response("ok")
    response.set_cookie("session", token, httponly=True, secure=True, samesite="Lax")
    return response


def login_seguro_strict(token):
    response = make_response("ok")
    response.set_cookie("session", token, httponly=True, secure=True, samesite="Strict")
    return response
