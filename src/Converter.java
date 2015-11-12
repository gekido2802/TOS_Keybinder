import java.util.Set;

public class Converter {

    private Set<String[]> set;

    public Converter(Set<String[]> set) {
        this.set = set;
    }

    // CONVERT A KEYCODE TO ITS TOS KEY VALUE
    public String fromKeyCodeToTOSKey(int keyCode) {
        String key = "" + keyCode;

        for (String[] s : set) {
            if (s[0].equals(key))
                return s[1];
        }

        return null;
    }

    // CONVERT A KEY CODE TO ITS DISPLAY VALUE
    public String fromKeyCodeToDisplay(int keyCode) {
        String key = "" + keyCode;

        for (String[] s : set)
            if (s[0].equals(key))
                return s[2];

        return "Key Not Supported...";
    }

    // CONVERT A TOS KEY TO ITS DISPLAY VALUE
    public String fromTOSKeyToDisplay(String tosKey) {
        for (String[] s : set) {
            if (s[1].equals(tosKey))
                return s[2];
        }

        return null;
    }
}
