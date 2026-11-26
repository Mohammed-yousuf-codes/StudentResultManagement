package com.student.util;

public final class Html {
    private Html() {}

    /** Escapes text for safe use in HTML element content and quoted attributes. */
    public static String esc(Object value) {
        if (value == null) return "";
        String s = value.toString();
        StringBuilder b = new StringBuilder(s.length() + 16);
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            switch (c) {
                case '&': b.append("&amp;"); break;
                case '<': b.append("&lt;"); break;
                case '>': b.append("&gt;"); break;
                case '"': b.append("&quot;"); break;
                case '\'': b.append("&#39;"); break;
                default: b.append(c);
            }
        }
        return b.toString();
    }

    /** 82 -> "82", 82.5 -> "82.5" */
    public static String num(double v) {
        return v == Math.rint(v) ? String.valueOf((long) v) : String.format("%.1f", v);
    }

    public static String pct(double v) {
        return String.format("%.1f", v);
    }
}
