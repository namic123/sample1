package com.example.sample1.dao;

import com.example.sample1.domain.Todo;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Mapper
public interface TodoDao {

    // LEFT JOIN으로 todo의 모든 리스트를 반환
    // todoFile에는 todoId가 가지고 있는 파일의 개수를 담고 있음
    // LEFT JOIN을 했으므로, todoFile에 포함되지 않은 todoId는 null
    @Select("""
        SELECT t.id, t.todo, t.inserted, COUNT(f.todoId) numOfFiles
        FROM todo t LEFT JOIN todoFile f ON t.id = f.todoId
        GROUP BY t.id
        ORDER BY t.id DESC
        """)
    public List<Todo> list();

    // Submit된 todo 필드를 insert
    @Insert("""
            INSERT INTO todo (todo)
            VALUE (#{todo})  
            """)
    // useGeneratedKeys = true : 삽입된 레코드의 자동 생성 키를 반환할 것임을 지시
    // keyProperty = "id" : 반환된 자동 생성 키를 Todo 객체의 id 필드에 설정하는 지시
    @Options(useGeneratedKeys = true, keyProperty = "id")
    public int insert(Todo todo);

    // submit된 todoId와 파일 명을 insert
    @Insert("""
        INSERT INTO todoFile (todoId, name)
        VALUES (#{todo.id}, #{fileName})
        """)
    int insertFile(Todo todo, String fileName);

    // todoFile에 todoId가 가진 파일명 반환
    @Select("""
        SELECT name
        FROM todoFile
        WHERE todoId = #{todoId}
        """)
    List<String> selectFilesByTodoId(Integer todoId);
}
