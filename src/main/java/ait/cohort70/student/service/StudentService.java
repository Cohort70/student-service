package ait.cohort70.student.service;

import ait.cohort70.student.dto.ScoreDto;
import ait.cohort70.student.dto.StudentCredentialsDto;
import ait.cohort70.student.dto.StudentDto;
import ait.cohort70.student.dto.StudentUpdateDto;

import java.util.List;
import java.util.Set;

public interface StudentService {
    void addStudent(StudentCredentialsDto studentCredentialsDto);
    StudentDto findStudent(Long id);
    StudentDto removeStudent(Long id);
    StudentCredentialsDto updateStudent(Long id, StudentUpdateDto studentUpdateDto);
    void addScore(Long id, ScoreDto scoreDto);
    List<StudentDto> findStudentsByName(String name);
    Long countStudentsByNames(Set<String> names);
    List<StudentDto> findStudentsByExamNameMinScore(String examName, Integer minScore);

}
