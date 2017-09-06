package de.waschndolos.gradle.licenseguard


import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File

class LicenseReportTaskTest {

    @Rule
    @JvmField
    var testProjectDir: TemporaryFolder = TemporaryFolder()

    var buildFile: File? = null

    @Before
    fun setUp() {
        buildFile = testProjectDir.newFile("build.gradle")
    }

    @Test
    fun testBuildFileCreated() {
        Assert.assertNotNull(buildFile)
    }

    @Test
    fun fail() {
        Assert.fail("TODO: Find out how to do some real tests")
    }
}