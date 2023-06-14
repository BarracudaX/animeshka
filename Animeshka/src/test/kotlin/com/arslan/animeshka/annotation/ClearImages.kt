package com.arslan.animeshka.annotation

/**
 * Used by ImageCleanerTestExecutionListener to decide whether to run image clearing code.
 * If test method is annotated with this annotation and ImageCleanerTestExecutionListener is registered,
 * files in the location identified by spring env. key image.path.location will be deleted.
 */
annotation class ClearImages
