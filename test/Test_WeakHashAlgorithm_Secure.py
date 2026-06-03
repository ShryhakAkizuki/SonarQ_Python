# Test para WeakHashAlgorithm - SEGURO

import hashlib
import hmac
import os

def hashear_datos_seguro(data):
    # SEGURO: SHA-256 para integridad de datos (no contraseñas)
    sha256_hash = hashlib.sha256(data.encode()).hexdigest()
    return sha256_hash

def hashear_token_seguro(token):
    # SEGURO: HMAC con SHA-256 y clave desde variable de entorno
    secret_key = os.getenv('HMAC_SECRET_KEY', 'default_key_only_for_testing').encode()
    hmac_hash = hmac.new(secret_key, token.encode(), hashlib.sha256).hexdigest()
    return hmac_hash

def hashear_archivo_seguro(file_content):
    # SEGURO: SHA-512 para checksums de archivos
    sha512_hash = hashlib.sha512(file_content.encode()).hexdigest()
    return sha512_hash

def verificar_integridad(data, expected_hash):
    # SEGURO: Verificacion de integridad con SHA-256
    actual_hash = hashlib.sha256(data.encode()).hexdigest()
    return actual_hash == expected_hash

if __name__ == "__main__":
    # Nota: Para contraseñas usar bcrypt, scrypt o Argon2
    # SHA-256/SHA-512 son apropiados para integridad de datos, no contraseñas
    print(hashear_datos_seguro('sensitive_data'))
    print(hashear_token_seguro('abc123'))
    print(hashear_archivo_seguro('file_content'))
    print(verificar_integridad('data', 'expected_hash'))

# Made with Bob
