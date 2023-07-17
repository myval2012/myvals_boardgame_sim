package util;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.PushbackReader;

public class LineSeparator {
    /**
     * Checks if given string is valid line separator.
     *
     * @param n           String that is checked.
     * @param osDependent if set to true, read line separator must match the system separator, if set to false any line separator is valid (CR, CRLF. LF)
     * @return Result.
     */
    public static boolean isNewLine(String n, boolean osDependent) {
        if (osDependent) {
            return n.equals("\r") || n.equals("\r\n") || n.equals("\n");
        } else {
            return System.lineSeparator().equals(n);
        }
    }

    /**
     * Checks if character or sequence of characters read from stream is a valid line separator.
     * Characters that are not part of line separator are pushed back (max one character is pushed).
     *
     * @param fr          Stream to read from.
     * @param osDependent if set to true, read line separator must match the system separator, if set to false any line separator is valid (CR, CRLF. LF)
     * @return true if condition is met, otherwise false.
     */
    public static boolean isNewLine(@NotNull PushbackReader fr, boolean osDependent) throws IOException {
        int c = fr.read();
        if (osDependent) {
            String separator = System.lineSeparator();
            if (separator.length() != 1) {
                if (c == '\r') {
                    c = fr.read();
                    if (c == '\n') {
                        return true;
                    }
                }
            } else {
                if (separator.equals(Character.toString(c))) {
                    return true;
                }
            }
            fr.unread(c);
            return false;
        } else {
            if (c == '\r') {
                c = fr.read();
                if (c != '\n') {
                    fr.unread(c);
                }
                return true;
            } else if (c == '\n') {
                return true;
            } else {
                fr.unread(c);
                return false;
            }
        }
    }

    /**
     * Checks if character or sequence of characters is valid line separator. First character is c1. Addition character may be read from stream.
     * Characters that are not part of line separator are pushed back (max one character is pushed).
     *
     * @param c1          First character to check.
     * @param fr          Stream to read from.
     * @param osDependent if set to true, read line separator must match the system separator, if set to false any line separator is valid (CR, CRLF. LF)
     * @return true if condition is met, otherwise false.
     */
    public static boolean isNewLine(int c1, @NotNull PushbackReader fr, boolean osDependent) throws IOException {
        if (osDependent) {
            String separator = System.lineSeparator();
            if (separator.length() != 1) {
                if (c1 == '\r') {
                    int c = fr.read();
                    if (c == '\n') {
                        return true;
                    }
                    fr.unread(c);
                }
            } else {
                return separator.equals(Character.toString(c1));
            }
            return false;
        }

        // check is OS independent
        if (c1 == '\r') {
            int c = fr.read();
            if (c != '\n') {
                fr.unread(c);
            }
            return true;
        }
        return c1 == '\n';
    }
}
