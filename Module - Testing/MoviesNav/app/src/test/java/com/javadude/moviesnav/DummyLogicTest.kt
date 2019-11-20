package com.javadude.moviesnav

import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 * We can use function names with embedded spaces if we want... Funky...
 *
 * Note that this test does not run on an emulator, and does not use Android classes.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class DummyLogicTest {
    lateinit var logic : DummyLogic
    @Before
    fun setup() {
        logic = DummyLogic()
    }

    @Test
    fun `test set name`() {
        assertEquals("", logic.name)
        logic.name = "Scott"
        assertEquals("Scott", logic.name)
    }

    @Test
    fun `test double name`() {
        assertEquals("", logic.name)
        logic.name = "Pete"
        assertEquals("Pete", logic.name)
        logic.doubleName()
        assertEquals("PetePete", logic.name)
    }
}
