"""
Test54.py - Código Limpio Sin Deuda Técnica
============================================
Este archivo demuestra código Python de alta calidad que NO genera
ninguna alerta de las 9 reglas de deuda técnica implementadas.

Resultado esperado: 0 issues, 0min de deuda técnica
"""

import os
from pathlib import Path
from typing import List, Dict, Optional


# ============================================================================
# CONSTANTES - Sin números mágicos
# ============================================================================

MAX_RETRY_ATTEMPTS = 3
DEFAULT_TIMEOUT_SECONDS = 30
CACHE_SIZE_MB = 100
MIN_PASSWORD_LENGTH = 8
MAX_FILE_SIZE_MB = 50
DISCOUNT_RATE = 0.15
TAX_RATE = 0.19
BASE_PRICE = 1000.0
EXAMPLE_ORDER_AMOUNT_1 = 100.0
EXAMPLE_ORDER_AMOUNT_2 = 200.0


# ============================================================================
# CLASES - Encapsulación para evitar muchos parámetros
# ============================================================================

class UserAccount:
    """Representa una cuenta de usuario."""
    
    def __init__(self, username: str, email: str):
        self.username = username
        self.email = email
        self.is_active = True
    
    def deactivate(self) -> None:
        """Desactiva la cuenta."""
        self.is_active = False
    
    def validate_email(self) -> bool:
        """Valida formato del email."""
        return '@' in self.email and '.' in self.email


class OrderProcessor:
    """Procesa pedidos - evita código duplicado."""
    
    def __init__(self, client: str, amount: float):
        self.client = client
        self.amount = amount
    
    def create_order(self) -> Dict[str, any]:
        """Crea un pedido base."""
        return {
            'client': self.client,
            'amount': self.amount,
            'status': 'pending',
            'processed': False
        }


class Counter:
    """Contador sin variables globales."""
    
    def __init__(self):
        self.count = 0
    
    def increment(self) -> int:
        """Incrementa el contador."""
        self.count += 1
        return self.count


# ============================================================================
# FUNCIONES - Cortas, sin anidamiento, sin código muerto
# ============================================================================

def calculate_price_with_discount(price: float) -> float:
    """
    Calcula precio con descuento.
    Función corta (<50 líneas), sin números mágicos.
    """
    discount = price * DISCOUNT_RATE
    return price - discount


def calculate_price_with_tax(price: float) -> float:
    """Calcula precio con impuesto."""
    tax = price * TAX_RATE
    return price + tax


def validate_password(password: str) -> bool:
    """
    Valida contraseña.
    Sin anidamiento excesivo - usa early returns.
    """
    if len(password) < MIN_PASSWORD_LENGTH:
        return False
    
    if not any(c.isdigit() for c in password):
        return False
    
    if not any(c.isupper() for c in password):
        return False
    
    if not any(c.islower() for c in password):
        return False
    
    return True


def filter_active_users(users: List[UserAccount]) -> List[UserAccount]:
    """Filtra usuarios activos."""
    return [user for user in users if user.is_active]


def process_file_safely(file_path: str) -> Optional[str]:
    """
    Procesa archivo con manejo de errores.
    Sin código muerto después de return.
    """
    path = Path(file_path)
    
    if not path.exists():
        return None
    
    if not path.is_file():
        return None
    
    try:
        content = path.read_text(encoding='utf-8')
        return content
    except (IOError, UnicodeDecodeError):
        return None


def check_file_exists(file_path: str) -> bool:
    """Verifica si archivo existe - usa imports."""
    return os.path.exists(file_path) and Path(file_path).is_file()


def format_currency(amount: float) -> str:
    """Formatea monto como moneda."""
    return f"${amount:,.2f}"


# ============================================================================
# FUNCIÓN PRINCIPAL - Sin comentarios pendientes (TODO/FIXME)
# ============================================================================

def demo_users() -> None:
    """Demuestra manejo de usuarios."""
    user1 = UserAccount("alice", "alice@example.com")
    user2 = UserAccount("bob", "bob@example.com")
    user3 = UserAccount("charlie", "charlie@example.com")
    
    user2.deactivate()
    
    all_users = [user1, user2, user3]
    active_users = filter_active_users(all_users)
    print(f"Usuarios activos: {len(active_users)}")


def demo_pricing() -> None:
    """Demuestra cálculo de precios."""
    price = BASE_PRICE
    discounted = calculate_price_with_discount(price)
    final = calculate_price_with_tax(discounted)
    
    print(f"Precio base: {format_currency(price)}")
    print(f"Con descuento: {format_currency(discounted)}")
    print(f"Precio final: {format_currency(final)}")


def demo_orders() -> None:
    """Demuestra procesamiento de pedidos."""
    processor1 = OrderProcessor("Juan", EXAMPLE_ORDER_AMOUNT_1)
    processor2 = OrderProcessor("Maria", EXAMPLE_ORDER_AMOUNT_2)
    
    order1 = processor1.create_order()
    order2 = processor2.create_order()
    print(f"Pedidos creados: {len([order1, order2])}")


def demo_files() -> None:
    """Demuestra operaciones con archivos."""
    exists = check_file_exists("data.txt")
    print(f"Archivo existe: {exists}")
    
    content = process_file_safely("config.json")
    if content:
        print(f"Archivo procesado: {len(content)} caracteres")
    else:
        print("Archivo no encontrado")


def main() -> None:
    """Función principal - orquesta las demos."""
    demo_users()
    demo_pricing()
    
    test_password = "SecurePass123"
    is_valid = validate_password(test_password)
    print(f"Contraseña válida: {is_valid}")
    
    demo_orders()
    
    counter = Counter()
    counter.increment()
    counter.increment()
    print(f"Contador: {counter.count}")
    
    demo_files()


if __name__ == "__main__":
    main()

# Made with Bob
