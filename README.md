# anyconnect-vpn-tester
Find the quickest AnyConnect VPN.

I used IntelliJ idea to build the project and wrote it with Kotlin.
Tested on Mac OS and Windows10.

## How to use:
You need to be disconnected from VPN connection before you run the test.

## Compile and run
2 ways:
1. From intelliJ IDE , open the src/tester.kt file and look for the green arrow near the main function.
2. From commandline:  
To compile: `kotlinc src/tester.kt -include-runtime -d tester.jar`
To run: `java -jar tester.jar`

