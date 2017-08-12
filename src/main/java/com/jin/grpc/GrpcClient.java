package com.jin.grpc;

import com.jin.proto.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

import java.util.Iterator;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class GrpcClient {
    public static void main(String[] args) throws InterruptedException {
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 8899).usePlaintext(true).build();

        StudentServiceGrpc.StudentServiceBlockingStub blockingStub = StudentServiceGrpc.newBlockingStub(channel);

        MyResponse myResponse = blockingStub.getStudentByUsername(MyRequest.newBuilder().setUsername("zhangsan").build());
        System.out.println(myResponse.getRealname());

        System.out.println("==========================");

        Iterator<StudentResponse> iter = blockingStub.getStudentsByAge(StudentRequest.newBuilder().setAge(20).build());

        while (iter.hasNext())
        {
            StudentResponse studentResponse = iter.next();

            System.out.println("name:" + studentResponse.getName());
            System.out.println("city:" + studentResponse.getCity());
            System.out.println("age:" + studentResponse.getAge());
        }

        System.out.println("==========================");
        final CountDownLatch latch = new CountDownLatch(1);

        StudentServiceGrpc.StudentServiceStub asyncStub = StudentServiceGrpc.newStub(channel);

        StreamObserver<StudentInfoResponse> studentInfoResponse = new StreamObserver<StudentInfoResponse>() {
            @Override
            public void onNext(StudentInfoResponse value) {
                System.out.println(value.getStatus());
            }

            @Override
            public void onError(Throwable t) {
                latch.countDown();
            }

            @Override
            public void onCompleted() {
                latch.countDown();
            }
        };

        StreamObserver<StudentInfoRequest> studentInfoRequest = asyncStub.saveStudents(studentInfoResponse);

        studentInfoRequest.onNext(StudentInfoRequest.newBuilder().setName("刘备").setAge(40).build());
        studentInfoRequest.onNext(StudentInfoRequest.newBuilder().setName("关羽").setAge(50).build());
        studentInfoRequest.onNext(StudentInfoRequest.newBuilder().setName("张飞").setAge(60).build());

        studentInfoRequest.onCompleted();

        // Receiving happens asynchronously
        if (!latch.await(1, TimeUnit.MINUTES)) {
            System.out.println("recordRoute can not finish within 1 minutes");
        }
    }
}
