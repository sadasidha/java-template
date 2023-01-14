package simple.mind.template;

import java.io.IOException;
import java.util.StringJoiner;

import org.junit.jupiter.api.Test;

public class TestMain {
    @Test
    public void main() throws IOException {
        try {
            TemplateProcessor template = new TemplateProcessor(this.getClass(), "webcontroller");

            template.setValue("PACKAGE_NAME", "my.package");
            TemplateProcessor imports = template.addImportBlock("imports", "imports");
            imports.addToInsert("ADDITIONAL_IMPORT", "import java.util.Map;\n");
            imports.addToInsert("ADDITIONAL_IMPORT", "import java.util.HashMap;\nimport java.util.ArrayList;");
            template.setValue("CLASS_NAME", "ExtendedWebController");
            template.setValue("WEB_HANDLE_CLASS", "simple.mind.school.Search");
            TemplateProcessor single_one = template.addImportBlock("singleton", "singleton1");
            single_one.setValue("VARIABLE_TYPE", "String");
            single_one.setValue("VARIABLE_NAME", "theString");
            StringJoiner sj = new StringJoiner(", ");
            sj.add("thString");
            TemplateProcessor single_two = template.addImportBlock("singleton", "singleton2");

            single_two.setValue("VARIABLE_TYPE", "Cobar");
            single_two.setValue("VARIABLE_NAME", "theCobra");
            sj.add("theCobra");
            template.setValue("SINGLETON_LIST", sj.toString());

            template.addImportBlock("get", "get");
            template.addImportBlock("get_exe", "get_exe");
            System.out.println(template.toString());
        } catch (DuplicateNameException | CommonExceptions | BlockMissingException e) {
            e.printStackTrace();
        }
    }

}
