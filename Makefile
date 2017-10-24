VERSION=$(shell cat VERSION)
WAR=GateServices\#$(VERSION).war
TGZ=GateServices-$(VERSION).tgz

help:
	@echo
	@echo "GOALS"
	@echo
	@echo "         war : generates the war file."
	@echo "       peace : an alias for the above."
	@echo "       clean : removes build artifacts."
	@echo "      deploy : copies the war to the local tomcat."
	@echo "deploy-remote: uploads the war to the tomcat/webapps directory on the server"
	@echo "      upload : uploads the war to the LAPPS download area on anc.org"
	@echo "        help : prints this help screen."
	@echo
	@echo "WAR file is currently: $(WAR)"
	@echo
	
war:
	mvn package
	
peace:
	mvn package

clean:
	mvn clean

compile:
	mvn compile

all: clean compile war docker

deploy:
	sudo cp target/$(WAR) /Applications/Servers/tomcat/server-1/webapps

upload:
	cd target && tar czf $(TGZ) $(WAR)
	anc-put target/$(TGZ) /home/www/anc/downloads/docker
	scp -i $(HOME)/.ssh/lappsgrid-shared-key.pem target/$(TGZ) root@downloads.lappsgrid.org:/var/lib/downloads
	
#	anc-put target/$(WAR) /home/www/anc/LAPPS/downloads
	
deploy-remote:
	grid-put target/$(WAR) /tmp
	ssh -p 22022 grid.anc.org "cp /tmp/$(WAR) /usr/share/tomcat/server-1/webapps"
#	/usr/share/tomcat/server-1/webapps

docker:
	if [ ! -f src/main/Docker/$(WAR) ] ; then cp target/$(WAR) src/main/Docker ; fi
	cd src/main/Docker ; make

run:
	docker run -d -p 8080:8080 --name gate lappsgrid/gate	

stop:
	docker rm -f gate

