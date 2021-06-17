package domain;

import java.util.List;

import domain.exceptions.EnrollmentRulesViolationException;

public class EnrollCtrl {
	public void enroll(Student s, List<CSE> courses) throws EnrollmentRulesViolationException {
        checkForAlreadyPassedCourses(s, courses);
        checkForPrerequisitesPassedCourses(s, courses);
        checkForExamTimeConflict(s, courses);
        checkForAlreadyTakenCourses(s, courses);
        checkForGPAAndNumberOfUnits(s, courses);
		for (CSE o : courses)
			s.takeCourse(o.getCourse(), o.getSection());
	}

    private void checkForGPAAndNumberOfUnits(Student s, List<CSE> courses) throws EnrollmentRulesViolationException {
        if (
                (s.getGpa() < 12 && getUnitsRequested(courses) > 14) ||
                (s.getGpa() < 16 && getUnitsRequested(courses) > 16) ||
                (getUnitsRequested(courses) > 20)
        )
            throw new EnrollmentRulesViolationException(String.format("Number of units (%d) requested does not match GPA of %f", getUnitsRequested(courses), s.getGpa()));
    }

    private void checkForAlreadyTakenCourses(Student s, List<CSE> courses) throws EnrollmentRulesViolationException {
        for (CSE o : courses) {
            for (CSE o2 : courses) {
                if (o == o2)
                    continue;
                if (o.getCourse().equals(o2.getCourse()))
                    throw new EnrollmentRulesViolationException(String.format("%s is requested to be taken twice", o.getCourse().getName()));
            }
        }
    }

    private void checkForExamTimeConflict(Student s, List<CSE> courses) throws EnrollmentRulesViolationException {
        for (CSE o : courses) {
            for (CSE o2 : courses) {
                if (o == o2)
                    continue;
                if (o.getExamTime().equals(o2.getExamTime()))
                    throw new EnrollmentRulesViolationException(String.format("Two offerings %s and %s have the same exam time", o, o2));
            }
        }
    }

    private void checkForPrerequisitesPassedCourses(Student s, List<CSE> courses) throws EnrollmentRulesViolationException {
        for (CSE o : courses) {
            List<Course> prereqs = o.getCourse().getPrerequisites();
            for (Course pre : prereqs) {
                if (!s.hasPassedCourse(pre)) {
                    throw new EnrollmentRulesViolationException(String.format("The student has not passed %s as a prerequisite of %s", pre.getName(), o.getCourse().getName()));
                }
            }
        }
    }

    private void checkForAlreadyPassedCourses(Student s, List<CSE> courses) throws EnrollmentRulesViolationException {
        for (CSE o : courses) {
            if (s.hasPassedCourse(o.getCourse())) {
                throw new EnrollmentRulesViolationException(String.format("The student has already passed %s", o.getCourse().getName()));
            }
        }
    }

    private int getUnitsRequested(List<CSE> courses) {
        int unitsRequested = 0;
        for (CSE o : courses)
            unitsRequested += o.getCourse().getUnits();
        return unitsRequested;
    }

}
