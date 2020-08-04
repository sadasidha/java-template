package simple.mind.template;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class TemplateBlock {
    boolean isLoaded;
    String templateString;
    int tabCount;
    LineType lineType;
    Integer lineNumber;
    String name;
    String produces;
    TemplateProcessor importTempate = null;

    List<Token> tokenList = null;

    public TemplateBlock(String tmplateLine, int lineNumber) {
        templateString = tmplateLine;
        setTabCount();
        setLineType();
    }

    public boolean isLoaded() {
        return isLoaded;
    }

    public List<Token> getTokenList() {
        return tokenList;
    }

    private String getFirstLine() {
        String[] ar = templateString.split("\n");
        for (String s : ar) {
            if (!s.trim().startsWith(TemplateProcessor.START)) {
                return s;
            }
        }
        return null;
    }

    private void setTabCount() {
        tabCount = 0;
        String s = getFirstLine();
        Matcher m = Pattern.compile("^( +)").matcher(s);
        if (m.find()) {
            tabCount = m.group(1).length() / TemplateProcessor.TAB.length();
        }
    }

    private void setName() {
        String a = templateString.split("\n")[0];
        String[] tr = a.replaceAll(" +", " ").split(" ");
        name = tr[tr.length - 1];
    }

    private void setLineType() {
        name = "";
        produces = "";

        String s = templateString.trim();
        if (s.startsWith(TemplateProcessor.MAY_IMPORT)) {
            isLoaded = false;
            lineType = LineType.MAY_IMPORT;
            setName();
        } else if (s.startsWith(TemplateProcessor.IMPORT)) {
            isLoaded = false;
            lineType = LineType.IMPORT;
            setName();
        } else if (s.startsWith(TemplateProcessor.INSERT)) {
            isLoaded = true;
            lineType = LineType.INSERT;
            setName();
        } else if (s.startsWith(TemplateProcessor.REPEAT_IMPORT)) {
            isLoaded = false;
            lineType = LineType.REPEAT_IMPORT;
            setName();
        } else if (s.startsWith(TemplateProcessor.START) && s.endsWith(TemplateProcessor.END)) {
            if (s.contains(TemplateProcessor.IMPORT) || s.contains(TemplateProcessor.MAY_IMPORT)) {
                isLoaded = false;
            } else {
                isLoaded = true;
            }
            lineType = LineType.REPEATE;
            setName();
        } else {
            isLoaded = true;
            produces = s;
            tokenize();
        }
    }

    private void tokenize() {
        int firstIndex = 0;
        int secondIndex;
        int lastWordPos = 0;
        String processing = templateString.trim();
        tokenList = new ArrayList<Token>();
        do {
            firstIndex = processing.indexOf(TemplateProcessor.LOOK, firstIndex);
            if (firstIndex == -1)
                break;
            secondIndex = processing.indexOf(TemplateProcessor.LOOK, firstIndex + TemplateProcessor.LOOK_LENGTH);
            if (secondIndex == -1) {
                break;
            }
            secondIndex += TemplateProcessor.LOOK_LENGTH;
            if (lastWordPos != firstIndex) {
                tokenList.add(new Token(true, processing.substring(lastWordPos, firstIndex)));
            }
            tokenList.add(new Token(false, processing.substring(firstIndex, secondIndex)));
            firstIndex = secondIndex;
            lastWordPos = firstIndex;
        } while (true);
        if (lastWordPos + 1 < processing.length()) {
            tokenList.add(new Token(true, processing.substring(lastWordPos, processing.length())));
        }
    }

    Integer setVariables(String varName, String value) {
        Integer i = 0;
        String s = TemplateProcessor.LOOK + varName + TemplateProcessor.LOOK;
        for (Token t : tokenList) {
            if (t.processed)
                continue;
            if (t.token.equals(s)) {
                i++;
                t.token = value;
                t.processed = true;
            }
        }
        return i;
    }

    public String toString() {
        if (isLoaded == false)
            return templateString;
        StringBuilder sbv = new StringBuilder();
        for (int i = 0; i < tabCount; i++) {
            sbv.append(TemplateProcessor.TAB);
        }
        if (tokenList == null)
            return sbv.append("\n").toString();
        for (Token t : tokenList) {
            sbv.append(t.token);
        }
        return sbv.append("\n").toString();
    }
}
