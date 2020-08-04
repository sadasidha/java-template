package simple.mind.template;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author johny Not writing this to be a brand new way of parsing template
 * 
 */
public class TemplateProcessor {
    static final String TEMPLATE = ".template";
    static final String IMPORT = "#import ";
    static final String MAY_IMPORT = "#may_import ";
    static final String REPEAT_IMPORT = "#repeat_import";
    static final String START = "#start ";
    static final String END = "#end ";
    static final String INSERT = "#insert ";
    static final String TAB = "    ";
    static final String LOOK = "##";
    static final int LOOK_LENGTH = 2;
    List<TemplateBlock> templateLines;
    Map<String, Integer> varList = new HashMap<String, Integer>();

    TemplateProcessor(String fileName) throws IOException {
        prepare(this.getClass().getClassLoader().getResourceAsStream(fileName + TEMPLATE));
    }

    private void addCount() {
        TemplateBlock last = templateLines.get(templateLines.size() - 1);
        if (last == null)
            return;
        if (last.getTokenList() == null)
            return;
        for (Token t : last.getTokenList()) {
            if (t.processed = false) {
                String p = t.token.replace("#", "");
                Integer i;
                if (varList.containsKey(p)) {
                    i = varList.get(p);
                } else {
                    i = 1;
                }
                varList.put(p, i);
            }
        }
    }

    private void subtract(String s, Integer count) {
        if (!varList.containsKey(s))
            return;
        varList.put(s, (varList.get(s) - count));
    }

    private void prepare(InputStream is) throws IOException {
        templateLines = new ArrayList<TemplateBlock>();
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        StringBuilder contLine = null;
        String line;
        int lineNumber = 0;
        while ((line = br.readLine()) != null) {
            lineNumber++;
            if (contLine == null && line.trim().startsWith(START)) {
                contLine = new StringBuilder();
                contLine.append(line).append("\n");
            } else if (contLine != null && line.trim().startsWith(END)) {
                contLine.append(line).append("\n");
                templateLines.add(new TemplateBlock(contLine.toString(), lineNumber));
                contLine = null;
            } else if (contLine != null) {
                contLine.append(line).append("\n");
            } else {
                templateLines.add(new TemplateBlock(line, lineNumber));
            }
            addCount();
        }
    }

    private Integer setValueToAll(String varName, String value) {
        Integer count = 0;
        for (TemplateBlock t : templateLines) {
            if (t.lineType == LineType.SIMPLE_LINE) {
                count += t.setVariables(varName, value);
            }
        }
        return count;
    }

    public boolean setValue(String varName, String value) {
        if (!varList.containsKey(varName) || varList.get(varName) == 0) {
            return false;
        }
        Integer i = setValueToAll(varName, value);
        subtract(varName, i);
        return i > 0;
    }
    
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for(TemplateBlock tb: templateLines) {
            sb.append(tb.toString());
        }
        return sb.toString();
    }
}
