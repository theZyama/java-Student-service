package telran.java58.student.service;

import telran.java58.student.dto.ScoreDto;
import telran.java58.student.dto.StudentCredentialsDto;
import telran.java58.student.dto.StudentDto;
import telran.java58.student.dto.StudentUpdateDto;
import telran.java58.student.model.Student;

import java.util.List;
import java.util.Set;

public interface StudentService {
    void addStudent(StudentCredentialsDto student);

    StudentDto findStudent(Long id);

    StudentDto removeStudent(Long id);

    StudentCredentialsDto updateStudent(Long id, StudentUpdateDto studentUpdateDto);

    void addScore(Long id, ScoreDto scoreDto);

    List<StudentDto> findStudentsByName(String name);

    Long countStudentByNames(Set<String> names);

    List<StudentDto> findStudentsByExamNameMinScore(String examName, Integer minScore);

}
