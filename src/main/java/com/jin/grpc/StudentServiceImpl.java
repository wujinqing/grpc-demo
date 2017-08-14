package com.jin.grpc;

import com.jin.proto.*;
import io.grpc.stub.StreamObserver;

import java.util.UUID;

public class StudentServiceImpl extends StudentServiceGrpc.StudentServiceImplBase{
    @Override
    public void getStudentByUsername(MyRequest request, StreamObserver<MyResponse> responseObserver) {
        System.out.println("来自客户端的请求:" + request.getUsername());

        responseObserver.onNext(MyResponse.newBuilder().setRealname("张三").build());
        responseObserver.onCompleted();
    }

    @Override
    public void getStudentsByAge(StudentRequest request, StreamObserver<StudentResponse> responseObserver) {
        System.out.println("来自客户端的请求:" + request.getAge());

        responseObserver.onNext(StudentResponse.newBuilder().setAge(10).setName("张三").setCity("上海").build());
        responseObserver.onNext(StudentResponse.newBuilder().setAge(20).setName("李四").setCity("北京").build());
        responseObserver.onNext(StudentResponse.newBuilder().setAge(30).setName("王五").setCity("广州").build());

        responseObserver.onCompleted();
    }

    @Override
    public StreamObserver<StudentInfoRequest> saveStudents(StreamObserver<StudentInfoResponse> responseObserver) {

        return new StreamObserver<StudentInfoRequest>() {
            @Override
            public void onNext(StudentInfoRequest value) {
                System.out.println("来自客户端, name:" + value.getName());
                System.out.println("来自客户端, age:" + value.getAge());
                System.out.println("======================");
            }

            @Override
            public void onError(Throwable t) {
                System.out.println("onError!");
            }

            @Override
            public void onCompleted() {
                System.out.println("完成");
                responseObserver.onNext(StudentInfoResponse.newBuilder().setStatus("success!").build());
                responseObserver.onCompleted();
            }
        };
    }

    @Override
    public StreamObserver<StreamRequest> biTalk(StreamObserver<StreamResponse> responseObserver) {
        return new StreamObserver<StreamRequest>() {
            @Override
            public void onNext(StreamRequest value) {
                System.out.println(value.getRequestInfo());

                responseObserver.onNext(StreamResponse.newBuilder().setResponseInfo(UUID.randomUUID().toString()).build());
            }

            @Override
            public void onError(Throwable t) {
                System.out.println(t.getMessage());
            }

            @Override
            public void onCompleted() {

                responseObserver.onCompleted();
            }
        };
    }
}
