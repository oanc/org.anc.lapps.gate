VERSION=$(shell cat VERSION)
WAR=GateServices\#$(VERSION).war
TGZ=GateServices-$(VERSION).tgz

include ../master.mk

docker:
	if [ ! -f src/main/Docker/$(WAR) ] ; then cp target/$(WAR) src/main/Docker ; fi
	cd src/main/Docker ; make
	
