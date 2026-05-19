import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

public class Main {

    public static void main(String[] args) {

        // Código Python de prueba
        String code = """
                from __future__ import annotations
                
                import asyncio
                import json
                import logging
                import math
                import random
                import re
                import threading
                import time
                from contextlib import contextmanager
                from dataclasses import dataclass, field
                from enum import Enum
                from functools import lru_cache, wraps
                from pathlib import Path
                from queue import Queue
                from typing import Generator, Iterable
                
                
                # =========================================================
                # LOGGING
                # =========================================================
                
                logging.basicConfig(
                    level=logging.INFO,
                    format="[%(levelname)s] %(message)s"
                )
                
                logger = logging.getLogger(__name__)
                
                
                # =========================================================
                # ENUM
                # =========================================================
                
                class Priority(Enum):
                    LOW = 1
                    MEDIUM = 2
                    HIGH = 3
                
                
                # =========================================================
                # EXCEPCIÓN PERSONALIZADA
                # =========================================================
                
                class InvalidTaskError(Exception):
                    pass
                
                
                # =========================================================
                # DECORADOR
                # =========================================================
                
                def execution_time(func):
                    @wraps(func)
                    def wrapper(*args, **kwargs):
                        start = time.perf_counter()
                        result = func(*args, **kwargs)
                        elapsed = time.perf_counter() - start
                        logger.info(f"{func.__name__} ejecutado en {elapsed:.4f}s")
                        return result
                    return wrapper
                
                
                # =========================================================
                # DATACLASS
                # =========================================================
                
                @dataclass(order=True)
                class Task:
                    priority: int
                    title: str
                    completed: bool = False
                    tags: list[str] = field(default_factory=list)
                
                    def complete(self):
                        self.completed = True
                
                    @property
                    def status(self):
                        return "DONE" if self.completed else "PENDING"
                
                    def __str__(self):
                        return f"[{self.status}] {self.title} ({self.priority})"
                
                    # Sobrecarga de operador
                    def __add__(self, other: "Task"):
                        return Task(
                            priority=max(self.priority, other.priority),
                            title=f"{self.title} + {other.title}"
                        )
                
                
                # =========================================================
                # HERENCIA
                # =========================================================
                
                class TimedTask(Task):
                
                    def __init__(self, priority, title, duration):
                        super().__init__(priority, title)
                        self.duration = duration
                
                    def run(self):
                        logger.info(f"Ejecutando tarea: {self.title}")
                        time.sleep(self.duration)
                        self.complete()
                
                
                # =========================================================
                # ITERADOR PERSONALIZADO
                # =========================================================
                
                class Countdown:
                
                    def __init__(self, start):
                        self.current = start
                
                    def __iter__(self):
                        return self
                
                    def __next__(self):
                        if self.current <= 0:
                            raise StopIteration
                        self.current -= 1
                        return self.current + 1
                
                
                # =========================================================
                # GENERADOR
                # =========================================================
                
                def fibonacci(n: int) -> Generator[int, None, None]:
                    a, b = 0, 1
                    for _ in range(n):
                        yield a
                        a, b = b, a + b
                
                
                # =========================================================
                # MEMOIZATION
                # =========================================================
                
                @lru_cache(maxsize=None)
                def factorial(n: int) -> int:
                    if n <= 1:
                        return 1
                    return n * factorial(n - 1)
                
                
                # =========================================================
                # CONTEXT MANAGER
                # =========================================================
                
                @contextmanager
                def open_json(path: str, mode="r"):
                    file = open(path, mode, encoding="utf-8")
                    try:
                        yield file
                    finally:
                        file.close()
                
                
                # =========================================================
                # REGEX
                # =========================================================
                
                def extract_emails(text: str) -> list[str]:
                    pattern = r"[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+"
                    return re.findall(pattern, text)
                
                
                # =========================================================
                # THREADING
                # =========================================================
                
                def worker(queue: Queue):
                    while not queue.empty():
                        item = queue.get()
                        logger.info(f"Procesando elemento {item}")
                        time.sleep(random.uniform(0.1, 0.5))
                        queue.task_done()
                
                
                # =========================================================
                # ASYNCIO
                # =========================================================
                
                async def async_download(name: str, delay: float):
                    logger.info(f"Iniciando descarga {name}")
                    await asyncio.sleep(delay)
                    logger.info(f"Descarga completada {name}")
                    return name
                
                
                # =========================================================
                # FUNCIONES AVANZADAS
                # =========================================================
                
                @execution_time
                def process_tasks(tasks: Iterable[Task]):
                
                    # Comprensión de listas
                    titles = [task.title.upper() for task in tasks]
                
                    # Lambda + sorted
                    ordered = sorted(tasks, key=lambda t: t.priority)
                
                    logger.info(f"Títulos: {titles}")
                
                    for task in ordered:
                        logger.info(task)
                
                
                # =========================================================
                # MANEJO DE ARCHIVOS + JSON
                # =========================================================
                
                def save_tasks(tasks: list[Task], filename="tasks.json"):
                
                    data = [
                        {
                            "title": t.title,
                            "priority": t.priority,
                            "completed": t.completed,
                            "tags": t.tags,
                        }
                        for t in tasks
                    ]
                
                    with open_json(filename, "w") as file:
                        json.dump(data, file, indent=4)
                
                
                def load_tasks(filename="tasks.json") -> list[dict]:
                
                    path = Path(filename)
                
                    if not path.exists():
                        return []
                
                    with open_json(filename) as file:
                        return json.load(file)
                
                
                # =========================================================
                # VALIDACIONES
                # =========================================================
                
                def validate_task_name(name: str):
                
                    if len(name.strip()) < 3:
                        raise InvalidTaskError(
                            "El nombre de la tarea es demasiado corto"
                        )
                
                
                # =========================================================
                # MAIN
                # =========================================================
                
                async def main():
                
                    validate_task_name("Aprender Python")
                
                    task1 = Task(
                        priority=Priority.HIGH.value,
                        title="Estudiar ANTLR",
                        tags=["parser", "compilers"]
                    )
                
                    task2 = TimedTask(
                        priority=Priority.MEDIUM.value,
                        title="Entrenar YOLO",
                        duration=1
                    )
                
                    combined = task1 + task2
                
                    logger.info(combined)
                
                    # Generador
                    fib_numbers = list(fibonacci(10))
                    logger.info(f"Fibonacci: {fib_numbers}")
                
                    # Memoization
                    logger.info(f"Factorial(10): {factorial(10)}")
                
                    # Regex
                    emails = extract_emails(
                        "Contactos: test@gmail.com y admin@empresa.org"
                    )
                    logger.info(f"Emails encontrados: {emails}")
                
                    # Iterador personalizado
                    for number in Countdown(5):
                        logger.info(f"Countdown: {number}")
                
                    # Herencia
                    task2.run()
                
                    tasks = [task1, task2, combined]
                
                    process_tasks(tasks)
                
                    # JSON
                    save_tasks(tasks)
                
                    loaded = load_tasks()
                    logger.info(f"JSON cargado: {loaded}")
                
                    # Threading
                    q = Queue()
                
                    for i in range(5):
                        q.put(i)
                
                    threads = [
                        threading.Thread(target=worker, args=(q,))
                        for _ in range(3)
                    ]
                
                    for t in threads:
                        t.start()
                
                    for t in threads:
                        t.join()
                
                    # Asyncio
                    results = await asyncio.gather(
                        async_download("file1.zip", 1),
                        async_download("file2.zip", 2),
                        async_download("file3.zip", 1.5),
                    )
                
                    logger.info(f"Descargas: {results}")
                
                    # Set comprehension
                    unique_lengths = {
                        len(task.title)
                        for task in tasks
                    }
                
                    logger.info(f"Longitudes únicas: {unique_lengths}")
                
                    # Diccionario por comprensión
                    task_map = {
                        task.title: task.status
                        for task in tasks
                    }
                
                    logger.info(task_map)
                
                    # Matemática avanzada
                    values = [random.randint(1, 100) for _ in range(10)]
                
                    avg = sum(values) / len(values)
                
                    std_dev = math.sqrt(
                        sum((x - avg) ** 2 for x in values) / len(values)
                    )
                
                    logger.info(f"Valores: {values}")
                    logger.info(f"Promedio: {avg:.2f}")
                    logger.info(f"Desviación estándar: {std_dev:.2f}")
                
                
                if __name__ == "__main__":
                    asyncio.run(main())
                """;

        try {

            // Convertir string a stream de caracteres
            CharStream input = CharStreams.fromString(code);

            // Crear lexer
            PythonLexer lexer = new PythonLexer(input);

            // Generar tokens
            CommonTokenStream tokens = new CommonTokenStream(lexer);

            // Crear parser
            PythonParser parser = new PythonParser(tokens);

            // Punto de entrada de la gramática
            ParseTree tree = parser.file_input();

            // Mostrar árbol sintáctico
            System.out.println(tree.toStringTree(parser));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}