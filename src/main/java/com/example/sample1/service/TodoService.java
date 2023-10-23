package com.example.sample1.service;

import com.example.sample1.dao.TodoDao;
import com.example.sample1.domain.Todo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)   // Exception 발생 시 롤백
public class TodoService {

    private final TodoDao dao;  // todo 관련 데이터베이스 작업을 수행하는 DAO 객체

    private final S3Client s3Client;    // AWS S3 클라이언트

    @Value("${aws.bucketName}") // custom.properties의 값을 읽어옴
    private String bucketName;

    @Value("${image.url.prefix}")    // custom.properties의 값을 읽어옴
    private String urlPrefix;

    public List<Todo> list() {  // todo의 전체 목록과 각 todoId가 가지고 있는 파일의 개수를 포함
        return dao.list();
    }

    // todo 객체의 필드, 사용자가 업로드한 파일들의 배열을 파라미터로 받음
    public boolean insert(Todo todo , MultipartFile[] files) throws IOException {
        // 추가된 레코드의 수를 저장
        int count = dao.insert(todo);

        // 업로드 된 파일이 있는지 검증
        if (files != null && files.length > 0) {
            for (MultipartFile file : files) {
                if (file.getSize() > 0) {   // 파일의 크기가 0보다 큰지 검증
                    // todoFile 테이블에 todo field와, file 이름을 insert
                    dao.insertFile(todo, file.getOriginalFilename());

                    // AWS S3에 파일 업로드
                    // S3에 저장될 파일의 키를 생성
                    // sample 폴더 + todo의 기본키 값 + 파일명
                    String key = "sample1/" + todo.getId() + "/" + file.getOriginalFilename();

                    // S3에 파일을 업로드하기 위한 요청 객체 생성
                    PutObjectRequest request = PutObjectRequest.builder()
                            .key(key)   // 키
                            .bucket(bucketName) // 버킷 이름
                            .acl(ObjectCannedACL.PUBLIC_READ)   // 권한 전체 읽기 가능
                            .build();   // 빌드

                    // 실제로 S3에 파일을 업로드
                    s3Client.putObject(request, // 위 요청 객체
                            RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
                    // file.getInputStream : MultipartFile 객체에서 제공하는
                    // getInputStream getSize() 메서드를 호출하여 파일의 크기(바이트 단위)를 가져옴

                    // RequestBody 객체는 HTTP 요청 본문을 나타내며,
                    // 이 경우에는 AWS S3에 업로드할 파일의 내용과 크기 정보를 담고 있음.

                    // 이렇게 생성된 RequestBody객체는 s3Client.putObject() 메서드에 전달되어,
                    // 실제로 AWS S3에 파일이 업로드됨.

                    // 이 객체는 파일의 내용과 크기 정보를 가지고 있어 S3 서버가 이를 적절히 처리함.
                }
            }
        }
        return count == 1;  // 성공 여부 반환
    }

    public List<String> listFiles(Integer todoId) {
        // todoId를 입력해서 해당 id에 포함된 파일의 이름을 list로 저장
        List<String> list = dao.selectFilesByTodoId(todoId);

        // 가져온 파일 목록에 url 접두사와 todoId를 추가하여 최종 파일 경로를 생성해서 리스트에 담는다.
        return list.stream()
                .map(e -> urlPrefix + "/" + todoId + "/" + e)
                .collect(Collectors.toList());
    }
}
