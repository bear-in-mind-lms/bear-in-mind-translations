package com.kwezal.bearinmind.translation.utils;

import static java.util.Objects.isNull;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Collection;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AssertionUtils {

    /**
     * Asserts two collections contain the same elements.
     * The order of the elements is ignored.
     *
     * @param expected expected collection
     * @param actual   actual collection
     */
    public static void assertEqualsIgnoringOrder(final Collection<?> expected, final Collection<?> actual) {
        assertBothNotNullOrBothNull(expected, actual);
        if (isNull(expected)) {
            return;
        }

        assertEquals(expected.size(), actual.size());
        if (expected.isEmpty()) {
            return;
        }

        final var actualCopy = new ArrayList<>(actual);
        for (final var element : expected) {
            // Remove the expected element from the copy of the actual collection
            // to ensure proper handling of duplicates
            if (!actualCopy.remove(element)) {
                // This assertion always fails, but provides a standard error message
                assertEquals(expected, actual);
            }
        }
    }

    private static void assertBothNotNullOrBothNull(final Object expected, final Object actual) {
        // The condition is met when one of the objects is null and the other is not
        if (isNull(expected) ^ isNull(actual)) {
            // These assertions always fail, but provide a standard error message
            if (isNull(expected)) {
                assertNull(actual);
            } else {
                assertNotNull(actual);
            }
        }
    }
}
