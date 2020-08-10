package simple.mind.template;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.base.Strings;

/**
 * @author johny Not writing this to be a brand new way of parsing template
 * 
 */
public class TemplateProcessor {
    static final String TEMPLATE = ".template";
    static final String IMPORT = "#import ";
    static final String START = "#start ";
    static final String END = "#end ";
    static final String INSERT = "#insert ";
    static final String TAB = "    ";
    static final String LOOK = "##";
    static final int LOOK_LENGTH = 2;
    public static final String IMPORT_ONCE = "#import_once ";
    int base_tab = 0;
    String sourceFile;
    List<TemplateBlock> templateLines;
    Map<String, Integer> varList = new HashMap<String, Integer>();
    Map<String, TemplateBlock> blockList = new HashMap<String, TemplateBlock>();

    public TemplateProcessor(String dataSource) {
        InputStream is;

        sourceFile = dataSource;
        is = this.getClass().getClassLoader().getResourceAsStream(dataSource + TEMPLATE);
        if (is == null) {
            throw new BadIOException("Resource " + sourceFile + TEMPLATE + " not found");
        }
        prepare(is);
    }

    public TemplateProcessor(String dataSource, String from, boolean loadFromFile) {

        InputStream is;
        if (loadFromFile) {
            sourceFile = from + " > " + dataSource;
            is = this.getClass().getClassLoader().getResourceAsStream(dataSource + TEMPLATE);
            if (is == null) {
                throw new BadIOException("Resource " + sourceFile + TEMPLATE + " not found.");
            }
        } else {
            sourceFile = from;
            is = new ByteArrayInputStream(dataSource.getBytes());
        }
        prepare(is);
    }

    TemplateProcessor(InputStream is) {
        prepare(is);
    }

    private void addCount() {
        TemplateBlock last = templateLines.get(templateLines.size() - 1);
        if (last == null)
            return;
        if (last.getTokenList() == null)
            return;
        for (Token t : last.getTokenList()) {
            if (false == t.processed) {
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

    private void addToBlockMap() {
        TemplateBlock last = templateLines.get(templateLines.size() - 1);
        if (Strings.isNullOrEmpty(last.name))
            return;
        if (blockList.containsKey(last.name))
            throw new DuplicateNameException(last.name + " repeat in the same file");
        blockList.put(last.name, last);
    }

    private void prepare(InputStream is) {
        templateLines = new ArrayList<TemplateBlock>();
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        StringBuilder contLine = null;
        String line;
        int lineNumber = 0;
        String startName = "";
        try {
            while ((line = br.readLine()) != null) {
                lineNumber++;
                String simpleLine = line.trim().replaceAll("\\s+", " ");
                if (contLine == null && line.trim().startsWith(START)) {
                    startName = simpleLine.split(" ")[1];
                    contLine = new StringBuilder();
                    contLine.append(line).append("\n");
                } else if (contLine != null && simpleLine.startsWith(END + startName)) {
                    contLine.append(line).append("\n");
                    templateLines.add(new TemplateBlock(contLine.toString(), lineNumber));
                    contLine = null;
                    startName = null;
                } else if (contLine != null) {
                    contLine.append(line).append("\n");
                } else {
                    templateLines.add(new TemplateBlock(line, lineNumber));
                }
                if (contLine == null) {
                    addCount();
                    addToBlockMap();
                }
            }
        } catch (IOException e) {
            throw new BadIOException(e.getMessage());
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

        for (TemplateBlock tb : templateLines) {
            sb.append(tb.toString(base_tab));
        }
        return sb.toString();
    }

    public TemplateProcessor addRepeatBlock(String name, String strBlockName) {
        TemplateBlock block = blockList.get(name);
        if (block == null) {
            throw new BlockMissingException(
                    name + " block is missing, checking your spelling in file " + this.sourceFile);
        }
        if (block.lineType != LineType.REPEATE) {
            throw new IncorrectActionException(
                    "Repeat inclusion cannot be done on type " + block.lineType + " for name: " + name + ", fileName: "
                            + block.loadableTemplateName + "[" + block.lineNumber + "]");
        }

        if (block.lineType == LineType.IMPORT_ONCE && block.templateProcessorMap.size() > 1) {
            throw new IncorrectActionException("Import can be done one time only. at fileName: "
                    + block.loadableTemplateName + ": " + block.lineNumber);
        }
        TemplateProcessor tp = block.templateProcessorMap.get(strBlockName);
        if (tp != null) {
            return tp;
        }

        tp = new TemplateProcessor(block.loadableTemplateName, this.sourceFile, false);
        tp.base_tab = block.tabCount;
        block.templateProcessorMap.put(strBlockName, tp);
        return tp;
    }

    public TemplateProcessor addImportBlock(String name, String strBlockName) {
        TemplateBlock block = blockList.get(name);
        if (block == null) {
            throw new BlockMissingException(
                    name + " block is missing, checking your spelling in file " + this.sourceFile);
        }
        if (block.lineType != LineType.IMPORT && block.lineType != LineType.IMPORT_ONCE) {
            throw new IncorrectActionException("Import cannot be done on type " + block.lineType + " for name: " + name
                    + ", fileName: " + block.loadableTemplateName + "[" + block.lineNumber + "]");
        }
        if (block.lineType == LineType.IMPORT_ONCE && block.templateProcessorMap.size() > 1) {
            throw new IncorrectActionException("Import can be done one time only. at fileName: "
                    + block.loadableTemplateName + ": " + block.lineNumber);
        }
        TemplateProcessor tp = block.templateProcessorMap.get(strBlockName);
        if (tp != null) {
            return tp;
        }
        tp = new TemplateProcessor(block.loadableTemplateName, this.sourceFile, true);
        tp.base_tab = block.tabCount;
        block.templateProcessorMap.put(strBlockName, tp);
        return tp;
    }

    public void addToInsert(String name, String text) {
        TemplateBlock block = blockList.get(name);
        if (block.lineType != LineType.INSERT) {
            throw new IncorrectActionException("Insert cannot be done on type " + block.lineType + " for name: " + name
                    + ", fileName: " + block.loadableTemplateName + "[" + block.lineNumber + "]");
        }
        block.simpleInsret.append(text);
    }

}
