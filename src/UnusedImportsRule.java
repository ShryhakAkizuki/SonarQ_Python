import java.util.*;

/**
 * Detecta imports no utilizados en el codigo.
 * Enfoque simplificado: recolecta imports y busca uso de cada simbolo.
 */
public class UnusedImportsRule extends AnalysisRule {

    private final Map<String, ImportInfo> imports = new HashMap<>();
    private final Set<String> usedNames = new HashSet<>();
    
    private record ImportInfo(String name, int line) {}

    @Override
    protected String name() {
        return "UnusedImports";
    }

    @Override
    public void enterFile_input(PythonParser.File_inputContext ctx) {
        // Limpiar estado para nuevo archivo
        imports.clear();
        usedNames.clear();
    }

    // Detectar: import os, sys, json
    @Override
    public void enterImport_name(PythonParser.Import_nameContext ctx) {
        if (ctx.dotted_as_names() != null) {
            for (var dotted : ctx.dotted_as_names().dotted_as_name()) {
                String moduleName = dotted.dotted_name().getText();
                String alias = dotted.name() != null ? dotted.name().getText() : null;
                
                // El nombre que se usa en el código
                String useName = alias != null ? alias : moduleName.split("\\.")[0];
                
                imports.put(useName, new ImportInfo(moduleName, ctx.start.getLine()));
            }
        }
    }

    // Detectar: from pathlib import Path
    @Override
    public void enterImport_from(PythonParser.Import_fromContext ctx) {
        // Ignorar: from module import *
        if (ctx.getText().contains("*")) {
            return;
        }
        
        if (ctx.import_from_targets() != null &&
            ctx.import_from_targets().import_from_as_names() != null) {
            for (var target : ctx.import_from_targets().import_from_as_names().import_from_as_name()) {
                String name = target.name(0).getText();
                String alias = target.name().size() > 1 ? target.name(1).getText() : null;
                
                // El nombre que se usa en el código
                String useName = alias != null ? alias : name;
                
                imports.put(useName, new ImportInfo(name, ctx.start.getLine()));
            }
        }
    }

    // Registrar TODOS los nombres usados en el código
    @Override
    public void enterName(PythonParser.NameContext ctx) {
        usedNames.add(ctx.getText());
    }
    
    // También capturar nombres en expresiones primarias (os.path.exists)
    @Override
    public void enterPrimary(PythonParser.PrimaryContext ctx) {
        String text = ctx.getText();
        // Si contiene punto, capturar el primer nombre
        if (text.contains(".")) {
            String firstName = text.split("\\.")[0];
            usedNames.add(firstName);
        }
    }

    @Override
    public void exitFile_input(PythonParser.File_inputContext ctx) {
        // Al final del archivo, verificar imports no usados
        for (Map.Entry<String, ImportInfo> entry : imports.entrySet()) {
            String importName = entry.getKey();
            ImportInfo info = entry.getValue();
            
            if (!usedNames.contains(importName)) {
                flag(Severity.LOW,
                     info.line(),
                     0,
                     String.format("Import no utilizado: '%s' - eliminar para reducir dependencias",
                         info.name()));
            }
        }
    }
}

// Made with Bob
