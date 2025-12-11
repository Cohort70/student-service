package ait.cohort70.student.service;

import ait.cohort70.student.dao.StudentRepository;
import ait.cohort70.student.dto.ScoreDto;
import ait.cohort70.student.dto.StudentCredentialsDto;
import ait.cohort70.student.dto.StudentDto;
import ait.cohort70.student.dto.StudentUpdateDto;
import ait.cohort70.student.dto.exceptions.EntityExistsException;
import ait.cohort70.student.dto.exceptions.NotFoundException;
import ait.cohort70.student.model.Student;
import lombok.RequiredArgsConstructor;
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
        Student student = studentRepository.findById(id).orElseThrow(NotFoundException::new);
        return new StudentDto(student.getId(), student.getName(), student.getScores());
    }

    @Override
    public StudentDto removeStudent(Long id) {
        Student student = studentRepository.findById(id).orElseThrow(NotFoundException::new);
        studentRepository.deleteById(id);
        return new StudentDto(student.getId(), student.getName(), student.getScores());
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
    public Long countStudentsByNames(Set<String> names) {
        return studentRepository.findAll().stream()
                .filter(s -> names.contains(s.getName()))
                .count();
    }

    @Override
    public List<StudentDto> findStudentsByExamNameMinScore(String examName, Integer minScore) {
        return studentRepository.findByExamAndScoreGreaterThan(examName, minScore)
                .map(s -> new StudentDto(s.getId(), s.getName(), s.getScores()))
                .toList();
    }
}
