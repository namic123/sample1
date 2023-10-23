package com.example.sample1.controller;

import com.example.sample1.dao.TodoDao;
import com.example.sample1.domain.Todo;
import com.example.sample1.service.TodoService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class TodoController {

    private final TodoService service;  // 비즈니스 로직 객체

    @GetMapping("/")
    public String home(Model model) throws Exception {
        // todo테이블의 전체 레코드 + todoFiles의 파일을 가진 todoId를 count한 목록 반환
        List<Todo> list = service.list();
        model.addAttribute("todoList", list);


        return "home";
    }

    @PostMapping("/add")
    public String add(Todo todo,
                      MultipartFile[] files,
                      RedirectAttributes rttr) throws SQLException, IOException {

        // form에 submit된 todo와 file을 insert
        boolean result = service.insert(todo, files);

        // home으로 redirect
        return "redirect:/";
    }

    @GetMapping("files")
    public void listFiles(
            @RequestParam("id") Integer todoId,
            Model model) {
        List<String> filePathList = service.listFiles(todoId);  // file을 가진 todoId의 리스트
        model.addAttribute("filePathList", filePathList);   // 모델에 추가
    }
}
