public record Issue(Severity severity, String rule, int line, int col, String message) {
    @Override
    public String toString() {
        return String.format("[%-8s] %-30s línea %d:%d — %s", severity, rule, line, col, message);
    }
}
 