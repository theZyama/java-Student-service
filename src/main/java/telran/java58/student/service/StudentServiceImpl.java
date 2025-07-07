package telran.java58.student.service;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import telran.java58.student.dao.StudentRepository;
import telran.java58.student.dto.ScoreDto;
import telran.java58.student.dto.StudentCredentialsDto;
import telran.java58.student.dto.StudentDto;
import telran.java58.student.dto.StudentUpdateDto;
import telran.java58.student.dto.exceptions.ConflictException;
import telran.java58.student.dto.exceptions.NotFoundException;
import telran.java58.student.model.Student;

import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class StudentServiceImpl implements StudentService {
    private final StudentRepository studentRepository;

    @Override
    public void addStudent(StudentCredentialsDto studentDto) {
        if (studentRepository.findById(studentDto.getId()).isPresent()) {
            throw new ConflictException();
        }
        Student student = new Student(studentDto.getId(), studentDto.getName(), studentDto.getPassword());
        studentRepository.save(student);
    }

    @Override
    public StudentDto findStudent(Long id) {
        Student student = studentRepository.findById(id).orElseThrow(NotFoundException::new);
        return new StudentDto(student.getId(), student.getName(), student.getScores());
    }

    @Override
    public StudentDto removeStudent(Long id) {
        Student student = studentRepository.findById(id).orElseThrow(NotFoundException::new);
        studentRepository.deleteById(id);
        return new StudentDto(id, student.getName(), student.getScores());
    }

    @Override
    public StudentCredentialsDto updateStudent(Long id, StudentUpdateDto studentUpdateDto) {
        Student student = studentRepository.findById(id).orElseThrow(NotFoundException::new);
        if (studentUpdateDto.getName() != null) {
            student.setName(studentUpdateDto.getName());
        }
        if (studentUpdateDto.getPassword() != null) {
            student.setPassword(studentUpdateDto.getPassword());
        }
        studentRepository.save(student);
        return new StudentCredentialsDto(student.getId(), student.getName(), student.getPassword());

    }

    @Override
    public void addScore(Long id, ScoreDto scoreDto) {
        Student student = studentRepository.findById(id).orElseThrow(NotFoundException::new);
        student.addScore(scoreDto.getExamName(), scoreDto.getScore());
        studentRepository.save(student);

    }

    @Override
    public List<StudentDto> findStudentsByName(String name) {
        return studentRepository.findByNameIgnoreCase(name)
                .map(s -> new StudentDto(s.getId(), s.getName(), s.getScores()))
                .toList();
    }

    @Override
    public Long countStudentByNames(Set<String> names) {
        return studentRepository.countByNameInIgnoreCase(names);
    }

    @Override
    public List<StudentDto> findStudentsByExamNameMinScore(String examName, Integer minScore) {
        return studentRepository.findByExamAndScoresGreaterThan(examName, minScore)
                .map(s -> new StudentDto(s.getId(), s.getName(), s.getScores()))
                .toList();
    }
}