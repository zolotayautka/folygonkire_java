import gui.gui;

public class Folygonkire {
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            gui Gui = new gui();
            Gui.setVisible(true);
        });
    }
}