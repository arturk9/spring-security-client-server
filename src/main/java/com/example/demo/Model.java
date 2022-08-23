package com.example.demo;

public class Model {

    public Model () {}

    public Model (String name, int age) {
        this.name = name;
        this.age = age;
    }

    private String name;

    private int age;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    @Override
    public String toString() {
        return "Model{" +
                "name='" + name + '\'' +
                ", age=" + age +
                '}';
    }
}
