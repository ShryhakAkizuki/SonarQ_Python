# Test para CyclomaticComplexity - VULNERABLE

def funcion_compleja(a, b, c, d, e, f, g, h):
    # HIGH: Complejidad ciclomática = 22
    if a:
        if b and c and d:
            pass
        elif e:
            pass
    elif b:
        if f or g:
            pass
        elif h:
            if a or b:
                pass
    elif c:
        pass
    
    for i in range(a):
        if d:
            pass
    
    for j in range(b):
        while c:
            try:
                pass
            except ValueError:
                pass
    
    while d:
        try:
            pass
        except TypeError:
            pass
    
    x = a if b else c
    y = d if e else f
    
    if g:
        pass
    
    return x, y

if __name__ == "__main__":
    print(funcion_compleja(1, 2, 3, 4, 5, 6, 7, 8))

# Made with Bob
