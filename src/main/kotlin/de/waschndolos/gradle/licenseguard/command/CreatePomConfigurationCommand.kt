package de.waschndolos.gradle.licenseguard.command

import org.gradle.api.Project

class CreatePomConfigurationCommand {

    fun execute(project: Project, configuration: String) {
        project.configurations.create("poms")

        project.configurations.getByName(configuration).resolvedConfiguration.lenientConfiguration.artifacts.forEach { resolvedArtifact ->
            val id = resolvedArtifact.moduleVersion.id

            var name = resolvedArtifact.name
            if (name == null) {
                name = id.name
            }
            project.dependencies.add("poms", id.group + ":" + name + ":" + id.version + "@pom")
        }

    }
}