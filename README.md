# java-template

A simple and more readable tool generate Java Source code


### basic characteristics

1. #import resource/path name<br>
  Can be imported as many time it is necessary
1. #import_once resource/path name<br>
  Can be imported at most one time
1. #start name1¥n...¥n#end name1<br>
  Code block that can be injected more than once
1. #comment literal string<br>
  Will be ignored
1. #insert name<br>
  Can be insert string more than once
1. ##NAME##<br>
  Variable. All variables in the current template file will be replaced, except repeat block #start name1¥n...¥nend name1¥n
  
  
### Example
1. template file
```java
// main.template
#package ##package_name##;
#import_once imports

public class ##class_name## {
    public static void main(String []args) {
        List<String> strs = new ArrayList<String>();
        #start value_set
        strs.add("##string##");
        #end value_set
        for(String s: strs) {
            System.out.print(s);
        }
        System.out.println("##final_message##");
    }
}

```
2. imports.template
```java
import java.util.List;
import java.util.ArrayList;
```
2. Some.java
```java
    public static void main(String []args) {
        TemplateProcessor templateProcessor = new TemplateProcessor(Some.class, "main");
        templateProcessor.setValue("package_name", "my.world");
        templateProcessor.setValue("class_name", "HelloWorld");        
        templateProcessor.setValue("final_message", "");
        templateProcessor.addImportBlock("imports");
        templateProcessor.addRepeatBlock("string", "n1").setValue("string", "Hello");
        templateProcessor.addRepeatBlock("string", "n2").setValue("string", " World");
        
        System.out.println(templateProcessor.toString());
    }
```
