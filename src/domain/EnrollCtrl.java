package domain;

import java.util.List;
import java.util.Map;

import domain.exceptions.EnrollmentRulesViolationException;

public class EnrollCtrl {
	public void enroll(Student s, List<CourseSection> courses) throws EnrollmentRulesViolationException {
        Map<Term, Map<Course, Double>> transcript = s.getTranscript();
        this.checkAlreadyPassedCourses(courses, transcript);
        this.checkPrerequisitesArePassed(courses, transcript);
		this.checkExamDateConfliction(courses);
		this.checkMaximumUnitLimitations(courses, s);
		for (CourseSection o : courses)
			s.takeCourse(o);
	}

	public void checkAlreadyPassedCourses(List<CourseSection> courses, Map<Term, Map<Course, Double>> transcript)
            throws EnrollmentRulesViolationException {
        for (CourseSection o : courses) {
            for (Map.Entry<Term, Map<Course, Double>> tr : transcript.entrySet()) {
                for (Map.Entry<Course, Double> r : tr.getValue().entrySet()) {
                    if (r.getKey().equals(o.getCourse()) && r.getValue() >= 10)
                        throw new EnrollmentRulesViolationException(String.format("The student has already passed %s", o.getCourse().getName()));
                }
            }
        }
    }

    public void checkPrerequisitesArePassed(List<CourseSection> courses, Map<Term, Map<Course, Double>> transcript)
            throws EnrollmentRulesViolationException {
        for (CourseSection o : courses) {
            List<Course> prereqs = o.getCourse().getPrerequisites();
            nextPre:
            for (Course pre : prereqs) {
                for (Map.Entry<Term, Map<Course, Double>> tr : transcript.entrySet()) {
                    for (Map.Entry<Course, Double> r : tr.getValue().entrySet()) {
                        if (r.getKey().equals(pre) && r.getValue() >= 10)
                            continue nextPre;
                    }
                }
                throw new EnrollmentRulesViolationException(String.format("The student has not passed %s as a prerequisite of %s", pre.getName(), o.getCourse().getName()));
            }
        }
    }

    public void checkExamDateConfliction(List<CourseSection> courses) throws EnrollmentRulesViolationException {
        for (CourseSection o : courses) {
            for (CourseSection o2 : courses) {
                if (o == o2)
                    continue;
                if (o.getExamDate().equals(o2.getExamDate()))
                    throw new EnrollmentRulesViolationException(String.format("Two offerings %s and %s have the same exam time", o, o2));
                if (o.getCourse().equals(o2.getCourse()))
                    throw new EnrollmentRulesViolationException(String.format("%s is requested to be taken twice", o.getCourse().getName()));
            }
        }
    }

    public void checkMaximumUnitLimitations(List<CourseSection> courses, Student student)
            throws EnrollmentRulesViolationException {
        int unitsRequested = 0;
        for (CourseSection o : courses)
            unitsRequested += o.getCourse().getUnits();
        double gpa = student.getLastTermGPA();
        if ((gpa < 12 && unitsRequested > 14) ||
                (gpa < 16 && unitsRequested > 16) ||
                (unitsRequested > 20))
            throw new EnrollmentRulesViolationException(String.format("Number of units (%d) requested does not match GPA of %f", unitsRequested, gpa));
    }
}
