public record Issue(Severity severity, String rule, int line, int col, String message) {
    
    /**
     * Calcula el tiempo estimado para resolver esta incidencia.
     */
    public int getEstimatedMinutes() {
        return switch (severity) {
            case CRITICAL -> 60;  // 1 hora
            case HIGH -> 30;      // 30 minutos
            case MEDIUM -> 15;    // 15 minutos
            case LOW -> 5;        // 5 minutos
            case INFO -> 2;       // 2 minutos
        };
    }
    
    /**
     * Formatea el tiempo estimado en formato legible.
     */
    public String getFormattedTime() {
        return formatMinutesToTime(getEstimatedMinutes());
    }
    
    /**
     * Formatea minutos a formato SonarQube: "Xd Yh Zmin"
     * Días laborales de 8 horas.
     */
    public static String formatMinutesToTime(int totalMinutes) {
        if (totalMinutes == 0) {
            return "0min";
        }
        
        int days = totalMinutes / (8 * 60);
        int remainingMinutes = totalMinutes % (8 * 60);
        int hours = remainingMinutes / 60;
        int minutes = remainingMinutes % 60;
        
        StringBuilder result = new StringBuilder();
        if (days > 0) {
            result.append(days).append("d ");
        }
        if (hours > 0) {
            result.append(hours).append("h ");
        }
        if (minutes > 0 || result.length() == 0) {
            result.append(minutes).append("min");
        }
        
        return result.toString().trim();
    }
    
    @Override
    public String toString() {
        return String.format("[%-8s] %-30s linea %d:%d - %s [%s]",
            severity, rule, line, col, message, getFormattedTime());
    }
}
 