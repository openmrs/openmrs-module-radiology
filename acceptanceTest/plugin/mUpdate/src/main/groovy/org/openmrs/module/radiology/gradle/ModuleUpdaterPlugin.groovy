package org.openmrs.module.radiology.gradle
import org.gradle.api.Project
import org.gradle.api.Plugin

class ModuleUpdaterPlugin implements Plugin<Project> {
    void apply(Project target) {
        target.task('updateModuleDefault', type: ModuleUpdaterTask)
    }
}
