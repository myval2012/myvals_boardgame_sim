package parser;

import util.LineSeparator;

import java.io.PushbackReader;

/**
 * Contains methods for lexical analysis
 */
public class Lex {
    private static State state;
    private static StringBuilder buffer;


    /**
     * Represents inner state of lexical analyzer.
     */
    private enum State {
        LEX_S,
        /**
         * Not yet final state of string.
         */
        LEX_STR1,
        /**
         * Not yet final state of comment.
         */
        LEX_COMMENT1,
        LEX_COMMENTF,
        LEX_INT,
        LEX_NAME,
        /**
         * ( was found.
         */
        LEX_FIELD1,
        /**
         * x was found --> this field has no id.
         */
        LEX_FIELDX,
        /**
         * id was found --> this field has id.
         */
        LEX_FIELD2,
        /**
         * White space found --> end of field's id.
         */
        LEX_FIELD3,
        /**
         * '-' was read --> must be negative integer.
         */
        LEX_MINUS,
    }

    /**
     * Reads a single token out of given stream.
     *
     * @param br reader to find token in.
     * @return Found Token.
     */
    public static Token readToken(PushbackReader br) throws Exception {
        Token t;
        buffer = new StringBuilder();
        state = State.LEX_S;
        while (true) {
            int c = br.read();
            switch (Lex.state) {
                case LEX_S -> {
                    t = stateSHelper(c, br);
                    if (t != null) {
                        Lex.state = State.LEX_S;
                        return t;
                    }
                }
                case LEX_STR1 -> {
                    if (c == '"') {
                        return new Token(Token.Type.T_STR, buffer.toString());
                    } else if (!Character.isISOControl(c) && !LineSeparator.isNewLine(c, br, false)) {
                        buffer.append(Character.toString(c));
                    } else {
                        throw new Exception("Unsupported character:'" + Character.toString(c) + "'");
                    }
                }
                case LEX_COMMENT1 -> {
                    if (c == '/') {
                        state = State.LEX_COMMENTF;
                    } else {
                        throw new Exception("Unsupported character:'" + Character.toString(c) + "'");
                    }
                }
                case LEX_COMMENTF -> {
                    if (LineSeparator.isNewLine(c, br, false)) {
                        return new Token(Token.Type.T_NEWLINE, null);
                    }
                }
                case LEX_INT -> {
                    if (Character.isDigit(c)) {
                        buffer.append(Character.toString(c));
                    } else {
                        br.unread(c);
                        return new Token(Token.Type.T_INT, Integer.valueOf(buffer.toString()));
                    }
                }
                case LEX_NAME -> {
                    if (Character.isLetterOrDigit(c) || c == '_') {
                        buffer.append(Character.toString(c));
                    } else {
                        br.unread(c);
                        String value = buffer.toString();
                        if (value.equals("true") || value.equals("false")) {
                            return new Token(Token.Type.T_BOOL, value.equals("true"));
                        } else {
                            return new Token(Token.Type.T_NAME, value);
                        }
                    }
                }
                case LEX_FIELD1 -> {
                    if (Character.isDigit(c)) {
                        state = State.LEX_FIELD2;
                        buffer.append(Character.toString(c));
                    } else if (c == 'x') {
                        state = State.LEX_FIELDX;
                    } else if (!(Character.isSpaceChar(c) && !LineSeparator.isNewLine(c, br, false))) {
                        throw new Exception("Unsupported character:'" + Character.toString(c) + "'");
                    }
                }
                case LEX_FIELD2 -> {
                    if (Character.isDigit(c)) {
                        buffer.append(Character.toString(c));
                    } else if (Character.isSpaceChar(c) && !LineSeparator.isNewLine(c, br, false)) {
                        state = State.LEX_FIELD3;
                    } else if (c == ')') {
                        return new Token(Token.Type.T_FIELD, Integer.valueOf(buffer.toString()));
                    } else {
                        throw new Exception("Unsupported character:'" + Character.toString(c) + "'");
                    }
                }
                case LEX_FIELD3 -> {
                    if (c == ')') {
                        return new Token(Token.Type.T_FIELD, Integer.valueOf(buffer.toString()));
                    } else if (!(Character.isSpaceChar(c) && !LineSeparator.isNewLine(c, br, false))) {
                        throw new Exception("Unsupported character:'" + Character.toString(c) + "'");
                    }
                }
                case LEX_FIELDX -> {
                    if (c == ')') {
                        return new Token(Token.Type.T_FIELDEMPTY, null);
                    } else if (!(Character.isSpaceChar(c) && !LineSeparator.isNewLine(c, br, false))) {
                        throw new Exception("Unsupported character:'" + Character.toString(c) + "'");
                    }
                }
                case LEX_MINUS -> {
                    if (Character.isDigit(c) && c != '0') {
                        state = State.LEX_INT;
                        buffer.append(Character.toString(c));
                    } else {
                        throw new Exception("Unsupported character:'" + Character.toString(c) + "'");
                    }
                }
                default -> // should never get here
                        throw new Exception("There is a state of Lex that is not in readToken switch.");
            }
        }
    }

    private static Token stateSHelper(int c, PushbackReader br) throws Exception {
        Token t = null;
        switch (c) {
            case '{' -> t = new Token(Token.Type.T_LEFTCB, null);
            case '=' -> t = new Token(Token.Type.T_EQ, null);
            case '}' -> t = new Token(Token.Type.T_RIGHTCB, null);
            case ',' -> t = new Token(Token.Type.T_COMMA, null);
            case '"' -> Lex.state = State.LEX_STR1;
            case '/' -> Lex.state = State.LEX_COMMENT1;
            case '1', '2', '3', '4', '5', '6', '7', '8', '9' -> {
                buffer.append(Character.toString(c));
                Lex.state = State.LEX_INT;
            }
            case '(' -> Lex.state = State.LEX_FIELD1;
            case '-' -> {
                buffer.append(Character.toString(c));
                Lex.state = State.LEX_MINUS;
            }
            case -1 -> t = new Token(Token.Type.T_EOF, null);
            default -> {
                if (LineSeparator.isNewLine(c, br, false)){
                    t = new Token(Token.Type.T_NEWLINE, null);
                }
                else if (Character.isLetter(c) || c == '_') {
                    buffer.append(Character.toString(c));
                    Lex.state = State.LEX_NAME;
                } else if (!(Character.isSpaceChar(c))) {
                    throw new Exception("Unsupported character:'" + Character.toString(c) + "'");
                }
            }
        }
        return t;
    }
}
