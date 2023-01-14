# java-template

A simple and more readable tool generate Java Source code


# Usage

1. <b>#insert</b>. String can be inserted in this 

```sh
#insert imports_line;
```

2. <b>#comment</b>. Comment will be ignroed all together.

```sh
#comment this is just comment. 
#comment this lines will not be available in our resulted source code
```

3. <b>\#\#VARIABLE##</b>. All variables in the current template file will be replaced, except *repeat block*. Every variable name starts with ## and ends with ##

```sh
#comment example
String fileName = "##FILE_NAME##";
```

4. <b>#import</b>. Can be imported multiple time. After improting variables can be updated. 

```sh
#comment example
#import resource_path
```

5. <b>import_once</b>. Can bee imported only once. Variables can be updated after improting.

```sh
#comment example
#import_once resource_path
```

6. <b>Code block</b> that can be injected more than once. Code block start with <b>#start</b> followed by a name. Block will be end with <b>#end </b> followed by the exact same name. Can be loaded only once.

```sh
#comment example
int i;
#start file_process_block
  i = ##VALUE_TO_SET##;
  fileOpen(##FILE_NAME##, ##VALUE_TO_SET##);
#end file_process_block
```
  
# Example
1. template file

```java
// main.template
package ##package_name##;
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

3. Processing the block

```java
import org.junit.jupiter.api.Test;

public class SimpleExampleTest {
    @Test
    public void execute() {
        TemplateProcessor tp = new TemplateProcessor(this.getClass(), "main");
        tp.setValue("package_name", "hello.world");
        tp.setValue("class_name", "HelloWorld");
        tp.setValue("final_message", "");
        tp.addImportBlock("imports");
        tp.addRepeatBlock("value_set", "n1").setValue("string", "Hello");
        tp.addRepeatBlock("value_set", "n2").setValue("string", " World");
        System.out.println(tp.toString());
    }
}
```

4. Generated Code

```java

package hello.world;

// Importing imports
import java.util.List;
import java.util.ArrayList;

public class HelloWorld {
    public static void main(String []args) {
        List<String> strs = new ArrayList<String>();
        // Repeat block: value_set starts
        strs.add("Hello");
        strs.add(" World");
        // Repeat block: value_set ends
        for(String s: strs) {
            System.out.print(s);
        }
        System.out.println("");
    }
}
```

# Including in your source code

1. Clone this repository
2. execute *install.sh*

```sh
bash install.sh
```
3. add the following lines in your pom.xml

```xml
<dependency>
	<groupId>simple.mind</groupId>
	<artifactId>java-template</artifactId>
	<version>0.0.1</version>
</dependency>
```

__note__: Use at your own risk
