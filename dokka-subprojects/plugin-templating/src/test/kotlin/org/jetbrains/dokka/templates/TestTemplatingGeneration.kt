/*
 * Copyright 2014-2024 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license.
 */

package org.jetbrains.dokka.templates

import org.jetbrains.dokka.Timer
import org.jetbrains.dokka.generation.Generation
import org.jetbrains.dokka.plugability.DokkaContext
import org.jetbrains.dokka.plugability.plugin
import org.jetbrains.dokka.plugability.query
import org.jetbrains.dokka.plugability.querySingle
import org.jetbrains.dokka.utilities.LoggingLevel

class TestTemplatingGeneration(private val context: DokkaContext) : Generation {

    val templatingPlugin by lazy { context.plugin<TemplatingPlugin>() }

    override fun Timer.generate() {
        report("Processing submodules", LoggingLevel.DEBUG)
        processSubmodules()

        report("Finishing processing", LoggingLevel.PROGRESS)
        finishProcessing()
    }

    fun processSubmodules() =
        templatingPlugin.querySingle { submoduleTemplateProcessor }.process(context.configuration.modules)

    fun finishProcessing() =
        templatingPlugin.query { templateProcessingStrategy }.forEach { it.finish(context.configuration.outputDir) }


    override val generationName = "test template generation"
}
