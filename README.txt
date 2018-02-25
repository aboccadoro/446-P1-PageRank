All of the source files contained in this archive are java files. Therefore, an up to date
JRE and JDK are required to compile and run this program. If not importing into an IDE of
choice, .class files are provided for command line execution. To run the program via
command line simply type:

java PageRank

and to compile:

javac PageRank.java MapUtil.java Tuple.java

There are no other outside dependencies and the files are hardcoded in for reading and
writing. The written text files can be found in the root directoy and the links.srt
file is set to open at the path of the root directory as well.

For convenience "Ranking..." is printed to standard out for every iteration of the
algorithm followed by the runtime in nanoseconds.