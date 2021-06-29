package domain;

import java.util.List;

import domain.exceptions.EnrollmentRulesViolationException;

public class EnrollCtrl {
	public void enroll(Student student, List<CourseSection> courses) throws EnrollmentRulesViolationException {
        this.checkAlreadyPassedCourses(courses, student);
        this.checkPrerequisitesArePassed(courses, student);
		this.checkExamDateConfliction(courses);
		this.checkDuplicateCourseEnrollment(courses);
		this.checkMaximumUnitLimitations(courses, student);
		for (CourseSection course : courses)
			student.takeCourse(course);
	}

	public void checkAlreadyPassedCourses(List<CourseSection> courses, Student student)
            throws EnrollmentRulesViolationException {
        for (CourseSection course : courses) {
            if (student.checkHasPassedCourse(course.getCourse())) {
                throw new EnrollmentRulesViolationException(
                        String.format("The student has already passed %s", course.getCourse().getName())
                );
            }
        }
    }

    public void checkPrerequisitesArePassed(List<CourseSection> courses, Student student)
            throws EnrollmentRulesViolationException {
        for (CourseSection o : courses) {
            List<Course> prerequisites = o.getCourse().getPrerequisites();
            for (Course prereq : prerequisites) {
                if (!student.checkHasPassedCourse(prereq)) {
                    throw new EnrollmentRulesViolationException(
                            String.format(
                                    "The student has not passed %s as a prerequisite of %s",
                                    prereq.getName(),
                                    o.getCourse().getName()
                            ));
                }
            }
        }
    }

    public void checkExamDateConfliction(List<CourseSection> courses) throws EnrollmentRulesViolationException {
        for (CourseSection course1 : courses) {
            for (CourseSection course2 : courses) {
                if (course1 == course2)
                    continue;
                if (course1.getExamDate().equals(course2.getExamDate()))
                    throw new EnrollmentRulesViolationException(String.format("Two offerings %s and %s have the same exam time", course1, course2));
            }
        }
    }

    public void checkDuplicateCourseEnrollment(List<CourseSection> courses) throws EnrollmentRulesViolationException {
        for (CourseSection course1 : courses) {
            for (CourseSection course2 : courses) {
                if (course1 == course2)
                    continue;
                if (course1.getCourse().equals(course2.getCourse()))
                    throw new EnrollmentRulesViolationException(String.format("%s is requested to be taken twice", course1.getCourse().getName()));
            }
        }
    }

    public void checkMaximumUnitLimitations(List<CourseSection> courses, Student student)
            throws EnrollmentRulesViolationException {
        int unitsRequested = 0;
        for (CourseSection course : courses)
            unitsRequested += course.getCourse().getUnits();
        double gpa = student.calculateGPA();
        if ((gpa < 12 && unitsRequested > 14) ||
                (gpa < 16 && unitsRequested > 16) ||
                (unitsRequested > 20))
            throw new EnrollmentRulesViolationException(String.format("Number of units (%d) requested does not match GPA of %f", unitsRequested, gpa));
    }
}
