package net.willwebberley.gowertides.classes;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Tide {
    public final int LOW = 0;
    public final int HIGH = 1;

    public Double height;
    public Calendar time;
    public double timeHours;
    public String type;

    public String getTimeString(){
        SimpleDateFormat sdf = new SimpleDateFormat("H:m");
        return sdf.format(time.getTime());
    }

    public int getType(){
        if(type.equals("high")){return HIGH;}
        else{return LOW;}
    }

       /* highTideTimes = new String[3];
        highTideHeights = new String[3];
        lowTideTimes = new String[2];
        lowTideHeights = new String[2];

        highTideTimes[0] = info.getString(7);
        highTideTimes[1] = info.getString(11);
        highTideTimes[2] = info.getString(15);

        highTideHeights[0] = info.getString(8);
        highTideHeights[1] = info.getString(12);
        highTideHeights[2] = info.getString(16);

        lowTideTimes[0] = info.getString(9);
        lowTideTimes[1] = info.getString(13);

        lowTideHeights[0] = info.getString(10);
        lowTideHeights[1] = info.getString(14);*/



    /*
    * Get an array of tide heights to plot on graph, stripping out the numeric values.
     */
    /*public Double[] getTideHeights(){
        ArrayList heights = new ArrayList();
        for(int i = 0; i < highTideHeights.length; i++){
            String highTideStripped = highTideHeights[i].replaceAll("\\s","");
            if(!highTideStripped.equals("")){
                String[] tokens = highTideStripped.split("m");

                if(i == 0){
                    heights.add(2.0);
                }
                heights.add(Double.parseDouble(tokens[0]));
                if(i == 1 && lowTideTimes[1].equals("")){
                    heights.add(2.0);
                }
                if(i == 2){
                    heights.add(2.0);
                }
            }
            if(i < 2){
                String lowTideStripped = lowTideHeights[i].replaceAll("\\s","");
                if(!lowTideStripped.equals("")){
                    String[] tokens = lowTideStripped.split("m");

                    if(i == 0 && heights.size() == 0){
                        heights.add(8.0);
                    }
                    heights.add(Double.parseDouble(tokens[0]));
                    if(i == 1 && highTideTimes[2].equals("")){
                        heights.add(8.0);
                    }
                }
            }
        }
        return (Double[]) heights.toArray(new Double[heights.size()]);
    }*/

    /*
    * Get the time of tide events for plotting on the graph.
    */
   /* public Double[] getTideTimesPlot(){
        ArrayList times = new ArrayList();
        for(int i = 0; i < highTideTimes.length; i++){
            String highTimeStripped = highTideTimes[i].trim();
            if(!highTimeStripped.equals("")){
                try {
                    Date date = dayView.dayDateFormatter.parse(highTideTimes[i].replace("GMT", ""));
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(date);
                    //if(highTideTimes[i].contains("BST")){
                    //cal.add(Calendar.HOUR, -1);
                    //}
                    int min = cal.get(Calendar.MINUTE);
                    Double minOfHour = (min+0.0)/60.0;
                    int hour = cal.get(Calendar.HOUR_OF_DAY);

                    if(i == 0){
                        times.add(hour-6.0);
                    }
                    times.add(hour+minOfHour);
                    if(i == 1 && lowTideTimes[1].equals("")){
                        times.add(hour+10.0);
                    }
                    if(i == 2){
                        times.add(hour+6.0);
                    }
                }
                catch (ParseException e) {
                    System.out.println(e);
                }
            }
            if(i < 2){
                String lowTimeStripped = lowTideTimes[i].trim();
                if(!lowTimeStripped.equals("")){
                    try {
                        Date date = dayView.dayDateFormatter.parse(lowTideTimes[i].replace("GMT", ""));
                        Calendar cal = Calendar.getInstance();
                        cal.setTime(date);
                        //if(lowTideTimes[i].contains("BST")){
                        //cal.add(Calendar.HOUR, -1);
                        //}
                        int min = cal.get(Calendar.MINUTE);
                        Double minOfHour = (min+0.0)/60.0;
                        int hour = cal.get(Calendar.HOUR_OF_DAY);

                        if(i == 0 && times.size() == 0){
                            times.add(hour-6.0);
                        }
                        times.add(hour+minOfHour);
                        if(i == 1 && highTideTimes[2].equals("")){
                            times.add(hour+6.0);
                        }
                    }
                    catch (ParseException e) {
                        System.out.println(e);
                    }
                }
            }
        }
        return (Double[]) times.toArray(new Double[times.size()]);
    }*/

    /*
    * Get String[] representing order of tide events for the day. (e.g. high, low, high, low)
     */
    /*public String[] getTideTypes(){
        ArrayList types = new ArrayList();
        for(int i = 0; i < highTideTimes.length; i++){
            String highTimeStripped = highTideTimes[i].trim();
            if(!highTimeStripped.equals("")){
                types.add("high");
            }
            if(i < 2){
                String lowTimeStripped = lowTideTimes[i].trim();
                if(!lowTimeStripped.equals("")){
                    types.add("low");
                }
            }
        }
        return (String[]) types.toArray(new String[types.size()]);
    }*/

    /*
    * Get Calendar[] representing the time of the tide events for the day (in same order as above method).
     */
    /*public Calendar[] getTideTimes(){
        ArrayList times = new ArrayList();
        for(int i = 0; i < highTideTimes.length; i++){
            String highTimeStripped = highTideTimes[i].trim();
            if(!highTimeStripped.equals("")){
                try {
                    Date date = dayView.dayDateFormatter.parse(highTideTimes[i].replace("BST", "GMT"));
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(date);
                    //if(highTideTimes[i].contains("BST")){
                    //cal.add(Calendar.HOUR, -1);
                    //}
                    times.add(cal);
                }
                catch (ParseException e) {
                    System.out.println(e);
                }
            }
            if(i < 2){
                String lowTimeStripped = lowTideTimes[i].trim();
                if(!lowTimeStripped.equals("")){
                    try {
                        Date date = dayView.dayDateFormatter.parse(lowTideTimes[i].replace("BST", "GMT"));
                        Calendar cal = Calendar.getInstance();
                        cal.setTime(date);
                        //if(lowTideTimes[i].contains("BST")){
                        //	cal.add(Calendar.HOUR, -1);
                        //}
                        times.add(cal);
                    }
                    catch (ParseException e) {
                        System.out.println(e);
                    }
                }
            }
        }
        return (Calendar[]) times.toArray(new Calendar[times.size()]);
    }*/
}

