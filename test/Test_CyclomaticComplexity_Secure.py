# Test para CyclomaticComplexity - SEGURO

class DataProcessor:
    # SEGURO: Complejidad reducida mediante extracción de métodos
    
    def procesar_datos(self, data):
        # Complejidad ciclomática = 3 (bajo)
        resultado = self._validar_datos(data)
        if resultado:
            return self._procesar_validos(data)
        return self._procesar_invalidos(data)
    
    def _validar_datos(self, data):
        # Complejidad ciclomática = 2
        if data and len(data) > 0:
            return True
        return False
    
    def _procesar_validos(self, data):
        # Complejidad ciclomática = 2
        resultado = []
        for item in data:
            resultado.append(item * 2)
        return resultado
    
    def _procesar_invalidos(self, data):
        # Complejidad ciclomática = 1
        return []

if __name__ == "__main__":
    processor = DataProcessor()
    print(processor.procesar_datos([1, 2, 3]))

# Made with Bob
