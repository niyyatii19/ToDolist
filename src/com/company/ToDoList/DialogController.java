package com.company.ToDoList;

import com.company.ToDoList.leftview.ToDoData;
import com.company.ToDoList.leftview.TodoItems;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.time.LocalDate;

public class DialogController {
    @FXML
    private TextField shortDescriptionField;
    @FXML
    private TextArea detailArea;
    @FXML
    private DatePicker deadlineDate;

    public TodoItems processing(){
        String shortDescription = shortDescriptionField.getText().trim();
        String details= detailArea.getText().trim();
        LocalDate deadline = deadlineDate.getValue();
        TodoItems newitem = new TodoItems(shortDescription, details, deadline);
        ToDoData.getInstance().addTodoItem(newitem);
        return newitem;
    }
}
