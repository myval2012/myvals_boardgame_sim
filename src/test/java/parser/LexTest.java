package parser;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.FileReader;
import java.io.IOException;
import java.io.PushbackReader;

import static org.junit.jupiter.api.Assertions.*;

class LexTest {

    private PushbackReader br = null;
    private Token t = null;
    private String pathToResources = "./src/test/resources/";

    @Test
    void blankFileTest() throws Exception {
        br = new PushbackReader(new FileReader(pathToResources + "blank.txt"));

        t = Lex.readToken(br);
        assertEquals(Token.Type.T_EOF, t.getType());
    }

    @Test
    void newLineTest() throws Exception {
        Token.Type[] types = {Token.Type.T_NEWLINE, Token.Type.T_EOF};
        br = new PushbackReader(new FileReader(pathToResources + "newline.txt"));

        for (Token.Type type :
                types) {
            t = Lex.readToken(br);
            assertEquals(type, t.getType());
        }
    }

    @Test
    void eQTest() throws Exception {
        Token.Type[] types = {Token.Type.T_EQ, Token.Type.T_EOF};
        br = new PushbackReader(new FileReader(pathToResources + "eq.txt"));

        for (Token.Type type :
                types) {
            t = Lex.readToken(br);
            assertEquals(type, t.getType());
        }
    }

    @Test
    void BoardTest() throws Exception {
        Token.Type[] types = {Token.Type.T_NAME, Token.Type.T_LEFTCB, Token.Type.T_NEWLINE,
                Token.Type.T_FIELDEMPTY, Token.Type.T_FIELD, Token.Type.T_FIELDEMPTY, Token.Type.T_NEWLINE,
                Token.Type.T_FIELDEMPTY, Token.Type.T_FIELD, Token.Type.T_FIELDEMPTY, Token.Type.T_NEWLINE,
                Token.Type.T_FIELDEMPTY, Token.Type.T_FIELD, Token.Type.T_FIELDEMPTY, Token.Type.T_NEWLINE,
                Token.Type.T_RIGHTCB, Token.Type.T_EOF};
        br = new PushbackReader(new FileReader(pathToResources + "model_board.txt"));
        int[] values = {1, 2, 3};
        int m = 0;

        for (Token.Type type :
                types) {
            t = Lex.readToken(br);
            assertEquals(type, t.getType());
            if (t.getType() == Token.Type.T_FIELD) {
                assertEquals(t.getId(), values[m]);
                m++;
            } else if (t.getType() == Token.Type.T_NAME) {
                assertEquals("board", t.getName());
            } else {
                assertThrows(NullPointerException.class, () -> t.getStrValue());
                assertThrows(NullPointerException.class, () -> t.getBoolValue());
                assertThrows(NullPointerException.class, () -> t.getIntValue());
            }
        }
    }

    @Test
    void PlayerTest() throws Exception {
        Token.Type[] types = {Token.Type.T_NAME, Token.Type.T_LEFTCB, Token.Type.T_NEWLINE,
                Token.Type.T_NAME, Token.Type.T_EQ, Token.Type.T_INT, Token.Type.T_COMMA, Token.Type.T_NEWLINE,
                Token.Type.T_NAME, Token.Type.T_EQ, Token.Type.T_STR, Token.Type.T_COMMA,
                Token.Type.T_NAME, Token.Type.T_EQ, Token.Type.T_BOOL, Token.Type.T_RIGHTCB,
                Token.Type.T_NEWLINE, Token.Type.T_EOF};
        br = new PushbackReader(new FileReader(pathToResources + "model_player.txt"));
        String[] name_values = {"player", "income", "name", "isGOOD"};
        int m = 0;

        for (Token.Type type :
                types) {
            t = Lex.readToken(br);
            assertEquals(type, t.getType());

            if (t.getType() == Token.Type.T_NAME){
                assertEquals(t.getName(), name_values[m]);
                m++;
            } else if (t.getType() == Token.Type.T_STR) {
                assertEquals("Petr", t.getStrValue());
            } else if (t.getType() == Token.Type.T_BOOL) {
                assertEquals(true, t.getBoolValue());
            } else if (t.getType() == Token.Type.T_INT) {
                assertEquals( 14000, t.getIntValue());
            }
        }
    }

    @Test
    void CommentTest() throws Exception {
        Token.Type[] types = {Token.Type.T_NAME, Token.Type.T_LEFTCB,
                Token.Type.T_NAME, Token.Type.T_EQ, Token.Type.T_INT, Token.Type.T_RIGHTCB,
                Token.Type.T_NEWLINE,
                Token.Type.T_NAME, Token.Type.T_LEFTCB, Token.Type.T_NEWLINE, Token.Type.T_NAME,
                Token.Type.T_EQ, Token.Type.T_BOOL, Token.Type.T_NEWLINE, Token.Type.T_RIGHTCB,
                Token.Type.T_EOF };
        br = new PushbackReader(new FileReader(pathToResources + "comment.txt"));
        for (Token.Type type :
                types) {
            t = Lex.readToken(br);
            assertEquals(type, t.getType());

            if (t.getType() == Token.Type.T_INT){
                assertEquals( -1, t.getIntValue());
            }
        }
    }

    @AfterEach
    void tearDown() throws IOException {
        if (br != null) {
            br.close();
            br = null;
        }
        t = null;
    }
}