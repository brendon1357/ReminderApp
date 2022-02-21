package Company;
import javax.swing.*;
import javax.swing.plaf.basic.BasicArrowButton;
import java.awt.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;

public class ReminderWindow extends JFrame {
    private final JButton deleteButton, fullTextButton;
    private final BasicArrowButton backButton;
    private final JList<String> list;
    private final JPanel listPanel, buttonPanel, mainPanel, bottomContainerPanel, backButtonPanel, topFlowPanel;
    private final JScrollPane scrollPane;

    public ReminderWindow() throws IOException {
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        Font font = new Font("Helvetica", Font.BOLD, 14);
        Font big_font = new Font("Helvetica", Font.BOLD, 28);

        backButton = new BasicArrowButton(BasicArrowButton.WEST){
            @Override
            public Dimension getPreferredSize(){
                return new Dimension(50, 30);
            }
        };
        backButton.setFont(big_font);
        backButton.addActionListener(ev -> backToMain());

        backButtonPanel = new JPanel();
        backButtonPanel.setLayout(new BoxLayout(backButtonPanel, BoxLayout.X_AXIS));
        backButtonPanel.add(backButton);

        topFlowPanel = new JPanel();
        topFlowPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        topFlowPanel.add(backButtonPanel);
        topFlowPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 0, 0));

        list = new JList<>(listedReminders());
        listPanel = new JPanel();
        list.setFont(font);
        list.setFixedCellHeight(50);

        scrollPane = new JScrollPane(list);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        listPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 50, 0));
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.PAGE_AXIS));
        listPanel.add(scrollPane);

        deleteButton = new JButton("Delete Element");
        deleteButton.setFont(font);
        deleteButton.setPreferredSize(new Dimension(150, 30));
        deleteButton.addActionListener(e -> {
            try {
                deleteListElement();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        });

        fullTextButton = new JButton("View Text");
        fullTextButton.setFont(font);
        fullTextButton.setPreferredSize(new Dimension(150, 30));
        fullTextButton.addActionListener(e -> viewElementText());

        buttonPanel = new JPanel();
        buttonPanel.setPreferredSize(new Dimension(100, 100));
        buttonPanel.add(deleteButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(60, 0)));
        buttonPanel.add(fullTextButton);

        bottomContainerPanel = new JPanel();
        bottomContainerPanel.setLayout(new BorderLayout());
        bottomContainerPanel.add(listPanel, BorderLayout.CENTER);
        bottomContainerPanel.add(buttonPanel, BorderLayout.SOUTH);
        bottomContainerPanel.setBorder(BorderFactory.createEmptyBorder(40, 80, 0, 80));

        mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.PAGE_AXIS));
        mainPanel.add(topFlowPanel);
        mainPanel.add(bottomContainerPanel);

        getContentPane().add(mainPanel);
        pack();
        
        setMinimumSize(new Dimension(850, 725));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocation(dim.width / 2 - this.getSize().width / 2, dim.height / 2 - this.getSize().height / 2);
        setResizable(true);
        setVisible(true);
    }

    public static DefaultListModel<String> listedReminders() throws IOException {
        FileInputStream fstream = new FileInputStream("C:/RemindersData/Reminders.txt");
        BufferedReader buffered_reader = new BufferedReader(new InputStreamReader(fstream));
        DefaultListModel<String> reminders = new DefaultListModel<>();
        String line;

        while ((line = buffered_reader.readLine()) != null) {
            //splitting message and time
            String[] split_line = line.split("\\[");
            String message = Arrays.toString(new String[]{split_line[0].trim()});
            String time = Arrays.toString(new String[]{split_line[1].trim()});
            //removing leading '[(' and trailing ')]'
            String formatted_line = message.substring(2, message.length() - 2);
            //removing trailing ']' in time
            reminders.addElement(formatted_line + " " + time.substring(0, time.length() - 1));
        }
        fstream.close();
        return reminders;
    }

    public void backToMain() {
        new Main();
        this.dispose();
    }

    public void deleteListElement() throws IOException {
        int selected_index = list.getSelectedIndex();
        DefaultListModel<String> listModel = (DefaultListModel<String>) list.getModel();

        if (list.getSelectedIndex() != -1) {
            String[] elem = listModel.getElementAt(selected_index).split("\\[");
            listModel.remove(selected_index);

            String message = Arrays.toString(new String[]{elem[0].trim()});
            String time = Arrays.toString(new String[]{elem[1].trim()});
            String replaced_message = message.replace("[", "(").replace("]", ")");
            //using time.substring() to remove ']' from end of time String
            String formatted_message = replaced_message + " " + time.substring(0, time.length() - 1);
            removeLineFromFile(formatted_message);
        } else {
            JOptionPane.showMessageDialog(null, "Must select a list element to be deleted.",
                    "Error: " + "List element not selected", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void removeLineFromFile(String line_to_remove) throws IOException {
        File inputFile = new File("C:/RemindersData/Reminders.txt");
        File tempFile = new File("C:/RemindersData/TempFile.txt");

        BufferedReader reader = new BufferedReader(new FileReader(inputFile));
        BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));

        String currentLine;

        while ((currentLine = reader.readLine()) != null) {
            String trimmedLine = currentLine.trim();
            if (trimmedLine.equals(line_to_remove)) continue;
            writer.write(currentLine + System.getProperty("line.separator"));
        }
        writer.close();
        reader.close();

        Path source = Paths.get("C:/RemindersData/TempFile.txt");
        Files.move(source, source.resolveSibling("Reminders.txt"), StandardCopyOption.REPLACE_EXISTING);

        JOptionPane.showMessageDialog(null, "Reminder Successfully deleted.",
                "Info: " + "Deletion", JOptionPane.INFORMATION_MESSAGE);
    }

    public void viewElementText() {
        String text = list.getSelectedValue();
        if (list.getSelectedIndex() != -1) {
            if (!text.equals("")) {
                FixedWidthText.showLabel(360, "px", text);
            }
        } else {
            JOptionPane.showMessageDialog(null, "Must select a list element to be viewed.",
                    "Error: " + "List element not selected", JOptionPane.ERROR_MESSAGE);
        }
    }
}
