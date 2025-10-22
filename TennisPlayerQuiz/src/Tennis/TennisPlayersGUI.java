package Tennis;

import java.awt.Image;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;

public class TennisPlayersGUI extends javax.swing.JFrame {
    
    private BinarySearchTree studentsTree = new BinarySearchTree();
    private List<TennisPlayer> tennisPlayerList = new LinkedList<TennisPlayer>();
    private Map<String, String> tennisPlayersTreeMap = new TreeMap<String, String>();
    private ArrayList<Boolean> tennisPlayersUsedArrayList = new ArrayList<Boolean>();
    
    private int currentQuestion = 0;
    private int totalQuestions = 0;
    private int correctAnswers = 0;
    private String currentTennisPlayerName;
    private Student selectedStudent;
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(TennisPlayersGUI.class.getName());

    public TennisPlayersGUI() 
    {
        initComponents();
        questionsTextField.addActionListener(this::questionsTextFieldActionPerformed);
        submitButton.addActionListener(this::submitButtonActionPerformed);
        nextButton.addActionListener(this::nextButtonActionPerformed);
        playAgainButton.addActionListener(this::playAgainButtonActionPerformed);
        studentsJList.addListSelectionListener(this::studentsJListValueChanged);
        aboutMenuItem.addActionListener(e -> showAboutDialog());
        readTennisPlayers("src/Data/Temp.txt");
        readStudents("src/Data/Temp2.txt");
        startQuiz();
        setDefaultPicture();
        
    
    }
    private void readTennisPlayers(String filename) {
    try {
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        String line;
        while ((line = reader.readLine()) != null) {
            String[] parts = line.split(",");
            if (parts.length >= 6) {
                String name = parts[0].trim();
                String country = parts[1].trim();
                int age = Integer.parseInt(parts[2].trim());
                String email = parts[3].trim();
                String phone = parts[4].trim();
                String rank = parts[5].trim();
                
                // Create TennisPlayer with all parameters
                TennisPlayer player = new TennisPlayer(name, age, country, email, phone, rank);
                tennisPlayerList.add(player);
                tennisPlayersTreeMap.put(name, country);
                tennisPlayersUsedArrayList.add(false);
            }
        }
        reader.close();
    } catch (IOException e) {
        e.printStackTrace();
    }
}
    private int getUniqueRandomNumber() {
        int randomIndex;
        do {
            randomIndex = (int)(Math.random() * tennisPlayerList.size());
        } while (tennisPlayersUsedArrayList.get(randomIndex));
        
        tennisPlayersUsedArrayList.set(randomIndex, true);
        return randomIndex;
    }
    
    private void readStudents(String filename) {
    try {
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        String line;
        DefaultListModel<String> listModel = new DefaultListModel<>();
        
        while ((line = reader.readLine()) != null) {
            String[] parts = line.split(",");
            if (parts.length >= 4) {
                String name = parts[0].trim();
                int age = Integer.parseInt(parts[1].trim());
                int correct = Integer.parseInt(parts[2].trim());
                int total = Integer.parseInt(parts[3].trim());
                
                // Create Student object and add to BST
                Student student = new Student(name, age, correct, total);
                studentsTree.add(student);
                
                // Add to JList for display
                listModel.addElement(name);
            }
        }
        studentsJList.setModel(listModel);
        reader.close();
    } catch (IOException e) {
        e.printStackTrace();
        javax.swing.JOptionPane.showMessageDialog(this, 
            "Error reading students file: " + e.getMessage(), 
            "File Error", 
            javax.swing.JOptionPane.ERROR_MESSAGE);
    } catch (NumberFormatException e) {
        e.printStackTrace();
        javax.swing.JOptionPane.showMessageDialog(this, 
            "Error parsing student data: " + e.getMessage(), 
            "Data Error", 
            javax.swing.JOptionPane.ERROR_MESSAGE);
    }
}
    private void questionsTextFieldActionPerformed(java.awt.event.ActionEvent evt) {
    try {
        totalQuestions = Integer.parseInt(questionsTextField.getText().trim());
        if (totalQuestions > 0 && totalQuestions <= tennisPlayerList.size()) {
            questionsTextField.setEnabled(false);
            displayRandomPlayer(); // This will now show the image!
        } else {
            javax.swing.JOptionPane.showMessageDialog(this,
                "Number of questions must be between 1 and " + tennisPlayerList.size(),
                "Invalid Input",
                javax.swing.JOptionPane.ERROR_MESSAGE);
        }
    } catch (NumberFormatException e) {
        javax.swing.JOptionPane.showMessageDialog(this,
            "Please enter a valid number",
            "Invalid Input",
            javax.swing.JOptionPane.ERROR_MESSAGE);
    }
    }
    private void startQuiz() {
    // Initialize quiz state
    currentQuestion = 0;
    correctAnswers = 0;
    
    // Reset used players array
    for (int i = 0; i < tennisPlayersUsedArrayList.size(); i++) {
        tennisPlayersUsedArrayList.set(i, false);
    }
    
    // Set up initial button states
    submitButton.setEnabled(false);
    nextButton.setEnabled(false);
    playAgainButton.setEnabled(false);
    questionsTextField.setEnabled(true);
    
    // Populate countries combo box with unique countries
    populateCountriesComboBox();
}

    private void populateCountriesComboBox() {
    // Use a TreeSet to get unique sorted countries
    java.util.Set<String> uniqueCountries = new java.util.TreeSet<>();
    for (TennisPlayer player : tennisPlayerList) {
        uniqueCountries.add(player.getCountry());
    }
    
    countriesComboBox.removeAllItems();
    for (String country : uniqueCountries) {
        countriesComboBox.addItem(country);
    }
}
    private void endQuiz() {
    // Update student's score if a student is selected
    if (selectedStudent != null) {
        // Update the student's record
        studentsTree.remove(selectedStudent);
        Student updatedStudent = new Student(
            selectedStudent.getName(),
            selectedStudent.getAge(),
            correctAnswers,
            totalQuestions
        );
        studentsTree.add(updatedStudent);
        
        // Save students to file
        saveStudents("Temp2.txt");
    }
    
    // Enable play again button
    playAgainButton.setEnabled(true);
    submitButton.setEnabled(false);
    nextButton.setEnabled(false);
    
    resultJLabel.setText("Quiz finished! Score: " + correctAnswers + "/" + totalQuestions);
}
    private void saveStudents(String filename) {
    try {
        java.io.PrintWriter writer = new java.io.PrintWriter(new java.io.FileWriter(filename));
        saveStudentsHelper(studentsTree.getRoot(), writer);
        writer.close();
    } catch (IOException e) {
        e.printStackTrace();
    }
}

private void saveStudentsHelper(BinarySearchTreeNode node, java.io.PrintWriter writer) {
    if (node != null) {
        saveStudentsHelper(node.getLeft(), writer);
        Student student = node.getData();
        writer.println(student.getName() + "," + 
                      student.getAge() + "," + 
                      student.getCorrect() + "," + 
                      student.getTotalQuestions());
        saveStudentsHelper(node.getRight(), writer);
    }
}


private void nextButtonActionPerformed(java.awt.event.ActionEvent evt) {
    currentQuestion++;
    displayRandomPlayer();
}

private void playAgainButtonActionPerformed(java.awt.event.ActionEvent evt) {
    startQuiz();
}
    
    private void displayRandomPlayer() {
        if (currentQuestion >= totalQuestions) {
            // Quiz finished
            endQuiz();
            return;
        }
        
        int randomIndex = getUniqueRandomNumber();
        TennisPlayer randomPlayer = tennisPlayerList.get(randomIndex);
        currentTennisPlayerName = randomPlayer.getName();
        
        // Display player image and name
        playerJLabel.setText(randomPlayer.getName());
        // Load and display player image...
        loadPlayerImage(randomPlayer.getName());
        // Enable submit button
        submitButton.setEnabled(true);
        nextButton.setEnabled(false);
        resultJLabel.setText("");
        
    }
    
    private void loadPlayerImage(String playerName) {
    try {
        // Remove spaces and special characters from filename
        String imageName = playerName.replaceAll("[^a-zA-Z0-9]", "");
        String imagePath = "src/Image/" + imageName + ".jpg";
        
        java.io.File imageFile = new java.io.File(imagePath);
        if (imageFile.exists()) {
            ImageIcon imageIcon = new ImageIcon(imagePath);
            
            // Scale image to fit the label if needed
            Image image = imageIcon.getImage();
            Image scaledImage = image.getScaledInstance(200, 200, Image.SCALE_SMOOTH);
            imageIcon = new ImageIcon(scaledImage);
            
            MainMenuPicture.setIcon(imageIcon);
            MainMenuPicture.setText("");
        } else 
        {
            setDefaultPicture();
        }
    } catch (Exception e) {
       setDefaultPicture();
    }
}
    private void setDefaultPicture()
    {
         try {
        String defaultImagePath = "src/Image/default.jpeg"; // or whatever your default image is
        java.io.File imageFile = new java.io.File(defaultImagePath);
        if (imageFile.exists()) {
            ImageIcon defaultIcon = new ImageIcon(defaultImagePath);
            Image image = defaultIcon.getImage();
            Image scaledImage = image.getScaledInstance(200, 200, Image.SCALE_SMOOTH);
            defaultIcon = new ImageIcon(scaledImage);
            
            MainMenuPicture.setIcon(defaultIcon);
            MainMenuPicture.setText(""); // Remove text
        }
    } catch (Exception e) {
        // If default image fails, just remove the text
        MainMenuPicture.setText("");
    }
    }
    
    private void submitButtonActionPerformed(java.awt.event.ActionEvent evt) {
        String selectedCountry = (String) countriesComboBox.getSelectedItem();
        String correctCountry = tennisPlayersTreeMap.get(currentTennisPlayerName);
        
        if (selectedCountry.equals(correctCountry)) {
            correctAnswers++;
            resultJLabel.setText("Correct! " + correctAnswers + "/" + (currentQuestion + 1));
        } else {
            resultJLabel.setText("<html>Incorrect!<br>Correct answer: " + correctCountry + "</html>");
        }
        
        submitButton.setEnabled(false);
        nextButton.setEnabled(true);
    }
    private void studentsJListValueChanged(javax.swing.event.ListSelectionEvent evt) {
    if (!evt.getValueIsAdjusting()) {
        String selectedName = studentsJList.getSelectedValue();
        if (selectedName != null) {
            // Find the student in BST
            BinarySearchTreeNode node = studentsTree.nodeWith(selectedName, studentsTree.getRoot());
            if (node != null) {
                selectedStudent = node.getData();
            }
        }
    }
    }
  
    private void showAboutDialog()
    {
        About aboutDialog = new About(this, true);
              aboutDialog.setVisible(true);
    }
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        studentList = new javax.swing.JScrollPane();
        studentsJList = new javax.swing.JList<>();
        TitleLabel = new javax.swing.JLabel();
        MainMenuPicture = new javax.swing.JLabel();
        questionsTextField = new javax.swing.JTextField();
        questionsLabel = new javax.swing.JLabel();
        countriesComboBox = new javax.swing.JComboBox<>();
        submitButton = new javax.swing.JButton();
        nextButton = new javax.swing.JButton();
        playAgainButton = new javax.swing.JButton();
        selectCountryLabel = new javax.swing.JLabel();
        playerJLabel = new javax.swing.JLabel();
        resultJLabel = new javax.swing.JLabel();
        topMenuBar = new javax.swing.JMenuBar();
        fileMenu = new javax.swing.JMenu();
        studentDatabaseMenu = new javax.swing.JMenu();
        helpMenu = new javax.swing.JMenu();
        aboutMenuItem = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        studentList.setToolTipText("");

        studentsJList.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        studentList.setViewportView(studentsJList);

        getContentPane().add(studentList, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 290, 127, 100));

        TitleLabel.setFont(new java.awt.Font("SansSerif", 1, 18)); // NOI18N
        TitleLabel.setForeground(new java.awt.Color(102, 255, 51));
        TitleLabel.setText("Tennis Player Quiz");
        getContentPane().add(TitleLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(15, 17, 170, 61));

        MainMenuPicture.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        MainMenuPicture.setMaximumSize(new java.awt.Dimension(200, 200));
        MainMenuPicture.setMinimumSize(new java.awt.Dimension(200, 200));
        MainMenuPicture.setPreferredSize(new java.awt.Dimension(200, 200));
        getContentPane().add(MainMenuPicture, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 130, -1, -1));

        questionsTextField.setPreferredSize(new java.awt.Dimension(80, 25));
        getContentPane().add(questionsTextField, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 38, -1, -1));

        questionsLabel.setText("Questions:");
        getContentPane().add(questionsLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(226, 41, 62, -1));

        countriesComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        getContentPane().add(countriesComboBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 220, 127, -1));

        submitButton.setText("Submit");
        getContentPane().add(submitButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(6, 408, -1, -1));

        nextButton.setText("Next");
        getContentPane().add(nextButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(84, 408, -1, -1));

        playAgainButton.setText("Play Again");
        getContentPane().add(playAgainButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(162, 408, -1, -1));

        selectCountryLabel.setFont(new java.awt.Font("SansSerif", 1, 18)); // NOI18N
        selectCountryLabel.setText("Select Country:");
        getContentPane().add(selectCountryLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 200, -1, 14));
        getContentPane().add(playerJLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(241, 104, -1, -1));
        getContentPane().add(resultJLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(241, 148, -1, -1));

        fileMenu.setText("File");
        topMenuBar.add(fileMenu);

        studentDatabaseMenu.setText("Student Database");
        topMenuBar.add(studentDatabaseMenu);

        helpMenu.setText("Help");

        aboutMenuItem.setText("About");
        helpMenu.add(aboutMenuItem);

        topMenuBar.add(helpMenu);

        setJMenuBar(topMenuBar);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        Splash splash = new Splash(3000);
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }
        java.awt.EventQueue.invokeLater(() -> new TennisPlayersGUI().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel MainMenuPicture;
    private javax.swing.JLabel TitleLabel;
    private javax.swing.JMenuItem aboutMenuItem;
    private javax.swing.JComboBox<String> countriesComboBox;
    private javax.swing.JMenu fileMenu;
    private javax.swing.JMenu helpMenu;
    private javax.swing.JButton nextButton;
    private javax.swing.JButton playAgainButton;
    private javax.swing.JLabel playerJLabel;
    private javax.swing.JLabel questionsLabel;
    private javax.swing.JTextField questionsTextField;
    private javax.swing.JLabel resultJLabel;
    private javax.swing.JLabel selectCountryLabel;
    private javax.swing.JMenu studentDatabaseMenu;
    private javax.swing.JScrollPane studentList;
    private javax.swing.JList<String> studentsJList;
    private javax.swing.JButton submitButton;
    private javax.swing.JMenuBar topMenuBar;
    // End of variables declaration//GEN-END:variables
}
