# Test para InsecureCookieConfig - VULNERABLE

def crear_cookie_insegura(response, user_id):
    # HIGH: Cookie sin atributos de seguridad
    response.set_cookie('session_id', user_id)
    
    # HIGH: Cookie sin HttpOnly
    response.set_cookie('user_token', 'abc123', secure=True)
    
    # HIGH: Cookie sin Secure
    response.set_cookie('auth_token', 'xyz789', httponly=True)
    
    # HIGH: Cookie con SameSite=None
    response.set_cookie('tracking', 'data', samesite='None')
    
    return response

if __name__ == "__main__":
    print("Cookies inseguras creadas")

# Made with Bob
