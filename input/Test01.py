# archivo: app.py
import sqlite3
from flask import Flask, request

app = Flask(__name__)

# Credenciales hardcoded
DB_PASSWORD = "admin123"
API_KEY = "sk_live_51HxYz2KqP9vN8X7K"

# SQL Injection vulnerable
@app.route('/user')
def get_user():
    user_id = request.args.get('id')
    conn = sqlite3.connect('users.db')
    cursor = conn.cursor()
    # Concatenación directa - VULNERABLE
    query = "SELECT * FROM users WHERE id = '" + user_id + "'"
    cursor.execute(query)
    result = cursor.fetchone()
    return str(result)

if __name__ == '__main__':
    app.run(debug=True, host='0.0.0.0')