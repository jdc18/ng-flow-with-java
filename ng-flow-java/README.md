ng-flow with Java
========

Project based on https://github.com/flowjs/flow.js/tree/master/samples/java, updated everything to work wiht flow and ng-flow, instead of resumable.
It should be able to work with cors.

I am using wildfly, but with some minor modifications on the pom you can probably use it on glassfish or any other server.



Building
--------
You need bower and nodejs to install the libraries, https://github.com/bower/bower

Change directory to  ng-flow-java/src/main/webapp and run bower install
    
    bower install
    
Just run a mvn install 
    
    mvn install


License
-------

* [Apache License, Version 2.0](https://www.apache.org/licenses/LICENSE-2.0)


TODO
-------

Fix the cors part
