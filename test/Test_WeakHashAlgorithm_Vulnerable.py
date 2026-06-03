# Test para WeakHashAlgorithm - VULNERABLE

import hashlib

def hashear_password(password):
    # HIGH: MD5 para contraseñas
    md5_hash = hashlib.md5(password.encode()).hexdigest()
    return md5_hash

def hashear_token(token):
    # HIGH: SHA1 para tokens
    sha1_hash = hashlib.sha1(token.encode()).hexdigest()
    return sha1_hash

def hashear_datos(data):
    # HIGH: Uso de hashlib.new() con algoritmo débil
    weak_hash = hashlib.new('md5', data.encode()).hexdigest()
    return weak_hash

if __name__ == "__main__":
    print(hashear_password('secret123'))
    print(hashear_token('abc123'))
    print(hashear_datos('sensitive_data'))

# Made with Bob
