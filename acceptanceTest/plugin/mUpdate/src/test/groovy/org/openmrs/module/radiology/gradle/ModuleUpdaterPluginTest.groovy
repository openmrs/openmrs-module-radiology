package org.openmrs.module.radiology.gradle
import org.junit.Test
import org.gradle.testfixtures.ProjectBuilder
import org.gradle.api.Project
import static org.junit.Assert.*

class ModuleUpdaterPluginTest {
    @Test
    public void greeterPluginAddsGreetingTaskToProject() {
        Project project = ProjectBuilder.builder().build()
        project.pluginManager.apply 'org.openmrs.module.radiology.gradle.moduleUpdater'

        assertTrue(project.tasks.updateModuleDefault instanceof ModuleUpdaterTask)
    }
}
