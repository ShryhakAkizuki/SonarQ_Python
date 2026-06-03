# Test para DeadCode - SEGURO

def funcion_sin_codigo_muerto():
    # SEGURO: Sin código después de return
    x = 10
    return x

def funcion_con_logica_correcta():
    # SEGURO: Lógica condicional válida
    resultado = []
    condicion = True
    
    if condicion:
        resultado.append(1)
        resultado.append(2)
    
    return resultado

def funcion_con_early_return(valor):
    # SEGURO: Early return sin código inalcanzable
    if valor < 0:
        return "negativo"
    
    if valor == 0:
        return "cero"
    
    return "positivo"

if __name__ == "__main__":
    print(funcion_sin_codigo_muerto())
    print(funcion_con_logica_correcta())
    print(funcion_con_early_return(5))

# Made with Bob
