# global service name
SERVICE                 := cihi-claml

#######################################################################
#                 OVERRIDE THIS TO MATCH YOUR PROJECT                 #
#######################################################################
APP_VERSION             := $(shell echo `mvn org.apache.maven.plugins:maven-help-plugin:3.2.0:evaluate -Dexpression=project.version -q -DforceStdout`)

.PHONY: build

#######################################################################
#               OVERRIDE this to match your config                    #
#######################################################################

# Run the docker image local for development and testing. This is may be
# specific to each project, however, it should run with the minimal of inputs.

clean:
	mvn clean

# Build the library without tests
build:
	mvn clean install -DskipTests=true

test:
	mvn clean test

install:
	mvn clean install

# Publish artifacts to Nexus (requires Nexus) 
release:
	mvn package

version:
	@echo $(APP_VERSION)
