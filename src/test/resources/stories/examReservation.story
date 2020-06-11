Narrative:
Stories to test ExamReservations application


Scenario: Initial state of student and exam list
Given The Database contains few students and few exams
And The Exam Reservation View is shown
When The user selects an Exam
Then Exam list contains exams info
And Student list contains students info
And Reservation list contains reservations info

Scenario: Add exam
Given The Database contains few students and few exams
And The Exam Reservation View is shown
And The user enter exam name
When The user clicks AddExam button
Then Exam list contains new exam info

Scenario: Add student
Given The Database contains few students and few exams
And The Exam Reservation View is shown
And The user enter student name and surname
When The user clicks AddStudent button
Then Student list contains new student info

Scenario: Add reservation
Given The Database contains few students and few exams
And The Exam Reservation View is shown
And The user selects an Exam
And The user selects a Student
When The user clicks AddReservation button
Then Reservation list contains selected student info

Scenario: Delete student
Given The Database contains few students and few exams
And The Exam Reservation View is shown
And The user selects a Student
When The user clicks DeleteStudent button
Then Student list does not contain selected student info

Scenario: Delete exam
Given The Database contains few students and few exams
And The Exam Reservation View is shown
And The user selects an Exam
When The user clicks DeleteExam button
Then Exam list does not contain selected exam info

Scenario: Delete reservation
Given The Database contains few students and few exams
And The Exam Reservation View is shown
And The user selects an Exam
And The user selects a Reservation
When The user clicks DeleteReservation button
Then Reservation list does not contain selected reservation info

Scenario: Add reservation that exists
Given The Database contains few students and few exams
And The Exam Reservation View is shown
And The user selects an Exam
And The user selects Student that is also in reservations list
When The user clicks AddReservation button
Then Error student already present is shown