FROM java:openjdk-7-jdk


RUN \
    apt-get update && \
    apt-get install -y build-essential \
	python2.7 \
        python2.7-dev \
        python-pip \
        vim wget \
	openssl \
	openssh-client \
	openssh-server \
	supervisor \
        openjdk-7-jdk \
        ant ant-optional unzip ntp \
        postgresql

RUN pip install supervisor==3.2

# wget http://download.jboss.org/jbossas/7.1/jboss-as-7.1.1.Final/jboss-as-7.1.1.Final.zip
# wget http://downloads.sourceforge.net/project/ejbca/ejbca6/ejbca_6_3_1_1/ejbca_ce_6_3_1_1.zip

ADD . /ejbca/src
WORKDIR /ejbca/src


ENV JAVA_HOME=/usr/lib/jvm/java-7-openjdk-amd64
ENV EJBCA=/ejbca/src/ejbca_ce_6_3_1_1/
ENV JBOSS=/ejbca/src/jboss-as-7.1.1.Final/
ENV PGPASSWORD=postgres


RUN /ejbca/src/setup.sh

EXPOSE 8080
EXPOSE 8442
EXPOSE 8443
EXPOSE 9990

CMD ["/ejbca/src/deploy.sh"]
ENTRYPOINT []
