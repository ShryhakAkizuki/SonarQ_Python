"""
Vulnerabilidad: TLSVerificationDisabled.

Caso de estudio: un cliente de facturacion consume el API del proveedor. Para
evitar errores de certificados en pruebas, el codigo dejo verify=False en una
llamada real. El contraste seguro mantiene validacion TLS y timeout explicito.
"""

import requests


def consultar_factura_vulnerable(invoice_id):
    url = "https://billing.example.com/api/invoices/" + invoice_id
    response = requests.get(url, verify=False)
    return response.json()


def consultar_factura_segura(invoice_id):
    url = "https://billing.example.com/api/invoices/" + invoice_id
    response = requests.get(url, verify=True, timeout=10)
    return response.json()
