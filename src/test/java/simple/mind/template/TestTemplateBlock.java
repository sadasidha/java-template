package simple.mind.template;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

public class TestTemplateBlock {

    @Test
    public void All_Comb_Test() {
        TemplateBlock tb = new TemplateBlock("I am ##NAME##", 0);
        List<Token> t = tb.getTokenList();
        assertEquals(t.size(), 2);
        assertTrue(t.get(0).processed);
        assertEquals(t.get(0).token, "I am ");
        assertFalse(t.get(1).processed);
        assertEquals(t.get(1).token, "##NAME##");

        tb = new TemplateBlock("##NAME## is my name", 0);
        t = tb.getTokenList();
        assertEquals(t.size(), 2);
        assertFalse(t.get(0).processed);
        assertEquals(t.get(0).token, "##NAME##");
        assertTrue(t.get(1).processed);
        assertEquals(t.get(1).token, " is my name");

        tb = new TemplateBlock("This is a ##STATUS## day", 0);
        t = tb.getTokenList();
        assertEquals(t.size(), 3);
        assertTrue(t.get(0).processed);
        assertEquals(t.get(0).token, "This is a ");
        assertFalse(t.get(1).processed);
        assertEquals(t.get(1).token, "##STATUS##");
        assertTrue(t.get(2).processed);
        assertEquals(t.get(2).token, " day");

        tb = new TemplateBlock("My name is ##NAME##. People call me ##NAME##. I am ##AGE## year old.", 0);
        t = tb.getTokenList();
        assertEquals(t.size(), 7);
        assertTrue(t.get(0).processed);
        assertEquals(t.get(0).token, "My name is ");
        assertFalse(t.get(1).processed);
        assertEquals(t.get(1).token, "##NAME##");
        assertTrue(t.get(2).processed);
        assertEquals(t.get(2).token, ". People call me ");
        assertFalse(t.get(3).processed);
        assertEquals(t.get(3).token, "##NAME##");
        assertTrue(t.get(4).processed);
        assertEquals(t.get(4).token, ". I am ");
        assertFalse(t.get(5).processed);
        assertEquals(t.get(5).token, "##AGE##");
        assertTrue(t.get(6).processed);
        assertEquals(t.get(6).token, " year old.");
    }
}
