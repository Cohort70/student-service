package ait.cohort70.student.service;

import ait.cohort70.student.dao.StudentRepository;
import ait.cohort70.student.dto.ScoreDto;
import ait.cohort70.student.dto.StudentCredentialsDto;
import ait.cohort70.student.dto.StudentDto;
import ait.cohort70.student.dto.StudentUpdateDto;
import ait.cohort70.student.dto.exceptions.EntityExistsException;
import ait.cohort70.student.dto.exceptions.NoFoundException;
import ait.cohort70.student.model.Student;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class StudentServiceImpl implements StudentService {
    private final StudentRepository studentRepository;

    @Override
    public void addStudent(StudentCredentialsDto studentCredentialsDto) {
        if (studentRepository.findById(studentCredentialsDto.getId()).isEmpty()) {
            Student student = new Student(studentCredentialsDto.getId(), studentCredentialsDto.getName(),
                    studentCredentialsDto.getPassword());
            studentRepository.save(student);
        } else {
            throw new EntityExistsException();
        }
    }

    @Override
    public StudentDto findStudent(Long id) {
        Student student = studentRepository.findById(id).orElseThrow(NoFoundException::new);
        return new StudentDto(student.getId(), student.getName(), student.getScores());
    }

    @Override
    public StudentDto removeStudent(Long id) {
        Student student = studentRepository.findById(id).orElseThrow(NoFoundException::new);
        studentRepository.deleteById(id);
        return new StudentDto(student.getId(), student.getName(), student.getScores());
    }

    @Override
    public StudentCredentialsDto updateStudent(Long id, StudentUpdateDto studentUpdateDto) {
        return null;
    }

    @Override
    public void addScore(Long id, ScoreDto scoreDto) {

    }

    @Override
    public List<StudentDto> findStudentsByName(String name) {
        return List.of();
    }

    @Override
    public Long countStudentsByNames(Set<String> names) {
        return 0L;
    }

    @Override
    public List<StudentDto> findStudentsByExamNameMinScore(String examName, Integer minScore) {
        return List.of();
    }
}
