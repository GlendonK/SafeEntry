
### For mac/linux, replace ';' with ':'. Replace '\' with '/'</br>

### cd to /src </br>

- start rmiregistry (windows)
- rmiregistry  & (linux)

### To compile: </br>

- javac -classpath ".;..\lib\opencsv-5.4.jar;..\lib\commons-lang3-3.12.0.jar" *.java </br> 

or just run the compile.sh file for windows. </br>

### To run: </br>

- java -classpath ".;..\lib\opencsv-5.4.jar;..\lib\commons-lang3-3.12.0.jar" .\Server.java
 </br>

- java .\User.java </br>

- java .\Officer.java </br>

- java -classpath ".;..\lib\opencsv-5.4.jar;..\lib\commons-lang3-3.12.0.jar" .\Test.java </br>

### Do make sure the safe_entry_db.csv is empty, remove all entries before running Test.java