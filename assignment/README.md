# COM3529 Software Testing & Analysis Assignment - Spring 2021

## Project Contributors: 
&nbsp;&nbsp; Andrew Payne - apayne5@sheffield.ac.uk \
&nbsp;&nbsp; Ambrose Robinson - arobinson10@sheffield.ac.uk

## Overview

As per the assignment brief set out by the lecturer, this program aims to provide an automated testing 
framework by parsing Java files and automatically creating relevant tests.

Because this is a developmental assignment with no set *gold standard* to measure against, the 
following features have been implemented in this submission: 

* Parse Java file  automatically to obtain a list of methods and their parameters
* Parse Java file automatically to obtain a list of if branches and their conditions
* Generate test case suites in terms of meeting Restricted MCDC Coverage Criterion
* Generate test case suites in terms of meeting Branch Coverage Criterion
* Generate test case suites in terms of meeting Condition Coverage Criterion
* Generate J-Unit test cases automatically and parse to a java test file
* Generate configurably random integer, double, boolean, string and miscellaneous object inputs (see additional features)


## Technologies
* Java - JDK Version 11 
* JavaParser - Version 3.20.2 


## Launch/Setup

1. Place files you want to test inside the src directory somewhere, we recommend src/main/java/assignmentFiles/subjectFiles/ but anywhere will do
2. Run Main.java (it must be ran once to create the instrumented class and then a second time to create the test cases) with the following command line arguments:
	- argument 1 - coverage criteria out of branch, condition and MCDC
	- argument 2 - will be the path to the java file the user wants to instrument from src onwards e.g. src/main/java/assignmentFiles/subjectFiles/Triangle.java
	
	- **IF THE USER WANTS TO CONFIGURE INPUT PARAMETERS THEN ADD THE FOLLOWING ADDITIONAL ARGUMENTS:**
        - argument 3 - min_doub = minimum value for double generation
        - argument 4 - max_doub = maximum value for double generation
        - argument 5 - min_int = minimum value for integer generation
        - argument 6 - max_int = maximum value for integer generation
        - argument 7 - min_str_len = minimum length of strings generated in string generation
        - argument 8 - max_str_len = maximum length of strings generated in string generation
        - argument 9 - alphanumeric = boolean true if strings generated should be alphanumeric else alphabetic only
3. The J-Unit test cases will then be put inside of BLANK with their respective outputs. It is up to you, the user, to decide whether the outputs are correct and change them accordingly.

* Note that all classes used by the class to be instrumented must have an import statement for all classes that it references/uses even if said classes are in the same directory/package usually as the file gets taken out and put as Instrumented.java in the instrumentedFiles package

## Additional Features

* Although the test data generation is random only, the user can configure the random generation with the options shown above. There is also random generation for ints, doubles, strings and booleans. Strings can be alphanumeric (default) or alphabetic.
* Any input can be generated automatically including objects and objects that take in objects as parameters to their constructors, as long as their basic foundational values are made up of ints, doubles, strings, booleans or objects with default constructors (no parameters). The CompareStrings.java example class shows this off by recursively creating Cuboid objects that take in Rectangle objects as parameters.

## Limitations

* The names of the methods are used as keys in most of the hashmaps that hold the information about used for all the different types of coverage tracking and method input information for input generation. However, a huge oversight was overloading of methods, as its only the simple names (no package or full signature) of the methods used in this respect here. Therefore the program only works when passed classes without overloaded classes.
* Switch cases and ternary if statements are not supported within the instrumentation implementation.