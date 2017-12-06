# About

This repository contains source code for a gradle plugin called gradle-license-guard. 
It's able to generate a report of the licenses of your 3rd party dependencies. I developed it to have an automated way to generate such reports and to remove the manual work.

It all started as a tryout for Kotlin language so feel free to contribute if you want :)


# Configuration

This section describes how you can use the Plugin. It will not create a default task so you need to
create one by your own. An example can be seen below. 
You don't need to specify parameters but in case you want some minor customizations you can specify the following:

| Parameter | Purpose | Type | Example |
|-----------|---------|------|---------|
|projectName|You can add a custom project name if you want|String|projectName="myProject"|



### import the plugin into your build script (old way)

    buildscript {
      repositories {
        maven {
          url "https://plugins.gradle.org/m2/"
        }
      }
      dependencies {
        classpath "gradle.plugin.de.waschndolos:gradle-license-guard:1.0"
      }
    }
    
    apply plugin: "de.waschndolos.licenseguard"

### use new plugin mechanism

    plugins {
      id "de.waschndolos.licenseguard" version "1.0"
    }


### Apply the plugin in your build.gradle file


    apply plugin: 'de.waschndolos.licenseguard'
        
        
    task createThirdPartyLicenseReport(type: de.waschndolos.gradle.licenseguard.LicenseReportTask, dependsOn: build) {
	    projectName = "My Project" // custom project name. Default= project.name
	    licenseMap = ["spring-boot-1.5.9.RELEASE": "Some License", // manually add license information
				                          "slf4j-api-1.7.25": "Some Other License"]          // for unknown dependencies
	    logo = "${project.projectDir.path}/myLogo.png" // custom logo url
	    excludedDependencies = ["custom-jar.*", "postgresql.*"] // Exclude Dependencies from the report
	    outputFormat = "pdf" // Supported: pdf, rtf
	    configuration = "runtime" // For which gradle configuration the report shall be created
    }


