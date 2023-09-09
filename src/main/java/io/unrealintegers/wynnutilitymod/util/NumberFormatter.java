package io.unrealintegers.wynnutilitymod.util;

public class NumberFormatter {
    private static final String[] SUFFIXES = {"", "k", "M", "B", "T"};

    public static String format(long num) {
        if (num < 0) {
            return "-" + format(-num);
        } else if (num < 1000) {
            return "" + num;
        }

        int pow1k;
        for (pow1k = 0; pow1k < SUFFIXES.length; pow1k++) {
            if (num < 999500) {  // 999499 = 999k largest number representable
                break;
            }

            num /= 1000;
        }

        int divisor;
        for (divisor = 1; num >= divisor; divisor *= 10);
        divisor /= 10;

        double base = (double) num / divisor;
        if (divisor >= 1000) {
            pow1k++;
            divisor /= 1000;
        }

        if (divisor == 100) {
            return "" + Math.round(base * 100) + SUFFIXES[pow1k];
        } else {
            return "" + (double) Math.round(base * 100) / (100 / divisor) + SUFFIXES[pow1k];
        }
    }
}
