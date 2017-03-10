package model;

import java.io.Serializable;
import java.util.Collection;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

@Entity
@Table(name = "student")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Student.findAll", query = "SELECT s FROM Student s")
    , @NamedQuery(name = "Student.findByStudentId", query = "SELECT s FROM Student s WHERE s.studentId = :studentId")
    , @NamedQuery(name = "Student.findByStudentFio", query = "SELECT s FROM Student s WHERE s.studentFio = :studentFio")
    , @NamedQuery(name = "Student.findByStudentSpeciality", query = "SELECT s FROM Student s WHERE s.studentSpeciality = :studentSpeciality")
    , @NamedQuery(name = "Student.findByStudentCourse", query = "SELECT s FROM Student s WHERE s.studentCourse = :studentCourse")})
public class Student implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "student_id")
    private Integer studentId;
    @Basic(optional = false)
    @Column(name = "student_fio")
    private String studentFio;
    @Basic(optional = false)
    @Column(name = "student_speciality")
    private String studentSpeciality;
    @Basic(optional = false)
    @Column(name = "student_course")
    private Integer studentCourse;

    public Student() {}

    public Student(Integer studentId) {
        this.studentId = studentId;
    }

    public Student(Integer studentId, String studentFio, String studentSpeciality, Integer studentCourse) {
        this.studentId = studentId;
        this.studentFio = studentFio;
        this.studentSpeciality = studentSpeciality;
        this.studentCourse = studentCourse;
    }

    public Integer getStudentId() {
        return studentId;
    }

    public void setStudentId(Integer studentId) {
        this.studentId = studentId;
    }

    public String getStudentFio() {
        return studentFio;
    }

    public void setStudentFio(String studentFio) {
        this.studentFio = studentFio;
    }

    public String getStudentSpeciality() {
        return studentSpeciality;
    }

    public void setStudentSpeciality(String studentSpeciality) {
        this.studentSpeciality = studentSpeciality;
    }

    public int getStudentCourse() {
        return studentCourse;
    }

    public void setStudentCourse(Integer studentCourse) {
        this.studentCourse = studentCourse;
    }

    @Override
    public String toString() {
        return "model.Student[ studentId=" + studentId + " ]";
    }    
}
