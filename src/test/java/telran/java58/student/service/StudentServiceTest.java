package telran.java58.student.service;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import telran.java58.configuration.ServiceConfiguration;
import telran.java58.student.dao.StudentRepository;
import telran.java58.student.dto.ScoreDto;
import telran.java58.student.dto.StudentCredentialsDto;
import telran.java58.student.dto.StudentDto;
import telran.java58.student.dto.StudentUpdateDto;
import telran.java58.student.dto.exceptions.ConflictException;
import telran.java58.student.dto.exceptions.NotFoundException;
import telran.java58.student.model.Student;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


// AAA - Arrange, act, Assert

@ContextConfiguration(classes = {ServiceConfiguration.class})
@SpringBootTest
public class StudentServiceTest {
    private final Long studentId = 1000L;
    private final String name = "John";
    private final String password = "password";
    private Student student;

    @Autowired
    private ModelMapper modelMapper;

    @MockBean
    private StudentRepository studentRepository;


    private StudentService studentService;

    @BeforeEach
    public void setUp() {
        student = new Student(studentId, name, password);
        studentService = new StudentServiceImpl(studentRepository, modelMapper);
    }

    @Test
    void testAddStudentWhenDoesExists() {
        //Arrange
        StudentCredentialsDto dto = new StudentCredentialsDto(studentId, name, password);
        when(studentRepository.existsById(studentId)).thenReturn(false);
        when(studentRepository.save(any(Student.class))).thenReturn(student);


        //Action
        studentService.addStudent(dto);

        //Assert
        verify(studentRepository, times(1)).save(student);
    }

    @Test
    void testAddStudentWhenExists() {
        //Arrange
        StudentCredentialsDto dto = new StudentCredentialsDto(studentId, name, password);
        when(studentRepository.existsById(studentId)).thenReturn(true);

        //Action


        //Action & Assert
        assertThrows(ConflictException.class, () -> studentService.addStudent(dto));
        verify(studentRepository, never()).save(any(Student.class));
    }

    @Test
    void testFindStudentWhenStudioExists() {

        when(studentRepository.findById(studentId)).thenReturn(Optional.ofNullable(student));

        StudentDto dto = studentService.findStudent(studentId);

        assertNotNull(dto);
        assertEquals(studentId, dto.getId());
    }

    @Test
    void testFindStudentWhenStudioDoesExists() {
        when(studentRepository.findById(studentId)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> studentService.findStudent(studentId));
        verify(studentRepository, times(1)).findById(studentId);
        verify(studentRepository, never()).save(any(Student.class));
    }

    @Test
    void testRemoveStudent(){
        when(studentRepository.findById(studentId)).thenReturn(Optional.ofNullable(student));

        StudentDto dto = studentService.removeStudent(studentId);

        assertNotNull(dto);
        assertEquals(studentId, dto.getId());
        verify(studentRepository, times(1)).deleteById(studentId);
    }


    @Test
    void testUpdateStudent(){

        String newName ="Jane";
        StudentUpdateDto dto = new StudentUpdateDto(newName, null);
        when(studentRepository.findById(studentId)).thenReturn(Optional.ofNullable(student));

        StudentCredentialsDto updateDto = studentService.updateStudent(studentId, dto);

        assertNotNull(dto);
        assertEquals(studentId, updateDto.getId());
        assertEquals(newName, updateDto.getName());
        assertEquals(password, updateDto.getPassword());
        verify(studentRepository, times(1)).save(student);
    }

    @Test
    void testAddscore(){
        when(studentRepository.findById(studentId)).thenReturn(Optional.ofNullable(student));
        String examName = "Exam";
        int score = 90;
        ScoreDto dto = new ScoreDto(examName, score);

        studentService.addScore(studentId, dto);

        verify(studentRepository, times(1)).save(student);
        assertTrue( student.getScores().containsKey(examName));
        assertEquals(score, student.getScores().get(examName));
    }

    @Test
    void testFindStudentByName(){

        when(studentRepository.findByNameIgnoreCase(name)).thenReturn(Stream.of(student));

        List<StudentDto> dtos = studentService.findStudentsByName(name);

        assertNotNull(dtos);
        assertEquals(1, dtos.size());
        assertEquals(studentId, dtos.get(0).getId());
        assertEquals(name, dtos.get(0).getName());
    }

    @Test
    void testCountStudentByNames(){

        Set<String> names = Set.of(name, "Peter");
        when(studentRepository.countByNameInIgnoreCase(names)).thenReturn(2L);

        Long count = studentService.countStudentByNames(names);

        assertNotNull(count);
        assertEquals(2L, count);
    }

    @Test
    void testFindStudentsByExamNameMinScore(){
        String examName = "Exam";
        int minScore = 90;
        when(studentRepository.findByExamAndScoresGreaterThan(examName, minScore))
                .thenReturn(Stream.of(student));

        List<StudentDto> dtos = studentService.findStudentsByExamNameMinScore(examName, minScore);

        assertNotNull(dtos);
        assertEquals(1, dtos.size());
        assertEquals(studentId, dtos.get(0).getId());
        assertEquals(name, dtos.get(0).getName());
    }

}
