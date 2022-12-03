package com.hfad.todolist;

public class Todo {
        private String name;
        public static final Todo[] Todos = {
                new Todo("School"),
                new Todo("Job"),
                new Todo("Internship"),
        };
        //Each Workout has a name and description
        private Todo(String name) {
            this.name = name;
        }
        public String getName() {
            return name;
        }
        public String toString() {
            return this.name;
        }
}
