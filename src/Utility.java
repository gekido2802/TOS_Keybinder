import java.util.ArrayList;
import java.util.List;

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
}
