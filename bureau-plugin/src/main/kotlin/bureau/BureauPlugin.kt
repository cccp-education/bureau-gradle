package bureau

import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * Bureau Gradle Plugin — learner entry point for the education.ccp ecosystem.
 *
 * Surnom borough: Saint-Denis (93, France).
 *
 * Three roles:
 *  - createOffice   — scaffold the learner office/ structure (AsciiDoc pivot, git repo)
 *  - pullMaterial    — pull versionned training material (MaterialUpdateContract, git pull)
 *  - bureauInfo      — list available OSS tools (capsule, slider, document, plantuml, bakery, ...)
 *
 * Diagnostics (verifyBoroughs, inferMetadata, validateApiSchema, info, BoroughDef)
 * are secondary tasks inherited from the pilot plugin extraction.
 *
 * This is the OSS edition. The private edition (workspace-gradle)
 * implements FormationRuntimePort (LocalAdapter + CloudAdapter) for edster.cloud.
 */
class BureauPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.extensions.create("bureau", BureauExtension::class.java)
    }
}