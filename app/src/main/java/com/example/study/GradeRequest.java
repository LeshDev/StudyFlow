package com.example.study;

import com.google.gson.annotations.SerializedName;

public class GradeRequest {

    @SerializedName("student_id")
    private long studentId;

    @SerializedName("teacher_id")
    private long teacherId;

    @SerializedName("value")
    private int value;

    public GradeRequest() {
    }

    public GradeRequest(long studentId, long teacherId, int value) {
        this.studentId = studentId;
        this.teacherId = teacherId;
        this.value = value;
    }

    public long getStudentId() {
        return studentId;
    }

    public void setStudentId(long studentId) {
        this.studentId = studentId;
    }

    public long getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(long teacherId) {
        this.teacherId = teacherId;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}