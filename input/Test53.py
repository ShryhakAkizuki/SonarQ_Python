# Test53.py - Suite de Pruebas de Deuda Técnica
# ================================================
# Este archivo contiene ejemplos de TODAS las 9 reglas de deuda técnica
# implementadas en SonarQ_Python

import os
import sys
import json
import datetime
from pathlib import Path

# ============================================================================
# REGLA 1: LongMethodRule - Funciones Largas
# HIGH (>50 líneas), CRITICAL (>100 líneas)
# ============================================================================

def funcion_muy_larga():
    """Esta función tiene 55 líneas - debería reportar HIGH"""
    resultado = "inicio"
    resultado = resultado + " procesando"
    resultado = resultado + " datos"
    resultado = resultado + " de"
    resultado = resultado + " entrada"
    resultado = resultado + " validando"
    resultado = resultado + " formato"
    resultado = resultado + " verificando"
    resultado = resultado + " integridad"
    resultado = resultado + " calculando"
    resultado = resultado + " valores"
    resultado = resultado + " intermedios"
    resultado = resultado + " aplicando"
    resultado = resultado + " transformaciones"
    resultado = resultado + " normalizando"
    resultado = resultado + " resultados"
    resultado = resultado + " generando"
    resultado = resultado + " reporte"
    resultado = resultado + " preliminar"
    resultado = resultado + " revisando"
    resultado = resultado + " consistencia"
    resultado = resultado + " ajustando"
    resultado = resultado + " parametros"
    resultado = resultado + " optimizando"
    resultado = resultado + " rendimiento"
    resultado = resultado + " finalizando"
    resultado = resultado + " proceso"
    resultado = resultado + " guardando"
    resultado = resultado + " estado"
    resultado = resultado + " limpiando"
    resultado = resultado + " recursos"
    resultado = resultado + " cerrando"
    resultado = resultado + " conexiones"
    resultado = resultado + " liberando"
    resultado = resultado + " memoria"
    resultado = resultado + " registrando"
    resultado = resultado + " eventos"
    resultado = resultado + " notificando"
    resultado = resultado + " usuarios"
    resultado = resultado + " actualizando"
    resultado = resultado + " cache"
    resultado = resultado + " sincronizando"
    resultado = resultado + " datos"
    resultado = resultado + " externos"
    resultado = resultado + " validando"
    resultado = resultado + " salida"
    resultado = resultado + " completando"
    resultado = resultado + " transaccion"
    resultado = resultado + " confirmando"
    resultado = resultado + " cambios"
    resultado = resultado + " fin"
    return resultado


# ============================================================================
# REGLA 2: MagicNumbersRule - Números Mágicos (LOW)
# ============================================================================

def calcular_precio():
    """Números mágicos sin constantes"""
    precio = 1000  # ❌ Número mágico
    descuento = precio * 0.15  # ❌ Número mágico
    impuesto = precio * 0.19  # ❌ Número mágico
    return precio - descuento + impuesto


# ============================================================================
# REGLA 3: TodoCommentsRule - Comentarios Pendientes
# INFO (TODO), LOW (FIXME), MEDIUM (HACK/XXX/BUG)
# ============================================================================

def funcion_con_pendientes():
    """Comentarios que indican trabajo pendiente"""
    # TODO: Implementar validación de entrada
    datos = obtener_datos()
    
    # FIXME: Este cálculo no funciona con negativos
    resultado = datos * 2
    
    # HACK: Solución temporal hasta refactorizar
    if resultado < 0:
        resultado = 0
    
    # XXX: Puede fallar con valores grandes
    # BUG: Falla cuando datos es None
    return resultado

def obtener_datos():
    return 10


# ============================================================================
# REGLA 4: DeepNestingRule - Anidamiento Excesivo
# MEDIUM (>4 niveles), HIGH (>6 niveles)
# ============================================================================

def validar_con_anidamiento(usuario, pedido, pago, config, estado):
    """Anidamiento de 5 niveles - MEDIUM"""
    if usuario:
        if usuario.get('activo'):
            if pedido:
                if pedido.get('valido'):
                    if pago:  # ❌ 5 niveles
                        return True
    return False


# ============================================================================
# REGLA 5: CodeDuplicationRule - Código Duplicado (MEDIUM)
# ============================================================================

def procesar_pedido_a(cliente, monto):
    """Primera función con código duplicado"""
    pedido = {}
    pedido['cliente'] = cliente
    pedido['monto'] = monto
    pedido['estado'] = 'pendiente'
    pedido['fecha'] = 'hoy'
    pedido['procesado'] = False
    return pedido

def procesar_pedido_b(cliente, monto):
    """Segunda función con código duplicado"""
    pedido = {}
    pedido['cliente'] = cliente
    pedido['monto'] = monto
    pedido['estado'] = 'pendiente'
    pedido['fecha'] = 'hoy'
    pedido['procesado'] = False
    return pedido


# ============================================================================
# REGLA 6: LongParameterListRule - Muchos Parámetros
# LOW (>5), MEDIUM (>7)
# ============================================================================

def funcion_con_muchos_parametros(p1, p2, p3, p4, p5, p6):
    """6 parámetros - LOW"""
    return p1 + p2 + p3 + p4 + p5 + p6

def funcion_con_demasiados_parametros(p1, p2, p3, p4, p5, p6, p7, p8):
    """8 parámetros - MEDIUM"""
    return p1 + p2 + p3 + p4 + p5 + p6 + p7 + p8


# ============================================================================
# REGLA 7: GlobalVariableRule - Variables Globales
# MEDIUM (global), LOW (nonlocal)
# ============================================================================

contador = 0
estado = "iniciando"

def incrementar_contador():
    """Uso de variable global - MEDIUM"""
    global contador  # ❌ MEDIUM
    contador += 1
    return contador

def cambiar_estado(nuevo):
    """Uso de variable global - MEDIUM"""
    global estado  # ❌ MEDIUM
    estado = nuevo

def crear_contador():
    """Uso de nonlocal - LOW"""
    cuenta = 0
    def incrementar():
        nonlocal cuenta  # ❌ LOW
        cuenta += 1
        return cuenta
    return incrementar


# ============================================================================
# REGLA 8: UnusedImportsRule - Imports No Usados (LOW)
# ============================================================================

# sys, json, datetime NO se usan - deberían reportarse como LOW
# os y Path SÍ se usan

def verificar_archivo(ruta):
    """Solo usa os y Path"""
    if os.path.exists(ruta):
        p = Path(ruta)
        return p.is_file()
    return False


# ============================================================================
# REGLA 9: DeadCodeRule - Código Muerto
# MEDIUM (después de return), LOW (if False)
# ============================================================================

def funcion_con_codigo_muerto():
    """Código después de return - MEDIUM"""
    resultado = calcular_total([1, 2, 3])
    return resultado
    # ❌ MEDIUM: Código inalcanzable
    print("Esto nunca se ejecuta")
    resultado = resultado * 2
    return resultado

def calcular_total(items):
    return sum(items)

def funcion_con_bloque_muerto():
    """Bloques que nunca se ejecutan - LOW"""
    datos = [1, 2, 3]
    
    if False:  # ❌ LOW
        print("Nunca se ejecuta")
        datos = []
    
    if 0:  # ❌ LOW
        print("Tampoco se ejecuta")
        datos = None
    
    return datos


# ============================================================================
# FUNCIÓN PRINCIPAL
# ============================================================================

if __name__ == "__main__":
    print("=== Test de Deuda Técnica ===")
    
    # Ejecutar funciones con problemas
    funcion_muy_larga()
    calcular_precio()
    funcion_con_pendientes()
    validar_con_anidamiento({'activo': True}, {'valido': True}, True, {}, {})
    procesar_pedido_a("Juan", 100)
    funcion_con_muchos_parametros(1, 2, 3, 4, 5, 6)
    incrementar_contador()
    verificar_archivo("test.txt")
    funcion_con_codigo_muerto()
    
    print("Análisis completado")

# Made with Bob
