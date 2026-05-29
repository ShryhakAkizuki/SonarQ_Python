"""
Vulnerabilidad: InsecureHttpUsage.

Caso de estudio: un webhook de integracion envia tokens de sincronizacion a un
servicio externo. La variante vulnerable hace POST por HTTP claro. La variante
segura conserva payload y timeout pero usa HTTPS.
"""

import requests


def notificar_webhook_vulnerable(token, event):
    payload = {"token": token, "event": event}
    return requests.post("http://hooks.example.com/webhook", data=payload)


def notificar_webhook_seguro(token, event):
    payload = {"token": token, "event": event}
    return requests.post("https://hooks.example.com/webhook", data=payload, timeout=5)
