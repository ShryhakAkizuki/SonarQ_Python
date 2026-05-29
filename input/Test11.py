"""
Vulnerabilidad: InsecureYamlLoad.

Este archivo prueba usos directos del modulo yaml. yaml.load() sin Loader seguro,
yaml.full_load() y yaml.unsafe_load() pueden construir objetos Python a partir
de datos controlados externamente. El contraste seguro usa yaml.safe_load() o
yaml.load() con SafeLoader/CSafeLoader.
"""

import yaml


def vulnerable_load_sin_loader(texto):
    return yaml.load(texto)


def vulnerable_full_load(texto):
    return yaml.full_load(texto)


def vulnerable_unsafe_load(texto):
    return yaml.unsafe_load(texto)


def vulnerable_load_full_loader(texto):
    return yaml.load(texto, Loader=yaml.FullLoader)


def vulnerable_load_unsafe_loader(texto):
    return yaml.load(texto, Loader=yaml.UnsafeLoader)


def seguro_safe_load(texto):
    return yaml.safe_load(texto)


def seguro_load_safe_loader(texto):
    return yaml.load(texto, Loader=yaml.SafeLoader)


def seguro_load_csafe_loader(texto):
    return yaml.load(texto, Loader=yaml.CSafeLoader)
