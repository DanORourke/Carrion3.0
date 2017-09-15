import GUI.Entry;
import Server.Server;

public class Main {
    public static void main(String[] args) {
        if (args.length == 0){
            javax.swing.SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    new Entry();
                }
            });
        }else if (args.length == 1 && isInteger(args[0], 10)){
            new Server(Integer.parseInt(args[0]));
        }else{
            System.out.println("add port argument to start in Server mode");
        }
    }

    private static boolean isInteger(String s, int radix) {
        if(s.isEmpty()) return false;
        for(int i = 0; i < s.length(); i++) {
            if(i == 0 && s.charAt(i) == '-') {
                if(s.length() == 1) return false;
                else continue;
            }
            if(Character.digit(s.charAt(i),radix) < 0) return false;
        }
        return true;
    }
}

