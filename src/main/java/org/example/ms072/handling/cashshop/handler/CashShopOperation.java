package org.example.ms072.handling.cashshop.handler;

import  org.example.ms072.client.MapleCharacter;
import  org.example.ms072.client.MapleCharacterUtil;
import  org.example.ms072.client.MapleClient;
import  org.example.ms072.client.MapleQuestStatus;
import  org.example.ms072.client.inventory.Item;
import  org.example.ms072.client.inventory.MapleInventory;
import  org.example.ms072.client.inventory.MapleInventoryIdentifier;
import  org.example.ms072.client.inventory.MapleInventoryType;
import  org.example.ms072.client.inventory.MapleRing;
import  org.example.ms072.constants.GameConstants;
import  org.example.ms072.constants.SetConstants;
import  org.example.ms072.database.DatabaseConnection;
import org.example.ms072.handling.cashshop.CashShopServer;
import  org.example.ms072.handling.channel.ChannelServer;
import  org.example.ms072.handling.login.LoginServer;
import  org.example.ms072.handling.world.CharacterTransfer;
import  org.example.ms072.handling.world.World;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import  org.example.ms072.server.AutobanManager;
import  org.example.ms072.server.CashItem;
import  org.example.ms072.server.CashItemFactory;
import  org.example.ms072.server.CashItemInfo;
import  org.example.ms072.server.CashShop;
import  org.example.ms072.server.MapleInventoryManipulator;
import  org.example.ms072.server.MapleItemInformationProvider;
import  org.example.ms072.server.quest.MapleQuest;
import  org.example.ms072.tools.FileoutputUtil;
import  org.example.ms072.tools.Triple;
import  org.example.ms072.tools.data.LittleEndianAccessor;
import  org.example.ms072.tools.packet.CField;
import  org.example.ms072.tools.packet.CSPacket;
import  org.example.ms072.tools.packet.CWvsContext;

public class CashShopOperation {

    public static void LeaveCS(final LittleEndianAccessor slea, final MapleClient c, final MapleCharacter chr) throws InterruptedException {
        if (chr == null) {
            return;
        }
        int channel = c.getChannel(); //角色要更换的频道
        ChannelServer toch = ChannelServer.getInstance(channel); //角色从商城出来更换的频道信息
        if (toch == null) {
            System.err.println("玩家: " + chr.getName() + " 从商城离开发生错误.找不到频道[" + channel + "]的信息.");
            c.getSession().close();
            return;
        }
        //开始处理
        World.ChannelChange_Data(new CharacterTransfer(chr), chr.getId(), c.getChannel());
        CashShopServer.getPlayerStorage().deregisterPlayer(chr);
        c.updateLoginState(MapleClient.LOGIN_SERVER_TRANSITION, c.getSessionIPAddress());
        c.sendPacket(CField.getChannelChange(c, toch.getPort())); //发送更换频道的封包信息
        c.setPlayer(null);
        c.setReceiving(false);
        /*CashShopServer.getPlayerStorage().deregisterPlayer(chr);
        c.updateLoginState(MapleClient.LOGIN_SERVER_TRANSITION, c.getSessionIPAddress());
        final String s = c.getSessionIPAddress();
        LoginServer.addIPAuth(s.substring(s.indexOf('/') + 1, s.length()));
        World.ChannelChange_Data(new CharacterTransfer(chr), chr.getId(), c.getChannel());
        c.sendPacket(CField.getChannelChange(c, Integer.parseInt(ChannelServer.getInstance(c.getChannel()).getIP().split(":")[1])));
        chr.saveToDB(false, true);
        c.setPlayer(null);
        c.setReceiving(false);
        c.getSession().close();*/
    }

    public static void EnterCS(final CharacterTransfer transfer, final MapleClient c) {
        if (transfer == null) {
            c.getSession().close();
            FileoutputUtil.logToFile(FileoutputUtil.DC_Log, "\r\n服务器主动断开用户端连接，调用位置: " + new Throwable().getStackTrace()[0]);
            return;
        }
        MapleCharacter chr = MapleCharacter.ReconstructChr(transfer, c, false);

        c.setPlayer(chr);
        c.setAccID(chr.getAccountID());

        if (!c.CheckIPAddress()) { // Remote hack
            c.getSession().close();
            FileoutputUtil.logToFile(FileoutputUtil.DC_Log, "\r\n服务器主动断开用户端连接，调用位置: " + new Throwable().getStackTrace()[0]);
            return;
        }

        final int state = c.getLoginState();
        boolean allowLogin = false;
        if (state == MapleClient.LOGIN_SERVER_TRANSITION || state == MapleClient.CHANGE_CHANNEL) {
            if (!World.isCharacterListConnected(c.loadCharacterNames(c.getWorld()))) {
                allowLogin = true;
            }
        }
        if (!allowLogin) {
            c.setPlayer(null);
            c.getSession().close();
            FileoutputUtil.logToFile(FileoutputUtil.DC_Log, "\r\n服务器主动断开用户端连接，调用位置: " + new Throwable().getStackTrace()[0]);
            return;
        }
        c.updateLoginState(MapleClient.LOGIN_LOGGEDIN, c.getSessionIPAddress());
        CashShopServer.getPlayerStorage().registerPlayer(chr);
        c.sendPacket(CSPacket.warpCS(c));

        //c.sendPacket(CSPacket.disableCS()); // Updated to v146.1 // Nothing changed
        //c.sendPacket(CSPacket.loadCategories()); // Updated to v146.1 // Noting changed
        //c.sendPacket(CSPacket.CS_Picture_Item()); // Updated - Need to check if nothing changed
        //c.sendPacket(CSPacket.CS_Top_Items()); // Updated to v146.1
        //c.sendPacket(CSPacket.CS_Special_Item()); // Updated to v146.1
        //c.sendPacket(CSPacket.CS_Featured_Item()); // Updated to v146.
        //c.sendPacket(CSPacket.showNXMapleTokens(c.getPlayer()));
        //doCSPackets(c);
        //loadCashShop(c);
        doCSPackets(c);
        //if (c.getPlayer().isAdmin()) {
        //    c.StartWindow();
        //}
    }

    public static void loadCashShop(MapleClient c) {
        c.sendPacket(CSPacket.loadCategories());
        String head = "E2 02";
        c.sendPacket(CField.getPacketFromHexString(head + " 04 01 09 00 09 3D 00 40 A5 3D 00 38 6D 0F 00 55 00 68 74 74 70 3A 2F 2F 6E 78 63 61 63 68 65 2E 6E 65 78 6F 6E 2E 6E 65 74 2F 73 70 6F 74 6C 69 67 68 74 2F 32 38 36 2F 30 30 45 53 33 2D 64 62 33 63 63 36 64 38 2D 32 36 31 62 2D 34 35 36 30 2D 38 33 31 33 2D 62 30 36 61 66 62 66 30 66 34 39 34 2E 6A 70 67 1B E5 F5 05 30 71 54 00 01 00 00 00 03 00 00 00 00 00 00 00 00 00 00 00 B8 0B 00 00 00 80 22 D6 94 EF C4 01 00 80 05 BB 46 E6 17 02 00 40 36 59 61 3A CF 01 00 00 A0 83 2A 3B CF 01 84 03 00 00 00 00 00 00 01 00 00 00 01 00 00 00 01 00 01 00 01 00 00 00 01 00 02 00 00 00 05 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 09 3D 00 40 A5 3D 00 E4 DE 0F 00 55 00 68 74 74 70 3A 2F 2F 6E 78 63 61 63 68 65 2E 6E 65 78 6F 6E 2E 6E 65 74 2F 73 70 6F 74 6C 69 67 68 74 2F 32 38 36 2F 30 30 45 53 33 2D 64 62 33 63 63 36 64 38 2D 32 36 31 62 2D 34 35 36 30 2D 38 33 31 33 2D 62 30 36 61 66 62 66 30 66 34 39 34 2E 6A 70 67 0F E4 F5 05 E2 E7 8A 00 01 00 00 00 03 00 00 00 01 00 00 00 00 00 00 00 40 38 00 00 00 80 22 D6 94 EF C4 01 00 80 05 BB 46 E6 17 02 00 80 22 D6 94 EF C4 01 00 80 05 BB 46 E6 17 02 30 2A 00 00 00 00 00 00 01 00 00 00 5A 00 00 00 01 00 01 00 01 00 00 00 01 00 02 00 00 00 0F 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 02 00 00 00 10 E4 F5 05 79 3D 4D 00 01 00 00 00 40 38 00 00 30 2A 00 00 00 00 00 00 0C 00 00 00 5A 00 00 00 02 00 00 00 BC E1 F5 05 FF 61 54 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 01 00 00 00 5A 00 00 00 02 00 00 00 00 09 3D 00 40 A5 3D 00 E4 DE 0F 00 55 00 68 74 74 70 3A 2F 2F 6E 78 63 61 63 68 65 2E 6E 65 78 6F 6E 2E 6E 65 74 2F 73 70 6F 74 6C 69 67 68 74 2F 32 38 36 2F 30 30 45 53 33 2D 64 62 33 63 63 36 64 38 2D 32 36 31 62 2D 34 35 36 30 2D 38 33 31 33 2D 62 30 36 61 66 62 66 30 66 34 39 34 2E 6A 70 67 13 E4 F5 05 E4 E7 8A 00 01 00 00 00 03 00 00 00 01 00 00 00 00 00 00 00 20 67 00 00 00 80 22 D6 94 EF C4 01 00 80 05 BB 46 E6 17 02 00 80 22 D6 94 EF C4 01 00 80 05 BB 46 E6 17 02 58 4D 00 00 00 00 00 00 01 00 00 00 5A 00 00 00 01 00 01 00 01 00 00 00 01 00 02 00 00 00 08 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 02 00 00 00 14 E4 F5 05 7A 3D 4D 00 01 00 00 00 20 67 00 00 58 4D 00 00 00 00 00 00 0C 00 00 00 5A 00 00 00 02 00 00 00 BC E1 F5 05 FF 61 54 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 01 00 00 00 5A 00 00 00 02 00 00 00 00 09 3D 00 40 A5 3D 00 E4 DE 0F 00 55 00 68 74 74 70 3A 2F 2F 6E 78 63 61 63 68 65 2E 6E 65 78 6F 6E 2E 6E 65 74 2F 73 70 6F 74 6C 69 67 68 74 2F 32 38 36 2F 30 30 45 53 33 2D 64 62 33 63 63 36 64 38 2D 32 36 31 62 2D 34 35 36 30 2D 38 33 31 33 2D 62 30 36 61 66 62 66 30 66 34 39 34 2E 6A 70 67 0A E4 F5 05 79 3D 4D 00 01 00 00 00 03 00 00 00 00 00 00 00 00 00 00 00 E0 2E 00 00 00 80 22 D6 94 EF C4 01 00 80 05 BB 46 E6 17 02 00 40 36 59 61 3A CF 01 80 69 07 83 2A 3B CF 01 10 27 00 00 00 00 00 00 0B 00 00 00 5A 00 00 00 01 00 01 00 01 00 00 00 01 00 02 00 00 00 10 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 09 3D 00 40 A5 3D 00 E4 DE 0F 00 55 00 68 74 74 70 3A 2F 2F 6E 78 63 61 63 68 65 2E 6E 65 78 6F 6E 2E 6E 65 74 2F 73 70 6F 74 6C 69 67 68 74 2F 32 38 36 2F 30 30 45 53 33 2D 64 62 33 63 63 36 64 38 2D 32 36 31 62 2D 34 35 36 30 2D 38 33 31 33 2D 62 30 36 61 66 62 66 30 66 34 39 34 2E 6A 70 67 73 E2 F5 05 64 3F 4D 00 01 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 C0 5D 00 00 00 80 22 D6 94 EF C4 01 00 80 05 BB 46 E6 17 02 00 80 22 D6 94 EF C4 01 00 80 05 BB 46 E6 17 02 C0 5D 00 00 00 00 00 00 0B 00 00 00 5A 00 00 00 01 00 01 00 01 00 00 00 01 00 02 00 00 00 0F 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 09 3D 00 40 A5 3D 00 A0 E1 0F 00 55 00 68 74 74 70 3A 2F 2F 6E 78 63 61 63 68 65 2E 6E 65 78 6F 6E 2E 6E 65 74 2F 73 70 6F 74 6C 69 67 68 74 2F 32 38 36 2F 30 30 45 53 33 2D 64 62 33 63 63 36 64 38 2D 32 36 31 62 2D 34 35 36 30 2D 38 33 31 33 2D 62 30 36 61 66 62 66 30 66 34 39 34 2E 6A 70 67 7F E2 F5 05 93 E7 8A 00 01 00 00 00 03 00 00 00 01 00 00 00 00 00 00 00 B8 3D 00 00 00 80 22 D6 94 EF C4 01 00 80 05 BB 46 E6 17 02 00 40 50 3D 41 30 CE 01 00 80 05 BB 46 E6 17 02 00 32 00 00 00 00 00 00 01 00 00 00 5A 00 00 00 01 00 01 00 01 00 00 00 01 00 02 00 00 00 09 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 03 00 00 00 74 E2 F5 05 64 3F 4D 00 00 00 00 00 E0 2E 00 00 AC 26 00 00 00 00 00 00 05 00 00 00 5A 00 00 00 02 00 00 00 06 2D 9A 00 9C 62 54 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 01 00 00 00 5A 00 00 00 02 00 00 00 71 E2 F5 05 F8 62 54 00 01 00 00 00 D8 0E 00 00 54 0B 00 00 00 00 00 00 01 00 00 00 5A 00 00 00 02 00 00 00 00 09 3D 00 40 A5 3D 00 A0 E1 0F 00 55 00 68 74 74 70 3A 2F 2F 6E 78 63 61 63 68 65 2E 6E 65 78 6F 6E 2E 6E 65 74 2F 73 70 6F 74 6C 69 67 68 74 2F 32 38 36 2F 30 30 45 53 33 2D 64 62 33 63 63 36 64 38 2D 32 36 31 62 2D 34 35 36 30 2D 38 33 31 33 2D 62 30 36 61 66 62 66 30 66 34 39 34 2E 6A 70 67 80 E2 F5 05 94 E7 8A 00 01 00 00 00 03 00 00 00 01 00 00 00 00 00 00 00 98 6C 00 00 00 80 22 D6 94 EF C4 01 00 80 05 BB 46 E6 17 02 00 40 50 3D 41 30 CE 01 00 80 05 BB 46 E6 17 02 28 55 00 00 00 00 00 00 01 00 00 00 5A 00 00 00 01 00 01 00 01 00 00 00 01 00 02 00 00 00 11 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 04 00 00 00 75 E2 F5 05 64 3F 4D 00 00 00 00 00 C0 5D 00 00 D4 49 00 00 00 00 00 00 0A 00 00 00 5A 00 00 00 02 00 00 00 06 2D 9A 00 9C 62 54 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 01 00 00 00 5A 00 00 00 02 00 00 00 09 2D 9A 00 9D 62 54 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 01 00 00 00 5A 00 00 00 02 00 00 00 71 E2 F5 05 F8 62 54 00 01 00 00 00 D8 0E 00 00 54 0B 00 00 00 00 00 00 01 00 00 00 5A 00 00 00 02 00 00 00 00 09 3D 00 40 A5 3D 00 48 DF 0F 00 55 00 68 74 74 70 3A 2F 2F 6E 78 63 61 63 68 65 2E 6E 65 78 6F 6E 2E 6E 65 74 2F 73 70 6F 74 6C 69 67 68 74 2F 32 38 36 2F 30 30 45 53 33 2D 64 62 33 63 63 36 64 38 2D 32 36 31 62 2D 34 35 36 30 2D 38 33 31 33 2D 62 30 36 61 66 62 66 30 66 34 39 34 2E 6A 70 67 71 E2 F5 05 F8 62 54 00 01 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 D8 0E 00 00 00 80 22 D6 94 EF C4 01 00 80 05 BB 46 E6 17 02 00 80 22 D6 94 EF C4 01 00 80 05 BB 46 E6 17 02 D8 0E 00 00 00 00 00 00 01 00 00 00 5A 00 00 00 01 00 01 00 01 00 00 00 01 00 02 00 00 00 18 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 09 3D 00 40 A5 3D 00 48 DF 0F 00 55 00 68 74 74 70 3A 2F 2F 6E 78 63 61 63 68 65 2E 6E 65 78 6F 6E 2E 6E 65 74 2F 73 70 6F 74 6C 69 67 68 74 2F 32 38 36 2F 30 30 45 53 33 2D 64 62 33 63 63 36 64 38 2D 32 36 31 62 2D 34 35 36 30 2D 38 33 31 33 2D 62 30 36 61 66 62 66 30 66 34 39 34 2E 6A 70 67 7E E2 F5 05 E6 62 54 00 01 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 8C 0A 00 00 00 80 22 D6 94 EF C4 01 00 80 05 BB 46 E6 17 02 00 80 22 D6 94 EF C4 01 00 80 05 BB 46 E6 17 02 8C 0A 00 00 00 00 00 00 01 00 00 00 5A 00 00 00 01 00 01 00 01 00 00 00 01 00 02 00 00 00 07 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00"));
        c.sendPacket(CField.getPacketFromHexString(head + " 05 01 04 C0 C6 2D 00 D0 ED 2D 00 48 DF 0F 00 00 00 71 E2 F5 05 F8 62 54 00 01 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 D8 0E 00 00 00 80 22 D6 94 EF C4 01 00 80 05 BB 46 E6 17 02 00 80 22 D6 94 EF C4 01 00 80 05 BB 46 E6 17 02 D8 0E 00 00 00 00 00 00 01 00 00 00 5A 00 00 00 01 00 01 00 01 00 00 00 01 00 02 00 00 00 18 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 C0 C6 2D 00 D0 ED 2D 00 48 DF 0F 00 00 00 7E E2 F5 05 E6 62 54 00 01 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 8C 0A 00 00 00 80 22 D6 94 EF C4 01 00 80 05 BB 46 E6 17 02 00 80 22 D6 94 EF C4 01 00 80 05 BB 46 E6 17 02 8C 0A 00 00 00 00 00 00 01 00 00 00 5A 00 00 00 01 00 01 00 01 00 00 00 01 00 02 00 00 00 07 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 C0 C6 2D 00 D0 ED 2D 00 D4 B7 0F 00 00 00 DA FE FD 02 A0 A6 4F 00 01 00 00 00 00 00 00 00 00 00 00 00 32 00 00 00 10 27 00 00 00 80 22 D6 94 EF C4 01 00 80 05 BB 46 E6 17 02 00 80 22 D6 94 EF C4 01 00 80 05 BB 46 E6 17 02 10 27 00 00 00 00 00 00 0B 00 00 00 5A 00 00 00 01 00 01 00 01 00 00 00 01 00 02 00 00 00 23 02 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 C0 C6 2D 00 D0 ED 2D 00 7C 6A 0F 00 00 00 87 2C 9A 00 AC AE 4F 00 01 00 00 00 00 00 00 00 00 00 00 00 22 00 00 00 48 0D 00 00 00 80 22 D6 94 EF C4 01 00 80 05 BB 46 E6 17 02 00 80 22 D6 94 EF C4 01 00 80 05 BB 46 E6 17 02 48 0D 00 00 00 00 00 00 01 00 00 00 5A 00 00 00 01 00 01 00 01 00 00 00 01 00 02 00 00 00 23 08 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00"));
        c.sendPacket(CField.getPacketFromHexString(head + " 06 01 05 C0 C6 2D 00 E0 14 2E 00 15 54 10 00 00 00 9C F1 FA 02 58 95 4E 00 01 00 00 00 04 00 00 00 00 00 00 00 32 00 00 00 E4 0C 00 00 00 80 22 D6 94 EF C4 01 00 80 05 BB 46 E6 17 02 00 80 22 D6 94 EF C4 01 00 80 05 BB 46 E6 17 02 E4 0C 00 00 00 00 00 00 01 00 00 00 5A 00 00 00 01 00 01 00 01 00 00 00 01 00 02 00 00 00 11 07 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 C0 C6 2D 00 E0 14 2E 00 16 54 10 00 00 00 C9 F1 FA 02 35 9D 4E 00 01 00 00 00 04 00 00 00 00 00 00 00 32 00 00 00 E4 0C 00 00 00 80 22 D6 94 EF C4 01 00 80 05 BB 46 E6 17 02 00 80 22 D6 94 EF C4 01 00 80 05 BB 46 E6 17 02 E4 0C 00 00 00 00 00 00 01 00 00 00 5A 00 00 00 01 00 01 00 01 00 00 00 01 00 02 00 00 00 77 04 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 C0 C6 2D 00 E0 14 2E 00 C4 90 0F 00 00 00 BF C3 C9 01 84 E7 4C 00 01 00 00 00 00 00 00 00 00 00 00 00 0F 00 00 00 AC 26 00 00 00 80 22 D6 94 EF C4 01 00 80 05 BB 46 E6 17 02 00 80 22 D6 94 EF C4 01 00 80 05 BB 46 E6 17 02 AC 26 00 00 00 00 00 00 01 00 00 00 1E 00 00 00 01 00 01 00 01 00 00 00 01 00 02 00 00 00 24 03 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 C0 C6 2D 00 E0 14 2E 00 7C 6A 0F 00 00 00 87 2C 9A 00 AC AE 4F 00 01 00 00 00 00 00 00 00 00 00 00 00 22 00 00 00 48 0D 00 00 00 80 22 D6 94 EF C4 01 00 80 05 BB 46 E6 17 02 00 80 22 D6 94 EF C4 01 00 80 05 BB 46 E6 17 02 48 0D 00 00 00 00 00 00 01 00 00 00 5A 00 00 00 01 00 01 00 01 00 00 00 01 00 02 00 00 00 23 08 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 C0 C6 2D 00 E0 14 2E 00 D4 B7 0F 00 00 00 D9 FE FD 02 A0 A6 4F 00 01 00 00 00 00 00 00 00 00 00 00 00 32 00 00 00 30 75 00 00 00 80 22 D6 94 EF C4 01 00 80 05 BB 46 E6 17 02 00 80 22 D6 94 EF C4 01 00 80 05 BB 46 E6 17 02 30 75 00 00 00 00 00 00 23 00 00 00 5A 00 00 00 01 00 01 00 01 00 00 00 01 00 02 00 00 00 DA 02 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00"));
        c.sendPacket(CField.getPacketFromHexString(head + " 09 01 01 C0 C6 2D 00 00 63 2E 00 B0 08 10 00 00 00 18 E3 F5 05 A8 69 52 00 01 00 00 00 05 00 00 00 00 00 00 00 00 00 00 00 01 00 00 00 00 80 22 D6 94 EF C4 01 00 80 05 BB 46 E6 17 02 00 80 22 D6 94 EF C4 01 00 80 05 BB 46 E6 17 02 01 00 00 00 00 00 00 00 01 00 00 00 1E 00 00 00 01 01 01 01 01 02 00 00 00 65 01 00 00 32 00 00 00 0A 00 31 4D 53 35 34 30 31 30 30 30 00 00 00 00 00 00 00 00 00 00 00 00 00 00"));
        c.sendPacket(CField.getPacketFromHexString(head + " 08 01 05 C0 C6 2D 00 F0 3B 2E 00 C4 90 0F 00 00 00 BF C3 C9 01 84 E7 4C 00 01 00 00 00 04 00 00 00 00 00 00 00 0F 00 00 00 AC 26 00 00 00 80 22 D6 94 EF C4 01 00 80 05 BB 46 E6 17 02 00 80 22 D6 94 EF C4 01 00 80 05 BB 46 E6 17 02 AC 26 00 00 00 00 00 00 01 00 00 00 1E 00 00 00 01 00 01 00 01 00 00 00 01 00 02 00 00 00 24 03 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 C0 C6 2D 00 F0 3B 2E 00 74 E0 0F 00 00 00 7C FE FD 02 81 3A 54 00 01 00 00 00 04 00 00 00 00 00 00 00 02 00 00 00 A0 0F 00 00 00 80 22 D6 94 EF C4 01 00 80 05 BB 46 E6 17 02 00 80 22 D6 94 EF C4 01 00 80 05 BB 46 E6 17 02 A0 0F 00 00 00 00 00 00 01 00 00 00 5A 00 00 00 01 00 01 00 01 00 00 00 01 00 02 00 00 00 B8 01 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 C0 C6 2D 00 F0 3B 2E 00 E8 07 10 00 00 00 40 FE FD 02 70 13 54 00 01 00 00 00 04 00 00 00 00 00 00 00 01 00 00 00 F4 01 00 00 00 80 22 D6 94 EF C4 01 00 80 05 BB 46 E6 17 02 00 80 22 D6 94 EF C4 01 00 80 05 BB 46 E6 17 02 F4 01 00 00 00 00 00 00 01 00 00 00 5A 00 00 00 01 00 01 00 01 00 00 00 01 00 02 00 00 00 8E 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 C0 C6 2D 00 F0 3B 2E 00 10 E0 0F 00 00 00 3D FE FD 02 D0 FD 54 00 01 00 00 00 04 00 00 00 00 00 00 00 04 00 00 00 24 13 00 00 00 80 22 D6 94 EF C4 01 00 80 05 BB 46 E6 17 02 00 80 22 D6 94 EF C4 01 00 80 05 BB 46 E6 17 02 24 13 00 00 00 00 00 00 01 00 00 00 5A 00 00 00 01 00 01 00 01 00 00 00 01 00 02 00 00 00 77 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 C0 C6 2D 00 F0 3B 2E 00 74 E0 0F 00 00 00 35 FE FD 02 80 3A 54 00 01 00 00 00 04 00 00 00 00 00 00 00 03 00 00 00 B8 0B 00 00 00 80 22 D6 94 EF C4 01 00 80 05 BB 46 E6 17 02 00 80 22 D6 94 EF C4 01 00 80 05 BB 46 E6 17 02 B8 0B 00 00 00 00 00 00 01 00 00 00 5A 00 00 00 01 00 01 00 01 00 00 00 01 00 02 00 00 00 F2 01 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00"));
    }

    public static void CSUpdate(final MapleClient c) {

        doCSPackets(c);
    }

    private static boolean CouponCodeAttempt(final MapleClient c) {
        c.couponAttempt++;
        return c.couponAttempt > 5;
    }

    public static void CouponCode(final String code, final MapleClient c) {
        if (code.length() <= 0) {
            return;
        }
        Triple<Boolean, Integer, Integer> info = null;
        try {
            info = MapleCharacterUtil.getNXCodeInfo(code);
        } catch (SQLException e) {
        }
        if (info != null && info.left) {
            if (!CouponCodeAttempt(c)) {
                int type = info.mid, item = info.right;
                try {
                    MapleCharacterUtil.setNXCodeUsed(c.getPlayer().getName(), code);
                } catch (SQLException e) {
                }
                /*
                 * Explanation of type!
                 * Basically, this makes coupon codes do
                 * different things!
                 *
                 * Type 1: A-Cash,
                 * Type 2: Maple Points
                 * Type 3: Item.. use SN
                 * Type 4: Mesos
                 */
                Map<Integer, Item> itemz = new HashMap<>();
                int maplePoints = 0, mesos = 0;
                switch (type) {
                    case 1:
                    case 2:
                        c.getPlayer().modifyCSPoints(type, item, false);
                        maplePoints = item;
                        break;
                    case 3:
                        CashItemInfo itez = CashItemFactory.getInstance().getItem(item);
                        if (itez == null) {
                            c.sendPacket(CSPacket.sendCSFail(0));
                            return;
                        }
                        byte slot = MapleInventoryManipulator.addId(c, itez.getId(), (short) 1, "", "Cash shop: coupon code" + " on " + FileoutputUtil.CurrentReadable_Date());
                        if (slot < 0) {
                            c.sendPacket(CSPacket.sendCSFail(0));
                            return;
                        } else {
                            itemz.put(item, c.getPlayer().getInventory(GameConstants.getInventoryType(item)).getItem(slot));
                        }
                        break;
                    case 4:
                        c.getPlayer().gainMeso(item, false);
                        mesos = item;
                        break;
                }
                c.sendPacket(CSPacket.showCouponRedeemedItem(itemz, mesos, maplePoints, c));
                doCSPackets(c);
            }
        } else if (CouponCodeAttempt(c) == true) {
            c.sendPacket(CSPacket.sendCSFail(48)); //A1, 9F
        } else {
            c.sendPacket(CSPacket.sendCSFail(info == null ? 14 : 17)); //A1, 9F
        }
    }

    public static void BuyCashItem(final LittleEndianAccessor slea, final MapleClient c, final MapleCharacter chr) {
        SetConstants item_id = new SetConstants();
        String itembp_id[] = item_id.getItempb_id();
        final int action = slea.readByte();
        if (chr.isAdmin()) {
            System.out.println("商城操作类型：" + action);
        }
        switch (action) {
            case 0:
                slea.skip(2);
                CouponCode(slea.readMapleAsciiString(), c);
                break;
            case 2: {
                slea.skip(1);
                int type = slea.readInt();//type
                int sn = slea.readInt();
                //            final CashItem item = CashItemFactory.getInstance().getMenuItem(sn);
                final CashItem item = CashItemFactory.getInstance().getAllItem(sn);
                final int toCharge = slea.readInt();//price
                if (item == null) {
                    c.sendPacket(CSPacket.sendCSFail(0));
                }
                chr.modifyCSPoints(type, -toCharge, true);
                Item itemz = chr.getCashInventory().toItem(item);
                if (itemz != null) {
                    chr.getCashInventory().addToInventory(itemz);
                    c.sendPacket(CSPacket.showBoughtCSItem(itemz, item.getSN(), c.getAccID()));
                } else {
                    c.sendPacket(CSPacket.sendCSFail(0));
                }
                break;
            }
            case 101: {
                //TODO BETTER idk what it is
//            System.out.println("action 101");//might be farm mesos? RITE NOW IS FREEH
                slea.skip(1);
                int type = slea.readInt();//type
                int sn = slea.readInt();
                final CashItem item = CashItemFactory.getInstance().getAllItem(sn);
                if (item == null) {
                    c.sendPacket(CSPacket.sendCSFail(0));
                }       //            chr.modifyCSPoints(type, -toCharge, true);
                Item itemz = chr.getCashInventory().toItem(item);
                if (itemz != null) {
                    chr.getCashInventory().addToInventory(itemz);
                    c.sendPacket(CSPacket.showBoughtCSItem(itemz, item.getSN(), c.getAccID()));
                } else {
                    c.sendPacket(CSPacket.sendCSFail(0));
                }
                break;
            }

            case 0x03: {
                //商城购买
                final int toCharge = slea.readByte() + 1;
                final CashItemInfo item = CashItemFactory.getInstance().getItem(slea.readInt());
                if (item == null) {
                    chr.dropMessage(1, "该物品暂未开放！");
                    doCSPackets(c);
                    return;
                } else {
                    System.out.println("购买事件ID: " + action + " 物品代码: " + item.getId() + " 物品SN:" + item.getSN());
                }
                for (String itembp_id1 : itembp_id) {
                    if (item.getId() == Integer.parseInt(itembp_id1)) {
                        c.getPlayer().dropMessage(1, "这个物品是禁止购买的.");
                        doCSPackets(c);
                        return;
                    }
                }
                if (item.getPrice() < 100) {
                    c.getPlayer().dropMessage(1, "价格(" + item.getPrice() + ")低于100点卷的物品是禁止购买的.");
                    doCSPackets(c);
                    return;
                }
                if (item != null && chr.getCSPoints(toCharge) >= item.getPrice()) {
                    if (!item.genderEquals(c.getPlayer().getGender())/* && c.getPlayer().getAndroid() == null*/) {
                        c.sendPacket(CSPacket.sendCSFail(0xA7));//背包空间不足
                        doCSPackets(c);
                        return;
                    } else if (item.getId() == 4031191 || item.getId() == 4031192) {
                        c.sendPacket(CWvsContext.broadcastMsg(1, "您不能通过现金商店购买此商品。"));
                        c.sendPacket(CWvsContext.enableActions());
                        doCSPackets(c);
                        return;
                    } else if (c.getPlayer().getCashInventory().getItemsSize() >= 100) {
                        c.sendPacket(CSPacket.sendCSFail(0xB2));
                        doCSPackets(c);
                        return;
                    }
                    chr.modifyCSPoints(toCharge, -item.getPrice(), false);

                    Item itemz = chr.getCashInventory().toItem(item);
                    if (itemz != null && itemz.getUniqueId() > 0 && itemz.getItemId() == item.getId() && itemz.getQuantity() == item.getCount()) {
                        chr.getCashInventory().addToInventory(itemz);
                        c.sendPacket(CSPacket.showBoughtCSItem(itemz, item.getSN(), c.getAccID()));
                    } else {
                        c.sendPacket(CSPacket.sendCSFail(0));
                    }
                } else {
                    c.sendPacket(CSPacket.sendCSFail(0));
                }
                break;
            }
            case 4:
            case 0x1F: {//31商城送礼包
                //gift, package
                final CashItemInfo item = CashItemFactory.getInstance().getItem(slea.readInt());
                String partnerName = slea.readMapleAsciiString();
                String msg = slea.readMapleAsciiString();
                if (item == null || c.getPlayer().getCSPoints(1) < item.getPrice() || msg.length() > 73 || msg.length() < 1) { //dont want packet editors gifting random stuff =P
                    c.sendPacket(CSPacket.sendCSFail(0));
                    doCSPackets(c);
                    return;
                }
                for (String itembp_id1 : itembp_id) {
                    if (item.getId() == Integer.parseInt(itembp_id1)) {
                        c.getPlayer().dropMessage(1, "这个物品是禁止购买的.");
                        doCSPackets(c);
                        return;
                    }
                }
                Triple<Integer, Integer, Integer> info = MapleCharacterUtil.getInfoByName(partnerName, c.getPlayer().getWorld());
                if (info == null || info.getLeft() <= 0 || info.getLeft() == c.getPlayer().getId() || info.getMid() == c.getAccID()) {
                    c.sendPacket(CSPacket.sendCSFail(0)); //9E v75
                    doCSPackets(c);
                    return;
                } else if (!item.genderEquals(info.getRight())) {
                    c.sendPacket(CSPacket.sendCSFail(0));
                    doCSPackets(c);
                    return;
                } else {
                    //get the packets for that
                    c.getPlayer().getCashInventory().gift(info.getLeft(), c.getPlayer().getName(), msg, item.getSN(), MapleInventoryIdentifier.getInstance());
                    c.getPlayer().modifyCSPoints(1, -item.getPrice(), false);
                    c.sendPacket(CSPacket.sendGift(item.getPrice(), item.getId(), item.getCount(), partnerName));
                    chr.sendNote(partnerName, partnerName + " 您已收到" + chr.getName() + "送给您的礼包，请进入现金商城查看！");
                    //int chz = WorldFindService.getInstance().findChannel(partnerName);
                    int chz = World.Find.findChannel(partnerName);
                    if (chz > 0) {
                        MapleCharacter receiver = ChannelServer.getInstance(chz).getPlayerStorage().getCharacterByName(partnerName);
                        if (receiver != null) {
                            receiver.showNote();
                        }
                    }
                }
                break;
            }

            case 5: {//加入购物车
                chr.clearWishlist();
                if (slea.available() < 40) {
                    c.sendPacket(CSPacket.sendCSFail(0));
                    doCSPackets(c);
                    return;
                }
                int[] wishlist = new int[10];
                for (int i = 0; i < 10; i++) {
                    wishlist[i] = slea.readInt();
                }
                chr.setWishlist(wishlist);
                c.sendPacket(CSPacket.sendWishList(chr, true));
                break;
            }
            case 6: {//背包扩充
                int useNX = slea.readByte() + 1;//是点券
                final boolean coupon = slea.readByte() > 0;//扩充类型
                if (coupon) {//道具扩充按钮
                    final MapleInventoryType type = getInventoryType(slea.readInt());
                    if (chr.getCSPoints(useNX) >= 1100 && chr.getInventory(type).getSlotLimit() < 96) {
                        chr.modifyCSPoints(useNX, -1100, false);
                        chr.getInventory(type).addSlot((byte) 8);
                        chr.dropMessage(1, "扩充成功，当前栏位: " + chr.getInventory(type).getSlotLimit());
                    } else {
                        c.sendPacket(CSPacket.sendCSFail(0));//扩充失败，点卷余额不足或者栏位已超过上限。
                    }
                } else {//背包旁边的扩充按钮
                    final MapleInventoryType type = MapleInventoryType.getByType(slea.readByte());
                    if (chr.getCSPoints(useNX) >= 600 && chr.getInventory(type).getSlotLimit() < 96) {
                        chr.modifyCSPoints(useNX, -600, false);
                        chr.getInventory(type).addSlot((byte) 4);
                        chr.dropMessage(1, "扩充成功，当前栏位: " + chr.getInventory(type).getSlotLimit());
                    } else {
                        c.sendPacket(CSPacket.sendCSFail(0));//扩充失败，点卷余额不足或者栏位已超过上限。
                    }
                }
                break;
            }
            case 7: {//仓库扩充
                int useNX = slea.readByte() + 1;//是点券
                if (chr.getCSPoints(useNX) >= 600 && chr.getStorage().getSlots() < 48) {//仓库是48个栏位
                    chr.modifyCSPoints(useNX, -600, false);
                    chr.getStorage().increaseSlots((byte) 4);
                    chr.getStorage().saveToDB();
                    chr.dropMessage(1, "仓库扩充成功，当前栏位: " + chr.getStorage().getSlots());
                } else {
                    c.sendPacket(CSPacket.sendCSFail(0));//仓库扩充失败，点卷余额不足或者栏位已超过上限 48 个位置。
                }
                break;
            }
            /*case 8: {//角色卡扩充
                //...10 = pendant slot expansion
                //slea.skip(1);
                final int toCharge = slea.readInt();
                CashItemInfo item = CashItemFactory.getInstance().getItem(slea.readInt());
                int slots = c.getCharacterSlots();
                if (item == null || c.getPlayer().getCSPoints(toCharge) < item.getPrice() || slots > 15 || item.getId() != 5430000) {
                    c.sendPacket(CSPacket.sendCSFail(0));
                    doCSPackets(c);
                    return;
                }
                if (c.gainCharacterSlot()) {
                    c.getPlayer().modifyCSPoints(toCharge, -item.getPrice(), false);
                    chr.dropMessage(1, "角色栏位增加到: " + (slots + 1));
                } else {
                    c.sendPacket(CSPacket.sendCSFail(0));
                }
                break;
            }*/
            case 10: {
                //...10 = pendant slot expansion
                //Data: 00 01 00 00 00 DC FE FD 02
                slea.readByte(); //Action is short?
                slea.readInt(); //always 1 - No Idea
                final int sn = slea.readInt();
                CashItemInfo item = CashItemFactory.getInstance().getItem(sn);
                if (item == null || c.getPlayer().getCSPoints(1) < item.getPrice() || item.getId() / 10000 != 555) {
                    c.sendPacket(CSPacket.sendCSFail(0));
                    doCSPackets(c);
                    return;
                }
                MapleQuestStatus marr = c.getPlayer().getQuestNoAdd(MapleQuest.getInstance(GameConstants.PENDANT_SLOT));
                if (marr != null && marr.getCustomData() != null && Long.parseLong(marr.getCustomData()) >= System.currentTimeMillis()) {
                    c.sendPacket(CSPacket.sendCSFail(0));
                } else {
                    c.getPlayer().getQuestNAdd(MapleQuest.getInstance(GameConstants.PENDANT_SLOT)).setCustomData(String.valueOf(System.currentTimeMillis() + ((long) item.getPeriod() * 24 * 60 * 60000)));
                    c.getPlayer().modifyCSPoints(1, -item.getPrice(), false);
                    chr.dropMessage(1, "获得额外的挂件槽.");
                }
                break;
            }
            case 0x0C: {//12商城到背包
                Item item = c.getPlayer().getCashInventory().findByCashId((int) slea.readLong());
                if (chr.isAdmin()) {
                    System.out.println("商城 => 背包 - 道具是否为空 :" + (item == null));
                }
                if (item != null && item.getQuantity() > 0 && MapleInventoryManipulator.checkSpace(c, item.getItemId(), item.getQuantity(), item.getOwner())) {
                    Item item_ = item.copy();
                    short pos = MapleInventoryManipulator.addbyItem(c, item_, true);
                    if (pos >= 0) {
                        if (item_.getPet() != null) {
                            item_.getPet().setInventoryPosition(pos);
                            c.getPlayer().addPet(item_.getPet());
                        }
                        c.getPlayer().getCashInventory().removeFromInventory(item);
                        c.sendPacket(CSPacket.confirmFromCSInventory(item_, pos));
                        if (chr.isAdmin()) {
                            System.out.println("商城 => 背包 - 移动成功");
                        }
                    } else {
                        c.sendPacket(CSPacket.sendCSFail(0xB1));
                    }
                } else {
                    c.sendPacket(CSPacket.sendCSFail(0xB1));
                }
                break;
            }
            case 0x0D: {//13背包到商城
                int cashId = (int) slea.readLong();
                byte type = slea.readByte();
                Item item = chr.getInventory(MapleInventoryType.getByType(type)).findByUniqueId(cashId);
                if (item == null) {
                    c.sendPacket(CSPacket.showNXMapleTokens(chr));
                    return;
                }
                if (chr.getCashInventory().getItemsSize() < 100) {
                    chr.getCashInventory().addToInventory(item);
                    chr.getInventory(MapleInventoryType.getByType(type)).removeSlot(item.getPosition());
                    c.sendPacket(CSPacket.confirmToCSInventory(item, c.getAccID(), CashItemFactory.getInstance().getItemSN(item.getItemId())));
                } else {
                    chr.dropMessage(1, "移动失败。");
                    doCSPackets(c);
                }
                break;
            }
            case 0x19: {//25商品换购
                int toCharge = 2;//抵用卷
                long uniqueId = (int) slea.readLong();
                Item item = c.getPlayer().getCashInventory().findByCashId((int) uniqueId);
                if (item == null) {
                    c.sendPacket(CSPacket.showNXMapleTokens(chr));
                    return;
                }
                int sn = CashItemFactory.getInstance().getItemSN(item.getItemId());
                CashItemInfo cItem = CashItemFactory.getInstance().getItem((int) sn);
                if (!MapleItemInformationProvider.getInstance().isCash(cItem.getId())) {
                    AutobanManager.getInstance().autoban(chr.getClient(), "商城非法换购道具.");
                    return;
                }
                int Money = cItem.getPrice() / 10 * 3;
                c.getPlayer().getCashInventory().removeFromInventory(item);
                chr.modifyCSPoints(toCharge, Money, false);
                c.sendPacket(CWvsContext.broadcastMsg(1, "成功换购抵用券" + Money + "点。"));
                doCSPackets(c);
                break;
            }
            case 28:
            case 35: {
                //38 = 挚友戒, 28 = 恋人戒
                final int toCharge = 1;
                final CashItemInfo item = CashItemFactory.getInstance().getItem(slea.readInt());
                final String partnerName = slea.readMapleAsciiString();
                final String msg = slea.readMapleAsciiString();
                for (String itembp_id1 : itembp_id) {
                    if (item.getId() == Integer.parseInt(itembp_id1)) {
                        c.getPlayer().dropMessage(1, "这个物品是禁止购买的.");
                        doCSPackets(c);
                        return;
                    }
                }
                if (item == null || !GameConstants.isEffectRing(item.getId()) || c.getPlayer().getCSPoints(toCharge) < item.getPrice() || msg.length() > 73 || msg.length() < 1) {
                    c.sendPacket(CSPacket.sendCSFail(0));
                    doCSPackets(c);
                    return;
                } else if (!item.genderEquals(c.getPlayer().getGender())) {
                    c.sendPacket(CSPacket.sendCSFail(0));//0xA6
                    doCSPackets(c);
                    return;
                } else if (c.getPlayer().getCashInventory().getItemsSize() >= 100) {
                    c.sendPacket(CSPacket.sendCSFail(0));//0xB1
                    doCSPackets(c);
                    return;

                }
                Triple<Integer, Integer, Integer> info = MapleCharacterUtil.getInfoByName(partnerName, c.getPlayer().getWorld());
                if (info == null || info.getLeft() <= 0 || info.getLeft() == c.getPlayer().getId()) {
                    c.sendPacket(CSPacket.sendCSFail(0)); //9E v75 -- B4
                    doCSPackets(c);
                    return;
                } else if (info.getMid() == c.getAccID()) {
                    c.sendPacket(CSPacket.sendCSFail(0)); //9D v75 --  A3
                    doCSPackets(c);
                    return;
                } else {
                    if (info.getRight() == c.getPlayer().getGender() && action == 28) {
                        c.sendPacket(CSPacket.sendCSFail(0)); //9B v75--  A1
                        doCSPackets(c);
                        return;
                    }
                    int err = MapleRing.createRing(item.getId(), c.getPlayer(), partnerName, msg, info.getLeft().intValue(), item.getSN());
                    if (err != 1) {
                        c.sendPacket(CSPacket.sendCSFail(0)); //9E v75
                        doCSPackets(c);
                        return;
                    }
                    c.getPlayer().modifyCSPoints(toCharge, -item.getPrice(), false);
                    //c.sendPacket(CSPacket.sendGift(item.getPrice(), item.getId(), item.getCount(), partnerName));
                    c.sendPacket(CSPacket.商城送礼物(item.getId(), item.getCount(), partnerName));
                    chr.sendNote(partnerName, partnerName + " 您已收到" + chr.getName() + "送给您的戒指，请进入现金商城查看！");
                    //int chz = WorldFindService.getInstance().findChannel(partnerName);
                    int chz = World.Find.findChannel(partnerName);
                    if (chz > 0) {
                        MapleCharacter receiver = ChannelServer.getInstance(chz).getPlayerStorage().getCharacterByName(partnerName);
                        if (receiver != null) {
                            receiver.showNote();
                        }
                    }
                }
                break;
            }
            case 30: {
                //was 33 - packages
                int type = slea.readByte() + 1;
                int snID = slea.readInt();
                final CashItemInfo item = CashItemFactory.getInstance().getItem(snID);
                List<Integer> ccc = null;
                for (String itembp_id1 : itembp_id) {
                    if (item.getId() == Integer.parseInt(itembp_id1)) {
                        c.getPlayer().dropMessage(1, "这个物品是禁止购买的.");
                        doCSPackets(c);
                        return;
                    }
                }
                if (item != null) {
                    ccc = CashItemFactory.getInstance().getPackageItems(item.getId());
                }
                if (item == null || ccc == null || c.getPlayer().getCSPoints(type) < item.getPrice()) {
                    c.sendPacket(CSPacket.sendCSFail(0));
                    doCSPackets(c);
                    return;
                } else if (!item.genderEquals(c.getPlayer().getGender())) {
                    c.sendPacket(CSPacket.sendCSFail(0xA6));
                    doCSPackets(c);
                    return;
                } else if (c.getPlayer().getCashInventory().getItemsSize() >= (100 - ccc.size())) {
                    c.sendPacket(CSPacket.sendCSFail(0xB1));
                    doCSPackets(c);
                    return;
                }
                Item itemz = null;
                Map<Integer, Item> ccz = new HashMap<>();
                for (int i : ccc) {
                    final CashItemInfo cii = CashItemFactory.getInstance().getPackageItem(i);
                    if (cii == null) {
                        continue;
                    }
                    itemz = c.getPlayer().getCashInventory().toItem(cii);
                    if (itemz == null || itemz.getUniqueId() <= 0) {
                        continue;
                    }

                    ccz.put(i, itemz);
                    c.getPlayer().getCashInventory().addToInventory(itemz);
                }
                chr.modifyCSPoints(type, -item.getPrice(), false);
                //     c.sendPacket(CSPacket.showBoughtCSPackage(ccz, c.getAccID()));
                c.sendPacket(CSPacket.showBoughtCSItem(itemz, item.getSN(), c.getAccID()));
                break;
            }
            case 32: {//0x20 购买任务道具
                //4031191 - 金铃铛 - 为了重新制作奈洛的项绳所需的金色铃铛，每当移动时就会发生声音。\n#c使用处:任务用道具#
                //4031192 - 红色蝴蝶结 - 为了重新制作奈洛的项绳所需的、用红色缎带所制作的蝴蝶结。
                chr.dropMessage(1, "暂未开放任务道具购买");
                doCSPackets(c);
                break;
            }
            case 99: {
                //99 buy with mesos
                final CashItemInfo item = CashItemFactory.getInstance().getItem(slea.readInt());
                if (item == null || !MapleItemInformationProvider.getInstance().isQuestItem(item.getId())) {
                    c.sendPacket(CSPacket.sendCSFail(0));
                    doCSPackets(c);
                    return;
                } else if (c.getPlayer().getMeso() < item.getPrice()) {
                    c.sendPacket(CSPacket.sendCSFail(0xB8));
                    doCSPackets(c);
                    return;
                } else if (c.getPlayer().getInventory(GameConstants.getInventoryType(item.getId())).getNextFreeSlot() < 0) {
                    c.sendPacket(CSPacket.sendCSFail(0xB1));
                    doCSPackets(c);
                    return;
                }
                byte pos = MapleInventoryManipulator.addId(c, item.getId(), (short) item.getCount(), null, "Cash shop: quest item" + " on " + FileoutputUtil.CurrentReadable_Date());
                if (pos < 0) {
                    c.sendPacket(CSPacket.sendCSFail(0xB1));
                    doCSPackets(c);
                    return;
                }
                chr.gainMeso(-item.getPrice(), false);
                c.sendPacket(CSPacket.showBoughtCSQuestItem(item.getPrice(), (short) item.getCount(), pos, item.getId()));
                break;
            }
            case 48:
                c.sendPacket(CSPacket.updatePurchaseRecord());
                break;
            case 91:
                // Open random box.
                final int uniqueid = (int) slea.readLong();

                //c.sendPacket(CSPacket.sendRandomBox(uniqueid, new Item(1302000, (short) 1, (short) 1, (short) 0, 10), (short) 0));
                //} else if (action == 99) { //buy with mesos
                //    int sn = slea.readInt();
                //    int price = slea.readInt();
                break;
            default:
                System.out.println("New Action: " + action + " Remaining: " + slea.toString());
                c.sendPacket(CSPacket.sendCSFail(0));
                break;
        }
        doCSPackets(c);
    }

    public static void SwitchCategory(final LittleEndianAccessor slea, final MapleClient c) {
        int Scategory = slea.readByte();
//        System.out.println("Scategory " + Scategory);
        if (Scategory == 103) {
            slea.skip(1);
            int itemSn = slea.readInt();
            try (Connection con = DatabaseConnection.getInstance().getDataSource().getConnection()) {
                try (PreparedStatement ps = con.prepareStatement("INSERT INTO `wishlist` VALUES (?, ?)")) {
                    ps.setInt(1, c.getPlayer().getId());
                    ps.setInt(2, itemSn);
                    ps.executeUpdate();
                    ps.close();
                }
            } catch (SQLException ex) {
                System.err.println("SwitchCategory 103" + ex);
                FileoutputUtil.outputFileError("logs/数据库异常.txt", ex);
            }
            c.sendPacket(CSPacket.addFavorite(itemSn));
        } else if (Scategory == 105) {
            int item = slea.readInt();
            try (Connection con = DatabaseConnection.getInstance().getDataSource().getConnection()) {
                try (PreparedStatement ps = con.prepareStatement("UPDATE cashshop_items SET likes = likes+" + 1 + " WHERE sn = ?")) {
                    ps.setInt(1, item);
                    ps.executeUpdate();
                }
            } catch (SQLException ex) {
                System.err.println("SwitchCategory 105" + ex);
                FileoutputUtil.outputFileError("logs/数据库异常.txt", ex);
            }
            c.sendPacket(CSPacket.Like(item));
        } else if (Scategory == 109) {
            c.sendPacket(CSPacket.Favorite(c.getPlayer()));
        } else if (Scategory == 112) {//click on special item TODO
            //int C8 - C9 - CA
        } else if (Scategory == 113) {//buy from cart inventory TODO
            //byte buy = 1 or gift = 0
            //byte amount
            //for each SN
        } else {
            int category = slea.readInt();
            if (category == 4000000) {
                c.sendPacket(CSPacket.CS_Top_Items());
                c.sendPacket(CSPacket.CS_Picture_Item());
            } else if (category == 1060100) {
                c.sendPacket(CSPacket.showNXChar(category));
                c.sendPacket(CSPacket.changeCategory(category));
            } else {
//                System.err.println(category);
                c.sendPacket(CSPacket.changeCategory(category));
            }
        }
    }

    private static MapleInventoryType getInventoryType(final int id) {
        switch (id) {
            case 50200018:
                return MapleInventoryType.EQUIP;
            case 50200019:
                return MapleInventoryType.USE;
            case 50200020:
                return MapleInventoryType.SETUP;
            case 50200021:
                return MapleInventoryType.ETC;
            default:
                System.out.println("未处理的 InventoryType : " + id);
                return MapleInventoryType.UNDEFINED;
        }
    }

    public static void doCSPackets(MapleClient c) {
        c.sendPacket(CSPacket.sendWishList(c.getPlayer(), false));
        c.sendPacket(CSPacket.showNXMapleTokens(c.getPlayer()));
        c.sendPacket(CSPacket.getCSGifts(c));
        c.sendPacket(CSPacket.getCSInventory(c));
        //c.sendPacket(CSPacket.doCSMagic());
        //c.sendPacket(CSPacket.getCSGifts(c));
        //c.sendPacket(CWvsContext.BuddylistPacket.updateBuddylist(c.getPlayer().getBuddylist().getBuddies()));
        //c.sendPacket(CSPacket.showNXMapleTokens(c.getPlayer()));
        // c.sendPacket(CSPacket.sendWishList(c.getPlayer(), false));
//        c.sendPacket(CSPacket.showNXMapleTokens(c.getPlayer()));
        c.sendPacket(CSPacket.enableCSUse());

//         c.getPlayer().getCashInventory().checkExpire(c);
    }
}
