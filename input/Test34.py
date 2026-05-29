"""
Vulnerabilidad: RequestsWithoutTimeout.

Caso de estudio: un worker envia lotes a un proveedor usando httpx. La variante
vulnerable no define timeout y puede quedarse esperando indefinidamente. La
variante segura define un limite explicito.
"""

import httpx


def enviar_lote_vulnerable(endpoint, payload):
    return httpx.post(endpoint, json=payload)


def enviar_lote_seguro(endpoint, payload):
    return httpx.post(endpoint, json=payload, timeout=8)
