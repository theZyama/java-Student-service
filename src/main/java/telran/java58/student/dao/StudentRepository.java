package telran.java58.student.dao;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import telran.java58.student.model.Student;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

public interface StudentRepository  extends MongoRepository<Student, Long> {
    Stream<Student> findByNameIgnoreCase(String name);

    Long countByNameInIgnoreCase(Set<String> names);

    @Query("{'scores.Math': {'$gt': 80}}")
    Stream<Student> findByExamAndScoresGreaterThan(String exam, int score);






}

