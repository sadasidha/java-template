package simple.mind.template;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.List;

import org.junit.jupiter.api.Test;

public class TestTemplateBlock {

    @Test
    public void All_Comb_Test() throws BadFormatException {
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

    @Test
    public void testInputBlock() throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append("#start if\n");
        sb.append("#import webcontroller/grab_and_convert/mandatory/biginteger bigint\n");
        sb.append("#end if");
        TemplateProcessor tp = new TemplateProcessor(this.getClass(), sb.toString(), "", false);
        TemplateProcessor if1 = tp.addRepeatBlock("if", "if1");
        TemplateProcessor bigint = if1.addImportBlock("bigint", "bigint");
        bigint.setValue("VAR_NAME", "userName");
        assertEquals(tp.toString(), "if (userName == null || !userName.matches(\"^[0-9]+$\")\n" + "    return false;\n"
                + "userName_fnc = new java.math.BigInteger(userName);\n");
    }
    
    @Test
    public void testBlock() throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append("public static void main(String []args){").append("\n");
        sb.append("    #start block").append("\n");
        sb.append("    #insert mulLine").append("\n");
        sb.append("    #end block").append("\n");
        sb.append("}").append("\n");
        TemplateProcessor tp = new TemplateProcessor(this.getClass(), sb.toString(), "", false);
        TemplateProcessor block = tp.addRepeatBlock("block", "crank");
        block.addToInsertSplitNewLIne("mulLine", "System.out.println(\"First Line\");\nSystem.out.println(\"Second Line\");\n");
        
        System.out.println(tp.toString());
    }
}
