package com.java.renyi.db;

public class PandemicStatus {
    public String region;
    public Integer [] status;
    // status.size() == 4
    // status[0]: CONFIRMED
    // status[1]: SUSPECTED
    // status[2]: CURED
    // status[3]: DEAD

    PandemicStatus(String region, String s){
        this.region = region;
        status = new Integer[4];
        String [] info = s.substring(1, s.length() - 1).split(",");
        for (int i = 0; i < 4; i++) {
            if ("null".equals(info[i])) {
                status[i] = null;
            } else {
                status[i] = Integer.parseInt(info[i]);
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder tmp = new StringBuilder();
        tmp.append(region);
        for (Integer i: status) {
            if (i != null)
                tmp.append(" "+i.toString());
            else
                tmp.append(" null");
        }
        return tmp.toString();
    }
}