public class Tokens {
    public int type;
    public String value;

    public Tokens(int _type, String _value) {
        type = _type;
        value = _value;
    }

    void displayToken(){
        System.out.printf("Id: " + type + " Value: " + value + "\n");
    }

}