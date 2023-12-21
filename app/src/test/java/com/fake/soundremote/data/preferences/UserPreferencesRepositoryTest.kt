package com.fake.soundremote.data.preferences

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import com.fake.soundremote.util.DEFAULT_CLIENT_PORT
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.nio.file.Path

@DisplayName("UserPreferencesRepository")
class UserPreferencesRepositoryTest {

    @OptIn(ExperimentalCoroutinesApi::class)
    private val testScope = TestScope(UnconfinedTestDispatcher())

    @TempDir
    lateinit var tempDir: Path

    private lateinit var subject: UserPreferencesRepository

    @BeforeEach
    fun setup() {
        val dataStore = PreferenceDataStoreFactory.create(
            scope = testScope,
        ) {
            tempDir.resolve("test_user_preferences.preferences_pb").toFile()
        }
        subject = UserPreferencesRepository(dataStore)
    }

    @Test
    @DisplayName("default client port value is correct")
    fun getClientPort_defaultValue() = runTest {
        val actual = subject.getClientPort()

        assertEquals(DEFAULT_CLIENT_PORT, actual)
    }

    @Test
    @DisplayName("setClientPort sets client port correctly")
    fun setClientPort_setsPreferenceCorrectly() = runTest {
        val expected = DEFAULT_CLIENT_PORT + 123

        subject.setClientPort(expected)
        val actual = subject.getClientPort()

        assertEquals(expected, actual)
    }
}