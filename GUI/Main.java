package Company;
import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.UtilDateModel;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

public class Main extends JFrame {
    private final JScrollPane text_area_scroll;
    private final JTextArea text_area;
    private final JDatePickerImpl datePicker;
    private final JPanel mainPanel, headerPanel, typeReminderPanel, textAreaPanel, containerPanel, fieldPanel,
    dateInputPanel, timeInputPanel, buttonsContainerPanel, addButtonPanel, viewButtonPanel;
    private final JLabel headerLabel, typeReminderLabel, selectDateLabel, selectTimeLabel;
    private final JTextField timeField;
    private final JButton addButton, viewButton;
    private int currentDisplayIndex;

    public Main() {
        super();
        currentDisplayIndex = -1;

        //Regular font and big font
        Font regFont = new Font("Arial", Font.BOLD, 14);
        Font bigFont = new Font("Arial", Font.BOLD, 24);
        Font smallFont = new Font("Arial", Font.BOLD, 12);

        //Date model
        UtilDateModel model = new UtilDateModel();

        //Setting properties for date picker
        Properties p = new Properties();
        p.put("text.today", "Today");
        p.put("text.month", "Month");
        p.put("text.year", "Year");

        //Creating a new date panel and a new date picker
        JDatePanelImpl date_panel = new JDatePanelImpl(model, p);
        datePicker = new JDatePickerImpl(date_panel, new DateLabelFormatter());
        JFormattedTextField datePickerField = datePicker.getJFormattedTextField();
        datePickerField.setFont(smallFont);

        containerPanel = new JPanel();
        containerPanel.setLayout(new BoxLayout(containerPanel, BoxLayout.PAGE_AXIS));
        containerPanel.setBorder(BorderFactory.createEmptyBorder(40, 80, 40, 80));

        mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.PAGE_AXIS));

        headerPanel = new JPanel();
        headerPanel.setLayout(new FlowLayout());

        headerLabel = new JLabel("Reminder Application");
        headerLabel.setFont(bigFont);
        headerLabel.setSize(headerLabel.getPreferredSize());
        headerPanel.add(headerLabel);

        //Creating a scrollable text area
        text_area = new JTextArea(15, 40);
        text_area_scroll = new JScrollPane(text_area);
        text_area_scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        text_area.setLineWrap(true);
        text_area.setWrapStyleWord(true);
        text_area.setEditable(true);
        text_area.setFont(regFont);

        typeReminderPanel = new JPanel();
        typeReminderPanel.setLayout(new BoxLayout(typeReminderPanel, BoxLayout.PAGE_AXIS));

        textAreaPanel = new JPanel();
        textAreaPanel.setLayout(new BoxLayout(textAreaPanel, BoxLayout.X_AXIS));
        textAreaPanel.add(text_area_scroll);

        typeReminderLabel = new JLabel("Type Reminder Below");
        typeReminderLabel.setFont(regFont);
        typeReminderLabel.setSize(typeReminderLabel.getPreferredSize());
        typeReminderPanel.add(typeReminderLabel);
        typeReminderPanel.add(textAreaPanel);
        typeReminderLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        selectDateLabel = new JLabel("Select Date");
        selectDateLabel.setFont(smallFont);
        selectDateLabel.setSize(selectDateLabel.getPreferredSize());

        dateInputPanel = new JPanel();
        dateInputPanel.setLayout(new BoxLayout(dateInputPanel, BoxLayout.PAGE_AXIS));
        dateInputPanel.add(Box.createVerticalGlue());
        dateInputPanel.add(selectDateLabel);
        dateInputPanel.add(datePicker);
        dateInputPanel.add(Box.createVerticalGlue());
        selectDateLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        selectTimeLabel = new JLabel("Input Time (e.g. 8:00 AM)");
        selectTimeLabel.setFont(smallFont);

        timeField = new JTextField();
        timeField.setFont(regFont);

        timeInputPanel = new JPanel();
        timeInputPanel.setLayout(new BoxLayout(timeInputPanel, BoxLayout.PAGE_AXIS));
        timeInputPanel.add(Box.createVerticalGlue());
        timeInputPanel.add(selectTimeLabel);
        timeInputPanel.add(timeField);
        timeInputPanel.add(Box.createVerticalGlue());
        selectTimeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        fieldPanel = new JPanel();
        fieldPanel.setLayout(new GridLayout(1, 3));
        fieldPanel.add(timeInputPanel);
        fieldPanel.add(Box.createRigidArea(new Dimension()));
        fieldPanel.add(dateInputPanel);

        addButton = new JButton("Add Reminder");
        addButton.setPreferredSize(new Dimension(150, 30));
        addButton.setMaximumSize(new Dimension(150, 30));
        addButton.setFont(regFont);
        addButton.addActionListener(ev -> {
            try {
                addReminder();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        viewButton = new JButton("View Reminders");
        viewButton.setPreferredSize(new Dimension(150, 30));
        viewButton.setMaximumSize(new Dimension(150, 30));
        viewButton.setFont(regFont);
        viewButton.addActionListener(ev -> {
            try {
                viewReminders();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        addButtonPanel = new JPanel();
        addButtonPanel.setLayout(new BoxLayout(addButtonPanel, BoxLayout.PAGE_AXIS));
        addButtonPanel.add(addButton);
        addButton.setAlignmentX(Component.RIGHT_ALIGNMENT);

        viewButtonPanel = new JPanel();
        viewButtonPanel.setLayout(new BoxLayout(viewButtonPanel, BoxLayout.PAGE_AXIS));
        viewButtonPanel.add(viewButton);
        viewButton.setAlignmentX(Component.LEFT_ALIGNMENT);

        buttonsContainerPanel = new JPanel();
        buttonsContainerPanel.setLayout(new GridLayout(1, 3));
        buttonsContainerPanel.add(addButtonPanel);
        buttonsContainerPanel.add(Box.createRigidArea(new Dimension()));
        buttonsContainerPanel.add(viewButtonPanel);

        mainPanel.add(headerLabel);
        headerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(Box.createVerticalStrut(75));
        mainPanel.add(typeReminderPanel);
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(fieldPanel);
        mainPanel.add(Box.createVerticalStrut(80));
        mainPanel.add(buttonsContainerPanel);

        containerPanel.add(Box.createVerticalGlue());
        containerPanel.add(mainPanel);
        containerPanel.add(Box.createVerticalGlue());

        getContentPane().add(containerPanel);
        pack();

        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(dim.width / 2 - this.getSize().width / 2, dim.height / 2 - this.getSize().height / 2);
        setTitle("Reminder App");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setVisible(true);

        addComponentListener(new ComponentListener() {
            @Override
            public void componentMoved(ComponentEvent e) {
                GraphicsConfiguration conf = getGraphicsConfiguration();
                GraphicsDevice curDisplay = conf.getDevice();
                GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();

                GraphicsDevice[] allDisplays = env.getScreenDevices();
                for (int i = 0; i < allDisplays.length; i++)
                {
                    if (allDisplays[i].equals(curDisplay))
                    {
                        //the window has been dragged to another display
                        if (i != currentDisplayIndex)
                        {
                            containerPanel.removeAll();
                            containerPanel.add(mainPanel);
                            containerPanel.revalidate();
                            containerPanel.repaint();
                            currentDisplayIndex = i;
                        }
                    }
                }

            }

            @Override
            public void componentResized(ComponentEvent e) {
            }

            @Override
            public void componentShown(ComponentEvent e) {
            }

            @Override
            public void componentHidden(ComponentEvent e) {
            }
        });
    }

    public void viewReminders() throws IOException{
        this.dispose();
        new ReminderWindow();
    }

    public String getTextFromField(){
        return timeField.getText();
    }

    public String getTextFromArea(){
        return text_area.getText();
    }

    //Formatting reminder time to store time as YYYY-MM-DD H:M:S format
    public static String formatReminderTime(String reminder_time){
        String time;

        if (reminder_time.length() == 19){
            time = reminder_time.substring(11, 16);
        }
        else{
            time = reminder_time.substring(11, 15);
        }

        String date = reminder_time.substring(0, 10);
        String formatted_time = "";

        if (reminder_time.toUpperCase().contains("AM")){
            formatted_time = date + " " + time + ":00" + " AM";
        }
        else if (reminder_time.toUpperCase().contains("PM")){
            formatted_time = date + " " + time + ":00" + " PM";
        }
        return formatted_time;
    }

    //Function that executes when Add Reminder button is pressed
    //Creates a RemindersData directory on users C drive if not already exists
    //Also creates a Reminders.txt file in the directory and adds the reminder message plus time to the file
    public void addReminder() throws IOException {
        String date = datePicker.getJFormattedTextField().getText();
        String time = getTextFromField().toUpperCase();

        String date_and_time = (date + " " + time);
        String text_from_area = getTextFromArea().toUpperCase();

        if (!Files.isDirectory(Paths.get("C:/RemindersData"))){
            Files.createDirectories(Paths.get("C:/RemindersData"));
        }

        if (text_from_area.equals("") || date_and_time.equals(" ")){
            JOptionPane.showMessageDialog(null, "Missing reminder time or reminder message.",
                    "Error: " + "Missing field", JOptionPane.ERROR_MESSAGE);
        }
        else if (date_and_time.length() < 18 || date_and_time.length() > 19){
            JOptionPane.showMessageDialog(null, "Check format for reminder time.",
                    "Error: " + "Improper format", JOptionPane.ERROR_MESSAGE);
        }
        else if (time.contains(":") && (time.contains("AM") || time.contains("PM"))){
            FileWriter writer;
            File file = new File("C:/RemindersData/Reminders.txt");

            if (file.createNewFile()){
                writer = new FileWriter(file);
            }
            else{
                writer = new FileWriter(file, true);
            }
            writer.write("(" + getTextFromArea() + ")" + " " + "[" + formatReminderTime(date_and_time) + "]");
            writer.write("\n");
            writer.close();
            JOptionPane.showMessageDialog(null, "Reminder successfully added.",
                    "InfoBox: " + "Reminder added", JOptionPane.INFORMATION_MESSAGE);
        }
        else{
            JOptionPane.showMessageDialog(null, "Must include AM or PM and a semicolon.",
                    "Error: " + "Missing AM/PM", JOptionPane.ERROR_MESSAGE);
        }
    }
}
