package com.kmorawski.sampleapp.api

import com.google.gson.Gson
import junit.framework.Assert.assertEquals
import org.junit.Test

class ProfileDeserializationTest {
    @Test
    fun `given a single json element based on the API response - once deserialized to a model instance - gets deserialized correctly with no crash`() {
        val singleJsonElement = """
            {
                  "name": "Amy",
                  "surname": "West",
                  "gender": "female",
                  "region": "United States",
                  "age": 29,
                  "title": "mrs",
                  "phone": "(860) 188 4599",
                  "birthday": {
                    "dmy": "09/03/1989",
                    "mdy": "03/09/1989",
                    "raw": 605442538
                  },
                  "email": "amy_west@example.com",
                  "password": "West89_",
                  "credit_card": {
                    "expiration": "11/21",
                    "number": "6622-9932-1616-5631",
                    "pin": 9436,
                    "security": 657
                  },
                  "photo": "https://uinames.com/api/photos/female/21.jpg"
            }
        """

        // proves the Profile class is implemented correctly
        val deserializedModel = Gson().fromJson<Profile>(
                singleJsonElement,
                Profile::class.java)

        with(deserializedModel) {
            assertEquals("Amy", name)
            assertEquals("West", surname)
            assertEquals("https://uinames.com/api/photos/female/21.jpg", photo)
            assertEquals(29, age)
            assertEquals("United States", region)
        }
    }

    @Test
    fun `given a json element of an unknown structure - once deserialized to a model instance - produces an empty entity with no crash`() {
        val elementStructuredDifferentlyThanExpected = """
            {
                  "firstName": "this field isn't part of the expected response ",
                  "otherFields": "are missing"
            }
        """

        val deserializedModel = Gson().fromJson<Profile>(
                elementStructuredDifferentlyThanExpected,
                Profile::class.java)

        fun<T> T?.mustBeNull() {
            assertEquals(null, this)
        }
        with(deserializedModel) {
            name.mustBeNull()
            surname.mustBeNull()
            photo.mustBeNull()
            age.mustBeNull()
            region.mustBeNull()
        }
    }
}