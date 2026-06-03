# Test para InsecureCookieConfig - SEGURO

def crear_cookie_segura(response, user_id):
    # SEGURO: Cookie con todos los atributos de seguridad
    response.set_cookie(
        'session_id', 
        user_id,
        httponly=True,
        secure=True,
        samesite='Strict'
    )
    
    # SEGURO: Cookie con configuración completa
    response.set_cookie(
        'auth_token',
        'xyz789',
        httponly=True,
        secure=True,
        samesite='Lax'
    )
    
    return response

if __name__ == "__main__":
    print("Cookies seguras creadas")

# Made with Bob
