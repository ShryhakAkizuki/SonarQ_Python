# Test para HardcodedCredentials - VULNERABLE

# CRITICAL: Credenciales hardcodeadas en el código
DB_PASSWORD = "SuperSecret123!"
API_KEY = "sk_live_51HxYz1234567890abcdefghijklmnop"
AWS_SECRET = "wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY"

def conectar_base_datos():
    connection_string = f"postgresql://admin:{DB_PASSWORD}@localhost:5432/mydb"
    return connection_string

def configurar_api():
    config = {
        'api_key': API_KEY,
        'secret_key': AWS_SECRET,
        'password': 'hardcoded_pass_2024'
    }
    return config

if __name__ == "__main__":
    print(conectar_base_datos())
    print(configurar_api())

# Made with Bob
