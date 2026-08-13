package bureau

import org.gradle.api.provider.Property

/**
 * Configuration for the Bureau plugin.
 *
 * Currently minimal — will grow as US BUREAU-2/3/4 are implemented.
 */
interface BureauExtension {
    val officeDir: Property<String>
}