"""
Vulnerabilidad: InsecureYamlLoad.

Este archivo prueba alias e imports directos. Un atacante puede esconder el uso
inseguro con import yaml as y o from yaml import load/full_load/unsafe_load.
La regla debe reconocer esos nombres y no reportar safe_load.
"""

import yaml as y
from yaml import load, full_load, unsafe_load, safe_load
from yaml import load as yaml_load_alias
from yaml import full_load as full_load_alias


def vulnerable_alias_modulo(texto):
    return y.load(texto)


def vulnerable_import_load(texto):
    return load(texto)


def vulnerable_import_full_load(texto):
    return full_load(texto)


def vulnerable_import_unsafe_load(texto):
    return unsafe_load(texto)


def vulnerable_import_load_alias(texto):
    return yaml_load_alias(texto)


def vulnerable_import_full_load_alias(texto):
    return full_load_alias(texto)


def seguro_import_safe_load(texto):
    return safe_load(texto)


def seguro_alias_modulo_safe_load(texto):
    return y.safe_load(texto)
