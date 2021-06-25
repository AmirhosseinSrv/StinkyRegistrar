package domain;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class EnrollCtrl {
    private final ViolationException ve = new ViolationException();

    public String getErrors() {
        return ve.toString();
    }

	public boolean enroll(Student s, List<CSE> courses) {
	    boolean hadError;
        hadError = isAlreadyPassed(s, courses);
        hadError |= !isPrerequisitesPassed(s, courses);
        hadError |= isExamTimeConflict(s, courses);
        hadError |= isDuplicateCourse(s, courses);
        hadError |= !isGPAandUnitsCorrect(s, courses);
        if (hadError)
            return false;
		for (CSE o : courses)
			s.takeCourse(o.getCourse(), o.getSection());
		return true;
	}

    private boolean isGPAandUnitsCorrect(Student s, List<CSE> courses) {
	    int unitsRequested = getUnitsRequested(courses);
        double gpa = s.getGpa();
        if (
                (gpa < 12 && unitsRequested > 14) ||
                (gpa < 16 && unitsRequested > 16) ||
                (unitsRequested > 20)
        ) {
            String message = String.format("Number of units (%d) requested does not match GPA of %.2f", unitsRequested, gpa);
            ve.addError(message);
            return false;
        }
        return true;
    }

    private boolean isDuplicateCourse(Student s, List<CSE> courses) {
	    boolean errorHappened = false;
        Set<String> duplicates = new HashSet<>();
        for (CSE o : courses) {
            for (CSE o2 : courses) {
                if (o == o2)
                    continue;
                if (o.getCourse().equals(o2.getCourse()) && !duplicates.contains(o.getCourse().getName())) {
                    String message = String.format("%s is requested to be taken twice", o.getCourse().getName());
                    ve.addError(message);
                    errorHappened = true;
                    duplicates.add(o.getCourse().getName());
                }
            }
        }
        return errorHappened;
    }

    private boolean isExamTimeConflict(Student s, List<CSE> courses) {
	    boolean conflictHappened = false;
        for (CSE o : courses) {
            for (CSE o2 : courses) {
                if (o == o2)
                    continue;
                if (o.getExamTime().equals(o2.getExamTime())) {
                    String message = String.format("Two offerings %s and %s have the same exam time", o, o2);
                    ve.addError(message);
                    conflictHappened = true;
                }
            }
        }
        return conflictHappened;
    }

    private boolean isPrerequisitesPassed(Student s, List<CSE> courses) {
	    boolean passed = true;
        for (CSE o : courses) {
            List<Course> prereqs = o.getCourse().getPrerequisites();
            for (Course pre : prereqs) {
                if (!s.hasPassedCourse(pre)) {
                    String message = String.format("The student has not passed %s as a prerequisite of %s", pre.getName(), o.getCourse().getName());
                    ve.addError(message);
                    passed = false;
                }
            }
        }
        return passed;
    }

    private boolean isAlreadyPassed(Student s, List<CSE> courses) {
	    boolean passed = false;
        for (CSE o : courses) {
            if (s.hasPassedCourse(o.getCourse())) {
                String message = String.format("The student has already passed %s", o.getCourse().getName());
                ve.addError(message);
                passed = true;
            }
        }
        return passed;
    }

    private int getUnitsRequested(List<CSE> courses) {
        int unitsRequested = 0;
        for (CSE o : courses)
            unitsRequested += o.getCourse().getUnits();
        return unitsRequested;
    }

}
