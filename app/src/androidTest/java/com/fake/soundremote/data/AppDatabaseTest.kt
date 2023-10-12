package com.fake.soundremote.data

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.CoreMatchers.nullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.BeforeClass
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test
import org.junit.experimental.runners.Enclosed
import org.junit.runner.RunWith

// https://developer.android.com/kotlin/coroutines/test
@OptIn(ExperimentalCoroutinesApi::class)
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
        fun createKeystroke_CreatesKeystrokeOrder() = runTest(dispatcher) {
            val orderCountBefore = database.keystrokeOrderRepository.count()

            database.keystrokeRepository.insert(Keystroke(1, "Test"))
            val orderCountAfter = database.keystrokeOrderRepository.count()

            assertThat(
                "Creating Keystroke must create KeystrokeOrder",
                orderCountAfter, `is`(orderCountBefore + 1)
            )
        }

        @Test
        fun createKeystroke_CreatesKeystrokeOrder_WithCorrectKeystrokeId() = runTest(dispatcher) {
            val keystrokeId = database.keystrokeRepository
                .insert(Keystroke(1, "Test"))

            val order = database.keystrokeOrderRepository.getByKeystrokeId(keystrokeId.toInt())
            assertThat(
                "Creating Keystroke must create correct KeystrokeOrder",
                order, notNullValue()
            )
        }

        @Test
        fun createKeystroke_CreatesKeystrokeOrder_WithDefaultOrder() = runTest(dispatcher) {
            val keystrokeId = database.keystrokeRepository.insert(Keystroke(1, "Test"))

            val order = database.keystrokeOrderRepository.getByKeystrokeId(keystrokeId.toInt())
            assertThat(
                "Creating Keystroke must create KeystrokeOrder with default value for Order",
                order!!.order, equalTo(KeystrokeOrder.ORDER_DEFAULT_VALUE)
            )
        }

        @Test
        fun deleteKeystroke_DeletesKeystrokeOrder() = runTest(dispatcher) {
            val keystrokeId = database.keystrokeRepository
                .insert(Keystroke(1, "Test")).toInt()

            database.keystrokeRepository.deleteById(keystrokeId)

            val order = database.keystrokeOrderRepository.getByKeystrokeId(keystrokeId)
            assertThat(
                "Deleting Keystroke must delete its KeystrokeOrder",
                order, nullValue()
            )
        }

        @Test
        fun deleteEventBoundKeystroke_UnbindsEvent() = runTest(dispatcher) {
            val eventId = Event.CALL_BEGIN.id
            val keystrokeId = database.keystrokeRepository
                .insert(Keystroke(1, "Test")).toInt()
            database.eventActionRepository.insert(EventAction(eventId, keystrokeId))

            database.keystrokeRepository.deleteById(keystrokeId)

            val actual: EventAction? = database.eventActionRepository.getById(eventId)
            assertThat(
                "Deleting Event bound Keystroke must delete the Event",
                actual, nullValue()
            )
        }
    }
}
