package Company;
import javax.swing.*;

public class FixedWidthText {
    public static void showLabel(int width, String units, String message) {
        String content1 = "<html>"
                + "<body style='background-color: grey; width: ";
        String content2 = "'>"
                + "<h1>Reminder</h1>"
                + "<h3>" + message + "</h3>";
        final String content = content1 + width + units
                + content2;
        Runnable r = () -> {
            JLabel label = new JLabel(content);
            JOptionPane.showMessageDialog(null, label);
        };
        SwingUtilities.invokeLater(r);
    }
}