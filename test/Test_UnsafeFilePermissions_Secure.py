# Test para UnsafeFilePermissions - SEGURO

import os

def crear_archivo_seguro(filename):
    # SEGURO: Permisos 600 (solo propietario puede leer/escribir)
    with open(filename, 'w') as f:
        f.write('datos sensibles')
    os.chmod(filename, 0o600)

def crear_directorio_seguro(dirname):
    # SEGURO: Directorio con permisos 700 (solo propietario)
    os.mkdir(dirname, 0o700)

def abrir_archivo_seguro(filename):
    # SEGURO: Abrir archivo con permisos restrictivos
    fd = os.open(filename, os.O_CREAT | os.O_WRONLY, 0o600)
    os.close(fd)

if __name__ == "__main__":
    print("Archivos con permisos seguros")

# Made with Bob
