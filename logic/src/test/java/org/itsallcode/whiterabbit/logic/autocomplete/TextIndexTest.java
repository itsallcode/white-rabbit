package org.itsallcode.whiterabbit.logic.autocomplete;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class TextIndexTest
{

    @ParameterizedTest(name = "[{index}] available values {0}, search text ''{1}''")
    @ArgumentsSource(AutocompleterArgumentsProvider.class)
    void autocompleter(List<String> availableEntries, String searchText, List<String> expectedResult)
    {
        final List<AutocompleteProposal> entries = TextIndex.build(availableEntries).getEntries(searchText);
        assertThat(entries)
                .as("autocomplete for available values " + availableEntries + " and search text '" + searchText + "'")
                .extracting(AutocompleteProposal::getText)
                .containsExactly(expectedResult.toArray(new String[0]));
    }

    private static class AutocompleterArgumentsProvider implements ArgumentsProvider
    {
        @Override
        public Stream<Arguments> provideArguments(ExtensionContext context)
        {
            return Stream.of(
                    Arguments.of(List.of(), "text", List.of()),
                    Arguments.of(List.of("text"), null, List.of("text")),
                    Arguments.of(List.of("text"), "", List.of("text")),
                    Arguments.of(List.of("text"), " ", List.of("text")),
                    Arguments.of(List.of("text", "text", "text"), " ", List.of("text")),
                    Arguments.of(List.of("t0", "t1", "t2", "t3", "t4", "t5", "t6", "t7", "t8", "t9", "ta", "tb"), "",
                            List.of("t0", "t1", "t2", "t3", "t4", "t5", "t6", "t7", "t8", "t9")),
                    Arguments.of(List.of("TEXT"), "text", List.of("TEXT")),
                    Arguments.of(List.of("match", "nomatch"), "ma", List.of("match")),
                    Arguments.of(List.of("match1", "match2"), "ma", List.of("match1", "match2")),
                    Arguments.of(List.of("first second"), "fi", List.of("first second")),
                    Arguments.of(List.of("first second"), "sec", List.of()));
        }
    }
}
