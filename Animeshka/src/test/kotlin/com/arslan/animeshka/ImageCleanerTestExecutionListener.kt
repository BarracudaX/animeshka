package com.arslan.animeshka

import com.arslan.animeshka.annotation.ClearImages
import org.springframework.core.env.get
import org.springframework.test.context.TestContext
import org.springframework.test.context.TestExecutionListener
import java.nio.file.Files
import kotlin.io.path.Path
import kotlin.io.path.deleteExisting
import kotlin.io.path.isRegularFile

class ImageCleanerTestExecutionListener : TestExecutionListener {

    override fun afterTestMethod(testContext: TestContext) {

        if(!testContext.testMethod.isAnnotationPresent(ClearImages::class.java)) return

        val imageLocation = Path(testContext.applicationContext.environment["image.path.location"]!!)
        Files.newDirectoryStream(imageLocation){ path -> path.isRegularFile() }.use { stream ->
            for(path in stream){
                path.deleteExisting()
            }
        }
    }

}