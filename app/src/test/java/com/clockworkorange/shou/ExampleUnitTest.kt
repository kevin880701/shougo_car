package com.clockworkorange.shou

import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.regex.Pattern

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun testRegex(){
        val youtube = """
            &lt;iframe width=&quot;560&quot; height=&quot;315&quot; src=&quot;https://www.youtube.com/embed/zNHUqWUiyAM&quot; title=&quot;YouTube video player&quot; frameborder=&quot;0&quot; allow=&quot;accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture&quot; allowfullscreen&gt;&lt;/iframe&gt;
        """.trimIndent()
        val p = Pattern.compile("www.youtube.com\\/embed\\/([A-Za-z0-9_\\-]{11})")
        val m = p.matcher(youtube)
        if (m.find()){
            if (m.groupCount() >= 1){
                println(m.group(1))
            }
        }
    }
}