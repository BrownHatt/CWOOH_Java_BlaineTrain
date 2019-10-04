import java.io.File;
import java.io.FileNotFoundException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

import com.googlecode.lanterna.TextColor;

class trackPiece  {
    int tpID;
    int x,y;
    char trackType;    
    public trackPiece(int cx,int cy,char ctrack,int id){
        x=cx;y=cy;trackType=ctrack; tpID=id;
    }

    public int[] giveCoord(){
        return new int[]{x,y};
    }

    @Override
    public boolean equals(Object obj){
  
        trackPiece tp=(trackPiece) obj;
        if (obj==null) return false;
        if (x==tp.x && y==tp.y) return true;
        return false;
    }

    @Override
    public int hashCode () {
        return 1;
    }
}

class Train {
    int type; // 1 = normal - 2 = express
    public String origTchu;
    public int direction;
    public int enginePos;
    ArrayList<Integer> wagons;
    int timeWait=0;
    boolean atStation;
    public Train(String train, int pos,int trackLength){       
        atStation=false;
        origTchu=train;              
        enginePos=pos;
        direction = (int)train.charAt(0)>96 ? 1 : -1;
        type= (int)train.charAt(0)=='X' || (int)train.charAt(0)=='x' ? 2 : 1;
        wagons = new ArrayList<Integer>();
        for (int i = 0; i < train.length()-1; i++) {
            pos+=direction*-1;
            pos = pos==trackLength ? 0 : pos<0 ? trackLength-1 : pos;
            wagons.add(pos);
        }
    }

    public void Move(int trackLength){
        timeWait--;
        if (atStation && timeWait>=0) { return; }
        if (atStation) {atStation=false;}        
      
        enginePos= enginePos+direction>=trackLength ? 0 :
                enginePos+direction<0 ? enginePos=trackLength-1 : enginePos+direction ; 

        for (int i = 0; i < wagons.size(); i++) {
            int wagon=wagons.get(i);
            wagon=  wagon+direction>=trackLength ? 0 :
                    wagon+direction<0 ? wagon=trackLength-1 : wagon+direction ;
            wagons.set(i,wagon); 
        }
    }
    
    public void arriveStation(){        
        if (atStation || type==2) return;
        atStation=true;
        timeWait=wagons.size();        
    }

  
}

public class cw_blainetrain {

    // =======================================
    // Blaine is a pain, and that is the truth
    // =======================================
    int[] startTrack;
    List<trackPiece> railWay;
    int trackLength;
    Train t1,t2;
    int timer;
    lanternaDisplay lt;
    public cw_blainetrain(String filename) throws FileNotFoundException {

        File fr= new File(filename);
        Scanner sc= new Scanner(fr);
        ArrayList<char[]> trackText= new ArrayList<char[]>();        
        while (sc.hasNextLine()){             
            trackText.add(sc.nextLine().toCharArray());
        }
        sc.close();        
        startTrack=new int[]{String.copyValueOf(trackText.get(0)).indexOf("/"),0};
        railWay=makeTrackArray(trackText);
        trackLength=railWay.size();
    }
    private ArrayList<trackPiece> makeTrackArray(ArrayList<char[]> trackMap){
        int[] curTrack=new int[]{startTrack[0],startTrack[1]};
        int[] clockDir=new int[]{0,-1};
        int[] nextCoord;
        ArrayList<trackPiece> result=new ArrayList<trackPiece>();
        char trackChar;
        int tpID=0;
        do {
            trackChar=trackMap.get(curTrack[1])[curTrack[0]];

            result.add(new trackPiece(curTrack[0], curTrack[1], trackChar,tpID));
            nextCoord=calcNextCoord(clockDir, trackChar, curTrack, trackMap);
  
            if (nextCoord[2]==0 && nextCoord[3]==0) break;
            curTrack=Arrays.copyOfRange(nextCoord, 0,2);
            clockDir=Arrays.copyOfRange(nextCoord, 2, 4);
            tpID++;
        }
        while(!(curTrack[0]==startTrack[0] && curTrack[1]==startTrack[1]));
        return result;
    }


    private int[] calcNextCoord(int[] dir,char input,int[] coord,ArrayList<char[]> trackMap){
        int[] result = new int[]{0,0,0,0};
         // to check / and \, map rev of diry to x and rev of dirx to y
        int xtc=dir[1]*-1;
        int ytc=dir[0]*-1;
        switch (input) {
            case '/':
                    
                    if (dir[0]!=0 && dir[1]!=0) {
                        if ( coord[1]+1<trackMap.size() && coord[1]-1>-1 &&                         
                        coord[0]+1<trackMap.get(coord[1]+1).length && coord[0]-1>-1 &&                     
                        ( trackMap.get(coord[1]+dir[1])[coord[0]+dir[0]]=='/' || 
                        trackMap.get(coord[1]+dir[1])[coord[0]+dir[0]]=='X' || 
                        trackMap.get(coord[1]+dir[1])[coord[0]+dir[0]]=='S'))
                        {result=new int[]{coord[0]+dir[0],coord[1]+dir[1],dir[0],dir[1]};break;}
                    }    
                        
                    if (coord[0]+xtc<trackMap.get(coord[1]).length && trackMap.get(coord[1])[coord[0] + xtc] == '-') {
                        result= new int[]{coord[0]+xtc,coord[1],xtc,0};break;
                    }                                        
                    if (coord[1]+ytc>-1 && coord[1]+ytc<trackMap.size() && trackMap.get(coord[1]+ytc)[coord[0]] == '|'){
                        result= new int[]{coord[0],coord[1]+ytc,0,ytc};break;
                    } 
                    if (coord[1]-ytc>-1 && coord[1]-ytc<trackMap.size() && trackMap.get(coord[1]-ytc)[coord[0]] == '|'){
                        result= new int[]{coord[0],coord[1]-ytc,0,ytc*-1};break;
                    }                       
                                 
                            
                    if (dir[1]==0) {
                        result=new int[]{coord[0]+dir[0],coord[1]+(dir[0]*-1),dir[0],dir[0]*-1};break;
                    }
                    if (dir[0]==0) {
                        result=new int[]{coord[0]+dir[1]*-1,coord[1]+dir[1],dir[1]*-1,dir[1]};break;
                    }
                    
                    break;
            case '\\':
                    if (dir[0]!=0 && dir[1]!=0) {
                        if ( coord[1]+1<trackMap.size() && coord[1]-1>-1 &&                         
                        coord[0]+1<trackMap.get(coord[1]+1).length && coord[0]-1>-1 &&                     
                        ( trackMap.get(coord[1]+dir[1])[coord[0]+dir[0]]=='\\' || 
                        trackMap.get(coord[1]+dir[1])[coord[0]+dir[0]]=='X' || 
                        trackMap.get(coord[1]+dir[1])[coord[0]+dir[0]]=='S'))
                        {result=new int[]{coord[0]+dir[0],coord[1]+dir[1],dir[0],dir[1]};break;}
                    }
                    

                    if (coord[0]-xtc>-1 && trackMap.get(coord[1])[coord[0] - xtc] == '-') {
                        result= new int[]{coord[0]-xtc,coord[1],xtc*-1,0};break;
                    }                                        
                    if (coord[1]+ytc>-1 && coord[1]+ytc<trackMap.size() && trackMap.get(coord[1]+ytc).length<coord[0] && trackMap.get(coord[1]+ytc)[coord[0]] == '|'){
                        result= new int[]{coord[0],coord[1]+ytc,0,ytc};break;
                    } 
                    if (coord[1]-ytc>-1 && coord[1]-ytc<trackMap.size() && trackMap.get(coord[1]-ytc)[coord[0]] == '|'){
                        result= new int[]{coord[0],coord[1]-ytc,0,ytc*-1};break;
                    }  
                    
                    

                    if (dir[1]==0) {
                        result=new int[]{coord[0]+dir[0],coord[1]+(dir[0]),dir[0],dir[0]};break;
                    }
                    if (dir[0]==0) {
                        result=new int[]{coord[0]+dir[1],coord[1]+dir[1],dir[1],dir[1]};break;
                    }
                    
                    break;
            case '-':
                    result=new int[]{coord[0]+dir[0],coord[1]+dir[1],dir[0],dir[1]};
                    break;
            case '|':
                    result=new int[]{coord[0]+dir[0],coord[1]+dir[1],dir[0],dir[1]};
                    break;
            case '+':
                    result=new int[]{coord[0]+dir[0],coord[1]+dir[1],dir[0],dir[1]};
                    break;
            case 'X':
                    result=new int[]{coord[0]+dir[0],coord[1]+dir[1],dir[0],dir[1]};
                    break;
            case 'S':
                    result=new int[]{coord[0]+dir[0],coord[1]+dir[1],dir[0],dir[1]};
                    break;
            default:
      
                break;
        }
        return result;
    }

    public boolean checkCollision(){
        trackPiece t1coord=railWay.get(t1.enginePos);
        trackPiece t2coord=railWay.get(t2.enginePos);

        List<trackPiece> wagonsA=t1.wagons.stream().map(e -> railWay.get(e)).collect(Collectors.toList());
        List<trackPiece> wagonsB=t2.wagons.stream().map(e -> railWay.get(e)).collect(Collectors.toList());
        
        

        if (t1coord.equals(t2coord)) return true;
      

        if (wagonsA.size()!=wagonsA.stream().collect(Collectors.toSet()).size()) {return true;}
        if (wagonsB.size()!=wagonsB.stream().collect(Collectors.toSet()).size()) {return true;}

        for (trackPiece var : wagonsA) {
            if (var.equals(t1coord)) {return true;}
            if (var.equals(t2coord)) {return true;}
            for (trackPiece var2 : wagonsB) {
                if (var.equals(var2)) {return true;}                
            }
        }

        for (trackPiece var : wagonsB) {
            if (var.equals(t1coord)) {return true;}
            if (var.equals(t2coord)) {return true;}
            for (trackPiece var2 : wagonsA) {
                if (var.equals(var2)) {return true;}
            }
        }
        return false;                
       
    }

    public int trainCrash(final String track, final String aTrain, final int aTrainPos, final String bTrain, final int bTrainPos, final int limit) {
        t1=new Train(aTrain,aTrainPos,railWay.size());
        t2=new Train(bTrain,bTrainPos,railWay.size());
        timer=0;
       
        displayAllToTerminal();
        
        try {
            Thread.sleep(1000);
        } catch (Exception e) {            
        }
        boolean crashed=false;
        while (timer<=limit){
                              
           
            try {
                Thread.sleep(10);
            } catch (Exception e) {                
            }
            
            if (checkCollision()) {;crashed=true;break;}
            t1.Move(trackLength);
        
            t2.Move(trackLength);       
           
            if (railWay.get(t1.enginePos).trackType=='S') t1.arriveStation();
            if (railWay.get(t2.enginePos).trackType=='S') t2.arriveStation();

            displayAllToTerminal();
            timer++; 
        }
        displayAllToTerminal();
      
        return crashed ? timer : 
              timer==0 ? -1 : 
              timer<limit ? timer : -1 ;
    }

    public void displayFullTrain(Train t){
        System.out.print(t.origTchu+ 
                        ": "+t.enginePos+
                        railWay.get(t.enginePos).x+" - "+
                        railWay.get(t.enginePos).y+" -  wagons: ");
        for (int pos : t.wagons) {
            System.out.print(railWay.get(pos).x+"-"+railWay.get(pos).y+":");
        }
        System.out.println();

    }
    
    public void displayAllToTerminal(){
        
            ArrayList<int[]> mapCoords=new ArrayList<int[]>();
            ArrayList<Character> mapChars=new ArrayList<Character>();
            for (trackPiece var : railWay) {
                mapCoords.add(var.giveCoord());
                mapChars.add(var.trackType);
            }
            try {
                
                lt.mapToTerminal(mapCoords, mapChars, TextColor.ANSI.BLACK, TextColor.ANSI.WHITE);    
             
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
            mapCoords.clear();mapChars.clear(); 
            mapCoords.add(new int[]{railWay.get(t1.enginePos).x,railWay.get(t1.enginePos).y});
            mapChars.add(t1.origTchu.substring(0,1).toUpperCase().charAt(0));
            for (int pos : t1.wagons) {
                mapCoords.add(new int[]{railWay.get(pos).x,railWay.get(pos).y});
                mapChars.add(t1.origTchu.substring(0,1).toLowerCase().charAt(0));
            }
            try {
                lt.mapToTerminal(mapCoords, mapChars, TextColor.ANSI.RED, TextColor.ANSI.WHITE);    
             
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }

            mapCoords.clear();mapChars.clear(); 
            mapCoords.add(new int[]{railWay.get(t2.enginePos).x,railWay.get(t2.enginePos).y});
            mapChars.add(t2.origTchu.substring(0,1).toUpperCase().charAt(0));
            for (int pos : t2.wagons) {
                mapCoords.add(new int[]{railWay.get(pos).x,railWay.get(pos).y});
                mapChars.add(t2.origTchu.substring(0,1).toLowerCase().charAt(0));
            }
            try {
                lt.mapToTerminal(mapCoords, mapChars, TextColor.ANSI.GREEN, TextColor.ANSI.WHITE);    
                lt.putString("time: "+timer, 60, 1, TextColor.ANSI.WHITE, TextColor.ANSI.BLACK);
                lt.flushTerminal();
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
     

             
    }
    
    public static void main(String[] args) {

        try {           
            
            // cw_blainetrain cwb = new cw_blainetrain("traintrack_crashSelfDestruct.txt");
            
             cw_blainetrain cwb = new cw_blainetrain("traintrack.txt");

            // cw_blainetrain cwb = new cw_blainetrain("traintrack_CrashChickenRun.txt");

            // cw_blainetrain cwb = new cw_blainetrain("traintrack_nearMiss.txt");
           
            // cw_blainetrain cwb = new cw_blainetrain("traintrack_noCrashTricky.txt");

            // cw_blainetrain cwb = new cw_blainetrain("traintrack_limits.txt");

            // cw_blainetrain cwb = new cw_blainetrain("traintrack_random1.txt");

             //cw_blainetrain cwb = new cw_blainetrain("traintrack_random100.txt");

           //cw_blainetrain cwb = new cw_blainetrain("traintrack_another.txt");
            try {
                cwb.lt = new lanternaDisplay();  
            } catch (Exception e) {
                System.out.println("lanterna error: "+e.getMessage());
            }
            
            // System.out.println(cwb.trainCrash("","aA", 10 , "oooooooooooooooooooooooooO", 70, 1000));

             System.out.println(cwb.trainCrash("","Aaaa", 147 , "Bbbbbbbbbbb", 288, 1000));

            // System.out.println(cwb.trainCrash("","aaaA", 10 , "Bbbb", 40, 1000));        

            // System.out.println(cwb.trainCrash("","ooooooO", 10 , "xxxxxxX", 27, 1000));

            // System.out.println(cwb.trainCrash("","aaaA", 15 , "bbbB", 5, 1000));

            // System.out.println(cwb.trainCrash("","aaaA", 22 , "bbbbB", 0, 1000));

            // System.out.println(cwb.trainCrash("","bbbbbbbbbbbbbbbB", 11 , "Xxxx", 12, 1000));

            // System.out.println(cwb.trainCrash("","Ccccccccccccccccccccccccc", 64  , "Xxxxxxxx", 49, 2000));
            
           // System.out.println(cwb.trainCrash("","Eeeeeeeeeeeeeeeeeeeeeeeeeeeeeee", 7 , "Xxxx", 0, 100));

            try {
                Thread.sleep(1000);
            } catch (Exception e) {
         
            }
            try {
                cwb.lt.closeTerminal();   
            } catch (Exception e) {
                //TODO: handle exception
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }
  }