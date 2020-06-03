package com.nuti.puccia.view.swing;

import com.nuti.puccia.controller.Controller;
import com.nuti.puccia.model.Exam;
import com.nuti.puccia.model.Student;
import com.nuti.puccia.view.ExamReservationsView;

import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;

public class ExamReservationsSwingView extends JFrame implements ExamReservationsView {
    private final DefaultListModel<Exam> examModel = new DefaultListModel<>();
    private final DefaultListModel<Student> reservationModel = new DefaultListModel<>();
    private final DefaultListModel<Student> studentModel = new DefaultListModel<>();
    private JPanel formPanel;
    private JList<Exam> examsList;
    private JList<Student> reservationsList;
    private JList<Student> studentsList;
    private JButton removeExamButton;
    private JButton removeStudentButton;
    private JTextField studentName;
    private JTextField studentSurname;
    private JButton addStudentButton;
    private JTextField examName;
    private JButton addExamButton;
    private JButton addReservationButton;
    private JButton removeReservationButton;
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

        removeExamButton.setEnabled(false);
        removeReservationButton.setEnabled(false);
        removeStudentButton.setEnabled(false);

        // Enabling add exam button when insert some text into exam name text field
        examName.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                addExamButton.setEnabled(
                        !examName.getText().trim().isEmpty()
                );
            }
        });

        // Enabling add student button when insert some text into student name and surname text field
        KeyAdapter buttonAddStudentEnabler = new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                addStudentButton.setEnabled(
                        !studentName.getText().trim().isEmpty() && !studentSurname.getText().trim().isEmpty()
                );
            }
        };
        studentName.addKeyListener(buttonAddStudentEnabler);
        studentSurname.addKeyListener(buttonAddStudentEnabler);

        ListSelectionListener buttonAddReservationEnabler = listSelectionEvent -> {
            if (examsList.getSelectedIndex() != -1 &&
                    studentsList.getSelectedIndex() != -1) {
                addReservationButton.setEnabled(true);
                reservationLabel.setText(studentsList.getSelectedValue().toString());
            } else
                reservationLabel.setText("Select a student to add a reservation");

        };

        // Enabling add reservation button and change reservation label when a student and an exam are selected
        examsList.addListSelectionListener(buttonAddReservationEnabler);

        // Enabling add reservation button when a student and an exam are selected
        studentsList.addListSelectionListener(buttonAddReservationEnabler);

        // Show reservation for an exam
        examsList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                reservationModel.clear();
                if (examsList.getSelectedIndex() != -1)
                    examsList.getSelectedValue().getStudents().forEach(reservationModel::addElement);
            }
        });

        // Enabling delete exam button when an exam is selected
        examsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        examsList.addListSelectionListener(
                e -> removeExamButton.setEnabled(examsList.getSelectedIndex() != -1));

        // Enabling delete student button when a student is selected
        studentsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        studentsList.addListSelectionListener(
                e -> removeStudentButton.setEnabled(studentsList.getSelectedIndex() != -1));


        // Enabling delete reservation button when a reservation is selected
        reservationsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        reservationsList.addListSelectionListener(
                e -> removeReservationButton.setEnabled(reservationsList.getSelectedIndex() != -1));


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

    }

    public static void main(String[] args) {
        new ExamReservationsSwingView();
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

    @Override
    public void updateStudents(List<Student> students) {
    }

    @Override
    public void updateExams(List<Exam> exams) {
    }

    @Override
    public void updateReservations() {

    }

    @Override
    public void showError(String message) {

    }
}
