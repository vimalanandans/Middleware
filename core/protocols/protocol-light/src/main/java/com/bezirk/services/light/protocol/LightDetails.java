package com.bezirk.services.light.protocol;

public class LightDetails {

    private String hubIp;
    private String hubMac;
    private Integer lightNumber;
    private HueVocab.Color lightState;

    public HueVocab.Color getLightState() {
        return lightState;
    }

    public void setLightState(HueVocab.Color lightState) {
        this.lightState = lightState;
    }

    public String getHubIp() {
        return hubIp;
    }

    public void setHubIp(String hubIp) {
        this.hubIp = hubIp;
    }

    public String getHubMac() {
        return hubMac;
    }

    public void setHubMac(String hubMac) {
        this.hubMac = hubMac;
    }

    public Integer getLightNumber() {
        return lightNumber;
    }

    public void setLightNumber(Integer lightNumber) {
        this.lightNumber = lightNumber;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((hubIp == null) ? 0 : hubIp.hashCode());
        result = prime * result + ((hubMac == null) ? 0 : hubMac.hashCode());
        result = prime * result
                + ((lightNumber == null) ? 0 : lightNumber.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        LightDetails other = (LightDetails) obj;
        if (hubIp == null) {
            if (other.hubIp != null)
                return false;
        } else if (!hubIp.equals(other.hubIp))
            return false;
        if (hubMac == null) {
            if (other.hubMac != null)
                return false;
        } else if (!hubMac.equals(other.hubMac))
            return false;
        if (lightNumber == null) {
            if (other.lightNumber != null)
                return false;
        } else if (!lightNumber.equals(other.lightNumber))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "LightDetails [hubIp=" + hubIp + ", hubMac=" + hubMac
                + ", lightNumber=" + lightNumber + "]";
    }

}
