# This is a two part Demo:
## Part 1 - Comparison of Spring Boot versus Quarkus in memory consumption and startup times. 
## Part 2 - Implements a popular use case for Quarkus in Native mode - as a serverless workload on Kubernetes, in our case the industry leading Kubernetes distribution, Red Hat OpenShift. 

It takes two very simple and pretty identical RESTful applications and compares memory consumption and startup times in
- Spring Boot
- Quarkus in JVM mode
- Quarkus in Native mode
We also briefly demonstrate Quarkus' live code updating capabilities.

We then push our native application as container to the popular Quay container registry and from there we pull it into a KNative Serverless application running on Red Hat's industry leading Kubernetes distribution, OpenShift.

## Prerequisites
To run this demo, you'll need Java (I use version 8), Maven and GraalVM installed.

## Steps

Clone this repo and change to its directory. Then: 
```
export REPO_HOME=`pwd`
```

## 1 - Spring Boot
```
cd $REPO_HOME/springboot-hello-world
mvn package
java -jar target/springboot-hello-world-1.0.0.jar
```

Take note of the startup time - you can see mine took 2.258 seconds
![](https://raw.githubusercontent.com/tnscorcoran/springboot-quarkus-compare/master/images/01-springboot-startup.png)

Also take note of the memory consumption - under rss (Resident Set Size) in the following command
```
ps -o pid,rss,command -p $(pgrep -f springboot)
```
-  you can see mine consumed 629,024k of memory
![](https://raw.githubusercontent.com/tnscorcoran/springboot-quarkus-compare/master/images/02-springboot-rss.png)

And of course - test your application
```
curl http://localhost:8080/greeting
```

Stop Spring Boot by clicking CTRL + c


## 2 - Quarkus JVM Mode
```
cd $REPO_HOME/quarkus-hello-world
mvn package
```

Now run it
```
java -jar target/quarkus-hello-world-1.0-SNAPSHOT-runner.jar
```

Take note of the startup time -  you can see mine took 0.667 seconds
![](https://raw.githubusercontent.com/tnscorcoran/springboot-quarkus-compare/master/images/03-quarkus-jvm-startup.png)

I created a sheet that shows
- how many times faster Quarkus is to start up than Spring Boot (in both JVM and Native mode).
- what percentage of Spring Boot's memory requirement Quarkus has.

Quarkus' 0.667 seconds startup time is more than 3 times faster than Spring Boot's 2.258 seconds - for an identical application:
![](https://raw.githubusercontent.com/tnscorcoran/springboot-quarkus-compare/master/images/04-quarkus-jvm-versus-spring-boot-startup.png)

Now take note of the memory consumption, rss:
```
ps -o pid,rss,command -p $(pgrep -f runner)
```

You can see mine consumed 158,696k of memory
![](https://raw.githubusercontent.com/tnscorcoran/springboot-quarkus-compare/master/images/05-quarkus-jvm-rss.png)



Quarkus used about a quarter of Spring Boot's memory for effectively the same application:


![](https://raw.githubusercontent.com/tnscorcoran/springboot-quarkus-compare/master/images/06-quarkus-jvm-versus-spring-boot-memory.png)

And again - verify your application
```
curl http://localhost:8080/greeting
```


Stop Quarkus by clicking CTRL + c


















## 3 - Quarkus Native Mode

Stay in directory $REPO_HOME/quarkus-hello-world
Now compile the application down to a native image using GraalVM (for instructions on how to set this up - go to https://quarkus.io/guides/building-native-image)

```
./mvnw package -Pnative
```

If it complains it can't find .mvn/wrapper/maven-wrapper.properties, run this first
```
mvn -N io.takari:maven:wrapper
```

Now after *mvnw package -Pnative* run it:
```
./target/quarkus-hello-world-1.0-SNAPSHOT-runner
```

Again take note of the startup time -  you can see mine took 0.012 seconds
![](https://raw.githubusercontent.com/tnscorcoran/springboot-quarkus-compare/master/images/07-quarkus-native-startup.png)

That's nearly 200 times faster than Spring Boot - for an identical application:
![](https://raw.githubusercontent.com/tnscorcoran/springboot-quarkus-compare/master/images/08-quarkus-native-versus-spring-boot-startup.png)

Now take note of the memory consumption, rss:
```
ps -o pid,rss,command -p $(pgrep -f runner)
```

You can see mine consumed 18,258k of memory
![](https://raw.githubusercontent.com/tnscorcoran/springboot-quarkus-compare/master/images/09-quarkus-native-memory.png)



Quarkus in Native mode uses about 3% of Spring Boot's memory for this hello world application:

![](https://raw.githubusercontent.com/tnscorcoran/springboot-quarkus-compare/master/images/10-quarkus-native-versus-spring-boot-memory.png)

And again - test your application
```
curl http://localhost:8080/greeting
```

Stop Quarkus by clicking CTRL + c

ps -o pid,rss,command -p $(pgrep -f runner)

# Bonus - Live code update
This demo focuses on the startup-time and memory advantages of Quarkus over a traditional cloud native stack like Spring Boot.
Quarkus several another benefits - like the ability to combine imperative and reactive programming in the same application, user friendly error reporting, and a superb extension network. But a massive benefit of Quarkus is *live code updates*. When running in *dev* mode, to see code, package, maven dependency changes, all you need to do is save the file - no need to rebuild.

Let's test this out. Execute the following to startup in *dev* mode
```
cd $REPO_HOME/quarkus-hello-world
 ./mvnw compile quarkus:dev
``` 
Test out the app:
```
curl http://localhost:8080/greeting
```

Now, keep the app running but make a change to a source file - say
this file's default message:
![](https://raw.githubusercontent.com/tnscorcoran/springboot-quarkus-compare/master/images/11-modified-Greeting.png)

Save the file. Test it again:
```
curl http://localhost:8080/greeting
```
Your application now reflects the change you did without a rebuild!


## Summary

Quarkus is a new Open Source Red Hat sponsored Java framework. It's designed with cloud native development and Kubernetes in mind.

It's got radically lower memory and faster startup than traditional cloud native Java (we used Spring Boot to represent that). The advantages are most pronounced in Native mode - making it an ideal candidate for Serverless workloads. However in JVM mode, it's advantages are still significant - making it the best choice for longer lived applications where JVM capabilities, in particular Garbage Collection, are needed.
