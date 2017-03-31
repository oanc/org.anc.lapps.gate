VERSION=$(shell cat VERSION)
WAR=GateServices\#$(VERSION).war
TGZ=GateServices-$(VERSION).tgz

include ../master.mk
