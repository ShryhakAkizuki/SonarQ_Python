# Test para UnsafeFilePermissions - VULNERABLE

import os

def crear_archivo_inseguro(filename):
    # HIGH: Permisos 777 (lectura, escritura, ejecución para todos)
    with open(filename, 'w') as f:
        f.write('datos sensibles')
    os.chmod(filename, 0o777)

def crear_directorio_inseguro(dirname):
    # HIGH: Directorio con permisos 777
    os.mkdir(dirname, 0o777)

def abrir_archivo_inseguro(filename):
    # HIGH: Abrir archivo con permisos amplios
    fd = os.open(filename, os.O_CREAT | os.O_WRONLY, 0o666)
    os.close(fd)

if __name__ == "__main__":
    print("Archivos con permisos inseguros")

# Made with Bob
