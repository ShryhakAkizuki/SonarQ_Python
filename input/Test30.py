"""
Vulnerabilidad: TLSVerificationDisabled.

Caso de estudio: un conector MQTT crea manualmente el contexto SSL. La variante
vulnerable usa _create_unverified_context() y desactiva hostname checking. La
variante segura usa create_default_context() con validacion normal.
"""

import ssl


def contexto_mqtt_vulnerable():
    context = ssl._create_unverified_context()
    context.check_hostname = False
    return context


def contexto_mqtt_seguro():
    context = ssl.create_default_context()
    context.check_hostname = True
    return context
