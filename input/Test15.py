"""
Vulnerabilidad: InsecureCookieConfig.

Este archivo prueba variantes comunes de frameworks. Django usa set_cookie() y
set_signed_cookie(); otros clientes o respuestas exponen cookies.set(). La regla
debe reportar cuando esos metodos crean cookies sin HttpOnly, Secure o SameSite,
y no reportar cuando los tres atributos estan presentes.
"""


def django_set_cookie_vulnerable(response, token):
    response.set_cookie("auth", token, httponly=True)
    return response


def django_set_signed_cookie_vulnerable(response, token):
    response.set_signed_cookie("auth", token, secure=True, samesite="Lax")
    return response


def client_cookies_set_vulnerable(client, token):
    client.cookies.set("session", token, secure=True)
    return client


def django_set_cookie_seguro(response, token):
    response.set_cookie("auth", token, httponly=True, secure=True, samesite="Lax")
    return response


def django_set_signed_cookie_seguro(response, token):
    response.set_signed_cookie("auth", token, httponly=True, secure=True, samesite="Strict")
    return response
