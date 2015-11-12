public class HotKey implements Comparable<HotKey> {

    // FIELDS
    private String id;
    private String name;
    private String key;
    private String downScp;
    private String upScp;
    private boolean useShift;
    private boolean useAlt;
    private boolean useCtrl;
    private boolean onEdit;


    // CONSTRUCTORS
    public HotKey() {
    }


    // GETTERS
    public String getId() {
        return id;
    }

    // SETTERS
    public HotKey setId(String id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public HotKey setName(String name) {
        this.name = name;
        return this;
    }

    public String getDownScp() {
        return downScp;
    }

    public HotKey setDownScp(String downScp) {
        this.downScp = downScp;
        return this;
    }

    public String getUpScp() {
        return upScp;
    }

    public HotKey setUpScp(String upScp) {
        this.upScp = upScp;
        return this;
    }

    public String getKey() {
        return key;
    }

    public HotKey setKey(String key) {
        this.key = key;
        return this;
    }

    public boolean useShift() {
        return useShift;
    }

    public boolean useAlt() {
        return useAlt;
    }

    public boolean useCtrl() {
        return useCtrl;
    }

    public boolean isOnEdit() {
        return onEdit;
    }

    public HotKey setOnEdit(boolean onEdit) {
        this.onEdit = onEdit;
        return this;
    }

    public HotKey setUseShift(boolean useShift) {
        this.useShift = useShift;
        return this;
    }

    public HotKey setUseAlt(boolean useAlt) {
        this.useAlt = useAlt;
        return this;
    }

    public HotKey setUseCtrl(boolean useCtrl) {
        this.useCtrl = useCtrl;
        return this;
    }

    // OVERRIDES
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        HotKey hotKey = (HotKey) o;

        return useShift == hotKey.useShift && useAlt == hotKey.useAlt && useCtrl == hotKey.useCtrl && onEdit == hotKey.onEdit && key.equals(hotKey.key);
    }

    @Override
    public int hashCode() {
        int result = key.hashCode();
        result = 31 * result + (useShift ? 1 : 0);
        result = 31 * result + (useAlt ? 1 : 0);
        result = 31 * result + (useCtrl ? 1 : 0);
        result = 31 * result + (onEdit ? 1 : 0);
        return result;
    }

    @Override
    public int compareTo(HotKey o) {

        String a = JSONParser.getKeys().get(this.id), b = JSONParser.getKeys().get(o.id);

        if (a == null || b == null)
            return this.id.compareTo(o.id);
        else
            return JSONParser.getKeys().get(this.id).compareTo(JSONParser.getKeys().get(o.id));
    }

    @Override
    public String toString() {
        return "{ID : " + id + "} {Name : " + name + "} {DownScp : " + downScp + "} " +
                "{UpScp : " + upScp + "} {Key : " + key + "} {UseShift : " + useShift + "} " +
                "{UseAlt : " + useAlt + "} {UseCtrl : " + useCtrl + "} {OnEdit : " + onEdit + "}";
    }
}
