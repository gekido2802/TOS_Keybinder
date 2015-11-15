import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarFile;

public abstract class FrameHelper {

    private static final String version = "v0.1 [Beta]";
    private static JTextField edit;
    private static Converter converter;
    private static List<HotKey> savedHotKeys, modifiedHotKeys;
    private static JTextField[] inputs;
    private static List<Integer> keyPressed;
    private static String fileName;
    private static boolean conflict;

    // INITIALIZATION
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

        // CREATE BACKUP
        try {
            if (!new File(fileName + ".bak").exists())
                Utility.copy(Files.newInputStream(Paths.get(fileName)), Files.newOutputStream(Paths.get(fileName + ".bak")));
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, e);
        }

        // INITIALIZE KEY PRESSED STACK
        keyPressed = new ArrayList<>();

        // PARSING FILES
        try {
            JarFile jarFile = new JarFile(URLDecoder.decode(FrameHelper.class.getProtectionDomain().getCodeSource().getLocation().getPath(), "UTF-8"));
            converter = new Converter(JSONParser.parseKeyCode(jarFile.getInputStream(jarFile.getEntry("resources/keycode.json"))));
            JSONParser.parseTOSKeyId(jarFile.getInputStream(jarFile.getEntry("resources/toskeyid.json")));
            savedHotKeys = XMLParser.parse(Files.newInputStream(Paths.get(fileName)));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
            System.exit(0);
        }

        modifiedHotKeys = Utility.copy(savedHotKeys);

        // THIS WILL HELP US KEEP TRACK OF THE TEXT BOXES
        inputs = new JTextField[savedHotKeys.size()];
    }

    // BUILD THE FRAME
    public static void build(JFrame frame) {

        // SET FRAME CONFIGURATION
        frame.setTitle("TOS Keybinder " + version);
        frame.setResizable(false);
        frame.setLayout(new BorderLayout(0, 0));
        frame.setPreferredSize(new Dimension(500, 700));
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        // CREATE MENU BAR
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBorder(BorderFactory.createEmptyBorder());
        frame.getContentPane().add(menuBar, BorderLayout.NORTH);
        JMenu menu = new JMenu("File");
        menuBar.add(menu);
        JMenuItem menuItem = new JMenuItem("Restore Backup");
        menuItem.addActionListener(e -> {
            try {
                Utility.copy(Files.newInputStream(Paths.get(fileName + ".bak")), Files.newOutputStream(Paths.get(fileName)));
                JOptionPane.showMessageDialog(null, "Back up has been restored successfully!");
                savedHotKeys = XMLParser.parse(Files.newInputStream(Paths.get(fileName)));
                reset();
            } catch (IOException e1) {
                JOptionPane.showMessageDialog(null, e1);
            }
        });
        menu.add(menuItem);
        menuItem = new JMenuItem("Quit");
        menuItem.addActionListener(e -> System.exit(0));
        menu.add(menuItem);


        // CREATE MAIN PANEL WITH A SCROLLBAR
        JPanel panel = new JPanel(new GridLayout(savedHotKeys.size(), 2, 50, 50));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JScrollPane scrollPane = new JScrollPane(panel);
        scrollPane.getVerticalScrollBar().setUnitIncrement(20);

        frame.getContentPane().add(scrollPane, BorderLayout.CENTER);

        // FILL MAIN PANEL
        for (int i = 0; i < savedHotKeys.size(); i++) {
            // CREATE ID'S TEXT BOX
            JTextField textField = new JTextField(JSONParser.getKeys().get(savedHotKeys.get(i).getId()));

            textField.setEditable(false);
            textField.setHorizontalAlignment(JLabel.CENTER);
            panel.add(textField);

            // CREATE KEY'S TEXT BOX
            inputs[i] = textField = new JTextField();
            textField.setHorizontalAlignment(JLabel.CENTER);
            textField.setEditable(false);
            textField.setFocusTraversalKeysEnabled(false);
            textField.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (edit == null) {
                        edit = ((JTextField) e.getSource());
                        edit.setText("Press Key...");
                    }
                }
            });
            panel.add(textField);
        }

        setTextFieldKeyListeners(inputs);

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

    // SET VALUES TO COMPONENTS
    public static void postBuild() {
        reset();
    }

    // SET THE KEY LISTENERS TO THE JTEXTFIELD
    private static void setTextFieldKeyListeners(JTextField[] textFields) {

        for (int i = 0; i < textFields.length; i++) {
            JTextField textField = textFields[i];
            KeyListener[] keyListeners = textField.getKeyListeners();

            if (keyListeners.length > 0)
                textField.removeKeyListener(keyListeners[0]);

            final HotKey hotKey = modifiedHotKeys.get(i);
            textField.addKeyListener(new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent e) {
                    setKey(hotKey, e.getKeyCode(), false);
                }

                @Override
                public void keyReleased(KeyEvent e) {
                    if (edit != null) {
                        setKey(hotKey, e.getKeyCode(), true);
                    }
                }
            });
        }
    }

    // SAVE CURRENT SETTINGS INTO XML FILE
    private static void save() {
        savedHotKeys = modifiedHotKeys;
        modifiedHotKeys = Utility.copy(savedHotKeys);

        setTextFieldKeyListeners(inputs);

        if (conflict) {
            JOptionPane.showMessageDialog(null, "There are some duplicated keys.\nPlease verify and save again.");
            return;
        }

        try {
            JOptionPane.showMessageDialog(null, XMLParser.save(savedHotKeys, Files.newOutputStream(Paths.get(fileName))) ?
                    "Control keys have been saved successfully!" : "Control keys couldn't be saved...");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }

    // RESET ALL TEXT BOXES TO LAST SAVE
    private static void reset() {

        modifiedHotKeys = Utility.copy(savedHotKeys);

        setTextFieldKeyListeners(inputs);

        for (int i = 0; i < savedHotKeys.size(); i++) {
            inputs[i].setText(Utility.format(converter, savedHotKeys.get(i)));
        }

        validation();
    }

    // SET A HOTKEY USING THE KEYCODE
    private static void setKey(HotKey hotKey, int keyCode, boolean onRelease) {
        if (edit != null) {
            keyPressed.add(keyCode);

            if (!Utility.isControlKey(keyCode) || onRelease) {

                hotKey.setKey(converter.fromKeyCodeToTOSKey(keyCode)).setUseShift(keyPressed.contains(KeyEvent.VK_SHIFT))
                        .setUseCtrl(keyPressed.contains(KeyEvent.VK_CONTROL)).setUseAlt(keyPressed.contains(KeyEvent.VK_ALT));

                keyPressed = new ArrayList<>();

                edit.setText(Utility.format(converter, hotKey));
                edit = null;

                validation();
            }
        }
    }

    // VALIDATE IF MULTIPLE KEYS ARE THE SAME
    private static void validation() {
        conflict = false;

        for (JTextField j : inputs) {
            j.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        }

        for (JTextField j : inputs) {

            if (j.getText().equals("null")) {
                j.setBorder(BorderFactory.createLineBorder(Color.RED));
                conflict = true;
            }

            for (JTextField j2 : inputs) {
                if (j == j2)
                    continue;

                if (j.getText().equals(j2.getText())) {
                    j.setBorder(BorderFactory.createLineBorder(Color.RED));
                    j2.setBorder(BorderFactory.createLineBorder(Color.RED));
                    conflict = true;
                }
            }
        }
    }
}
