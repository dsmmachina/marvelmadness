package com.dsmmachina.marvelmadness

import com.google.gson.JsonObject
import com.dsmmachina.marvelmadness.api.Api
import com.dsmmachina.marvelmadness.deserializers.CharactersDeserializer
import com.dsmmachina.marvelmadness.deserializers.ComicsDeserializer
import com.dsmmachina.marvelmadness.models.ComicModel
import com.dsmmachina.marvelmadness.models.ImageModel
import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class AppUnitTests {


    private val testId = 385899L
    private val testTitle = "APP TEST TITLE"
    private val testDescription = "THIS IS A PRETTY COOL THING"
    private val testPath = "https://sample.path.to.somewhere/image"
    private val testExtension = "jpg"

    @Test
    fun testComicsDeserializerError() {
        val element = JsonObject()
        val deserializer = ComicsDeserializer.deserialize(element)
        assertTrue(deserializer?.isEmpty() == true)
    }

    @Test
    fun testCharacterDeserializerError() {
        val element = JsonObject()
        val deserializer = CharactersDeserializer.deserialize(element)
        assertTrue(deserializer?.isEmpty() == true)
    }


    @Test
    fun testComicModelNullValues() {
        val model = ComicModel()
        assertTrue(model.id == null)
        assertTrue(model.title == null)
        assertTrue(model.thumbnail == null)
        assertTrue(model.description == null)
        assertTrue(model.characters == null)
        assertTrue(model.creators == null)
        assertTrue(model.issueNumber == null)
        assertTrue(model.images == null)
        assertTrue(model.getThumbnailImage().isEmpty())
    }

    @Test
    fun testComicModelSavedValues() {
        val model = ComicModel()
        model.id = testId
        model.title = testTitle
        model.description = testDescription

        val thumbnail = ImageModel()
        thumbnail.path = testPath
        thumbnail.extension = testExtension
        model.thumbnail = thumbnail

        assertTrue(model.id == testId)
        assertTrue(model.description == testDescription)
        assertTrue(model.title == testTitle)
        assertTrue(model.thumbnail?.path == testPath)
        assertTrue(model.thumbnail?.extension == testExtension)
    }



    @Test
    fun testApiIsAuthorized() {
        assertTrue(Api.isAuthorized)
    }

}
