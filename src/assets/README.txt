Simcir

Copyright (c) 2009 Kazuhiko Arase

URL: http://www.d-project.com/

Licensed under the MIT license:
  http://www.opensource.org/licenses/mit-license.php


-- System Requirements

Simcir requires a browser or Java Runtime Environment that support Java1.1.
Successfully tested on following environment.

Sun JDK/JRE 1.4.2

-- Contents

README.txt  -- this file
index.html  -- some information
simcir.html -- applet example
simcir.jar  -- executable-jar file
sample      -- sample circuit files

-- How to run

*Run as application

simcir.jar is executable-jar file.

$ java -jar simcir.jar

*Run as applet

You can set the file of default circuit as parameter 'file'.

Example

<applet code="simcir.Main"
		width="600" height="400" archive="lib/simcir.jar">
	<param name="file" value="sample/rs-ff.cml">
</applet>
