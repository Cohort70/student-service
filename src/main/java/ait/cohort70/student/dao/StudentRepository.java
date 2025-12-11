package ait.cohort70.student.dao;

import ait.cohort70.student.model.Student;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.stream.Stream;

public interface StudentRepository extends MongoRepository<Student, Long> {
    Stream<Student> findByNameIgnoreCase(String name);

    @Query("{'scores.?0': {'$gt': ?1}}")
    Stream<Student> findByExamAndScoreGreaterThan(String exam, int score);

}
