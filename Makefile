VERSION=$(shell cat VERSION)
#TOMCAT_HOME=/usr/share/tomcat/service-manager
TOMCAT=/Applications/Servers/tomcat/service-manager
WAR=GateServices\#$(VERSION).war

help:
	@echo
	@echo "GOALS"
	@echo
	@echo "     war : generates the war file."
	@echo "   peace : an alias for the above."
	@echo "   clean : removes build artifacts."
	@echo "  deploy : copies the war to the local tomcat."
	@echo "  upload : uploads the war to grid.anc.org"
	@echo "    help : prints this help screen."
	@echo

war:
	mvn package
	
peace:
	mvn package

clean:
	mvn clean
	
deploy:
	sudo cp target/$(WAR) $(TOMCAT)/webapps
	
upload:
	grid-put target/$(WAR) /tmp
	

