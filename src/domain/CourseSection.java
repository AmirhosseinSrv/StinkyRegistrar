package domain;

import java.util.Date;

public class CourseSection {
    private Course course;
    private int section;
    private Date examDate;

    CourseSection(Course course, int section, Date examDate) {
        this.course = course;
        this.section = section;
        this.examDate = examDate;
    }

    public Course getCourse() {
        return course;
    }

    public int getSection() {
        return section;
    }

    public Date getExamDate() {
        return examDate;
    }
    public String toString() {
        return course.getName() + " - " + section;
    }

}
