# Dormatory Alert Bot
## About
Dormatory Alert Bot is a java console program, which outputs the users data from dormatory listing on www.sdl.si.
## Usage
[Firstly download the latest release of the program](https://github.com/benjaminplayer/JavaDomAlertBot/releases/tag/v.1.0.0), then:
### Compile the program manually
Post the following command into the powershell terminal to compile the program and its dependencies:
```ps1
javac -cp ".;lib\pdfbox-app-2.0.34.jar;lib\poi-bin-5.2.3\*;lib\poi-bin-5.2.3\auxiliary\*;lib\poi-bin-5.2.3\lib\*;lib\poi-bin-5.2.3\ooxml-lib\*" Bot.java
```
Then use this command to run the program:
```ps1
java -cp ".;lib\pdfbox-app-2.0.34.jar;lib\poi-bin-5.2.3\*;lib\poi-bin-5.2.3\auxiliary\*;lib\poi-bin-5.2.3\lib\*;lib\poi-bin-5.2.3\ooxml-lib\*" #enty_number(optional) keep_files[true/false](optional)
```
### Use the powershell script
To run the program with the script just simply double click it or paste the following code in powershell while in the same directory as the program:
```ps1
.\Run.ps1 #entry_number(optional) keep_files[true/false](optional)
```

## Dependencies
Requires at least java 17 to run
