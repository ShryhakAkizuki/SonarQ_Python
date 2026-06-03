# Test para LongMethod - SEGURO

def funcion_corta_1():
    # SEGURO: Función con menos de 50 líneas
    resultado = 0
    for i in range(10):
        resultado += i
    return resultado

def funcion_corta_2():
    # SEGURO: Otra función pequeña
    datos = [1, 2, 3, 4, 5]
    return sum(datos)

def funcion_corta_3():
    # SEGURO: Función bien estructurada
    valores = []
    for i in range(5):
        valores.append(i * 2)
    return valores

def procesar_datos():
    # SEGURO: Función que orquesta otras funciones pequeñas
    resultado1 = funcion_corta_1()
    resultado2 = funcion_corta_2()
    resultado3 = funcion_corta_3()
    return resultado1 + resultado2 + sum(resultado3)

if __name__ == "__main__":
    print(procesar_datos())

# Made with Bob
