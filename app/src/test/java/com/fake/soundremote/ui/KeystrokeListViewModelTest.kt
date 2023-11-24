package com.fake.soundremote.ui

import com.fake.soundremote.MainDispatcherExtension
import com.fake.soundremote.data.Keystroke
import com.fake.soundremote.data.TestKeystrokeRepository
import com.fake.soundremote.ui.keystrokelist.KeystrokeListViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertIterableEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

@OptIn(ExperimentalCoroutinesApi::class)
@ExtendWith(MainDispatcherExtension::class)
@DisplayName("KeystrokeListViewModel")
class KeystrokeListViewModelTest {
    private var keystrokeRepository = TestKeystrokeRepository()

    private lateinit var viewModel: KeystrokeListViewModel

    @BeforeEach
    fun setup() {
        viewModel = KeystrokeListViewModel(keystrokeRepository)
    }

    @Test
    @DisplayName("deleteKeystroke() deletes")
    fun deleteKeystroke_deletes() = runTest {
        val collectJob = backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.keystrokeListState.collect {}
        }

        val id = 10
        val keystrokes = listOf(
            Keystroke(id, 100, 0, "Test1", false, 0),
            Keystroke(id + 1, 200, 0, "Test2", true, 0),
        )
        keystrokeRepository.setKeystrokes(keystrokes)

        viewModel.deleteKeystroke(id)

        val actual = viewModel.keystrokeListState.value.keystrokes.find { it.id == id }
        assertNull(actual)

        collectJob.cancel()
    }

    @Test
    @DisplayName("changeFavoured() changes favoured status")
    fun changeFavoured_changes() = runTest {
        val collectJob = backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.keystrokeListState.collect {}
        }

        val id = 10
        val keystrokes = listOf(Keystroke(id, 100, 0, "Test", true, 0))
        keystrokeRepository.setKeystrokes(keystrokes)

        viewModel.changeFavoured(id, false)

        val actual = viewModel.keystrokeListState.value.keystrokes.find { it.id == id }!!.favoured
        assertFalse(actual)

        collectJob.cancel()
    }

    @ParameterizedTest
    @MethodSource("com.fake.soundremote.ui.KeystrokeListViewModelTest#moveKeystrokeProvider")
    @DisplayName("moveKeystroke() moves correctly")
    fun moveKeystroke_movesCorrectly(
        keystrokes: List<Keystroke>,
        from: Int,
        to: Int,
        expected: List<Int>
    ) = runTest {
        val collectJob = backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.keystrokeListState.collect {}
        }

        keystrokeRepository.setKeystrokes(keystrokes)

        viewModel.moveKeystroke(from, to)

        val actual = viewModel.keystrokeListState.value.keystrokes.map { it.id }
        assertIterableEquals(expected, actual)

        collectJob.cancel()
    }

    companion object {
        /**
         * List`<Keystroke>`, from: Int, to: Int, expectedOrderedIds: List`<Int>`
         */
        @JvmStatic
        fun moveKeystrokeProvider(): Stream<Arguments> = Stream.of(
            Arguments.arguments(
                generateKeystrokes(10),
                3,
                8,
                listOf(1, 2, 3, 5, 6, 7, 8, 9, 4, 10),
            ),
            Arguments.arguments(
                generateKeystrokes(10),
                9,
                0,
                listOf(10, 1, 2, 3, 4, 5, 6, 7, 8, 9)
            ),
            Arguments.arguments(
                generateKeystrokes(8),
                5,
                5,
                listOf(1, 2, 3, 4, 5, 6, 7, 8)
            ),
        )

        /**
         * Generates keystrokes with order value of 0
         */
        private fun generateKeystrokes(n: Int): List<Keystroke> = buildList {
            repeat(n) {
                val id = it + 1
                add(Keystroke(id, 100, 0, "K_$id", false, 0))
            }
        }
    }
}
