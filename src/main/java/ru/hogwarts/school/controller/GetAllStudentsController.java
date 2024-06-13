package ru.hogwarts.school.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.hogwarts.school.entity.Student;
import ru.hogwarts.school.service.StudentService;

import java.util.Collection;

@RestController
public class GetAllStudentsController {

    /*
    Added service
     */
    private final StudentService studentService;

    public GetAllStudentsController(StudentService studentService) {
        this.studentService = studentService;
    }

    /*
    Count all students
     */
    @GetMapping
    @Operation(summary = "Количество всех студентов")
    public Integer numberAllStudents() {
        return studentService.numberAllStudents();
    }

    /*
    Average age for students
     */
    @GetMapping("get-avg-age")
    @Operation(summary = "Средний возраст студентов")
    public Integer averageAgeStudents() {
        return studentService.averageAgeStudents();
    }

    /*
    Get last fives students
     */
    @GetMapping("get-fiv-stud")
    @Operation(summary = "Пять последних студентов")
    public Collection<Student> getLastFiveStudents() {
        return studentService.getLastFiveStudents();
    }
}
