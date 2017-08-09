package com.github.osiegmar.java9test.app;

import com.github.osiegmar.java9test.api.HelloService;

public class Example {

    public static void main(String[] args) {
        System.out.println(new HelloService().sayHello("Java 9"));
    }

}
