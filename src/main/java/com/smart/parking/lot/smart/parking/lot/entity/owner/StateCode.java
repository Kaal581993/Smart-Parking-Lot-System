package com.smart.parking.lot.smart.parking.lot.entity.owner;



public enum StateCode {
    ANDHRA_PRADESH("AP"),
    ARUNACHAL_PRADESH("AR"),
    ASSAM("AS"),
    BIHAR("BR"),
    CHHATTISGARH("CG"),
    GOA("GA"),
    GUJARAT("GJ"),
    HARYANA("HR"),
    HIMACHAL_PRADESH("HP"),
    JHARKHAND("JH"),
    KARNATAKA("KA"),
    KERALA("KL"),
    MADHYA_PRADESH("MP"),
    MAHARASHTRA("MH"),
    MANIPUR("MN"),
    MEGHALAYA("ML"),
    MIZORAM("MZ"),
    NAGALAND("NL"),
    ODISHA("OD"),
    PUNJAB("PB"),
    RAJASTHAN("RJ"),
    SIKKIM("SK"),
    TAMIL_NADU("TN"),
    TELANGANA("TS"),
    TRIPURA("TR"),
    UTTAR_PRADESH("UP"),
    UTTARAKHAND("UK"),
    WEST_BENGAL("WB"),
    ANDAMAN_AND_NICOBAR("AN"),
    CHANDIGARH("CH"),
    DADRA_AND_NAGAR_HAVELI_AND_DAMAN_AND_DIU("DD"),
    DELHI("DL"),
    JAMMU_AND_KASHMIR("JK"),
    LADAKH("LA"),
    LAKSHADWEEP("LD"),
    PUDUCHERRY("PY");

    private final String code;

    StateCode(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public static StateCode fromCode(String code) {
        for (StateCode state : values()) {
            if (state.code.equals(code)) {
                return state;
            }
        }
        throw new IllegalArgumentException("Invalid state code: " + code);
    }
}
