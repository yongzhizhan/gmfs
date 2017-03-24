package cn.zhanyongzhi.gmfs.store;

public class Block {
    static private String magicNumber = "GMFS";
    static private int packSize = 8;

    private int size;
    private byte[] data;
    private long checksum;

    static public int getHeaderSize(){
        return magicNumber.getBytes().length + 4;
    }

    static public int getDataSize(byte[] header){
        return 0;
    }

    static public Block fromBinary(byte[] header, byte[] data){
        return null;
    }

    public byte[] toBinary(){
        int totalSize = magicNumber.getBytes().length + 4 + data.length + 8;
        totalSize = ((totalSize + packSize - 1) / totalSize) * packSize;
        return null;
    }

    public String getMagicNumber() {
        return magicNumber;
    }

    public void setMagicNumber(String magicNumber) {
        this.magicNumber = magicNumber;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public long getChecksum() {
        return checksum;
    }

    static public int getPackSize() {
        return packSize;
    }

    public void setChecksum(long checksum) {
        this.checksum = checksum;
    }
}
