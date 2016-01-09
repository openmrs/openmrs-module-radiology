package org.openmrs.module.radiology.gradle
import org.junit.Test
import org.gradle.testfixtures.ProjectBuilder
import org.gradle.api.Project
import static org.junit.Assert.*

class ModuleUpdaterTaskTest {
    @Test
    public void canAddTaskToProject() {
        Project project = ProjectBuilder.builder().build()
        def task = project.task('updateModule', type: ModuleUpdaterTask)
        assertTrue(task instanceof ModuleUpdaterTask)
    }
}
