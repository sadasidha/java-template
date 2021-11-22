package simple.mind.template;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Mohibur Rashid
 *
 */
class TemplateBlock {
    String templateString;
    int tabCount;
    LineType lineType;
    Integer lineNumber;
    String name;
    String loadableTemplateName;
    Map<String, TemplateProcessor> templateProcessorMap;
    List<String> simpleInsert;
    List<Token> tokenList;

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
            if (!s.trim().startsWith(TagsConst.START)) {
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
        if (s.startsWith(TagsConst.COMMENT)) {
            lineType = LineType.COMMENT;
        } else if (s.startsWith(TagsConst.IMPORT)) {
            lineType = LineType.IMPORT;
            templateProcessorMap = getNewMap();
            setName(true);
        } else if (s.startsWith(TagsConst.IMPORT_ONCE)) {
            lineType = LineType.IMPORT_ONCE;
            templateProcessorMap = getNewMap();
            setName(true);
        } else if (s.startsWith(TagsConst.INSERT)) {
            lineType = LineType.INSERT;
            simpleInsert = new ArrayList<String>();
            setName(false);
        } else if (s.startsWith(TagsConst.START)
                && splits[splits.length - 1].trim().startsWith(TagsConst.END)) {
            lineType = LineType.REPEATE;
            setName(false);
            // name information will be lost
            StringJoiner sb = new StringJoiner("\n");

            for (int i = 1; i < splits.length - 1; i++) {
                sb.add(splits[i].trim());
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
            firstIndex = processing.indexOf(TagsConst.LOOK, firstIndex);
            if (firstIndex == -1)
                break;
            secondIndex = processing.indexOf(TagsConst.LOOK, firstIndex + CommConst.LOOK_LENGTH);
            if (secondIndex == -1) {
                break;
            }
            secondIndex += CommConst.LOOK_LENGTH;
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
        String s = TagsConst.LOOK + varName + TagsConst.LOOK;
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
        String baseTabStr = getTabs(base_tab);
        StringBuilder sbv = new StringBuilder();
        if (lineType == LineType.IMPORT || lineType == LineType.IMPORT_ONCE) {
            if(templateProcessorMap.size()!=0)
                sbv.append(baseTabStr).append("// Importing ").append(this.loadableTemplateName).append("\n");
            for (TemplateProcessor temp : templateProcessorMap.values()) {
                sbv.append(temp.toString());
            }
            return sbv.toString();
        }
        if (lineType == LineType.REPEATE) {
            if(templateProcessorMap.size()!=0)
                sbv.append(baseTabStr).append("// Repeat block: ").append(this.name).append(" starts").append("\n");
            for (TemplateProcessor temp : templateProcessorMap.values()) {
                sbv.append(temp.toString());
            }
            if(templateProcessorMap.size()!=0)
                sbv.append(baseTabStr).append("// Repeat block: ").append(this.name).append(" ends").append("\n");
            return sbv.toString();
        }
        if (lineType == LineType.INSERT) {
            if(simpleInsert.size()!=0)
                sbv.append(baseTabStr).append("// Inserting :").append(this.name).append("\n");
            for (String s : simpleInsert) {
                sbv.append(baseTabStr).append(s).append("\n");
            }
            return sbv.toString();
        }
        if (lineType == LineType.SIMPLE_LINE) {
            sbv.append(baseTabStr);
            for (Token t : tokenList) {
                sbv.append(t.token);
            }
            return sbv.append("\n").toString();
        }
        if (lineType == LineType.COMMENT) {
            return "";
        }
        throw new NotImpementedYetException(lineType + " Not implemented yet");
    }
}

