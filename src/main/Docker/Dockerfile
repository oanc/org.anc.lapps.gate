FROM lappsgrid/tomcat7:1.2.0

ARG VERSION

RUN mkdir /usr/local/lapps
ADD gate-plugins-2.1.0.tgz /usr/share
ADD GateConverter_2.0.0.tgz /var/lib/tomcat7/webapps
COPY GateServices#$VERSION.war /var/lib/tomcat7/webapps
