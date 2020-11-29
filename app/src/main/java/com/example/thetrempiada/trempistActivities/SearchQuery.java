package com.example.thetrempiada.trempistActivities;

import com.example.thetrempiada.driverActivities.DtaeAndTime;
import com.example.thetrempiada.driverActivities.LanLat;

public class SearchQuery {
    protected LanLat src,dst;
    protected DtaeAndTime dateTime;
    protected int rangeSrc,rangeDst;

    public SearchQuery(LanLat src, LanLat dst, DtaeAndTime dateTime, int rangeSrc, int rangeDst) {
        this.src = src;
        this.dst = dst;
        this.dateTime = dateTime;
        this.rangeSrc = rangeSrc;
        this.rangeDst = rangeDst;
    }

    public LanLat getSrc() {
        return src;
    }

    public void setSrc(LanLat src) {
        this.src = src;
    }

    public LanLat getDst() {
        return dst;
    }

    public void setDst(LanLat dst) {
        this.dst = dst;
    }

    public DtaeAndTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(DtaeAndTime dateTime) {
        this.dateTime = dateTime;
    }

    public int getRangeSrc() {
        return rangeSrc;
    }

    public void setRangeSrc(int rangeSrc) {
        this.rangeSrc = rangeSrc;
    }

    public int getRangeDst() {
        return rangeDst;
    }

    public void setRangeDst(int rangeDst) {
        this.rangeDst = rangeDst;
    }
}
