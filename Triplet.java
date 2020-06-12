public class Triplet{

    public int first;
    public int second;
    public int third;

    Triplet(int first, int second, int third){
        this.first = first;
        this.second = second;
        this.third = third;
    }

    public boolean compareLine(Triplet triplet){
        return triplet.second == this.second;
    }

    public boolean compareColumn(Triplet triplet){
        return triplet.third == this.third;
    }

    @Override
    public String toString(){
        return "(" + first + ", " + second + ", " + third + ")";
    }
}
