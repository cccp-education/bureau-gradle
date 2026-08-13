package bureau

import kotlin.test.Test
import kotlin.test.assertNotNull

class BureauPluginTest {

    @Test
    fun `BureauPlugin class exists and is instantiable`() {
        val plugin = BureauPlugin()
        assertNotNull(plugin)
    }
}