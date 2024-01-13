package com.fake.soundremote.data

import com.fake.soundremote.util.KeyCode
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.CoreMatchers.nullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.BeforeClass
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test
import org.junit.experimental.runners.Enclosed
import org.junit.runner.RunWith

@RunWith(Enclosed::class)
internal class AppDatabaseTest {
    @Ignore
    companion object {

        private val dispatcher = StandardTestDispatcher()

        @JvmStatic
        @BeforeClass
        fun setUpClass() {
        }
    }

    internal class TriggersAndForeignKeysTests {
        @JvmField
        @Rule
        val database = DatabaseResource(dispatcher)

        @Test
        fun createKeystroke_setsOrderToDefaultValue() = runTest(dispatcher) {
            val expected = Keystroke.ORDER_DEFAULT_VALUE
            val keystrokeId = database.keystrokeRepository.insert(Keystroke(KeyCode(1), "Test"))

            val actual = database.keystrokeRepository.getById(keystrokeId.toInt())?.order

            assertThat(
                "Creating a Keystroke must init the order field with the default value",
                actual, equalTo(expected)
            )
        }

        @Test
        fun deleteEventBoundKeystroke_deletesEventAction() = runTest(dispatcher) {
            val keystroke = Keystroke(KeyCode(123), "Test")
            val keystrokeId = database.keystrokeRepository.insert(keystroke).toInt()
            val eventId = Event.CALL_END.id
            val eventAction = EventAction(eventId, ActionData(ActionType.KEYSTROKE, keystrokeId))
            database.eventActionRepository.insert(eventAction)

            database.keystrokeRepository.deleteById(keystrokeId)

            val actual: EventAction? = database.eventActionRepository.getById(eventId)
            assertThat(
                "Deleting Event bound Keystroke must delete the EventAction",
                actual, nullValue()
            )
        }
    }
}
