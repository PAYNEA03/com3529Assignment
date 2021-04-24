package assignmentFiles.subjectFiles;

public class Cuboid {
    public boolean cube = false;
    public int width;
    public int length;
    public int height;

    public Cuboid(Rectangle rect1, Rectangle rect2, Rectangle rect3) throws Exception{
        length = rect1.side1;
        width = rect2.side1;
        height = rect3.side1;
        if (length==width&&width==height){
            cube = true;
        }
        if (length == rect2.side2 && width == rect3.side2 && height == rect1.side2){

        }
        else if (length == rect3.side2 && width == rect1.side2 && height == rect2.side2){

        }
        else {
            throw new Exception();
        }
    }

    public Cuboid(Rectangle rect1, Rectangle rect2) throws Exception{
        if (rect1.side1==rect1.side2 && rect1.side1==rect2.side1 && rect2.side1==rect2.side2){
            width = rect1.side1;
            length = rect1.side1;
            height = rect1.side1;
            cube = true;
        }
        else {
            throw new Exception();
        }
    }
}
