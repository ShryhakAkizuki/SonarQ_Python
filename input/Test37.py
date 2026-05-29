"""
Vulnerabilidad: OpenRedirect.

Caso de estudio: despues del login, la aplicacion redirige al valor next que
viene en query string. Si no se valida contra rutas internas permitidas, un
atacante puede abusar del dominio confiable para phishing. El contraste aplica
una allowlist simple de rutas locales.
"""

from flask import redirect, request


def login_vulnerable():
    next_url = request.args.get("next")
    return redirect(next_url)


def login_seguro():
    next_url = request.args.get("next")
    if next_url not in ("/dashboard", "/profile"):
        next_url = "/dashboard"
    return redirect(next_url)
