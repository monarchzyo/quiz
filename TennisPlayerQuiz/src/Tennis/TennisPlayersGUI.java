package Tennis;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import javax.swing.DefaultListModel;

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
        readTennisPlayers("Temp.txt");
        readStudents("Temp2.txt");
        startQuiz();
    }
    private void readTennisPlayers(String filename) {
        try {
            // Read file and populate both LinkedList and TreeMap
            BufferedReader reader = new BufferedReader(new FileReader(filename));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 2) {
                    String name = parts[0].trim();
                    String country = parts[1].trim();
                    
                    // Add to LinkedList
                    TennisPlayer player = new TennisPlayer(name, 0, country, "", "", "");
                    tennisPlayerList.add(player);
                    
                    // Add to TreeMap (name -> country)
                    tennisPlayersTreeMap.put(name, country);
                    
                    // Initialize used flags
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

// Add action listeners for the buttons (you can set these in NetBeans designer)
private void questionsTextFieldActionPerformed(java.awt.event.ActionEvent evt) {
    try {
        totalQuestions = Integer.parseInt(questionsTextField.getText().trim());
        if (totalQuestions > 0 && totalQuestions <= tennisPlayerList.size()) {
            questionsTextField.setEnabled(false);
            displayRandomPlayer();
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
        
        // Enable submit button
        submitButton.setEnabled(true);
        nextButton.setEnabled(false);
    }
    private void submitButtonActionPerformed(java.awt.event.ActionEvent evt) {
        String selectedCountry = (String) countriesComboBox.getSelectedItem();
        String correctCountry = tennisPlayersTreeMap.get(currentTennisPlayerName);
        
        if (selectedCountry.equals(correctCountry)) {
            correctAnswers++;
            resultJLabel.setText("Correct! " + correctAnswers + "/" + (currentQuestion + 1));
        } else {
            resultJLabel.setText("Incorrect! Correct answer: " + correctCountry);
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
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
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

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        studentList.setToolTipText("");

        studentsJList.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        studentList.setViewportView(studentsJList);

        TitleLabel.setText("Tennis Player Quiz");

        MainMenuPicture.setText("PICTURE");

        questionsLabel.setText("Questions:");

        countriesComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        submitButton.setText("Submit");

        nextButton.setText("Next");

        playAgainButton.setText("Play Again");

        selectCountryLabel.setText("Select Country:");

        fileMenu.setText("File");
        topMenuBar.add(fileMenu);

        studentDatabaseMenu.setText("Student Database");
        topMenuBar.add(studentDatabaseMenu);

        helpMenu.setText("Help");
        topMenuBar.add(helpMenu);

        setJMenuBar(topMenuBar);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(submitButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(nextButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(playAgainButton)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(9, 9, 9)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(MainMenuPicture, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(selectCountryLabel)
                                .addGap(50, 50, 50))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(TitleLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(41, 41, 41)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(questionsLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(questionsTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(0, 36, Short.MAX_VALUE))
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                        .addGap(0, 0, Short.MAX_VALUE)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(playerJLabel)
                                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                                .addComponent(studentList)
                                                .addComponent(countriesComboBox, 0, 127, Short.MAX_VALUE))
                                            .addComponent(resultJLabel))
                                        .addGap(32, 32, 32))))))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(questionsLabel)
                    .addComponent(questionsTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(TitleLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(playerJLabel)
                .addGap(30, 30, 30)
                .addComponent(resultJLabel)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(selectCountryLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(MainMenuPicture, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(4, 4, 4)
                .addComponent(countriesComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(studentList, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(submitButton)
                    .addComponent(nextButton)
                    .addComponent(playAgainButton))
                .addGap(18, 18, 18))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
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
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> new TennisPlayersGUI().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel MainMenuPicture;
    private javax.swing.JLabel TitleLabel;
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
