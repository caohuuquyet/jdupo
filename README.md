# jDUPO Install
## Checkout Source Code
*  `git clone git@github.com:caohuuquyet/jdupo.git` 
* or download at https://github.com/caohuuquyet/jdupo/archive/master.zip

## Install JDK 32 bit 
* http://download.oracle.com/otn-pub/java/jdk/8u171-b11/512cd62ec5174c3487ac17c61aaa89e8/jdk-8u171-windows-i586.exe

## Install Eclipse
* https://www.eclipse.org/downloads/packages/release/neon/3

## Run jDUPO
* Eclipse -> File -> Open Projects from File System -> Import source -> Finish
* Build Path -> Configure Build Path -> Java Build Path -> Libraries -> Add external JARs
	`<jDUPO source code folder> / lib` and `<jDUPO source code folder> / lib/ jena`
* SRC -> com.jdupo.Editor -> Run

## Test Data
* Data Provider -> Load Usage Policy -> `<jDUPO source code folder> / samples / dupo.dfl` or `<jDUPO source code folder> / samples / dupo2.dfl`
* Data Usage Control Module -> Process Comsumer Request

