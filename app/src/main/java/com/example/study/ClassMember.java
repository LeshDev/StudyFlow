package com.example.study;

import com.google.gson.annotations.SerializedName;

public class ClassMember {
    @SerializedName("teacher_id")
    private long teacherId;

    @SerializedName("student_id")    private long studentId;

    public ClassMember(long teacherId, long studentId) {
        this.teacherId = teacherId;
        this.studentId = studentId;
    }
}
