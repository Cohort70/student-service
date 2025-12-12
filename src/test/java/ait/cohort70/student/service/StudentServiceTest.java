package ait.cohort70.student.service;

import ait.cohort70.configuration.ServiceConfiguration;
import ait.cohort70.student.dao.StudentRepository;
import ait.cohort70.student.dto.ScoreDto;
import ait.cohort70.student.dto.StudentCredentialsDto;
import ait.cohort70.student.dto.StudentDto;
import ait.cohort70.student.dto.StudentUpdateDto;
import ait.cohort70.student.dto.exceptions.EntityExistsException;
import ait.cohort70.student.dto.exceptions.NotFoundException;
import ait.cohort70.student.model.Student;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

// AAA - Arrange, Act, Assert

@ContextConfiguration(classes = {ServiceConfiguration.class})
@SpringBootTest
public class StudentServiceTest {
    private final long studentId = 1000L;
    private final String name = "John";
    private final String password = "1234";
    private Student student;

    @Autowired
    private ModelMapper modelMapper;

    @MockitoBean
    private StudentRepository studentRepository;

    private StudentService studentService;

    @BeforeEach
    public void setUp() {
        student = new Student(studentId, name, password);
        studentService = new StudentServiceImpl(studentRepository, modelMapper);
    }

    @Test
    void testAddStudentWhenStudentDoesNotExist() {
        // Arrange
        StudentCredentialsDto studentCredentialsDto = new StudentCredentialsDto(studentId, name, password);
        when(studentRepository.existsById(studentId)).thenReturn(false);
        when(studentRepository.save(student)).thenReturn(student);

        // Act
        studentService.addStudent(studentCredentialsDto);

        // Assert
        verify(studentRepository, times(1)).save(student);

    }

    @Test
    void testAddStudentWhenStudentExist() {
        // Arrange
        StudentCredentialsDto studentCredentialsDto = new StudentCredentialsDto(studentId, name, password);
        when(studentRepository.existsById(studentId)).thenReturn(true);

        // Act & Assert
        assertThrows(EntityExistsException.class, () -> studentService.addStudent(studentCredentialsDto));
        verify(studentRepository, never()).save(any(Student.class));
    }

    @Test
    void testFindStudentWhenStudentExists() {
        // Arrange
        when(studentRepository.findById(studentId)).thenReturn(Optional.ofNullable(student));

        // Act
        StudentDto studentDto = studentService.findStudent(studentId);

        // Assert
        assertNotNull(studentDto);
        assertEquals(studentId, studentDto.getId());
        assertEquals(name, studentDto.getName());
    }

    @Test
    void testFindStudentWhenStudentNotExists() {
        // Arrange
        when(studentRepository.findById(studentId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> studentService.findStudent(studentId));
    }

    @Test
    void testRemoveStudent() {
        // Arrange
        when(studentRepository.findById(studentId)).thenReturn(Optional.ofNullable(student));

        // Act
        StudentDto studentDto = studentService.removeStudent(studentId);

        // Assert
        assertNotNull(studentDto);
        assertEquals(studentId, studentDto.getId());
        verify(studentRepository, times(1)).deleteById(studentId);
    }

    @Test
    void testUpdateStudent() {
        // Arrange
        String newName = "Jane";
        StudentUpdateDto studentUpdateDto = new StudentUpdateDto(newName, null);
        when(studentRepository.findById(studentId)).thenReturn(Optional.ofNullable(student));

        // Act
        StudentCredentialsDto studentCredentialsDto = studentService.updateStudent(studentId, studentUpdateDto);

        // Assert
        assertNotNull(studentCredentialsDto);
        assertEquals(studentId, studentCredentialsDto.getId());
        assertEquals(newName, studentCredentialsDto.getName());
        assertEquals(password, studentCredentialsDto.getPassword());
        verify(studentRepository, times(1)).save(student);
    }

    @Test
    void testAddScore() {
        // Arrange
        String examName = "Java Exam";
        int score = 100;
        ScoreDto scoreDto = new ScoreDto(examName, score);
        when(studentRepository.findById(studentId)).thenReturn(Optional.ofNullable(student));

        // Act
        studentService.addScore(studentId, scoreDto);

        // Assert
        verify(studentRepository, times(1)).save(student);
        assertTrue(student.getScores().containsKey(examName));
        assertEquals(score, student.getScores().get(examName));
    }

    @Test
    void testFindStudentsByName(){
        // Arrange
        when(studentRepository.findByNameIgnoreCase(name)).thenReturn(Stream.of(student));

        // Act
        List<StudentDto> studentDtos = studentService.findStudentsByName(name);

        // Asserts
        assertNotNull(studentDtos);
        assertEquals(1, studentDtos.size());
        assertEquals(studentId, studentDtos.getFirst().getId());
        assertEquals(name, studentDtos.getFirst().getName());
    }

    @Test
    void testCountStudentsByNames() {
        // Arrange
        Set<String> names = Set.of(name, "Peter", "Mary", "Jane");
        when(studentRepository.countByNameInIgnoreCase(names)).thenReturn(2L);

        // Act
        Long count = studentService.countStudentsByNames(names);

        // Assert
        assertNotNull(count);
        assertEquals(2L, count);
    }

    @Test
    void testFindStudentsByExamNameMinScore() {
        // Arrange
        String examName = "Java Exam";
        int minScore = 100;
        when(studentRepository.findByExamAndScoreGreaterThan(examName, minScore)).thenReturn(Stream.of(student));

        // Act
        List<StudentDto> studentDtos = studentService.findStudentsByExamNameMinScore(examName, minScore);

        // Assert
        assertNotNull(studentDtos);
        assertEquals(1, studentDtos.size());
        assertEquals(studentId, studentDtos.getFirst().getId());
        assertEquals(name, studentDtos.getFirst().getName());
    }
}
