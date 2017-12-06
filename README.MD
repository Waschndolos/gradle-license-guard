About
=====

This repository contains source code for a gradle plugin called gradle-license-guard. 
It's able to generate a report of the licenses of your 3rd party dependencies. I developed it to have an automated way to generate such reports and to remove the manual work.

It all started as a tryout for Kotlin language so feel free to contribute if you want :)


Configuration
=============


`apply plugin: 'de.waschndolos.licenseguard'`



    task createThirdPartyLicenseReport(type: de.waschndolos.gradle.licenseguard.LicenseReportTask, dependsOn: build) {
	    projectName = "My Project" // custom project name. Default= project.name
	    licenseMap = ["spring-boot-1.5.9.RELEASE": "Some License", // manually add license information
				                          "slf4j-api-1.7.25": "Some Other License"]          // for unknown dependencies
	    logo = "${project.projectDir.path}/myLogo.png" // custom logo url
	    excludedDependencies = ["custom-jar.*", "postgresql.*"] // Exclude Dependencies from the report
	    outputFormat = "pdf" // Supported: pdf, rtf
	    configuration = "runtime" // For which gradle configuration the report shall be created
    }
