package simple.mind.template;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

public class TestMain {

    @Test
    public void main() throws IOException {
        TemplateProcessor template = new TemplateProcessor("webcontroller");
        template.setValue("PACKAGE_NAME", "my.package");
        
        System.out.println(template.toString());
    }
}
