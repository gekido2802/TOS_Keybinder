import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class Utility {

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

    public static String format(Converter converter, HotKey hotKey) {
        return (hotKey.useCtrl() ? "Ctrl + " : "") + (hotKey.useAlt() ? "Alt + " : "") + (hotKey.useShift() ? "Shift + " : "") + converter.fromTOSKeyToDisplay(hotKey.getKey());
    }

    public static String format(Converter converter, Set<Integer> set) {
        int key = 0;

        for (Integer i : set) {
            key = i;
        }

        return (set.contains(KeyEvent.VK_CONTROL) ? "Ctrl + " : "") + (set.contains(KeyEvent.VK_ALT) ? "Alt + " : "") + (set.contains(KeyEvent.VK_SHIFT) ? "Shift + " : "") + converter.fromKeyCodeToDisplay(key);
    }

    public static boolean isControlKey(int keyCode) {
        return Arrays.asList(KeyEvent.VK_SHIFT, KeyEvent.VK_CONTROL, KeyEvent.VK_ALT).indexOf(keyCode) != -1;
    }
}
