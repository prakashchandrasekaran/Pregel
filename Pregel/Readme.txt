Readme

* Download Pregel.jar 
* Extract jar
	jar xvf Pregel.jar

 (or)

* clone source from git repository


01. set java home
	use java 1.7 or latest
	export JAVA_HOME=<PATH_TO_JAVAHOME>
	export PATH=$JAVA_HOME/bin:$PATH
02. move to Project folder
	cd Pregel
03. Edit build.xml
	change username and password
	change project working directory
	change server name
04. Add host names
	in build.xml add host names
05. Edit system.properties file in config directory
	Change number of vertices
	Change checkpoint frequency
06. Compiling the source
	ant dist (or just ant)
07. Start master
	ant runMaster
08. Start worker(s)
	* Single Worker
		ant runWorker
	* Multiple Workers
		ant x2Workers
		ant x4Workers
		ant x8Workers
		ant x16Workers
09. Start task
	* Shortest Path
		ant runShortestPathClient
	* PageRank
		ant runPageRankClient
10. Shutdown machines
	* ant Shutdown

11. The output file is created in the name of the current timestamp in the output directory.

