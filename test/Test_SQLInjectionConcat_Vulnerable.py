# Test para SQLInjectionConcat - VULNERABLE

import sqlite3

def buscar_usuario(username):
    conn = sqlite3.connect('users.db')
    cursor = conn.cursor()
    
    # CRITICAL: SQL injection mediante concatenación
    query = "SELECT * FROM users WHERE username = '" + username + "'"
    cursor.execute(query)
    
    result = cursor.fetchall()
    conn.close()
    return result

def actualizar_email(user_id, email):
    conn = sqlite3.connect('users.db')
    cursor = conn.cursor()
    
    # CRITICAL: SQL injection con f-string
    sql = f"UPDATE users SET email = '{email}' WHERE id = {user_id}"
    cursor.execute(sql)
    
    conn.commit()
    conn.close()

if __name__ == "__main__":
    print(buscar_usuario('admin'))
    actualizar_email(1, 'test@example.com')

# Made with Bob
