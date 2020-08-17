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
 * @author Johny Not writing this to be a brand new way of parsing template
 * 
 */
public class TemplateProcessor {
    static final String TAB = "    ";
    Class<?> classs;
    int base_tab = 0;
    String sourceFile;
    List<TemplateBlock> templateLines;
    Map<String, TemplateBlock> blockList = new HashMap<String, TemplateBlock>();

    /**
     * Resource file must have to be in the same package of the declared class.<br>
     * If resource file name including path is resources/template/web.template<br>
     * <b>resourceFileName</b> must have to be <b>template/web</b>
     * 
     * @param cls              : give us the base for resource files
     * @param resourceFileName : loadable resource file file
     */
    public TemplateProcessor(Class<?> cls, String resourceFileName) {
        classs = cls;
        InputStream inputStream;
        sourceFile = resourceFileName;
        inputStream = cls.getClassLoader().getResourceAsStream(resourceFileName + Tags.TEMPLATE);
        if (inputStream == null) {
            throw new BadIOException("Resource " + sourceFile + Tags.TEMPLATE + " not found");
        }
        prepare(inputStream);
    }

    TemplateProcessor(Class<?> cls, String dataSource, String templateName, boolean loadFromFile) {
        classs = cls;
        InputStream is;
        if (loadFromFile) {
            sourceFile = templateName + " > " + dataSource;
            is = cls.getClassLoader().getResourceAsStream(dataSource + Tags.TEMPLATE);
            if (is == null) {
                throw new BadIOException("Resource " + sourceFile + Tags.TEMPLATE + " not found.");
            }
        } else {
            sourceFile = templateName;
            is = new ByteArrayInputStream(dataSource.getBytes());
        }
        prepare(is);
    }

    /**
     * @param cls
     * @param inputStream
     */
    public TemplateProcessor(Class<?> cls, InputStream inputStream) {
        classs = cls;
        prepare(inputStream);
    }

    private void addToBlockMap() {
        TemplateBlock last = templateLines.get(templateLines.size() - 1);
        if (Strings.isNullOrEmpty(last.name))
            return;
        if (blockList.containsKey(last.name))
            throw new DuplicateNameException(last.name + " repeat in the same file");
        blockList.put(last.name, last);
    }

    private void prepare(InputStream inputStream) {
        templateLines = new ArrayList<TemplateBlock>();
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder contLine = null;
        String line;
        int lineNumber = 0;
        String startName = "";
        try {
            while ((line = br.readLine()) != null) {
                lineNumber++;
                String simpleLine = line.trim().replaceAll("\\s+", " ");
                if (contLine == null && line.trim().startsWith(Tags.START)) {
                    startName = simpleLine.split(" ")[1];
                    contLine = new StringBuilder();
                    contLine.append(line).append("\n");
                } else if (contLine != null && simpleLine.startsWith(Tags.END + startName)) {
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
                    addToBlockMap();
                }
            }
        } catch (IOException e) {
            throw new BadIOException(e.getMessage());
        }
        try {
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param varName to set
     * @param value   to set with
     * @return
     */
    public TemplateProcessor setValue(String varName, String value) {
        Integer count = 0;
        for (TemplateBlock t : templateLines) {
            if (t.lineType == LineType.SIMPLE_LINE) {
                count += t.setVariables(varName, value);
            }
        }
        return this;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();

        for (TemplateBlock tb : templateLines) {
            sb.append(tb.toString(base_tab));
        }
        return sb.toString();
    }

    /**
     * if strBlockName is not unique then already existing block with the same name
     * will be returned
     * 
     * @param name         Template to get
     * @param strBlockName Name for the Template block that will be inserted
     * @return TemplateProcessor object
     */
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

        tp = new TemplateProcessor(classs, block.loadableTemplateName, this.sourceFile, false);
        tp.base_tab = block.tabCount + base_tab;
        block.templateProcessorMap.put(strBlockName, tp);
        return tp;
    }

    /**
     * This function is okay to use for import_once type block
     * 
     * @param name Template to get, also name will be used for inserting Import
     *             block with the same name
     * @return
     */
    public TemplateProcessor addImportBlock(String name) {
        return addImportBlock(name, name);
    }

    /**
     * @param name         import block to get
     * @param strBlockName name of inserted import block
     * @return
     */
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
        tp = new TemplateProcessor(classs, block.loadableTemplateName, this.sourceFile, true);
        tp.base_tab = block.tabCount + base_tab;
        block.templateProcessorMap.put(strBlockName, tp);
        return tp;
    }

    /**
     * Add in the array of insert, tabs will not be inserted properly
     * 
     * @param name insert line to get
     * @param text to insert
     */
    public void addToInsert(String name, String text) {
        TemplateBlock block = blockList.get(name);
        if (block.lineType != LineType.INSERT) {
            throw new IncorrectActionException("Insert cannot be done on type " + block.lineType + " for name: " + name
                    + ", fileName: " + block.loadableTemplateName + "[" + block.lineNumber + "]");
        }
        block.simpleInsert.add(text);
    }

    /**
     * If the text string is multiline and should be written with proper tabs, then
     * consider using this function
     * 
     * @param name insert line to get
     * @param text to insert
     */
    public void addToInsertSplitNewLIne(String name, String text) {
        TemplateBlock block = blockList.get(name);
        if (block.lineType != LineType.INSERT) {
            throw new IncorrectActionException("Insert cannot be done on type " + block.lineType + " for name: " + name
                    + ", fileName: " + block.loadableTemplateName + "[" + block.lineNumber + "]");
        }
        for (String s : text.split("\n")) {
            block.simpleInsert.add(s);
        }
    }
}
