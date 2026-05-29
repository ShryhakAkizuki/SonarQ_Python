"""
Vulnerabilidad: InsecureHttpUsage.

Caso de estudio: una aplicacion mantiene una URL de login de un proveedor
externo. La variante vulnerable usa http:// para autenticacion y podria exponer
credenciales o tokens en transito. El contraste cambia el endpoint a HTTPS.
"""


def construir_login_vulnerable(tenant):
    login_url = "http://api.example.com/login?tenant=" + tenant
    return login_url


def construir_login_seguro(tenant):
    login_url = "https://api.example.com/login?tenant=" + tenant
    return login_url
