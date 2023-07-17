package parser;

/**
 * Represents a token from lexical analyzer.
 * Token always has {@link Token.Type} that can be accessed by {@link #getType()}.
 * Some types also have value:
 * <ul>
 *     <li>{@link Token.Type#T_STR} --> Has a string literal as its value (use {@link #getStrValue()} to get it).</li>
 *     <li>{@link Token.Type#T_NAME} --> Has a some name/property/value as its value
 *     (use {@link #getName()} to get it).</li>
 *     <li>{@link Token.Type#T_INT} --> Has a int literal as its value (use {@link #getIntValue()} to get it).</li>
 *     <li>{@link Token.Type#T_BOOL} --> Has a boolean literal as its value (use {@link #getBoolValue()} to get it).</li>
 *     <li>{@link Token.Type#T_FIELD} --> Has a int id as its value (use {@link #getId()} to get it).</li>
 * </ul>
 */
public class Token {

    private Token.Type type;

    //TODO convert to union type if possible
    private String strValue;
    private Integer intValue;
    private Boolean boolValue;

    /**
     * @param type Type of token.
     * @param value Value of token. If this type of token has no value, null is passed instead.
     */
    public Token(Token.Type type, Object value){
        this.strValue = null;
        this.intValue = null;
        this.boolValue = null;
        this.type = type;
        switch (type){
            case T_BOOL -> this.boolValue = (Boolean) value;
            case T_INT -> this.intValue = (Integer) value;
            case T_STR, T_NAME -> this.strValue = (String) value;
        }
    }

    /**
     * Type of token.
     */
    public enum Type {
        T_NEWLINE,

        /**
         * Left curly bracket.
         */
        T_LEFTCB,

        /**
         * Right curly bracket.
         */
        T_RIGHTCB,

        /**
         * Equal.
         */
        T_EQ,

        T_COMMA,
        /**
         * String literal.
         */
        T_STR,

        /**
         * Int literal.
         */
        T_INT,

        /**
         * Bool literal (allowed is lowercase false/true).
         */
        T_BOOL,
        /**
         * General name/property/value/model following standard naming conventions.
         */
        T_NAME,

        /**
         * Represents end of file.
         */
        T_EOF,

        /**
         * Represents field with given id.
         */
        T_FIELD,

        /**
         * Represents field without id.
         */
        T_FIELDEMPTY
    }


    /**
     * Getter for value if it is {@link String}.
     * @return Value if token contains {@link String} value, otherwise {@link  NullPointerException}.
     */
    public String getStrValue() {
        if (this.strValue == null){
            throw new NullPointerException("This token does not contain string value.");
        }
        return this.strValue;
    }

    /**
     * Getter for value if it is {@link Boolean}.
     * @return Value if token contains {@link Boolean} value, otherwise {@link  NullPointerException}.
     */
    public Boolean getBoolValue() {
        if (this.boolValue == null){
            throw new NullPointerException("This token does not contain boolean value.");
        }
        return this.boolValue;
    }

    /**
     * Getter for value if it is {@link Integer}.
     * @return Value if token contains {@link Integer} value, otherwise {@link  NullPointerException}.
     */
    public Integer getIntValue(){
        if (this.intValue == null){
            throw new NullPointerException("This token does not contain integer value.");
        }
        return this.intValue;
    }

    /**
     * Getter for token value if token's type is {@link Token.Type#T_NAME}. This method is only sugar for {@link #getStrValue()}.
     * @return Value if token contains {@link Integer} value, otherwise {@link  NullPointerException}.
     */
    public String getName() {
        return this.getStrValue();
    }

    /**
     * Getter for token value if token's type is {@link Token.Type#T_FIELD}. This method is only sugar for {@link #getIntValue()}.
     * @return Value if token contains id, otherwise {@link  NullPointerException}.
     */
    public Integer getId(){
        return this.getIntValue();
    }

    /**
     * Getter for token type.
     * @return Type of token.
     */
    public Type getType() {
        return type;
    }
}
