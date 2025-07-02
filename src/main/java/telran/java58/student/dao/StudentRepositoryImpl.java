package telran.java58.student.dao;

import org.springframework.stereotype.Component;
import telran.java58.student.model.Student;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class StudentRepositoryImpl implements StudentRepository {
    private Map<Long, Student> students = new ConcurrentHashMap<>();

    @Override
    public Student save(Student student) {
        students.put(student.getId(), student);
        return student;
    }

    @Override
    public Optional<Student> findById(long id) {
        return Optional.ofNullable(students.get(id));
    }

    @Override
    public void deleteById(long id) {
        students.remove(id);
    }

    @Override
    public List<Student> findAll() {
        return new ArrayList<>(students.values());
    }

    @Override
    public List<Student> findByNameIgnoreCase(String name) {
        return students.values().stream()
                .filter(s -> s.getName().equalsIgnoreCase(name))
                .toList();
    }

    @Override
    public Long countByNameInIgnoreCase(Set<String> names) {
        return students.values().stream()
                .filter(student -> names.contains(student.getName().toLowerCase()))
                .count();
    }

    @Override
    public List<Student> findByExamAndScoreGreaterThan(String examName, int minScore) {
        return students.values().stream()
                .filter(student -> {
                    Integer score = student.getScores().get(examName);
                    return score != null && score > minScore;
                })
                .toList();
    }

}
