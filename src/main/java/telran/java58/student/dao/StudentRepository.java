package telran.java58.student.dao;

import org.springframework.data.mongodb.repository.MongoRepository;
import telran.java58.student.model.Student;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface StudentRepository  extends MongoRepository<Student, Long> {



}

