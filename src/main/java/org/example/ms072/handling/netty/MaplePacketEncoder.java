package org.example.ms072.handling.netty;

import  org.example.ms072.client.MapleClient;
import  org.example.ms072.constants.ServerConfig;
import org.example.ms072.handling.SendPacketOpcode;
import  org.example.ms072.tools.MapleAESOFB;
import java.util.concurrent.locks.Lock;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import  org.example.ms072.tools.FileoutputUtil;
import  org.example.ms072.tools.HexTool;
import  org.example.ms072.tools.StringUtil;
import  org.example.ms072.tools.data.input.ByteArrayByteStream;
import  org.example.ms072.tools.data.input.GenericLittleEndianAccessor;

public class MaplePacketEncoder extends MessageToByteEncoder<Object> {

    @Override
    protected void encode(ChannelHandlerContext ctx, Object message, ByteBuf buffer) throws Exception {
        final MapleClient client = ctx.channel().attr(MapleClient.CLIENT_KEY).get();
        if (client != null) {
            final MapleAESOFB send_crypto = client.getSendCrypto();
            // final byte[] inputInitialPacket = ((byte[]) message);
            final byte[] input = ((byte[]) message);
            int packetLen = input.length;
            int pHeader = readFirstShort(input);
            String pHeaderStr = Integer.toHexString(pHeader).toUpperCase();
            pHeaderStr = StringUtil.getLeftPaddedStr(pHeaderStr, '0', 4);
            String op = lookupRecv(pHeader);
            final byte[] unencrypted = new byte[input.length];
            System.arraycopy(input, 0, unencrypted, 0, input.length);
            final byte[] ret = new byte[unencrypted.length + 4];
            MapleCustomEncryption.encryptData(unencrypted);
            final Lock mutex = client.getLock();
            mutex.lock();
            try {
                final byte[] header = send_crypto.getPacketHeader(unencrypted.length);
                send_crypto.crypt(unencrypted);
                System.arraycopy(header, 0, ret, 0, 4);
                System.arraycopy(unencrypted, 0, ret, 4, unencrypted.length);
                buffer.writeBytes(ret);
                if (ServerConfig.logPackets && !SendPacketOpcode.isSpamHeader(SendPacketOpcode.valueOf(op))) {
                    StringBuilder sendString = new StringBuilder();
                    sendString.append("---------------------- 发 ----------------------").append("\r\n")
                            .append(op).append("[").append(pHeaderStr).append("] (").append(packetLen).append(")")
                            .append(client.getPlayer() != null ? " to : " + client.getPlayer().getName() : "").append("\r\n")
                            .append("ori: ").append(HexTool.toString(input)).append("\r\n")
                            .append("enc: ").append(HexTool.toString(ret)).append("\r\n")
                            .append("text: ").append(HexTool.toStringFromAscii(input));
                    System.out.println(sendString);
                    FileoutputUtil.log("logs/数据包_发.txt", "\r\n\r\n" + sendString + "\r\n\r\n");
                }
            } finally {
                mutex.unlock();
            }
        } else {
            buffer.writeBytes((byte[]) message);
        }
    }

    private String lookupRecv(int val) {
        for (SendPacketOpcode op : SendPacketOpcode.values()) {
            if (op.getValue() == val) {
                return op.name();
            }
        }
        return "UNKNOWN";
    }

    private int readFirstShort(byte[] arr) {
        return new GenericLittleEndianAccessor(new ByteArrayByteStream(arr)).readShort();
    }
}
