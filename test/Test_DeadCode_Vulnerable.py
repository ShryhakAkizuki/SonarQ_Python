# Test para DeadCode - VULNERABLE

def funcion_con_codigo_muerto():
    x = 10
    return x
    # MEDIUM: Código inalcanzable después de return
    print("Esto nunca se ejecuta")
    y = 20
    return y

def funcion_con_if_false():
    resultado = []
    
    # LOW: Bloque if False nunca se ejecuta
    if False:
        print("Código muerto")
        resultado.append(1)
        resultado.append(2)
    
    # LOW: Bloque if 0 nunca se ejecuta
    if 0:
        print("Más código muerto")
        resultado.append(3)
    
    return resultado

if __name__ == "__main__":
    print(funcion_con_codigo_muerto())
    print(funcion_con_if_false())

# Made with Bob
