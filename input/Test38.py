"""
Vulnerabilidad: OpenRedirect.

Caso de estudio: un formulario de soporte permite indicar return_to para volver
a una pagina previa. La variante vulnerable redirige directamente el valor del
formulario. La variante segura usa un mapa de destinos internos conocidos.
"""

from flask import redirect, request


ROUTES = {"tickets": "/support/tickets", "home": "/"}


def cerrar_ticket_vulnerable():
    return redirect(request.form.get("return_to"))


def cerrar_ticket_seguro():
    destination = ROUTES.get("tickets", "/")
    return redirect(destination)
