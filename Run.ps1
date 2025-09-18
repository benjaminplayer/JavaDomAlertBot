$classes = Get-ChildItem -Path . -Filter "*.class" -ErrorAction SilentlyContinue
if($classes -eq 0)
{
    javac -cp ".;lib\pdfbox-app-2.0.34.jar;lib\poi-bin-5.2.3\*;lib\poi-bin-5.2.3\auxiliary\*;lib\poi-bin-5.2.3\lib\*;lib\poi-bin-5.2.3\ooxml-lib\*" Bot.java
}
    java -cp ".;lib\pdfbox-app-2.0.34.jar;lib\poi-bin-5.2.3\*;lib\poi-bin-5.2.3\auxiliary\*;lib\poi-bin-5.2.3\lib\*;lib\poi-bin-5.2.3\ooxml-lib\*" Bot $args[0] $args[1]