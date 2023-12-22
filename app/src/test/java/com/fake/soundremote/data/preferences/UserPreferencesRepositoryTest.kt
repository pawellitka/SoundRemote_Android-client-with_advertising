package com.fake.soundremote.data.preferences

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import com.fake.soundremote.util.DEFAULT_AUDIO_COMPRESSION
import com.fake.soundremote.util.DEFAULT_CLIENT_PORT
import com.fake.soundremote.util.DEFAULT_SERVER_ADDRESS
import com.fake.soundremote.util.DEFAULT_SERVER_PORT
import com.fake.soundremote.util.Net
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.nio.file.Path

@DisplayName("UserPreferencesRepository")
class UserPreferencesRepositoryTest {

    private val testScope = TestScope()

    @TempDir
    lateinit var tempDir: Path

    private lateinit var testRepo: UserPreferencesRepository

    @BeforeEach
    fun setup() {
        val dataStore = PreferenceDataStoreFactory.create(
            scope = testScope,
        ) {
            tempDir.resolve("test_user_preferences.preferences_pb").toFile()
        }
        testRepo = UserPreferencesRepository(dataStore)
    }

    @Test
    @DisplayName("default client port value is correct")
    fun getClientPort_defaultValue() = testScope.runTest {
        val actual = testRepo.getClientPort()

        assertEquals(DEFAULT_CLIENT_PORT, actual)
    }

    @Test
    @DisplayName("default server port value is correct")
    fun getServerPort_defaultValue() = testScope.runTest {
        val actual = testRepo.getServerPort()

        assertEquals(DEFAULT_SERVER_PORT, actual)
    }

    @Test
    @DisplayName("default server address value is correct")
    fun getServerAddress_defaultValue() = testScope.runTest {
        val actual = testRepo.getServerAddress()

        assertEquals(DEFAULT_SERVER_ADDRESS, actual)
    }

    @Test
    @DisplayName("default audio compression value is correct")
    fun getAudioCompression_defaultValue() = testScope.runTest {
        val actual = testRepo.getAudioCompression()

        assertEquals(DEFAULT_AUDIO_COMPRESSION, actual)
    }

    @Test
    @DisplayName("setClientPort sets client port correctly")
    fun setClientPort_setsCorrectly() = testScope.runTest {
        val expected = DEFAULT_CLIENT_PORT + 123

        testRepo.setClientPort(expected)
        val actual = testRepo.getClientPort()

        assertEquals(expected, actual)
    }

    @Test
    @DisplayName("setServerPort sets server port correctly")
    fun setServerPort_setsCorrectly() = testScope.runTest {
        val expected = DEFAULT_SERVER_PORT + 123

        testRepo.setServerPort(expected)
        val actual = testRepo.getServerPort()

        assertEquals(expected, actual)
    }

    @Test
    @DisplayName("setServerAddress sets server address correctly")
    fun setServerAddress_setsCorrectly() = testScope.runTest {
        val expected = "123.45.67.89"

        testRepo.setServerAddress(expected)
        val actual = testRepo.getServerAddress()

        assertEquals(expected, actual)
    }

    @Test
    @DisplayName("setAudioCompression sets audio compression correctly")
    fun setAudioCompression_setsCorrectly() = testScope.runTest {
        val expected = Net.COMPRESSION_320
        assertNotEquals(DEFAULT_AUDIO_COMPRESSION, expected)

        testRepo.setAudioCompression(expected)
        val actual = testRepo.getAudioCompression()

        assertEquals(expected, actual)
    }

    @Test
    @DisplayName("settingsScreenPreferencesFlow updates client port")
    fun settingsScreenPreferencesFlow_updatesClientPort() = testScope.runTest {
        val expected = DEFAULT_CLIENT_PORT + 100

        testRepo.setClientPort(expected)
        val actual = testRepo.settingsScreenPreferencesFlow.first().clientPort

        assertEquals(expected, actual)
    }

    @Test
    @DisplayName("settingsScreenPreferencesFlow updates server port")
    fun settingsScreenPreferencesFlow_updatesServerPort() = testScope.runTest {
        val expected = DEFAULT_SERVER_PORT + 100

        testRepo.setServerPort(expected)
        val actual = testRepo.settingsScreenPreferencesFlow.first().serverPort

        assertEquals(expected, actual)
    }

    @Test
    @DisplayName("settingsScreenPreferencesFlow updates audio compression")
    fun settingsScreenPreferencesFlow_updatesAudioCompression() = testScope.runTest {
        val expected = Net.COMPRESSION_320
        assertNotEquals(DEFAULT_AUDIO_COMPRESSION, expected)

        testRepo.setAudioCompression(expected)
        val actual = testRepo.settingsScreenPreferencesFlow.first().audioCompression

        assertEquals(expected, actual)
    }

    @Test
    @DisplayName("serverAddressFlow updates")
    fun serverAddressFlow_updates() = testScope.runTest {
        val expected = "123.45.67.89"

        testRepo.setServerAddress(expected)
        val actual = testRepo.serverAddressFlow.first()

        assertEquals(expected, actual)
    }

    @Test
    @DisplayName("audioCompressionFlow updates")
    fun audioCompressionFlow_updates() = testScope.runTest {
        val expected = Net.COMPRESSION_320
        assertNotEquals(DEFAULT_AUDIO_COMPRESSION, expected)

        testRepo.setAudioCompression(expected)
        val actual = testRepo.audioCompressionFlow.first()

        assertEquals(expected, actual)
    }
}
