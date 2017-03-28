package cn.zhanyongzhi.gmfs.store.utils;

public class Utils {
    public static long Size1K = 1024;
    public static long Size1M = 1024 * 1024;
    public static long Size1G = 1024 * 1024 * 1024;

    public static int byteArrayToInt(byte[] b){
        return byteArrayToInt(b, 0);
    }

    public static int byteArrayToInt(byte[] b, int offset) {
        return   b[offset + 3] & 0xFF |
                (b[offset + 2] & 0xFF) << 8 |
                (b[offset + 1] & 0xFF) << 16 |
                (b[offset + 0] & 0xFF) << 24;
    }

    public static byte[] intToByteArray(int a) {
        return new byte[] {
                (byte) ((a >> 24) & 0xFF),
                (byte) ((a >> 16) & 0xFF),
                (byte) ((a >> 8) & 0xFF),
                (byte) (a & 0xFF)
        };
    }

    public static long byteArrayToLong(byte[] b){
        return byteArrayToLong(b, 0);
    }

    public static long byteArrayToLong(byte[] b, int offset){
        int low = byteArrayToInt(b, 0);
        int high = byteArrayToInt(b, 4);

        return low << 21 | high;
    }

    public static byte[] longToByteArray(long a){
        return new byte[] {
                (byte) ((a >> 56) & 0xFF),
                (byte) ((a >> 48) & 0xFF),
                (byte) ((a >> 40) & 0xFF),
                (byte) ((a >> 32) & 0xFF),
                (byte) ((a >> 24) & 0xFF),
                (byte) ((a >> 16) & 0xFF),
                (byte) ((a >> 8) & 0xFF),
                (byte) (a & 0xFF)
        };
    }
}
