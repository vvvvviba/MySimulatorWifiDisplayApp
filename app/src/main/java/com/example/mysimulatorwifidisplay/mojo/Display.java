package com.example.mysimulatorwifidisplay.mojo;

/**
 * @ClassName: Display
 * @Description:
 * @Author: shuailin.wang
 * @CreateDate: 2023/7/18
 */
public class Display {
    /**
     * Device: 客厅的小米盒子_2bdc
     * deviceAddress: 5e:c5:63:d1:2b:dc
     * primary type: 7-0050F204-1
     * secondary type: null
     * wps: 392
     * grpcapab: 0
     * devcapab: 33
     * status: 3
     * wfdInfo: WFD enabled: trueWFD
     * DeviceInfo: 17
     * WFD CtrlPort: 7236
     * WFD MaxThroughput: 50
     * WFD R2 DeviceInfo: -1
     * vendorElements: null
     */
    private String name;
    private String address;
    private String primaryType;

    public Display(String name, String address, String primaryType) {
        this.name = name;
        this.address = address;
        this.primaryType = primaryType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPrimaryType() {
        return primaryType;
    }

    public void setPrimaryType(String primaryType) {
        this.primaryType = primaryType;
    }
}
