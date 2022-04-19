FROM amazonlinux:latest

# Install Java8 
RUN yum install -y java-1.8.0-openjdk-devel

# Install Scala and SBT
RUN yum install -y https://downloads.lightbend.com/scala/2.13.8/scala-2.13.8.rpm
RUN rm -f /etc/yum.repos.d/bintray-rpm.repo || true \
    && curl -L https://www.scala-sbt.org/sbt-rpm.repo > sbt-rpm.repo \
    && mv sbt-rpm.repo /etc/yum.repos.d/ \
    && yum install sbt -y

WORKDIR /app

COPY ./ ./

ENTRYPOINT [ "sbt", "run" ]
