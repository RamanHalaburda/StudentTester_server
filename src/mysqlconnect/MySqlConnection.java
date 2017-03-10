
package mysqlconnect;

import java.sql.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.DriverManager;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableModel;
import model.Student;
import org.eclipse.persistence.jpa.jpql.parser.DateTime;

public class MySqlConnection 
{    
    public static Connection conn;
    public static String query;
    public static Statement stmt;
    public static ResultSet rs;
    
    public void ConnectToDB() 
    {
        if(conn != null) return;
        try 
        {
            String url = "jdbc:mysql://localhost/TesterDB";
            String user = "root";
            String pass = "1656";
            conn = (Connection) DriverManager.getConnection(url, user, pass);
            //conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/TesterDB?user=root&password=1656");
            // jdbc:mysql://localhost:3306/TesterDB?zeroDateTimeBehavior=convertToNull
        } 
        catch (SQLException sqlE) { System.err.println(sqlE); }
    }
        
    public Student findStudent(Student _student)
    {
        Statement stmt = null;
        try 
        { 
            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT * FROM TesterDB.student where student.student_id = '" 
                    + _student.getStudentId().toString() + "' and student.student_fio = '" 
                    + _student.getStudentFio() + "';");
            rs.next();
            _student.setStudentSpeciality(rs.getObject(3).toString());
            _student.setStudentCourse(Integer.parseInt(rs.getObject(4).toString()));
            return _student; 
        } catch (SQLException sqlE) { System.out.println(sqlE); }
        return _student;
    }
    
    public int findTest(Student _student)
    {
        Statement stmt = null;
        int id_test = 0;
        try 
        { 
            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT * FROM TesterDB.test where test.test_speciality = '" 
                    + _student.getStudentSpeciality() + "' and test.test_course = '" 
                    + _student.getStudentCourse() + "';");
            rs.next();
            id_test = Integer.parseInt(rs.getObject(1).toString());
            return id_test; 
        } catch (SQLException sqlE) { System.out.println(sqlE); return id_test;}
    }
    
    public ResultSet findQuestionList(int _id_test)
    {
        Statement stmt = null;
        try 
        { 
            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT * FROM TesterDB.question where question.question_test_id = '" 
                    + _id_test + "';");
            return rs; 
        } catch (SQLException sqlE) { System.out.println(sqlE); return null;}
    }
    
    public ResultSet findAnswerList(int _id_question)
    {
        Statement stmt = null;
        try 
        { 
            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT * FROM TesterDB.answer where answer.answer_question_id = '" 
                    + _id_question + "';");
            return rs; 
        } catch (SQLException sqlE) { System.out.println(sqlE); return null;}
    }
    
    public int checkAnswer(int _id_ans)
    {
        Statement stmt = null;
        try 
        { 
            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT * FROM TesterDB.answer where answer_id = '" 
                    + _id_ans + "';");
            rs.next();
            if(1 == Integer.parseInt(rs.getObject(4).toString()))
                return 1;
            else
                return 0;
        } catch (SQLException sqlE) { System.out.println(sqlE); return -1;}
    }
    
    public void insertMark(int _mark, int _test_id, int _student_id)
    {
        Statement stmt = null;        
        try 
        { 
            stmt = conn.createStatement();
            stmt.executeUpdate("insert into TesterDB.mark set mark_value = '" + _mark
                                + "', mark_date = now()"         
                                + ", mark_test_id = '"         + _test_id
                                + "', mark_student_id = '" +  _student_id + "';");
        } catch (SQLException sqlE) {System.err.println(sqlE);}
    }    
}
