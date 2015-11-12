import java.awt.event.KeyEvent;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Utility {

    // CREATE A DEEP COPY OF A LIST OF HOTKEYS
    public static List<HotKey> copy(List<HotKey> hotKeys) {
        List<HotKey> copiedList = new ArrayList<>();

        for (HotKey h : hotKeys) {
            HotKey hotKey = new HotKey();

            hotKey.setId(h.getId());
            hotKey.setName(h.getName());
            hotKey.setDownScp(h.getDownScp());
            hotKey.setUpScp(h.getUpScp());
            hotKey.setKey(h.getKey());
            hotKey.setUseShift(h.useShift());
            hotKey.setUseAlt(h.useAlt());
            hotKey.setUseCtrl(h.useCtrl());
            hotKey.setOnEdit(h.isOnEdit());


            copiedList.add(hotKey);
        }

        return copiedList;
    }

    // FORMAT A HOTKEY PROPERLY
    public static String format(Converter converter, HotKey hotKey) {
        return (hotKey.useCtrl() ? "Ctrl + " : "") + (hotKey.useAlt() ? "Alt + " : "") + (hotKey.useShift() ? "Shift + " : "") + converter.fromTOSKeyToDisplay(hotKey.getKey());
    }

    // VALIDATE IF THE KEYCODE IS A CONTROL KEY
    public static boolean isControlKey(int keyCode) {
        return Arrays.asList(KeyEvent.VK_SHIFT, KeyEvent.VK_CONTROL, KeyEvent.VK_ALT).indexOf(keyCode) != -1;
    }

    // COPY A SOURCE FILE TO ITS DESTINATION
    public static void copy(InputStream inputStream, OutputStream outputStream) throws IOException {
        try {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, bytesRead);
            }
        } finally {
            inputStream.close();
            outputStream.close();
        }
    }
}
