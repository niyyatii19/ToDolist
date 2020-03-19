package com.company.ToDoList;

import com.company.ToDoList.leftview.ToDoData;
import com.company.ToDoList.leftview.TodoItems;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.util.Callback;
import javafx.scene.input.KeyEvent;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class Controller {

    private List<TodoItems> todoitems;

    @FXML
    private ListView<TodoItems> todolist;
    @FXML
    private TextArea details;
    @FXML
    private Label deadline;
    @FXML
    private BorderPane mainBorderPane;
    @FXML
    private ContextMenu listItems;
    @FXML
    private ToggleButton toggleButton;

    private Predicate<TodoItems> wantAll;
    private Predicate<TodoItems> wantTodays;

    private FilteredList<TodoItems> filteredList;

     public void initialize(){
         listItems = new ContextMenu();
         MenuItem deleteItem = new MenuItem("Delete");
         deleteItem.setOnAction(new EventHandler<ActionEvent>() {
             @Override
             public void handle(ActionEvent actionEvent) {
                 TodoItems item = todolist.getSelectionModel().getSelectedItem();
                 delete(item);
             }
         });
         listItems.getItems().addAll(deleteItem);
//        TodoItems item1 = new TodoItems("Mike's Birthday", "Buy a Birthday Card for 7 year old",
//                 LocalDate.of(2019, 10, 10));
//         TodoItems item2 = new TodoItems("Mike's Birthday", "Buy a Birthday Card for 7 year old",
//                 LocalDate.of(2019, 12, 9));
//       TodoItems item3 = new TodoItems("Mike's Birthday", "Buy a Birthday Card for 7 year old",
//                 LocalDate.of(2019, 11, 9));
//       todoitems = new ArrayList<TodoItems>();
//         todoitems.add(item1);
//         todoitems.add(item2);
//         todoitems.add(item3);
//        ToDoData.getInstance().setTodoitem(todoitems);

          todolist.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<TodoItems>() {
              @Override
              public void changed(ObservableValue<? extends TodoItems> observableValue, TodoItems todoItems, TodoItems t1) {
                  if(t1!= null){
                      TodoItems item = todolist.getSelectionModel().getSelectedItem();
                      details.setText(item.getDetails());
                      DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd MMMM, YYYY");
                      deadline.setText(dtf.format(item.getDeadline()));
                  }
              }
          });
          wantAll = new Predicate<TodoItems>() {
              @Override
              public boolean test(TodoItems todoItems) {
                  return true;
              }
          };
          wantTodays = new Predicate<TodoItems>() {
              @Override
              public boolean test(TodoItems todoItems) {
                  return todoItems.getDeadline().equals(LocalDate.now()) ;
              }
          };

          filteredList = new FilteredList<TodoItems>(ToDoData.getInstance().getTodoitem(),wantAll);


         SortedList<TodoItems> sortedList = new SortedList<>(filteredList,
                 new Comparator<TodoItems>() {
                     @Override
                     public int compare(TodoItems o1, TodoItems o2) {
                         return o1.getDeadline().compareTo(o2.getDeadline());
                     }
                 });
          todolist.setItems(sortedList);
          todolist.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
          todolist.getSelectionModel().selectFirst();

          todolist.setCellFactory(new Callback<ListView<TodoItems>, ListCell<TodoItems>>() {
              @Override
              public ListCell<TodoItems> call(ListView<TodoItems> todoItemsListView) {
                  ListCell<TodoItems>cell = new ListCell<TodoItems>(){

                      @Override
                      protected void updateItem(TodoItems todoItems, boolean b) {
                          super.updateItem(todoItems, b);
                          if(b){
                              setText(null);
                          }else {
                              setText(todoItems.getShortDescription());
                              if(todoItems.getDeadline().isBefore(LocalDate.now().plusDays(1))){
                                  setTextFill(Color.RED);
                              } else if(todoItems.getDeadline().equals(LocalDate.now().plusDays(1))){
                                  setTextFill(Color.GREEN);
                              }
                          }
                      }
                  };
                  cell.emptyProperty().addListener(
                          (obs, wasEmpty, isNowEmpty)->{
                              if(isNowEmpty){
                                  cell.setContextMenu(null);
                              }else{
                                  cell.setContextMenu(listItems);
                              }
                  });

                  return cell;

              }

          });
     }

     public void showDialog(){
         Dialog<ButtonType> dialog = new Dialog<>();
         dialog.initOwner(mainBorderPane.getScene().getWindow());
         dialog.setTitle("Add New todo item");
         dialog.setHeaderText("Use this to add new item");
         FXMLLoader fxmlLoader = new FXMLLoader();
         fxmlLoader.setLocation(getClass().getResource("todoitemDialog.fxml"));
         try{
             dialog.getDialogPane().setContent(fxmlLoader.load());
         }catch(IOException e){
             System.out.println(e.getMessage());
             e.printStackTrace();
             return ;
         }

         dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
         dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);

         Optional<ButtonType> result = dialog.showAndWait();
         if(result.isPresent() && result.get() == ButtonType.OK){
             DialogController controller = fxmlLoader.getController();
             TodoItems newItem = controller.processing();
           //  todolist.getItems().setAll(ToDoData.getInstance().getTodoitem());
             todolist.getSelectionModel().select(newItem);
         }
     }

     public void handleSelectedItem(){
         TodoItems item = todolist.getSelectionModel().getSelectedItem();
         details.setText(item.getDetails());
         deadline.setText(item.getDeadline().toString());
     }

     @FXML
     public void handleKeyPress(KeyEvent key){
         TodoItems selectedItem = todolist.getSelectionModel().getSelectedItem();
         if(selectedItem!= null){
             if(key.getCode().equals(KeyCode.DELETE)){
                 delete(selectedItem);
             }
         }
     }

     public void delete(TodoItems item){
         Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
         alert.setTitle("Delete the item");
         alert.setHeaderText("Delete item: "+item.getShortDescription());
         alert.setContentText("Are you sure to delete the item?? Press Ok to delte otherwise press cancel");
         Optional<ButtonType> result = alert.showAndWait();
         if(result.isPresent() && (result.get() == ButtonType.OK)){
             ToDoData.getInstance().deleteItem(item);
         }
     }

     public void handleToggle(){
         TodoItems selected = todolist.getSelectionModel().getSelectedItem();
         if(toggleButton.isSelected()){
             filteredList.setPredicate(wantTodays);
             if(filteredList.isEmpty()){
                 details.clear();
                 deadline.setText("");
             }else if(filteredList.contains(selected)){
                 todolist.getSelectionModel().select(selected);
             }
         }else{
             filteredList.setPredicate(wantAll);
             todolist.getSelectionModel().select(selected);
         }

     }

     public void handleExit(){
         Platform.exit();
     }

}
