/*
 * Copyright 2014-2024 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license.
 */

package org.jetbrains.dokka.it.gradle

import org.gradle.testkit.runner.TaskOutcome
import org.jetbrains.dokka.it.TestOutputCopier
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ArgumentsSource
import java.io.File
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class UiShowcaseIntegrationTest : AbstractGradleIntegrationTest(), TestOutputCopier {
    override val projectOutputLocation: File by lazy { File(projectDir, "build/dokka/htmlMultiModule") }

    @ParameterizedTest(name = "{0}")
    @ArgumentsSource(LatestTestedVersionsArgumentsProvider::class)
    fun execute(buildVersions: BuildVersions) {
        val result = createGradleRunner(
            buildVersions,
            "dokkaHtmlMultiModule",
            "-i",
            "-s"
        ).buildRelaxed()

        assertEquals(TaskOutcome.SUCCESS, assertNotNull(result.task(":dokkaHtmlMultiModule")).outcome)
        assertEquals(TaskOutcome.SUCCESS, assertNotNull(result.task(":jvm:dokkaHtmlPartial")).outcome)
        assertEquals(TaskOutcome.SUCCESS, assertNotNull(result.task(":kmp:dokkaHtmlPartial")).outcome)

        assertTrue(projectOutputLocation.isDirectory, "Missing dokka output directory")

        projectOutputLocation.allHtmlFiles().forEach { file ->
            assertContainsNoErrorClass(file)
            assertNoUnresolvedLinks(file)
            assertNoHrefToMissingLocalFileOrDirectory(file)
            assertNoEmptyLinks(file)
            assertNoEmptySpans(file)
        }
    }

    override fun assertNoEmptyLinks(file: File) {
        // the versioning plugin's js script splits by '#', but it's not an empty link.
        // the page itself doesn't have any links, so it's fine to skip this file
        if (file.name.endsWith("not-found-version.html")) {
            return
        } else {
            super.assertNoEmptyLinks(file)
        }
    }
}
