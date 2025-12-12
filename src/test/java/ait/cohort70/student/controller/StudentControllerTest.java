package ait.cohort70.student.controller;

import ait.cohort70.student.dto.ScoreDto;
import ait.cohort70.student.dto.StudentCredentialsDto;
import ait.cohort70.student.dto.StudentDto;
import ait.cohort70.student.dto.StudentUpdateDto;
import ait.cohort70.student.dto.exceptions.EntityExistsException;
import ait.cohort70.student.dto.exceptions.NotFoundException;
import ait.cohort70.student.service.StudentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StudentController.class)
class StudentControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    StudentService studentService;

    // ---------- POST /student ----------

    @Test
    void addStudentSuccessReturns204() throws Exception {
        StudentCredentialsDto dto =
                new StudentCredentialsDto(1L, "John", "1234");

        mockMvc.perform(post("/student")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNoContent());
    }

    @Test
    void addStudentAlreadyExistsReturns409() throws Exception {
        doThrow(EntityExistsException.class)
                .when(studentService)
                .addStudent(any());

        StudentCredentialsDto dto =
                new StudentCredentialsDto(1L, "John", "1234");

        mockMvc.perform(post("/student")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isConflict());
    }

    // ---------- GET /student/{id} ----------

    @Test
    void findStudentSuccessReturnsStudentJson() throws Exception {
        StudentDto student =
                new StudentDto(1L, "John", Map.of("math", 90));

        when(studentService.findStudent(1L)).thenReturn(student);

        mockMvc.perform(get("/student/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("John"))
                .andExpect(jsonPath("$.scores.math").value(90));
    }

    @Test
    void findStudentNotFoundReturns404() throws Exception {
        when(studentService.findStudent(99L))
                .thenThrow(NotFoundException.class);

        mockMvc.perform(get("/student/{id}", 99L))
                .andExpect(status().isNotFound());
    }

    // ---------- DELETE /student/{id} ----------

    @Test
    void removeStudentSuccessReturnsStudent() throws Exception {
        StudentDto removed =
                new StudentDto(2L, "Ann", Map.of());

        when(studentService.removeStudent(2L)).thenReturn(removed);

        mockMvc.perform(delete("/student/{id}", 2L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.name").value("Ann"));
    }

    // ----------  PATCH /student/{id} ----------

    @Test
    void updateStudentSuccessReturnsCredentials() throws Exception {
        StudentUpdateDto updateDto =
                new StudentUpdateDto("NewName", "newPass");

        StudentCredentialsDto updated =
                new StudentCredentialsDto(1L, "NewName", "newPass");

        when(studentService.updateStudent(eq(1L), any(StudentUpdateDto.class)))
                .thenReturn(updated);

        mockMvc.perform(patch("/student/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("NewName"))
                .andExpect(jsonPath("$.password").value("newPass"));
    }

    // ---------- PATCH /score/student/{id} ----------

    @Test
    void addScoreSuccessReturns204() throws Exception {
        ScoreDto scoreDto = new ScoreDto("physics", 85);

        mockMvc.perform(patch("/score/student/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(scoreDto)))
                .andExpect(status().isNoContent());
    }

    // ---------- GET /students/name/{name} ----------

    @Test
    void findStudentsByNameReturnsList() throws Exception {
        List<StudentDto> students = List.of(
                new StudentDto(1L, "John", Map.of()),
                new StudentDto(2L, "John", Map.of("math", 100))
        );

        when(studentService.findStudentsByName("John"))
                .thenReturn(students);

        mockMvc.perform(get("/students/name/{name}", "John"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[1].scores.math").value(100));
    }

    // ---------- GET /quantity/students ----------

    @Test
    void countStudentsByNamesReturnsNumber() throws Exception {
        when(studentService.countStudentsByNames(Set.of("Ann", "Bob")))
                .thenReturn(2L);

        mockMvc.perform(get("/quantity/students")
                        .param("names", "Ann", "Bob"))
                .andExpect(status().isOk())
                .andExpect(content().string("2"));
    }

    // ---------- GET /students/exam/{examName}/minscore/{minScore} ----------

    @Test
    void findStudentsByExamNameMinScoreReturnsList() throws Exception {
        List<StudentDto> students = List.of(
                new StudentDto(1L, "John", Map.of("math", 85)),
                new StudentDto(3L, "Kate", Map.of("math", 95))
        );

        when(studentService.findStudentsByExamNameMinScore("math", 80))
                .thenReturn(students);

        mockMvc.perform(get("/students/exam/{examName}/minscore/{minScore}", "math", 80))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].scores.math").value(85))
                .andExpect(jsonPath("$[1].name").value("Kate"))
                .andExpect(jsonPath("$[1].scores.math").value(95));
    }
}

