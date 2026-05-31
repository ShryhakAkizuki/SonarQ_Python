# Guía de Implementación de Reglas de Deuda Técnica

## 🎯 Objetivo

Implementar reglas que detecten **deuda técnica** en código Python: código que funciona pero que debería refactorizarse para mejorar mantenibilidad, legibilidad, performance o seguir mejores prácticas.

---

## 📋 Catálogo de Reglas de Deuda Técnica

### 1. **CodeDuplicationRule** - Código Duplicado

**Problema:** Bloques de código repetidos que deberían extraerse a funciones.

**Detección:**
- Buscar secuencias idénticas de 5+ líneas
- Comparar estructuras de AST similares
- Ignorar imports y comentarios

**Severidad:** MEDIUM

**Ejemplo:**
```python
# ❌ DEUDA TÉCNICA
def process_user_data(user):
    if user.age < 18:
        print("Minor")
        log_event("minor_access")
        send_notification("parent")
    # ... más código ...

def process_admin_data(admin):
    if admin.age < 18:
        print("Minor")
        log_event("minor_access")
        send_notification("parent")
    # ... más código ...

# ✅ REFACTORIZADO
def handle_minor(person):
    print("Minor")
    log_event("minor_access")
    send_notification("parent")

def process_user_data(user):
    if user.age < 18:
        handle_minor(user)
```

---

### 2. **LongMethodRule** - Métodos Muy Largos

**Problema:** Funciones con demasiadas líneas que deberían dividirse.

**Detección:**
- Contar líneas de código (sin comentarios/blancos)
- Umbral: >50 líneas → MEDIUM, >100 líneas → HIGH

**Severidad:** MEDIUM/HIGH

**Ejemplo:**
```python
# ❌ DEUDA TÉCNICA (80 líneas)
def process_order(order):
    # Validación (20 líneas)
    if not order.items:
        raise ValueError("Empty order")
    # ... más validaciones ...
    
    # Cálculo de precio (20 líneas)
    total = 0
    for item in order.items:
        # ... cálculos complejos ...
    
    # Aplicar descuentos (20 líneas)
    # ... lógica de descuentos ...
    
    # Guardar en DB (20 líneas)
    # ... operaciones de base de datos ...

# ✅ REFACTORIZADO
def process_order(order):
    validate_order(order)
    total = calculate_total(order)
    total = apply_discounts(total, order)
    save_order(order, total)
```

---

### 3. **DeepNestingRule** - Anidamiento Excesivo

**Problema:** Demasiados niveles de indentación que dificultan la lectura.

**Detección:**
- Contar niveles de anidamiento
- Umbral: >4 niveles → MEDIUM, >6 niveles → HIGH

**Severidad:** MEDIUM/HIGH

**Ejemplo:**
```python
# ❌ DEUDA TÉCNICA (6 niveles)
def process_data(data):
    if data:
        for item in data:
            if item.valid:
                if item.type == "A":
                    if item.status == "active":
                        if item.value > 0:
                            # Código aquí
                            pass

# ✅ REFACTORIZADO (early returns, guard clauses)
def process_data(data):
    if not data:
        return
    
    for item in data:
        if not item.valid:
            continue
        if item.type != "A":
            continue
        if item.status != "active":
            continue
        if item.value <= 0:
            continue
        
        # Código aquí
        process_item(item)
```

---

### 4. **MagicNumbersRule** - Números Mágicos

**Problema:** Números literales sin contexto que deberían ser constantes nombradas.

**Detección:**
- Buscar literales numéricos (excepto 0, 1, -1)
- Ignorar en definiciones de constantes
- Ignorar en tests

**Severidad:** LOW

**Ejemplo:**
```python
# ❌ DEUDA TÉCNICA
def calculate_discount(price):
    if price > 1000:
        return price * 0.15
    elif price > 500:
        return price * 0.10
    return price * 0.05

# ✅ REFACTORIZADO
PREMIUM_THRESHOLD = 1000
STANDARD_THRESHOLD = 500
PREMIUM_DISCOUNT = 0.15
STANDARD_DISCOUNT = 0.10
BASIC_DISCOUNT = 0.05

def calculate_discount(price):
    if price > PREMIUM_THRESHOLD:
        return price * PREMIUM_DISCOUNT
    elif price > STANDARD_THRESHOLD:
        return price * STANDARD_DISCOUNT
    return price * BASIC_DISCOUNT
```

---

### 5. **TodoCommentsRule** - Comentarios TODO/FIXME

**Problema:** Comentarios que indican trabajo pendiente.

**Detección:**
- Buscar comentarios con: TODO, FIXME, HACK, XXX, BUG
- Extraer descripción del comentario

**Severidad:** INFO/LOW

**Ejemplo:**
```python
# ❌ DEUDA TÉCNICA
def process_payment(amount):
    # TODO: Implementar validación de tarjeta
    # FIXME: Este método falla con montos negativos
    # HACK: Workaround temporal para bug #123
    return charge_card(amount)
```

---

### 6. **LongParameterListRule** - Lista Larga de Parámetros

**Problema:** Funciones con demasiados parámetros (ya implementada parcialmente).

**Detección:**
- Contar parámetros en función
- Umbral: >5 → LOW, >7 → MEDIUM

**Severidad:** LOW/MEDIUM

**Ejemplo:**
```python
# ❌ DEUDA TÉCNICA
def create_user(name, email, age, address, phone, city, country, zip_code):
    pass

# ✅ REFACTORIZADO
from dataclasses import dataclass

@dataclass
class UserData:
    name: str
    email: str
    age: int
    address: str
    phone: str
    city: str
    country: str
    zip_code: str

def create_user(user_data: UserData):
    pass
```

---

### 7. **UnusedImportsRule** - Imports No Utilizados

**Problema:** Imports que no se usan en el código.

**Detección:**
- Recolectar todos los imports
- Buscar uso de cada símbolo importado
- Reportar los no utilizados

**Severidad:** LOW

**Ejemplo:**
```python
# ❌ DEUDA TÉCNICA
import os
import sys
import json
from typing import List, Dict, Optional, Tuple

def process_data(data: List[str]):
    return json.loads(data[0])

# ✅ REFACTORIZADO
import json
from typing import List

def process_data(data: List[str]):
    return json.loads(data[0])
```

---

### 8. **BroadExceptionRule** - Excepciones Genéricas

**Problema:** Capturar excepciones demasiado amplias.

**Detección:**
- Buscar `except Exception:` o `except:`
- Sugerir excepciones específicas

**Severidad:** MEDIUM

**Ejemplo:**
```python
# ❌ DEUDA TÉCNICA
try:
    data = json.loads(text)
    result = process(data)
except Exception:  # Demasiado amplio
    print("Error")

# ✅ REFACTORIZADO
try:
    data = json.loads(text)
    result = process(data)
except json.JSONDecodeError as e:
    print(f"Invalid JSON: {e}")
except ValueError as e:
    print(f"Invalid data: {e}")
```

---

### 9. **MutableDefaultArgumentRule** - Argumentos Mutables por Defecto

**Problema:** Usar listas/diccionarios como valores por defecto.

**Detección:**
- Buscar parámetros con default `[]`, `{}`, `set()`
- Reportar como bug potencial

**Severidad:** HIGH

**Ejemplo:**
```python
# ❌ DEUDA TÉCNICA (BUG!)
def add_item(item, items=[]):  # ¡Peligro!
    items.append(item)
    return items

# ✅ REFACTORIZADO
def add_item(item, items=None):
    if items is None:
        items = []
    items.append(item)
    return items
```

---

### 10. **StringConcatenationInLoopRule** - Concatenación en Loop

**Problema:** Concatenar strings en un loop (ineficiente).

**Detección:**
- Buscar `+=` con strings dentro de loops
- Sugerir usar `join()` o `StringIO`

**Severidad:** MEDIUM

**Ejemplo:**
```python
# ❌ DEUDA TÉCNICA (O(n²))
result = ""
for item in items:
    result += str(item) + "\n"

# ✅ REFACTORIZADO (O(n))
result = "\n".join(str(item) for item in items)
```

---

### 11. **GlobalVariableRule** - Variables Globales

**Problema:** Uso excesivo de variables globales.

**Detección:**
- Buscar declaraciones `global`
- Contar modificaciones a variables globales

**Severidad:** MEDIUM

**Ejemplo:**
```python
# ❌ DEUDA TÉCNICA
counter = 0

def increment():
    global counter
    counter += 1

# ✅ REFACTORIZADO
class Counter:
    def __init__(self):
        self.value = 0
    
    def increment(self):
        self.value += 1
```

---

### 12. **MissingDocstringRule** - Falta de Documentación

**Problema:** Funciones/clases sin docstrings.

**Detección:**
- Verificar presencia de docstring en funciones públicas
- Verificar presencia de docstring en clases

**Severidad:** LOW

**Ejemplo:**
```python
# ❌ DEUDA TÉCNICA
def calculate_tax(amount, rate):
    return amount * rate

# ✅ REFACTORIZADO
def calculate_tax(amount: float, rate: float) -> float:
    """
    Calculate tax amount based on rate.
    
    Args:
        amount: Base amount for tax calculation
        rate: Tax rate as decimal (e.g., 0.15 for 15%)
    
    Returns:
        Tax amount
    """
    return amount * rate
```

---

### 13. **ComplexBooleanExpressionRule** - Expresiones Booleanas Complejas

**Problema:** Condiciones con demasiados operadores lógicos.

**Detección:**
- Contar operadores `and`/`or` en una expresión
- Umbral: >3 operadores → MEDIUM

**Severidad:** MEDIUM

**Ejemplo:**
```python
# ❌ DEUDA TÉCNICA
if user.age > 18 and user.verified and user.active and not user.banned and user.country == "US":
    grant_access()

# ✅ REFACTORIZADO
def is_eligible_user(user):
    return (
        user.age > 18 and
        user.verified and
        user.active and
        not user.banned and
        user.country == "US"
    )

if is_eligible_user(user):
    grant_access()
```

---

### 14. **EmptyExceptBlockRule** - Bloques Except Vacíos

**Problema:** Silenciar excepciones sin manejo.

**Detección:**
- Buscar `except:` con solo `pass`
- Reportar como mala práctica

**Severidad:** HIGH

**Ejemplo:**
```python
# ❌ DEUDA TÉCNICA
try:
    risky_operation()
except:
    pass  # Silencia todos los errores

# ✅ REFACTORIZADO
try:
    risky_operation()
except SpecificError as e:
    logger.warning(f"Operation failed: {e}")
    # Manejo apropiado
```

---

### 15. **ClassTooLargeRule** - Clases Muy Grandes

**Problema:** Clases con demasiados métodos o líneas (violación SRP).

**Detección:**
- Contar métodos en clase
- Contar líneas de código
- Umbral: >10 métodos o >300 líneas → MEDIUM

**Severidad:** MEDIUM

**Ejemplo:**
```python
# ❌ DEUDA TÉCNICA
class User:
    # 20 métodos diferentes
    def validate_email(self): pass
    def validate_password(self): pass
    def send_email(self): pass
    def send_sms(self): pass
    def calculate_age(self): pass
    def format_address(self): pass
    # ... 14 métodos más

# ✅ REFACTORIZADO
class User:
    def __init__(self, validator, notifier):
        self.validator = validator
        self.notifier = notifier

class UserValidator:
    def validate_email(self, email): pass
    def validate_password(self, pwd): pass

class UserNotifier:
    def send_email(self, user): pass
    def send_sms(self, user): pass
```

---

## 🏗️ Arquitectura de Implementación

### Estructura de Archivos Propuesta

```
src/
├── rules/
│   ├── debt/                           # Reglas de deuda técnica
│   │   ├── CodeDuplicationRule.java
│   │   ├── LongMethodRule.java
│   │   ├── DeepNestingRule.java
│   │   ├── MagicNumbersRule.java
│   │   ├── TodoCommentsRule.java
│   │   ├── UnusedImportsRule.java
│   │   ├── BroadExceptionRule.java
│   │   ├── MutableDefaultArgumentRule.java
│   │   ├── StringConcatenationInLoopRule.java
│   │   ├── GlobalVariableRule.java
│   │   ├── MissingDocstringRule.java
│   │   ├── ComplexBooleanExpressionRule.java
│   │   ├── EmptyExceptBlockRule.java
│   │   └── ClassTooLargeRule.java
│   ├── security/                       # Reglas de seguridad
│   │   ├── HardcodedCredentialsRule.java (existente)
│   │   └── SqlInjectionRule.java
│   └── quality/                        # Reglas de calidad
│       └── CyclomaticComplexityRule.java (existente)
├── AnalysisRule.java
├── RulesListener.java
└── Main.java
```

---

## 💻 Ejemplo de Implementación: LongMethodRule

```java
package rules.debt;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import java.util.HashSet;
import java.util.Set;

public class LongMethodRule extends AnalysisRule {

    private static final int MEDIUM_THRESHOLD = 50;
    private static final int HIGH_THRESHOLD = 100;

    @Override
    protected String name() {
        return "LongMethod";
    }

    @Override
    public void exitFunction_def_raw(PythonParser.Function_def_rawContext ctx) {
        String functionName = ctx.name() != null ? ctx.name().getText() : "<anonymous>";
        int lineCount = countCodeLines(ctx);
        
        if (lineCount > HIGH_THRESHOLD) {
            flag(Severity.HIGH, 
                 ctx.start.getLine(), 
                 ctx.start.getCharPositionInLine(),
                 String.format("Función '%s' tiene %d líneas (umbral: %d) — dividir en funciones más pequeñas",
                     functionName, lineCount, HIGH_THRESHOLD));
        } else if (lineCount > MEDIUM_THRESHOLD) {
            flag(Severity.MEDIUM,
                 ctx.start.getLine(),
                 ctx.start.getCharPositionInLine(),
                 String.format("Función '%s' tiene %d líneas (umbral: %d) — considerar refactorizar",
                     functionName, lineCount, MEDIUM_THRESHOLD));
        }
    }

    /**
     * Cuenta líneas de código excluyendo comentarios y líneas en blanco
     */
    private int countCodeLines(ParserRuleContext ctx) {
        if (ctx.start == null || ctx.stop == null) return 0;
        
        int startLine = ctx.start.getLine();
        int endLine = ctx.stop.getLine();
        
        // Obtener tokens entre start y stop
        Set<Integer> codeLines = new HashSet<>();
        Token token = ctx.start;
        
        while (token != null && token.getLine() <= endLine) {
            int line = token.getLine();
            String text = token.getText().trim();
            
            // Ignorar líneas vacías y comentarios
            if (!text.isEmpty() && !text.startsWith("#")) {
                codeLines.add(line);
            }
            
            if (token == ctx.stop) break;
            token = token.getTokenSource().nextToken();
        }
        
        return codeLines.size();
    }
}
```

---

## 💻 Ejemplo de Implementación: MagicNumbersRule

```java
package rules.debt;

import org.antlr.v4.runtime.tree.TerminalNode;
import java.util.Set;

public class MagicNumbersRule extends AnalysisRule {

    private static final Set<String> ALLOWED_NUMBERS = Set.of("0", "1", "-1", "0.0", "1.0");
    private boolean inConstantDefinition = false;

    @Override
    protected String name() {
        return "MagicNumbers";
    }

    @Override
    public void enterAssignment(PythonParser.AssignmentContext ctx) {
        // Detectar si estamos en una definición de constante
        if (ctx.name() != null) {
            String varName = ctx.name().getText();
            inConstantDefinition = varName.equals(varName.toUpperCase());
        }
    }

    @Override
    public void exitAssignment(PythonParser.AssignmentContext ctx) {
        inConstantDefinition = false;
    }

    @Override
    public void enterNumber(PythonParser.NumberContext ctx) {
        if (inConstantDefinition) return;
        
        String number = ctx.getText();
        
        // Ignorar números permitidos
        if (ALLOWED_NUMBERS.contains(number)) return;
        
        // Ignorar números en notación científica o hexadecimal (probablemente constantes)
        if (number.contains("e") || number.contains("E") || 
            number.startsWith("0x") || number.startsWith("0X")) {
            return;
        }
        
        flag(Severity.LOW,
             ctx.start.getLine(),
             ctx.start.getCharPositionInLine(),
             String.format("Número mágico '%s' — considerar extraer a constante nombrada", number));
    }
}
```

---

## 💻 Ejemplo de Implementación: TodoCommentsRule

```java
package rules.debt;

import org.antlr.v4.runtime.Token;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class TodoCommentsRule extends AnalysisRule {

    private static final Pattern TODO_PATTERN = Pattern.compile(
        "#\\s*(TODO|FIXME|HACK|XXX|BUG)\\s*:?\\s*(.+)",
        Pattern.CASE_INSENSITIVE
    );

    @Override
    protected String name() {
        return "TodoComments";
    }

    @Override
    public void visitTerminal(TerminalNode node) {
        Token token = node.getSymbol();
        
        // Solo procesar comentarios
        if (token.getType() != PythonLexer.COMMENT) return;
        
        String comment = token.getText();
        Matcher matcher = TODO_PATTERN.matcher(comment);
        
        if (matcher.find()) {
            String type = matcher.group(1).toUpperCase();
            String description = matcher.group(2).trim();
            
            Severity severity = switch (type) {
                case "BUG", "FIXME" -> Severity.MEDIUM;
                case "HACK" -> Severity.LOW;
                default -> Severity.INFO;
            };
            
            flag(severity,
                 token.getLine(),
                 token.getCharPositionInLine(),
                 String.format("%s: %s", type, description));
        }
    }
}
```

---

## 💻 Ejemplo de Implementación: MutableDefaultArgumentRule

```java
package rules.debt;

public class MutableDefaultArgumentRule extends AnalysisRule {

    @Override
    protected String name() {
        return "MutableDefaultArgument";
    }

    @Override
    public void enterParam(PythonParser.ParamContext ctx) {
        if (ctx.default_assignment() == null) return;
        
        String defaultValue = ctx.default_assignment().expression().getText();
        String paramName = ctx.name().getText();
        
        // Detectar [], {}, set()
        if (defaultValue.equals("[]") || 
            defaultValue.equals("{}") || 
            defaultValue.equals("set()") ||
            defaultValue.matches("\\[.+\\]") ||
            defaultValue.matches("\\{.+\\}")) {
            
            flag(Severity.HIGH,
                 ctx.start.getLine(),
                 ctx.start.getCharPositionInLine(),
                 String.format("Parámetro '%s' usa mutable por defecto (%s) — usar None y crear dentro de la función",
                     paramName, defaultValue));
        }
    }
}
```

---

## 📊 Registro de Reglas en RulesListener

```java
import rules.debt.*;
import rules.security.*;
import rules.quality.*;

public class RulesListener {

    private final List<AnalysisRule> rules = List.of(
        // Seguridad
        new HardcodedCredentialsRule(),
        
        // Calidad
        new CyclomaticComplexityRule(),
        
        // Deuda Técnica
        new LongMethodRule(),
        new DeepNestingRule(),
        new MagicNumbersRule(),
        new TodoCommentsRule(),
        new UnusedImportsRule(),
        new BroadExceptionRule(),
        new MutableDefaultArgumentRule(),
        new StringConcatenationInLoopRule(),
        new GlobalVariableRule(),
        new MissingDocstringRule(),
        new ComplexBooleanExpressionRule(),
        new EmptyExceptBlockRule(),
        new ClassTooLargeRule(),
        new CodeDuplicationRule()
    );
    
    // ... resto del código
}
```

---

## 🎯 Priorización de Implementación

### Fase 1: Quick Wins (1-2 días)
1. ✅ **TodoCommentsRule** - Fácil, útil inmediatamente
2. ✅ **MagicNumbersRule** - Simple, gran impacto
3. ✅ **MutableDefaultArgumentRule** - Detecta bugs reales
4. ✅ **EmptyExceptBlockRule** - Crítico para calidad

### Fase 2: Calidad de Código (2-3 días)
5. ✅ **LongMethodRule** - Métrica importante
6. ✅ **DeepNestingRule** - Mejora legibilidad
7. ✅ **MissingDocstringRule** - Fomenta documentación
8. ✅ **ComplexBooleanExpressionRule** - Simplifica lógica

### Fase 3: Mejores Prácticas (2-3 días)
9. ✅ **BroadExceptionRule** - Manejo de errores
10. ✅ **UnusedImportsRule** - Limpieza de código
11. ✅ **GlobalVariableRule** - Diseño limpio
12. ✅ **StringConcatenationInLoopRule** - Performance

### Fase 4: Avanzadas (3-4 días)
13. ✅ **ClassTooLargeRule** - Principios SOLID
14. ✅ **CodeDuplicationRule** - Complejo pero valioso

---

## 📈 Métricas de Deuda Técnica

### Cálculo de Deuda Total

```java
public class TechnicalDebtCalculator {
    
    public static DebtMetrics calculate(List<Issue> issues) {
        Map<Severity, Long> counts = issues.stream()
            .collect(Collectors.groupingBy(Issue::severity, Collectors.counting()));
        
        // Puntos de deuda por severidad
        int debtPoints = 
            counts.getOrDefault(Severity.CRITICAL, 0L).intValue() * 10 +
            counts.getOrDefault(Severity.HIGH, 0L).intValue() * 5 +
            counts.getOrDefault(Severity.MEDIUM, 0L).intValue() * 3 +
            counts.getOrDefault(Severity.LOW, 0L).intValue() * 1;
        
        // Estimación de tiempo de refactoring (minutos)
        int estimatedMinutes =
            counts.getOrDefault(Severity.CRITICAL, 0L).intValue() * 60 +
            counts.getOrDefault(Severity.HIGH, 0L).intValue() * 30 +
            counts.getOrDefault(Severity.MEDIUM, 0L).intValue() * 15 +
            counts.getOrDefault(Severity.LOW, 0L).intValue() * 5;
        
        return new DebtMetrics(debtPoints, estimatedMinutes, calculateGrade(debtPoints));
    }
    
    private static String calculateGrade(int debtPoints) {
        if (debtPoints == 0) return "A+";
        if (debtPoints <= 10) return "A";
        if (debtPoints <= 25) return "B";
        if (debtPoints <= 50) return "C";
        if (debtPoints <= 100) return "D";
        return "F";
    }
}

record DebtMetrics(int debtPoints, int estimatedMinutes, String grade) {
    public String formatTime() {
        int hours = estimatedMinutes / 60;
        int mins = estimatedMinutes % 60;
        return String.format("%dh %dm", hours, mins);
    }
}
```

---

## 📊 Reporte Mejorado con Deuda Técnica

```
══════════════════════════════════════════════════════════════════════
                    ANÁLISIS DE DEUDA TÉCNICA
══════════════════════════════════════════════════════════════════════

📁 Archivo: src/payment_processor.py

🔴 CRITICAL Issues (2)
──────────────────────────────────────────────────────────────────────
  [CRITICAL] HardcodedCredentials        línea 15:0
    Credencial hardcodeada en 'API_KEY'
  
  [CRITICAL] MutableDefaultArgument      línea 45:0
    Parámetro 'items' usa mutable por defecto ([])

🟠 HIGH Issues (3)
──────────────────────────────────────────────────────────────────────
  [HIGH    ] CyclomaticComplexity        línea 78:0
    Función 'process_payment': CC=22 — refactorizar urgente
  
  [HIGH    ] LongMethod                  línea 78:0
    Función 'process_payment' tiene 120 líneas — dividir
  
  [HIGH    ] EmptyExceptBlock            línea 156:0
    Bloque except vacío silencia errores

🟡 MEDIUM Issues (5)
──────────────────────────────────────────────────────────────────────
  [MEDIUM  ] DeepNesting                 línea 92:0
    Anidamiento de 7 niveles — usar guard clauses
  
  [MEDIUM  ] BroadException              línea 145:0
    Captura Exception genérica — usar excepciones específicas
  
  [MEDIUM  ] ComplexBooleanExpression    línea 103:0
    Expresión con 5 operadores lógicos — extraer a función
  
  [MEDIUM  ] TodoComments                línea 67:0
    FIXME: Este método falla con montos negativos
  
  [MEDIUM  ] StringConcatenationInLoop   línea 189:0
    Concatenación de strings en loop — usar join()

🟢 LOW Issues (8)
──────────────────────────────────────────────────────────────────────
  [LOW     ] MagicNumbers                línea 23:0
    Número mágico '0.15' — extraer a constante
  
  [LOW     ] MagicNumbers                línea 25:0
    Número mágico '1000' — extraer a constante
  
  [LOW     ] UnusedImports               línea 3:0
    Import 'datetime' no utilizado
  
  [LOW     ] MissingDocstring            línea 78:0
    Función 'process_payment' sin docstring
  
  [LOW     ] GlobalVariable              línea 12:0
    Variable global 'transaction_count' modificada
  
  ... (3 más)

══════════════════════════════════════════════════════════════════════
                         RESUMEN DE DEUDA
══════════════════════════════════════════════════════════════════════

Total de Issues:        18
  CRITICAL:              2  (⚠️  Atención inmediata)
  HIGH:                  3  (🔥 Refactorizar pronto)
  MEDIUM:                5  (⚡ Mejorar cuando sea posible)
  LOW:                   8  (💡 Sugerencias de mejora)

Puntos de Deuda:       76  (Calificación: D)
Tiempo Estimado:       5h 45m de refactoring

Recomendaciones:
  1. Eliminar credenciales hardcodeadas (CRÍTICO)
  2. Corregir argumento mutable por defecto (CRÍTICO)
  3. Dividir función 'process_payment' (120 líneas → <50)
  4. Reducir complejidad ciclomática (CC=22 → <10)
  5. Implementar manejo de excepciones específico

══════════════════════════════════════════════════════════════════════
```

---

## 🎨 Visualización en GUI

### Dashboard de Deuda Técnica

```
┌─────────────────────────────────────────────────────────────┐
│  📊 Technical Debt Dashboard                                │
├─────────────────────────────────────────────────────────────┤
│                                                              │
│  Calificación General: D                                    │
│  ████████████████████░░░░░░░░░░░░░░░░░░░░░░░░ 76/100       │
│                                                              │
│  ┌──────────────┬──────────┬──────────────┐                │
│  │ Puntos Deuda │ Issues   │ Tiempo Est.  │                │
│  │     76       │    18    │   5h 45m     │                │
│  └──────────────┴──────────┴──────────────┘                │
│                                                              │
│  Distribución por Severidad:                                │
│  ┌────────────────────────────────────────┐                │
│  │ CRITICAL  ██████ 2                     │                │
│  │ HIGH      █████████ 3                  │                │
│  │ MEDIUM    ███████████████ 5            │                │
│  │ LOW       ████████████████████████ 8   │                │
│  └────────────────────────────────────────┘                │
│                                                              │
│  Top 5 Problemas:                                           │
│  1. 🔴 Credenciales hardcodeadas (2)                        │
│  2. 🟠 Métodos muy largos (3)                               │
│  3. 🟡 Complejidad ciclomática alta (2)                     │
│  4. 🟢 Números mágicos (8)                                  │
│  5. 🔵 Comentarios TODO (5)                                 │
│                                                              │
│  [Ver Detalles] [Exportar Reporte] [Configurar Reglas]     │
└─────────────────────────────────────────────────────────────┘
```

---

## 🚀 Roadmap de Implementación

### Sprint 1 (Semana 1)
- [ ] Implementar estructura de paquetes `rules/debt/`
- [ ] Crear 4 reglas básicas (TODO, MagicNumbers, MutableDefault, EmptyExcept)
- [ ] Agregar tests unitarios
- [ ] Actualizar RulesListener

### Sprint 2 (Semana 2)
- [ ] Implementar 4 reglas de calidad (LongMethod, DeepNesting, MissingDocstring, ComplexBoolean)
- [ ] Crear TechnicalDebtCalculator
- [ ] Mejorar formato de reporte

### Sprint 3 (Semana 3)
- [ ] Implementar 4 reglas de mejores prácticas
- [ ] Agregar dashboard de deuda técnica
- [ ] Implementar exportación de métricas

### Sprint 4 (Semana 4)
- [ ] Implementar reglas avanzadas (ClassTooLarge, CodeDuplication)
- [ ] Agregar configuración de umbrales
- [ ] Documentación completa

---

## 📚 Recursos Adicionales

- [Martin Fowler - Technical Debt](https://martinfowler.com/bliki/TechnicalDebt.html)
- [SonarQube Rules](https://rules.sonarsource.com/python/)
- [Python Anti-Patterns](https://docs.quantifiedcode.com/python-anti-patterns/)
- [Clean Code Principles](https://www.amazon.com/Clean-Code-Handbook-Software-Craftsmanship/dp/0132350882)

---

¿Te gustaría que implemente alguna de estas reglas específicas? Puedo crear el código completo para las que prefieras.