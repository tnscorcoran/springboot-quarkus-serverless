# This is a two part Demo:
## Part 1 - Comparison of Spring Boot versus Quarkus in memory consumption and startup times. 
## Part 2 - Implements a popular use case for Quarkus in Native mode - as a serverless workload on Kubernetes, in our case the industry leading Kubernetes distribution, Red Hat OpenShift. 

We take two very simple and pretty identical RESTful applications and compares memory consumption and startup times in
- Spring Boot
- Quarkus in JVM mode
- Quarkus in Native mode
We also briefly demonstrate Quarkus' live code updating capabilities.

We then push our native application as container to the popular Quay container registry and from there we pull it into a KNative Serverless application running on Red Hat's industry leading Kubernetes distribution, OpenShift.

## Prerequisites
To run the first part of this demo, you'll need Java (I use version 8), Maven and GraalVM installed.
To run the second part, you'll need an OpenShift 4.4 cluster. I recommend [Codeready Containers](https://developers.redhat.com/products/codeready-containers/overview) for a local cluster or [try.openshift.com](https://www.openshift.com/try) for a full production ready cluster. You'll also need access to a public copntainer registry. I use the excellent free one from [http://quay.io](http://quay.io).


## Part 1 - Spring Boot / Quarkus comparison

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


## Part 2 - Quarkus in Native mode - as a serverless workload on Kubernetes/OpenShift

## Steps
```
cd $REPO_HOME/quarkus-hello-world
```
First we need to package our native application into a container image using the provided Dockerfile (/quarkus-hello-world/Dockerfile.native) and push it to our remote container registry. First login to your remote container registry, using _podman login_ or _docker login_ then execute the following or your equivalent according how you want to tag and name your repo:
```
docker build -f ./Dockerfile.native -t <registry-username>/<repo-name>:latest .
docker tag <registry-username>/<repo-name>:latest <registry>/<registry-username>/<repo-name>:latest
docker push <registry>/<registry-username>/<repo-name>:latest
```
or in my case:
```
docker build -f ./Dockerfile.native -t tnscorcoran/native-quarkus-hello:latest .
docker tag tnscorcoran/native-quarkus-hello:latest quay.io/tnscorcoran/native-quarkus-hello:latest
docker push quay.io/tnscorcoran/native-quarkus-hello:latest
```

*Note I had to run the above _docker build_ commands on a Linux box*

On [http://quay.io](http://quay.io), I label my new repo _quarkus-serverless_ with _latest_ 

![](https://github.com/tnscorcoran/springboot-quarkus-serverless/blob/master/images/12-tag-image-latest.png)


When I then make this new repo public, as shown, it will be available to pull into my cluster.

![](https://github.com/tnscorcoran/springboot-quarkus-serverless/blob/master/images/13-make-repo-public.png)

Next login to your OpenShift cluster as an adminstrator. 

Now we're going to provision our Serverless Operator, which will allow us to create a new _KNative_ serverless runtime for our application. 

Go to Operators -> Operator Hub -> search for _serverless_ and choose the OpenShift Serverless Operator:

![](https://github.com/tnscorcoran/springboot-quarkus-serverless/blob/master/images/15-OpenShift-Serverless-Operator.png)

Click Install

![](https://github.com/tnscorcoran/springboot-quarkus-serverless/blob/master/images/16-install-OpenShift-Serverless-Operator.png)

Click Update Channel 4.4 and Subscribe.

![](https://github.com/tnscorcoran/springboot-quarkus-serverless/blob/master/images/17-subscribe.png)

We need a project / namespace called _knative-serving_ (it needs that name). Create as follows

![](https://github.com/tnscorcoran/springboot-quarkus-serverless/blob/master/images/18-new-project-knative-serving.png)

With your new project _knative-serving_ selected, we will deploy the knative serving _API_. As follows

![](https://github.com/tnscorcoran/springboot-quarkus-serverless/blob/master/images/19-new-knative-serving.png)

Move to Workloads -> Pods and wait until all are ready and running:

![](https://github.com/tnscorcoran/springboot-quarkus-serverless/blob/master/images/20-knative-serving-pods-ready.png)


Next we're going to pull in our Quarkus container image and run it in _Serverlesss_ mode. To house our new Quarkus Serverless application, create a new namespace or project, in my case I call it _quarkus-serverless_:

![](https://github.com/tnscorcoran/springboot-quarkus-serverless/blob/master/images/14-new-project.png)

Now it's time to pull in our Quarkus image in Serverless mode. Change to the Developer perpective, choose Topology and create an application from a container image as shown:

![](https://github.com/tnscorcoran/springboot-quarkus-serverless/blob/master/images/21-developer-perspective-create-from-container-image.png)

Choose the registry/repository you created earlier, in my case _quay.io/tnscorcoran/native-quarkus-hello_. Choose _KNative Service_ and accept the other defaults:

![](https://github.com/tnscorcoran/springboot-quarkus-serverless/blob/master/images/22-knative-app-create.png)

*Note if you want you can modify the scaling defaults using the _Scaling_ link. By default it scakles to zero after some seconds, which is great for saving cloud costs and we'll experience below.*






## Summary

Quarkus is a new Open Source Red Hat sponsored Java framework. It's designed with cloud native development and Kubernetes in mind.

It's got radically lower memory and faster startup than traditional cloud native Java (we used Spring Boot to represent that). The advantages are most pronounced in Native mode - making it an ideal candidate for Serverless workloads. However in JVM mode, it's advantages are still significant - making it the best choice for longer lived applications where JVM capabilities, in particular Garbage Collection, are needed.
