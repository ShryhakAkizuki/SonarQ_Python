# Test para SQLInjectionConcat - SEGURO

import sqlite3

def buscar_usuario_seguro(username):
    conn = sqlite3.connect('users.db')
    cursor = conn.cursor()
    
    # SEGURO: Consulta parametrizada con placeholder
    query = "SELECT * FROM users WHERE username = ?"
    cursor.execute(query, (username,))
    
    result = cursor.fetchall()
    conn.close()
    return result

def actualizar_email_seguro(user_id, email):
    conn = sqlite3.connect('users.db')
    cursor = conn.cursor()
    
    # SEGURO: UPDATE parametrizado
    sql = "UPDATE users SET email = ? WHERE id = ?"
    cursor.execute(sql, (email, user_id))
    
    conn.commit()
    conn.close()

if __name__ == "__main__":
    print(buscar_usuario_seguro('admin'))
    actualizar_email_seguro(1, 'test@example.com')

# Made with Bob
