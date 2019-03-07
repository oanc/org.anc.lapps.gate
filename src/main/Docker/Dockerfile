FROM lappsgrid/tomcat7:1.2.1

ARG VERSION
ARG CONVERTER_VERSION

RUN mkdir /usr/local/lapps
ADD gate-plugins-2.1.0.tgz /usr/share
ADD GateConverter_${CONVERTER_VERSION}.tgz /var/lib/tomcat7/webapps
COPY GateServices\#${VERSION}.war /var/lib/tomcat7/webapps
#ADD GateServices-${VERSION}.tgz /var/lib/tomcat7/webapps