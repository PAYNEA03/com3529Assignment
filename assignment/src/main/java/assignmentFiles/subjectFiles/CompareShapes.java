package assignmentFiles.subjectFiles;
import assignmentFiles.subjectFiles.Cuboid;
import assignmentFiles.subjectFiles.Rectangle;

public class CompareShapes {
    public enum Similarity {
        COMPLETELY_DIFFERENT,
        ONE_SIDE_SAME,
        TWO_SIDES_SAME,
        SAME_AREA,
        SAME_VOLUME,
        SAME_CUBOID
    }

    public static Similarity compareCuboids(Cuboid cuboid1, Cuboid cuboid2) {
        if (cuboid1.cube && cuboid2.cube){
            return Similarity.SAME_CUBOID;
        }
        int sides_same = 0;
        if (cuboid1.length == cuboid2.length){
            sides_same++;
        }
        if (cuboid1.width == cuboid2.width){
            sides_same++;
        }
        if (cuboid1.height == cuboid2.height){
            sides_same++;
        }
        if (cuboid1.width == cuboid2.height){
            sides_same++;
        }
        if (cuboid1.height == cuboid2.width){
            sides_same++;
        }
        if (cuboid1.length == cuboid2.width){
            sides_same++;
        }
        if (cuboid1.length == cuboid2.height){
            sides_same++;
        }
        if (cuboid1.width == cuboid2.length){
            sides_same++;
        }
        if (cuboid1.height == cuboid2.length){
            sides_same++;
        }
        if (sides_same >= 3){
            return Similarity.SAME_CUBOID;
        }
        else if (sides_same == 2){
            return Similarity.TWO_SIDES_SAME;
        }
        else if (sides_same == 1){
            return Similarity.ONE_SIDE_SAME;
        }
        else if (cuboid1.length*cuboid1.width*cuboid1.height==cuboid2.length*cuboid2.width*cuboid2.height){
            return Similarity.SAME_VOLUME;
        }
        return Similarity.COMPLETELY_DIFFERENT;
    }

    public static Similarity compareRectangles(Rectangle rect1, Rectangle rect2) {
        int sides_same = 0;
        if (rect1.side1 == rect2.side1){
            sides_same++;
            if (rect1.side2 == rect2.side2){
                sides_same++;
            }
        }
        else if (rect1.side1 == rect2.side2){
            sides_same++;
            if (rect1.side2 == rect2.side1){
                sides_same++;
            }
        }
        else if (rect1.area == rect2.area){
            return Similarity.SAME_AREA;
        }
        if (sides_same == 1){
            return Similarity.ONE_SIDE_SAME;
        }
        else if (sides_same == 2){
            return Similarity.TWO_SIDES_SAME;
        }
        return Similarity.COMPLETELY_DIFFERENT;
    }

    static int hammingDist(String str1, String str2)
    {
        int i = 0, count = 0;
        while (i < str1.length())
        {
            if (str1.charAt(i) != str2.charAt(i))
                count++;
            i++;
        }
        return count;
    }
}