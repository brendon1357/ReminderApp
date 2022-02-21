package Company;
import javax.swing.*;
import java.io.IOException;

public class Start {
    public static void main(String[] args) throws IOException {
        try
        {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        new Main();
    }
}
