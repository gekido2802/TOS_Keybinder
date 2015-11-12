import javax.swing.*;

public class Main {

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
                e.printStackTrace();
            }

            // INITIALIZATION
            FrameHelper.preBuild();

            // BUILD THE FRAME
            FrameHelper.build(new JFrame());

            // SET VALUES TO COMPONENTS
            FrameHelper.postBuild();
        });
    }
}
