package org.example.ms072.handling.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.util.AttributeKey;
import org.example.ms072.client.MapleClient;
import org.example.ms072.constants.ServerConfig;
import org.example.ms072.handling.RecvPacketOpcode;
import org.example.ms072.tools.FileoutputUtil;
import org.example.ms072.tools.HexTool;
import org.example.ms072.tools.MapleAESOFB;
import org.example.ms072.tools.StringUtil;
import org.example.ms072.tools.data.input.ByteArrayByteStream;
import org.example.ms072.tools.data.input.GenericLittleEndianAccessor;

import java.util.List;

public class MaplePacketDecoder extends ByteToMessageDecoder {

    public static final AttributeKey<DecoderState> DECODER_STATE_KEY = AttributeKey.valueOf(MaplePacketDecoder.class.getName() + ".STATE");

    public static class DecoderState {

        public int packetlength = -1;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> message) throws Exception {
        final MapleClient client = ctx.channel().attr(MapleClient.CLIENT_KEY).get();
        DecoderState decoderState = ctx.channel().attr(DECODER_STATE_KEY).get();
        if (decoderState == null) {
            decoderState = new DecoderState();
            ctx.channel().attr(DECODER_STATE_KEY).set(decoderState);
        }
        if (in.readableBytes() >= 4 && decoderState.packetlength == -1) {
            int packetHeader = in.readInt();
            if (!client.getReceiveCrypto().checkPacket(packetHeader)) {
                ctx.channel().disconnect();
                return;
            }
            decoderState.packetlength = MapleAESOFB.getPacketLength(packetHeader);
        } else if (in.readableBytes() < 4 && decoderState.packetlength == -1) {
            return;
        }
        if (in.readableBytes() >= decoderState.packetlength) {
            byte[] decryptedPacket = new byte[decoderState.packetlength];
            in.readBytes(decryptedPacket);
            String originPacket = HexTool.toString(decryptedPacket);
            decoderState.packetlength = -1;
            client.getReceiveCrypto().crypt(decryptedPacket);
            MapleCustomEncryption.decryptData(decryptedPacket);
            message.add(decryptedPacket);
            int packetLen = decryptedPacket.length;
            int pHeader = readFirstShort(decryptedPacket);
            String pHeaderStr = Integer.toHexString(pHeader).toUpperCase();
            pHeaderStr = StringUtil.getLeftPaddedStr(pHeaderStr, '0', 4);
            String op = lookupSend(pHeader);
            if (ServerConfig.logPackets && !RecvPacketOpcode.isSpamHeader(RecvPacketOpcode.valueOf(op))) {
                StringBuilder recvString = new StringBuilder();
                recvString.append("---------------------- 收 ----------------------").append("\r\n")
                        .append(op).append("[").append(pHeaderStr).append("] (").append(packetLen).append(")")
                        .append(client.getPlayer() != null ? " of : " + client.getPlayer().getName() : "").append("\r\n")
                        .append("ori: ").append(originPacket).append("\r\n")
                        .append("dec: ").append(HexTool.toString(decryptedPacket)).append("\r\n")
                        .append("text: ").append(HexTool.toStringFromAscii(decryptedPacket));
                System.out.println(recvString);
                FileoutputUtil.log("logs/数据包_收.txt", "\r\n\r\n" + recvString + "\r\n\r\n");
            }
        }
    }

    private String lookupSend(int val) {
        for (RecvPacketOpcode op : RecvPacketOpcode.values()) {
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
