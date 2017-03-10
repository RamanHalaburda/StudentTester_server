package tcpip;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JTextArea;
import java.sql.*;
import javax.swing.JTable;
import model.Student;
import mysqlconnect.MySqlConnection;

public class TCPIPConnection {
    private static final String address = "localhost"; 
    private static String port = null;
    private static Socket socket = null;
    private static InetAddress ipAddress = null;
    private static BufferedReader br = null; 
    private static ServerSocket server;
    private static PrintStream ps = null;
    
    MySqlConnection msc = null;
    Student student = null;
       
    public void doConnect(String port, JTextArea log) throws IOException, SQLException 
    {
        try {
            ipAddress = InetAddress.getByName(address);
            log.append("Подключение установлено. IP: " + ipAddress + ".\n");
        } catch (UnknownHostException ex) {
            Logger.getLogger(TCPIPConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
            
        try { 
            server = new ServerSocket(Integer.parseInt(port));           
            log.append("Ожидание клиента...\n");
            System.out.println("ожидание соединения...");            
            socket = server.accept();//ожидание соединения

            // чтение фио, специальности, курса
            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            
            String fio = br.readLine();
            String num = br.readLine();
            
            Student student = new Student(Integer.parseInt(num),fio,"",0);
                        
            log.append("Клиент: " + fio + ". Номер: " + num + "\n");
            System.out.println("Клиент: " + fio + ". Номер: " + num);
            
            msc = new MySqlConnection();
            msc.ConnectToDB();
            student = msc.findStudent(student);
            if(student.getStudentCourse() != 0)
            {
                ps = new PrintStream(socket.getOutputStream());
                ps.flush();
                ps.println(student.getStudentId()); ps.flush();
                log.append("Клиент найден: " + student.getStudentId() +"\n");
                System.out.println("Клиент найден: " + student.getStudentId());
            }
            else
            {
                ps = new PrintStream(socket.getOutputStream());
                ps.flush();
                ps.println(0); ps.flush();
                log.append("Клиент не найден!\n");
                System.out.println("Клиент не найден!");
            }     
            
            log.append("Студент: " + student.getStudentFio() 
                    + ". Номер: " + student.getStudentId().toString() 
                    + ". Специальность: " + student.getStudentSpeciality() 
                    + ". Курс: " + student.getStudentCourse() + ".\n");
            System.out.println("Студент: " + student.getStudentFio() 
                    + ". Номер: " + student.getStudentId().toString() 
                    + ". Специальность: " + student.getStudentSpeciality() 
                    + ". Курс: " + student.getStudentCourse());
                        
            ps = new PrintStream(socket.getOutputStream());
            ps.flush();
            ps.println(student.getStudentId()); ps.flush();
            ps.println(student.getStudentFio());           ps.flush();
            ps.println(student.getStudentSpeciality());    ps.flush();
            ps.println(student.getStudentCourse());        ps.flush();
            
            int id_test = msc.findTest(student);
            log.append("ИД теста: " + id_test + ".\n");
            System.out.println("ИД теста: " + id_test);
            ResultSet question_rs = msc.findQuestionList(id_test);
           
            //отправка количества вопросов (ответов на вопрос - 2)
            question_rs.last();
            int question_count = question_rs.getRow();
            question_rs.beforeFirst();
            log.append("Количество вопросов: " + question_count + ".\n");
            System.out.println("Количество вопросов: " + question_count);
            ps = new PrintStream(socket.getOutputStream());
            ps.flush();
            ps.println(question_count); ps.flush();
            
            // отправка вопросов и вариантов ответов
            int i = 1;
            int true_answer = 0;
            while(question_rs.next())
            {                
                ps = new PrintStream(socket.getOutputStream());
                ps.flush();
                ps.println(question_rs.getObject(3)); 
                log.append("\nВопрос #" + i + ": " + question_rs.getObject(3) + ".\n");
                System.out.println("Вопрос #" + i + ": " + question_rs.getObject(3));
               
                int id_question = Integer.parseInt(question_rs.getObject(1).toString());
                ResultSet answer_rs = msc.findAnswerList(id_question);
                int j = 1;
                int id_ans[] = new int[3];
                int bool_ans[] = new int[3];
                while(answer_rs.next())
                {
                    ps.flush();
                    ps.println(answer_rs.getObject(3));                    
                    id_ans[j] = Integer.parseInt(answer_rs.getObject(1).toString());
                    bool_ans[j] = Integer.parseInt(answer_rs.getObject(4).toString());
                    log.append("Вариант #" + j + ": " + answer_rs.getObject(3) + ".\n");
                    System.out.println("Вариант #" + j + ": " + answer_rs.getObject(3));
                    j++;
                }                
                
                // прием ответов
                br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                int recieved_ans = Integer.parseInt(br.readLine());
                System.out.println("Получен ответ на вопрос #" + i + ": " + recieved_ans);
                log.append("Получен ответ на вопрос #" + i + ": " + recieved_ans + ".\n");
                                
                // проверка ответа
                if(bool_ans[recieved_ans] == 1)
                {
                    true_answer++;
                    log.append("Ответ на вопрос #" + i + " (1-t,0-f): " + 1 + ".\n");
                    System.out.println("Ответ на вопрос #" + i + " (1-t,0-f): " + 1);
                }
                else
                {
                    log.append("Ответ на вопрос #" + i + " (1-t,0-f): " + 2 + ".\n");
                    System.out.println("Ответ на вопрос #" + i + " (1-t,0-f): " + 0);
                }
                ++i;
            }
             
            double temp_mark = 10.0 * ( (double) true_answer / question_count );
            int result_mark = (int)Math.ceil(temp_mark);
            log.append("\nРезультат (по 10-бальной системе): " + result_mark + ".\n");
            System.out.println("Результат (по 10-бальной системе): " + result_mark);
            
            // вычисление оценки
            ps.flush();
            ps.println((int)result_mark);                    
                    
            // отправка оценки клиенту и запись оценки в базу данных
            msc.ConnectToDB();
            msc.insertMark((int)result_mark, id_test, student.getStudentId());
        }
        catch (IOException e) { System.err.println("Tester server error: " + e); } 
        finally { if(socket != null) socket.close(); } // close connection
    }
}
