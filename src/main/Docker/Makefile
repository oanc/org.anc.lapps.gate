VERSION=$(shell cat ../../../VERSION)
IMAGE=lappsgrid/gate

SERVER=http://downloads.lappsgrid.org
#SERVER=http://www.anc.org/downloads/docker

PLUGINS=gate-plugins-2.1.0.tgz
WAR=GateServices-$(VERSION).war
CONV=GateConverter_2.1.0.tgz

image: download
	docker build --build-arg VERSION=$(VERSION) -t $(IMAGE) .

download:
	if [ ! -e $(PLUGINS) ] ; then wget $(SERVER)/$(PLUGINS) ; fi
	if [ ! -e $(CONV) ] ; then wget $(SERVER)/$(CONV) ; fi

latest: download
	docker build --build-arg VERSION=$(VERSION) -t $(IMAGE):latest .

version: download
	docker build --build-arg VERSION=$(VERSION) -t $(IMAGE):$(VERSION) .

push: download
	docker push $(IMAGE):latest

push-version: download
	docker push $(IMAGE):$(VERSION)
	
tag: download
	docker tag $(IMAGE) $(IMAGE):$(VERSION)
	docker push $(IMAGE):$(VERSION)

help:
	@echo
	@echo "GOALS"
	@echo
	@echo "image        - creates unversioned $(IMAGE)"
	@echo "latest       - create $(IMAGE):latest"
	@echo "version      - creates $(IMAGE):$(VERSION)"
	@echo "push         - pushes 'latest' to the Hub"
	@echo "push-version - pushes version image to Hub"
	@echo "tag          - tags a version and pushed it to Hub"
	@echo
