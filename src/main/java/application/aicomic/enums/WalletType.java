package application.aicomic.enums;

public enum WalletType {
    MAIN((byte) 0),
    PROMOTION((byte) 1);

    private final byte value;

    WalletType(byte value) {
        this.value = value;
    }
    public byte getValue(){
        return value;
    }
    public static WalletType fromValue(byte value){
        for (WalletType walletType : WalletType.values()){
            if(walletType.value==value){
                return walletType;
            }
        }
        throw new IllegalArgumentException("Unknow walletType value:" + value);
    }
}
