
/**
 * lanterna_test
 */
import java.io.IOException;
import java.util.ArrayList;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;


public class lanternaDisplay {

    public DefaultTerminalFactory dtf;
    public Terminal tm;
    public lanternaDisplay() throws IOException{
        dtf = new DefaultTerminalFactory();
        // try {           
        tm= dtf.createTerminal();
        System.out.println("terminal: "+tm);
        // } catch (Exception e) {
        //    throw new IOException("constr: "+e.getMessage());
        // }
    }
    public void putChar(char value,int x,int y,TextColor colorFG,TextColor colorBG) throws IOException{
        try {
            tm.setCursorPosition(x, y);
            tm.setBackgroundColor(colorBG);
            tm.setForegroundColor(colorFG);
            tm.putCharacter(value);    
        } catch (Exception e) {
            throw new IOException("putChar: "+e.getMessage());
        }        
    }

    public void putString(String value, int x,int y, TextColor colorFG,TextColor colorBG) throws IOException{
        try {
            tm.setBackgroundColor(colorBG);
            tm.setForegroundColor(colorFG);            
            for (char var : value.toCharArray()) {
                tm.setCursorPosition(x,y);
                
                tm.putCharacter(var);
                x++;
            } 
        } catch (Exception e) {
            throw new IOException("putString: "+e.getMessage());
        }
    }
    public void flushTerminal() throws IOException{
        try {
            tm.flush();
        } catch (Exception e) {
            throw new IOException("flush: "+e.getMessage());
        }
    }

    public void closeTerminal() throws IOException {
        try {
            tm.close();
        } catch (Exception e) {
            throw new IOException("close: "+e.getMessage());
        }
    }

    public void clearTerminal() throws IOException {
        try {
            tm.clearScreen();
        } catch (Exception e) {
            throw new IOException("clear: "+e.getMessage());
        }
    }

    public void mapToTerminal(ArrayList<int[]> mapTM,ArrayList<Character> mapChr,TextColor bg,TextColor fg) throws IOException {
        int i=0;
        for (int[] var : mapTM) {
            try {
                putChar(mapChr.get(i),var[0],var[1],fg,bg);
                
            } catch (Exception e) {
                throw new IOException("map: "+e.getMessage());
            }
            i++;
        }
    }
    // Test run to see if Lanterna works
    // public static void main(String[] args) {
    //     DefaultTerminalFactory dtf = new DefaultTerminalFactory();
    //     System.out.println(dtf.toString());
    //     Terminal tm= null;
    //     try {
    //         tm= dtf.createTerminal();
    //         System.out.println(tm.toString());
    //         tm.putCharacter('H');
    //         tm.putCharacter('i');
    //         tm.putCharacter('\n');
    //         tm.flush();
    //         Thread.sleep(1000);
    //         TerminalPosition startPosition;
    //         startPosition=tm.getCursorPosition() ;
            
    //         tm.setCursorPosition(startPosition.withRelativeColumn(3).withRelativeRow(2));
    //         tm.flush();
    //         Thread.sleep(2000);
    //         tm.putCharacter('Y');
    //         tm.putCharacter('o');
    //         tm.putCharacter('\n');
    //         tm.flush();
    //         Thread.sleep(1000);
    //     } catch (Exception e) {
    //       e.printStackTrace();
    //     }
    //     finally {
    //         if (tm!=null){
    //             try {
    //                 tm.close();
    //             }
    //             catch (IOException e){e.printStackTrace();}
    //         }
    //     }
    // }
    
}