# About

This repository contains source code for a gradle plugin called gradle-license-guard. 
It's able to generate a report of the licenses of your 3rd party dependencies. I developed it to have an automated way to generate such reports and to remove the manual work.

It all started as a tryout for Kotlin language so feel free to contribute if you want :)


# Configuration

This section describes how you can use the Plugin. It will not create a default task so you need to
create one by your own. An example can be seen below. 
You don't need to specify parameters but in case you want some minor customizations you can specify the following:

| Parameter | Purpose | Type | Example | Default |
|-----------|---------|------|---------|---------|
|projectName|You can add a custom project name if you want|String|projectName="myProject"|project.name|
|licenseMap|If the plugin can't determine the license you can add it manually here|Map|licenseMap= ["myjar-2.jar":"someLicense"]|""|
|logo|Define a custom logo url which will then be inserted into your report|String|logo="${project.projectDir.path}/mylogo.png"|""|
|excludedDependencies|Define dependencies which shall not appear in the report. You can use simple Regex (i hope^^)|List<String>|excludedDependencies=["custom.*"]|""|
|outputFormat|Define the output format. It can be "pdf" or "rtf"|String|outputFormat=pdf|pdf|
|configuration|Define for which gradle configuration the report is|String|configuration=runtime|runtime|



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
	    projectName = "My Project"
	    licenseMap = ["spring-boot-1.5.9.RELEASE": "Some License", "slf4j-api-1.7.25": "Some Other License"]          
	    logo = "${project.projectDir.path}/myLogo.png"
	    excludedDependencies = ["custom-jar.*", "postgresql.*"]
	    outputFormat = "pdf"
	    configuration = "runtime"
    }


