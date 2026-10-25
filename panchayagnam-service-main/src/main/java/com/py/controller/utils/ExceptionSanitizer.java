package com.py.controller.utils;

public final class ExceptionSanitizer {

    private ExceptionSanitizer() {}

    public static String sanitize(String message) {
        if (message == null) return "An unexpected error occurred";
        String m = message;
        // Remove fully qualified exception class names like com.foo.BarException
        m = m.replaceAll("\\b([A-Za-z0-9_\\.]+Exception)\\b", "");
        // Remove common stacktrace fragments
        m = m.replaceAll("\\n\\s*at [^\\n]+", "");
        // Remove 'Caused by' fragments
        m = m.replaceAll("Caused by:[^\\n]+", "");
        m = m.trim();
        if (m.isEmpty()) return "An unexpected error occurred";
        return m.length() > 200 ? m.substring(0, 197) + "..." : m;
    }
}
