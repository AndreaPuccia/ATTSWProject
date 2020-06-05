package com.nuti.puccia.view.swing;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.nuti.puccia.controller.Controller;
import com.nuti.puccia.model.Exam;
import com.nuti.puccia.model.Student;
import com.nuti.puccia.view.ExamReservationsView;

import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class ExamReservationsSwingView extends JFrame implements ExamReservationsView {
    private final DefaultListModel<Exam> examModel = new DefaultListModel<>();
    private final DefaultListModel<Student> reservationModel = new DefaultListModel<>();
    private final DefaultListModel<Student> studentModel = new DefaultListModel<>();
    private JPanel formPanel;
    private JList<Exam> examsList;
    private JList<Student> reservationsList;
    private JList<Student> studentsList;
    private JButton deleteExamButton;
    private JButton deleteStudentButton;
    private JTextField studentName;
    private JTextField studentSurname;
    private JButton addStudentButton;
    private JTextField examName;
    private JButton addExamButton;
    private JButton addReservationButton;
    private JButton deleteReservationButton;
    private JLabel reservationLabel;
    private JLabel errorLabel;
    private Controller controller;

    public ExamReservationsSwingView() {
        setTitle("Exam Reservations");
        setContentPane(formPanel);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setVisible(true);

        addExamButton.setEnabled(false);
        addReservationButton.setEnabled(false);
        addStudentButton.setEnabled(false);

        deleteExamButton.setEnabled(false);
        deleteReservationButton.setEnabled(false);
        deleteStudentButton.setEnabled(false);

        // Enabling add exam button when insert some text into exam name text field
        examName.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                addExamButton.setEnabled(!examName.getText().trim().isEmpty());
            }
        });

        // Enabling add student button when insert some text into student name and surname text field
        KeyAdapter buttonAddStudentEnabler = new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                addStudentButton.setEnabled(
                        !studentName.getText().trim().isEmpty() && !studentSurname.getText().trim().isEmpty());
            }
        };
        studentName.addKeyListener(buttonAddStudentEnabler);
        studentSurname.addKeyListener(buttonAddStudentEnabler);

        ListSelectionListener buttonAddReservationEnabler = listSelectionEvent -> {
            if (examsList.getSelectedIndex() != -1 && studentsList.getSelectedIndex() != -1) {
                addReservationButton.setEnabled(true);
                reservationLabel.setText(studentsList.getSelectedValue().toString());
            } else
                reservationLabel.setText("Select a student to add");
        };

        // Enabling add reservation button and change reservation label when a student and an exam are selected
        examsList.addListSelectionListener(buttonAddReservationEnabler);

        // Enabling add reservation button when a student and an exam are selected
        studentsList.addListSelectionListener(buttonAddReservationEnabler);

        // Show reservation for an exam
        examsList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting())
                updateReservations();
        });

        // Enabling delete exam button when an exam is selected
        examsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        examsList.addListSelectionListener(e -> deleteExamButton.setEnabled(examsList.getSelectedIndex() != -1));

        // Enabling delete student button when a student is selected
        studentsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        studentsList.addListSelectionListener(e ->
                deleteStudentButton.setEnabled(studentsList.getSelectedIndex() != -1));


        // Enabling delete reservation button when a reservation is selected
        reservationsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        reservationsList.addListSelectionListener(e ->
                deleteReservationButton.setEnabled(reservationsList.getSelectedIndex() != -1));


        DefaultListCellRenderer cellRender = new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                          boolean isSelected, boolean cellHasFocus) {
                return super.getListCellRendererComponent(list, value.toString(), index, isSelected, cellHasFocus);
            }
        };
        examsList.setModel(examModel);
        examsList.setCellRenderer(cellRender);

        studentsList.setModel(studentModel);
        studentsList.setCellRenderer(cellRender);

        reservationsList.setModel(reservationModel);
        reservationsList.setCellRenderer(cellRender);

        // Click listeners
        addExamButton.addActionListener(e -> controller.addExam(new Exam(examName.getText(), new ArrayList<>())));

        addStudentButton.addActionListener(e ->
                controller.addStudent(new Student(studentName.getText(), studentSurname.getText())));

        addReservationButton.addActionListener(e ->
                controller.addReservation(examsList.getSelectedValue(), studentsList.getSelectedValue()));

        deleteExamButton.addActionListener(e -> controller.deleteExam(examsList.getSelectedValue()));

        deleteStudentButton.addActionListener(e -> controller.deleteStudent(studentsList.getSelectedValue()));

        deleteReservationButton.addActionListener(e ->
                controller.deleteReservation(examsList.getSelectedValue(), reservationsList.getSelectedValue()));
    }

    public void setController(Controller controller) {
        this.controller = controller;
    }

    public DefaultListModel<Exam> getExamModel() {
        return examModel;
    }

    public DefaultListModel<Student> getStudentModel() {
        return studentModel;
    }

    public DefaultListModel<Student> getReservationModel() {
        return reservationModel;
    }

    public JLabel getErrorLabel() {
        return errorLabel;
    }

    @Override
    public void updateStudents(List<Student> students) {
        studentModel.clear();
        students.forEach(studentModel::addElement);
        errorLabel.setText("");
    }

    @Override
    public void updateExams(List<Exam> exams) {
        examModel.clear();
        exams.forEach(examModel::addElement);
        errorLabel.setText("");
    }

    @Override
    public void updateReservations() {
        reservationModel.clear();
        if (examsList.getSelectedIndex() != -1)
            examsList.getSelectedValue().getStudents().forEach(reservationModel::addElement);
        errorLabel.setText("");
    }

    @Override
    public void showError(String message) {
        errorLabel.setText(message);
    }


    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        formPanel = new JPanel();
        formPanel.setLayout(new GridLayoutManager(4, 1, new Insets(10, 10, 20, 10), -1, -1));
        formPanel.setMinimumSize(new Dimension(400, 315));
        formPanel.setPreferredSize(new Dimension(700, 500));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(2, 3, new Insets(0, 0, 0, 0), -1, -1, true, false));
        formPanel.add(panel1, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final JScrollPane scrollPane1 = new JScrollPane();
        panel1.add(scrollPane1, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        examsList = new JList();
        examsList.setName("ExamList");
        examsList.setSelectionMode(0);
        scrollPane1.setViewportView(examsList);
        final JScrollPane scrollPane2 = new JScrollPane();
        panel1.add(scrollPane2, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        reservationsList = new JList();
        reservationsList.setName("ReservationList");
        reservationsList.setSelectionMode(0);
        scrollPane2.setViewportView(reservationsList);
        final JScrollPane scrollPane3 = new JScrollPane();
        panel1.add(scrollPane3, new GridConstraints(1, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        studentsList = new JList();
        studentsList.setName("StudentList");
        studentsList.setSelectionMode(0);
        scrollPane3.setViewportView(studentsList);
        final JLabel label1 = new JLabel();
        label1.setText("Exams");
        panel1.add(label1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label2 = new JLabel();
        label2.setText("Reservations");
        panel1.add(label2, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label3 = new JLabel();
        label3.setText("Students");
        panel1.add(label3, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(1, 3, new Insets(10, 0, 10, 0), -1, -1, true, false));
        formPanel.add(panel2, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_NORTH, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        deleteExamButton = new JButton();
        deleteExamButton.setName("DeleteExam");
        deleteExamButton.setText("Remove");
        panel2.add(deleteExamButton, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        deleteStudentButton = new JButton();
        deleteStudentButton.setName("DeleteStudent");
        deleteStudentButton.setText("Remove");
        panel2.add(deleteStudentButton, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        deleteReservationButton = new JButton();
        deleteReservationButton.setName("DeleteReservation");
        deleteReservationButton.setText("Remove");
        panel2.add(deleteReservationButton, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new GridLayoutManager(1, 3, new Insets(10, 0, 10, 0), -1, -1, true, false));
        formPanel.add(panel3, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel4 = new JPanel();
        panel4.setLayout(new GridLayoutManager(2, 2, new Insets(0, 0, 0, 0), -1, -1));
        panel3.add(panel4, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_SOUTH, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        addExamButton = new JButton();
        addExamButton.setName("AddExam");
        addExamButton.setText("Add");
        panel4.add(addExamButton, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        examName = new JTextField();
        examName.setName("ExamNameText");
        examName.setText("");
        panel4.add(examName, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JLabel label4 = new JLabel();
        label4.setText("Exam");
        label4.setVerticalAlignment(0);
        panel4.add(label4, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel5 = new JPanel();
        panel5.setLayout(new GridLayoutManager(4, 2, new Insets(0, 0, 0, 0), -1, -1));
        panel3.add(panel5, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_SOUTH, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        studentSurname = new JTextField();
        studentSurname.setName("StudentSurnameText");
        studentSurname.setText("");
        panel5.add(studentSurname, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JLabel label5 = new JLabel();
        label5.setText("Name");
        panel5.add(label5, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        studentName = new JTextField();
        studentName.setName("StudentNameText");
        studentName.setText("");
        panel5.add(studentName, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JLabel label6 = new JLabel();
        label6.setText("Surname");
        panel5.add(label6, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        addStudentButton = new JButton();
        addStudentButton.setName("AddStudent");
        addStudentButton.setText("Add");
        panel5.add(addStudentButton, new GridConstraints(3, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel6 = new JPanel();
        panel6.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        panel3.add(panel6, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_SOUTH, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        addReservationButton = new JButton();
        addReservationButton.setName("AddReservation");
        addReservationButton.setText("Add");
        panel6.add(addReservationButton, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        reservationLabel = new JLabel();
        reservationLabel.setName("ReservationLabel");
        reservationLabel.setText("Select a student to add");
        panel6.add(reservationLabel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        errorLabel = new JLabel();
        Font errorLabelFont = this.$$$getFont$$$(null, Font.BOLD, 14, errorLabel.getFont());
        if (errorLabelFont != null) errorLabel.setFont(errorLabelFont);
        errorLabel.setForeground(new Color(-6348277));
        errorLabel.setName("ErrorLabel");
        errorLabel.setText("");
        formPanel.add(errorLabel, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    private Font $$$getFont$$$(String fontName, int style, int size, Font currentFont) {
        if (currentFont == null) return null;
        String resultName;
        if (fontName == null) {
            resultName = currentFont.getName();
        } else {
            Font testFont = new Font(fontName, Font.PLAIN, 10);
            if (testFont.canDisplay('a') && testFont.canDisplay('1')) {
                resultName = fontName;
            } else {
                resultName = currentFont.getName();
            }
        }
        return new Font(resultName, style >= 0 ? style : currentFont.getStyle(), size >= 0 ? size : currentFont.getSize());
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return formPanel;
    }

}
