package simple.mind.template;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class TemplateBlock {
    String templateString;
    int tabCount;
    LineType lineType;
    Integer lineNumber;
    String name;
    String loadableTemplateName;
    Map<String, TemplateProcessor> templateProcessorMap;
    StringBuilder simpleInsret;
    List<Token> tokenList = null;

    public TemplateBlock(String tmplateLine, int lineNumber) throws BadFormatException {
        this.lineNumber = lineNumber;
        templateString = tmplateLine;

        setTabCount();
        setLineType();
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

    private void setName(boolean loadable) throws BadFormatException {
        String a = templateString.trim().split("\n")[0];

        String[] tr = a.replaceAll(" +", " ").split(" ");

        if (tr.length < 2)
            throw new BadFormatException(templateString + "\n is incorrect");
        if (loadable)
            loadableTemplateName = tr[1];
        name = tr[tr.length - 1];
    }

    private Map<String, TemplateProcessor> getNewMap() {
        return new TreeMap<String, TemplateProcessor>();
    }

    private void setLineType() {
        name = "";
        String s = templateString.trim();
        String splits[] = s.split("\n");
        if (s.startsWith(TemplateProcessor.IMPORT)) {
            lineType = LineType.IMPORT;
            templateProcessorMap = getNewMap();
            setName(true);
        } else if (s.startsWith(TemplateProcessor.IMPORT_ONCE)) {
            lineType = LineType.IMPORT_ONCE;
            templateProcessorMap = getNewMap();
            setName(true);
        } else if (s.startsWith(TemplateProcessor.INSERT)) {
            lineType = LineType.INSERT;
            simpleInsret = new StringBuilder();
            setName(false);
        } else if (s.startsWith(TemplateProcessor.START)
                && splits[splits.length - 1].trim().startsWith(TemplateProcessor.END)) {
            lineType = LineType.REPEATE;
            setName(false);
            // name information will be lost
            StringJoiner sb = new StringJoiner("\n");

            for (int i = 1; i < splits.length - 1; i++) {
                sb.add(splits[i]);
            }
            loadableTemplateName = sb.toString();
            templateProcessorMap = getNewMap();

        } else {
            lineType = LineType.SIMPLE_LINE;
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
        if (lastWordPos < processing.length()) {
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

    private String getTabs(int base_tab) {
        StringBuilder sbv = new StringBuilder();
        for (int i = 0; i < tabCount + base_tab; i++) {
            sbv.append(TemplateProcessor.TAB);
        }
        return sbv.toString();
    }

    public String toString() {
        return toString(0);
    }

    public String toString(int base_tab) {
        String baseTabeStr = getTabs(base_tab);
        StringBuilder sbv = new StringBuilder();
        if (lineType == LineType.IMPORT || lineType == LineType.IMPORT_ONCE) {
            for (TemplateProcessor temp : templateProcessorMap.values()) {
                sbv.append(temp.toString());
            }
            return sbv.toString();
        }
        if(lineType == LineType.REPEATE) {
            for (TemplateProcessor temp : templateProcessorMap.values()) {
                sbv.append(temp.toString());
            }
            return sbv.toString();
        }
        if (lineType == LineType.INSERT) {
            sbv.append(baseTabeStr);
            return sbv.append(simpleInsret).toString();
        }
        if (lineType == LineType.SIMPLE_LINE) {
            sbv.append(baseTabeStr);
            for (Token t : tokenList) {
                sbv.append(t.token);
            }
            return sbv.append("\n").toString();
        }
        throw new NotImpementedYetException(lineType + " Not implemented yet");
    }
}
