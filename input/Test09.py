"""
Vulnerabilidad: InsecureDeserializationPickle.

Este archivo prueba fuentes externas no HTTP: input(), sockets y el uso
explicito de pickle.Unpickler. Estos origenes son no confiables porque el valor
puede ser controlado fuera del programa. La regla debe reportar cuando esos
datos llegan a pickle.loads(), pickle.load() o pickle.Unpickler().
"""

import pickle
import socket


def cargar_desde_input_vulnerable():
    payload = input("payload: ")
    return pickle.loads(payload)


def cargar_desde_socket_vulnerable():
    sock = socket.socket()
    payload = sock.recv(4096)
    return pickle.loads(payload)


def unpickler_con_archivo_vulnerable(path):
    file_obj = open(path, "rb")
    unpickler = pickle.Unpickler(file_obj)
    return unpickler.load()


def socket_seguro_sin_pickle():
    sock = socket.socket()
    payload = sock.recv(4096)
    return payload.decode("utf-8")


def pickle_seguro_con_dato_interno():
    trusted = b"\x80\x04}\x94."
    return pickle.loads(trusted)
