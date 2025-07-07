package telran.java58.student.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import telran.java58.student.dto.ScoreDto;
import telran.java58.student.dto.StudentCredentialsDto;
import telran.java58.student.dto.StudentDto;
import telran.java58.student.dto.StudentUpdateDto;
import telran.java58.student.service.StudentService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class StudentControllerTest {

    private MockMvc mockMvc;

    @Mock
    private StudentService studentService;

    @InjectMocks
    private StudentController studentController;

    private ObjectMapper objectMapper = new ObjectMapper();

    private final Long studentId = 1000L;
    private final String name = "John";
    private final String password = "1234";
    private StudentCredentialsDto studentCredentialsDto;
    private StudentDto studentDto;
    private Map<String, Integer> scores;

    @BeforeEach
    void setUp() {
        scores = new HashMap<>();
        scores.put("Math", 95);
        scores.put("History", 85);

        studentCredentialsDto = new StudentCredentialsDto(studentId, name, password);
        studentDto = new StudentDto(studentId, name, scores);

        mockMvc = MockMvcBuilders.standaloneSetup(studentController).build();
    }

    @Test
    void testAddStudent() throws Exception {
        doNothing().when(studentService).addStudent(any(StudentCredentialsDto.class));

        mockMvc.perform(post("/student")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(studentCredentialsDto)))
                .andExpect(status().isNoContent());

        verify(studentService, times(1)).addStudent(any(StudentCredentialsDto.class));
    }

    @Test
    void testFindStudent() throws Exception {
        when(studentService.findStudent(studentId)).thenReturn(studentDto);

        mockMvc.perform(get("/student/{id}", studentId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(studentId))
                .andExpect(jsonPath("$.name").value(name))
                .andExpect(jsonPath("$.scores.Math").value(95))
                .andExpect(jsonPath("$.scores.History").value(85));

        verify(studentService, times(1)).findStudent(studentId);
    }

    @Test
    void testRemoveStudent() throws Exception {
        when(studentService.removeStudent(studentId)).thenReturn(studentDto);

        mockMvc.perform(delete("/student/{id}", studentId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(studentId))
                .andExpect(jsonPath("$.name").value(name));

        verify(studentService, times(1)).removeStudent(studentId);
    }

    @Test
    void testUpdateStudent() throws Exception {
        StudentUpdateDto updateDto = new StudentUpdateDto("Jane", "5678");
        when(studentService.updateStudent(eq(studentId), any(StudentUpdateDto.class)))
                .thenReturn(new StudentCredentialsDto(studentId, "Jane", "5678"));

        mockMvc.perform(patch("/student/{id}", studentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(studentId))
                .andExpect(jsonPath("$.name").value("Jane"))
                .andExpect(jsonPath("$.password").value("5678"));

        verify(studentService, times(1)).updateStudent(eq(studentId), any(StudentUpdateDto.class));
    }

    @Test
    void testAddScore() throws Exception {
        ScoreDto scoreDto = new ScoreDto("Physics", 90);
        doNothing().when(studentService).addScore(eq(studentId), any(ScoreDto.class));

        mockMvc.perform(patch("/score/student/{id}", studentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(scoreDto)))
                .andExpect(status().isNoContent());

        verify(studentService, times(1)).addScore(eq(studentId), any(ScoreDto.class));
    }

    @Test
    void testFindStudentsByName() throws Exception {
        when(studentService.findStudentsByName(name)).thenReturn(List.of(studentDto));

        mockMvc.perform(get("/students/name/{name}", name))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(studentId))
                .andExpect(jsonPath("$[0].name").value(name));

        verify(studentService, times(1)).findStudentsByName(name);
    }

    @Test
    void testCountStudentByNames() throws Exception {
        Set<String> names = Set.of("John", "Jane");
        when(studentService.countStudentByNames(names)).thenReturn(2L);

        mockMvc.perform(get("/quantity/students")
                        .param("names", "John", "Jane"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string("2"));

        verify(studentService, times(1)).countStudentByNames(names);
    }

    @Test
    void testFindStudentsByExamNameMinScore() throws Exception {
        String examName = "Math";
        Integer minScore = 90;
        when(studentService.findStudentsByExamNameMinScore(examName, minScore))
                .thenReturn(List.of(studentDto));

        mockMvc.perform(get("/students/exam/{examName}/minscore/{minScore}", examName, minScore))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(studentId))
                .andExpect(jsonPath("$[0].name").value(name));

        verify(studentService, times(1)).findStudentsByExamNameMinScore(examName, minScore);
    }
}