
For mac/linux, replace ';' with ':'. </br>

cd to /src </br>

To compile: </br>

javac -classpath ".;<directory to file>\safe\safe_entry\lib\opencsv-5.4.jar;C:\Users\glend\Desktop\safe\safe_entry\lib\commons-lang3-3.12.0.jar" *.java *.java </br>

To run: </br>

java -classpath ".;<directory to file>\safe\safe_entry\lib\opencsv-5.4.jar;C:\Users\glend\Desktop\safe\safe_entry\lib\commons-lang3-3.12.0.jar" *.java Server.java </br>
java ./User.java </br>
java ./Officer.java </br>

Example compile/run terminal commands: </br>

javac -classpath ".;C:\Users\glend\Desktop\safe\safe_entry\lib\opencsv-5.4.jar;C:\Users\glend\Desktop\safe\safe_entry\lib\commons-lang3-3.12.0.jar" *.java </br>

java -classpath ".;C:\Users\glend\Desktop\safe\safe_entry\lib\opencsv-5.4.jar;C:\Users\glend\Desktop\safe\safe_entry\lib\commons-lang3-3.12.0.jar" Server.java </br>

java -classpath ".;C:\Users\glend\Desktop\safe\safe_entry\lib\opencsv-5.4.jar;C:\Users\glend\Desktop\safe\safe_entry\lib\commons-lang3-3.12.0.jar" .\Test.java </br>