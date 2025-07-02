package telran.java58.student.dao;

import telran.java58.student.model.Student;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface StudentRepository {
    Student save(Student student);

    Optional<Student> findById(long id);

    void deleteById(long id);

    List<Student> findAll();

    List<Student> findByNameIgnoreCase(String name);

    Long countByNameInIgnoreCase(Set<String> names);

    List<Student> findByExamAndScoreGreaterThan(String examName, int minScore);


}

