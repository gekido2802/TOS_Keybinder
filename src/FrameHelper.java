import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.jar.JarFile;

public abstract class FrameHelper {

    private static JTextField edit;
    private static Converter converter;
    private static List<HotKey> saveHotKeys, modifiedHotKeys;
    private static JTextField[] inputs;
    private static Set<Integer> keyPressed;
    private static String fileName;

    public static void preBuild() {

        fileName = "C:\\Program Files (x86)\\Steam\\steamapps\\common\\TreeOfSavior\\release\\hotkey.xml";

        // TRY TO OPEN DEFAULT LOCATION OTHERWISE IT WILL ASK FOR FILE'S LOCATION
        if (!new File(fileName).exists()) {
            JOptionPane.showMessageDialog(null, fileName + " couldn't be found.\nPlease indicate the path to the hotkey.xml file.");

            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            fileChooser.setAcceptAllFileFilterUsed(false);
            fileChooser.setFileFilter(new FileNameExtensionFilter("XML files", "xml"));

            if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION)
                fileName = fileChooser.getSelectedFile().getAbsolutePath();
            else
                System.exit(0);
        }

        // INITIALIZE KEYPRESSED STACK
        keyPressed = new HashSet<>();

        // PARSING FILES
        try {
            JarFile jarFile = new JarFile("TOS_Keybinder.jar");
            converter = new Converter(JSONParser.parse(jarFile.getInputStream(jarFile.getEntry("resources/keycode.json"))));
            saveHotKeys = XMLParser.parse(Files.newInputStream(Paths.get(fileName)));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
            System.exit(0);
        }

        modifiedHotKeys = Utility.copy(saveHotKeys);

        // CREATE ARRAY OF JTEXTFIELD TO KEEP TRACK
        inputs = new JTextField[saveHotKeys.size()];
    }

    public static void build(JFrame frame) {

        preBuild();

        // SET FRAME CONFIGURATION
        frame.setTitle("TOS Keybinder");
        frame.setResizable(false);
        frame.setLayout(new BorderLayout(0, 0));
        frame.setPreferredSize(new Dimension(350, 600));
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        // CREATE MENU BAR
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBorder(BorderFactory.createEmptyBorder());
        frame.getContentPane().add(menuBar, BorderLayout.NORTH);
        JMenu menu = new JMenu("File");
        menuBar.add(menu);
        JMenuItem menuItem = new JMenuItem("Quit");
        menuItem.addActionListener(e -> System.exit(0));
        menu.add(menuItem);


        // CREATE MAIN PANEL WITH A SCROLLBAR
        JPanel panel = new JPanel(new GridLayout(saveHotKeys.size(), 2, 50, 50));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JScrollPane scrollPane = new JScrollPane(panel);
        scrollPane.getVerticalScrollBar().setUnitIncrement(20);

        frame.getContentPane().add(scrollPane, BorderLayout.CENTER);

        // FILL MAIN PANEL
        for (int i = 0; i < saveHotKeys.size(); i++) {
            // CREATE ID'S TEXT BOX
            JTextField textField = new JTextField(saveHotKeys.get(i).getId());
            textField.setPreferredSize(new Dimension(75, 25));
            textField.setEditable(false);
            textField.setHorizontalAlignment(JLabel.CENTER);
            panel.add(textField);

            // CREATE KEY'S TEXT BOX
            textField = new JTextField(format(saveHotKeys.get(i)));
            textField.setEditable(false);
            textField.setFocusTraversalKeysEnabled(false);
            textField.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    edit = ((JTextField) e.getSource());
                    edit.setText("Press Key...");
                }
            });
            inputs[i] = textField;
            panel.add(textField);
        }

        setTextFieldListeners(inputs);


        // CREATE BUTTON PANEL
        panel = new JPanel(new GridLayout(1, 2, 50, 50));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // CREATE SAVE BUTTON
        JButton button = new JButton("Save");
        button.setPreferredSize(new Dimension(0, 40));
        button.addActionListener(e -> save());
        panel.add(button);

        // CREATE RESET BUTTON
        button = new JButton("Reset");
        button.setPreferredSize(new Dimension(0, 40));
        button.addActionListener(e -> reset());
        panel.add(button);

        frame.getContentPane().add(panel, BorderLayout.SOUTH);

        // SET FRAME CONFIGURATION
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private static void setTextFieldListeners(JTextField[] textFields) {

        for (int i = 0; i < textFields.length; i++) {
            JTextField textField = textFields[i];
            KeyListener[] keyListeners = textField.getKeyListeners();

            if (keyListeners.length > 0)
                textField.removeKeyListener(keyListeners[0]);

            final HotKey hotKey = modifiedHotKeys.get(i);
            textField.addKeyListener(new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent e) {
                    setKey(hotKey, e.getKeyCode());
                }

                @Override
                public void keyReleased(KeyEvent e) {
                    if (edit != null) {
                        keyPressed = new HashSet<>();
                    }
                }
            });
        }
    }

    // SAVE CURRENT SETTINGS INTO XML FILE
    private static void save() {
        saveHotKeys = modifiedHotKeys;
        modifiedHotKeys = Utility.copy(saveHotKeys);

        setTextFieldListeners(inputs);

        try {
            JOptionPane.showMessageDialog(null, XMLParser.save(saveHotKeys, Files.newOutputStream(Paths.get(fileName))) ?
                    "Control keys have been saved successfully!" : "Control keys couldn't be saved...");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }

    // RESET ALL TEXT BOXES TO LAST SAVE
    private static void reset() {

        modifiedHotKeys = Utility.copy(saveHotKeys);

        for (int i = 0; i < saveHotKeys.size(); i++) {
            inputs[i].setText(format(saveHotKeys.get(i)));
        }
    }

    private static void setKey(HotKey hotKey, int keyCode) {
        if (edit != null) {
            keyPressed.add(keyCode);

            if (!isControlKey(keyCode)) {
                hotKey.setKey(converter.fromKeyCodeToTOSKey(keyCode)).setUseShift(keyPressed.contains(KeyEvent.VK_SHIFT))
                        .setUseCtrl(keyPressed.contains(KeyEvent.VK_CONTROL)).setUseAlt(keyPressed.contains(KeyEvent.VK_ALT));

                edit.setText(format(keyPressed));
                edit = null;
                keyPressed = new HashSet<>();
            }
        }
    }

    private static String format(HotKey hotKey) {
        return (hotKey.useCtrl() ? "Ctrl + " : "") + (hotKey.useAlt() ? "Alt + " : "") + (hotKey.useShift() ? "Shift + " : "") + converter.fromTOSKeyToDisplay(hotKey.getKey());
    }

    private static String format(Set<Integer> set) {
        int key = 0;

        for (Integer i : set) {
            key = i;
        }

        return (set.contains(KeyEvent.VK_CONTROL) ? "Ctrl + " : "") + (set.contains(KeyEvent.VK_ALT) ? "Alt + " : "") + (set.contains(KeyEvent.VK_SHIFT) ? "Shift + " : "") + converter.fromKeyCodeToDisplay(key);
    }

    private static boolean isControlKey(int keyCode) {
        return Arrays.asList(KeyEvent.VK_SHIFT, KeyEvent.VK_CONTROL, KeyEvent.VK_ALT).indexOf(keyCode) != -1;
    }
}
