package com.fxyandtjh.voiceaccounting

import org.junit.Test

import org.junit.Assert.*

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
    fun testSub() {
        val str = "http://123.23.23:5080/image/abcd.jpg"
        val targetStr = str.substring(str.lastIndexOf("/"))
        println(targetStr)
    }
}