package com.karmanov.tools.clipboardcollector.service;

import com.karmanov.tools.clipboardcollector.component.validation.TextValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.awt.datatransfer.*;

import java.awt.datatransfer.Clipboard;
import java.io.IOException;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ClipboardCollectorServiceTest {
    @Mock
    private TextValidator textValidator;

    @Mock
    Clipboard clipboard;

    @InjectMocks
    private ClipboardCollectorService clipboardCollectorService;

    @Test
    void processClipboardOnce_whenClipboardIsNull_shouldDoNothing() {
        clipboardCollectorService.processClipboardOnce(null);

        verify(textValidator, times(0)).validate(any());
    }

    @Test
    void processClipboardOnce_whenClipboardHasNoStringFlavor_shouldDoNothing() {
        when(clipboard.isDataFlavorAvailable(DataFlavor.stringFlavor)).thenReturn(false);

        clipboardCollectorService.processClipboardOnce(clipboard);

        verify(textValidator, times(0)).validate(any());
    }

    @Test
    void processClipboardOnce_whenTextIsSame_shouldNotCallValidate() throws IOException, UnsupportedFlavorException {
        when(clipboard.isDataFlavorAvailable(DataFlavor.stringFlavor)).thenReturn(true);
        when(clipboard.getData(DataFlavor.stringFlavor)).thenReturn("This is a test");

        clipboardCollectorService.processClipboardOnce(clipboard);
        clipboardCollectorService.processClipboardOnce(clipboard);

        verify(textValidator, times(1)).validate("This is a test");
    }

    @Test
    void processClipboardOnce_whenTextChanged_shouldCallValidateAndUpdateLastText() throws IOException, UnsupportedFlavorException {
        when(clipboard.isDataFlavorAvailable(DataFlavor.stringFlavor)).thenReturn(true);
        when(clipboard.getData(DataFlavor.stringFlavor)).thenReturn("Hello");

        clipboardCollectorService.processClipboardOnce(clipboard);

        when(clipboard.getData(DataFlavor.stringFlavor)).thenReturn("World");

        clipboardCollectorService.processClipboardOnce(clipboard);

        verify(textValidator, times(1)).validate("Hello");
        verify(textValidator, times(1)).validate("World");
        verify(textValidator, times(2)).validate(any());
    }

    @Test
    void processClipboardOnce_whenClipboardBusy_shouldLogWarningAndContinue() throws IOException, UnsupportedFlavorException {
        when(clipboard.isDataFlavorAvailable(DataFlavor.stringFlavor)).thenReturn(true);

        when(clipboard.getData(DataFlavor.stringFlavor)).thenThrow(new IllegalStateException("Busy"));

        clipboardCollectorService.processClipboardOnce(clipboard);

        verify(textValidator, times(0)).validate(any());
    }

    @Test
    void processClipboardOnce_whenClipboardThrowsUnsupportedFlavorException_shouldLogErrorAndContinue() throws IOException, UnsupportedFlavorException {
        when(clipboard.isDataFlavorAvailable(DataFlavor.stringFlavor)).thenReturn(true);
        when(clipboard.getData(DataFlavor.stringFlavor)).thenThrow(new UnsupportedFlavorException(DataFlavor.stringFlavor));

        clipboardCollectorService.processClipboardOnce(clipboard);

        verify(textValidator, times(0)).validate(any());
    }

    @Test
    void processClipboardOnce_whenClipboardThrowsIOException_shouldLogErrorAndContinue() throws IOException, UnsupportedFlavorException {
        when(clipboard.isDataFlavorAvailable(DataFlavor.stringFlavor)).thenReturn(true);
        when(clipboard.getData(DataFlavor.stringFlavor)).thenThrow(new IOException());

        clipboardCollectorService.processClipboardOnce(clipboard);

        verify(textValidator, times(0)).validate(any());
    }
}
