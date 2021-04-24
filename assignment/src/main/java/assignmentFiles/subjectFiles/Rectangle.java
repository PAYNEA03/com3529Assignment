package assignmentFiles.subjectFiles;

public class Rectangle {
    int side1;
    int side2;
    int area;
    boolean square = false;
    public Rectangle(int side1, int side2){
        this.side1 = side1;
        this.side2 = side2;
        area = side1*side2;
        square = true;
    }
    public Rectangle(int side1){
        this.side1 = side1;
        area = side1*side1;
    }
}
