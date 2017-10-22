#!/usr/bin/env bash

# Use the Makefile instead.

VERSION=`cat ../../../VERSION`

dload_url=http://www.anc.org/downloads/docker

warfile=GateServices\#$VERSION.war
plugins=gate-plugins-2.1.0.tgz
converter=GateConverter_2.1.0.tgz

if [ ! -e $warfile ] ; then
    cp ../../../target/$warfile ./
fi

if [ ! -e $plugins ] ; then
    wget $dload_url/$plugins
fi
if [ ! -e $converter ] ; then
    wget $dload_url/$converter
fi
docker build --build-arg VERSION=$VERSION -t lappsgrid/gate .
