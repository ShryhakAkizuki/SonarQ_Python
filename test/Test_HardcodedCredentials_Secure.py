# Test para HardcodedCredentials - SEGURO

import os

# SEGURO: Usar variables de entorno en lugar de hardcodear
def conectar_base_datos():
    db_password = os.getenv('DB_PASSWORD', 'default_for_dev')
    connection_string = f"postgresql://admin:{db_password}@localhost:5432/mydb"
    return connection_string

def configurar_api():
    config = {
        'api_key': os.getenv('API_KEY'),
        'secret_key': os.getenv('AWS_SECRET'),
        'password': os.getenv('APP_PASSWORD')
    }
    return config

if __name__ == "__main__":
    print(conectar_base_datos())
    print(configurar_api())

# Made with Bob
