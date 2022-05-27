package com.example.pika2;

public class TaskModel {
    private String task_name;
    private int task_level;
    private int task_img;
    private String task_status;

    public TaskModel(String task_name, int task_level, int task_img, String task_status) {
        this.task_name = task_name;
        this.task_level = task_level;
        this.task_img = task_img;
        this.task_status = task_status;
    }

    public int getTask_img() {
        return task_img;
    }

    public void setTask_img(int task_img) {
        this.task_img = task_img;
    }

    public String getTask_name() {
        return task_name;
    }

    public void setTask_name(String task_name) {
        this.task_name = task_name;
    }

    public int getTask_level() {
        return task_level;
    }

    public void setTask_level(int task_level) {
        this.task_level = task_level;
    }

    public String isTask_status() {
        return task_status;
    }

    public void setTask_status(String task_status) {
        this.task_status = task_status;
    }
}
