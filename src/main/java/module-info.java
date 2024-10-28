module mg.quizz {
    requires javafx.graphics;
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    
    
    
    
    
    opens mg.quizz.admin to javafx.fxml;
    opens mg.quizz.user to javafx.fxml;
    exports mg.quizz.user;
    exports mg.quizz.admin;
}
