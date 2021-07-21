package org.itsallcode.whiterabbit.jfxui.uistate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.itsallcode.whiterabbit.jfxui.property.DelayedPropertyListener;
import org.itsallcode.whiterabbit.jfxui.uistate.model.StageStateModel;
import org.itsallcode.whiterabbit.logic.Config;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import jakarta.json.bind.JsonbBuilder;

@ExtendWith(MockitoExtension.class)
class UiStateServiceTest
{
    @Mock
    private Config configMock;
    @Mock
    private DelayedPropertyListener propertyListenerMock;

    @TempDir
    Path tempDir;

    private Path uiStateFile;

    @BeforeEach
    void setUp()
    {
        uiStateFile = tempDir.resolve("ui-state.json");
        when(configMock.getUiStatePath()).thenReturn(uiStateFile);
    }

    @Test
    void missingUiStateFileLoadsFreshState()
    {
        final UiStateService service = load();

        assertThat(service.state.splitPanes).isEmpty();
        assertThat(service.state.stages).isEmpty();
        assertThat(service.state.tables).isEmpty();
    }

    @Test
    void invalidUiStateFileLoadsFreshState() throws IOException
    {
        Files.writeString(uiStateFile, "invalidJsonContent");
        final UiStateService service = load();

        assertThat(service.state.splitPanes).isEmpty();
        assertThat(service.state.stages).isEmpty();
        assertThat(service.state.tables).isEmpty();
    }

    @Test
    void validUiStateFileLoaded() throws IOException
    {
        Files.writeString(uiStateFile,
                "{\"splitPanes\":{\"s1\":{\"dividerPositions\":[0.42],\"id\":\"id\"}},\"stages\":{},\"tables\":{}}");
        final UiStateService service = load();

        assertThat(service.state.splitPanes).hasSize(1);
        assertThat(service.state.splitPanes.get("s1").dividerPositions).containsExactly(0.42);
        assertThat(service.state.stages).isEmpty();
        assertThat(service.state.tables).isEmpty();
    }

    @Test
    void persistWritesStateFile()
    {
        final UiStateService service = load();
        service.persistState();

        assertThat(uiStateFile).exists()
                .hasContent("{\"splitPanes\":{},\"stages\":{},\"tables\":{},\"titledPanes\":{}}");
    }

    @Test
    void persistFailsWhenJsonNotSerializable()
    {
        final UiStateService service = load();
        final StageStateModel stage = new StageStateModel();
        stage.height = Double.NaN;
        service.state.stages.put("s1", stage);

        assertThatThrownBy(() -> service.persistState())
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Error serializing ui state");
    }

    private UiStateService load()
    {
        return UiStateService.loadState(configMock, propertyListenerMock, JsonbBuilder.create());
    }

}
