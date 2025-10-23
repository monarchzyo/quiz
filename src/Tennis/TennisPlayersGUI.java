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
import javax.swing.JOptionPane;

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
        addStudentMenu.addActionListener(e -> addStudent());
        editStudentMenu.addActionListener(e -> editStudent());
        deleteStudentMenu.addActionListener(e -> deleteStudent());
        searchStudentMenu.addActionListener(e -> searchStudent());
        studentDetailsMenu.addActionListener(e -> showStudentDetails());
        tennisPlayersDetailsMenu.addActionListener(e -> showTennisPlayerDetails());
        newMenu.addActionListener(e -> newFile());
        clearMenu.addActionListener(e -> clearQuiz());
        exitMenu.addActionListener(e -> exitApplication());
        questionsTextField.setToolTipText("Enter number of questions (1-" + tennisPlayerList.size() + ")");
        submitButton.setToolTipText("Submit your country selection");
        nextButton.setToolTipText("Move to next question");
        playAgainButton.setToolTipText("Start a new quiz");
        countriesComboBox.setToolTipText("Select the country for the displayed tennis player");
        studentsJList.setToolTipText("Select a student to take the quiz");
        printFormMenuItem.setToolTipText("Print the main quiz form as GUI");
        printStudentMenuItem.setToolTipText("Print detailed report of selected student");
        aboutMenuItem.setToolTipText("View information about this application");
        studentDetailsMenu.setToolTipText("View detailed information about selected student");
        newMenu.setToolTipText("Load a new set of tennis players");
        clearMenu.setToolTipText("Cancel and reset current quiz");
        exitMenu.setToolTipText("Exit the application");
        printMenuItems();
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
    private void questionsTextFieldActionPerformed(java.awt.event.ActionEvent evt) 
    {
            String input = questionsTextField.getText().trim();
            if (!Validation.isInteger(input, 1, tennisPlayerList.size())) {
                javax.swing.JOptionPane.showMessageDialog(this,
                    "Number of questions must be between 1 and " + tennisPlayerList.size(),
                    "Invalid Input",
                    javax.swing.JOptionPane.ERROR_MESSAGE);
                questionsTextField.setText("");
                questionsTextField.requestFocus();
                return;
            }

            totalQuestions = Integer.parseInt(input);
            questionsTextField.setEnabled(false);
            displayRandomPlayer();
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
            try {
                // Create updated student with cumulative scores
                Student updatedStudent = new Student(
                    selectedStudent.getName(),
                    selectedStudent.getAge(),
                    selectedStudent.getCorrect() + correctAnswers,  // Add to existing correct
                    selectedStudent.getTotalQuestions() + totalQuestions  // Add to existing total
                );

                // Remove old student and add updated one
                studentsTree.remove(selectedStudent);
                studentsTree.add(updatedStudent);

                // Update the selectedStudent reference
                selectedStudent = updatedStudent;

                // Save students to file
                saveStudents("Temp2.txt");

                // Refresh the students list to show updated data
                refreshStudentsList();

            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                    "Error updating student score: " + e.getMessage(),
                    "Update Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }

        // Enable play again button
        playAgainButton.setEnabled(true);
        submitButton.setEnabled(false);
        nextButton.setEnabled(false);

        resultJLabel.setText("Quiz finished! Score: " + correctAnswers + "/" + totalQuestions);
    }
    private void saveStudents(String filename) {
    try {
        java.io.PrintWriter writer = new java.io.PrintWriter(new java.io.FileWriter("src/Data/" + filename));
        saveStudentsHelper(studentsTree.getRoot(), writer);
        writer.close();
    } catch (IOException e) {
        e.printStackTrace();
    }
}

        private void deleteStudent() {
        System.out.println("=== DEBUG: deleteStudent() STARTED ===");
    
    if (selectedStudent == null) {
        javax.swing.JOptionPane.showMessageDialog(this,
            "Please select a student from the list first.",
            "No Student Selected",
            javax.swing.JOptionPane.WARNING_MESSAGE);
        return;
    }

    // Confirm deletion
    int confirmation = javax.swing.JOptionPane.showConfirmDialog(this,
        "Are you sure you want to delete student:\n" +
        "Name: " + selectedStudent.getName() + "\n" +
        "Age: " + selectedStudent.getAge() + "\n" +
        "This action cannot be undone!",
        "Confirm Deletion",
        javax.swing.JOptionPane.YES_NO_OPTION,
        javax.swing.JOptionPane.WARNING_MESSAGE);

    if (confirmation == javax.swing.JOptionPane.YES_OPTION) {
        try {
            System.out.println("DEBUG: Deleting student: " + selectedStudent.getName());
            
            // Remove from BST
            boolean removed = studentsTree.remove(selectedStudent);
            System.out.println("DEBUG: Student removed from BST: " + removed);
            
            if (removed) {
                // Save to file
                saveStudents("Temp2.txt");
                System.out.println("DEBUG: File saved after deletion");
                
                // Refresh the list
                refreshStudentsList();
                System.out.println("DEBUG: List refreshed after deletion");
                
                // Clear selection
                selectedStudent = null;
                studentsJList.clearSelection();
                
                javax.swing.JOptionPane.showMessageDialog(this,
                    "Student deleted successfully!",
                    "Deletion Successful",
                    javax.swing.JOptionPane.INFORMATION_MESSAGE);
            } else {
                javax.swing.JOptionPane.showMessageDialog(this,
                    "Error: Could not delete student from database.",
                    "Deletion Error",
                    javax.swing.JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            System.out.println("DEBUG: Error during deletion: " + e.getMessage());
            e.printStackTrace();
            javax.swing.JOptionPane.showMessageDialog(this,
                "Error deleting student: " + e.getMessage(),
                "Deletion Error",
                javax.swing.JOptionPane.ERROR_MESSAGE);
        }
    } else {
        System.out.println("DEBUG: Deletion cancelled by user");
    }
    System.out.println("=== DEBUG: deleteStudent() COMPLETED ===");
}
        
        
        private void searchStudent() {
    System.out.println("=== DEBUG: searchStudent() STARTED ===");
    
    // Show input dialog for search
    String searchName = javax.swing.JOptionPane.showInputDialog(this,
        "Enter student name to search:",
        "Search Student",
        javax.swing.JOptionPane.QUESTION_MESSAGE);

    // If user clicked Cancel or entered nothing
    if (searchName == null || searchName.trim().isEmpty()) {
        System.out.println("DEBUG: Search cancelled or empty input");
        return;
    }

    searchName = searchName.trim();
    System.out.println("DEBUG: Searching for student: '" + searchName + "'");
    
    try {
        // Search in BST
        Student foundStudent = studentsTree.findStudentByName(searchName);
        
        if (foundStudent != null) {
            System.out.println("DEBUG: Student found: " + foundStudent.getName());
            
            // Select the student in the JList
            selectStudentInList(foundStudent.getName());
            
            // Show detailed information
            String message = "STUDENT FOUND!\n\n" +
                           "Name: " + foundStudent.getName() + "\n" +
                           "Age: " + foundStudent.getAge() + "\n" +
                           "Correct Answers: " + foundStudent.getCorrect() + "\n" +
                           "Total Questions: " + foundStudent.getTotalQuestions() + "\n" +
                           "Success Rate: " + String.format("%.1f", foundStudent.calculatePercent()) + "%\n\n" +
                           "Performance: " + getPerformanceRating(foundStudent.calculatePercent());
            
            javax.swing.JOptionPane.showMessageDialog(this,
                message,
                "Student Found - " + foundStudent.getName(),
                javax.swing.JOptionPane.INFORMATION_MESSAGE);
        } else {
            System.out.println("DEBUG: Student not found: '" + searchName + "'");
            javax.swing.JOptionPane.showMessageDialog(this,
                "Student '" + searchName + "' was not found in the database.\n" +
                "Please check the spelling and try again.",
                "Student Not Found",
                javax.swing.JOptionPane.WARNING_MESSAGE);
        }
    } catch (Exception e) {
        System.out.println("DEBUG: Error during search: " + e.getMessage());
        e.printStackTrace();
        javax.swing.JOptionPane.showMessageDialog(this,
            "Error searching for student: " + e.getMessage(),
            "Search Error",
            javax.swing.JOptionPane.ERROR_MESSAGE);
    }
    System.out.println("=== DEBUG: searchStudent() COMPLETED ===");
}
    
        private String getPerformanceRating(double percent) 
        {
        if (percent >= 80) {
            return "Excellent! Keep up the great work!";
        } else if (percent >= 60) {
            return "Good performance! Room for improvement.";
        } else {
            return "Needs improvement. Practice more!";
        }
        }
    
    
        private void showTennisPlayerDetails() {
            System.out.println("=== DEBUG: showTennisPlayerDetails() STARTED ===");

            // Get the currently displayed tennis player
            if (currentTennisPlayerName == null) {
                javax.swing.JOptionPane.showMessageDialog(this,
                    "Please start a quiz first to view tennis player details.",
                    "No Player Displayed",
                    javax.swing.JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                // Find the tennis player in the list
                TennisPlayer currentPlayer = null;
                for (TennisPlayer player : tennisPlayerList) {
                    if (player.getName().equals(currentTennisPlayerName)) {
                        currentPlayer = player;
                        break;
                    }
                }

                if (currentPlayer != null) {
                    System.out.println("DEBUG: Showing details for: " + currentPlayer.getName());
                    TennisPlayerDetails dialog = new TennisPlayerDetails(this, true, currentPlayer);
                    dialog.setVisible(true);
                } else {
                    javax.swing.JOptionPane.showMessageDialog(this,
                        "Could not find details for the current tennis player.",
                        "Player Not Found",
                        javax.swing.JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                System.out.println("DEBUG: Error showing tennis player details: " + e.getMessage());
                e.printStackTrace();
                javax.swing.JOptionPane.showMessageDialog(this,
                    "Error displaying tennis player details: " + e.getMessage(),
                    "Display Error",
                    javax.swing.JOptionPane.ERROR_MESSAGE);
            }
        }
    
        private void newFile() 
        {
            // Ask user what type of file they want to load
            String[] options = {"Tennis Players", "Students"};
            int choice = JOptionPane.showOptionDialog(this,
                "What type of file do you want to load?",
                "Select File Type",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]);

            if (choice == 0) {
                loadNewTennisPlayers();
            } else if (choice == 1) {
                loadNewStudents();
            }
        }

            private void loadNewStudents() {
                javax.swing.JFileChooser fileChooser = new javax.swing.JFileChooser();
                fileChooser.setDialogTitle("Select New Students File");
                fileChooser.setCurrentDirectory(new java.io.File("src/Data/"));
                fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Text Files (*.txt)", "txt"));

                int result = fileChooser.showOpenDialog(this);
                if (result == javax.swing.JFileChooser.APPROVE_OPTION) {
                    try {
                        java.io.File selectedFile = fileChooser.getSelectedFile();

                        // Clear existing student data
                        studentsTree = new BinarySearchTree();
                        selectedStudent = null;
                        studentsJList.clearSelection();

                        // Read new students file
                        readStudents(selectedFile.getPath());

                        JOptionPane.showMessageDialog(this,
                            "New students file loaded successfully!\n" +
                            "Loaded " + studentsTree.getSize() + " students.",
                            "File Loaded",
                            JOptionPane.INFORMATION_MESSAGE);

                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(this,
                            "Error loading students file: " + e.getMessage(),
                            "File Error",
                            JOptionPane.ERROR_MESSAGE);
                    }
                }
            }

            private void loadNewTennisPlayers() {
                javax.swing.JFileChooser fileChooser = new javax.swing.JFileChooser();
                fileChooser.setDialogTitle("Select New Tennis Players File");
                fileChooser.setCurrentDirectory(new java.io.File("src/Data/"));
                fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Text Files (*.txt)", "txt"));

                int result = fileChooser.showOpenDialog(this);
                if (result == javax.swing.JFileChooser.APPROVE_OPTION) {
                    try {
                        java.io.File selectedFile = fileChooser.getSelectedFile();

                        // Clear existing tennis player data
                        tennisPlayerList.clear();
                        tennisPlayersTreeMap.clear();
                        tennisPlayersUsedArrayList.clear();

                        // Read new tennis players file
                        readTennisPlayers(selectedFile.getPath());

                        // Reset quiz state
                        startQuiz();
                        populateCountriesComboBox();

                        // Update tooltip with new size
                        questionsTextField.setToolTipText("Enter number of questions (1-" + tennisPlayerList.size() + ")");

                        JOptionPane.showMessageDialog(this,
                            "New tennis players file loaded successfully!\n" +
                            "Loaded " + tennisPlayerList.size() + " players.",
                            "File Loaded",
                            JOptionPane.INFORMATION_MESSAGE);

                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(this,
                            "Error loading tennis players file: " + e.getMessage(),
                            "File Error",
                            JOptionPane.ERROR_MESSAGE);
                    }
                }
            }

            private void clearQuiz() {
                System.out.println("=== DEBUG: clearQuiz() STARTED ===");

                // Confirm clear action if quiz is in progress
                if (currentQuestion > 0) {
                    int confirmation = JOptionPane.showConfirmDialog(this,
                        "Are you sure you want to clear the current quiz?\n" +
                        "Progress will be lost and no score will be recorded.",
                        "Confirm Clear Quiz",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE);

                    if (confirmation != JOptionPane.YES_OPTION) {
                        return;
                    }
                }

                // Reset quiz state
                currentQuestion = 0;
                correctAnswers = 0;
                totalQuestions = 0;
                currentTennisPlayerName = null;

                // Reset UI components
                questionsTextField.setText("");
                questionsTextField.setEnabled(true);
                submitButton.setEnabled(false);
                nextButton.setEnabled(false);
                playAgainButton.setEnabled(false);
                resultJLabel.setText("");
                playerJLabel.setText("");
                countriesComboBox.setSelectedIndex(0);

                // Reset used players array
                for (int i = 0; i < tennisPlayersUsedArrayList.size(); i++) {
                    tennisPlayersUsedArrayList.set(i, false);
                }

                // Set default picture
                setDefaultPicture();

                // Enable student selection
                studentsJList.setEnabled(true);

                System.out.println("DEBUG: Quiz cleared successfully");
                System.out.println("=== DEBUG: clearQuiz() COMPLETED ===");
            }

            private void exitApplication() {
                System.out.println("=== DEBUG: exitApplication() STARTED ===");

                int confirmation = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to exit the application?",
                    "Confirm Exit",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);

                if (confirmation == JOptionPane.YES_OPTION) {
                    System.out.println("DEBUG: Application exiting...");

                    // Save any pending data
                    saveStudents("Temp2.txt");

                    // Exit the application
                    System.exit(0);
                }
                System.out.println("=== DEBUG: exitApplication() COMPLETED ===");
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
        String imageName = playerName.replaceAll("[^a-zA-Z0-9]", "");
        String imagePath = "src/Image/" + imageName + ".jpg";
        
        java.io.File imageFile = new java.io.File(imagePath);
        if (imageFile.exists()) {
            ImageIcon imageIcon = new ImageIcon(imagePath);
            
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
        String defaultImagePath = "src/Image/default.jpeg";
        java.io.File imageFile = new java.io.File(defaultImagePath);
        if (imageFile.exists()) {
            ImageIcon defaultIcon = new ImageIcon(defaultImagePath);
            Image image = defaultIcon.getImage();
            Image scaledImage = image.getScaledInstance(200, 200, Image.SCALE_SMOOTH);
            defaultIcon = new ImageIcon(scaledImage);
            
            MainMenuPicture.setIcon(defaultIcon);
            MainMenuPicture.setText("");
        }
    } catch (Exception e) {
        
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
            resultJLabel.setText("<html>Incorrect!<br>Correct answer: <br>" + correctCountry + "</html>");
        }
        
        submitButton.setEnabled(false);
        nextButton.setEnabled(true);
    }
    private void studentsJListValueChanged(javax.swing.event.ListSelectionEvent evt) {
    if (!evt.getValueIsAdjusting()) {
        String selectedName = studentsJList.getSelectedValue();
        if (selectedName != null) {
          selectedStudent = studentsTree.findStudentByName(selectedName);
            
        }
    }
    }
    
    private void printMenuItems()
    {
        printFormMenuItem.addActionListener(e -> printFormAsGUI());
        printFormMenuItem.setToolTipText("Print the main quiz form as GUI");
        printStudentMenuItem.addActionListener(e -> printStudentDetails());
        printStudentMenuItem.setToolTipText("Print detailed report of selected student");
    }
        private void printFormAsGUI() {
    try {
        PrintUtilities.printComponent(this); // 'this' refers to the main JFrame
        javax.swing.JOptionPane.showMessageDialog(this, 
            "Form printed successfully!", "Print Success", 
            javax.swing.JOptionPane.INFORMATION_MESSAGE);
    } catch (Exception e) {
        javax.swing.JOptionPane.showMessageDialog(this, 
            "Error printing form: " + e.getMessage(), "Print Error", 
            javax.swing.JOptionPane.ERROR_MESSAGE);
    }
}

        private void printStudentDetails() {
            if (selectedStudent == null) {
                javax.swing.JOptionPane.showMessageDialog(this, 
                    "Please select a student first.", "No Student Selected", 
                    javax.swing.JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                String studentDetails = formatStudentDetails(selectedStudent);
                PrintUtilities.printStudentDetails(studentDetails);

                javax.swing.JOptionPane.showMessageDialog(this, 
                    "Student details printed successfully!", "Print Success", 
                    javax.swing.JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                javax.swing.JOptionPane.showMessageDialog(this, 
                    "Error printing student details: " + e.getMessage(), "Print Error", 
                    javax.swing.JOptionPane.ERROR_MESSAGE);
            }
        }

    private String formatStudentDetails(Student student) 
    {
        StringBuilder sb = new StringBuilder();
        sb.append("STUDENT QUIZ PERFORMANCE REPORT\n");
        sb.append("===============================\n\n");
        sb.append("Personal Information:\n");
        sb.append("  Name: ").append(student.getName()).append("\n");
        sb.append("  Age: ").append(student.getAge()).append("\n\n");

        sb.append("Quiz Statistics:\n");
        sb.append("  Correct Answers: ").append(student.getCorrect()).append("\n");
        sb.append("  Total Questions: ").append(student.getTotalQuestions()).append("\n");
        sb.append("  Success Rate: ").append(String.format("%.1f", student.calculatePercent())).append("%\n\n");

        sb.append("Performance Analysis:\n");
        double percent = student.calculatePercent();
        if (percent >= 80) {
            sb.append("  Excellent! Keep up the great work!\n");
        } else if (percent >= 60) {
            sb.append("  Good performance! Room for improvement.\n");
        } else {
            sb.append("  Needs improvement. Practice more!\n");
        }

        sb.append("\n===============================\n");
        sb.append("Tennis Players Quiz System\n");
        sb.append("Report generated: ").append(new java.util.Date());

        return sb.toString();
    }
    
        private void addStudent() 
        {
        AddStudent dialog = new AddStudent(this, true, studentsTree);
        dialog.setVisible(true);

        if (dialog.isStudentAdded()) {
            
            saveStudents("Temp2.txt");
            refreshStudentsList();

            javax.swing.JOptionPane.showMessageDialog(this,
                "Student added successfully to the database!",
                "Success",
                javax.swing.JOptionPane.INFORMATION_MESSAGE);
        }
        }
    
         private void refreshStudentsList() 
            {
                DefaultListModel<String> model = new DefaultListModel<>();
                java.util.List<String> studentNames = studentsTree.getAllStudentNames();
                for (String name : studentNames) {
                    model.addElement(name);
                }
                studentsJList.setModel(model);
            }
        
         
         
         
    private void editStudent() {
    if (selectedStudent == null) {
        javax.swing.JOptionPane.showMessageDialog(this,
            "Please select a student from the list first.",
            "No Student Selected",
            javax.swing.JOptionPane.WARNING_MESSAGE);
        return;
    }

    // Store original name for selection after refresh
    String originalName = selectedStudent.getName();
    
    EditStudent dialog = new EditStudent(this, true, studentsTree, selectedStudent);
    dialog.setVisible(true);
    
    if (dialog.isStudentEdited()) {
        System.out.println("DEBUG: Student edited, saving file...");
        saveStudents("Temp2.txt");
        refreshStudentsList();
        try {
            Student updatedStudent = studentsTree.findStudentByName(originalName);
            if (updatedStudent == null) 
            {
                selectedStudent = null;
                studentsJList.clearSelection();
            } else {
                selectedStudent = updatedStudent;
            }
            } catch (Exception e) {
                System.out.println("DEBUG: Error selecting student: " + e.getMessage());
            }

            javax.swing.JOptionPane.showMessageDialog(this,
                "Student information updated successfully!",
                "Success",
                javax.swing.JOptionPane.INFORMATION_MESSAGE);
            } else {
                System.out.println("DEBUG: Student edit was cancelled");
            }
        }
        
       private void showStudentDetails() 
       {
            System.out.println("=== DEBUG: showStudentDetails() STARTED ===");

            if (selectedStudent == null) {
                JOptionPane.showMessageDialog(this,
                    "Please select a student from the list first.",
                    "No Student Selected",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }

            System.out.println("DEBUG: Showing details for: " + selectedStudent.getName());

            try {
                StudentDetails dialog = new StudentDetails(this, true, selectedStudent);
                dialog.setVisible(true);
                System.out.println("DEBUG: StudentDetails dialog closed successfully");
            } catch (Exception e) {
                System.out.println("DEBUG: Error in StudentDetails: " + e.getMessage());
                e.printStackTrace();
                JOptionPane.showMessageDialog(this,
                    "Error displaying student details: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
            
       private void selectStudentInList(String studentName) 
       {
        DefaultListModel<String> model = (DefaultListModel<String>) studentsJList.getModel();

        // Find the student in the list and select it
        for (int i = 0; i < model.getSize(); i++) {
            if (model.getElementAt(i).equals(studentName)) {
                studentsJList.setSelectedIndex(i);
                studentsJList.ensureIndexIsVisible(i); // Scroll to make it visible
                break;
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
        newMenu = new javax.swing.JMenuItem();
        clearMenu = new javax.swing.JMenuItem();
        printFormMenuItem = new javax.swing.JMenuItem();
        printStudentMenuItem = new javax.swing.JMenuItem();
        exitMenu = new javax.swing.JMenuItem();
        studentDatabaseMenu = new javax.swing.JMenu();
        addStudentMenu = new javax.swing.JMenuItem();
        editStudentMenu = new javax.swing.JMenuItem();
        deleteStudentMenu = new javax.swing.JMenuItem();
        searchStudentMenu = new javax.swing.JMenuItem();
        studentDetailsMenu = new javax.swing.JMenuItem();
        tennisPlayersDetailsMenu = new javax.swing.JMenuItem();
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

        newMenu.setText("New");
        fileMenu.add(newMenu);

        clearMenu.setText("Clear");
        fileMenu.add(clearMenu);

        printFormMenuItem.setText("Print Form");
        fileMenu.add(printFormMenuItem);

        printStudentMenuItem.setText("Print Student Details");
        fileMenu.add(printStudentMenuItem);

        exitMenu.setText("Exit");
        fileMenu.add(exitMenu);

        topMenuBar.add(fileMenu);

        studentDatabaseMenu.setText("Student Database");

        addStudentMenu.setText("Add");
        studentDatabaseMenu.add(addStudentMenu);

        editStudentMenu.setText("Edit");
        studentDatabaseMenu.add(editStudentMenu);

        deleteStudentMenu.setText("Delete");
        studentDatabaseMenu.add(deleteStudentMenu);

        searchStudentMenu.setText("Search");
        studentDatabaseMenu.add(searchStudentMenu);

        studentDetailsMenu.setText("Student Details");
        studentDatabaseMenu.add(studentDetailsMenu);

        tennisPlayersDetailsMenu.setText("Tennis Player Details");
        studentDatabaseMenu.add(tennisPlayersDetailsMenu);

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
    private javax.swing.JMenuItem addStudentMenu;
    private javax.swing.JMenuItem clearMenu;
    private javax.swing.JComboBox<String> countriesComboBox;
    private javax.swing.JMenuItem deleteStudentMenu;
    private javax.swing.JMenuItem editStudentMenu;
    private javax.swing.JMenuItem exitMenu;
    private javax.swing.JMenu fileMenu;
    private javax.swing.JMenu helpMenu;
    private javax.swing.JMenuItem newMenu;
    private javax.swing.JButton nextButton;
    private javax.swing.JButton playAgainButton;
    private javax.swing.JLabel playerJLabel;
    private javax.swing.JMenuItem printFormMenuItem;
    private javax.swing.JMenuItem printStudentMenuItem;
    private javax.swing.JLabel questionsLabel;
    private javax.swing.JTextField questionsTextField;
    private javax.swing.JLabel resultJLabel;
    private javax.swing.JMenuItem searchStudentMenu;
    private javax.swing.JLabel selectCountryLabel;
    private javax.swing.JMenu studentDatabaseMenu;
    private javax.swing.JMenuItem studentDetailsMenu;
    private javax.swing.JScrollPane studentList;
    private javax.swing.JList<String> studentsJList;
    private javax.swing.JButton submitButton;
    private javax.swing.JMenuItem tennisPlayersDetailsMenu;
    private javax.swing.JMenuBar topMenuBar;
    // End of variables declaration//GEN-END:variables
}
