package simple.mind.template;

import org.junit.jupiter.api.Test;

public class SimpleExampleTest {
    @Test
    public void execute() {
        TemplateProcessor templateProcessor = new TemplateProcessor(this.getClass(), "main");
        templateProcessor.setValue("package_name", "hello.world");
        templateProcessor.setValue("class_name", "HelloWorld");
        templateProcessor.setValue("final_message", "");
        templateProcessor.addImportBlock("imports");
        templateProcessor.addRepeatBlock("value_set", "n1").setValue("string", "Hello");
        templateProcessor.addRepeatBlock("value_set", "n2").setValue("string", " World");
        System.out.println(templateProcessor.toString());
    }
}
