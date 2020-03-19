package com.company.ToDoList.leftview;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;

public class ToDoData {
    public static ToDoData instance = new ToDoData();
    private String fileName = "ToDoList.txt";

    ObservableList<TodoItems> todoitem;
    DateTimeFormatter formatter;

    public static ToDoData getInstance() {
        return instance;
    }

    private ToDoData() {
        formatter = DateTimeFormatter.ISO_DATE;
    }

    public ObservableList<TodoItems> getTodoitem() {
        return todoitem;
    }

//     public void setTodoitem(List<TodoItems> todoitem) {
//   this.todoitem = todoitem;
//   }

    public void addTodoItem(TodoItems item) {
        todoitem.add(item);
    }

    public void loadTodoItems() throws IOException {
        todoitem = FXCollections.observableArrayList();
        Path paths = Paths.get(fileName);
        BufferedReader br = Files.newBufferedReader(paths);
        String input;
        try {
            while ((input = br.readLine()) != null) {
                String[] itemPieces = input.split("\t");
                String shortDescription = itemPieces[0];
                String details = itemPieces[1];
                String dateString = itemPieces[2];
                LocalDate date = LocalDate.parse(dateString, formatter);
                TodoItems todoitems = new TodoItems(shortDescription, details, date);
                todoitem.add(todoitems);
            }

        } finally {
            if (br != null) {
                br.close();
            }
        }
    }

    public void storeTodoItems() throws IOException {
        Path paths = Paths.get(fileName);
        BufferedWriter bw = Files.newBufferedWriter(paths);

        try {
            Iterator<TodoItems> iter = todoitem.iterator();
            while (iter.hasNext()) {
                TodoItems item = iter.next();
                bw.write(String.format("%s\t%s\t%s\t",
                        item.getShortDescription(), item.getDetails(), item.getDeadline()
                ));
                bw.newLine();
            }


        } finally {
            if (bw != null) {
                bw.close();
            }
        }
    }

    public void deleteItem(TodoItems item ){
        todoitem.remove(item);
    }

}
