package com.example.mooyaho.data_class;

public class PostTossResult { // Post 정보를 서버에서 가져와야 할 때 해당 클래스 형식으로 가져온다

    private Success success;

    public PostTossResult(Success success) {
        this.success = success;
    }

    public Success getSuccess() {
        return success;
    }

    public void setSuccess(Success success) {
        this.success = success;
    }
}